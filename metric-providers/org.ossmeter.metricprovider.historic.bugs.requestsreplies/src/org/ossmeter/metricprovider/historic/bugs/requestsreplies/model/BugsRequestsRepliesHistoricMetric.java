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
package org.ossmeter.metricprovider.historic.bugs.requestsreplies.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class BugsRequestsRepliesHistoricMetric extends Pongo {
	
	protected List<DailyBugTrackerData> bugs = null;
	
	
	public BugsRequestsRepliesHistoricMetric() { 
		super();
		dbObject.put("bugs", new BasicDBList());
		NUMBEROFREQUESTS.setOwningType("org.ossmeter.metricprovider.historic.bugs.requestsreplies.model.BugsRequestsRepliesHistoricMetric");
		NUMBEROFREPLIES.setOwningType("org.ossmeter.metricprovider.historic.bugs.requestsreplies.model.BugsRequestsRepliesHistoricMetric");
		CUMULATIVENUMBEROFREQUESTS.setOwningType("org.ossmeter.metricprovider.historic.bugs.requestsreplies.model.BugsRequestsRepliesHistoricMetric");
		CUMULATIVENUMBEROFREPLIES.setOwningType("org.ossmeter.metricprovider.historic.bugs.requestsreplies.model.BugsRequestsRepliesHistoricMetric");
	}
	
	public static NumericalQueryProducer NUMBEROFREQUESTS = new NumericalQueryProducer("numberOfRequests");
	public static NumericalQueryProducer NUMBEROFREPLIES = new NumericalQueryProducer("numberOfReplies");
	public static NumericalQueryProducer CUMULATIVENUMBEROFREQUESTS = new NumericalQueryProducer("cumulativeNumberOfRequests");
	public static NumericalQueryProducer CUMULATIVENUMBEROFREPLIES = new NumericalQueryProducer("cumulativeNumberOfReplies");
	
	
	public int getNumberOfRequests() {
		return parseInteger(dbObject.get("numberOfRequests")+"", 0);
	}
	
	public BugsRequestsRepliesHistoricMetric setNumberOfRequests(int numberOfRequests) {
		dbObject.put("numberOfRequests", numberOfRequests);
		notifyChanged();
		return this;
	}
	public int getNumberOfReplies() {
		return parseInteger(dbObject.get("numberOfReplies")+"", 0);
	}
	
	public BugsRequestsRepliesHistoricMetric setNumberOfReplies(int numberOfReplies) {
		dbObject.put("numberOfReplies", numberOfReplies);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfRequests() {
		return parseInteger(dbObject.get("cumulativeNumberOfRequests")+"", 0);
	}
	
	public BugsRequestsRepliesHistoricMetric setCumulativeNumberOfRequests(int cumulativeNumberOfRequests) {
		dbObject.put("cumulativeNumberOfRequests", cumulativeNumberOfRequests);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfReplies() {
		return parseInteger(dbObject.get("cumulativeNumberOfReplies")+"", 0);
	}
	
	public BugsRequestsRepliesHistoricMetric setCumulativeNumberOfReplies(int cumulativeNumberOfReplies) {
		dbObject.put("cumulativeNumberOfReplies", cumulativeNumberOfReplies);
		notifyChanged();
		return this;
	}
	
	
	public List<DailyBugTrackerData> getBugs() {
		if (bugs == null) {
			bugs = new PongoList<DailyBugTrackerData>(this, "bugs", true);
		}
		return bugs;
	}
	
	
}