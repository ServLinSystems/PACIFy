package com.geewhiz.pacify.exceptions;

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

public class PropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String            property;
    private String            reference;

    public PropertyNotFoundException(String property) {
        super("Property [" + property + "] not found in any resolver!");
        this.property = property;
    }

    public PropertyNotFoundException(String property, String reference) {
        super("Property [" + property + "] references property [" + reference + "] and couldnt find ["
                + reference + "] in any resolver!");
        this.property = property;
        this.reference = reference;
    }

    public String getProperty() {
        return property;
    }

    public String getReference() {
        return reference;
    }

}