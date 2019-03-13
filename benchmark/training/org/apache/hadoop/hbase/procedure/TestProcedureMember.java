/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.procedure;


import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.errorhandling.ForeignExceptionDispatcher;
import org.apache.hadoop.hbase.errorhandling.TimeoutException;
import org.apache.hadoop.hbase.procedure.Subprocedure.SubprocedureImpl;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Test the procedure member, and it's error handling mechanisms.
 */
@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureMember {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureMember.class);

    private static final long WAKE_FREQUENCY = 100;

    private static final long TIMEOUT = 100000;

    private static final long POOL_KEEP_ALIVE = 1;

    private final String op = "some op";

    private final byte[] data = new byte[0];

    private final ForeignExceptionDispatcher mockListener = Mockito.spy(new ForeignExceptionDispatcher());

    private final SubprocedureFactory mockBuilder = Mockito.mock(SubprocedureFactory.class);

    private final ProcedureMemberRpcs mockMemberComms = Mockito.mock(ProcedureMemberRpcs.class);

    private ProcedureMember member;

    private ForeignExceptionDispatcher dispatcher;

    Subprocedure spySub;

    /**
     * Test the normal sub procedure execution case.
     */
    @Test
    public void testSimpleRun() throws Exception {
        member = buildCohortMember();
        TestProcedureMember.EmptySubprocedure subproc = new TestProcedureMember.EmptySubprocedure(member, mockListener);
        TestProcedureMember.EmptySubprocedure spy = Mockito.spy(subproc);
        Mockito.when(mockBuilder.buildSubprocedure(op, data)).thenReturn(spy);
        // when we get a prepare, then start the commit phase
        addCommitAnswer();
        // run the operation
        // build a new operation
        Subprocedure subproc1 = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc1);
        // and wait for it to finish
        waitForLocallyCompleted();
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spy);
        acquireBarrier();
        order.verify(mockMemberComms).sendMemberAcquired(ArgumentMatchers.eq(spy));
        insideBarrier();
        order.verify(mockMemberComms).sendMemberCompleted(ArgumentMatchers.eq(spy), ArgumentMatchers.eq(data));
        order.verify(mockMemberComms, Mockito.never()).sendMemberAborted(ArgumentMatchers.eq(spy), ArgumentMatchers.any());
    }

    /**
     * Make sure we call cleanup etc, when we have an exception during
     * {@link Subprocedure#acquireBarrier()}.
     */
    @Test
    public void testMemberPrepareException() throws Exception {
        buildCohortMemberPair();
        // mock an exception on Subprocedure's prepare
        acquireBarrier();
        // run the operation
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        member.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spySub);
        acquireBarrier();
        // Later phases not run
        order.verify(mockMemberComms, Mockito.never()).sendMemberAcquired(ArgumentMatchers.eq(spySub));
        insideBarrier();
        order.verify(mockMemberComms, Mockito.never()).sendMemberCompleted(ArgumentMatchers.eq(spySub), ArgumentMatchers.eq(data));
        // error recovery path exercised
        order.verify(spySub).cancel(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        order.verify(spySub).cleanup(ArgumentMatchers.any());
    }

    /**
     * Make sure we call cleanup etc, when we have an exception during prepare.
     */
    @Test
    public void testSendMemberAcquiredCommsFailure() throws Exception {
        buildCohortMemberPair();
        // mock an exception on Subprocedure's prepare
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException("Forced IOException in memeber prepare");
            }
        }).when(mockMemberComms).sendMemberAcquired(ArgumentMatchers.any());
        // run the operation
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        member.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spySub);
        acquireBarrier();
        order.verify(mockMemberComms).sendMemberAcquired(ArgumentMatchers.eq(spySub));
        // Later phases not run
        insideBarrier();
        order.verify(mockMemberComms, Mockito.never()).sendMemberCompleted(ArgumentMatchers.eq(spySub), ArgumentMatchers.eq(data));
        // error recovery path exercised
        order.verify(spySub).cancel(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        order.verify(spySub).cleanup(ArgumentMatchers.any());
    }

    /**
     * Fail correctly if coordinator aborts the procedure.  The subprocedure will not interrupt a
     * running {@link Subprocedure#acquireBarrier()} -- prepare needs to finish first, and the the abort
     * is checked.  Thus, the {@link Subprocedure#acquireBarrier()} should succeed but later get rolled back
     * via {@link Subprocedure#cleanup}.
     */
    @Test
    public void testCoordinatorAbort() throws Exception {
        buildCohortMemberPair();
        // mock that another node timed out or failed to prepare
        final TimeoutException oate = new TimeoutException("bogus timeout", 1, 2, 0);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // inject a remote error (this would have come from an external thread)
                spySub.cancel("bogus message", oate);
                // sleep the wake frequency since that is what we promised
                Thread.sleep(TestProcedureMember.WAKE_FREQUENCY);
                return null;
            }
        }).when(spySub).waitForReachedGlobalBarrier();
        // run the operation
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        member.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spySub);
        acquireBarrier();
        order.verify(mockMemberComms).sendMemberAcquired(ArgumentMatchers.eq(spySub));
        // Later phases not run
        insideBarrier();
        order.verify(mockMemberComms, Mockito.never()).sendMemberCompleted(ArgumentMatchers.eq(spySub), ArgumentMatchers.eq(data));
        // error recovery path exercised
        order.verify(spySub).cancel(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        order.verify(spySub).cleanup(ArgumentMatchers.any());
    }

    /**
     * Handle failures if a member's commit phase fails.
     *
     * NOTE: This is the core difference that makes this different from traditional 2PC.  In true
     * 2PC the transaction is committed just before the coordinator sends commit messages to the
     * member.  Members are then responsible for reading its TX log.  This implementation actually
     * rolls back, and thus breaks the normal TX guarantees.
     */
    @Test
    public void testMemberCommitException() throws Exception {
        buildCohortMemberPair();
        // mock an exception on Subprocedure's prepare
        insideBarrier();
        // run the operation
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        member.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spySub);
        acquireBarrier();
        order.verify(mockMemberComms).sendMemberAcquired(ArgumentMatchers.eq(spySub));
        insideBarrier();
        // Later phases not run
        order.verify(mockMemberComms, Mockito.never()).sendMemberCompleted(ArgumentMatchers.eq(spySub), ArgumentMatchers.eq(data));
        // error recovery path exercised
        order.verify(spySub).cancel(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        order.verify(spySub).cleanup(ArgumentMatchers.any());
    }

    /**
     * Handle Failures if a member's commit phase succeeds but notification to coordinator fails
     *
     * NOTE: This is the core difference that makes this different from traditional 2PC.  In true
     * 2PC the transaction is committed just before the coordinator sends commit messages to the
     * member.  Members are then responsible for reading its TX log.  This implementation actually
     * rolls back, and thus breaks the normal TX guarantees.
     */
    @Test
    public void testMemberCommitCommsFailure() throws Exception {
        buildCohortMemberPair();
        final TimeoutException oate = new TimeoutException("bogus timeout", 1, 2, 0);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // inject a remote error (this would have come from an external thread)
                spySub.cancel("commit comms fail", oate);
                // sleep the wake frequency since that is what we promised
                Thread.sleep(TestProcedureMember.WAKE_FREQUENCY);
                return null;
            }
        }).when(mockMemberComms).sendMemberCompleted(ArgumentMatchers.any(), ArgumentMatchers.eq(data));
        // run the operation
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        member.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spySub);
        acquireBarrier();
        order.verify(mockMemberComms).sendMemberAcquired(ArgumentMatchers.eq(spySub));
        insideBarrier();
        order.verify(mockMemberComms).sendMemberCompleted(ArgumentMatchers.eq(spySub), ArgumentMatchers.eq(data));
        // error recovery path exercised
        order.verify(spySub).cancel(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        order.verify(spySub).cleanup(ArgumentMatchers.any());
    }

    /**
     * Fail correctly on getting an external error while waiting for the prepared latch
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testPropagateConnectionErrorBackToManager() throws Exception {
        // setup the operation
        member = buildCohortMember();
        ProcedureMember memberSpy = Mockito.spy(member);
        // setup the commit and the spy
        final ForeignExceptionDispatcher dispatcher = new ForeignExceptionDispatcher();
        ForeignExceptionDispatcher dispSpy = Mockito.spy(dispatcher);
        Subprocedure commit = new TestProcedureMember.EmptySubprocedure(member, dispatcher);
        Subprocedure spy = Mockito.spy(commit);
        Mockito.when(mockBuilder.buildSubprocedure(op, data)).thenReturn(spy);
        // fail during the prepare phase
        acquireBarrier();
        // and throw a connection error when we try to tell the controller about it
        Mockito.doThrow(new IOException("Controller is down!")).when(mockMemberComms).sendMemberAborted(ArgumentMatchers.eq(spy), ArgumentMatchers.any());
        // run the operation
        // build a new operation
        Subprocedure subproc = memberSpy.createSubprocedure(op, data);
        memberSpy.submitSubprocedure(subproc);
        // if the operation doesn't die properly, then this will timeout
        memberSpy.closeAndWait(TestProcedureMember.TIMEOUT);
        // make sure everything ran in order
        InOrder order = Mockito.inOrder(mockMemberComms, spy, dispSpy);
        // make sure we acquire.
        acquireBarrier();
        order.verify(mockMemberComms, Mockito.never()).sendMemberAcquired(spy);
        // TODO Need to do another refactor to get this to propagate to the coordinator.
        // make sure we pass a remote exception back the controller
        // order.verify(mockMemberComms).sendMemberAborted(eq(spy),
        // any());
        // order.verify(dispSpy).receiveError(anyString(),
        // any(), any());
    }

    /**
     * Test that the cohort member correctly doesn't attempt to start a task when the builder cannot
     * correctly build a new task for the requested operation
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testNoTaskToBeRunFromRequest() throws Exception {
        ThreadPoolExecutor pool = Mockito.mock(ThreadPoolExecutor.class);
        Mockito.when(mockBuilder.buildSubprocedure(op, data)).thenReturn(null).thenThrow(new IllegalStateException("Wrong state!"), new IllegalArgumentException("can't understand the args"));
        member = new ProcedureMember(mockMemberComms, pool, mockBuilder);
        // builder returns null
        // build a new operation
        Subprocedure subproc = member.createSubprocedure(op, data);
        member.submitSubprocedure(subproc);
        // throws an illegal state exception
        try {
            // build a new operation
            Subprocedure subproc2 = member.createSubprocedure(op, data);
            member.submitSubprocedure(subproc2);
        } catch (IllegalStateException ise) {
        }
        // throws an illegal argument exception
        try {
            // build a new operation
            Subprocedure subproc3 = member.createSubprocedure(op, data);
            member.submitSubprocedure(subproc3);
        } catch (IllegalArgumentException iae) {
        }
        // no request should reach the pool
        Mockito.verifyZeroInteractions(pool);
        // get two abort requests
        // TODO Need to do another refactor to get this to propagate to the coordinator.
        // verify(mockMemberComms, times(2)).sendMemberAborted(any(), any());
    }

    /**
     * Helper {@link Procedure} who's phase for each step is just empty
     */
    public class EmptySubprocedure extends SubprocedureImpl {
        public EmptySubprocedure(ProcedureMember member, ForeignExceptionDispatcher dispatcher) {
            // TODO 1000000 is an arbitrary number that I picked.
            super(member, op, dispatcher, TestProcedureMember.WAKE_FREQUENCY, TestProcedureMember.TIMEOUT);
        }
    }
}

