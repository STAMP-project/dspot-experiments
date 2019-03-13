package org.activiti.spring.conformance.set2;


import BPMNActivityEvent.ActivityEvents.ACTIVITY_COMPLETED;
import BPMNActivityEvent.ActivityEvents.ACTIVITY_STARTED;
import BPMNSequenceFlowTakenEvent.SequenceFlowEvents.SEQUENCE_FLOW_TAKEN;
import ProcessInstance.ProcessInstanceStatus.RUNNING;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_COMPLETED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_CREATED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_STARTED;
import SpringBootTest.WebEnvironment;
import Task.TaskStatus.ASSIGNED;
import Task.TaskStatus.COMPLETED;
import Task.TaskStatus.CREATED;
import TaskRuntimeEvent.TaskEvents.TASK_ASSIGNED;
import TaskRuntimeEvent.TaskEvents.TASK_COMPLETED;
import TaskRuntimeEvent.TaskEvents.TASK_CREATED;
import TaskRuntimeEvent.TaskEvents.TASK_UPDATED;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.NotFoundException;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.spring.conformance.util.security.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UserTaskCandidateGroupRuntimeTest {
    private final String processKey = "usertaskwi-75883efa-38c6-4c17-9da4-161234523fbf";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessAdminRuntime processAdminRuntime;

    @Test
    public void shouldCreateClaimAndCompleteTask() {
        securityUtil.logInAs("user1");
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processKey).withBusinessKey("my-business-key").withName("my-process-instance-name").build());
        // then
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getStatus()).isEqualTo(RUNNING);
        assertThat(processInstance.getBusinessKey()).isEqualTo("my-business-key");
        assertThat(processInstance.getName()).isEqualTo("my-process-instance-name");
        // I should be able to get the process instance from the Runtime because it is still running
        ProcessInstance processInstanceById = processRuntime.processInstance(processInstance.getId());
        assertThat(processInstanceById).isEqualTo(processInstance);
        // I should get a task for User1
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        Task task = tasks.getContent().get(0);
        Task taskById = taskRuntime.task(task.getId());
        assertThat(taskById.getStatus()).isEqualTo(CREATED);
        assertThat(task).isEqualTo(taskById);
        assertThat(task.getAssignee()).isNull();
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, TASK_CREATED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // Check with user2
        securityUtil.logInAs("user2");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(0);
        Throwable throwable = catchThrowable(() -> taskRuntime.task(task.getId()));
        assertThat(throwable).isInstanceOf(NotFoundException.class);
        // Check with user3
        securityUtil.logInAs("user3");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        // Try to complete without claim should error
        securityUtil.logInAs("user1");
        throwable = catchThrowable(() -> taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build()));
        assertThat(throwable).isInstanceOf(IllegalStateException.class);
        // Claim should be allowed
        Task claimedTask = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        assertThat(claimedTask.getStatus()).isEqualTo(ASSIGNED);
        assertThat(claimedTask.getAssignee()).isEqualTo("user1");
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_ASSIGNED, TASK_UPDATED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // complete task now should work
        Task completedTask = taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(claimedTask.getId()).build());
        assertThat(completedTask.getStatus()).isEqualTo(COMPLETED);
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_COMPLETED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, ACTIVITY_COMPLETED, PROCESS_COMPLETED);
    }

    @Test
    public void shouldCreateClaimAndReleaseTask() {
        securityUtil.logInAs("user1");
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processKey).withBusinessKey("my-business-key").withName("my-process-instance-name").build());
        // then
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getStatus()).isEqualTo(RUNNING);
        // I should get a task for User1
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        Task task = tasks.getContent().get(0);
        Task taskById = taskRuntime.task(task.getId());
        assertThat(taskById.getStatus()).isEqualTo(CREATED);
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, TASK_CREATED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // Check with user3
        securityUtil.logInAs("user3");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        // Claim with User 1
        securityUtil.logInAs("user1");
        Task claimedTask = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        assertThat(claimedTask.getStatus()).isEqualTo(ASSIGNED);
        assertThat(claimedTask.getAssignee()).isEqualTo("user1");
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_ASSIGNED, TASK_UPDATED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // Check with user3, he/she shouldn't see any task now that the task was assigned
        securityUtil.logInAs("user3");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(0);
        // Release with User 1
        securityUtil.logInAs("user1");
        Task releasedTask = taskRuntime.release(TaskPayloadBuilder.release().withTaskId(task.getId()).build());
        assertThat(releasedTask.getStatus()).isEqualTo(CREATED);
        assertThat(releasedTask.getAssignee()).isNull();
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_ASSIGNED, TASK_UPDATED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // User 1 should be able to see the task to claim now
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        // User 3 also should be able to see the task now
        // Check with user3
        securityUtil.logInAs("user3");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
    }
}

