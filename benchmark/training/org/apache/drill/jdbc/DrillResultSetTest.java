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
package org.apache.drill.jdbc;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.exec.ExecConstants;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// TODO:  Ideally, test other methods.
@Category({ SlowTest.class, JdbcTest.class })
public class DrillResultSetTest extends JdbcTestBase {
    // TODO: Move Jetty status server disabling to DrillTest.
    private static final String STATUS_SERVER_PROPERTY_NAME = ExecConstants.HTTP_ENABLE;

    private static final String origStatusServerPropValue = System.getProperty(DrillResultSetTest.STATUS_SERVER_PROPERTY_NAME, "true");

    @Test
    public void test_next_blocksFurtherAccessAfterEnd() throws SQLException {
        Connection connection = JdbcTestBase.connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(("SELECT 1 AS x \n" + ("FROM cp.`donuts.json` \n" + "LIMIT 2")));
        // Advance to first row; confirm can access data.
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.getInt(1), CoreMatchers.is(1));
        // Advance from first to second (last) row, confirming data access.
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.getInt(1), CoreMatchers.is(1));
        // Now advance past last row.
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        // Main check:  That row data access methods now throw SQLException.
        try {
            resultSet.getInt(1);
            Assert.fail("Didn't get expected SQLException.");
        } catch (SQLException e) {
            // Expect something like current InvalidCursorStateSqlException saying
            // "Result set cursor is already positioned past all rows."
            Assert.assertThat(e, CoreMatchers.instanceOf(InvalidCursorStateSqlException.class));
            Assert.assertThat(e.toString(), StringContains.containsString("past"));
        }
        // (Any other exception is unexpected result.)
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        // TODO:  Ideally, test all other accessor methods.
    }

    @Test
    public void test_next_blocksFurtherAccessWhenNoRows() throws Exception {
        Connection connection = JdbcTestBase.connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(("SELECT \'Hi\' AS x \n" + ("FROM cp.`donuts.json` \n" + "WHERE false")));
        // Do initial next(). (Advance from before results to next possible
        // position (after the set of zero rows).
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        // Main check:  That row data access methods throw SQLException.
        try {
            resultSet.getString(1);
            Assert.fail("Didn't get expected SQLException.");
        } catch (SQLException e) {
            // Expect something like current InvalidRowSQLException saying
            // "Result set cursor is already positioned past all rows."
            Assert.assertThat(e, CoreMatchers.instanceOf(InvalidCursorStateSqlException.class));
            Assert.assertThat(e.toString(), StringContains.containsString("past"));
            Assert.assertThat(e.toString(), StringContains.containsString("rows"));
        }
        // (Any non-SQLException exception is unexpected result.)
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        // TODO:  Ideally, test all other accessor methods.
    }

    @Test
    public void test_getRow_isOneBased() throws Exception {
        Connection connection = JdbcTestBase.connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("VALUES (1), (2)");
        // Expect 0 when before first row:
        Assert.assertThat("getRow() before first next()", resultSet.getRow(), CoreMatchers.equalTo(0));
        resultSet.next();
        // Expect 1 at first row:
        Assert.assertThat("getRow() at first row", resultSet.getRow(), CoreMatchers.equalTo(1));
        resultSet.next();
        // Expect 2 at second row:
        Assert.assertThat("getRow() at second row", resultSet.getRow(), CoreMatchers.equalTo(2));
        resultSet.next();
        // Expect 0 again when after last row:
        Assert.assertThat("getRow() after last row", resultSet.getRow(), CoreMatchers.equalTo(0));
        resultSet.next();
        Assert.assertThat("getRow() after last row", resultSet.getRow(), CoreMatchers.equalTo(0));
    }
}

