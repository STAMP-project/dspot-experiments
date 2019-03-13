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
package org.flowable.engine.test.bpmn.dynamic;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.dynamic.DynamicProcessDefinitionSummary;
import org.flowable.engine.dynamic.PropertiesParserConstants;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 * Created by Pardo David on 1/12/2016.
 */
public class DynamicProcessDefinitionSummaryTest extends PluggableFlowableTestCase implements DynamicBpmnConstants , PropertiesParserConstants {
    private static final String TASK_ONE_SID = "sid-B94D5D22-E93E-4401-ADC5-C5C073E1EEB4";

    private static final String TASK_TWO_SID = "sid-B1C37EBE-A273-4DDE-B909-89302638526A";

    private static final String SCRIPT_TASK_SID = "sid-A403BAE0-E367-449A-90B2-48834FCAA2F9";

    @Test
    public void testProcessDefinitionInfoCacheIsEnabledWithPluggableActivitiTestCase() throws Exception {
        Assert.assertThat(processEngineConfiguration.isEnableProcessDefinitionInfoCache(), CoreMatchers.is(true));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testIfNoProcessInfoIsAvailableTheBpmnModelIsUsed() throws Exception {
        // setup
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processInstance.getProcessDefinitionId());
        ArrayNode candidateGroups = processEngineConfiguration.getObjectMapper().createArrayNode();
        ArrayNode candidateUsers = processEngineConfiguration.getObjectMapper().createArrayNode();
        candidateUsers.add("david");
        // first task
        JsonNode jsonNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(jsonNode.get(USER_TASK_NAME).get(BPMN_MODEL_VALUE).asText(), CoreMatchers.is("Taak 1"));
        Assert.assertThat(jsonNode.get(USER_TASK_NAME).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(jsonNode.get(USER_TASK_ASSIGNEE).get(BPMN_MODEL_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(jsonNode.get(USER_TASK_ASSIGNEE).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(((ArrayNode) (jsonNode.get(USER_TASK_CANDIDATE_USERS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(candidateUsers));
        Assert.assertThat(jsonNode.get(USER_TASK_CANDIDATE_USERS).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(((ArrayNode) (jsonNode.get(USER_TASK_CANDIDATE_GROUPS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(candidateGroups));
        Assert.assertThat(jsonNode.get(USER_TASK_CANDIDATE_GROUPS).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        // second tasks
        candidateGroups = processEngineConfiguration.getObjectMapper().createArrayNode();
        candidateGroups.add("HR");
        candidateGroups.add("SALES");
        jsonNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_TWO_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(jsonNode.get(USER_TASK_ASSIGNEE).get(BPMN_MODEL_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(jsonNode.get(USER_TASK_ASSIGNEE).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(((ArrayNode) (jsonNode.get(USER_TASK_CANDIDATE_USERS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(candidateUsers));
        Assert.assertThat(((ArrayNode) (jsonNode.get(USER_TASK_CANDIDATE_GROUPS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(candidateGroups));
        // script tasks
        jsonNode = summary.getElement(DynamicProcessDefinitionSummaryTest.SCRIPT_TASK_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(jsonNode.get(SCRIPT_TASK_SCRIPT).get(BPMN_MODEL_VALUE).asText(), CoreMatchers.is("var test = \"hallo\";"));
        Assert.assertThat(jsonNode.get(SCRIPT_TASK_SCRIPT).get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testTheCandidateUserOfTheFirstTasksIsChanged() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode processInfo = dynamicBpmnService.changeUserTaskCandidateUser(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "bob", false);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, processInfo);
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        ArrayNode bpmnModelCandidateUsers = processEngineConfiguration.getObjectMapper().createArrayNode();
        bpmnModelCandidateUsers.add("david");
        ArrayNode dynamicCandidateUsers = processEngineConfiguration.getObjectMapper().createArrayNode();
        dynamicCandidateUsers.add("bob");
        JsonNode taskOneNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_USERS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(bpmnModelCandidateUsers));
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_USERS).get(DYNAMIC_VALUE))), CoreMatchers.is(dynamicCandidateUsers));
        // verify if runtime is up to date
        runtimeService.startProcessInstanceById(processDefinitionId);
        // bob and david both should have a single task.
        Task bobTask = taskService.createTaskQuery().taskCandidateUser("bob").singleResult();
        Assert.assertThat("Bob must have one task", bobTask, CoreMatchers.is(CoreMatchers.notNullValue()));
        Task davidTask = taskService.createTaskQuery().taskCandidateUser("david").singleResult();
        Assert.assertThat("David must have one task", davidTask, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testTheCandidateUserOfTheFirstTasksIsChangedMultipleTimes() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode processInfo = dynamicBpmnService.changeUserTaskCandidateUser(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "bob", false);
        dynamicBpmnService.changeUserTaskCandidateUser(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "david", false, processInfo);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, processInfo);
        ArrayNode bpmnModelCandidateUsers = processEngineConfiguration.getObjectMapper().createArrayNode();
        bpmnModelCandidateUsers.add("david");
        ArrayNode dynamicCandidateUsers = processEngineConfiguration.getObjectMapper().createArrayNode();
        dynamicCandidateUsers.add("bob");
        dynamicCandidateUsers.add("david");
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        JsonNode taskOneNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_USERS).get(BPMN_MODEL_VALUE))), CoreMatchers.is(bpmnModelCandidateUsers));
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_USERS).get(DYNAMIC_VALUE))), CoreMatchers.is(dynamicCandidateUsers));
        // verify if runtime is up to date
        runtimeService.startProcessInstanceById(processDefinitionId);
        Task bobTask = taskService.createTaskQuery().taskCandidateUser("bob").singleResult();
        Assert.assertThat("Bob must have one task", bobTask, CoreMatchers.is(CoreMatchers.notNullValue()));
        List<Task> davidTasks = taskService.createTaskQuery().taskCandidateUser("david").list();
        Assert.assertThat("David must have two task", davidTasks.size(), CoreMatchers.is(2));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testTheCandidateGroupOfTheFirstTasksIsChanged() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode processInfo = dynamicBpmnService.changeUserTaskCandidateGroup(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "HR", false);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, processInfo);
        ArrayNode dynamicCandidateGroups = processEngineConfiguration.getObjectMapper().createArrayNode();
        dynamicCandidateGroups.add("HR");
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        JsonNode taskOneNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_GROUPS).get(DYNAMIC_VALUE))), CoreMatchers.is(dynamicCandidateGroups));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testTheCandidateGroupOfTheFirstTasksIsChangedMultipleTimes() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode processInfo = dynamicBpmnService.changeUserTaskCandidateGroup(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "HR", false);
        dynamicBpmnService.changeUserTaskCandidateGroup(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "SALES", false, processInfo);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, processInfo);
        ArrayNode candidateGroups = processEngineConfiguration.getObjectMapper().createArrayNode();
        candidateGroups.add("HR");
        candidateGroups.add("SALES");
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        JsonNode taskOneNode = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(((ArrayNode) (taskOneNode.get(USER_TASK_CANDIDATE_GROUPS).get(DYNAMIC_VALUE))), CoreMatchers.is(candidateGroups));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testTheScriptOfTheScriptTasksIsChanged() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode jsonNodes = dynamicBpmnService.changeScriptTaskScript(DynamicProcessDefinitionSummaryTest.SCRIPT_TASK_SID, "var x = \"hallo\";");
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, jsonNodes);
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        JsonNode scriptTaskNode = summary.getElement(DynamicProcessDefinitionSummaryTest.SCRIPT_TASK_SID).get(ELEMENT_PROPERTIES);
        Assert.assertThat(scriptTaskNode.get(SCRIPT_TASK_SCRIPT).get(DYNAMIC_VALUE).asText(), CoreMatchers.is("var x = \"hallo\";"));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/dynamic/dynamic-bpmn-test-process.bpmn20.xml" })
    public void testItShouldBePossibleToResetDynamicCandidateUsers() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        String processDefinitionId = processInstance.getProcessDefinitionId();
        ObjectNode jsonNodes = dynamicBpmnService.changeUserTaskCandidateUser(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, "bob", false);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, jsonNodes);
        // delete
        jsonNodes = dynamicBpmnService.getProcessDefinitionInfo(processDefinitionId);
        dynamicBpmnService.resetProperty(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID, USER_TASK_CANDIDATE_USERS, jsonNodes);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, jsonNodes);
        runtimeService.startProcessInstanceByKey("dynamicServiceTest");
        long count = taskService.createTaskQuery().taskCandidateUser("david").count();
        Assert.assertThat(count, CoreMatchers.is(2L));
        // additional checks of summary
        ArrayNode candidateUsersNode = processEngineConfiguration.getObjectMapper().createArrayNode();
        candidateUsersNode.add("david");
        DynamicProcessDefinitionSummary summary = dynamicBpmnService.getDynamicProcessDefinitionSummary(processDefinitionId);
        JsonNode candidateUsers = summary.getElement(DynamicProcessDefinitionSummaryTest.TASK_ONE_SID).get(ELEMENT_PROPERTIES).get(USER_TASK_CANDIDATE_USERS);
        Assert.assertThat(((ArrayNode) (candidateUsers.get(BPMN_MODEL_VALUE))), CoreMatchers.is(candidateUsersNode));
        Assert.assertThat(candidateUsers.get(DYNAMIC_VALUE), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

