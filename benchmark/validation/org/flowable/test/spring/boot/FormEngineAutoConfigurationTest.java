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


import org.flowable.app.engine.AppEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.form.api.FormEngineConfigurationApi;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.engine.FormEngine;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineServicesAutoConfiguration;
import org.flowable.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


public class FormEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, FormEngineServicesAutoConfiguration.class, FormEngineAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneFormEngineWithBasicDataSource() {
        contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("formProcessEngineConfigurationConfigurer").doesNotHaveBean("formAppEngineConfigurationConfigurer");
            FormEngine formEngine = context.getBean(.class);
            assertThat(formEngine).as("Form engine").isNotNull();
            assertThat(context.getBean(.class)).as("Form service").isEqualTo(formEngine.getFormService());
            FormRepositoryService repositoryService = context.getBean(.class);
            assertThat(repositoryService).as("Form repository service").isEqualTo(formEngine.getFormRepositoryService());
            assertThat(context.getBean(.class)).as("Form management service").isEqualTo(formEngine.getFormManagementService());
            assertThat(context.getBean(.class)).as("Form engine configuration").isEqualTo(formEngine.getFormEngineConfiguration());
            assertAutoDeployment(repositoryService);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class);
            });
            deleteDeployments(formEngine);
        });
    }

    @Test
    public void formEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).hasBean("formProcessEngineConfigurationConfigurer").doesNotHaveBean("formAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            FormEngineConfigurationApi formProcessConfigurationApi = formEngine(processEngine);
            FormEngineConfigurationApi formEngine = context.getBean(.class);
            assertThat(formEngine).isEqualTo(formProcessConfigurationApi);
            assertThat(formEngine).as("Form engine").isNotNull();
            assertThat(context.getBean(.class)).as("Form service").isEqualTo(formEngine.getFormService());
            FormRepositoryService repositoryService = context.getBean(.class);
            assertThat(repositoryService).as("Form repository service").isEqualTo(formEngine.getFormRepositoryService());
            assertThat(context.getBean(.class)).as("Form management service").isEqualTo(formEngine.getFormManagementService());
            assertAutoDeployment(repositoryService);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class);
            });
            deleteDeployments(processEngine);
            deleteDeployments(context.getBean(.class));
        });
    }

    @Test
    public void formEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean("formProcessEngineConfigurationConfigurer").hasBean("formAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            FormEngineConfigurationApi formProcessConfigurationApi = formEngine(appEngine);
            FormEngineConfigurationApi formEngine = context.getBean(.class);
            assertThat(formEngine).isEqualTo(formProcessConfigurationApi);
            assertThat(formEngine).as("Form engine").isNotNull();
            assertThat(context.getBean(.class)).as("Form service").isEqualTo(formEngine.getFormService());
            FormRepositoryService repositoryService = context.getBean(.class);
            assertThat(repositoryService).as("Form repository service").isEqualTo(formEngine.getFormRepositoryService());
            assertThat(context.getBean(.class)).as("Form management service").isEqualTo(formEngine.getFormManagementService());
            assertAutoDeploymentWithAppEngine(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(.class));
            deleteDeployments(context.getBean(.class));
        });
    }
}

