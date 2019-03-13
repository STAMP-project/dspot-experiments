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


import DrillProperties.KERBEROS_FROM_SUBJECT;
import DrillProperties.KEYTAB;
import DrillProperties.PASSWORD;
import DrillProperties.SASL_ENCRYPT;
import DrillProperties.SERVICE_PRINCIPAL;
import DrillProperties.TEST_SASL_LEVEL;
import DrillProperties.USER;
import ExecConstants.AUTHENTICATION_MECHANISMS;
import ExecConstants.BIT_AUTHENTICATION_ENABLED;
import ExecConstants.BIT_AUTHENTICATION_MECHANISM;
import ExecConstants.BIT_ENCRYPTION_SASL_ENABLED;
import ExecConstants.BIT_ENCRYPTION_SASL_MAX_WRAPPED_SIZE;
import ExecConstants.SERVICE_KEYTAB_LOCATION;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import ExecConstants.USER_AUTHENTICATOR_IMPL;
import ExecConstants.USER_ENCRYPTION_SASL_ENABLED;
import ExecConstants.USER_ENCRYPTION_SASL_MAX_WRAPPED_SIZE;
import ExecConstants.USE_LOGIN_PRINCIPAL;
import com.typesafe.config.ConfigValueFactory;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import javax.security.auth.Subject;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.rpc.NonTransientRpcException;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.control.ControlRpcMetrics;
import org.apache.drill.exec.rpc.data.DataRpcMetrics;
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.rpc.user.UserRpcMetrics;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestUserBitKerberosEncryption extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestUserBitKerberosEncryption.class);

    private static KerberosHelper krbHelper;

    private static DrillConfig newConfig;

    @Test
    public void successKeytabWithoutChunking() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json`");
    }

    /**
     * Test connection counter values for both encrypted and unencrypted connections over all Drillbit channels.
     * Encryption is enabled only for UserRpc NOT for ControlRpc and DataRpc. Test validates corresponding connection
     * count for each channel.
     * For example: There is only 1 DrillClient so encrypted connection count of UserRpcMetrics will be 1. Before
     * running any query there should not be any connection (control or data) between Drillbits, hence those counters
     * are 0. After running a simple query since there is only 1 fragment which is root fragment the Control Connection
     * count is 0 (for unencrypted counter) since with DRILL-5721 status update of fragment to Foreman happens locally.
     * There is no Data Connection because there is no data exchange between multiple fragments.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionCounters() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        TestCase.assertTrue(((UserRpcMetrics.getInstance().getEncryptedConnectionCount()) == 1));
        TestCase.assertTrue(((UserRpcMetrics.getInstance().getUnEncryptedConnectionCount()) == 0));
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        // Check encrypted counters value
        TestCase.assertTrue((1 == (UserRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getEncryptedConnectionCount())));
        // Check unencrypted counters value
        TestCase.assertTrue((0 == (UserRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
    }

    @Test
    public void successTicketWithoutChunking() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(KERBEROS_FROM_SUBJECT, "true");
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsoluteFile());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        Subject.doAs(clientSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
                return null;
            }
        });
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    @Test
    public void successKeytabWithChunking() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_ENCRYPTION_SASL_MAX_WRAPPED_SIZE, ConfigValueFactory.fromAnyRef(100)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json`");
    }

    @Test
    public void successKeytabWithChunkingDefaultChunkSize() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    /**
     * This test will not cover the data channel since we are using only 1 Drillbit and the query doesn't involve
     *  any exchange operator. But Data Channel encryption testing is covered separately in
     *  {@link org.apache.drill.exec.rpc.data.TestBitBitKerberos}
     */
    @Test
    public void successEncryptionAllChannelChunkMode() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_ENCRYPTION_SASL_MAX_WRAPPED_SIZE, ConfigValueFactory.fromAnyRef(10000)).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_ENCRYPTION_SASL_MAX_WRAPPED_SIZE, ConfigValueFactory.fromAnyRef(10000)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    /**
     * This test will not cover the data channel since we are using only 1 Drillbit and the query doesn't involve
     *  any exchange operator. But Data Channel encryption testing is covered separately in
     *  {@link org.apache.drill.exec.rpc.data.TestBitBitKerberos}
     */
    @Test
    public void successEncryptionAllChannel() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    /**
     * Test connection counter values for both encrypted and unencrypted connections over all Drillbit channels.
     * Encryption is enabled for UserRpc, ControlRpc and DataRpc. Test validates corresponding connection
     * count for each channel.
     * For example: There is only 1 DrillClient so encrypted connection count of UserRpcMetrics
     * will be 1. Before running any query there should not be any connection (control or data) between Drillbits,
     * hence those counters are 0. After running a simple query since there is only 1 fragment which is root fragment
     * the Control Connection count is 0 (for encrypted counter), since with DRILL-5721 status update of fragment to
     * Foreman happens locally. There is no Data Connection because there is no data exchange between multiple fragments.
     */
    @Test
    public void testEncryptedConnectionCountersAllChannel() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberosEncryption.krbHelper.CLIENT_SHORT_NAME).go();
        // Check encrypted counters value
        TestCase.assertTrue((1 == (UserRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getEncryptedConnectionCount())));
        // Check unencrypted counters value
        TestCase.assertTrue((0 == (UserRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
    }

    @Test
    public void failurePlainMech() {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(USER, "anonymous");
            connectionProps.setProperty(PASSWORD, "anything works!");
            TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
            BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            assert (ex.getCause()) instanceof NonTransientRpcException;
            TestUserBitKerberosEncryption.logger.error("Caught exception: ", ex);
        }
    }

    @Test
    public void encryptionEnabledWithOnlyPlainMech() {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
            connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
            connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
            TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
            BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            assert (ex.getCause()) instanceof NonTransientRpcException;
            TestUserBitKerberosEncryption.logger.error("Caught exception: ", ex);
        }
    }

    /**
     * Test to validate that older clients are not allowed to connect to secure cluster
     * with encryption enabled.
     */
    @Test
    public void failureOldClientEncryptionEnabled() {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
            connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
            connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
            connectionProps.setProperty(TEST_SASL_LEVEL, "1");
            TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
            BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            assert (ex.getCause()) instanceof RpcException;
            TestUserBitKerberosEncryption.logger.error("Caught exception: ", ex);
        }
    }

    /**
     * Test to validate that older clients are successfully connecting to secure cluster
     * with encryption disabled.
     */
    @Test
    public void successOldClientEncryptionDisabled() {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
        connectionProps.setProperty(TEST_SASL_LEVEL, "1");
        TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))));
        BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
    }

    /**
     * Test to validate that clients which needs encrypted connection fails to connect
     * to server with encryption disabled.
     */
    @Test
    public void clientNeedsEncryptionWithNoServerSupport() throws Exception {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
            connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
            connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
            connectionProps.setProperty(SASL_ENCRYPT, "true");
            TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))));
            BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
            TestCase.fail();
        } catch (Exception ex) {
            assert (ex.getCause()) instanceof NonTransientRpcException;
        }
    }

    /**
     * Test to validate that clients which needs encrypted connection connects
     * to server with encryption enabled.
     */
    @Test
    public void clientNeedsEncryptionWithServerSupport() throws Exception {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL);
            connectionProps.setProperty(USER, TestUserBitKerberosEncryption.krbHelper.CLIENT_PRINCIPAL);
            connectionProps.setProperty(KEYTAB, TestUserBitKerberosEncryption.krbHelper.clientKeytab.getAbsolutePath());
            connectionProps.setProperty(SASL_ENCRYPT, "true");
            TestUserBitKerberosEncryption.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(ExecConstants.SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestUserBitKerberosEncryption.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(USER_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)));
            BaseTestQuery.updateTestCluster(1, TestUserBitKerberosEncryption.newConfig, connectionProps);
        } catch (Exception ex) {
            TestCase.fail();
            assert (ex.getCause()) instanceof NonTransientRpcException;
        }
    }
}

