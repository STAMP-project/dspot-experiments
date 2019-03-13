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
package org.flowable.test.spring.boot.cmmn;


import org.flowable.app.engine.AppEngine;
import org.flowable.cmmn.api.CmmnEngineConfigurationApi;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
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
public class CmmnEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, IdmEngineAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class, CmmnEngineServicesAutoConfiguration.class, CmmnEngineAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneCmmnEngineWithBasicDataSource() {
        contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("cmmnProcessEngineConfigurationConfigurer").doesNotHaveBean("cmmnAppEngineConfigurationConfigurer");
            CmmnEngine cmmnEngine = context.getBean(.class);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();
            assertAllServicesPresent(context, cmmnEngine);
            assertAutoDeployment(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactlyInAnyOrder(.class, .class);
            });
            deleteDeployments(cmmnEngine);
        });
    }

    @Test
    public void cmmnEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(HibernateJpaAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).hasBean("cmmnProcessEngineConfigurationConfigurer").doesNotHaveBean("cmmnAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            CmmnEngineConfigurationApi cmmnProcessConfigurationApi = cmmnEngine(processEngine);
            CmmnEngine cmmnEngine = context.getBean(.class);
            assertThat(cmmnEngine.getCmmnEngineConfiguration()).as("Cmmn Engine Configuration").isEqualTo(cmmnProcessConfigurationApi);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();
            assertAllServicesPresent(context, cmmnEngine);
            assertAutoDeployment(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactlyInAnyOrder(.class, .class, .class);
            });
            deleteDeployments(processEngine);
            deleteDeployments(cmmnEngine);
        });
    }

    @Test
    public void cmmnEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(HibernateJpaAutoConfiguration.class, AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean("cmmnProcessEngineConfigurationConfigurer").hasBean("cmmnAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            CmmnEngineConfigurationApi cmmnProcessConfigurationApi = cmmnEngine(appEngine);
            CmmnEngine cmmnEngine = context.getBean(.class);
            assertThat(cmmnEngine.getCmmnEngineConfiguration()).as("Cmmn Engine Configuration").isEqualTo(cmmnProcessConfigurationApi);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();
            assertAllServicesPresent(context, cmmnEngine);
            assertAutoDeploymentWithAppEngine(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactlyInAnyOrder(.class, .class, .class, .class);
            });
            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(.class));
            deleteDeployments(cmmnEngine);
        });
    }
}

