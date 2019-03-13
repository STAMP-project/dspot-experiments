/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.store;


import GetMessageStatus.FOUND;
import java.io.File;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.apache.rocketmq.store.stats.BrokerStatsManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * HATest
 */
public class HATest {
    private final String StoreMessage = "Once, there was a chance for me!";

    private int QUEUE_TOTAL = 100;

    private AtomicInteger QueueId = new AtomicInteger(0);

    private SocketAddress BornHost;

    private SocketAddress StoreHost;

    private byte[] MessageBody;

    private MessageStore messageStore;

    private MessageStore slaveMessageStore;

    private MessageStoreConfig masterMessageStoreConfig;

    private MessageStoreConfig slaveStoreConfig;

    private BrokerStatsManager brokerStatsManager = new BrokerStatsManager("simpleTest");

    private String storePathRootParentDir = ((System.getProperty("user.home")) + (File.separator)) + (UUID.randomUUID().toString().replace("-", ""));

    private String storePathRootDir = ((storePathRootParentDir) + (File.separator)) + "store";

    @Test
    public void testHandleHA() {
        long totalMsgs = 10;
        QUEUE_TOTAL = 1;
        MessageBody = StoreMessage.getBytes();
        for (long i = 0; i < totalMsgs; i++) {
            messageStore.putMessage(buildMessage());
        }
        for (int i = 0; (i < 100) && (isCommitLogAvailable(((DefaultMessageStore) (messageStore)))); i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        for (int i = 0; (i < 100) && (isCommitLogAvailable(((DefaultMessageStore) (slaveMessageStore)))); i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        for (long i = 0; i < totalMsgs; i++) {
            GetMessageResult result = slaveMessageStore.getMessage("GROUP_A", "FooBar", 0, i, (1024 * 1024), null);
            assertThat(result).isNotNull();
            Assert.assertTrue(FOUND.equals(result.getStatus()));
            result.release();
        }
    }
}

