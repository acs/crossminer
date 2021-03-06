/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * James Williams
 *  - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.historic.totaldownloadcounter;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.downloadcounter.model.Download;
import org.ossmeter.metricprovider.downloadcounter.model.DownloadCounter;
import org.ossmeter.metricprovider.downloadcounter.sourceforge.SourceForgeDownloadCounterMetricProvider;
import org.ossmeter.metricprovider.historic.totaldownloadcounter.model.TotalDownloadCounter;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.sourceforge.SourceForgeProject;

import com.googlecode.pongo.runtime.Pongo;

public class TotalDownloadCounterMetricProvider extends AbstractHistoricalMetricProvider{

	protected MetricProviderContext context;
	protected SourceForgeDownloadCounterMetricProvider downloadCounterMetricProvider;
	
	@Override
	public String getIdentifier() {
		return TotalDownloadCounterMetricProvider.class.getCanonicalName();
	}

	@Override
	public boolean appliesTo(Project project) {
		return project instanceof SourceForgeProject;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		// TODO Auto-generated method stub
		this.downloadCounterMetricProvider = (SourceForgeDownloadCounterMetricProvider)uses.get(0);
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		// TODO Auto-generated method stub
		return Arrays.asList("DownloadCounterMetricProvider");
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}
	
	@Override
	public Pongo measure(Project project) {
		
		DownloadCounter downloadCounter =  new DownloadCounter(context.getProjectDB(project));
		int totalCounter = 0;
		
		for (Download download : downloadCounter.getDownloads()) {
			totalCounter = totalCounter + download.getCounter();
		}
		
		TotalDownloadCounter totalDownloadCounter = new TotalDownloadCounter();
		totalDownloadCounter.setDownloads(totalCounter);
		
		return totalDownloadCounter;
	}

	@Override
	public String getShortIdentifier() {
		return "tdc";
	}

	@Override
	public String getFriendlyName() {
		return "Total downloads";
	}

	@Override
	public String getSummaryInformation() {
		return "Lorum";
	}

}
