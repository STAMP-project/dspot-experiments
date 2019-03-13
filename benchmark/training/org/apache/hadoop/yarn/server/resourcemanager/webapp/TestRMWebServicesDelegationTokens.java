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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import AuthenticationFilter.AUTH_TYPE;
import KerberosAuthenticationHandler.KEYTAB;
import KerberosAuthenticationHandler.PRINCIPAL;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import Status.BAD_REQUEST;
import Status.FORBIDDEN;
import Status.OK;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.YARN_ACL_ENABLE;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.DelegationToken;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRMWebServicesDelegationTokens extends JerseyTestBase {
    private static File testRootDir;

    private static File httpSpnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static String httpSpnegoPrincipal = KerberosTestUtils.getServerPrincipal();

    private static MiniKdc testMiniKDC;

    private static MockRM rm;

    private boolean isKerberosAuth = false;

    // Make sure the test uses the published header string
    final String yarnTokenHeader = "Hadoop-YARN-RM-Delegation-Token";

    @Singleton
    public static class TestKerberosAuthFilter extends AuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) throws ServletException {
            Properties properties = super.getConfiguration(configPrefix, filterConfig);
            properties.put(PRINCIPAL, TestRMWebServicesDelegationTokens.httpSpnegoPrincipal);
            properties.put(KEYTAB, TestRMWebServicesDelegationTokens.httpSpnegoKeytabFile.getAbsolutePath());
            properties.put(AUTH_TYPE, "kerberos");
            return properties;
        }
    }

    @Singleton
    public static class TestSimpleAuthFilter extends AuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) throws ServletException {
            Properties properties = super.getConfiguration(configPrefix, filterConfig);
            properties.put(PRINCIPAL, TestRMWebServicesDelegationTokens.httpSpnegoPrincipal);
            properties.put(KEYTAB, TestRMWebServicesDelegationTokens.httpSpnegoKeytabFile.getAbsolutePath());
            properties.put(AUTH_TYPE, "simple");
            properties.put(ANONYMOUS_ALLOWED, "false");
            return properties;
        }
    }

    private class TestServletModule extends ServletModule {
        public Configuration rmconf = new Configuration();

        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            Configuration rmconf = new Configuration();
            rmconf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
            rmconf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
            rmconf.setBoolean(YARN_ACL_ENABLE, true);
            TestRMWebServicesDelegationTokens.rm = new MockRM(rmconf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesDelegationTokens.rm);
            if ((isKerberosAuth) == true) {
                filter("/*").through(TestRMWebServicesDelegationTokens.TestKerberosAuthFilter.class);
            } else {
                filter("/*").through(TestRMWebServicesDelegationTokens.TestSimpleAuthFilter.class);
            }
            serve("/*").with(GuiceContainer.class);
        }
    }

    public TestRMWebServicesDelegationTokens(int run) throws Exception {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
        switch (run) {
            case 0 :
            default :
                GuiceServletConfig.setInjector(getKerberosAuthInjector());
                break;
            case 1 :
                GuiceServletConfig.setInjector(getSimpleAuthInjector());
                break;
        }
    }

    // Simple test - try to create a delegation token via web services and check
    // to make sure we get back a valid token. Validate token using RM function
    // calls. It should only succeed with the kerberos filter
    @Test
    public void testCreateDelegationToken() throws Exception {
        start();
        client().addFilter(new LoggingFilter(System.out));
        final String renewer = "test-renewer";
        String jsonBody = ("{ \"renewer\" : \"" + renewer) + "\" }";
        String xmlBody = ("<delegation-token><renewer>" + renewer) + "</renewer></delegation-token>";
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put(APPLICATION_JSON, jsonBody);
        bodyMap.put(APPLICATION_XML, xmlBody);
        for (final String mediaType : mediaTypes) {
            final String body = bodyMap.get(mediaType);
            for (final String contentType : mediaTypes) {
                if ((isKerberosAuth) == true) {
                    verifyKerberosAuthCreate(mediaType, contentType, body, renewer);
                } else {
                    verifySimpleAuthCreate(mediaType, contentType, body);
                }
            }
        }
        stop();
        return;
    }

    // Test to verify renew functionality - create a token and then try to renew
    // it. The renewer should succeed; owner and third user should fail
    @Test
    public void testRenewDelegationToken() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        start();
        final String renewer = "client2";
        client().addFilter(new LoggingFilter(System.out));
        final DelegationToken dummyToken = new DelegationToken();
        dummyToken.setRenewer(renewer);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (final String mediaType : mediaTypes) {
            for (final String contentType : mediaTypes) {
                if ((isKerberosAuth) == false) {
                    verifySimpleAuthRenew(mediaType, contentType);
                    continue;
                }
                // test "client" and client2" trying to renew "client" token
                final DelegationToken responseToken = KerberosTestUtils.doAsClient(new Callable<DelegationToken>() {
                    @Override
                    public DelegationToken call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").accept(contentType).entity(dummyToken, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        DelegationToken tok = getDelegationTokenFromResponse(response);
                        Assert.assertFalse(tok.getToken().isEmpty());
                        String body = TestRMWebServicesDelegationTokens.generateRenewTokenBody(mediaType, tok.getToken());
                        response = resource().path("ws").path("v1").path("cluster").path("delegation-token").path("expiration").header(yarnTokenHeader, tok.getToken()).accept(contentType).entity(body, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(FORBIDDEN, response.getStatusInfo());
                        return tok;
                    }
                });
                KerberosTestUtils.doAs(renewer, new Callable<DelegationToken>() {
                    @Override
                    public DelegationToken call() throws Exception {
                        // renew twice so that we can confirm that the
                        // expiration time actually changes
                        long oldExpirationTime = Time.now();
                        assertValidRMToken(responseToken.getToken());
                        String body = TestRMWebServicesDelegationTokens.generateRenewTokenBody(mediaType, responseToken.getToken());
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").path("expiration").header(yarnTokenHeader, responseToken.getToken()).accept(contentType).entity(body, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        DelegationToken tok = getDelegationTokenFromResponse(response);
                        String message = (("Expiration time not as expected: old = " + oldExpirationTime) + "; new = ") + (tok.getNextExpirationTime());
                        Assert.assertTrue(message, ((tok.getNextExpirationTime()) > oldExpirationTime));
                        oldExpirationTime = tok.getNextExpirationTime();
                        // artificial sleep to ensure we get a different expiration time
                        Thread.sleep(1000);
                        response = resource().path("ws").path("v1").path("cluster").path("delegation-token").path("expiration").header(yarnTokenHeader, responseToken.getToken()).accept(contentType).entity(body, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        tok = getDelegationTokenFromResponse(response);
                        message = (("Expiration time not as expected: old = " + oldExpirationTime) + "; new = ") + (tok.getNextExpirationTime());
                        Assert.assertTrue(message, ((tok.getNextExpirationTime()) > oldExpirationTime));
                        return tok;
                    }
                });
                // test unauthorized user renew attempt
                KerberosTestUtils.doAs("client3", new Callable<DelegationToken>() {
                    @Override
                    public DelegationToken call() throws Exception {
                        String body = TestRMWebServicesDelegationTokens.generateRenewTokenBody(mediaType, responseToken.getToken());
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").path("expiration").header(yarnTokenHeader, responseToken.getToken()).accept(contentType).entity(body, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(FORBIDDEN, response.getStatusInfo());
                        return null;
                    }
                });
                // test bad request - incorrect format, empty token string and random
                // token string
                KerberosTestUtils.doAsClient(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        String token = "TEST_TOKEN_STRING";
                        String body = "";
                        if (mediaType.equals(APPLICATION_JSON)) {
                            body = ("{\"token\": \"" + token) + "\" }";
                        } else {
                            body = ("<delegation-token><token>" + token) + "</token></delegation-token>";
                        }
                        // missing token header
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").path("expiration").accept(contentType).entity(body, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
                        return null;
                    }
                });
            }
        }
        stop();
        return;
    }

    // Test to verify cancel functionality - create a token and then try to cancel
    // it. The owner and renewer should succeed; third user should fail
    @Test
    public void testCancelDelegationToken() throws Exception {
        start();
        client().addFilter(new LoggingFilter(System.out));
        if ((isKerberosAuth) == false) {
            verifySimpleAuthCancel();
            return;
        }
        final DelegationToken dtoken = new DelegationToken();
        String renewer = "client2";
        dtoken.setRenewer(renewer);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (final String mediaType : mediaTypes) {
            for (final String contentType : mediaTypes) {
                // owner should be able to cancel delegation token
                KerberosTestUtils.doAsClient(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").accept(contentType).entity(dtoken, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        DelegationToken tok = getDelegationTokenFromResponse(response);
                        response = resource().path("ws").path("v1").path("cluster").path("delegation-token").header(yarnTokenHeader, tok.getToken()).accept(contentType).delete(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        assertTokenCancelled(tok.getToken());
                        return null;
                    }
                });
                // renewer should be able to cancel token
                final DelegationToken tmpToken = KerberosTestUtils.doAsClient(new Callable<DelegationToken>() {
                    @Override
                    public DelegationToken call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").accept(contentType).entity(dtoken, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        DelegationToken tok = getDelegationTokenFromResponse(response);
                        return tok;
                    }
                });
                KerberosTestUtils.doAs(renewer, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").header(yarnTokenHeader, tmpToken.getToken()).accept(contentType).delete(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        assertTokenCancelled(tmpToken.getToken());
                        return null;
                    }
                });
                // third user should not be able to cancel token
                final DelegationToken tmpToken2 = KerberosTestUtils.doAsClient(new Callable<DelegationToken>() {
                    @Override
                    public DelegationToken call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").accept(contentType).entity(dtoken, mediaType).post(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                        DelegationToken tok = getDelegationTokenFromResponse(response);
                        return tok;
                    }
                });
                KerberosTestUtils.doAs("client3", new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ClientResponse response = resource().path("ws").path("v1").path("cluster").path("delegation-token").header(yarnTokenHeader, tmpToken2.getToken()).accept(contentType).delete(ClientResponse.class);
                        WebServicesTestUtils.assertResponseStatusCode(FORBIDDEN, response.getStatusInfo());
                        assertValidRMToken(tmpToken2.getToken());
                        return null;
                    }
                });
                testCancelTokenBadRequests(mediaType, contentType);
            }
        }
        stop();
        return;
    }
}

