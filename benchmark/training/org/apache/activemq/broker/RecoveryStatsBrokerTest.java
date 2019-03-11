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


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.store.MessageStoreStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RecoveryStatsBrokerTest extends BrokerRestartTestSupport {
    private RecoveryStatsBrokerTest.RestartType restartType;

    enum RestartType {

        NORMAL,
        FULL_RECOVERY,
        UNCLEAN_SHUTDOWN;}

    public RecoveryStatsBrokerTest(RecoveryStatsBrokerTest.RestartType restartType) {
        this.restartType = restartType;
    }

    @Test(timeout = 60 * 1000)
    public void testStaticsRecovery() throws Exception {
        List<ActiveMQDestination> destinations = ImmutableList.of(new ActiveMQQueue("TEST.A"), new ActiveMQQueue("TEST.B"));
        Random random = new Random();
        Map<ActiveMQDestination, Integer> consumedMessages = new HashMap<>();
        destinations.forEach(( destination) -> consumedMessages.put(destination, 0));
        int numberOfMessages = 400;
        StubConnection connection = createConnection();
        ConnectionInfo connectionInfo = createConnectionInfo();
        SessionInfo sessionInfo = createSessionInfo(connectionInfo);
        ProducerInfo producerInfo = createProducerInfo(sessionInfo);
        connection.send(connectionInfo);
        connection.send(sessionInfo);
        connection.send(producerInfo);
        for (int i = 0; i < numberOfMessages; i++) {
            for (ActiveMQDestination destination : destinations) {
                Message message = createMessage(producerInfo, destination);
                message.setPersistent(true);
                message.setProducerId(message.getMessageId().getProducerId());
                connection.request(message);
            }
        }
        Map<ActiveMQDestination, MessageStoreStatistics> originalStatistics = getCurrentStatistics(destinations);
        checkStatistics(destinations, originalStatistics);
        restartBroker(restartType);
        checkStatistics(destinations, originalStatistics);
        for (ActiveMQDestination destination : destinations) {
            consume(destination, 100, false);
        }
        checkStatistics(destinations, originalStatistics);
        restartBroker(restartType);
        checkStatistics(destinations, originalStatistics);
        for (ActiveMQDestination destination : destinations) {
            int messagesToConsume = random.nextInt(numberOfMessages);
            consume(destination, messagesToConsume, true);
            consumedMessages.compute(destination, ( key, value) -> value = value + messagesToConsume);
        }
        originalStatistics = getCurrentStatistics(destinations);
        for (ActiveMQDestination destination : destinations) {
            int consumedCount = consumedMessages.get(destination);
            assertEquals("", (numberOfMessages - consumedCount), originalStatistics.get(destination).getMessageCount().getCount());
        }
        checkStatistics(destinations, originalStatistics);
        restartBroker(restartType);
        checkStatistics(destinations, originalStatistics);
    }
}

