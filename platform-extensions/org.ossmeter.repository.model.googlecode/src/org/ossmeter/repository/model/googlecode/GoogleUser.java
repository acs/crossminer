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
package org.ossmeter.repository.model.googlecode;

import com.googlecode.pongo.runtime.querying.*;


public class GoogleUser extends org.ossmeter.repository.model.Person {
	
	
	
	public GoogleUser() { 
		super();
		super.setSuperTypes("org.ossmeter.repository.model.googlecode.Person");
		EMAIL.setOwningType("org.ossmeter.repository.model.googlecode.GoogleUser");
	}
	
	public static StringQueryProducer EMAIL = new StringQueryProducer("email"); 
	
	
	public String getEmail() {
		return parseString(dbObject.get("email")+"", "");
	}
	
	public GoogleUser setEmail(String email) {
		dbObject.put("email", email);
		notifyChanged();
		return this;
	}
	
	
	
	
}