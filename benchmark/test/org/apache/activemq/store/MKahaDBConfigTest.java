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
package org.apache.activemq.store;


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.BaseDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.store.kahadb.MultiKahaDBPersistenceAdapter;
import org.apache.activemq.usage.SystemUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MKahaDBConfigTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(MKahaDBConfigTest.class);

    /* This tests configuring the different broker properties using
    xbeans-spring
     */
    public void testBrokerConfig() throws Exception {
        BrokerService broker;
        broker = createBroker("org/apache/activemq/store/mKahaDB.xml");
        MKahaDBConfigTest.LOG.info("Success");
        try {
            TestCase.assertEquals("Broker Config Error (brokerName)", "brokerConfigTest", broker.getBrokerName());
            TestCase.assertEquals("Broker Config Error (populateJMSXUserID)", false, broker.isPopulateJMSXUserID());
            TestCase.assertEquals("Broker Config Error (persistent)", true, broker.isPersistent());
            MKahaDBConfigTest.LOG.info("Success");
            SystemUsage systemUsage = broker.getSystemUsage();
            TestCase.assertTrue("Should have a SystemUsage", (systemUsage != null));
            TestCase.assertEquals("SystemUsage Config Error (StoreUsage.limit)", (((1 * 1024) * 1024) * 1024), systemUsage.getStoreUsage().getLimit());
            TestCase.assertEquals("SystemUsage Config Error (StoreUsage.name)", "foo", systemUsage.getStoreUsage().getName());
            TestCase.assertNotNull(systemUsage.getStoreUsage().getStore());
            TestCase.assertTrue(((systemUsage.getStoreUsage().getStore()) instanceof MultiKahaDBPersistenceAdapter));
            MKahaDBConfigTest.LOG.info("Success");
            broker.getAdminView().addQueue("A.B");
            BaseDestination queue = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(new ActiveMQQueue("A.B"))));
            TestCase.assertTrue(((queue.getSystemUsage().getStoreUsage().getStore()) instanceof KahaDBPersistenceAdapter));
            TestCase.assertEquals(((50 * 1024) * 1024), queue.getSystemUsage().getStoreUsage().getLimit());
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }
}

