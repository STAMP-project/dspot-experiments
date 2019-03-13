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
package org.apache.camel.component.jms;


import JmsConstants.JMS_DESTINATION;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple request / late reply test.
 */
public class JmsSimpleRequestLateReplyTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsSimpleRequestLateReplyTest.class);

    private static Destination replyDestination;

    private static String cid;

    protected String expectedBody = "Late Reply";

    protected JmsComponent activeMQComponent;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testRequestLateReplyUsingCustomDestinationHeaderForReply() throws Exception {
        doTest(new JmsSimpleRequestLateReplyTest.SendLateReply());
    }

    private class SendLateReply implements Runnable {
        public void run() {
            try {
                JmsSimpleRequestLateReplyTest.LOG.info("Waiting for latch");
                latch.await(30, TimeUnit.SECONDS);
                // wait 1 sec after latch before sending he late replay
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignore
            }
            JmsSimpleRequestLateReplyTest.LOG.info("Sending late reply");
            // use some dummy queue as we override this with the property: JmsConstants.JMS_DESTINATION
            Map<String, Object> headers = new HashMap<>();
            headers.put(JMS_DESTINATION, JmsSimpleRequestLateReplyTest.replyDestination);
            headers.put("JMSCorrelationID", JmsSimpleRequestLateReplyTest.cid);
            template.sendBodyAndHeaders("activemq:dummy", expectedBody, headers);
        }
    }
}

