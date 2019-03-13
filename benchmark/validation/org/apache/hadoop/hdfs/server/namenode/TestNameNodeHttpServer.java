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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_HTTP_POLICY_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.web.URLConnectionFactory;
import org.apache.hadoop.http.HttpConfig.Policy;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestNameNodeHttpServer {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestNameNodeHttpServer.class.getSimpleName());

    private static String keystoresDir;

    private static String sslConfDir;

    private static Configuration conf;

    private static URLConnectionFactory connectionFactory;

    private final Policy policy;

    public TestNameNodeHttpServer(Policy policy) {
        super();
        this.policy = policy;
    }

    @Test
    public void testHttpPolicy() throws Exception {
        TestNameNodeHttpServer.conf.set(DFS_HTTP_POLICY_KEY, policy.name());
        TestNameNodeHttpServer.conf.set(DFS_NAMENODE_HTTPS_ADDRESS_KEY, "localhost:0");
        InetSocketAddress addr = InetSocketAddress.createUnresolved("localhost", 0);
        NameNodeHttpServer server = null;
        try {
            server = new NameNodeHttpServer(TestNameNodeHttpServer.conf, null, addr);
            server.start();
            Assert.assertTrue(TestNameNodeHttpServer.implies(policy.isHttpEnabled(), TestNameNodeHttpServer.canAccess("http", server.getHttpAddress())));
            Assert.assertTrue(TestNameNodeHttpServer.implies((!(policy.isHttpEnabled())), ((server.getHttpAddress()) == null)));
            Assert.assertTrue(TestNameNodeHttpServer.implies(policy.isHttpsEnabled(), TestNameNodeHttpServer.canAccess("https", server.getHttpsAddress())));
            Assert.assertTrue(TestNameNodeHttpServer.implies((!(policy.isHttpsEnabled())), ((server.getHttpsAddress()) == null)));
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}

