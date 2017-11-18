/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Davide Di Ruscio - Implementation,
 *    Juri Di Rocco - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.trans.githubstars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.ossmeter.metricprovider.trans.githubstars.model.Stargazers;
import org.ossmeter.metricprovider.trans.githubstars.model.Stars;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.logging.OssmeterLogger;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.github.GitHubRepository;

import com.mongodb.DB;

public class StarsTransientMetricProvider implements ITransientMetricProvider<Stars> {

	private static final String UTF8 = "UTF-8";
	protected OssmeterLogger logger;
	protected MetricProviderContext context;

	public StarsTransientMetricProvider() {
		logger = (OssmeterLogger) OssmeterLogger.getLogger("metricprovider.trans.githubstars.StarsTransientMetricProvider");
	}

	public final static String IDENTIFIER = "org.ossmeter.metricprovider.trans.githubstar";

	@Override
	public String getIdentifier() {
		return StarsTransientMetricProvider.class.getCanonicalName();
	}

	@Override
	public String getShortIdentifier() {
		return "stars";
	}

	@Override
	public String getFriendlyName() {
		return "Stars";
	}

	@Override
	public String getSummaryInformation() {
		return "Transient metric provider for github stars";
	}

	@Override
	public boolean appliesTo(Project project) {
		if (project instanceof GitHubRepository)
			return true;
		else
			return false;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {

	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Collections.emptyList();
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {

	}

	@Override
	public Stars adapt(DB db) {
		return new Stars(db);
	}

	private static String getToken() {
		ResourceBundle myResources = ResourceBundle.getBundle("github_auth");
		return myResources.getString("token");
	}

	private JSONObject getRemainingResource() throws IOException {
		URL url = new URL("https://api.github.com/rate_limit");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "token " + getToken());
		connection.connect();
		InputStream is = connection.getInputStream();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is, Charset.forName(UTF8)));
		String jsonText = readAll(bufferReader);
		JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
		return (JSONObject) obj.get("resources");
	}

	private long getRemainingResource(String value) {
		try {
			JSONObject core = (JSONObject) getRemainingResource().get(value);
			return Long.parseLong(core.get("remaining").toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return 0;
	}

	private void waitApiCoreRate() {
		boolean sleep = true;
		while (sleep) {

			long remaining = getRemainingResource("core");
			if (remaining > 0)
				sleep = false;
			else
				try {
					Thread.sleep(100000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error(e.getMessage());
				}
		}
	}

	@Override
	public void measure(Project project, ProjectDelta delta, Stars db) {
		GitHubRepository rep = (GitHubRepository) project;
		int page = 1;
		boolean continueValue = true;
		//Clear stargazers collection
		try{
			db.getStargazers().getDbCollection().drop();
			db.sync();
			while (continueValue) {
				if (getRemainingResource("core") == 0)
					waitApiCoreRate();
				URL url;
				try {
					url = new URL("https://api.github.com/repos/" + rep.getFull_name() + "/stargazers?page=" + page
							+ "&per_page=100&access_token=" + getToken());
					HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
					connection.setRequestProperty("Accept", "application/vnd.github.v3.star+json");
					connection.connect();
					InputStream is = connection.getInputStream();
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is, Charset.forName(UTF8)));
					String jsonText = readAll(bufferReader);
					JSONArray obj = (JSONArray) JSONValue.parse(jsonText);
					if (obj.size() == 0)
						continueValue = false;
					for (Object object : obj) {
						JSONObject star = ((JSONObject) object);
						JSONObject userStarred = (JSONObject) star.get("user");
						Stargazers s = new Stargazers();
						s.setLogin(userStarred.get("login").toString());
						s.setDatestamp(star.get("starred_at").toString());
						db.getStargazers().add(s);
					}
					page++;
					db.sync();
				} catch (MalformedURLException e) {
					logger.error(e.getMessage());
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		} catch (Exception e){
			logger.error(e.getMessage());
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
