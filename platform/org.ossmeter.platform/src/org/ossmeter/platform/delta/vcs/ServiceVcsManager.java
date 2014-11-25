/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.ossmeter.platform.delta.vcs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ServiceVcsManager extends PlatformVcsManager {
	
	public ServiceVcsManager() {
		getVcsManagers();
	}
	
	public List<IVcsManager> getVcsManagers() {
		if (vcsManagers == null) {
			vcsManagers = new ArrayList<IVcsManager>();
			
			ServiceLoader<IVcsManager> vcsLoader = ServiceLoader.load(IVcsManager.class);
			Iterator<IVcsManager> it = vcsLoader.iterator();
			
			while(it.hasNext()) {
				vcsManagers.add(it.next());
			}
		}
		System.err.println("VcsManagers: " + vcsManagers);
		return vcsManagers;
	}
}
