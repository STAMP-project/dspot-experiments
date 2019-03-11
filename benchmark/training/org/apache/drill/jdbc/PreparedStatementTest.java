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


import InfoSchemaConstants.IS_CATALOG_NAME;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for Drill's implementation of PreparedStatement's methods.
 */
@Category(JdbcTest.class)
public class PreparedStatementTest extends JdbcTestBase {
    private static final Logger logger = LoggerFactory.getLogger(PreparedStatementTest.class);

    private static final String SYS_VERSION_SQL = "select * from sys.version";

    private static final String SYS_RANDOM_SQL = "SELECT cast(random() as varchar) as myStr FROM (VALUES(1)) " + ("union SELECT cast(random() as varchar) as myStr FROM (VALUES(1)) " + "union SELECT cast(random() as varchar) as myStr FROM (VALUES(1)) ");

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
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement("VALUES 11")) {
            try (ResultSet rs = stmt.executeQuery()) {
                Assert.assertThat("Unexpected column count", rs.getMetaData().getColumnCount(), CoreMatchers.equalTo(1));
                Assert.assertTrue("No expected first row", rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.equalTo(11));
                Assert.assertFalse("Unexpected second row", rs.next());
            }
        }
    }

    @Test
    public void testQueryMetadataInPreparedStatement() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(("SELECT " + (((((("cast(1 as INTEGER ) as int_field, " + "cast(12384729 as BIGINT ) as bigint_field, ") + "cast('varchar_value' as varchar(50)) as varchar_field, ") + "timestamp '2008-2-23 10:00:20.123' as ts_field, ") + "date '2008-2-23' as date_field, ") + "cast('99999912399.4567' as decimal(18, 5)) as decimal_field") + " FROM sys.version")))) {
            List<PreparedStatementTest.ExpectedColumnResult> exp = ImmutableList.of(new PreparedStatementTest.ExpectedColumnResult("int_field", Types.INTEGER, ResultSetMetaData.columnNoNulls, 11, 0, 0, true, Integer.class.getName()), new PreparedStatementTest.ExpectedColumnResult("bigint_field", Types.BIGINT, ResultSetMetaData.columnNoNulls, 20, 0, 0, true, Long.class.getName()), new PreparedStatementTest.ExpectedColumnResult("varchar_field", Types.VARCHAR, ResultSetMetaData.columnNoNulls, 50, 50, 0, false, String.class.getName()), new PreparedStatementTest.ExpectedColumnResult("ts_field", Types.TIMESTAMP, ResultSetMetaData.columnNoNulls, 19, 0, 0, false, Timestamp.class.getName()), new PreparedStatementTest.ExpectedColumnResult("date_field", Types.DATE, ResultSetMetaData.columnNoNulls, 10, 0, 0, false, Date.class.getName()), new PreparedStatementTest.ExpectedColumnResult("decimal_field", Types.DECIMAL, ResultSetMetaData.columnNoNulls, 20, 18, 5, true, BigDecimal.class.getName()));
            ResultSetMetaData prepareMetadata = stmt.getMetaData();
            PreparedStatementTest.verifyMetadata(prepareMetadata, exp);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData executeMetadata = rs.getMetaData();
                PreparedStatementTest.verifyMetadata(executeMetadata, exp);
                Assert.assertTrue("No expected first row", rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.equalTo(1));
                Assert.assertThat(rs.getLong(2), CoreMatchers.equalTo(12384729L));
                Assert.assertThat(rs.getString(3), CoreMatchers.equalTo("varchar_value"));
                Assert.assertThat(rs.getTimestamp(4), CoreMatchers.equalTo(Timestamp.valueOf("2008-2-23 10:00:20.123")));
                Assert.assertThat(rs.getDate(5), CoreMatchers.equalTo(Date.valueOf("2008-2-23")));
                Assert.assertThat(rs.getBigDecimal(6), CoreMatchers.equalTo(new BigDecimal("99999912399.45670")));
                Assert.assertFalse("Unexpected second row", rs.next());
            }
        }
    }

    private static class ExpectedColumnResult {
        final String columnName;

        final int type;

        final int nullable;

        final int displaySize;

        final int precision;

        final int scale;

        final boolean signed;

        final String className;

        ExpectedColumnResult(String columnName, int type, int nullable, int displaySize, int precision, int scale, boolean signed, String className) {
            this.columnName = columnName;
            this.type = type;
            this.nullable = nullable;
            this.displaySize = displaySize;
            this.precision = precision;
            this.scale = scale;
            this.signed = signed;
            this.className = className;
        }

        boolean isEqualsTo(ResultSetMetaData metadata, int colNum) throws SQLException {
            return (((((((((((((((((metadata.getCatalogName(colNum).equals(IS_CATALOG_NAME)) && (metadata.getSchemaName(colNum).isEmpty())) && (metadata.getTableName(colNum).isEmpty())) && (metadata.getColumnName(colNum).equals(columnName))) && (metadata.getColumnLabel(colNum).equals(columnName))) && ((metadata.getColumnType(colNum)) == (type))) && ((metadata.isNullable(colNum)) == (nullable))) && // There is an existing bug where query results doesn't contain the precision for VARCHAR field.
            // metadata.getPrecision(colNum) == precision &&
            ((metadata.getScale(colNum)) == (scale))) && ((metadata.isSigned(colNum)) == (signed))) && ((metadata.getColumnDisplaySize(colNum)) == (displaySize))) && (metadata.getColumnClassName(colNum).equals(className))) && (metadata.isSearchable(colNum))) && ((metadata.isAutoIncrement(colNum)) == false)) && ((metadata.isCaseSensitive(colNum)) == false)) && (metadata.isReadOnly(colNum))) && ((metadata.isWritable(colNum)) == false)) && ((metadata.isDefinitelyWritable(colNum)) == false)) && ((metadata.isCurrency(colNum)) == false);
        }

        @Override
        public String toString() {
            return ((((((((((((((((((("ExpectedColumnResult[" + "columnName='") + (columnName)) + '\'') + ", type='") + (type)) + '\'') + ", nullable=") + (nullable)) + ", displaySize=") + (displaySize)) + ", precision=") + (precision)) + ", scale=") + (scale)) + ", signed=") + (signed)) + ", className='") + (className)) + '\'') + ']';
        }
    }

    /**
     * Test for reading of default query timeout
     */
    @Test
    public void testDefaultGetQueryTimeout() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_VERSION_SQL)) {
            int timeoutValue = stmt.getQueryTimeout();
            Assert.assertEquals(0L, timeoutValue);
        }
    }

    /**
     * Test Invalid parameter by giving negative timeout
     */
    @Test
    public void testInvalidSetQueryTimeout() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_VERSION_SQL)) {
            // Setting negative value
            int valueToSet = -10;
            try {
                stmt.setQueryTimeout(valueToSet);
            } catch (final SQLException e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("illegal timeout value"));
            }
        }
    }

    /**
     * Test setting a valid timeout
     */
    @Test
    public void testValidSetQueryTimeout() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_VERSION_SQL)) {
            // Setting positive value
            int valueToSet = (new Random(20150304).nextInt(59)) + 1;
            PreparedStatementTest.logger.info("Setting timeout as {} seconds", valueToSet);
            stmt.setQueryTimeout(valueToSet);
            Assert.assertEquals(valueToSet, stmt.getQueryTimeout());
        }
    }

    /**
     * Test setting timeout as zero and executing
     */
    @Test
    public void testSetQueryTimeoutAsZero() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_RANDOM_SQL)) {
            stmt.setQueryTimeout(0);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            int rowCount = 0;
            while (rs.next()) {
                rs.getBytes(1);
                rowCount++;
            } 
            Assert.assertEquals(3, rowCount);
        }
    }

    /**
     * Test setting timeout for a query that actually times out
     */
    @Test
    public void testClientTriggeredQueryTimeout() throws Exception {
        // Setting to a very low value (3sec)
        int timeoutDuration = 3;
        int rowsCounted = 0;
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_RANDOM_SQL)) {
            stmt.setQueryTimeout(timeoutDuration);
            PreparedStatementTest.logger.info("Set a timeout of {} seconds", stmt.getQueryTimeout());
            ResultSet rs = stmt.executeQuery();
            // Fetch each row and pause (simulate a slow client)
            try {
                while (rs.next()) {
                    rs.getString(1);
                    rowsCounted++;
                    // Pause briefly (a second beyond the timeout) before attempting to fetch rows
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis((timeoutDuration + 1)));
                    } catch (InterruptedException e) {
                        /* DoNothing */
                    }
                    PreparedStatementTest.logger.info("Paused for {} seconds", (timeoutDuration + 1));
                } 
            } catch (SQLTimeoutException sqlEx) {
                PreparedStatementTest.logger.info((("Counted " + rowsCounted) + " rows before hitting timeout"));
                return;// Successfully return

            }
        }
        // Throw an exception to indicate that we shouldn't have reached this point
        throw new Exception((("Failed to trigger timeout of " + timeoutDuration) + " sec"));
    }

    /**
     * Test setting timeout that never gets triggered
     */
    @Test
    public void testNonTriggeredQueryTimeout() throws SQLException {
        try (PreparedStatement stmt = PreparedStatementTest.connection.prepareStatement(PreparedStatementTest.SYS_VERSION_SQL)) {
            stmt.setQueryTimeout(60);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            int rowCount = 0;
            while (rs.next()) {
                rs.getBytes(1);
                rowCount++;
            } 
            Assert.assertEquals(1, rowCount);
        }
    }

    // ////////
    // Parameters-not-implemented tests:
    /**
     * Tests that basic case of trying to create a prepare statement with parameters.
     */
    @Test(expected = SQLException.class)
    public void testSqlQueryWithParamNotSupported() throws SQLException {
        try {
            PreparedStatementTest.connection.prepareStatement("VALUES ?, ?");
        } catch (final SQLException e) {
            Assert.assertThat("Check whether params.-unsupported wording changed or checks changed.", e.toString(), CoreMatchers.containsString("Illegal use of dynamic parameter"));
            throw e;
        }
    }

    /**
     * Tests that "not supported" has priority over possible "no parameters"
     *  check.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testParamSettingWhenNoParametersIndexSaysUnsupported() throws SQLException {
        try (PreparedStatement prepStmt = PreparedStatementTest.connection.prepareStatement("VALUES 1")) {
            try {
                prepStmt.setBytes(4, null);
            } catch (final SQLFeatureNotSupportedException e) {
                Assert.assertThat("Check whether params.-unsupported wording changed or checks changed.", e.toString(), PreparedStatementTest.PARAMETERS_NOT_SUPPORTED_MSG_MATCHER);
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
        try (PreparedStatement prepStmt = PreparedStatementTest.connection.prepareStatement("VALUES 1")) {
            try {
                prepStmt.setClob(2, ((Clob) (null)));
            } catch (final SQLFeatureNotSupportedException e) {
                Assert.assertThat("Check whether params.-unsupported wording changed or checks changed.", e.toString(), PreparedStatementTest.PARAMETERS_NOT_SUPPORTED_MSG_MATCHER);
                throw e;
            }
        }
    }
}

