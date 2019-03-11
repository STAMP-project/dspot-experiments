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


import HttpUrlConnectorProvider.USE_FIXED_LENGTH_STREAMING;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests possibility of disabling buffering of outgoing entity in
 * {@link org.glassfish.jersey.client.HttpUrlConnectorProvider}.
 *
 * @author Miroslav Fuksa
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
@RunWith(ConcurrentRunner.class)
public class ClientBufferingDisabledTest extends JerseyTest {
    private static final long LENGTH = 200000000L;

    private static final int CHUNK = 2048;

    private static CountDownLatch postLatch = new CountDownLatch(1);

    @Path("resource")
    public static class MyResource {
        @POST
        public long post(InputStream is) throws IOException {
            int b;
            long count = 0;
            boolean firstByte = true;
            while ((b = is.read()) != (-1)) {
                if (firstByte) {
                    firstByte = false;
                    ClientBufferingDisabledTest.postLatch.countDown();
                }
                count++;
            } 
            return count;
        }
    }

    /**
     * Test that buffering can be disabled with {@link HttpURLConnection}. By default, the
     * {@code HttpURLConnection} buffers the output entity in order to calculate the
     * Content-length request attribute. This cause problems for large entities.
     * <p>
     * This test uses {@link HttpUrlConnectorProvider#USE_FIXED_LENGTH_STREAMING} to enable
     * fix length streaming on {@code HttpURLConnection}.
     */
    @Test
    public void testDisableBufferingWithFixedLengthViaProperty() {
        ClientBufferingDisabledTest.postLatch = new CountDownLatch(1);
        // This IS sends out 10 chunks and waits whether they were received on the server. This tests
        // whether the buffering is disabled.
        InputStream is = getInputStream();
        final HttpUrlConnectorProvider connectorProvider = new HttpUrlConnectorProvider();
        ClientConfig clientConfig = new ClientConfig().connectorProvider(connectorProvider);
        clientConfig.property(USE_FIXED_LENGTH_STREAMING, true);
        Client client = ClientBuilder.newClient(clientConfig);
        final Response response = client.target(getBaseUri()).path("resource").request().header(HttpHeaders.CONTENT_LENGTH, ClientBufferingDisabledTest.LENGTH).post(javax.ws.rs.client.Entity.entity(is, MediaType.APPLICATION_OCTET_STREAM));
        Assert.assertEquals(200, response.getStatus());
        final long count = response.readEntity(long.class);
        Assert.assertEquals("Unexpected content length received.", ClientBufferingDisabledTest.LENGTH, count);
    }

    /**
     * Test that buffering can be disabled with {@link HttpURLConnection}. By default, the
     * {@code HttpURLConnection} buffers the output entity in order to calculate the
     * Content-length request attribute. This cause problems for large entities.
     * <p>
     * This test uses {@link HttpUrlConnectorProvider#useFixedLengthStreaming()} to enable
     * fix length streaming on {@code HttpURLConnection}.
     */
    @Test
    public void testDisableBufferingWithFixedLengthViaMethod() {
        ClientBufferingDisabledTest.postLatch = new CountDownLatch(1);
        // This IS sends out 10 chunks and waits whether they were received on the server. This tests
        // whether the buffering is disabled.
        InputStream is = getInputStream();
        final HttpUrlConnectorProvider connectorProvider = new HttpUrlConnectorProvider().useFixedLengthStreaming();
        ClientConfig clientConfig = new ClientConfig().connectorProvider(connectorProvider);
        Client client = ClientBuilder.newClient(clientConfig);
        final Response response = client.target(getBaseUri()).path("resource").request().header(HttpHeaders.CONTENT_LENGTH, ClientBufferingDisabledTest.LENGTH).post(javax.ws.rs.client.Entity.entity(is, MediaType.APPLICATION_OCTET_STREAM));
        Assert.assertEquals(200, response.getStatus());
        final long count = response.readEntity(long.class);
        Assert.assertEquals("Unexpected content length received.", ClientBufferingDisabledTest.LENGTH, count);
    }
}

