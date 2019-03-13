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
package org.apache.activemq.store.kahadb;


import javax.jms.Destination;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.BaseDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;


public class KahaDBStoreRecoveryExpiryTest {
    private BrokerService broker;

    private ActiveMQConnection connection;

    private final Destination destination = new ActiveMQQueue("Test");

    private Session session;

    @Test
    public void testRestartWitExpired() throws Exception {
        publishMessages(1, 0);
        publishMessages(1, 2000);
        publishMessages(1, 0);
        restartBroker(3000);
        consumeMessages(2);
    }

    @Test
    public void testRestartWitExpiredLargerThanBatchRecovery() throws Exception {
        publishMessages(((BaseDestination.MAX_PAGE_SIZE) + 10), 2000);
        publishMessages(10, 0);
        restartBroker(3000);
        consumeMessages(10);
    }
}

