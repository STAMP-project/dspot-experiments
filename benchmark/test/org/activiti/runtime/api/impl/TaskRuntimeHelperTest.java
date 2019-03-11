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
package org.activiti.runtime.api.impl;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.activiti.api.runtime.shared.NotFoundException;
import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.impl.TaskImpl;
import org.activiti.api.task.model.payloads.UpdateTaskPayload;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TaskRuntimeHelperTest {
    private static final String AUTHENTICATED_USER = "user";

    private TaskRuntimeHelper taskRuntimeHelper;

    @Mock
    private SecurityManager securityManager;

    @Mock
    private UserGroupManager userGroupManager;

    @Mock
    private TaskService taskService;

    @Mock
    private APITaskConverter taskConverter;

    @Test
    public void updateShouldSetAllFieldsAndSaveChangesWhenAssignee() {
        // given
        Date now = new Date();
        UpdateTaskPayload updateTaskPayload = TaskPayloadBuilder.update().withTaskId("taskId").withDescription("new description").withName("New name").withPriority(42).withDueDate(now).withFormKey("new form key").build();
        Task internalTask = buildInternalTask(TaskRuntimeHelperTest.AUTHENTICATED_USER);
        Mockito.doReturn(internalTask).when(taskRuntimeHelper).getInternalTaskWithChecks("taskId");
        Mockito.doReturn(internalTask).when(taskRuntimeHelper).getInternalTask("taskId");
        // when
        taskRuntimeHelper.applyUpdateTaskPayload(false, updateTaskPayload);
        // then
        Mockito.verify(internalTask).setDescription("new description");
        Mockito.verify(internalTask).setName("New name");
        Mockito.verify(internalTask).setPriority(42);
        Mockito.verify(internalTask).setDueDate(now);
        Mockito.verify(internalTask).setFormKey("new form key");
        Mockito.verify(taskService).saveTask(internalTask);
    }

    @Test
    public void applyUpdateTaskPayloadShouldThrowExceptionWhenAssigneeIsNotSetAndIsNotAdmin() {
        // given
        TaskQuery taskQuery = Mockito.mock(TaskQuery.class);
        BDDMockito.given(taskService.createTaskQuery()).willReturn(taskQuery);
        BDDMockito.given(taskQuery.taskCandidateOrAssigned(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(taskQuery);
        BDDMockito.given(taskQuery.taskId("taskId")).willReturn(taskQuery);
        Task internalTask = Mockito.mock(Task.class);
        Mockito.doReturn(internalTask).when(taskRuntimeHelper).getInternalTaskWithChecks("taskId");
        UpdateTaskPayload updateTaskPayload = TaskPayloadBuilder.update().withTaskId("taskId").withDescription("new description").build();
        // when
        Throwable throwable = catchThrowable(() -> taskRuntimeHelper.applyUpdateTaskPayload(false, updateTaskPayload));
        // then
        assertThat(throwable).isInstanceOf(IllegalStateException.class).hasMessage("You cannot update a task where you are not the assignee");
    }

    @Test
    public void updateShouldBeAbleToUpdateDescriptionOnly() {
        // given
        UpdateTaskPayload updateTaskPayload = TaskPayloadBuilder.update().withTaskId("taskId").withDescription("new description").build();
        TaskImpl task = new TaskImpl();
        String assignee = TaskRuntimeHelperTest.AUTHENTICATED_USER;
        task.setAssignee(assignee);
        Task internalTask = buildInternalTask(assignee);
        Mockito.doReturn(internalTask).when(taskRuntimeHelper).getInternalTaskWithChecks("taskId");
        Mockito.doReturn(internalTask).when(taskRuntimeHelper).getInternalTask("taskId");
        TaskQuery taskQuery = Mockito.mock(TaskQuery.class);
        BDDMockito.given(taskQuery.taskId("taskId")).willReturn(taskQuery);
        BDDMockito.given(taskService.createTaskQuery()).willReturn(taskQuery);
        TaskRuntimeHelper taskUpdater = Mockito.mock(TaskRuntimeHelper.class);
        BDDMockito.given(taskQuery.singleResult()).willReturn(internalTask);
        Mockito.when(taskUpdater.getInternalTaskWithChecks(ArgumentMatchers.any())).thenReturn(internalTask);
        Mockito.when(taskConverter.from(ArgumentMatchers.any(Task.class))).thenReturn(task);
        // when
        taskRuntimeHelper.applyUpdateTaskPayload(false, updateTaskPayload);
        // then
        Mockito.verify(internalTask).getDescription();
        Mockito.verify(internalTask).setDescription("new description");
        Mockito.verify(taskService).saveTask(internalTask);
    }

    @Test
    public void getInternalTaskWithChecksShouldReturnMatchinTaskFromTaskQuery() {
        // given
        List<String> groups = Collections.singletonList("doctor");
        BDDMockito.given(userGroupManager.getUserGroups(TaskRuntimeHelperTest.AUTHENTICATED_USER)).willReturn(groups);
        TaskQuery taskQuery = Mockito.mock(TaskQuery.class);
        BDDMockito.given(taskQuery.taskCandidateOrAssigned(TaskRuntimeHelperTest.AUTHENTICATED_USER, groups)).willReturn(taskQuery);
        BDDMockito.given(taskQuery.taskId("taskId")).willReturn(taskQuery);
        Task internalTask = Mockito.mock(Task.class);
        BDDMockito.given(taskQuery.singleResult()).willReturn(internalTask);
        BDDMockito.given(taskService.createTaskQuery()).willReturn(taskQuery);
        // when
        Task retrievedTask = taskRuntimeHelper.getInternalTaskWithChecks("taskId");
        // then
        assertThat(retrievedTask).isEqualTo(internalTask);
    }

    @Test
    public void getInternalTaskWithChecksShouldThrowNotFoundExceptionWhenNoTaskIsFound() {
        // given
        List<String> groups = Collections.singletonList("doctor");
        BDDMockito.given(userGroupManager.getUserGroups(TaskRuntimeHelperTest.AUTHENTICATED_USER)).willReturn(groups);
        TaskQuery taskQuery = Mockito.mock(TaskQuery.class);
        BDDMockito.given(taskQuery.taskCandidateOrAssigned(TaskRuntimeHelperTest.AUTHENTICATED_USER, groups)).willReturn(taskQuery);
        BDDMockito.given(taskQuery.taskId("taskId")).willReturn(taskQuery);
        BDDMockito.given(taskQuery.singleResult()).willReturn(null);
        BDDMockito.given(taskService.createTaskQuery()).willReturn(taskQuery);
        // when
        Throwable thrown = catchThrowable(() -> taskRuntimeHelper.getInternalTaskWithChecks("taskId"));
        // then
        assertThat(thrown).isInstanceOf(NotFoundException.class).hasMessageStartingWith("Unable to find task for the given id:");
    }

    @Test
    public void getInternalTaskWithChecksShouldThrowExceptionIfAuthenticatedUserIsNotSet() {
        // given
        BDDMockito.given(getAuthenticatedUserId()).willReturn(null);
        // when
        Throwable thrown = catchThrowable(() -> taskRuntimeHelper.getInternalTaskWithChecks("taskId"));
        // then
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("There is no authenticated user, we need a user authenticated to find tasks");
    }
}

