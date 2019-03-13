/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class BrowseDLQTest {
    private static final int NUM_MESSAGES = 100;

    private BrokerService brokerService;

    private ActiveMQQueue testQueue = new ActiveMQQueue("TEST.FOO");

    private ActiveMQQueue dlq = new ActiveMQQueue("ActiveMQ.DLQ");

    @Test
    public void testCannotBrowseDLQAsTable() throws Exception {
        startBroker();
        // send 100 messages to queue with TTL of 1 second
        sendMessagesToBeExpired();
        // let's let the messages expire
        TimeUnit.SECONDS.sleep(2);
        assertCanBrowse();
    }
}

