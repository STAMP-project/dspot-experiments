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
package org.apache.camel.test.spring;


import org.junit.Test;


// START SNIPPET: e1
/**
 * Just extend the CamelSpringTestSupport and use Camel test kit for easy Camel based unit testing.
 */
public class CamelSpringTestSupportActiveProfileTest extends CamelSpringTestSupport {
    @Test
    public void testLoadActiveProfile() throws InterruptedException {
        getMockEndpoint("mock:test").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
    }
}

/**
 * END SNIPPET: e1
 */
