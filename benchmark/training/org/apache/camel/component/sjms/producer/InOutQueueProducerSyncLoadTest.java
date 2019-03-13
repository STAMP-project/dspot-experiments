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
package org.apache.camel.component.sjms.producer;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.apache.camel.component.sjms.support.JmsTestSupport;
import org.junit.Test;


public class InOutQueueProducerSyncLoadTest extends JmsTestSupport {
    private static final String TEST_DESTINATION_NAME = "in.out.queue.producer.test";

    private MessageConsumer mc1;

    private MessageConsumer mc2;

    public InOutQueueProducerSyncLoadTest() {
    }

    /**
     * Test to verify that when using the consumer listener for the InOut
     * producer we get the correct message back.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInOutQueueProducer() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 5000; i++) {
            final int tempI = i;
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    try {
                        final String requestText = "Message " + tempI;
                        final String responseText = "Response Message " + tempI;
                        String response = template.requestBody("direct:start", requestText, String.class);
                        assertNotNull(response);
                        assertEquals(responseText, response);
                    } catch (Exception e) {
                        log.error("TODO Auto-generated catch block", e);
                    }
                }
            };
            executor.execute(worker);
        }
        while ((context.getInflightRepository().size()) > 0) {
        } 
        executor.shutdown();
        while (!(executor.isTerminated())) {
            // 
        } 
    }

    protected class MyMessageListener implements MessageListener {
        private MessageProducer mp;

        @Override
        public void onMessage(Message message) {
            try {
                TextMessage request = ((TextMessage) (message));
                String text = request.getText();
                TextMessage response = getSession().createTextMessage();
                response.setText(("Response " + text));
                response.setJMSCorrelationID(request.getJMSCorrelationID());
                if ((mp) == null) {
                    mp = getSession().createProducer(message.getJMSReplyTo());
                }
                mp.send(response);
            } catch (JMSException e) {
                fail(e.getLocalizedMessage());
            }
        }

        public void close() throws JMSException {
            mp.close();
        }
    }
}

