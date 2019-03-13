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
package org.jboss.as.test.xts.wsba.participantcompletion.client;


import com.arjuna.mw.wst11.UserBusinessActivity;
import com.arjuna.wst.TransactionRolledBackException;
import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.xts.base.BaseFunctionalTest;
import org.jboss.as.test.xts.base.TestApplicationException;
import org.jboss.as.test.xts.util.EventLog;
import org.jboss.as.test.xts.util.EventLogEvent;
import org.jboss.as.test.xts.util.ServiceCommand;
import org.jboss.as.test.xts.wsba.participantcompletion.service.BAParticipantCompletion;
import org.jboss.jbossts.xts.bytemanSupport.participantCompletion.ParticipantCompletionCoordinatorRules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * XTS business activities - participant completition test case
 */
@RunWith(Arquillian.class)
public class BAParticipantCompletionTestCase extends BaseFunctionalTest {
    UserBusinessActivity uba;

    BAParticipantCompletion client1;

    BAParticipantCompletion client2;

    BAParticipantCompletion client3;

    @Inject
    private EventLog eventLog;

    public static final String ARCHIVE_NAME = "wsba-participantcompletition-test";

    @Test
    public void testWSBAParticipantCompleteSingle() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(1);
        uba.begin();
        client1.saveData(ServiceCommand.DO_COMPLETE);
        uba.close();
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
    }

    @Test
    public void testWSBAParticipantCompleteSimple() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        uba.begin();
        client1.saveData(ServiceCommand.DO_COMPLETE);
        client2.saveData(ServiceCommand.DO_COMPLETE);
        client3.saveData(ServiceCommand.DO_COMPLETE);
        uba.close();
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
        assertEventLogClient2(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
        assertEventLogClient3(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.CLOSE);
    }

    @Test
    public void testWSBAParticipantDoNotComplete() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        try {
            uba.begin();
            client1.saveData(ServiceCommand.DO_COMPLETE);
            client2.saveData(ServiceCommand.DO_COMPLETE);
            client3.saveData();// this participant does not inform about correct completition

            uba.close();
            Assert.fail("Exception should have been thrown by now");
        } catch (TransactionRolledBackException e) {
            // we expect this :)
        }
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient2(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient3(EventLogEvent.CANCEL);
    }

    @Test
    public void testWSBAParticipantClientCancelNotComplete() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        uba.begin();
        client1.saveData(ServiceCommand.DO_COMPLETE);
        client2.saveData();
        client3.saveData(ServiceCommand.DO_COMPLETE);
        uba.cancel();
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient2(EventLogEvent.CANCEL);
        assertEventLogClient3(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
    }

    @Test
    public void testWSBAParticipantCannotComplete() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        try {
            uba.begin();
            client1.saveData(ServiceCommand.DO_COMPLETE);
            client2.saveData(ServiceCommand.CANNOT_COMPLETE);
            client3.saveData(ServiceCommand.DO_COMPLETE);
            Assert.fail("Exception should have been thrown by now");
        } catch (SOAPFaultException sfe) {
            // Exception is expected - enlisting participant #3 can't be done
        }
        try {
            uba.close();
        } catch (TransactionRolledBackException e) {
            // Exception is expected - rollback on close because of cannotComplete
        }
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient2();
        assertEventLogClient3();
    }

    @Test
    public void testWSBAParticipantClientCancel() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        uba.begin();
        client1.saveData(ServiceCommand.DO_COMPLETE);
        client2.saveData(ServiceCommand.DO_COMPLETE);
        client3.saveData(ServiceCommand.DO_COMPLETE);
        uba.cancel();
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient2(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient3(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
    }

    @Test
    public void testWSBAParticipantApplicationException() throws Exception {
        ParticipantCompletionCoordinatorRules.setParticipantCount(3);
        try {
            uba.begin();
            client1.saveData(ServiceCommand.DO_COMPLETE);
            client2.saveData(ServiceCommand.APPLICATION_EXCEPTION);
            Assert.fail("Exception should have been thrown by now");
        } catch (TestApplicationException e) {
            // Exception is expected
        }
        try {
            client3.saveData(ServiceCommand.DO_COMPLETE);
            uba.close();
            Assert.fail("Exception should have been thrown by now");
        } catch (com.arjuna tre) {
            // Exception is expected to be throws from close() method
        }
        assertEventLogClient1(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
        assertEventLogClient2(EventLogEvent.CANCEL);
        assertEventLogClient3(EventLogEvent.CONFIRM_COMPLETED, EventLogEvent.COMPENSATE);
    }
}

