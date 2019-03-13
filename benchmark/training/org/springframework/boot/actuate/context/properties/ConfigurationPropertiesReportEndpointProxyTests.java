/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.context.properties;


import EmbeddedDatabaseType.HSQL;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ApplicationConfigurationProperties;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ConfigurationPropertiesBeanDescriptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Tests for {@link ConfigurationPropertiesReportEndpoint} when used against a proxy
 * class.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ConfigurationPropertiesReportEndpointProxyTests {
    @Test
    public void testWithProxyClass() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointProxyTests.Config.class, ConfigurationPropertiesReportEndpointProxyTests.SqlExecutor.class);
        contextRunner.run(( context) -> {
            ApplicationConfigurationProperties applicationProperties = context.getBean(.class).configurationProperties();
            assertThat(applicationProperties.getContexts().get(context.getId()).getBeans().values().stream().map(ConfigurationPropertiesBeanDescriptor::getPrefix).filter("executor.sql"::equals).findFirst()).isNotEmpty();
        });
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass = false)
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(HSQL).build();
        }
    }

    public interface Executor {
        void execute();
    }

    public abstract static class AbstractExecutor implements ConfigurationPropertiesReportEndpointProxyTests.Executor {}

    @Component
    @ConfigurationProperties("executor.sql")
    public static class SqlExecutor extends ConfigurationPropertiesReportEndpointProxyTests.AbstractExecutor {
        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void execute() {
        }
    }
}

