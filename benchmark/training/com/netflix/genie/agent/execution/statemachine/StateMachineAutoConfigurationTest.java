/**
 * Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.agent.execution.statemachine;


import Events.CANCEL_JOB_LAUNCH;
import Events.ERROR;
import Events.START;
import StateAction.CleanupJob;
import StateAction.ConfigureAgent;
import StateAction.HandleError;
import StateAction.Initialize;
import StateAction.LaunchJob;
import StateAction.MonitorJob;
import StateAction.ResolveJobSpecification;
import StateAction.SetUpJob;
import StateAction.Shutdown;
import States.CLEANUP_JOB;
import States.CONFIGURE_AGENT;
import States.END;
import States.HANDLE_ERROR;
import States.INITIALIZE;
import States.LAUNCH_JOB;
import States.MONITOR_JOB;
import States.READY;
import States.RESOLVE_JOB_SPECIFICATION;
import States.SETUP_JOB;
import States.SHUTDOWN;
import com.netflix.genie.agent.execution.statemachine.actions.StateAction;
import com.netflix.genie.agent.execution.statemachine.listeners.JobExecutionListener;
import com.netflix.genie.test.categories.UnitTest;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;


/**
 * Test the state machine configured for job execution using mock state actions.
 */
@Slf4j
@Category(UnitTest.class)
public class StateMachineAutoConfigurationTest {
    private static final int MIN_ACTION_DURATION_MS = 10;

    private static final int MAX_ACTION_DURATION_MS = 300;

    private static final int AWAIT_TIME = 5;

    private List<States> visitedStates;

    private List<Action<States, Events>> executedActions;

    private StateMachine<States, Events> sm;

    private Collection<Pair<States, StateAction>> statesWithActions;

    private Initialize initializeActionMock;

    private ConfigureAgent configureAgentActionMock;

    private ResolveJobSpecification resolveJobSpecificationActionMock;

    private SetUpJob setupJobActionMock;

    private LaunchJob launchJobActionMock;

    private MonitorJob monitorJobActionMock;

    private CleanupJob cleanupJobActionMock;

    private Shutdown shutdownActionMock;

    private HandleError handleErrorActionMock;

    /**
     * Execution with no errors or cancellation.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void successfulExecutionTest() throws Exception {
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(9).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Cancel event before job execution is started.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelBeforeStartTest() throws Exception {
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(CANCEL_JOB_LAUNCH).expectState(END).expectStateChanged(3).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is entering CONFIGURE_AGENT state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelBeforeAgentConfigure() throws Exception {
        cancelJobBeforeEnteringState(CONFIGURE_AGENT);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(5).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is preparing to exit CONFIGURE_AGENT state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelAfterAgentConfigure() throws Exception {
        cancelJobAfterEnteringState(CONFIGURE_AGENT);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(6).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is executing CONFIGURE_AGENT state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelDuringAgentConfigure() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (configureAgentActionMock)).cancelJobDuringExecution();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(5).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is entering SETUP_JOB state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelBeforeSetup() throws Exception {
        cancelJobBeforeEnteringState(SETUP_JOB);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(7).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is preparing to exit SETUP_JOB state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelAfterSetup() throws Exception {
        cancelJobAfterEnteringState(SETUP_JOB);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(9).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is executing SETUP_JOB state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelDuringSetup() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (setupJobActionMock)).cancelJobDuringExecution();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(7).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is entering LAUNCH_JOB state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelBeforeLaunch() throws Exception {
        cancelJobBeforeEnteringState(LAUNCH_JOB);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(9).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is preparing to exit LAUNCH_JOB state.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelAfterLaunch() throws Exception {
        cancelJobAfterEnteringState(LAUNCH_JOB);
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(9).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Handle cancel signal as state machine is executing LAUNCH_JOB state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void cancelDuringLaunch() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (launchJobActionMock)).cancelJobDuringExecution();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectState(END).expectStateChanged(9).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock);
    }

    /**
     * Test failure during INITIALIZATION state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void initErrorTest() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (initializeActionMock)).makeActionFail();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectStateChanged(3).expectState(END).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, HANDLE_ERROR, END);
        assertActionsExecuted(initializeActionMock, handleErrorActionMock);
    }

    /**
     * Test failure during RESOLVE_JOB_SPECIFICATION state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void resolveJobSpecificationErrorTest() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (resolveJobSpecificationActionMock)).makeActionFail();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectStateChanged(5).expectState(END).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, HANDLE_ERROR, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, handleErrorActionMock);
    }

    /**
     * Test failure during SHUTDOWN state action.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void shutdownErrorTest() throws Exception {
        ((StateMachineAutoConfigurationTest.TestStateAction) (shutdownActionMock)).makeActionFail();
        StateMachineTestPlanBuilder.<States, Events>builder().defaultAwaitTime(StateMachineAutoConfigurationTest.AWAIT_TIME).stateMachine(sm).step().expectState(READY).and().step().sendEvent(START).expectStateChanged(10).expectState(END).expectStateMachineStopped(1).and().build().test();
        Assert.assertTrue(sm.isComplete());
        assertStatesVisited(READY, INITIALIZE, CONFIGURE_AGENT, RESOLVE_JOB_SPECIFICATION, SETUP_JOB, LAUNCH_JOB, MONITOR_JOB, CLEANUP_JOB, SHUTDOWN, HANDLE_ERROR, END);
        assertActionsExecuted(initializeActionMock, configureAgentActionMock, resolveJobSpecificationActionMock, setupJobActionMock, launchJobActionMock, monitorJobActionMock, cleanupJobActionMock, shutdownActionMock, handleErrorActionMock);
    }

    private class ExecutionPathListener extends StateMachineListenerAdapter<States, Events> implements JobExecutionListener {
        @Override
        public void stateEntered(final State<States, Events> state) {
            visitedStates.add(state.getId());
        }

        @Override
        public void onExecute(final StateMachine<States, Events> stateMachine, final Action<States, Events> action, final long duration) {
            executedActions.add(action);
        }
    }

    private static class TestStateAction implements StateAction.CleanupJob , StateAction.ConfigureAgent , StateAction.HandleError , StateAction.Initialize , StateAction.LaunchJob , StateAction.MonitorJob , StateAction.ResolveJobSpecification , StateAction.SetUpJob , StateAction.Shutdown {
        private final Random random;

        private final States state;

        private final Events completionEvent;

        private boolean cancelDuringExecution;

        private boolean failAction;

        private final AtomicInteger executionCount;

        TestStateAction(final States state, final Events completionEvent) {
            this.state = state;
            this.completionEvent = completionEvent;
            this.random = new Random();
            this.executionCount = new AtomicInteger();
        }

        @Override
        public void execute(final StateContext<States, Events> context) {
            log.info("Executing test action for state {}", state);
            if ((StateMachineAutoConfigurationTest.MIN_ACTION_DURATION_MS) > 0) {
                final int sleepMillis = (random.nextInt(((StateMachineAutoConfigurationTest.MAX_ACTION_DURATION_MS) - (StateMachineAutoConfigurationTest.MIN_ACTION_DURATION_MS)))) + (StateMachineAutoConfigurationTest.MIN_ACTION_DURATION_MS);
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (cancelDuringExecution) {
                log.info("Sending CANCEL_JOB_LAUNCH event during action for state {}", state);
                context.getStateMachine().sendEvent(CANCEL_JOB_LAUNCH);
            }
            if (failAction) {
                log.info("Failing test action for state {}", state);
                context.getStateMachine().sendEvent(ERROR);
            } else {
                context.getStateMachine().sendEvent(completionEvent);
            }
            executionCount.incrementAndGet();
        }

        void cancelJobDuringExecution() {
            this.cancelDuringExecution = true;
        }

        void makeActionFail() {
            this.failAction = true;
        }

        int getExecutionCount() {
            return executionCount.get();
        }

        @Override
        public void cleanup() {
        }
    }
}

