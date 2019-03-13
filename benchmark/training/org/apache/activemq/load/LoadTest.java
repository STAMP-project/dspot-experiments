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
package org.apache.activemq.load;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class LoadTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(LoadTest.class);

    protected BrokerService broker;

    protected String bindAddress = "tcp://localhost:61616";

    protected LoadController controller;

    protected LoadClient[] clients;

    protected ConnectionFactory factory;

    protected Destination destination;

    protected int numberOfClients = 50;

    protected int deliveryMode = DeliveryMode.PERSISTENT;

    protected int batchSize = 1000;

    protected int numberOfBatches = 10;

    protected int timeout = Integer.MAX_VALUE;

    protected boolean connectionPerMessage = false;

    protected Connection managementConnection;

    protected Session managementSession;

    public void testLoad() throws InterruptedException, JMSException {
        for (int i = 0; i < (numberOfClients); i++) {
            clients[i].start();
        }
        controller.start();
        TestCase.assertEquals(((batchSize) * (numberOfBatches)), controller.awaitTestComplete());
    }
}

