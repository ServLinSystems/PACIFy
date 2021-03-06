/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.resolver.file-resolver
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.geewhiz.pacify;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.utils.FileUtils;

public class FileResolverTestBase extends TestBase {

    public PropertyResolveManager createPropertyResolveManager(String testFolder) {
        File myTestProperty = new File("target/test-resources/" + testFolder, "properties/propertiesToResolve.properties");
        URL myTestPropertyURL = FileUtils.getFileUrl(myTestProperty);

        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(myTestPropertyURL);
        resolverList.add(filePropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
