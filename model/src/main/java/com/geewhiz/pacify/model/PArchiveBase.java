package com.geewhiz.pacify.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

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

public abstract class PArchiveBase {

    public String getInternalType() {
        int idx = getRelativePath().lastIndexOf(".");
        return getRelativePath().substring(idx + 1);
    }

    public String getType() {
        String type = getInternalType();

        if ("jar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("war".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("ear".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.ZIP;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.TAR;
        }
        return type;
    }

    public abstract String getRelativePath();

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return 1;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return true;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return buffer;
    }
    
    public abstract List<Object> getFilesAndXMLFiles(); 
    
    public List<PFile> getPFiles() {
        List<PFile> result = new ArrayList<PFile>();

        for (Object entry : getFilesAndXMLFiles()) {
            if (entry instanceof PFile) {
                result.add((PFile) entry);
            }
        }
        return result;
    }
    
    public List<PXml> getPXmls() {
        List<PXml> result = new ArrayList<PXml>();

        for (Object entry : getFilesAndXMLFiles()) {
            if (entry instanceof PXml) {
                result.add((PXml) entry);
            }
        }
        return result;
    }
    
}
