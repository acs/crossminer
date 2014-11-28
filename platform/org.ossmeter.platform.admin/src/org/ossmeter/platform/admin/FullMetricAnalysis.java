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
package org.ossmeter.platform.admin;

import java.net.UnknownHostException;
import java.util.Date;

import org.ossmeter.platform.Configuration;
import org.restlet.engine.header.Header;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class FullMetricAnalysis extends ServerResource {

		@Get("json")
		public String represent() {
			Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
			if (responseHeaders == null) {
			    responseHeaders = new Series(Header.class);
			    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
			}
			responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));
			responseHeaders.add(new Header("Access-Control-Allow-Methods", "GET"));
			
			try {
				String metricId = (String) getRequest().getAttributes().get("metricId");
				
				Mongo mongo = Configuration.getInstance().getMongoConnection();
				
				DB db = mongo.getDB("ossmeter");
				DBCollection col = db.getCollection("metricAnalysis");
				
				BasicDBObject query = new BasicDBObject("metricId", metricId);
				
				BasicDBObject sort = new BasicDBObject("analysisDate", 1);
				
				DBCursor cursor = col.find(query).sort(sort);
				
				ObjectMapper mapper = new ObjectMapper();
				ArrayNode results = mapper.createArrayNode();
				
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					
					if ("true".equals(getQueryValue("incDate"))){
						ObjectNode on = mapper.createObjectNode();
						on.put("date", obj.get("analysisDate").toString());
						on.put("millis", (Double)obj.get("millisTaken"));
						results.add(on);
					} else {
						results.add((Double)obj.get("millisTaken"));
					}
				}
				
				mongo.close();
				
				return results.toString();
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
}
