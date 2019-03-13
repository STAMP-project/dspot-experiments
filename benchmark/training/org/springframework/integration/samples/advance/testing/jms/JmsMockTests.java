/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.samples.advance.testing.jms;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.JMSException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author David Turanski
 * @author Gunnar Hillert
 * @author Gary Russell
 * @author Artem Bilan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JmsMockTests {
    private static final Log LOGGER = LogFactory.getLog(JmsMockTests.class);

    private final AtomicReference<String> testMessageHolder = new AtomicReference<>();

    @Autowired
    private JmsTemplate mockJmsTemplate;

    @Autowired
    private SourcePollingChannelAdapter jmsInboundChannelAdapter;

    @Autowired
    @Qualifier("inputChannel")
    private MessageChannel inputChannel;

    @Autowired
    @Qualifier("outputChannel")
    private SubscribableChannel outputChannel;

    @Autowired
    @Qualifier("invalidMessageChannel")
    private SubscribableChannel invalidMessageChannel;

    /**
     * This test verifies that a message received on a polling JMS inbound channel adapter is
     * routed to the designated channel and that the message payload is as expected
     *
     * @throws JMSException
     * 		
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testReceiveMessage() throws IOException, InterruptedException, JMSException {
        String msg = "hello";
        boolean sent = verifyJmsMessageReceivedOnOutputChannel(msg, outputChannel, new JmsMockTests.CountDownHandler() {
            @Override
            protected void verifyMessage(Message<?> message) {
                Assert.assertEquals("hello", message.getPayload());
            }
        });
        Assert.assertTrue("message not sent to expected output channel", sent);
    }

    /**
     * This test verifies that a message received on a polling JMS inbound channel adapter is
     * routed to the errorChannel and that the message payload is the expected exception
     *
     * @throws JMSException
     * 		
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testReceiveInvalidMessage() throws IOException, InterruptedException, JMSException {
        String msg = "whoops";
        boolean sent = verifyJmsMessageReceivedOnOutputChannel(msg, invalidMessageChannel, new JmsMockTests.CountDownHandler() {
            @Override
            protected void verifyMessage(Message<?> message) {
                Assert.assertEquals("invalid payload", message.getPayload());
            }
        });
        Assert.assertTrue("message not sent to expected output channel", sent);
    }

    /* A MessageHandler that uses a CountDownLatch to synchronize with the calling thread */
    private abstract class CountDownHandler implements MessageHandler {
        CountDownLatch latch;

        public final void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        protected abstract void verifyMessage(Message<?> message);

        /* (non-Javadoc)

        @see
        org.springframework.integration.core.MessageHandler#handleMessage
        (org.springframework.integration.Message)
         */
        @Override
        public void handleMessage(Message<?> message) throws MessagingException {
            verifyMessage(message);
            latch.countDown();
        }
    }
}

