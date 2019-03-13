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
package org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.datasource;


import ConnectionMode.MEMORY_STRICTLY;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.constant.ConnectionMode;
import org.apache.shardingsphere.shardingproxy.config.yaml.YamlDataSourceParameter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class JDBCBackendDataSourceTest {
    private JDBCBackendDataSource jdbcBackendDataSource = new JDBCBackendDataSource(Collections.<String, YamlDataSourceParameter>emptyMap());

    @Test
    public void assertGetConnectionFixedOne() throws SQLException {
        Connection actual = jdbcBackendDataSource.getConnection("ds_1");
        Assert.assertThat(actual, CoreMatchers.instanceOf(Connection.class));
    }

    @Test
    public void assertGetConnectionsSucceed() throws SQLException {
        List<Connection> actual = jdbcBackendDataSource.getConnections(MEMORY_STRICTLY, "ds_1", 5);
        Assert.assertEquals(5, actual.size());
    }

    @Test(expected = SQLException.class)
    public void assertGetConnectionsFailed() throws SQLException {
        jdbcBackendDataSource.getConnections(MEMORY_STRICTLY, "ds_1", 6);
    }

    @Test
    public void assertGetConnectionsByMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<List<Connection>>> futures = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            futures.add(executorService.submit(new JDBCBackendDataSourceTest.CallableTask(ConnectionMode.MEMORY_STRICTLY, "ds_1", 6)));
        }
        List<Connection> actual = new ArrayList<>();
        for (Future<List<Connection>> each : futures) {
            try {
                actual.addAll(each.get());
            } catch (InterruptedException | ExecutionException ex) {
                Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("Could't get 6 connections one time, partition succeed connection(5) have released!"));
            }
        }
        Assert.assertTrue(actual.isEmpty());
        executorService.shutdown();
    }

    @RequiredArgsConstructor
    private class CallableTask implements Callable<List<Connection>> {
        private final ConnectionMode connectionMode;

        private final String datasourceName;

        private final int connectionSize;

        @Override
        public List<Connection> call() throws SQLException {
            return jdbcBackendDataSource.getConnections(connectionMode, datasourceName, connectionSize);
        }
    }
}

