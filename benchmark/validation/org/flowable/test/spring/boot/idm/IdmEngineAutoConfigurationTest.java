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
package org.flowable.test.spring.boot.idm;


import org.flowable.app.engine.AppEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.idm.api.IdmEngineConfigurationApi;
import org.flowable.idm.api.PasswordEncoder;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class IdmEngineAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class, IdmEngineAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneIdmEngineWithBasicDataSource() {
        contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("idmProcessEngineConfigurationConfigurer").doesNotHaveBean("idmAppEngineConfigurationConfigurer");
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(idmEngine).as("Idm engine").isNotNull();
            assertAllServicesPresent(context, idmEngine);
            assertThat(context).hasSingleBean(.class).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class);
            });
            PasswordEncoder flowablePasswordEncoder = idmEngine.getIdmEngineConfiguration().getPasswordEncoder();
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = context.getBean(.class);
            assertThat(flowablePasswordEncoder).isInstanceOfSatisfying(.class, ( springEncoder) -> {
                assertThat(springEncoder.getSpringEncodingProvider()).isEqualTo(passwordEncoder);
            });
            assertThat(passwordEncoder).isInstanceOf(.class);
        });
    }

    @Test
    public void standaloneIdmEngineWithBCryptPasswordEncoder() {
        contextRunner.withPropertyValues("flowable.idm.password-encoder=spring_bcrypt").run(( context) -> {
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(context).hasSingleBean(.class);
            PasswordEncoder flowablePasswordEncoder = idmEngine.getIdmEngineConfiguration().getPasswordEncoder();
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = context.getBean(.class);
            assertThat(flowablePasswordEncoder).isInstanceOfSatisfying(.class, ( springEncoder) -> {
                assertThat(springEncoder.getSpringEncodingProvider()).isEqualTo(passwordEncoder);
            });
            assertThat(passwordEncoder).isInstanceOf(.class);
        });
    }

    @Test
    public void standaloneIdmEngineWithDelegatingPasswordEncoder() {
        contextRunner.withPropertyValues("flowable.idm.password-encoder=spring_delegating").run(( context) -> {
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(context).hasSingleBean(.class);
            PasswordEncoder flowablePasswordEncoder = idmEngine.getIdmEngineConfiguration().getPasswordEncoder();
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = context.getBean(.class);
            assertThat(flowablePasswordEncoder).isInstanceOfSatisfying(.class, ( springEncoder) -> {
                assertThat(springEncoder.getSpringEncodingProvider()).isEqualTo(passwordEncoder);
            });
            assertThat(passwordEncoder).isInstanceOf(.class);
            assertThat(flowablePasswordEncoder.encode("test", null)).as("encoded password").startsWith("{bcrypt}");
            assertThatThrownBy(() -> flowablePasswordEncoder.isMatches("test", "test", null)).as("encoder matches password").isInstanceOf(.class).hasMessage("There is no PasswordEncoder mapped for the id \"null\"");
        });
    }

    @Test
    public void standaloneIdmEngineWithDelegatingBCryptDefaultPasswordEncoder() {
        contextRunner.withPropertyValues("flowable.idm.password-encoder=spring_delegating_bcrypt").run(( context) -> {
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(context).hasSingleBean(.class);
            PasswordEncoder flowablePasswordEncoder = idmEngine.getIdmEngineConfiguration().getPasswordEncoder();
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = context.getBean(.class);
            assertThat(flowablePasswordEncoder).isInstanceOfSatisfying(.class, ( springEncoder) -> {
                assertThat(springEncoder.getSpringEncodingProvider()).isEqualTo(passwordEncoder);
            });
            assertThat(passwordEncoder).isInstanceOf(.class);
            assertThat(flowablePasswordEncoder.encode("test", null)).as("encoded password").startsWith("{bcrypt}");
            assertThat(flowablePasswordEncoder.isMatches("test", "test", null)).as("encoder matchers clear text password").isFalse();
            assertThat(flowablePasswordEncoder.isMatches("test", new BCryptPasswordEncoder().encode("test"), null)).as("encoder matchers only bcrypt text password").isTrue();
        });
    }

    @Test
    public void standaloneIdmEngineWithDelegatingNoopDefaultPasswordEncoder() {
        contextRunner.withPropertyValues("flowable.idm.password-encoder=spring_delegating_noop").run(( context) -> {
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(context).hasSingleBean(.class);
            PasswordEncoder flowablePasswordEncoder = idmEngine.getIdmEngineConfiguration().getPasswordEncoder();
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = context.getBean(.class);
            assertThat(flowablePasswordEncoder).isInstanceOfSatisfying(.class, ( springEncoder) -> {
                assertThat(springEncoder.getSpringEncodingProvider()).isEqualTo(passwordEncoder);
            });
            assertThat(passwordEncoder).isInstanceOf(.class);
            assertThat(flowablePasswordEncoder.encode("test", null)).as("encoded password").startsWith("{bcrypt}");
            assertThat(flowablePasswordEncoder.isMatches("test", "test", null)).as("encoder matchers clear text password").isTrue();
            assertThat(flowablePasswordEncoder.isMatches("test", new BCryptPasswordEncoder().encode("test"), null)).as("encoder matchers only bcrypt text password").isFalse();
        });
    }

    @Test
    public void idmEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class).hasBean("idmProcessEngineConfigurationConfigurer").doesNotHaveBean("idmAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            IdmEngineConfigurationApi idmProcessConfiguration = idmEngine(processEngine);
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(idmEngine).as("Idm engine").isNotNull();
            assertThat(idmEngine.getIdmEngineConfiguration()).as("Idm Engine Configuration").isEqualTo(idmProcessConfiguration);
            assertAllServicesPresent(context, idmEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class);
            });
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void idmEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean("idmProcessEngineConfigurationConfigurer").hasBean("idmAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            IdmEngineConfigurationApi idmProcessConfiguration = idmEngine(appEngine);
            IdmEngine idmEngine = context.getBean(.class);
            assertThat(idmEngine).as("Idm engine").isNotNull();
            assertThat(idmEngine.getIdmEngineConfiguration()).as("Idm Engine Configuration").isEqualTo(idmProcessConfiguration);
            assertAllServicesPresent(context, idmEngine);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(.class));
        });
    }
}

