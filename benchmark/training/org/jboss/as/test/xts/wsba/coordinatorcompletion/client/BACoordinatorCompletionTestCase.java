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
package org.jboss.as.test.xts.wsba.coordinatorcompletion.client;


import com.arjuna.mw.wst11.UserBusinessActivity;
import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.xts.base.BaseFunctionalTest;
import org.jboss.as.test.xts.base.TestApplicationException;
import org.jboss.as.test.xts.util.EventLog;
import org.jboss.as.test.xts.util.EventLogEvent;
import org.jboss.as.test.xts.util.ServiceCommand;
import org.jboss.as.test.xts.wsba.coordinatorcompletion.service.BACoordinatorCompletion;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * XTS business activities - coordinator completition test case
 */
@RunWith(Arquillian.class)
public class BACoordinatorCompletionTestCase extends BaseFunctionalTest {
    private UserBusinessActivity uba;

    private BACoordinatorCompletion client1;

    private BACoordinatorCompletion client2;

    private BACoordinatorCompletion client3;

    @Inject
    private EventLog eventLog;

    public static final String ARCHIVE_NAME = "wsba-coordinatorcompletition-test";

    @Test
    public void testWSBACoordinatorSingle() throws Exception {
        uba.begin();
        client1.saveData();
        uba.close();
        assertEventLogClient1(EventLogEvent.COMPLETE, EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
    }

    @Test
    public void testWSBACoordinatorSimple() throws Exception {
        uba.begin();
        client1.saveData();
        client2.saveData();
        client3.saveData();
        uba.close();
        assertEventLogClient1(EventLogEvent.COMPLETE, EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
        assertEventLogClient2(EventLogEvent.COMPLETE, EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
        assertEventLogClient3(EventLogEvent.COMPLETE, EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
    }

    @Test
    public void testWSBACoordinatorCannotComplete() throws Exception {
        try {
            uba.begin();
            client1.saveData();
            client2.saveData(ServiceCommand.CANNOT_COMPLETE);
            client3.saveData();
            Assert.fail("Exception should have been thrown by now");
        } catch (SOAPFaultException sfe) {
            // This is OK - exception expected
            // P2 set transaction status to ABORT_ONLY
            // P3 enlist is failed with WrongStateException and can't be done
            // It needs call uba.cancel() to rollback the transaction
            uba.cancel();
        } finally {
            assertEventLogClient1(EventLogEvent.CANCEL);
            assertEventLogClient2();
            assertEventLogClient3();
        }
    }

    @Test
    public void testWSBACoordinatorClientCancel() throws Exception {
        uba.begin();
        client1.saveData();
        client2.saveData();
        client3.saveData();
        uba.cancel();
        assertEventLogClient1(EventLogEvent.CANCEL);
        assertEventLogClient2(EventLogEvent.CANCEL);
        assertEventLogClient3(EventLogEvent.CANCEL);
    }

    @Test
    public void testWSBACoordinatorApplicationException() throws Exception {
        try {
            uba.begin();
            client1.saveData();
            client2.saveData(ServiceCommand.APPLICATION_EXCEPTION);
            Assert.fail("Exception should have been thrown by now");
        } catch (TestApplicationException e) {
            // This is OK - exception expected
        } finally {
            client3.saveData();
            // TM can't know this application exception, so it needs cancel.
            uba.cancel();
        }
        assertEventLogClient1(EventLogEvent.CANCEL);
        assertEventLogClient2(EventLogEvent.CANCEL);
        assertEventLogClient3(EventLogEvent.CANCEL);
    }

    @Test
    public void testWSBACoordinatorCompletionFailToComplete() throws Exception {
        try {
            uba.begin();
            client1.saveData();
            client2.saveData(ServiceCommand.SYSTEM_EXCEPTION_ON_COMPLETE);
            client3.saveData();
            uba.close();
            Assert.fail("Exception should have been thrown by now");
        } catch (com.arjuna trbe) {
            // This is OK - exception expected
        } finally {
            assertEventLogClient1(EventLogEvent.COMPLETE, EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
            assertEventLogClient2(EventLogEvent.COMPLETE, EventLogEvent.CANCEL);
            assertEventLogClient3(EventLogEvent.CANCEL);
        }
    }
}

