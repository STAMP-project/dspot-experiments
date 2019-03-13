/**
 * Copyright 2002-2019 the original author or authors.
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
import TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jms.StubQueue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 26.07.2004
 */
public class JmsTransactionManagerTests {
    @Test
    public void testTransactionCommit() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        tm.commit(ts);
        Mockito.verify(session).commit();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionRollback() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        tm.rollback(ts);
        Mockito.verify(session).rollback();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testParticipatingTransactionWithCommit() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        final JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jt.execute(((SessionCallback<Void>) (( sess) -> {
                    assertSame(sess, session);
                    return null;
                })));
            }
        });
        tm.commit(ts);
        Mockito.verify(session).commit();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testParticipatingTransactionWithRollbackOnly() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        final JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jt.execute(((SessionCallback<Void>) (( sess) -> {
                    assertSame(sess, session);
                    return null;
                })));
                status.setRollbackOnly();
            }
        });
        try {
            tm.commit(ts);
            Assert.fail("Should have thrown UnexpectedRollbackException");
        } catch (UnexpectedRollbackException ex) {
            // expected
        }
        Mockito.verify(session).rollback();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testSuspendedTransaction() throws JMSException {
        final ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final Session session2 = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        BDDMockito.given(con.createSession(false, AUTO_ACKNOWLEDGE)).willReturn(session2);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        final JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NOT_SUPPORTED);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jt.execute(((SessionCallback<Void>) (( sess) -> {
                    assertNotSame(sess, session);
                    return null;
                })));
            }
        });
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        tm.commit(ts);
        Mockito.verify(session).commit();
        Mockito.verify(session).close();
        Mockito.verify(session2).close();
        Mockito.verify(con, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionSuspension() throws JMSException {
        final ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final Session session2 = Mockito.mock(Session.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session, session2);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        final JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jt.execute(((SessionCallback<Void>) (( sess) -> {
                    assertNotSame(sess, session);
                    return null;
                })));
            }
        });
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        tm.commit(ts);
        Mockito.verify(session).commit();
        Mockito.verify(session2).commit();
        Mockito.verify(session).close();
        Mockito.verify(session2).close();
        Mockito.verify(con, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionCommitWithMessageProducer() throws JMSException {
        Destination dest = new StubQueue();
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        Session session = Mockito.mock(Session.class);
        MessageProducer producer = Mockito.mock(MessageProducer.class);
        final Message message = Mockito.mock(Message.class);
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        BDDMockito.given(session.createProducer(dest)).willReturn(producer);
        BDDMockito.given(session.getTransacted()).willReturn(true);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        JmsTemplate jt = new JmsTemplate(cf);
        jt.send(dest, ( sess) -> message);
        tm.commit(ts);
        Mockito.verify(producer).send(message);
        Mockito.verify(session).commit();
        Mockito.verify(producer).close();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testLazyTransactionalSession() throws JMSException {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        Connection con = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        tm.setLazyResourceRetrieval(true);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        BDDMockito.given(cf.createConnection()).willReturn(con);
        BDDMockito.given(con.createSession(true, AUTO_ACKNOWLEDGE)).willReturn(session);
        JmsTemplate jt = new JmsTemplate(cf);
        jt.execute(((SessionCallback<Void>) (( sess) -> {
            assertSame(sess, session);
            return null;
        })));
        tm.commit(ts);
        Mockito.verify(session).commit();
        Mockito.verify(session).close();
        Mockito.verify(con).close();
    }

    @Test
    public void testLazyWithoutSessionAccess() {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        JmsTransactionManager tm = new JmsTransactionManager(cf);
        tm.setLazyResourceRetrieval(true);
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        tm.commit(ts);
    }
}

