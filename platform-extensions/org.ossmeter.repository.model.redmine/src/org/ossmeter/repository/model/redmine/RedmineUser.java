/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Davide Di Ruscio - Implementation.
 *******************************************************************************/
package org.ossmeter.repository.model.redmine;

import com.googlecode.pongo.runtime.querying.*;


public class RedmineUser extends org.ossmeter.repository.model.Person {
	
	
	
	public RedmineUser() { 
		super();
		super.setSuperTypes("org.ossmeter.repository.model.redmine.Person");
		LOGIN.setOwningType("org.ossmeter.repository.model.redmine.RedmineUser");
		ID.setOwningType("org.ossmeter.repository.model.redmine.RedmineUser");
	}
	
	public static StringQueryProducer LOGIN = new StringQueryProducer("login"); 
	public static StringQueryProducer ID = new StringQueryProducer("ID"); 
	
	
	public String getLogin() {
		return parseString(dbObject.get("login")+"", "");
	}
	
	public RedmineUser setLogin(String login) {
		dbObject.put("login", login);
		notifyChanged();
		return this;
	}
	public String getID() {
		return parseString(dbObject.get("ID")+"", "");
	}
	
	public RedmineUser setID(String ID) {
		dbObject.put("ID", ID);
		notifyChanged();
		return this;
	}
	
	
	
	
}