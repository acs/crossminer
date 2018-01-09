/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.recommendation.applier;

import org.crossmeter.plugin.logger.Logger;
import org.crossmeter.plugin.logger.LoggerMessageKind;
import org.crossmeter.plugin.recommendation.LibraryUpdater;
import org.crossmeter.plugin.recommendation.LibraryUpdaterException;
import org.crossmeter.commons.recommendation.library.AddSimpleLibraryRecommendation;
import org.eclipse.core.runtime.Path;

public class AddSimpleLibraryApplier implements IRecommendationApplier<AddSimpleLibraryRecommendation> {
	
	@Override
	public void apply(AddSimpleLibraryRecommendation recommendation, RecommendationSetApplier applier) {
		
		Path libraryPath = new Path(recommendation.getDownloadLink());
		try {
			LibraryUpdater.addLibraryToProject(libraryPath);
		} catch (LibraryUpdaterException e) {
			Logger.log(e.getMessage(), LoggerMessageKind.FROM_CLIENT);
			e.printStackTrace();
		}
	}
	
}
