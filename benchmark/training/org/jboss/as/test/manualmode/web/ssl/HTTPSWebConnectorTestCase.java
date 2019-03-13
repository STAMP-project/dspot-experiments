/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.manualmode.web.ssl;


import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import PrincipalPrintingServlet.SERVLET_PATH;
import SecurityTestConstants.KEYSTORE_PASSWORD;
import SimpleSecuredServlet.ALLOWED_ROLE;
import SimpleSecuredServlet.RESPONSE_BODY;
import SystemUtils.JAVA_VENDOR;
import java.io.File;
import java.net.SocketException;
import java.net.URL;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.apache.commons.lang.SystemUtils;
import org.apache.http.client.HttpClient;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.AddRoleLoginModule;
import org.jboss.as.test.integration.security.common.SSLTruststoreUtil;
import org.jboss.as.test.integration.security.common.SecurityTestConstants;
import org.jboss.as.test.integration.security.common.SecurityTraceLoggingServerSetupTask;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.config.JSSE;
import org.jboss.as.test.integration.security.common.config.SecureStore;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.servlets.SimpleSecuredServlet;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.jboss.security.auth.spi.BaseCertLoginModule;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Testing https connection to Web Connector with configured two-way SSL.
 * HTTP client has set client keystore with valid/invalid certificate, which is used for
 * authentication to management interface. Result of authentication depends on whether client
 * certificate is accepted in server truststore. HTTP client uses client truststore with accepted
 * server certificate to authenticate server identity.
 *
 * Keystores and truststores have valid certificates until 25 Octover 2033.
 *
 * @author Filip Bogyai
 * @author Josef cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(CommonCriteria.class)
public class HTTPSWebConnectorTestCase {
    private static final String STANDARD_SOCKETS = "standard-sockets";

    private static final String HTTPS = "https";

    public static final int HTTPS_PORT = 8444;

    private static Logger LOGGER = Logger.getLogger(HTTPSWebConnectorTestCase.class);

    private static SecurityTraceLoggingServerSetupTask TRACE_SECURITY = new SecurityTraceLoggingServerSetupTask();

    private static final File WORK_DIR = new File("https-workdir");

    public static final File SERVER_KEYSTORE_FILE = new File(HTTPSWebConnectorTestCase.WORK_DIR, SecurityTestConstants.SERVER_KEYSTORE);

    public static final File SERVER_TRUSTSTORE_FILE = new File(HTTPSWebConnectorTestCase.WORK_DIR, SecurityTestConstants.SERVER_TRUSTSTORE);

    public static final File CLIENT_KEYSTORE_FILE = new File(HTTPSWebConnectorTestCase.WORK_DIR, SecurityTestConstants.CLIENT_KEYSTORE);

    public static final File CLIENT_TRUSTSTORE_FILE = new File(HTTPSWebConnectorTestCase.WORK_DIR, SecurityTestConstants.CLIENT_TRUSTSTORE);

    public static final File UNTRUSTED_KEYSTORE_FILE = new File(HTTPSWebConnectorTestCase.WORK_DIR, SecurityTestConstants.UNTRUSTED_KEYSTORE);

    private static final String HTTPS_NAME_VERIFY_NOT_REQUESTED = "https-verify-not-requested";

    private static final String HTTPS_NAME_VERIFY_REQUESTED = "https-verify-requested";

    private static final String HTTPS_NAME_VERIFY_REQUIRED = "https-verify-required";

    private static final String HTTPS_REALM = "httpsRealm";

    private static final String APP_CONTEXT = HTTPSWebConnectorTestCase.HTTPS;

    private static final String SECURED_SERVLET_WITH_SESSION = (((SimpleSecuredServlet.SERVLET_PATH) + "?") + (SimpleSecuredServlet.CREATE_SESSION_PARAM)) + "=true";

    private static final String SECURITY_DOMAIN_CERT = "client cert domain";

    private static final String SECURITY_DOMAIN_JSSE = "jsse_!@#$%^&*()_domain";

    public static final String CONTAINER = "default-jbossas";

    @ArquillianResource
    private static ContainerController containerController;

    @ArquillianResource
    private Deployer deployer;

    @Test
    @InSequence(-1)
    public void startAndSetupContainer() throws Exception {
        HTTPSWebConnectorTestCase.LOGGER.trace("*** starting server");
        HTTPSWebConnectorTestCase.containerController.start(HTTPSWebConnectorTestCase.CONTAINER);
        ModelControllerClient client = TestSuiteEnvironment.getModelControllerClient();
        ManagementClient managementClient = new ManagementClient(client, TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getServerPort(), "remote+http");
        HTTPSWebConnectorTestCase.LOGGER.trace("*** will configure server now");
        serverSetup(managementClient);
        deployer.deploy(HTTPSWebConnectorTestCase.APP_CONTEXT);
    }

    /**
     *
     *
     * @unknown tsfi.keystore.file
     * @unknown tsfi.truststore.file
     * @unknown Testing default HTTPs connector with verify-client attribute set to "false". The CLIENT-CERT
    authentication (BaseCertLoginModule) is configured for this test. Trusted client is allowed to access
    both secured/unsecured resource. Untrusted client can only access unprotected resources.
     * @unknown Trusted client has access to protected and unprotected resources. Untrusted client has only access
    to unprotected resources.
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(1)
    public void testNonVerifyingConnector() throws Exception {
        Assume.assumeFalse(((SystemUtils.IS_JAVA_1_6) && (JAVA_VENDOR.toUpperCase(Locale.ENGLISH).contains("IBM"))));
        final URL printPrincipalUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_FALSE, SERVLET_PATH);
        final URL securedUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_FALSE, HTTPSWebConnectorTestCase.SECURED_SERVLET_WITH_SESSION);
        final URL unsecuredUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_FALSE, SimpleServlet.SERVLET_PATH);
        final HttpClient httpClient = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.CLIENT_KEYSTORE_FILE);
        final HttpClient httpClientUntrusted = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.UNTRUSTED_KEYSTORE_FILE);
        try {
            Utils.makeCallWithHttpClient(printPrincipalUrl, httpClient, SC_FORBIDDEN);
            String responseBody = Utils.makeCallWithHttpClient(securedUrl, httpClient, SC_OK);
            Assert.assertEquals("Secured page was not reached", RESPONSE_BODY, responseBody);
            String principal = Utils.makeCallWithHttpClient(printPrincipalUrl, httpClient, SC_OK);
            Assert.assertEquals("Unexpected principal", "cn=client", principal.toLowerCase());
            responseBody = Utils.makeCallWithHttpClient(unsecuredUrl, httpClientUntrusted, SC_OK);
            Assert.assertEquals("Secured page was not reached", SimpleServlet.RESPONSE_BODY, responseBody);
            try {
                Utils.makeCallWithHttpClient(securedUrl, httpClientUntrusted, SC_FORBIDDEN);
            } catch (SSLHandshakeException e) {
                // OK
            } catch (SocketException se) {
                // OK - on windows usually fails with this one
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
            httpClientUntrusted.getConnectionManager().shutdown();
        }
    }

    /**
     *
     *
     * @unknown tsfi.keystore.file
     * @unknown tsfi.truststore.file
     * @unknown Testing default HTTPs connector with verify-client attribute set to "want". The CLIENT-CERT
    authentication (BaseCertLoginModule) is configured for this test. Trusted client is allowed to access
    both secured/unsecured resource. Untrusted client can only access unprotected resources.
     * @unknown Trusted client has access to protected and unprotected resources. Untrusted client has only access
    to unprotected resources.
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(1)
    public void testWantVerifyConnector() throws Exception {
        Assume.assumeFalse(((SystemUtils.IS_JAVA_1_6) && (JAVA_VENDOR.toUpperCase(Locale.ENGLISH).contains("IBM"))));
        final URL printPrincipalUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_WANT, SERVLET_PATH);
        final URL securedUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_WANT, HTTPSWebConnectorTestCase.SECURED_SERVLET_WITH_SESSION);
        final URL unsecuredUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_WANT, SimpleServlet.SERVLET_PATH);
        final HttpClient httpClient = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.CLIENT_KEYSTORE_FILE);
        final HttpClient httpClientUntrusted = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.UNTRUSTED_KEYSTORE_FILE);
        try {
            Utils.makeCallWithHttpClient(printPrincipalUrl, httpClientUntrusted, SC_FORBIDDEN);
            final String principal = Utils.makeCallWithHttpClient(printPrincipalUrl, httpClient, SC_OK);
            Assert.assertEquals("Unexpected principal", "cn=client", principal.toLowerCase());
            String responseBody = Utils.makeCallWithHttpClient(unsecuredUrl, httpClient, SC_OK);
            Assert.assertEquals("Unsecured page was not reached", RESPONSE_BODY, responseBody);
            responseBody = Utils.makeCallWithHttpClient(securedUrl, httpClient, SC_OK);
            Assert.assertEquals("Secured page was not reached", RESPONSE_BODY, responseBody);
            responseBody = Utils.makeCallWithHttpClient(unsecuredUrl, httpClientUntrusted, SC_OK);
            Assert.assertEquals("Unsecured page was not reached", SimpleServlet.RESPONSE_BODY, responseBody);
            Utils.makeCallWithHttpClient(securedUrl, httpClientUntrusted, SC_FORBIDDEN);
        } finally {
            httpClient.getConnectionManager().shutdown();
            httpClientUntrusted.getConnectionManager().shutdown();
        }
    }

    /**
     *
     *
     * @unknown tsfi.keystore.file
     * @unknown tsfi.truststore.file
     * @unknown Testing default HTTPs connector with verify-client attribute set to "true". The CLIENT-CERT
    authentication (BaseCertLoginModule) is configured for this test. Trusted client is allowed to access
    both secured/unsecured resource. Untrusted client is not allowed to access anything.
     * @unknown Trusted client has access to protected and unprotected resources. Untrusted client can't access
    anything.
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(1)
    public void testVerifyingConnector() throws Exception {
        final HttpClient httpClient = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.CLIENT_KEYSTORE_FILE);
        final HttpClient httpClientUntrusted = HTTPSWebConnectorTestCase.getHttpClient(HTTPSWebConnectorTestCase.UNTRUSTED_KEYSTORE_FILE);
        try {
            final URL printPrincipalUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_TRUE, SERVLET_PATH);
            final URL securedUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_TRUE, HTTPSWebConnectorTestCase.SECURED_SERVLET_WITH_SESSION);
            final URL unsecuredUrl = getServletUrl(SSLTruststoreUtil.HTTPS_PORT_VERIFY_TRUE, SimpleServlet.SERVLET_PATH);
            String principal = Utils.makeCallWithHttpClient(printPrincipalUrl, httpClient, SC_OK);
            Assert.assertEquals("Unexpected principal", "cn=client", principal.toLowerCase());
            String responseBody = Utils.makeCallWithHttpClient(securedUrl, httpClient, SC_OK);
            Assert.assertEquals("Secured page was not reached", RESPONSE_BODY, responseBody);
            try {
                Utils.makeCallWithHttpClient(unsecuredUrl, httpClientUntrusted, SC_FORBIDDEN);
                Assert.fail("Untrusted client should not be authenticated.");
            } catch (SSLHandshakeException | SSLPeerUnverifiedException | SocketException e) {
                // depending on the OS and the version of HTTP client in use any one of these exceptions may be thrown
                // in particular the SocketException gets thrown on Windows
                // OK
            } catch (SSLException e) {
                if (!((e.getCause()) instanceof SocketException)) {
                    // OK
                    throw e;
                }
            }
            try {
                Utils.makeCallWithHttpClient(printPrincipalUrl, httpClientUntrusted, SC_FORBIDDEN);
                Assert.fail("Untrusted client should not be authenticated.");
            } catch (SSLHandshakeException | SSLPeerUnverifiedException | SocketException e) {
                // OK
            } catch (SSLException e) {
                if (!((e.getCause()) instanceof SocketException)) {
                    // OK
                    throw e;
                }
            }
            try {
                Utils.makeCallWithHttpClient(securedUrl, httpClientUntrusted, SC_FORBIDDEN);
                Assert.fail("Untrusted client should not be authenticated.");
            } catch (SSLHandshakeException | SSLPeerUnverifiedException | SocketException e) {
                // OK
            } catch (SSLException e) {
                if (!((e.getCause()) instanceof SocketException)) {
                    // OK
                    throw e;
                }
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
            httpClientUntrusted.getConnectionManager().shutdown();
        }
    }

    @Test
    @InSequence(3)
    public void stopContainer() throws Exception {
        deployer.undeploy(HTTPSWebConnectorTestCase.APP_CONTEXT);
        final ModelControllerClient client = TestSuiteEnvironment.getModelControllerClient();
        final ManagementClient managementClient = new ManagementClient(client, TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getServerPort(), "remote+http");
        HTTPSWebConnectorTestCase.LOGGER.trace("*** reseting test configuration");
        serverTearDown(managementClient);
        HTTPSWebConnectorTestCase.LOGGER.trace("*** stopping container");
        HTTPSWebConnectorTestCase.containerController.stop(HTTPSWebConnectorTestCase.CONTAINER);
    }

    /**
     * Security domains configuration for this test.
     */
    private static class SecurityDomainsSetup extends AbstractSecurityDomainsServerSetupTask {
        private static final HTTPSWebConnectorTestCase.SecurityDomainsSetup INSTANCE = new HTTPSWebConnectorTestCase.SecurityDomainsSetup();

        @Override
        protected SecurityDomain[] getSecurityDomains() throws Exception {
            final SecurityDomain sd = // 
            new SecurityDomain.Builder().name(HTTPSWebConnectorTestCase.SECURITY_DOMAIN_CERT).loginModules(new SecurityModule.Builder().name(BaseCertLoginModule.class.getName()).putOption("securityDomain", HTTPSWebConnectorTestCase.SECURITY_DOMAIN_JSSE).putOption("password-stacking", "useFirstPass").build(), new SecurityModule.Builder().name(AddRoleLoginModule.class.getName()).flag("optional").putOption("password-stacking", "useFirstPass").putOption("roleName", ALLOWED_ROLE).build()).build();
            final SecurityDomain sdJsse = // 
            new SecurityDomain.Builder().name(HTTPSWebConnectorTestCase.SECURITY_DOMAIN_JSSE).jsse(// 
            new JSSE.Builder().trustStore(new SecureStore.Builder().type("JKS").url(HTTPSWebConnectorTestCase.SERVER_TRUSTSTORE_FILE.toURI().toURL()).password(KEYSTORE_PASSWORD).build()).build()).build();
            return new SecurityDomain[]{ sdJsse, sd };
        }
    }
}

