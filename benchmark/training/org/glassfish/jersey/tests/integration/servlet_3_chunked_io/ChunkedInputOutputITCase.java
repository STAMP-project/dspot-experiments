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
package org.glassfish.jersey.tests.integration.servlet_3_chunked_io;


import MediaType.APPLICATION_JSON_TYPE;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Chunked I/O integration tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ChunkedInputOutputITCase extends JerseyTest {
    private static final int MAX_LISTENERS = 5;

    /**
     * Test retrieving string-based chunked stream as a single response string.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testChunkedOutputToSingleString() throws Exception {
        final String response = target().path("test/from-string").request(APPLICATION_JSON_TYPE).get(String.class);
        Assert.assertEquals("Unexpected value of chunked response unmarshalled as a single string.", ("{\"id\":0,\"data\":\"test\"}\r\n" + ("{\"id\":1,\"data\":\"test\"}\r\n" + "{\"id\":2,\"data\":\"test\"}\r\n")), response);
    }

    /**
     * Test retrieving string-based chunked stream sequentially as individual chunks using chunked input.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testChunkedOutputToChunkInputFromString() throws Exception {
        final ChunkedInput<Message> input = target().path("test/from-string").request(APPLICATION_JSON_TYPE).get(new javax.ws.rs.core.GenericType<ChunkedInput<Message>>() {});
        int counter = 0;
        Message chunk;
        while ((chunk = input.read()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), new Message(counter, "test"), chunk);
            counter++;
        } 
        Assert.assertEquals("Unexpected numbed of received chunks.", 3, counter);
    }

    /**
     * Test retrieving POJO-based chunked stream sequentially as individual chunks using chunked input.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testChunkedOutputToChunkInputFromPojo() throws Exception {
        final ChunkedInput<Message> input = target().path("test/from-pojo").request(APPLICATION_JSON_TYPE).get(new javax.ws.rs.core.GenericType<ChunkedInput<Message>>() {});
        int counter = 0;
        Message chunk;
        while ((chunk = input.read()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), new Message(counter, "test"), chunk);
            counter++;
        } 
        Assert.assertEquals("Unexpected numbed of received chunks.", 3, counter);
    }

    /**
     * Test combination of AsyncResponse and ChunkedOutput.
     */
    @Test
    public void chunkedOutputWithAsyncResponse() throws Exception {
        final ChunkedInput<Message> input = target().path("test/chunked-async").request(APPLICATION_JSON_TYPE).get(new javax.ws.rs.core.GenericType<ChunkedInput<Message>>() {});
        int counter = 0;
        Message chunk;
        while ((chunk = input.read()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), new Message(counter, "test"), chunk);
            counter++;
        } 
        Assert.assertEquals("Unexpected numbed of received chunks.", 3, counter);
    }

    /**
     * Reproducer for JERSEY-2558. Checking that the connection is properly closed even when the
     * {@link org.glassfish.jersey.server.ChunkedOutput#close()} is called before the response is processed by the runtime.
     */
    @Test
    public void checkConnectionIsClosedUrlConnection() throws Exception {
        final URI uri = UriBuilder.fromUri(getBaseUri()).path("test/close-before-return").build();
        final URLConnection connection = uri.toURL().openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.connect();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        int counter = 0;
        while ((line = reader.readLine()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), new Message(counter, "test").toString(), line);
            counter++;
        } 
        Assert.assertEquals("Unexpected numbed of received chunks.", 3, counter);
    }
}

