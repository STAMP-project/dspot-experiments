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


public class CacheDatabaseMetaTest {
    private CacheDatabaseMeta cdm;

    private CacheDatabaseMeta cdmODBC;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, cdm.getAccessTypeList());
        Assert.assertEquals(1972, cdm.getDefaultDatabasePort());
        Assert.assertEquals((-1), cdmODBC.getDefaultDatabasePort());
        Assert.assertFalse(cdm.supportsSetCharacterStream());
        Assert.assertFalse(cdm.isFetchSizeSupported());
        Assert.assertFalse(cdm.supportsAutoInc());
        Assert.assertEquals("com.intersys.jdbc.CacheDriver", cdm.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", cdmODBC.getDriverClass());
        Assert.assertEquals("jdbc:Cache://FOO:BAR/WIBBLE", cdm.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:odbc:FOO", cdmODBC.getURL(null, null, "FOO"));
        Assert.assertEquals("jdbc:odbc:FOO", cdmODBC.getURL("xxxxxx", "zzzzzzz", "FOO"));
        Assert.assertArrayEquals(new String[]{ "CacheDB.jar" }, cdm.getUsedLibraries());
        Assert.assertTrue(cdm.requiresCreateTablePrimaryKeyAppend());
        Assert.assertFalse(cdm.supportsNewLinesInSQL());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR VARCHAR(15) ) ", cdm.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR TIMESTAMP ) ", cdm.getAddColumnStatement("FOO", new ValueMetaDate("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR TIMESTAMP ) ", cdm.getAddColumnStatement("FOO", new ValueMetaTimestamp("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR CHAR(1) ) ", cdm.getAddColumnStatement("FOO", new ValueMetaBoolean("BAR"), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR INT ) ", cdm.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 0, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR DOUBLE ) ", cdm.getAddColumnStatement("FOO", new ValueMetaInteger("BAR"), "", false, "", false));// I believe this is a bug!

        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR DOUBLE ) ", cdm.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 10, (-7)), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR DOUBLE ) ", cdm.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", (-10), 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR DECIMAL(5, 7) ) ", cdm.getAddColumnStatement("FOO", new ValueMetaNumber("BAR", 5, 7), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN ( BAR  UNKNOWN ) ", cdm.getAddColumnStatement("FOO", new ValueMetaInternetAddress("BAR"), "", false, "", false));
        String lineSep = System.getProperty("line.separator");
        Assert.assertEquals(("ALTER TABLE FOO DROP COLUMN BAR" + lineSep), cdm.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
        Assert.assertEquals("ALTER TABLE FOO ALTER COLUMN BAR VARCHAR(15)", cdm.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", true));
    }
}

