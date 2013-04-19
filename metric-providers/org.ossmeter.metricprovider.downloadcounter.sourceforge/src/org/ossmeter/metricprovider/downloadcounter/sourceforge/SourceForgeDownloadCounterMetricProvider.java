package org.ossmeter.metricprovider.downloadcounter.sourceforge;

import java.text.SimpleDateFormat;

import org.ossmeter.metricprovider.downloadcounter.model.Download;
import org.ossmeter.metricprovider.downloadcounter.model.DownloadCounter;
import org.ossmeter.platform.AbstractTransientMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.sourceforge.SourceForgeProject;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.DB;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SourceForgeDownloadCounterMetricProvider extends AbstractTransientMetricProvider<DownloadCounter> {


	@Override
	public boolean appliesTo(Project project) {
		return project instanceof SourceForgeProject;
 	}

	@Override
	public DownloadCounter adapt(DB db) {
		return new DownloadCounter(db);
	}

	@Override
	public void measure(Project project, ProjectDelta delta, DownloadCounter db) {
		
		
		String deltaDate = toString(delta.getDate());
		String previousDeltaDate = toString(delta.getDate().addDays(-1));
			
		try {
			Client client = Client.create(); 
			
			String restRequest = "http://sourceforge.net/projects/" +  project.getName() + "/files/stats/json?start_date=" + previousDeltaDate + "&end_date=" + deltaDate;
			
			WebResource webResource = client.resource(restRequest);
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			String output = response.getEntity(String.class);
			
			int counter = JsonPath.read(output, "$.summaries.time.downloads");

			Download download = new Download();
			download.setCounter(counter);
			download.setDate(deltaDate);
			
			db.getDownloads().add(download);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
 
		db.sync();
		
	}
	
	public String toString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date.toJavaDate());
	}
	
}
