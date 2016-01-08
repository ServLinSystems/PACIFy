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

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.mavenplugin.resolver.MavenPropertyResolver;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

public abstract class BaseResolveMojo extends BaseMojo {

	@Parameter(property = "startPath", defaultValue = "${project.build.outputDirectory}", required = true)
	private File startPath;

	/**
	 * should we use the maven properties instead of a file?
	 * 
	 */
	@Parameter(property = "useMavenProperties", defaultValue = "true", required = true)
	protected boolean useMavenProperties;

	/**
	 * If you defined useMavenProperties with false, you have to define the
	 * propertyFile.
	 * 
	 */
	@Parameter(property = "pacify.usePropertyFile")
	protected String propertyFile;

	/**
	 * If you defined useMavenProperties with false, you have to define in which
	 * jar is the propertyFile contained?
	 * 
	 */
	@Parameter(property = "pacify.propertyFileArtifact")
	protected String propertyFileArtifact;

	protected PropertyResolveManager createPropertyResolveManager() throws MojoExecutionException {
		Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
		propertyResolverList.add(getPropertyResolver(propertyFile));

		PropertyResolveManager propertyResolveManager = new PropertyResolveManager(propertyResolverList);
		return propertyResolveManager;
	}

	protected void checkStartPath() throws MojoExecutionException {
		if (getStartPath().exists()) {
			return;
		}

		File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
		if (getStartPath().equals(outputDirectory)) {
			getLog().debug("Directory [" + getStartPath().getAbsolutePath() + "] does  not exists. Nothing to do.");
			return; // if it is a maven project which doesn't have a target
					// folder, do nothing.
		}
		throw new MojoExecutionException("The folder [" + getStartPath().getAbsolutePath() + "] does not exist.");
	}

	protected PropertyResolver getPropertyResolver(String propertyFileParameter) throws MojoExecutionException {
		PropertyResolver propertyResolver;
		if (useMavenProperties) {
			propertyResolver = new MavenPropertyResolver(project.getProperties(),
					project.getModel().getModelEncoding());
		} else {
			propertyResolver = new FilePropertyResolver(
					getPropertyFileURL(propertyFileArtifact, propertyFileParameter));
		}

		getLog().info("Loading properties from [" + propertyResolver.getPropertyResolverDescription() + "]... ");
		return propertyResolver;
	}

	public File getStartPath() {
		return startPath;
	}
}
