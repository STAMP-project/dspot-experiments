package org.activiti.spring.conformance.set3;


import BPMNActivityEvent.ActivityEvents.ACTIVITY_COMPLETED;
import BPMNActivityEvent.ActivityEvents.ACTIVITY_STARTED;
import BPMNSequenceFlowTakenEvent.SequenceFlowEvents.SEQUENCE_FLOW_TAKEN;
import ProcessInstance.ProcessInstanceStatus.RUNNING;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_CREATED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_STARTED;
import SpringBootTest.WebEnvironment;
import Task.TaskStatus.ASSIGNED;
import Task.TaskStatus.CREATED;
import TaskRuntimeEvent.TaskEvents.TASK_ASSIGNED;
import TaskRuntimeEvent.TaskEvents.TASK_COMPLETED;
import TaskRuntimeEvent.TaskEvents.TASK_CREATED;
import java.util.List;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.activiti.api.process.runtime.ProcessRuntime;
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
public class UserTaskCandidateGroupAndAssigneeTest {
    private final String processKey = "usertaskas-b5300a4b-8950-4486-ba20-a8d775a3d75d";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessAdminRuntime processAdminRuntime;

    @Test
    public void shouldCreateAndCompleteATaskAndDontSeeNext() {
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
        assertThat(taskById.getStatus()).isEqualTo(ASSIGNED);
        assertThat(task).isEqualTo(taskById);
        assertThat(task.getAssignee()).isEqualTo("user1");
        assertThat(Set3RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, TASK_CREATED, TASK_ASSIGNED);
        Set3RuntimeTestConfiguration.collectedEvents.clear();
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
        assertThat(Set3RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_COMPLETED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, TASK_CREATED);
        // Check with user1 as he is a candidate
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        task = tasks.getContent().get(0);
        taskById = taskRuntime.task(task.getId());
        assertThat(taskById.getStatus()).isEqualTo(CREATED);
        List<String> candidateUsers = taskRuntime.userCandidates(task.getId());
        assertThat(candidateUsers.size()).isEqualTo(0);
        List<String> candidateGroups = taskRuntime.groupCandidates(task.getId());
        assertThat(candidateGroups).contains("group1");
        // Check with user2 candidates which is not a candidate
        securityUtil.logInAs("user2");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(0);
        // Check with user2 candidates which is not a candidate
        securityUtil.logInAs("user3");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(1);
        task = tasks.getContent().get(0);
        taskById = taskRuntime.task(task.getId());
        assertThat(taskById.getStatus()).isEqualTo(CREATED);
    }
}

