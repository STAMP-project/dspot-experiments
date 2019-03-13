/**
 * Copyright 2016 the original author or authors.
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
package org.springframework.batch.item.database.builder;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 */
public class JdbcBatchItemWriterBuilderTests {
    private DataSource dataSource;

    private ConfigurableApplicationContext context;

    @Test
    public void testBasicMap() throws Exception {
        JdbcBatchItemWriter<Map<String, Object>> writer = new JdbcBatchItemWriterBuilder<Map<String, Object>>().columnMapped().dataSource(this.dataSource).sql("INSERT INTO FOO (first, second, third) VALUES (:first, :second, :third)").build();
        writer.afterPropertiesSet();
        List<Map<String, Object>> items = buildMapItems();
        writer.write(items);
        verifyWrite();
    }

    @Test
    public void testCustomJdbcTemplate() throws Exception {
        NamedParameterJdbcOperations template = new NamedParameterJdbcTemplate(this.dataSource);
        JdbcBatchItemWriter<Map<String, Object>> writer = new JdbcBatchItemWriterBuilder<Map<String, Object>>().columnMapped().namedParametersJdbcTemplate(template).sql("INSERT INTO FOO (first, second, third) VALUES (:first, :second, :third)").build();
        writer.afterPropertiesSet();
        List<Map<String, Object>> items = buildMapItems();
        writer.write(items);
        verifyWrite();
        Object usedTemplate = ReflectionTestUtils.getField(writer, "namedParameterJdbcTemplate");
        Assert.assertTrue((template == usedTemplate));
    }

    @Test
    public void testBasicPojo() throws Exception {
        JdbcBatchItemWriter<JdbcBatchItemWriterBuilderTests.Foo> writer = new JdbcBatchItemWriterBuilder<JdbcBatchItemWriterBuilderTests.Foo>().beanMapped().dataSource(this.dataSource).sql("INSERT INTO FOO (first, second, third) VALUES (:first, :second, :third)").build();
        writer.afterPropertiesSet();
        List<JdbcBatchItemWriterBuilderTests.Foo> items = new ArrayList<>(3);
        items.add(new JdbcBatchItemWriterBuilderTests.Foo(1, "two", "three"));
        items.add(new JdbcBatchItemWriterBuilderTests.Foo(4, "five", "six"));
        items.add(new JdbcBatchItemWriterBuilderTests.Foo(7, "eight", "nine"));
        writer.write(items);
        verifyWrite();
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testAssertUpdates() throws Exception {
        JdbcBatchItemWriter<JdbcBatchItemWriterBuilderTests.Foo> writer = new JdbcBatchItemWriterBuilder<JdbcBatchItemWriterBuilderTests.Foo>().beanMapped().dataSource(this.dataSource).sql("UPDATE FOO SET second = :second, third = :third WHERE first = :first").assertUpdates(true).build();
        writer.afterPropertiesSet();
        List<JdbcBatchItemWriterBuilderTests.Foo> items = new ArrayList<>(1);
        items.add(new JdbcBatchItemWriterBuilderTests.Foo(1, "two", "three"));
        writer.write(items);
    }

    @Test
    public void testCustomPreparedStatementSetter() throws Exception {
        JdbcBatchItemWriter<Map<String, Object>> writer = new JdbcBatchItemWriterBuilder<Map<String, Object>>().itemPreparedStatementSetter(( item, ps) -> {
            ps.setInt(0, ((int) (item.get("first"))));
            ps.setString(1, ((String) (item.get("second"))));
            ps.setString(2, ((String) (item.get("third"))));
        }).dataSource(this.dataSource).sql("INSERT INTO FOO (first, second, third) VALUES (:first, :second, :third)").build();
        writer.afterPropertiesSet();
        List<Map<String, Object>> items = buildMapItems();
        writer.write(items);
        verifyWrite();
    }

    @Test
    public void testCustomPSqlParameterSourceProvider() throws Exception {
        JdbcBatchItemWriter<Map<String, Object>> writer = new JdbcBatchItemWriterBuilder<Map<String, Object>>().itemSqlParameterSourceProvider(MapSqlParameterSource::new).dataSource(this.dataSource).sql("INSERT INTO FOO (first, second, third) VALUES (:first, :second, :third)").build();
        writer.afterPropertiesSet();
        List<Map<String, Object>> items = buildMapItems();
        writer.write(items);
        verifyWrite();
    }

    @Test
    public void testBuildAssertions() {
        try {
            new JdbcBatchItemWriterBuilder<Map<String, Object>>().itemSqlParameterSourceProvider(MapSqlParameterSource::new).build();
        } catch (IllegalStateException ise) {
            Assert.assertEquals("Either a DataSource or a NamedParameterJdbcTemplate is required", ise.getMessage());
        } catch (Exception e) {
            Assert.fail(("Incorrect exception was thrown when missing DataSource and JdbcTemplate: " + (e.getMessage())));
        }
        try {
            new JdbcBatchItemWriterBuilder<Map<String, Object>>().itemSqlParameterSourceProvider(MapSqlParameterSource::new).dataSource(this.dataSource).build();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("A SQL statement is required", ise.getMessage());
        } catch (Exception e) {
            Assert.fail(("Incorrect exception was thrown when testing missing SQL: " + e));
        }
        try {
            new JdbcBatchItemWriterBuilder<Map<String, Object>>().dataSource(this.dataSource).sql("INSERT INTO FOO VALUES (?, ?, ?)").columnMapped().beanMapped().build();
        } catch (IllegalStateException ise) {
            Assert.assertEquals("Either an item can be mapped via db column or via bean spec, can't be both", ise.getMessage());
        } catch (Exception e) {
            Assert.fail(("Incorrect exception was thrown both mapping types are used" + (e.getMessage())));
        }
    }

    public static class Foo {
        private int first;

        private String second;

        private String third;

        public Foo(int first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        public String getThird() {
            return third;
        }

        public void setThird(String third) {
            this.third = third;
        }
    }

    @Configuration
    public static class TestDataSourceConfiguration {
        private static final String CREATE_SQL = "CREATE TABLE FOO  (\n" + ((("\tID BIGINT IDENTITY NOT NULL PRIMARY KEY ,\n" + "\tFIRST BIGINT ,\n") + "\tSECOND VARCHAR(5) NOT NULL,\n") + "\tTHIRD VARCHAR(5) NOT NULL) ;");

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseFactory().getDatabase();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            Resource create = new ByteArrayResource(JdbcBatchItemWriterBuilderTests.TestDataSourceConfiguration.CREATE_SQL.getBytes());
            dataSourceInitializer.setDatabasePopulator(new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(create));
            return dataSourceInitializer;
        }
    }
}

