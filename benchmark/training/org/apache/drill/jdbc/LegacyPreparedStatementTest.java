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


import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test that prepared statements works even if not supported on server, to some extent.
 */
@Category(JdbcTest.class)
public class LegacyPreparedStatementTest extends JdbcTestBase {
    /**
     * Fuzzy matcher for parameters-not-supported message assertions.  (Based on
     *  current "Prepared-statement dynamic parameters are not supported.")
     */
    private static final Matcher<String> PARAMETERS_NOT_SUPPORTED_MSG_MATCHER = // allows "Parameter"
    // (could have false matches)
    CoreMatchers.allOf(CoreMatchers.containsString("arameter"), CoreMatchers.containsString("not"), CoreMatchers.containsString("support"));// allows "supported"


    private static Connection connection;

    // ////////
    // Basic querying-works test:
    /**
     * Tests that basic executeQuery() (with query statement) works.
     */
    @Test
    public void testExecuteQueryBasicCaseWorks() throws SQLException {
        try (PreparedStatement stmt = LegacyPreparedStatementTest.connection.prepareStatement("VALUES 11")) {
            try (ResultSet rs = stmt.executeQuery()) {
                Assert.assertThat("Unexpected column count", rs.getMetaData().getColumnCount(), CoreMatchers.equalTo(1));
                Assert.assertTrue("No expected first row", rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.equalTo(11));
                Assert.assertFalse("Unexpected second row", rs.next());
            }
        }
    }

    // ////////
    // Parameters-not-implemented tests:
    /**
     * Tests that "not supported" has priority over possible "no parameters"
     *  check.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testParamSettingWhenNoParametersIndexSaysUnsupported() throws SQLException {
        try (PreparedStatement prepStmt = LegacyPreparedStatementTest.connection.prepareStatement("VALUES 1")) {
            try {
                prepStmt.setBytes(4, null);
            } catch (final SQLFeatureNotSupportedException e) {
                Assert.assertThat("Check whether params.-unsupported wording changed or checks changed.", e.toString(), LegacyPreparedStatementTest.PARAMETERS_NOT_SUPPORTED_MSG_MATCHER);
                throw e;
            }
        }
    }

    /**
     * Tests that "not supported" has priority over possible "type not supported"
     *  check.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testParamSettingWhenUnsupportedTypeSaysUnsupported() throws SQLException {
        try (PreparedStatement prepStmt = LegacyPreparedStatementTest.connection.prepareStatement("VALUES 1")) {
            try {
                prepStmt.setClob(2, ((Clob) (null)));
            } catch (final SQLFeatureNotSupportedException e) {
                Assert.assertThat("Check whether params.-unsupported wording changed or checks changed.", e.toString(), LegacyPreparedStatementTest.PARAMETERS_NOT_SUPPORTED_MSG_MATCHER);
                throw e;
            }
        }
    }
}

