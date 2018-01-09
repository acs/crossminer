/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.recommendation.highlight;

import java.util.Collections;
import java.util.List;

import org.crossmeter.plugin.ui.abstractmvc.AbstractModel;
import org.crossmeter.commons.highlight.IHighlightable;

public class SourceCodeHighlighterModel extends AbstractModel {
	
	private List<IHighlightable> highlights;
	
	public SourceCodeHighlighterModel(List<IHighlightable> highlightables) {
		highlights = highlightables;
	}
	
	public List<IHighlightable> getHighlightables() {
		return Collections.unmodifiableList(highlights);
	}
	
	@Override
	public void dispose() {
		highlights.clear();
	}
	
}
