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


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Header;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsPerformanceTest extends CamelTestSupport {
    private List<Integer> receivedHeaders = new ArrayList<>(getMessageCount());

    private List<Object> receivedMessages = new ArrayList<>(getMessageCount());

    @Test
    public void testSendingAndReceivingMessages() throws Exception {
        log.info("Sending {} messages", getMessageCount());
        sendLoop(getMessageCount());
        log.info("Sending {} messages completed, now will assert on their content as well as the order of their receipt", getMessageCount());
        // should wait a bit to make sure all messages have been received by the MyBean#onMessage() method
        // as this happens asynchronously, that's not inside the 'main' thread
        Thread.sleep(3000);
        assertExpectedMessagesReceived();
    }

    protected class MyBean {
        public void onMessage(@Header("counter")
        int counter, Object body) {
            // the invocation of this method happens inside the same thread so no need for a thread-safe list here
            receivedHeaders.add(counter);
            receivedMessages.add(body);
        }
    }
}

