/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.producer;


import RecordBatch.NO_TIMESTAMP;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.internals.FutureRecordMetadata;
import org.apache.kafka.clients.producer.internals.ProduceRequestResult;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.CorruptRecordException;
import org.apache.kafka.common.record.RecordBatch;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class RecordSendTest {
    private final TopicPartition topicPartition = new TopicPartition("test", 0);

    private final long baseOffset = 45;

    private final long relOffset = 5;

    /**
     * Test that waiting on a request that never completes times out
     */
    @Test
    public void testTimeout() throws Exception {
        ProduceRequestResult request = new ProduceRequestResult(topicPartition);
        FutureRecordMetadata future = new FutureRecordMetadata(request, relOffset, RecordBatch.NO_TIMESTAMP, 0L, 0, 0, Time.SYSTEM);
        Assert.assertFalse("Request is not completed", future.isDone());
        try {
            future.get(5, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown exception.");
        } catch (TimeoutException e) {
            /* this is good */
        }
        request.set(baseOffset, NO_TIMESTAMP, null);
        request.done();
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(((baseOffset) + (relOffset)), future.get().offset());
    }

    /**
     * Test that an asynchronous request will eventually throw the right exception
     */
    @Test(expected = ExecutionException.class)
    public void testError() throws Exception {
        FutureRecordMetadata future = new FutureRecordMetadata(asyncRequest(baseOffset, new CorruptRecordException(), 50L), relOffset, RecordBatch.NO_TIMESTAMP, 0L, 0, 0, Time.SYSTEM);
        future.get();
    }

    /**
     * Test that an asynchronous request will eventually return the right offset
     */
    @Test
    public void testBlocking() throws Exception {
        FutureRecordMetadata future = new FutureRecordMetadata(asyncRequest(baseOffset, null, 50L), relOffset, RecordBatch.NO_TIMESTAMP, 0L, 0, 0, Time.SYSTEM);
        Assert.assertEquals(((baseOffset) + (relOffset)), future.get().offset());
    }
}

