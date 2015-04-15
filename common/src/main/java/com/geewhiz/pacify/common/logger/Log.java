package com.geewhiz.pacify.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

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

public class Log {

	private static Logger INSTANCE;

	public synchronized static Logger getInstance() {
		if (INSTANCE == null) {
			System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
			INSTANCE = LoggerFactory.getLogger(Log.class);
		}
		return INSTANCE;
	}

	// public static void log(LogLevel logLevel, String message) {
	// getInstance().
	// if (getInstance().getLogLevel().compareTo(logLevel) > 0) {
	// return;
	// }
	//
	// Calendar c = Calendar.getInstance();
	// System.out.format(FORMAT, logLevel.toString(), c, c, c, c.get(Calendar.HOUR_OF_DAY), c, c.get(Calendar.SECOND),
	// message);
	// }
}