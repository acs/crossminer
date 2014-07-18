package org.ossmeter.platform.bugtrackingsystem.github.api;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// API rate limits are:
//      60 per hour for unauthenticated users
//      5000 per hour for authenticated users
// See:
//  // http://developer.github.com/v3/libraries/

public class GitHubSession {
    
    public static final String SORT_UPDATED = "updated";
    public static final String SORT_CREATED = "created";
    
    private static final int PAGE_SIZE = 100;

    private GitHubClient github;
    private DateTimeFormatter dateTimeFormatter;
    
    

    public GitHubSession() {
        github = new GitHubClient();
        dateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ssZ");
    }

    public GitHubSession(String url) {
        github = new GitHubClient(url);
    }

    public void setCredentials(String user, String password) {
        github.setCredentials(user, password);
    }

    public PageIterator<Repository> getAllRepositories() {
        RepositoryService rs = new RepositoryService(github);
        return rs.pageAllRepositories();
    }

    public Repository getRepository(String owner, String name)
            throws IOException {
        RepositoryService rs = new RepositoryService(github);
        return rs.getRepository(owner, name);
    }

    /**
     * 
     * @param keyword
     * @param page
     * @return List of SearchRepositories, containing at most 100 items.
     * @throws IOException
     */
    public List<SearchRepository> searchRepositories(String keyword, int page)
            throws IOException {
        RepositoryService rs = new RepositoryService(github);
        return rs.searchRepositories(keyword, page);
    }

    public PageIterator<Issue> getIssues(GitHubIssueQuery query)
            throws IOException {
        IssueService is = new IssueService(github);
        Map<String, String> filterData = new HashMap<String, String>();

        filterData.put("state", query.getState());

        Iterator<String> it = query.getLabels().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            String label = it.next();
            sb.append(label);
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        if (sb.length() > 0) {
            filterData.put("labels", sb.toString());
        }

        if (null != query.getSince()) {
            // ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
            String date = dateTimeFormatter.print(query.getSince());
            filterData.put("since", date);
        }
        
        if ( null != query.getSort() ) {
            filterData.put("sort", query.getSort());
        } 
        
        if ( null != query.getDirection() ) {
            filterData.put("direction", query.getDirection());
        }

        return is.pageIssues(query.getUser(), query.getRepository(),
                filterData, PAGE_SIZE);
    }

    public Issue getIssue(String user, String repository, String issueNumber)
            throws IOException {
        IssueService is = new IssueService(github);
        return is.getIssue(user, repository, issueNumber);
    }

    public Issue getIssue(String user, String repository, int issueNumber)
            throws IOException {
        IssueService is = new IssueService(github);
        return is.getIssue(user, repository, issueNumber);
    }

    public Comment getComment(String user, String repository, long commentId)
            throws IOException {
        IssueService is = new IssueService(github);
        return is.getComment(user, repository, commentId);
    }

    public List<Comment> getComments(String user, String repository,
            String issueNumber) throws IOException {
        IssueService is = new IssueService(github);
        return is.getComments(user, repository, issueNumber);
    }

    public List<Comment> getComments(String user, String repository,
            int issueNumber) throws IOException {
        IssueService is = new IssueService(github);
        return is.getComments(user, repository, issueNumber);
    }

    public PageIterator<ExtendedComment> getComments(String user,
            String repository, DateTime since, String sort) throws IOException {
        ExtendedIssueService is = new ExtendedIssueService(github);

        // ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
        String date = dateTimeFormatter.print(since);

        return is.getComments(user, repository,
                com.google.common.collect.ImmutableMap.of("since", date,
                        "sort", sort, "direction", "asc"));

    }

    public PageIterator<PullRequest> getPullRequests(String user,
            String repository, DateTime since) {
        ExtendedPullRequestService prs = new ExtendedPullRequestService(github);
        RepositoryId repositoryId = new RepositoryId(user, repository);

        // ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
        String date = dateTimeFormatter.print(since);

        return prs.pagePullRequests(repositoryId,
                Collections.singletonMap("since", date));
    }
}
