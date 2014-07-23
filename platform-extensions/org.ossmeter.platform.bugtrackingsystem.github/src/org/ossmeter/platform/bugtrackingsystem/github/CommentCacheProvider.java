package org.ossmeter.platform.bugtrackingsystem.github;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.egit.github.core.client.PageIterator;
import org.joda.time.DateTime;
import org.ossmeter.platform.bugtrackingsystem.cache.provider.DateRangeCacheProvider;
import org.ossmeter.platform.bugtrackingsystem.github.api.ExtendedComment;
import org.ossmeter.platform.bugtrackingsystem.github.api.GitHubIssueQuery;
import org.ossmeter.platform.bugtrackingsystem.github.api.GitHubSession;
import org.ossmeter.repository.model.BugTrackingSystem;
import org.ossmeter.repository.model.github.GitHubBugTracker;

public class CommentCacheProvider extends
		DateRangeCacheProvider<GitHubComment, String> {

	private class ItemIterator implements Iterator<GitHubComment> {

		private Iterator<ExtendedComment> comments;
		// private Date after;
		private Date before;

		private ExtendedComment nextComment;

		ItemIterator(GitHubBugTracker bugTracker, Date after, Date before)
				throws IOException {

			GitHubIssueQuery query = new GitHubIssueQuery(bugTracker.getUser(),
					bugTracker.getRepository());
			query = query.setAllState().setSince(new DateTime(after))
					.sortByUpdated().setDescendingDirection();

			GitHubSession github = GitHubManager.getSession(bugTracker);

			PageIterator<ExtendedComment> pages = github.getComments(
					bugTracker.getUser(), bugTracker.getRepository(),
					new DateTime(after), GitHubSession.SORT_UPDATED,
					GitHubSession.DIRECTION_ASCENDING);

			comments = new SimpleIterator<ExtendedComment>(pages);

			// this.after = after;
			this.before = before;
		}

		@Override
		public boolean hasNext() {
			while (comments.hasNext()) {
				ExtendedComment comment = comments.next();
				Date updated = comment.getUpdatedAt();

				if (null != before && updated.after(before)) {
					break;
				}
				nextComment = comment;
				return true;
			}

			return false;
		}

		@Override
		public GitHubComment next() {
			return processor.process(nextComment);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final GitHubEntityManager processor;

	public CommentCacheProvider(GitHubEntityManager processor) {
		this.processor = processor;
	}

	@Override
	public Iterator<GitHubComment> getItems(Date after, Date before,
			BugTrackingSystem bugTracker) throws Exception {
		return new ItemIterator((GitHubBugTracker) bugTracker, after, before);
	}

	@Override
	public boolean changedOnDate(GitHubComment item, Date date,
			BugTrackingSystem bugTracker) {
		return findMatchOnDate(date, item.getCreationTime(),
				item.getUpdatedAt());
	}

	@Override
	public boolean changedSinceDate(GitHubComment item, Date date,
			BugTrackingSystem bugTracker) {
		return findMatchSinceDate(date, item.getCreationTime(),
				item.getUpdatedAt());
	}

	@Override
	public String getKey(GitHubComment item) {
		return item.getCommentId();
	}

	@Override
	public void process(GitHubComment item, BugTrackingSystem bugTracker) {
		item.setBugTrackingSystem(bugTracker); // Is this needed?
	}
}
