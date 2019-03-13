package org.activiti.spring.conformance.set0;


import BPMNActivityEvent.ActivityEvents.ACTIVITY_CANCELLED;
import BPMNActivityEvent.ActivityEvents.ACTIVITY_COMPLETED;
import BPMNActivityEvent.ActivityEvents.ACTIVITY_STARTED;
import BPMNSequenceFlowTakenEvent.SequenceFlowEvents.SEQUENCE_FLOW_TAKEN;
import ProcessInstance.ProcessInstanceStatus.DELETED;
import ProcessInstance.ProcessInstanceStatus.RUNNING;
import ProcessInstance.ProcessInstanceStatus.SUSPENDED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_CANCELLED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_CREATED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_RESUMED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_STARTED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_SUSPENDED;
import SpringBootTest.WebEnvironment;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.NotFoundException;
import org.activiti.spring.conformance.util.security.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProcessInstanceOperationsTest {
    private final String processKey = "usertaskwi-4d5c4312-e8fc-4766-a727-b55a4d3255e9";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    /*  */
    @Test
    public void shouldBeAbleToStartAndDeleteProcessInstance() {
        securityUtil.logInAs("user1");
        // when
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processKey).withBusinessKey("my-business-key").withName("my-process-instance-name").build());
        // then
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getStatus()).isEqualTo(RUNNING);
        assertThat(processInstance.getBusinessKey()).isEqualTo("my-business-key");
        assertThat(processInstance.getName()).isEqualTo("my-process-instance-name");
        assertThat(Set0RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED);
        Set0RuntimeTestConfiguration.collectedEvents.clear();
        ProcessInstance deletedProcessInstance = processRuntime.delete(ProcessPayloadBuilder.delete(processInstance.getId()));
        assertThat(deletedProcessInstance.getStatus()).isEqualTo(DELETED);
        assertThat(Set0RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(ACTIVITY_CANCELLED, PROCESS_CANCELLED);
        // No Process Instance should be found
        Throwable throwable = catchThrowable(() -> processRuntime.processInstance(deletedProcessInstance.getId()));
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    /*  */
    @Test
    public void shouldBeAbleToStartSuspendAndResumeProcessInstance() {
        securityUtil.logInAs("user1");
        // when
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processKey).withBusinessKey("my-business-key").withName("my-process-instance-name").build());
        // then
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getStatus()).isEqualTo(RUNNING);
        assertThat(processInstance.getBusinessKey()).isEqualTo("my-business-key");
        assertThat(processInstance.getName()).isEqualTo("my-process-instance-name");
        assertThat(Set0RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED);
        Set0RuntimeTestConfiguration.collectedEvents.clear();
        ProcessInstance suspendedProcessInstance = processRuntime.suspend(ProcessPayloadBuilder.suspend(processInstance.getId()));
        assertThat(suspendedProcessInstance.getStatus()).isEqualTo(SUSPENDED);
        assertThat(Set0RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_SUSPENDED);
        Set0RuntimeTestConfiguration.collectedEvents.clear();
        ProcessInstance resumedProcessInstance = processRuntime.resume(ProcessPayloadBuilder.resume(suspendedProcessInstance.getId()));
        assertThat(resumedProcessInstance.getStatus()).isEqualTo(RUNNING);
        assertThat(Set0RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_RESUMED);
    }
}

