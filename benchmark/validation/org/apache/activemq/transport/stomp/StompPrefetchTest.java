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


import Stomp.Headers.Subscribe.AckModeValues.AUTO;
import java.util.HashMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StompPrefetchTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompPrefetchTest.class);

    @Test(timeout = 60000)
    public void testTopicSubPrefetch() throws Exception {
        stompConnect();
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/T", AUTO);
        verifyPrefetch(10, new ActiveMQTopic("T"));
    }

    @Test(timeout = 60000)
    public void testDurableSubPrefetch() throws Exception {
        stompConnect();
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("id", "durablesub");
        stompConnection.subscribe("/topic/T", AUTO, headers);
        verifyPrefetch(10, new ActiveMQTopic("T"));
    }

    @Test(timeout = 60000)
    public void testQBrowserSubPrefetch() throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("login", "system");
        headers.put("passcode", "manager");
        headers.put("id", "aBrowser");
        headers.put("browser", "true");
        headers.put("accept-version", "1.1");
        stompConnect();
        stompConnection.connect(headers);
        stompConnection.subscribe("/queue/Q", AUTO, headers);
        verifyPrefetch(10, new ActiveMQQueue("Q"));
    }

    @Test(timeout = 60000)
    public void testQueueSubPrefetch() throws Exception {
        stompConnect();
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/queue/Q", AUTO);
        verifyPrefetch(10, new ActiveMQQueue("Q"));
    }
}

