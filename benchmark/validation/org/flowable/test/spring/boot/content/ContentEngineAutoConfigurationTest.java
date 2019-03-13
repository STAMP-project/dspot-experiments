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
package org.flowable.test.spring.boot.content;


import org.flowable.app.engine.AppEngine;
import org.flowable.content.api.ContentEngineConfigurationApi;
import org.flowable.content.engine.ContentEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineServicesAutoConfiguration;
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
public class ContentEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, ContentEngineServicesAutoConfiguration.class, ContentEngineAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneContentEngineWithBasicDataSource() {
        contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("contentProcessEngineConfigurationConfigurer").doesNotHaveBean("contentAppEngineConfigurationConfigurer");
            ContentEngine contentEngine = context.getBean(.class);
            assertThat(contentEngine).as("Content engine").isNotNull();
            assertAllServicesPresent(context, contentEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class);
            });
        });
    }

    @Test
    public void contentEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(HibernateJpaAutoConfiguration.class, ProcessEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).hasBean("contentProcessEngineConfigurationConfigurer").doesNotHaveBean("contentAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            ContentEngineConfigurationApi contentProcessConfigurationApi = contentEngine(processEngine);
            ContentEngine contentEngine = context.getBean(.class);
            assertThat(contentEngine).as("Content engine").isNotNull();
            assertThat(contentEngine.getContentEngineConfiguration()).as("Content Engine Configuration").isEqualTo(contentProcessConfigurationApi);
            assertAllServicesPresent(context, contentEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class);
            });
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void contentEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(HibernateJpaAutoConfiguration.class, AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean("contentProcessEngineConfigurationConfigurer").hasBean("contentAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ContentEngineConfigurationApi contentProcessConfigurationApi = contentEngine(appEngine);
            ContentEngine contentEngine = context.getBean(.class);
            assertThat(contentEngine).as("Content engine").isNotNull();
            assertThat(contentEngine.getContentEngineConfiguration()).as("Content Engine Configuration").isEqualTo(contentProcessConfigurationApi);
            assertAllServicesPresent(context, contentEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(.class));
        });
    }
}

