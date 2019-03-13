/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class OracleRDBDatabaseMetaTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private OracleRDBDatabaseMeta nativeMeta;

    private OracleRDBDatabaseMeta odbcMeta;

    private OracleRDBDatabaseMeta jndiMeta;

    @Test
    public void testOverriddenSettings() throws Exception {
        // Tests the settings of the Oracle Database Meta
        // according to the features of the DB as we know them
        Assert.assertEquals((-1), nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertFalse(nativeMeta.supportsAutoInc());
        Assert.assertEquals("oracle.rdb.jdbc.rdbThin.Driver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL(null, null, "FOO"));
        Assert.assertEquals("jdbc:rdbThin://FOO:1024/BAR", nativeMeta.getURL("FOO", "1024", "BAR"));
        Assert.assertEquals("jdbc:rdbThin://FOO:11/:BAR", nativeMeta.getURL("FOO", "11", ":BAR"));
        Assert.assertEquals("jdbc:rdbThin://BAR:65534//FOO", nativeMeta.getURL("BAR", "65534", "/FOO"));
        Assert.assertEquals("jdbc:rdbThin://:/FOO", nativeMeta.getURL("", "", "FOO"));// Pretty sure this is a bug...

        Assert.assertEquals("jdbc:rdbThin://null:-1/FOO", nativeMeta.getURL(null, "-1", "FOO"));// Pretty sure this is a bug...

        Assert.assertEquals("jdbc:rdbThin://null:null/FOO", nativeMeta.getURL(null, null, "FOO"));// Pretty sure this is a bug...

        Assert.assertEquals("jdbc:rdbThin://FOO:1234/BAR", nativeMeta.getURL("FOO", "1234", "BAR"));
        Assert.assertEquals("jdbc:rdbThin://:/", nativeMeta.getURL("", "", ""));// Pretty sure this is a bug...

        Assert.assertEquals("jdbc:rdbThin://null:null/BAR", jndiMeta.getURL(null, null, "BAR"));
        Assert.assertFalse(nativeMeta.supportsOptionsInURL());
        Assert.assertTrue(nativeMeta.supportsSequences());
        Assert.assertTrue(nativeMeta.useSchemaNameForTableList());
        Assert.assertTrue(nativeMeta.supportsSynonyms());
        String[] reservedWords = new String[]{ "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "ARRAYLEN", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOTFOUND", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWLABEL", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "SQLBUF", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH" };
        Assert.assertArrayEquals(reservedWords, nativeMeta.getReservedWords());
        Assert.assertArrayEquals(new String[]{ "rdbthin.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertFalse(nativeMeta.supportsRepository());
        Assert.assertEquals(9999999, nativeMeta.getMaxVARCHARLength());
        Assert.assertEquals("SELECT SEQUENCE_NAME FROM USER_SEQUENCES", nativeMeta.getSQLListOfSequences());
        Assert.assertEquals("SELECT * FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'FOO'", nativeMeta.getSQLSequenceExists("FOO"));
        Assert.assertEquals("SELECT * FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'FOO'", nativeMeta.getSQLSequenceExists("foo"));
        Assert.assertEquals("SELECT FOO.currval FROM DUAL", nativeMeta.getSQLCurrentSequenceValue("FOO"));
        Assert.assertEquals("SELECT FOO.nextval FROM dual", nativeMeta.getSQLNextSequenceValue("FOO"));
        String reusedFieldsQuery = "SELECT * FROM FOO WHERE 1=0";
        Assert.assertEquals(reusedFieldsQuery, nativeMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals(reusedFieldsQuery, nativeMeta.getSQLTableExists("FOO"));
        String reusedColumnsQuery = "SELECT FOO FROM BAR WHERE 1=0";
        Assert.assertEquals(reusedColumnsQuery, nativeMeta.getSQLQueryColumnFields("FOO", "BAR"));
        Assert.assertEquals(reusedColumnsQuery, nativeMeta.getSQLColumnExists("FOO", "BAR"));
    }
}

