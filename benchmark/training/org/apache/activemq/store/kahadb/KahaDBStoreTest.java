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
package org.apache.activemq.store.kahadb;


import KahaDBStore.KahaDBMessageStore;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.ProducerId;
import org.junit.Assert;
import org.junit.Test;


public class KahaDBStoreTest {
    KahaDBMessageStore underTest;

    KahaDBStore store;

    ActiveMQMessage message;

    ProducerId producerId = new ProducerId("1.1.1");

    private static final int MESSAGE_COUNT = 2000;

    private Vector<Throwable> exceptions = new Vector<Throwable>();

    @Test
    public void testConcurrentStoreAndDispatchQueue() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < (KahaDBStoreTest.MESSAGE_COUNT); i++) {
            final int id = ++i;
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        Message msg = message.copy();
                        msg.setMessageId(new org.apache.activemq.command.MessageId(producerId, id));
                        underTest.asyncAddQueueMessage(null, msg);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        ExecutorService executor2 = Executors.newCachedThreadPool();
        for (int i = 0; i < (KahaDBStoreTest.MESSAGE_COUNT); i++) {
            final int id = ++i;
            executor2.execute(new Runnable() {
                public void run() {
                    try {
                        MessageAck ack = new MessageAck();
                        ack.setLastMessageId(new org.apache.activemq.command.MessageId(producerId, id));
                        underTest.removeAsyncMessage(null, ack);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        executor2.shutdown();
        executor2.awaitTermination(60, TimeUnit.SECONDS);
        Assert.assertTrue(("no exceptions " + (exceptions)), exceptions.isEmpty());
    }
}

