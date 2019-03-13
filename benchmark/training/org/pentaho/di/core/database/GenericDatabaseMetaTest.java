/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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


import GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS;
import GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


@RunWith(PowerMockRunner.class)
public class GenericDatabaseMetaTest {
    GenericDatabaseMeta nativeMeta;

    GenericDatabaseMeta odbcMeta;

    @Mock
    GenericDatabaseMeta mockedMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Properties attrs = new Properties();
        attrs.put(ATRRIBUTE_CUSTOM_DRIVER_CLASS, "foo.bar.wibble");
        attrs.put(ATRRIBUTE_CUSTOM_URL, "jdbc:foo:bar://foodb");
        nativeMeta.setAttributes(attrs);
        Assert.assertEquals("foo.bar.wibble", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:foo:bar://foodb", nativeMeta.getURL("NOT", "GOINGTO", "BEUSED"));
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("NOT", "USED", "FOO"));
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsPreparedStatementMetadataRetrieval());
        Assert.assertArrayEquals(new String[]{  }, nativeMeta.getUsedLibraries());
        Assert.assertFalse(nativeMeta.supportsPreparedStatementMetadataRetrieval());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("SELECT * FROM FOO", nativeMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals("SELECT 1 FROM FOO", nativeMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TIMESTAMP", nativeMeta.getAddColumnStatement("FOO", new ValueMetaDate("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TIMESTAMP", nativeMeta.getAddColumnStatement("FOO", new ValueMetaTimestamp("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR CHAR(1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE PRECISION", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 0, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR INTEGER", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(10, 3)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(10, 3)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 3), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(21, 4)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 21, 4), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TEXT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", ((nativeMeta.getMaxVARCHARLength()) + 2), 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, (-7)), "", false, "", false));// Bug here - invalid SQL

        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(22, 7)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 22, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR DOUBLE PRECISION", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", (-10), 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(5, 7)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR  UNKNOWN", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInternetAddress("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGSERIAL", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "BAR", true, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGSERIAL", nativeMeta.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 26, 8), "BAR", true, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals(("ALTER TABLE FOO DROP BAR" + lineSep), nativeMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR()", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR"), "", false, "", true));// I think this is a bug ..

        Assert.assertEquals("ALTER TABLE FOO ADD BAR SMALLINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR", 4, 0), "", true, "", false));
        // do a boolean check
        odbcMeta.setSupportsBooleanDataType(true);
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BOOLEAN", odbcMeta.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        odbcMeta.setSupportsBooleanDataType(false);
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGSERIAL", nativeMeta.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "BAR", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR BIGINT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 10, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR NUMERIC(22, 0)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBigNumber("BAR", 22, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(1)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 1, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR TEXT", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 16777250, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD BAR  UNKNOWN", nativeMeta.getAddColumnStatement("FOO", new ValueMetaBinary("BAR", 16777250, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOVERSION) values (1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    @PrepareForTest(DatabaseMeta.class)
    public void testSettingDialect() {
        String dialect = "testDialect";
        DatabaseInterface[] dbInterfaces = new DatabaseInterface[]{ mockedMeta };
        PowerMockito.mockStatic(DatabaseMeta.class);
        PowerMockito.when(DatabaseMeta.getDatabaseInterfaces()).thenReturn(dbInterfaces);
        Mockito.when(mockedMeta.getPluginName()).thenReturn(dialect);
        nativeMeta.addAttribute("DATABASE_DIALECT_ID", dialect);
        Assert.assertEquals(mockedMeta, Whitebox.getInternalState(nativeMeta, "databaseDialect"));
    }
}

