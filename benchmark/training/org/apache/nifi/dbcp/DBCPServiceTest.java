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


import DBCPConnectionPool.DATABASE_URL;
import DBCPConnectionPool.DB_DRIVERNAME;
import DBCPConnectionPool.DB_PASSWORD;
import DBCPConnectionPool.DB_USER;
import DBCPConnectionPool.EVICTION_RUN_PERIOD;
import DBCPConnectionPool.MAX_CONN_LIFETIME;
import DBCPConnectionPool.MAX_IDLE;
import DBCPConnectionPool.MAX_WAIT_TIME;
import DBCPConnectionPool.MIN_EVICTABLE_IDLE_TIME;
import DBCPConnectionPool.MIN_IDLE;
import DBCPConnectionPool.SOFT_MIN_EVICTABLE_IDLE_TIME;
import DBCPConnectionPool.VALIDATION_QUERY;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DBCPServiceTest {
    static final String DB_LOCATION = "target/db";

    /**
     * Missing property values.
     */
    @Test
    public void testMissingPropertyValues() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        final Map<String, String> properties = new HashMap<String, String>();
        runner.addControllerService("test-bad1", service, properties);
        runner.assertNotValid(service);
    }

    /**
     * Max wait set to -1
     */
    @Test
    public void testMaxWait() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.setProperty(service, MAX_WAIT_TIME, "-1");
        runner.enableControllerService(service);
        runner.assertValid(service);
    }

    /**
     * Checks validity of idle limit and time settings including a default
     */
    @Test
    public void testIdleConnectionsSettings() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.setProperty(service, MAX_WAIT_TIME, "-1");
        runner.setProperty(service, MAX_IDLE, "2");
        runner.setProperty(service, MAX_CONN_LIFETIME, "1 secs");
        runner.setProperty(service, EVICTION_RUN_PERIOD, "1 secs");
        runner.setProperty(service, MIN_EVICTABLE_IDLE_TIME, "1 secs");
        runner.setProperty(service, SOFT_MIN_EVICTABLE_IDLE_TIME, "1 secs");
        runner.enableControllerService(service);
        runner.assertValid(service);
    }

    @Test
    public void testMinIdleCannotBeNegative() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.setProperty(service, MAX_WAIT_TIME, "-1");
        runner.setProperty(service, MIN_IDLE, "-1");
        runner.assertNotValid(service);
    }

    /**
     * Checks to ensure that settings have been passed down into the DBCP
     */
    @Test
    public void testIdleSettingsAreSet() throws SQLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.setProperty(service, MAX_WAIT_TIME, "-1");
        runner.setProperty(service, MAX_IDLE, "6");
        runner.setProperty(service, MIN_IDLE, "4");
        runner.setProperty(service, MAX_CONN_LIFETIME, "1 secs");
        runner.setProperty(service, EVICTION_RUN_PERIOD, "1 secs");
        runner.setProperty(service, MIN_EVICTABLE_IDLE_TIME, "1 secs");
        runner.setProperty(service, SOFT_MIN_EVICTABLE_IDLE_TIME, "1 secs");
        runner.enableControllerService(service);
        Assert.assertEquals(6, service.getDataSource().getMaxIdle());
        Assert.assertEquals(4, service.getDataSource().getMinIdle());
        Assert.assertEquals(1000, service.getDataSource().getMaxConnLifetimeMillis());
        Assert.assertEquals(1000, service.getDataSource().getTimeBetweenEvictionRunsMillis());
        Assert.assertEquals(1000, service.getDataSource().getMinEvictableIdleTimeMillis());
        Assert.assertEquals(1000, service.getDataSource().getSoftMinEvictableIdleTimeMillis());
        service.getDataSource().close();
    }

    /**
     * Creates a few connections and step closes them to see what happens
     */
    @Test
    public void testIdle() throws InterruptedException, SQLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.setProperty(service, MAX_WAIT_TIME, "-1");
        runner.setProperty(service, MAX_IDLE, "4");
        runner.setProperty(service, MIN_IDLE, "1");
        runner.setProperty(service, MAX_CONN_LIFETIME, "1000 millis");
        runner.setProperty(service, EVICTION_RUN_PERIOD, "100 millis");
        runner.setProperty(service, MIN_EVICTABLE_IDLE_TIME, "100 millis");
        runner.setProperty(service, SOFT_MIN_EVICTABLE_IDLE_TIME, "100 millis");
        runner.enableControllerService(service);
        ArrayList<Connection> connections = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            connections.add(service.getConnection());
        }
        Assert.assertEquals(6, service.getDataSource().getNumActive());
        connections.get(0).close();
        Assert.assertEquals(5, service.getDataSource().getNumActive());
        Assert.assertEquals(1, service.getDataSource().getNumIdle());
        connections.get(1).close();
        connections.get(2).close();
        connections.get(3).close();
        // now at max idle
        Assert.assertEquals(2, service.getDataSource().getNumActive());
        Assert.assertEquals(4, service.getDataSource().getNumIdle());
        // now a connection should get closed for real so that numIdle does not exceed maxIdle
        connections.get(4).close();
        Assert.assertEquals(4, service.getDataSource().getNumIdle());
        Assert.assertEquals(1, service.getDataSource().getNumActive());
        connections.get(5).close();
        Assert.assertEquals(4, service.getDataSource().getNumIdle());
        Assert.assertEquals(0, service.getDataSource().getNumActive());
        service.getDataSource().close();
    }

    /**
     * Test database connection using Derby. Connect, create table, insert, select, drop table.
     */
    @Test
    public void testCreateInsertSelect() throws SQLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-good1", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final DBCPService dbcpService = ((DBCPService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-good1")));
        Assert.assertNotNull(dbcpService);
        final Connection connection = dbcpService.getConnection();
        Assert.assertNotNull(connection);
        createInsertSelectDrop(connection);
        connection.close();// return to pool

    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test get database connection using Derby. Get many times, after a while pool should not contain any available connection and getConnection should fail.
     */
    @Test
    public void testExhaustPool() throws SQLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-exhaust", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final DBCPService dbcpService = ((DBCPService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-exhaust")));
        Assert.assertNotNull(dbcpService);
        exception.expect(ProcessException.class);
        exception.expectMessage("Cannot get a connection, pool error Timeout waiting for idle object");
        for (int i = 0; i < 100; i++) {
            final Connection connection = dbcpService.getConnection();
            Assert.assertNotNull(connection);
        }
    }

    /**
     * Test Drop invalid connections and create new ones.
     * Default behavior, invalid connections in pool.
     */
    @Test
    public void testDropInvalidConnectionsH2_Default() throws Exception {
        // start the H2 TCP Server
        String[] args = new String[0];
        Server server = Server.createTcpServer(args).start();
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-dropcreate", service);
        runner.setProperty(service, DATABASE_URL, (("jdbc:h2:tcp://localhost:" + (server.getPort())) + "/~/test"));
        runner.setProperty(service, DB_DRIVERNAME, "org.h2.Driver");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final DBCPService dbcpService = ((DBCPService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-dropcreate")));
        Assert.assertNotNull(dbcpService);
        // get and verify connections
        for (int i = 0; i < 10; i++) {
            final Connection connection = dbcpService.getConnection();
            System.out.println(connection);
            Assert.assertNotNull(connection);
            assertValidConnectionH2(connection, i);
            connection.close();
        }
        // restart server, connections in pool should became invalid
        server.stop();
        server.shutdown();
        server.start();
        for (int i = 0; i < 10; i++) {
            final Connection connection = dbcpService.getConnection();
            System.out.println(connection);
            Assert.assertNotNull(connection);
            assertValidConnectionH2(connection, i);
            connection.close();
        }
        server.shutdown();
    }

    /**
     * Test Drop invalid connections and create new ones.
     * Better behavior, invalid connections are dropped and valid created.
     */
    @Test
    public void testDropInvalidConnectionsH2_Better() throws Exception {
        // start the H2 TCP Server
        String[] args = new String[0];
        Server server = Server.createTcpServer(args).start();
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-dropcreate", service);
        runner.setProperty(service, DATABASE_URL, (("jdbc:h2:tcp://localhost:" + (server.getPort())) + "/~/test"));
        runner.setProperty(service, DB_DRIVERNAME, "org.h2.Driver");
        runner.setProperty(service, VALIDATION_QUERY, "SELECT 5");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final DBCPService dbcpService = ((DBCPService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-dropcreate")));
        Assert.assertNotNull(dbcpService);
        // get and verify connections
        for (int i = 0; i < 10; i++) {
            final Connection connection = dbcpService.getConnection();
            System.out.println(connection);
            Assert.assertNotNull(connection);
            assertValidConnectionH2(connection, i);
            connection.close();
        }
        // restart server, connections in pool should became invalid
        server.stop();
        server.shutdown();
        server.start();
        // Note!! We should not get something like:
        // org.h2.jdbc.JdbcSQLException: Connection is broken: "session closed" [90067-192]
        // Pool should remove invalid connections and create new valid connections.
        for (int i = 0; i < 10; i++) {
            final Connection connection = dbcpService.getConnection();
            System.out.println(connection);
            Assert.assertNotNull(connection);
            assertValidConnectionH2(connection, i);
            connection.close();
        }
        server.shutdown();
    }

    /**
     * Test get database connection using Derby. Get many times, release immediately and getConnection should not fail.
     */
    @Test
    public void testGetManyNormal() throws SQLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final DBCPConnectionPool service = new DBCPConnectionPool();
        runner.addControllerService("test-exhaust", service);
        // remove previous test database, if any
        final File dbLocation = new File(DBCPServiceTest.DB_LOCATION);
        dbLocation.delete();
        // set embedded Derby database connection url
        runner.setProperty(service, DATABASE_URL, (("jdbc:derby:" + (DBCPServiceTest.DB_LOCATION)) + ";create=true"));
        runner.setProperty(service, DB_USER, "tester");
        runner.setProperty(service, DB_PASSWORD, "testerp");
        runner.setProperty(service, DB_DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final DBCPService dbcpService = ((DBCPService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-exhaust")));
        Assert.assertNotNull(dbcpService);
        for (int i = 0; i < 1000; i++) {
            final Connection connection = dbcpService.getConnection();
            Assert.assertNotNull(connection);
            connection.close();// will return connection to pool

        }
    }

    @Test
    public void testDriverLoad() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Assert.assertNotNull(clazz);
    }

    String createTable = "create table restaurants(id integer, name varchar(20), city varchar(50))";

    String dropTable = "drop table restaurants";
}

