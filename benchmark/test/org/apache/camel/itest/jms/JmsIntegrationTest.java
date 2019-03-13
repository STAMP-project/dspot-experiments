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
package org.apache.camel.itest.jms;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsIntegrationTest extends CamelTestSupport {
    protected CountDownLatch receivedCountDown = new CountDownLatch(1);

    protected JmsIntegrationTest.MyBean myBean = new JmsIntegrationTest.MyBean();

    @Test
    public void testOneWayInJmsOutPojo() throws Exception {
        // Send a message to the JMS endpoint
        template.sendBodyAndHeader("jms:test", "Hello", "cheese", 123);
        // The Activated endpoint should send it to the pojo due to the configured route.
        assertTrue("The message ware received by the Pojo", receivedCountDown.await(5, TimeUnit.SECONDS));
    }

    protected class MyBean {
        public void onMessage(String body) {
            log.info(("Received: " + body));
            receivedCountDown.countDown();
        }
    }
}

