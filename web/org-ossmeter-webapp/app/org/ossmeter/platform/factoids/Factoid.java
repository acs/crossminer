package org.ossmeter.platform.factoids;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.ossmeter.repository.model.redmine.*;
import org.ossmeter.repository.model.vcs.svn.*;
import org.ossmeter.repository.model.cc.forum.*;
import org.ossmeter.repository.model.bts.bugzilla.*;
import org.ossmeter.repository.model.cc.nntp.*;
import org.ossmeter.repository.model.vcs.cvs.*;
import org.ossmeter.repository.model.eclipse.*;
import org.ossmeter.repository.model.googlecode.*;
import org.ossmeter.repository.model.vcs.git.*;
import org.ossmeter.repository.model.sourceforge.*;
import org.ossmeter.repository.model.github.*;
import org.ossmeter.repository.model.*;
import org.ossmeter.repository.model.cc.wiki.*;
import org.ossmeter.repository.model.metrics.*;
import org.ossmeter.platform.factoids.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = Factoid.class, name="org.ossmeter.platform.factoids.Factoid"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Factoid extends Object {

	protected List<String> metricDependencies;
	protected String metricId;
	protected String factoid;
	protected StarRating stars;
	protected FactoidCategory category;
	
	public String getMetricId() {
		return metricId;
	}
	public String getFactoid() {
		return factoid;
	}
	public StarRating getStars() {
		return stars;
	}
	public FactoidCategory getCategory() {
		return category;
	}
	
	public List<String> getMetricDependencies() {
		return metricDependencies;
	}
}
