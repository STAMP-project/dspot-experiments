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


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LogChannelInterface;


/**
 * User: Dzmitry Stsiapanau Date: 12/11/13 Time: 1:59 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ConnectionPoolUtilTest implements Driver {
    private static final String PASSWORD = "manager";

    private static final String ENCR_PASSWORD = "Encrypted 2be98afc86aa7f2e4cb14af7edf95aac8";

    @Mock(answer = Answers.RETURNS_MOCKS)
    LogChannelInterface logChannelInterface;

    @Mock(answer = Answers.RETURNS_MOCKS)
    DatabaseMeta dbMeta;

    @Mock
    BasicDataSource dataSource;

    final int INITIAL_SIZE = 1;

    final int MAX_SIZE = 10;

    public ConnectionPoolUtilTest() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetConnection() throws Exception {
        Mockito.when(dbMeta.getName()).thenReturn("CP1");
        Mockito.when(dbMeta.getPassword()).thenReturn(ConnectionPoolUtilTest.PASSWORD);
        Connection conn = ConnectionPoolUtil.getConnection(logChannelInterface, dbMeta, "", 1, 2);
        Assert.assertTrue((conn != null));
    }

    @Test
    public void testGetConnectionEncrypted() throws Exception {
        Mockito.when(dbMeta.getName()).thenReturn("CP2");
        Mockito.when(dbMeta.getPassword()).thenReturn(ConnectionPoolUtilTest.ENCR_PASSWORD);
        Connection conn = ConnectionPoolUtil.getConnection(logChannelInterface, dbMeta, "", 1, 2);
        Assert.assertTrue((conn != null));
    }

    @Test
    public void testGetConnectionWithVariables() throws Exception {
        Mockito.when(dbMeta.getName()).thenReturn("CP3");
        Mockito.when(dbMeta.getPassword()).thenReturn(ConnectionPoolUtilTest.ENCR_PASSWORD);
        Mockito.when(dbMeta.getInitialPoolSizeString()).thenReturn("INITIAL_POOL_SIZE");
        Mockito.when(dbMeta.environmentSubstitute("INITIAL_POOL_SIZE")).thenReturn("5");
        Mockito.when(dbMeta.getInitialPoolSize()).thenCallRealMethod();
        Mockito.when(dbMeta.getMaximumPoolSizeString()).thenReturn("MAXIMUM_POOL_SIZE");
        Mockito.when(dbMeta.environmentSubstitute("MAXIMUM_POOL_SIZE")).thenReturn("10");
        Mockito.when(dbMeta.getMaximumPoolSize()).thenCallRealMethod();
        Connection conn = ConnectionPoolUtil.getConnection(logChannelInterface, dbMeta, "");
        Assert.assertTrue((conn != null));
    }

    @Test
    public void testGetConnectionName() throws Exception {
        Mockito.when(dbMeta.getName()).thenReturn("CP2");
        Mockito.when(dbMeta.getPassword()).thenReturn(ConnectionPoolUtilTest.ENCR_PASSWORD);
        String connectionName = ConnectionPoolUtil.buildPoolName(dbMeta, "");
        Assert.assertTrue(connectionName.equals("CP2"));
        Assert.assertFalse(connectionName.equals("CP2pentaho"));
        Mockito.when(dbMeta.getDatabaseName()).thenReturn("pentaho");
        connectionName = ConnectionPoolUtil.buildPoolName(dbMeta, "");
        Assert.assertTrue(connectionName.equals("CP2pentaho"));
        Assert.assertFalse(connectionName.equals("CP2pentaholocal"));
        Mockito.when(dbMeta.getHostname()).thenReturn("local");
        connectionName = ConnectionPoolUtil.buildPoolName(dbMeta, "");
        Assert.assertTrue(connectionName.equals("CP2pentaholocal"));
        Assert.assertFalse(connectionName.equals("CP2pentaholocal3306"));
        Mockito.when(dbMeta.getDatabasePortNumberString()).thenReturn("3306");
        connectionName = ConnectionPoolUtil.buildPoolName(dbMeta, "");
        Assert.assertTrue(connectionName.equals("CP2pentaholocal3306"));
    }

    @Test
    public void testConfigureDataSource() throws KettleDatabaseException {
        Mockito.when(dbMeta.getURL("partId")).thenReturn("jdbc:foo://server:111");
        Mockito.when(dbMeta.getUsername()).thenReturn("suzy");
        Mockito.when(dbMeta.getPassword()).thenReturn("password");
        ConnectionPoolUtil.configureDataSource(dataSource, dbMeta, "partId", INITIAL_SIZE, MAX_SIZE);
        Mockito.verify(dataSource).setDriverClassName("org.pentaho.di.core.database.ConnectionPoolUtilTest");
        Mockito.verify(dataSource).setDriverClassLoader(ArgumentMatchers.any(ClassLoader.class));
        Mockito.verify(dataSource).setUrl("jdbc:foo://server:111");
        Mockito.verify(dataSource).addConnectionProperty("user", "suzy");
        Mockito.verify(dataSource).addConnectionProperty("password", "password");
        Mockito.verify(dataSource).setInitialSize(INITIAL_SIZE);
        Mockito.verify(dataSource).setMaxActive(MAX_SIZE);
    }

    @Test
    public void testConfigureDataSourceWhenNoDatabaseInterface() throws KettleDatabaseException {
        Mockito.when(dbMeta.getDatabaseInterface()).thenReturn(null);
        ConnectionPoolUtil.configureDataSource(dataSource, dbMeta, "partId", INITIAL_SIZE, MAX_SIZE);
        Mockito.verify(dataSource, Mockito.never()).setDriverClassLoader(ArgumentMatchers.any(ClassLoader.class));
    }
}

