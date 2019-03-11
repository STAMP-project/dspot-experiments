/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.test.extension.rts;


import HeuristicType.HEURISTIC_COMMIT;
import HeuristicType.HEURISTIC_ROLLBACK;
import TxStatus.TransactionCommitted;
import TxStatus.TransactionRolledBack;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.jbossts.star.util.TxSupport;
import org.jboss.narayana.rest.integration.api.Aborted;
import org.jboss.narayana.rest.integration.api.ParticipantsManagerFactory;
import org.jboss.narayana.rest.integration.api.Prepared;
import org.jboss.narayana.rest.integration.api.ReadOnly;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.extension.rts.common.LoggingParticipant;


/**
 *
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RunWith(Arquillian.class)
public final class ParticipantTestCase extends AbstractTestCase {
    private static final String APPLICATION_ID = "org.wildfly.test.extension.rts";

    private static final String DEPENDENCIES = "Dependencies: org.jboss.narayana.rts\n";

    private static final String SERVER_HOST_PORT = ((TestSuiteEnvironment.getServerAddress()) + ":") + (TestSuiteEnvironment.getHttpPort());

    @Test
    public void testCommit() {
        txSupport.startTx();
        LoggingParticipant participant1 = new LoggingParticipant(new Prepared());
        LoggingParticipant participant2 = new LoggingParticipant(new Prepared());
        ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), participant1);
        ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), participant2);
        txSupport.commitTx();
        Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "commit" }), participant1.getInvocations());
        Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "commit" }), participant2.getInvocations());
    }

    @Test
    public void testCommitOnePhase() {
        txSupport.startTx();
        LoggingParticipant participant = new LoggingParticipant(new Prepared());
        ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), participant);
        txSupport.commitTx();
        Assert.assertEquals(Arrays.asList(new String[]{ "commitOnePhase" }), participant.getInvocations());
    }

    @Test
    public void testReadOnly() {
        txSupport.startTx();
        final List<LoggingParticipant> participants = Arrays.asList(new LoggingParticipant[]{ new LoggingParticipant(new ReadOnly()), new LoggingParticipant(new Prepared()), new LoggingParticipant(new Prepared()) });
        for (LoggingParticipant p : participants) {
            ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), p);
        }
        txSupport.commitTx();
        // One of the participants was only prepared, while other two were prepared and committed.
        Assert.assertEquals(5, (((participants.get(0).getInvocations().size()) + (participants.get(1).getInvocations().size())) + (participants.get(2).getInvocations().size())));
        for (LoggingParticipant p : participants) {
            if ((p.getInvocations().size()) == 1) {
                Assert.assertEquals(Arrays.asList(new String[]{ "prepare" }), p.getInvocations());
            } else {
                Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "commit" }), p.getInvocations());
            }
        }
    }

    @Test
    public void testRollback() {
        txSupport.startTx();
        LoggingParticipant participant1 = new LoggingParticipant(new Prepared());
        LoggingParticipant participant2 = new LoggingParticipant(new Prepared());
        ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), participant1);
        ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), participant2);
        txSupport.rollbackTx();
        Assert.assertEquals(Arrays.asList(new String[]{ "rollback" }), participant1.getInvocations());
        Assert.assertEquals(Arrays.asList(new String[]{ "rollback" }), participant2.getInvocations());
    }

    @Test
    public void testRollbackByParticipant() {
        txSupport.startTx();
        final List<LoggingParticipant> participants = Arrays.asList(new LoggingParticipant[]{ new LoggingParticipant(new Aborted()), new LoggingParticipant(new Aborted()) });
        for (LoggingParticipant p : participants) {
            ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), p);
        }
        txSupport.commitTx();
        // One of the participants was prepared and then decided to rollback, the other was rolledback straight away.
        Assert.assertEquals(3, ((participants.get(0).getInvocations().size()) + (participants.get(1).getInvocations().size())));
        for (LoggingParticipant p : participants) {
            if ((p.getInvocations().size()) == 1) {
                Assert.assertEquals(Arrays.asList(new String[]{ "rollback" }), p.getInvocations());
            } else {
                Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "rollback" }), p.getInvocations());
            }
        }
    }

    @Test
    public void testHeuristicRollbackBeforePrepare() throws JAXBException {
        txSupport.startTx();
        final List<LoggingParticipant> participants = Arrays.asList(new LoggingParticipant[]{ new LoggingParticipant(new Prepared()), new LoggingParticipant(new Prepared()) });
        String lastParticipantid = null;
        for (LoggingParticipant p : participants) {
            lastParticipantid = ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), p);
        }
        ParticipantsManagerFactory.getInstance().reportHeuristic(lastParticipantid, HEURISTIC_ROLLBACK);
        final String transactionStatus = TxSupport.getStatus(txSupport.commitTx());
        Assert.assertEquals(TransactionRolledBack.name(), transactionStatus);
        if ((participants.get(0).getInvocations().size()) == 1) {
            Assert.assertEquals(Arrays.asList(new String[]{ "rollback" }), participants.get(0).getInvocations());
        } else {
            Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "rollback" }), participants.get(0).getInvocations());
        }
    }

    @Test
    public void testHeuristicCommitBeforePrepare() throws JAXBException {
        txSupport.startTx();
        final List<LoggingParticipant> participants = Arrays.asList(new LoggingParticipant[]{ new LoggingParticipant(new Prepared()), new LoggingParticipant(new Prepared()) });
        String lastParticipantid = null;
        for (LoggingParticipant p : participants) {
            lastParticipantid = ParticipantsManagerFactory.getInstance().enlist(ParticipantTestCase.APPLICATION_ID, txSupport.getDurableParticipantEnlistmentURI(), p);
        }
        ParticipantsManagerFactory.getInstance().reportHeuristic(lastParticipantid, HEURISTIC_COMMIT);
        final String transactionStatus = TxSupport.getStatus(txSupport.commitTx());
        Assert.assertEquals(TransactionCommitted.name(), transactionStatus);
        Assert.assertEquals(Arrays.asList(new String[]{ "prepare", "commit" }), participants.get(0).getInvocations());
        Assert.assertEquals(Collections.EMPTY_LIST, participants.get(1).getInvocations());
    }
}

