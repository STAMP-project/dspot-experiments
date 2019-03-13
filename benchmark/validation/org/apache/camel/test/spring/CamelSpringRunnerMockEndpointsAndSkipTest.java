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


import ServiceStatus.Started;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.InterceptSendToEndpoint;
import org.junit.Assert;
import org.junit.Test;


@MockEndpointsAndSkip("{{mock.skip}}")
public class CamelSpringRunnerMockEndpointsAndSkipTest extends CamelSpringRunnerPlainTest {
    @EndpointInject(uri = "mock:seda:context2.seda", context = "camelContext2")
    protected MockEndpoint mock;

    @EndpointInject(uri = "seda:context2.seda", context = "camelContext2")
    private InterceptSendToEndpoint original;

    @Test
    @Override
    public void testPositive() throws Exception {
        Assert.assertEquals(Started, camelContext.getStatus());
        Assert.assertEquals(Started, camelContext2.getStatus());
        mockA.expectedBodiesReceived("David");
        mockB.expectedBodiesReceived("Hello David");
        mock.expectedBodiesReceived("Hello David");
        start.sendBody("David");
        start2.sendBody("David");
        MockEndpoint.assertIsSatisfied(camelContext);
        MockEndpoint.assertIsSatisfied(camelContext2);
        Assert.assertTrue("Original endpoint was invoked", getExchanges().isEmpty());
    }
}

