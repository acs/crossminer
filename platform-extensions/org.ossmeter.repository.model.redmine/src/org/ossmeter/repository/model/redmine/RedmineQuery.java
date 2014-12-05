/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Davide Di Ruscio - Implementation,
 *    Juri Di Rocco - Implementation.
 *******************************************************************************/
package org.ossmeter.repository.model.redmine;

import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class RedmineQuery extends Pongo {
	
	
	
	public RedmineQuery() { 
		super();
		NAME.setOwningType("org.ossmeter.repository.model.redmine.RedmineQuery");
	}
	
	public static StringQueryProducer NAME = new StringQueryProducer("name"); 
	
	
	public String getName() {
		return parseString(dbObject.get("name")+"", "");
	}
	
	public RedmineQuery setName(String name) {
		dbObject.put("name", name);
		notifyChanged();
		return this;
	}
	
	
	
	
}