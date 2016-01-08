package com.geewhiz.pacify.mavenplugin;

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
import java.util.Properties;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

import com.geewhiz.pacify.mavenplugin.mojo.ReplaceMojo;

public class ReplaceMojoTest extends AbstractMojoTestCase {

	Properties propertiesShouldLookLike = new Properties();

	public void testPseudo() throws Exception {
		File pom = getTestFile("target/test-classes/unit/ReplaceMojo/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
		ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
		ProjectBuilder projectBuilder = this.lookup(ProjectBuilder.class);
		MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

		ReplaceMojo replaceMojo = (ReplaceMojo) this.lookupConfiguredMojo(project, "replace");
		assertNotNull(replaceMojo);

		try {
			replaceMojo.execute();
		} catch (MojoExecutionException e) {
			fail(e.getMessage());
		}
	}
}
