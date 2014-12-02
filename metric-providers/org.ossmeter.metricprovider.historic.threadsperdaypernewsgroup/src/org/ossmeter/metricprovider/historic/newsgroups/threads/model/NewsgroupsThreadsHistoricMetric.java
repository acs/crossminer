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
package org.ossmeter.metricprovider.historic.newsgroups.threads.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class NewsgroupsThreadsHistoricMetric extends Pongo {
	
	protected List<DailyNewsgroupData> newsgroups = null;
	
	
	public NewsgroupsThreadsHistoricMetric() { 
		super();
		dbObject.put("newsgroups", new BasicDBList());
		NUMBEROFTHREADS.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEARTICLESPERTHREAD.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEREQUESTSPERTHREAD.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEREPLIESPERTHREAD.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEARTICLESPERUSER.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEREQUESTSPERUSER.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
		AVERAGEREPLIESPERUSER.setOwningType("org.ossmeter.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric");
	}
	
	public static NumericalQueryProducer NUMBEROFTHREADS = new NumericalQueryProducer("numberOfThreads");
	public static NumericalQueryProducer AVERAGEARTICLESPERTHREAD = new NumericalQueryProducer("averageArticlesPerThread");
	public static NumericalQueryProducer AVERAGEREQUESTSPERTHREAD = new NumericalQueryProducer("averageRequestsPerThread");
	public static NumericalQueryProducer AVERAGEREPLIESPERTHREAD = new NumericalQueryProducer("averageRepliesPerThread");
	public static NumericalQueryProducer AVERAGEARTICLESPERUSER = new NumericalQueryProducer("averageArticlesPerUser");
	public static NumericalQueryProducer AVERAGEREQUESTSPERUSER = new NumericalQueryProducer("averageRequestsPerUser");
	public static NumericalQueryProducer AVERAGEREPLIESPERUSER = new NumericalQueryProducer("averageRepliesPerUser");
	
	
	public int getNumberOfThreads() {
		return parseInteger(dbObject.get("numberOfThreads")+"", 0);
	}
	
	public NewsgroupsThreadsHistoricMetric setNumberOfThreads(int numberOfThreads) {
		dbObject.put("numberOfThreads", numberOfThreads);
		notifyChanged();
		return this;
	}
	public float getAverageArticlesPerThread() {
		return parseFloat(dbObject.get("averageArticlesPerThread")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageArticlesPerThread(float averageArticlesPerThread) {
		dbObject.put("averageArticlesPerThread", averageArticlesPerThread);
		notifyChanged();
		return this;
	}
	public float getAverageRequestsPerThread() {
		return parseFloat(dbObject.get("averageRequestsPerThread")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageRequestsPerThread(float averageRequestsPerThread) {
		dbObject.put("averageRequestsPerThread", averageRequestsPerThread);
		notifyChanged();
		return this;
	}
	public float getAverageRepliesPerThread() {
		return parseFloat(dbObject.get("averageRepliesPerThread")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageRepliesPerThread(float averageRepliesPerThread) {
		dbObject.put("averageRepliesPerThread", averageRepliesPerThread);
		notifyChanged();
		return this;
	}
	public float getAverageArticlesPerUser() {
		return parseFloat(dbObject.get("averageArticlesPerUser")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageArticlesPerUser(float averageArticlesPerUser) {
		dbObject.put("averageArticlesPerUser", averageArticlesPerUser);
		notifyChanged();
		return this;
	}
	public float getAverageRequestsPerUser() {
		return parseFloat(dbObject.get("averageRequestsPerUser")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageRequestsPerUser(float averageRequestsPerUser) {
		dbObject.put("averageRequestsPerUser", averageRequestsPerUser);
		notifyChanged();
		return this;
	}
	public float getAverageRepliesPerUser() {
		return parseFloat(dbObject.get("averageRepliesPerUser")+"", 0.0f);
	}
	
	public NewsgroupsThreadsHistoricMetric setAverageRepliesPerUser(float averageRepliesPerUser) {
		dbObject.put("averageRepliesPerUser", averageRepliesPerUser);
		notifyChanged();
		return this;
	}
	
	
	public List<DailyNewsgroupData> getNewsgroups() {
		if (newsgroups == null) {
			newsgroups = new PongoList<DailyNewsgroupData>(this, "newsgroups", true);
		}
		return newsgroups;
	}
	
	
}