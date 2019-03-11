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
package org.apache.activemq.transport.failover;


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FailoverTransportBackupsTest {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverTransportBackupsTest.class);

    protected Transport transport;

    protected FailoverTransport failoverTransport;

    private int transportInterruptions;

    private int transportResumptions;

    BrokerService broker1;

    BrokerService broker2;

    BrokerService broker3;

    @Test
    public void testBackupsAreCreated() throws Exception {
        this.transport = createTransport(2);
        Assert.assertNotNull(failoverTransport);
        Assert.assertTrue(failoverTransport.isBackup());
        Assert.assertEquals(2, failoverTransport.getBackupPoolSize());
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 2;
            }
        }));
    }

    @Test
    public void testFailoverToBackups() throws Exception {
        this.transport = createTransport(2);
        Assert.assertNotNull(failoverTransport);
        Assert.assertTrue(failoverTransport.isBackup());
        Assert.assertEquals(2, failoverTransport.getBackupPoolSize());
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 2;
            }
        }));
        Assert.assertEquals("conected to..", "1", currentBrokerInfo.getBrokerName());
        broker1.stop();
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 1;
            }
        }));
        Assert.assertTrue("Incorrect number of Transport interruptions", ((transportInterruptions) >= 1));
        Assert.assertTrue("Incorrect number of Transport resumptions", ((transportResumptions) >= 1));
        Assert.assertEquals("conected to..", "2", currentBrokerInfo.getBrokerName());
        broker2.stop();
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 0;
            }
        }));
        Assert.assertTrue("Incorrect number of Transport interruptions", ((transportInterruptions) >= 2));
        Assert.assertTrue("Incorrect number of Transport resumptions", ((transportResumptions) >= 2));
        Assert.assertEquals("conected to..", "3", currentBrokerInfo.getBrokerName());
    }

    @Test
    public void testBackupsRefilled() throws Exception {
        this.transport = createTransport(1);
        Assert.assertNotNull(failoverTransport);
        Assert.assertTrue(failoverTransport.isBackup());
        Assert.assertEquals(1, failoverTransport.getBackupPoolSize());
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 1;
            }
        }));
        broker1.stop();
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 1;
            }
        }));
        broker2.stop();
        Assert.assertTrue("Timed out waiting for Backups to connect.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                FailoverTransportBackupsTest.LOG.debug(("Current Backup Count = " + (failoverTransport.getCurrentBackups())));
                return (failoverTransport.getCurrentBackups()) == 0;
            }
        }));
    }

    BrokerInfo currentBrokerInfo;
}

