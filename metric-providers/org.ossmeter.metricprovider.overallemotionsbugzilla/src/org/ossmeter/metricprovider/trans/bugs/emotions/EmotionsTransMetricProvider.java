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
package org.ossmeter.metricprovider.trans.bugs.emotions;

import java.util.Collections;
import java.util.List;

import org.ossmeter.metricprovider.trans.bugs.emotions.model.BugTrackerData;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.BugsEmotionsTransMetric;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.EmotionDimension;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemComment;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemProjectDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.PlatformBugTrackingSystemManager;
import org.ossmeter.repository.model.BugTrackingSystem;
import org.ossmeter.repository.model.Project;
import org.ossmeter.sentimentclassifier.opennlptartarus.libsvm.ClassificationInstance;
import org.ossmeter.sentimentclassifier.opennlptartarus.libsvm.EmotionalDimensions;

import com.mongodb.DB;

public class EmotionsTransMetricProvider implements ITransientMetricProvider<BugsEmotionsTransMetric>{

	protected PlatformBugTrackingSystemManager platformBugTrackingSystemManager;

	@Override
	public String getIdentifier() {
		return EmotionsTransMetricProvider.class.getCanonicalName();
	}

	@Override
	public boolean appliesTo(Project project) {
	    return !project.getBugTrackingSystems().isEmpty();	   
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		// DO NOTHING -- we don't use anything
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Collections.emptyList();
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.platformBugTrackingSystemManager = context.getPlatformBugTrackingSystemManager();
	}

	@Override
	public BugsEmotionsTransMetric adapt(DB db) {
		return new BugsEmotionsTransMetric(db);
	}

	@Override
	public void measure(Project project, ProjectDelta projectDelta, BugsEmotionsTransMetric db) {
		BugTrackingSystemProjectDelta delta = projectDelta.getBugTrackingSystemDelta();
		
		for (BugTrackingSystemDelta bugTrackingSystemDelta : delta.getBugTrackingSystemDeltas()) {
			
			BugTrackingSystem bugTracker = bugTrackingSystemDelta.getBugTrackingSystem();
			Iterable<BugTrackerData> bugTrackerDataIt = 
					db.getBugTrackerData().find(BugTrackerData.BUGTRACKERID.eq(bugTracker.getOSSMeterId()));
			BugTrackerData bugTrackerData = null;
			for (BugTrackerData bd:  bugTrackerDataIt) bugTrackerData = bd;
			if (bugTrackerData == null) {
				bugTrackerData = new BugTrackerData();
				bugTrackerData.setBugTrackerId(bugTracker.getOSSMeterId());
				bugTrackerData.setNumberOfComments(0);
				bugTrackerData.setCumulativeNumberOfComments(0);
				db.getBugTrackerData().add(bugTrackerData);
			}
			bugTrackerData.setNumberOfComments(bugTrackingSystemDelta.getComments().size());
			bugTrackerData.setCumulativeNumberOfComments(bugTrackerData.getCumulativeNumberOfComments() + 
					bugTrackingSystemDelta.getComments().size());
			
			db.sync();
			
			Iterable<EmotionDimension> emotionIt = db.getDimensions().
					find(EmotionDimension.BUGTRACKERID.eq(bugTracker.getOSSMeterId()));
			for (EmotionDimension emotion:  emotionIt) {
				emotion.setNumberOfComments(0);
			}
			
			for (BugTrackingSystemComment comment: bugTrackingSystemDelta.getComments()) {
				
				ClassificationInstance instance = new ClassificationInstance();
				instance.setBugTrackerId(bugTracker.getOSSMeterId());
				instance.setBugId(comment.getBugId());
				instance.setCommentId(comment.getCommentId());
				instance.setText(comment.getText());
				
				String[] emotionalDimensions = EmotionalDimensions.getDimensions(instance).split(",");
				
				for (String dimension: emotionalDimensions) {
					dimension = dimension.trim();
					if (dimension.length()>0) {
						emotionIt = db.getDimensions().find(EmotionDimension.BUGTRACKERID.eq(bugTracker.getOSSMeterId()),
								EmotionDimension.EMOTIONLABEL.eq(dimension));
						EmotionDimension emotion = null;
						for (EmotionDimension em:  emotionIt) emotion = em;
						if (emotion == null) {
							emotion = new EmotionDimension();
							emotion.setBugTrackerId(bugTracker.getOSSMeterId());
							emotion.setEmotionLabel(dimension);
							emotion.setNumberOfComments(0);
							emotion.setCumulativeNumberOfComments(0);
							db.getDimensions().add(emotion);
						}
						emotion.setNumberOfComments(emotion.getNumberOfComments() + 1);
						emotion.setCumulativeNumberOfComments(emotion.getCumulativeNumberOfComments() + 1);
						db.sync();
					}
				}
			}

			db.sync();

			emotionIt = db.getDimensions().
					find(EmotionDimension.BUGTRACKERID.eq(bugTracker.getOSSMeterId()));
			for (EmotionDimension emotion: emotionIt) {
				if ( bugTrackerData.getNumberOfComments() > 0 ) {
					emotion.setPercentage( 
							((float)100*emotion.getNumberOfComments()) / bugTrackerData.getNumberOfComments());
				}
				else
					emotion.setPercentage( (float) 0 );
				if ( bugTrackerData.getCumulativeNumberOfComments() > 0 )
					emotion.setCumulativePercentage( 
						((float)100*emotion.getCumulativeNumberOfComments()) / bugTrackerData.getCumulativeNumberOfComments());
				else
					emotion.setCumulativePercentage( (float) 0 );
			}
			
			db.sync();
			
		}

	}

	@Override
	public String getShortIdentifier() {
		return "Bugemotions";
	}

	@Override
	public String getFriendlyName() {
		return "Emotional Dimensions in Bug Comments";
	}

	@Override
	public String getSummaryInformation() {
		return "Emotional Dimensions in Bug Comments";
	}

}
