/**
 * Copyright 2015-2017 Telefonica Investigaci?n y Desarrollo, S.A.U
 *
 * This file is part of fiware-cygnus (FIWARE project).
 *
 * fiware-cygnus is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * fiware-cygnus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with fiware-cygnus. If not, see
 * http://www.gnu.org/licenses/.
 *
 * For those usages not covered by the GNU Affero General Public License please contact with iot_support at tid dot es
 */
/**
 * PostgreSQLBackendImplTest
 */


package com.telefonica.iot.cygnus.backends.postgresql;


/**
 * @author frb
 */
@org.junit.runner.RunWith(value = org.mockito.runners.MockitoJUnitRunner.class)
public class AmplPostgreSQLBackendImplTest {
    // instance to be tested
    private com.telefonica.iot.cygnus.backends.postgresql.PostgreSQLBackendImpl backend;

    // mocks
    @org.mockito.Mock
    private com.telefonica.iot.cygnus.backends.postgresql.PostgreSQLBackendImpl.PostgreSQLDriver mockDriverSchemaCreate;

    @org.mockito.Mock
    private com.telefonica.iot.cygnus.backends.postgresql.PostgreSQLBackendImpl.PostgreSQLDriver mockDriverTableCreate;

    @org.mockito.Mock
    private java.sql.Connection mockConnection;

    @org.mockito.Mock
    private java.sql.Statement mockStatement;

    // constants
    private final java.lang.String host = "localhost";

    private final java.lang.String port = "5432";

    private final java.lang.String database = "my-database";

    private final java.lang.String user = "root";

    private final java.lang.String password = "12345abcde";

    private final java.lang.String schemaName1 = "db1";

    private final java.lang.String schemaName2 = "db2";

    private final java.lang.String tableName1 = "table1";

    private final java.lang.String tableName2 = "table2";

    private final java.lang.String fieldNames1 = "a text, b text";

    private final java.lang.String fieldNames2 = "c text, d text";

    /**
     * Sets up tests by creating a unique instance of the tested class, and by defining the behaviour of the mocked
     * classes.
     *
     * @throws Exception
     */
    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        // set up the instance of the tested class
        backend = new com.telefonica.iot.cygnus.backends.postgresql.PostgreSQLBackendImpl(host, port, database, user, password, true);
        // set up the behaviour of the mocked classes
        org.mockito.Mockito.when(mockDriverSchemaCreate.getConnection(org.mockito.Mockito.anyString())).thenReturn(mockConnection);
        org.mockito.Mockito.when(mockDriverSchemaCreate.isConnectionCreated(org.mockito.Mockito.anyString())).thenReturn(true);
        org.mockito.Mockito.when(mockDriverSchemaCreate.numConnectionsCreated()).thenReturn(1);
        org.mockito.Mockito.when(mockDriverTableCreate.getConnection(org.mockito.Mockito.anyString())).thenReturn(mockConnection);
        org.mockito.Mockito.when(mockDriverTableCreate.isConnectionCreated(org.mockito.Mockito.anyString())).thenReturn(true, true, true, true, true);
        org.mockito.Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        org.mockito.Mockito.when(mockStatement.executeUpdate(org.mockito.Mockito.anyString())).thenReturn(1);
    }

    // setUp
    /**
     * Test of createSchema method, of class PostgreSQLBackendImpl.
     */
    @org.junit.Test
    public void testCreateSchema() {
        java.lang.System.out.println("Testing PostgreSQLBackend.createSchema (first schema creation");
        try {
            backend.setDriver(mockDriverSchemaCreate);
            backend.createSchema(schemaName1);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(""));
        }// try catch finally
        
        java.lang.System.out.println("Testing PostgreSQLBackend.createSchema (second schema creation");
        try {
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(""));
            backend.createSchema(schemaName2);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            // despite the number of schemas we create, the default connections associated to the empty database name
            // must be the unique element within the map
            org.junit.Assert.assertTrue(((backend.getDriver().numConnectionsCreated()) == 1));
        }// try catch finally
        
    }

    // testCreateSchema
    /**
     * Test of createTable method, of class PostgreSQLBackendImpl.
     */
    @org.junit.Test
    public void testCreateTable() {
        java.lang.System.out.println("Testing PostgreSQLBackend.createTable (within first schema");
        try {
            backend.setDriver(mockDriverTableCreate);
            backend.createSchema(schemaName1);
            backend.createTable(schemaName1, tableName1, fieldNames1);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(""));
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(schemaName1));
        }// try catch finally
        
        java.lang.System.out.println("Testing PostgreSQLBackend.createTable (within second schema");
        try {
            backend.createSchema(schemaName2);
            backend.createTable(schemaName2, tableName2, fieldNames2);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(""));
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(schemaName1));
            org.junit.Assert.assertTrue(backend.getDriver().isConnectionCreated(schemaName2));
        }// try catch finally
        
    }

    // testCreateTable
    /**
     * Test of insertContextData method, of class PostgreSQLBackendImpl.
     */
    // testInsertContextData
    @org.junit.Test
    public void testInsertContextData() {
        java.lang.System.out.println("Testing PostgreSQLBackend.insertContextData");
    }
}

