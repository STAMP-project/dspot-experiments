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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.errorhandling.ForeignException;
import org.apache.hadoop.hbase.errorhandling.ForeignExceptionDispatcher;
import org.apache.hadoop.hbase.errorhandling.TimeoutException;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.ArrayEquals;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Cluster-wide testing of a distributed three-phase commit using a 'real' zookeeper cluster
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestZKProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKProcedure.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final String COORDINATOR_NODE_NAME = "coordinator";

    private static final long KEEP_ALIVE = 100;// seconds


    private static final int POOL_SIZE = 1;

    private static final long TIMEOUT = 10000;// when debugging make this larger for debugging


    private static final long WAKE_FREQUENCY = 500;

    private static final String opName = "op";

    private static final byte[] data = new byte[]{ 1, 2 };// TODO what is this used for?


    private static final VerificationMode once = Mockito.times(1);

    @Test
    public void testEmptyMemberSet() throws Exception {
        runCommit();
    }

    @Test
    public void testSingleMember() throws Exception {
        runCommit("one");
    }

    @Test
    public void testMultipleMembers() throws Exception {
        runCommit("one", "two", "three", "four");
    }

    /**
     * Test a distributed commit with multiple cohort members, where one of the cohort members has a
     * timeout exception during the prepare stage.
     */
    @Test
    public void testMultiCohortWithMemberTimeoutDuringPrepare() throws Exception {
        String opDescription = "error injection coordination";
        String[] cohortMembers = new String[]{ "one", "two", "three" };
        List<String> expected = Lists.newArrayList(cohortMembers);
        // error constants
        final int memberErrorIndex = 2;
        final CountDownLatch coordinatorReceivedErrorLatch = new CountDownLatch(1);
        // start running the coordinator and its controller
        ZKWatcher coordinatorWatcher = TestZKProcedure.newZooKeeperWatcher();
        ZKProcedureCoordinator coordinatorController = new ZKProcedureCoordinator(coordinatorWatcher, opDescription, TestZKProcedure.COORDINATOR_NODE_NAME);
        ThreadPoolExecutor pool = ProcedureCoordinator.defaultPool(TestZKProcedure.COORDINATOR_NODE_NAME, TestZKProcedure.POOL_SIZE, TestZKProcedure.KEEP_ALIVE);
        ProcedureCoordinator coordinator = Mockito.spy(new ProcedureCoordinator(coordinatorController, pool));
        // start a member for each node
        SubprocedureFactory subprocFactory = Mockito.mock(SubprocedureFactory.class);
        List<Pair<ProcedureMember, ZKProcedureMemberRpcs>> members = new ArrayList<>(expected.size());
        for (String member : expected) {
            ZKWatcher watcher = TestZKProcedure.newZooKeeperWatcher();
            ZKProcedureMemberRpcs controller = new ZKProcedureMemberRpcs(watcher, opDescription);
            ThreadPoolExecutor pool2 = ProcedureMember.defaultPool(member, 1, TestZKProcedure.KEEP_ALIVE);
            ProcedureMember mem = new ProcedureMember(controller, pool2, subprocFactory);
            members.add(new Pair(mem, controller));
            controller.start(member, mem);
        }
        // setup mock subprocedures
        final List<Subprocedure> cohortTasks = new ArrayList<>();
        final int[] elem = new int[1];
        for (int i = 0; i < (members.size()); i++) {
            ForeignExceptionDispatcher cohortMonitor = new ForeignExceptionDispatcher();
            final ProcedureMember comms = members.get(i).getFirst();
            Subprocedure commit = Mockito.spy(new org.apache.hadoop.hbase.procedure.Subprocedure.SubprocedureImpl(comms, TestZKProcedure.opName, cohortMonitor, TestZKProcedure.WAKE_FREQUENCY, TestZKProcedure.TIMEOUT));
            // This nasty bit has one of the impls throw a TimeoutException
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    int index = elem[0];
                    if (index == memberErrorIndex) {
                        TestZKProcedure.LOG.debug("Sending error to coordinator");
                        ForeignException remoteCause = new ForeignException("TIMER", new TimeoutException("subprocTimeout", 1, 2, 0));
                        Subprocedure r = ((Subprocedure) (invocation.getMock()));
                        TestZKProcedure.LOG.error(("Remote commit failure, not propagating error:" + remoteCause));
                        comms.receiveAbortProcedure(r.getName(), remoteCause);
                        Assert.assertTrue(r.isComplete());
                        // don't complete the error phase until the coordinator has gotten the error
                        // notification (which ensures that we never progress past prepare)
                        try {
                            Procedure.waitForLatch(coordinatorReceivedErrorLatch, new ForeignExceptionDispatcher(), TestZKProcedure.WAKE_FREQUENCY, "coordinator received error");
                        } catch (InterruptedException e) {
                            TestZKProcedure.LOG.debug(("Wait for latch interrupted, done:" + ((coordinatorReceivedErrorLatch.getCount()) == 0)));
                            // reset the interrupt status on the thread
                            Thread.currentThread().interrupt();
                        }
                    }
                    elem[0] = ++index;
                    return null;
                }
            }).when(commit).acquireBarrier();
            cohortTasks.add(commit);
        }
        // pass out a task per member
        final AtomicInteger taskIndex = new AtomicInteger();
        Mockito.when(subprocFactory.buildSubprocedure(Mockito.eq(TestZKProcedure.opName), ((byte[]) (Mockito.argThat(new ArrayEquals(TestZKProcedure.data)))))).thenAnswer(new Answer<Subprocedure>() {
            @Override
            public Subprocedure answer(InvocationOnMock invocation) throws Throwable {
                int index = taskIndex.getAndIncrement();
                Subprocedure commit = cohortTasks.get(index);
                return commit;
            }
        });
        // setup spying on the coordinator
        ForeignExceptionDispatcher coordinatorTaskErrorMonitor = Mockito.spy(new ForeignExceptionDispatcher());
        Procedure coordinatorTask = Mockito.spy(new Procedure(coordinator, coordinatorTaskErrorMonitor, TestZKProcedure.WAKE_FREQUENCY, TestZKProcedure.TIMEOUT, TestZKProcedure.opName, TestZKProcedure.data, expected));
        Mockito.when(coordinator.createProcedure(ArgumentMatchers.any(), ArgumentMatchers.eq(TestZKProcedure.opName), ArgumentMatchers.eq(TestZKProcedure.data), ArgumentMatchers.anyListOf(String.class))).thenReturn(coordinatorTask);
        // count down the error latch when we get the remote error
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // pass on the error to the master
                invocation.callRealMethod();
                // then count down the got error latch
                coordinatorReceivedErrorLatch.countDown();
                return null;
            }
        }).when(coordinatorTask).receive(Mockito.any());
        // ----------------------------
        // start running the operation
        // ----------------------------
        Procedure task = coordinator.startProcedure(coordinatorTaskErrorMonitor, TestZKProcedure.opName, TestZKProcedure.data, expected);
        Assert.assertEquals("Didn't mock coordinator task", coordinatorTask, task);
        // wait for the task to complete
        try {
            task.waitForCompleted();
        } catch (ForeignException fe) {
            // this may get caught or may not
        }
        // -------------
        // verification
        // -------------
        // always expect prepared, never committed, and possible to have cleanup and finish (racy since
        // error case)
        waitAndVerifyProc(coordinatorTask, TestZKProcedure.once, Mockito.never(), TestZKProcedure.once, Mockito.atMost(1), true);
        verifyCohortSuccessful(expected, subprocFactory, cohortTasks, TestZKProcedure.once, Mockito.never(), TestZKProcedure.once, TestZKProcedure.once, true);
        // close all the open things
        closeAll(coordinator, coordinatorController, members);
    }
}

