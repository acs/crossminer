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
package org.ossmeter.metricprovider.historic.bugs.emotions;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.historic.bugs.emotions.model.BugData;
import org.ossmeter.metricprovider.historic.bugs.emotions.model.Dimension;
import org.ossmeter.metricprovider.historic.bugs.emotions.model.BugsEmotionsHistoricMetric;
import org.ossmeter.metricprovider.trans.bugs.emotions.EmotionsTransMetricProvider;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.BugTrackerData;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.BugsEmotionsTransMetric;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.EmotionDimension;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;

public class EmotionsHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = "org.ossmeter.metricprovider.historic.bugs.emotions";

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
	    return !project.getBugTrackingSystems().isEmpty();	   
	}

	@Override
	public Pongo measure(Project project) {
		BugsEmotionsHistoricMetric emotions = new BugsEmotionsHistoricMetric();
		if (uses.size()==1) {
			 BugsEmotionsTransMetric usedEmotions = ((EmotionsTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
			 for (BugTrackerData bugTrackerData: usedEmotions.getBugTrackerData()) {
				 BugData bugData = new BugData();
				 emotions.getBugData().add(bugData);
				 bugData.setBugTrackerId(bugTrackerData.getBugTrackerId());
				 bugData.setCumulativeNumberOfComments(bugTrackerData.getCumulativeNumberOfComments());
				 bugData.setNumberOfComments(bugTrackerData.getNumberOfComments());
			 }
			 
			 for (EmotionDimension emotionDimension: usedEmotions.getDimensions()) {
				 Dimension dimension = new Dimension();
				 emotions.getDimensions().add(dimension);
				 dimension.setBugTrackerId(emotionDimension.getBugTrackerId());
				 dimension.setEmotionLabel(emotionDimension.getEmotionLabel());
				 dimension.setCumulativeNumberOfComments(emotionDimension.getCumulativeNumberOfComments());
				 dimension.setCumulativePercentage(emotionDimension.getCumulativePercentage());
				 dimension.setNumberOfComments(emotionDimension.getNumberOfComments());
				 dimension.setPercentage(emotionDimension.getPercentage());
			 }
		}
		return emotions;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(EmotionsTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "bugemotions";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of Bug Emotions Per Day";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of emotional dimensions in comments submitted every day.";
	}
}
