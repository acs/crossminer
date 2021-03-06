/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jacob Carter - Implementation.
 *******************************************************************************/
package org.ossmeter.platform.bugtrackingsystem.bitbucket.api;

import java.util.Date;

import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemComment;

public class BitbucketIssueComment extends BugTrackingSystemComment {

	private static final long serialVersionUID = 1L;

	// private String content;
	// private String author_info;
	// private String utc_created_on;
	// private long comment_id;
	private Date updateDate;
	private boolean convertMarkup;
	private boolean isSpam;

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isConvertMarkup() {
		return convertMarkup;
	}

	public void setConvertMarkup(boolean convertMarkup) {
		this.convertMarkup = convertMarkup;
	}

	public boolean isSpam() {
		return isSpam;
	}

	public void setSpam(boolean isSpam) {
		this.isSpam = isSpam;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCommentId()).append(": ").append(getText());
		return sb.toString();
	}

}
