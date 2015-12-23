package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PLocation;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

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

public abstract class DefectException extends Exception implements Defect {

	private static final long serialVersionUID = 1L;

	private PMarker pMarker;
	private PArchive pArchive;
	private PFile pFile;
	private PProperty pProperty;

	private PLocation pLocation;

	public DefectException() {
	}

	public DefectException(PMarker pMarker) {
		this(pMarker, null, null, null, null);
	}

	public DefectException(PMarker pMarker, PArchive pArchive) {
		this(pMarker, pArchive, null, null, null);
	}

	public DefectException(PMarker pMarker, PFile pFile) {
		this(pMarker, null, pFile, null, null);
	}

	public DefectException(PMarker pMarker, PArchive pArchive, PFile pFile) {
		this(pMarker, pArchive, pFile, null, null);
	}

	public DefectException(PMarker pMarker, PFile pFile, PProperty pProperty) {
		this(pMarker, null, pFile, null, pProperty);
	}

	public DefectException(PMarker pMarker, PLocation pLocation, PProperty pProperty) {
		this(pMarker, null, null, pLocation, pProperty);
	}

	public DefectException(PMarker pMarker, PArchive pArchive, PFile pFile, PProperty pProperty) {
		this(pMarker, pArchive, pFile, null, pProperty);
	}

	public DefectException(PMarker pMarker, PArchive pArchive, PFile pFile, PLocation pLocation, PProperty pProperty) {
		this.pMarker = pMarker;
		this.pArchive = pArchive;
		this.pFile = pFile;
		this.pLocation = pLocation;
		this.pProperty = pProperty;
	}

	public PMarker getPMarker() {
		return pMarker;
	}

	public PArchive getPArchive() {
		return pArchive;
	}

	public PFile getPFile() {
		return pFile;
	}

	public PProperty getPProperty() {
		return pProperty;
	}

	public String getDefectMessage() {
		StringBuffer result = new StringBuffer();
		result.append(this.getClass().getSimpleName()).append(":");

		if (pMarker != null) {
			result.append(String.format("\n\t[MarkerFile=%s]", pMarker.getFile().getAbsolutePath()));
		}
		if (pArchive != null) {
			result.append(String.format("\n\t[Archive=%s]", pMarker.getAbsoluteFileFor(pArchive)));
			if (pFile != null || pLocation != null) {
				result.append(String.format("\n\t[Archive File=%s]",
						pFile != null ? pFile.getRelativePath() : pLocation.getRelativePath()));
			}
		} else {
			if (pFile != null || pLocation != null) {
				result.append(String.format("\n\t[File=%s]",
						pFile != null ? pMarker.getAbsoluteFileFor(pFile) : pMarker.getAbsoluteFileFor(pLocation)));
			}
		}

		if (pProperty != null) {
			result.append(String.format("\n\t[Property=%s]", pProperty.getName()));
		}

		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pArchive == null) ? 0 : pArchive.hashCode());
		result = prime * result + ((pFile == null) ? 0 : pFile.hashCode());
		result = prime * result + ((pMarker == null) ? 0 : pMarker.hashCode());
		result = prime * result + ((pProperty == null) ? 0 : pProperty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefectException other = (DefectException) obj;
		if (pArchive == null) {
			if (other.pArchive != null) {
				return false;
			}
		} else if (!pArchive.equals(other.pArchive)) {
			return false;
		}
		if (pFile == null) {
			if (other.pFile != null) {
				return false;
			}
		} else if (!pFile.equals(other.pFile)) {
			return false;
		}
		if (pMarker == null) {
			if (other.pMarker != null) {
				return false;
			}
		} else if (!pMarker.equals(other.pMarker)) {
			return false;
		}
		if (pProperty == null) {
			if (other.pProperty != null) {
				return false;
			}
		} else if (!pProperty.equals(other.pProperty)) {
			return false;
		}
		return true;
	}
}
