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
package org.ossmeter.factoid.bugs.emotion;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.trans.bugs.emotions.EmotionsTransMetricProvider;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.BugsEmotionsTransMetric;
import org.ossmeter.metricprovider.trans.bugs.emotions.model.EmotionDimension;
import org.ossmeter.platform.AbstractFactoidMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.factoids.Factoid;
import org.ossmeter.platform.factoids.StarRating;
import org.ossmeter.repository.model.Project;

public class BugsChannelEmotionFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "BugChannelEmotion";
	}

	@Override
	public String getFriendlyName() {
		return "Bug Tracker Emotion";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "summaryblah"; // This method will NOT be removed in a later version.
	}

	@Override
	public boolean appliesTo(Project project) {
	    return !project.getBugTrackingSystems().isEmpty();	   
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(EmotionsTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.BUGS);
		factoid.setName(getFriendlyName());

		BugsEmotionsTransMetric emotionsTransMetric = 
				((EmotionsTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		StringBuffer stringBuffer = new StringBuffer();
		
		float positiveEmotionPercentageSum = 0,
			  negativeEmotionPercentageSum = 0,
			  mostCommonPositivePercentage = 0,
			  mostCommonNegativePercentage = 0;
		String mostCommonPositive = "",
			   mostCommonNegative = "";
		for (EmotionDimension dimension: emotionsTransMetric.getDimensions()) {
			if ( ( dimension.getEmotionLabel().equals("anger") )
			  || ( dimension.getEmotionLabel().equals("sadness") )
			  || ( dimension.getEmotionLabel().equals("disgust") )
			  || ( dimension.getEmotionLabel().equals("fear") ) ) {
				negativeEmotionPercentageSum += dimension.getCumulativePercentage();
				if ( mostCommonNegativePercentage < dimension.getCumulativePercentage() ) {
					mostCommonNegativePercentage = dimension.getCumulativePercentage();
					mostCommonNegative = dimension.getEmotionLabel();
				}
			} else {
				positiveEmotionPercentageSum += dimension.getCumulativePercentage();
				if ( mostCommonPositivePercentage < dimension.getCumulativePercentage() ) {
					mostCommonPositivePercentage = dimension.getCumulativePercentage();
					mostCommonPositive = dimension.getEmotionLabel();
				}
			}
		}
		
		float positiveEmotionPercentage = positiveEmotionPercentageSum / 4,
			  negativeEmotionPercentage = negativeEmotionPercentageSum / 4;
		
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		if ( ( positiveEmotionPercentage > 80 ) || ( negativeEmotionPercentage < 35 ) ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( ( positiveEmotionPercentage > 65 ) || ( negativeEmotionPercentage < 50 ) ) {
			factoid.setStars(StarRating.THREE);
		} else if ( ( positiveEmotionPercentage > 50 ) || ( negativeEmotionPercentage < 65 ) ) {
			factoid.setStars(StarRating.TWO);
		} else {
			factoid.setStars(StarRating.ONE);
		}
		
		stringBuffer.append(decimalFormat.format(positiveEmotionPercentage));
		stringBuffer.append(" % of comments and postings express positive emotions, while ");
		stringBuffer.append(decimalFormat.format(negativeEmotionPercentage));
		stringBuffer.append(" % express negative emotions. ");
		stringBuffer.append("A comment or posting can express both positive and negative emotions at the same time.\n");

		stringBuffer.append("The most common negative emotion is ");
		stringBuffer.append(mostCommonNegative);
		stringBuffer.append(", while the most common positive emotions is ");
		stringBuffer.append(mostCommonPositive);
		stringBuffer.append(".\n");
		
		factoid.setFactoid(stringBuffer.toString());
	}

}
