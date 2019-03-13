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


import DatabaseMeta.TYPE_ACCESS_JNDI;
import DatabaseMeta.TYPE_ACCESS_ODBC;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for RedshiftDatabaseMeta
 */
public class RedshiftDatabaseMetaTest {
    private RedshiftDatabaseMeta dbMeta;

    @Test
    public void testExtraOption() {
        Map<String, String> opts = dbMeta.getExtraOptions();
        Assert.assertNotNull(opts);
        Assert.assertEquals("true", opts.get("REDSHIFT.tcpKeepAlive"));
    }

    @Test
    public void testGetDefaultDatabasePort() throws Exception {
        Assert.assertEquals(5439, dbMeta.getDefaultDatabasePort());
        dbMeta.setAccessType(TYPE_ACCESS_JNDI);
        Assert.assertEquals((-1), dbMeta.getDefaultDatabasePort());
    }

    @Test
    public void testGetDriverClass() throws Exception {
        Assert.assertEquals("com.amazon.redshift.jdbc4.Driver", dbMeta.getDriverClass());
        dbMeta.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", dbMeta.getDriverClass());
    }

    @Test
    public void testGetURL() throws Exception {
        Assert.assertEquals("jdbc:redshift://:/", dbMeta.getURL("", "", ""));
        Assert.assertEquals("jdbc:redshift://rs.pentaho.com:4444/myDB", dbMeta.getURL("rs.pentaho.com", "4444", "myDB"));
        dbMeta.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertEquals("jdbc:odbc:myDB", dbMeta.getURL(null, "Not Null", "myDB"));
    }

    @Test
    public void testGetExtraOptionsHelpText() throws Exception {
        Assert.assertEquals("http://docs.aws.amazon.com/redshift/latest/mgmt/configure-jdbc-connection.html", dbMeta.getExtraOptionsHelpText());
    }

    @Test
    public void testIsFetchSizeSupported() throws Exception {
        Assert.assertFalse(dbMeta.isFetchSizeSupported());
    }

    @Test
    public void testSupportsSetMaxRows() throws Exception {
        Assert.assertFalse(dbMeta.supportsSetMaxRows());
    }

    @Test
    public void testGetUsedLibraries() throws Exception {
        String[] libs = dbMeta.getUsedLibraries();
        Assert.assertNotNull(libs);
        Assert.assertEquals(1, libs.length);
        Assert.assertEquals("RedshiftJDBC4_1.0.10.1010.jar", libs[0]);
    }
}

