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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class AsyncResponseTest extends JerseyTest {
    public static CountDownLatch callbackCalledSignal1;

    public static CountDownLatch callbackCalledSignal2;

    @Test
    public void testMultipleCancel() throws IOException, InterruptedException {
        final Response response = target().path("resource/1").request().get();
        final InputStream inputStream = response.readEntity(InputStream.class);
        for (int i = 0; i < 500; i++) {
            inputStream.read();
        }
        response.close();
        AsyncResponseTest.callbackCalledSignal1.await();
    }

    @Test
    public void testCancelAfterResume() throws IOException, InterruptedException {
        final Response response = target().path("resource/2").request().get();
        final InputStream inputStream = response.readEntity(InputStream.class);
        for (int i = 0; i < 500; i++) {
            inputStream.read();
        }
        response.close();
        AsyncResponseTest.callbackCalledSignal2.await();
    }

    @Test
    public void testResumeWebApplicationException() throws Exception {
        testResumeException("resumeWebApplicationException", "resumeWebApplicationException");
    }

    @Test
    public void testResumeMappedException() throws Exception {
        testResumeException("resumeMappedException", "resumeMappedException");
    }

    @Test
    public void testResumeRuntimeException() throws Exception {
        testResumeException("resumeRuntimeException", null);
        Assert.assertThat(getLastLoggedRecord().getThrown(), CoreMatchers.instanceOf(RuntimeException.class));
    }

    @Test
    public void testResumeCheckedException() throws Exception {
        testResumeException("resumeCheckedException", null);
        Assert.assertThat(getLastLoggedRecord().getThrown(), CoreMatchers.instanceOf(IOException.class));
    }

    @Path("resource")
    public static class Resource {
        @GET
        @Path("1")
        @ManagedAsync
        public void get1(@Suspended
        final AsyncResponse asyncResponse) throws IOException, InterruptedException {
            if (asyncResponse.cancel()) {
                AsyncResponseTest.callbackCalledSignal1.countDown();
            }
            if (asyncResponse.cancel()) {
                AsyncResponseTest.callbackCalledSignal1.countDown();
            }
            if (asyncResponse.cancel()) {
                AsyncResponseTest.callbackCalledSignal1.countDown();
            }
        }

        @GET
        @Path("2")
        @ManagedAsync
        public void get2(@Suspended
        final AsyncResponse asyncResponse) throws IOException, InterruptedException {
            asyncResponse.resume("ok");
            if (!(asyncResponse.cancel())) {
                AsyncResponseTest.callbackCalledSignal2.countDown();
            }
            if (!(asyncResponse.cancel())) {
                AsyncResponseTest.callbackCalledSignal2.countDown();
            }
            if (!(asyncResponse.cancel())) {
                AsyncResponseTest.callbackCalledSignal2.countDown();
            }
        }
    }

    public static class MappedException extends RuntimeException {
        public MappedException(final String message) {
            super(message);
        }
    }

    public static class MappedExceptionMapper implements ExceptionMapper<AsyncResponseTest.MappedException> {
        @Override
        public Response toResponse(final AsyncResponseTest.MappedException exception) {
            return Response.serverError().entity(exception.getMessage()).build();
        }
    }

    @Path("errorResource")
    public static class ErrorResource {
        private static final BlockingQueue<AsyncResponse> suspended = new ArrayBlockingQueue<AsyncResponse>(1);

        @GET
        @Path("suspend")
        public void suspend(@Suspended
        final AsyncResponse asyncResponse) {
            AsyncResponseTest.ErrorResource.suspended.add(asyncResponse);
        }

        @GET
        @Path("resumeWebApplicationException")
        public String resumeWebApplicationException() throws Exception {
            return resume(new WebApplicationException(Response.serverError().entity("resumeWebApplicationException").build()));
        }

        @GET
        @Path("resumeMappedException")
        public String resumeMappedException() throws Exception {
            return resume(new AsyncResponseTest.MappedException("resumeMappedException"));
        }

        @GET
        @Path("resumeRuntimeException")
        public String resumeRuntimeException() throws Exception {
            return resume(new RuntimeException("resumeRuntimeException"));
        }

        @GET
        @Path("resumeCheckedException")
        public String resumeCheckedException() throws Exception {
            return resume(new IOException("resumeCheckedException"));
        }

        private String resume(final Throwable throwable) throws Exception {
            return AsyncResponseTest.ErrorResource.suspended.take().resume(throwable) ? "ok" : "ko";
        }
    }

    public static class EntityAnnotationChecker {}

    public static class EntityAnnotationCheckerWriter implements MessageBodyWriter<AsyncResponseTest.EntityAnnotationChecker> {
        @Override
        public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return true;
        }

        @Override
        public long getSize(final AsyncResponseTest.EntityAnnotationChecker entityAnnotationChecker, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(final AsyncResponseTest.EntityAnnotationChecker entityAnnotationChecker, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
            final String entity = ((annotations.length) > 0) ? "ok" : "ko";
            entityStream.write(entity.getBytes());
        }
    }

    @Path("annotations")
    public static class AsyncMessageBodyProviderResource {
        private static final BlockingQueue<AsyncResponse> suspended = new ArrayBlockingQueue<AsyncResponse>(1);

        @GET
        @Path("suspend")
        public void suspend(@Suspended
        final AsyncResponse asyncResponse) {
            AsyncResponseTest.AsyncMessageBodyProviderResource.suspended.add(asyncResponse);
        }

        @GET
        @Path("suspend-resume")
        public void suspendResume(@Suspended
        final AsyncResponse asyncResponse) {
            asyncResponse.resume(new AsyncResponseTest.EntityAnnotationChecker());
        }

        @GET
        @Path("resume")
        public String resume() throws Exception {
            return AsyncResponseTest.AsyncMessageBodyProviderResource.suspended.take().resume(new AsyncResponseTest.EntityAnnotationChecker()) ? "ok" : "ko";
        }
    }

    @Test
    public void testAnnotations() throws Exception {
        final WebTarget errorResource = target("annotations");
        final Future<Response> suspended = errorResource.path("suspend").request().async().get();
        final Response response = errorResource.path("resume").request().get();
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.is("ok"));
        final Response suspendedResponse = suspended.get();
        Assert.assertThat("Entity annotations are not propagated to MBW.", suspendedResponse.readEntity(String.class), CoreMatchers.is("ok"));
        suspendedResponse.close();
    }

    @Test
    public void testAnnotationsSuspendResume() throws Exception {
        final Response response = target("annotations").path("suspend-resume").request().async().get().get();
        Assert.assertThat("Entity annotations are not propagated to MBW.", response.readEntity(String.class), CoreMatchers.is("ok"));
        response.close();
    }
}

