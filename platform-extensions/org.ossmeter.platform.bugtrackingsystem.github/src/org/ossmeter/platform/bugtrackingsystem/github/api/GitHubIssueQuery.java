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
package org.ossmeter.platform.bugtrackingsystem.github.api;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class GitHubIssueQuery {
    private static final String STATE_OPEN = "open";
    private static final String STATE_CLOSED = "closed";
    private static final String STATE_ALL = "all";

    private DateTime since;
    private String user;
    private String repository;
    private String state = "open";
    private String direction = null;
    private String sort = null;
    private List<String> labels = new ArrayList<String>();
    private int pageSize = 100;

    public GitHubIssueQuery(String user, String repository) {
        this.user = user;
        this.repository = repository;
    }

    public String getUser() {
        return user;
    }

    public String getRepository() {
        return repository;
    }

    public GitHubIssueQuery setOpenState() {
        state = STATE_OPEN;
        return this;
    }

    public GitHubIssueQuery setClosedState() {
        state = STATE_CLOSED;
        return this;
    }
    
    public GitHubIssueQuery setAllState() {
    	state = STATE_ALL;
    	return this;
    }
    
    public GitHubIssueQuery setAscendingDirection() {
        direction = "asc";
        return this;
    }
    
    public GitHubIssueQuery setDescendingDirection() {
        direction = "desc";
        return this;
    }
    
    public GitHubIssueQuery sortByCreated() {
        sort = "created";
        return this;
    }
    
    public GitHubIssueQuery sortByUpdated() {
        sort = "updated";
        return this;
    }
    
    public GitHubIssueQuery sortByComments() {
        sort = "comments";
        return this;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    

    public String getState() {
        return state;
    }

    public GitHubIssueQuery addLabel(String label) {
        labels.add(label);
        return this;
    }

    public List<String> getLabels() {
        return labels;
    }

    /**
     * Limit returned issues to only those updated or created after the date
     * 'since'
     * 
     * @param since
     * @return
     */
    public GitHubIssueQuery setSince(DateTime since) {
        this.since = since;
        return this;
    }

    public DateTime getSince() {
        return since;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public String getSort() {
        return sort;
    }
    
    public int getPageSize() {
        return pageSize;
    }
}
