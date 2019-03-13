/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager;


import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.ClientBaseWithFixes;
import org.apache.hadoop.ha.ServiceFailedException;
import org.apache.hadoop.service.ServiceStateException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMEmbeddedElector extends ClientBaseWithFixes {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMEmbeddedElector.class.getName());

    private static final String RM1_NODE_ID = "rm1";

    private static final int RM1_PORT_BASE = 10000;

    private static final String RM2_NODE_ID = "rm2";

    private static final int RM2_PORT_BASE = 20000;

    private Configuration conf;

    private AtomicBoolean callbackCalled;

    private AtomicInteger transitionToActiveCounter;

    private AtomicInteger transitionToStandbyCounter;

    private enum SyncTestType {

        ACTIVE,
        STANDBY,
        NEUTRAL,
        ACTIVE_TIMING,
        STANDBY_TIMING;}

    /**
     * Test that tries to see if there is a deadlock between
     * (a) the thread stopping the RM
     * (b) thread processing the ZK event asking RM to transition to active
     *
     * The test times out if there is a deadlock.
     */
    @Test(timeout = 10000)
    public void testDeadlockShutdownBecomeActive() throws InterruptedException {
        MockRM rm = new TestRMEmbeddedElector.MockRMWithElector(conf, 1000);
        start();
        TestRMEmbeddedElector.LOG.info("Waiting for callback");
        while (!(callbackCalled.get()));
        TestRMEmbeddedElector.LOG.info("Stopping RM");
        stop();
        TestRMEmbeddedElector.LOG.info("Stopped RM");
    }

    /**
     * Test that neutral mode plays well with all other transitions.
     *
     * @throws IOException
     * 		if there's an issue transitioning
     * @throws InterruptedException
     * 		if interrupted
     */
    @Test
    public void testCallbackSynchronization() throws IOException, InterruptedException, TimeoutException {
        testCallbackSynchronization(TestRMEmbeddedElector.SyncTestType.ACTIVE);
        testCallbackSynchronization(TestRMEmbeddedElector.SyncTestType.STANDBY);
        testCallbackSynchronization(TestRMEmbeddedElector.SyncTestType.NEUTRAL);
        testCallbackSynchronization(TestRMEmbeddedElector.SyncTestType.ACTIVE_TIMING);
        testCallbackSynchronization(TestRMEmbeddedElector.SyncTestType.STANDBY_TIMING);
    }

    /**
     * Test that active elector service triggers a fatal RM Event when connection
     * to ZK fails. YARN-8409
     */
    @Test
    public void testFailureToConnectToZookeeper() throws Exception {
        stopServer();
        Configuration myConf = new Configuration(conf);
        ResourceManager rm = new MockRM(conf);
        ActiveStandbyElectorBasedElectorService ees = new ActiveStandbyElectorBasedElectorService(rm);
        try {
            ees.init(myConf);
            Assert.fail("expect failure to connect to Zookeeper");
        } catch (ServiceStateException sse) {
            Assert.assertTrue(sse.getMessage().contains("ConnectionLoss"));
        }
    }

    private class MockRMWithElector extends MockRM {
        private long delayMs = 0;

        MockRMWithElector(Configuration conf) {
            super(conf);
        }

        MockRMWithElector(Configuration conf, long delayMs) {
            this(conf);
            this.delayMs = delayMs;
        }

        @Override
        protected EmbeddedElector createEmbeddedElector() {
            return new ActiveStandbyElectorBasedElectorService(this) {
                @Override
                public void becomeActive() throws ServiceFailedException {
                    try {
                        callbackCalled.set(true);
                        TestRMEmbeddedElector.LOG.info("Callback called. Sleeping now");
                        Thread.sleep(delayMs);
                        TestRMEmbeddedElector.LOG.info("Sleep done");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.becomeActive();
                }
            };
        }
    }
}

