package com.geewhiz.pacify.mavenplugin.mojo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;

public abstract class BaseMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	/**
	 * Should it be skipped??
	 */
	@Parameter(property = "skipPacify", defaultValue = "false")
	protected boolean skip;

	/**
	 * The entry point to Aether, i.e. the component doing all the work.
	 */
	@Component
	private RepositorySystem repoSystem;

	/**
	 * The current repository/network configuration of Maven.
	 */
	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repoSession;

	@Parameter(defaultValue = "${project.remoteArtifactRepositories}")
	private List<RemoteRepository> remoteRepositories;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Pacify is skipped.");
			return;
		}

		executePacify();
	}

	protected abstract void executePacify() throws MojoExecutionException;

	protected URL getPropertyFileURL(String propertyFileArtifact, String propertyFile) throws MojoExecutionException {
		if (propertyFile == null) {
			throw new MojoExecutionException("You didn't define the propertyFile... Aborting!");
		}

		try {
			Artifact artifact = new DefaultArtifact(propertyFileArtifact);

			ArtifactRequest request = new ArtifactRequest();
			request.setArtifact(artifact);
			request.setRepositories(remoteRepositories);

			repoSystem.resolveArtifact(repoSession, request);

			ClassLoader cl = new URLClassLoader(new URL[] { artifact.getFile().toURI().toURL() });

			URL propertyFileURL = cl.getResource(propertyFile);

			if (propertyFileURL == null) {
				throw new MojoExecutionException("Couldn't find property file [" + propertyFile + "] in ["
						+ propertyFileArtifact + "]... Aborting!");
			}

			return propertyFileURL;

		} catch (MalformedURLException e) {
			throw new MojoExecutionException("Couldn't find artifact [" + propertyFileArtifact + "].", e);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException("Couldn't resolve artifact [" + propertyFileArtifact + "].", e);
		}
	}
}