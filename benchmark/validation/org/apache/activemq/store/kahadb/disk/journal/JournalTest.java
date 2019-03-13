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
package org.apache.activemq.store.kahadb.disk.journal;


import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.apache.activemq.util.ByteSequence;


public class JournalTest extends TestCase {
    protected static final int DEFAULT_MAX_BATCH_SIZE = (1024 * 1024) * 4;

    Journal dataManager;

    File dir;

    public void testBatchWriteCallbackCompleteAfterTimeout() throws Exception {
        final int iterations = 10;
        final CountDownLatch latch = new CountDownLatch(iterations);
        ByteSequence data = new ByteSequence("DATA".getBytes());
        for (int i = 0; i < iterations; i++) {
            dataManager.write(data, new Runnable() {
                public void run() {
                    latch.countDown();
                }
            });
        }
        // at this point most probably dataManager.getInflightWrites().size() >= 0
        // as the Thread created in DataFileAppender.enqueue() may not have caught up.
        TestCase.assertTrue("queued data is written", latch.await(5, TimeUnit.SECONDS));
    }

    public void testBatchWriteCallbackCompleteAfterClose() throws Exception {
        final int iterations = 10;
        final CountDownLatch latch = new CountDownLatch(iterations);
        ByteSequence data = new ByteSequence("DATA".getBytes());
        for (int i = 0; i < iterations; i++) {
            dataManager.write(data, new Runnable() {
                public void run() {
                    latch.countDown();
                }
            });
        }
        dataManager.close();
        TestCase.assertTrue("queued data is written", dataManager.getInflightWrites().isEmpty());
        TestCase.assertEquals("none written", 0, latch.getCount());
    }

    public void testBatchWriteCompleteAfterClose() throws Exception {
        ByteSequence data = new ByteSequence("DATA".getBytes());
        final int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            dataManager.write(data, false);
        }
        dataManager.close();
        TestCase.assertTrue(("queued data is written:" + (dataManager.getInflightWrites().size())), dataManager.getInflightWrites().isEmpty());
    }

    public void testBatchWriteToMaxMessageSize() throws Exception {
        final int iterations = 4;
        final CountDownLatch latch = new CountDownLatch(iterations);
        Runnable done = new Runnable() {
            public void run() {
                latch.countDown();
            }
        };
        int messageSize = (JournalTest.DEFAULT_MAX_BATCH_SIZE) / iterations;
        byte[] message = new byte[messageSize];
        ByteSequence data = new ByteSequence(message);
        for (int i = 0; i < iterations; i++) {
            dataManager.write(data, done);
        }
        // write may take some time
        TestCase.assertTrue("all callbacks complete", latch.await(10, TimeUnit.SECONDS));
    }

    public void testNoBatchWriteWithSync() throws Exception {
        ByteSequence data = new ByteSequence("DATA".getBytes());
        final int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            dataManager.write(data, true);
            TestCase.assertTrue("queued data is written", dataManager.getInflightWrites().isEmpty());
        }
    }
}

