package org.activiti.spring.boot.tasks;


import SpringBootTest.WebEnvironment;
import Task.TaskStatus.CREATED;
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
public class TaskRuntimeTaskForOtherTest {
    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskCleanUpUtil taskCleanUpUtil;

    @Test
    public void createStandaloneTaskWithNoCandidates() {
        securityUtil.logInAs("garth");
        Task standAloneTask = taskRuntime.create(TaskPayloadBuilder.create().withName("task with no candidates besides owner").build());
        // the owner should be able to see the created task
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(1);
        Task task = tasks.getContent().get(0);
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getStatus()).isEqualTo(CREATED);
        securityUtil.logInAs("salaboy");
        // Other users beside the owner shouldn't see the task
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getContent()).hasSize(0);
    }
}

