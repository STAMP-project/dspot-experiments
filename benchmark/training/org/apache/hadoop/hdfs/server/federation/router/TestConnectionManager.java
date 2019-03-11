/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.federation.router;


import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test functionalities of {@link ConnectionManager}, which manages a pool
 * of connections to NameNodes.
 */
public class TestConnectionManager {
    private Configuration conf;

    private ConnectionManager connManager;

    private static final String[] TEST_GROUP = new String[]{ "TEST_GROUP" };

    private static final UserGroupInformation TEST_USER1 = UserGroupInformation.createUserForTesting("user1", TestConnectionManager.TEST_GROUP);

    private static final UserGroupInformation TEST_USER2 = UserGroupInformation.createUserForTesting("user2", TestConnectionManager.TEST_GROUP);

    private static final UserGroupInformation TEST_USER3 = UserGroupInformation.createUserForTesting("user3", TestConnectionManager.TEST_GROUP);

    private static final String TEST_NN_ADDRESS = "nn1:8080";

    @Test
    public void testCleanup() throws Exception {
        Map<ConnectionPoolId, ConnectionPool> poolMap = connManager.getPools();
        ConnectionPool pool1 = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER1, 0, 10, ClientProtocol.class);
        addConnectionsToPool(pool1, 9, 4);
        poolMap.put(new ConnectionPoolId(TestConnectionManager.TEST_USER1, TestConnectionManager.TEST_NN_ADDRESS, ClientProtocol.class), pool1);
        ConnectionPool pool2 = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER2, 0, 10, ClientProtocol.class);
        addConnectionsToPool(pool2, 10, 10);
        poolMap.put(new ConnectionPoolId(TestConnectionManager.TEST_USER2, TestConnectionManager.TEST_NN_ADDRESS, ClientProtocol.class), pool2);
        checkPoolConnections(TestConnectionManager.TEST_USER1, 9, 4);
        checkPoolConnections(TestConnectionManager.TEST_USER2, 10, 10);
        // Clean up first pool, one connection should be removed, and second pool
        // should remain the same.
        connManager.cleanup(pool1);
        checkPoolConnections(TestConnectionManager.TEST_USER1, 8, 4);
        checkPoolConnections(TestConnectionManager.TEST_USER2, 10, 10);
        // Clean up the first pool again, it should have no effect since it reached
        // the MIN_ACTIVE_RATIO.
        connManager.cleanup(pool1);
        checkPoolConnections(TestConnectionManager.TEST_USER1, 8, 4);
        checkPoolConnections(TestConnectionManager.TEST_USER2, 10, 10);
        // Make sure the number of connections doesn't go below minSize
        ConnectionPool pool3 = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER3, 2, 10, ClientProtocol.class);
        addConnectionsToPool(pool3, 8, 0);
        poolMap.put(new ConnectionPoolId(TestConnectionManager.TEST_USER3, TestConnectionManager.TEST_NN_ADDRESS, ClientProtocol.class), pool3);
        checkPoolConnections(TestConnectionManager.TEST_USER3, 10, 0);
        for (int i = 0; i < 10; i++) {
            connManager.cleanup(pool3);
        }
        checkPoolConnections(TestConnectionManager.TEST_USER3, 2, 0);
        // With active connections added to pool, make sure it honors the
        // MIN_ACTIVE_RATIO again
        addConnectionsToPool(pool3, 8, 2);
        checkPoolConnections(TestConnectionManager.TEST_USER3, 10, 2);
        for (int i = 0; i < 10; i++) {
            connManager.cleanup(pool3);
        }
        checkPoolConnections(TestConnectionManager.TEST_USER3, 4, 2);
    }

    @Test
    public void testGetConnection() throws Exception {
        Map<ConnectionPoolId, ConnectionPool> poolMap = connManager.getPools();
        final int totalConns = 10;
        int activeConns = 5;
        ConnectionPool pool = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER1, 0, 10, ClientProtocol.class);
        addConnectionsToPool(pool, totalConns, activeConns);
        poolMap.put(new ConnectionPoolId(TestConnectionManager.TEST_USER1, TestConnectionManager.TEST_NN_ADDRESS, ClientProtocol.class), pool);
        // All remaining connections should be usable
        final int remainingSlots = totalConns - activeConns;
        for (int i = 0; i < remainingSlots; i++) {
            ConnectionContext cc = pool.getConnection();
            Assert.assertTrue(cc.isUsable());
            cc.getClient();
            activeConns++;
        }
        checkPoolConnections(TestConnectionManager.TEST_USER1, totalConns, activeConns);
        // Ask for more and this returns an active connection
        ConnectionContext cc = pool.getConnection();
        Assert.assertTrue(cc.isActive());
    }

    @Test
    public void testValidClientIndex() throws Exception {
        ConnectionPool pool = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER1, 2, 2, ClientProtocol.class);
        for (int i = -3; i <= 3; i++) {
            pool.getClientIndex().set(i);
            ConnectionContext conn = pool.getConnection();
            Assert.assertNotNull(conn);
            Assert.assertTrue(conn.isUsable());
        }
    }

    @Test
    public void getGetConnectionNamenodeProtocol() throws Exception {
        Map<ConnectionPoolId, ConnectionPool> poolMap = connManager.getPools();
        final int totalConns = 10;
        int activeConns = 5;
        ConnectionPool pool = new ConnectionPool(conf, TestConnectionManager.TEST_NN_ADDRESS, TestConnectionManager.TEST_USER1, 0, 10, NamenodeProtocol.class);
        addConnectionsToPool(pool, totalConns, activeConns);
        poolMap.put(new ConnectionPoolId(TestConnectionManager.TEST_USER1, TestConnectionManager.TEST_NN_ADDRESS, NamenodeProtocol.class), pool);
        // All remaining connections should be usable
        final int remainingSlots = totalConns - activeConns;
        for (int i = 0; i < remainingSlots; i++) {
            ConnectionContext cc = pool.getConnection();
            Assert.assertTrue(cc.isUsable());
            cc.getClient();
            activeConns++;
        }
        checkPoolConnections(TestConnectionManager.TEST_USER1, totalConns, activeConns);
        // Ask for more and this returns an active connection
        ConnectionContext cc = pool.getConnection();
        Assert.assertTrue(cc.isActive());
    }
}

