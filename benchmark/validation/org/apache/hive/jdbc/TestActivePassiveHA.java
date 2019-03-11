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
package org.apache.hive.jdbc;


import ConfVars.HIVE_SERVER2_WEBUI_ENABLE_CORS;
import ConfVars.HIVE_SERVER2_WEBUI_PORT.varname;
import HS2Peers.HS2Instances;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.http.security.PamAuthenticator;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.server.HS2ActivePassiveHARegistry;
import org.apache.hive.service.server.HS2ActivePassiveHARegistryClient;
import org.apache.hive.service.server.HiveServer2Instance;
import org.apache.hive.service.servlet.HS2Peers;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class TestActivePassiveHA {
    private MiniHS2 miniHS2_1 = null;

    private MiniHS2 miniHS2_2 = null;

    private static TestingServer zkServer;

    private Connection hs2Conn = null;

    private static String ADMIN_USER = "user1";// user from TestPamAuthenticator


    private static String ADMIN_PASSWORD = "1";

    private static String serviceDiscoveryMode = "zooKeeperHA";

    private static String zkHANamespace = "hs2ActivePassiveHATest";

    private HiveConf hiveConf1;

    private HiveConf hiveConf2;

    private static Path kvDataFilePath;

    @Test(timeout = 60000)
    public void testActivePassiveHA() throws Exception {
        String instanceId1 = UUID.randomUUID().toString();
        miniHS2_1.start(getConfOverlay(instanceId1));
        String instanceId2 = UUID.randomUUID().toString();
        miniHS2_2.start(getConfOverlay(instanceId2));
        Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
        Assert.assertEquals(true, miniHS2_1.isLeader());
        String url = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
        Assert.assertEquals("true", sendGet(url));
        Assert.assertEquals(false, miniHS2_2.isLeader());
        url = ("http://localhost:" + (hiveConf2.get(varname))) + "/leader";
        Assert.assertEquals("false", sendGet(url));
        url = ("http://localhost:" + (hiveConf1.get(varname))) + "/peers";
        String resp = sendGet(url);
        ObjectMapper objectMapper = new ObjectMapper();
        HS2Peers.HS2Instances hs2Peers = objectMapper.readValue(resp, HS2Instances.class);
        int port1 = Integer.parseInt(hiveConf1.get(ConfVars.HIVE_SERVER2_THRIFT_PORT.varname));
        Assert.assertEquals(2, hs2Peers.getHiveServer2Instances().size());
        for (HiveServer2Instance hsi : hs2Peers.getHiveServer2Instances()) {
            if (((hsi.getRpcPort()) == port1) && (hsi.getWorkerIdentity().equals(instanceId1))) {
                Assert.assertEquals(true, hsi.isLeader());
            } else {
                Assert.assertEquals(false, hsi.isLeader());
            }
        }
        Configuration conf = new Configuration();
        TestActivePassiveHA.setHAConfigs(conf);
        HS2ActivePassiveHARegistry client = HS2ActivePassiveHARegistryClient.getClient(conf);
        List<HiveServer2Instance> hs2Instances = new ArrayList(client.getAll());
        Assert.assertEquals(2, hs2Instances.size());
        List<HiveServer2Instance> leaders = new ArrayList<>();
        List<HiveServer2Instance> standby = new ArrayList<>();
        for (HiveServer2Instance instance : hs2Instances) {
            if (instance.isLeader()) {
                leaders.add(instance);
            } else {
                standby.add(instance);
            }
        }
        Assert.assertEquals(1, leaders.size());
        Assert.assertEquals(1, standby.size());
        miniHS2_1.stop();
        Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
        Assert.assertEquals(true, miniHS2_2.isLeader());
        url = ("http://localhost:" + (hiveConf2.get(varname))) + "/leader";
        Assert.assertEquals("true", sendGet(url));
        while ((client.getAll().size()) != 1) {
            Thread.sleep(100);
        } 
        client = HS2ActivePassiveHARegistryClient.getClient(conf);
        hs2Instances = new ArrayList(client.getAll());
        Assert.assertEquals(1, hs2Instances.size());
        leaders = new ArrayList();
        standby = new ArrayList();
        for (HiveServer2Instance instance : hs2Instances) {
            if (instance.isLeader()) {
                leaders.add(instance);
            } else {
                standby.add(instance);
            }
        }
        Assert.assertEquals(1, leaders.size());
        Assert.assertEquals(0, standby.size());
        url = ("http://localhost:" + (hiveConf2.get(varname))) + "/peers";
        resp = sendGet(url);
        objectMapper = new ObjectMapper();
        hs2Peers = objectMapper.readValue(resp, HS2Instances.class);
        int port2 = Integer.parseInt(hiveConf2.get(ConfVars.HIVE_SERVER2_THRIFT_PORT.varname));
        Assert.assertEquals(1, hs2Peers.getHiveServer2Instances().size());
        for (HiveServer2Instance hsi : hs2Peers.getHiveServer2Instances()) {
            if (((hsi.getRpcPort()) == port2) && (hsi.getWorkerIdentity().equals(instanceId2))) {
                Assert.assertEquals(true, hsi.isLeader());
            } else {
                Assert.assertEquals(false, hsi.isLeader());
            }
        }
        // start 1st server again
        instanceId1 = UUID.randomUUID().toString();
        miniHS2_1.start(getConfOverlay(instanceId1));
        Assert.assertEquals(false, miniHS2_1.isLeader());
        url = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
        Assert.assertEquals("false", sendGet(url));
        while ((client.getAll().size()) != 2) {
            Thread.sleep(100);
        } 
        client = HS2ActivePassiveHARegistryClient.getClient(conf);
        hs2Instances = new ArrayList(client.getAll());
        Assert.assertEquals(2, hs2Instances.size());
        leaders = new ArrayList();
        standby = new ArrayList();
        for (HiveServer2Instance instance : hs2Instances) {
            if (instance.isLeader()) {
                leaders.add(instance);
            } else {
                standby.add(instance);
            }
        }
        Assert.assertEquals(1, leaders.size());
        Assert.assertEquals(1, standby.size());
        url = ("http://localhost:" + (hiveConf1.get(varname))) + "/peers";
        resp = sendGet(url);
        objectMapper = new ObjectMapper();
        hs2Peers = objectMapper.readValue(resp, HS2Instances.class);
        port2 = Integer.parseInt(hiveConf2.get(ConfVars.HIVE_SERVER2_THRIFT_PORT.varname));
        Assert.assertEquals(2, hs2Peers.getHiveServer2Instances().size());
        for (HiveServer2Instance hsi : hs2Peers.getHiveServer2Instances()) {
            if (((hsi.getRpcPort()) == port2) && (hsi.getWorkerIdentity().equals(instanceId2))) {
                Assert.assertEquals(true, hsi.isLeader());
            } else {
                Assert.assertEquals(false, hsi.isLeader());
            }
        }
    }

    @Test(timeout = 60000)
    public void testConnectionActivePassiveHAServiceDiscovery() throws Exception {
        String instanceId1 = UUID.randomUUID().toString();
        miniHS2_1.start(getConfOverlay(instanceId1));
        String instanceId2 = UUID.randomUUID().toString();
        Map<String, String> confOverlay = getConfOverlay(instanceId2);
        confOverlay.put(ConfVars.HIVE_SERVER2_TRANSPORT_MODE.varname, "http");
        confOverlay.put(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH.varname, "clidriverTest");
        miniHS2_2.start(confOverlay);
        Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
        Assert.assertEquals(true, miniHS2_1.isLeader());
        String url = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
        Assert.assertEquals("true", sendGet(url));
        Assert.assertEquals(false, miniHS2_2.isLeader());
        url = ("http://localhost:" + (hiveConf2.get(varname))) + "/leader";
        Assert.assertEquals("false", sendGet(url));
        // miniHS2_1 will be leader
        String zkConnectString = TestActivePassiveHA.zkServer.getConnectString();
        String zkJdbcUrl = miniHS2_1.getJdbcURL();
        // getAllUrls will parse zkJdbcUrl and will plugin the active HS2's host:port
        String parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
        String hs2_1_directUrl = ((((((("jdbc:hive2://" + (miniHS2_1.getHost())) + ":") + (miniHS2_1.getBinaryPort())) + "/default;serviceDiscoveryMode=") + (TestActivePassiveHA.serviceDiscoveryMode)) + ";zooKeeperNamespace=") + (TestActivePassiveHA.zkHANamespace)) + ";";
        Assert.assertTrue(zkJdbcUrl.contains(zkConnectString));
        Assert.assertEquals(hs2_1_directUrl, parsedUrl);
        openConnectionAndRunQuery(zkJdbcUrl);
        // miniHS2_2 will become leader
        miniHS2_1.stop();
        Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
        parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
        String hs2_2_directUrl = ((((((("jdbc:hive2://" + (miniHS2_2.getHost())) + ":") + (miniHS2_2.getHttpPort())) + "/default;serviceDiscoveryMode=") + (TestActivePassiveHA.serviceDiscoveryMode)) + ";zooKeeperNamespace=") + (TestActivePassiveHA.zkHANamespace)) + ";";
        Assert.assertTrue(zkJdbcUrl.contains(zkConnectString));
        Assert.assertEquals(hs2_2_directUrl, parsedUrl);
        openConnectionAndRunQuery(zkJdbcUrl);
        // miniHS2_2 will continue to be leader
        instanceId1 = UUID.randomUUID().toString();
        miniHS2_1.start(getConfOverlay(instanceId1));
        parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
        Assert.assertTrue(zkJdbcUrl.contains(zkConnectString));
        Assert.assertEquals(hs2_2_directUrl, parsedUrl);
        openConnectionAndRunQuery(zkJdbcUrl);
        // miniHS2_1 will become leader
        miniHS2_2.stop();
        Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
        parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
        hs2_1_directUrl = ((((((("jdbc:hive2://" + (miniHS2_1.getHost())) + ":") + (miniHS2_1.getBinaryPort())) + "/default;serviceDiscoveryMode=") + (TestActivePassiveHA.serviceDiscoveryMode)) + ";zooKeeperNamespace=") + (TestActivePassiveHA.zkHANamespace)) + ";";
        Assert.assertTrue(zkJdbcUrl.contains(zkConnectString));
        Assert.assertEquals(hs2_1_directUrl, parsedUrl);
        openConnectionAndRunQuery(zkJdbcUrl);
    }

    @Test(timeout = 60000)
    public void testManualFailover() throws Exception {
        hiveConf1.setBoolVar(HIVE_SERVER2_WEBUI_ENABLE_CORS, true);
        hiveConf2.setBoolVar(HIVE_SERVER2_WEBUI_ENABLE_CORS, true);
        setPamConfs(hiveConf1);
        setPamConfs(hiveConf2);
        PamAuthenticator pamAuthenticator1 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf1);
        PamAuthenticator pamAuthenticator2 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf2);
        try {
            String instanceId1 = UUID.randomUUID().toString();
            miniHS2_1.setPamAuthenticator(pamAuthenticator1);
            miniHS2_1.start(getSecureConfOverlay(instanceId1));
            String instanceId2 = UUID.randomUUID().toString();
            Map<String, String> confOverlay = getSecureConfOverlay(instanceId2);
            confOverlay.put(ConfVars.HIVE_SERVER2_TRANSPORT_MODE.varname, "http");
            confOverlay.put(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH.varname, "clidriverTest");
            miniHS2_2.setPamAuthenticator(pamAuthenticator2);
            miniHS2_2.start(confOverlay);
            String url1 = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
            String url2 = ("http://localhost:" + (hiveConf2.get(varname))) + "/leader";
            // when we start miniHS2_1 will be leader (sequential start)
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            Assert.assertEquals("true", sendGet(url1, true, true));
            // trigger failover on miniHS2_1
            String resp = sendDelete(url1, true, true);
            Assert.assertTrue(resp.contains("Failover successful!"));
            // make sure miniHS2_1 is not leader
            Assert.assertEquals(true, miniHS2_1.getNotLeaderTestFuture().get());
            Assert.assertEquals(false, miniHS2_1.isLeader());
            Assert.assertEquals("false", sendGet(url1, true, true));
            // make sure miniHS2_2 is the new leader
            Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_2.isLeader());
            Assert.assertEquals("true", sendGet(url2, true, true));
            // send failover request again to miniHS2_1 and get a failure
            resp = sendDelete(url1, true, true);
            Assert.assertTrue(resp.contains("Cannot failover an instance that is not a leader"));
            Assert.assertEquals(true, miniHS2_1.getNotLeaderTestFuture().get());
            Assert.assertEquals(false, miniHS2_1.isLeader());
            // send failover request to miniHS2_2 and make sure miniHS2_1 takes over (returning back to leader, test listeners)
            resp = sendDelete(url2, true, true);
            Assert.assertTrue(resp.contains("Failover successful!"));
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            Assert.assertEquals("true", sendGet(url1, true, true));
            Assert.assertEquals(true, miniHS2_2.getNotLeaderTestFuture().get());
            Assert.assertEquals("false", sendGet(url2, true, true));
            Assert.assertEquals(false, miniHS2_2.isLeader());
        } finally {
            resetFailoverConfs();
        }
    }

    @Test(timeout = 60000)
    public void testManualFailoverUnauthorized() throws Exception {
        setPamConfs(hiveConf1);
        PamAuthenticator pamAuthenticator1 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf1);
        try {
            String instanceId1 = UUID.randomUUID().toString();
            miniHS2_1.setPamAuthenticator(pamAuthenticator1);
            miniHS2_1.start(getSecureConfOverlay(instanceId1));
            // dummy HS2 instance just to trigger failover
            String instanceId2 = UUID.randomUUID().toString();
            Map<String, String> confOverlay = getSecureConfOverlay(instanceId2);
            confOverlay.put(ConfVars.HIVE_SERVER2_TRANSPORT_MODE.varname, "http");
            confOverlay.put(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH.varname, "clidriverTest");
            miniHS2_2.start(confOverlay);
            String url1 = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
            // when we start miniHS2_1 will be leader (sequential start)
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            Assert.assertEquals("true", sendGet(url1, true));
            // trigger failover on miniHS2_1 without authorization header
            Assert.assertTrue(sendDelete(url1, false).contains("Unauthorized"));
            Assert.assertTrue(sendDelete(url1, true).contains("Failover successful!"));
            Assert.assertEquals(true, miniHS2_1.getNotLeaderTestFuture().get());
            Assert.assertEquals(false, miniHS2_1.isLeader());
            Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_2.isLeader());
        } finally {
            // revert configs to not affect other tests
            unsetPamConfs(hiveConf1);
        }
    }

    @Test(timeout = 60000)
    public void testNoConnectionOnPassive() throws Exception {
        hiveConf1.setBoolVar(HIVE_SERVER2_WEBUI_ENABLE_CORS, true);
        hiveConf2.setBoolVar(HIVE_SERVER2_WEBUI_ENABLE_CORS, true);
        setPamConfs(hiveConf1);
        setPamConfs(hiveConf2);
        try {
            PamAuthenticator pamAuthenticator1 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf1);
            PamAuthenticator pamAuthenticator2 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf2);
            String instanceId1 = UUID.randomUUID().toString();
            miniHS2_1.setPamAuthenticator(pamAuthenticator1);
            miniHS2_1.start(getSecureConfOverlay(instanceId1));
            String instanceId2 = UUID.randomUUID().toString();
            Map<String, String> confOverlay = getSecureConfOverlay(instanceId2);
            miniHS2_2.setPamAuthenticator(pamAuthenticator2);
            miniHS2_2.start(confOverlay);
            String url1 = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            // Don't get urls from ZK, it will actually be a service discovery URL that we don't want.
            String hs1Url = (("jdbc:hive2://" + (miniHS2_1.getHost())) + ":") + (miniHS2_1.getBinaryPort());
            Connection hs2Conn = getConnection(hs1Url, System.getProperty("user.name"));// Should work.

            hs2Conn.close();
            String resp = sendDelete(url1, true);
            Assert.assertTrue(resp, resp.contains("Failover successful!"));
            // wait for failover to close sessions
            while ((miniHS2_1.getOpenSessionsCount()) != 0) {
                Thread.sleep(100);
            } 
            Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_2.isLeader());
            try {
                hs2Conn = getConnection(hs1Url, System.getProperty("user.name"));
                Assert.fail("Should throw");
            } catch (Exception e) {
                if (!(e.getMessage().contains("Cannot open sessions on an inactive HS2"))) {
                    throw e;
                }
            }
        } finally {
            resetFailoverConfs();
        }
    }

    @Test(timeout = 60000)
    public void testClientConnectionsOnFailover() throws Exception {
        setPamConfs(hiveConf1);
        setPamConfs(hiveConf2);
        PamAuthenticator pamAuthenticator1 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf1);
        PamAuthenticator pamAuthenticator2 = new org.apache.hive.service.server.TestHS2HttpServerPam.TestPamAuthenticator(hiveConf2);
        try {
            String instanceId1 = UUID.randomUUID().toString();
            miniHS2_1.setPamAuthenticator(pamAuthenticator1);
            miniHS2_1.start(getSecureConfOverlay(instanceId1));
            String instanceId2 = UUID.randomUUID().toString();
            Map<String, String> confOverlay = getSecureConfOverlay(instanceId2);
            confOverlay.put(ConfVars.HIVE_SERVER2_TRANSPORT_MODE.varname, "http");
            confOverlay.put(ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH.varname, "clidriverTest");
            miniHS2_2.setPamAuthenticator(pamAuthenticator2);
            miniHS2_2.start(confOverlay);
            String url1 = ("http://localhost:" + (hiveConf1.get(varname))) + "/leader";
            String url2 = ("http://localhost:" + (hiveConf2.get(varname))) + "/leader";
            String zkJdbcUrl = miniHS2_1.getJdbcURL();
            String zkConnectString = TestActivePassiveHA.zkServer.getConnectString();
            Assert.assertTrue(zkJdbcUrl.contains(zkConnectString));
            // when we start miniHS2_1 will be leader (sequential start)
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            Assert.assertEquals("true", sendGet(url1, true));
            // before failover, check if we are getting connection from miniHS2_1
            String hs2_1_directUrl = ((((((("jdbc:hive2://" + (miniHS2_1.getHost())) + ":") + (miniHS2_1.getBinaryPort())) + "/default;serviceDiscoveryMode=") + (TestActivePassiveHA.serviceDiscoveryMode)) + ";zooKeeperNamespace=") + (TestActivePassiveHA.zkHANamespace)) + ";";
            String parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
            Assert.assertEquals(hs2_1_directUrl, parsedUrl);
            hs2Conn = getConnection(zkJdbcUrl, System.getProperty("user.name"));
            while ((miniHS2_1.getOpenSessionsCount()) != 1) {
                Thread.sleep(100);
            } 
            // trigger failover on miniHS2_1 and make sure the connections are closed
            String resp = sendDelete(url1, true);
            Assert.assertTrue(resp.contains("Failover successful!"));
            // wait for failover to close sessions
            while ((miniHS2_1.getOpenSessionsCount()) != 0) {
                Thread.sleep(100);
            } 
            // make sure miniHS2_1 is not leader
            Assert.assertEquals(true, miniHS2_1.getNotLeaderTestFuture().get());
            Assert.assertEquals(false, miniHS2_1.isLeader());
            Assert.assertEquals("false", sendGet(url1, true));
            // make sure miniHS2_2 is the new leader
            Assert.assertEquals(true, miniHS2_2.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_2.isLeader());
            Assert.assertEquals("true", sendGet(url2, true));
            // when we make a new connection we should get it from miniHS2_2 this time
            String hs2_2_directUrl = ((((((("jdbc:hive2://" + (miniHS2_2.getHost())) + ":") + (miniHS2_2.getHttpPort())) + "/default;serviceDiscoveryMode=") + (TestActivePassiveHA.serviceDiscoveryMode)) + ";zooKeeperNamespace=") + (TestActivePassiveHA.zkHANamespace)) + ";";
            parsedUrl = HiveConnection.getAllUrls(zkJdbcUrl).get(0).getJdbcUriString();
            Assert.assertEquals(hs2_2_directUrl, parsedUrl);
            hs2Conn = getConnection(zkJdbcUrl, System.getProperty("user.name"));
            while ((miniHS2_2.getOpenSessionsCount()) != 1) {
                Thread.sleep(100);
            } 
            // send failover request again to miniHS2_1 and get a failure
            resp = sendDelete(url1, true);
            Assert.assertTrue(resp.contains("Cannot failover an instance that is not a leader"));
            Assert.assertEquals(true, miniHS2_1.getNotLeaderTestFuture().get());
            Assert.assertEquals(false, miniHS2_1.isLeader());
            // send failover request to miniHS2_2 and make sure miniHS2_1 takes over (returning back to leader, test listeners)
            resp = sendDelete(url2, true);
            Assert.assertTrue(resp.contains("Failover successful!"));
            Assert.assertEquals(true, miniHS2_1.getIsLeaderTestFuture().get());
            Assert.assertEquals(true, miniHS2_1.isLeader());
            Assert.assertEquals("true", sendGet(url1, true));
            Assert.assertEquals(true, miniHS2_2.getNotLeaderTestFuture().get());
            Assert.assertEquals("false", sendGet(url2, true));
            Assert.assertEquals(false, miniHS2_2.isLeader());
            // make sure miniHS2_2 closes all its connections
            while ((miniHS2_2.getOpenSessionsCount()) != 0) {
                Thread.sleep(100);
            } 
            // new connections goes to miniHS2_1 now
            hs2Conn = getConnection(zkJdbcUrl, System.getProperty("user.name"));
            while ((miniHS2_1.getOpenSessionsCount()) != 1) {
                Thread.sleep(100);
            } 
        } finally {
            // revert configs to not affect other tests
            unsetPamConfs(hiveConf1);
            unsetPamConfs(hiveConf2);
        }
    }

    // This is test for llap command AuthZ added in HIVE-19033 which require ZK access for it to pass
    @Test(timeout = 60000)
    public void testNoAuthZLlapClusterInfo() throws Exception {
        String instanceId1 = UUID.randomUUID().toString();
        miniHS2_1.start(getConfOverlay(instanceId1));
        Connection hs2Conn = getConnection(miniHS2_1.getJdbcURL(), "user1");
        boolean caughtException = false;
        Statement stmt = hs2Conn.createStatement();
        try {
            stmt.execute("set hive.llap.daemon.service.hosts=@localhost");
            stmt.execute("llap cluster -info");
        } catch (SQLException e) {
            caughtException = true;
        } finally {
            stmt.close();
            hs2Conn.close();
        }
        Assert.assertEquals(false, caughtException);
    }
}

