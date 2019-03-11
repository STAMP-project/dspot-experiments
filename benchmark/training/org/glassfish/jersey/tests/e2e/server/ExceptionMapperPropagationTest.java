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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test exception mappers handling exceptions thrown from different part of code.
 * <p/>
 * There are more tests for exception mappers. This one focuses on testing that exceptions
 * thrown from providers are correctly propagated to the exception mapper.
 *
 * @author Miroslav Fuksa
 */
// sub resource locator
@RunWith(ConcurrentRunner.class)
public class ExceptionMapperPropagationTest extends JerseyTest {
    public static final String EXCEPTION_TYPE = "exception-type";

    public static final String MAPPED = "-mapped-";

    public static final String MAPPED_WAE = "-wae-";

    public static final String PROVIDER = "provider";

    public static class TestRuntimeException extends RuntimeException {
        public TestRuntimeException(String message) {
            super(message);
        }
    }

    public static class TestCheckedException extends Exception {
        public TestCheckedException(String message) {
            super(message);
        }
    }

    public static class TestWebAppException extends WebApplicationException {
        public TestWebAppException(String message, Response response) {
            super(message, response);
        }
    }

    public static class UniversalThrowableMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable exception) {
            return Response.ok().entity((((exception.getClass().getSimpleName()) + (ExceptionMapperPropagationTest.MAPPED)) + (exception.getMessage()))).build();
        }
    }

    public static class WebAppMapper implements ExceptionMapper<ExceptionMapperPropagationTest.TestWebAppException> {
        @Override
        public Response toResponse(ExceptionMapperPropagationTest.TestWebAppException exception) {
            final Response response = getResponse();
            return Response.status(response.getStatus()).entity((((exception.getClass().getSimpleName()) + (ExceptionMapperPropagationTest.MAPPED_WAE)) + (getMessage()))).build();
        }
    }

    @Path("exception")
    public static class ExceptionResource {
        @Path("general")
        @POST
        public Response post(@HeaderParam(ExceptionMapperPropagationTest.EXCEPTION_TYPE)
        String exceptionType, @HeaderParam(ExceptionMapperPropagationTest.PROVIDER)
        String provider, String entity) throws Throwable {
            ExceptionMapperPropagationTest.throwException(exceptionType, provider, this.getClass());
            return Response.ok().entity("exception/general#get called").header(ExceptionMapperPropagationTest.EXCEPTION_TYPE, exceptionType).header(ExceptionMapperPropagationTest.PROVIDER, provider).build();
        }

        @Path("sub")
        public ExceptionMapperPropagationTest.SubResourceLocator subResourceLocator(@HeaderParam(ExceptionMapperPropagationTest.EXCEPTION_TYPE)
        String exceptionType, @HeaderParam(ExceptionMapperPropagationTest.PROVIDER)
        String provider) throws Throwable {
            ExceptionMapperPropagationTest.throwException(exceptionType, provider, this.getClass());
            return new ExceptionMapperPropagationTest.SubResourceLocator();
        }
    }

    public static class SubResourceLocator {
        @POST
        public String post(@HeaderParam(ExceptionMapperPropagationTest.EXCEPTION_TYPE)
        String exceptionType, @HeaderParam(ExceptionMapperPropagationTest.PROVIDER)
        String provider, String entity) throws Throwable {
            ExceptionMapperPropagationTest.throwException(exceptionType, provider, this.getClass());
            return "sub-get";
        }
    }

    public static class TestResponseFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            final String exceptionType = responseContext.getHeaderString(ExceptionMapperPropagationTest.EXCEPTION_TYPE);
            final String provider = responseContext.getHeaderString(ExceptionMapperPropagationTest.PROVIDER);
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
        }
    }

    public static class TestRequestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            final String exceptionType = requestContext.getHeaderString(ExceptionMapperPropagationTest.EXCEPTION_TYPE);
            final String provider = requestContext.getHeaderString(ExceptionMapperPropagationTest.PROVIDER);
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
        }
    }

    @Consumes(MediaType.TEXT_PLAIN)
    public static class TestMBR implements MessageBodyReader<String> {
        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            final String exceptionType = httpHeaders.getFirst(ExceptionMapperPropagationTest.EXCEPTION_TYPE);
            final String provider = httpHeaders.getFirst(ExceptionMapperPropagationTest.PROVIDER);
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
            byte b;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((b = ((byte) (entityStream.read()))) != (-1)) {
                baos.write(b);
            } 
            return new String(baos.toByteArray());
        }
    }

    @Produces({ "text/plain", "*/*" })
    public static class TestMBW implements MessageBodyWriter<String> {
        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return 0;
        }

        @Override
        public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            final String exceptionType = ((String) (httpHeaders.getFirst(ExceptionMapperPropagationTest.EXCEPTION_TYPE)));
            final String provider = ((String) (httpHeaders.getFirst(ExceptionMapperPropagationTest.PROVIDER)));
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
            entityStream.write(s.getBytes());
            entityStream.flush();
        }
    }

    @Consumes(MediaType.TEXT_PLAIN)
    public static class TestReaderInterceptor implements ReaderInterceptor {
        @Override
        public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
            final String exceptionType = context.getHeaders().getFirst(ExceptionMapperPropagationTest.EXCEPTION_TYPE);
            final String provider = context.getHeaders().getFirst(ExceptionMapperPropagationTest.PROVIDER);
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
            return context.proceed();
        }
    }

    public static class TestWriterInterceptor implements WriterInterceptor {
        @Override
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            final String exceptionType = ((String) (context.getHeaders().getFirst(ExceptionMapperPropagationTest.EXCEPTION_TYPE)));
            final String provider = ((String) (context.getHeaders().getFirst(ExceptionMapperPropagationTest.PROVIDER)));
            ExceptionMapperPropagationTest.throwRuntimeExceptionAndIO(exceptionType, this.getClass(), provider);
            context.proceed();
        }
    }

    // Resource
    @Test
    public void testCheckedExceptionInResource() {
        _test(ExceptionMapperPropagationTest.TestCheckedException.class, ExceptionMapperPropagationTest.ExceptionResource.class);
    }

    @Test
    public void testRuntimeExceptionInResource() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.ExceptionResource.class);
    }

    @Test
    public void testWebApplicationExceptionInResource() {
        _testWae(ExceptionMapperPropagationTest.ExceptionResource.class);
    }

    @Test
    public void testProcessingExceptionInResource() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.ExceptionResource.class);
    }

    // Sub resource
    @Test
    public void testCheckedExceptionInSubResourceLocatorMethod() {
        _test(ExceptionMapperPropagationTest.TestCheckedException.class, ExceptionMapperPropagationTest.ExceptionResource.class, "exception/sub");
    }

    @Test
    public void testRuntimeExceptionInSubResourceLocatorMethod() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.ExceptionResource.class, "exception/sub");
    }

    @Test
    public void testWaeInSubResourceLocatorMethod() {
        _testWae(ExceptionMapperPropagationTest.ExceptionResource.class, "exception/sub");
    }

    @Test
    public void testProcessingExceptionInSubResourceLocatorMethod() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.ExceptionResource.class, "exception/sub");
    }

    @Test
    public void testCheckedExceptionInSubResource() {
        _test(ExceptionMapperPropagationTest.TestCheckedException.class, ExceptionMapperPropagationTest.SubResourceLocator.class, "exception/sub");
    }

    @Test
    public void testRuntimeExceptionInSubResource() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.SubResourceLocator.class, "exception/sub");
    }

    @Test
    public void testWaeInSubResource() {
        _testWae(ExceptionMapperPropagationTest.SubResourceLocator.class, "exception/sub");
    }

    @Test
    public void testProcessingExceptionInSubResource() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.SubResourceLocator.class, "exception/sub");
    }

    // response filters
    @Test
    public void testRuntimeExceptionInResponseFilter() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestResponseFilter.class);
    }

    @Test
    public void testIOExceptionInResponseFilter() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestResponseFilter.class);
    }

    @Test
    public void testWaeInResponseFilter() {
        _testWae(ExceptionMapperPropagationTest.TestResponseFilter.class);
    }

    @Test
    public void testProcessingExceptionInResponseFilter() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestResponseFilter.class);
    }

    // response filters
    @Test
    public void testRuntimeExceptionInRequestFilter() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestRequestFilter.class);
    }

    @Test
    public void testIOExceptionInRequestFilter() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestRequestFilter.class);
    }

    @Test
    public void testWaeInRequestFilter() {
        _testWae(ExceptionMapperPropagationTest.TestRequestFilter.class);
    }

    @Test
    public void testProcessingExceptionInRequestFilter() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestRequestFilter.class);
    }

    // MBR/W
    @Test
    public void testRuntimeExceptionInMBW() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestMBW.class);
    }

    @Test
    public void testIOExceptionInMBW() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestMBW.class);
    }

    @Test
    public void testWaeInMBW() {
        _testWae(ExceptionMapperPropagationTest.TestMBW.class);
    }

    @Test
    public void testProcessingExceptionInMBW() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestMBW.class);
    }

    @Test
    public void testRuntimeExceptionInMBR() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestMBR.class);
    }

    @Test
    public void testIOExceptionInMBR() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestMBR.class);
    }

    @Test
    public void testWaeInMBR() {
        _testWae(ExceptionMapperPropagationTest.TestMBR.class);
    }

    @Test
    public void testProcessingExceptionInMBR() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestMBR.class);
    }

    // interceptors
    @Test
    public void testRuntimeExceptionInReaderInterceptor() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestReaderInterceptor.class);
    }

    @Test
    public void testIOExceptionInReaderInterceptor() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestReaderInterceptor.class);
    }

    @Test
    public void testWaeInReaderInterceptor() {
        _testWae(ExceptionMapperPropagationTest.TestReaderInterceptor.class);
    }

    @Test
    public void testProcessingExceptionInReaderInterceptor() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestReaderInterceptor.class);
    }

    @Test
    public void testRuntimeExceptionInWriterInterceptor() {
        _test(ExceptionMapperPropagationTest.TestRuntimeException.class, ExceptionMapperPropagationTest.TestWriterInterceptor.class);
    }

    @Test
    public void testIOExceptionInWriterInterceptor() {
        _test(IOException.class, ExceptionMapperPropagationTest.TestWriterInterceptor.class);
    }

    @Test
    public void testWaeInWriterInterceptor() {
        _testWae(ExceptionMapperPropagationTest.TestWriterInterceptor.class);
    }

    @Test
    public void testProcessingExceptionInWriterInterceptor() {
        _test(ProcessingException.class, ExceptionMapperPropagationTest.TestWriterInterceptor.class);
    }
}

