/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cmmn.test;


import CallbackTypes.PLAN_ITEM_CHILD_PROCESS;
import EntityLinkType.CHILD;
import HierarchyType.PARENT;
import HierarchyType.ROOT;
import HistoryLevel.ACTIVITY;
import PlanItemDefinitionType.PROCESS_TASK;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.ENABLED;
import ScopeTypes.BPMN;
import ScopeTypes.CMMN;
import ScopeTypes.TASK;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.flowable.cmmn.api.history.HistoricMilestoneInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.UserEventListenerInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.history.HistoricEntityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class ProcessTaskTest extends AbstractProcessEngineIntegrationTest {
    @Test
    @CmmnDeployment
    public void testOneTaskProcessNonBlocking() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess();
        List<Task> processTasks = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().list();
        Assert.assertEquals(1, processTasks.size());
        // Non-blocking process task, plan item should have been completed
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
        AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(processTasks.get(0).getId());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testOneCallActivityProcessBlocking() {
        Deployment deployment = AbstractProcessEngineIntegrationTest.processEngine.getRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/oneCallActivityProcess.bpmn20.xml").addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").deploy();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
            Assert.assertEquals(1, planItemInstances.size());
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
            List<Task> processTasks = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().list();
            Assert.assertEquals(1, processTasks.size());
            Task processTask = processTasks.get(0);
            String subProcessInstanceId = processTask.getProcessInstanceId();
            ProcessInstance processInstance = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().subProcessInstanceId(subProcessInstanceId).singleResult();
            Task task = cmmnTaskService.createTaskQuery().caseInstanceIdWithChildren(caseInstance.getId()).singleResult();
            Assert.assertEquals(processTask.getId(), task.getId());
            task = cmmnTaskService.createTaskQuery().processInstanceIdWithChildren(processInstance.getId()).singleResult();
            Assert.assertEquals(processTask.getId(), task.getId());
            List<EntityLink> entityLinks = cmmnRuntimeService.getEntityLinkChildrenForCaseInstance(caseInstance.getId());
            Assert.assertEquals(3, entityLinks.size());
            EntityLink processEntityLink = null;
            EntityLink subProcessEntityLink = null;
            EntityLink taskEntityLink = null;
            for (EntityLink entityLink : entityLinks) {
                if (BPMN.equals(entityLink.getReferenceScopeType())) {
                    if (processInstance.getId().equals(entityLink.getReferenceScopeId())) {
                        processEntityLink = entityLink;
                    } else {
                        subProcessEntityLink = entityLink;
                    }
                } else
                    if (TASK.equals(entityLink.getReferenceScopeType())) {
                        taskEntityLink = entityLink;
                    }

            }
            Assert.assertEquals(CHILD, processEntityLink.getLinkType());
            Assert.assertNotNull(processEntityLink.getCreateTime());
            Assert.assertEquals(caseInstance.getId(), processEntityLink.getScopeId());
            Assert.assertEquals(CMMN, processEntityLink.getScopeType());
            Assert.assertNull(processEntityLink.getScopeDefinitionId());
            Assert.assertEquals(processInstance.getId(), processEntityLink.getReferenceScopeId());
            Assert.assertEquals(BPMN, processEntityLink.getReferenceScopeType());
            Assert.assertNull(processEntityLink.getReferenceScopeDefinitionId());
            Assert.assertEquals(ROOT, processEntityLink.getHierarchyType());
            Assert.assertEquals(CHILD, subProcessEntityLink.getLinkType());
            Assert.assertNotNull(subProcessEntityLink.getCreateTime());
            Assert.assertEquals(caseInstance.getId(), subProcessEntityLink.getScopeId());
            Assert.assertEquals(CMMN, subProcessEntityLink.getScopeType());
            Assert.assertNull(subProcessEntityLink.getScopeDefinitionId());
            Assert.assertEquals(subProcessInstanceId, subProcessEntityLink.getReferenceScopeId());
            Assert.assertEquals(BPMN, subProcessEntityLink.getReferenceScopeType());
            Assert.assertNull(subProcessEntityLink.getReferenceScopeDefinitionId());
            Assert.assertEquals(ROOT, subProcessEntityLink.getHierarchyType());
            Assert.assertEquals(CHILD, taskEntityLink.getLinkType());
            Assert.assertNotNull(taskEntityLink.getCreateTime());
            Assert.assertEquals(caseInstance.getId(), taskEntityLink.getScopeId());
            Assert.assertEquals(CMMN, taskEntityLink.getScopeType());
            Assert.assertNull(taskEntityLink.getScopeDefinitionId());
            Assert.assertEquals(processTasks.get(0).getId(), taskEntityLink.getReferenceScopeId());
            Assert.assertEquals(TASK, taskEntityLink.getReferenceScopeType());
            Assert.assertNull(taskEntityLink.getReferenceScopeDefinitionId());
            Assert.assertEquals(ROOT, taskEntityLink.getHierarchyType());
            entityLinks = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().getEntityLinkChildrenForProcessInstance(processInstance.getId());
            Assert.assertEquals(2, entityLinks.size());
            entityLinks = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().getEntityLinkChildrenForProcessInstance(subProcessInstanceId);
            Assert.assertEquals(1, entityLinks.size());
            EntityLink entityLink = entityLinks.get(0);
            Assert.assertEquals(CHILD, entityLink.getLinkType());
            Assert.assertNotNull(entityLink.getCreateTime());
            Assert.assertEquals(subProcessInstanceId, entityLink.getScopeId());
            Assert.assertEquals(BPMN, entityLink.getScopeType());
            Assert.assertNull(entityLink.getScopeDefinitionId());
            Assert.assertEquals(processTasks.get(0).getId(), entityLink.getReferenceScopeId());
            Assert.assertEquals(TASK, entityLink.getReferenceScopeType());
            Assert.assertNull(entityLink.getReferenceScopeDefinitionId());
            Assert.assertEquals(PARENT, entityLink.getHierarchyType());
            entityLinks = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().getEntityLinkParentsForTask(processTask.getId());
            Assert.assertEquals(3, entityLinks.size());
            entityLink = entityLinks.get(0);
            Assert.assertEquals(CHILD, entityLink.getLinkType());
            Assert.assertNotNull(entityLink.getCreateTime());
            AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(processTasks.get(0).getId());
            Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
            List<HistoricEntityLink> historicEntityLinks = cmmnHistoryService.getHistoricEntityLinkChildrenForCaseInstance(caseInstance.getId());
            Assert.assertEquals(3, historicEntityLinks.size());
            historicEntityLinks = AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().getHistoricEntityLinkChildrenForProcessInstance(processInstance.getId());
            Assert.assertEquals(2, historicEntityLinks.size());
            historicEntityLinks = AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().getHistoricEntityLinkChildrenForProcessInstance(subProcessInstanceId);
            Assert.assertEquals(1, historicEntityLinks.size());
            Assert.assertEquals(PARENT, historicEntityLinks.get(0).getHierarchyType());
            historicEntityLinks = AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().getHistoricEntityLinkParentsForTask(processTasks.get(0).getId());
            Assert.assertEquals(3, historicEntityLinks.size());
            HistoricTaskInstance historicTask = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceIdWithChildren(caseInstance.getId()).singleResult();
            Assert.assertEquals(processTask.getId(), historicTask.getId());
            historicTask = cmmnHistoryService.createHistoricTaskInstanceQuery().processInstanceIdWithChildren(processInstance.getId()).singleResult();
            Assert.assertEquals(processTask.getId(), historicTask.getId());
        } finally {
            AbstractProcessEngineIntegrationTest.processEngine.getRepositoryService().deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment
    public void testOneTaskProcessBlocking() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess();
        Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
        // Blocking process task, plan item should be in state ACTIVE
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("The Process", planItemInstances.get(0).getName());
        Assert.assertNotNull(planItemInstances.get(0).getReferenceId());
        Assert.assertEquals(PLAN_ITEM_CHILD_PROCESS, planItemInstances.get(0).getReferenceType());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
        ProcessInstance processInstance = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        PlanItemInstance processTaskPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PROCESS_TASK).singleResult();
        Assert.assertEquals(processTaskPlanItemInstance.getId(), processInstance.getCallbackId());
        Assert.assertEquals(PLAN_ITEM_CHILD_PROCESS, processInstance.getCallbackType());
        Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceCallbackId(processInstance.getCallbackId()).singleResult().getId());
        Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceCallbackType(PLAN_ITEM_CHILD_PROCESS).singleResult().getId());
        Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceCallbackId(processTaskPlanItemInstance.getId()).processInstanceCallbackType(PLAN_ITEM_CHILD_PROCESS).singleResult().getId());
        if (AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricProcessInstance historicProcessInstance = AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertEquals(processInstance.getCallbackId(), historicProcessInstance.getCallbackId());
            Assert.assertEquals(processInstance.getCallbackType(), historicProcessInstance.getCallbackType());
            Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceCallbackId(processInstance.getCallbackId()).singleResult().getId());
            Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceCallbackType(PLAN_ITEM_CHILD_PROCESS).singleResult().getId());
            Assert.assertEquals(processInstance.getId(), AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceCallbackId(processTaskPlanItemInstance.getId()).processInstanceCallbackType(PLAN_ITEM_CHILD_PROCESS).singleResult().getId());
        }
        // Completing task will trigger completion of process task plan item
        AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(task.getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(tenantId = "flowable", resources = "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessBlocking.cmmn")
    public void testOneTaskProcessBlockingWithTenant() {
        try {
            if ((processEngineRepositoryService.createDeploymentQuery().count()) == 1) {
                Deployment deployment = processEngineRepositoryService.createDeploymentQuery().singleResult();
                processEngineRepositoryService.deleteDeployment(deployment.getId());
            }
            processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").tenantId("flowable").deploy();
            CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("flowable");
            ProcessInstance processInstance = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().singleResult();
            Assert.assertEquals("flowable", processInstance.getTenantId());
            Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
            Assert.assertEquals("flowable", task.getTenantId());
            this.cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        } finally {
            if ((processEngineRepositoryService.createDeploymentQuery().count()) == 1) {
                Deployment deployment = processEngineRepositoryService.createDeploymentQuery().singleResult();
                processEngineRepositoryService.deleteDeployment(deployment.getId());
            }
            if ((AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().count()) == 1) {
                HistoricTaskInstance historicTaskInstance = AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().singleResult();
                AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().deleteHistoricTaskInstance(historicTaskInstance.getId());
            }
            if ((AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricTaskLogEntryQuery().count()) > 0) {
                List<HistoricTaskLogEntry> historicTaskLogEntries = AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().createHistoricTaskLogEntryQuery().list();
                for (HistoricTaskLogEntry historicTaskLogEntry : historicTaskLogEntries) {
                    AbstractProcessEngineIntegrationTest.processEngine.getHistoryService().deleteHistoricTaskLogEntry(historicTaskLogEntry.getLogNumber());
                }
            }
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessBlocking.cmmn")
    public void testTwoTaskProcessBlocking() {
        try {
            if ((processEngineRepositoryService.createDeploymentQuery().count()) == 1) {
                Deployment deployment = processEngineRepositoryService.createDeploymentQuery().singleResult();
                processEngineRepositoryService.deleteDeployment(deployment.getId());
            }
            processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/twoTaskProcess.bpmn20.xml").deploy();
            CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess();
            Task task = processEngineTaskService.createTaskQuery().singleResult();
            Assert.assertEquals("my task", task.getName());
            EntityLink taskEntityLink = null;
            List<EntityLink> entityLinks = cmmnRuntimeService.getEntityLinkChildrenForCaseInstance(caseInstance.getId());
            for (EntityLink entityLink : entityLinks) {
                if (task.getId().equals(entityLink.getReferenceScopeId())) {
                    taskEntityLink = entityLink;
                }
            }
            Assert.assertNotNull(taskEntityLink);
            Assert.assertEquals(task.getId(), taskEntityLink.getReferenceScopeId());
            Assert.assertEquals(TASK, taskEntityLink.getReferenceScopeType());
            Assert.assertEquals(ROOT, taskEntityLink.getHierarchyType());
            processEngineTaskService.complete(task.getId());
            Task task2 = processEngineTaskService.createTaskQuery().singleResult();
            Assert.assertEquals("my task2", task2.getName());
            EntityLink taskEntityLink2 = null;
            entityLinks = cmmnRuntimeService.getEntityLinkChildrenForCaseInstance(caseInstance.getId());
            for (EntityLink entityLink : entityLinks) {
                if (task2.getId().equals(entityLink.getReferenceScopeId())) {
                    taskEntityLink2 = entityLink;
                }
            }
            Assert.assertNotNull(taskEntityLink2);
            Assert.assertEquals(task2.getId(), taskEntityLink2.getReferenceScopeId());
            Assert.assertEquals(TASK, taskEntityLink2.getReferenceScopeType());
            Assert.assertEquals(ROOT, taskEntityLink2.getHierarchyType());
            processEngineTaskService.complete(task2.getId());
            List<ProcessInstance> processInstances = processEngineRuntimeService.createProcessInstanceQuery().processDefinitionKey("oneTask").list();
            Assert.assertEquals(0, processInstances.size());
            this.cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        } finally {
            if ((processEngineRepositoryService.createDeploymentQuery().count()) == 1) {
                Deployment deployment = processEngineRepositoryService.createDeploymentQuery().singleResult();
                processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
            }
            if ((AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().count()) == 1) {
                HistoricTaskInstance historicTaskInstance = AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().singleResult();
                AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().getHistoryService().deleteHistoricTaskInstance(historicTaskInstance.getId());
            }
        }
    }

    @Test
    @CmmnDeployment
    public void testProcessRefExpression() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("processDefinitionKey", "oneTask");
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(cmmnRepositoryService.createCaseDefinitionQuery().singleResult().getId()).variables(variables).start();
        Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertNotNull(task);
        // Completing task will trigger completion of process task plan item
        AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(task.getId());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testProcessIOParameter() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("processDefinitionKey", "oneTask");
        variables.put("num2", 123);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(cmmnRepositoryService.createCaseDefinitionQuery().singleResult().getId()).variables(variables).start();
        Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertNotNull(task);
        // Completing task will trigger completion of process task plan item
        AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(task.getId());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
        Assert.assertEquals(123, cmmnRuntimeService.getVariable(caseInstance.getId(), "num3"));
        Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testProcessIOParameterExpressions() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(cmmnRepositoryService.createCaseDefinitionQuery().singleResult().getId()).variable("processDefinitionKey", "oneTask").start();
        Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertNotNull(task);
        // Completing task will trigger completion of process task plan item
        Assert.assertEquals(2L, ((Number) (AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().getVariable(task.getProcessInstanceId(), "numberVariable"))).longValue());
        AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(task.getId(), Collections.singletonMap("processVariable", "Hello World"));
        Assert.assertEquals("Hello World", cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVariable"));
    }

    @Test
    @CmmnDeployment
    public void testIOParameterCombinations() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testProcessTaskParameterExpressions").variable("caseVariableA", "variable A from the case instance").variable("caseVariableName1", "aCaseString1").variable("caseVariableName2", "aCaseString2").start();
        PlanItemInstance processTaskPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionType(PROCESS_TASK).singleResult();
        String processInstanceId = processTaskPlanItemInstance.getReferenceId();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Assertions.assertThat(processInstance).isNotNull();
        // In parameters
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "caseVariableA")).isNull();
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "caseVariableName1")).isNull();
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "caseVariableName2")).isNull();
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "processVariableA")).isEqualTo("variable A from the case instance");
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "aCaseString1")).isEqualTo("variable A from the case instance");
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "processVariableB")).isEqualTo(2L);
        Assertions.assertThat(processEngineRuntimeService.getVariable(processInstanceId, "aCaseString2")).isEqualTo(4L);
        // Out parameters
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        Map<String, Object> variables = new HashMap<>();
        variables.put("processVariableC", "hello");
        variables.put("processVariableD", 123);
        variables.put("processVariableName1", "processString1");
        variables.put("processVariableName2", "processString2");
        processEngineTaskService.complete(task.getId(), variables);
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processVariableC")).isNull();
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processVariableD")).isNull();
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processVariableName1")).isNull();
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processVariableName2")).isNull();
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "caseVariableC")).isEqualTo("hello");
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processString1")).isEqualTo("hello");
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "caseVariableD")).isEqualTo(124L);
        Assertions.assertThat(cmmnRuntimeService.getVariable(caseInstance.getId(), "processString2")).isEqualTo(6L);
        Assertions.assertThat(cmmnRuntimeService.getVariables(caseInstance.getId())).hasSize((3 + 4));// 3 from start, 4 from out mapping

        Assertions.assertThat(processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list()).hasSize((4 + 4));// 4 from in mapping, 4 from task complete

    }

    @Test
    @CmmnDeployment
    public void testProcessTaskWithSkipExpressions() {
        Deployment deployment = processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/processWithSkipExpressions.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = processEngineRepositoryService.createProcessDefinitionQuery().processDefinitionKey("testSkipExpressionProcess").singleResult();
        ObjectNode infoNode = processEngineDynamicBpmnService.enableSkipExpression();
        // skip test user task
        processEngineDynamicBpmnService.changeSkipExpression("sequenceflow2", "${true}", infoNode);
        processEngineDynamicBpmnService.saveProcessDefinitionInfo(processDefinition.getId(), infoNode);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("skipExpressionCaseTest").start();
        Assert.assertTrue(processEngineTaskService.createTaskQuery().list().isEmpty());
        Assert.assertTrue(processEngineRuntimeService.createProcessInstanceQuery().list().isEmpty());
    }

    @Test
    @CmmnDeployment
    public void testProcessTaskWithInclusiveGateway() {
        Deployment deployment = processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/processWithInclusiveGateway.bpmn20.xml").deploy();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
            Assert.assertEquals(0, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
            Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionId("theTask").planItemInstanceState(ACTIVE).list();
            Assert.assertEquals(1, planItemInstances.size());
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
            Assert.assertEquals("No process instance started", 1L, processEngineRuntimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(2, processEngineTaskService.createTaskQuery().count());
            List<Task> tasks = processEngineTaskService.createTaskQuery().list();
            AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(tasks.get(0).getId());
            AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(tasks.get(1).getId());
            Assert.assertEquals(0, processEngineTaskService.createTaskQuery().count());
            Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
            planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionId("theTask2").list();
            Assert.assertEquals(1, planItemInstances.size());
            Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
            Assert.assertEquals(ENABLED, planItemInstances.get(0).getState());
        } finally {
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment
    public void testTransactionRollback() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(cmmnRepositoryService.createCaseDefinitionQuery().singleResult().getId()).start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertEquals("Task One", planItemInstance.getName());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        /* Triggering the plan item will lead to the plan item that starts the one task process in a non-blocking way.
        Due to the non-blocking, the plan item completes and the new task, mile stone and service task are called.
        The service task throws an exception. The process should also roll back now and never have been inserted.
         */
        try {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
            Assert.fail();
        } catch (Exception e) {
        }
        // Without shared transaction, following would be 1
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        // Both case and process should have rolled back
        Assert.assertEquals("Task One", planItemInstance.getName());
    }

    @Test
    @CmmnDeployment
    public void testTriggerUnfinishedProcessPlanItem() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertEquals("The Process", planItemInstance.getName());
        Assert.assertEquals("my task", AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult().getName());
        // Triggering the process plan item should cancel the process instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().singleResult();
        Assert.assertEquals("Process planitem done", historicMilestoneInstance.getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment
    public void testStartProcessInstanceNonBlockingAndCaseInstanceFinished() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(1, AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(1, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().singleResult();
        Assert.assertEquals("Process planitem done", historicMilestoneInstance.getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment
    public void testStartMultipleProcessInstancesBlocking() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertEquals("Task One", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(4, AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(4, processEngineRuntimeService.createProcessInstanceQuery().count());
        // Completing all the tasks should lead to the milestone
        for (Task task : processEngineTaskService.createTaskQuery().list()) {
            processEngineTaskService.complete(task.getId());
        }
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().singleResult();
        Assert.assertEquals("Processes done", historicMilestoneInstance.getName());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment
    public void testTerminateCaseInstanceWithBlockingProcessTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemInstanceName("Task One").singleResult();
        Assert.assertNotNull(planItemInstance);
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(4, AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(4, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        Assert.assertEquals(0, AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ProcessTaskTest.testParentStageTerminatedBeforeProcessStarted.cmmn", "org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml" })
    public void testParentStageTerminatedBeforeProcessStarted() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testProcessTask").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("A", task.getName());
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Complete stage", userEventListenerInstance.getName());
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessFallbackToDefaultTenant.cmmn" }, tenantId = "flowable")
    public void testOneTaskProcessFallbackToDefaultTenant() {
        Deployment deployment = this.processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").deploy();
        try {
            CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("flowable");
            List<Task> processTasks = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().list();
            Assert.assertEquals(1, processTasks.size());
            Assert.assertEquals("flowable", processTasks.get(0).getTenantId());
            ProcessInstance processInstance = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processTasks.get(0).getProcessInstanceId()).singleResult();
            Assert.assertNotNull(processInstance);
            Assert.assertEquals("flowable", processInstance.getTenantId());
            // Non-blocking process task, plan item should have been completed
            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
            Assert.assertEquals(1, planItemInstances.size());
            Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
            Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
            AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(processTasks.get(0).getId());
            Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        } finally {
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessNonBlocking.cmmn" }, tenantId = "someTenant")
    public void testOneTaskProcessGlobalFallbackToDefaultTenant() {
        Deployment deployment = this.processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").tenantId("defaultFlowable").deploy();
        String originalDefaultTenantValue = this.processEngineConfiguration.getDefaultTenantValue();
        this.processEngineConfiguration.setFallbackToDefaultTenant(true);
        this.processEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        try {
            CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("someTenant");
            List<Task> processTasks = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().list();
            Assert.assertEquals(1, processTasks.size());
            Assert.assertEquals("someTenant", processTasks.get(0).getTenantId());
            ProcessInstance processInstance = AbstractProcessEngineIntegrationTest.processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processTasks.get(0).getProcessInstanceId()).singleResult();
            Assert.assertNotNull(processInstance);
            Assert.assertEquals("someTenant", processInstance.getTenantId());
            // Non-blocking process task, plan item should have been completed
            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
            Assert.assertEquals(1, planItemInstances.size());
            Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
            Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
            AbstractProcessEngineIntegrationTest.processEngine.getTaskService().complete(processTasks.get(0).getId());
            Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        } finally {
            this.processEngineConfiguration.setFallbackToDefaultTenant(false);
            this.processEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessNonBlocking.cmmn" }, tenantId = "someTenant")
    public void testOneTaskProcessGlobalFallbackToDefaultTenantNoDefinition() {
        Deployment deployment = this.processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").tenantId("tenant1").deploy();
        String originalDefaultTenantValue = this.processEngineConfiguration.getDefaultTenantValue();
        this.processEngineConfiguration.setFallbackToDefaultTenant(true);
        this.processEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        try {
            startCaseInstanceWithOneTaskProcess("someTenant");
            Assert.fail();
        } catch (FlowableObjectNotFoundException e) {
            // expected
        } finally {
            this.processEngineConfiguration.setFallbackToDefaultTenant(false);
            this.processEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessFallbackToDefaultTenantFalse.cmmn" }, tenantId = "flowable")
    public void testOneTaskProcessFallbackToDefaultTenantFalse() {
        Deployment deployment = this.processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").deploy();
        try {
            startCaseInstanceWithOneTaskProcess("flowable");
            Assert.fail();
        } catch (FlowableObjectNotFoundException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("Process definition with key 'oneTask' and tenantId 'flowable' was not found"));
        } finally {
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessBlocking.cmmn")
    public void testDeleteProcessTaskShouldNotBePossible() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess();
        Task task = AbstractProcessEngineIntegrationTest.processEngine.getTaskService().createTaskQuery().singleResult();
        assertThatThrownBy(() -> cmmnTaskService.deleteTask(task.getId())).isExactlyInstanceOf(FlowableException.class).hasMessageContaining("The task cannot be deleted").hasMessageContaining("running process");
    }

    @Test
    @CmmnDeployment
    public void testChangeActiveStagesWithProcessTasks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("activatePlanItemTest").start();
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage2", "available"), tuple("oneexpandedstage1", "available"), tuple("oneprocesstask1", "active"));
        Task task = processEngineTaskService.createTaskQuery().taskDefinitionKey("theTask").singleResult();
        processEngineTaskService.complete(task.getId());
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage2", "available"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneprocesstask2", "active"), tuple("oneprocesstask4", "active"));
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("oneexpandedstage1", "oneprocesstask3").changeState();
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "active"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask3", "active"));
        Assert.assertEquals(1, processEngineRuntimeService.createProcessInstanceQuery().processDefinitionKey("oneTask").count());
        task = processEngineTaskService.createTaskQuery().taskDefinitionKey("theTask").singleResult();
        processEngineTaskService.complete(task.getId());
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask2", "active"), tuple("oneprocesstask4", "active"));
        Assert.assertEquals(2, processEngineRuntimeService.createProcessInstanceQuery().processDefinitionKey("oneTask").count());
    }

    @Test
    @CmmnDeployment
    public void testChangeActiveStagesWithManualProcessTasks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("activatePlanItemTest").start();
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage2", "available"), tuple("oneexpandedstage1", "available"), tuple("oneprocesstask1", "active"));
        Task task = processEngineTaskService.createTaskQuery().taskDefinitionKey("theTask").singleResult();
        processEngineTaskService.complete(task.getId());
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage2", "available"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneprocesstask2", "enabled"), tuple("oneprocesstask4", "enabled"));
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("oneexpandedstage1", "oneprocesstask3").changeState();
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "active"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask3", "active"));
        Assert.assertEquals(1, processEngineRuntimeService.createProcessInstanceQuery().processDefinitionKey("oneTask").count());
        task = processEngineTaskService.createTaskQuery().taskDefinitionKey("theTask").singleResult();
        processEngineTaskService.complete(task.getId());
        Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask2", "enabled"), tuple("oneprocesstask4", "enabled"));
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().processDefinitionKey("oneTask").count());
    }

    @Test
    @CmmnDeployment
    public void testChangeActiveStages() {
        Deployment deployment = this.processEngineRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/emptyProcess.bpmn20.xml").deploy();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("activatePlanItemTest").start();
            Assert.assertEquals(1, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").count());
            Assert.assertEquals(0, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").finished().count());
            Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneprocesstask1", "active"), tuple("oneexpandedstage1", "available"), tuple("oneexpandedstage2", "available"));
            Task task = processEngineTaskService.createTaskQuery().processDefinitionKey("oneTask").singleResult();
            processEngineTaskService.complete(task.getId());
            Assert.assertEquals(1, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").finished().count());
            Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage2", "available"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneprocesstask2", "enabled"), tuple("oneprocesstask4", "enabled"));
            cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("oneexpandedstage1", "oneprocesstask3").changeState();
            Assert.assertEquals(2, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").count());
            Assert.assertEquals(1, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").finished().count());
            task = processEngineTaskService.createTaskQuery().processDefinitionKey("oneTask").singleResult();
            processEngineTaskService.complete(task.getId());
            Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "active"), tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask2", "enabled"), tuple("oneprocesstask4", "enabled"));
            PlanItemInstance deactivateInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("oneprocesstask4").planItemInstanceState("enabled").singleResult();
            cmmnRuntimeService.startPlanItemInstance(deactivateInstance.getId());
            Assert.assertEquals(2, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("oneTask").finished().count());
            Assert.assertEquals(1, processEngineHistoryService.createHistoricProcessInstanceQuery().processDefinitionKey("emptyProcess").finished().count());
            Assertions.assertThat(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list()).extracting(PlanItemInstance::getPlanItemDefinitionId, PlanItemInstance::getState).containsExactlyInAnyOrder(tuple("oneexpandedstage1", "wait_repetition"), tuple("oneexpandedstage2", "active"), tuple("oneexpandedstage2", "wait_repetition"), tuple("oneprocesstask3", "enabled"));
        } finally {
            processEngineRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    @CmmnDeployment
    public void testPassChildTaskVariables() {
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("startChildProcess").start();
        PlanItemInstance processPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().singleResult();
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(processPlanItemInstance.getId()).variable("caseVar", "caseValue").childTaskVariable("processVar1", "processValue").childTaskVariables(CollectionUtil.map("processVar2", 123, "processVar3", 456)).start();
        Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstance.getId());
        // also includes initiator variable
        Assert.assertEquals(2, variables.size());
        Assert.assertEquals("caseValue", variables.get("caseVar"));
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().processInstanceCallbackId(processPlanItemInstance.getId()).singleResult();
        Map<String, Object> processInstanceVariables = processEngineRuntimeService.getVariables(processInstance.getId());
        Assert.assertEquals(3, processInstanceVariables.size());
        Assert.assertEquals("processValue", processInstanceVariables.get("processVar1"));
        Assert.assertEquals(123, processInstanceVariables.get("processVar2"));
        Assert.assertEquals(456, processInstanceVariables.get("processVar3"));
    }
}

