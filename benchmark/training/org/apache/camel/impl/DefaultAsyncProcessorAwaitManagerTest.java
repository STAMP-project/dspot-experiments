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
package org.apache.camel.impl;


import AsyncProcessorAwaitManager.AwaitThread;
import Exchange.MESSAGE_HISTORY;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import org.apache.camel.MessageHistory;
import org.apache.camel.NamedNode;
import org.apache.camel.spi.AsyncProcessorAwaitManager;
import org.apache.camel.spi.MessageHistoryFactory;
import org.apache.camel.support.DefaultExchange;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class DefaultAsyncProcessorAwaitManagerTest {
    private static final MessageHistoryFactory MESSAGE_HISTORY_FACTORY = new DefaultMessageHistoryFactory();

    private DefaultAsyncProcessorAwaitManager defaultAsyncProcessorAwaitManager;

    private DefaultExchange exchange;

    private CountDownLatch latch;

    private Thread thread;

    @Test
    public void testNoMessageHistory() throws Exception {
        startAsyncProcess();
        AsyncProcessorAwaitManager.AwaitThread awaitThread = defaultAsyncProcessorAwaitManager.browse().iterator().next();
        Assert.assertThat(awaitThread.getRouteId(), Is.is(IsNull.nullValue()));
        Assert.assertThat(awaitThread.getNodeId(), Is.is(IsNull.nullValue()));
        waitForEndOfAsyncProcess();
    }

    @Test
    public void testMessageHistoryWithEmptyList() throws Exception {
        startAsyncProcess();
        exchange.setProperty(MESSAGE_HISTORY, new LinkedList<MessageHistory>());
        AsyncProcessorAwaitManager.AwaitThread awaitThread = defaultAsyncProcessorAwaitManager.browse().iterator().next();
        Assert.assertThat(awaitThread.getRouteId(), Is.is(IsNull.nullValue()));
        Assert.assertThat(awaitThread.getNodeId(), Is.is(IsNull.nullValue()));
        waitForEndOfAsyncProcess();
    }

    @Test
    public void testMessageHistoryWithNullMessageHistory() throws Exception {
        startAsyncProcess();
        LinkedList<MessageHistory> messageHistories = new LinkedList<>();
        messageHistories.add(null);
        exchange.setProperty(MESSAGE_HISTORY, messageHistories);
        AsyncProcessorAwaitManager.AwaitThread awaitThread = defaultAsyncProcessorAwaitManager.browse().iterator().next();
        Assert.assertThat(awaitThread.getRouteId(), Is.is(IsNull.nullValue()));
        Assert.assertThat(awaitThread.getNodeId(), Is.is(IsNull.nullValue()));
        waitForEndOfAsyncProcess();
    }

    @Test
    public void testMessageHistoryWithNullElements() throws Exception {
        startAsyncProcess();
        LinkedList<MessageHistory> messageHistories = new LinkedList<>();
        messageHistories.add(DefaultAsyncProcessorAwaitManagerTest.MESSAGE_HISTORY_FACTORY.newMessageHistory(null, new DefaultAsyncProcessorAwaitManagerTest.MockNamedNode().withId(null), 0));
        exchange.setProperty(MESSAGE_HISTORY, messageHistories);
        AsyncProcessorAwaitManager.AwaitThread awaitThread = defaultAsyncProcessorAwaitManager.browse().iterator().next();
        Assert.assertThat(awaitThread.getRouteId(), Is.is(IsNull.nullValue()));
        Assert.assertThat(awaitThread.getNodeId(), Is.is(IsNull.nullValue()));
        waitForEndOfAsyncProcess();
    }

    @Test
    public void testMessageHistoryWithNotNullElements() throws Exception {
        startAsyncProcess();
        LinkedList<MessageHistory> messageHistories = new LinkedList<>();
        messageHistories.add(DefaultAsyncProcessorAwaitManagerTest.MESSAGE_HISTORY_FACTORY.newMessageHistory("routeId", new DefaultAsyncProcessorAwaitManagerTest.MockNamedNode().withId("nodeId"), 0));
        exchange.setProperty(MESSAGE_HISTORY, messageHistories);
        AsyncProcessorAwaitManager.AwaitThread awaitThread = defaultAsyncProcessorAwaitManager.browse().iterator().next();
        Assert.assertThat(awaitThread.getRouteId(), Is.is("routeId"));
        Assert.assertThat(awaitThread.getNodeId(), Is.is("nodeId"));
        waitForEndOfAsyncProcess();
    }

    private class BackgroundAwait implements Runnable {
        @Override
        public void run() {
            defaultAsyncProcessorAwaitManager.await(exchange, latch);
        }
    }

    private static class MockNamedNode implements NamedNode {
        private String id;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getShortName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getLabel() {
            return this.getClass().getName();
        }

        @Override
        public String getDescriptionText() {
            return this.getClass().getCanonicalName();
        }

        public DefaultAsyncProcessorAwaitManagerTest.MockNamedNode withId(String id) {
            this.id = id;
            return this;
        }
    }
}

