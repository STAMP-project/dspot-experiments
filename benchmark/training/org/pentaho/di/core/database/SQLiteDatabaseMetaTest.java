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
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.CLOB_LENGTH;
import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class SQLiteDatabaseMetaTest {
    private SQLiteDatabaseMeta nativeMeta;

    private SQLiteDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals((-1), nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("org.sqlite.JDBC", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:sqlite:WIBBLE", nativeMeta.getURL("IGNORED", "IGNORED", "WIBBLE"));
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertFalse(nativeMeta.supportsErrorHandling());
        Assert.assertArrayEquals(new String[]{ "sqlite-jdbc-3.7.2.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertEquals("\"FOO\".\"BAR\"", nativeMeta.getSchemaTableCombination("FOO", "BAR"));
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TEXT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR TEXT", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        // PK/TK
        Assert.assertEquals("INTEGER PRIMARY KEY AUTOINCREMENT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("INTEGER PRIMARY KEY AUTOINCREMENT", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", false, false, false));
        // Numeric Types
        Assert.assertEquals("NUMERIC", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, (-6)), "", "", false, false, false));
        Assert.assertEquals("NUMERIC", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", (-13), 0), "", "", false, false, false));
        Assert.assertEquals("NUMERIC", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 11, 0), "", "", false, false, false));
        // Strings
        Assert.assertEquals("TEXT", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 50, 0), "", "", false, false, false));
        Assert.assertEquals("BLOB", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", ((CLOB_LENGTH) + 1), 0), "", "", false, false, false));
        // Others
        Assert.assertEquals("BLOB", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals(("UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

