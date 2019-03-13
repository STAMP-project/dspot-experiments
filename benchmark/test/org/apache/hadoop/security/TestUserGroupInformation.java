/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.security;


import AuthMethod.KERBEROS;
import AuthenticationMethod.PROXY;
import AuthenticationMethod.SIMPLE;
import AuthenticationMethod.TOKEN;
import CommonConfigurationKeysPublic.HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN;
import CommonConfigurationKeysPublic.HADOOP_TOKENS;
import CommonConfigurationKeysPublic.HADOOP_TOKEN_FILES;
import Shell.TOKEN_SEPARATOR_REGEX;
import UserGroupInformation.AutoRenewalForUserCredsRunnable;
import UserGroupInformation.LOG;
import UserGroupInformation.metrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.ipc.TestRpcBase;
import org.apache.hadoop.security.UserGroupInformation.AuthenticationMethod;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestUserGroupInformation {
    static final Logger LOG = LoggerFactory.getLogger(TestUserGroupInformation.class);

    private static final String USER_NAME = "user1@HADOOP.APACHE.ORG";

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String GROUP3_NAME = "group3";

    private static final String[] GROUP_NAMES = new String[]{ TestUserGroupInformation.GROUP1_NAME, TestUserGroupInformation.GROUP2_NAME, TestUserGroupInformation.GROUP3_NAME };

    // Rollover interval of percentile metrics (in seconds)
    private static final int PERCENTILES_INTERVAL = 1;

    private static Configuration conf;

    /**
     * UGI should not use the default security conf, else it will collide
     * with other classes that may change the default conf.  Using this dummy
     * class that simply throws an exception will ensure that the tests fail
     * if UGI uses the static default config instead of its own config
     */
    private static class DummyLoginConfiguration extends javax.security.auth.login.Configuration {
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            throw new RuntimeException("UGI is not using its own security conf!");
        }
    }

    // must be set immediately to avoid inconsistent testing issues.
    static {
        // fake the realm is kerberos is enabled
        System.setProperty("java.security.krb5.kdc", "");
        System.setProperty("java.security.krb5.realm", "DEFAULT.REALM");
    }

    @Test(timeout = 30000)
    public void testSimpleLogin() throws IOException {
        tryLoginAuthenticationMethod(SIMPLE, true);
    }

    @Test(timeout = 30000)
    public void testTokenLogin() throws IOException {
        tryLoginAuthenticationMethod(TOKEN, false);
    }

    @Test(timeout = 30000)
    public void testProxyLogin() throws IOException {
        tryLoginAuthenticationMethod(PROXY, false);
    }

    @Test(timeout = 30000)
    public void testGetRealAuthenticationMethod() {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("user1");
        ugi.setAuthenticationMethod(SIMPLE);
        Assert.assertEquals(SIMPLE, ugi.getAuthenticationMethod());
        Assert.assertEquals(SIMPLE, ugi.getRealAuthenticationMethod());
        ugi = UserGroupInformation.createProxyUser("user2", ugi);
        Assert.assertEquals(PROXY, ugi.getAuthenticationMethod());
        Assert.assertEquals(SIMPLE, ugi.getRealAuthenticationMethod());
    }

    @Test(timeout = 30000)
    public void testCreateRemoteUser() {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("user1");
        Assert.assertEquals(SIMPLE, ugi.getAuthenticationMethod());
        Assert.assertTrue(ugi.toString().contains("(auth:SIMPLE)"));
        ugi = UserGroupInformation.createRemoteUser("user1", KERBEROS);
        Assert.assertEquals(AuthenticationMethod.KERBEROS, ugi.getAuthenticationMethod());
        Assert.assertTrue(ugi.toString().contains("(auth:KERBEROS)"));
    }

    /**
     * Test login method
     */
    @Test(timeout = 30000)
    public void testLogin() throws Exception {
        TestUserGroupInformation.conf.set(CommonConfigurationKeys.HADOOP_USER_GROUP_METRICS_PERCENTILES_INTERVALS, String.valueOf(TestUserGroupInformation.PERCENTILES_INTERVAL));
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        // login from unix
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        Assert.assertEquals(UserGroupInformation.getCurrentUser(), UserGroupInformation.getLoginUser());
        Assert.assertTrue(((ugi.getGroupNames().length) >= 1));
        TestUserGroupInformation.verifyGroupMetrics(1);
        // ensure that doAs works correctly
        UserGroupInformation userGroupInfo = UserGroupInformation.createUserForTesting(TestUserGroupInformation.USER_NAME, TestUserGroupInformation.GROUP_NAMES);
        UserGroupInformation curUGI = userGroupInfo.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
            @Override
            public UserGroupInformation run() throws IOException {
                return UserGroupInformation.getCurrentUser();
            }
        });
        // make sure in the scope of the doAs, the right user is current
        Assert.assertEquals(curUGI, userGroupInfo);
        // make sure it is not the same as the login user
        Assert.assertFalse(curUGI.equals(UserGroupInformation.getLoginUser()));
    }

    /**
     * given user name - get all the groups.
     * Needs to happen before creating the test users
     */
    @Test(timeout = 30000)
    public void testGetServerSideGroups() throws IOException, InterruptedException {
        // get the user name
        Process pp = Runtime.getRuntime().exec("whoami");
        BufferedReader br = new BufferedReader(new InputStreamReader(pp.getInputStream()));
        String userName = br.readLine().trim();
        // If on windows domain, token format is DOMAIN\\user and we want to
        // extract only the user name
        if (Shell.WINDOWS) {
            int sp = userName.lastIndexOf('\\');
            if (sp != (-1)) {
                userName = userName.substring((sp + 1));
            }
            // user names are case insensitive on Windows. Make consistent
            userName = StringUtils.toLowerCase(userName);
        }
        // get the groups
        pp = Runtime.getRuntime().exec((Shell.WINDOWS ? (Shell.getWinUtilsPath()) + " groups -F" : "id -Gn " + userName));
        br = new BufferedReader(new InputStreamReader(pp.getInputStream()));
        String line = br.readLine();
        System.out.println(((userName + ":") + line));
        Set<String> groups = new LinkedHashSet<String>();
        String[] tokens = line.split(TOKEN_SEPARATOR_REGEX);
        for (String s : tokens) {
            groups.add(s);
        }
        final UserGroupInformation login = UserGroupInformation.getCurrentUser();
        String loginUserName = login.getShortUserName();
        if (Shell.WINDOWS) {
            // user names are case insensitive on Windows. Make consistent
            loginUserName = StringUtils.toLowerCase(loginUserName);
        }
        Assert.assertEquals(userName, loginUserName);
        String[] gi = login.getGroupNames();
        Assert.assertEquals(groups.size(), gi.length);
        for (int i = 0; i < (gi.length); i++) {
            Assert.assertTrue(groups.contains(gi[i]));
        }
        final UserGroupInformation fakeUser = UserGroupInformation.createRemoteUser("foo.bar");
        fakeUser.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
                UserGroupInformation current = UserGroupInformation.getCurrentUser();
                Assert.assertFalse(current.equals(login));
                Assert.assertEquals(current, fakeUser);
                Assert.assertEquals(0, current.getGroupNames().length);
                return null;
            }
        });
    }

    /**
     * test constructor
     */
    @Test(timeout = 30000)
    public void testConstructor() throws Exception {
        // security off, so default should just return simple name
        testConstructorSuccess("user1", "user1");
        testConstructorSuccess("user2@DEFAULT.REALM", "user2");
        testConstructorSuccess("user3/cron@DEFAULT.REALM", "user3");
        testConstructorSuccess("user4@OTHER.REALM", "user4");
        testConstructorSuccess("user5/cron@OTHER.REALM", "user5");
        // failure test
        testConstructorFailures(null);
        testConstructorFailures("");
    }

    /**
     * test constructor
     */
    @Test(timeout = 30000)
    public void testConstructorWithRules() throws Exception {
        // security off, but use rules if explicitly set
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL, "RULE:[1:$1@$0](.*@OTHER.REALM)s/(.*)@.*/other-$1/");
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "hadoop");
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        testConstructorSuccess("user1", "user1");
        testConstructorSuccess("user4@OTHER.REALM", "other-user4");
        // failure test
        testConstructorFailures("user2@DEFAULT.REALM");
        testConstructorFailures("user3/cron@DEFAULT.REALM");
        testConstructorFailures("user5/cron@OTHER.REALM");
        // with MIT
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "mit");
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        testConstructorSuccess("user2@DEFAULT.REALM", "user2@DEFAULT.REALM");
        testConstructorSuccess("user3/cron@DEFAULT.REALM", "user3/cron@DEFAULT.REALM");
        testConstructorSuccess("user5/cron@OTHER.REALM", "user5/cron@OTHER.REALM");
        // failures
        testConstructorFailures("user6@example.com@OTHER.REALM");
        testConstructorFailures("user7@example.com@DEFAULT.REALM");
        testConstructorFailures(null);
        testConstructorFailures("");
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "hadoop");
    }

    /**
     * test constructor
     */
    @Test(timeout = 30000)
    public void testConstructorWithKerberos() throws Exception {
        // security on, default is remove default realm
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "hadoop");
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, TestUserGroupInformation.conf);
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        testConstructorSuccess("user1", "user1");
        testConstructorSuccess("user2@DEFAULT.REALM", "user2");
        testConstructorSuccess("user3/cron@DEFAULT.REALM", "user3");
        // failure test
        testConstructorFailures("user4@OTHER.REALM");
        testConstructorFailures("user5/cron@OTHER.REALM");
        // with MIT
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "mit");
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        testConstructorSuccess("user4@OTHER.REALM", "user4@OTHER.REALM");
        testConstructorSuccess("user5/cron@OTHER.REALM", "user5/cron@OTHER.REALM");
        // failures
        testConstructorFailures(null);
        testConstructorFailures("");
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL_MECHANISM, "hadoop");
    }

    /**
     * test constructor
     */
    @Test(timeout = 30000)
    public void testConstructorWithKerberosRules() throws Exception {
        // security on, explicit rules
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, TestUserGroupInformation.conf);
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL, ("RULE:[2:$1@$0](.*@OTHER.REALM)s/(.*)@.*/other-$1/" + ("RULE:[1:$1@$0](.*@OTHER.REALM)s/(.*)@.*/other-$1/" + "DEFAULT")));
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        testConstructorSuccess("user1", "user1");
        testConstructorSuccess("user2@DEFAULT.REALM", "user2");
        testConstructorSuccess("user3/cron@DEFAULT.REALM", "user3");
        testConstructorSuccess("user4@OTHER.REALM", "other-user4");
        testConstructorSuccess("user5/cron@OTHER.REALM", "other-user5");
        // failure test
        testConstructorFailures(null);
        testConstructorFailures("");
    }

    @Test(timeout = 30000)
    public void testSetConfigWithRules() {
        String[] rules = new String[]{ "RULE:[1:TEST1]", "RULE:[1:TEST2]", "RULE:[1:TEST3]" };
        // explicitly set a rule
        UserGroupInformation.reset();
        Assert.assertFalse(KerberosName.hasRulesBeenSet());
        KerberosName.setRules(rules[0]);
        Assert.assertTrue(KerberosName.hasRulesBeenSet());
        Assert.assertEquals(rules[0], KerberosName.getRules());
        // implicit init should honor rules already being set
        UserGroupInformation.createUserForTesting("someone", new String[0]);
        Assert.assertEquals(rules[0], KerberosName.getRules());
        // set conf, should override
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL, rules[1]);
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        Assert.assertEquals(rules[1], KerberosName.getRules());
        // set conf, should again override
        TestUserGroupInformation.conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL, rules[2]);
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        Assert.assertEquals(rules[2], KerberosName.getRules());
        // implicit init should honor rules already being set
        UserGroupInformation.createUserForTesting("someone", new String[0]);
        Assert.assertEquals(rules[2], KerberosName.getRules());
    }

    @Test(timeout = 30000)
    public void testEnsureInitWithRules() throws IOException {
        String rules = "RULE:[1:RULE1]";
        // trigger implicit init, rules should init
        UserGroupInformation.reset();
        Assert.assertFalse(KerberosName.hasRulesBeenSet());
        UserGroupInformation.createUserForTesting("someone", new String[0]);
        Assert.assertTrue(KerberosName.hasRulesBeenSet());
        // set a rule, trigger implicit init, rule should not change
        UserGroupInformation.reset();
        KerberosName.setRules(rules);
        Assert.assertTrue(KerberosName.hasRulesBeenSet());
        Assert.assertEquals(rules, KerberosName.getRules());
        UserGroupInformation.createUserForTesting("someone", new String[0]);
        Assert.assertEquals(rules, KerberosName.getRules());
    }

    @Test(timeout = 30000)
    public void testEquals() throws Exception {
        UserGroupInformation uugi = UserGroupInformation.createUserForTesting(TestUserGroupInformation.USER_NAME, TestUserGroupInformation.GROUP_NAMES);
        Assert.assertEquals(uugi, uugi);
        // The subjects should be different, so this should fail
        UserGroupInformation ugi2 = UserGroupInformation.createUserForTesting(TestUserGroupInformation.USER_NAME, TestUserGroupInformation.GROUP_NAMES);
        Assert.assertFalse(uugi.equals(ugi2));
        Assert.assertFalse(((uugi.hashCode()) == (ugi2.hashCode())));
        // two ugi that have the same subject need to be equal
        UserGroupInformation ugi3 = new UserGroupInformation(uugi.getSubject());
        Assert.assertEquals(uugi, ugi3);
        Assert.assertEquals(uugi.hashCode(), ugi3.hashCode());
    }

    @Test(timeout = 30000)
    public void testEqualsWithRealUser() throws Exception {
        UserGroupInformation realUgi1 = UserGroupInformation.createUserForTesting("RealUser", TestUserGroupInformation.GROUP_NAMES);
        UserGroupInformation proxyUgi1 = UserGroupInformation.createProxyUser(TestUserGroupInformation.USER_NAME, realUgi1);
        UserGroupInformation proxyUgi2 = new UserGroupInformation(proxyUgi1.getSubject());
        UserGroupInformation remoteUgi = UserGroupInformation.createRemoteUser(TestUserGroupInformation.USER_NAME);
        Assert.assertEquals(proxyUgi1, proxyUgi2);
        Assert.assertFalse(remoteUgi.equals(proxyUgi1));
    }

    @Test(timeout = 30000)
    public void testGettingGroups() throws Exception {
        UserGroupInformation uugi = UserGroupInformation.createUserForTesting(TestUserGroupInformation.USER_NAME, TestUserGroupInformation.GROUP_NAMES);
        Assert.assertEquals(TestUserGroupInformation.USER_NAME, uugi.getUserName());
        String[] expected = new String[]{ TestUserGroupInformation.GROUP1_NAME, TestUserGroupInformation.GROUP2_NAME, TestUserGroupInformation.GROUP3_NAME };
        Assert.assertArrayEquals(expected, uugi.getGroupNames());
        Assert.assertArrayEquals(expected, uugi.getGroups().toArray(new String[0]));
        Assert.assertEquals(TestUserGroupInformation.GROUP1_NAME, uugi.getPrimaryGroupName());
    }

    // from Mockito mocks
    @SuppressWarnings("unchecked")
    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testAddToken() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Token<T> t1 = Mockito.mock(Token.class);
        Token<T> t2 = Mockito.mock(Token.class);
        Token<T> t3 = Mockito.mock(Token.class);
        // add token to ugi
        ugi.addToken(t1);
        checkTokens(ugi, t1);
        // replace token t1 with t2 - with same key (null)
        ugi.addToken(t2);
        checkTokens(ugi, t2);
        // change t1 service and add token
        Mockito.when(t1.getService()).thenReturn(new Text("t1"));
        ugi.addToken(t1);
        checkTokens(ugi, t1, t2);
        // overwrite t1 token with t3 - same key (!null)
        Mockito.when(t3.getService()).thenReturn(new Text("t1"));
        ugi.addToken(t3);
        checkTokens(ugi, t2, t3);
        // just try to re-add with new name
        Mockito.when(t1.getService()).thenReturn(new Text("t1.1"));
        ugi.addToken(t1);
        checkTokens(ugi, t1, t2, t3);
        // just try to re-add with new name again
        ugi.addToken(t1);
        checkTokens(ugi, t1, t2, t3);
    }

    // from Mockito mocks
    @SuppressWarnings("unchecked")
    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testGetCreds() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Text service = new Text("service");
        Token<T> t1 = Mockito.mock(Token.class);
        Mockito.when(t1.getService()).thenReturn(service);
        Token<T> t2 = Mockito.mock(Token.class);
        Mockito.when(t2.getService()).thenReturn(new Text("service2"));
        Token<T> t3 = Mockito.mock(Token.class);
        Mockito.when(t3.getService()).thenReturn(service);
        // add token to ugi
        ugi.addToken(t1);
        ugi.addToken(t2);
        checkTokens(ugi, t1, t2);
        Credentials creds = ugi.getCredentials();
        creds.addToken(t3.getService(), t3);
        Assert.assertSame(t3, creds.getToken(service));
        // check that ugi wasn't modified
        checkTokens(ugi, t1, t2);
    }

    // from Mockito mocks
    @SuppressWarnings("unchecked")
    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testAddCreds() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Text service = new Text("service");
        Token<T> t1 = Mockito.mock(Token.class);
        Mockito.when(t1.getService()).thenReturn(service);
        Token<T> t2 = Mockito.mock(Token.class);
        Mockito.when(t2.getService()).thenReturn(new Text("service2"));
        byte[] secret = new byte[]{  };
        Text secretKey = new Text("sshhh");
        // fill credentials
        Credentials creds = new Credentials();
        creds.addToken(t1.getService(), t1);
        creds.addToken(t2.getService(), t2);
        creds.addSecretKey(secretKey, secret);
        // add creds to ugi, and check ugi
        ugi.addCredentials(creds);
        checkTokens(ugi, t1, t2);
        Assert.assertSame(secret, ugi.getCredentials().getSecretKey(secretKey));
    }

    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testGetCredsNotSame() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Credentials creds = ugi.getCredentials();
        // should always get a new copy
        Assert.assertNotSame(creds, ugi.getCredentials());
    }

    // from Mockito mocks
    @SuppressWarnings("unchecked")
    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testAddNamedToken() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Token<T> t1 = Mockito.mock(Token.class);
        Text service1 = new Text("t1");
        Text service2 = new Text("t2");
        Mockito.when(t1.getService()).thenReturn(service1);
        // add token
        ugi.addToken(service1, t1);
        Assert.assertSame(t1, ugi.getCredentials().getToken(service1));
        // add token with another name
        ugi.addToken(service2, t1);
        Assert.assertSame(t1, ugi.getCredentials().getToken(service1));
        Assert.assertSame(t1, ugi.getCredentials().getToken(service2));
    }

    // from Mockito mocks
    @SuppressWarnings("unchecked")
    @Test(timeout = 30000)
    public <T extends TokenIdentifier> void testUGITokens() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("TheDoctor", new String[]{ "TheTARDIS" });
        Token<T> t1 = Mockito.mock(Token.class);
        Mockito.when(t1.getService()).thenReturn(new Text("t1"));
        Token<T> t2 = Mockito.mock(Token.class);
        Mockito.when(t2.getService()).thenReturn(new Text("t2"));
        Credentials creds = new Credentials();
        byte[] secretKey = new byte[]{  };
        Text secretName = new Text("shhh");
        creds.addSecretKey(secretName, secretKey);
        ugi.addToken(t1);
        ugi.addToken(t2);
        ugi.addCredentials(creds);
        Collection<Token<? extends TokenIdentifier>> z = ugi.getTokens();
        Assert.assertTrue(z.contains(t1));
        Assert.assertTrue(z.contains(t2));
        Assert.assertEquals(2, z.size());
        Credentials ugiCreds = ugi.getCredentials();
        Assert.assertSame(secretKey, ugiCreds.getSecretKey(secretName));
        Assert.assertEquals(1, ugiCreds.numberOfSecretKeys());
        try {
            z.remove(t1);
            Assert.fail("Shouldn't be able to modify token collection from UGI");
        } catch (UnsupportedOperationException uoe) {
            // Can't modify tokens
        }
        // ensure that the tokens are passed through doAs
        Collection<Token<? extends TokenIdentifier>> otherSet = ugi.doAs(new PrivilegedExceptionAction<Collection<Token<?>>>() {
            @Override
            public Collection<Token<?>> run() throws IOException {
                return UserGroupInformation.getCurrentUser().getTokens();
            }
        });
        Assert.assertTrue(otherSet.contains(t1));
        Assert.assertTrue(otherSet.contains(t2));
    }

    @Test(timeout = 30000)
    public void testTokenIdentifiers() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("TheDoctor", new String[]{ "TheTARDIS" });
        TokenIdentifier t1 = Mockito.mock(TokenIdentifier.class);
        TokenIdentifier t2 = Mockito.mock(TokenIdentifier.class);
        ugi.addTokenIdentifier(t1);
        ugi.addTokenIdentifier(t2);
        Collection<TokenIdentifier> z = ugi.getTokenIdentifiers();
        Assert.assertTrue(z.contains(t1));
        Assert.assertTrue(z.contains(t2));
        Assert.assertEquals(2, z.size());
        // ensure that the token identifiers are passed through doAs
        Collection<TokenIdentifier> otherSet = ugi.doAs(new PrivilegedExceptionAction<Collection<TokenIdentifier>>() {
            @Override
            public Collection<TokenIdentifier> run() throws IOException {
                return UserGroupInformation.getCurrentUser().getTokenIdentifiers();
            }
        });
        Assert.assertTrue(otherSet.contains(t1));
        Assert.assertTrue(otherSet.contains(t2));
        Assert.assertEquals(2, otherSet.size());
    }

    @Test(timeout = 30000)
    public void testTestAuthMethod() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        // verify the reverse mappings works
        for (AuthenticationMethod am : AuthenticationMethod.values()) {
            if ((am.getAuthMethod()) != null) {
                ugi.setAuthenticationMethod(am.getAuthMethod());
                Assert.assertEquals(am, ugi.getAuthenticationMethod());
            }
        }
    }

    @Test(timeout = 30000)
    public void testUGIAuthMethod() throws Exception {
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final AuthenticationMethod am = AuthenticationMethod.KERBEROS;
        ugi.setAuthenticationMethod(am);
        Assert.assertEquals(am, ugi.getAuthenticationMethod());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
                Assert.assertEquals(am, UserGroupInformation.getCurrentUser().getAuthenticationMethod());
                return null;
            }
        });
    }

    @Test(timeout = 30000)
    public void testUGIAuthMethodInRealUser() throws Exception {
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        UserGroupInformation proxyUgi = UserGroupInformation.createProxyUser("proxy", ugi);
        final AuthenticationMethod am = AuthenticationMethod.KERBEROS;
        ugi.setAuthenticationMethod(am);
        Assert.assertEquals(am, ugi.getAuthenticationMethod());
        Assert.assertEquals(PROXY, proxyUgi.getAuthenticationMethod());
        Assert.assertEquals(am, UserGroupInformation.getRealAuthenticationMethod(proxyUgi));
        proxyUgi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
                Assert.assertEquals(PROXY, UserGroupInformation.getCurrentUser().getAuthenticationMethod());
                Assert.assertEquals(am, UserGroupInformation.getCurrentUser().getRealUser().getAuthenticationMethod());
                return null;
            }
        });
        UserGroupInformation proxyUgi2 = new UserGroupInformation(proxyUgi.getSubject());
        proxyUgi2.setAuthenticationMethod(PROXY);
        Assert.assertEquals(proxyUgi, proxyUgi2);
        // Equality should work if authMethod is null
        UserGroupInformation realugi = UserGroupInformation.getCurrentUser();
        UserGroupInformation proxyUgi3 = UserGroupInformation.createProxyUser("proxyAnother", realugi);
        UserGroupInformation proxyUgi4 = new UserGroupInformation(proxyUgi3.getSubject());
        Assert.assertEquals(proxyUgi3, proxyUgi4);
    }

    @Test(timeout = 30000)
    public void testLoginObjectInSubject() throws Exception {
        UserGroupInformation loginUgi = UserGroupInformation.getLoginUser();
        UserGroupInformation anotherUgi = new UserGroupInformation(loginUgi.getSubject());
        LoginContext login1 = loginUgi.getSubject().getPrincipals(User.class).iterator().next().getLogin();
        LoginContext login2 = anotherUgi.getSubject().getPrincipals(User.class).iterator().next().getLogin();
        // login1 and login2 must be same instances
        Assert.assertTrue((login1 == login2));
    }

    @Test(timeout = 30000)
    public void testLoginModuleCommit() throws Exception {
        UserGroupInformation loginUgi = UserGroupInformation.getLoginUser();
        User user1 = loginUgi.getSubject().getPrincipals(User.class).iterator().next();
        LoginContext login = user1.getLogin();
        login.logout();
        login.login();
        User user2 = loginUgi.getSubject().getPrincipals(User.class).iterator().next();
        // user1 and user2 must be same instances.
        Assert.assertTrue((user1 == user2));
    }

    /**
     * Test for the case that UserGroupInformation.getCurrentUser()
     * is called when the AccessControlContext has a Subject associated
     * with it, but that Subject was not created by Hadoop (ie it has no
     * associated User principal)
     */
    @Test(timeout = 30000)
    public void testUGIUnderNonHadoopContext() throws Exception {
        Subject nonHadoopSubject = new Subject();
        Subject.doAs(nonHadoopSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws IOException {
                UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
                Assert.assertNotNull(ugi);
                return null;
            }
        });
    }

    @Test(timeout = 30000)
    public void testGetUGIFromSubject() throws Exception {
        KerberosPrincipal p = new KerberosPrincipal("guest");
        Subject subject = new Subject();
        subject.getPrincipals().add(p);
        UserGroupInformation ugi = UserGroupInformation.getUGIFromSubject(subject);
        Assert.assertNotNull(ugi);
        Assert.assertEquals("guest@DEFAULT.REALM", ugi.getUserName());
    }

    /**
     * Test hasSufficientTimeElapsed method
     */
    @Test(timeout = 30000)
    public void testHasSufficientTimeElapsed() throws Exception {
        // Make hasSufficientTimeElapsed public
        Method method = UserGroupInformation.class.getDeclaredMethod("hasSufficientTimeElapsed", long.class);
        method.setAccessible(true);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        User user = ugi.getSubject().getPrincipals(User.class).iterator().next();
        long now = System.currentTimeMillis();
        // Using default relogin time (1 minute)
        user.setLastLogin((now - ((2 * 60) * 1000)));// 2 minutes before "now"

        Assert.assertTrue(((Boolean) (method.invoke(ugi, now))));
        user.setLastLogin((now - (30 * 1000)));
        // 30 seconds before "now"
        Assert.assertFalse(((Boolean) (method.invoke(ugi, now))));
        // Using relogin time of 10 minutes
        Configuration conf2 = new Configuration(TestUserGroupInformation.conf);
        conf2.setLong(HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN, (10 * 60));
        UserGroupInformation.setConfiguration(conf2);
        user.setLastLogin((now - ((15 * 60) * 1000)));// 15 minutes before "now"

        Assert.assertTrue(((Boolean) (method.invoke(ugi, now))));
        user.setLastLogin((now - ((6 * 60) * 1000)));// 6 minutes before "now"

        Assert.assertFalse(((Boolean) (method.invoke(ugi, now))));
        // Restore original conf to UGI
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        // Restore hasSufficientTimElapsed back to private
        method.setAccessible(false);
    }

    @Test(timeout = 10000)
    public void testSetLoginUser() throws IOException {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        UserGroupInformation.setLoginUser(ugi);
        Assert.assertEquals(ugi, UserGroupInformation.getLoginUser());
    }

    /**
     * In some scenario, such as HA, delegation tokens are associated with a
     * logical name. The tokens are cloned and are associated with the
     * physical address of the server where the service is provided.
     * This test ensures cloned delegated tokens are locally used
     * and are not returned in {@link UserGroupInformation#getCredentials()}
     */
    @Test
    public void testPrivateTokenExclusion() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("privateUser", new String[]{ "PRIVATEUSERS" });
        TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier();
        Token<TestRpcBase.TestTokenIdentifier> token = new Token<TestRpcBase.TestTokenIdentifier>(getBytes(), "password".getBytes(), tokenId.getKind(), null);
        ugi.addToken(new Text("regular-token"), token);
        // Now add cloned private token
        Text service = new Text("private-token");
        ugi.addToken(service, token.privateClone(service));
        Text service1 = new Text("private-token1");
        ugi.addToken(service1, token.privateClone(service1));
        // Ensure only non-private tokens are returned
        Collection<Token<? extends TokenIdentifier>> tokens = ugi.getCredentials().getAllTokens();
        Assert.assertEquals(1, tokens.size());
    }

    /**
     * This test checks a race condition between getting and adding tokens for
     * the current user.  Calling UserGroupInformation.getCurrentUser() returns
     * a new object each time, so simply making these methods synchronized was not
     * enough to prevent race conditions and causing a
     * ConcurrentModificationException.  These methods are synchronized on the
     * Subject, which is the same object between UserGroupInformation instances.
     * This test tries to cause a CME, by exposing the race condition.  Previously
     * this test would fail every time; now it does not.
     */
    @Test
    public void testTokenRaceCondition() throws Exception {
        UserGroupInformation userGroupInfo = UserGroupInformation.createUserForTesting(TestUserGroupInformation.USER_NAME, TestUserGroupInformation.GROUP_NAMES);
        userGroupInfo.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                // make sure it is not the same as the login user because we use the
                // same UGI object for every instantiation of the login user and you
                // won't run into the race condition otherwise
                Assert.assertNotEquals(UserGroupInformation.getLoginUser(), UserGroupInformation.getCurrentUser());
                TestUserGroupInformation.GetTokenThread thread = new TestUserGroupInformation.GetTokenThread();
                try {
                    thread.start();
                    for (int i = 0; i < 100; i++) {
                        @SuppressWarnings("unchecked")
                        Token<? extends TokenIdentifier> t = Mockito.mock(Token.class);
                        Mockito.when(t.getService()).thenReturn(new Text(("t" + i)));
                        UserGroupInformation.getCurrentUser().addToken(t);
                        Assert.assertNull("ConcurrentModificationException encountered", thread.cme);
                    }
                } catch (ConcurrentModificationException cme) {
                    cme.printStackTrace();
                    Assert.fail("ConcurrentModificationException encountered");
                } finally {
                    thread.runThread = false;
                    thread.join((5 * 1000));
                }
                return null;
            }
        });
    }

    static class GetTokenThread extends Thread {
        boolean runThread = true;

        volatile ConcurrentModificationException cme = null;

        @Override
        public void run() {
            while (runThread) {
                try {
                    UserGroupInformation.getCurrentUser().getCredentials();
                } catch (ConcurrentModificationException cme) {
                    this.cme = cme;
                    cme.printStackTrace();
                    runThread = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
        }
    }

    @Test
    public void testExternalTokenFiles() throws Exception {
        StringBuilder tokenFullPathnames = new StringBuilder();
        String tokenFilenames = "token1,token2";
        String[] tokenFiles = StringUtils.getTrimmedStrings(tokenFilenames);
        final File testDir = new File("target", ((TestUserGroupInformation.class.getName()) + "-tmpDir")).getAbsoluteFile();
        String testDirPath = testDir.getAbsolutePath();
        // create path for token files
        for (String tokenFile : tokenFiles) {
            if ((tokenFullPathnames.length()) > 0) {
                tokenFullPathnames.append(",");
            }
            tokenFullPathnames.append(testDirPath).append("/").append(tokenFile);
        }
        // create new token and store it
        TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier();
        Credentials cred1 = new Credentials();
        Token<TestRpcBase.TestTokenIdentifier> token1 = new Token<TestRpcBase.TestTokenIdentifier>(getBytes(), "password".getBytes(), tokenId.getKind(), new Text("token-service1"));
        cred1.addToken(token1.getService(), token1);
        cred1.writeTokenStorageFile(new Path(testDirPath, tokenFiles[0]), TestUserGroupInformation.conf);
        Credentials cred2 = new Credentials();
        Token<TestRpcBase.TestTokenIdentifier> token2 = new Token<TestRpcBase.TestTokenIdentifier>(getBytes(), "password".getBytes(), tokenId.getKind(), new Text("token-service2"));
        cred2.addToken(token2.getService(), token2);
        cred2.writeTokenStorageFile(new Path(testDirPath, tokenFiles[1]), TestUserGroupInformation.conf);
        // set property for token external token files
        System.setProperty("hadoop.token.files", tokenFullPathnames.toString());
        UserGroupInformation.setLoginUser(null);
        UserGroupInformation tokenUgi = UserGroupInformation.getLoginUser();
        Collection<Token<?>> credsugiTokens = tokenUgi.getTokens();
        Assert.assertTrue(credsugiTokens.contains(token1));
        Assert.assertTrue(credsugiTokens.contains(token2));
        System.clearProperty("hadoop.token.files");
    }

    @Test
    public void testCheckTGTAfterLoginFromSubject() throws Exception {
        // security on, default is remove default realm
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, TestUserGroupInformation.conf);
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        // Login from a pre-set subject with a keytab
        final Subject subject = new Subject();
        KeyTab keytab = KeyTab.getInstance();
        subject.getPrivateCredentials().add(keytab);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws IOException {
                UserGroupInformation.loginUserFromSubject(subject);
                // this should not throw.
                UserGroupInformation.getLoginUser().checkTGTAndReloginFromKeytab();
                return null;
            }
        });
    }

    @Test
    public void testGetNextRetryTime() throws Exception {
        GenericTestUtils.setLogLevel(UserGroupInformation.LOG, Level.DEBUG);
        final long reloginInterval = 1;
        final long reloginIntervalMs = reloginInterval * 1000;
        // Relogin happens every 1 second.
        TestUserGroupInformation.conf.setLong(CommonConfigurationKeysPublic.HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN, reloginInterval);
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, TestUserGroupInformation.conf);
        UserGroupInformation.setConfiguration(TestUserGroupInformation.conf);
        // Suppose tgt start time is now, end time is 20 seconds from now.
        final long now = Time.now();
        final Date endDate = new Date((now + 20000));
        // Explicitly test the exponential back-off logic.
        // Suppose some time (10 seconds) passed.
        // Verify exponential backoff and max=(login interval before endTime).
        final long currentTime = now + 10000;
        final long endTime = endDate.getTime();
        Assert.assertEquals(0, metrics.getRenewalFailures().value());
        RetryPolicy rp = RetryPolicies.exponentialBackoffRetry(((Long.SIZE) - 2), 1000, TimeUnit.MILLISECONDS);
        long lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        assertWithinBounds(metrics.getRenewalFailures().value(), lastRetry, reloginIntervalMs, currentTime);
        metrics.getRenewalFailures().incr();
        lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        assertWithinBounds(metrics.getRenewalFailures().value(), lastRetry, reloginIntervalMs, currentTime);
        metrics.getRenewalFailures().incr();
        lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        assertWithinBounds(metrics.getRenewalFailures().value(), lastRetry, reloginIntervalMs, currentTime);
        metrics.getRenewalFailures().incr();
        lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        assertWithinBounds(metrics.getRenewalFailures().value(), lastRetry, reloginIntervalMs, currentTime);
        // last try should be right before expiry.
        metrics.getRenewalFailures().incr();
        lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        String str = (("5th retry, now:" + currentTime) + ", retry:") + lastRetry;
        TestUserGroupInformation.LOG.info(str);
        Assert.assertEquals(str, (endTime - reloginIntervalMs), lastRetry);
        // make sure no more retries after (tgt endTime - login interval).
        metrics.getRenewalFailures().incr();
        lastRetry = UserGroupInformation.getNextTgtRenewalTime(endTime, currentTime, rp);
        str = (("overflow retry, now:" + currentTime) + ", retry:") + lastRetry;
        TestUserGroupInformation.LOG.info(str);
        Assert.assertEquals(str, (endTime - reloginIntervalMs), lastRetry);
    }

    // verify that getCurrentUser on the same and different subjects can be
    // concurrent.  Ie. no synchronization.
    @Test(timeout = 8000)
    public void testConcurrentGetCurrentUser() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final CountDownLatch latch = new CountDownLatch(1);
        final UserGroupInformation testUgi1 = UserGroupInformation.createRemoteUser("testUgi1");
        final UserGroupInformation testUgi2 = UserGroupInformation.createRemoteUser("testUgi2");
        // swap the User with a spy to allow getCurrentUser to block when the
        // spy is called for the user name.
        Set<Principal> principals = testUgi1.getSubject().getPrincipals();
        User user = testUgi1.getSubject().getPrincipals(User.class).iterator().next();
        final User spyUser = Mockito.spy(user);
        principals.remove(user);
        principals.add(spyUser);
        Mockito.when(spyUser.getName()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                latch.countDown();
                barrier.await();
                return ((String) (invocation.callRealMethod()));
            }
        });
        // wait for the thread to block on the barrier in getCurrentUser.
        Future<UserGroupInformation> blockingLookup = Executors.newSingleThreadExecutor().submit(new Callable<UserGroupInformation>() {
            @Override
            public UserGroupInformation call() throws Exception {
                return testUgi1.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
                    @Override
                    public UserGroupInformation run() throws Exception {
                        return UserGroupInformation.getCurrentUser();
                    }
                });
            }
        });
        latch.await();
        // old versions of mockito synchronize on returning mocked answers so
        // the blocked getCurrentUser will block all other calls to getName.
        // workaround this by swapping out the spy with the original User.
        principals.remove(spyUser);
        principals.add(user);
        // concurrent getCurrentUser on ugi1 should not be blocked.
        UserGroupInformation ugi;
        ugi = testUgi1.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
            @Override
            public UserGroupInformation run() throws Exception {
                return UserGroupInformation.getCurrentUser();
            }
        });
        Assert.assertSame(testUgi1.getSubject(), ugi.getSubject());
        // concurrent getCurrentUser on ugi2 should not be blocked.
        ugi = testUgi2.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
            @Override
            public UserGroupInformation run() throws Exception {
                return UserGroupInformation.getCurrentUser();
            }
        });
        Assert.assertSame(testUgi2.getSubject(), ugi.getSubject());
        // unblock the original call.
        barrier.await();
        Assert.assertSame(testUgi1.getSubject(), blockingLookup.get().getSubject());
    }

    @Test
    public void testKerberosTicketIsDestroyedChecked() throws Exception {
        // Create UserGroupInformation
        GenericTestUtils.setLogLevel(UserGroupInformation.LOG, Level.DEBUG);
        Set<User> users = new HashSet<>();
        users.add(new User("Foo"));
        Subject subject = new Subject(true, users, new HashSet(), new HashSet());
        UserGroupInformation ugi = Mockito.spy(new UserGroupInformation(subject));
        // throw IOException in the middle of the autoRenewalForUserCreds
        Mockito.doThrow(new IOException()).when(ugi).reloginFromTicketCache();
        // Create and destroy the KerberosTicket, so endTime will be null
        Date d = new Date();
        KerberosPrincipal kp = new KerberosPrincipal("Foo");
        KerberosTicket tgt = Mockito.spy(new KerberosTicket(new byte[]{  }, kp, kp, new byte[]{  }, 0, null, d, d, d, d, null));
        tgt.destroy();
        // run AutoRenewalForUserCredsRunnable with this
        UserGroupInformation.AutoRenewalForUserCredsRunnable userCredsRunnable = ugi.new TicketCacheRenewalRunnable(tgt, Boolean.toString(Boolean.TRUE), 100);
        // Set the runnable to not to run in a loop
        userCredsRunnable.setRunRenewalLoop(false);
        // there should be no exception when calling this
        userCredsRunnable.run();
        // isDestroyed should be called at least once
        Mockito.verify(tgt, Mockito.atLeastOnce()).isDestroyed();
    }

    @Test
    public void testImportTokensFromConfig() throws IOException {
        Configuration config = new Configuration();
        // Add a base64 token
        String service0 = "testTokenImportService0";
        byte[] identity = "identityImportConfig".getBytes();
        byte[] password = "passwordImportConfig".getBytes();
        Token<TokenIdentifier> expectedToken0 = new Token(identity, password, new Text("testTokenKind0"), new Text(service0));
        String tokenBase64 = expectedToken0.encodeToUrlString();
        config.set(HADOOP_TOKENS, (tokenBase64 + ",badtoken"));
        // Add a token from a file
        String service1 = "testTokenImportService1";
        Credentials cred0 = new Credentials();
        Token<TokenIdentifier> expectedToken1 = expectedToken0.copyToken();
        expectedToken1.setKind(new Text("testTokenKind1"));
        expectedToken1.setService(new Text(service1));
        cred0.addToken(expectedToken1.getService(), expectedToken1);
        Path workDir = new Path(GenericTestUtils.getRandomizedTestDir().getAbsolutePath());
        Path tokenPath1 = new Path(workDir, "dt.token");
        cred0.writeTokenStorageFile(tokenPath1, config);
        config.set(HADOOP_TOKEN_FILES, ((tokenPath1 + ",") + (new Path(workDir, "badfile"))));
        UserGroupInformation.reset();
        UserGroupInformation.setConfiguration(config);
        // Check if the tokens were loaded
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        Credentials outCred = ugi.getCredentials();
        Assert.assertEquals(("Tokens: " + (outCred.getAllTokens())), 2, outCred.getAllTokens().size());
        boolean found0 = false;
        boolean found1 = false;
        for (Token<? extends TokenIdentifier> token : outCred.getAllTokens()) {
            Assert.assertArrayEquals(identity, token.getIdentifier());
            if (token.getService().toString().equals(service0)) {
                Assert.assertEquals(expectedToken0.encodeToUrlString(), token.encodeToUrlString());
                found0 = true;
            }
            if (token.getService().toString().equals(service1)) {
                found1 = true;
            }
        }
        Assert.assertTrue(("Expected token testTokenService0 not found: " + outCred), found0);
        Assert.assertTrue(("Expected token testTokenService1 not found: " + outCred), found1);
        // Try to add the same token through configuration and file
        Credentials cred1 = new Credentials();
        cred1.addToken(expectedToken0.getService(), expectedToken0);
        cred1.writeTokenStorageFile(tokenPath1, config);
        UserGroupInformation.reset();
        UserGroupInformation.setConfiguration(config);
        UserGroupInformation ugi1 = UserGroupInformation.getLoginUser();
        Credentials outCred1 = ugi1.getCredentials();
        Assert.assertEquals(("Tokens: " + (outCred1.getAllTokens())), 1, outCred1.getAllTokens().size());
    }

    @Test
    public void testImportTokensFromProperty() throws IOException {
        // Add a base64 token
        Text service = new Text("testTokenProperty");
        byte[] identity = "identityImportProperty".getBytes();
        byte[] password = "passwordImportProperty".getBytes();
        Token<TokenIdentifier> expectedToken0 = new Token(identity, password, new Text("testTokenKind0"), service);
        String tokenBase64 = expectedToken0.encodeToUrlString();
        System.setProperty(HADOOP_TOKENS, tokenBase64);
        // Check if the tokens were loaded
        UserGroupInformation.reset();
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        Credentials creds = ugi.getCredentials();
        Assert.assertEquals(("Tokens: " + (creds.getAllTokens())), 1, creds.getAllTokens().size());
        Assert.assertArrayEquals(creds.getToken(service).getIdentifier(), identity);
        // Cleanup
        System.clearProperty(HADOOP_TOKENS);
    }
}

