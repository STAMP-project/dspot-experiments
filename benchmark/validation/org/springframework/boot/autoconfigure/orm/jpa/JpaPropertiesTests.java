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
package org.springframework.boot.autoconfigure.orm.jpa;


import Database.DEFAULT;
import Database.H2;
import Database.MYSQL;
import Database.POSTGRESQL;
import java.sql.SQLException;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.Database;


/**
 * Tests for {@link JpaProperties}.
 *
 * @author Stephane Nicoll
 */
public class JpaPropertiesTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(JpaPropertiesTests.TestConfiguration.class);

    @Test
    public void determineDatabaseNoCheckIfDatabaseIsSet() {
        this.contextRunner.withPropertyValues("spring.jpa.database=postgresql").run(assertJpaProperties(( properties) -> {
            DataSource dataSource = mockStandaloneDataSource();
            Database database = properties.determineDatabase(dataSource);
            assertThat(database).isEqualTo(POSTGRESQL);
            try {
                Mockito.verify(dataSource, Mockito.never()).getConnection();
            } catch (SQLException ex) {
                throw new IllegalStateException("Should not happen", ex);
            }
        }));
    }

    @Test
    public void determineDatabaseWithKnownUrl() {
        this.contextRunner.run(assertJpaProperties(( properties) -> {
            Database database = properties.determineDatabase(mockDataSource("jdbc:h2:mem:testdb"));
            assertThat(database).isEqualTo(H2);
        }));
    }

    @Test
    public void determineDatabaseWithKnownUrlAndUserConfig() {
        this.contextRunner.withPropertyValues("spring.jpa.database=mysql").run(assertJpaProperties(( properties) -> {
            Database database = properties.determineDatabase(mockDataSource("jdbc:h2:mem:testdb"));
            assertThat(database).isEqualTo(MYSQL);
        }));
    }

    @Test
    public void determineDatabaseWithUnknownUrl() {
        this.contextRunner.run(assertJpaProperties(( properties) -> {
            Database database = properties.determineDatabase(mockDataSource("jdbc:unknown://localhost"));
            assertThat(database).isEqualTo(DEFAULT);
        }));
    }

    @Configuration
    @EnableConfigurationProperties(JpaProperties.class)
    static class TestConfiguration {}
}

