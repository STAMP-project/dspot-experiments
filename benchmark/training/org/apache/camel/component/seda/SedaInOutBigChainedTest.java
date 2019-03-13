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
package org.apache.camel.component.seda;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SedaInOutBigChainedTest extends ContextTestSupport {
    @Test
    public void testInOutBigSedaChained() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("start");
        getMockEndpoint("mock:b").expectedBodiesReceived("start-a");
        getMockEndpoint("mock:c").expectedBodiesReceived("start-a-b");
        getMockEndpoint("mock:d").expectedBodiesReceived("start-a-b-c");
        getMockEndpoint("mock:e").expectedBodiesReceived("start-a-b-c-d");
        getMockEndpoint("mock:f").expectedBodiesReceived("start-a-b-c-d-e");
        getMockEndpoint("mock:g").expectedBodiesReceived("start-a-b-c-d-e-f");
        getMockEndpoint("mock:h").expectedBodiesReceived("start-a-b-c-d-e-f-g");
        String reply = template.requestBody("seda:a", "start", String.class);
        Assert.assertEquals("start-a-b-c-d-e-f-g-h", reply);
        assertMockEndpointsSatisfied();
    }
}

