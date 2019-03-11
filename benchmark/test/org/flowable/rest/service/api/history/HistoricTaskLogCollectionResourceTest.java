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


import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luis Belloch
 */
public class HistoricTaskLogCollectionResourceTest extends BaseSpringRestTestCase {
    @Test
    @Deployment(resources = { "org/flowable/rest/api/history/HistoricTaskLogCollectionResourceTest.bpmn20.xml" })
    public void itCanQueryByTaskId() throws Exception {
        Calendar startTime = Calendar.getInstance();
        ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).singleResult();
        taskService.setVariableLocal(task1.getId(), "local", "foo");
        taskService.setOwner(task1.getId(), "test");
        taskService.setDueDate(task1.getId(), startTime.getTime());
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
        taskService.setOwner(task2.getId(), "test");
        JsonNode list = queryTaskLogEntries((("?taskId=" + (encode(task1.getId()))) + "&sort=logNumber&order=asc"));
        expectSequence(list, Arrays.asList(task1.getId(), task1.getId(), task1.getId()), Arrays.asList("USER_TASK_CREATED", "USER_TASK_OWNER_CHANGED", "USER_TASK_DUEDATE_CHANGED"));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/history/HistoricTaskLogCollectionResourceTest.bpmn20.xml" })
    public void whenTaskIdDoesNotExistItReturnsEmptyList() throws Exception {
        JsonNode list = queryTaskLogEntries("?taskId=FOOBAR_4242");
        Assert.assertEquals(0, list.size());
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/history/HistoricTaskLogCollectionResourceTest.bpmn20.xml" })
    public void itCanQueryByProcessInstanceId() throws Exception {
        ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).singleResult();
        taskService.complete(task1.getId());
        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).singleResult();
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task3 = taskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
        JsonNode list1 = queryTaskLogEntries((("?processInstanceId=" + (encode(processInstance1.getId()))) + "&sort=logNumber&order=asc"));
        expectSequence(list1, Arrays.asList(task1.getId(), task1.getId(), task2.getId()), Arrays.asList("USER_TASK_CREATED", "USER_TASK_COMPLETED", "USER_TASK_CREATED"));
        JsonNode list2 = queryTaskLogEntries((("?processInstanceId=" + (encode(processInstance2.getId()))) + "&sort=logNumber&order=asc"));
        expectSequence(list2, Arrays.asList(task3.getId()), Arrays.asList("USER_TASK_CREATED"));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/history/HistoricTaskLogCollectionResourceTest.bpmn20.xml" })
    public void itCanQueryUsingFromToDates() throws IOException {
        Calendar startTime = Calendar.getInstance();
        processEngineConfiguration.getClock().setCurrentTime(startTime.getTime());
        ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).singleResult();
        taskService.setDueDate(task1.getId(), startTime.getTime());
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", "testBusinessKey");
        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
        taskService.setOwner(task2.getId(), "test");
        startTime.add(Calendar.DAY_OF_YEAR, 2);
        processEngineConfiguration.getClock().setCurrentTime(startTime.getTime());
        taskService.setOwner(task1.getId(), "test");
        processEngineConfiguration.getClock().reset();
        JsonNode listAfter = queryTaskLogEntries(((("?taskId=" + (encode(task1.getId()))) + "&sort=logNumber&order=asc&from=") + (dateFormat.format(startTime.getTime()))));
        expectSequence(listAfter, Arrays.asList(task1.getId()), Arrays.asList("USER_TASK_OWNER_CHANGED"));
        startTime.add(Calendar.DAY_OF_YEAR, (-1));
        JsonNode listBefore = queryTaskLogEntries(((("?taskId=" + (encode(task1.getId()))) + "&sort=logNumber&order=asc&to=") + (dateFormat.format(startTime.getTime()))));
        expectSequence(listBefore, Arrays.asList(task1.getId(), task1.getId()), Arrays.asList("USER_TASK_CREATED", "USER_TASK_DUEDATE_CHANGED"));
    }
}

