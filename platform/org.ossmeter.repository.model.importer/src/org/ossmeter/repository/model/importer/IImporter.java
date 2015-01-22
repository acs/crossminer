package org.ossmeter.repository.model.importer;

import java.io.IOException;
import java.net.MalformedURLException;

import org.ossmeter.platform.Platform;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.importer.exception.ProjectUnknownException;
import org.ossmeter.repository.model.importer.exception.RepoInfoNotFound;
import org.ossmeter.repository.model.importer.exception.WrongUrlException;

public interface IImporter {
	public Project importProject(String projectId, Platform platform)
			throws ProjectUnknownException, WrongUrlException, RepoInfoNotFound;

	public void importAll(Platform platform) throws RepoInfoNotFound;

	public Project importProjectByUrl(String url, Platform platform)
			throws WrongUrlException, ProjectUnknownException, RepoInfoNotFound;

	public void importProjects(Platform platform, int numberfOfProjects) throws WrongUrlException, RepoInfoNotFound;

	public boolean isProjectInDB(String projectId, Platform platform)
			throws WrongUrlException, ProjectUnknownException,
			MalformedURLException, IOException;
	
	public boolean isProjectInDBByUrl(String projectId, Platform platform)
			throws WrongUrlException, ProjectUnknownException,
			MalformedURLException, IOException;
}
