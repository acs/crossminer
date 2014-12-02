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
package org.ossmeter.metricprovider.historic.newsgroups.articles;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.historic.newsgroups.articles.model.DailyNewsgroupData;
import org.ossmeter.metricprovider.historic.newsgroups.articles.model.NewsgroupsArticlesHistoricMetric;
import org.ossmeter.metricprovider.trans.newsgroups.articles.ArticlesTransMetricProvider;
import org.ossmeter.metricprovider.trans.newsgroups.articles.model.NewsgroupData;
import org.ossmeter.metricprovider.trans.newsgroups.articles.model.NewsgroupsArticlesTransMetric;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class ArticlesHistoricMetricProvider extends AbstractHistoricalMetricProvider {

	public final static String IDENTIFIER = "org.ossmeter.metricprovider.historic.newsgroups.articles";

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
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		NewsgroupsArticlesHistoricMetric dailyNoa = new NewsgroupsArticlesHistoricMetric();
		if (uses.size()==1) {
			NewsgroupsArticlesTransMetric usedNoa = ((ArticlesTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
			int sumOfArticles = 0,
				sumOfCumulativeArticles = 0;
			for (NewsgroupData newsgroup: usedNoa.getNewsgroups()) {
				int articles = newsgroup.getNumberOfArticles(),
					cumulativeArticles = newsgroup.getCumulativeNumberOfArticles();
				sumOfArticles += articles;
				sumOfCumulativeArticles += cumulativeArticles;
				if ( (articles > 0) || (cumulativeArticles > 0) ) {
					DailyNewsgroupData dailyNewsgroupData = new DailyNewsgroupData();
					dailyNewsgroupData.setNewsgroupName(newsgroup.getNewsgroupName());
					dailyNewsgroupData.setNumberOfArticles(articles);
					dailyNewsgroupData.setCumulativeNumberOfArticles(cumulativeArticles);
					dailyNoa.getNewsgroups().add(dailyNewsgroupData);
				}
			}
			dailyNoa.setNumberOfArticles(sumOfArticles);
			dailyNoa.setCumulativeNumberOfArticles(sumOfCumulativeArticles);
		}
		return dailyNoa;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ArticlesTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "articlespernewsgroup";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of Articles Per Day Per Newsgroup";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of articles per day for each newsgroup separately.";
	}
}
