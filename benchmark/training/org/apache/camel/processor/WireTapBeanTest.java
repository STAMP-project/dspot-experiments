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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Wire tap unit test
 */
public class WireTapBeanTest extends ContextTestSupport {
    protected MockEndpoint tap;

    protected MockEndpoint result;

    @Test
    public void testSend() throws Exception {
        result.expectedBodiesReceived("Bye World");
        tap.expectedBodiesReceived("World");
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        public String transform(String body) {
            return "Bye " + body;
        }
    }
}

