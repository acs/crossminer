package org.ossmeter.metricprovider.trans.bugs.contentclasses.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class ContentClass extends Pongo {
	
	
	
	public ContentClass() { 
		super();
		BUGTRACKERID.setOwningType("org.ossmeter.metricprovider.trans.bugs.contentclasses.model.ContentClass");
		CLASSLABEL.setOwningType("org.ossmeter.metricprovider.trans.bugs.contentclasses.model.ContentClass");
		NUMBEROFCOMMENTS.setOwningType("org.ossmeter.metricprovider.trans.bugs.contentclasses.model.ContentClass");
		PERCENTAGE.setOwningType("org.ossmeter.metricprovider.trans.bugs.contentclasses.model.ContentClass");
	}
	
	public static StringQueryProducer BUGTRACKERID = new StringQueryProducer("bugTrackerId"); 
	public static StringQueryProducer CLASSLABEL = new StringQueryProducer("classLabel"); 
	public static NumericalQueryProducer NUMBEROFCOMMENTS = new NumericalQueryProducer("numberOfComments");
	public static NumericalQueryProducer PERCENTAGE = new NumericalQueryProducer("percentage");
	
	
	public String getBugTrackerId() {
		return parseString(dbObject.get("bugTrackerId")+"", "");
	}
	
	public ContentClass setBugTrackerId(String bugTrackerId) {
		dbObject.put("bugTrackerId", bugTrackerId);
		notifyChanged();
		return this;
	}
	public String getClassLabel() {
		return parseString(dbObject.get("classLabel")+"", "");
	}
	
	public ContentClass setClassLabel(String classLabel) {
		dbObject.put("classLabel", classLabel);
		notifyChanged();
		return this;
	}
	public int getNumberOfComments() {
		return parseInteger(dbObject.get("numberOfComments")+"", 0);
	}
	
	public ContentClass setNumberOfComments(int numberOfComments) {
		dbObject.put("numberOfComments", numberOfComments);
		notifyChanged();
		return this;
	}
	public float getPercentage() {
		return parseFloat(dbObject.get("percentage")+"", 0.0f);
	}
	
	public ContentClass setPercentage(float percentage) {
		dbObject.put("percentage", percentage);
		notifyChanged();
		return this;
	}
	
	
	
	
}