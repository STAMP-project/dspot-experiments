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


import HiveAuthzSessionContext.CLIENT_TYPE.HIVECLI;
import org.apache.hadoop.hive.cli.CliDriver;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizer;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizerFactory;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzSessionContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveMetastoreClientFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test context information that gets passed to authorization factory
 */
public class TestCLIAuthzSessionContext {
    private static HiveAuthzSessionContext sessionCtx;

    private static CliDriver driver;

    /**
     * This factory captures the HiveAuthzSessionContext argument and returns mocked
     * HiveAuthorizer class
     */
    static class MockedHiveAuthorizerFactory implements HiveAuthorizerFactory {
        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) {
            TestCLIAuthzSessionContext.sessionCtx = ctx;
            HiveAuthorizer mockedAuthorizer = Mockito.mock(HiveAuthorizer.class);
            return mockedAuthorizer;
        }
    }

    @Test
    public void testAuthzSessionContextContents() throws Exception {
        TestCLIAuthzSessionContext.driver.processCmd("show tables");
        // session string is supposed to be unique, so its got to be of some reasonable size
        Assert.assertTrue("session string size check", ((TestCLIAuthzSessionContext.sessionCtx.getSessionString().length()) > 10));
        Assert.assertEquals("Client type ", HIVECLI, TestCLIAuthzSessionContext.sessionCtx.getClientType());
    }
}

