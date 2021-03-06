/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.ui.abstractmvc;

import org.eclipse.ui.services.IDisposable;

public abstract class DisplayableView<DisplayType extends IDisposable> extends AbstractView {
	private final DisplayType display;
	
	public DisplayableView(DisplayType display) {
		this.display = display;
	}
	
	public DisplayType getDisplay() {
		return display;
	}
	
	@Override
	public void close() {
		super.close();
		display.dispose();
	}
}
