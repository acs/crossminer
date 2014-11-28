package org.ossmeter.platform.client.api;

import org.ossmeter.platform.factoids.Factoid;
import org.ossmeter.platform.factoids.FactoidCategory;
import org.ossmeter.platform.factoids.Factoids;
import org.ossmeter.platform.factoids.StarRating;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.ProjectRepository;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FactoidResource extends AbstractApiResource {

    public Representation doRepresent() {
		String projectName = (String) getRequest().getAttributes().get("projectid");
		String id = (String) getRequest().getAttributes().get("factoidid");
		
		ProjectRepository projectRepo = platform.getProjectRepositoryManager().getProjectRepository();
		
		Project project = projectRepo.getProjects().findOneByShortName(projectName);
		if (project == null) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			ObjectNode node = mapper.createObjectNode();
			node.put("status", "error");
			node.put("msg", "No project was found with the requested name.");
			node.put("request", generateRequestJson(projectName, id));
			return Util.createJsonRepresentation(node);
		}
	
		Factoids factoids = new Factoids(platform.getMetricsRepository(project).getDb());
		
		// If they didn't provide an id.
		if (id == null || id.equals("")) {
			String filter = getQueryValue("cat"); // filter by category --unimplemented
			
			Iterable<Factoid> fs = factoids.getFactoids();
			
			if (filter != null && !"".equals(filter)) {
				try {
					FactoidCategory cat = FactoidCategory.valueOf(filter);
					fs = factoids.getFactoids().findByCategory(cat);
				} catch (IllegalArgumentException e) {
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					ObjectNode node = mapper.createObjectNode();
					node.put("status", "error");
					node.put("msg", "No category matched that requested.");
					node.put("request", generateRequestJson(projectName, id));
					return Util.createJsonRepresentation(node);
				}
			}
			
			ArrayNode arr = mapper.createArrayNode();
			
			for (Factoid f : fs) {
				// TODO: Inefficient as it looks up twice
				arr.add(getFactoidById(factoids, f.getMetricId()));
			}
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return Util.createJsonRepresentation(arr);
		} else {// If an id (or a list of ids) was provided
			String[] i = id.split("\\+");
			
			if (i.length == 1) {
				return Util.createJsonRepresentation(getFactoidById(factoids, id));
			} else {
				ArrayNode result = mapper.createArrayNode();
				
				for (String fId : i) {
					result.add(getFactoidById(factoids, fId));
				}
				return Util.createJsonRepresentation(result);
			}
		}
	}
    
    protected ObjectNode getFactoidById(Factoids factoids, String factoidId) {
    	Factoid f = factoids.getFactoids().findOneByMetricId(factoidId);
		
		if (f == null) {
			ObjectNode factoid = mapper.createObjectNode();
			factoid.put("id", factoidId);
			factoid.put("status", "error");
			factoid.put("msg", "Unable to find factoid with given ID.");
			return factoid;
		} else {
			ObjectNode factoid = mapper.createObjectNode();
			factoid.put("id", f.getMetricId());
			factoid.put("factoid", f.getFactoid());
			factoid.put("name", f.getName());
			if (f.getStars() != null) factoid.put("stars", f.getStars().toString());
			else factoid.put("stars", StarRating.ONE.toString());
			if (f.getCategory() != null) factoid.put("category", f.getCategory().toString());
			return factoid;
		}
    }
	
	private JsonNode generateRequestJson(String projectName, String factoidid) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode r = mapper.createObjectNode();
		
		r.put("project", projectName);
		r.put("factoidId", factoidid);
		
		return r;
	}
	
}
