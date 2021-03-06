/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.business.dto;

import java.util.List;

/**
 * @author Juri Di Rocco
 *
 */
public class Recommendation {

	private List<RecommendationItem> recommendationItems;

	public List<RecommendationItem> getRecommendationItems() {
		return recommendationItems;
	}

	public void setRecommendationItems(List<RecommendationItem> recommendationItems) {
		this.recommendationItems = recommendationItems;
	}
}
