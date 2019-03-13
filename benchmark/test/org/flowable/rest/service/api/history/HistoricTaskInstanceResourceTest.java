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
package org.flowable.rest.service.api.history;


import DelegationState.RESOLVED;
import HistoryLevel.AUDIT;
import RestUrls.URL_HISTORIC_PROCESS_INSTANCE;
import RestUrls.URL_HISTORIC_TASK_INSTANCE;
import RestUrls.URL_HISTORIC_TASK_INSTANCE_FORM;
import RestUrls.URL_PROCESS_DEFINITION;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormDeployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for all REST-operations related to a single Historic task instance resource.
 */
public class HistoricTaskInstanceResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting a single task, spawned by a process. GET history/historic-task-instances/{taskId}
     */
    @Test
    @Deployment
    public void testGetProcessTask() throws Exception {
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            Calendar now = Calendar.getInstance();
            processEngineConfiguration.getClock().setCurrentTime(now.getTime());
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            taskService.setDueDate(task.getId(), now.getTime());
            taskService.setOwner(task.getId(), "owner");
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            String url = buildUrl(URL_HISTORIC_TASK_INSTANCE, task.getId());
            CloseableHttpResponse response = executeRequest(new HttpGet(url), HttpStatus.SC_OK);
            // Check resulting task
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertEquals(task.getId(), responseNode.get("id").asText());
            Assert.assertEquals(task.getAssignee(), responseNode.get("assignee").asText());
            Assert.assertEquals(task.getOwner(), responseNode.get("owner").asText());
            Assert.assertEquals(task.getFormKey(), responseNode.get("formKey").asText());
            Assert.assertEquals(task.getExecutionId(), responseNode.get("executionId").asText());
            Assert.assertEquals(task.getDescription(), responseNode.get("description").asText());
            Assert.assertEquals(task.getName(), responseNode.get("name").asText());
            Assert.assertEquals(task.getDueDate(), getDateFromISOString(responseNode.get("dueDate").asText()));
            Assert.assertEquals(task.getCreateTime(), getDateFromISOString(responseNode.get("startTime").asText()));
            Assert.assertEquals(task.getPriority(), responseNode.get("priority").asInt());
            Assert.assertTrue(responseNode.get("endTime").isNull());
            Assert.assertTrue(responseNode.get("parentTaskId").isNull());
            Assert.assertEquals("", responseNode.get("tenantId").textValue());
            Assert.assertEquals(buildUrl(URL_HISTORIC_PROCESS_INSTANCE, task.getProcessInstanceId()), responseNode.get("processInstanceUrl").asText());
            Assert.assertEquals(buildUrl(URL_PROCESS_DEFINITION, task.getProcessDefinitionId()), responseNode.get("processDefinitionUrl").asText());
            Assert.assertEquals(responseNode.get("url").asText(), url);
        }
    }

    /**
     * Test getting a single task, created using the API. GET history/historic-task-instances/{taskId}
     */
    @Test
    public void testGetProcessAdhoc() throws Exception {
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            try {
                Calendar now = Calendar.getInstance();
                processEngineConfiguration.getClock().setCurrentTime(now.getTime());
                Task parentTask = taskService.newTask();
                taskService.saveTask(parentTask);
                Task task = taskService.newTask();
                task.setParentTaskId(parentTask.getId());
                task.setName("Task name");
                task.setDescription("Descriptions");
                task.setAssignee("kermit");
                task.setDelegationState(RESOLVED);
                task.setDescription("Description");
                task.setDueDate(now.getTime());
                task.setOwner("owner");
                task.setPriority(20);
                taskService.saveTask(task);
                String url = buildUrl(URL_HISTORIC_TASK_INSTANCE, task.getId());
                CloseableHttpResponse response = executeRequest(new HttpGet(url), HttpStatus.SC_OK);
                // Check resulting task
                JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
                closeResponse(response);
                Assert.assertEquals(task.getId(), responseNode.get("id").asText());
                Assert.assertEquals(task.getAssignee(), responseNode.get("assignee").asText());
                Assert.assertEquals(task.getOwner(), responseNode.get("owner").asText());
                Assert.assertEquals(task.getDescription(), responseNode.get("description").asText());
                Assert.assertEquals(task.getName(), responseNode.get("name").asText());
                Assert.assertEquals(task.getDueDate(), getDateFromISOString(responseNode.get("dueDate").asText()));
                Assert.assertEquals(task.getCreateTime(), getDateFromISOString(responseNode.get("startTime").asText()));
                Assert.assertEquals(task.getPriority(), responseNode.get("priority").asInt());
                Assert.assertEquals(task.getParentTaskId(), responseNode.get("parentTaskId").asText());
                Assert.assertTrue(responseNode.get("executionId").isNull());
                Assert.assertTrue(responseNode.get("processInstanceId").isNull());
                Assert.assertTrue(responseNode.get("processDefinitionId").isNull());
                Assert.assertEquals("", responseNode.get("tenantId").textValue());
                Assert.assertEquals(responseNode.get("url").asText(), url);
            } finally {
                // Clean adhoc-tasks even if test fails
                List<Task> tasks = taskService.createTaskQuery().list();
                for (Task task : tasks) {
                    taskService.deleteTask(task.getId(), true);
                }
            }
        }
    }

    /**
     * Test deleting a single task. DELETE history/historic-task-instances/{taskId}
     */
    @Test
    public void testDeleteTask() throws Exception {
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            try {
                // 1. Simple delete
                Task task = taskService.newTask();
                taskService.saveTask(task);
                String taskId = task.getId();
                // Execute the request
                HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_HISTORIC_TASK_INSTANCE, taskId))));
                closeResponse(executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT));
                Assert.assertNull(historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult());
            } finally {
                // Clean adhoc-tasks even if test fails
                List<Task> tasks = taskService.createTaskQuery().list();
                for (Task task : tasks) {
                    taskService.deleteTask(task.getId(), true);
                }
                // Clean historic tasks with no runtime-counterpart
                List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery().list();
                for (HistoricTaskInstance task : historicTasks) {
                    historyService.deleteHistoricTaskInstance(task.getId());
                }
            }
        }
    }

    /**
     * Test deleting a task that is part of a process. DELETE history/historic-task-instances/{taskId}
     */
    @Test
    @Deployment
    public void testDeleteTaskInProcess() throws Exception {
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_HISTORIC_TASK_INSTANCE, task.getId()))));
            closeResponse(executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT));
            Assert.assertNull(historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult());
        }
    }

    @Test
    @Deployment
    public void testCompletedTaskForm() throws Exception {
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("oneTaskProcess").singleResult();
            try {
                formRepositoryService.createDeployment().addClasspathResource("org/flowable/rest/service/api/runtime/simple.form").deploy();
                FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
                Assert.assertNotNull(formDefinition);
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
                Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
                String taskId = task.getId();
                String url = RestUrls.createRelativeResourceUrl(URL_HISTORIC_TASK_INSTANCE_FORM, taskId);
                CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + url)), HttpStatus.SC_OK);
                JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
                closeResponse(response);
                Assert.assertEquals(formDefinition.getId(), responseNode.get("id").asText());
                Assert.assertEquals(formDefinition.getKey(), responseNode.get("key").asText());
                Assert.assertEquals(formDefinition.getName(), responseNode.get("name").asText());
                Assert.assertEquals(2, responseNode.get("fields").size());
                Map<String, Object> variables = new HashMap<>();
                variables.put("user", "First value");
                variables.put("number", 789);
                taskService.completeTaskWithForm(taskId, formDefinition.getId(), null, variables);
                Assert.assertNull(taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult());
                response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + url)), HttpStatus.SC_OK);
                responseNode = objectMapper.readTree(response.getEntity().getContent());
                closeResponse(response);
                Assert.assertEquals(formDefinition.getId(), responseNode.get("id").asText());
                Assert.assertEquals(formDefinition.getKey(), responseNode.get("key").asText());
                Assert.assertEquals(formDefinition.getName(), responseNode.get("name").asText());
                Assert.assertEquals(2, responseNode.get("fields").size());
                JsonNode fieldNode = responseNode.get("fields").get(0);
                Assert.assertEquals("user", fieldNode.get("id").asText());
                Assert.assertEquals("First value", fieldNode.get("value").asText());
                fieldNode = responseNode.get("fields").get(1);
                Assert.assertEquals("number", fieldNode.get("id").asText());
                Assert.assertEquals(789, fieldNode.get("value").asInt());
            } finally {
                formEngineFormService.deleteFormInstancesByProcessDefinition(processDefinition.getId());
                List<FormDeployment> formDeployments = formRepositoryService.createDeploymentQuery().list();
                for (FormDeployment formDeployment : formDeployments) {
                    formRepositoryService.deleteDeployment(formDeployment.getId());
                }
            }
        }
    }
}

