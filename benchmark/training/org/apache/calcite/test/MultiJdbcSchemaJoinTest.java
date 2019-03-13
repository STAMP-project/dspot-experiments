/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import com.google.common.collect.Sets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for joining tables from two different JDBC databases.
 */
public class MultiJdbcSchemaJoinTest {
    @Test
    public void test() throws ClassNotFoundException, SQLException {
        // Create two databases
        // It's two times hsqldb, but imagine they are different rdbms's
        final String db1 = MultiJdbcSchemaJoinTest.TempDb.INSTANCE.getUrl();
        Connection c1 = DriverManager.getConnection(db1, "", "");
        Statement stmt1 = c1.createStatement();
        stmt1.execute(("create table table1(id varchar(10) not null primary key, " + "field1 varchar(10))"));
        stmt1.execute("insert into table1 values('a', 'aaaa')");
        c1.close();
        final String db2 = MultiJdbcSchemaJoinTest.TempDb.INSTANCE.getUrl();
        Connection c2 = DriverManager.getConnection(db2, "", "");
        Statement stmt2 = c2.createStatement();
        stmt2.execute(("create table table2(id varchar(10) not null primary key, " + "field1 varchar(10))"));
        stmt2.execute("insert into table2 values('a', 'aaaa')");
        c2.close();
        // Connect via calcite to these databases
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        final DataSource ds1 = JdbcSchema.dataSource(db1, "org.hsqldb.jdbcDriver", "", "");
        rootSchema.add("DB1", JdbcSchema.create(rootSchema, "DB1", ds1, null, null));
        final DataSource ds2 = JdbcSchema.dataSource(db2, "org.hsqldb.jdbcDriver", "", "");
        rootSchema.add("DB2", JdbcSchema.create(rootSchema, "DB2", ds2, null, null));
        Statement stmt3 = connection.createStatement();
        ResultSet rs = stmt3.executeQuery(("select table1.id, table1.field1 " + "from db1.table1 join db2.table2 on table1.id = table2.id"));
        Assert.assertThat(CalciteAssert.toString(rs), CoreMatchers.equalTo("ID=a; FIELD1=aaaa\n"));
    }

    /**
     * Makes sure that {@link #test} is re-entrant.
     * Effectively a test for {@code TempDb}.
     */
    @Test
    public void test2() throws ClassNotFoundException, SQLException {
        test();
    }

    @Test
    public void testJdbcWithEnumerableJoin() throws SQLException {
        // This query works correctly
        String query = "select t.id, t.field1 " + "from db.table1 t join \"hr\".\"emps\" e on e.\"empid\" = t.id";
        final Set<Integer> expected = Sets.newHashSet(100, 200);
        Assert.assertThat(runQuery(setup(), query), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testEnumerableWithJdbcJoin() throws SQLException {
        // * compared to testJdbcWithEnumerableJoin, the join order is reversed
        // * the query fails with a CannotPlanException
        String query = "select t.id, t.field1 " + "from \"hr\".\"emps\" e join db.table1 t on e.\"empid\" = t.id";
        final Set<Integer> expected = Sets.newHashSet(100, 200);
        Assert.assertThat(runQuery(setup(), query), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testEnumerableWithJdbcJoinWithWhereClause() throws SQLException {
        // Same query as above but with a where condition added:
        // * the good: this query does not give a CannotPlanException
        // * the bad: the result is wrong: there is only one emp called Bill.
        // The query plan shows the join condition is always true,
        // afaics, the join condition is pushed down to the non-jdbc
        // table. It might have something to do with the cast that
        // is introduced in the join condition.
        String query = "select t.id, t.field1 " + ("from \"hr\".\"emps\" e join db.table1 t on e.\"empid\" = t.id" + " where e.\"name\" = \'Bill\'");
        final Set<Integer> expected = Sets.newHashSet(100);
        Assert.assertThat(runQuery(setup(), query), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testSchemaConsistency() throws Exception {
        // Create a database
        final String db = MultiJdbcSchemaJoinTest.TempDb.INSTANCE.getUrl();
        Connection c1 = DriverManager.getConnection(db, "", "");
        Statement stmt1 = c1.createStatement();
        stmt1.execute(("create table table1(id varchar(10) not null primary key, " + "field1 varchar(10))"));
        // Connect via calcite to these databases
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        final DataSource ds = JdbcSchema.dataSource(db, "org.hsqldb.jdbcDriver", "", "");
        rootSchema.add("DB", JdbcSchema.create(rootSchema, "DB", ds, null, null));
        Statement stmt3 = connection.createStatement();
        ResultSet rs;
        // fails, table does not exist
        try {
            rs = stmt3.executeQuery("select * from db.table2");
            Assert.fail(("expected error, got " + rs));
        } catch (SQLException e) {
            Assert.assertThat(e.getCause().getCause().getMessage(), CoreMatchers.equalTo("Object 'TABLE2' not found within 'DB'"));
        }
        stmt1.execute(("create table table2(id varchar(10) not null primary key, " + "field1 varchar(10))"));
        stmt1.execute("insert into table2 values('a', 'aaaa')");
        PreparedStatement stmt2 = connection.prepareStatement("select * from db.table2");
        stmt1.execute("alter table table2 add column field2 varchar(10)");
        // "field2" not visible to stmt2
        rs = stmt2.executeQuery();
        Assert.assertThat(CalciteAssert.toString(rs), CoreMatchers.equalTo("ID=a; FIELD1=aaaa\n"));
        // "field2" visible to a new query
        rs = stmt3.executeQuery("select * from db.table2");
        Assert.assertThat(CalciteAssert.toString(rs), CoreMatchers.equalTo("ID=a; FIELD1=aaaa; FIELD2=null\n"));
        c1.close();
    }

    /**
     * Pool of temporary databases.
     */
    static class TempDb {
        public static final MultiJdbcSchemaJoinTest.TempDb INSTANCE = new MultiJdbcSchemaJoinTest.TempDb();

        private final AtomicInteger id = new AtomicInteger(1);

        TempDb() {
        }

        /**
         * Allocates a URL for a new Hsqldb database.
         */
        public String getUrl() {
            return "jdbc:hsqldb:mem:db" + (id.getAndIncrement());
        }
    }
}

/**
 * End MultiJdbcSchemaJoinTest.java
 */
