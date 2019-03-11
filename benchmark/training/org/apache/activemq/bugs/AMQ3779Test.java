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
package org.apache.activemq.bugs;


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class AMQ3779Test {
    private static final Logger LOG = Logger.getLogger(AMQ3779Test.class);

    private static final String qName = "QNameToFind";

    private BrokerService brokerService;

    private Appender appender;

    private final AtomicBoolean ok = new AtomicBoolean(false);

    private final AtomicBoolean gotZeroSize = new AtomicBoolean(false);

    @Test(timeout = 60000)
    public void testLogPerDest() throws Exception {
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer(session.createQueue(AMQ3779Test.qName));
        messageProducer.setDeliveryMode(PERSISTENT);
        connection.start();
        messageProducer.send(session.createTextMessage("Hi"));
        connection.close();
        Assert.assertTrue("got expected log message", ok.get());
        Assert.assertFalse("did not get zero size in send message", gotZeroSize.get());
    }
}

