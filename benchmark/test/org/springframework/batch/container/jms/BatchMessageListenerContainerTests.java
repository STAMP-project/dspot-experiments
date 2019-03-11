/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.container.jms;


import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;


public class BatchMessageListenerContainerTests {
    BatchMessageListenerContainer container;

    @Test
    public void testReceiveAndExecuteWithCallback() throws Exception {
        RepeatTemplate template = new RepeatTemplate();
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        container = getContainer(template);
        container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message arg0) {
            }
        });
        Session session = Mockito.mock(Session.class);
        MessageConsumer consumer = Mockito.mock(MessageConsumer.class);
        Message message = Mockito.mock(Message.class);
        // Expect two calls to consumer (chunk size)...
        Mockito.when(session.getTransacted()).thenReturn(true);
        Mockito.when(session.getTransacted()).thenReturn(true);
        Mockito.when(consumer.receive(1000)).thenReturn(message);
        boolean received = doExecute(session, consumer);
        Assert.assertTrue("Message not received", received);
    }

    @Test
    public void testReceiveAndExecuteWithCallbackReturningNull() throws Exception {
        RepeatTemplate template = new RepeatTemplate();
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        container = getContainer(template);
        Session session = Mockito.mock(Session.class);
        MessageConsumer consumer = Mockito.mock(MessageConsumer.class);
        Message message = null;
        // Expect one call to consumer (chunk size is 2 but terminates on
        // first)...
        Mockito.when(consumer.receive(1000)).thenReturn(message);
        Mockito.when(session.getTransacted()).thenReturn(false);
        boolean received = doExecute(session, consumer);
        Assert.assertFalse("Message not received", received);
    }

    @Test
    public void testTransactionalReceiveAndExecuteWithCallbackThrowingException() throws Exception {
        RepeatTemplate template = new RepeatTemplate();
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        container = getContainer(template);
        container.setSessionTransacted(true);
        try {
            boolean received = doTestWithException(new IllegalStateException("No way!"), true, 2);
            Assert.assertFalse("Message received", received);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("No way!", e.getMessage());
        }
    }

    @Test
    public void testNonTransactionalReceiveAndExecuteWithCallbackThrowingException() throws Exception {
        RepeatTemplate template = new RepeatTemplate();
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        container = getContainer(template);
        container.setSessionTransacted(false);
        boolean received = doTestWithException(new IllegalStateException("No way!"), false, 2);
        Assert.assertTrue("Message not received but listener not transactional so this should be true", received);
    }

    @Test
    public void testNonTransactionalReceiveAndExecuteWithCallbackThrowingError() throws Exception {
        RepeatTemplate template = new RepeatTemplate();
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        container = getContainer(template);
        container.setSessionTransacted(false);
        try {
            boolean received = doTestWithException(new RuntimeException("No way!"), false, 2);
            Assert.assertTrue("Message not received but listener not transactional so this should be true", received);
        } catch (RuntimeException e) {
            Assert.assertEquals("No way!", e.getMessage());
            Assert.fail("Unexpected Error - should be swallowed");
        }
    }
}

