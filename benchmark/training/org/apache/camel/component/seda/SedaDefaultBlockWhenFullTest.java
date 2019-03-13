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
 * Tests that a Seda component properly set blockWhenFull on endpoints.
 */
public class SedaDefaultBlockWhenFullTest extends ContextTestSupport {
    private static final int QUEUE_SIZE = 1;

    private static final int DELAY = 100;

    private static final int DELAY_LONG = 1000;

    private static final String MOCK_URI = "mock:blockWhenFullOutput";

    private static final String SIZE_PARAM = "?size=%d";

    private static final String BLOCK_WHEN_FULL_URI = ("seda:blockingFoo" + (String.format(SedaDefaultBlockWhenFullTest.SIZE_PARAM, SedaDefaultBlockWhenFullTest.QUEUE_SIZE))) + "&timeout=0";

    private static final String DEFAULT_URI = ("seda:foo" + (String.format(SedaDefaultBlockWhenFullTest.SIZE_PARAM, SedaDefaultBlockWhenFullTest.QUEUE_SIZE))) + "&blockWhenFull=false&timeout=0";

    @Test
    public void testSedaEndpoints() {
        Assert.assertFalse(context.getEndpoint(SedaDefaultBlockWhenFullTest.DEFAULT_URI, SedaEndpoint.class).isBlockWhenFull());
        Assert.assertTrue(context.getEndpoint(SedaDefaultBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaEndpoint.class).isBlockWhenFull());
    }

    @Test
    public void testSedaDefaultWhenFull() throws Exception {
        try {
            SedaEndpoint seda = context.getEndpoint(SedaDefaultBlockWhenFullTest.DEFAULT_URI, SedaEndpoint.class);
            Assert.assertFalse("Seda Endpoint is not setting the correct default (should be false) for \"blockWhenFull\"", seda.isBlockWhenFull());
            sendTwoOverCapacity(SedaDefaultBlockWhenFullTest.DEFAULT_URI, SedaDefaultBlockWhenFullTest.QUEUE_SIZE);
            Assert.fail("The route didn't fill the queue beyond capacity: test class isn't working as intended");
        } catch (Exception e) {
            TestSupport.assertIsInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

    @Test
    public void testSedaBlockingWhenFull() throws Exception {
        getMockEndpoint(SedaDefaultBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((SedaDefaultBlockWhenFullTest.QUEUE_SIZE) + 2));
        SedaEndpoint seda = context.getEndpoint(SedaDefaultBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaEndpoint.class);
        Assert.assertEquals(SedaDefaultBlockWhenFullTest.QUEUE_SIZE, seda.getQueue().remainingCapacity());
        sendTwoOverCapacity(SedaDefaultBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaDefaultBlockWhenFullTest.QUEUE_SIZE);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAsyncSedaBlockingWhenFull() throws Exception {
        getMockEndpoint(SedaDefaultBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((SedaDefaultBlockWhenFullTest.QUEUE_SIZE) + 1));
        getMockEndpoint(SedaDefaultBlockWhenFullTest.MOCK_URI).setResultWaitTime(((SedaDefaultBlockWhenFullTest.DELAY_LONG) * 3));
        SedaEndpoint seda = context.getEndpoint(SedaDefaultBlockWhenFullTest.BLOCK_WHEN_FULL_URI, SedaEndpoint.class);
        Assert.assertEquals(SedaDefaultBlockWhenFullTest.QUEUE_SIZE, seda.getQueue().remainingCapacity());
        asyncSendTwoOverCapacity(SedaDefaultBlockWhenFullTest.BLOCK_WHEN_FULL_URI, ((SedaDefaultBlockWhenFullTest.QUEUE_SIZE) + 4));
        assertMockEndpointsSatisfied();
    }
}

