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


import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.scheduler.JobSchedulerStoreImpl;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KahaDBSchedulerIndexRebuildTest {
    static final Logger LOG = LoggerFactory.getLogger(KahaDBSchedulerIndexRebuildTest.class);

    @Rule
    public TestName name = new TestName();

    private BrokerService broker = null;

    private final int NUM_JOBS = 50;

    static String basedir;

    static {
        try {
            ProtectionDomain protectionDomain = SchedulerDBVersionTest.class.getProtectionDomain();
            KahaDBSchedulerIndexRebuildTest.basedir = new File(new File(protectionDomain.getCodeSource().getLocation().getPath()), "../.").getCanonicalPath();
        } catch (IOException e) {
            KahaDBSchedulerIndexRebuildTest.basedir = ".";
        }
    }

    private File schedulerStoreDir;

    private final File storeDir = new File(KahaDBSchedulerIndexRebuildTest.basedir, "activemq-data/store/");

    @Test
    public void testIndexRebuilds() throws Exception {
        IOHelper.deleteFile(schedulerStoreDir);
        JobSchedulerStoreImpl schedulerStore = createScheduler();
        broker = createBroker(schedulerStore);
        broker.start();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost");
        Connection connection = cf.createConnection();
        connection.start();
        for (int i = 0; i < (NUM_JOBS); ++i) {
            scheduleRepeating(connection);
        }
        connection.close();
        JobScheduler scheduler = schedulerStore.getJobScheduler("JMS");
        Assert.assertNotNull(scheduler);
        Assert.assertEquals(NUM_JOBS, scheduler.getAllJobs().size());
        broker.stop();
        IOHelper.delete(new File(schedulerStoreDir, "scheduleDB.data"));
        schedulerStore = createScheduler();
        broker = createBroker(schedulerStore);
        broker.start();
        scheduler = schedulerStore.getJobScheduler("JMS");
        Assert.assertNotNull(scheduler);
        Assert.assertEquals(NUM_JOBS, scheduler.getAllJobs().size());
    }

    @Test
    public void testIndexRebuildsAfterSomeJobsExpire() throws Exception {
        IOHelper.deleteFile(schedulerStoreDir);
        JobSchedulerStoreImpl schedulerStore = createScheduler();
        broker = createBroker(schedulerStore);
        broker.start();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost");
        Connection connection = cf.createConnection();
        connection.start();
        for (int i = 0; i < (NUM_JOBS); ++i) {
            scheduleRepeating(connection);
            scheduleOneShot(connection);
        }
        connection.close();
        JobScheduler scheduler = schedulerStore.getJobScheduler("JMS");
        Assert.assertNotNull(scheduler);
        Assert.assertEquals(((NUM_JOBS) * 2), scheduler.getAllJobs().size());
        final JobScheduler awaitingOneShotTimeout = scheduler;
        Assert.assertTrue("One shot jobs should time out", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (awaitingOneShotTimeout.getAllJobs().size()) == (NUM_JOBS);
            }
        }, TimeUnit.MINUTES.toMillis(2)));
        broker.stop();
        IOHelper.delete(new File(schedulerStoreDir, "scheduleDB.data"));
        schedulerStore = createScheduler();
        broker = createBroker(schedulerStore);
        broker.start();
        scheduler = schedulerStore.getJobScheduler("JMS");
        Assert.assertNotNull(scheduler);
        Assert.assertEquals(NUM_JOBS, scheduler.getAllJobs().size());
    }
}

