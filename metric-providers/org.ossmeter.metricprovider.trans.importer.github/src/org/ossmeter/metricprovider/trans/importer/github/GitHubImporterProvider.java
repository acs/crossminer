/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Davide Di Ruscio- Implementation,
 *    Juri Di Rocco - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.trans.importer.github;
import java.util.List;

import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.Platform;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.logging.OssmeterLogger;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.ProjectExecutionInformation;
import org.ossmeter.repository.model.github.GitHubRepository;
import org.ossmeter.repository.model.github.importer.GitHubImporter;
import org.ossmeter.repository.model.importer.exception.WrongUrlException;

import com.googlecode.pongo.runtime.PongoDB;
import com.mongodb.DB;
public class GitHubImporterProvider implements ITransientMetricProvider{

	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.trans.githubimporter";
	protected OssmeterLogger logger;
	protected MetricProviderContext context;
	public GitHubImporterProvider()
	{
		logger = (OssmeterLogger) OssmeterLogger.getLogger("metricprovider.importer.gitHub");
	}
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return IDENTIFIER;
	}

	@Override
	public String getShortIdentifier() {
		// TODO Auto-generated method stub
		return "githubimporter";
	}

	@Override
	public String getFriendlyName() {
		// TODO Auto-generated method stub
		return "GitHub importer";
	}

	@Override
	public String getSummaryInformation() {
		// TODO Auto-generated method stub
		return "This provider enable to update a projects calling a importProject from github importer";
	}

	@Override
	public boolean appliesTo(Project project) {
		return (project instanceof GitHubRepository) ? true : false;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;

	}

	@Override
	public List<String> getIdentifiersOfUses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public PongoDB adapt(DB db) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void measure(Project project, ProjectDelta delta, PongoDB db) {
		GitHubRepository ep = null;

		
		GitHubImporter epi = new GitHubImporter();
		Platform platform = Platform.getInstance();
		try{
			ep = epi.importRepository( ((GitHubRepository)project).getFull_name(), platform);
			if (ep == null)
			{
				if(project.getExecutionInformation() == null)
					project.setExecutionInformation(new ProjectExecutionInformation());
				project.getExecutionInformation().setInErrorState(false);
			}
		}catch(WrongUrlException e)
		{
			if(project.getExecutionInformation() == null)
				project.setExecutionInformation(new ProjectExecutionInformation());
			project.getExecutionInformation().setInErrorState(true);
		}
		
	}

//	@Override
//	public Pongo measure(Project project) {
//		GitHubRepository ep = null;
//		Mongo mongo;
//		try {
//			GitHubImporter epi = new GitHubImporter("ffab283e2be3265c7b0af244e474b28430351973");
//			mongo = new Mongo();
//			PongoFactory.getInstance().getContributors().add(new OsgiPongoFactoryContributor());
//			Platform platform = new Platform(mongo);
//			//logger.info("J "+ project.getId());
//			ep = epi.importRepository( ((GitHubRepository)project).getFull_name(), platform);
//			mongo.close();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			logger.error("GitHub metric provider exception:" + e.getMessage());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("GitHub metric provider exception:" + e.getMessage());
//		}
//		return ep;
//	}

}
