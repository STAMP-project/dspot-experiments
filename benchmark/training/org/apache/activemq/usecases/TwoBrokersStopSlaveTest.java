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
package org.apache.activemq.usecases;


import java.io.File;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;


/**
 *
 *
 * @author Carlo Dapor
 */
public class TwoBrokersStopSlaveTest extends TestCase {
    private static final File KahaDbDirectory = new File("target/TwoBrokersStopSlaveTest");

    public void testStartMasterAndSlaveShutdownSlaveFirst() throws Exception {
        BrokerService masterBroker = createBroker("masterBroker", 9100);
        BrokerService slaveBroker = createBroker("slaveBroker", 9101);
        Thread.sleep(1000L);
        TestCase.assertTrue(masterBroker.isPersistent());
        TestCase.assertTrue(slaveBroker.isPersistent());
        TestCase.assertFalse(masterBroker.isSlave());
        TestCase.assertTrue(slaveBroker.isSlave());
        // stop slave broker
        slaveBroker.stop();
        slaveBroker.waitUntilStopped();
        masterBroker.stop();
    }
}

