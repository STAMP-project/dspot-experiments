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
package org.apache.activemq.broker.scheduler;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.store.kahadb.scheduler.JobSchedulerStoreImpl;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobSchedulerStoreCheckpointTest {
    static final Logger LOG = LoggerFactory.getLogger(JobSchedulerStoreCheckpointTest.class);

    private JobSchedulerStoreImpl store;

    private JobScheduler scheduler;

    private ByteSequence payload;

    @Test
    public void testStoreCleanupLinear() throws Exception {
        final int COUNT = 10;
        final CountDownLatch latch = new CountDownLatch(COUNT);
        scheduler.addListener(new JobListener() {
            @Override
            public void scheduledJob(String id, ByteSequence job) {
                latch.countDown();
            }
        });
        long time = TimeUnit.SECONDS.toMillis(30);
        for (int i = 0; i < COUNT; i++) {
            scheduler.schedule(("id" + i), payload, "", time, 0, 0);
        }
        int size = scheduler.getAllJobs().size();
        Assert.assertEquals(size, COUNT);
        JobSchedulerStoreCheckpointTest.LOG.info("Number of journal log files: {}", getNumJournalFiles());
        // need a little slack so go over 60 seconds
        Assert.assertTrue(latch.await(70, TimeUnit.SECONDS));
        Assert.assertEquals(0, latch.getCount());
        for (int i = 0; i < COUNT; i++) {
            scheduler.schedule(("id" + i), payload, "", time, 0, 0);
        }
        JobSchedulerStoreCheckpointTest.LOG.info("Number of journal log files: {}", getNumJournalFiles());
        // need a little slack so go over 60 seconds
        Assert.assertTrue(latch.await(70, TimeUnit.SECONDS));
        Assert.assertEquals(0, latch.getCount());
        Assert.assertTrue(("Should be only one log left: " + (getNumJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumJournalFiles()) == 1;
            }
        }, TimeUnit.MINUTES.toMillis(2)));
        JobSchedulerStoreCheckpointTest.LOG.info("Number of journal log files: {}", getNumJournalFiles());
    }

    @Test
    public void testColocatedAddRemoveCleanup() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        scheduler.addListener(new JobListener() {
            @Override
            public void scheduledJob(String id, ByteSequence job) {
                latch.countDown();
            }
        });
        byte[] data = new byte[1024];
        for (int i = 0; i < (data.length); ++i) {
            data[i] = ((byte) (i % 256));
        }
        long time = TimeUnit.SECONDS.toMillis(2);
        scheduler.schedule("Message-1", new ByteSequence(data), "", time, 0, 0);
        Assert.assertTrue(latch.await(70, TimeUnit.SECONDS));
        Assert.assertEquals(0, latch.getCount());
        scheduler.schedule("Message-2", payload, "", time, 0, 0);
        scheduler.schedule("Message-3", payload, "", time, 0, 0);
        Assert.assertTrue(("Should be only one log left: " + (getNumJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumJournalFiles()) == 1;
            }
        }, TimeUnit.MINUTES.toMillis(2)));
        JobSchedulerStoreCheckpointTest.LOG.info("Number of journal log files: {}", getNumJournalFiles());
    }
}

