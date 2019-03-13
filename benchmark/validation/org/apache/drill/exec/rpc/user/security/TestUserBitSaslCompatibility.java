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


import DrillProperties.AUTH_MECHANISM;
import DrillProperties.PASSWORD;
import DrillProperties.SASL_ENCRYPT;
import DrillProperties.USER;
import ExecConstants.AUTHENTICATION_MECHANISMS;
import ExecConstants.IMPERSONATION_ENABLED;
import ExecConstants.IMPERSONATION_MAX_CHAINED_USER_HOPS;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import ExecConstants.USER_AUTHENTICATOR_IMPL;
import ExecConstants.USER_ENCRYPTION_SASL_ENABLED;
import com.typesafe.config.ConfigValueFactory;
import java.util.Properties;
import javax.security.sasl.SaslException;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.rpc.NonTransientRpcException;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Helps to test different scenarios based on security configuration on client and Drillbit side with respect to SASL
 * and specifically using PLAIN mechanism
 */
@Category({ SecurityTest.class })
public class TestUserBitSaslCompatibility extends BaseTestQuery {
    /**
     * Test showing when Drillbit is not configured for authentication whereas client explicitly requested for PLAIN
     * authentication then connection succeeds without authentication.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableDrillbitAuth_EnableClientAuthPlain() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
        } catch (Exception ex) {
            TestCase.fail();
        }
    }

    /**
     * Test showing when Drillbit is not configured for authentication whereas client explicitly requested for Kerberos
     * authentication then connection fails due to new check before SASL Handshake.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableDrillbitAuth_EnableClientAuthKerberos() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(AUTH_MECHANISM, "kerberos");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue((!((ex.getCause().getCause()) instanceof SaslException)));
        }
    }

    /**
     * Test showing failure before SASL handshake when Drillbit is not configured for authentication whereas client
     * explicitly requested for encrypted connection.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableDrillbitAuth_EnableClientEncryption() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        connectionProps.setProperty(SASL_ENCRYPT, "true");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue((!((ex.getCause().getCause()) instanceof SaslException)));
        }
    }

    /**
     * Test showing failure before SASL handshake when Drillbit is not configured for encryption whereas client explicitly
     * requested for encrypted connection.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableDrillbitEncryption_EnableClientEncryption() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        connectionProps.setProperty(SASL_ENCRYPT, "true");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue((!((ex.getCause().getCause()) instanceof SaslException)));
        }
    }

    /**
     * Test showing failure in SASL handshake when Drillbit is configured for authentication only whereas client doesn't
     * provide any security properties like username/password in this case.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDrillbitAuth_DisableClientAuth() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue(((ex.getCause().getCause()) instanceof SaslException));
        }
    }

    /**
     * Test showing failure in SASL handshake when Drillbit is configured for encryption whereas client doesn't provide any
     * security properties like username/password in this case.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDrillbitEncryption_DisableClientAuth() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue(((ex.getCause().getCause()) instanceof SaslException));
        }
    }

    /**
     * Test showing successful SASL handshake when both Drillbit and client side authentication is enabled using PLAIN
     * mechanism.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDrillbitClientAuth() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
        } catch (Exception ex) {
            TestCase.fail();
        }
    }

    /**
     * Below test shows the failure in Sasl layer with client and Drillbit side encryption enabled using PLAIN
     * mechanism. This is expected since PLAIN mechanism doesn't support encryption using SASL. Whereas same test
     * setup using Kerberos or any other mechanism with encryption support will result in successful SASL handshake.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDrillbitClientEncryption_UsingPlain() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        connectionProps.setProperty(PASSWORD, "anything works!");
        connectionProps.setProperty(SASL_ENCRYPT, "true");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            TestCase.assertTrue(((ex.getCause()) instanceof NonTransientRpcException));
            TestCase.assertTrue(((ex.getCause().getCause()) instanceof SaslException));
        }
    }

    /**
     * Test showing successful handshake when authentication is disabled on Drillbit side and client also
     * doesn't provide any security properties in connection URL.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableDrillbitClientAuth() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(false)));
        final Properties connectionProps = new Properties();
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
        } catch (Exception ex) {
            TestCase.fail();
        }
    }

    /**
     * Test showing successful handshake when authentication is disabled but impersonation is enabled on Drillbit side
     * and client only provides USERNAME as a security property in connection URL.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDrillbitImpersonation_DisableClientAuth() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(false)).withValue(IMPERSONATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(IMPERSONATION_MAX_CHAINED_USER_HOPS, ConfigValueFactory.fromAnyRef(3)));
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, "anonymous");
        try {
            BaseTestQuery.updateTestCluster(1, newConfig, connectionProps);
        } catch (Exception ex) {
            TestCase.fail();
        }
    }
}

