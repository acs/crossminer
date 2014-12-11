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

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class ManagerAnalysisCollection extends PongoCollection<ManagerAnalysis> {
	
	public ManagerAnalysisCollection(DBCollection dbCollection) {
		super(dbCollection);
	}
	
	public Iterable<ManagerAnalysis> findById(String id) {
		return new IteratorIterable<ManagerAnalysis>(new PongoCursorIterator<ManagerAnalysis>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	
	@Override
	public Iterator<ManagerAnalysis> iterator() {
		return new PongoCursorIterator<ManagerAnalysis>(this, dbCollection.find());
	}
	
	public void add(ManagerAnalysis managerAnalysis) {
		super.add(managerAnalysis);
	}
	
	public void remove(ManagerAnalysis managerAnalysis) {
		super.remove(managerAnalysis);
	}
	
}