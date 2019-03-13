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
package org.springframework.boot.autoconfigure.quartz;


import java.util.UUID;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Tests for {@link QuartzDataSourceInitializer}.
 *
 * @author Stephane Nicoll
 */
public class QuartzDataSourceInitializerTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class)).withPropertyValues(("spring.datasource.url=" + (String.format("jdbc:h2:mem:test-%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", UUID.randomUUID().toString()))));

    @Test
    public void commentPrefixCanBeCustomized() {
        this.contextRunner.withUserConfiguration(QuartzDataSourceInitializerTests.TestConfiguration.class).withPropertyValues("spring.quartz.jdbc.comment-prefix=##", "spring.quartz.jdbc.schema=classpath:org/springframework/boot/autoconfigure/quartz/tables_@@platform@@.sql").run(( context) -> {
            JdbcTemplate jdbcTemplate = context.getBean(.class);
            assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM QRTZ_TEST_TABLE", .class)).isEqualTo(0);
        });
    }

    @Configuration
    @EnableConfigurationProperties(QuartzProperties.class)
    static class TestConfiguration {
        @Bean
        public QuartzDataSourceInitializer initializer(DataSource dataSource, ResourceLoader resourceLoader, QuartzProperties properties) {
            return new QuartzDataSourceInitializer(dataSource, resourceLoader, properties);
        }
    }
}

