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
package org.springframework.boot.autoconfigure.jooq;


import SQLDialect.DEFAULT;
import SQLDialect.H2;
import SQLDialect.MYSQL;
import SQLDialect.POSTGRES;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link JooqProperties}.
 *
 * @author Stephane Nicoll
 */
public class JooqPropertiesTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void determineSqlDialectNoCheckIfDialectIsSet() throws SQLException {
        JooqProperties properties = load("spring.jooq.sql-dialect=postgres");
        DataSource dataSource = mockStandaloneDataSource();
        SQLDialect sqlDialect = properties.determineSqlDialect(dataSource);
        assertThat(sqlDialect).isEqualTo(POSTGRES);
        Mockito.verify(dataSource, Mockito.never()).getConnection();
    }

    @Test
    public void determineSqlDialectWithKnownUrl() {
        JooqProperties properties = load();
        SQLDialect sqlDialect = properties.determineSqlDialect(mockDataSource("jdbc:h2:mem:testdb"));
        assertThat(sqlDialect).isEqualTo(H2);
    }

    @Test
    public void determineSqlDialectWithKnownUrlAndUserConfig() {
        JooqProperties properties = load("spring.jooq.sql-dialect=mysql");
        SQLDialect sqlDialect = properties.determineSqlDialect(mockDataSource("jdbc:h2:mem:testdb"));
        assertThat(sqlDialect).isEqualTo(MYSQL);
    }

    @Test
    public void determineSqlDialectWithUnknownUrl() {
        JooqProperties properties = load();
        SQLDialect sqlDialect = properties.determineSqlDialect(mockDataSource("jdbc:unknown://localhost"));
        assertThat(sqlDialect).isEqualTo(DEFAULT);
    }

    @Configuration
    @EnableConfigurationProperties(JooqProperties.class)
    static class TestConfiguration {}
}

