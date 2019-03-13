/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.pgbulkloader;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class PGBulkLoaderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMockHelper<PGBulkLoaderMeta, PGBulkLoaderData> stepMockHelper;

    private PGBulkLoader pgBulkLoader;

    private static final String CONNECTION_NAME = "PSQLConnect";

    private static final String CONNECTION_DB_NAME = "test1181";

    private static final String CONNECTION_DB_HOST = "localhost";

    private static final String CONNECTION_DB_PORT = "5093";

    private static final String CONNECTION_DB_USERNAME = "postgres";

    private static final String CONNECTION_DB_PASSWORD = "password";

    private static final String DB_NAME_OVVERRIDE = "test1181_2";

    private static final String DB_NAME_EMPTY = "";

    private static final String PG_TEST_CONNECTION = ((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<connection> <name>") + (PGBulkLoaderTest.CONNECTION_NAME)) + "</name><server>") + (PGBulkLoaderTest.CONNECTION_DB_HOST)) + "</server><type>POSTGRESQL</type><access>Native</access><database>") + (PGBulkLoaderTest.CONNECTION_DB_NAME)) + "</database>") + "  <port>") + (PGBulkLoaderTest.CONNECTION_DB_PORT)) + "</port><username>") + (PGBulkLoaderTest.CONNECTION_DB_USERNAME)) + "</username><password>Encrypted 2be98afc86aa7f2e4bb18bd63c99dbdde</password></connection>";

    @Test
    public void testCreateCommandLine() throws Exception {
        PGBulkLoaderMeta meta = Mockito.mock(PGBulkLoaderMeta.class);
        Mockito.doReturn(new DatabaseMeta()).when(meta).getDatabaseMeta();
        Mockito.doReturn(new String[0]).when(meta).getFieldStream();
        PGBulkLoaderData data = Mockito.mock(PGBulkLoaderData.class);
        PGBulkLoader spy = Mockito.spy(pgBulkLoader);
        Mockito.doReturn(new Object[0]).when(spy).getRow();
        Mockito.doReturn("").when(spy).getCopyCommand();
        Mockito.doNothing().when(spy).connect();
        Mockito.doNothing().when(spy).checkClientEncoding();
        Mockito.doNothing().when(spy).processTruncate();
        spy.processRow(meta, data);
        Mockito.verify(spy).processTruncate();
    }

    @Test
    public void testDBNameOverridden_IfDbNameOverrideSetUp() throws Exception {
        // Db Name Override is set up
        PGBulkLoaderMeta pgBulkLoaderMock = PGBulkLoaderTest.getPgBulkLoaderMock(PGBulkLoaderTest.DB_NAME_OVVERRIDE);
        Database database = pgBulkLoader.getDatabase(pgBulkLoader, pgBulkLoaderMock);
        Assert.assertNotNull(database);
        // Verify DB name is overridden
        Assert.assertEquals(PGBulkLoaderTest.DB_NAME_OVVERRIDE, database.getDatabaseMeta().getDatabaseName());
        // Check additionally other connection information
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_NAME, database.getDatabaseMeta().getName());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_HOST, database.getDatabaseMeta().getHostname());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PORT, database.getDatabaseMeta().getDatabasePortNumberString());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_USERNAME, database.getDatabaseMeta().getUsername());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PASSWORD, database.getDatabaseMeta().getPassword());
    }

    @Test
    public void testDBNameNOTOverridden_IfDbNameOverrideEmpty() throws Exception {
        // Db Name Override is empty
        PGBulkLoaderMeta pgBulkLoaderMock = PGBulkLoaderTest.getPgBulkLoaderMock(PGBulkLoaderTest.DB_NAME_EMPTY);
        Database database = pgBulkLoader.getDatabase(pgBulkLoader, pgBulkLoaderMock);
        Assert.assertNotNull(database);
        // Verify DB name is NOT overridden
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_NAME, database.getDatabaseMeta().getDatabaseName());
        // Check additionally other connection information
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_NAME, database.getDatabaseMeta().getName());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_HOST, database.getDatabaseMeta().getHostname());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PORT, database.getDatabaseMeta().getDatabasePortNumberString());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_USERNAME, database.getDatabaseMeta().getUsername());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PASSWORD, database.getDatabaseMeta().getPassword());
    }

    @Test
    public void testDBNameNOTOverridden_IfDbNameOverrideNull() throws Exception {
        // Db Name Override is null
        PGBulkLoaderMeta pgBulkLoaderMock = PGBulkLoaderTest.getPgBulkLoaderMock(null);
        Database database = pgBulkLoader.getDatabase(pgBulkLoader, pgBulkLoaderMock);
        Assert.assertNotNull(database);
        // Verify DB name is NOT overridden
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_NAME, database.getDatabaseMeta().getDatabaseName());
        // Check additionally other connection information
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_NAME, database.getDatabaseMeta().getName());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_HOST, database.getDatabaseMeta().getHostname());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PORT, database.getDatabaseMeta().getDatabasePortNumberString());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_USERNAME, database.getDatabaseMeta().getUsername());
        Assert.assertEquals(PGBulkLoaderTest.CONNECTION_DB_PASSWORD, database.getDatabaseMeta().getPassword());
    }

    @Test
    public void testProcessRow_StreamIsNull() throws Exception {
        PGBulkLoader pgBulkLoaderStreamIsNull = Mockito.mock(PGBulkLoader.class);
        Mockito.doReturn(null).when(pgBulkLoaderStreamIsNull).getRow();
        PGBulkLoaderMeta meta = Mockito.mock(PGBulkLoaderMeta.class);
        PGBulkLoaderData data = Mockito.mock(PGBulkLoaderData.class);
        Assert.assertEquals(false, pgBulkLoaderStreamIsNull.processRow(meta, data));
    }
}

