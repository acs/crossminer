package org.ossmeter.repository.model;

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
	@Type(value = NamedElement.class, name="org.ossmeter.repository.model.NamedElement"), 	@Type(value = org.ossmeter.repository.model.eclipse.EclipsePlatform.class, name="org.ossmeter.repository.model.eclipse.EclipsePlatform"),
	@Type(value = org.ossmeter.repository.model.eclipse.Release.class, name="org.ossmeter.repository.model.eclipse.Release"),
	@Type(value = org.ossmeter.repository.model.googlecode.GoogleLabel.class, name="org.ossmeter.repository.model.googlecode.GoogleLabel"),
	@Type(value = org.ossmeter.repository.model.sourceforge.OS.class, name="org.ossmeter.repository.model.sourceforge.OS"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Topic.class, name="org.ossmeter.repository.model.sourceforge.Topic"),
	@Type(value = org.ossmeter.repository.model.sourceforge.ProgrammingLanguage.class, name="org.ossmeter.repository.model.sourceforge.ProgrammingLanguage"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Audience.class, name="org.ossmeter.repository.model.sourceforge.Audience"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Environment.class, name="org.ossmeter.repository.model.sourceforge.Environment"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Category.class, name="org.ossmeter.repository.model.sourceforge.Category"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Tracker.class, name="org.ossmeter.repository.model.sourceforge.Tracker"),
	@Type(value = org.ossmeter.repository.model.sourceforge.BugTS.class, name="org.ossmeter.repository.model.sourceforge.BugTS"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Request.class, name="org.ossmeter.repository.model.sourceforge.Request"),
	@Type(value = org.ossmeter.repository.model.sourceforge.FeatureRequest.class, name="org.ossmeter.repository.model.sourceforge.FeatureRequest"),
	@Type(value = org.ossmeter.repository.model.sourceforge.SupportRequest.class, name="org.ossmeter.repository.model.sourceforge.SupportRequest"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Patch.class, name="org.ossmeter.repository.model.sourceforge.Patch"),
	@Type(value = org.ossmeter.repository.model.sourceforge.Bug.class, name="org.ossmeter.repository.model.sourceforge.Bug"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class NamedElement extends Object {

	protected String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
