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


import DrillProperties.DISABLE_CERT_VERIFICATION;
import DrillProperties.DISABLE_HOST_VERIFICATION;
import DrillProperties.ENABLE_TLS;
import DrillProperties.TRUSTSTORE_PASSWORD;
import DrillProperties.TRUSTSTORE_PATH;
import ExecConstants.SSL_KEYSTORE_PASSWORD;
import ExecConstants.SSL_KEYSTORE_PATH;
import ExecConstants.SSL_KEYSTORE_TYPE;
import ExecConstants.SSL_PROTOCOL;
import ExecConstants.USER_SSL_ENABLED;
import com.typesafe.config.ConfigValueFactory;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestUserBitSSL extends BaseTestQuery {
    private static DrillConfig newConfig;

    private static Properties initProps;// initial client properties


    private static ClassLoader classLoader;

    private static String ksPath;

    private static String tsPath;

    private static String emptyTSPath;

    private static String unknownKsPath;

    @Test
    public void testSSLConnection() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            TestCase.fail(new StringBuilder().append("SSL Connection failed with exception [").append(e.getMessage()).append("]").toString());
        }
    }

    @Test
    public void testSSLConnectionWithKeystore() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.ksPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            TestCase.fail(new StringBuilder().append("SSL Connection failed with exception [").append(e.getMessage()).append("]").toString());
        }
    }

    @Test
    public void testSSLConnectionFailBadTrustStore() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, "");// NO truststore

        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testSSLConnectionFailBadPassword() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "bad_password");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testSSLConnectionFailEmptyTrustStore() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.emptyTSPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testSSLQuery() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            TestCase.fail(new StringBuilder().append("SSL Connection failed with exception [").append(e.getMessage()).append("]").toString());
        }
        BaseTestQuery.test("SELECT * FROM cp.`region.json`");
    }

    @Test
    public void testClientConfigHostNameVerificationFail() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(ENABLE_TLS, "true");
        connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
        connectionProps.setProperty(TRUSTSTORE_PASSWORD, "password");
        connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "false");
        boolean failureCaught = false;
        try {
            BaseTestQuery.updateClient(connectionProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testClientConfigCertificateVerification() {
        // Fail if certificate is not valid
        boolean failureCaught = false;
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(ENABLE_TLS, "true");
            connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
            connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
            connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
            // connectionProps.setProperty(DrillProperties.DISABLE_CERT_VERIFICATION, "true");
            DrillConfig sslConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_SSL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(SSL_KEYSTORE_TYPE, ConfigValueFactory.fromAnyRef("JKS")).withValue(SSL_KEYSTORE_PATH, ConfigValueFactory.fromAnyRef(TestUserBitSSL.unknownKsPath)).withValue(SSL_KEYSTORE_PASSWORD, ConfigValueFactory.fromAnyRef("drill123")).withValue(SSL_PROTOCOL, ConfigValueFactory.fromAnyRef("TLSv1.2")));
            BaseTestQuery.updateTestCluster(1, sslConfig, connectionProps);
        } catch (Exception e) {
            failureCaught = true;
        }
        // reset cluster
        BaseTestQuery.updateTestCluster(1, TestUserBitSSL.newConfig, TestUserBitSSL.initProps);
        Assert.assertEquals(failureCaught, true);
    }

    @Test
    public void testClientConfigNoCertificateVerification() {
        // Pass if certificate is not valid, but mode is insecure.
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(ENABLE_TLS, "true");
            connectionProps.setProperty(TRUSTSTORE_PATH, TestUserBitSSL.tsPath);
            connectionProps.setProperty(TRUSTSTORE_PASSWORD, "drill123");
            connectionProps.setProperty(DISABLE_HOST_VERIFICATION, "true");
            connectionProps.setProperty(DISABLE_CERT_VERIFICATION, "true");
            DrillConfig sslConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_SSL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(SSL_KEYSTORE_TYPE, ConfigValueFactory.fromAnyRef("JKS")).withValue(SSL_KEYSTORE_PATH, ConfigValueFactory.fromAnyRef(TestUserBitSSL.unknownKsPath)).withValue(SSL_KEYSTORE_PASSWORD, ConfigValueFactory.fromAnyRef("drill123")).withValue(SSL_PROTOCOL, ConfigValueFactory.fromAnyRef("TLSv1.2")));
            BaseTestQuery.updateTestCluster(1, sslConfig, connectionProps);
        } catch (Exception e) {
            TestCase.fail(e.getMessage());
        }
        // reset cluster
        BaseTestQuery.updateTestCluster(1, TestUserBitSSL.newConfig, TestUserBitSSL.initProps);
    }
}

