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
package org.glassfish.jersey.tests.e2e.server;


import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Test for JERSEY-1545.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class InterceptorHttpHeadersInjectionTest extends JerseyTest {
    static final String WriterHEADER = "custom-writer-header";

    static final String ReaderHEADER = "custom-reader-header";

    static final String RawCONTENT = "SIMPLE";

    @Provider
    public static class InjectedWriterInterceptor implements WriterInterceptor {
        @Context
        HttpHeaders headers;

        // Replace content with WriterHEADER header value if corresponding header is seen.
        @Override
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            final String writerHeaderValue = headers.getHeaderString(InterceptorHttpHeadersInjectionTest.WriterHEADER);
            if (writerHeaderValue != null) {
                context.getOutputStream().write(writerHeaderValue.getBytes());
            }
            context.proceed();
        }
    }

    @Provider
    public static class InjectedReaderInterceptor implements ReaderInterceptor {
        @Context
        HttpHeaders headers;

        // Replace content with ReaderHEADER header value if corresponding header is seen.
        @Override
        public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
            final String readerHeaderValue = headers.getHeaderString(InterceptorHttpHeadersInjectionTest.ReaderHEADER);
            if (readerHeaderValue != null) {
                return readerHeaderValue;
            }
            return context.proceed();
        }
    }

    @Path("/")
    public static class SimpleResource {
        @GET
        public String getIt() {
            return InterceptorHttpHeadersInjectionTest.RawCONTENT;
        }

        @POST
        public String echo(String message) {
            return message;
        }
    }

    @Test
    public void testWriter() throws Exception {
        _checkRawGet();
        _checkWriterInterceptor("writer-one");
        _checkWriterInterceptor("writer-two");
    }

    @Test
    public void testReader() throws Exception {
        _checkRawEcho();
        _checkReaderInterceptor("reader-one");
        _checkReaderInterceptor("reader-two");
    }
}

