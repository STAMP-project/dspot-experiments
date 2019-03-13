/**
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
package org.flowable.crystalball.simulator;


import org.flowable.crystalball.simulator.delegate.event.impl.InMemoryRecordFlowableEventListener;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.service.impl.el.NoExecutionVariableScope;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author martin.grofcik
 */
public class SimpleSimulationRunTest {
    // deployment created
    private static final String DEPLOYMENT_CREATED_EVENT_TYPE = "DEPLOYMENT_CREATED_EVENT";

    private static final String DEPLOYMENT_RESOURCES_KEY = "deploymentResources";

    // Process instance start event
    private static final String PROCESS_INSTANCE_START_EVENT_TYPE = "PROCESS_INSTANCE_START";

    private static final String PROCESS_DEFINITION_ID_KEY = "processDefinitionId";

    private static final String VARIABLES_KEY = "variables";

    // User task completed event
    private static final String USER_TASK_COMPLETED_EVENT_TYPE = "USER_TASK_COMPLETED";

    private static final String BUSINESS_KEY = "testBusinessKey";

    public static final String TEST_VALUE = "TestValue";

    public static final String TEST_VARIABLE = "testVariable";

    private static final String USERTASK_PROCESS = "org/flowable/crystalball/simulator/impl/playback/PlaybackProcessStartTest.testUserTask.bpmn20.xml";

    protected InMemoryRecordFlowableEventListener listener;

    @Test
    public void testStep() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        RuntimeService runtimeService = SimulationRunContext.getRuntimeService();
        TaskService taskService = SimulationRunContext.getTaskService();
        HistoryService historyService = SimulationRunContext.getHistoryService();
        // debugger step - deploy processDefinition
        simDebugger.step();
        step0Check(SimulationRunContext.getRepositoryService());
        // debugger step - start process and stay on the userTask
        simDebugger.step();
        step1Check(runtimeService, taskService);
        // debugger step - complete userTask and finish process
        simDebugger.step();
        step2Check(runtimeService, taskService);
        checkStatus(historyService);
        simDebugger.close();
        ProcessEngines.destroy();
    }

    @Test
    public void testRunToTime() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        RuntimeService runtimeService = SimulationRunContext.getRuntimeService();
        TaskService taskService = SimulationRunContext.getTaskService();
        HistoryService historyService = SimulationRunContext.getHistoryService();
        simDebugger.runTo(0);
        ProcessInstance procInstance = runtimeService.createProcessInstanceQuery().active().processInstanceBusinessKey("oneTaskProcessBusinessKey").singleResult();
        Assert.assertNull(procInstance);
        // debugger step - deploy process
        simDebugger.runTo(1);
        step0Check(SimulationRunContext.getRepositoryService());
        // debugger step - start process and stay on the userTask
        simDebugger.runTo(1001);
        step1Check(runtimeService, taskService);
        // process engine should be in the same state as before
        simDebugger.runTo(2000);
        step1Check(runtimeService, taskService);
        // debugger step - complete userTask and finish process
        simDebugger.runTo(2500);
        step2Check(runtimeService, taskService);
        checkStatus(historyService);
        simDebugger.close();
        ProcessEngines.destroy();
    }

    @Test(expected = RuntimeException.class)
    public void testRunToTimeInThePast() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        try {
            simDebugger.runTo((-1));
            Assert.fail("RuntimeException expected - unable to execute event from the past");
        } finally {
            simDebugger.close();
            ProcessEngines.destroy();
        }
    }

    @Test
    public void testRunToEvent() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        try {
            simDebugger.runTo(SimpleSimulationRunTest.USER_TASK_COMPLETED_EVENT_TYPE);
            step1Check(SimulationRunContext.getRuntimeService(), SimulationRunContext.getTaskService());
            simDebugger.runContinue();
        } finally {
            simDebugger.close();
            ProcessEngines.destroy();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRunToNonExistingEvent() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        try {
            simDebugger.runTo("");
            checkStatus(SimulationRunContext.getHistoryService());
        } finally {
            simDebugger.close();
            ProcessEngines.destroy();
        }
    }

    @Test
    public void testRunContinue() throws Exception {
        recordEvents();
        SimulationDebugger simDebugger = createDebugger();
        simDebugger.init(new NoExecutionVariableScope());
        simDebugger.runContinue();
        checkStatus(SimulationRunContext.getHistoryService());
        simDebugger.close();
        ProcessEngines.destroy();
    }
}

