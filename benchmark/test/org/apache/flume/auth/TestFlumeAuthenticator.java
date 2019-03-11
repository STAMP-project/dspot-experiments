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
package org.apache.flume.auth;


import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import org.apache.hadoop.minikdc.MiniKdc;
import org.junit.Assert;
import org.junit.Test;


public class TestFlumeAuthenticator {
    private static MiniKdc kdc;

    private static File workDir;

    private static File flumeKeytab;

    private static String flumePrincipal = "flume/localhost";

    private static File aliceKeytab;

    private static String alicePrincipal = "alice";

    private static Properties conf;

    @Test
    public void testNullLogin() throws IOException {
        String principal = null;
        String keytab = null;
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab);
        Assert.assertFalse(authenticator.isAuthenticated());
    }

    @Test
    public void testFlumeLogin() throws IOException {
        String principal = TestFlumeAuthenticator.flumePrincipal;
        String keytab = TestFlumeAuthenticator.flumeKeytab.getAbsolutePath();
        String expResult = principal;
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab);
        Assert.assertTrue(authenticator.isAuthenticated());
        String result = getUserName();
        Assert.assertEquals("Initial login failed", expResult, result);
        authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab);
        result = getUserName();
        Assert.assertEquals("Re-login failed", expResult, result);
        principal = TestFlumeAuthenticator.alicePrincipal;
        keytab = TestFlumeAuthenticator.aliceKeytab.getAbsolutePath();
        try {
            authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab);
            result = getUserName();
            Assert.fail(("Login should have failed with a new principal: " + result));
        } catch (Exception ex) {
            Assert.assertTrue((("Login with a new principal failed, but for an unexpected " + "reason: ") + (ex.getMessage())), ex.getMessage().contains("Cannot use multiple kerberos principals"));
        }
    }

    /**
     * Test whether the exception raised in the <code>PrivilegedExceptionAction</code> gets
     * propagated as-is from {@link KerberosAuthenticator#execute(PrivilegedExceptionAction)}.
     */
    @Test(expected = IOException.class)
    public void testKerberosAuthenticatorExceptionInExecute() throws Exception {
        String principal = TestFlumeAuthenticator.flumePrincipal;
        String keytab = TestFlumeAuthenticator.flumeKeytab.getAbsolutePath();
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab);
        Assert.assertTrue((authenticator instanceof KerberosAuthenticator));
        authenticator.execute(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                throw new IOException();
            }
        });
    }

    /**
     * Test whether the exception raised in the <code>PrivilegedExceptionAction</code> gets
     * propagated as-is from {@link SimpleAuthenticator#execute(PrivilegedExceptionAction)}.
     */
    @Test(expected = IOException.class)
    public void testSimpleAuthenticatorExceptionInExecute() throws Exception {
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(null, null);
        Assert.assertTrue((authenticator instanceof SimpleAuthenticator));
        authenticator.execute(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                throw new IOException();
            }
        });
    }

    @Test
    public void testProxyAs() throws IOException {
        String username = "alice";
        String expResult = username;
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(null, null);
        String result = getUserName();
        Assert.assertEquals("Proxy as didn't generate the expected username", expResult, result);
        authenticator = FlumeAuthenticationUtil.getAuthenticator(TestFlumeAuthenticator.flumePrincipal, TestFlumeAuthenticator.flumeKeytab.getAbsolutePath());
        String login = getUserName();
        Assert.assertEquals("Login succeeded, but the principal doesn't match", TestFlumeAuthenticator.flumePrincipal, login);
        result = getUserName();
        Assert.assertEquals("Proxy as didn't generate the expected username", expResult, result);
    }

    @Test
    public void testFlumeLoginPrincipalWithoutRealm() throws Exception {
        String principal = "flume";
        File keytab = new File(TestFlumeAuthenticator.workDir, "flume2.keytab");
        TestFlumeAuthenticator.kdc.createPrincipal(keytab, principal);
        String expResult = (principal + "@") + (TestFlumeAuthenticator.kdc.getRealm());
        FlumeAuthenticator authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab.getAbsolutePath());
        Assert.assertTrue(authenticator.isAuthenticated());
        String result = getUserName();
        Assert.assertEquals("Initial login failed", expResult, result);
        authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab.getAbsolutePath());
        result = getUserName();
        Assert.assertEquals("Re-login failed", expResult, result);
        principal = "alice";
        keytab = TestFlumeAuthenticator.aliceKeytab;
        try {
            authenticator = FlumeAuthenticationUtil.getAuthenticator(principal, keytab.getAbsolutePath());
            result = getUserName();
            Assert.fail(("Login should have failed with a new principal: " + result));
        } catch (Exception ex) {
            Assert.assertTrue((("Login with a new principal failed, but for an unexpected " + "reason: ") + (ex.getMessage())), ex.getMessage().contains("Cannot use multiple kerberos principals"));
        }
    }
}

