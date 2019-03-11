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
package org.flowable.engine.test.api.task;


import HistoryLevel.ACTIVITY;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class TaskAndVariablesQueryTest extends PluggableFlowableTestCase {
    private List<String> taskIds;

    private List<String> multipleTaskIds;

    @Test
    @Deployment
    public void testQuery() {
        Task task = taskService.createTaskQuery().includeTaskLocalVariables().taskAssignee("gonzo").singleResult();
        Map<String, Object> variableMap = task.getTaskLocalVariables();
        assertEquals(3, variableMap.size());
        assertEquals(0, task.getProcessVariables().size());
        assertNotNull(variableMap.get("testVar"));
        assertEquals("someVariable", variableMap.get("testVar"));
        assertNotNull(variableMap.get("testVar2"));
        assertEquals(123, variableMap.get("testVar2"));
        assertNotNull(variableMap.get("testVarBinary"));
        assertEquals("This is a binary variable", new String(((byte[]) (variableMap.get("testVarBinary")))));
        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(3, tasks.size());
        task = taskService.createTaskQuery().includeProcessVariables().taskAssignee("gonzo").singleResult();
        assertEquals(0, task.getProcessVariables().size());
        assertEquals(0, task.getTaskLocalVariables().size());
        Map<String, Object> startMap = new HashMap<>();
        startMap.put("processVar", true);
        startMap.put("binaryVariable", "This is a binary process variable".getBytes());
        runtimeService.startProcessInstanceByKey("oneTaskProcess", startMap);
        task = taskService.createTaskQuery().includeProcessVariables().taskAssignee("kermit").singleResult();
        assertEquals(2, task.getProcessVariables().size());
        assertEquals(0, task.getTaskLocalVariables().size());
        assertTrue(((Boolean) (task.getProcessVariables().get("processVar"))));
        assertEquals("This is a binary process variable", new String(((byte[]) (task.getProcessVariables().get("binaryVariable")))));
        taskService.setVariable(task.getId(), "anotherProcessVar", 123);
        taskService.setVariableLocal(task.getId(), "localVar", "test");
        task = taskService.createTaskQuery().includeTaskLocalVariables().taskAssignee("kermit").singleResult();
        assertEquals(0, task.getProcessVariables().size());
        assertEquals(1, task.getTaskLocalVariables().size());
        assertEquals("test", task.getTaskLocalVariables().get("localVar"));
        task = taskService.createTaskQuery().includeProcessVariables().taskAssignee("kermit").singleResult();
        assertEquals(3, task.getProcessVariables().size());
        assertEquals(0, task.getTaskLocalVariables().size());
        assertEquals(true, task.getProcessVariables().get("processVar"));
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        assertEquals("This is a binary process variable", new String(((byte[]) (task.getProcessVariables().get("binaryVariable")))));
        tasks = taskService.createTaskQuery().includeTaskLocalVariables().taskCandidateUser("kermit").list();
        assertEquals(2, tasks.size());
        assertEquals(2, tasks.get(0).getTaskLocalVariables().size());
        assertEquals("test", tasks.get(0).getTaskLocalVariables().get("test"));
        assertEquals(0, tasks.get(0).getProcessVariables().size());
        tasks = taskService.createTaskQuery().includeProcessVariables().taskCandidateUser("kermit").list();
        assertEquals(2, tasks.size());
        assertEquals(0, tasks.get(0).getProcessVariables().size());
        assertEquals(0, tasks.get(0).getTaskLocalVariables().size());
        task = taskService.createTaskQuery().includeTaskLocalVariables().taskAssignee("kermit").taskVariableValueEquals("localVar", "test").singleResult();
        assertEquals(0, task.getProcessVariables().size());
        assertEquals(1, task.getTaskLocalVariables().size());
        assertEquals("test", task.getTaskLocalVariables().get("localVar"));
        task = taskService.createTaskQuery().includeProcessVariables().taskAssignee("kermit").taskVariableValueEquals("localVar", "test").singleResult();
        assertEquals(3, task.getProcessVariables().size());
        assertEquals(0, task.getTaskLocalVariables().size());
        assertEquals(true, task.getProcessVariables().get("processVar"));
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        task = taskService.createTaskQuery().includeTaskLocalVariables().includeProcessVariables().taskAssignee("kermit").singleResult();
        assertEquals(3, task.getProcessVariables().size());
        assertEquals(1, task.getTaskLocalVariables().size());
        assertEquals("test", task.getTaskLocalVariables().get("localVar"));
        assertEquals(true, task.getProcessVariables().get("processVar"));
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        assertEquals("This is a binary process variable", new String(((byte[]) (task.getProcessVariables().get("binaryVariable")))));
    }

    @Test
    @Deployment
    public void testVariableExistsQuery() {
        Map<String, Object> startMap = new HashMap<>();
        startMap.put("processVar", true);
        startMap.put("binaryVariable", "This is a binary process variable".getBytes());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", startMap);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).processVariableExists("processVar").singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).processVariableNotExists("processVar").singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().or().processVariableExists("processVar").processVariableExists("test").endOr().singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskVariableExists("processVar").singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskVariableNotExists("processVar").singleResult();
        assertNotNull(task);
        taskService.setVariable(task.getId(), "anotherProcessVar", 123);
        taskService.setVariableLocal(task.getId(), "localVar", "test");
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskVariableExists("localVar").singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskVariableNotExists("localVar").singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).or().processVariableExists("processVar").processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).or().processVariableNotExists("processVar").processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).or().processVariableExists("processVar").endOr().or().processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertNotNull(task);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).or().processVariableNotExists("processVar").endOr().or().processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertNull(task);
    }

    @Test
    public void testQueryWithPagingAndVariables() {
        List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().orderByTaskPriority().desc().listPage(0, 1);
        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        Map<String, Object> variableMap = task.getTaskLocalVariables();
        assertEquals(3, variableMap.size());
        assertEquals("someVariable", variableMap.get("testVar"));
        assertEquals(123, variableMap.get("testVar2"));
        assertEquals("This is a binary variable", new String(((byte[]) (variableMap.get("testVarBinary")))));
        tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().orderByTaskPriority().asc().listPage(1, 2);
        assertEquals(2, tasks.size());
        task = tasks.get(1);
        variableMap = task.getTaskLocalVariables();
        assertEquals(3, variableMap.size());
        assertEquals("someVariable", variableMap.get("testVar"));
        assertEquals(123, variableMap.get("testVar2"));
        assertEquals("This is a binary variable", new String(((byte[]) (variableMap.get("testVarBinary")))));
        tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().orderByTaskPriority().asc().listPage(2, 4);
        assertEquals(1, tasks.size());
        task = tasks.get(0);
        variableMap = task.getTaskLocalVariables();
        assertEquals(3, variableMap.size());
        assertEquals("someVariable", variableMap.get("testVar"));
        assertEquals(123, variableMap.get("testVar2"));
        assertEquals("This is a binary variable", new String(((byte[]) (variableMap.get("testVarBinary")))));
        tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().orderByTaskPriority().asc().listPage(4, 2);
        assertEquals(0, tasks.size());
    }

    // Unit test for https://activiti.atlassian.net/browse/ACT-4152
    @Test
    public void testQueryWithIncludeTaskVariableAndTaskCategory() {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("gonzo").list();
        for (Task task : tasks) {
            assertNotNull(task.getCategory());
            assertEquals("testCategory", task.getCategory());
        }
        tasks = taskService.createTaskQuery().taskAssignee("gonzo").includeTaskLocalVariables().list();
        for (Task task : tasks) {
            assertNotNull(task.getCategory());
            assertEquals("testCategory", task.getCategory());
        }
        tasks = taskService.createTaskQuery().taskAssignee("gonzo").includeProcessVariables().list();
        for (Task task : tasks) {
            assertNotNull(task.getCategory());
            assertEquals("testCategory", task.getCategory());
        }
    }

    @Test
    public void testQueryWithLimitAndVariables() throws Exception {
        int taskVariablesLimit = 2000;
        int expectedNumberOfTasks = 103;
        try {
            // setup - create 100 tasks
            multipleTaskIds = generateMultipleTestTasks();
            // limit results to 2000 and set maxResults for paging to 200
            // please see MNT-16040
            List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().limitTaskVariables(taskVariablesLimit).orderByTaskPriority().asc().listPage(0, 200);
            // 100 tasks created by generateMultipleTestTasks and 3 created previously at setUp
            assertEquals(expectedNumberOfTasks, tasks.size());
            tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables().orderByTaskPriority().limitTaskVariables(taskVariablesLimit).asc().listPage(50, 100);
            assertEquals(53, tasks.size());
        } finally {
            taskService.deleteTasks(multipleTaskIds, true);
        }
    }

    @Test
    @Deployment
    public void testOrQuery() {
        Map<String, Object> startMap = new HashMap<>();
        startMap.put("anotherProcessVar", 123);
        runtimeService.startProcessInstanceByKey("oneTaskProcess", startMap);
        Task task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("undefined", 999).processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("undefined", 999).endOr().singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("anotherProcessVar", 123).processVariableValueEquals("undefined", 999).endOr().singleResult();
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
        task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("anotherProcessVar", 999).endOr().singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("anotherProcessVar", 999).processVariableValueEquals("anotherProcessVar", 123).endOr().singleResult();
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
    }

    @Test
    @Deployment
    public void testOrQueryMultipleVariableValues() {
        Map<String, Object> startMap = new HashMap<>();
        startMap.put("aProcessVar", 1);
        startMap.put("anotherProcessVar", 123);
        runtimeService.startProcessInstanceByKey("oneTaskProcess", startMap);
        TaskQuery query0 = taskService.createTaskQuery().includeProcessVariables().or();
        for (int i = 0; i < 20; i++) {
            query0 = query0.processVariableValueEquals("anotherProcessVar", i);
        }
        query0 = query0.endOr();
        assertNull(query0.singleResult());
        TaskQuery query1 = taskService.createTaskQuery().includeProcessVariables().or().processVariableValueEquals("anotherProcessVar", 123);
        for (int i = 0; i < 20; i++) {
            query1 = query1.processVariableValueEquals("anotherProcessVar", i);
        }
        query1 = query1.endOr();
        Task task = query1.singleResult();
        assertEquals(2, task.getProcessVariables().size());
        assertEquals(123, task.getProcessVariables().get("anotherProcessVar"));
    }

    @Test
    public void testQueryTaskDefinitionId() {
        Task taskWithDefinitionId = createTaskWithDefinitionId("testTaskId");
        try {
            this.taskService.saveTask(taskWithDefinitionId);
            Task updatedTask = taskService.createTaskQuery().taskDefinitionId("testTaskDefinitionId").singleResult();
            Assert.assertThat(updatedTask.getName(), Is.is("taskWithDefinitionId"));
            Assert.assertThat(updatedTask.getTaskDefinitionId(), Is.is("testTaskDefinitionId"));
            if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
                HistoricTaskInstance updatedHistoricTask = historyService.createHistoricTaskInstanceQuery().taskDefinitionId("testTaskDefinitionId").singleResult();
                Assert.assertThat(updatedHistoricTask.getName(), Is.is("taskWithDefinitionId"));
                Assert.assertThat(updatedHistoricTask.getTaskDefinitionId(), Is.is("testTaskDefinitionId"));
            }
        } finally {
            this.taskService.deleteTask("testTaskId", true);
        }
    }

    @Test
    public void testQueryTaskDefinitionId_multipleResults() {
        Task taskWithDefinitionId1 = createTaskWithDefinitionId("testTaskId1");
        Task taskWithDefinitionId2 = createTaskWithDefinitionId("testTaskId2");
        try {
            this.taskService.saveTask(taskWithDefinitionId1);
            this.taskService.saveTask(taskWithDefinitionId2);
            List<Task> updatedTasks = taskService.createTaskQuery().taskDefinitionId("testTaskDefinitionId").list();
            Assert.assertThat(updatedTasks.size(), Is.is(2));
            if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
                List<HistoricTaskInstance> updatedHistoricTasks = historyService.createHistoricTaskInstanceQuery().taskDefinitionId("testTaskDefinitionId").list();
                Assert.assertThat(updatedHistoricTasks.size(), Is.is(2));
            }
        } finally {
            this.taskService.deleteTask("testTaskId1", true);
            this.taskService.deleteTask("testTaskId2", true);
        }
    }
}

