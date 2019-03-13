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


public class LucidDBDatabaseMetaTest {
    private LucidDBDatabaseMeta nativeMeta;

    private LucidDBDatabaseMeta jndiMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(8034, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), jndiMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("org.luciddb.jdbc.LucidDbClientDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("jdbc:luciddb:http://FOO", nativeMeta.getURL("FOO", "BAR", "IGNORED"));
        Assert.assertEquals("jdbc:luciddb:http://FOO:1033", nativeMeta.getURL("FOO", "1033", "IGNORED"));
        Assert.assertEquals("jdbc:luciddb:http://FOO", nativeMeta.getURL("FOO", "", "IGNORED"));
        Assert.assertTrue(nativeMeta.isFetchSizeSupported());
        Assert.assertTrue(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertArrayEquals(new String[]{ "LucidDbClient.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertArrayEquals(new String[]{ "ABS", "ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALLOW", "ALTER", "ANALYZE", "AND", "ANY", "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE", "ASSERTION", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CHECKPOINT", "CLOB", "CLOSE", "CLUSTERED", "COALESCE", "COLLATE", "COLLATION", "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DENSE_RANK", "DEREF", "DESC", "DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTICS", "DISALLOW", "DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXPLAIN", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FIRST", "FIRST_VALUE", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FOUND", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IMMEDIATE", "IMPORT", "IN", "INADD", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LARGE", "LAST", "LAST_VALUE", "LATERAL", "LEADING", "LEFT", "LEVEL", "LIKE", "LIMIT", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NEXT", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTADD", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PAD", "PARAMETER", "PARTIAL", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "RANGE", "RANK", "READ", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "RELATIVE", "RELEASE", "RESTRICT", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROW_NUMBER", "ROWS", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SECTION", "SELECT", "SENSITIVE", "SESSION", "SESSION_USER", "SET", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TINYINT", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "TRUNCATE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VARBINARY", "VARCHAR", "VAR_POP", "VAR_SAMP", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE" }, nativeMeta.getReservedWords());
        Assert.assertEquals("http://pub.eigenbase.org/wiki/LucidDbDocs", nativeMeta.getExtraOptionsHelpText());
        Assert.assertTrue(nativeMeta.useSchemaNameForTableList());
        Assert.assertTrue(nativeMeta.requiresCastToVariousForIsNull());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("SELECT * FROM FOO", nativeMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("SELECT FOO FROM BAR", nativeMeta.getSQLQueryColumnFields("FOO", "BAR"));
        Assert.assertEquals("SELECT FOO FROM BAR", nativeMeta.getSQLColumnExists("FOO", "BAR"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertNull(nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertNull(nativeMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, false, false));
        // Simple hack to prevent duplication of code. Checking the case of supported boolean type
        // both supported and unsupported. Should return BOOLEAN if supported, or CHAR(1) if not.
        String[] typeCk = new String[]{ "CHAR(1)", "BOOLEAN", "CHAR(1)" };
        int i = (nativeMeta.supportsBooleanDataType()) ? 1 : 0;
        Assert.assertEquals(typeCk[i], nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        jndiMeta.setSupportsBooleanDataType((!(jndiMeta.supportsBooleanDataType())));
        Assert.assertEquals(typeCk[(i + 1)], jndiMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        jndiMeta.setSupportsBooleanDataType((!(jndiMeta.supportsBooleanDataType())));
        Assert.assertEquals("BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", true, false, false));
        Assert.assertEquals("BIGINT PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("BIGINT PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", false, false, false));
        // Integer types
        Assert.assertEquals("INT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 8, 0), "", "", false, false, false));
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 9, 0), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(19,0)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, 0), "", "", false, false, false));
        // Other Numbers
        Assert.assertEquals("DECIMAL(10,5)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 5), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", (-10), (-5)), "", "", false, false, false));
        // String Types
        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(100)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO"), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

