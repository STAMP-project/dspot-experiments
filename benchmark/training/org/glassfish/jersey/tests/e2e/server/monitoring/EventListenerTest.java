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
package org.glassfish.jersey.tests.e2e.server.monitoring;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Miroslav Fuksa
 */
public class EventListenerTest extends JerseyTest {
    private static final String APPLICATION_NAME = "MyApplication";

    private static EventListenerTest.AppEventListener applicationEventListener;

    public static class AppEventListener implements ApplicationEventListener {
        private ApplicationEvent appEventInitStart;

        private ApplicationEvent appEventInitFinished;

        private RequestEvent newRequestEvent;

        private volatile int resourceMethodEventCount = 0;

        private volatile CountDownLatch finishedCalled = new CountDownLatch(1);

        @Override
        public void onEvent(ApplicationEvent event) {
            switch (event.getType()) {
                case INITIALIZATION_START :
                    this.appEventInitStart = event;
                    break;
                case INITIALIZATION_APP_FINISHED :
                    this.appEventInitFinished = event;
                    break;
            }
        }

        @Override
        public RequestEventListener onRequest(RequestEvent newRequestEvent) {
            this.newRequestEvent = newRequestEvent;
            if ("POST".equals(newRequestEvent.getContainerRequest().getMethod())) {
                return null;
            }
            return new EventListenerTest.ReqEventListener(this);
        }
    }

    public static class ReqEventListener implements RequestEventListener {
        private final EventListenerTest.AppEventListener appEventListener;

        public final MultivaluedMap<String, String> eventData = new javax.ws.rs.core.MultivaluedHashMap<String, String>();

        public ReqEventListener(EventListenerTest.AppEventListener appEventListener) {
            this.appEventListener = appEventListener;
        }

        private int index = 1;

        @Override
        public void onEvent(RequestEvent event) {
            switch (event.getType()) {
                case REQUEST_MATCHED :
                    eventData.add("R.REQ_FILTERS_START.order", String.valueOf(((index)++)));
                    break;
                case REQUEST_FILTERED :
                    eventData.add("R.REQ_FILTERS_FINISHED.order", String.valueOf(((index)++)));
                    break;
                case LOCATOR_MATCHED :
                    eventData.add("R.MATCHED_LOCATOR.order", String.valueOf(((index)++)));
                    final List<ResourceMethod> locators = event.getUriInfo().getMatchedResourceLocators();
                    String msg = ((String.valueOf(locators.size())) + ":") + (locators.get(0).getInvocable().getHandlingMethod().getName());
                    eventData.add("R.MATCHED_LOCATOR", msg);
                    break;
                case SUBRESOURCE_LOCATED :
                    eventData.add("R.MATCHED_SUB_RESOURCE.order", String.valueOf(((index)++)));
                    break;
                case RESOURCE_METHOD_START :
                    eventData.add("R.RESOURCE_METHOD_START.order", String.valueOf(((index)++)));
                    (this.appEventListener.resourceMethodEventCount)++;
                    final ResourceMethod resourceMethod = event.getUriInfo().getMatchedResourceMethod();
                    eventData.add("R.RESOURCE_METHOD_START.method", resourceMethod.getInvocable().getHandlingMethod().getName());
                    break;
                case RESOURCE_METHOD_FINISHED :
                    eventData.add("R.RESOURCE_METHOD_FINISHED.order", String.valueOf(((index)++)));
                    eventData.add("R.RESOURCE_METHOD_FINISHED", "ok");
                    break;
                case RESP_FILTERS_START :
                    eventData.add("R.RESP_FILTERS_START.order", String.valueOf(((index)++)));
                    break;
                case EXCEPTION_MAPPER_FOUND :
                    eventData.add("R.EXCEPTION_MAPPER_FOUND.order", String.valueOf(((index)++)));
                    eventData.add("R.EXCEPTION_MAPPER_FOUND.exception", event.getException().getMessage());
                    break;
                case RESP_FILTERS_FINISHED :
                    eventData.add("R.RESP_FILTERS_FINISHED.order", String.valueOf(((index)++)));
                    for (Map.Entry<String, List<String>> entry : eventData.entrySet()) {
                        event.getContainerResponse().getHeaders().addAll(entry.getKey(), entry.getValue());
                    }
                    break;
                case FINISHED :
                    Assert.assertNotNull(event.getContainerResponse());
                    this.appEventListener.finishedCalled.countDown();
                    break;
            }
        }
    }

    public static class MyMappableException extends RuntimeException {
        public MyMappableException(String message) {
            super(message);
        }
    }

    public static class MyExceptionMapper implements ExceptionMapper<EventListenerTest.MyMappableException> {
        @Override
        public Response toResponse(EventListenerTest.MyMappableException exception) {
            return Response.ok("mapped").build();
        }
    }

    public static class RequestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }
    }

    @PreMatching
    public static class PreMatchingRequestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }
    }

    public static class ResponseFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        }
    }

    @Path("resource")
    public static class MyResource {
        @GET
        public String getMethod() {
            return "get";
        }

        @POST
        public void post(String entity) {
        }

        @Path("locator")
        public EventListenerTest.SubResource locator() {
            return new EventListenerTest.SubResource();
        }

        @GET
        @Path("async")
        @ManagedAsync
        public void getAsync(@Suspended
        AsyncResponse asyncResponse) {
            asyncResponse.resume(Response.ok("async").build());
        }

        /**
         * This works in the async way but it is served by only one thread.
         *
         * @param asyncResponse
         * 		
         */
        @GET
        @Path("asyncOneThread")
        public void getAsyncOneThread(@Suspended
        AsyncResponse asyncResponse) {
            asyncResponse.resume(Response.ok("async").build());
        }
    }

    public static class SubResource {
        @GET
        public String get() {
            return "sub";
        }

        @GET
        @Path("exception")
        public String getException() {
            throw new EventListenerTest.MyMappableException("test-error");
        }
    }

    @Test
    public void testApplicationEvents() {
        Assert.assertNotNull(EventListenerTest.applicationEventListener.appEventInitStart);
        Assert.assertNotNull(EventListenerTest.applicationEventListener.appEventInitFinished);
        Assert.assertEquals(EventListenerTest.APPLICATION_NAME, EventListenerTest.applicationEventListener.appEventInitStart.getResourceConfig().getApplicationName());
        Assert.assertNull(EventListenerTest.applicationEventListener.newRequestEvent);
        final Response response = target().path("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(EventListenerTest.applicationEventListener.newRequestEvent);
    }

    @Test
    public void testSimpleRequestEvent() {
        Assert.assertEquals(0, EventListenerTest.applicationEventListener.resourceMethodEventCount);
        Assert.assertNotNull(EventListenerTest.applicationEventListener.appEventInitStart);
        Assert.assertNull(EventListenerTest.applicationEventListener.newRequestEvent);
        Response response = target().path("resource").request().post(javax.ws.rs.client.Entity.entity("entity", MediaType.TEXT_PLAIN_TYPE));
        Assert.assertEquals(204, response.getStatus());
        Assert.assertNotNull(EventListenerTest.applicationEventListener.newRequestEvent);
        Assert.assertEquals(0, EventListenerTest.applicationEventListener.resourceMethodEventCount);
        response = target().path("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(1, EventListenerTest.applicationEventListener.resourceMethodEventCount);
    }

    @Test
    public void testMatchedLocator() {
        final Response response = target().path("resource/locator").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("sub", response.readEntity(String.class));
        Assert.assertEquals("[1:locator]", response.getHeaderString("R.MATCHED_LOCATOR"));
    }

    @Test
    public void testMatchedMethod() {
        final Response response = target().path("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get", response.readEntity(String.class));
        Assert.assertEquals("[getMethod]", response.getHeaderString("R.RESOURCE_METHOD_START.method"));
        Assert.assertEquals("[ok]", response.getHeaderString("R.RESOURCE_METHOD_FINISHED"));
    }

    @Test
    public void testException() {
        final Response response = target().path("resource/locator/exception").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("mapped", response.readEntity(String.class));
        Assert.assertEquals("[org.glassfish.jersey.tests.e2e.server.monitoring.EventListenerTest$MyMappableException: test-error]", response.getHeaderString("R.EXCEPTION_MAPPER_FOUND.exception"));
    }

    @Test
    public void testSimpleProcessing() {
        final Response response = target().path("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get", response.readEntity(String.class));
        int i = 1;
        System.out.println(response.getHeaders());
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESP_FILTERS_START.order"));
        Assert.assertEquals((("[" + i) + "]"), response.getHeaderString("R.RESP_FILTERS_FINISHED.order"));
    }

    @Test
    public void testLocatorProcessing() {
        final Response response = target().path("resource/locator").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("sub", response.readEntity(String.class));
        int i = 1;
        System.out.println(response.getHeaders());
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.MATCHED_LOCATOR.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.MATCHED_SUB_RESOURCE.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESP_FILTERS_START.order"));
        Assert.assertEquals((("[" + i) + "]"), response.getHeaderString("R.RESP_FILTERS_FINISHED.order"));
    }

    @Test
    public void testExceptionProcessing() {
        final Response response = target().path("resource/locator/exception").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("mapped", response.readEntity(String.class));
        int i = 1;
        System.out.println(response.getHeaders());
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.MATCHED_LOCATOR.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.MATCHED_SUB_RESOURCE.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.EXCEPTION_MAPPER_FOUND.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESP_FILTERS_START.order"));
        Assert.assertEquals((("[" + i) + "]"), response.getHeaderString("R.RESP_FILTERS_FINISHED.order"));
    }

    @Test
    public void testAsyncProcessing() throws InterruptedException {
        final Response response = target().path("resource/async").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("async", response.readEntity(String.class));
        int i = 1;
        System.out.println(response.getHeaders());
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESP_FILTERS_START.order"));
        Assert.assertEquals((("[" + i) + "]"), response.getHeaderString("R.RESP_FILTERS_FINISHED.order"));
        final boolean success = EventListenerTest.applicationEventListener.finishedCalled.await((3 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS);
        Assert.assertTrue(success);
    }

    @Test
    public void testAsyncProcessingWithOneThread() throws InterruptedException {
        final Response response = target().path("resource/asyncOneThread").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("async", response.readEntity(String.class));
        int i = 1;
        System.out.println(response.getHeaders());
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.REQ_FILTERS_FINISHED.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESOURCE_METHOD_START.order"));
        Assert.assertEquals((("[" + (i++)) + "]"), response.getHeaderString("R.RESP_FILTERS_START.order"));
        Assert.assertEquals((("[" + i) + "]"), response.getHeaderString("R.RESP_FILTERS_FINISHED.order"));
        final boolean success = EventListenerTest.applicationEventListener.finishedCalled.await((3 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS);
        Assert.assertTrue(success);
    }
}

