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
package org.apache.drill.exec.server.rest.spnego;


import ExecConstants.ADMIN_USERS_VALIDATOR;
import ExecConstants.ADMIN_USERS_VALIDATOR.DEFAULT_ADMIN_USERS;
import ExecConstants.ADMIN_USER_GROUPS_VALIDATOR;
import ExecConstants.ADMIN_USER_GROUPS_VALIDATOR.DEFAULT_ADMIN_USER_GROUPS;
import ExecConstants.HTTP_AUTHENTICATION_MECHANISMS;
import ExecConstants.HTTP_SPNEGO_KEYTAB;
import ExecConstants.HTTP_SPNEGO_PRINCIPAL;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import PlainFactory.SIMPLE_NAME;
import com.typesafe.config.ConfigValueFactory;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.scanner.ClassPathScanner;
import org.apache.drill.common.scanner.persistence.ScanResult;
import org.apache.drill.exec.exception.DrillbitStartupException;
import org.apache.drill.exec.rpc.security.AuthenticatorProviderImpl;
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.server.options.SystemOptionManager;
import org.apache.drill.exec.server.rest.auth.DrillHttpSecurityHandlerProvider;
import org.apache.drill.exec.server.rest.auth.DrillSpnegoLoginService;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.eclipse.jetty.server.UserIdentity;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import sun.security.jgss.GSSUtil;


/**
 * Test for validating {@link DrillSpnegoLoginService}
 */
@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestSpnegoAuthentication {
    private static KerberosHelper spnegoHelper;

    private static final String primaryName = "HTTP";

    private static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Both SPNEGO and FORM mechanism is enabled for WebServer in configuration. Test to see if the respective security
     * handlers are created successfully or not.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSPNEGOAndFORMEnabled() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("form", "spnego"))).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.serverKeytab.toString())));
        final ScanResult scanResult = ClassPathScanner.fromPrescan(newConfig);
        final AuthenticatorProviderImpl authenticatorProvider = Mockito.mock(AuthenticatorProviderImpl.class);
        Mockito.when(authenticatorProvider.containsFactory(SIMPLE_NAME)).thenReturn(true);
        final DrillbitContext context = Mockito.mock(DrillbitContext.class);
        Mockito.when(context.getClasspathScan()).thenReturn(scanResult);
        Mockito.when(context.getConfig()).thenReturn(newConfig);
        Mockito.when(context.getAuthProvider()).thenReturn(authenticatorProvider);
        final DrillHttpSecurityHandlerProvider securityProvider = new DrillHttpSecurityHandlerProvider(newConfig, context);
        Assert.assertTrue(securityProvider.isFormEnabled());
        Assert.assertTrue(securityProvider.isSpnegoEnabled());
    }

    /**
     * Validate if FORM security handler is created successfully when only form is configured as auth mechanism
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOnlyFORMEnabled() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("form"))).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.serverKeytab.toString())));
        final ScanResult scanResult = ClassPathScanner.fromPrescan(newConfig);
        final AuthenticatorProviderImpl authenticatorProvider = Mockito.mock(AuthenticatorProviderImpl.class);
        Mockito.when(authenticatorProvider.containsFactory(SIMPLE_NAME)).thenReturn(true);
        final DrillbitContext context = Mockito.mock(DrillbitContext.class);
        Mockito.when(context.getClasspathScan()).thenReturn(scanResult);
        Mockito.when(context.getConfig()).thenReturn(newConfig);
        Mockito.when(context.getAuthProvider()).thenReturn(authenticatorProvider);
        final DrillHttpSecurityHandlerProvider securityProvider = new DrillHttpSecurityHandlerProvider(newConfig, context);
        Assert.assertTrue(securityProvider.isFormEnabled());
        Assert.assertTrue((!(securityProvider.isSpnegoEnabled())));
    }

    /**
     * Validate failure in creating FORM security handler when PAM authenticator is absent. PAM authenticator is provided
     * via {@link PlainFactory#getAuthenticator()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFORMEnabledWithPlainDisabled() throws Exception {
        try {
            final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("form"))).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.serverKeytab.toString())));
            final ScanResult scanResult = ClassPathScanner.fromPrescan(newConfig);
            final AuthenticatorProviderImpl authenticatorProvider = Mockito.mock(AuthenticatorProviderImpl.class);
            Mockito.when(authenticatorProvider.containsFactory(SIMPLE_NAME)).thenReturn(false);
            final DrillbitContext context = Mockito.mock(DrillbitContext.class);
            Mockito.when(context.getClasspathScan()).thenReturn(scanResult);
            Mockito.when(context.getConfig()).thenReturn(newConfig);
            Mockito.when(context.getAuthProvider()).thenReturn(authenticatorProvider);
            final DrillHttpSecurityHandlerProvider securityProvider = new DrillHttpSecurityHandlerProvider(newConfig, context);
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof DrillbitStartupException));
        }
    }

    /**
     * Validate only SPNEGO security handler is configured properly when enabled via configuration
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOnlySPNEGOEnabled() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("spnego"))).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.serverKeytab.toString())));
        final ScanResult scanResult = ClassPathScanner.fromPrescan(newConfig);
        final AuthenticatorProviderImpl authenticatorProvider = Mockito.mock(AuthenticatorProviderImpl.class);
        Mockito.when(authenticatorProvider.containsFactory(SIMPLE_NAME)).thenReturn(false);
        final DrillbitContext context = Mockito.mock(DrillbitContext.class);
        Mockito.when(context.getClasspathScan()).thenReturn(scanResult);
        Mockito.when(context.getConfig()).thenReturn(newConfig);
        Mockito.when(context.getAuthProvider()).thenReturn(authenticatorProvider);
        final DrillHttpSecurityHandlerProvider securityProvider = new DrillHttpSecurityHandlerProvider(newConfig, context);
        Assert.assertTrue((!(securityProvider.isFormEnabled())));
        Assert.assertTrue(securityProvider.isSpnegoEnabled());
    }

    /**
     * Validate when none of the security mechanism is specified in the
     * {@link ExecConstants#HTTP_AUTHENTICATION_MECHANISMS}, FORM security handler is still configured correctly when
     * authentication is enabled along with PAM authenticator module.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConfigBackwardCompatibility() throws Exception {
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        final ScanResult scanResult = ClassPathScanner.fromPrescan(newConfig);
        final AuthenticatorProviderImpl authenticatorProvider = Mockito.mock(AuthenticatorProviderImpl.class);
        Mockito.when(authenticatorProvider.containsFactory(SIMPLE_NAME)).thenReturn(true);
        final DrillbitContext context = Mockito.mock(DrillbitContext.class);
        Mockito.when(context.getClasspathScan()).thenReturn(scanResult);
        Mockito.when(context.getConfig()).thenReturn(newConfig);
        Mockito.when(context.getAuthProvider()).thenReturn(authenticatorProvider);
        final DrillHttpSecurityHandlerProvider securityProvider = new DrillHttpSecurityHandlerProvider(newConfig, context);
        Assert.assertTrue(securityProvider.isFormEnabled());
        Assert.assertTrue((!(securityProvider.isSpnegoEnabled())));
    }

    /**
     * Validate successful {@link DrillSpnegoLoginService#login(String, Object)} when provided with client token for a
     * configured service principal.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDrillSpnegoLoginService() throws Exception {
        // Create client subject using it's principal and keytab
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestSpnegoAuthentication.spnegoHelper.CLIENT_PRINCIPAL, TestSpnegoAuthentication.spnegoHelper.clientKeytab.getAbsoluteFile());
        // Generate a SPNEGO token for the peer SERVER_PRINCIPAL from this CLIENT_PRINCIPAL
        final String token = Subject.doAs(clientSubject, new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                final GSSManager gssManager = GSSManager.getInstance();
                GSSContext gssContext = null;
                try {
                    final Oid oid = GSSUtil.GSS_SPNEGO_MECH_OID;
                    final GSSName serviceName = gssManager.createName(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL, GSSName.NT_USER_NAME, oid);
                    gssContext = gssManager.createContext(serviceName, oid, null, GSSContext.DEFAULT_LIFETIME);
                    gssContext.requestCredDeleg(true);
                    gssContext.requestMutualAuth(true);
                    byte[] outToken = new byte[0];
                    outToken = gssContext.initSecContext(outToken, 0, outToken.length);
                    return Base64.encodeBase64String(outToken);
                } finally {
                    if (gssContext != null) {
                        gssContext.dispose();
                    }
                }
            }
        });
        // Create a DrillbitContext with service principal and keytab for DrillSpnegoLoginService
        final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("spnego"))).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoAuthentication.spnegoHelper.serverKeytab.toString())));
        final SystemOptionManager optionManager = Mockito.mock(SystemOptionManager.class);
        Mockito.when(optionManager.getOption(ADMIN_USERS_VALIDATOR)).thenReturn(DEFAULT_ADMIN_USERS);
        Mockito.when(optionManager.getOption(ADMIN_USER_GROUPS_VALIDATOR)).thenReturn(DEFAULT_ADMIN_USER_GROUPS);
        final DrillbitContext drillbitContext = Mockito.mock(DrillbitContext.class);
        Mockito.when(drillbitContext.getConfig()).thenReturn(newConfig);
        Mockito.when(drillbitContext.getOptionManager()).thenReturn(optionManager);
        final DrillSpnegoLoginService loginService = new DrillSpnegoLoginService(drillbitContext);
        // Authenticate the client using its SPNEGO token
        final UserIdentity user = loginService.login(null, token);
        // Validate the UserIdentity of authenticated client
        Assert.assertTrue((user != null));
        Assert.assertTrue(user.getUserPrincipal().getName().equals(TestSpnegoAuthentication.spnegoHelper.CLIENT_SHORT_NAME));
        Assert.assertTrue(user.isUserInRole("authenticated", null));
    }
}

