package org.ossmeter.metricprovider.trans.newsgroups.emotions.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class NewsgroupData extends Pongo {
	
	
	
	public NewsgroupData() { 
		super();
		NEWSGROUPNAME.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.emotions.model.NewsgroupData");
		NUMBEROFARTICLES.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.emotions.model.NewsgroupData");
		CUMULATIVENUMBEROFARTICLES.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.emotions.model.NewsgroupData");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsgroupName"); 
	public static NumericalQueryProducer NUMBEROFARTICLES = new NumericalQueryProducer("numberOfArticles");
	public static NumericalQueryProducer CUMULATIVENUMBEROFARTICLES = new NumericalQueryProducer("cumulativeNumberOfArticles");
	
	
	public String getNewsgroupName() {
		return parseString(dbObject.get("newsgroupName")+"", "");
	}
	
	public NewsgroupData setNewsgroupName(String newsgroupName) {
		dbObject.put("newsgroupName", newsgroupName);
		notifyChanged();
		return this;
	}
	public int getNumberOfArticles() {
		return parseInteger(dbObject.get("numberOfArticles")+"", 0);
	}
	
	public NewsgroupData setNumberOfArticles(int numberOfArticles) {
		dbObject.put("numberOfArticles", numberOfArticles);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfArticles() {
		return parseInteger(dbObject.get("cumulativeNumberOfArticles")+"", 0);
	}
	
	public NewsgroupData setCumulativeNumberOfArticles(int cumulativeNumberOfArticles) {
		dbObject.put("cumulativeNumberOfArticles", cumulativeNumberOfArticles);
		notifyChanged();
		return this;
	}
	
	
	
	
}