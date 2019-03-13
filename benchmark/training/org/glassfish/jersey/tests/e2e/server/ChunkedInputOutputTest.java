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
package org.glassfish.jersey.tests.e2e.server;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.server.ChunkedOutput;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Chunked input/output tests.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ChunkedInputOutputTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(ChunkedInputOutputTest.class.getName());

    /**
     * Test resource.
     */
    @Path("/test")
    public static class TestResource {
        /**
         * Get chunk stream.
         *
         * @return chunk stream.
         */
        @GET
        public ChunkedOutput<String> get() {
            final ChunkedOutput<String> output = new ChunkedOutput(String.class, "\r\n");
            new Thread() {
                @Override
                public void run() {
                    try {
                        output.write("test");
                        output.write("test");
                        output.write("test");
                    } catch (final IOException e) {
                        ChunkedInputOutputTest.LOGGER.log(Level.SEVERE, "Error writing chunk.", e);
                    } finally {
                        try {
                            output.close();
                        } catch (final IOException e) {
                            ChunkedInputOutputTest.LOGGER.log(Level.INFO, "Error closing chunked output.", e);
                        }
                    }
                }
            }.start();
            return output;
        }

        /**
         * Get chunk stream with an attached interceptor.
         *
         * @return intercepted chunk stream.
         */
        @GET
        @Path("intercepted")
        @ChunkedInputOutputTest.Intercepted
        public ChunkedOutput<String> interceptedGet() {
            return get();
        }
    }

    /**
     * Test interceptor binding.
     */
    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Intercepted {}

    /**
     * Test interceptor - counts number of interception as well as number of wrapper output stream method calls.
     */
    @ChunkedInputOutputTest.Intercepted
    public static class TestWriterInterceptor implements WriterInterceptor {
        private static final AtomicInteger interceptCounter = new AtomicInteger(0);

        private static final AtomicInteger writeCounter = new AtomicInteger(0);

        private static final AtomicInteger flushCounter = new AtomicInteger(0);

        private static final AtomicInteger closeCounter = new AtomicInteger(0);

        @Override
        public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
            ChunkedInputOutputTest.TestWriterInterceptor.interceptCounter.incrementAndGet();
            final OutputStream out = context.getOutputStream();
            context.setOutputStream(new OutputStream() {
                @Override
                public void write(final int b) throws IOException {
                    ChunkedInputOutputTest.TestWriterInterceptor.writeCounter.incrementAndGet();
                    out.write(b);
                }

                @Override
                public void write(final byte[] b) throws IOException {
                    ChunkedInputOutputTest.TestWriterInterceptor.writeCounter.incrementAndGet();
                    out.write(b);
                }

                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    ChunkedInputOutputTest.TestWriterInterceptor.writeCounter.incrementAndGet();
                    out.write(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    ChunkedInputOutputTest.TestWriterInterceptor.flushCounter.incrementAndGet();
                    out.flush();
                }

                @Override
                public void close() throws IOException {
                    ChunkedInputOutputTest.TestWriterInterceptor.closeCounter.incrementAndGet();
                    out.close();
                }
            });
            context.proceed();
        }
    }

    /**
     * Test retrieving chunked response stream as a single response string.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testChunkedOutputToSingleString() throws Exception {
        final String response = target().path("test").request().get(String.class);
        Assert.assertEquals("Unexpected value of chunked response unmarshalled as a single string.", "test\r\ntest\r\ntest\r\n", response);
    }

    /**
     * Test retrieving chunked response stream sequentially as individual chunks using chunked input.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testChunkedOutputToChunkInput() throws Exception {
        final ChunkedInput<String> input = target().path("test").request().get(new javax.ws.rs.core.GenericType<ChunkedInput<String>>() {});
        int counter = 0;
        String chunk;
        while ((chunk = input.read()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), "test", chunk);
            counter++;
        } 
        Assert.assertEquals("Unexpected numbed of received chunks.", 3, counter);
    }

    /**
     * Test retrieving intercepted chunked response stream sequentially as individual chunks using chunked input.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testInterceptedChunkedOutputToChunkInput() throws Exception {
        final ChunkedInput<String> input = target().path("test/intercepted").request().get(new javax.ws.rs.core.GenericType<ChunkedInput<String>>() {});
        int counter = 0;
        String chunk;
        while ((chunk = input.read()) != null) {
            Assert.assertEquals(("Unexpected value of chunk " + counter), "test", chunk);
            counter++;
        } 
        Assert.assertThat("Unexpected numbed of received chunks.", counter, Matchers.equalTo(3));
        Assert.assertThat("Unexpected number of chunked output interceptions.", ChunkedInputOutputTest.TestWriterInterceptor.interceptCounter.get(), Matchers.equalTo(1));
        Assert.assertThat("Unexpected number of intercepted output write calls.", ChunkedInputOutputTest.TestWriterInterceptor.writeCounter.get(), Matchers.greaterThanOrEqualTo(1));
        Assert.assertThat("Unexpected number of intercepted output flush calls.", ChunkedInputOutputTest.TestWriterInterceptor.flushCounter.get(), Matchers.greaterThanOrEqualTo(3));
        Assert.assertThat("Unexpected number of intercepted output close calls.", ChunkedInputOutputTest.TestWriterInterceptor.closeCounter.get(), Matchers.equalTo(1));
    }
}

