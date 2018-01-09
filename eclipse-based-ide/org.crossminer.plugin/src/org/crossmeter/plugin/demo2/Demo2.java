/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.demo2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crossmeter.plugin.context.librarystatus.LibraryStatusDetector;
import org.crossmeter.plugin.knowledgebase.access.KnowledgeBaseAccessManager;
import org.crossmeter.plugin.logger.Logger;
import org.crossmeter.plugin.logger.LoggerMessageKind;
import org.crossmeter.plugin.recommendation.RecommendationManager;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterController;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterModel;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterView;
import org.crossmeter.plugin.utils.Utils;
import org.crossmeter.commons.highlight.IHighlightable;
import org.crossmeter.commons.library.Library;
import org.crossmeter.commons.recommendation.RecommendationKind;
import org.crossmeter.commons.recommendation.RecommendationSet;
import org.crossmeter.commons.recommendation.source.SourceRecommendation;
import org.eclipse.jdt.core.IJavaProject;

public class Demo2 {
	
	public static void start() throws Exception {
		IJavaProject project = Utils.getCurrentlyEditedProject();
		KnowledgeBaseAccessManager knowledgeBaseAccessManager = new KnowledgeBaseAccessManager();
		RecommendationManager recommendationManager = new RecommendationManager(knowledgeBaseAccessManager);
		
		if (project == null)
			return;
		
		// Get used libraries
		List<Library> librariesFromProject = LibraryStatusDetector.getLibrariesFromProject(project);
		
		librariesFromProject.forEach(library -> Logger.log("Library used in project: " + library,
				LoggerMessageKind.LIBRARY));
		
		// Request for newer version of one
		Library chosenLibrary = librariesFromProject.get(0);
		Logger.log("search alternatives for: " + chosenLibrary, LoggerMessageKind.LIBRARY);
		List<Library> alternativeLibraries = knowledgeBaseAccessManager.requestAlternativesOfLibrary(chosenLibrary);
		Library newLibrary = alternativeLibraries.get(0);
		
		Arrays.asList(alternativeLibraries).forEach(library -> Logger.log("Alternative library: " + library,
				LoggerMessageKind.LIBRARY));
		Logger.log("Chosen alternative library: " + newLibrary, LoggerMessageKind.LIBRARY);
		
		// Request some information about this library
		String description = knowledgeBaseAccessManager.requestDescriptionOfLibrary(newLibrary);
		Logger.log("Library description: " + description, LoggerMessageKind.FROM_SERVER);
		//
		// Request for API changes
		RecommendationSet recommendationSet = recommendationManager.requestRecommendationsToUpdateLibraryInProject(
				project, chosenLibrary, newLibrary);
		
		// List Recommendations
		recommendationSet.forEach(recommendation -> Logger.log("Recommendation: " + recommendation,
				LoggerMessageKind.FROM_CLIENT));
		
		// Mark updatable code chunks
		List<SourceRecommendation> sourceRecommendations = Utils.filterType(recommendationSet.asList(),
				RecommendationKind.SOURCE_REPLACE);
		List<IHighlightable> highlightables = sourceRecommendations.stream().map(IHighlightable.class::cast).collect(
				Collectors.toList());
		
		SourceCodeHighlighterModel sourceCodeHighlighterModel = new SourceCodeHighlighterModel(highlightables);
		SourceCodeHighlighterView sourceCodeHighlighterView = new SourceCodeHighlighterView(project.getProject());
		SourceCodeHighlighterController sourceCodeHighlighterController = new SourceCodeHighlighterController(
				sourceCodeHighlighterModel, sourceCodeHighlighterView);
		
		// Apply recommendations, which we receive from the server
		recommendationManager.applyRecommendations(project.getProject(), recommendationSet);
		
		sourceCodeHighlighterController.close();
	}
	
}
