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
package org.springframework.boot.test.autoconfigure.orm.jpa;


import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Specific tests for {@link TestDatabaseAutoConfiguration} when no embedded database is
 * available.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions({ "h2-*.jar", "hsqldb-*.jar", "derby-*.jar" })
public class TestDatabaseAutoConfigurationNoEmbeddedTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(TestDatabaseAutoConfigurationNoEmbeddedTests.ExistingDataSourceConfiguration.class).withConfiguration(AutoConfigurations.of(TestDatabaseAutoConfiguration.class));

    @Test
    public void applyAnyReplace() {
        this.contextRunner.run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("Failed to replace DataSource with an embedded database for tests.").hasMessageContaining("If you want an embedded database please put a supported one on the classpath").hasMessageContaining("or tune the replace attribute of @AutoConfigureTestDatabase."));
    }

    @Test
    public void applyNoReplace() {
        this.contextRunner.withPropertyValues("spring.test.database.replace=NONE").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).getBean(.class).isSameAs(context.getBean("myCustomDataSource"));
        });
    }

    @Configuration
    static class ExistingDataSourceConfiguration {
        @Bean
        public DataSource myCustomDataSource() {
            return Mockito.mock(DataSource.class);
        }
    }
}

