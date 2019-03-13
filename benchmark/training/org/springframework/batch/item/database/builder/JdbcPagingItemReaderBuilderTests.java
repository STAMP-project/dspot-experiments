/**
 * Copyright 2017 the original author or authors.
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


import Order.DESCENDING;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.AbstractSqlPagingQueryProvider;
import org.springframework.batch.item.database.support.HsqlPagingQueryProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 */
public class JdbcPagingItemReaderBuilderTests {
    private DataSource dataSource;

    private ConfigurableApplicationContext context;

    @Test
    public void testBasicConfigurationQueryProvider() throws Exception {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ID", DESCENDING);
        AbstractSqlPagingQueryProvider provider = new HsqlPagingQueryProvider();
        provider.setSelectClause("SELECT ID, FIRST, SECOND, THIRD");
        provider.setFromClause("FOO");
        provider.setSortKeys(sortKeys);
        JdbcPagingItemReader<JdbcPagingItemReaderBuilderTests.Foo> reader = new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().name("fooReader").currentItemCount(1).dataSource(this.dataSource).queryProvider(provider).fetchSize(2).maxItemCount(2).rowMapper(( rs, rowNum) -> new org.springframework.batch.item.database.builder.Foo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4))).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        JdbcPagingItemReaderBuilderTests.Foo item1 = reader.read();
        Assert.assertNull(reader.read());
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(3, item1.getId());
        Assert.assertEquals(10, item1.getFirst());
        Assert.assertEquals("11", item1.getSecond());
        Assert.assertEquals("12", item1.getThird());
        Assert.assertTrue((((int) (ReflectionTestUtils.getField(reader, "fetchSize"))) == 2));
        Assert.assertEquals(2, executionContext.size());
    }

    @Test
    public void testBasicConfiguration() throws Exception {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ID", DESCENDING);
        JdbcPagingItemReader<JdbcPagingItemReaderBuilderTests.Foo> reader = new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().name("fooReader").currentItemCount(1).dataSource(this.dataSource).maxItemCount(2).selectClause("SELECT ID, FIRST, SECOND, THIRD").fromClause("FOO").sortKeys(sortKeys).rowMapper(( rs, rowNum) -> new org.springframework.batch.item.database.builder.Foo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4))).build();
        reader.afterPropertiesSet();
        reader.open(new ExecutionContext());
        JdbcPagingItemReaderBuilderTests.Foo item1 = reader.read();
        Assert.assertNull(reader.read());
        Assert.assertEquals(3, item1.getId());
        Assert.assertEquals(10, item1.getFirst());
        Assert.assertEquals("11", item1.getSecond());
        Assert.assertEquals("12", item1.getThird());
    }

    @Test
    public void testPageSize() throws Exception {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ID", DESCENDING);
        JdbcPagingItemReader<JdbcPagingItemReaderBuilderTests.Foo> reader = new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().name("fooReader").dataSource(this.dataSource).pageSize(1).maxItemCount(2).selectClause("SELECT ID, FIRST, SECOND, THIRD").fromClause("FOO").sortKeys(sortKeys).rowMapper(( rs, rowNum) -> new org.springframework.batch.item.database.builder.Foo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4))).build();
        reader.afterPropertiesSet();
        reader.open(new ExecutionContext());
        JdbcPagingItemReaderBuilderTests.Foo item1 = reader.read();
        JdbcPagingItemReaderBuilderTests.Foo item2 = reader.read();
        Assert.assertNull(reader.read());
        Assert.assertEquals(4, item1.getId());
        Assert.assertEquals(13, item1.getFirst());
        Assert.assertEquals("14", item1.getSecond());
        Assert.assertEquals("15", item1.getThird());
        Assert.assertEquals(3, item2.getId());
        Assert.assertEquals(10, item2.getFirst());
        Assert.assertEquals("11", item2.getSecond());
        Assert.assertEquals("12", item2.getThird());
    }

    @Test
    public void testSaveState() throws Exception {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ID", DESCENDING);
        JdbcPagingItemReader<JdbcPagingItemReaderBuilderTests.Foo> reader = new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().dataSource(this.dataSource).pageSize(1).maxItemCount(2).selectClause("SELECT ID, FIRST, SECOND, THIRD").fromClause("FOO").sortKeys(sortKeys).saveState(false).rowMapper(( rs, rowNum) -> new org.springframework.batch.item.database.builder.Foo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4))).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        JdbcPagingItemReaderBuilderTests.Foo item1 = reader.read();
        JdbcPagingItemReaderBuilderTests.Foo item2 = reader.read();
        Assert.assertNull(reader.read());
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(4, item1.getId());
        Assert.assertEquals(13, item1.getFirst());
        Assert.assertEquals("14", item1.getSecond());
        Assert.assertEquals("15", item1.getThird());
        Assert.assertEquals(3, item2.getId());
        Assert.assertEquals(10, item2.getFirst());
        Assert.assertEquals("11", item2.getSecond());
        Assert.assertEquals("12", item2.getThird());
        Assert.assertEquals(0, executionContext.size());
    }

    @Test
    public void testParameters() throws Exception {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ID", DESCENDING);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("min", 1);
        parameterValues.put("max", 10);
        JdbcPagingItemReader<JdbcPagingItemReaderBuilderTests.Foo> reader = new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().name("fooReader").dataSource(this.dataSource).pageSize(1).maxItemCount(1).selectClause("SELECT ID, FIRST, SECOND, THIRD").fromClause("FOO").whereClause("FIRST > :min AND FIRST < :max").sortKeys(sortKeys).parameterValues(parameterValues).rowMapper(( rs, rowNum) -> new org.springframework.batch.item.database.builder.Foo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4))).build();
        reader.afterPropertiesSet();
        reader.open(new ExecutionContext());
        JdbcPagingItemReaderBuilderTests.Foo item1 = reader.read();
        Assert.assertNull(reader.read());
        Assert.assertEquals(2, item1.getId());
        Assert.assertEquals(7, item1.getFirst());
        Assert.assertEquals("8", item1.getSecond());
        Assert.assertEquals("9", item1.getThird());
    }

    @Test
    public void testValidation() {
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().build();
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("dataSource is required", iae.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().pageSize((-2)).build();
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("pageSize must be greater than zero", iae.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().pageSize(2).build();
            Assert.fail();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("dataSource is required", ise.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().pageSize(2).dataSource(this.dataSource).build();
            Assert.fail();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("A name is required when saveState is set to true", ise.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().saveState(false).pageSize(2).dataSource(this.dataSource).build();
            Assert.fail();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("selectClause is required when not providing a PagingQueryProvider", ise.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().name("fooReader").pageSize(2).dataSource(this.dataSource).selectClause("SELECT *").build();
            Assert.fail();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("fromClause is required when not providing a PagingQueryProvider", ise.getMessage());
        }
        try {
            new JdbcPagingItemReaderBuilder<JdbcPagingItemReaderBuilderTests.Foo>().saveState(false).pageSize(2).dataSource(this.dataSource).selectClause("SELECT *").fromClause("FOO").build();
            Assert.fail();
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("sortKeys are required when not providing a PagingQueryProvider", ise.getMessage());
        }
    }

    public static class Foo {
        private int id;

        private int first;

        private String second;

        private String third;

        public Foo(int id, int first, String second, String third) {
            this.id = id;
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        private static final String INSERT_SQL = "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (1, '2', '3');" + ((("INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (4, '5', '6');" + "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (7, '8', '9');") + "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (10, '11', '12');") + "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (13, '14', '15');");

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseFactory().getDatabase();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            Resource create = new ByteArrayResource(JdbcPagingItemReaderBuilderTests.TestDataSourceConfiguration.CREATE_SQL.getBytes());
            Resource insert = new ByteArrayResource(JdbcPagingItemReaderBuilderTests.TestDataSourceConfiguration.INSERT_SQL.getBytes());
            dataSourceInitializer.setDatabasePopulator(new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(create, insert));
            return dataSourceInitializer;
        }
    }
}

