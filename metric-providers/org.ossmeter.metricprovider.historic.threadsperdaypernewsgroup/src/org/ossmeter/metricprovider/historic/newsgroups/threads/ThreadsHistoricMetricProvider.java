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
package org.ossmeter.metricprovider.historic.newsgroups.threads;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.ossmeter.metricprovider.historic.newsgroups.threads.model.DailyNewsgroupData;
import org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric;
import org.ossmeter.metricprovider.trans.newsgroups.activeusers.ActiveUsersTransMetricProvider;
import org.ossmeter.metricprovider.trans.newsgroups.activeusers.model.NewsgroupsActiveUsersTransMetric;
import org.ossmeter.metricprovider.trans.newsgroups.activeusers.model.User;
import org.ossmeter.metricprovider.trans.newsgroups.threads.ThreadsTransMetricProvider;
import org.ossmeter.metricprovider.trans.newsgroups.threads.model.NewsgroupData;
import org.ossmeter.metricprovider.trans.newsgroups.threads.model.NewsgroupsThreadsTransMetric;
import org.ossmeter.metricprovider.trans.newsgroups.threads.model.ThreadData;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;
import org.ossmeter.repository.model.sourceforge.Discussion;

import com.googlecode.pongo.runtime.Pongo;

public class ThreadsHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.historic.newsgroups.threads";

	protected MetricProviderContext context;
	
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}
	
	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
			if (communicationChannel instanceof Discussion) return true;
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		if (uses.size()!=2) {
			System.err.println("Metric: threadsperdaypernewsgroup failed to retrieve " + 
								"the transient metric it needs!");
			System.exit(-1);
		}

		NewsgroupsThreadsTransMetric usedThreads = 
				((ThreadsTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
		NewsgroupsActiveUsersTransMetric usedUsers = 
				((ActiveUsersTransMetricProvider)uses.get(1)).adapt(context.getProjectDB(project));
		

		HashSet<Integer> threadIdSet = new HashSet<Integer>();
		for (ThreadData thread: usedThreads.getThreads())
			threadIdSet.add(thread.getThreadId());
		
		NewsgroupsThreadsHistoricMetric dailyThreads = new NewsgroupsThreadsHistoricMetric();
		

		int sumOfThreads = 0;
		int numberOfArticles = 0,
			numberOfRequests = 0,
			numberOfReplies = 0;

		for (NewsgroupData newsgroups: usedThreads.getNewsgroups()) {
			sumOfThreads += newsgroups.getThreads();
			DailyNewsgroupData newsgroupData = new DailyNewsgroupData();
			newsgroupData.setNewsgroupName(newsgroups.getNewsgroupName());
			newsgroupData.setNumberOfThreads(newsgroups.getThreads());
			dailyThreads.getNewsgroups().add(newsgroupData);
		}

		for (User user: usedUsers.getUsers()) {
			numberOfArticles += user.getArticles();
			numberOfReplies += user.getReplies();
			numberOfRequests += user.getRequests();
		}
		
		dailyThreads.setNumberOfThreads(sumOfThreads);

		float avgArticles = 0,
			  avgReplies = 0,
			  avgRequests = 0;
		
		if (threadIdSet.size()>0) {
			avgArticles = ((float) numberOfArticles) / threadIdSet.size();
			avgReplies = ((float) numberOfReplies) / threadIdSet.size();
			avgRequests = ((float) numberOfRequests) / threadIdSet.size();
		}
		
		dailyThreads.setAverageArticlesPerThread(avgArticles);
		dailyThreads.setAverageRepliesPerThread(avgReplies);
		dailyThreads.setAverageRequestsPerThread(avgRequests);
		
		avgArticles = 0;
		avgReplies = 0;
		avgRequests = 0;

		if (usedUsers.getUsers().size()>0) {
			avgArticles = ((float) numberOfArticles) / usedUsers.getUsers().size();
			avgReplies = ((float) numberOfReplies) / usedUsers.getUsers().size();
			avgRequests = ((float) numberOfRequests) / usedUsers.getUsers().size();
		}

		dailyThreads.setAverageArticlesPerUser(avgArticles);
		dailyThreads.setAverageRepliesPerUser(avgReplies);
		dailyThreads.setAverageRequestsPerUser(avgRequests);

		return dailyThreads;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ThreadsTransMetricProvider.class.getCanonicalName(),
							 ActiveUsersTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "threadspernewsgroup";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of Threads Per Day Per Newsgroup";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of threads per day for each newsgroup separately.";
	}
}
