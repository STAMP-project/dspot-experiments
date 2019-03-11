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
package org.flowable.test.spring.boot.app;


import org.flowable.app.engine.AppEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.db.DbIdGenerator;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Tijs Rademakers
 * @author Filip Hrisafov
 */
public class AppEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, IdmEngineAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneAppEngineWithBasicDatasource() {
        contextRunner.run(( context) -> {
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            assertAllServicesPresent(context, appEngine);
            assertAutoDeployment(context);
            deleteDeployments(appEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class);
            });
        });
    }

    @Test
    public void appEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ProcessEngineConfiguration processConfiguration = processEngine(appEngine);
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(processEngineConfiguration).as("Proccess Engine Configuration").isEqualTo(processConfiguration);
            assertThat(processEngine).as("Process engine").isNotNull();
            assertAllServicesPresent(context, appEngine);
            assertAutoDeployment(context);
            processEngineConfiguration.getIdentityService().setAuthenticatedUserId("test");
            ProcessInstance processInstance = processEngineConfiguration.getRuntimeService().startProcessInstanceByKey("vacationRequest");
            Task task = processEngineConfiguration.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertThat(task).isNotNull();
            deleteDeployments(appEngine);
            deleteDeployments(processEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
        });
    }

    @Test
    public void appEngineWithProcessEngineAndTaskIdGenerator() {
        contextRunner.withUserConfiguration(AppEngineAutoConfigurationTest.CustomIdGeneratorConfiguration.class).withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(processEngineConfiguration.getIdGenerator().getNextId()).as("Process id generator must be DB id generator").doesNotContain("-");
            AppEngine appEngine = context.getBean(.class);
            deleteDeployments(appEngine);
            deleteDeployments(processEngine);
        });
    }

    @Configuration
    static class CustomIdGeneratorConfiguration {
        @Bean
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customIdGeneratorConfigurer() {
            return ( engineConfiguration) -> engineConfiguration.setIdGenerator(new DbIdGenerator());
        }
    }
}

