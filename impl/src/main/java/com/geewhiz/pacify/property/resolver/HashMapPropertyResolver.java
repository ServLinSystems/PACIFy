package com.geewhiz.pacify.property.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.BasePropertyResolver;

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

public class HashMapPropertyResolver extends BasePropertyResolver {

    Map<String, String> properties = new HashMap<String, String>();

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public boolean containsProperty(String property) {
        return properties.containsKey(property);
    }

    @Override
    public String getPropertyValue(String key) {
        return properties.get(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public String getPropertyResolverDescription() {
        return HashMapPropertyResolver.class.getSimpleName();
    }

    @Override
    public List<Defect> checkForDuplicateEntry() {
        return Collections.emptyList();
    }

    @Override
    public String getBeginToken() {
        return "%{";
    }

    @Override
    public String getEndToken() {
        return "}";
    }

}