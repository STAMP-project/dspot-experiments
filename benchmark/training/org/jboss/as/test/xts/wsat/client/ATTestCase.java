/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.xts.wsat.client;


import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.wst.TransactionRolledBackException;
import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.xts.base.BaseFunctionalTest;
import org.jboss.as.test.xts.base.TestApplicationException;
import org.jboss.as.test.xts.util.EventLog;
import org.jboss.as.test.xts.util.EventLogEvent;
import org.jboss.as.test.xts.util.ServiceCommand;
import org.jboss.as.test.xts.wsat.service.AT;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * XTS atomic transaction test case
 */
@RunWith(Arquillian.class)
public class ATTestCase extends BaseFunctionalTest {
    private UserTransaction ut;

    private AT client1;

    private AT client2;

    private AT client3;

    public static final String ARCHIVE_NAME = "wsat-test";

    @Inject
    EventLog eventLog;

    @Test
    public void testWSATSingleSimple() throws Exception {
        ut.begin();
        client1.invoke();
        ut.commit();
        assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
    }

    @Test
    public void testWSATSimple() throws Exception {
        ut.begin();
        client1.invoke();
        client2.invoke();
        client3.invoke();
        ut.commit();
        assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
        assertEventLogClient2(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
        assertEventLogClient3(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
    }

    @Test
    public void testWSATClientRollback() throws Exception {
        ut.begin();
        client1.invoke();
        client2.invoke();
        client3.invoke();
        ut.rollback();
        assertEventLogClient1(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
        assertEventLogClient2(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
        assertEventLogClient3(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
    }

    @Test(expected = TransactionRolledBackException.class)
    public void testWSATVoteRollback() throws Exception {
        try {
            ut.begin();
            client1.invoke();
            client2.invoke(ServiceCommand.VOTE_ROLLBACK);// rollback voted on durable participant

            client3.invoke();
            ut.commit();
        } catch (TransactionRolledBackException e) {
            assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            assertEventLogClient2(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.VOLATILE_ROLLBACK);
            assertEventLogClient3(EventLogEvent.BEFORE_PREPARE, EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            throw e;
        }
    }

    @Test(expected = TransactionRolledBackException.class)
    public void testWSATVoteRollbackPrePrepare() throws Exception {
        try {
            ut.begin();
            client1.invoke();
            client2.invoke(ServiceCommand.VOTE_ROLLBACK_PRE_PREPARE);// rollback voted on volatile participant

            client3.invoke();
            ut.commit();
        } catch (TransactionRolledBackException e) {
            assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            assertEventLogClient2(EventLogEvent.BEFORE_PREPARE, EventLogEvent.ROLLBACK);
            assertEventLogClient3(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            throw e;
        }
    }

    @Test
    public void testWSATRollbackOnly() throws Exception {
        try {
            ut.begin();
            client1.invoke();
            client2.invoke(ServiceCommand.ROLLBACK_ONLY);
            client3.invoke();// failing on enlisting next participant

            // ut.commit();
            Assert.fail((("The " + (SOAPFaultException.class.getName())) + " is expected for RollbackOnly test"));
        } catch (SOAPFaultException sfe) {
            assertEventLogClient1(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            assertEventLogClient2(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
            assertEventLogClient3();
        }
    }

    @Test
    public void testWSATVoteReadOnly() throws Exception {
        ut.begin();
        client1.invoke(ServiceCommand.VOTE_READONLY_VOLATILE);// volatile for VOLATILE_COMMIT

        client2.invoke(ServiceCommand.VOTE_READONLY_DURABLE);// durable for COMMIT

        client3.invoke(ServiceCommand.VOTE_READONLY_DURABLE, ServiceCommand.VOTE_READONLY_VOLATILE);
        ut.commit();
        assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT);
        assertEventLogClient2(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.VOLATILE_COMMIT);
        assertEventLogClient3(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE);
    }

    @Test
    public void testWSATApplicationException() throws Exception {
        try {
            ut.begin();
            client1.invoke();
            client2.invoke(ServiceCommand.APPLICATION_EXCEPTION);
            Assert.fail("Exception should have been thrown by now");
        } catch (TestApplicationException e) {
            // Exception expected
        } finally {
            client3.invoke();
            ut.rollback();
        }
        assertEventLogClient1(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
        assertEventLogClient2(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
        assertEventLogClient3(EventLogEvent.ROLLBACK, EventLogEvent.VOLATILE_ROLLBACK);
    }

    @Test
    public void testWSATApplicationExceptionCommit() throws Exception {
        try {
            ut.begin();
            client1.invoke();
            client2.invoke(ServiceCommand.APPLICATION_EXCEPTION);
            Assert.fail("Exception should have been thrown by now");
        } catch (TestApplicationException e) {
            // Exception expected
        } finally {
            client3.invoke();
            ut.commit();
        }
        assertEventLogClient1(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
        assertEventLogClient2(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
        assertEventLogClient3(EventLogEvent.BEFORE_PREPARE, EventLogEvent.PREPARE, EventLogEvent.COMMIT, EventLogEvent.VOLATILE_COMMIT);
    }
}

