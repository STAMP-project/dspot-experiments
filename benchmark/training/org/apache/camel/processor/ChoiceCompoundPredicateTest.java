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
package org.apache.camel.processor;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ChoiceCompoundPredicateTest extends ContextTestSupport {
    @Test
    public void testGuest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:guest");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUser() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:user");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello World", "username", "goofy");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAdmin() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:admin");
        mock.expectedMessageCount(1);
        Map<String, Object> headers = new HashMap<>();
        headers.put("username", "donald");
        headers.put("admin", "true");
        template.sendBodyAndHeaders("direct:start", "Hello World", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testGod() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:god");
        mock.expectedMessageCount(1);
        Map<String, Object> headers = new HashMap<>();
        headers.put("username", "pluto");
        headers.put("admin", "true");
        headers.put("type", "god");
        template.sendBodyAndHeaders("direct:start", "Hello World", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRiderGod() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:god");
        mock.expectedMessageCount(1);
        Map<String, Object> headers = new HashMap<>();
        headers.put("username", "Camel");
        headers.put("admin", "true");
        template.sendBodyAndHeaders("direct:start", "Hello Camel Rider", headers);
        assertMockEndpointsSatisfied();
    }
}

