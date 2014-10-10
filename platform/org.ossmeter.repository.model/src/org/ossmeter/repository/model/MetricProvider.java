package org.ossmeter.repository.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class MetricProvider extends NamedElement {
	
	
	
	public MetricProvider() { 
		super();
		super.setSuperTypes("org.ossmeter.repository.model.NamedElement");
		NAME.setOwningType("org.ossmeter.repository.model.MetricProvider");
		METRICPROVIDERID.setOwningType("org.ossmeter.repository.model.MetricProvider");
		TYPE.setOwningType("org.ossmeter.repository.model.MetricProvider");
		CATEGORY.setOwningType("org.ossmeter.repository.model.MetricProvider");
	}
	
	public static StringQueryProducer NAME = new StringQueryProducer("name"); 
	public static StringQueryProducer METRICPROVIDERID = new StringQueryProducer("metricProviderId"); 
	public static StringQueryProducer TYPE = new StringQueryProducer("type"); 
	public static StringQueryProducer CATEGORY = new StringQueryProducer("category"); 
	
	
	public String getMetricProviderId() {
		return parseString(dbObject.get("metricProviderId")+"", "");
	}
	
	public MetricProvider setMetricProviderId(String metricProviderId) {
		dbObject.put("metricProviderId", metricProviderId);
		notifyChanged();
		return this;
	}
	public MetricProviderType getType() {
		MetricProviderType type = null;
		try {
			type = MetricProviderType.valueOf(dbObject.get("type")+"");
		}
		catch (Exception ex) {}
		return type;
	}
	
	public MetricProvider setType(MetricProviderType type) {
		dbObject.put("type", type.toString());
		notifyChanged();
		return this;
	}
	public MetricProviderCategory getCategory() {
		MetricProviderCategory category = null;
		try {
			category = MetricProviderCategory.valueOf(dbObject.get("category")+"");
		}
		catch (Exception ex) {}
		return category;
	}
	
	public MetricProvider setCategory(MetricProviderCategory category) {
		dbObject.put("category", category.toString());
		notifyChanged();
		return this;
	}
	
	
	
	
}