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


public class ExtenDBDatabaseMetaTest {
    private ExtenDBDatabaseMeta nativeMeta;

    private ExtenDBDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(6453, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("com.extendb.connect.XDBDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:xdb://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:xdb://FOO:/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));// Pretty sure this is a bug (colon after foo)

        Assert.assertArrayEquals(new String[]{ "AFTER", "BINARY", "BOOLEAN", "DATABASES", "DBA", "ESTIMATE", "MODIFY", "NODE", "NODES", "OWNER", "PARENT", "PARTITION", "PARTITIONING", "PASSWORD", "PERCENT", "PUBLIC", "RENAME", "REPLICATED", "RESOURCE", "SAMPLE", "SERIAL", "SHOW", "STANDARD", "STAT", "STATISTICS", "TABLES", "TEMP", "TRAN", "UNSIGNED", "ZEROFILL" }, nativeMeta.getReservedWords());
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertArrayEquals(new String[]{ "xdbjdbc.jar" }, nativeMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals((((("ALTER TABLE FOO DROP BAR" + lineSep) + ";") + lineSep) + "ALTER TABLE FOO ADD BAR VARCHAR(15)"), nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    public void testGetFieldDefinition() throws Exception {
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        // Simple hack to prevent duplication of code. Checking the case of supported boolean type
        // both supported and unsupported. Should return BOOLEAN if supported, or CHAR(1) if not.
        String[] typeCk = new String[]{ "CHAR(1)", "BOOLEAN", "CHAR(1)" };
        int i = (nativeMeta.supportsBooleanDataType()) ? 1 : 0;
        Assert.assertEquals(typeCk[i], nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals(typeCk[(i + 1)], odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals("BIGSERIAL", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("SERIAL", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 8, 0), "", "FOO", false, false, false));
        Assert.assertEquals("NUMERIC(19, 0)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 19, 0), "", "", false, false, false));
        Assert.assertEquals("NUMERIC(22, 7)", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 22, 7), "", "", false, false, false));
        Assert.assertEquals("DOUBLE PRECISION", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO"), "", "", false, false, false));
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 0), "", "", false, false, false));
        Assert.assertEquals("SMALLINT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 3, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 5, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR()", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));// Pretty sure this is a bug ...

        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaBinary("FOO", 0, 0), "", "", false, false, false));
        // assertEquals( "VARBINARY(50)",
        // nativeMeta.getFieldDefinition( new ValueMetaBinary( "FOO", 50, 0 ), "", "", false, false, false ) );
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

