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

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class ThreadDataCollection extends PongoCollection<ThreadData> {
	
	public ThreadDataCollection(DBCollection dbCollection) {
		super(dbCollection);
		createIndex("threadId");
	}
	
	public Iterable<ThreadData> findById(String id) {
		return new IteratorIterable<ThreadData>(new PongoCursorIterator<ThreadData>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	public Iterable<ThreadData> findByThreadId(int q) {
		return new IteratorIterable<ThreadData>(new PongoCursorIterator<ThreadData>(this, dbCollection.find(new BasicDBObject("threadId", q + ""))));
	}
	
	public ThreadData findOneByThreadId(int q) {
		ThreadData threadData = (ThreadData) PongoFactory.getInstance().createPongo(dbCollection.findOne(new BasicDBObject("threadId", q + "")));
		if (threadData != null) {
			threadData.setPongoCollection(this);
		}
		return threadData;
	}
	

	public long countByThreadId(int q) {
		return dbCollection.count(new BasicDBObject("threadId", q + ""));
	}
	
	@Override
	public Iterator<ThreadData> iterator() {
		return new PongoCursorIterator<ThreadData>(this, dbCollection.find());
	}
	
	public void add(ThreadData threadData) {
		super.add(threadData);
	}
	
	public void remove(ThreadData threadData) {
		super.remove(threadData);
	}
	
}