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
package org.ossmeter.repository.model.eclipse;

import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class Release extends org.ossmeter.repository.model.NamedElement {
	
	
	
	public Release() { 
		super();
		super.setSuperTypes("org.ossmeter.repository.model.eclipse.NamedElement");
		TYPE.setOwningType("org.ossmeter.repository.model.eclipse.Release");
		DATE.setOwningType("org.ossmeter.repository.model.eclipse.Release");
		LINK.setOwningType("org.ossmeter.repository.model.eclipse.Release");
	}
	
	public static StringQueryProducer TYPE = new StringQueryProducer("type"); 
	public static StringQueryProducer DATE = new StringQueryProducer("date"); 
	public static StringQueryProducer LINK = new StringQueryProducer("link"); 
	
	
	public String getType() {
		return parseString(dbObject.get("type")+"", "");
	}
	
	public Release setType(String type) {
		dbObject.put("type", type);
		notifyChanged();
		return this;
	}
	public String getDate() {
		return parseString(dbObject.get("date")+"", "");
	}
	
	public Release setDate(String date) {
		dbObject.put("date", date);
		notifyChanged();
		return this;
	}
	public String getLink() {
		return parseString(dbObject.get("link")+"", "");
	}
	
	public Release setLink(String link) {
		dbObject.put("link", link);
		notifyChanged();
		return this;
	}
	
	
	
	
}