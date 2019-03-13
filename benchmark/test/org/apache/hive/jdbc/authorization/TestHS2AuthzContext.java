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
package org.apache.hive.jdbc.authorization;


import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizer;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizerFactory;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzSessionContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveMetastoreClientFactory;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test context information that gets passed to authorization api
 */
public class TestHS2AuthzContext {
    private static MiniHS2 miniHS2 = null;

    static HiveAuthorizer mockedAuthorizer;

    static HiveAuthenticationProvider authenticator;

    /**
     * This factory creates a mocked HiveAuthorizer class.
     * Use the mocked class to capture the argument passed to it in the test case.
     */
    static class MockedHiveAuthorizerFactory implements HiveAuthorizerFactory {
        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) {
            TestHS2AuthzContext.mockedAuthorizer = Mockito.mock(HiveAuthorizer.class);
            TestHS2AuthzContext.authenticator = authenticator;
            return TestHS2AuthzContext.mockedAuthorizer;
        }
    }

    @Test
    public void testAuthzContextContentsDriverCmd() throws Exception {
        String cmd = "show tables";
        verifyContextContents(cmd, cmd);
    }

    @Test
    public void testAuthzContextContentsCmdProcessorCmd() throws Exception {
        verifyContextContents("dfs -ls /", "-ls /");
    }
}

