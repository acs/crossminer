/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.trans.newsgroups.threads.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class CurrentDate extends Pongo {
	
	
	
	public CurrentDate() { 
		super();
		DATE.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.CurrentDate");
	}
	
	public static StringQueryProducer DATE = new StringQueryProducer("date"); 
	
	
	public String getDate() {
		return parseString(dbObject.get("date")+"", "");
	}
	
	public CurrentDate setDate(String date) {
		dbObject.put("date", date);
		notifyChanged();
		return this;
	}
	
	
	
	
}