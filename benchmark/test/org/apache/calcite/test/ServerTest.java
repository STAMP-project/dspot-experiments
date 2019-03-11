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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for server and DDL.
 */
public class ServerTest {
    static final String URL = "jdbc:calcite:";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testStatement() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement();ResultSet r = s.executeQuery("values 1, 2")) {
            MatcherAssert.assertThat(r.next(), Is.is(true));
            MatcherAssert.assertThat(r.getString(1), CoreMatchers.notNullValue());
            MatcherAssert.assertThat(r.next(), Is.is(true));
            MatcherAssert.assertThat(r.next(), Is.is(false));
        }
    }

    @Test
    public void testCreateSchema() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            boolean b = s.execute("create schema s");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create table s.t (i int not null)");
            MatcherAssert.assertThat(b, Is.is(false));
            int x = s.executeUpdate("insert into s.t values 1");
            MatcherAssert.assertThat(x, Is.is(1));
            try (ResultSet r = s.executeQuery("select count(*) from s.t")) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(1));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
        }
    }

    @Test
    public void testCreateType() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            boolean b = s.execute("create type mytype1 as BIGINT");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create or replace type mytype2 as (i int not null, jj mytype1)");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create type mytype3 as (i int not null, jj mytype2)");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create or replace type mytype1 as DOUBLE");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create table t (c mytype1 NOT NULL)");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create type mytype4 as BIGINT");
            MatcherAssert.assertThat(b, Is.is(false));
            int x = s.executeUpdate("insert into t values 12.0");
            MatcherAssert.assertThat(x, Is.is(1));
            x = s.executeUpdate("insert into t values 3.0");
            MatcherAssert.assertThat(x, Is.is(1));
            try (ResultSet r = s.executeQuery("select CAST(c AS mytype4) from t")) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(12));
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(3));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
        }
    }

    @Test
    public void testDropType() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            boolean b = s.execute("create type mytype1 as BIGINT");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("drop type mytype1");
            MatcherAssert.assertThat(b, Is.is(false));
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            boolean b = s.execute("create table t (i int not null)");
            MatcherAssert.assertThat(b, Is.is(false));
            int x = s.executeUpdate("insert into t values 1");
            MatcherAssert.assertThat(x, Is.is(1));
            x = s.executeUpdate("insert into t values 3");
            MatcherAssert.assertThat(x, Is.is(1));
            try (ResultSet r = s.executeQuery("select sum(i) from t")) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(4));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
            // CALCITE-2464: Allow to set nullability for columns of structured types
            b = s.execute("create type mytype as (i int)");
            MatcherAssert.assertThat(b, Is.is(false));
            b = s.execute("create table w (i int not null, j mytype)");
            MatcherAssert.assertThat(b, Is.is(false));
            x = s.executeUpdate("insert into w values (1, NULL)");
            MatcherAssert.assertThat(x, Is.is(1));
        }
    }

    @Test
    public void testStoredGeneratedColumn() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            final String sql0 = "create table t (\n" + ((" h int not null,\n" + " i int,\n") + " j int as (i + 1) stored)");
            boolean b = s.execute(sql0);
            MatcherAssert.assertThat(b, Is.is(false));
            int x;
            // A successful row.
            x = s.executeUpdate("insert into t (h, i) values (3, 4)");
            MatcherAssert.assertThat(x, Is.is(1));
            final String sql1 = "explain plan for\n" + "insert into t (h, i) values (3, 4)";
            try (ResultSet r = s.executeQuery(sql1)) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                final String plan = "" + (("EnumerableTableModify(table=[[T]], operation=[INSERT], flattened=[false])\n" + "  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], proj#0..1=[{exprs}], J=[$t3])\n") + "    EnumerableValues(tuples=[[{ 3, 4 }]])\n");
                MatcherAssert.assertThat(r.getString(1), Matchers.isLinux(plan));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
            try (ResultSet r = s.executeQuery("select * from t")) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt("H"), Is.is(3));
                MatcherAssert.assertThat(r.wasNull(), Is.is(false));
                MatcherAssert.assertThat(r.getInt("I"), Is.is(4));
                MatcherAssert.assertThat(r.getInt("J"), Is.is(5));// j = i + 1

                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
            // No target column list; too few values provided
            try {
                x = s.executeUpdate("insert into t values (2, 3)");
                Assert.fail(("expected error, got " + x));
            } catch (SQLException e) {
                MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(("Number of INSERT target columns (3) does not equal " + "number of source items (2)")));
            }
            // No target column list; too many values provided
            try {
                x = s.executeUpdate("insert into t values (3, 4, 5, 6)");
                Assert.fail(("expected error, got " + x));
            } catch (SQLException e) {
                MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(("Number of INSERT target columns (3) does not equal " + "number of source items (4)")));
            }
            // No target column list;
            // source count = target count;
            // but one of the target columns is virtual.
            try {
                x = s.executeUpdate("insert into t values (3, 4, 5)");
                Assert.fail(("expected error, got " + x));
            } catch (SQLException e) {
                MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Cannot INSERT into generated column 'J'"));
            }
            // Explicit target column list, omits virtual column
            x = s.executeUpdate("insert into t (h, i) values (1, 2)");
            MatcherAssert.assertThat(x, Is.is(1));
            // Explicit target column list, includes virtual column but assigns
            // DEFAULT.
            x = s.executeUpdate("insert into t (h, i, j) values (1, 2, DEFAULT)");
            MatcherAssert.assertThat(x, Is.is(1));
            // As previous, re-order columns.
            x = s.executeUpdate("insert into t (h, j, i) values (1, DEFAULT, 3)");
            MatcherAssert.assertThat(x, Is.is(1));
            // Target column list exists,
            // target column count equals the number of non-virtual columns;
            // but one of the target columns is virtual.
            try {
                x = s.executeUpdate("insert into t (h, j) values (1, 3)");
                Assert.fail(("expected error, got " + x));
            } catch (SQLException e) {
                MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Cannot INSERT into generated column 'J'"));
            }
            // Target column list exists and contains all columns,
            // expression for virtual column is not DEFAULT.
            try {
                x = s.executeUpdate("insert into t (h, i, j) values (2, 3, 3 + 1)");
                Assert.fail(("expected error, got " + x));
            } catch (SQLException e) {
                MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Cannot INSERT into generated column 'J'"));
            }
            x = s.executeUpdate("insert into t (h, i) values (0, 1)");
            MatcherAssert.assertThat(x, Is.is(1));
            x = s.executeUpdate("insert into t (h, i, j) values (0, 1, DEFAULT)");
            MatcherAssert.assertThat(x, Is.is(1));
            x = s.executeUpdate("insert into t (j, i, h) values (DEFAULT, NULL, 7)");
            MatcherAssert.assertThat(x, Is.is(1));
            x = s.executeUpdate("insert into t (h, i) values (6, 5), (7, 4)");
            MatcherAssert.assertThat(x, Is.is(2));
            try (ResultSet r = s.executeQuery("select sum(i), count(*) from t")) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(19));
                MatcherAssert.assertThat(r.getInt(2), Is.is(9));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
        }
    }

    @Test
    public void testVirtualColumn() throws Exception {
        try (Connection c = ServerTest.connect();Statement s = c.createStatement()) {
            final String sql0 = "create table t (\n" + ((" h int not null,\n" + " i int,\n") + " j int as (i + 1) virtual)");
            boolean b = s.execute(sql0);
            MatcherAssert.assertThat(b, Is.is(false));
            int x = s.executeUpdate("insert into t (h, i) values (1, 2)");
            MatcherAssert.assertThat(x, Is.is(1));
            // In plan, "j" is replaced by "i + 1".
            final String sql = "select * from t";
            try (ResultSet r = s.executeQuery(sql)) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getInt(1), Is.is(1));
                MatcherAssert.assertThat(r.getInt(2), Is.is(2));
                MatcherAssert.assertThat(r.getInt(3), Is.is(3));
                MatcherAssert.assertThat(r.next(), Is.is(false));
            }
            final String plan = "" + ("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], proj#0..1=[{exprs}], J=[$t3])\n" + "  EnumerableTableScan(table=[[T]])\n");
            try (ResultSet r = s.executeQuery(("explain plan for " + sql))) {
                MatcherAssert.assertThat(r.next(), Is.is(true));
                MatcherAssert.assertThat(r.getString(1), Matchers.isLinux(plan));
            }
        }
    }
}

/**
 * End ServerTest.java
 */
