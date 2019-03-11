package org.activiti.spring.boot;


import SpringBootTest.WebEnvironment;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.process.runtime.conf.ProcessRuntimeConfiguration;
import org.activiti.core.common.spring.security.policies.ProcessSecurityPoliciesManager;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.activiti.runtime.api.model.impl.APIProcessInstanceConverter;
import org.activiti.runtime.api.model.impl.APIVariableInstanceConverter;
import org.activiti.spring.boot.security.util.SecurityUtil;
import org.activiti.spring.boot.test.util.ProcessCleanUpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource("classpath:application-history.properties")
public class HistoryConfigurationTest {
    private static final String CATEGORIZE_PROCESS = "categorizeProcess";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private APIProcessDefinitionConverter processDefinitionConverter;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProcessSecurityPoliciesManager securityPoliciesManager;

    @Autowired
    private APIProcessInstanceConverter processInstanceConverter;

    @Autowired
    private APIVariableInstanceConverter variableInstanceConverter;

    @Autowired
    private ProcessRuntimeConfiguration configuration;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ProcessCleanUpUtil processCleanUpUtil;

    @Test
    public void shouldRecordHistory() {
        securityUtil.logInAs("salaboy");
        // when
        ProcessInstance categorizeProcess = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(HistoryConfigurationTest.CATEGORIZE_PROCESS).withVariable("expectedKey", true).build());
        assertThat(RuntimeTestConfiguration.completedProcesses).contains(categorizeProcess.getId());
        assertThat(historyService.createHistoricProcessInstanceQuery().processInstanceId(categorizeProcess.getId()).count()).isEqualTo(1);
    }
}

