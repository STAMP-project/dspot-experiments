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


import Journal.USER_RECORD_TYPE;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.util.ByteSequence;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the single threaded DataFileAppender class.
 */
public class TargetedDataFileAppenderTest {
    private Journal dataManager;

    private TargetedDataFileAppender appender;

    private DataFile dataFile;

    private File dir;

    @Test
    public void testWritesAreBatched() throws Exception {
        final int iterations = 10;
        ByteSequence data = new ByteSequence("DATA".getBytes());
        for (int i = 0; i < iterations; i++) {
            appender.storeItem(data, USER_RECORD_TYPE, false);
        }
        Assert.assertTrue("Data file should not be empty", ((dataFile.getLength()) > 0));
        Assert.assertTrue("Data file should be empty", ((dataFile.getFile().length()) == 0));
        appender.close();
        // at this point most probably dataManager.getInflightWrites().size() >= 0
        // as the Thread created in DataFileAppender.enqueue() may not have caught up.
        Assert.assertTrue("Data file should not be empty", ((dataFile.getLength()) > 0));
        Assert.assertTrue("Data file should not be empty", ((dataFile.getFile().length()) > 0));
    }

    @Test
    public void testBatchWritesCompleteAfterClose() throws Exception {
        final int iterations = 10;
        ByteSequence data = new ByteSequence("DATA".getBytes());
        for (int i = 0; i < iterations; i++) {
            appender.storeItem(data, USER_RECORD_TYPE, false);
        }
        appender.close();
        // at this point most probably dataManager.getInflightWrites().size() >= 0
        // as the Thread created in DataFileAppender.enqueue() may not have caught up.
        Assert.assertTrue("Data file should not be empty", ((dataFile.getLength()) > 0));
        Assert.assertTrue("Data file should not be empty", ((dataFile.getFile().length()) > 0));
    }

    @Test
    public void testBatchWriteCallbackCompleteAfterClose() throws Exception {
        final int iterations = 10;
        final CountDownLatch latch = new CountDownLatch(iterations);
        ByteSequence data = new ByteSequence("DATA".getBytes());
        for (int i = 0; i < iterations; i++) {
            appender.storeItem(data, USER_RECORD_TYPE, new Runnable() {
                @Override
                public void run() {
                    latch.countDown();
                }
            });
        }
        appender.close();
        // at this point most probably dataManager.getInflightWrites().size() >= 0
        // as the Thread created in DataFileAppender.enqueue() may not have caught up.
        Assert.assertTrue("queued data is written", latch.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("Data file should not be empty", ((dataFile.getLength()) > 0));
        Assert.assertTrue("Data file should not be empty", ((dataFile.getFile().length()) > 0));
    }
}

