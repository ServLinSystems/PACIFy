package com.geewhiz.pacify.model;

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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import com.geewhiz.pacify.checker.PacifyChecker;
import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.utils.PacifyFilesFinder;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.replacer.PropertyPFReplacer;

public class EntityManager {

	private File startPath;
	private List<Pacify> pacifyList;

	Logger logger = Log.getInstance();

	public EntityManager(File startPath) {
		this.startPath = startPath;
	}

	public int getPFListCount() {
		return getPacifyFiles().size();
	}

	public List<Defect> checkCorrectnessOfPFListFiles(PropertyContainer propertyContainer) {
		PacifyChecker pacifyChecker = new PacifyChecker(propertyContainer);

		List<Defect> defects = new ArrayList<Defect>();
		for (Pacify pfListEntity : getPacifyFiles()) {
			defects.addAll(pacifyChecker.check(pfListEntity));
		}

		return defects;
	}

	public List<Defect> doReplacement(PropertyContainer propertyContainer) {
		List<Defect> defects = new ArrayList<Defect>();
		for (Pacify pacify : getPacifyFiles()) {
			logger.info("====== Replacing stuff which is configured in [" + pacify.getFile().getPath()
			        + "] ...");
			PropertyPFReplacer propertyReplacer = new PropertyPFReplacer(propertyContainer, pacify);
			defects.addAll(propertyReplacer.replace());
		}
		return defects;
	}

	public List<Pacify> getPacifyFiles() {
		if (pacifyList != null) {
			return pacifyList;
		}

		pacifyList = new ArrayList<Pacify>();

		List<File> pacifyFiles = new PacifyFilesFinder(startPath).getPacifyFiles();

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Pacify.class);
		} catch (JAXBException e) {
			throw new RuntimeException("Couldn't create jaxbContext", e);
		}

		for (File file : pacifyFiles) {
			try {
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Pacify pacify = (Pacify) jaxbUnmarshaller.unmarshal(file);
				pacify.setFile(file);
				pacifyList.add(pacify);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
			}
		}

		return pacifyList;
	}
}
