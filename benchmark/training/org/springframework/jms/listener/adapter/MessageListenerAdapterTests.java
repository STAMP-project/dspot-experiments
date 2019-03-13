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
package org.springframework.jms.listener.adapter;


import MessageListenerAdapter.ORIGINAL_DEFAULT_LISTENER_METHOD;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class MessageListenerAdapterTests {
    private static final String TEXT = "I fancy a good cuppa right now";

    private static final Integer NUMBER = new Integer(1);

    private static final MessageListenerAdapterTests.SerializableObject OBJECT = new MessageListenerAdapterTests.SerializableObject();

    private static final String CORRELATION_ID = "100";

    private static final String RESPONSE_TEXT = "... wi' some full fat creamy milk. Top banana.";

    @Test
    public void testWithMessageContentsDelegateForTextMessage() throws Exception {
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        // TextMessage contents must be unwrapped...
        BDDMockito.given(textMessage.getText()).willReturn(MessageListenerAdapterTests.TEXT);
        MessageContentsDelegate delegate = Mockito.mock(MessageContentsDelegate.class);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        adapter.onMessage(textMessage);
        Mockito.verify(delegate).handleMessage(MessageListenerAdapterTests.TEXT);
    }

    @Test
    public void testWithMessageContentsDelegateForBytesMessage() throws Exception {
        BytesMessage bytesMessage = Mockito.mock(BytesMessage.class);
        // BytesMessage contents must be unwrapped...
        BDDMockito.given(bytesMessage.getBodyLength()).willReturn(new Long(MessageListenerAdapterTests.TEXT.getBytes().length));
        BDDMockito.given(bytesMessage.readBytes(ArgumentMatchers.any(byte[].class))).willAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                byte[] bytes = ((byte[]) (invocation.getArguments()[0]));
                ByteArrayInputStream inputStream = new ByteArrayInputStream(MessageListenerAdapterTests.TEXT.getBytes());
                return inputStream.read(bytes);
            }
        });
        MessageContentsDelegate delegate = Mockito.mock(MessageContentsDelegate.class);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        adapter.onMessage(bytesMessage);
        Mockito.verify(delegate).handleMessage(MessageListenerAdapterTests.TEXT.getBytes());
    }

    @Test
    public void testWithMessageContentsDelegateForObjectMessage() throws Exception {
        ObjectMessage objectMessage = Mockito.mock(ObjectMessage.class);
        BDDMockito.given(objectMessage.getObject()).willReturn(MessageListenerAdapterTests.NUMBER);
        MessageContentsDelegate delegate = Mockito.mock(MessageContentsDelegate.class);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        adapter.onMessage(objectMessage);
        Mockito.verify(delegate).handleMessage(MessageListenerAdapterTests.NUMBER);
    }

    @Test
    public void testWithMessageContentsDelegateForObjectMessageWithPlainObject() throws Exception {
        ObjectMessage objectMessage = Mockito.mock(ObjectMessage.class);
        BDDMockito.given(objectMessage.getObject()).willReturn(MessageListenerAdapterTests.OBJECT);
        MessageContentsDelegate delegate = Mockito.mock(MessageContentsDelegate.class);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        adapter.onMessage(objectMessage);
        Mockito.verify(delegate).handleMessage(MessageListenerAdapterTests.OBJECT);
    }

    @Test
    public void testWithMessageDelegate() throws Exception {
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        MessageDelegate delegate = Mockito.mock(MessageDelegate.class);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        // we DON'T want the default SimpleMessageConversion happening...
        adapter.setMessageConverter(null);
        adapter.onMessage(textMessage);
        Mockito.verify(delegate).handleMessage(textMessage);
    }

    @Test
    public void testWhenTheAdapterItselfIsTheDelegate() throws Exception {
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        // TextMessage contents must be unwrapped...
        BDDMockito.given(textMessage.getText()).willReturn(MessageListenerAdapterTests.TEXT);
        StubMessageListenerAdapter adapter = new StubMessageListenerAdapter();
        adapter.onMessage(textMessage);
        Assert.assertTrue(adapter.wasCalled());
    }

    @Test
    public void testRainyDayWithNoApplicableHandlingMethods() throws Exception {
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        // TextMessage contents must be unwrapped...
        BDDMockito.given(textMessage.getText()).willReturn(MessageListenerAdapterTests.TEXT);
        StubMessageListenerAdapter adapter = new StubMessageListenerAdapter();
        setDefaultListenerMethod("walnutsRock");
        adapter.onMessage(textMessage);
        Assert.assertFalse(adapter.wasCalled());
    }

    @Test
    public void testThatAnExceptionThrownFromTheHandlingMethodIsSimplySwallowedByDefault() throws Exception {
        final IllegalArgumentException exception = new IllegalArgumentException();
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        MessageDelegate delegate = Mockito.mock(MessageDelegate.class);
        BDDMockito.willThrow(exception).given(delegate).handleMessage(textMessage);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected void handleListenerException(Throwable ex) {
                Assert.assertNotNull("The Throwable passed to the handleListenerException(..) method must never be null.", ex);
                Assert.assertTrue("The Throwable passed to the handleListenerException(..) method must be of type [ListenerExecutionFailedException].", (ex instanceof ListenerExecutionFailedException));
                ListenerExecutionFailedException lefx = ((ListenerExecutionFailedException) (ex));
                Throwable cause = lefx.getCause();
                Assert.assertNotNull("The cause of a ListenerExecutionFailedException must be preserved.", cause);
                Assert.assertSame(exception, cause);
            }
        };
        // we DON'T want the default SimpleMessageConversion happening...
        adapter.setMessageConverter(null);
        adapter.onMessage(textMessage);
    }

    @Test
    public void testThatTheDefaultMessageConverterisIndeedTheSimpleMessageConverter() throws Exception {
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        Assert.assertNotNull("The default [MessageConverter] must never be null.", adapter.getMessageConverter());
        Assert.assertTrue("The default [MessageConverter] must be of the type [SimpleMessageConverter]", ((adapter.getMessageConverter()) instanceof SimpleMessageConverter));
    }

    @Test
    public void testThatWhenNoDelegateIsSuppliedTheDelegateIsAssumedToBeTheMessageListenerAdapterItself() throws Exception {
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        Assert.assertSame(adapter, adapter.getDelegate());
    }

    @Test
    public void testThatTheDefaultMessageHandlingMethodNameIsTheConstantDefault() throws Exception {
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        Assert.assertEquals(ORIGINAL_DEFAULT_LISTENER_METHOD, adapter.getDefaultListenerMethod());
    }

    @Test
    public void testWithResponsiveMessageDelegate_DoesNotSendReturnTextMessageIfNoSessionSupplied() throws Exception {
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(textMessage)).willReturn(MessageListenerAdapterTests.TEXT);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
        // we DON'T want the default SimpleMessageConversion happening...
        adapter.setMessageConverter(null);
        adapter.onMessage(textMessage);
    }

    @Test
    public void testWithResponsiveMessageDelegateWithDefaultDestination_SendsReturnTextMessageWhenSessionSupplied() throws Exception {
        Queue destination = Mockito.mock(Queue.class);
        TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        // correlation ID is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSCorrelationID()).willReturn(MessageListenerAdapterTests.CORRELATION_ID);
        // Reply-To is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSReplyTo()).willReturn(null);// we want to fall back to the default...

        TextMessage responseTextMessage = Mockito.mock(TextMessage.class);
        QueueSender queueSender = Mockito.mock(QueueSender.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTextMessage(MessageListenerAdapterTests.RESPONSE_TEXT)).willReturn(responseTextMessage);
        BDDMockito.given(session.createProducer(destination)).willReturn(queueSender);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(MessageListenerAdapterTests.RESPONSE_TEXT);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        adapter.setDefaultResponseDestination(destination);
        adapter.onMessage(sentTextMessage, session);
        Mockito.verify(responseTextMessage).setJMSCorrelationID(MessageListenerAdapterTests.CORRELATION_ID);
        Mockito.verify(queueSender).send(responseTextMessage);
        Mockito.verify(queueSender).close();
        Mockito.verify(delegate).handleMessage(sentTextMessage);
    }

    @Test
    public void testWithResponsiveMessageDelegateNoDefaultDestination_SendsReturnTextMessageWhenSessionSupplied() throws Exception {
        Queue destination = Mockito.mock(Queue.class);
        TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        // correlation ID is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSCorrelationID()).willReturn(null);
        BDDMockito.given(sentTextMessage.getJMSMessageID()).willReturn(MessageListenerAdapterTests.CORRELATION_ID);
        // Reply-To is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSReplyTo()).willReturn(destination);
        TextMessage responseTextMessage = Mockito.mock(TextMessage.class);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTextMessage(MessageListenerAdapterTests.RESPONSE_TEXT)).willReturn(responseTextMessage);
        BDDMockito.given(session.createProducer(destination)).willReturn(messageProducer);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(MessageListenerAdapterTests.RESPONSE_TEXT);
        MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        adapter.onMessage(sentTextMessage, session);
        Mockito.verify(responseTextMessage).setJMSCorrelationID(MessageListenerAdapterTests.CORRELATION_ID);
        Mockito.verify(messageProducer).send(responseTextMessage);
        Mockito.verify(messageProducer).close();
        Mockito.verify(delegate).handleMessage(sentTextMessage);
    }

    @Test
    public void testWithResponsiveMessageDelegateNoDefaultDestinationAndNoReplyToDestination_SendsReturnTextMessageWhenSessionSupplied() throws Exception {
        final TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        // correlation ID is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSCorrelationID()).willReturn(MessageListenerAdapterTests.CORRELATION_ID);
        // Reply-To is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSReplyTo()).willReturn(null);
        TextMessage responseTextMessage = Mockito.mock(TextMessage.class);
        final QueueSession session = Mockito.mock(QueueSession.class);
        BDDMockito.given(session.createTextMessage(MessageListenerAdapterTests.RESPONSE_TEXT)).willReturn(responseTextMessage);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(MessageListenerAdapterTests.RESPONSE_TEXT);
        final MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        try {
            adapter.onMessage(sentTextMessage, session);
            Assert.fail("expected CouldNotSendReplyException with InvalidDestinationException");
        } catch (ReplyFailureException ex) {
            Assert.assertEquals(InvalidDestinationException.class, ex.getCause().getClass());
        }
        Mockito.verify(responseTextMessage).setJMSCorrelationID(MessageListenerAdapterTests.CORRELATION_ID);
        Mockito.verify(delegate).handleMessage(sentTextMessage);
    }

    @Test
    public void testWithResponsiveMessageDelegateNoDefaultDestination_SendsReturnTextMessageWhenSessionSupplied_AndSendingThrowsJMSException() throws Exception {
        Queue destination = Mockito.mock(Queue.class);
        final TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        // correlation ID is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSCorrelationID()).willReturn(MessageListenerAdapterTests.CORRELATION_ID);
        // Reply-To is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSReplyTo()).willReturn(destination);
        TextMessage responseTextMessage = Mockito.mock(TextMessage.class);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        BDDMockito.willThrow(new JMSException("Doe!")).given(messageProducer).send(responseTextMessage);
        final QueueSession session = Mockito.mock(QueueSession.class);
        BDDMockito.given(session.createTextMessage(MessageListenerAdapterTests.RESPONSE_TEXT)).willReturn(responseTextMessage);
        BDDMockito.given(session.createProducer(destination)).willReturn(messageProducer);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(MessageListenerAdapterTests.RESPONSE_TEXT);
        final MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        try {
            adapter.onMessage(sentTextMessage, session);
            Assert.fail("expected CouldNotSendReplyException with JMSException");
        } catch (ReplyFailureException ex) {
            Assert.assertEquals(JMSException.class, ex.getCause().getClass());
        }
        Mockito.verify(responseTextMessage).setJMSCorrelationID(MessageListenerAdapterTests.CORRELATION_ID);
        Mockito.verify(messageProducer).close();
        Mockito.verify(delegate).handleMessage(sentTextMessage);
    }

    @Test
    public void testWithResponsiveMessageDelegateDoesNotSendReturnTextMessageWhenSessionSupplied_AndListenerMethodThrowsException() throws Exception {
        final TextMessage message = Mockito.mock(TextMessage.class);
        final QueueSession session = Mockito.mock(QueueSession.class);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.willThrow(new IllegalArgumentException("Doe!")).given(delegate).handleMessage(message);
        final MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        try {
            adapter.onMessage(message, session);
            Assert.fail("expected ListenerExecutionFailedException");
        } catch (ListenerExecutionFailedException ex) {
            /* expected */
        }
    }

    @Test
    public void testWithResponsiveMessageDelegateWhenReturnTypeIsNotAJMSMessageAndNoMessageConverterIsSupplied() throws Exception {
        final TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        final Session session = Mockito.mock(Session.class);
        ResponsiveMessageDelegate delegate = Mockito.mock(ResponsiveMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(MessageListenerAdapterTests.RESPONSE_TEXT);
        final MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        adapter.setMessageConverter(null);
        try {
            adapter.onMessage(sentTextMessage, session);
            Assert.fail("expected CouldNotSendReplyException with MessageConversionException");
        } catch (ReplyFailureException ex) {
            Assert.assertEquals(MessageConversionException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testWithResponsiveMessageDelegateWhenReturnTypeIsAJMSMessageAndNoMessageConverterIsSupplied() throws Exception {
        Queue destination = Mockito.mock(Queue.class);
        final TextMessage sentTextMessage = Mockito.mock(TextMessage.class);
        // correlation ID is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSCorrelationID()).willReturn(MessageListenerAdapterTests.CORRELATION_ID);
        // Reply-To is queried when response is being created...
        BDDMockito.given(sentTextMessage.getJMSReplyTo()).willReturn(destination);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        QueueSender queueSender = Mockito.mock(QueueSender.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createProducer(destination)).willReturn(queueSender);
        ResponsiveJmsTextMessageReturningMessageDelegate delegate = Mockito.mock(ResponsiveJmsTextMessageReturningMessageDelegate.class);
        BDDMockito.given(delegate.handleMessage(sentTextMessage)).willReturn(responseMessage);
        final MessageListenerAdapter adapter = new MessageListenerAdapter(delegate) {
            @Override
            protected Object extractMessage(Message message) {
                return message;
            }
        };
        adapter.setMessageConverter(null);
        adapter.onMessage(sentTextMessage, session);
        Mockito.verify(responseMessage).setJMSCorrelationID(MessageListenerAdapterTests.CORRELATION_ID);
        Mockito.verify(queueSender).send(responseMessage);
        Mockito.verify(queueSender).close();
    }

    @SuppressWarnings("serial")
    private static class SerializableObject implements Serializable {}
}

