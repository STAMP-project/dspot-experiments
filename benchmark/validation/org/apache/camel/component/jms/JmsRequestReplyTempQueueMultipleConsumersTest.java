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
package org.apache.camel.component.jms;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Reliability tests for JMS TempQueue Reply Manager with multiple consumers.
 */
public class JmsRequestReplyTempQueueMultipleConsumersTest extends CamelTestSupport {
    private final Map<String, AtomicInteger> msgsPerThread = new ConcurrentHashMap<>();

    private PooledConnectionFactory connectionFactory;

    private ExecutorService executorService;

    @Test
    public void testMultipleConsumingThreads() throws Exception {
        executorService = context.getExecutorServiceManager().newFixedThreadPool(this, "test", 5);
        doSendMessages(1000);
        assertTrue(("Expected multiple consuming threads, but only found: " + (msgsPerThread.keySet().size())), ((msgsPerThread.keySet().size()) > 1));
        context.getExecutorServiceManager().shutdown(executorService);
    }

    @Test
    public void testTempQueueRefreshed() throws Exception {
        executorService = context.getExecutorServiceManager().newFixedThreadPool(this, "test", 5);
        doSendMessages(100);
        connectionFactory.clear();
        Thread.sleep(1000);
        doSendMessages(100);
        connectionFactory.clear();
        Thread.sleep(1000);
        doSendMessages(100);
        context.getExecutorServiceManager().shutdown(executorService);
    }
}

