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
package org.springframework.boot.actuate.autoconfigure.liquibase;


import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link LiquibaseEndpointAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class LiquibaseEndpointAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(LiquibaseEndpointAutoConfiguration.class));

    @Test
    public void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=liquibase").withUserConfiguration(LiquibaseEndpointAutoConfigurationTests.LiquibaseConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointAutoConfigurationTests.LiquibaseConfiguration.class).withPropertyValues("management.endpoint.liquibase.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void disablesCloseOfDataSourceWhenEndpointIsEnabled() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointAutoConfigurationTests.DataSourceClosingLiquibaseConfiguration.class).withPropertyValues("management.endpoints.web.exposure.include=liquibase").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).hasFieldOrPropertyWithValue("closeDataSourceOnceMigrated", false);
        });
    }

    @Test
    public void doesNotDisableCloseOfDataSourceWhenEndpointIsDisabled() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointAutoConfigurationTests.DataSourceClosingLiquibaseConfiguration.class).withPropertyValues("management.endpoint.liquibase.enabled:false").run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            DataSourceClosingSpringLiquibase bean = context.getBean(.class);
            assertThat(bean).hasFieldOrPropertyWithValue("closeDataSourceOnceMigrated", true);
        });
    }

    @Configuration
    static class LiquibaseConfiguration {
        @Bean
        public SpringLiquibase liquibase() {
            return Mockito.mock(SpringLiquibase.class);
        }
    }

    @Configuration
    static class DataSourceClosingLiquibaseConfiguration {
        @Bean
        public SpringLiquibase liquibase() {
            return new DataSourceClosingSpringLiquibase() {
                private boolean propertiesSet = false;

                @Override
                public void setCloseDataSourceOnceMigrated(boolean closeDataSourceOnceMigrated) {
                    if (this.propertiesSet) {
                        throw new IllegalStateException(("setCloseDataSourceOnceMigrated " + "invoked after afterPropertiesSet"));
                    }
                    super.setCloseDataSourceOnceMigrated(closeDataSourceOnceMigrated);
                }

                @Override
                public void afterPropertiesSet() throws LiquibaseException {
                    this.propertiesSet = true;
                }
            };
        }
    }
}

