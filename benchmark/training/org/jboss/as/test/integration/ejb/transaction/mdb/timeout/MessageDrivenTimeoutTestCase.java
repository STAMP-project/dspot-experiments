/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.transaction.mdb.timeout;


import javax.inject.Inject;
import javax.jms.Queue;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.integration.transactions.TransactionCheckerSingleton;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test of timeout of global transaction where MDB receiving message
 * and mock {@link XAResource} is added to the mix to get 2PC processing.
 */
@RunWith(Arquillian.class)
@ServerSetup(TransactionTimeoutQueueSetupTask.class)
public class MessageDrivenTimeoutTestCase {
    @ArquillianResource
    private InitialContext initCtx;

    @Inject
    private TransactionCheckerSingleton checker;

    /**
     * MDB receives a message where <code>onMessage</code> method using an {@link XAResource} and adding transaction
     * synchronization. The processing should be finished with sucessful 2PC commit.
     */
    @Test
    public void noTimeout() throws Exception {
        String text = "no timeout";
        Queue q = MessageDrivenTimeoutTestCase.sendMessage(text, TransactionTimeoutQueueSetupTask.NO_TIMEOUT_JNDI_NAME, initCtx);
        Assert.assertEquals("Sent and received message does not match at expected way", ((NoTimeoutMDB.REPLY_PREFIX) + text), MessageDrivenTimeoutTestCase.receiveMessage(q, initCtx, true));
        Assert.assertEquals("Synchronization before completion has to be called", 1, checker.countSynchronizedBefore());
        Assert.assertEquals("Synchronization after completion has to be called", 1, checker.countSynchronizedAfter());
        Assert.assertEquals("Expecting one test XA resources being commmitted", 1, checker.getCommitted());
        Assert.assertEquals("Expecting no rollback happened", 0, checker.getRolledback());
    }

    /**
     * MDB receives a message when annotated to have transaction timeout defined.
     * MDB using {@link XAResource} but processing takes longer than and timeout time is exceeded.
     * As transaction annotation does not affects MDB processing the commit should happen.
     */
    @Test
    public void transactionTimeoutAnnotation() throws Exception {
        String text = "annotation timeout";
        Queue q = MessageDrivenTimeoutTestCase.sendMessage(text, TransactionTimeoutQueueSetupTask.ANNOTATION_TIMEOUT_JNDI_NAME, initCtx);
        Assert.assertEquals("Sent and received message does not match at expected way", ((AnnotationTimeoutMDB.REPLY_PREFIX) + text), MessageDrivenTimeoutTestCase.receiveMessage(q, initCtx, true));
        Assert.assertEquals("Expecting one test XA resources being commmitted", 1, checker.getCommitted());
        Assert.assertEquals("Expecting no rollback happened", 0, checker.getRolledback());
    }

    /**
     * MDB receives a message MDB activation property is used to have transaction timeout defined.
     * MDB using {@link XAResource} but processing takes longer than and timeout time is exceeded.
     * As activation property instruct the RA to set transaction timeout then the transaction
     * should be rolled-back.
     */
    @Test
    public void transactionTimeoutActivationProperty() throws Exception {
        String text = "activation property timeout";
        Queue q = MessageDrivenTimeoutTestCase.sendMessage(text, TransactionTimeoutQueueSetupTask.PROPERTY_TIMEOUT_JNDI_NAME, initCtx);
        Assert.assertNull("No message should be received as mdb timeouted", MessageDrivenTimeoutTestCase.receiveMessage(q, initCtx, false));
        Assert.assertEquals("Expecting no commmit happened", 0, checker.getCommitted());
    }
}

