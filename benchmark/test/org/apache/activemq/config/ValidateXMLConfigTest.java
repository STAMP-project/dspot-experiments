/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.config;


import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;


public class ValidateXMLConfigTest {
    private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

    @Test
    public void validateDefaultConfig() throws Exception {
        validateXML("src/release/conf/activemq.xml");
    }

    @Test
    public void validateExampleConfig() throws Exception {
        // resource:copy-resource brings all config files into target/conf
        File sampleConfDir = new File("target/conf");
        final HashSet<String> skipped = new HashSet<String>(Arrays.asList(new String[]{ "resin-web.xml", "web.xml" }));
        for (File xmlFile : sampleConfDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return ((pathname.isFile()) && (pathname.getName().endsWith("xml"))) && (!(skipped.contains(pathname.getName())));
            }
        })) {
            validateXML(xmlFile);
        }
    }
}

