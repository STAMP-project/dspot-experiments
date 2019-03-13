/**
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.runtime.api.model.impl;


import java.util.Date;
import org.activiti.api.task.model.Task;
import org.junit.Test;
import org.mockito.InjectMocks;


public class APITaskConverterTest {
    @InjectMocks
    private APITaskConverter taskConverter;

    @Test
    public void convertFromTaskShouldSetAllFieldsInTheConvertedTask() {
        // WHEN
        Date now = new Date();
        Task convertedTask = taskConverter.from(MockTaskBuilder.taskBuilder().withId("testTaskId").withAssignee("testUser").withName("testTaskName").withDescription("testTaskDescription").withCreatedDate(now).withClaimedDate(now).withDueDate(now).withPriority(112).withProcessDefinitionId("testProcessDefinitionId").withProcessInstanceId("testProcessInstanceId").withParentTaskId("testParentTaskId").withFormKey("testFormKey").build());
        // THEN
        assertThat(convertedTask).isNotNull().extracting(Task::getId, Task::getAssignee, Task::getName, Task::getDescription, Task::getCreatedDate, Task::getClaimedDate, Task::getDueDate, Task::getPriority, Task::getProcessDefinitionId, Task::getProcessInstanceId, Task::getParentTaskId, Task::getFormKey, Task::getStatus).containsExactly("testTaskId", "testUser", "testTaskName", "testTaskDescription", now, now, now, 112, "testProcessDefinitionId", "testProcessInstanceId", "testParentTaskId", "testFormKey", ASSIGNED);
    }

    @Test
    public void calculateStatusForACancelledTaskShouldReturnCancelled() {
        assertThat(taskConverter.from(MockTaskBuilder.taskEntityBuilder().withCancelled(true).build())).isNotNull().extracting(Task::getStatus).isEqualTo(CANCELLED);
    }

    @Test
    public void calculateStatusForASuspendedTaskShouldReturnSuspended() {
        assertThat(taskConverter.from(MockTaskBuilder.taskEntityBuilder().withSuspended(true).build())).isNotNull().extracting(Task::getStatus).isEqualTo(SUSPENDED);
    }

    @Test
    public void calculateStatusForAnAssignedTaskShouldReturnAssigned() {
        assertThat(taskConverter.from(MockTaskBuilder.taskBuilder().withAssignee("testUser").build())).isNotNull().extracting(Task::getStatus).isEqualTo(ASSIGNED);
    }

    @Test
    public void calculateStatusCreatedAndNotAssignedTaskShouldReturnCreated() {
        assertThat(taskConverter.from(MockTaskBuilder.taskBuilder().build())).isNotNull().extracting(Task::getStatus).isEqualTo(CREATED);
    }
}

