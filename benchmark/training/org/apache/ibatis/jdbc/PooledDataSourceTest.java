/**
 * Copyright 2009-2011 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.jdbc;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.hsqldb.jdbc.JDBCConnection;
import org.junit.Assert;
import org.junit.Test;


public class PooledDataSourceTest extends BaseDataTest {
    @Test
    public void shouldProperlyMaintainPoolOf3ActiveAnd2IdleConnections() throws Exception {
        PooledDataSource ds = BaseDataTest.createPooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        try {
            BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DDL);
            ds.setDefaultAutoCommit(false);
            ds.setDriverProperties(new Properties() {
                {
                    setProperty("username", "sa");
                    setProperty("password", "");
                }
            });
            ds.setPoolMaximumActiveConnections(3);
            ds.setPoolMaximumIdleConnections(2);
            ds.setPoolMaximumCheckoutTime(10000);
            ds.setPoolPingConnectionsNotUsedFor(1);
            ds.setPoolPingEnabled(true);
            ds.setPoolPingQuery("SELECT * FROM PRODUCT");
            ds.setPoolTimeToWait(10000);
            ds.setLogWriter(null);
            List<Connection> connections = new ArrayList<Connection>();
            for (int i = 0; i < 3; i++) {
                connections.add(ds.getConnection());
            }
            Assert.assertEquals(3, ds.getPoolState().getActiveConnectionCount());
            for (Connection c : connections) {
                c.close();
            }
            Assert.assertEquals(2, ds.getPoolState().getIdleConnectionCount());
            Assert.assertEquals(4, ds.getPoolState().getRequestCount());
            Assert.assertEquals(0, ds.getPoolState().getBadConnectionCount());
            Assert.assertEquals(0, ds.getPoolState().getHadToWaitCount());
            Assert.assertEquals(0, ds.getPoolState().getAverageOverdueCheckoutTime());
            Assert.assertEquals(0, ds.getPoolState().getClaimedOverdueConnectionCount());
            Assert.assertEquals(0, ds.getPoolState().getAverageWaitTime());
            Assert.assertNotNull(ds.getPoolState().toString());
        } finally {
            ds.forceCloseAll();
        }
    }

    @Test
    public void shouldNotFailCallingToStringOverAnInvalidConnection() throws Exception {
        PooledDataSource ds = BaseDataTest.createPooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection c = ds.getConnection();
        c.close();
        c.toString();
    }

    @Test
    public void ShouldReturnRealConnection() throws Exception {
        PooledDataSource ds = BaseDataTest.createPooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection c = ds.getConnection();
        JDBCConnection realConnection = ((JDBCConnection) (PooledDataSource.unwrapConnection(c)));
    }
}

