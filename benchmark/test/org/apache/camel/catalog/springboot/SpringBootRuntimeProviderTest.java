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
package org.apache.camel.catalog.springboot;


import java.util.List;
import org.apache.camel.catalog.CamelCatalog;
import org.junit.Assert;
import org.junit.Test;


public class SpringBootRuntimeProviderTest {
    static CamelCatalog catalog;

    @Test
    public void testGetVersion() throws Exception {
        String version = SpringBootRuntimeProviderTest.catalog.getCatalogVersion();
        Assert.assertNotNull(version);
        String loaded = SpringBootRuntimeProviderTest.catalog.getLoadedVersion();
        Assert.assertNotNull(loaded);
        Assert.assertEquals(version, loaded);
    }

    @Test
    public void testProviderName() throws Exception {
        Assert.assertEquals("springboot", SpringBootRuntimeProviderTest.catalog.getRuntimeProvider().getProviderName());
    }

    @Test
    public void testFindComponentNames() throws Exception {
        List<String> names = SpringBootRuntimeProviderTest.catalog.findComponentNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("file"));
        Assert.assertTrue(names.contains("ftp"));
        Assert.assertTrue(names.contains("jms"));
        // camel-ejb does not work in spring-boot
        Assert.assertFalse(names.contains("ejb"));
        // camel-pac-logging does not work in spring-boot
        Assert.assertFalse(names.contains("paxlogging"));
    }

    @Test
    public void testFindDataFormatNames() throws Exception {
        List<String> names = SpringBootRuntimeProviderTest.catalog.findDataFormatNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("bindy-csv"));
        Assert.assertTrue(names.contains("zip"));
        Assert.assertTrue(names.contains("zipfile"));
    }

    @Test
    public void testFindLanguageNames() throws Exception {
        List<String> names = SpringBootRuntimeProviderTest.catalog.findLanguageNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("simple"));
        Assert.assertTrue(names.contains("spel"));
        Assert.assertTrue(names.contains("xpath"));
    }

    @Test
    public void testFindOtherNames() throws Exception {
        List<String> names = SpringBootRuntimeProviderTest.catalog.findOtherNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("hystrix"));
        Assert.assertTrue(names.contains("spring-boot"));
        Assert.assertTrue(names.contains("zipkin"));
        Assert.assertFalse(names.contains("blueprint"));
    }

    @Test
    public void testComponentArtifactId() throws Exception {
        String json = SpringBootRuntimeProviderTest.catalog.componentJSonSchema("ftp");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("camel-ftp-starter"));
    }

    @Test
    public void testDataFormatArtifactId() throws Exception {
        String json = SpringBootRuntimeProviderTest.catalog.dataFormatJSonSchema("bindy-csv");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("camel-bindy-starter"));
    }

    @Test
    public void testLanguageArtifactId() throws Exception {
        String json = SpringBootRuntimeProviderTest.catalog.languageJSonSchema("spel");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("camel-spring-starter"));
    }

    @Test
    public void testOtherArtifactId() throws Exception {
        String json = SpringBootRuntimeProviderTest.catalog.otherJSonSchema("zipkin");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("camel-zipkin-starter"));
    }
}

