/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.client.filter;


import ClientProperties.USE_ENCODING;
import Invocation.Builder;
import MediaType.TEXT_PLAIN_TYPE;
import Response.Status;
import Response.Status.OK;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Client-side content encoding filter unit tests.
 *
 * @author Martin Matula
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class EncodingFilterTest {
    @Test
    public void testAcceptEncoding() {
        Client client = ClientBuilder.newClient(new ClientConfig(EncodingFilter.class, GZipEncoder.class, DeflateEncoder.class).connectorProvider(new EncodingFilterTest.TestConnector()));
        Invocation.Builder invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
        Response r = invBuilder.get();
        Assert.assertEquals("deflate,gzip,x-gzip", r.getHeaderString(HttpHeaders.ACCEPT_ENCODING));
        Assert.assertNull(r.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test
    public void testContentEncoding() {
        Client client = ClientBuilder.newClient(new ClientConfig(EncodingFilter.class, GZipEncoder.class, DeflateEncoder.class).property(USE_ENCODING, "gzip").connectorProvider(new EncodingFilterTest.TestConnector()));
        Invocation.Builder invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
        Response r = invBuilder.post(Entity.entity("Hello world", TEXT_PLAIN_TYPE));
        Assert.assertEquals("deflate,gzip,x-gzip", r.getHeaderString(HttpHeaders.ACCEPT_ENCODING));
        Assert.assertEquals("gzip", r.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test
    public void testContentEncodingViaFeature() {
        Client client = ClientBuilder.newClient(new ClientConfig().connectorProvider(new EncodingFilterTest.TestConnector()).register(new EncodingFeature("gzip", GZipEncoder.class, DeflateEncoder.class)));
        Invocation.Builder invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
        Response r = invBuilder.post(Entity.entity("Hello world", TEXT_PLAIN_TYPE));
        Assert.assertEquals("deflate,gzip,x-gzip", r.getHeaderString(HttpHeaders.ACCEPT_ENCODING));
        Assert.assertEquals("gzip", r.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test
    public void testContentEncodingSkippedForNoEntity() {
        Client client = ClientBuilder.newClient(new ClientConfig(EncodingFilter.class, GZipEncoder.class, DeflateEncoder.class).property(USE_ENCODING, "gzip").connectorProvider(new EncodingFilterTest.TestConnector()));
        Invocation.Builder invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
        Response r = invBuilder.get();
        Assert.assertEquals("deflate,gzip,x-gzip", r.getHeaderString(HttpHeaders.ACCEPT_ENCODING));
        Assert.assertNull(r.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test
    public void testUnsupportedContentEncoding() {
        Client client = ClientBuilder.newClient(new ClientConfig(EncodingFilter.class, GZipEncoder.class, DeflateEncoder.class).property(USE_ENCODING, "non-gzip").connectorProvider(new EncodingFilterTest.TestConnector()));
        Invocation.Builder invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
        Response r = invBuilder.get();
        Assert.assertEquals("deflate,gzip,x-gzip", r.getHeaderString(HttpHeaders.ACCEPT_ENCODING));
        Assert.assertNull(r.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    /**
     * Reproducer for JERSEY-2028.
     *
     * @see #testClosingClientResponseStreamRetrievedByValueOnError
     */
    @Test
    public void testClosingClientResponseStreamRetrievedByResponseOnError() {
        final EncodingFilterTest.TestInputStream responseStream = new EncodingFilterTest.TestInputStream();
        Client client = ClientBuilder.newClient(new ClientConfig().connectorProvider(new EncodingFilterTest.TestConnector() {
            @Override
            public ClientResponse apply(ClientRequest requestContext) throws ProcessingException {
                final ClientResponse responseContext = new ClientResponse(Status.OK, requestContext);
                responseContext.header(HttpHeaders.CONTENT_ENCODING, "gzip");
                responseContext.setEntityStream(responseStream);
                return responseContext;
            }
        }).register(new EncodingFeature(GZipEncoder.class, DeflateEncoder.class)));
        final Response response = client.target(UriBuilder.fromUri("/").build()).request().get();
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("gzip", response.getHeaderString(HttpHeaders.CONTENT_ENCODING));
        try {
            response.readEntity(String.class);
            Assert.fail("Exception caused by invalid gzip stream expected.");
        } catch (ProcessingException ex) {
            Assert.assertTrue("Response input stream not closed when exception is thrown.", responseStream.isClosed);
        }
    }

    /**
     * Reproducer for JERSEY-2028.
     *
     * @see #testClosingClientResponseStreamRetrievedByResponseOnError
     */
    @Test
    public void testClosingClientResponseStreamRetrievedByValueOnError() {
        final EncodingFilterTest.TestInputStream responseStream = new EncodingFilterTest.TestInputStream();
        Client client = ClientBuilder.newClient(new ClientConfig().connectorProvider(new EncodingFilterTest.TestConnector() {
            @Override
            public ClientResponse apply(ClientRequest requestContext) throws ProcessingException {
                final ClientResponse responseContext = new ClientResponse(Status.OK, requestContext);
                responseContext.header(HttpHeaders.CONTENT_ENCODING, "gzip");
                responseContext.setEntityStream(responseStream);
                return responseContext;
            }
        }).register(new EncodingFeature(GZipEncoder.class, DeflateEncoder.class)));
        try {
            client.target(UriBuilder.fromUri("/").build()).request().get(String.class);
            Assert.fail("Exception caused by invalid gzip stream expected.");
        } catch (ProcessingException ex) {
            Assert.assertTrue("Response input stream not closed when exception is thrown.", responseStream.isClosed);
        }
    }

    private static class TestConnector implements Connector , ConnectorProvider {
        @Override
        public Connector getConnector(Client client, Configuration runtimeConfig) {
            return this;
        }

        @Override
        public ClientResponse apply(ClientRequest requestContext) {
            final ClientResponse responseContext = new ClientResponse(Status.OK, requestContext);
            String headerValue = requestContext.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
            if (headerValue != null) {
                responseContext.header(HttpHeaders.ACCEPT_ENCODING, headerValue);
            }
            headerValue = requestContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);
            if (headerValue != null) {
                responseContext.header(HttpHeaders.CONTENT_ENCODING, headerValue);
            }
            return responseContext;
        }

        @Override
        public Future<?> apply(ClientRequest clientRequest, AsyncConnectorCallback callback) {
            throw new UnsupportedOperationException("Asynchronous execution not supported.");
        }

        @Override
        public void close() {
            // do nothing
        }

        @Override
        public String getName() {
            return "test-connector";
        }
    }

    private static class TestInputStream extends ByteArrayInputStream {
        private boolean isClosed;

        private TestInputStream() {
            super("test".getBytes());
        }

        @Override
        public void close() throws IOException {
            isClosed = true;
            super.close();
        }
    }
}

