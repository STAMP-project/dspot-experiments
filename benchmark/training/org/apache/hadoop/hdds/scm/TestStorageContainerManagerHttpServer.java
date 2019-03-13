/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm;


import DFSConfigKeys.DFS_HTTP_POLICY_KEY;
import ScmConfigKeys.OZONE_SCM_HTTPS_ADDRESS_KEY;
import ScmConfigKeys.OZONE_SCM_HTTP_ADDRESS_KEY;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.scm.server.StorageContainerManagerHttpServer;
import org.apache.hadoop.hdfs.web.URLConnectionFactory;
import org.apache.hadoop.http.HttpConfig.Policy;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test http server os SCM with various HTTP option.
 */
@RunWith(Parameterized.class)
public class TestStorageContainerManagerHttpServer {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestStorageContainerManagerHttpServer.class.getSimpleName());

    private static String keystoresDir;

    private static String sslConfDir;

    private static Configuration conf;

    private static URLConnectionFactory connectionFactory;

    private final Policy policy;

    public TestStorageContainerManagerHttpServer(Policy policy) {
        super();
        this.policy = policy;
    }

    @Test
    public void testHttpPolicy() throws Exception {
        TestStorageContainerManagerHttpServer.conf.set(DFS_HTTP_POLICY_KEY, policy.name());
        TestStorageContainerManagerHttpServer.conf.set(OZONE_SCM_HTTP_ADDRESS_KEY, "localhost:0");
        TestStorageContainerManagerHttpServer.conf.set(OZONE_SCM_HTTPS_ADDRESS_KEY, "localhost:0");
        InetSocketAddress.createUnresolved("localhost", 0);
        StorageContainerManagerHttpServer server = null;
        try {
            server = new StorageContainerManagerHttpServer(TestStorageContainerManagerHttpServer.conf);
            server.start();
            Assert.assertTrue(TestStorageContainerManagerHttpServer.implies(policy.isHttpEnabled(), TestStorageContainerManagerHttpServer.canAccess("http", server.getHttpAddress())));
            Assert.assertTrue(TestStorageContainerManagerHttpServer.implies((!(policy.isHttpEnabled())), ((server.getHttpAddress()) == null)));
            Assert.assertTrue(TestStorageContainerManagerHttpServer.implies(policy.isHttpsEnabled(), TestStorageContainerManagerHttpServer.canAccess("https", server.getHttpsAddress())));
            Assert.assertTrue(TestStorageContainerManagerHttpServer.implies((!(policy.isHttpsEnabled())), ((server.getHttpsAddress()) == null)));
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}

