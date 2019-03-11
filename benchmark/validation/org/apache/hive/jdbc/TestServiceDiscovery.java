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


import Utils.JdbcConnectionParams;
import Utils.JdbcConnectionParams.ZOOKEEPER_NAMESPACE;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Hive dynamic service discovery.
 */
public class TestServiceDiscovery {
    private static TestingServer server;

    private static CuratorFramework client;

    private static String rootNamespace = "hiveserver2";

    @Test
    public void testConnect() throws Exception {
        Map<String, String> confs = new HashMap<String, String>();
        confs.put("hive.server2.thrift.bind.host", "host-1");
        confs.put("hive.server2.transport.mode", "binary");
        confs.put("hive.server2.thrift.port", "8000");
        confs.put("hive.server2.authentication", "PLAIN");
        publishConfsToZk(confs, "uri1");
        confs.clear();
        confs.put("hive.server2.thrift.bind.host", "host-2");
        confs.put("hive.server2.transport.mode", "binary");
        confs.put("hive.server2.thrift.port", "9000");
        confs.put("hive.server2.authentication", "PLAIN");
        publishConfsToZk(confs, "uri2");
        confs.clear();
        confs.put("hive.server2.thrift.bind.host", "host-3");
        confs.put("hive.server2.transport.mode", "binary");
        confs.put("hive.server2.thrift.port", "10000");
        confs.put("hive.server2.authentication", "PLAIN");
        publishConfsToZk(confs, "uri3");
        Utils.JdbcConnectionParams connParams = new Utils.JdbcConnectionParams();
        connParams.setZooKeeperEnsemble(TestServiceDiscovery.server.getConnectString());
        connParams.getSessionVars().put(ZOOKEEPER_NAMESPACE, "hiveserver2");
        List<TestServiceDiscovery.ConnParamInfo> allConnectParams = new ArrayList<>();
        while (true) {
            // Reject all paths to force it to continue.  When no more paths, should throw an exception.
            try {
                ZooKeeperHiveClientHelper.configureConnParams(connParams);
            } catch (ZooKeeperHiveClientException e) {
                break;
            }
            connParams.getRejectedHostZnodePaths().add(connParams.getCurrentHostZnodePath());
            allConnectParams.add(new TestServiceDiscovery.ConnParamInfo(connParams.getHost(), connParams.getPort(), connParams.getCurrentHostZnodePath()));
        } 
        // Make sure it itereated through all possible ConnParams
        Collection<TestServiceDiscovery.ConnParamInfo> cp1 = Collections2.filter(allConnectParams, new TestServiceDiscovery.ConnParamInfoPred("host-1", 8000, "serverUri=uri1"));
        Collection<TestServiceDiscovery.ConnParamInfo> cp2 = Collections2.filter(allConnectParams, new TestServiceDiscovery.ConnParamInfoPred("host-2", 9000, "serverUri=uri2"));
        Collection<TestServiceDiscovery.ConnParamInfo> cp3 = Collections2.filter(allConnectParams, new TestServiceDiscovery.ConnParamInfoPred("host-3", 10000, "serverUri=uri3"));
        Assert.assertEquals(cp1.size(), 1);
        Assert.assertEquals(cp2.size(), 1);
        Assert.assertEquals(cp3.size(), 1);
    }

    // Helper classes for ConnParam comparison logics.
    private class ConnParamInfo {
        String host;

        int port;

        String path;

        public ConnParamInfo(String host, int port, String path) {
            this.host = host;
            this.port = port;
            this.path = path;
        }
    }

    private class ConnParamInfoPred implements Predicate<TestServiceDiscovery.ConnParamInfo> {
        String host;

        int port;

        String pathPrefix;

        ConnParamInfoPred(String host, int port, String pathPrefix) {
            this.host = host;
            this.port = port;
            this.pathPrefix = pathPrefix;
        }

        @Override
        public boolean apply(TestServiceDiscovery.ConnParamInfo inputParam) {
            return ((inputParam.host.equals(host)) && ((inputParam.port) == (port))) && (inputParam.path.startsWith(pathPrefix));
        }
    }
}

