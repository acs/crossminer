package org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.activeusers.ActiveUsersMetricProvider;
import org.ossmeter.metricprovider.activeusers.model.ActiveUsers;
import org.ossmeter.metricprovider.activeusers.model.NewsgroupData;
import org.ossmeter.metricprovider.activeusers.model.User;
import org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies.model.ArticleData;
import org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies.model.AverageRR;
import org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies.model.ReplyData;
import org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies.model.RequestData;
import org.ossmeter.platform.IHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class AverageNumberOfRequestsRepliesProvider implements IHistoricalMetricProvider{
	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.generic.avgnumberofrequestsreplies";

	protected MetricProviderContext context;
	
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}
	
	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		if (uses.size()!=1) {
			System.err.println("Metric: avgnumberofrequestsreplies failed to retrieve " + 
								"the two transient metrics it needs!");
			System.exit(-1);
		}

		 ActiveUsers usedUsers = 
				 ((ActiveUsersMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		int numberOfArticles = 0,
			numberOrRequests = 0,
			numberOrReplies = 0;
		for (User user: usedUsers.getUsers()) {
			numberOfArticles += user.getArticles();
			numberOrReplies += user.getReplies();
			numberOrRequests += user.getRequests();
		}
		int days = 0;
		for (NewsgroupData newsgroup: usedUsers.getNewsgroups()) {
			if (days < newsgroup.getDays())
				days = newsgroup.getDays();
		}
		
		ArticleData avgArticles = new ArticleData();
		avgArticles.setAverageArticles( 
				((float) numberOfArticles) / days );
			
		ReplyData avgReplies = new ReplyData();
		avgReplies.setAverageReplies(
				((float) numberOrReplies) / days);

		RequestData avgRequests = new RequestData();
		avgRequests.setAverageRequests( 
				((float) numberOrRequests) / days );

		AverageRR avgRRThread = new AverageRR();
		avgRRThread.getArticles().add(avgArticles);
		avgRRThread.getReplies().add(avgReplies);
		avgRRThread.getRequests().add(avgRequests);
		
		return avgRRThread;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ActiveUsersMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "avgrequestsreplies";
	}

	@Override
	public String getFriendlyName() {
		return "Average Number of Articles, Requests and Replies Per Day";
	}

	@Override
	public String getSummaryInformation() {
		return "This class computes the average number of articles, " +
				"request and reply newsgroup articles per day.";
	}

}