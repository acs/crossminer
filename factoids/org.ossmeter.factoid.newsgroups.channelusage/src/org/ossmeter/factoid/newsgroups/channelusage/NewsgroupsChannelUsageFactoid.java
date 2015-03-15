/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.ossmeter.factoid.newsgroups.channelusage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ossmeter.metricprovider.historic.newsgroups.articles.ArticlesHistoricMetricProvider;
import org.ossmeter.metricprovider.historic.newsgroups.articles.model.DailyNewsgroupData;
import org.ossmeter.metricprovider.historic.newsgroups.articles.model.NewsgroupsArticlesHistoricMetric;
import org.ossmeter.metricprovider.historic.newsgroups.newthreads.NewThreadsHistoricMetricProvider;
import org.ossmeter.metricprovider.historic.newsgroups.newthreads.model.NewsgroupsNewThreadsHistoricMetric;
import org.ossmeter.platform.AbstractFactoidMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.factoids.Factoid;
import org.ossmeter.platform.factoids.StarRating;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class NewsgroupsChannelUsageFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "NewsgroupChannelUsage";
	}

	@Override
	public String getFriendlyName() {
		return "Newsgroup Channel Usage";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "summaryblah"; // This method will NOT be removed in a later version.
	}

	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
		}
		return false;
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ArticlesHistoricMetricProvider.IDENTIFIER,
							 NewThreadsHistoricMetricProvider.IDENTIFIER);
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.BUGS);
		factoid.setName(getFriendlyName());

		ArticlesHistoricMetricProvider articlesProvider = null;
		NewThreadsHistoricMetricProvider threadsProvider = null;

		for (IMetricProvider m : this.uses) {
			if (m instanceof ArticlesHistoricMetricProvider) {
				articlesProvider = (ArticlesHistoricMetricProvider) m;
				continue;
			}
			if (m instanceof NewThreadsHistoricMetricProvider) {
				threadsProvider = (NewThreadsHistoricMetricProvider) m;
				continue;
			}
		}

		Date end = new Date();
		Date start = (new Date()).addDays(-365);
//		Date start=null, end=null;
//		try {
//			start = new Date("20040801");
//			end = new Date("20050801");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		List<Pongo> articlesList = articlesProvider.getHistoricalMeasurements(context, project, start, end),
					threadsList = threadsProvider.getHistoricalMeasurements(context, project, start, end);
		
//		System.err.println("---USAGE===RETRIEVED PONGOLIST FOR " + articlesList.size() + " DAYS===---");

		Map<String, Integer> newsgroupArticles = new HashMap<String, Integer>();
		int numberOfArticles = parseArticlesPongos(articlesList, newsgroupArticles);

		Map<String, Integer> newsgroupThreads = new HashMap<String, Integer>();
		int numberOfThreads = parseThreadsPongos(threadsList, newsgroupThreads);

		int workingDaysInAYear = 250;
		if ( (numberOfArticles > workingDaysInAYear) || (numberOfThreads > workingDaysInAYear) ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( (2 * numberOfArticles > workingDaysInAYear) || (2 * numberOfThreads > workingDaysInAYear) ) {
			factoid.setStars(StarRating.THREE);
		} else if ( (4 * numberOfArticles > workingDaysInAYear) || (4 * numberOfThreads > workingDaysInAYear) ) {
			factoid.setStars(StarRating.TWO);
		} else {
			factoid.setStars(StarRating.ONE);
		}
		

		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append("Over the last year, ");
		stringBuffer.append(numberOfArticles);
		stringBuffer.append(" new articles and ");
		stringBuffer.append(numberOfThreads);
		stringBuffer.append(" new threads have been posted in total.\n");

		end = new Date();
		start = (new Date()).addDays(-30);
		articlesList = articlesProvider.getHistoricalMeasurements(context, project, start, end);
		threadsList = threadsProvider.getHistoricalMeasurements(context, project, start, end);
		
		newsgroupArticles = new HashMap<String, Integer>();
		numberOfArticles = parseArticlesPongos(articlesList, newsgroupArticles);

		newsgroupThreads = new HashMap<String, Integer>();
		numberOfThreads = parseThreadsPongos(threadsList, newsgroupThreads);
		
		stringBuffer.append("Over the last month, ");
		stringBuffer.append(numberOfArticles);
		stringBuffer.append(" new bugs and ");
		stringBuffer.append(numberOfThreads);
		stringBuffer.append(" new threads have been posted.\n");

		factoid.setFactoid(stringBuffer.toString());

	}

	private int parseArticlesPongos(List<Pongo> articlesList, Map<String, Integer> newsgroupArticles) {
		int sumOfArticles = 0;
		for (Pongo pongo: articlesList) {
			NewsgroupsArticlesHistoricMetric articlesPongo = (NewsgroupsArticlesHistoricMetric) pongo;
			for (DailyNewsgroupData newsgroup: articlesPongo.getNewsgroups()) {
				int numberOfArticles = newsgroup.getNumberOfArticles(); 
				sumOfArticles += numberOfArticles; 
				if (newsgroupArticles.containsKey(newsgroup.getNewsgroupName()))
					newsgroupArticles.put(newsgroup.getNewsgroupName(), 
										  newsgroupArticles.get(newsgroup.getNewsgroupName()) + 
										  numberOfArticles);
				else
					newsgroupArticles.put(newsgroup.getNewsgroupName(), numberOfArticles);
			}
		}
		return sumOfArticles;
	}
		
	private int parseThreadsPongos(List<Pongo> threadsList, Map<String, Integer> newsgroupThreads) {
		int sumOfThreads = 0;
		for (Pongo pongo: threadsList) {
			NewsgroupsNewThreadsHistoricMetric threadsPongo = (NewsgroupsNewThreadsHistoricMetric) pongo;
			for ( org.ossmeter.metricprovider.historic.newsgroups.newthreads.model.DailyNewsgroupData 
					newsgroup: threadsPongo.getNewsgroups()) {
				int numberOfThreads = newsgroup.getNumberOfNewThreads();
				sumOfThreads += numberOfThreads;
				if (newsgroupThreads.containsKey(newsgroup.getNewsgroupName()))
					newsgroupThreads.put(newsgroup.getNewsgroupName(), 
										 newsgroupThreads.get(newsgroup.getNewsgroupName()) + 
										 numberOfThreads);
				else
					newsgroupThreads.put(newsgroup.getNewsgroupName(), numberOfThreads);
			}
		}
		return sumOfThreads;
	}
	
}
