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
package org.ossmeter.repository.model;

import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class ManagerAnalysis extends Pongo {
	
	// protected region custom-fields-and-methods on begin
	public static ManagerAnalysis create(String manager, String method, String project, Date analysis, Date execution){
		ManagerAnalysis m = new ManagerAnalysis();
		m.setManager(manager);
		m.setMethod(method);
		m.setProjectId(project);
		m.setAnalysisDate(analysis);
		m.setExecutionDate(execution);
		
		return m;
	}
	// protected region custom-fields-and-methods end
	
	
	
	public ManagerAnalysis() { 
		super();
		MANAGER.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
		METHOD.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
		PROJECTID.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
		ANALYSISDATE.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
		EXECUTIONDATE.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
		MILLISTAKEN.setOwningType("org.ossmeter.repository.model.MetricAnalysis");
	}
	
	public static StringQueryProducer MANAGER = new StringQueryProducer("manager"); 
	public static StringQueryProducer METHOD = new StringQueryProducer("method");
	public static StringQueryProducer PROJECTID = new StringQueryProducer("projectId"); 
	public static StringQueryProducer ANALYSISDATE = new StringQueryProducer("analysisDate"); 
	public static StringQueryProducer EXECUTIONDATE = new StringQueryProducer("executionDate"); 
	public static NumericalQueryProducer MILLISTAKEN = new NumericalQueryProducer("millisTaken");
	
	
	public String getManager() {
		return parseString(dbObject.get("manager")+"", "");
	}
	
	public ManagerAnalysis setManager(String manager) {
		dbObject.put("manager", manager);
		notifyChanged();
		return this;
	}
	public String getMethod() {
		return parseString(dbObject.get("method")+"", "");
	}
	
	public ManagerAnalysis setMethod(String method) {
		dbObject.put("method", method);
		notifyChanged();
		return this;
	}
	public String getProjectId() {
		return parseString(dbObject.get("projectId")+"", "");
	}
	
	public ManagerAnalysis setProjectId(String projectId) {
		dbObject.put("projectId", projectId);
		notifyChanged();
		return this;
	}
	public Date getAnalysisDate() {
		return parseDate(dbObject.get("analysisDate")+"", null);
	}
	
	public ManagerAnalysis setAnalysisDate(Date analysisDate) {
		dbObject.put("analysisDate", analysisDate);
		notifyChanged();
		return this;
	}
	public Date getExecutionDate() {
		return parseDate(dbObject.get("executionDate")+"", null);
	}
	
	public ManagerAnalysis setExecutionDate(Date executionDate) {
		dbObject.put("executionDate", executionDate);
		notifyChanged();
		return this;
	}
	public double getMillisTaken() {
		return parseDouble(dbObject.get("millisTaken")+"", 0.0d);
	}
	
	public ManagerAnalysis setMillisTaken(double millisTaken) {
		dbObject.put("millisTaken", millisTaken);
		notifyChanged();
		return this;
	}
	
	
	
	
}