/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.database;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class DerbyDatabaseMetaTest {
    private DerbyDatabaseMeta nativeMeta;

    private DerbyDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("org.apache.derby.jdbc.EmbeddedDriver", nativeMeta.getDriverClass());
        nativeMeta.setHostname("FOOHOST");
        Assert.assertEquals("org.apache.derby.jdbc.ClientDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("jdbc:derby://FOO/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));
        Assert.assertEquals("jdbc:derby://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:derby:FOO", nativeMeta.getURL("", "", "FOO"));
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL(null, null, "FOO"));
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("xxxxxx", "zzzzzzz", "FOO"));
        Assert.assertTrue(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertArrayEquals(new String[]{ "derbyclient.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertEquals(1527, nativeMeta.getDefaultDatabasePort());
        Assert.assertFalse(nativeMeta.supportsGetBlob());
        Assert.assertEquals("http://db.apache.org/derby/papers/DerbyClientSpec.html", nativeMeta.getExtraOptionsHelpText());
        Assert.assertEquals(new String[]{ "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BOOLEAN", "BOTH", "BY", "CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CHAR", "CHARACTER", "CHECK", "CLOSE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "ELSE", "END", "ENDEXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXPLAIN", "EXTERNAL", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL", "FUNCTION", "GET", "GET_CURRENT_CONNECTION", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LAST", "LEFT", "LIKE", "LONGINT", "LOWER", "LTRIM", "MATCH", "MAX", "MIN", "MINUTE", "NATIONAL", "NATURAL", "NCHAR", "NVARCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "RTRIM", "SCHEMA", "SCROLL", "SECOND", "SELECT", "SESSION_USER", "SET", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTR", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USER", "USING", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE", "XML", "XMLEXISTS", "XMLPARSE", "XMLSERIALIZE", "YEAR" }, nativeMeta.getReservedWords());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TIMESTAMP", nativeMeta.getAddColumnStatement("FOO", new ValueMetaDate("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TIMESTAMP", nativeMeta.getAddColumnStatement("FOO", new ValueMetaTimestamp("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR CHAR(1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR SMALLINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 0, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR INTEGER", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR FLOAT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR FLOAT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DECIMAL(21, 4)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 21, 4), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR CLOB", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", ((nativeMeta.getMaxVARCHARLength()) + 2), 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BLOB", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBinary("BAR", ((nativeMeta.getMaxVARCHARLength()) + 2), 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BLOB", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBinary("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BLOB", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBinary("BAR", 200, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR FLOAT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, (-7)), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DECIMAL(22, 7)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 22, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR FLOAT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", (-10), 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR FLOAT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR UNKNOWN", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInternetAddress("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "BAR", true, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 26, 8), "BAR", true, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals(("ALTER TABLE FOO DROP BAR" + lineSep), nativeMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO ALTER BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("insert into FOO(FOOVERSION) values (1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }
}

