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
package org.ossmeter.metricprovider.historic.bugs.topics.model;

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class BugTopicCollection extends PongoCollection<BugTopic> {
	
	public BugTopicCollection(DBCollection dbCollection) {
		super(dbCollection);
		createIndex("bugTrackerId");
	}
	
	public Iterable<BugTopic> findById(String id) {
		return new IteratorIterable<BugTopic>(new PongoCursorIterator<BugTopic>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	public Iterable<BugTopic> findByBugTrackerId(String q) {
		return new IteratorIterable<BugTopic>(new PongoCursorIterator<BugTopic>(this, dbCollection.find(new BasicDBObject("bugTrackerId", q + ""))));
	}
	
	public BugTopic findOneByBugTrackerId(String q) {
		BugTopic bugTopic = (BugTopic) PongoFactory.getInstance().createPongo(dbCollection.findOne(new BasicDBObject("bugTrackerId", q + "")));
		if (bugTopic != null) {
			bugTopic.setPongoCollection(this);
		}
		return bugTopic;
	}
	

	public long countByBugTrackerId(String q) {
		return dbCollection.count(new BasicDBObject("bugTrackerId", q + ""));
	}
	
	@Override
	public Iterator<BugTopic> iterator() {
		return new PongoCursorIterator<BugTopic>(this, dbCollection.find());
	}
	
	public void add(BugTopic bugTopic) {
		super.add(bugTopic);
	}
	
	public void remove(BugTopic bugTopic) {
		super.remove(bugTopic);
	}
	
}