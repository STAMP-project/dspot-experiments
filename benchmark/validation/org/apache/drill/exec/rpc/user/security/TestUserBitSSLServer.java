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
package org.apache.drill.exec.rpc.user.security;


import ExecConstants.SSL_KEYSTORE_PASSWORD;
import ExecConstants.SSL_KEYSTORE_PATH;
import ExecConstants.SSL_KEY_PASSWORD;
import com.typesafe.config.ConfigValueFactory;
import java.util.Properties;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTest.class })
public class TestUserBitSSLServer extends BaseTestQuery {
    private static DrillConfig sslConfig;

    private static Properties initProps;// initial client properties


    private static ClassLoader classLoader;

    private static String ksPath;

    private static String tsPath;

    @Test
    public void testInvalidKeystorePath() throws Exception {
        DrillConfig testConfig = new DrillConfig(DrillConfig.create(TestUserBitSSLServer.sslConfig).withValue(SSL_KEYSTORE_PATH, ConfigValueFactory.fromAnyRef("/bad/path")));
        // Start an SSL enabled cluster
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateTestCluster(1, testConfig, TestUserBitSSLServer.initProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testInvalidKeystorePassword() throws Exception {
        DrillConfig testConfig = new DrillConfig(DrillConfig.create(TestUserBitSSLServer.sslConfig).withValue(SSL_KEYSTORE_PASSWORD, ConfigValueFactory.fromAnyRef("badpassword")));
        // Start an SSL enabled cluster
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateTestCluster(1, testConfig, TestUserBitSSLServer.initProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testInvalidKeyPassword() throws Exception {
        DrillConfig testConfig = new DrillConfig(DrillConfig.create(TestUserBitSSLServer.sslConfig).withValue(SSL_KEY_PASSWORD, ConfigValueFactory.fromAnyRef("badpassword")));
        // Start an SSL enabled cluster
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateTestCluster(1, testConfig, TestUserBitSSLServer.initProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    // Should pass because the keystore password will be used.
    @Test
    public void testNoKeyPassword() throws Exception {
        DrillConfig testConfig = new DrillConfig(DrillConfig.create(TestUserBitSSLServer.sslConfig).withValue(SSL_KEY_PASSWORD, ConfigValueFactory.fromAnyRef("")));
        // Start an SSL enabled cluster
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateTestCluster(1, testConfig, TestUserBitSSLServer.initProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, false);
    }
}

