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
package org.apache.camel.component.iec60870;


import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ConnectionTest extends CamelTestSupport {
    private static final String DIRECT_SEND_S_1 = "direct:sendServer1";

    private static final String DIRECT_SEND_C_1 = "direct:sendClient1";

    private static final String MOCK_CLIENT_1 = "mock:testClient1";

    private static final String MOCK_CLIENT_2 = "mock:testClient2";

    private static final String MOCK_SERVER_1 = "mock:testServer1";

    @Produce(uri = ConnectionTest.DIRECT_SEND_S_1)
    protected ProducerTemplate producerServer1;

    @Produce(uri = ConnectionTest.DIRECT_SEND_C_1)
    protected ProducerTemplate producerClient1;

    @EndpointInject(uri = ConnectionTest.MOCK_CLIENT_1)
    protected MockEndpoint testClient1Endpoint;

    @EndpointInject(uri = ConnectionTest.MOCK_CLIENT_2)
    protected MockEndpoint testClient2Endpoint;

    @EndpointInject(uri = ConnectionTest.MOCK_SERVER_1)
    protected MockEndpoint testServer1Endpoint;

    @Test
    public void testFloat1() throws InterruptedException {
        this.producerServer1.sendBody(1.23F);
        // expect - count
        this.testClient1Endpoint.setExpectedCount(1);
        this.testClient2Endpoint.setExpectedCount(0);
        // expect
        expectValue(testClient1Endpoint.message(0), ConnectionTest.assertGoodValue(1.23F));
        // assert
        assertMockEndpointsSatisfied(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testBoolean1() throws InterruptedException {
        this.producerServer1.sendBody(true);
        // expect - count
        this.testClient1Endpoint.setExpectedCount(1);
        this.testClient2Endpoint.setExpectedCount(0);
        // expect
        expectValue(testClient1Endpoint.message(0), ConnectionTest.assertGoodValue(true));
        // assert
        assertMockEndpointsSatisfied(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testCommand1() throws InterruptedException {
        Thread.sleep(1000);
        this.producerClient1.sendBody(true);
        // expect - count
        this.testServer1Endpoint.setExpectedCount(1);
        // expect
        expectRequest(testServer1Endpoint.message(0), expectRequest(true));
        // assert
        assertMockEndpointsSatisfied(2000, TimeUnit.MILLISECONDS);
        System.out.println(testServer1Endpoint.getExchanges().get(0).getIn().getBody());
    }
}

