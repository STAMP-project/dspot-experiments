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
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that a Seda producer supports the blockWhenFull option by blocking
 * when a message is sent while the queue is full.
 */
public class SedaBlockWhenFullTest extends ContextTestSupport {
    private static final int QUEUE_SIZE = 1;

    private static final int DELAY = 10;

    private static final int DELAY_LONG = 130;

    private static final String MOCK_URI = "mock:blockWhenFullOutput";

    private static final String SIZE_PARAM = "?size=%d";

    private static final String SEDA_WITH_OFFER_TIMEOUT_URI = ("seda:blockingFoo" + (String.format(SedaBlockWhenFullTest.SIZE_PARAM, SedaBlockWhenFullTest.QUEUE_SIZE))) + "&blockWhenFull=true&offerTimeout=100";

    private static final String BLOCK_WHEN_FULL_URI = ("seda:blockingBar" + (String.format(SedaBlockWhenFullTest.SIZE_PARAM, SedaBlockWhenFullTest.QUEUE_SIZE))) + "&blockWhenFull=true&timeout=0&offerTimeout=200";

    private static final String DEFAULT_URI = "seda:foo" + (String.format(SedaBlockWhenFullTest.SIZE_PARAM, SedaBlockWhenFullTest.QUEUE_SIZE));

    @Test
    public void testSedaOfferTimeoutWhenFull() throws Exception {
        try {
            SedaEndpoint seda = context.getEndpoint(SedaBlockWhenFullTest.SEDA_WITH_OFFER_TIMEOUT_URI, SedaEndpoint.class);
            Assert.assertEquals(SedaBlockWhenFullTest.QUEUE_SIZE, seda.getQueue().remainingCapacity());
            sendTwoOverCapacity(SedaBlockWhenFullTest.SEDA_WITH_OFFER_TIMEOUT_URI, SedaBlockWhenFullTest.QUEUE_SIZE);
            Assert.fail(((("Failed to insert element into queue, " + "after timeout of ") + (seda.getOfferTimeout())) + " milliseconds"));
        } catch (Exception e) {
            TestSupport.assertIsInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

    @Test
    public void testSedaDefaultWhenFull() throws Exception {
        try {
            SedaEndpoint seda = context.getEndpoint(SedaBlockWhenFullTest.DEFAULT_URI, SedaEndpoint.class);
            Assert.assertFalse("Seda Endpoint is not setting the correct default (should be false) for \"blockWhenFull\"", seda.isBlockWhenFull());
            sendTwoOverCapacity(SedaBlockWhenFullTest.DEFAULT_URI, SedaBlockWhenFullTest.QUEUE_SIZE);
            Assert.fail("The route didn't fill the queue beyond capacity: test class isn't working as intended");
        } catch (Exception e) {
            TestSupport.assertIsInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

    @Test
    public void testSedaBlockingWhenFull() throws Exception {
        getMockEndpoint(SedaBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((SedaBlockWhenFullTest.QUEUE_SIZE) + 2));
        SedaEndpoint seda = context.getEndpoint(SedaBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaEndpoint.class);
        Assert.assertEquals(SedaBlockWhenFullTest.QUEUE_SIZE, seda.getQueue().remainingCapacity());
        sendTwoOverCapacity(SedaBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaBlockWhenFullTest.QUEUE_SIZE);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAsyncSedaBlockingWhenFull() throws Exception {
        getMockEndpoint(SedaBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((SedaBlockWhenFullTest.QUEUE_SIZE) + 1));
        getMockEndpoint(SedaBlockWhenFullTest.MOCK_URI).setResultWaitTime(((SedaBlockWhenFullTest.DELAY_LONG) * 3));
        SedaEndpoint seda = context.getEndpoint(SedaBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaEndpoint.class);
        Assert.assertEquals(SedaBlockWhenFullTest.QUEUE_SIZE, seda.getQueue().remainingCapacity());
        asyncSendTwoOverCapacity(SedaBlockWhenFullTest.BLOCK_WHEN_FULL_URI, ((SedaBlockWhenFullTest.QUEUE_SIZE) + 4));
        assertMockEndpointsSatisfied();
    }
}

