package org.activiti.spring.boot.tasks;


import SpringBootTest.WebEnvironment;
import Task.TaskStatus.ASSIGNED;
import Task.TaskStatus.CREATED;
import Task.TaskStatus.DELETED;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.spring.boot.security.util.SecurityUtil;
import org.activiti.spring.boot.test.util.TaskCleanUpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TaskRuntimeTaskAssigneeTest {
    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskCleanUpUtil taskCleanUpUtil;

    @Test
    public void aCreateStandaloneTaskForAnotherAssignee() {
        securityUtil.logInAs("garth");
        taskRuntime.create(// but he should still be assigned the task
        TaskPayloadBuilder.create().withName("task for dean").withAssignee("dean").build());
        // the owner should be able to see the created task
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isEqualTo("dean");
        assertThat(task.getStatus()).isEqualTo(ASSIGNED);
        // Now the task should be visible for dean
        securityUtil.logInAs("dean");
        // the target user should be able to see the task as well
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isEqualTo("dean");
        assertThat(task.getStatus()).isEqualTo(ASSIGNED);
        Task deletedTask = taskRuntime.delete(TaskPayloadBuilder.delete().withTaskId(task.getId()).withReason("test clean up").build());
        assertThat(deletedTask).isNotNull();
        assertThat(deletedTask.getStatus()).isEqualTo(DELETED);
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(0);
    }

    @Test
    public void createStandaloneTaskForGroupAndClaim() {
        securityUtil.logInAs("garth");
        Task standAloneTask = taskRuntime.create(TaskPayloadBuilder.create().withName("group task").withCandidateGroup("doctor").build());
        // the owner should be able to see the created task
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getStatus()).isEqualTo(CREATED);
        Task claimedTask = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        assertThat(claimedTask.getAssignee()).isEqualTo("garth");
        assertThat(claimedTask.getStatus()).isEqualTo(ASSIGNED);
    }

    @Test
    public void createStandaloneTaskForGroupAndAdminAssignUser() {
        securityUtil.logInAs("garth");
        taskRuntime.create(TaskPayloadBuilder.create().withName("group task").withCandidateGroup("doctor").build());
        // the owner should be able to see the created task
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getStatus()).isEqualTo(CREATED);
        securityUtil.logInAs("admin");
        Task assignedTask = taskAdminRuntime.assign(TaskPayloadBuilder.assign().withTaskId(task.getId()).withAssignee("garth").build());
        assertThat(assignedTask.getAssignee()).isEqualTo("garth");
        assertThat(assignedTask.getStatus()).isEqualTo(ASSIGNED);
        securityUtil.logInAs("garth");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isEqualTo("garth");
        taskRuntime.delete(TaskPayloadBuilder.delete().withTaskId(task.getId()).withReason("test clean up").build());
    }

    @Test
    public void createStandaloneTaskForUsersAndAdminReassignUser() {
        securityUtil.logInAs("garth");
        taskRuntime.create(TaskPayloadBuilder.create().withName("group task").withCandidateUsers("dean").withCandidateUsers("garth").build());
        // the owner should be able to see the created task
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getStatus()).isEqualTo(CREATED);
        // Check that admin may assign a user to the task without assignee
        securityUtil.logInAs("admin");
        Task assignedTask = taskAdminRuntime.assign(TaskPayloadBuilder.assign().withTaskId(task.getId()).withAssignee("garth").build());
        assertThat(assignedTask.getAssignee()).isEqualTo("garth");
        assertThat(assignedTask.getStatus()).isEqualTo(ASSIGNED);
        // Check that admin may reassign a user to the task when assignee is present
        assignedTask = taskAdminRuntime.assign(TaskPayloadBuilder.assign().withTaskId(task.getId()).withAssignee("dean").build());
        assertThat(assignedTask.getAssignee()).isEqualTo("dean");
        assertThat(assignedTask.getStatus()).isEqualTo(ASSIGNED);
        securityUtil.logInAs("dean");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isEqualTo("dean");
        taskRuntime.delete(TaskPayloadBuilder.delete().withTaskId(task.getId()).withReason("test clean up").build());
    }
}

