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


import DatabaseMeta.TYPE_ACCESS_NATIVE;
import DatabaseMeta.TYPE_ACCESS_ODBC;
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


public class VectorWiseDatabaseMetaTest {
    @Test
    public void testIngresOverrides() throws Exception {
        VectorWiseDatabaseMeta nativeMeta;
        VectorWiseDatabaseMeta odbcMeta;
        nativeMeta = new VectorWiseDatabaseMeta();
        nativeMeta.setAccessType(TYPE_ACCESS_NATIVE);
        odbcMeta = new VectorWiseDatabaseMeta();
        odbcMeta.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:ingres://FOO:VW7/WIBBLE", nativeMeta.getURL("FOO", "VW7", "WIBBLE"));
        Assert.assertEquals("jdbc:ingres://FOO:VW7/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));// Empty bit

        Assert.assertEquals("jdbc:ingres://FOO:VW7/WIBBLE", nativeMeta.getURL("FOO", "-1", "WIBBLE"));// Empty bit

        Assert.assertEquals("jdbc:ingres://FOO:2345/WIBBLE", nativeMeta.getURL("FOO", "2345", "WIBBLE"));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ALTER COLUMN BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
        Assert.assertEquals(("ALTER TABLE FOO DROP COLUMN BAR" + (System.getProperty("line.separator"))), nativeMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("CALL VECTORWISE( COMBINE 'FOO - FOO' )", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertArrayEquals(new String[]{ "iijdbc.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertFalse(nativeMeta.supportsGetBlob());
    }

    @Test
    public void testGetFieldDefinition() {
        VectorWiseDatabaseMeta nativeMeta;
        VectorWiseDatabaseMeta odbcMeta;
        nativeMeta = new VectorWiseDatabaseMeta();
        nativeMeta.setAccessType(TYPE_ACCESS_NATIVE);
        odbcMeta = new VectorWiseDatabaseMeta();
        odbcMeta.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertEquals("CHAR(1)", nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        Assert.assertEquals("GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", true, false, false));
        Assert.assertEquals("GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 0), "", "FOO", true, false, false));
        Assert.assertEquals("BIGINT PRIMARY KEY NOT NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "FOO", "", false, false, false));
        Assert.assertEquals("BIGINT PRIMARY KEY NOT NULL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", false, false, false));
        // Integer tests
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 10, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 6, 0), "", "", false, false, false));
        Assert.assertEquals("SMALLINT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 3, 0), "", "", false, false, false));
        Assert.assertEquals("INTEGER1", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 2, 0), "", "", false, false, false));
        Assert.assertEquals("FLOAT8", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 6, 3), "", "", false, false, false));
        // String Types
        Assert.assertEquals("VARCHAR(15)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 15, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(32000)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32000, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(32000)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 32050, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(9999)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO"), "", "", true, false, false));
        Assert.assertEquals("VARCHAR(9999)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR(9999)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", (-34), 0), "", "", false, false, false));
        // Unknown
        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

