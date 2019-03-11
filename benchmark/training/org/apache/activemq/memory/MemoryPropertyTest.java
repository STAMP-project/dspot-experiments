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
package org.apache.activemq.memory;


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MemoryPropertyTest extends TestCase {
    private static final transient Logger LOG = LoggerFactory.getLogger(MemoryPropertyTest.class);

    BrokerService broker;

    public void testBrokerInitialized() {
        TestCase.assertTrue("We should have a broker", ((broker) != null));
        TestCase.assertEquals("test-broker", broker.getBrokerName());
        TestCase.assertEquals(1024, broker.getSystemUsage().getMemoryUsage().getLimit());
        TestCase.assertEquals(34, broker.getSystemUsage().getMemoryUsage().getPercentUsageMinDelta());
        TestCase.assertNotNull(broker.getSystemUsage().getStoreUsage().getStore());
        // non persistent broker so no temp storage
        TestCase.assertNull(broker.getSystemUsage().getTempUsage().getStore());
    }
}

