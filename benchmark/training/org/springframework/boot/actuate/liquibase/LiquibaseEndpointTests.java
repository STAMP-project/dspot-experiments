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
package org.springframework.boot.actuate.liquibase;


import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link LiquibaseEndpoint}.
 *
 * @author Edd? Mel?ndez
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class LiquibaseEndpointTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true");

    @Test
    public void liquibaseReportIsReturned() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointTests.Config.class).run(( context) -> assertThat(context.getBean(.class).liquibaseBeans().getContexts().get(context.getId()).getLiquibaseBeans()).hasSize(1));
    }

    @Test
    public void invokeWithCustomSchema() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointTests.Config.class).withPropertyValues("spring.liquibase.default-schema=CUSTOMSCHEMA", "spring.datasource.schema=classpath:/db/create-custom-schema.sql").run(( context) -> assertThat(context.getBean(.class).liquibaseBeans().getContexts().get(context.getId()).getLiquibaseBeans()).hasSize(1));
    }

    @Test
    public void connectionAutoCommitPropertyIsReset() {
        this.contextRunner.withUserConfiguration(LiquibaseEndpointTests.Config.class).run(( context) -> {
            DataSource dataSource = context.getBean(.class);
            assertThat(getAutoCommit(dataSource)).isTrue();
            context.getBean(.class).liquibaseBeans();
            assertThat(getAutoCommit(dataSource)).isTrue();
        });
    }

    @Configuration
    public static class Config {
        @Bean
        public LiquibaseEndpoint endpoint(ApplicationContext context) {
            return new LiquibaseEndpoint(context);
        }
    }
}

