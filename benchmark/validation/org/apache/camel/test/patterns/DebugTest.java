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
package org.apache.camel.test.patterns;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


// END SNIPPET: e2
public class DebugTest extends CamelTestSupport {
    // END SNIPPET: e1
    @Test
    public void testDebugger() throws Exception {
        // set mock expectations
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        // send a message
        template.sendBody("direct:start", "World");
        // assert mocks
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTwo() throws Exception {
        // set mock expectations
        getMockEndpoint("mock:a").expectedMessageCount(2);
        getMockEndpoint("mock:b").expectedMessageCount(2);
        // send a message
        template.sendBody("direct:start", "World");
        template.sendBody("direct:start", "Camel");
        // assert mocks
        assertMockEndpointsSatisfied();
    }
}

