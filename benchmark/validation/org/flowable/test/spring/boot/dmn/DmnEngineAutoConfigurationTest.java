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
package org.flowable.test.spring.boot.dmn;


import org.flowable.app.engine.AppEngine;
import org.flowable.dmn.api.DmnEngineConfigurationApi;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineServicesAutoConfiguration;
import org.flowable.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


public class DmnEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, DmnEngineServicesAutoConfiguration.class, DmnEngineAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneDmnEngineWithBasicDataSource() {
        contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("dmnProcessEngineConfigurationConfigurer").doesNotHaveBean("dmnAppEngineConfigurationConfigurer");
            DmnEngine dmnEngine = context.getBean(.class);
            assertThat(dmnEngine).as("Dmn engine").isNotNull();
            assertAllServicesPresent(context, dmnEngine);
            assertAutoDeployment(context.getBean(.class));
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class);
            });
            deleteDeployments(dmnEngine);
        });
    }

    @Test
    public void dmnEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).hasBean("dmnProcessEngineConfigurationConfigurer").doesNotHaveBean("dmnAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            DmnEngineConfigurationApi dmnProcessConfigurationApi = dmnEngine(processEngine);
            DmnEngine dmnEngine = context.getBean(.class);
            assertThat(dmnEngine.getDmnEngineConfiguration()).as("Dmn Engine Configuration").isEqualTo(dmnProcessConfigurationApi);
            assertThat(dmnEngine).as("Dmn engine").isNotNull();
            assertAllServicesPresent(context, dmnEngine);
            assertAutoDeployment(context.getBean(.class));
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class);
            });
            deleteDeployments(dmnEngine);
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void dmnEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean("dmnProcessEngineConfigurationConfigurer").hasBean("dmnAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("app engine").isNotNull();
            DmnEngineConfigurationApi dmnProcessConfigurationApi = dmnEngine(appEngine);
            DmnEngine dmnEngine = context.getBean(.class);
            assertThat(dmnEngine.getDmnEngineConfiguration()).as("Dmn Engine Configuration").isEqualTo(dmnProcessConfigurationApi);
            assertThat(dmnEngine).as("Dmn engine").isNotNull();
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
            assertAllServicesPresent(context, dmnEngine);
            assertAutoDeploymentWithAppEngine(context);
            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(.class));
            deleteDeployments(dmnEngine);
        });
    }
}

