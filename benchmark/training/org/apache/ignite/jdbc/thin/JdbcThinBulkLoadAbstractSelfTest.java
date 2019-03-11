/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * COPY statement tests.
 */
public abstract class JdbcThinBulkLoadAbstractSelfTest extends JdbcThinAbstractDmlStatementSelfTest {
    /**
     * Subdirectory with CSV files
     */
    private static final String CSV_FILE_SUBDIR = "/modules/clients/src/test/resources/";

    /**
     * Default table name.
     */
    private static final String TBL_NAME = "Person";

    /**
     * A CSV file with zero records
     */
    private static final String BULKLOAD_EMPTY_CSV_FILE = Objects.requireNonNull(resolveIgnitePath(((JdbcThinBulkLoadAbstractSelfTest.CSV_FILE_SUBDIR) + "bulkload0.csv"))).getAbsolutePath();

    /**
     * A CSV file with one record.
     */
    private static final String BULKLOAD_ONE_LINE_CSV_FILE = Objects.requireNonNull(resolveIgnitePath(((JdbcThinBulkLoadAbstractSelfTest.CSV_FILE_SUBDIR) + "bulkload1.csv"))).getAbsolutePath();

    /**
     * A CSV file with two records.
     */
    private static final String BULKLOAD_TWO_LINES_CSV_FILE = Objects.requireNonNull(resolveIgnitePath(((JdbcThinBulkLoadAbstractSelfTest.CSV_FILE_SUBDIR) + "bulkload2.csv"))).getAbsolutePath();

    /**
     * A CSV file in UTF-8.
     */
    private static final String BULKLOAD_UTF8_CSV_FILE = Objects.requireNonNull(resolveIgnitePath(((JdbcThinBulkLoadAbstractSelfTest.CSV_FILE_SUBDIR) + "bulkload2_utf8.csv"))).getAbsolutePath();

    /**
     * A CSV file in windows-1251.
     */
    private static final String BULKLOAD_CP1251_CSV_FILE = Objects.requireNonNull(resolveIgnitePath(((JdbcThinBulkLoadAbstractSelfTest.CSV_FILE_SUBDIR) + "bulkload2_windows1251.csv"))).getAbsolutePath();

    /**
     * Basic COPY statement used in majority of the tests.
     */
    public static final String BASIC_SQL_COPY_STMT = ((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "'") + " into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv";

    /**
     * JDBC statement.
     */
    private Statement stmt;

    /**
     * Dead-on-arrival test. Imports two-entry CSV file into a table and checks
     * the created entries using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testBasicStatement() throws SQLException {
        int updatesCnt = stmt.executeUpdate(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Imports zero-entry CSV file into a table and checks that no entries are created
     * using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testEmptyFile() throws SQLException {
        int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_EMPTY_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
        assertEquals(0, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 0);
    }

    /**
     * Imports one-entry CSV file into a table and checks the entry created using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testOneLineFile() throws SQLException {
        int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_ONE_LINE_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
        assertEquals(1, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 1);
    }

    /**
     * Verifies that error is reported for empty charset name.
     */
    @Test
    public void testEmptyCharset() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate(("copy from 'any.file' into Person " + ("(_key, age, firstName, lastName) " + "format csv charset ''")));
                return null;
            }
        }, SQLException.class, "Unknown charset name: ''");
    }

    /**
     * Verifies that error is reported for unsupported charset name.
     */
    @Test
    public void testNotSupportedCharset() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate(("copy from 'any.file' into Person " + ("(_key, age, firstName, lastName) " + "format csv charset 'nonexistent'")));
                return null;
            }
        }, SQLException.class, "Charset is not supported: 'nonexistent'");
    }

    /**
     * Verifies that error is reported for unknown charset name.
     */
    @Test
    public void testUnknownCharset() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate(("copy from 'any.file' into Person " + ("(_key, age, firstName, lastName) " + "format csv charset \'8^)\'")));
                return null;
            }
        }, SQLException.class, "Unknown charset name: '8^)'");
    }

    /**
     * Verifies that ASCII encoding is recognized and imported.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testAsciiCharset() throws SQLException {
        int updatesCnt = stmt.executeUpdate((((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "'") + " into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv charset 'ascii'"));
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Imports two-entry CSV file with UTF-8 characters into a table and checks
     * the created entries using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testUtf8Charset() throws SQLException {
        checkBulkLoadWithCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE, "utf-8");
    }

    /**
     * Verifies that ASCII encoding is recognized and imported.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testWin1251Charset() throws SQLException {
        checkBulkLoadWithCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_CP1251_CSV_FILE, "windows-1251");
    }

    /**
     * Verifies that no error is reported and characters are converted improperly when we import
     * UTF-8 as windows-1251.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testWrongCharset_Utf8AsWin1251() throws SQLException {
        checkBulkLoadWithWrongCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE, "UTF-8", "windows-1251");
    }

    /**
     * Verifies that no error is reported and characters are converted improperly when we import
     * windows-1251 as UTF-8.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testWrongCharset_Win1251AsUtf8() throws SQLException {
        checkBulkLoadWithWrongCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_CP1251_CSV_FILE, "windows-1251", "UTF-8");
    }

    /**
     * Verifies that no error is reported and characters are converted improperly when we import
     * UTF-8 as ASCII.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testWrongCharset_Utf8AsAscii() throws SQLException {
        checkBulkLoadWithWrongCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE, "UTF-8", "ascii");
    }

    /**
     * Verifies that no error is reported and characters are converted improperly when we import
     * windows-1251 as ASCII.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testWrongCharset_Win1251AsAscii() throws SQLException {
        checkBulkLoadWithWrongCharset(JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_CP1251_CSV_FILE, "windows-1251", "ascii");
    }

    /**
     * Checks that bulk load works when we use packet size of 1 byte and thus
     * create multiple packets per COPY.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPacketSize_1() throws SQLException {
        int updatesCnt = stmt.executeUpdate(((JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT) + " packet_size 1"));
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Imports two-entry CSV file with UTF-8 characters into a table and checks
     * the created entries using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testDefaultCharset() throws SQLException {
        int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
        assertEquals(2, updatesCnt);
        checkNationalCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME);
    }

    /**
     * Test imports CSV file into a table on not affinity node and checks the created entries using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testBulkLoadToNonAffinityNode() throws Exception {
        IgniteEx client = startGrid(getConfiguration("client").setClientMode(true));
        try (Connection con = connect(client, null)) {
            con.setSchema((('"' + (DEFAULT_CACHE_NAME)) + '"'));
            try (Statement stmt = con.createStatement()) {
                int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
                assertEquals(2, updatesCnt);
                checkNationalCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME);
            }
        }
        stopGrid(client.name());
    }

    /**
     * Imports two-entry CSV file with UTF-8 characters into a table using packet size of one byte
     * (thus splitting each two-byte UTF-8 character into two packets)
     * and checks the created entries using SELECT statement.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testDefaultCharsetPacketSize1() throws SQLException {
        int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv packet_size 1"));
        assertEquals(2, updatesCnt);
        checkNationalCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME);
    }

    /**
     * Checks that error is reported for a non-existent file.
     */
    @Test
    public void testWrongFileName() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate(("copy from 'nonexistent' into Person" + (" (_key, age, firstName, lastName)" + " format csv")));
                return null;
            }
        }, SQLException.class, "Failed to read file: 'nonexistent'");
    }

    /**
     * Checks that error is reported if the destination table is missing.
     */
    @Test
    public void testMissingTable() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "' into Peterson") + " (_key, age, firstName, lastName)") + " format csv"));
                return null;
            }
        }, SQLException.class, "Table does not exist: PETERSON");
    }

    /**
     * Checks that error is reported when a non-existing column is specified in the SQL command.
     */
    @Test
    public void testWrongColumnName() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "' into Person") + " (_key, age, firstName, lostName)") + " format csv"));
                return null;
            }
        }, SQLException.class, "Column \"LOSTNAME\" not found");
    }

    /**
     * Checks that error is reported if field read from CSV file cannot be converted to the type of the column.
     */
    @Test
    public void testWrongColumnType() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "' into Person") + " (_key, firstName, age, lastName)") + " format csv"));
                return null;
            }
        }, SQLException.class, "Value conversion failed [from=java.lang.String, to=java.lang.Integer]");
    }

    /**
     * Checks that if even a subset of fields is imported, the imported fields are set correctly.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testFieldsSubset() throws SQLException {
        int updatesCnt = stmt.executeUpdate((((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "'") + " into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName)") + " format csv"));
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, false, 2);
    }

    /**
     * Checks that bulk load works when we create table using 'CREATE TABLE' command.
     * <p>
     * The majority of the tests in this class use {@link CacheConfiguration#setIndexedTypes(Class[])}
     * to create the table.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testCreateAndBulkLoadTable() throws SQLException {
        String tblName = (QueryUtils.DFLT_SCHEMA) + ".\"PersonTbl\"";
        execute(conn, (("create table " + tblName) + " (id int primary key, age int, firstName varchar(30), lastName varchar(30))"));
        int updatesCnt = stmt.executeUpdate(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "' into ") + tblName) + "(_key, age, firstName, lastName)") + " format csv"));
        assertEquals(2, updatesCnt);
        checkCacheContents(tblName, true, 2);
    }

    /**
     * Checks that bulk load works when we create table with {@link CacheConfiguration#setQueryEntities(Collection)}.
     * <p>
     * The majority of the tests in this class use {@link CacheConfiguration#setIndexedTypes(Class[])}
     * to create a table.
     *
     * @throws SQLException
     * 		If failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConfigureQueryEntityAndBulkLoad() throws SQLException {
        ignite(0).getOrCreateCache(cacheConfigWithQueryEntity());
        int updatesCnt = stmt.executeUpdate(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Verifies exception thrown if COPY is added into a packet.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testMultipleStatement() throws SQLException {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.addBatch(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
                stmt.addBatch(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_ONE_LINE_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
                stmt.addBatch(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_UTF8_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format csv"));
                stmt.executeBatch();
                return null;
            }
        }, BatchUpdateException.class, "COPY command cannot be executed in batch mode.");
    }

    /**
     * Verifies that COPY command is rejected by Statement.executeQuery().
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecuteQuery() throws SQLException {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeQuery(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
                return null;
            }
        }, SQLException.class, "Given statement type does not match that declared by JDBC driver");
    }

    /**
     * Verifies that COPY command works in Statement.execute().
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecute() throws SQLException {
        boolean isRowSet = stmt.execute(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
        assertFalse(isRowSet);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Verifies that COPY command can be called with PreparedStatement.executeUpdate().
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPreparedStatementWithExecuteUpdate() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
        int updatesCnt = pstmt.executeUpdate();
        assertEquals(2, updatesCnt);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Verifies that COPY command reports an error when used with PreparedStatement parameter.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPreparedStatementWithParameter() throws SQLException {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                PreparedStatement pstmt = conn.prepareStatement(((((("copy from '" + (JdbcThinBulkLoadAbstractSelfTest.BULKLOAD_TWO_LINES_CSV_FILE)) + "' into ") + (JdbcThinBulkLoadAbstractSelfTest.TBL_NAME)) + " (_key, age, firstName, lastName)") + " format ?"));
                pstmt.setString(1, "csv");
                pstmt.executeUpdate();
                return null;
            }
        }, SQLException.class, "Unexpected token: \"?\" (expected: \"[identifier]\"");
    }

    /**
     * Verifies that COPY command can be called with PreparedStatement.execute().
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPreparedStatementWithExecute() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
        boolean isRowSet = pstmt.execute();
        assertFalse(isRowSet);
        checkCacheContents(JdbcThinBulkLoadAbstractSelfTest.TBL_NAME, true, 2);
    }

    /**
     * Verifies that COPY command is rejected by PreparedStatement.executeQuery().
     */
    @Test
    public void testPreparedStatementWithExecuteQuery() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                PreparedStatement pstmt = conn.prepareStatement(JdbcThinBulkLoadAbstractSelfTest.BASIC_SQL_COPY_STMT);
                pstmt.executeQuery();
                return null;
            }
        }, SQLException.class, "Given statement type does not match that declared by JDBC driver");
    }

    /**
     * Recodes an input string as if it was encoded in one charset and was read using
     * another charset using {@link CodingErrorAction#REPLACE} settings for
     * unmappable and malformed characters.
     */
    private static class WrongCharsetRecoder implements IgniteClosure<String, String> {
        /**
         * Charset in which the string we are reading is actually encoded.
         */
        private final Charset actualCharset;

        /**
         * Charset which we use to read the string.
         */
        private final Charset appliedCharset;

        /**
         * Creates the recoder.
         *
         * @param actualCharset
         * 		Charset in which the string we are reading is actually encoded.
         * @param appliedCharset
         * 		Charset which we use to read the string.
         * @throws UnsupportedCharsetException
         * 		if the charset name is wrong.
         */
        WrongCharsetRecoder(String actualCharset, String appliedCharset) {
            this.actualCharset = Charset.forName(actualCharset);
            this.appliedCharset = Charset.forName(appliedCharset);
        }

        /**
         * Converts string as it was read using a wrong charset.
         * <p>
         * First the method converts the string into {@link #actualCharset} and puts bytes into a buffer.
         * Then it tries to read these bytes from the buffer using {@link #appliedCharset} and
         * {@link CodingErrorAction#REPLACE} settings for unmappable and malformed characters
         * (NB: these settings implicitly come from {@link Charset#decode(ByteBuffer)} implementation, while
         * being explicitly set in {@link BulkLoadCsvParser#BulkLoadCsvParser(BulkLoadCsvFormat)}).
         *
         * @param input
         * 		The input string (in Java encoding).
         * @return The converted string.
         */
        @Override
        public String apply(String input) {
            ByteBuffer encodedBuf = actualCharset.encode(input);
            return appliedCharset.decode(encodedBuf).toString();
        }
    }
}

