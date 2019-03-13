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
package org.glassfish.jersey.server.filter;


import HttpHeaders.ACCEPT_ENCODING;
import HttpHeaders.CONTENT_ENCODING;
import HttpHeaders.VARY;
import Response.Status;
import Response.Status.NOT_ACCEPTABLE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.spi.ContentEncoder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Matula
 */
public class EncodingFilterTest {
    public static class FooEncoding extends ContentEncoder {
        public FooEncoding() {
            super("foo");
        }

        @Override
        public InputStream decode(String contentEncoding, InputStream encodedStream) throws IOException {
            return encodedStream;
        }

        @Override
        public OutputStream encode(String contentEncoding, OutputStream entityStream) throws IOException {
            return entityStream;
        }
    }

    @Test
    public void testNoInterceptor() {
        ResourceConfig rc = new ResourceConfig(EncodingFilter.class);
        ContainerResponseFilter filter = getInjectionManager().getInstance(ContainerResponseFilter.class);
        Assert.assertNotNull(filter);
        Assert.assertTrue((filter instanceof EncodingFilter));
        Assert.assertEquals(1, getSupportedEncodings().size());
    }

    @Test
    public void testEnableFor() {
        EncodingFilter filter = initializeAndGetFilter();
        Assert.assertNotNull(filter);
        Assert.assertEquals(4, filter.getSupportedEncodings().size());
    }

    @Test
    public void testNoAcceptEncodingHeader() throws IOException {
        testEncoding(null);
    }

    @Test
    public void testAcceptEncodingHeaderNotSupported() throws IOException {
        testEncoding(null, "not-gzip");
    }

    @Test
    public void testGZipAcceptEncodingHeader() throws IOException {
        testEncoding("gzip", "gzip");
    }

    @Test
    public void testGZipPreferred() throws IOException {
        testEncoding("gzip", "foo; q=.5", "gzip");
    }

    @Test
    public void testFooPreferred() throws IOException {
        testEncoding("foo", "foo", "gzip; q=.5");
    }

    @Test
    public void testNotAcceptable() throws IOException {
        try {
            testEncoding(null, "one", "two", "*; q=0");
            Assert.fail(((Status.NOT_ACCEPTABLE) + " response was expected."));
        } catch (WebApplicationException e) {
            Assert.assertEquals(NOT_ACCEPTABLE, e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void testIdentityPreferred() throws IOException {
        testEncoding(null, "identity", "foo; q=.5");
    }

    @Test
    public void testAnyAcceptableExceptGZipAndIdentity() throws IOException {
        testEncoding("foo", "*", "gzip; q=0", "identity; q=0");
    }

    @Test
    public void testNoEntity() throws IOException {
        EncodingFilter filter = initializeAndGetFilter();
        ContainerRequest request = RequestContextBuilder.from("/resource", "GET").header(ACCEPT_ENCODING, "gzip").build();
        ContainerResponse response = new ContainerResponse(request, Response.ok().build());
        filter.filter(request, response);
        Assert.assertNull(response.getHeaders().getFirst(CONTENT_ENCODING));
        Assert.assertNull(response.getHeaders().getFirst(VARY));
    }
}

