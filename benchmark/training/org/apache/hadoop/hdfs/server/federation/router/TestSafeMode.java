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


import SafeModeAction.SAFEMODE_GET;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.junit.Test;


/**
 * Test the SafeMode.
 */
public class TestSafeMode {
    /**
     * Federated HDFS cluster.
     */
    private MiniRouterDFSCluster cluster;

    @Test
    public void testProxySetSafemode() throws Exception {
        MiniRouterDFSCluster.RouterContext routerContext = cluster.getRandomRouter();
        ClientProtocol routerProtocol = routerContext.getClient().getNamenode();
        routerProtocol.setSafeMode(SAFEMODE_GET, true);
        routerProtocol.setSafeMode(SAFEMODE_GET, false);
    }
}

