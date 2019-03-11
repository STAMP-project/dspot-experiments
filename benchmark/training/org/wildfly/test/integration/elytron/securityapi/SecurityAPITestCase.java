/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.test.integration.elytron.securityapi;


import java.net.URI;
import java.net.URL;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;


/**
 * Test case to test the EE Security API with WildFly Elytron.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@org.jboss.as.arquillian.api.ServerSetup({ SecurityAPITestCase.ServerSetup.class })
public class SecurityAPITestCase {
    @ArquillianResource
    protected URL url;

    @Test
    public void testCalls() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "/test")));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Verify that we are not challenged.
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", "null", EntityUtils.toString(response.getEntity()));
            }
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?challenge=true")));
            // Now verify that we are challenged.
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_UNAUTHORIZED, statusCode);
                Assert.assertEquals("Unexpected challenge header", TestAuthenticationMechanism.MESSAGE, response.getFirstHeader(TestAuthenticationMechanism.MESSAGE_HEADER).getValue());
            }
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test")));
            // Verify a bad username and password results in a challenge.
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, "evil");
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, "password");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_UNAUTHORIZED, statusCode);
                Assert.assertEquals("Unexpected challenge header", TestAuthenticationMechanism.MESSAGE, response.getFirstHeader(TestAuthenticationMechanism.MESSAGE_HEADER).getValue());
            }
            // Verify a good username and password establishes an identity with the HttpServletRequest
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
            // Verify a good username and password establishes an identity with the SecurityDomain
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?source=SecurityDomain")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
            // Verify a good username and password establishes an identity with the SecurityContext
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?source=SecurityContext")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
            // Verify a good username and password establishes an identity with the EJB SessionContext
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?ejb=true")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
            // Verify a good username and password establishes an identity with the SecurityDomain within an EJB
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?ejb=true&source=SecurityDomain")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
            // Verify a good username and password establishes an identity with the SecurityContext within an EJB
            request = new HttpGet(new URI(((url.toExternalForm()) + "/test?ejb=true&source=SecurityContext")));
            request.addHeader(TestAuthenticationMechanism.USERNAME_HEADER, TestIdentityStore.USERNAME);
            request.addHeader(TestAuthenticationMechanism.PASSWORD_HEADER, TestIdentityStore.PASSWORD);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", TestIdentityStore.USERNAME, EntityUtils.toString(response.getEntity()));
            }
        }
    }

    static class ServerSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            ConfigurableElement[] elements = new ConfigurableElement[3];
            // 1 - Switch off PicketBox JACC
            elements[0] = new ConfigurableElement() {
                private final PathAddress ADDRESS = PathAddress.pathAddress(PathElement.pathElement("subsystem", "security"));

                @Override
                public String getName() {
                    return "Disable PicketBox JACC";
                }

                @Override
                public void create(ModelControllerClient client, CLIWrapper cli) throws Exception {
                    ModelNode write = Util.getWriteAttributeOperation(ADDRESS, "initialize-jacc", "false");
                    Utils.applyUpdate(write, client);
                }

                @Override
                public void remove(ModelControllerClient client, CLIWrapper cli) throws Exception {
                    ModelNode write = Util.getWriteAttributeOperation(ADDRESS, "initialize-jacc", "true");
                    Utils.applyUpdate(write, client);
                }
            };
            // 2 - Add empty JACC Policy
            elements[1] = withName("jacc").withJaccPolicy().build();
            // 3 - Map the application-security-domain
            elements[2] = withName("SecurityAPI").withSecurityDomain("ApplicationDomain").withIntegratedJaspi(false).build();
            return elements;
        }
    }
}

