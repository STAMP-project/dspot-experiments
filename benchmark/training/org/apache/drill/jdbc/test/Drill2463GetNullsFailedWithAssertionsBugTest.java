/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.test;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.JdbcTestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JdbcTest.class)
public class Drill2463GetNullsFailedWithAssertionsBugTest extends JdbcTestBase {
    private static Connection connection;

    private static Statement statement;

    // Test primitive types vs. non-primitive types:
    @Test
    public void testGetPrimitiveTypeNullAsOwnType() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS INTEGER ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getInt(...) for NULL", rs.getInt(1), CoreMatchers.equalTo(0));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testGetPrimitiveTypeNullAsObject() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS INTEGER ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getObject(...) for NULL", rs.getObject(1), CoreMatchers.nullValue());
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testGetNonprimitiveTypeNullAsOwnType() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS VARCHAR ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getString(...) for NULL", rs.getString(1), CoreMatchers.nullValue());
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    // Test a few specifics
    @Test
    public void testGetBooleanNullAsOwnType() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS BOOLEAN ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getBoolean(...) for NULL", rs.getBoolean(1), CoreMatchers.equalTo(false));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testGetBooleanNullAsObject() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS BOOLEAN ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getObject(...) for NULL", rs.getObject(1), CoreMatchers.nullValue());
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testGetIntegerNullAsOwnType() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS INTEGER ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getInt(...) for NULL", rs.getInt(1), CoreMatchers.equalTo(0));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testGetIntegerNullAsObject() throws Exception {
        final ResultSet rs = Drill2463GetNullsFailedWithAssertionsBugTest.statement.executeQuery("SELECT CAST( NULL AS INTEGER ) FROM INFORMATION_SCHEMA.CATALOGS");
        Assert.assertTrue(rs.next());
        Assert.assertThat("getObject(...) for NULL", rs.getObject(1), CoreMatchers.nullValue());
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }
}

