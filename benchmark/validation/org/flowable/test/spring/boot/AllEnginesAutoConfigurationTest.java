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
package org.flowable.test.spring.boot;


import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.spring.SpringCmmnEngineConfiguration;
import org.flowable.cmmn.spring.configurator.SpringCmmnEngineConfigurator;
import org.flowable.content.spring.SpringContentEngineConfiguration;
import org.flowable.content.spring.configurator.SpringContentEngineConfigurator;
import org.flowable.dmn.spring.SpringDmnEngineConfiguration;
import org.flowable.dmn.spring.configurator.SpringDmnEngineConfigurator;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.spring.configurator.SpringProcessEngineConfigurator;
import org.flowable.form.spring.SpringFormEngineConfiguration;
import org.flowable.form.spring.configurator.SpringFormEngineConfigurator;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.idm.spring.configurator.SpringIdmEngineConfigurator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineServicesAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineServicesAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.task.api.Task;
import org.flowable.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class AllEnginesAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, IdmEngineAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class, CmmnEngineAutoConfiguration.class, CmmnEngineServicesAutoConfiguration.class, ContentEngineAutoConfiguration.class, ContentEngineServicesAutoConfiguration.class, DmnEngineAutoConfiguration.class, DmnEngineServicesAutoConfiguration.class, FormEngineAutoConfiguration.class, FormEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void usingAllAutoConfigurationsTogetherShouldWorkCorrectly() {
        contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class);
            SpringAppEngineConfiguration appEngineConfiguration = context.getBean(.class);
            SpringCmmnEngineConfiguration cmmnEngineConfiguration = context.getBean(.class);
            SpringContentEngineConfiguration contentEngineConfiguration = context.getBean(.class);
            SpringDmnEngineConfiguration dmnEngineConfiguration = context.getBean(.class);
            SpringFormEngineConfiguration formEngineConfiguration = context.getBean(.class);
            SpringIdmEngineConfiguration idmEngineConfiguration = context.getBean(.class);
            SpringProcessEngineConfiguration processEngineConfiguration = context.getBean(.class);
            assertThat(appEngineConfiguration.getEngineConfigurations()).as("AppEngine configurations").containsOnly(entry(EngineConfigurationConstants.KEY_APP_ENGINE_CONFIG, appEngineConfiguration), entry(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG, cmmnEngineConfiguration), entry(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG, dmnEngineConfiguration), entry(EngineConfigurationConstants.KEY_CONTENT_ENGINE_CONFIG, contentEngineConfiguration), entry(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG, formEngineConfiguration), entry(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG, idmEngineConfiguration), entry(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG, processEngineConfiguration)).containsAllEntriesOf(cmmnEngineConfiguration.getEngineConfigurations()).containsAllEntriesOf(dmnEngineConfiguration.getEngineConfigurations()).containsAllEntriesOf(contentEngineConfiguration.getEngineConfigurations()).containsAllEntriesOf(formEngineConfiguration.getEngineConfigurations()).containsAllEntriesOf(idmEngineConfiguration.getEngineConfigurations()).containsAllEntriesOf(processEngineConfiguration.getEngineConfigurations());
            SpringCmmnEngineConfigurator cmmnConfigurator = context.getBean(.class);
            SpringContentEngineConfigurator contentConfigurator = context.getBean(.class);
            SpringDmnEngineConfigurator dmnConfigurator = context.getBean(.class);
            SpringFormEngineConfigurator formConfigurator = context.getBean(.class);
            SpringIdmEngineConfigurator idmConfigurator = context.getBean(.class);
            SpringProcessEngineConfigurator processConfigurator = context.getBean(.class);
            assertThat(appEngineConfiguration.getConfigurators()).as("AppEngineConfiguration configurators").containsExactly(processConfigurator, contentConfigurator, dmnConfigurator, formConfigurator, cmmnConfigurator);
            assertThat(cmmnEngineConfiguration.getIdmEngineConfigurator()).as("CmmnEngineConfiguration idmEngineConfigurator").isNull();
            assertThat(processEngineConfiguration.getIdmEngineConfigurator()).as("ProcessEngineConfiguration idmEngineConfigurator").isNull();
            assertThat(appEngineConfiguration.getIdmEngineConfigurator()).as("AppEngineConfiguration idmEngineConfigurator").isSameAs(idmConfigurator);
            assertThat(appEngineConfiguration.getExpressionManager()).isInstanceOf(.class);
            assertThat(appEngineConfiguration.getExpressionManager().getBeans()).isNull();
            assertThat(processEngineConfiguration.getExpressionManager()).isInstanceOf(.class);
            assertThat(processEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(.class);
            assertThat(cmmnEngineConfiguration.getExpressionManager()).isInstanceOf(.class);
            assertThat(cmmnEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(.class);
            assertThat(dmnEngineConfiguration.getExpressionManager()).isInstanceOf(.class);
            assertThat(dmnEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(.class);
            assertThat(formEngineConfiguration.getExpressionManager()).isInstanceOf(.class);
            assertThat(formEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(.class);
            deleteDeployments(context.getBean(.class));
            deleteDeployments(context.getBean(.class));
            deleteDeployments(context.getBean(.class));
            deleteDeployments(context.getBean(.class));
            deleteDeployments(context.getBean(.class));
        });
    }

    @Test
    public void testInclusiveGatewayProcessTask() {
        contextRunner.run(( context) -> {
            SpringCmmnEngineConfiguration cmmnEngineConfiguration = context.getBean(.class);
            SpringProcessEngineConfiguration processEngineConfiguration = context.getBean(.class);
            CmmnRuntimeService cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
            CmmnHistoryService cmmnHistoryService = cmmnEngineConfiguration.getCmmnHistoryService();
            RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
            TaskService taskService = processEngineConfiguration.getTaskService();
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
            assertEquals(0, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
            assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionId("theTask").planItemInstanceState(PlanItemInstanceState.ACTIVE).list();
            assertEquals(1, planItemInstances.size());
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
            assertEquals("No process instance started", 1L, runtimeService.createProcessInstanceQuery().count());
            assertEquals(2, taskService.createTaskQuery().count());
            List<Task> tasks = taskService.createTaskQuery().list();
            taskService.complete(tasks.get(0).getId());
            taskService.complete(tasks.get(1).getId());
            assertEquals(0, taskService.createTaskQuery().count());
            assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionId("theTask2").list();
            assertEquals(1, planItemInstances.size());
            assertEquals("Task Two", planItemInstances.get(0).getName());
            assertEquals(PlanItemInstanceState.ENABLED, planItemInstances.get(0).getState());
        });
    }
}

