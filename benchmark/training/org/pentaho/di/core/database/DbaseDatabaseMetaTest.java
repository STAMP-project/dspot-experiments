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

import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class DbaseDatabaseMetaTest {
    private DbaseDatabaseMeta nativeMeta;

    private DbaseDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_ODBC }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertEquals("\"BAR\"", nativeMeta.getSchemaTableCombination("FOO", "BAR"));
        Assert.assertFalse(nativeMeta.supportsTransactions());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsViews());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertFalse(nativeMeta.supportsSetMaxRows());
        Assert.assertEquals(new String[]{  }, nativeMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOVERSION) values (1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(10)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("DECIMAL(8)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 8, 0), "", "FOO", false, false, false));
        Assert.assertEquals("DECIMAL(19)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 19, 0), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(22)", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 22, 0), "", "", false, false, false));
        Assert.assertEquals("DECIMAL", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO"), "", "", false, false, false));// Pretty sure this is a bug ...

        Assert.assertEquals("VARCHAR()", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));// Pretty sure this is a bug ...

        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO", 0, 0), "", "", false, false, false));
        // assertEquals( "VARBINARY(50)",
        // nativeMeta.getFieldDefinition( new ValueMetaBinary( "FOO", 50, 0 ), "", "", false, false, false ) );
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

