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
package org.springframework.jms.core;


import JmsTemplate.RECEIVE_TIMEOUT_INDEFINITE_WAIT;
import JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT;
import Session.AUTO_ACKNOWLEDGE;
import TransactionSynchronization.STATUS_UNKNOWN;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.InvalidDestinationException;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageProducer;
import javax.jms.ResourceAllocationException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TransactionInProgressException;
import javax.jms.TransactionRolledBackException;
import javax.naming.Context;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jms.JmsException;
import org.springframework.jms.JmsSecurityException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Unit tests for the JmsTemplate implemented using JMS 1.1.
 *
 * @author Andre Biryukov
 * @author Mark Pollack
 * @author Stephane Nicoll
 */
public class JmsTemplateTests {
    private Context jndiContext;

    private ConnectionFactory connectionFactory;

    protected Connection connection;

    private Session session;

    private Destination queue;

    private QosSettings qosSettings = new QosSettings(DeliveryMode.PERSISTENT, 9, 10000);

    @Test
    public void testExceptionStackTrace() {
        JMSException jmsEx = new JMSException("could not connect");
        Exception innerEx = new Exception("host not found");
        jmsEx.setLinkedException(innerEx);
        JmsException springJmsEx = JmsUtils.convertJmsAccessException(jmsEx);
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        springJmsEx.printStackTrace(out);
        String trace = sw.toString();
        Assert.assertTrue("inner jms exception not found", ((trace.indexOf("host not found")) > 0));
    }

    @Test
    public void testProducerCallback() throws Exception {
        JmsTemplate template = createTemplate();
        template.setConnectionFactory(this.connectionFactory);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        BDDMockito.given(this.session.createProducer(null)).willReturn(messageProducer);
        BDDMockito.given(messageProducer.getPriority()).willReturn(4);
        template.execute(((ProducerCallback<Void>) (( session1, producer) -> {
            session1.getTransacted();
            producer.getPriority();
            return null;
        })));
        Mockito.verify(messageProducer).close();
        Mockito.verify(this.session).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testProducerCallbackWithIdAndTimestampDisabled() throws Exception {
        JmsTemplate template = createTemplate();
        template.setConnectionFactory(this.connectionFactory);
        template.setMessageIdEnabled(false);
        template.setMessageTimestampEnabled(false);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        BDDMockito.given(this.session.createProducer(null)).willReturn(messageProducer);
        BDDMockito.given(messageProducer.getPriority()).willReturn(4);
        template.execute(((ProducerCallback<Void>) (( session1, producer) -> {
            session1.getTransacted();
            producer.getPriority();
            return null;
        })));
        Mockito.verify(messageProducer).setDisableMessageID(true);
        Mockito.verify(messageProducer).setDisableMessageTimestamp(true);
        Mockito.verify(messageProducer).close();
        Mockito.verify(this.session).close();
        Mockito.verify(this.connection).close();
    }

    /**
     * Test the method execute(SessionCallback action).
     */
    @Test
    public void testSessionCallback() throws Exception {
        JmsTemplate template = createTemplate();
        template.setConnectionFactory(this.connectionFactory);
        template.execute(new SessionCallback<Void>() {
            @Override
            public Void doInJms(Session session) throws JMSException {
                session.getTransacted();
                return null;
            }
        });
        Mockito.verify(this.session).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testSessionCallbackWithinSynchronizedTransaction() throws Exception {
        SingleConnectionFactory scf = new SingleConnectionFactory(this.connectionFactory);
        JmsTemplate template = createTemplate();
        template.setConnectionFactory(scf);
        TransactionSynchronizationManager.initSynchronization();
        try {
            template.execute(new SessionCallback<Void>() {
                @Override
                public Void doInJms(Session session) throws JMSException {
                    session.getTransacted();
                    return null;
                }
            });
            template.execute(new SessionCallback<Void>() {
                @Override
                public Void doInJms(Session session) throws JMSException {
                    session.getTransacted();
                    return null;
                }
            });
            Assert.assertSame(this.session, ConnectionFactoryUtils.getTransactionalSession(scf, null, false));
            Assert.assertSame(this.session, ConnectionFactoryUtils.getTransactionalSession(scf, scf.createConnection(), false));
            TransactionAwareConnectionFactoryProxy tacf = new TransactionAwareConnectionFactoryProxy(scf);
            Connection tac = tacf.createConnection();
            Session tas = tac.createSession(false, AUTO_ACKNOWLEDGE);
            tas.getTransacted();
            tas.close();
            tac.close();
            List<TransactionSynchronization> synchs = TransactionSynchronizationManager.getSynchronizations();
            Assert.assertEquals(1, synchs.size());
            TransactionSynchronization synch = synchs.get(0);
            synch.beforeCommit(false);
            synch.beforeCompletion();
            synch.afterCommit();
            synch.afterCompletion(STATUS_UNKNOWN);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            scf.destroy();
        }
        Assert.assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty());
        Mockito.verify(this.connection).start();
        if (useTransactedTemplate()) {
            Mockito.verify(this.session).commit();
        }
        Mockito.verify(this.session).close();
        Mockito.verify(this.connection).stop();
        Mockito.verify(this.connection).close();
    }

    /**
     * Test sending to a destination using the method
     * send(Destination d, MessageCreator messageCreator)
     */
    @Test
    public void testSendDestination() throws Exception {
        doTestSendDestination(true, false, true, false);
    }

    /**
     * Test sending to a destination using the method
     * send(String d, MessageCreator messageCreator)
     */
    @Test
    public void testSendDestinationName() throws Exception {
        doTestSendDestination(false, false, true, false);
    }

    /**
     * Test sending to a destination using the method
     * send(Destination d, MessageCreator messageCreator) using QOS parameters.
     */
    @Test
    public void testSendDestinationWithQOS() throws Exception {
        doTestSendDestination(true, false, false, true);
    }

    /**
     * Test sending to a destination using the method
     * send(String d, MessageCreator messageCreator) using QOS parameters.
     */
    @Test
    public void testSendDestinationNameWithQOS() throws Exception {
        doTestSendDestination(false, false, false, true);
    }

    /**
     * Test sending to the default destination.
     */
    @Test
    public void testSendDefaultDestination() throws Exception {
        doTestSendDestination(true, true, true, true);
    }

    /**
     * Test sending to the default destination name.
     */
    @Test
    public void testSendDefaultDestinationName() throws Exception {
        doTestSendDestination(false, true, true, true);
    }

    /**
     * Test sending to the default destination using explicit QOS parameters.
     */
    @Test
    public void testSendDefaultDestinationWithQOS() throws Exception {
        doTestSendDestination(true, true, false, false);
    }

    /**
     * Test sending to the default destination name using explicit QOS parameters.
     */
    @Test
    public void testSendDefaultDestinationNameWithQOS() throws Exception {
        doTestSendDestination(false, true, false, false);
    }

    @Test
    public void testConverter() throws Exception {
        JmsTemplate template = createTemplate();
        template.setConnectionFactory(this.connectionFactory);
        template.setMessageConverter(new SimpleMessageConverter());
        String s = "Hello world";
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage textMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(this.session.createProducer(this.queue)).willReturn(messageProducer);
        BDDMockito.given(this.session.createTextMessage("Hello world")).willReturn(textMessage);
        template.convertAndSend(this.queue, s);
        Mockito.verify(messageProducer).send(textMessage);
        Mockito.verify(messageProducer).close();
        if (useTransactedTemplate()) {
            Mockito.verify(this.session).commit();
        }
        Mockito.verify(this.session).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testReceiveDefaultDestination() throws Exception {
        doTestReceive(true, true, false, false, false, false, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveDefaultDestinationName() throws Exception {
        doTestReceive(false, true, false, false, false, false, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveDestination() throws Exception {
        doTestReceive(true, false, false, false, false, true, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveDestinationWithClientAcknowledge() throws Exception {
        doTestReceive(true, false, false, true, false, false, 1000);
    }

    @Test
    public void testReceiveDestinationName() throws Exception {
        doTestReceive(false, false, false, false, false, true, 1000);
    }

    @Test
    public void testReceiveDefaultDestinationWithSelector() throws Exception {
        doTestReceive(true, true, false, false, true, true, 1000);
    }

    @Test
    public void testReceiveDefaultDestinationNameWithSelector() throws Exception {
        doTestReceive(false, true, false, false, true, true, RECEIVE_TIMEOUT_NO_WAIT);
    }

    @Test
    public void testReceiveDestinationWithSelector() throws Exception {
        doTestReceive(true, false, false, false, true, false, 1000);
    }

    @Test
    public void testReceiveDestinationWithClientAcknowledgeWithSelector() throws Exception {
        doTestReceive(true, false, false, true, true, true, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveDestinationNameWithSelector() throws Exception {
        doTestReceive(false, false, false, false, true, false, RECEIVE_TIMEOUT_NO_WAIT);
    }

    @Test
    public void testReceiveAndConvertDefaultDestination() throws Exception {
        doTestReceive(true, true, true, false, false, false, 1000);
    }

    @Test
    public void testReceiveAndConvertDefaultDestinationName() throws Exception {
        doTestReceive(false, true, true, false, false, false, 1000);
    }

    @Test
    public void testReceiveAndConvertDestinationName() throws Exception {
        doTestReceive(false, false, true, false, false, true, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveAndConvertDestination() throws Exception {
        doTestReceive(true, false, true, false, false, true, 1000);
    }

    @Test
    public void testReceiveAndConvertDefaultDestinationWithSelector() throws Exception {
        doTestReceive(true, true, true, false, true, true, RECEIVE_TIMEOUT_NO_WAIT);
    }

    @Test
    public void testReceiveAndConvertDestinationNameWithSelector() throws Exception {
        doTestReceive(false, false, true, false, true, true, RECEIVE_TIMEOUT_INDEFINITE_WAIT);
    }

    @Test
    public void testReceiveAndConvertDestinationWithSelector() throws Exception {
        doTestReceive(true, false, true, false, true, false, 1000);
    }

    @Test
    public void testSendAndReceiveDefaultDestination() throws Exception {
        doTestSendAndReceive(true, true, 1000L);
    }

    @Test
    public void testSendAndReceiveDefaultDestinationName() throws Exception {
        doTestSendAndReceive(false, true, 1000L);
    }

    @Test
    public void testSendAndReceiveDestination() throws Exception {
        doTestSendAndReceive(true, false, 1000L);
    }

    @Test
    public void testSendAndReceiveDestinationName() throws Exception {
        doTestSendAndReceive(false, false, 1000L);
    }

    @Test
    public void testIllegalStateException() throws Exception {
        doTestJmsException(new IllegalStateException(""), org.springframework.jms.IllegalStateException.class);
    }

    @Test
    public void testInvalidClientIDException() throws Exception {
        doTestJmsException(new InvalidClientIDException(""), org.springframework.jms.InvalidClientIDException.class);
    }

    @Test
    public void testInvalidDestinationException() throws Exception {
        doTestJmsException(new InvalidDestinationException(""), org.springframework.jms.InvalidDestinationException.class);
    }

    @Test
    public void testInvalidSelectorException() throws Exception {
        doTestJmsException(new InvalidSelectorException(""), org.springframework.jms.InvalidSelectorException.class);
    }

    @Test
    public void testJmsSecurityException() throws Exception {
        doTestJmsException(new JMSSecurityException(""), JmsSecurityException.class);
    }

    @Test
    public void testMessageEOFException() throws Exception {
        doTestJmsException(new MessageEOFException(""), org.springframework.jms.MessageEOFException.class);
    }

    @Test
    public void testMessageFormatException() throws Exception {
        doTestJmsException(new MessageFormatException(""), org.springframework.jms.MessageFormatException.class);
    }

    @Test
    public void testMessageNotReadableException() throws Exception {
        doTestJmsException(new MessageNotReadableException(""), org.springframework.jms.MessageNotReadableException.class);
    }

    @Test
    public void testMessageNotWriteableException() throws Exception {
        doTestJmsException(new MessageNotWriteableException(""), org.springframework.jms.MessageNotWriteableException.class);
    }

    @Test
    public void testResourceAllocationException() throws Exception {
        doTestJmsException(new ResourceAllocationException(""), org.springframework.jms.ResourceAllocationException.class);
    }

    @Test
    public void testTransactionInProgressException() throws Exception {
        doTestJmsException(new TransactionInProgressException(""), org.springframework.jms.TransactionInProgressException.class);
    }

    @Test
    public void testTransactionRolledBackException() throws Exception {
        doTestJmsException(new TransactionRolledBackException(""), org.springframework.jms.TransactionRolledBackException.class);
    }

    @Test
    public void testUncategorizedJmsException() throws Exception {
        doTestJmsException(new JMSException(""), UncategorizedJmsException.class);
    }
}

