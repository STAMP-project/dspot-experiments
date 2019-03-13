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
package org.apache.activemq.store.kahadb.scheduler;


import java.io.File;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ7086Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ7086Test.class);

    BrokerService brokerService;

    JobSchedulerStoreImpl jobSchedulerStore;

    KahaDBPersistenceAdapter kahaDBPersistenceAdapter;

    @Test
    public void testGcDoneAtStop() throws Exception {
        brokerService = createBroker(true);
        brokerService.start();
        produceWithScheduledDelayAndConsume();
        AMQ7086Test.LOG.info(("job store: " + (jobSchedulerStore)));
        int numSchedulerFiles = jobSchedulerStore.getJournal().getFileMap().size();
        AMQ7086Test.LOG.info(("kahadb store: " + (kahaDBPersistenceAdapter)));
        int numKahadbFiles = kahaDBPersistenceAdapter.getStore().getJournal().getFileMap().size();
        AMQ7086Test.LOG.info("Num files, job store: {}, messge store: {}", numKahadbFiles, numKahadbFiles);
        // pull the dirs before we stop
        File jobDir = jobSchedulerStore.getJournal().getDirectory();
        File kahaDir = kahaDBPersistenceAdapter.getStore().getJournal().getDirectory();
        brokerService.stop();
        Assert.assertEquals("Expected job store data files", 1, verifyFilesOnDisk(jobDir));
        Assert.assertEquals("Expected kahadb data files", 1, verifyFilesOnDisk(kahaDir));
    }

    @Test
    public void testNoGcAtStop() throws Exception {
        brokerService = createBroker(false);
        brokerService.start();
        produceWithScheduledDelayAndConsume();
        AMQ7086Test.LOG.info(("job store: " + (jobSchedulerStore)));
        int numSchedulerFiles = jobSchedulerStore.getJournal().getFileMap().size();
        AMQ7086Test.LOG.info(("kahadb store: " + (kahaDBPersistenceAdapter)));
        int numKahadbFiles = kahaDBPersistenceAdapter.getStore().getJournal().getFileMap().size();
        AMQ7086Test.LOG.info("Num files, job store: {}, messge store: {}", numKahadbFiles, numKahadbFiles);
        // pull the dirs before we stop
        File jobDir = jobSchedulerStore.getJournal().getDirectory();
        File kahaDir = kahaDBPersistenceAdapter.getStore().getJournal().getDirectory();
        brokerService.stop();
        Assert.assertEquals("Expected job store data files", numSchedulerFiles, verifyFilesOnDisk(jobDir));
        Assert.assertEquals("Expected kahadb data files", numKahadbFiles, verifyFilesOnDisk(kahaDir));
    }
}

