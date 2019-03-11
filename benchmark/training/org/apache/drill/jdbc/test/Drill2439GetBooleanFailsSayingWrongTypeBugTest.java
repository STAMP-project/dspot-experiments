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
public class Drill2439GetBooleanFailsSayingWrongTypeBugTest extends JdbcTestBase {
    private static Connection connection;

    private static Statement statement;

    @Test
    public void testGetBooleanGetsTrue() throws Exception {
        ResultSet rs = Drill2439GetBooleanFailsSayingWrongTypeBugTest.statement.executeQuery("SELECT TRUE FROM INFORMATION_SCHEMA.CATALOGS");
        rs.next();
        Assert.assertThat("getBoolean(...) for TRUE", rs.getBoolean(1), CoreMatchers.equalTo(true));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testGetBooleanGetsFalse() throws Exception {
        ResultSet rs = Drill2439GetBooleanFailsSayingWrongTypeBugTest.statement.executeQuery("SELECT FALSE FROM INFORMATION_SCHEMA.CATALOGS");
        rs.next();
        Assert.assertThat("getBoolean(...) for FALSE", rs.getBoolean(1), CoreMatchers.equalTo(false));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testGetBooleanGetsNull() throws Exception {
        ResultSet rs = Drill2439GetBooleanFailsSayingWrongTypeBugTest.statement.executeQuery("SELECT CAST( NULL AS BOOLEAN ) FROM INFORMATION_SCHEMA.CATALOGS");
        rs.next();
        Assert.assertThat("getBoolean(...) for BOOLEAN NULL", rs.getBoolean(1), CoreMatchers.equalTo(false));
        Assert.assertThat("wasNull", rs.wasNull(), CoreMatchers.equalTo(true));
    }
}

