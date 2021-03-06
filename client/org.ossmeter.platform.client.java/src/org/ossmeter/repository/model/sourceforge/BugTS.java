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
package org.ossmeter.repository.model.sourceforge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.ossmeter.repository.model.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = BugTS.class, name="org.ossmeter.repository.model.sourceforge.BugTS"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class BugTS extends Tracker {

	protected String description;
	protected Person assignee;
	protected Person submitted;
	protected int priority;
	protected String resolutionStatus;
	protected String bugVisibility;
	
	public String getDescription() {
		return description;
	}
	public int getPriority() {
		return priority;
	}
	public String getResolutionStatus() {
		return resolutionStatus;
	}
	public String getBugVisibility() {
		return bugVisibility;
	}
	
}
