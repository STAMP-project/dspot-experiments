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
package org.apache.activemq.transport.stomp;


import Stomp.NULL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


/**
 * Test that the inactivity monitor works as expected.
 */
@RunWith(Parameterized.class)
public class StompInactivityMonitorTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompInactivityMonitorTest.class);

    private final String transportScheme;

    public StompInactivityMonitorTest(String transportScheme) {
        this.transportScheme = transportScheme;
    }

    @Test
    public void test() throws Exception {
        stompConnect();
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:1000,0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String response = stompConnection.receiveFrame().trim();
        StompInactivityMonitorTest.LOG.info("Broker sent response: {}", response);
        String messageHead = ((("SEND\n" + (("receipt:1" + "\n") + "destination:/queue/")) + (getQueueName())) + "\n\n") + "AAAA";
        stompConnection.sendFrame(messageHead);
        for (int i = 0; i < 30; i++) {
            stompConnection.sendFrame("A");
            Thread.sleep(100);
        }
        stompConnection.sendFrame(NULL);
        response = stompConnection.receiveFrame().trim();
        Assert.assertTrue(response.startsWith("RECEIPT"));
    }
}

