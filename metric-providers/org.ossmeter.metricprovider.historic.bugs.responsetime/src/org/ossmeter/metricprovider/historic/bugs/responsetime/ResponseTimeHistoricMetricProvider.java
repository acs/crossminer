package org.ossmeter.metricprovider.historic.bugs.responsetime;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ossmeter.metricprovider.historic.bugs.responsetime.model.BugsResponseTimeHistoricMetric;
import org.ossmeter.metricprovider.trans.bugs.requestsreplies.BugsRequestsRepliesTransMetricProvider;
import org.ossmeter.metricprovider.trans.bugs.requestsreplies.model.BugStatistics;
import org.ossmeter.metricprovider.trans.bugs.requestsreplies.model.BugsRequestsRepliesTransMetric;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.communicationchannel.nntp.NntpUtil;
import org.ossmeter.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;

public class ResponseTimeHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.historic.bugs.responsetime";

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
	    return !project.getBugTrackingSystems().isEmpty();	   
	}

	@Override
	public Pongo measure(Project project) {
//		final long startTime = System.currentTimeMillis();

		if (uses.size()!=1) {
			System.err.println("Metric: " + IDENTIFIER + " failed to retrieve " + 
								"the transient metric it needs!");
			System.exit(-1);
		}

		BugsRequestsRepliesTransMetric usedBugsRequestsReplies = 
				((BugsRequestsRepliesTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		Date currentDate = context.getDate();

		long sumOfDurations = 0,
			 cumulativeSumOfDurations = 0;
		int bugsConsidered = 0,
			cumulativeBugsConsidered = 0;
		String bugTrackerId = "";
		
//		if (usedBugsRequestsReplies==null)
//			System.err.println("usedBugsRequestsReplies == null");
//		else if (usedBugsRequestsReplies.getBugs()==null) 
//			System.err.println("usedBugsRequestsReplies.getBugs() == null");
//		else
//			System.err.println("usedBugsRequestsReplies == " + usedBugsRequestsReplies.getBugs().size());
			
		for (BugStatistics bugStats: usedBugsRequestsReplies.getBugs()) {
			bugTrackerId = bugStats.getBugTrackerId();
			if (bugStats.getAnswered()) {
				cumulativeSumOfDurations += bugStats.getResponseDurationSec();
				cumulativeBugsConsidered++;
				java.util.Date responseDate = NntpUtil.parseDate(bugStats.getResponseDate());
				if (currentDate.compareTo(responseDate)==0) {
					sumOfDurations += bugStats.getResponseDurationSec();
					bugsConsidered++;
				}
			}
		}
		
		BugsResponseTimeHistoricMetric dailyAverageThreadResponseTime = new BugsResponseTimeHistoricMetric();

		if ( (bugsConsidered>0) || (cumulativeBugsConsidered>0) ) {
			
			dailyAverageThreadResponseTime.setBugTrackerId(bugTrackerId);
			dailyAverageThreadResponseTime.setBugsConsidered(bugsConsidered);
			dailyAverageThreadResponseTime.setCumulativeBugsConsidered(cumulativeBugsConsidered);
			
			long avgResponseTime = computeAverageDuration(sumOfDurations, bugsConsidered);
			dailyAverageThreadResponseTime.setAvgResponseTime(avgResponseTime);
			String avgResponseTimeFormatted = format(avgResponseTime);
			dailyAverageThreadResponseTime.setAvgResponseTimeFormatted(avgResponseTimeFormatted);
			
			long cumulativeAvgResponseTime = 
					computeAverageDuration(cumulativeSumOfDurations, cumulativeBugsConsidered);
			dailyAverageThreadResponseTime.setCumulativeAvgResponseTime(cumulativeAvgResponseTime);
			String cumulativeAvgResponseTimeFormatted = format(cumulativeAvgResponseTime);
			dailyAverageThreadResponseTime.setCumulativeAvgResponseTimeFormatted(cumulativeAvgResponseTimeFormatted);
		}

//		System.err.println(time(System.currentTimeMillis() - startTime) + "\tdaily_new");
		return dailyAverageThreadResponseTime;
	}

	private static final long SECONDS_DAY = 24 * 60 * 60;

	private long computeAverageDuration(long sumOfDurations, int threads) {
		if (threads>0)
			return sumOfDurations/threads;
		return 0;
	}

	private String format(long avgDuration) {
		String formatted = null;
		if (avgDuration>0) {
			int days = (int) (avgDuration / SECONDS_DAY);
			long lessThanDay = (avgDuration % SECONDS_DAY);
			formatted = days + ":" + 
					DurationFormatUtils.formatDuration(lessThanDay*1000, "HH:mm:ss:SS");
		} else {
			formatted = 0 + ":" + 
					DurationFormatUtils.formatDuration(0, "HH:mm:ss:SS");
		}
		return formatted;
	}
	
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(BugsRequestsRepliesTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "avgresponsetimeperbugtracker";
	}

	@Override
	public String getFriendlyName() {
		return "Average Thread Response Time Per Day Per bug Tracker";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the average time in which the community " +
			   "responds to open bugs per day for each bug tracker separately." + 
			   "Format: dd:HH:mm:ss:SS, where dd=days, HH:hours, mm=minutes, ss:seconds, SS=milliseconds.";
	}
}
