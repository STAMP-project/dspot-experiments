/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.jmx;


import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.activemq.TestSupport;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.junit.Test;


public class JMXMasterSlaveSharedStoreTest extends TestSupport {
    protected XBeanBrokerService master;

    protected XBeanBrokerService slave;

    protected AtomicReference<XBeanBrokerService> slaveAtomicReference = new AtomicReference<XBeanBrokerService>();

    protected CountDownLatch slaveStarted = new CountDownLatch(1);

    protected PersistenceAdapter persistenceAdapter;

    protected File messageStore;

    protected File schedulerStoreFile;

    @Test
    public void testJMXMBeanIsRegisteredForSlave() throws Exception {
        assertFalse(master.isSlave());
        assertTrue(slave.isSlave());
        // Expected MBeans:
        ObjectName masterMBeanName = new ObjectName(getXBeanBrokerServiceMBeanName("master"));
        ObjectName slaveMBeanName = new ObjectName(getXBeanBrokerServiceMBeanName("slave"));
        MBeanServerConnection connection = master.getManagementContext().getMBeanServer();
        assertFalse(connection.queryMBeans(masterMBeanName, null).isEmpty());
        assertFalse(connection.queryMBeans(slaveMBeanName, null).isEmpty());
    }
}

