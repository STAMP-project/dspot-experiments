/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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


public class MariaDBDatabaseMetaTest extends MySQLDatabaseMetaTest {
    @Test
    public void testMysqlOverrides() throws Exception {
        MariaDBDatabaseMeta nativeMeta = new MariaDBDatabaseMeta();
        nativeMeta.setAccessType(TYPE_ACCESS_NATIVE);
        MariaDBDatabaseMeta odbcMeta = new MariaDBDatabaseMeta();
        odbcMeta.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertArrayEquals(new String[]{ "mariadb-java-client-1.4.6.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertEquals(3306, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertEquals("org.mariadb.jdbc.Driver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:mariadb://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:mariadb://FOO/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));
        // The fullExceptionLog method is covered by another test case.
    }
}

