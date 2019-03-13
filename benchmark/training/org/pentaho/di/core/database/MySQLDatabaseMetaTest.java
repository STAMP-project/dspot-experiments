/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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


public class MySQLDatabaseMetaTest {
    MySQLDatabaseMeta nativeMeta;

    MySQLDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(3306, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("org.gjt.mm.mysql.Driver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:mysql://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:mysql://FOO/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));
        Assert.assertEquals("&", nativeMeta.getExtraOptionSeparator());
        Assert.assertEquals("?", nativeMeta.getExtraOptionIndicator());
        Assert.assertFalse(nativeMeta.supportsTransactions());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertTrue(nativeMeta.supportsViews());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertArrayEquals(new String[]{ "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCATE", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "POSITION", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "READ", "READS", "REAL", "REFERENCES", "REGEXP", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL" }, nativeMeta.getReservedWords());
        Assert.assertEquals("`", nativeMeta.getStartQuote());
        Assert.assertEquals("`", nativeMeta.getEndQuote());
        Assert.assertTrue(nativeMeta.needsToLockAllTables());
        Assert.assertEquals("http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-configuration-properties.html", nativeMeta.getExtraOptionsHelpText());
        Assert.assertArrayEquals(new String[]{ "mysql-connector-java-3.1.14-bin.jar" }, nativeMeta.getUsedLibraries());// this is way wrong

        Assert.assertTrue(nativeMeta.isSystemTable("sysTest"));
        Assert.assertTrue(nativeMeta.isSystemTable("dtproperties"));
        Assert.assertFalse(nativeMeta.isSystemTable("SysTest"));
        Assert.assertFalse(nativeMeta.isSystemTable("dTproperties"));
        Assert.assertFalse(nativeMeta.isSystemTable("Testsys"));
        Assert.assertTrue(nativeMeta.isMySQLVariant());
        Assert.assertFalse(nativeMeta.releaseSavepoint());
        Assert.assertTrue(nativeMeta.supportsErrorHandlingOnBatchUpdates());
        Assert.assertFalse(nativeMeta.isRequiringTransactionsOnQueries());
        Assert.assertTrue(nativeMeta.supportsRepository());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals(" LIMIT 15", nativeMeta.getLimitClause(15));
        Assert.assertEquals("SELECT * FROM FOO LIMIT 0", nativeMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals("SELECT * FROM FOO LIMIT 0", nativeMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("SELECT FOO FROM BAR LIMIT 0", nativeMeta.getSQLQueryColumnFields("FOO", "BAR"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DATETIME", nativeMeta.getAddColumnStatement("FOO", new ValueMetaDate("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DATETIME", nativeMeta.getAddColumnStatement("FOO", new ValueMetaTimestamp("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR CHAR(1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR INT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 0, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR INT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DECIMAL(21, 4)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 21, 4), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR MEDIUMTEXT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", ((nativeMeta.getMaxVARCHARLength()) + 2), 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, (-7)), "", false, "", false));// Bug here - invalid SQL

        Assert.assertEquals("ALTER TABLE FOO ADD BAR DECIMAL(22, 7)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 22, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", (-10), 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR  UNKNOWN", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInternetAddress("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "BAR", true, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 26, 8), "BAR", true, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals(("ALTER TABLE FOO DROP BAR" + lineSep), nativeMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR TINYTEXT", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR"), "", false, "", true));
        odbcMeta.setSupportsBooleanDataType(true);// some subclass of the MSSQL meta probably ...

        Assert.assertEquals("ALTER TABLE FOO ADD BAR BOOLEAN", odbcMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        odbcMeta.setSupportsBooleanDataType(false);
        Assert.assertEquals("ALTER TABLE FOO ADD BAR INT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR", 4, 0), "", true, "", false));
        // do a boolean check
        odbcMeta.setSupportsBooleanDataType(true);
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BOOLEAN", odbcMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        odbcMeta.setSupportsBooleanDataType(false);
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT NOT NULL PRIMARY KEY", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "BAR", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DECIMAL(22)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 22, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR CHAR(1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 1, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR LONGTEXT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 16777250, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR LONGBLOB", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBinary("BAR", 16777250, 0), "", false, "", false));
        Assert.assertEquals(("LOCK TABLES FOO WRITE, BAR WRITE;" + lineSep), nativeMeta.getSQLLockTables(new String[]{ "FOO", "BAR" }));
        Assert.assertEquals("UNLOCK TABLES", nativeMeta.getSQLUnlockTables(new String[]{  }));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (1, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }
}

