/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.test.context.jdbc;


import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Integration tests for {@link Sql @Sql} that verify support for multiple
 * {@link DataSource}s and {@link PlatformTransactionManager}s.
 * <p>Simultaneously tests for method-level overrides via {@code @SqlConfig}.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see MultipleDataSourcesAndTransactionManagersTransactionalSqlScriptsTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
@SqlConfig(dataSource = "dataSource1", transactionManager = "txMgr1")
public class MultipleDataSourcesAndTransactionManagersSqlScriptsTests {
    @Autowired
    private DataSource dataSource1;

    @Autowired
    private DataSource dataSource2;

    @Test
    @Sql("data-add-dogbert.sql")
    public void database1() {
        assertUsers(new JdbcTemplate(dataSource1), "Dilbert", "Dogbert");
    }

    @Test
    @Sql(scripts = "data-add-catbert.sql", config = @SqlConfig(dataSource = "dataSource2", transactionManager = "txMgr2"))
    public void database2() {
        assertUsers(new JdbcTemplate(dataSource2), "Dilbert", "Catbert");
    }

    @Configuration
    static class Config {
        @Bean
        public PlatformTransactionManager txMgr1() {
            return new DataSourceTransactionManager(dataSource1());
        }

        @Bean
        public PlatformTransactionManager txMgr2() {
            return new DataSourceTransactionManager(dataSource2());
        }

        @Bean
        public DataSource dataSource1() {
            return // 
            // 
            // 
            // 
            new EmbeddedDatabaseBuilder().setName("database1").addScript("classpath:/org/springframework/test/context/jdbc/schema.sql").addScript("classpath:/org/springframework/test/context/jdbc/data.sql").build();
        }

        @Bean
        public DataSource dataSource2() {
            return // 
            // 
            // 
            // 
            new EmbeddedDatabaseBuilder().setName("database2").addScript("classpath:/org/springframework/test/context/jdbc/schema.sql").addScript("classpath:/org/springframework/test/context/jdbc/data.sql").build();
        }
    }
}

