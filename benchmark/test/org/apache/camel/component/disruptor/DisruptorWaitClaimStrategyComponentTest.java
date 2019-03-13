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
package org.apache.camel.component.disruptor;


import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests the WaitStrategy and ClaimStrategy configuration of the disruptor component
 */
@RunWith(Parameterized.class)
public class DisruptorWaitClaimStrategyComponentTest extends CamelTestSupport {
    private static final Integer VALUE = Integer.valueOf(42);

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce
    protected ProducerTemplate template;

    private final String producerType;

    private final String waitStrategy;

    private String disruptorUri;

    public DisruptorWaitClaimStrategyComponentTest(final String waitStrategy, final String producerType) {
        this.waitStrategy = waitStrategy;
        this.producerType = producerType;
    }

    @Test
    public void testProduce() throws InterruptedException {
        resultEndpoint.expectedBodiesReceived(DisruptorWaitClaimStrategyComponentTest.VALUE);
        resultEndpoint.setExpectedMessageCount(1);
        template.asyncSendBody(disruptorUri, DisruptorWaitClaimStrategyComponentTest.VALUE);
        resultEndpoint.await(5, TimeUnit.SECONDS);
        resultEndpoint.assertIsSatisfied();
    }
}

