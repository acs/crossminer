/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    James Williams - Implementation.
 *******************************************************************************/
package org.ossmeter.platform.tests.metricproviders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class AbstractHistoricalMetricProviderTest {

	@Test
	public void test() throws Exception {
		MyHistoricalMP mp = new MyHistoricalMP();
		
		Mongo mongo = new Mongo();
		String dbName = "TestAbstractHistoricalMP";
		DB db = mongo.getDB(dbName);
		DBCollection col = db.getCollection(mp.getIdentifier());
		
		// Create some random data 
		for (Date d : Date.range(new Date("20140101"), new Date("20140120"))) {
			Pongo p = new Pongo();
			DBObject dbObject = p.getDbObject();
			dbObject.put("__date", d.toString());
			dbObject.put("__datetime", d.toJavaDate());
			col.insert(dbObject);
		}
		
		MetricProviderContext context = mock(MetricProviderContext.class);
		Project p = new Project();
		
		when(context.getProjectDB(p)).thenReturn(db);
		
		
		Date start = new Date("20140115");
		Date end = new Date("20140119");
		
		List<Pongo> plist = mp.getHistoricalMeasurements(context, p, start, end);

		assertEquals(5, plist.size());
		
		// Clean up
		mongo.dropDatabase(dbName);
		mongo.close();
	}

	class MyHistoricalMP extends AbstractHistoricalMetricProvider {

		@Override
		public Pongo measure(Project project) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier() {
			return "Foo";
		}

		@Override
		public String getShortIdentifier() {
			return "foo";
		}

		@Override
		public String getFriendlyName() {
			return "foo";
		}

		@Override
		public String getSummaryInformation() {
			return "foo";
		}

		@Override
		public boolean appliesTo(Project project) {
			return true;
		}

		@Override
		public void setUses(List<IMetricProvider> uses) { }

		@Override
		public List<String> getIdentifiersOfUses() {
			return Collections.emptyList();
		}

		@Override
		public void setMetricProviderContext(MetricProviderContext context) {
		}
	}
}

