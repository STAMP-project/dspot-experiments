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
package org.apache.activemq.broker;


import org.apache.activemq.TestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class QueueMbeanRestartTest extends TestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(QueueMbeanRestartTest.class);

    BrokerService broker;

    private final TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    public QueueMbeanRestartTest(TestSupport.PersistenceAdapterChoice choice) {
        this.persistenceAdapterChoice = choice;
    }

    @Test(timeout = 60000)
    public void testMBeanPresenceOnRestart() throws Exception {
        createBroker(true);
        sendMessages();
        verifyPresenceOfQueueMbean();
        QueueMbeanRestartTest.LOG.info("restart....");
        restartBroker();
        verifyPresenceOfQueueMbean();
    }
}

