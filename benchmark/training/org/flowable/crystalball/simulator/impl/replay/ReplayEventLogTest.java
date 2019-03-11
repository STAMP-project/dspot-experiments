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
package org.flowable.crystalball.simulator.impl.replay;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.flowable.crystalball.simulator.SimpleEventCalendar;
import org.flowable.crystalball.simulator.SimulationDebugger;
import org.flowable.crystalball.simulator.SimulationEvent;
import org.flowable.crystalball.simulator.SimulationEventComparator;
import org.flowable.crystalball.simulator.delegate.event.impl.EventLogTransformer;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.event.EventLogEntry;
import org.flowable.engine.impl.ProcessEngineImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.impl.el.NoExecutionVariableScope;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author martin.grofcik
 */
public class ReplayEventLogTest {
    // Process instance start event
    private static final String PROCESS_INSTANCE_START_EVENT_TYPE = "PROCESS_INSTANCE_START";

    private static final String PROCESS_DEFINITION_ID_KEY = "processDefinitionId";

    private static final String VARIABLES_KEY = "variables";

    // User task completed event
    private static final String USER_TASK_COMPLETED_EVENT_TYPE = "USER_TASK_COMPLETED";

    private static final String USERTASK_PROCESS = "oneTaskProcess";

    private static final String BUSINESS_KEY = "testBusinessKey";

    private static final String TEST_VALUE = "TestValue";

    private static final String TEST_VARIABLE = "testVariable";

    private static final String TASK_TEST_VALUE = "TaskTestValue";

    private static final String TASK_TEST_VARIABLE = "taskTestVariable";

    private static final String THE_USERTASK_PROCESS = "org/flowable/crystalball/simulator/impl/playback/PlaybackProcessStartTest.testUserTask.bpmn20.xml";

    @Test
    public void testProcessInstanceStartEvents() throws Exception {
        ProcessEngineImpl processEngine = initProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ManagementService managementService = processEngine.getManagementService();
        HistoryService historyService = processEngine.getHistoryService();
        // record events
        Map<String, Object> variables = new HashMap<>();
        variables.put(ReplayEventLogTest.TEST_VARIABLE, ReplayEventLogTest.TEST_VALUE);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ReplayEventLogTest.USERTASK_PROCESS, ReplayEventLogTest.BUSINESS_KEY, variables);
        Task task = taskService.createTaskQuery().taskDefinitionKey("userTask").singleResult();
        TimeUnit.MILLISECONDS.sleep(50);
        variables = new HashMap<>();
        variables.put(ReplayEventLogTest.TASK_TEST_VARIABLE, ReplayEventLogTest.TASK_TEST_VALUE);
        taskService.complete(task.getId(), variables);
        // transform log events
        List<EventLogEntry> eventLogEntries = managementService.getEventLogEntries(null, null);
        EventLogTransformer transformer = new EventLogTransformer(ReplayEventLogTest.getTransformers());
        List<SimulationEvent> simulationEvents = transformer.transform(eventLogEntries);
        SimpleEventCalendar eventCalendar = new SimpleEventCalendar(processEngine.getProcessEngineConfiguration().getClock(), new SimulationEventComparator());
        eventCalendar.addEvents(simulationEvents);
        // replay process instance run
        final SimulationDebugger simRun = new org.flowable.crystalball.simulator.ReplaySimulationRun(processEngine, eventCalendar, ReplayEventLogTest.getReplayHandlers(processInstance.getId()));
        simRun.init(new NoExecutionVariableScope());
        // original process is finished - there should not be any running process instance/task
        Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(ReplayEventLogTest.USERTASK_PROCESS).count());
        Assert.assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());
        simRun.step();
        // replay process was started
        ProcessInstance replayProcessInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey(ReplayEventLogTest.USERTASK_PROCESS).singleResult();
        Assert.assertNotNull(replayProcessInstance);
        Assert.assertNotEquals(replayProcessInstance.getId(), processInstance.getId());
        Assert.assertEquals(ReplayEventLogTest.TEST_VALUE, runtimeService.getVariable(replayProcessInstance.getId(), ReplayEventLogTest.TEST_VARIABLE));
        // there should be one task
        Assert.assertEquals(1, taskService.createTaskQuery().taskDefinitionKey("userTask").count());
        simRun.step();
        // userTask was completed - replay process was finished
        Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(ReplayEventLogTest.USERTASK_PROCESS).count());
        Assert.assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());
        HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(replayProcessInstance.getId()).variableName(ReplayEventLogTest.TASK_TEST_VARIABLE).singleResult();
        Assert.assertNotNull(variableInstance);
        Assert.assertEquals(ReplayEventLogTest.TASK_TEST_VALUE, variableInstance.getValue());
        // close simulation
        simRun.close();
        processEngine.close();
        ProcessEngines.destroy();
    }
}

