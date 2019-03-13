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


public class SybaseDatabaseMetaTest {
    private SybaseDatabaseMeta nativeMeta;

    private SybaseDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(5001, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("net.sourceforge.jtds.jdbc.Driver", nativeMeta.getDriverClass());
        Assert.assertEquals("jdbc:jtds:sybase://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:jtds:sybase://FOO:/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));// Pretty sure this is a bug - uses port empty or not

        Assert.assertEquals("BAR", nativeMeta.getSchemaTableCombination("FOO", "BAR"));
        Assert.assertFalse(nativeMeta.isRequiringTransactionsOnQueries());
        Assert.assertEquals("http://jtds.sourceforge.net/faq.html#urlFormat", nativeMeta.getExtraOptionsHelpText());
        Assert.assertArrayEquals(new String[]{ "jtds-1.2.jar" }, nativeMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15) NULL", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15) NULL", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOVERSION) values (1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME NULL", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME NULL", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, false, false));
        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType(true);
        Assert.assertEquals("BOOLEAN", odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType(false);
        Assert.assertEquals("INTEGER NOT NULL PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO"), "FOO", "", false, false, false));
        Assert.assertEquals("INTEGER NOT NULL PRIMARY KEY", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO"), "", "FOO", false, false, false));
        Assert.assertEquals("DOUBLE PRECISION NULL", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO"), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(11, 3) NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 11, 3), "", "", false, false, false));
        Assert.assertEquals("TINYINT NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 2, 0), "", "", false, false, false));
        Assert.assertEquals("SMALLINT NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 3, 0), "", "", false, false, false));
        Assert.assertEquals("SMALLINT NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 4, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 5, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(15) NULL", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("TEXT NULL", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 2050, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

