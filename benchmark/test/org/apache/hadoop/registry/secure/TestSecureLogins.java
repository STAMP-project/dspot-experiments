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


import Environment.JAAS_CONF_KEY;
import RegistrySecurity.UgiInfo;
import ZookeeperConfigOptions.SCHEME_SASL;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.registry.RegistryTestHelper;
import org.apache.hadoop.registry.client.impl.zk.RegistrySecurity;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.apache.zookeeper.Environment;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify that logins work
 */
public class TestSecureLogins extends AbstractSecureRegistryTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestSecureLogins.class);

    @Test
    public void testHasRealm() throws Throwable {
        Assert.assertNotNull(AbstractSecureRegistryTest.getRealm());
        TestSecureLogins.LOG.info("ZK principal = {}", AbstractSecureRegistryTest.getPrincipalAndRealm(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST));
    }

    @Test
    public void testJaasFileSetup() throws Throwable {
        // the JVM has seemed inconsistent on setting up here
        Assert.assertNotNull("jaasFile", AbstractSecureRegistryTest.jaasFile);
        String confFilename = System.getProperty(JAAS_CONF_KEY);
        Assert.assertEquals(AbstractSecureRegistryTest.jaasFile.getAbsolutePath(), confFilename);
    }

    @Test
    public void testJaasFileBinding() throws Throwable {
        // the JVM has seemed inconsistent on setting up here
        Assert.assertNotNull("jaasFile", AbstractSecureRegistryTest.jaasFile);
        RegistrySecurity.bindJVMtoJAASFile(AbstractSecureRegistryTest.jaasFile);
        String confFilename = System.getProperty(JAAS_CONF_KEY);
        Assert.assertEquals(AbstractSecureRegistryTest.jaasFile.getAbsolutePath(), confFilename);
    }

    @Test
    public void testClientLogin() throws Throwable {
        LoginContext client = login(AbstractSecureRegistryTest.ALICE_LOCALHOST, AbstractSecureRegistryTest.ALICE_CLIENT_CONTEXT, AbstractSecureRegistryTest.keytab_alice);
        try {
            RegistryTestHelper.logLoginDetails(AbstractSecureRegistryTest.ALICE_LOCALHOST, client);
            String confFilename = System.getProperty(JAAS_CONF_KEY);
            Assert.assertNotNull(("Unset: " + (Environment.JAAS_CONF_KEY)), confFilename);
            String config = FileUtils.readFileToString(new File(confFilename), Charset.defaultCharset());
            TestSecureLogins.LOG.info("{}=\n{}", confFilename, config);
            RegistrySecurity.setZKSaslClientProperties(AbstractSecureRegistryTest.ALICE, AbstractSecureRegistryTest.ALICE_CLIENT_CONTEXT);
        } finally {
            client.logout();
        }
    }

    @Test
    public void testZKServerContextLogin() throws Throwable {
        LoginContext client = login(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST, AbstractSecureRegistryTest.ZOOKEEPER_SERVER_CONTEXT, AbstractSecureRegistryTest.keytab_zk);
        RegistryTestHelper.logLoginDetails(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST, client);
        client.logout();
    }

    @Test
    public void testServerLogin() throws Throwable {
        LoginContext loginContext = createLoginContextZookeeperLocalhost();
        loginContext.login();
        loginContext.logout();
    }

    @Test
    public void testKerberosAuth() throws Throwable {
        File krb5conf = AbstractSecureRegistryTest.getKdc().getKrb5conf();
        String krbConfig = FileUtils.readFileToString(krb5conf, Charset.defaultCharset());
        TestSecureLogins.LOG.info("krb5.conf at {}:\n{}", krb5conf, krbConfig);
        Subject subject = new Subject();
        Class<?> kerb5LoginClass = Class.forName(KerberosUtil.getKrb5LoginModuleName());
        Constructor<?> kerb5LoginConstr = kerb5LoginClass.getConstructor();
        Object kerb5LoginObject = kerb5LoginConstr.newInstance();
        final Map<String, String> options = new HashMap<String, String>();
        options.put("debug", "true");
        if (IBM_JAVA) {
            options.put("useKeytab", (AbstractSecureRegistryTest.keytab_alice.getAbsolutePath().startsWith("file://") ? AbstractSecureRegistryTest.keytab_alice.getAbsolutePath() : "file://" + (AbstractSecureRegistryTest.keytab_alice.getAbsolutePath())));
            options.put("principal", AbstractSecureRegistryTest.ALICE_LOCALHOST);
            options.put("refreshKrb5Config", "true");
            options.put("credsType", "both");
            String ticketCache = System.getenv("KRB5CCNAME");
            if (ticketCache != null) {
                // IBM JAVA only respect system property and not env variable
                // The first value searched when "useDefaultCcache" is used.
                System.setProperty("KRB5CCNAME", ticketCache);
                options.put("useDefaultCcache", "true");
                options.put("renewTGT", "true");
            }
        } else {
            options.put("keyTab", AbstractSecureRegistryTest.keytab_alice.getAbsolutePath());
            options.put("principal", AbstractSecureRegistryTest.ALICE_LOCALHOST);
            options.put("doNotPrompt", "true");
            options.put("isInitiator", "true");
            options.put("refreshKrb5Config", "true");
            options.put("renewTGT", "true");
            options.put("storeKey", "true");
            options.put("useKeyTab", "true");
            options.put("useTicketCache", "true");
        }
        Method methodInitialize = kerb5LoginObject.getClass().getMethod("initialize", Subject.class, CallbackHandler.class, Map.class, Map.class);
        methodInitialize.invoke(kerb5LoginObject, subject, null, new HashMap<String, String>(), options);
        Method methodLogin = kerb5LoginObject.getClass().getMethod("login");
        boolean loginOk = ((Boolean) (methodLogin.invoke(kerb5LoginObject)));
        Assert.assertTrue("Failed to login", loginOk);
        Method methodCommit = kerb5LoginObject.getClass().getMethod("commit");
        boolean commitOk = ((Boolean) (methodCommit.invoke(kerb5LoginObject)));
        Assert.assertTrue("Failed to Commit", commitOk);
    }

    @Test
    public void testDefaultRealmValid() throws Throwable {
        String defaultRealm = KerberosUtil.getDefaultRealm();
        RegistryTestHelper.assertNotEmpty("No default Kerberos Realm", defaultRealm);
        TestSecureLogins.LOG.info("Default Realm '{}'", defaultRealm);
    }

    @Test
    public void testKerberosRulesValid() throws Throwable {
        Assert.assertTrue("!KerberosName.hasRulesBeenSet()", KerberosName.hasRulesBeenSet());
        String rules = KerberosName.getRules();
        Assert.assertEquals(AbstractSecureRegistryTest.kerberosRule, rules);
        TestSecureLogins.LOG.info(rules);
    }

    @Test
    public void testValidKerberosName() throws Throwable {
        KerberosName.setRuleMechanism(MECHANISM_HADOOP);
        new HadoopKerberosName(AbstractSecureRegistryTest.ZOOKEEPER).getShortName();
        // MECHANISM_MIT allows '/' and '@' in username
        KerberosName.setRuleMechanism(MECHANISM_MIT);
        new HadoopKerberosName(AbstractSecureRegistryTest.ZOOKEEPER).getShortName();
        new HadoopKerberosName(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST).getShortName();
        new HadoopKerberosName(AbstractSecureRegistryTest.ZOOKEEPER_REALM).getShortName();
        new HadoopKerberosName(AbstractSecureRegistryTest.ZOOKEEPER_LOCALHOST_REALM).getShortName();
        KerberosName.setRuleMechanism(DEFAULT_MECHANISM);
    }

    @Test
    public void testUGILogin() throws Throwable {
        UserGroupInformation ugi = RegistryTestHelper.loginUGI(AbstractSecureRegistryTest.ZOOKEEPER, AbstractSecureRegistryTest.keytab_zk);
        RegistrySecurity.UgiInfo ugiInfo = new RegistrySecurity.UgiInfo(ugi);
        TestSecureLogins.LOG.info("logged in as: {}", ugiInfo);
        Assert.assertTrue(("security is not enabled: " + ugiInfo), UserGroupInformation.isSecurityEnabled());
        Assert.assertTrue(("login is keytab based: " + ugiInfo), ugi.isFromKeytab());
        // now we are here, build a SASL ACL
        ACL acl = ugi.doAs(new PrivilegedExceptionAction<ACL>() {
            @Override
            public ACL run() throws Exception {
                return AbstractSecureRegistryTest.registrySecurity.createSaslACLFromCurrentUser(0);
            }
        });
        Assert.assertEquals(AbstractSecureRegistryTest.ZOOKEEPER_REALM, acl.getId().getId());
        Assert.assertEquals(SCHEME_SASL, acl.getId().getScheme());
        AbstractSecureRegistryTest.registrySecurity.addSystemACL(acl);
    }
}

