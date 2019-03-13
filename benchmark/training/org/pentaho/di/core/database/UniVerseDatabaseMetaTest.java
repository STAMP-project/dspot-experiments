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
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class UniVerseDatabaseMetaTest {
    private UniVerseDatabaseMeta nativeMeta;

    private UniVerseDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(65535, nativeMeta.getMaxVARCHARLength());
        Assert.assertEquals((-1), nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("com.ibm.u2.jdbc.UniJDBCDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:ibm-u2://FOO/WIBBLE", nativeMeta.getURL("FOO", "IGNORED", "WIBBLE"));
        Assert.assertEquals("\"FOO\".\"BAR\"", nativeMeta.getSchemaTableCombination("FOO", "BAR"));
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertTrue(nativeMeta.supportsNewLinesInSQL());
        Assert.assertFalse(nativeMeta.supportsTimeStampToDateConversion());
        Assert.assertArrayEquals(new String[]{ "@NEW", "@OLD", "ACTION", "ADD", "AL", "ALL", "ALTER", "AND", "AR", "AS", "ASC", "ASSOC", "ASSOCIATED", "ASSOCIATION", "AUTHORIZATION", "AVERAGE", "AVG", "BEFORE", "BETWEEN", "BIT", "BOTH", "BY", "CALC", "CASCADE", "CASCADED", "CAST", "CHAR", "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH", "CHECK", "COL.HDG", "COL.SPACES", "COL.SPCS", "COL.SUP", "COLUMN", "COMPILED", "CONNECT", "CONSTRAINT", "CONV", "CONVERSION", "COUNT", "COUNT.SUP", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "DATA", "DATE", "DBA", "DBL.SPC", "DEC", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DET.SUP", "DICT", "DISPLAY.NAME", "DISPLAYLIKE", "DISPLAYNAME", "DISTINCT", "DL", "DOUBLE", "DR", "DROP", "DYNAMIC", "E.EXIST", "EMPTY", "EQ", "EQUAL", "ESCAPE", "EVAL", "EVERY", "EXISTING", "EXISTS", "EXPLAIN", "EXPLICIT", "FAILURE", "FIRST", "FLOAT", "FMT", "FOOTER", "FOOTING", "FOR", "FOREIGN", "FORMAT", "FROM", "FULL", "GE", "GENERAL", "GRAND", "GRAND.TOTAL", "GRANT", "GREATER", "GROUP", "GROUP.SIZE", "GT", "HAVING", "HEADER", "HEADING", "HOME", "IMPLICIT", "IN", "INDEX", "INNER", "INQUIRING", "INSERT", "INT", "INTEGER", "INTO", "IS", "JOIN", "KEY", "LARGE.RECORD", "LAST", "LE", "LEADING", "LEFT", "LESS", "LIKE", "LOCAL", "LOWER", "LPTR", "MARGIN", "MATCHES", "MATCHING", "MAX", "MERGE.LOAD", "MIN", "MINIMIZE.SPACE", "MINIMUM.MODULUS", "MODULO", "MULTI.VALUE", "MULTIVALUED", "NATIONAL", "NCHAR", "NE", "NO", "NO.INDEX", "NO.OPTIMIZE", "NO.PAGE", "NOPAGE", "NOT", "NRKEY", "NULL", "NUMERIC", "NVARCHAR", "ON", "OPTION", "OR", "ORDER", "OUTER", "PCT", "PRECISION", "PRESERVING", "PRIMARY", "PRIVILEGES", "PUBLIC", "REAL", "RECORD.SIZE", "REFERENCES", "REPORTING", "RESOURCE", "RESTORE", "RESTRICT", "REVOKE", "RIGHT", "ROWUNIQUE", "SAID", "SAMPLE", "SAMPLED", "SCHEMA", "SELECT", "SEPARATION", "SEQ.NUM", "SET", "SINGLE.VALUE", "SINGLEVALUED", "SLIST", "SMALLINT", "SOME", "SPLIT.LOAD", "SPOKEN", "SUBSTRING", "SUCCESS", "SUM", "SUPPRESS", "SYNONYM", "TABLE", "TIME", "TO", "TOTAL", "TRAILING", "TRIM", "TYPE", "UNION", "UNIQUE", "UNNEST", "UNORDERED", "UPDATE", "UPPER", "USER", "USING", "VALUES", "VARBIT", "VARCHAR", "VARYING", "VERT", "VERTICALLY", "VIEW", "WHEN", "WHERE", "WITH" }, nativeMeta.getReservedWords());
        Assert.assertArrayEquals(new String[]{ "unijdbc.jar", "asjava.zip" }, nativeMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATE", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATE", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, false, false));// Note - Rocket U2 does *not* support timestamps ...

        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "", "FOO", false, false, false));
        // Numeric Types
        Assert.assertEquals("DECIMAL(5, 5)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 5, 5), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(19, 0)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 18, 0), "", "", false, false, false));
        Assert.assertEquals("DOUBLE PRECISION", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", (-7), (-3)), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(65535)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 65537, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

