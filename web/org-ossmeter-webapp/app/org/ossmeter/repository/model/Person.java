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
	@Type(value = Person.class, name="org.ossmeter.repository.model.Person"), 	@Type(value = org.ossmeter.repository.model.github.GitHubUser.class, name="org.ossmeter.repository.model.github.GitHubUser"),
	@Type(value = org.ossmeter.repository.model.googlecode.GoogleUser.class, name="org.ossmeter.repository.model.googlecode.GoogleUser"),
	@Type(value = org.ossmeter.repository.model.redmine.RedmineUser.class, name="org.ossmeter.repository.model.redmine.RedmineUser"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person extends NamedElement {

	protected List<Role> roles;
	protected String homePage;
	
	public String getHomePage() {
		return homePage;
	}
	
	public List<Role> getRoles() {
		return roles;
	}
}
