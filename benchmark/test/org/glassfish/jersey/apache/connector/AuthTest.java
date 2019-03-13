/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.apache.connector;


import ApacheClientProperties.CREDENTIALS_PROVIDER;
import ApacheClientProperties.PREEMPTIVE_BASIC_AUTHENTICATION;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Arul Dhesiaseelan (aruld at acm.org)
 */
public class AuthTest extends JerseyTest {
    @Path("/")
    public static class PreemptiveAuthResource {
        @GET
        public String get(@Context
        HttpHeaders h) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            Assert.assertNotNull(value);
            return "GET";
        }

        @POST
        public String post(@Context
        HttpHeaders h, String e) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            Assert.assertNotNull(value);
            return e;
        }
    }

    @Test
    public void testPreemptiveAuth() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.property(CREDENTIALS_PROVIDER, credentialsProvider).property(PREEMPTIVE_BASIC_AUTHENTICATION, true);
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri());
        Assert.assertEquals("GET", r.request().get(String.class));
    }

    @Test
    public void testPreemptiveAuthPost() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.property(CREDENTIALS_PROVIDER, credentialsProvider).property(PREEMPTIVE_BASIC_AUTHENTICATION, true);
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri());
        Assert.assertEquals("POST", r.request().post(javax.ws.rs.client.Entity.text("POST"), String.class));
    }

    @Path("/test")
    @Singleton
    public static class AuthResource {
        int requestCount = 0;

        @GET
        public String get(@Context
        HttpHeaders h) {
            (requestCount)++;
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                Assert.assertEquals(1, requestCount);
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            } else {
                Assert.assertTrue(((requestCount) > 1));
            }
            return "GET";
        }

        @GET
        @Path("filter")
        public String getFilter(@Context
        HttpHeaders h) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            }
            return "GET";
        }

        @POST
        public String post(@Context
        HttpHeaders h, String e) {
            (requestCount)++;
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                Assert.assertEquals(1, requestCount);
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            } else {
                Assert.assertTrue(((requestCount) > 1));
            }
            return e;
        }

        @POST
        @Path("filter")
        public String postFilter(@Context
        HttpHeaders h, String e) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            }
            return e;
        }

        @DELETE
        public void delete(@Context
        HttpHeaders h) {
            (requestCount)++;
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                Assert.assertEquals(1, requestCount);
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            } else {
                Assert.assertTrue(((requestCount) > 1));
            }
        }

        @DELETE
        @Path("filter")
        public void deleteFilter(@Context
        HttpHeaders h) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            }
        }

        @DELETE
        @Path("filter/withEntity")
        public String deleteFilterWithEntity(@Context
        HttpHeaders h, String e) {
            String value = h.getRequestHeaders().getFirst("Authorization");
            if (value == null) {
                throw new javax.ws.rs.WebApplicationException(Response.status(401).header("WWW-Authenticate", "Basic realm=\"WallyWorld\"").build());
            }
            return e;
        }
    }

    @Test
    public void testAuthGet() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.property(CREDENTIALS_PROVIDER, credentialsProvider);
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri()).path("test");
        Assert.assertEquals("GET", r.request().get(String.class));
    }

    @Test
    public void testAuthGetWithRequestCredentialsProvider() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri()).path("test");
        Assert.assertEquals("GET", r.request().property(CREDENTIALS_PROVIDER, credentialsProvider).get(String.class));
    }

    @Test
    public void testAuthGetWithClientFilter() {
        ClientConfig cc = new ClientConfig();
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        client.register(HttpAuthenticationFeature.basic("name", "password"));
        WebTarget r = client.target(getBaseUri()).path("test/filter");
        Assert.assertEquals("GET", r.request().get(String.class));
    }

    @Test
    public void testAuthPostWithClientFilter() {
        ClientConfig cc = new ClientConfig();
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        client.register(HttpAuthenticationFeature.basic("name", "password"));
        WebTarget r = client.target(getBaseUri()).path("test/filter");
        Assert.assertEquals("POST", r.request().post(javax.ws.rs.client.Entity.text("POST"), String.class));
    }

    @Test
    public void testAuthDelete() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.property(CREDENTIALS_PROVIDER, credentialsProvider);
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri()).path("test");
        Response response = r.request().delete();
        Assert.assertEquals(response.getStatus(), 204);
    }

    @Test
    public void testAuthDeleteWithClientFilter() {
        ClientConfig cc = new ClientConfig();
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        client.register(HttpAuthenticationFeature.basic("name", "password"));
        WebTarget r = client.target(getBaseUri()).path("test/filter");
        Response response = r.request().delete();
        Assert.assertEquals(204, response.getStatus());
    }

    @Test
    public void testAuthInteractiveGet() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("name", "password"));
        ClientConfig cc = new ClientConfig();
        cc.property(CREDENTIALS_PROVIDER, credentialsProvider);
        cc.connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(cc);
        WebTarget r = client.target(getBaseUri()).path("test");
        Assert.assertEquals("GET", r.request().get(String.class));
    }
}

