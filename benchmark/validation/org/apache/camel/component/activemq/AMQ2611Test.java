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
package org.apache.camel.component.activemq;


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2611Test extends TestCase {
    private static final String BROKER_URL = "tcp://localhost:61616";

    private static final String QUEUE_NAME = "test.queue";

    private static final Logger LOG = LoggerFactory.getLogger(AMQ2611Test.class);

    private BrokerService brokerService;

    private CamelContext camelContext;

    public static class Consumer {
        public void consume(@Body
        String message) {
            AMQ2611Test.LOG.info(("consume message = " + message));
        }
    }

    public void testConnections() {
        try {
            createBroker();
            int i = 0;
            while ((i++) < 5) {
                createCamelContext();
                Thread.sleep(1000);
                destroyCamelContext();
                Thread.sleep(1000);
                TestCase.assertEquals(0, brokerService.getConnectorByName(AMQ2611Test.BROKER_URL).getConnections().size());
            } 
        } catch (Exception e) {
            AMQ2611Test.LOG.warn("run", e);
        }
    }
}

