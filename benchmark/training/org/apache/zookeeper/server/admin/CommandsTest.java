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
package org.apache.zookeeper.server.admin;


import Commands.ConsCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerMetrics;
import org.apache.zookeeper.server.ServerStats;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.quorum.BufferStats;
import org.apache.zookeeper.test.ClientBase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CommandsTest extends ClientBase {
    private static class Field {
        String key;

        Class<?> type;

        Field(String key, Class<?> type) {
            this.key = key;
            this.type = type;
        }
    }

    @Test
    public void testConfiguration() throws IOException, InterruptedException {
        testCommand("configuration", new CommandsTest.Field("client_port", Integer.class), new CommandsTest.Field("data_dir", String.class), new CommandsTest.Field("data_log_dir", String.class), new CommandsTest.Field("tick_time", Integer.class), new CommandsTest.Field("max_client_cnxns", Integer.class), new CommandsTest.Field("min_session_timeout", Integer.class), new CommandsTest.Field("max_session_timeout", Integer.class), new CommandsTest.Field("server_id", Long.class), new CommandsTest.Field("client_port_listen_backlog", Integer.class));
    }

    @Test
    public void testConnections() throws IOException, InterruptedException {
        testCommand("connections", new CommandsTest.Field("connections", Iterable.class), new CommandsTest.Field("secure_connections", Iterable.class));
    }

    @Test
    public void testConnectionStatReset() throws IOException, InterruptedException {
        testCommand("connection_stat_reset");
    }

    @Test
    public void testDump() throws IOException, InterruptedException {
        testCommand("dump", new CommandsTest.Field("expiry_time_to_session_ids", Map.class), new CommandsTest.Field("session_id_to_ephemeral_paths", Map.class));
    }

    @Test
    public void testEnvironment() throws IOException, InterruptedException {
        testCommand("environment", new CommandsTest.Field("zookeeper.version", String.class), new CommandsTest.Field("host.name", String.class), new CommandsTest.Field("java.version", String.class), new CommandsTest.Field("java.vendor", String.class), new CommandsTest.Field("java.home", String.class), new CommandsTest.Field("java.class.path", String.class), new CommandsTest.Field("java.library.path", String.class), new CommandsTest.Field("java.io.tmpdir", String.class), new CommandsTest.Field("java.compiler", String.class), new CommandsTest.Field("os.name", String.class), new CommandsTest.Field("os.arch", String.class), new CommandsTest.Field("os.version", String.class), new CommandsTest.Field("user.name", String.class), new CommandsTest.Field("user.home", String.class), new CommandsTest.Field("user.dir", String.class), new CommandsTest.Field("os.memory.free", String.class), new CommandsTest.Field("os.memory.max", String.class), new CommandsTest.Field("os.memory.total", String.class));
    }

    @Test
    public void testGetTraceMask() throws IOException, InterruptedException {
        testCommand("get_trace_mask", new CommandsTest.Field("tracemask", Long.class));
    }

    @Test
    public void testIsReadOnly() throws IOException, InterruptedException {
        testCommand("is_read_only", new CommandsTest.Field("read_only", Boolean.class));
    }

    @Test
    public void testMonitor() throws IOException, InterruptedException {
        ArrayList<CommandsTest.Field> fields = new ArrayList<>(Arrays.asList(new CommandsTest.Field("version", String.class), new CommandsTest.Field("avg_latency", Double.class), new CommandsTest.Field("max_latency", Long.class), new CommandsTest.Field("min_latency", Long.class), new CommandsTest.Field("packets_received", Long.class), new CommandsTest.Field("packets_sent", Long.class), new CommandsTest.Field("num_alive_connections", Integer.class), new CommandsTest.Field("outstanding_requests", Long.class), new CommandsTest.Field("server_state", String.class), new CommandsTest.Field("znode_count", Integer.class), new CommandsTest.Field("watch_count", Integer.class), new CommandsTest.Field("ephemerals_count", Integer.class), new CommandsTest.Field("approximate_data_size", Long.class), new CommandsTest.Field("open_file_descriptor_count", Long.class), new CommandsTest.Field("max_file_descriptor_count", Long.class), new CommandsTest.Field("last_client_response_size", Integer.class), new CommandsTest.Field("max_client_response_size", Integer.class), new CommandsTest.Field("min_client_response_size", Integer.class), new CommandsTest.Field("uptime", Long.class), new CommandsTest.Field("global_sessions", Long.class), new CommandsTest.Field("local_sessions", Long.class), new CommandsTest.Field("connection_drop_probability", Double.class)));
        for (String metric : ServerMetrics.getAllValues().keySet()) {
            if (metric.startsWith("avg_")) {
                fields.add(new CommandsTest.Field(metric, Double.class));
            } else {
                fields.add(new CommandsTest.Field(metric, Long.class));
            }
        }
        CommandsTest.Field[] fieldsArray = fields.toArray(new CommandsTest.Field[0]);
        testCommand("monitor", fieldsArray);
    }

    @Test
    public void testRuok() throws IOException, InterruptedException {
        testCommand("ruok");
    }

    @Test
    public void testServerStats() throws IOException, InterruptedException {
        testCommand("server_stats", new CommandsTest.Field("version", String.class), new CommandsTest.Field("read_only", Boolean.class), new CommandsTest.Field("server_stats", ServerStats.class), new CommandsTest.Field("node_count", Integer.class), new CommandsTest.Field("client_response", BufferStats.class));
    }

    @Test
    public void testSetTraceMask() throws IOException, InterruptedException {
        Map<String, String> kwargs = new HashMap<String, String>();
        kwargs.put("traceMask", "1");
        testCommand("set_trace_mask", kwargs, new CommandsTest.Field("tracemask", Long.class));
    }

    @Test
    public void testStat() throws IOException, InterruptedException {
        testCommand("stats", new CommandsTest.Field("version", String.class), new CommandsTest.Field("read_only", Boolean.class), new CommandsTest.Field("server_stats", ServerStats.class), new CommandsTest.Field("node_count", Integer.class), new CommandsTest.Field("connections", Iterable.class), new CommandsTest.Field("client_response", BufferStats.class));
    }

    @Test
    public void testStatReset() throws IOException, InterruptedException {
        testCommand("stat_reset");
    }

    @Test
    public void testWatches() throws IOException, InterruptedException {
        testCommand("watches", new CommandsTest.Field("session_id_to_watched_paths", Map.class));
    }

    @Test
    public void testWatchesByPath() throws IOException, InterruptedException {
        testCommand("watches_by_path", new CommandsTest.Field("path_to_session_ids", Map.class));
    }

    @Test
    public void testWatchSummary() throws IOException, InterruptedException {
        testCommand("watch_summary", new CommandsTest.Field("num_connections", Integer.class), new CommandsTest.Field("num_paths", Integer.class), new CommandsTest.Field("num_total_watches", Integer.class));
    }

    @Test
    public void testConsCommandSecureOnly() {
        // Arrange
        Commands.ConsCommand cmd = new Commands.ConsCommand();
        ZooKeeperServer zkServer = Mockito.mock(ZooKeeperServer.class);
        ServerCnxnFactory cnxnFactory = Mockito.mock(ServerCnxnFactory.class);
        Mockito.when(zkServer.getSecureServerCnxnFactory()).thenReturn(cnxnFactory);
        // Act
        CommandResponse response = cmd.run(zkServer, null);
        // Assert
        Assert.assertThat(response.toMap().containsKey("connections"), Is.is(true));
        Assert.assertThat(response.toMap().containsKey("secure_connections"), Is.is(true));
    }
}

