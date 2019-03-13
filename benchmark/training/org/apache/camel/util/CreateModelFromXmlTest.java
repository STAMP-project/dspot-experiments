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
package org.apache.camel.util;


import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.model.RoutesDefinition;
import org.junit.Assert;
import org.junit.Test;


public class CreateModelFromXmlTest extends ContextTestSupport {
    public static final String NS_CAMEL = "http://camel.apache.org/schema/spring";

    public static final String NS_FOO = "http://foo";

    public static final String NS_BAR = "http://bar";

    @Test
    public void testCreateModelFromXmlForInputStreamWithDefaultNamespace() throws Exception {
        RoutesDefinition routesDefinition = createModelFromXml("simpleRoute.xml", false);
        Assert.assertNotNull(routesDefinition);
        Map<String, String> expectedNamespaces = new LinkedHashMap<>();
        expectedNamespaces.put("xmlns", CreateModelFromXmlTest.NS_CAMEL);
        assertNamespacesPresent(routesDefinition, expectedNamespaces);
    }

    @Test
    public void testCreateModelFromXmlForInputStreamWithAdditionalNamespaces() throws Exception {
        RoutesDefinition routesDefinition = createModelFromXml("simpleRouteWithNamespaces.xml", false);
        Assert.assertNotNull(routesDefinition);
        Map<String, String> expectedNamespaces = new LinkedHashMap<>();
        expectedNamespaces.put("xmlns", CreateModelFromXmlTest.NS_CAMEL);
        expectedNamespaces.put("foo", CreateModelFromXmlTest.NS_FOO);
        expectedNamespaces.put("bar", CreateModelFromXmlTest.NS_BAR);
        assertNamespacesPresent(routesDefinition, expectedNamespaces);
    }

    @Test
    public void testCreateModelFromXmlForStringWithDefaultNamespace() throws Exception {
        RoutesDefinition routesDefinition = createModelFromXml("simpleRoute.xml", true);
        Assert.assertNotNull(routesDefinition);
        Map<String, String> expectedNamespaces = new LinkedHashMap<>();
        expectedNamespaces.put("xmlns", CreateModelFromXmlTest.NS_CAMEL);
        assertNamespacesPresent(routesDefinition, expectedNamespaces);
    }

    @Test
    public void testCreateModelFromXmlForStringWithAdditionalNamespaces() throws Exception {
        RoutesDefinition routesDefinition = createModelFromXml("simpleRouteWithNamespaces.xml", true);
        Assert.assertNotNull(routesDefinition);
        Map<String, String> expectedNamespaces = new LinkedHashMap<>();
        expectedNamespaces.put("xmlns", CreateModelFromXmlTest.NS_CAMEL);
        expectedNamespaces.put("foo", CreateModelFromXmlTest.NS_FOO);
        expectedNamespaces.put("bar", CreateModelFromXmlTest.NS_BAR);
        assertNamespacesPresent(routesDefinition, expectedNamespaces);
    }
}

