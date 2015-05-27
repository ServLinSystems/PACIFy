package com.geewhiz.pacify.managers;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.marzapower.loggable.Log;
import com.marzapower.loggable.Loggable;

@Loggable(loggerName = "com.geewhiz.pacify")
public class MarkerFileManager {

	private PropertyResolveManager propertyResolveManager;
	private PMarker pMarker;

	public MarkerFileManager(PropertyResolveManager propertyResolveManager, PMarker pMarker) {
		this.propertyResolveManager = propertyResolveManager;
		this.pMarker = pMarker;
	}

	public List<Defect> doFilter() {
		List<Defect> defects = new ArrayList<Defect>();
		for (PFile pfile : pMarker.getPFiles()) {
			File file = pMarker.getAbsoluteFileFor(pfile);
			String encoding = pfile.getEncoding();

			Log.get().debug("     Filtering [" + file.getAbsolutePath() + "] using encoding [" + encoding + "]");

			FilterSetCollection filterSetCollection = getFilterSetCollection(pfile);

			File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

			try {
				FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true, encoding);
				if (!file.delete()) {
					throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
				}
				if (!tmpFile.renameTo(file)) {
					throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to ["
					        + file.getPath() + "]... Aborting!");
				}
				CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
				defects.addAll(checker.checkForErrors(file, pfile.getEncoding()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (defects.isEmpty()) {
				pMarker.getFile().deleteOnExit();
			}
		}
		return defects;
	}

	private FilterSetCollection getFilterSetCollection(PFile pfile) {
		FilterSet filterSet = getFilterSet(pfile);

		FilterSetCollection executionFilters = new FilterSetCollection();
		executionFilters.addFilterSet(filterSet);

		return executionFilters;
	}

	private FilterSet getFilterSet(PFile pfile) {
		FilterSet filterSet = new FilterSet();

		filterSet.setBeginToken("%{");
		filterSet.setEndToken("}");

		List<PProperty> pproperties = pMarker.getPPropertiesForFile(pfile);

		for (PProperty pproperty : pproperties) {
			String propertyName = pproperty.getName();
			String propertyValue = propertyResolveManager.getPropertyValue(propertyName);

			if (pproperty.isConvertBackslashToSlash()) {
				String convertedString = propertyValue;
				convertedString = propertyValue.replace('\\', '/');
				Log.get().debug(
				        "       Using property [" + propertyName + "] original value [" + propertyValue
				                + "] with backslash convertion to [" + convertedString + "]");
				propertyValue = convertedString;
			} else {
				Log.get().debug("       Using property [" + propertyName + "] with value [" + propertyValue + "] ");
			}

			filterSet.addFilter(propertyName, propertyValue);
		}
		return filterSet;
	}

	public static Pattern getPattern(String match, boolean quoteIt) {
		String searchPattern = Pattern.quote("%{")
		        + (quoteIt ? Pattern.quote(match) : match) + Pattern.quote("}");

		return Pattern.compile(searchPattern);
	}
}