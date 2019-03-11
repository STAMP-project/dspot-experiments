package org.activiti.spring.conformance.variables;


import SpringBootTest.WebEnvironment;
import java.util.List;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.spring.conformance.util.security.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProcessVariablesTest {
    private final String processKey = "usertaskas-b5300a4b-8950-4486-ba20-a8d775a3d75d";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessAdminRuntime processAdminRuntime;

    private String processInstanceId;

    private List<VariableInstance> variableInstanceList;

    @Test
    public void shouldGetSameNamesAndValues() {
        securityUtil.logInAs("user1");
        startProcess();
        setVariables();
        assertThat(VariablesRuntimeTestConfiguration.collectedEvents).extracting("eventType", "entity.name", "entity.value").containsExactly(tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(0).getName(), variableInstanceList.get(0).getValue()), tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(1).getName(), variableInstanceList.get(1).getValue()));
    }

    @Test
    public void shouldGetProcessIdAndNotTaskId() {
        securityUtil.logInAs("user1");
        startProcess();
        setVariables();
        VariableInstance variableOneRuntime = variableInstanceList.get(0);
        assertThat(variableOneRuntime.getProcessInstanceId()).isEqualTo(processInstanceId);
        assertThat(variableOneRuntime.getTaskId()).isNull();
        assertThat(VariablesRuntimeTestConfiguration.collectedEvents).extracting("eventType", "entity.name", "entity.value").containsExactly(tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(0).getName(), variableInstanceList.get(0).getValue()), tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(1).getName(), variableInstanceList.get(1).getValue()));
    }

    @Test
    public void shouldNotBeTaskVariable() {
        securityUtil.logInAs("user1");
        startProcess();
        setVariables();
        VariableInstance variableOneRuntime = variableInstanceList.get(0);
        assertThat(variableOneRuntime.isTaskVariable()).isFalse();
        assertThat(VariablesRuntimeTestConfiguration.collectedEvents).extracting("eventType", "entity.name", "entity.value").containsExactly(tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(0).getName(), variableInstanceList.get(0).getValue()), tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(1).getName(), variableInstanceList.get(1).getValue()));
    }

    @Test
    public void shouldGetRightVariableType() {
        securityUtil.logInAs("user1");
        startProcess();
        setVariables();
        VariableInstance variableOneRuntime = variableInstanceList.get(0);
        VariableInstance variableTwoRuntime = variableInstanceList.get(1);
        assertThat(variableOneRuntime.getType()).isEqualTo("string");
        assertThat(variableTwoRuntime.getType()).isEqualTo("integer");
        assertThat(VariablesRuntimeTestConfiguration.collectedEvents).extracting("eventType", "entity.name", "entity.value").containsExactly(tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(0).getName(), variableInstanceList.get(0).getValue()), tuple(VariableEvent.VariableEvents.VARIABLE_CREATED, variableInstanceList.get(1).getName(), variableInstanceList.get(1).getValue()));
    }
}

