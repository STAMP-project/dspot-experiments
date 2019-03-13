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
import org.apache.activemq.store.kahadb.plist.PListStoreImpl;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TempStoreDataCleanupTest {
    private static final Logger LOG = LoggerFactory.getLogger(TempStoreDataCleanupTest.class);

    private static final String QUEUE_NAME = (TempStoreDataCleanupTest.class.getName()) + "Queue";

    private final String str = new String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");

    private BrokerService broker;

    private String connectionUri;

    private ExecutorService pool;

    private String queueName;

    private Random r = new Random();

    @Test
    public void testIt() throws Exception {
        int startPercentage = broker.getAdminView().getMemoryPercentUsage();
        TempStoreDataCleanupTest.LOG.info(("MemoryUsage at test start = " + startPercentage));
        for (int i = 0; i < 2; i++) {
            TempStoreDataCleanupTest.LOG.info(((("Started the test iteration: " + i) + " using queueName = ") + (queueName)));
            queueName = (TempStoreDataCleanupTest.QUEUE_NAME) + i;
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
            TempStoreDataCleanupTest.LOG.info("Waiting on the send / receive latch");
            latch.await(5, TimeUnit.MINUTES);
            TempStoreDataCleanupTest.LOG.info("Resumed");
            destroyQueue();
            TimeUnit.SECONDS.sleep(2);
        }
        TempStoreDataCleanupTest.LOG.info(("MemoryUsage before awaiting temp store cleanup = " + (broker.getAdminView().getMemoryPercentUsage())));
        final PListStoreImpl pa = ((PListStoreImpl) (broker.getTempDataStore()));
        Assert.assertTrue(("only one journal file should be left: " + (pa.getJournal().getFileMap().size())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (pa.getJournal().getFileMap().size()) == 1;
            }
        }, TimeUnit.MINUTES.toMillis(3)));
        int endPercentage = broker.getAdminView().getMemoryPercentUsage();
        TempStoreDataCleanupTest.LOG.info(("MemoryUseage at test end = " + endPercentage));
        Assert.assertEquals(startPercentage, endPercentage);
    }
}

