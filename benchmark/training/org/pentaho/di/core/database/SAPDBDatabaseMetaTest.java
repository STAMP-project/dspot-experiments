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
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class SAPDBDatabaseMetaTest {
    private SAPDBDatabaseMeta nativeMeta;

    private SAPDBDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals((-1), nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertFalse(nativeMeta.supportsAutoInc());
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("com.sap.dbtech.jdbc.DriverSapDB", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:sapdb://FOO/WIBBLE", nativeMeta.getURL("FOO", "IGNORED", "WIBBLE"));
        Assert.assertTrue(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertArrayEquals(new String[]{ "sapdbc.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertEquals(new String[]{ "ABS", "ABSOLUTE", "ACOS", "ADDDATE", "ADDTIME", "ALL", "ALPHA", "ALTER", "ANY", "ASCII", "ASIN", "ATAN", "ATAN2", "AVG", "BINARY", "BIT", "BOOLEAN", "BYTE", "CASE", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHECK", "CHR", "COLUMN", "CONCAT", "CONSTRAINT", "COS", "COSH", "COT", "COUNT", "CROSS", "CURDATE", "CURRENT", "CURTIME", "DATABASE", "DATE", "DATEDIFF", "DAY", "DAYNAME", "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "DEC", "DECIMAL", "DECODE", "DEFAULT", "DEGREES", "DELETE", "DIGITS", "DISTINCT", "DOUBLE", "EXCEPT", "EXISTS", "EXP", "EXPAND", "FIRST", "FIXED", "FLOAT", "FLOOR", "FOR", "FROM", "FULL", "GET_OBJECTNAME", "GET_SCHEMA", "GRAPHIC", "GREATEST", "GROUP", "HAVING", "HEX", "HEXTORAW", "HOUR", "IFNULL", "IGNORE", "INDEX", "INITCAP", "INNER", "INSERT", "INT", "INTEGER", "INTERNAL", "INTERSECT", "INTO", "JOIN", "KEY", "LAST", "LCASE", "LEAST", "LEFT", "LENGTH", "LFILL", "LIST", "LN", "LOCATE", "LOG", "LOG10", "LONG", "LONGFILE", "LOWER", "LPAD", "LTRIM", "MAKEDATE", "MAKETIME", "MAPCHAR", "MAX", "MBCS", "MICROSECOND", "MIN", "MINUTE", "MOD", "MONTH", "MONTHNAME", "NATURAL", "NCHAR", "NEXT", "NO", "NOROUND", "NOT", "NOW", "NULL", "NUM", "NUMERIC", "OBJECT", "OF", "ON", "ORDER", "PACKED", "PI", "POWER", "PREV", "PRIMARY", "RADIANS", "REAL", "REJECT", "RELATIVE", "REPLACE", "RFILL", "RIGHT", "ROUND", "ROWID", "ROWNO", "RPAD", "RTRIM", "SECOND", "SELECT", "SELUPD", "SERIAL", "SET", "SHOW", "SIGN", "SIN", "SINH", "SMALLINT", "SOME", "SOUNDEX", "SPACE", "SQRT", "STAMP", "STATISTICS", "STDDEV", "SUBDATE", "SUBSTR", "SUBSTRING", "SUBTIME", "SUM", "SYSDBA", "TABLE", "TAN", "TANH", "TIME", "TIMEDIFF", "TIMESTAMP", "TIMEZONE", "TO", "TOIDENTIFIER", "TRANSACTION", "TRANSLATE", "TRIM", "TRUNC", "TRUNCATE", "UCASE", "UID", "UNICODE", "UNION", "UPDATE", "UPPER", "USER", "USERGROUP", "USING", "UTCDATE", "UTCDIFF", "VALUE", "VALUES", "VARCHAR", "VARGRAPHIC", "VARIANCE", "WEEK", "WEEKOFYEAR", "WHEN", "WHERE", "WITH", "YEAR", "ZONED" }, nativeMeta.getReservedWords());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ALTER COLUMN BAR TYPE VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        Assert.assertEquals("BIGINT NOT NULL PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("BIGINT NOT NULL PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 0), "", "FOO", false, false, false));
        // Decimal Types
        Assert.assertEquals("DECIMAL(10, 3)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 3), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(19)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 19, 0), "", "", false, false, false));
        // Integers
        Assert.assertEquals("INT64", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 0), "", "", false, false, false));
        Assert.assertEquals("SMALLINT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 4, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 5, 0), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", (-7), (-3)), "", "", false, false, false));
        // Strings
        Assert.assertEquals("VARCHAR(32719)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32719, 0), "", "", false, false, false));
        Assert.assertEquals("BLOB SUB_TYPE TEXT", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32720, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(8000)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", (-122), 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

