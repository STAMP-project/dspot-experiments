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


public class VerticaDatabaseMetaTest {
    private VerticaDatabaseMeta nativeMeta;

    private VerticaDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(5433, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals(5433, odbcMeta.getDefaultDatabasePort());// Inconsistent with all other DatabaseMetas

        Assert.assertEquals("com.vertica.Driver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:vertica://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:vertica://FOO:/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));// Believe this is a bug - must have the port. Inconsistent with others

        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertArrayEquals(new String[]{ "vertica_2.5_jdk_5.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertEquals(4000, nativeMeta.getMaxVARCHARLength());
        Assert.assertArrayEquals(new String[]{ // From "SQL Reference Manual.pdf" found on support.vertica.com
        "ABORT", "ABSOLUTE", "ACCESS", "ACTION", "ADD", "AFTER", "AGGREGATE", "ALL", "ALSO", "ALTER", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC", "ASSERTION", "ASSIGNMENT", "AT", "AUTHORIZATION", "BACKWARD", "BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BIT", "BLOCK_DICT", "BLOCKDICT_COMP", "BOOLEAN", "BOTH", "BY", "CACHE", "CALLED", "CASCADE", "CASE", "CAST", "CATALOG_PATH", "CHAIN", "CHAR", "CHARACTER", "CHARACTERISTICS", "CHECK", "CHECKPOINT", "CLASS", "CLOSE", "CLUSTER", "COALESCE", "COLLATE", "COLUMN", "COMMENT", "COMMIT", "COMMITTED", "COMMONDELTA_COMP", "CONSTRAINT", "CONSTRAINTS", "CONVERSION", "CONVERT", "COPY", "CORRELATION", "CREATE", "CREATEDB", "CREATEUSER", "CROSS", "CSV", "CURRENT_DATABASE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CYCLE", "DATA", "DATABASE", "DATAPATH", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINER", "DELETE", "DELIMITER", "DELIMITERS", "DELTARANGE_COMP", "DELTARANGE_COMP_SP", "DELTAVAL", "DESC", "DETERMINES", "DIRECT", "DISTINCT", "DISTVALINDEX", "DO", "DOMAIN", "DOUBLE", "DROP", "EACH", "ELSE", "ENCODING", "ENCRYPTED", "END", "EPOCH", "ERROR", "ESCAPE", "EXCEPT", "EXCEPTIONS", "EXCLUDING", "EXCLUSIVE", "EXECUTE", "EXISTS", "EXPLAIN", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FORCE", "FOREIGN", "FORWARD", "FREEZE", "FROM", "FULL", "FUNCTION", "GLOBAL", "GRANT", "GROUP", "HANDLER", "HAVING", "HOLD", "HOUR", "ILIKE", "IMMEDIATE", "IMMUTABLE", "IMPLICIT", "IN", "IN_P", "INCLUDING", "INCREMENT", "INDEX", "INHERITS", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INSTEAD", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "INVOKER", "IS", "ISNULL", "ISOLATION", "JOIN", "KEY", "LANCOMPILER", "LANGUAGE", "LARGE", "LAST", "LATEST", "LEADING", "LEFT", "LESS", "LEVEL", "LIKE", "LIMIT", "LISTEN", "LOAD", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCK", "MATCH", "MAXVALUE", "MERGEOUT", "MINUTE", "MINVALUE", "MOBUF", "MODE", "MONTH", "MOVE", "MOVEOUT", "MULTIALGORITHM_COMP", "MULTIALGORITHM_COMP_SP", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEW", "NEXT", "NO", "NOCREATEDB", "NOCREATEUSER", "NODE", "NODES", "NONE", "NOT", "NOTHING", "NOTIFY", "NOTNULL", "NOWAIT", "NULL", "NULLIF", "NUMERIC", "OBJECT", "OF", "OFF", "OFFSET", "OIDS", "OLD", "ON", "ONLY", "OPERATOR", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OVERLAPS", "OVERLAY", "OWNER", "PARTIAL", "PARTITION", "PASSWORD", "PLACING", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURAL", "PROCEDURE", "PROJECTION", "QUOTE", "READ", "REAL", "RECHECK", "RECORD", "RECOVER", "REFERENCES", "REFRESH", "REINDEX", "REJECTED", "RELATIVE", "RELEASE", "RENAME", "REPEATABLE", "REPLACE", "RESET", "RESTART", "RESTRICT", "RETURNS", "REVOKE", "RIGHT", "RLE", "ROLLBACK", "ROW", "ROWS", "RULE", "SAVEPOINT", "SCHEMA", "SCROLL", "SECOND", "SECURITY", "SEGMENTED", "SELECT", "SEQUENCE", "SERIALIZABLE", "SESSION", "SESSION_USER", "SET", "SETOF", "SHARE", "SHOW", "SIMILAR", "SIMPLE", "SMALLINT", "SOME", "SPLIT", "STABLE", "START", "STATEMENT", "STATISTICS", "STDIN", "STDOUT", "STORAGE", "STRICT", "SUBSTRING", "SYSID", "TABLE", "TABLESPACE", "TEMP", "TEMPLATE", "TEMPORARY", "TERMINATOR", "THAN", "THEN", "TIME", "TIMESTAMP", "TIMESTAMPTZ", "TIMETZ", "TO", "TOAST", "TRAILING", "TRANSACTION", "TREAT", "TRIGGER", "TRIM", "TRUE", "TRUE_P", "TRUNCATE", "TRUSTED", "TYPE", "UNCOMMITTED", "UNENCRYPTED", "UNION", "UNIQUE", "UNKNOWN", "UNLISTEN", "UNSEGMENTED", "UNTIL", "UPDATE", "USAGE", "USER", "USING", "VACUUM", "VALID", "VALIDATOR", "VALINDEX", "VALUES", "VARCHAR", "VARYING", "VERBOSE", "VIEW", "VOLATILE", "WHEN", "WHERE", "WITH", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE" }, nativeMeta.getReservedWords());
        Assert.assertArrayEquals(new String[]{  }, nativeMeta.getViewTypes());
        Assert.assertFalse(nativeMeta.supportsAutoInc());
        Assert.assertTrue(nativeMeta.supportsBooleanDataType());
        Assert.assertTrue(nativeMeta.requiresCastToVariousForIsNull());
        Assert.assertEquals("?", nativeMeta.getExtraOptionIndicator());
        Assert.assertEquals("&", nativeMeta.getExtraOptionSeparator());
        Assert.assertTrue(nativeMeta.supportsSequences());
        Assert.assertFalse(nativeMeta.supportsTimeStampToDateConversion());
        Assert.assertFalse(nativeMeta.supportsGetBlob());
        Assert.assertTrue(nativeMeta.isDisplaySizeTwiceThePrecision());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals(" LIMIT 5", nativeMeta.getLimitClause(5));
        Assert.assertEquals("--NOTE: Table cannot be altered unless all projections are dropped.\nALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("--NOTE: Table cannot be altered unless all projections are dropped.\nALTER TABLE FOO ALTER COLUMN BAR SET DATA TYPE VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("SELECT FOO FROM BAR LIMIT 1", nativeMeta.getSQLColumnExists("FOO", "BAR"));
        Assert.assertEquals("SELECT * FROM FOO LIMIT 1", nativeMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals("SELECT 1 FROM FOO LIMIT 1", nativeMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("SELECT sequence_name FROM sequences WHERE sequence_name = 'FOO'", nativeMeta.getSQLSequenceExists("FOO"));
        Assert.assertEquals("SELECT sequence_name FROM sequences", nativeMeta.getSQLListOfSequences());
        Assert.assertEquals("SELECT nextval('FOO')", nativeMeta.getSQLNextSequenceValue("FOO"));
        Assert.assertEquals("SELECT currval('FOO')", nativeMeta.getSQLCurrentSequenceValue("FOO"));
    }

    @Test
    public void testGetFieldDefinition() throws Exception {
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("BOOLEAN", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        Assert.assertEquals("FLOAT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO"), "", "", false, false, false));
        Assert.assertEquals("FLOAT", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO"), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO"), "", "", false, false, false));
        Assert.assertEquals("VARCHAR", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("VARBINARY", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO", 0, 0), "", "", false, false, false));
        Assert.assertEquals("VARBINARY(50)", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO", 50, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

