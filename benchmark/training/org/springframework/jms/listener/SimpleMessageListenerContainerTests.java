/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.jms.listener;


import Session.AUTO_ACKNOWLEDGE;
import java.util.HashSet;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.StubQueue;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Mark Fisher
 */
public class SimpleMessageListenerContainerTests {
    private static final String DESTINATION_NAME = "foo";

    private static final String EXCEPTION_MESSAGE = "This.Is.It";

    private static final StubQueue QUEUE_DESTINATION = new StubQueue();

    private final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    @Test
    public void testSettingMessageListenerToANullType() {
        this.container.setMessageListener(null);
        Assert.assertNull(this.container.getMessageListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingMessageListenerToAnUnsupportedType() {
        this.container.setMessageListener("Bingo");
    }

    @Test
    public void testSessionTransactedModeReallyDoesDefaultToFalse() {
        Assert.assertFalse(("The [pubSubLocal] property of SimpleMessageListenerContainer " + ("must default to false. Change this test (and the " + "attendant Javadoc) if you have changed the default.")), this.container.isPubSubNoLocal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingConcurrentConsumersToZeroIsNotAllowed() {
        this.container.setConcurrentConsumers(0);
        this.container.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingConcurrentConsumersToANegativeValueIsNotAllowed() {
        this.container.setConcurrentConsumers((-198));
        this.container.afterPropertiesSet();
    }

    @Test
    public void testContextRefreshedEventDoesNotStartTheConnectionIfAutoStartIsSetToFalse() throws Exception {
        MessageConsumer messageConsumer = Mockito.mock(MessageConsumer.class);
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SimpleMessageListenerContainerTests.TestMessageListener());
        this.container.setAutoStartup(false);
        this.container.afterPropertiesSet();
        GenericApplicationContext context = new GenericApplicationContext();
        context.getBeanFactory().registerSingleton("messageListenerContainer", this.container);
        context.refresh();
        Mockito.verify(connection).setExceptionListener(this.container);
    }

    @Test
    public void testContextRefreshedEventStartsTheConnectionByDefault() throws Exception {
        MessageConsumer messageConsumer = Mockito.mock(MessageConsumer.class);
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SimpleMessageListenerContainerTests.TestMessageListener());
        this.container.afterPropertiesSet();
        GenericApplicationContext context = new GenericApplicationContext();
        context.getBeanFactory().registerSingleton("messageListenerContainer", this.container);
        context.refresh();
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
    }

    @Test
    public void testCorrectSessionExposedForSessionAwareMessageListenerInvocation() throws Exception {
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        final Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        // an exception is thrown, so the rollback logic is being applied here...
        BDDMockito.given(session.getTransacted()).willReturn(false);
        BDDMockito.given(session.getAcknowledgeMode()).willReturn(AUTO_ACKNOWLEDGE);
        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        final Set<String> failure = new HashSet<>(1);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SessionAwareMessageListener<Message>() {
            @Override
            public void onMessage(Message message, @Nullable
            Session sess) {
                try {
                    // Check correct Session passed into SessionAwareMessageListener.
                    Assert.assertSame(sess, session);
                } catch (Throwable ex) {
                    failure.add(("MessageListener execution failed: " + ex));
                }
            }
        });
        this.container.afterPropertiesSet();
        this.container.start();
        final Message message = Mockito.mock(Message.class);
        messageConsumer.sendMessage(message);
        if (!(failure.isEmpty())) {
            Assert.fail(failure.iterator().next().toString());
        }
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
    }

    @Test
    public void testTaskExecutorCorrectlyInvokedWhenSpecified() throws Exception {
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        final Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        BDDMockito.given(session.getTransacted()).willReturn(false);
        BDDMockito.given(session.getAcknowledgeMode()).willReturn(AUTO_ACKNOWLEDGE);
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        final SimpleMessageListenerContainerTests.TestMessageListener listener = new SimpleMessageListenerContainerTests.TestMessageListener();
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(listener);
        this.container.setTaskExecutor(new TaskExecutor() {
            @Override
            public void execute(Runnable task) {
                listener.executorInvoked = true;
                Assert.assertFalse(listener.listenerInvoked);
                task.run();
                Assert.assertTrue(listener.listenerInvoked);
            }
        });
        this.container.afterPropertiesSet();
        this.container.start();
        final Message message = Mockito.mock(Message.class);
        messageConsumer.sendMessage(message);
        Assert.assertTrue(listener.executorInvoked);
        Assert.assertTrue(listener.listenerInvoked);
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
    }

    @Test
    public void testRegisteredExceptionListenerIsInvokedOnException() throws Exception {
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        // an exception is thrown, so the rollback logic is being applied here...
        BDDMockito.given(session.getTransacted()).willReturn(false);
        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        final JMSException theException = new JMSException(SimpleMessageListenerContainerTests.EXCEPTION_MESSAGE);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SessionAwareMessageListener<Message>() {
            @Override
            public void onMessage(Message message, @Nullable
            Session session) throws JMSException {
                throw theException;
            }
        });
        ExceptionListener exceptionListener = Mockito.mock(ExceptionListener.class);
        this.container.setExceptionListener(exceptionListener);
        this.container.afterPropertiesSet();
        this.container.start();
        // manually trigger an Exception with the above bad MessageListener...
        final Message message = Mockito.mock(Message.class);
        // a Throwable from a MessageListener MUST simply be swallowed...
        messageConsumer.sendMessage(message);
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
        Mockito.verify(exceptionListener).onException(theException);
    }

    @Test
    public void testRegisteredErrorHandlerIsInvokedOnException() throws Exception {
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        // an exception is thrown, so the rollback logic is being applied here...
        BDDMockito.given(session.getTransacted()).willReturn(false);
        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        final IllegalStateException theException = new IllegalStateException("intentional test failure");
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SessionAwareMessageListener<Message>() {
            @Override
            public void onMessage(Message message, @Nullable
            Session session) throws JMSException {
                throw theException;
            }
        });
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        this.container.setErrorHandler(errorHandler);
        this.container.afterPropertiesSet();
        this.container.start();
        // manually trigger an Exception with the above bad MessageListener...
        Message message = Mockito.mock(Message.class);
        // a Throwable from a MessageListener MUST simply be swallowed...
        messageConsumer.sendMessage(message);
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
        Mockito.verify(errorHandler).handleError(theException);
    }

    @Test
    public void testNoRollbackOccursIfSessionIsNotTransactedAndThatExceptionsDo_NOT_Propagate() throws Exception {
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        // an exception is thrown, so the rollback logic is being applied here...
        BDDMockito.given(session.getTransacted()).willReturn(false);
        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                throw new UnsupportedOperationException();
            }
        });
        this.container.afterPropertiesSet();
        this.container.start();
        // manually trigger an Exception with the above bad MessageListener...
        final Message message = Mockito.mock(Message.class);
        // a Throwable from a MessageListener MUST simply be swallowed...
        messageConsumer.sendMessage(message);
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
    }

    @Test
    public void testTransactedSessionsGetRollbackLogicAppliedAndThatExceptionsStillDo_NOT_Propagate() throws Exception {
        this.container.setSessionTransacted(true);
        final SimpleMessageListenerContainerTests.SimpleMessageConsumer messageConsumer = new SimpleMessageListenerContainerTests.SimpleMessageConsumer();
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        // an exception is thrown, so the rollback logic is being applied here...
        BDDMockito.given(session.getTransacted()).willReturn(true);
        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                throw new UnsupportedOperationException();
            }
        });
        this.container.afterPropertiesSet();
        this.container.start();
        // manually trigger an Exception with the above bad MessageListener...
        final Message message = Mockito.mock(Message.class);
        // a Throwable from a MessageListener MUST simply be swallowed...
        messageConsumer.sendMessage(message);
        // Session is rolled back 'cos it is transacted...
        Mockito.verify(session).rollback();
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
    }

    @Test
    public void testDestroyClosesConsumersSessionsAndConnectionInThatOrder() throws Exception {
        MessageConsumer messageConsumer = Mockito.mock(MessageConsumer.class);
        Session session = Mockito.mock(Session.class);
        // Queue gets created in order to create MessageConsumer for that Destination...
        BDDMockito.given(session.createQueue(SimpleMessageListenerContainerTests.DESTINATION_NAME)).willReturn(SimpleMessageListenerContainerTests.QUEUE_DESTINATION);
        // and then the MessageConsumer gets created...
        BDDMockito.given(session.createConsumer(SimpleMessageListenerContainerTests.QUEUE_DESTINATION, null)).willReturn(messageConsumer);// no MessageSelector...

        Connection connection = Mockito.mock(Connection.class);
        // session gets created in order to register MessageListener...
        BDDMockito.given(connection.createSession(this.container.isSessionTransacted(), this.container.getSessionAcknowledgeMode())).willReturn(session);
        // and the connection is start()ed after the listener is registered...
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        BDDMockito.given(connectionFactory.createConnection()).willReturn(connection);
        this.container.setConnectionFactory(connectionFactory);
        this.container.setDestinationName(SimpleMessageListenerContainerTests.DESTINATION_NAME);
        this.container.setMessageListener(new SimpleMessageListenerContainerTests.TestMessageListener());
        this.container.afterPropertiesSet();
        this.container.start();
        this.container.destroy();
        Mockito.verify(messageConsumer).close();
        Mockito.verify(session).close();
        Mockito.verify(connection).setExceptionListener(this.container);
        Mockito.verify(connection).start();
        Mockito.verify(connection).close();
    }

    private static class TestMessageListener implements MessageListener {
        public boolean executorInvoked = false;

        public boolean listenerInvoked = false;

        @Override
        public void onMessage(Message message) {
            this.listenerInvoked = true;
        }
    }

    private static class SimpleMessageConsumer implements MessageConsumer {
        private MessageListener messageListener;

        public void sendMessage(Message message) {
            this.messageListener.onMessage(message);
        }

        @Override
        public String getMessageSelector() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MessageListener getMessageListener() {
            return this.messageListener;
        }

        @Override
        public void setMessageListener(MessageListener messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public Message receive() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message receive(long l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message receiveNoWait() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }
    }
}

