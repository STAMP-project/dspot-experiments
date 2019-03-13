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


import SedaConstants.QUEUE_SIZE;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SedaConfigureTest extends ContextTestSupport {
    @SuppressWarnings("unchecked")
    @Test
    public void testBlockingQueueConfigured() throws Exception {
        SedaEndpoint endpoint = resolveMandatoryEndpoint("seda:foo?size=2000", SedaEndpoint.class);
        BlockingQueue<Exchange> queue = endpoint.getQueue();
        LinkedBlockingQueue<Exchange> blockingQueue = TestSupport.assertIsInstanceOf(LinkedBlockingQueue.class, queue);
        Assert.assertEquals("remainingCapacity", 2000, blockingQueue.remainingCapacity());
    }

    @Test
    public void testConcurrentConsumersConfigured() {
        SedaEndpoint endpoint = resolveMandatoryEndpoint("seda:foo?concurrentConsumers=5", SedaEndpoint.class);
        Assert.assertEquals("concurrentConsumers", 5, endpoint.getConcurrentConsumers());
    }

    @Test
    public void testBlockWhenFull() {
        SedaEndpoint endpoint = resolveMandatoryEndpoint("seda:foo?size=2000&blockWhenFull=true", SedaEndpoint.class);
        Assert.assertTrue("blockWhenFull", endpoint.isBlockWhenFull());
    }

    @Test
    public void testDefaults() {
        SedaEndpoint endpoint = resolveMandatoryEndpoint("seda:foo", SedaEndpoint.class);
        Assert.assertFalse("blockWhenFull: wrong default", endpoint.isBlockWhenFull());
        Assert.assertEquals("concurrentConsumers: wrong default", 1, endpoint.getConcurrentConsumers());
        Assert.assertEquals("size (remainingCapacity): wrong default", QUEUE_SIZE, endpoint.getSize());
        Assert.assertEquals("timeout: wrong default", 30000L, endpoint.getTimeout());
    }
}

