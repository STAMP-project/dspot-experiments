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
package org.apache.activemq.broker.region;


import javax.jms.Connection;
import javax.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Confirm that the broker does not resend unacknowledged messages during a broker shutdown.
 */
public class QueueResendDuringShutdownTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueueResendDuringShutdownTest.class);

    public static final int NUM_CONNECTION_TO_TEST = 8;

    private static boolean iterationFoundFailure = false;

    private BrokerService broker;

    private ActiveMQConnectionFactory factory;

    private Connection[] connections;

    private Connection producerConnection;

    private Queue queue;

    private Object messageReceiveSync = new Object();

    private int receiveCount;

    @Test(timeout = 3000)
    public void testRedeliverAtBrokerShutdownAutoAckMsgListenerIter1() throws Throwable {
        runTestIteration();
    }

    @Test(timeout = 3000)
    public void testRedeliverAtBrokerShutdownAutoAckMsgListenerIter2() throws Throwable {
        runTestIteration();
    }

    @Test(timeout = 3000)
    public void testRedeliverAtBrokerShutdownAutoAckMsgListenerIter3() throws Throwable {
        runTestIteration();
    }
}

