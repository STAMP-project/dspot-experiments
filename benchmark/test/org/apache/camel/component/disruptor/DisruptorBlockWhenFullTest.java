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


import org.apache.camel.CamelExecutionException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Tests that a Disruptor producer blocks when a message is sent while the ring buffer is full.
 */
public class DisruptorBlockWhenFullTest extends CamelTestSupport {
    private static final int QUEUE_SIZE = 8;

    private static final int DELAY = 100;

    private static final String MOCK_URI = "mock:blockWhenFullOutput";

    private static final String DEFAULT_URI = "disruptor:foo?size=" + (DisruptorBlockWhenFullTest.QUEUE_SIZE);

    private static final String EXCEPTION_WHEN_FULL_URI = "disruptor:foo?blockWhenFull=false&size=" + (DisruptorBlockWhenFullTest.QUEUE_SIZE);

    @Test
    public void testDisruptorBlockingWhenFull() throws Exception {
        getMockEndpoint(DisruptorBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((DisruptorBlockWhenFullTest.QUEUE_SIZE) + 20));
        final DisruptorEndpoint disruptor = context.getEndpoint(DisruptorBlockWhenFullTest.DEFAULT_URI, DisruptorEndpoint.class);
        assertEquals(DisruptorBlockWhenFullTest.QUEUE_SIZE, disruptor.getRemainingCapacity());
        sendSoManyOverCapacity(DisruptorBlockWhenFullTest.DEFAULT_URI, DisruptorBlockWhenFullTest.QUEUE_SIZE, 20);
        assertMockEndpointsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testDisruptorExceptionWhenFull() throws Exception {
        getMockEndpoint(DisruptorBlockWhenFullTest.MOCK_URI).setExpectedMessageCount(((DisruptorBlockWhenFullTest.QUEUE_SIZE) + 20));
        final DisruptorEndpoint disruptor = context.getEndpoint(DisruptorBlockWhenFullTest.DEFAULT_URI, DisruptorEndpoint.class);
        assertEquals(DisruptorBlockWhenFullTest.QUEUE_SIZE, disruptor.getRemainingCapacity());
        sendSoManyOverCapacity(DisruptorBlockWhenFullTest.EXCEPTION_WHEN_FULL_URI, DisruptorBlockWhenFullTest.QUEUE_SIZE, 20);
        assertMockEndpointsSatisfied();
    }
}

