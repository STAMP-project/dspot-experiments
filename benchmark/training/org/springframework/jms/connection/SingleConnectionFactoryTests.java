/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.jms.connection;


import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 26.07.2004
 */
public class SingleConnectionFactoryTests {
    @Test
    public void testWithConnection() throws JMSException {
        Connection con = Mockito.mock(Connection.class);
        SingleConnectionFactory scf = new SingleConnectionFactory(con);
        Connection con1 = scf.createConnection();
        con1.start();
        con1.stop();
        con1.close();
        Connection con2 = scf.createConnection();
        con2.start();
        con2.stop();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con, Mockito.times(2)).start();
        Mockito.verify(con, Mockito.times(2)).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithQueueConnection() throws JMSException {
        Connection con = Mockito.mock(QueueConnection.class);
        SingleConnectionFactory scf = new SingleConnectionFactory(con);
        QueueConnection con1 = scf.createQueueConnection();
        con1.start();
        con1.stop();
        con1.close();
        QueueConnection con2 = scf.createQueueConnection();
        con2.start();
        con2.stop();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con, Mockito.times(2)).start();
        Mockito.verify(con, Mockito.times(2)).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithTopicConnection() throws JMSException {
        Connection con = Mockito.mock(TopicConnection.class);
        SingleConnectionFactory scf = new SingleConnectionFactory(con);
        TopicConnection con1 = scf.createTopicConnection();
        con1.start();
        con1.stop();
        con1.close();
        TopicConnection con2 = scf.createTopicConnection();
        con2.start();
        con2.stop();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con, Mockito.times(2)).start();
        Mockito.verify(con, Mockito.times(2)).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithConnectionFactory() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        Connection con1 = scf.createConnection();
        Connection con2 = scf.createConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithQueueConnectionFactoryAndJms11Usage() throws JMSException {
        QueueConnectionFactory cf = Mockito.mock(QueueConnectionFactory.class);
        QueueConnection con = Mockito.mock(QueueConnection.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        Connection con1 = scf.createConnection();
        Connection con2 = scf.createConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithQueueConnectionFactoryAndJms102Usage() throws JMSException {
        QueueConnectionFactory cf = Mockito.mock(QueueConnectionFactory.class);
        QueueConnection con = Mockito.mock(QueueConnection.class);
        BDDMockito.given(cf.createQueueConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        Connection con1 = scf.createQueueConnection();
        Connection con2 = scf.createQueueConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithTopicConnectionFactoryAndJms11Usage() throws JMSException {
        TopicConnectionFactory cf = Mockito.mock(TopicConnectionFactory.class);
        TopicConnection con = Mockito.mock(TopicConnection.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        Connection con1 = scf.createConnection();
        Connection con2 = scf.createConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithTopicConnectionFactoryAndJms102Usage() throws JMSException {
        TopicConnectionFactory cf = Mockito.mock(TopicConnectionFactory.class);
        TopicConnection con = Mockito.mock(TopicConnection.class);
        BDDMockito.given(cf.createTopicConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        Connection con1 = scf.createTopicConnection();
        Connection con2 = scf.createTopicConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithConnectionAggregatedStartStop() throws JMSException {
        Connection con = Mockito.mock(Connection.class);
        SingleConnectionFactory scf = new SingleConnectionFactory(con);
        Connection con1 = scf.createConnection();
        con1.start();
        Mockito.verify(con).start();
        con1.stop();
        Mockito.verify(con).stop();
        Connection con2 = scf.createConnection();
        con2.start();
        Mockito.verify(con, Mockito.times(2)).start();
        con2.stop();
        Mockito.verify(con, Mockito.times(2)).stop();
        con2.start();
        Mockito.verify(con, Mockito.times(3)).start();
        con1.start();
        con2.stop();
        con1.stop();
        Mockito.verify(con, Mockito.times(3)).stop();
        con1.start();
        Mockito.verify(con, Mockito.times(4)).start();
        con1.close();
        Mockito.verify(con, Mockito.times(4)).stop();
        con2.close();
        scf.destroy();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithConnectionFactoryAndClientId() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        scf.setClientId("myId");
        Connection con1 = scf.createConnection();
        Connection con2 = scf.createConnection();
        con1.start();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).setClientID("myId");
        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testWithConnectionFactoryAndExceptionListener() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        ExceptionListener listener = new ChainedExceptionListener();
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.getExceptionListener()).willReturn(listener);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        scf.setExceptionListener(listener);
        Connection con1 = scf.createConnection();
        Assert.assertEquals(listener, con1.getExceptionListener());
        con1.start();
        con1.stop();
        con1.close();
        Connection con2 = scf.createConnection();
        con2.start();
        con2.stop();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(con).setExceptionListener(listener);
        Mockito.verify(con, Mockito.times(2)).start();
        Mockito.verify(con, Mockito.times(2)).stop();
        Mockito.verify(con).close();
    }

    @Test
    public void testWithConnectionFactoryAndReconnectOnException() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        TestConnection con = new TestConnection();
        BDDMockito.given(cf.createConnection()).willReturn(con);
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        scf.setReconnectOnException(true);
        Connection con1 = scf.createConnection();
        Assert.assertNull(con1.getExceptionListener());
        con1.start();
        con.getExceptionListener().onException(new JMSException(""));
        Connection con2 = scf.createConnection();
        con2.start();
        scf.destroy();// should trigger actual close

        Assert.assertEquals(2, con.getStartCount());
        Assert.assertEquals(2, con.getCloseCount());
    }

    @Test
    public void testWithConnectionFactoryAndExceptionListenerAndReconnectOnException() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        TestConnection con = new TestConnection();
        BDDMockito.given(cf.createConnection()).willReturn(con);
        TestExceptionListener listener = new TestExceptionListener();
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        scf.setExceptionListener(listener);
        scf.setReconnectOnException(true);
        Connection con1 = scf.createConnection();
        Assert.assertSame(listener, con1.getExceptionListener());
        con1.start();
        con.getExceptionListener().onException(new JMSException(""));
        Connection con2 = scf.createConnection();
        con2.start();
        scf.destroy();// should trigger actual close

        Assert.assertEquals(2, con.getStartCount());
        Assert.assertEquals(2, con.getCloseCount());
        Assert.assertEquals(1, listener.getCount());
    }

    @Test
    public void testWithConnectionFactoryAndLocalExceptionListenerWithCleanup() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        TestConnection con = new TestConnection();
        BDDMockito.given(cf.createConnection()).willReturn(con);
        TestExceptionListener listener0 = new TestExceptionListener();
        TestExceptionListener listener1 = new TestExceptionListener();
        TestExceptionListener listener2 = new TestExceptionListener();
        SingleConnectionFactory scf = new SingleConnectionFactory(cf) {
            @Override
            public void onException(JMSException ex) {
                // no-op
            }
        };
        scf.setReconnectOnException(true);
        scf.setExceptionListener(listener0);
        Connection con1 = scf.createConnection();
        con1.setExceptionListener(listener1);
        Assert.assertSame(listener1, con1.getExceptionListener());
        Connection con2 = scf.createConnection();
        con2.setExceptionListener(listener2);
        Assert.assertSame(listener2, con2.getExceptionListener());
        con.getExceptionListener().onException(new JMSException(""));
        con2.close();
        con.getExceptionListener().onException(new JMSException(""));
        con1.close();
        con.getExceptionListener().onException(new JMSException(""));
        scf.destroy();// should trigger actual close

        Assert.assertEquals(0, con.getStartCount());
        Assert.assertEquals(1, con.getCloseCount());
        Assert.assertEquals(3, listener0.getCount());
        Assert.assertEquals(2, listener1.getCount());
        Assert.assertEquals(1, listener2.getCount());
    }

    @Test
    public void testWithConnectionFactoryAndLocalExceptionListenerWithReconnect() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        TestConnection con = new TestConnection();
        BDDMockito.given(cf.createConnection()).willReturn(con);
        TestExceptionListener listener0 = new TestExceptionListener();
        TestExceptionListener listener1 = new TestExceptionListener();
        TestExceptionListener listener2 = new TestExceptionListener();
        SingleConnectionFactory scf = new SingleConnectionFactory(cf);
        scf.setReconnectOnException(true);
        scf.setExceptionListener(listener0);
        Connection con1 = scf.createConnection();
        con1.setExceptionListener(listener1);
        Assert.assertSame(listener1, con1.getExceptionListener());
        con1.start();
        Connection con2 = scf.createConnection();
        con2.setExceptionListener(listener2);
        Assert.assertSame(listener2, con2.getExceptionListener());
        con.getExceptionListener().onException(new JMSException(""));
        con2.close();
        con1.getMetaData();
        con.getExceptionListener().onException(new JMSException(""));
        con1.close();
        scf.destroy();// should trigger actual close

        Assert.assertEquals(2, con.getStartCount());
        Assert.assertEquals(2, con.getCloseCount());
        Assert.assertEquals(2, listener0.getCount());
        Assert.assertEquals(2, listener1.getCount());
        Assert.assertEquals(1, listener2.getCount());
    }

    @Test
    public void testCachingConnectionFactory() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        Session txSession = Mockito.mock(Session.class);
        Session nonTxSession = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(txSession);
        BDDMockito.given(txSession.getTransacted()).willReturn(true);
        BDDMockito.given(con.createSession(false, CLIENT_ACKNOWLEDGE)).willReturn(nonTxSession);
        CachingConnectionFactory scf = new CachingConnectionFactory(cf);
        scf.setReconnectOnException(false);
        Connection con1 = scf.createConnection();
        Session session1 = con1.createSession(true, AUTO_ACKNOWLEDGE);
        session1.getTransacted();
        session1.close();// should lead to rollback

        session1 = con1.createSession(false, CLIENT_ACKNOWLEDGE);
        session1.close();
        con1.start();
        Connection con2 = scf.createConnection();
        Session session2 = con2.createSession(false, CLIENT_ACKNOWLEDGE);
        session2.close();
        session2 = con2.createSession(true, AUTO_ACKNOWLEDGE);
        session2.commit();
        session2.close();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(txSession).commit();
        Mockito.verify(txSession).close();
        Mockito.verify(nonTxSession).close();
        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
    }

    @Test
    public void testCachingConnectionFactoryWithQueueConnectionFactoryAndJms102Usage() throws JMSException {
        QueueConnectionFactory cf = Mockito.mock(QueueConnectionFactory.class);
        QueueConnection con = Mockito.mock(QueueConnection.class);
        QueueSession txSession = Mockito.mock(QueueSession.class);
        QueueSession nonTxSession = Mockito.mock(QueueSession.class);
        BDDMockito.given(cf.createQueueConnection()).willReturn(con);
        BDDMockito.given(con.createQueueSession(true, AUTO_ACKNOWLEDGE)).willReturn(txSession);
        BDDMockito.given(txSession.getTransacted()).willReturn(true);
        BDDMockito.given(con.createQueueSession(false, CLIENT_ACKNOWLEDGE)).willReturn(nonTxSession);
        CachingConnectionFactory scf = new CachingConnectionFactory(cf);
        scf.setReconnectOnException(false);
        Connection con1 = scf.createQueueConnection();
        Session session1 = con1.createSession(true, AUTO_ACKNOWLEDGE);
        session1.rollback();
        session1.close();
        session1 = con1.createSession(false, CLIENT_ACKNOWLEDGE);
        session1.close();
        con1.start();
        QueueConnection con2 = scf.createQueueConnection();
        Session session2 = con2.createQueueSession(false, CLIENT_ACKNOWLEDGE);
        session2.close();
        session2 = con2.createSession(true, AUTO_ACKNOWLEDGE);
        session2.getTransacted();
        session2.close();// should lead to rollback

        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(txSession).rollback();
        Mockito.verify(txSession).close();
        Mockito.verify(nonTxSession).close();
        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
    }

    @Test
    public void testCachingConnectionFactoryWithTopicConnectionFactoryAndJms102Usage() throws JMSException {
        TopicConnectionFactory cf = Mockito.mock(TopicConnectionFactory.class);
        TopicConnection con = Mockito.mock(TopicConnection.class);
        TopicSession txSession = Mockito.mock(TopicSession.class);
        TopicSession nonTxSession = Mockito.mock(TopicSession.class);
        BDDMockito.given(cf.createTopicConnection()).willReturn(con);
        BDDMockito.given(con.createTopicSession(true, AUTO_ACKNOWLEDGE)).willReturn(txSession);
        BDDMockito.given(txSession.getTransacted()).willReturn(true);
        BDDMockito.given(con.createTopicSession(false, CLIENT_ACKNOWLEDGE)).willReturn(nonTxSession);
        CachingConnectionFactory scf = new CachingConnectionFactory(cf);
        scf.setReconnectOnException(false);
        Connection con1 = scf.createTopicConnection();
        Session session1 = con1.createSession(true, AUTO_ACKNOWLEDGE);
        session1.getTransacted();
        session1.close();// should lead to rollback

        session1 = con1.createSession(false, CLIENT_ACKNOWLEDGE);
        session1.close();
        con1.start();
        TopicConnection con2 = scf.createTopicConnection();
        Session session2 = con2.createTopicSession(false, CLIENT_ACKNOWLEDGE);
        session2.close();
        session2 = con2.createSession(true, AUTO_ACKNOWLEDGE);
        session2.getTransacted();
        session2.close();
        con2.start();
        con1.close();
        con2.close();
        scf.destroy();// should trigger actual close

        Mockito.verify(txSession).close();
        Mockito.verify(nonTxSession).close();
        Mockito.verify(con).start();
        Mockito.verify(con).stop();
        Mockito.verify(con).close();
    }
}

