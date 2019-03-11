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


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


public class TestServiceDiscoveryWithMiniHS2 {
    private MiniHS2 miniHS2 = null;

    private static HiveConf hiveConf;

    private static TestingServer zkServer;

    private static String zkRootNamespace = "hs2test";

    private static String dataFileDir;

    private static Path kvDataFilePath;

    private Connection hs2Conn = null;

    @Test
    public void testConnectionWithConfigsPublished() throws Exception {
        Map<String, String> confOverlay = new HashMap<String, String>();
        confOverlay.put("hive.server2.zookeeper.publish.configs", "true");
        miniHS2.start(confOverlay);
        openConnectionAndRunQuery();
    }

    @Test
    public void testConnectionWithoutConfigsPublished() throws Exception {
        Map<String, String> confOverlay = new HashMap<String, String>();
        confOverlay.put("hive.server2.zookeeper.publish.configs", "false");
        miniHS2.start(confOverlay);
        openConnectionAndRunQuery();
    }

    @Test
    public void testGetAllUrlsZk() throws Exception {
        Map<String, String> confOverlay = new HashMap<String, String>();
        confOverlay.put("hive.server2.zookeeper.publish.configs", "true");
        miniHS2.start(confOverlay);
        String directUrl = HiveConnection.getAllUrls(miniHS2.getJdbcURL()).get(0).getJdbcUriString();
        String jdbcUrl = ((("jdbc:hive2://" + (miniHS2.getHost())) + ":") + (miniHS2.getBinaryPort())) + "/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hs2test;";
        Assert.assertEquals(jdbcUrl, directUrl);
    }

    @Test
    public void testGetAllUrlsDirect() throws Exception {
        Map<String, String> confOverlay = new HashMap<String, String>();
        confOverlay.put("hive.server2.zookeeper.publish.configs", "false");
        miniHS2.start(confOverlay);
        String directUrl = HiveConnection.getAllUrls(miniHS2.getJdbcURL()).get(0).getJdbcUriString();
        String jdbcUrl = ((("jdbc:hive2://" + (miniHS2.getHost())) + ":") + (miniHS2.getBinaryPort())) + "/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hs2test;";
        Assert.assertEquals(jdbcUrl, directUrl);
    }
}

