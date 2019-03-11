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
package org.flowable.engine.configurator.test;


import EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG;
import EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG;
import IdentityLinkType.PARTICIPANT;
import IdentityLinkType.STARTER;
import java.util.HashMap;
import java.util.Map;
import org.flowable.app.api.repository.AppDeployment;
import org.flowable.app.engine.test.FlowableAppTestCase;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.engine.FormEngineConfiguration;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class ProcessTest extends FlowableAppTestCase {
    @Test
    public void testCompleteTask() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = ((ProcessEngineConfiguration) (appEngineConfiguration.getEngineConfigurations().get(KEY_PROCESS_ENGINE_CONFIG)));
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        AppDeployment deployment = appRepositoryService.createDeployment().addClasspathResource("org/flowable/engine/configurator/test/oneTaskProcess.bpmn20.xml").deploy();
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
            Assert.assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            Assert.assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());
            taskService.complete(task.getId());
            try {
                Assert.assertEquals(0, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
                Assert.fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            try {
                Assert.assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
                Assert.fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    public void testCompleteTaskWithForm() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = ((ProcessEngineConfiguration) (appEngineConfiguration.getEngineConfigurations().get(KEY_PROCESS_ENGINE_CONFIG)));
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        AppDeployment deployment = appRepositoryService.createDeployment().addClasspathResource("org/flowable/engine/configurator/test/oneTaskWithFormProcess.bpmn20.xml").addClasspathResource("org/flowable/engine/configurator/test/simple.form").deploy();
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
            Assert.assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            Assert.assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());
            FormEngineConfiguration formEngineConfiguration = ((FormEngineConfiguration) (appEngineConfiguration.getEngineConfigurations().get(KEY_FORM_ENGINE_CONFIG)));
            FormDefinition formDefinition = formEngineConfiguration.getFormRepositoryService().createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
            Assert.assertNotNull(formDefinition);
            Map<String, Object> variables = new HashMap<>();
            variables.put("input1", "test");
            taskService.completeTaskWithForm(task.getId(), formDefinition.getId(), null, variables);
            try {
                Assert.assertEquals(0, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
                Assert.fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            try {
                Assert.assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
                Assert.fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    public void testCompleteTaskWithAnotherForm() {
        ProcessEngineConfiguration processEngineConfiguration = ((ProcessEngineConfiguration) (appEngineConfiguration.getEngineConfigurations().get(KEY_PROCESS_ENGINE_CONFIG)));
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        AppDeployment deployment = appRepositoryService.createDeployment().addClasspathResource("org/flowable/engine/configurator/test/oneTaskWithFormProcess.bpmn20.xml").addClasspathResource("org/flowable/engine/configurator/test/another.form").addClasspathResource("org/flowable/engine/configurator/test/simple.form").deploy();
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
            Assert.assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            Assert.assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());
            FormEngineConfiguration formEngineConfiguration = ((FormEngineConfiguration) (appEngineConfiguration.getEngineConfigurations().get(KEY_FORM_ENGINE_CONFIG)));
            FormDefinition formDefinition = formEngineConfiguration.getFormRepositoryService().createFormDefinitionQuery().formDefinitionKey("anotherForm").singleResult();
            Assert.assertNotNull(formDefinition);
            Map<String, Object> variables = new HashMap<>();
            variables.put("anotherInput", "test");
            taskService.completeTaskWithForm(task.getId(), formDefinition.getId(), null, variables);
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
}

