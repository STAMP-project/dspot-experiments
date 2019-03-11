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


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3879Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ3841Test.class);

    private BrokerService broker;

    private ActiveMQConnectionFactory factory;

    @Test
    public void testConnectionDletesWrongTempDests() throws Exception {
        final Connection connection1 = factory.createConnection();
        final Connection connection2 = factory.createConnection();
        Session session1 = connection1.createSession(false, AUTO_ACKNOWLEDGE);
        Session session2 = connection2.createSession(false, AUTO_ACKNOWLEDGE);
        Destination tempDestAdvisory = AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC;
        MessageConsumer advisoryConsumer = session1.createConsumer(tempDestAdvisory);
        connection1.start();
        Destination tempQueue = session2.createTemporaryQueue();
        MessageProducer tempProducer = session2.createProducer(tempQueue);
        Assert.assertNotNull(advisoryConsumer.receive(5000));
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                    connection1.close();
                } catch (Exception e) {
                }
            }
        });
        t.start();
        for (int i = 0; i < 256; ++i) {
            Message msg = session2.createTextMessage("Temp Data");
            tempProducer.send(msg);
            Thread.sleep(2);
        }
        t.join();
    }
}

