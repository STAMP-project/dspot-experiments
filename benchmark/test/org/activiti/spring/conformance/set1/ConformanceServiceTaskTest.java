package org.activiti.spring.conformance.set1;


import BPMNActivityEvent.ActivityEvents.ACTIVITY_COMPLETED;
import BPMNActivityEvent.ActivityEvents.ACTIVITY_STARTED;
import BPMNSequenceFlowTakenEvent.SequenceFlowEvents.SEQUENCE_FLOW_TAKEN;
import ProcessInstance.ProcessInstanceStatus.COMPLETED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_COMPLETED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_CREATED;
import ProcessRuntimeEvent.ProcessEvents.PROCESS_STARTED;
import SpringBootTest.WebEnvironment;
import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.NotFoundException;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.spring.conformance.util.security.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ConformanceServiceTaskTest {
    private static final String MY_BUSINESS_KEY = "my-business-key";

    private final String processKey = "servicetas-820b2020-968d-4d34-bac4-5769192674f2";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    /* This test covers the ServiceTask with Implementation.bpmn20.xml process
    This execution should generate 11 events:
      - PROCESS_CREATED
      - PROCESS_STARTED,
      - ACTIVITY_STARTED,
      - ACTIVITY_COMPLETED,
      - SEQUENCE_FLOW_TAKEN,
      - ACTIVITY_STARTED,
      - ACTIVITY_COMPLETED,
      - SEQUENCE_FLOW_TAKEN,
      - ACTIVITY_STARTED,
      - ACTIVITY_COMPLETED,
      - PROCESS_COMPLETED
     And the Process Instance Status should be Completed
     Connectors are executed in a Sync fashion, so the logic will be exexuted and the BPMN Activity completed automatically.
     IntegrationContext attributes shall capture and contain valid execution context of the underlying process instance. 
     No further operation can be executed on the process due the fact that it start and finish in the same transaction
     */
    @Test
    public void shouldBeAbleToStartProcess() {
        securityUtil.logInAs("user1");
        // when
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(processKey).withBusinessKey(ConformanceServiceTaskTest.MY_BUSINESS_KEY).withName("my-process-instance-name").build());
        // then
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getStatus()).isEqualTo(COMPLETED);
        assertThat(processInstance.getBusinessKey()).isEqualTo(ConformanceServiceTaskTest.MY_BUSINESS_KEY);
        assertThat(processInstance.getName()).isEqualTo("my-process-instance-name");
        // No Process Instance should be found
        Throwable throwable = catchThrowable(() -> processRuntime.processInstance(processInstance.getId()));
        assertThat(throwable).isInstanceOf(NotFoundException.class);
        // No Variable Instance should be found
        throwable = catchThrowable(() -> processRuntime.variables(ProcessPayloadBuilder.variables().withProcessInstanceId(processInstance.getId()).build()));
        assertThat(throwable).isInstanceOf(NotFoundException.class);
        assertThat(Set1RuntimeTestConfiguration.isConnector1Executed()).isTrue();
        // and then
        IntegrationContext integrationContext = Set1RuntimeTestConfiguration.getResultIntegrationContext();
        assertThat(integrationContext).isNotNull();
        assertThat(integrationContext.getBusinessKey()).isEqualTo(processInstance.getBusinessKey());
        assertThat(integrationContext.getProcessDefinitionId()).isEqualTo(processInstance.getProcessDefinitionId());
        assertThat(integrationContext.getProcessInstanceId()).isEqualTo(processInstance.getId());
        assertThat(integrationContext.getProcessDefinitionKey()).isEqualTo(processInstance.getProcessDefinitionKey());
        assertThat(integrationContext.getProcessDefinitionVersion()).isEqualTo(1);
        assertThat(integrationContext.getParentProcessInstanceId()).isNull();
        assertThat(integrationContext.getClientId()).isNotNull();
        assertThat(integrationContext.getClientName()).isEqualTo("My Service Task");
        assertThat(integrationContext.getClientType()).isEqualTo(ServiceTask.class.getSimpleName());
        // and then
        assertThat(Set1RuntimeTestConfiguration.collectedEvents).extracting(RuntimeEvent::getEventType).containsExactly(PROCESS_CREATED, PROCESS_STARTED, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, ACTIVITY_COMPLETED, SEQUENCE_FLOW_TAKEN, ACTIVITY_STARTED, ACTIVITY_COMPLETED, PROCESS_COMPLETED);
    }
}

