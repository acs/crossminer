package org.ossmeter.metricprovider.trans.bugs.comments.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class BugTrackerData extends Pongo {
	
	
	
	public BugTrackerData() { 
		super();
		BUGTRACKERID.setOwningType("org.ossmeter.metricprovider.trans.bugs.comments.model.BugTrackerData");
		NUMBEROFCOMMENTS.setOwningType("org.ossmeter.metricprovider.trans.bugs.comments.model.BugTrackerData");
		CUMULATIVENUMBEROFCOMMENTS.setOwningType("org.ossmeter.metricprovider.trans.bugs.comments.model.BugTrackerData");
	}
	
	public static StringQueryProducer BUGTRACKERID = new StringQueryProducer("bugTrackerId"); 
	public static NumericalQueryProducer NUMBEROFCOMMENTS = new NumericalQueryProducer("numberOfComments");
	public static NumericalQueryProducer CUMULATIVENUMBEROFCOMMENTS = new NumericalQueryProducer("cumulativeNumberOfComments");
	
	
	public String getBugTrackerId() {
		return parseString(dbObject.get("bugTrackerId")+"", "");
	}
	
	public BugTrackerData setBugTrackerId(String bugTrackerId) {
		dbObject.put("bugTrackerId", bugTrackerId);
		notifyChanged();
		return this;
	}
	public int getNumberOfComments() {
		return parseInteger(dbObject.get("numberOfComments")+"", 0);
	}
	
	public BugTrackerData setNumberOfComments(int numberOfComments) {
		dbObject.put("numberOfComments", numberOfComments);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfComments() {
		return parseInteger(dbObject.get("cumulativeNumberOfComments")+"", 0);
	}
	
	public BugTrackerData setCumulativeNumberOfComments(int cumulativeNumberOfComments) {
		dbObject.put("cumulativeNumberOfComments", cumulativeNumberOfComments);
		notifyChanged();
		return this;
	}
	
	
	
	
}