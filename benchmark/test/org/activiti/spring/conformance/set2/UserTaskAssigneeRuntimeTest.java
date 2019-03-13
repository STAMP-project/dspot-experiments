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
import TaskRuntimeEvent.TaskEvents.TASK_ASSIGNED;
import TaskRuntimeEvent.TaskEvents.TASK_COMPLETED;
import TaskRuntimeEvent.TaskEvents.TASK_CREATED;
import java.util.List;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.activiti.api.runtime.shared.NotFoundException;
import org.activiti.api.runtime.shared.events.VariableEventListener;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.api.task.runtime.conf.TaskRuntimeConfiguration;
import org.activiti.api.task.runtime.events.listener.TaskRuntimeEventListener;
import org.activiti.spring.conformance.util.security.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UserTaskAssigneeRuntimeTest {
    private final String processKey = "usertask-6a854551-861f-4cc5-a1a1-73b8a14ccdc4";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Test
    public void shouldGetConfiguration() {
        securityUtil.logInAs("user1");
        // when
        TaskRuntimeConfiguration configuration = taskRuntime.configuration();
        // then
        assertThat(configuration).isNotNull();
        // when
        List<TaskRuntimeEventListener<?>> taskRuntimeEventListeners = configuration.taskRuntimeEventListeners();
        List<VariableEventListener<?>> variableEventListeners = configuration.variableEventListeners();
        List<ProcessRuntimeEventListener<?>> processRuntimeEventListeners = processRuntime.configuration().processEventListeners();
        // then
        assertThat(taskRuntimeEventListeners).hasSize(5);
        assertThat(variableEventListeners).hasSize(3);
        assertThat(processRuntimeEventListeners).hasSize(10);
    }

    @Test
    public void shouldStartAProcessCreateAndCompleteAssignedTask() {
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
        // Check with user2
        securityUtil.logInAs("user2");
        tasks = taskRuntime.tasks(Pageable.of(0, 50));
        assertThat(tasks.getTotalItems()).isEqualTo(0);
        Throwable throwable = catchThrowable(() -> taskRuntime.task(task.getId()));
        assertThat(throwable).isInstanceOf(NotFoundException.class);
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, TASK_CREATED, TASK_ASSIGNED);
        Set2RuntimeTestConfiguration.collectedEvents.clear();
        // complete with user1
        securityUtil.logInAs("user1");
        Task completedTask = taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
        assertThat(completedTask.getStatus()).isEqualTo(COMPLETED);
        assertThat(Set2RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(TASK_COMPLETED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, ACTIVITY_COMPLETED, PROCESS_COMPLETED);
    }
}

