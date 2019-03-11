/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.spark.streaming;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Assert;
import org.junit.Test;


public class MessageRetryManagerTest {
    MessageRetryManager messageRetryManager;

    Map<String, MessageSet> cache;

    BlockingQueue<MessageSet> queue;

    @Test
    public void testRetryLogics() {
        List<MessageExt> data = new ArrayList<>();
        // ack
        MessageSet messageSet = new MessageSet(data);
        messageRetryManager.mark(messageSet);
        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.containsKey(messageSet.getId()));
        messageRetryManager.ack(messageSet.getId());
        Assert.assertEquals(0, cache.size());
        Assert.assertFalse(cache.containsKey(messageSet.getId()));
        // fail need retry: retries < maxRetry
        messageSet = new MessageSet(data);
        messageRetryManager.mark(messageSet);
        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.containsKey(messageSet.getId()));
        messageRetryManager.fail(messageSet.getId());
        Assert.assertEquals(0, cache.size());
        Assert.assertFalse(cache.containsKey(messageSet.getId()));
        Assert.assertEquals(1, messageSet.getRetries());
        Assert.assertEquals(1, queue.size());
        Assert.assertEquals(messageSet, queue.poll());
        // fail need not retry: retries >= maxRetry
        messageSet = new MessageSet(data);
        messageRetryManager.mark(messageSet);
        messageRetryManager.fail(messageSet.getId());
        Assert.assertEquals(0, cache.size());
        Assert.assertFalse(cache.containsKey(messageSet.getId()));
        messageRetryManager.mark(messageSet);
        messageRetryManager.fail(messageSet.getId());
        Assert.assertEquals(2, messageSet.getRetries());
        messageRetryManager.mark(messageSet);
        messageRetryManager.fail(messageSet.getId());
        Assert.assertEquals(3, messageSet.getRetries());
        Assert.assertFalse(messageRetryManager.needRetry(messageSet));
        messageRetryManager.mark(messageSet);
        messageRetryManager.fail(messageSet.getId());
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(messageSet, queue.poll());
        // fail: no ack/fail received in ttl
        messageSet = new MessageSet(data);
        messageRetryManager.mark(messageSet);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(0, cache.size());
        Assert.assertFalse(cache.containsKey(messageSet.getId()));
    }
}

