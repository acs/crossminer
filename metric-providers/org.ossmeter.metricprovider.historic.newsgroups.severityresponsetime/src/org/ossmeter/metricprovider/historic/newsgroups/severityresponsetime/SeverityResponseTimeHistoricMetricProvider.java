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
package org.ossmeter.metricprovider.historic.newsgroups.severityresponsetime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ossmeter.metricprovider.historic.newsgroups.severityresponsetime.model.NewsgroupsSeverityResponseTimeHistoricMetric;
import org.ossmeter.metricprovider.historic.newsgroups.severityresponsetime.model.SeverityLevel;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.ThreadsRequestsRepliesTransMetricProvider;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.model.NewsgroupsThreadsRequestsRepliesTransMetric;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.model.ThreadStatistics;
import org.ossmeter.metricprovider.trans.severityclassification.SeverityClassificationTransMetricProvider;
import org.ossmeter.metricprovider.trans.severityclassification.model.NewsgroupThreadData;
import org.ossmeter.metricprovider.trans.severityclassification.model.SeverityClassificationTransMetric;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;
import org.ossmeter.repository.model.sourceforge.Discussion;

import com.googlecode.pongo.runtime.Pongo;

public class SeverityResponseTimeHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = "org.ossmeter.metricprovider.historic.newsgroups.severityresponsetime";

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
		NewsgroupsSeverityResponseTimeHistoricMetric metric = new NewsgroupsSeverityResponseTimeHistoricMetric();
		
		if (uses.size()==2) {

			SeverityClassificationTransMetric severityClassifier = 
					 ((SeverityClassificationTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
			 
			NewsgroupsThreadsRequestsRepliesTransMetric threadsRequestsReplies = 
					((ThreadsRequestsRepliesTransMetricProvider)uses.get(1)).adapt(context.getProjectDB(project));

			Map<String, Integer> severities = new HashMap<String, Integer>();
			Map<String, Long> durations = new HashMap<String, Long>();
			
			for (NewsgroupThreadData newsgroupThreadData: severityClassifier.getNewsgroupThreads()) {
				 
				String newsgroupName = newsgroupThreadData.getNewsgroupName();
				 
				String severity = newsgroupThreadData.getSeverity();
				addOrIncrease(severities, severity);
			 
				ThreadStatistics threadStatistics = null;
				Iterable<ThreadStatistics> threadStatisticsIt = threadsRequestsReplies.getThreads().
						 							 	   find(ThreadStatistics.NEWSGROUPNAME.eq(newsgroupName),
						 							 			 ThreadStatistics.THREADID.eq(newsgroupThreadData.getThreadId()));
				for (ThreadStatistics ts: threadStatisticsIt) threadStatistics = ts;

				if ((threadStatistics!=null) && threadStatistics.getAnswered()) {
					addOrIncrease(durations, severity, threadStatistics.getResponseDurationSec());
				}

			}
			 
			 for (String severity: severities.keySet()) {
				 int numberOfSeverityThreads = severities.get(severity);
				 SeverityLevel severityLevel = new SeverityLevel();
				 metric.getSeverityLevels().add(severityLevel);
				 severityLevel.setSeverityLevel(severity);
				 severityLevel.setNumberOfThreads(numberOfSeverityThreads);
				 
				 long duration = getValueLong(durations, severity);
				 long avgResponseTime = 0;
				 if (duration > 0)
					 avgResponseTime = computeAverageDuration(duration, numberOfSeverityThreads);
				 severityLevel.setAvgResponseTime(avgResponseTime);
				 String avgResponseTimeFormatted = format(avgResponseTime);
				 severityLevel.setAvgResponseTimeFormatted(avgResponseTimeFormatted);
			 }
			 
		}
		return metric;
	
	}
	
	private static final long SECONDS_DAY = 24 * 60 * 60;

	private long computeAverageDuration(long sumOfDurations, int threads) {
		if (threads>0)
			return sumOfDurations/threads;
		return 0;
	}

	private String format(long avgDuration) {
		String formatted = null;
		if (avgDuration>0) {
			int days = (int) (avgDuration / SECONDS_DAY);
			long lessThanDay = (avgDuration % SECONDS_DAY);
			formatted = days + ":" + 
					DurationFormatUtils.formatDuration(lessThanDay*1000, "HH:mm:ss:SS");
		} else {
			formatted = 0 + ":" + 
					DurationFormatUtils.formatDuration(0, "HH:mm:ss:SS");
		}
		return formatted;
	}
			
	private void addOrIncrease(Map<String, Integer> map, String item) {
		if (map.containsKey(item))
			map.put(item, map.get(item) + 1);
		else
			map.put(item, + 1);
	}
	
	private void addOrIncrease(Map<String, Long> map, String item, long increment) {
		if (map.containsKey(item))
			map.put(item, map.get(item) + increment);
		else
			map.put(item, increment);
	}

	private long getValueLong(Map<String, Long> map,	String component) {
		if (!map.containsKey(component))
			return 0;
		return map.get(component);
	}
	
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(SeverityClassificationTransMetricProvider.class.getCanonicalName(),
							 ThreadsRequestsRepliesTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "newsgroupseveritysentiment";
	}

	@Override
	public String getFriendlyName() {
		return "Sentiment Per Newsgroup Thread Severity Levels Per Day";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the average sentiment, the sentiment at " +
			   "the beginning of threads and the sentiment at the end of threads " +
			   "per severity level, in newsgroup threads submitted every day.";
	}
}
