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
package org.apache.activemq.broker.virtual;


import DeliveryMode.PERSISTENT;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.Arrays;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.EmbeddedBrokerTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test will determine that the producer flow control does not kick in.
 * The original MirroredQueue implementation was causing the queue to update
 * the topic memory usage instead of the queue memory usage.
 * The reason is that the message memory usage instance will not be updated
 * unless it is null.  This was the case when the message was initially sent
 * to the topic but then it was non-null when it was being sent to the queue.
 * When the region destination was set, the associated memory usage was not
 * updated to the passed queue destination and thus the memory usage of the
 * topic was being updated instead.
 *
 * @author Claudio Corsi
 */
public class MirroredQueueCorrectMemoryUsageTest extends EmbeddedBrokerTestSupport {
    private static final Logger logger = LoggerFactory.getLogger(MirroredQueueCorrectMemoryUsageTest.class);

    private static final long ONE_MB = 1048576;

    private static final long TEN_MB = (MirroredQueueCorrectMemoryUsageTest.ONE_MB) * 10;

    private static final long TWENTY_MB = (MirroredQueueCorrectMemoryUsageTest.TEN_MB) * 2;

    private static final String CREATED_STATIC_FOR_PERSISTENT = "created.static.for.persistent";

    @Test(timeout = 40000)
    public void testNoMemoryUsageIncreaseForTopic() throws Exception {
        Connection connection = super.createConnection();
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(MirroredQueueCorrectMemoryUsageTest.CREATED_STATIC_FOR_PERSISTENT);
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(PERSISTENT);
        char[] m = new char[1024];
        Arrays.fill(m, 'x');
        // create some messages that have 1k each
        for (int i = 1; i < 12000; i++) {
            producer.send(session.createTextMessage(new String(m)));
            MirroredQueueCorrectMemoryUsageTest.logger.debug(("Sent message: " + i));
        }
        producer.close();
        session.close();
        connection.stop();
        connection.close();
    }
}

