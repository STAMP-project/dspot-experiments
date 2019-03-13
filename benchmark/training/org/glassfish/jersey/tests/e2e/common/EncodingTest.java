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
package org.glassfish.jersey.tests.e2e.common;


import HttpHeaders.ACCEPT_ENCODING;
import HttpHeaders.CONTENT_ENCODING;
import Invocation.Builder;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Matula
 */
public class EncodingTest extends JerseyTest {
    @Path("/")
    public static class EchoResource {
        @POST
        public String post(String text) {
            return text;
        }
    }

    @Test
    public void testGZip() throws IOException {
        test(new EncodingTest.TestSpec() {
            @Override
            public InputStream decode(InputStream stream) throws IOException {
                return new GZIPInputStream(stream);
            }

            @Override
            public void checkHeadersAndStatus(Response response) {
                Assert.assertEquals("gzip", response.getHeaderString(CONTENT_ENCODING));
            }

            @Override
            public Builder setHeaders(Invocation.Builder invBuilder) {
                return invBuilder.header(ACCEPT_ENCODING, "gzip");
            }
        });
    }

    @Test
    public void testDeflate() throws IOException {
        test(new EncodingTest.TestSpec() {
            @Override
            public InputStream decode(InputStream stream) throws IOException {
                return new InflaterInputStream(stream);
            }

            @Override
            public void checkHeadersAndStatus(Response response) {
                Assert.assertEquals("deflate", response.getHeaderString(CONTENT_ENCODING));
            }

            @Override
            public Builder setHeaders(Invocation.Builder invBuilder) {
                return invBuilder.header(ACCEPT_ENCODING, "deflate");
            }
        });
    }

    @Test
    public void testGZipPreferred() throws IOException {
        test(new EncodingTest.TestSpec() {
            @Override
            public InputStream decode(InputStream stream) throws IOException {
                return new GZIPInputStream(stream);
            }

            @Override
            public void checkHeadersAndStatus(Response response) {
                Assert.assertEquals("x-gzip", response.getHeaderString(CONTENT_ENCODING));
            }

            @Override
            public Builder setHeaders(Invocation.Builder invBuilder) {
                return invBuilder.header(ACCEPT_ENCODING, "deflate; q=.5, x-gzip");
            }
        });
    }

    @Test
    public void testGZipClientServer() throws IOException {
        test(new EncodingTest.TestSpec() {
            @Override
            public WebTarget configure(WebTarget target) {
                target.register(GZipEncoder.class).register(EncodingFilter.class);
                return target;
            }

            @Override
            public void checkHeadersAndStatus(Response response) {
                Assert.assertEquals("gzip", response.getHeaderString(CONTENT_ENCODING));
            }
        });
    }

    @Test
    public void testDeflatePreferredClientServer() throws IOException {
        test(new EncodingTest.TestSpec() {
            @Override
            public Builder setHeaders(Invocation.Builder invBuilder) {
                return invBuilder.header(ACCEPT_ENCODING, "deflate,gzip=.5");
            }

            @Override
            public WebTarget configure(WebTarget target) {
                target.register(DeflateEncoder.class).register(GZipEncoder.class).register(EncodingFilter.class);
                return target;
            }

            @Override
            public void checkHeadersAndStatus(Response response) {
                Assert.assertEquals("deflate", response.getHeaderString(CONTENT_ENCODING));
            }
        });
    }

    private static class TestSpec {
        public InputStream decode(InputStream stream) throws IOException {
            return stream;
        }

        public Builder setHeaders(Invocation.Builder invBuilder) {
            return invBuilder;
        }

        public WebTarget configure(WebTarget target) {
            return target;
        }

        public void checkHeadersAndStatus(Response response) {
        }
    }
}

