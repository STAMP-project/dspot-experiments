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
package org.apache.camel.catalog.karaf;


import java.util.List;
import org.apache.camel.catalog.CamelCatalog;
import org.junit.Assert;
import org.junit.Test;


public class KarafRuntimeProviderTest {
    static CamelCatalog catalog;

    @Test
    public void testGetVersion() throws Exception {
        String version = KarafRuntimeProviderTest.catalog.getCatalogVersion();
        Assert.assertNotNull(version);
        String loaded = KarafRuntimeProviderTest.catalog.getLoadedVersion();
        Assert.assertNotNull(loaded);
        Assert.assertEquals(version, loaded);
    }

    @Test
    public void testProviderName() throws Exception {
        Assert.assertEquals("karaf", KarafRuntimeProviderTest.catalog.getRuntimeProvider().getProviderName());
    }

    @Test
    public void testFindComponentNames() throws Exception {
        List<String> names = KarafRuntimeProviderTest.catalog.findComponentNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("ftp"));
        Assert.assertTrue(names.contains("paxlogging"));
        // camel-ejb does not work in Karaf
        Assert.assertFalse(names.contains("ejb"));
    }

    @Test
    public void testFindDataFormatNames() throws Exception {
        List<String> names = KarafRuntimeProviderTest.catalog.findDataFormatNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("bindy-csv"));
        Assert.assertTrue(names.contains("zip"));
        Assert.assertTrue(names.contains("zipfile"));
    }

    @Test
    public void testFindLanguageNames() throws Exception {
        List<String> names = KarafRuntimeProviderTest.catalog.findLanguageNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("simple"));
        Assert.assertTrue(names.contains("spel"));
        Assert.assertTrue(names.contains("xpath"));
    }

    @Test
    public void testFindOtherNames() throws Exception {
        List<String> names = KarafRuntimeProviderTest.catalog.findOtherNames();
        Assert.assertNotNull(names);
        Assert.assertFalse(names.isEmpty());
        Assert.assertTrue(names.contains("blueprint"));
        Assert.assertTrue(names.contains("hystrix"));
        Assert.assertTrue(names.contains("swagger-java"));
        Assert.assertTrue(names.contains("zipkin"));
        Assert.assertFalse(names.contains("spring-boot"));
    }
}

