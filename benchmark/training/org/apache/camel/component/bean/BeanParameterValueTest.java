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
package org.apache.camel.component.bean;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.junit.Test;


/**
 *
 */
public class BeanParameterValueTest extends ContextTestSupport {
    @Test
    public void testBeanParameterValueBoolean() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueBoolean2() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start2", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueBoolean3() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start3", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueBoolean4() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello Camel");
        template.sendBody("direct:start4", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueInteger() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("WorldWorldWorld");
        template.sendBody("direct:echo", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueHeaderInteger() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("WorldWorld");
        template.sendBodyAndHeader("direct:echo2", "World", "times", 2);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueMap() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("direct:heads", "World", "hello", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterNoBody() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Is Hadrian 21 years old?");
        Map<String, Object> headers = new HashMap<>();
        headers.put("SomeTest", true);
        headers.put("SomeAge", 21);
        headers.put("SomeName", "Hadrian");
        template.sendBodyAndHeaders("direct:nobody", null, headers);
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        public String bar(String body, boolean hello) {
            if (hello) {
                return "Hello " + body;
            } else {
                return body;
            }
        }

        public String echo(String body, int times) {
            if (times > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < times; i++) {
                    sb.append(body);
                }
                return sb.toString();
            }
            return body;
        }

        public String heads(String body, Map<?, ?> headers) {
            return ((headers.get("hello")) + " ") + body;
        }

        public String nobody(int age, String name, boolean question) {
            StringBuilder sb = new StringBuilder();
            sb.append((question ? "Is " : ""));
            sb.append(name);
            sb.append((question ? " " : "is "));
            sb.append(age);
            sb.append(" years old");
            sb.append((question ? "?" : "."));
            return sb.toString();
        }
    }
}

