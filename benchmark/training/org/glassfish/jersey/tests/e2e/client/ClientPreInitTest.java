/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.client;


import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test pre initialization of the client.
 *
 * @author Miroslav Fuksa
 */
public class ClientPreInitTest extends JerseyTest {
    @Path("resource")
    public static class Resource {
        @GET
        public String get(@HeaderParam("filter-request")
        String header) {
            return "resource:" + (header == null ? "<null>" : header);
        }

        @GET
        @Path("child")
        public String getChild(@HeaderParam("filter-request")
        String header) {
            return "child:" + (header == null ? "<null>" : header);
        }
    }

    public static class MyRequestFilter implements ClientRequestFilter {
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add("filter-request", "called");
        }
    }

    public static class MyResponseFilter implements ClientResponseFilter {
        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add("filter-response", "called");
        }
    }

    public static class TestReader implements MessageBodyReader<Integer> {
        public static boolean initialized;

        public TestReader() {
            ClientPreInitTest.TestReader.initialized = true;
        }

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override
        public Integer readFrom(Class<Integer> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    }

    @Test
    public void testNonInitialized() {
        Client client = ClientBuilder.newClient();
        client.register(ClientPreInitTest.MyResponseFilter.class);
        client.register(ClientPreInitTest.TestReader.class);
        final WebTarget target = client.target(getBaseUri()).path("resource");
        Assert.assertFalse(ClientPreInitTest.TestReader.initialized);
        final Response resourceResponse = target.request().get();
        checkResponse(resourceResponse, "resource:<null>");
        Assert.assertTrue(ClientPreInitTest.TestReader.initialized);
    }

    @Test
    public void testSimplePreinitialize() {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(getBaseUri()).path("resource");
        target.register(ClientPreInitTest.MyResponseFilter.class);
        final WebTarget childTarget = target.path("child");
        preInitialize();
        final Response response = childTarget.request().get();
        checkResponse(response, "child:<null>");
        final Response resourceResponse = target.request().get();
        checkResponse(resourceResponse, "resource:<null>");
    }

    @Test
    public void testReusingPreinitializedConfig() {
        Client client = ClientBuilder.newClient();
        client.register(ClientPreInitTest.TestReader.class);
        final WebTarget target = client.target(getBaseUri()).path("resource");
        target.register(ClientPreInitTest.MyResponseFilter.class);
        preInitialize();
        Assert.assertTrue(ClientPreInitTest.TestReader.initialized);
        final WebTarget childTarget = target.path("child");
        final Response response = childTarget.request().get();
        checkResponse(response, "child:<null>");
        final Response resourceResponse = target.request().get();
        checkResponse(resourceResponse, "resource:<null>");
    }

    @Test
    public void testReusingPreinitializedConfig2() {
        Client client = ClientBuilder.newClient();
        client.register(ClientPreInitTest.TestReader.class);
        client.register(ClientPreInitTest.MyResponseFilter.class);
        Assert.assertFalse(ClientPreInitTest.TestReader.initialized);
        preInitialize();
        Assert.assertTrue(ClientPreInitTest.TestReader.initialized);
        final WebTarget target = client.target(getBaseUri()).path("resource");
        final WebTarget childTarget = target.path("child");
        final Response response = childTarget.request().get();
        checkResponse(response, "child:<null>");
        final Response resourceResponse = target.request().get();
        checkResponse(resourceResponse, "resource:<null>");
    }

    @Test
    public void testRegisterOnPreinitialized1() {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(getBaseUri()).path("resource");
        target.register(ClientPreInitTest.MyRequestFilter.class);
        preInitialize();
        target.register(ClientPreInitTest.MyResponseFilter.class);
        final Response response = target.request().get();
        checkResponse(response, "resource:called");
    }

    @Test
    public void testRegisterOnPreinitialized2() {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(getBaseUri()).path("resource");
        target.register(ClientPreInitTest.MyResponseFilter.class);
        preInitialize();
        final WebTarget child = target.path("child");
        child.register(ClientPreInitTest.MyRequestFilter.class);
        final Response response = target.request().get();
        checkResponse(response, "resource:<null>");
        final Response childResponse = child.request().get();
        checkResponse(childResponse, "child:called");
    }
}

