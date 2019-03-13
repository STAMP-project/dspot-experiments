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

import static DatabaseMeta.CLOB_LENGTH;
import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class InformixDatabaseMetaTest {
    private InformixDatabaseMeta nativeMeta;

    private InformixDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(1526, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        nativeMeta.setServername("FOODBNAME");
        Assert.assertEquals("com.informix.jdbc.IfxDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:informix-sqli://FOO:BAR/WIBBLE:INFORMIXSERVER=FOODBNAME;DELIMIDENT=Y", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:informix-sqli://FOO:/WIBBLE:INFORMIXSERVER=FOODBNAME;DELIMIDENT=Y", nativeMeta.getURL("FOO", "", "WIBBLE"));// Pretty sure this is a bug (colon after foo)

        Assert.assertTrue(nativeMeta.needsPlaceHolder());
        Assert.assertTrue(nativeMeta.isFetchSizeSupported());
        Assert.assertTrue(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertFalse(nativeMeta.needsToLockAllTables());
        Assert.assertArrayEquals(new String[]{ "ifxjdbc.jar" }, nativeMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("SELECT FIRST 1 * FROM FOO", nativeMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals("SELECT FIRST 1 * FROM FOO", nativeMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("SELECT FIRST 1 FOO FROM BAR", nativeMeta.getSQLQueryColumnFields("FOO", "BAR"));
        Assert.assertEquals("SELECT FIRST 1 FOO FROM BAR", nativeMeta.getSQLColumnExists("FOO", "BAR"));
        Assert.assertEquals("TRUNCATE TABLE FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (1, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals(((("LOCK TABLE FOO IN EXCLUSIVE MODE;" + lineSep) + "LOCK TABLE BAR IN EXCLUSIVE MODE;") + lineSep), nativeMeta.getSQLLockTables(new String[]{ "FOO", "BAR" }));
        Assert.assertNull(nativeMeta.getSQLUnlockTables(new String[]{ "FOO", "BAR" }));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME YEAR to FRACTION", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        // Simple hack to prevent duplication of code. Checking the case of supported boolean type
        // both supported and unsupported. Should return BOOLEAN if supported, or CHAR(1) if not.
        String[] typeCk = new String[]{ "CHAR(1)", "BOOLEAN", "CHAR(1)" };
        int i = (nativeMeta.supportsBooleanDataType()) ? 1 : 0;
        Assert.assertEquals(typeCk[i], nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals(typeCk[(i + 1)], odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals("SERIAL8", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", true, false, false));
        Assert.assertEquals("INTEGER PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("INTEGER PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", false, false, false));
        // Note - ValueMetaInteger returns zero always from the precision - so this avoids the weirdness
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", (-8), (-3)), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("FLOAT", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", (-8), (-3)), "", "", false, false, false));// Weird if statement ( length and precision less than zero)

        Assert.assertEquals("FLOAT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 3), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("FLOAT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 0), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 9, 0), "", "", false, false, false));// Weird if statement

        Assert.assertEquals("CLOB", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", ((CLOB_LENGTH) + 1), 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(10)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 10, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(255)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 255, 0), "", "", false, false, false));
        Assert.assertEquals("LVARCHAR", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 256, 0), "", "", false, false, false));
        Assert.assertEquals("LVARCHAR", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32767, 0), "", "", false, false, false));
        Assert.assertEquals("TEXT", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32768, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

