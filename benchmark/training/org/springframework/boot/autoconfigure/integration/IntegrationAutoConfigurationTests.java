/**
 * Copyright 2012-2019 the original author or authors.
 *
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
package org.springframework.boot.autoconfigure.integration;


import javax.management.MBeanServer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.endpoint.MessageProcessorMessageSource;
import org.springframework.integration.gateway.RequestReplyExchanger;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jmx.export.MBeanExporter;


/**
 * Tests for {@link IntegrationAutoConfiguration}.
 *
 * @author Artem Bilan
 * @author Stephane Nicoll
 * @author Vedran Pavic
 */
public class IntegrationAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JmxAutoConfiguration.class, IntegrationAutoConfiguration.class));

    @Test
    public void integrationIsAvailable() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void explicitIntegrationComponentScan() {
        this.contextRunner.withUserConfiguration(IntegrationAutoConfigurationTests.CustomIntegrationComponentScanConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void noMBeanServerAvailable() {
        ApplicationContextRunner contextRunnerWithoutJmx = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(IntegrationAutoConfiguration.class));
        contextRunnerWithoutJmx.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void parentContext() {
        this.contextRunner.run(( context) -> this.contextRunner.withParent(context).withPropertyValues("spring.jmx.default_domain=org.foo").run(( child) -> assertThat(child).hasSingleBean(.class)));
    }

    @Test
    public void enableJmxIntegration() {
        this.contextRunner.withPropertyValues("spring.jmx.enabled=true").run(( context) -> {
            MBeanServer mBeanServer = context.getBean(.class);
            assertThat(mBeanServer.getDomains()).contains("org.springframework.integration", "org.springframework.integration.monitor");
            assertThat(context).hasBean(IntegrationManagementConfigurer.MANAGEMENT_CONFIGURER_NAME);
        });
    }

    @Test
    public void jmxIntegrationIsDisabledByDefault() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void customizeJmxDomain() {
        this.contextRunner.withPropertyValues("spring.jmx.enabled=true", "spring.jmx.default_domain=org.foo").run(( context) -> {
            MBeanServer mBeanServer = context.getBean(.class);
            assertThat(mBeanServer.getDomains()).contains("org.foo").doesNotContain("org.springframework.integration", "org.springframework.integration.monitor");
        });
    }

    @Test
    public void primaryExporterIsAllowed() {
        this.contextRunner.withPropertyValues("spring.jmx.enabled=true").withUserConfiguration(IntegrationAutoConfigurationTests.CustomMBeanExporter.class).run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(2);
            assertThat(context.getBean(.class)).isSameAs(context.getBean("myMBeanExporter"));
        });
    }

    @Test
    public void integrationJdbcDataSourceInitializerEnabled() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, IntegrationAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true", "spring.integration.jdbc.initialize-schema=always").run(( context) -> {
            IntegrationProperties properties = context.getBean(.class);
            assertThat(properties.getJdbc().getInitializeSchema()).isEqualTo(DataSourceInitializationMode.ALWAYS);
            JdbcOperations jdbc = context.getBean(.class);
            assertThat(jdbc.queryForList("select * from INT_MESSAGE")).isEmpty();
            assertThat(jdbc.queryForList("select * from INT_GROUP_TO_MESSAGE")).isEmpty();
            assertThat(jdbc.queryForList("select * from INT_MESSAGE_GROUP")).isEmpty();
            assertThat(jdbc.queryForList("select * from INT_LOCK")).isEmpty();
            assertThat(jdbc.queryForList("select * from INT_CHANNEL_MESSAGE")).isEmpty();
        });
    }

    @Test
    public void integrationJdbcDataSourceInitializerDisabled() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, IntegrationAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true", "spring.integration.jdbc.initialize-schema=never").run(( context) -> {
            IntegrationProperties properties = context.getBean(.class);
            assertThat(properties.getJdbc().getInitializeSchema()).isEqualTo(DataSourceInitializationMode.NEVER);
            JdbcOperations jdbc = context.getBean(.class);
            assertThatExceptionOfType(.class).isThrownBy(() -> jdbc.queryForList("select * from INT_MESSAGE"));
        });
    }

    @Test
    public void integrationJdbcDataSourceInitializerEnabledByDefaultWithEmbeddedDb() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, IntegrationAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true").run(( context) -> {
            IntegrationProperties properties = context.getBean(.class);
            assertThat(properties.getJdbc().getInitializeSchema()).isEqualTo(DataSourceInitializationMode.EMBEDDED);
            JdbcOperations jdbc = context.getBean(.class);
            assertThat(jdbc.queryForList("select * from INT_MESSAGE")).isEmpty();
        });
    }

    @Test
    public void integrationEnablesDefaultCounts() {
        this.contextRunner.withUserConfiguration(IntegrationAutoConfigurationTests.MessageSourceConfiguration.class).run(( context) -> {
            assertThat(context).hasBean("myMessageSource");
            assertThat(((MessageProcessorMessageSource) (context.getBean("myMessageSource"))).isCountsEnabled()).isTrue();
        });
    }

    @Configuration
    static class CustomMBeanExporter {
        @Bean
        @Primary
        public MBeanExporter myMBeanExporter() {
            return Mockito.mock(MBeanExporter.class);
        }
    }

    @Configuration
    @IntegrationComponentScan
    static class CustomIntegrationComponentScanConfiguration {}

    @MessagingGateway
    public interface TestGateway extends RequestReplyExchanger {}

    @Configuration
    static class MessageSourceConfiguration {
        @Bean
        public MessageSource<?> myMessageSource() {
            return new MessageProcessorMessageSource(Mockito.mock(MessageProcessor.class));
        }
    }
}

