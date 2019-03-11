package org.activiti.engine.test.api.history;


import HistoryLevel.ACTIVITY;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.task.Task;
import org.junit.Test;


public class NonCascadeDeleteTest extends PluggableActivitiTestCase {
    private static String PROCESS_DEFINITION_KEY = "oneTaskProcess";

    private String deploymentId;

    private String processInstanceId;

    @Test
    public void testHistoricProcessInstanceQuery() {
        deploymentId = repositoryService.createDeployment().addClasspathResource("org/activiti/engine/test/api/runtime/oneTaskProcess.bpmn20.xml").deploy().getId();
        processInstanceId = runtimeService.startProcessInstanceByKey(NonCascadeDeleteTest.PROCESS_DEFINITION_KEY).getId();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        taskService.complete(task.getId());
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            assertEquals(NonCascadeDeleteTest.PROCESS_DEFINITION_KEY, processInstance.getProcessDefinitionKey());
            // Delete deployment and historic process instance remains.
            repositoryService.deleteDeployment(deploymentId, false);
            HistoricProcessInstance processInstanceAfterDelete = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            assertNull(processInstanceAfterDelete.getProcessDefinitionKey());
            assertNull(processInstanceAfterDelete.getProcessDefinitionName());
            assertNull(processInstanceAfterDelete.getProcessDefinitionVersion());
            // clean
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
    }
}

