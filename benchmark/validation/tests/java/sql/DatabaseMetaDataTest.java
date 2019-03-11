/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;


// END APACHE-DERBY
public class DatabaseMetaDataTest extends TestCase {
    private static String VIEW_NAME = "myView";

    private static String CREATE_VIEW_QUERY = (("CREATE VIEW " + (DatabaseMetaDataTest.VIEW_NAME)) + " AS SELECT * FROM ") + (DatabaseCreator.TEST_TABLE1);

    private static String DROP_VIEW_QUERY = "DROP VIEW " + (DatabaseMetaDataTest.VIEW_NAME);

    protected static Connection conn;

    protected static DatabaseMetaData meta;

    protected static Statement statement;

    protected static Statement statementForward;

    private static int id = 1;

    /* public void setUp() {
    try {
    super.setUp();
    try {
    conn = Support_SQL.getConnection();
    statement = conn.createStatement();
    statementForward = conn.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_READ_ONLY);
    meta = conn.getMetaData();

    assertFalse(conn.isClosed());
    } catch (SQLException e) {
    fail("Unexpected SQLException " + e.toString());
    }

    } catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    }

    }
     */
    /**
     * {@link java.sql.DatabaseMetaData #getBestRowIdentifier(java.lang.String,
     *        java.lang.String, java.lang.String, int, boolean)}
     */
    public void test_getBestRowIdentifierLjava_lang_StringLjava_lang_StringLjava_lang_StringIZ() throws SQLException {
        ResultSet result = DatabaseMetaDataTest.statementForward.executeQuery(("SELECT * FROM " + (DatabaseCreator.TEST_TABLE1)));
        // Updatable ResultSet not supported, converted to normal insert statement
        DatabaseMetaDataTest.statementForward.executeUpdate((("INSERT INTO " + (DatabaseCreator.TEST_TABLE1)) + " (id, field1) VALUES( 1234567, 'test1');"));
        /* not supported
        try {
        result.moveToInsertRow();
        result.updateInt("id", 1234567);
        result.updateString("field1", "test1");
        result.insertRow();
        } catch (SQLException e) {
        fail("Unexpected SQLException " + e.toString());
        }
         */
        result.close();
        ResultSet rs = DatabaseMetaDataTest.meta.getBestRowIdentifier(null, null, DatabaseCreator.TEST_TABLE1, DatabaseMetaData.bestRowSession, true);
        ResultSetMetaData rsmd = rs.getMetaData();
        TestCase.assertTrue("Rows not obtained", rs.next());
        int col = rsmd.getColumnCount();
        TestCase.assertEquals("Incorrect number of columns", 8, col);
        String[] columnNames = new String[]{ "SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN" };
        for (int c = 1; c <= col; ++c) {
            TestCase.assertEquals("Incorrect column name", columnNames[(c - 1)], rsmd.getColumnName(c));
        }
        TestCase.assertEquals("Incorrect scope", DatabaseMetaData.bestRowSession, rs.getShort("SCOPE"));
        TestCase.assertEquals("Incorrect column name", "_ROWID_", rs.getString("COLUMN_NAME"));
        TestCase.assertEquals("Incorrect data type", Types.INTEGER, rs.getInt("DATA_TYPE"));
        TestCase.assertEquals("Incorrect type name", "INTEGER", rs.getString("TYPE_NAME"));
        rs.close();
        // Exception testing
        DatabaseMetaDataTest.conn.close();
        try {
            DatabaseMetaDataTest.meta.getColumns(null, null, DatabaseCreator.TEST_TABLE1, "%");
            TestCase.fail("SQLException not thrown");
        } catch (SQLException e) {
            // ok
        }
    }

    /**
     * java.sql.DatabaseMetaData#getConnection()
     */
    public void test_getConnection() throws SQLException {
        TestCase.assertEquals("Incorrect connection value", DatabaseMetaDataTest.conn, DatabaseMetaDataTest.meta.getConnection());
        // Exception checking
        DatabaseMetaDataTest.conn.close();
        try {
            Connection con = DatabaseMetaDataTest.meta.getConnection();
            TestCase.assertTrue(con.isClosed());
        } catch (SQLException e) {
            // ok
        }
    }

    /**
     * java.sql.DatabaseMetaData#getDriverMajorVersion()
     */
    public void test_getDriverMajorVersion() throws SQLException {
        TestCase.assertTrue("Incorrect driver major version", ((DatabaseMetaDataTest.meta.getDriverMajorVersion()) >= 0));
    }

    /**
     * java.sql.DatabaseMetaData#getDriverMinorVersion()
     */
    public void test_getDriverMinorVersion() {
        TestCase.assertTrue("Incorrect driver minor version", ((DatabaseMetaDataTest.meta.getDriverMinorVersion()) >= 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxCursorNameLength()
     */
    public void test_getMaxCursorNameLength() throws SQLException {
        int nameLength = DatabaseMetaDataTest.meta.getMaxCursorNameLength();
        if (nameLength > 0) {
            try {
                DatabaseMetaDataTest.statement.setCursorName(new String(new byte[nameLength + 1]));
                TestCase.fail("Expected SQLException was not thrown");
            } catch (SQLException e) {
                // expected
            }
        } else
            if (nameLength < 0) {
                TestCase.fail("Incorrect length of cursor name");
            }

    }

    /**
     * java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
     */
    public void test_storesMixedCaseQuotedIdentifiers() throws SQLException {
        String quote = DatabaseMetaDataTest.meta.getIdentifierQuoteString();
        DatabaseMetaDataTest.insertNewRecord();
        String selectQuery = (((("SELECT " + quote) + "fieLD1") + quote) + " FROM ") + (DatabaseCreator.TEST_TABLE1);
        try {
            DatabaseMetaDataTest.statement.executeQuery(selectQuery);
            if (!(DatabaseMetaDataTest.meta.storesMixedCaseIdentifiers())) {
                TestCase.fail("mixed case is supported");
            }
        } catch (SQLException e) {
            if (DatabaseMetaDataTest.meta.storesMixedCaseQuotedIdentifiers()) {
                TestCase.fail("quoted case is not supported");
            }
        }
        // Exception checking
        /* conn.close();

        try {
        meta.storesMixedCaseIdentifiers();
        fail("SQLException not thrown");
        } catch (SQLException e) {
        //ok
        }

        conn.close();

        try {
        meta.storesMixedCaseQuotedIdentifiers();
        fail("SQLException not thrown");
        } catch (SQLException e) {
        //ok
        }

        }

        @KnownFailure("Ticket 98")
        public void testGetIdentifierQuoteString() throws SQLException {
        assertNotNull(
        meta.getIdentifierQuoteString()
        );

        //Exception test
        /*
        conn.close();
        try {
        meta.getIdentifierQuoteString();
        fail("Should throw exception");
        } catch (SQLException e) {
        //ok
        }
         */
    }

    /**
     * java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
     */
    public void test_supportsTransactionIsolationLevelI() throws SQLException {
        TestCase.assertFalse("database supports TRANSACTION_NONE isolation level", DatabaseMetaDataTest.meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
        // TODO only Connection.TRANSACTION_SERIALIZABLE is supported
        // assertTrue(
        // "database doesn't supports TRANSACTION_READ_COMMITTED isolation level",
        // meta
        // .supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
        // assertTrue(
        // "database doesn't supports TRANSACTION_READ_UNCOMMITTED isolation level",
        // meta
        // .supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
        // assertTrue(
        // "database doesn't supports TRANSACTION_REPEATABLE_READ isolation level",
        // meta
        // .supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
        TestCase.assertTrue("database doesn't supports TRANSACTION_SERIALIZABLE isolation level", DatabaseMetaDataTest.meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
        // Exception checking
        try {
            TestCase.assertFalse("database supports unknown isolation level", DatabaseMetaDataTest.meta.supportsTransactionIsolationLevel(Integer.MAX_VALUE));
        } catch (SQLException e) {
            // ok
        }
    }

    /**
     * java.sql.DatabaseMetaData#updatesAreDetected(int)
     */
    public void test_updatesAreDetectedI() throws SQLException {
        TestCase.assertFalse("visible row update can be detected for TYPE_FORWARD_ONLY type", DatabaseMetaDataTest.meta.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
        TestCase.assertFalse("visible row update can be detected for TYPE_SCROLL_INSENSITIVE type", DatabaseMetaDataTest.meta.updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
        TestCase.assertFalse("visible row update can be detected for TYPE_SCROLL_SENSITIVE type", DatabaseMetaDataTest.meta.updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
        TestCase.assertFalse("visible row update can be detected for unknown type", DatabaseMetaDataTest.meta.updatesAreDetected(100));
        // Exception checking
        DatabaseMetaDataTest.conn.close();
        try {
            DatabaseMetaDataTest.meta.updatesAreDetected(ResultSet.CLOSE_CURSORS_AT_COMMIT);
            TestCase.assertFalse("visible row update can be detected for unknown type", DatabaseMetaDataTest.meta.updatesAreDetected(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        } catch (SQLException e) {
            // ok
        }
    }

    // BEGIN Apache Derby DatabaseMetaDataTest
    /* Escaped function testing TODO complete this list */
    private static final String[][] NUMERIC_FUNCTIONS = // {"SIGN", "-34"}, {"SIN", "0.32"}, {"SQRT", "6.22"},
    // {"TAN", "0.57",}, {"TRUNCATE", "345.395", "1"}
    new String[][]{ // Section C.1 JDBC 3.0 spec.
    new String[]{ "ABS", "-25.67" }, // {"ACOS", "0.0707"}, {"ASIN", "0.997"},
    // {"ATAN", "14.10"}, {"ATAN2", "0.56", "1.2"}, {"CEILING", "3.45"},
    // {"COS", "1.2"}, {"COT", "3.4"}, {"DEGREES", "2.1"}, {"EXP", "2.3"},
    // {"FLOOR", "3.22"}, {"LOG", "34.1"}, {"LOG10", "18.7"},
    // {"MOD", "124", "7"}, {"PI"}, {"POWER", "2", "3"},
    // {"RADIANS", "54"}, {"RAND", "17"},
    new String[]{ "ROUND", "345.345", "1" } }// {"SIGN", "-34"}, {"SIN", "0.32"}, {"SQRT", "6.22"},
    // {"TAN", "0.57",}, {"TRUNCATE", "345.395", "1"}
    ;

    private static final String[][] TIMEDATE_FUNCTIONS = // TODO Complete list
    new String[][]{ // Section C.3 JDBC 3.0 spec.
    new String[]{ "date", "'now'" } }// TODO Complete list
    ;

    private static final String[][] SYSTEM_FUNCTIONS = new String[][]{ // Section C.4 JDBC 3.0 spec.
    new String[]{ "IFNULL", "'this'", "'that'" }, new String[]{ "USER" } };

    /* TODO complete or check this list */
    private static final String[][] STRING_FUNCTIONS = // {"REPEAT", "'echo'", "3"},
    // {"REPLACE", "'to be or not to be'", "'be'", "'England'"},
    // {"RTRIM", "'  right trim   '"}, {"SOUNDEX", "'Derby'"},
    // {"SPACE", "12"},
    // {"SUBSTRING", "'Ruby the Rubicon Jeep'", "10", "7",},
    // {"UCASE", "'Fernando Alonso'"}
    new String[][]{ // Section C.2 JDBC 3.0 spec.
    // {"ASCII", "'Yellow'"}, {"CHAR", "65"},
    // {"CONCAT", "'hello'", "'there'"},
    // {"DIFFERENCE", "'Pires'", "'Piers'"},
    // {"INSERT", "'Bill Clinton'", "4", "'William'"},
    // {"LCASE", "'Fernando Alonso'"}, {"LEFT", "'Bonjour'", "3"},
    // {"LENGTH", "'four    '"}, {"LOCATE", "'jour'", "'Bonjour'"},
    new String[]{ "LTRIM", "'   left trim   '" } }// {"REPEAT", "'echo'", "3"},
    // {"REPLACE", "'to be or not to be'", "'be'", "'England'"},
    // {"RTRIM", "'  right trim   '"}, {"SOUNDEX", "'Derby'"},
    // {"SPACE", "12"},
    // {"SUBSTRING", "'Ruby the Rubicon Jeep'", "10", "7",},
    // {"UCASE", "'Fernando Alonso'"}
    ;

    /**
     * Six combinations of valid identifiers with mixed case, to see how the
     * various pattern matching and returned values handle them. This test only
     * creates objects in these schemas.
     */
    private static final String[] IDS = new String[]{ "one_meta_test", "TWO_meta_test", "ThReE_meta_test", "\"four_meta_test\"", "\"FIVE_meta_test\"", "\"sIx_meta_test\"" };

    /**
     * All the builtin schemas.
     */
    private static final String[] BUILTIN_SCHEMAS = // TODO: Are there any other built in schemas?
    new String[]{  }// TODO: Are there any other built in schemas?
    ;

    /**
     * Test getSchemas() without modifying the database.
     *
     * @throws SQLException
     * 		
     */
    public void testGetSchemasReadOnly() throws SQLException {
        ResultSet rs = DatabaseMetaDataTest.meta.getSchemas();
        DatabaseMetaDataTest.checkSchemas(rs, new String[0]);
    }
}

