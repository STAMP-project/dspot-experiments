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
package org.apache.hadoop.registry.secure;


import CreateMode.PERSISTENT;
import RegistrySecurity.WorldReadWriteACL;
import ZooKeeperSaslServer.DEFAULT_LOGIN_CONTEXT_NAME;
import ZooKeeperSaslServer.LOGIN_CONTEXT_NAME_KEY;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import org.apache.hadoop.registry.RegistryTestHelper;
import org.apache.hadoop.registry.client.impl.zk.CuratorService;
import org.apache.hadoop.registry.client.impl.zk.RegistrySecurity;
import org.apache.hadoop.service.ServiceOperations;
import org.apache.zookeeper.Login;
import org.apache.zookeeper.server.auth.SaslServerCallbackHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify that the Mini ZK service can be started up securely
 */
public class TestSecureRegistry extends AbstractSecureRegistryTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestSecureRegistry.class);

    /**
     * this is a cut and paste of some of the ZK internal code that was
     * failing on windows and swallowing its exceptions
     */
    @Test
    public void testLowlevelZKSaslLogin() throws Throwable {
        RegistrySecurity.bindZKToServerJAASContext(AbstractSecureRegistryTest.ZOOKEEPER_SERVER_CONTEXT);
        String serverSection = System.getProperty(LOGIN_CONTEXT_NAME_KEY, DEFAULT_LOGIN_CONTEXT_NAME);
        Assert.assertEquals(AbstractSecureRegistryTest.ZOOKEEPER_SERVER_CONTEXT, serverSection);
        AppConfigurationEntry[] entries;
        entries = Configuration.getConfiguration().getAppConfigurationEntry(serverSection);
        Assert.assertNotNull("null entries", entries);
        SaslServerCallbackHandler saslServerCallbackHandler = new SaslServerCallbackHandler(Configuration.getConfiguration());
        Login login = new Login(serverSection, saslServerCallbackHandler);
        try {
            login.startThreadIfNeeded();
        } finally {
            login.shutdown();
        }
    }

    @Test
    public void testCreateSecureZK() throws Throwable {
        startSecureZK();
        secureZK.stop();
    }

    @Test
    public void testInsecureClientToZK() throws Throwable {
        startSecureZK();
        userZookeeperToCreateRoot();
        RegistrySecurity.clearZKSaslClientProperties();
        CuratorService curatorService = startCuratorServiceInstance("insecure client", false);
        curatorService.zkList("/");
        curatorService.zkMkPath("", PERSISTENT, false, WorldReadWriteACL);
    }

    /**
     * test that ZK can write as itself
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testZookeeperCanWrite() throws Throwable {
        System.setProperty("curator-log-events", "true");
        startSecureZK();
        CuratorService curator = null;
        LoginContext login = login(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST, AbstractSecureRegistryTest.ZOOKEEPER_CLIENT_CONTEXT, AbstractSecureRegistryTest.keytab_zk);
        try {
            RegistryTestHelper.logLoginDetails(AbstractSecureRegistryTest.ZOOKEEPER, login);
            RegistrySecurity.setZKSaslClientProperties(AbstractSecureRegistryTest.ZOOKEEPER, AbstractSecureRegistryTest.ZOOKEEPER_CLIENT_CONTEXT);
            curator = startCuratorServiceInstance("ZK", true);
            TestSecureRegistry.LOG.info(curator.toString());
            addToTeardown(curator);
            curator.zkMkPath("/", PERSISTENT, false, WorldReadWriteACL);
            curator.zkList("/");
            curator.zkMkPath("/zookeeper", PERSISTENT, false, WorldReadWriteACL);
        } finally {
            RegistryTestHelper.logout(login);
            ServiceOperations.stop(curator);
        }
    }

    @Test
    public void testSystemPropertyOverwrite() {
        System.setProperty(PROP_ZK_SASL_CLIENT_USERNAME, "");
        System.setProperty(PROP_ZK_SASL_CLIENT_CONTEXT, "");
        RegistrySecurity.setZKSaslClientProperties(AbstractSecureRegistryTest.ZOOKEEPER, AbstractSecureRegistryTest.ZOOKEEPER_CLIENT_CONTEXT);
        Assert.assertEquals(AbstractSecureRegistryTest.ZOOKEEPER, System.getProperty(PROP_ZK_SASL_CLIENT_USERNAME));
        Assert.assertEquals(AbstractSecureRegistryTest.ZOOKEEPER_CLIENT_CONTEXT, System.getProperty(PROP_ZK_SASL_CLIENT_CONTEXT));
        String userName = "user1";
        String context = "context1";
        System.setProperty(PROP_ZK_SASL_CLIENT_USERNAME, userName);
        System.setProperty(PROP_ZK_SASL_CLIENT_CONTEXT, context);
        RegistrySecurity.setZKSaslClientProperties(AbstractSecureRegistryTest.ZOOKEEPER, AbstractSecureRegistryTest.ZOOKEEPER_CLIENT_CONTEXT);
        Assert.assertEquals(userName, System.getProperty(PROP_ZK_SASL_CLIENT_USERNAME));
        Assert.assertEquals(context, System.getProperty(PROP_ZK_SASL_CLIENT_CONTEXT));
    }
}

