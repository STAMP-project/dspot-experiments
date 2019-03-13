/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.dbcp;


import DBCPConnectionPoolLookup.DATABASE_NAME_ATTRIBUTE;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestDBCPConnectionPoolLookup {
    private TestDBCPConnectionPoolLookup.MockConnection connectionA;

    private TestDBCPConnectionPoolLookup.MockConnection connectionB;

    private TestDBCPConnectionPoolLookup.MockDBCPService dbcpServiceA;

    private TestDBCPConnectionPoolLookup.MockDBCPService dbcpServiceB;

    private DBCPService dbcpLookupService;

    private TestRunner runner;

    @Test
    public void testLookupServiceA() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(DATABASE_NAME_ATTRIBUTE, "a");
        final Connection connection = dbcpLookupService.getConnection(attributes);
        Assert.assertNotNull(connection);
        Assert.assertTrue((connection instanceof TestDBCPConnectionPoolLookup.MockConnection));
        final TestDBCPConnectionPoolLookup.MockConnection mockConnection = ((TestDBCPConnectionPoolLookup.MockConnection) (connection));
        Assert.assertEquals(connectionA.getName(), mockConnection.getName());
    }

    @Test
    public void testLookupServiceB() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(DATABASE_NAME_ATTRIBUTE, "b");
        final Connection connection = dbcpLookupService.getConnection(attributes);
        Assert.assertNotNull(connection);
        Assert.assertTrue((connection instanceof TestDBCPConnectionPoolLookup.MockConnection));
        final TestDBCPConnectionPoolLookup.MockConnection mockConnection = ((TestDBCPConnectionPoolLookup.MockConnection) (connection));
        Assert.assertEquals(connectionB.getName(), mockConnection.getName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLookupWithoutAttributes() {
        dbcpLookupService.getConnection();
    }

    @Test(expected = ProcessException.class)
    public void testLookupMissingDatabaseNameAttribute() {
        final Map<String, String> attributes = new HashMap<>();
        dbcpLookupService.getConnection(attributes);
    }

    @Test(expected = ProcessException.class)
    public void testLookupWithDatabaseNameThatDoesNotExist() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(DATABASE_NAME_ATTRIBUTE, "DOES-NOT-EXIST");
        dbcpLookupService.getConnection(attributes);
    }

    @Test
    public void testCustomValidateAtLeaseOneServiceDefined() throws InitializationException {
        // enable lookup service with no services registered, verify not valid
        runner = TestRunners.newTestRunner(TestProcessor.class);
        runner.addControllerService("dbcp-lookup", dbcpLookupService);
        runner.assertNotValid(dbcpLookupService);
        final String dbcpServiceAIdentifier = "dbcp-a";
        runner.addControllerService(dbcpServiceAIdentifier, dbcpServiceA);
        // register a service and now verify valid
        runner.setProperty(dbcpLookupService, "a", dbcpServiceAIdentifier);
        runner.enableControllerService(dbcpLookupService);
        runner.assertValid(dbcpLookupService);
    }

    @Test
    public void testCustomValidateSelfReferenceNotAllowed() throws InitializationException {
        runner = TestRunners.newTestRunner(TestProcessor.class);
        runner.addControllerService("dbcp-lookup", dbcpLookupService);
        runner.setProperty(dbcpLookupService, "dbcp-lookup", "dbcp-lookup");
        runner.assertNotValid(dbcpLookupService);
    }

    /**
     * A mock DBCPService that will always return the passed in MockConnection.
     */
    private static class MockDBCPService extends AbstractControllerService implements DBCPService {
        private TestDBCPConnectionPoolLookup.MockConnection connection;

        public MockDBCPService(TestDBCPConnectionPoolLookup.MockConnection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() throws ProcessException {
            return connection;
        }

        @Override
        public Connection getConnection(Map<String, String> attributes) throws ProcessException {
            return connection;
        }
    }

    /**
     * A mock Connection that will allow us to mock getName so we can identify we have the expected Connection.
     */
    private interface MockConnection extends Connection {
        String getName();
    }
}

