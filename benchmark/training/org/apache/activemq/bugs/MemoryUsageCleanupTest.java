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
package org.apache.activemq.bugs;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MemoryUsageCleanupTest {
    private static final Logger LOG = LoggerFactory.getLogger(MemoryUsageCleanupTest.class);

    private static final String QUEUE_NAME = (MemoryUsageCleanupTest.class.getName()) + "Queue";

    private final String str = new String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");

    private BrokerService broker;

    private String connectionUri;

    private ExecutorService pool;

    private String queueName;

    private Random r = new Random();

    @Test
    public void testIt() throws Exception {
        final int startPercentage = broker.getAdminView().getMemoryPercentUsage();
        MemoryUsageCleanupTest.LOG.info(("MemoryUseage at test start = " + startPercentage));
        for (int i = 0; i < 2; i++) {
            MemoryUsageCleanupTest.LOG.info(((("Started the test iteration: " + i) + " using queueName = ") + (queueName)));
            queueName = (MemoryUsageCleanupTest.QUEUE_NAME) + i;
            final CountDownLatch latch = new CountDownLatch(11);
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    receiveAndDiscard100messages(latch);
                }
            });
            for (int j = 0; j < 10; j++) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        send10000messages(latch);
                    }
                });
            }
            MemoryUsageCleanupTest.LOG.info("Waiting on the send / receive latch");
            latch.await(5, TimeUnit.MINUTES);
            MemoryUsageCleanupTest.LOG.info("Resumed");
            destroyQueue();
            TimeUnit.SECONDS.sleep(2);
        }
        MemoryUsageCleanupTest.LOG.info(("MemoryUseage before awaiting temp store cleanup = " + (broker.getAdminView().getMemoryPercentUsage())));
        Assert.assertTrue((((("MemoryUsage should return to: " + startPercentage) + "% but was ") + (broker.getAdminView().getMemoryPercentUsage())) + "%"), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView().getMemoryPercentUsage()) <= (startPercentage + 1);
            }
        }));
        int endPercentage = broker.getAdminView().getMemoryPercentUsage();
        MemoryUsageCleanupTest.LOG.info(("MemoryUseage at test end = " + endPercentage));
    }
}

