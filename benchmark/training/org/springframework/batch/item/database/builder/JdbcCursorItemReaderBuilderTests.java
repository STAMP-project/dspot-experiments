/**
 * Copyright 2016-2018 the original author or authors.
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


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 * @author Drummond Dawson
 */
public class JdbcCursorItemReaderBuilderTests {
    private DataSource dataSource;

    private ConfigurableApplicationContext context;

    @Test
    public void testSimpleScenario() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO ORDER BY FIRST").rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 1, "2", "3");
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testMaxRows() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO ORDER BY FIRST").maxRows(2).saveState(false).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 1, "2", "3");
        validateFoo(reader.read(), 4, "5", "6");
        Assert.assertNull(reader.read());
        reader.close();
        Assert.assertEquals(0, executionContext.size());
    }

    @Test
    public void testQueryArgumentsList() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO WHERE FIRST > ? ORDER BY FIRST").queryArguments(Arrays.asList(3)).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testQueryArgumentsArray() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO WHERE FIRST > ? ORDER BY FIRST").queryArguments(3).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testQueryArgumentsTypedArray() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO WHERE FIRST > ? ORDER BY FIRST").queryArguments(new Integer[]{ 3 }, new int[]{ Types.BIGINT }).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testPreparedStatementSetter() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO WHERE FIRST > ? ORDER BY FIRST").preparedStatementSetter(new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, 3);
            }
        }).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testMaxItemCount() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO ORDER BY FIRST").maxItemCount(2).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 1, "2", "3");
        validateFoo(reader.read(), 4, "5", "6");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testCurrentItemCount() throws Exception {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO ORDER BY FIRST").currentItemCount(1).rowMapper(( rs, rowNum) -> {
            org.springframework.batch.item.database.builder.Foo foo = new org.springframework.batch.item.database.builder.Foo();
            foo.setFirst(rs.getInt("FIRST"));
            foo.setSecond(rs.getString("SECOND"));
            foo.setThird(rs.getString("THIRD"));
            return foo;
        }).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        validateFoo(reader.read(), 4, "5", "6");
        validateFoo(reader.read(), 7, "8", "9");
        Assert.assertNull(reader.read());
    }

    @Test
    public void testOtherProperties() {
        JdbcCursorItemReader<JdbcCursorItemReaderBuilderTests.Foo> reader = new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().dataSource(this.dataSource).name("fooReader").sql("SELECT * FROM FOO ORDER BY FIRST").fetchSize(1).queryTimeout(2).ignoreWarnings(true).driverSupportsAbsolute(true).useSharedExtendedConnection(true).beanRowMapper(JdbcCursorItemReaderBuilderTests.Foo.class).build();
        Assert.assertEquals(1, ReflectionTestUtils.getField(reader, "fetchSize"));
        Assert.assertEquals(2, ReflectionTestUtils.getField(reader, "queryTimeout"));
        Assert.assertTrue(((boolean) (ReflectionTestUtils.getField(reader, "ignoreWarnings"))));
        Assert.assertTrue(((boolean) (ReflectionTestUtils.getField(reader, "driverSupportsAbsolute"))));
    }

    @Test
    public void testValidation() {
        try {
            new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().saveState(true).build();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A name is required when saveSate is set to true", iae.getMessage());
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().saveState(false).build();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A query is required", iae.getMessage());
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().saveState(false).sql("select 1").build();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A datasource is required", iae.getMessage());
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            new JdbcCursorItemReaderBuilder<JdbcCursorItemReaderBuilderTests.Foo>().saveState(false).sql("select 1").dataSource(this.dataSource).build();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A rowmapper is required", iae.getMessage());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    public static class Foo {
        private int first;

        private String second;

        private String third;

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

        private static final String INSERT_SQL = "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (1, '2', '3');" + ("INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (4, '5', '6');" + "INSERT INTO FOO (FIRST, SECOND, THIRD) VALUES (7, '8', '9');");

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseFactory().getDatabase();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            Resource create = new ByteArrayResource(JdbcCursorItemReaderBuilderTests.TestDataSourceConfiguration.CREATE_SQL.getBytes());
            Resource insert = new ByteArrayResource(JdbcCursorItemReaderBuilderTests.TestDataSourceConfiguration.INSERT_SQL.getBytes());
            dataSourceInitializer.setDatabasePopulator(new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(create, insert));
            return dataSourceInitializer;
        }
    }
}

