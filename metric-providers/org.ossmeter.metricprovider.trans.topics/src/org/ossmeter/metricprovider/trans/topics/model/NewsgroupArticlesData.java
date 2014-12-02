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
package org.ossmeter.metricprovider.trans.topics.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class NewsgroupArticlesData extends Pongo {
	
	
	
	public NewsgroupArticlesData() { 
		super();
		NEWSGROUPNAME.setOwningType("org.ossmeter.metricprovider.trans.topics.model.NewsgroupArticlesData");
		ARTICLENUMBER.setOwningType("org.ossmeter.metricprovider.trans.topics.model.NewsgroupArticlesData");
		SUBJECT.setOwningType("org.ossmeter.metricprovider.trans.topics.model.NewsgroupArticlesData");
		TEXT.setOwningType("org.ossmeter.metricprovider.trans.topics.model.NewsgroupArticlesData");
		DATE.setOwningType("org.ossmeter.metricprovider.trans.topics.model.NewsgroupArticlesData");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsgroupName"); 
	public static NumericalQueryProducer ARTICLENUMBER = new NumericalQueryProducer("articleNumber");
	public static StringQueryProducer SUBJECT = new StringQueryProducer("subject"); 
	public static StringQueryProducer TEXT = new StringQueryProducer("text"); 
	public static StringQueryProducer DATE = new StringQueryProducer("date"); 
	
	
	public String getNewsgroupName() {
		return parseString(dbObject.get("newsgroupName")+"", "");
	}
	
	public NewsgroupArticlesData setNewsgroupName(String newsgroupName) {
		dbObject.put("newsgroupName", newsgroupName);
		notifyChanged();
		return this;
	}
	public int getArticleNumber() {
		return parseInteger(dbObject.get("articleNumber")+"", 0);
	}
	
	public NewsgroupArticlesData setArticleNumber(int articleNumber) {
		dbObject.put("articleNumber", articleNumber);
		notifyChanged();
		return this;
	}
	public String getSubject() {
		return parseString(dbObject.get("subject")+"", "");
	}
	
	public NewsgroupArticlesData setSubject(String subject) {
		dbObject.put("subject", subject);
		notifyChanged();
		return this;
	}
	public String getText() {
		return parseString(dbObject.get("text")+"", "");
	}
	
	public NewsgroupArticlesData setText(String text) {
		dbObject.put("text", text);
		notifyChanged();
		return this;
	}
	public String getDate() {
		return parseString(dbObject.get("date")+"", "");
	}
	
	public NewsgroupArticlesData setDate(String date) {
		dbObject.put("date", date);
		notifyChanged();
		return this;
	}
	
	
	
	
}