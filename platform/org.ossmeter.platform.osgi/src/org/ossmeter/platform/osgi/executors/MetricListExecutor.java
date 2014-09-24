package org.ossmeter.platform.osgi.executors;

import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ossmeter.platform.Date;
import org.ossmeter.platform.IHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricHistoryManager;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.Platform;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.logging.OssmeterLogger;
import org.ossmeter.platform.logging.OssmeterLoggerFactory;
import org.ossmeter.repository.model.MetricAnalysis;
import org.ossmeter.repository.model.MetricProviderExecution;
import org.ossmeter.repository.model.MetricProviderType;
import org.ossmeter.repository.model.Project;

public class MetricListExecutor implements Runnable {
	
	protected FileWriter writer;

	final protected Platform platform;
	final protected Project project;
	protected List<IMetricProvider> metrics;
	protected ProjectDelta delta;
	protected Date date;
	protected OssmeterLogger logger;
	
	public MetricListExecutor(Platform platform, Project project, ProjectDelta delta, Date date) {
		this.project = project;
		this.platform = platform;
		this.delta = delta;
		this.date = date;
		this.logger = (OssmeterLogger) OssmeterLogger.getLogger("MetricListExecutor (" + project.getName() + ", " + date.toString() + ")");
		this.logger.addConsoleAppender(OssmeterLogger.DEFAULT_PATTERN);
	}
	
	public void setMetricList(List<IMetricProvider> metrics) {
		this.metrics = metrics;
	}
	
	private final ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	
	private long now() {
		return  bean.isCurrentThreadCpuTimeSupported( ) ? bean.getCurrentThreadCpuTime( ) / 1000: -1L;
	}
	
	@Override
	public void run() {

		for (IMetricProvider m : metrics) {
			
			m.setMetricProviderContext(new MetricProviderContext(platform, new OssmeterLoggerFactory().makeNewLoggerInstance(m.getIdentifier())));
			addDependenciesToMetricProvider(m);
			
			// We need to check that it hasn't already been excuted for this date
			// e.g. in cases where a different MP 
			MetricProviderType type = MetricProviderType.TRANSIENT;
			if (m instanceof IHistoricalMetricProvider) type = MetricProviderType.HISTORIC;
			
			MetricProviderExecution mpd = getProjectModelMetricProvider(project,m);
			if (mpd == null) {
				mpd = new MetricProviderExecution();
				project.getExecutionInformation().getMetricProviderData().add(mpd);
				mpd.setMetricProviderId(m.getIdentifier());
				mpd.setType(type);
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}
			
			try {
				Date lastExec = new Date(mpd.getLastExecuted());
				
				// Check we haven't already executed the MP for this day.
				if (date.compareTo(lastExec) < 0) {
					logger.warn("Metric provider '" + m.getIdentifier() + "' has been executed for this date already. Ignoring.");
					continue;
				}
			} catch (ParseException e1) {
				// we can ignore this
			} 
			
			// Performance analysis
			MetricAnalysis mAnal = new MetricAnalysis();
			mAnal.setMetricId(m.getIdentifier());
			mAnal.setProjectId(project.getShortName()); // FIXME
			mAnal.setAnalysisDate(date.toJavaDate());
			mAnal.setExecutionDate(new java.util.Date());
			platform.getProjectRepositoryManager().getProjectRepository().getMetricAnalysis().add(mAnal);
			long start = now(); // TODO: Could edit the generated code to encapsulate this.

			// Now execute
			try {
				if (m instanceof ITransientMetricProvider) {
					//JURI added if statement
					if(m.appliesTo(project))
						((ITransientMetricProvider) m).measure(project, delta, ((ITransientMetricProvider) m).adapt(platform.getMetricsRepository(project).getDb()));
				} else if (m instanceof IHistoricalMetricProvider) {
					MetricHistoryManager historyManager = new MetricHistoryManager(platform);
					//JURI added if statement
					if(m.appliesTo(project))
						historyManager.store(project, date, (IHistoricalMetricProvider) m);
				}
				
				// Update the meta data
				mpd.setLastExecuted(date.toString()); 
//				platform.getProjectRepositoryManager().getProjectRepository().sync();
			} catch (Exception e) {
				logger.error("Exception thrown during metric provider execution ("+m.getShortIdentifier()+").", e);
				project.getExecutionInformation().setInErrorState(true);
				platform.getProjectRepositoryManager().getProjectRepository().sync();
				break;
			}
			
			mAnal.setMillisTaken(now() - start);
			platform.getProjectRepositoryManager().getProjectRepository().sync(); // Will sync-ing here mess things up?
		}
	}

	/**
	 * Adds references to the dependencies of a metric provider so that they
	 * can use their data for the calculations.
	 * 
	 * FIXME: This seems like an inefficient approach. Look at this later.
	 * @param mp
	 */
	protected void addDependenciesToMetricProvider(IMetricProvider mp) {
		if (mp.getIdentifiersOfUses() == null) return; 
		
		List<IMetricProvider> uses = new ArrayList<IMetricProvider>();
		for (String id : mp.getIdentifiersOfUses()) {
			for (IMetricProvider imp : platform.getMetricProviderManager().getMetricProviders()) {
				if (imp.getIdentifier().equals(id)) {
					uses.add(imp);
					break;
				}
			}
		}
		mp.setUses(uses);
	}
	
	/**
	 * Ensures that the project DB has the up-to-date information regarding
	 * the date of last execution.
	 * @param project
	 * @param provider
	 * @param date
	 * @param type
	 */
	protected void updateMetricProviderMetaData(Project project, IMetricProvider provider, Date date, MetricProviderType type) {
		// Update project MP meta-data
		MetricProviderExecution mp = getProjectModelMetricProvider(project, provider);
		if (mp == null) {
			mp = new MetricProviderExecution();
			project.getExecutionInformation().getMetricProviderData().add(mp);
			mp.setMetricProviderId(provider.getShortIdentifier());
			mp.setType(type);
		}	
		mp.setLastExecuted(date.toString()); 
		platform.getProjectRepositoryManager().getProjectRepository().sync();
	}
	
	/**
	 * 
	 * @param project
	 * @param iProvider
	 * @return A MetricProvider (part of the Project DB) that matches the given IMetricProvider.
	 */
	protected MetricProviderExecution getProjectModelMetricProvider(Project project, IMetricProvider iProvider) {
		Iterator<MetricProviderExecution> it = project.getExecutionInformation().getMetricProviderData().iterator();
		MetricProviderExecution mp = null;
		while (it.hasNext()) {
			mp = it.next();
			if (mp == null) continue; //FIXME: intermittent bug adds nulls
			if (mp.getMetricProviderId().equals(iProvider.getIdentifier())) {
				return mp;
			}
		}

		return null;
	}
}
