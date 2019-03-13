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
package org.flowable.test.spring.boot.process;


import javax.persistence.EntityManagerFactory;
import org.flowable.app.engine.AppEngine;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.db.DbIdGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
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
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class ProcessEngineAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ProcessEngineServicesAutoConfiguration.class, ProcessEngineAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class, TransactionAutoConfiguration.class)).withUserConfiguration(CustomUserEngineConfigurerConfiguration.class).withClassLoader(new FilteredClassLoader(EntityManagerFactory.class));

    @Test
    public void standaloneProcessEngineWithBasicDatasource() {
        contextRunner.run(( context) -> {
            assertThat(context).as("Process engine").hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).doesNotHaveBean("processAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine.getProcessEngineConfiguration().getIdGenerator()).isInstanceOf(.class);
            assertAllServicesPresent(context, processEngine);
            assertAutoDeployment(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class);
            });
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void processEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(DataSourceTransactionManagerAutoConfiguration.class, AppEngineServicesAutoConfiguration.class, AppEngineAutoConfiguration.class, IdmEngineAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasBean("processAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ProcessEngineConfiguration processConfiguration = processEngine(appEngine);
            ProcessEngine processEngine = context.getBean(.class);
            assertThat(processEngine.getProcessEngineConfiguration()).as("Proccess Engine Configuration").isEqualTo(processConfiguration);
            assertThat(processEngine).as("Process engine").isNotNull();
            assertAllServicesPresent(context, processEngine);
            assertAutoDeploymentWithAppEngine(context);
            assertThat(context).hasSingleBean(.class).getBean(.class).satisfies(( configuration) -> {
                assertThat(configuration.getInvokedConfigurations()).containsExactly(.class, .class, .class);
            });
            deleteDeployments(appEngine);
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void processEngineWithCustomIdGenerator() {
        contextRunner.withUserConfiguration(ProcessEngineAutoConfigurationTest.CustomIdGeneratorConfiguration.class).run(( context) -> {
            assertThat(context).as("Process engine").hasSingleBean(.class);
            assertThat(context).as("IdGenerator").doesNotHaveBean(.class);
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator()).isInstanceOfSatisfying(.class, ( dbIdGenerator) -> {
                assertThat(dbIdGenerator.getIdBlockSize()).isEqualTo(engineConfiguration.getIdBlockSize());
                assertThat(dbIdGenerator.getCommandExecutor()).isEqualTo(engineConfiguration.getCommandExecutor());
                assertThat(dbIdGenerator.getCommandConfig()).isEqualToComparingFieldByField(engineConfiguration.getDefaultCommandConfig().transactionRequiresNew());
            });
        });
    }

    @Test
    public void processEngineWithCustomIdGeneratorAsBean() {
        contextRunner.withUserConfiguration(ProcessEngineAutoConfigurationTest.CustomBeanIdGeneratorConfiguration.class).run(( context) -> {
            assertThat(context).as("Process engine").hasSingleBean(.class).as("Id generator").hasSingleBean(.class);
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator()).isInstanceOfSatisfying(.class, ( dbIdGenerator) -> {
                assertThat(dbIdGenerator.getIdBlockSize()).isEqualTo(engineConfiguration.getIdBlockSize());
                assertThat(dbIdGenerator.getCommandExecutor()).isEqualTo(engineConfiguration.getCommandExecutor());
                assertThat(dbIdGenerator.getCommandConfig()).isEqualToComparingFieldByField(engineConfiguration.getDefaultCommandConfig().transactionRequiresNew());
            }).isEqualTo(context.getBean(.class));
        });
    }

    @Test
    public void processEngineWithMultipleCustomIdGeneratorsAsBean() {
        contextRunner.withUserConfiguration(ProcessEngineAutoConfigurationTest.CustomBeanIdGeneratorConfiguration.class, ProcessEngineAutoConfigurationTest.SecondCustomBeanIdGeneratorConfiguration.class).run(( context) -> {
            assertThat(context).as("Process engine").hasSingleBean(.class).as("Custom Id generator").hasBean("customIdGenerator").as("Second Custom Id generator").hasBean("secondCustomIdGenerator");
            Map<String, IdGenerator> idGenerators = context.getBeansOfType(.class);
            assertThat(idGenerators).containsOnlyKeys("customIdGenerator", "secondCustomIdGenerator");
            IdGenerator customIdGenerator = idGenerators.get("customIdGenerator");
            assertThat(customIdGenerator).isInstanceOf(.class);
            IdGenerator secondCustomIdGenerator = idGenerators.get("secondCustomIdGenerator");
            assertThat(secondCustomIdGenerator).isInstanceOf(.class);
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator()).isInstanceOf(.class).isNotEqualTo(customIdGenerator).isNotEqualTo(secondCustomIdGenerator);
        });
    }

    @Test
    public void processEngineWithMultipleCustomIdGeneratorsAndAQualifiedProcessOneAsBean() {
        contextRunner.withUserConfiguration(ProcessEngineAutoConfigurationTest.CustomBeanIdGeneratorConfiguration.class, ProcessEngineAutoConfigurationTest.SecondCustomBeanIdGeneratorConfiguration.class, ProcessEngineAutoConfigurationTest.ProcessQualifiedCustomBeanIdGeneratorConfiguration.class).run(( context) -> {
            assertThat(context).as("Process engine").hasSingleBean(.class).as("Custom Id generator").hasBean("customIdGenerator").as("Second Custom Id generator").hasBean("secondCustomIdGenerator").as("Process Custom Id generator").hasBean("processQualifiedCustomIdGenerator");
            Map<String, IdGenerator> idGenerators = context.getBeansOfType(.class);
            assertThat(idGenerators).containsOnlyKeys("customIdGenerator", "secondCustomIdGenerator", "processQualifiedCustomIdGenerator");
            IdGenerator customIdGenerator = idGenerators.get("customIdGenerator");
            assertThat(customIdGenerator).isInstanceOf(.class);
            IdGenerator secondCustomIdGenerator = idGenerators.get("secondCustomIdGenerator");
            assertThat(secondCustomIdGenerator).isInstanceOf(.class);
            IdGenerator processCustomIdGenerator = idGenerators.get("processQualifiedCustomIdGenerator");
            assertThat(processCustomIdGenerator).isInstanceOf(.class);
            ProcessEngine processEngine = context.getBean(.class);
            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator()).isInstanceOf(.class).isNotEqualTo(customIdGenerator).isNotEqualTo(secondCustomIdGenerator).isEqualTo(processCustomIdGenerator);
        });
    }

    @Configuration
    static class CustomIdGeneratorConfiguration {
        @Bean
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customIdGeneratorConfigurer() {
            return ( engineConfiguration) -> engineConfiguration.setIdGenerator(new DbIdGenerator());
        }
    }

    @Configuration
    static class CustomBeanIdGeneratorConfiguration {
        @Bean
        public IdGenerator customIdGenerator() {
            return new DbIdGenerator();
        }
    }

    @Configuration
    static class SecondCustomBeanIdGeneratorConfiguration {
        @Bean
        public IdGenerator secondCustomIdGenerator() {
            return new StrongUuidGenerator();
        }
    }

    @Configuration
    static class ProcessQualifiedCustomBeanIdGeneratorConfiguration {
        @Bean
        @Process
        public IdGenerator processQualifiedCustomIdGenerator() {
            return new StrongUuidGenerator();
        }
    }
}

