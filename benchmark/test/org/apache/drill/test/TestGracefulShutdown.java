/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.test;


import ExecConstants.ALLOW_LOOPBACK_ADDRESS_BINDING;
import ExecConstants.INITIAL_BIT_PORT;
import ExecConstants.INITIAL_USER_PORT;
import ExecConstants.ZK_REFRESH;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.server.Drillbit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;


@Category({ SlowTest.class })
public class TestGracefulShutdown extends BaseTestQuery {
    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(120000);

    /* Start multiple drillbits and then shutdown a drillbit. Query the online
    endpoints and check if the drillbit still exists.
     */
    @Test
    public void testOnlineEndPoints() throws Exception {
        String[] drillbits = new String[]{ "db1", "db2", "db3" };
        ClusterFixtureBuilder builder = ClusterFixture.bareBuilder(ExecTest.dirTestWatcher).withLocalZk().withBits(drillbits);
        TestGracefulShutdown.enableDrillPortHunting(builder);
        try (ClusterFixture cluster = builder.build()) {
            Drillbit drillbit = cluster.drillbit("db2");
            int zkRefresh = drillbit.getContext().getConfig().getInt(ZK_REFRESH);
            DrillbitEndpoint drillbitEndpoint = drillbit.getRegistrationHandle().getEndPoint();
            cluster.closeDrillbit("db2");
            while (true) {
                Collection<DrillbitEndpoint> drillbitEndpoints = cluster.drillbit().getContext().getClusterCoordinator().getOnlineEndPoints();
                if (!(drillbitEndpoints.contains(drillbitEndpoint))) {
                    // Success
                    return;
                }
                Thread.sleep(zkRefresh);
            } 
        }
    }

    /* Test shutdown through RestApi */
    @Test
    public void testRestApi() throws Exception {
        String[] drillbits = new String[]{ "db1", "db2", "db3" };
        ClusterFixtureBuilder builder = ClusterFixture.bareBuilder(ExecTest.dirTestWatcher).withLocalZk().withBits(drillbits);
        TestGracefulShutdown.enableWebServer(builder);
        QueryBuilder.QuerySummaryFuture listener;
        final String sql = "select * from dfs.root.`.`";
        try (ClusterFixture cluster = builder.build();final ClientFixture client = cluster.clientFixture()) {
            Drillbit drillbit = cluster.drillbit("db1");
            int port = drillbit.getWebServerPort();
            int zkRefresh = drillbit.getContext().getConfig().getInt(ZK_REFRESH);
            listener = client.queryBuilder().sql(sql).futureSummary();
            URL url = new URL((("http://localhost:" + port) + "/gracefulShutdown"));
            HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("POST");
            if ((conn.getResponseCode()) != 200) {
                throw new RuntimeException(("Failed : HTTP error code : " + (conn.getResponseCode())));
            }
            while (true) {
                if (listener.isDone()) {
                    break;
                }
                Thread.sleep(100L);
            } 
            if (TestGracefulShutdown.waitAndAssertDrillbitCount(cluster, zkRefresh)) {
                return;
            }
            Assert.fail("Timed out");
        }
    }

    /* Test default shutdown through RestApi */
    @Test
    public void testRestApiShutdown() throws Exception {
        String[] drillbits = new String[]{ "db1", "db2", "db3" };
        ClusterFixtureBuilder builder = ClusterFixture.bareBuilder(ExecTest.dirTestWatcher).withLocalZk().withBits(drillbits);
        TestGracefulShutdown.enableWebServer(builder);
        QueryBuilder.QuerySummaryFuture listener;
        final String sql = "select * from dfs.root.`.`";
        try (ClusterFixture cluster = builder.build();final ClientFixture client = cluster.clientFixture()) {
            Drillbit drillbit = cluster.drillbit("db1");
            int port = drillbit.getWebServerPort();
            int zkRefresh = drillbit.getContext().getConfig().getInt(ZK_REFRESH);
            listener = client.queryBuilder().sql(sql).futureSummary();
            while (true) {
                if (listener.isDone()) {
                    break;
                }
                Thread.sleep(100L);
            } 
            URL url = new URL((("http://localhost:" + port) + "/shutdown"));
            HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("POST");
            if ((conn.getResponseCode()) != 200) {
                throw new RuntimeException(("Failed : HTTP error code : " + (conn.getResponseCode())));
            }
            if (TestGracefulShutdown.waitAndAssertDrillbitCount(cluster, zkRefresh)) {
                return;
            }
            Assert.fail("Timed out");
        }
    }

    // DRILL-6912
    @Test
    public void testDrillbitWithSamePortContainsShutdownThread() throws Exception {
        ClusterFixtureBuilder fixtureBuilder = ClusterFixture.bareBuilder(ExecTest.dirTestWatcher).withLocalZk().configProperty(ALLOW_LOOPBACK_ADDRESS_BINDING, true).configProperty(INITIAL_USER_PORT, QueryTestUtil.getFreePortNumber(31170, 300)).configProperty(INITIAL_BIT_PORT, QueryTestUtil.getFreePortNumber(31180, 300));
        try (ClusterFixture fixture = fixtureBuilder.build();Drillbit drillbitWithSamePort = new Drillbit(fixture.config(), fixtureBuilder.configBuilder().getDefinitions(), fixture.serviceSet())) {
            // Assert preconditions :
            // 1. First drillbit instance should be started normally
            // 2. Second instance startup should fail, because ports are occupied by the first one
            Assert.assertNotNull("First drillbit instance should be initialized", fixture.drillbit());
            try {
                drillbitWithSamePort.run();
                Assert.fail("Invocation of 'drillbitWithSamePort.run()' should throw UserException");
            } catch (UserException e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("RESOURCE ERROR: Drillbit could not bind to port"));
                // Ensure that drillbit with failed startup may be safely closed
                Assert.assertNotNull("Drillbit.gracefulShutdownThread shouldn't be null, otherwise close() may throw NPE (if so, check suppressed exception).", drillbitWithSamePort.getGracefulShutdownThread());
            }
        }
    }

    // DRILL-7056
    @Test
    public void testDrillbitTempDir() throws Exception {
        File originalDrillbitTempDir = null;
        ClusterFixtureBuilder fixtureBuilder = ClusterFixture.bareBuilder(ExecTest.dirTestWatcher).withLocalZk().configProperty(ALLOW_LOOPBACK_ADDRESS_BINDING, true).configProperty(INITIAL_USER_PORT, QueryTestUtil.getFreePortNumber(31170, 300)).configProperty(INITIAL_BIT_PORT, QueryTestUtil.getFreePortNumber(31180, 300));
        try (ClusterFixture fixture = fixtureBuilder.build();Drillbit twinDrillbitOnSamePort = new Drillbit(fixture.config(), fixtureBuilder.configBuilder().getDefinitions(), fixture.serviceSet())) {
            // Assert preconditions :
            // 1. First drillbit instance should be started normally
            // 2. Second instance startup should fail, because ports are occupied by the first one
            Drillbit originalDrillbit = fixture.drillbit();
            Assert.assertNotNull("First drillbit instance should be initialized", originalDrillbit);
            originalDrillbitTempDir = TestGracefulShutdown.getWebServerTempDirPath(originalDrillbit);
            Assert.assertTrue("First drillbit instance should have a temporary Javascript dir initialized", originalDrillbitTempDir.exists());
            try {
                twinDrillbitOnSamePort.run();
                Assert.fail("Invocation of 'twinDrillbitOnSamePort.run()' should throw UserException");
            } catch (UserException userEx) {
                Assert.assertThat(userEx.getMessage(), CoreMatchers.containsString("RESOURCE ERROR: Drillbit could not bind to port"));
            }
        }
        // Verify deletion
        Assert.assertFalse("First drillbit instance should have a temporary Javascript dir deleted", originalDrillbitTempDir.exists());
    }
}

