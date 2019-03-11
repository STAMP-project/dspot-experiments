package org.activiti.spring.boot.tasks;


import SpringBootTest.WebEnvironment;
import Task.TaskStatus.ASSIGNED;
import Task.TaskStatus.CREATED;
import Task.TaskStatus.DELETED;
import org.activiti.api.runtime.shared.NotFoundException;
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
public class TaskRuntimeDeleteTaskTest {
    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskCleanUpUtil taskCleanUpUtil;

    @Test
    public void createStandaloneTaskAndDelete() {
        securityUtil.logInAs("garth");
        Task standAloneTask = taskRuntime.create(TaskPayloadBuilder.create().withName("simple task").withAssignee("garth").build());
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isEqualTo("garth");
        assertThat(task.getStatus()).isEqualTo(ASSIGNED);
        Task deletedTask = taskRuntime.delete(TaskPayloadBuilder.delete().withTaskId(task.getId()).build());
        assertThat(deletedTask.getStatus()).isEqualTo(DELETED);
    }

    @Test
    public void createStandaloneGroupTaskClaimAndDeleteFail() {
        securityUtil.logInAs("garth");
        Task standAloneTask = taskRuntime.create(TaskPayloadBuilder.create().withName("simple task").withCandidateGroup("activitiTeam").build());
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getStatus()).isEqualTo(CREATED);
        // Claim a task created for a group
        securityUtil.logInAs("salaboy");
        Task claimedTask = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        assertThat(claimedTask.getAssignee()).isEqualTo("salaboy");
        assertThat(claimedTask.getStatus()).isEqualTo(ASSIGNED);
        // Try to delete a task that you cannot see because it was assigned
        securityUtil.logInAs("garth");
        // when
        Throwable throwable = catchThrowable(() -> taskRuntime.delete(TaskPayloadBuilder.delete().withTaskId(task.getId()).build()));
        // then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }
}

