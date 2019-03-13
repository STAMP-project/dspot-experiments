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
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class GuptaDatabaseMetaTest {
    private GuptaDatabaseMeta nativeMeta;

    private GuptaDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(2155, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertFalse(nativeMeta.supportsAutoInc());
        Assert.assertEquals("jdbc.gupta.sqlbase.SqlbaseDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:sqlbase://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:sqlbase://FOO:/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));// Pretty sure this is a bug (colon after foo)

        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertFalse(nativeMeta.supportsCatalogs());
        Assert.assertFalse(nativeMeta.supportsTimeStampToDateConversion());
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertArrayEquals(new String[]{ "SQLBaseJDBC.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertTrue(nativeMeta.isSystemTable("SYSFOO"));
        Assert.assertFalse(nativeMeta.isSystemTable("SySBAR"));
        Assert.assertFalse(nativeMeta.isSystemTable("BARSYS"));
        Assert.assertFalse(nativeMeta.supportsPreparedStatementMetadataRetrieval());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals((((("ALTER TABLE FOO DROP BAR" + lineSep) + ";") + lineSep) + "ALTER TABLE FOO ADD BAR VARCHAR(15)"), nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME NULL", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME NULL", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("INTEGER NOT NULL", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("INTEGER NOT NULL", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 8, 0), "", "FOO", false, false, false));
        // Note - ValueMetaInteger returns zero always from the precision - so this avoids the weirdness
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", (-8), (-3)), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("DOUBLE PRECISION", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", (-8), (-3)), "", "", false, false, false));// Weird if statement ( length and precision less than zero)

        Assert.assertEquals("DOUBLE PRECISION", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 3), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("DOUBLE PRECISION", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 0), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 9, 0), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("LONG VARCHAR", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 255, 0), "", "", false, false, false));
        Assert.assertEquals("LONG VARCHAR", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", (-33), 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(0)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

