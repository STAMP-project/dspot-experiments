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
package org.flowable.cmmn.test.validate;


import HistoricTaskLogEntryType.USER_TASK_ASSIGNEE_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_COMPLETED;
import HistoricTaskLogEntryType.USER_TASK_CREATED;
import HistoricTaskLogEntryType.USER_TASK_DUEDATE_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_ADDED;
import HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_REMOVED;
import HistoricTaskLogEntryType.USER_TASK_NAME_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_OWNER_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_PRIORITY_CHANGED;
import IdentityLinkType.PARTICIPANT;
import java.util.Collections;
import java.util.Date;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.test.AbstractProcessEngineIntegrationTest;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.engine.FlowableFormValidationException;
import org.flowable.task.api.Task;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author martin.grofcik
 */
public class CaseWithFormTest extends AbstractProcessEngineIntegrationTest {
    public static final String ONE_TASK_CASE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((((("<definitions xmlns=\"http://www.omg.org/spec/CMMN/20151109/MODEL\"\n" + "             xmlns:flowable=\"http://flowable.org/cmmn\"\n") + "\n") + "             targetNamespace=\"http://flowable.org/cmmn\">\n") + "\n") + "\n") + "    <case id=\"oneTaskCaseWithForm\">\n") + "        <casePlanModel id=\"myPlanModel\" name=\"My CasePlanModel\" flowable:formKey=\"form1\" flowable:formFieldValidation=\"CASE_VALIDATE_VALUE\">\n") + "\n") + "            <planItem id=\"planItem1\" name=\"Task One\" definitionRef=\"theTask\" />\n") + "\n") + "            <humanTask id=\"theTask\" name=\"The Task\" flowable:formKey=\"form1\" flowable:formFieldValidation=\"TASK_VALIDATE_VALUE\">\n") + "                <extensionElements>\n") + "                    <flowable:taskListener event=\"create\" class=\"org.flowable.cmmn.test.validate.SideEffectTaskListener\"></flowable:taskListener>\n") + "                    <flowable:taskListener event=\"complete\" class=\"org.flowable.cmmn.test.validate.SideEffectTaskListener\"></flowable:taskListener>\n") + "                </extensionElements>\n") + "            </humanTask>\n") + "\n") + "        </casePlanModel>\n") + "    </case>\n") + "</definitions>\n");

    protected FormRepositoryService formRepositoryService;

    @Test
    public void startCaseWithForm() {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").startFormVariables(Collections.singletonMap("variable", "VariableValue")).startWithForm();
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeTaskWithFormAndCheckTaskLogEntries() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey(task.getFormKey()).singleResult();
        task.setName("newName");
        task.setPriority(0);
        cmmnTaskService.saveTask(task);
        cmmnTaskService.setAssignee(task.getId(), "newAssignee");
        cmmnTaskService.setOwner(task.getId(), "newOwner");
        cmmnTaskService.setDueDate(task.getId(), new Date());
        cmmnTaskService.addUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
        cmmnTaskService.addGroupIdentityLink(task.getId(), "testGroup", PARTICIPANT);
        cmmnTaskService.deleteUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
        cmmnTaskService.deleteGroupIdentityLink(task.getId(), "testGroup", PARTICIPANT);
        cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", Collections.singletonMap("doNotThrowException", ""));
        Assert.assertEquals(11L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_CREATED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_NAME_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_PRIORITY_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_ASSIGNEE_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_OWNER_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_DUEDATE_CHANGED.name()).count());
        Assert.assertEquals(2L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_IDENTITY_LINK_ADDED.name()).count());
        Assert.assertEquals(2L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_IDENTITY_LINK_REMOVED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_COMPLETED.name()).count());
    }

    @Test
    public void startCaseAsyncWithForm() {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").startFormVariables(Collections.singletonMap("variable", "VariableValue")).startWithForm();
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void startCaseWithFormWithDisabledValidationOnEngineLevel() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.setFormFieldValidationEnabled(false);
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").startFormVariables(Collections.singletonMap("variable", "VariableValue")).startWithForm();
            Assert.assertThat(caseInstance, CoreMatchers.is(CoreMatchers.notNullValue()));
            Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        } finally {
            AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.setFormFieldValidationEnabled(true);
        }
    }

    @Test
    public void startCaseWithFormWithoutVariables() {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").startWithForm();
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeCaseTaskWithFormWithValidationDisabledOnConfigLevel() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.setFormFieldValidationEnabled(false);
        try {
            CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
            FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
            Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
            SideEffectTaskListener.reset();
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", Collections.singletonMap("var", "value"));
            Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        } finally {
            AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.setFormFieldValidationEnabled(true);
        }
    }

    @Test
    public void completeCaseTaskWithForm() {
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", Collections.singletonMap("var", "value"));
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeCaseTaskWithFormWithoutVariables() {
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeTaskWithoutValidationOnModelLevel() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getCmmnRepositoryService().createDeployment().addString("org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn", CaseWithFormTest.ONE_TASK_CASE.replace("CASE_VALIDATE_VALUE", "false").replace("TASK_VALIDATE_VALUE", "false")).deploy();
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
    }

    @Test
    public void completeTaskWithoutValidationOnModelLevelExpression() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getCmmnRepositoryService().createDeployment().addString("org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn", CaseWithFormTest.ONE_TASK_CASE.replace("CASE_VALIDATE_VALUE", "true").replace("TASK_VALIDATE_VALUE", "${allowValidation}")).deploy();
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").startFormVariables(Collections.singletonMap("allowValidation", true)).startWithForm();
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").variables(Collections.singletonMap("allowValidation", true)).start();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeTaskWithoutValidationOnModelLevelBadExpression() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getCmmnRepositoryService().createDeployment().addString("org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn", CaseWithFormTest.ONE_TASK_CASE.replace("CASE_VALIDATE_VALUE", "true").replace("TASK_VALIDATE_VALUE", "${BAD_EXPRESSION}")).deploy();
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            Assert.fail("Validation exception expected");
        } catch (RuntimeException e) {
            Assert.assertThat("Unknown property used in expression: ${BAD_EXPRESSION}", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeTaskWithValidationOnModelLevelStringExpression() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getCmmnRepositoryService().createDeployment().addString("org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn", CaseWithFormTest.ONE_TASK_CASE.replace("CASE_VALIDATE_VALUE", "true").replace("TASK_VALIDATE_VALUE", "${true}")).deploy();
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }

    @Test
    public void completeTaskWithoutValidationOnMissingModelLevel() {
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getCmmnRepositoryService().createDeployment().addString("org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn", CaseWithFormTest.ONE_TASK_CASE.replace("flowable:formFieldValidation=\"CASE_VALIDATE_VALUE\"", "").replace("flowable:formFieldValidation=\"TASK_VALIDATE_VALUE\"", "")).deploy();
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCaseWithForm").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(1));
        SideEffectTaskListener.reset();
        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            Assert.fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            Assert.assertThat("Validation failed by default", CoreMatchers.is(e.getMessage()));
        }
        Assert.assertThat(SideEffectTaskListener.getSideEffect(), CoreMatchers.is(0));
    }
}

