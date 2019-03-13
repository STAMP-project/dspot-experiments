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
package org.glassfish.jersey.tests.e2e.client;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.client.ClientAsyncExecutor;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests sync and async client invocations.
 *
 * @author Miroslav Fuksa
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class BasicClientTest extends JerseyTest {
    @Test
    public void testCustomExecutorsAsync() throws InterruptedException, ExecutionException {
        ClientConfig jerseyConfig = new ClientConfig();
        jerseyConfig.register(BasicClientTest.CustomExecutorProvider.class).register(BasicClientTest.ThreadInterceptor.class);
        Client client = ClientBuilder.newClient(jerseyConfig);
        runCustomExecutorTestAsync(client);
    }

    @Test
    public void testCustomExecutorsInstanceAsync() throws InterruptedException, ExecutionException {
        ClientConfig jerseyConfig = new ClientConfig();
        jerseyConfig.register(new BasicClientTest.CustomExecutorProvider()).register(BasicClientTest.ThreadInterceptor.class);
        Client client = ClientBuilder.newClient(jerseyConfig);
        runCustomExecutorTestAsync(client);
    }

    @Test
    public void testCustomExecutorsSync() throws InterruptedException, ExecutionException {
        ClientConfig jerseyConfig = new ClientConfig();
        jerseyConfig.register(BasicClientTest.CustomExecutorProvider.class).register(BasicClientTest.ThreadInterceptor.class);
        Client client = ClientBuilder.newClient(jerseyConfig);
        runCustomExecutorTestSync(client);
    }

    @Test
    public void testCustomExecutorsInstanceSync() throws InterruptedException, ExecutionException {
        ClientConfig jerseyConfig = new ClientConfig();
        jerseyConfig.register(new BasicClientTest.CustomExecutorProvider()).register(BasicClientTest.ThreadInterceptor.class);
        Client client = ClientBuilder.newClient(jerseyConfig);
        runCustomExecutorTestSync(client);
    }

    @Test
    public void testAsyncClientInvocation() throws InterruptedException, ExecutionException {
        final WebTarget resource = target().path("resource");
        Future<Response> f1 = resource.request().async().post(Entity.text("post1"));
        final Response response = f1.get();
        Assert.assertEquals("post1", response.readEntity(String.class));
        Future<String> f2 = resource.request().async().post(Entity.text("post2"), String.class);
        Assert.assertEquals("post2", f2.get());
        Future<List<BasicClientTest.JaxbString>> f3 = resource.request().async().get(new javax.ws.rs.core.GenericType<List<BasicClientTest.JaxbString>>() {});
        Assert.assertEquals(Arrays.asList("a", "b", "c").toString(), f3.get().stream().map(( input) -> input.value).collect(Collectors.toList()).toString());
        CompletableFuture<String> future1 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<Response> c1 = new BasicClientTest.TestCallback<Response>(future1) {
            @Override
            protected String process(Response result) {
                return result.readEntity(String.class);
            }
        };
        resource.request().async().post(Entity.text("post"), c1);
        Assert.assertEquals("post", future1.get());
        CompletableFuture<String> future2 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<String> c2 = new BasicClientTest.TestCallback<String>(future2) {
            @Override
            protected String process(String result) {
                return result;
            }
        };
        resource.request().async().post(Entity.text("post"), c2);
        Assert.assertEquals("post", future2.get());
        CompletableFuture<String> future3 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<List<BasicClientTest.JaxbString>> c3 = new BasicClientTest.TestCallback<List<BasicClientTest.JaxbString>>(future3) {
            @Override
            protected String process(List<BasicClientTest.JaxbString> result) {
                return result.stream().map(( jaxbString) -> jaxbString.value).collect(Collectors.toList()).toString();
            }
        };
        resource.request().async().get(c3);
        Assert.assertEquals(Arrays.asList("a", "b", "c").toString(), future3.get());
    }

    @Test
    public void testAsyncClientInvocationErrorResponse() throws InterruptedException, ExecutionException {
        final WebTarget errorResource = target().path("resource").path("error");
        Future<Response> f1 = errorResource.request().async().get();
        final Response response = f1.get();
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("error", response.readEntity(String.class));
        Future<String> f2 = errorResource.request().async().get(String.class);
        try {
            f2.get();
            Assert.fail("ExecutionException expected.");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            Assert.assertTrue(WebApplicationException.class.isAssignableFrom(cause.getClass()));
            final Response r = getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
        Future<List<String>> f3 = target().path("resource").path("errorlist").request().async().get(new javax.ws.rs.core.GenericType<List<String>>() {});
        try {
            f3.get();
            Assert.fail("ExecutionException expected.");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            Assert.assertTrue(WebApplicationException.class.isAssignableFrom(cause.getClass()));
            final Response r = getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
        CompletableFuture<String> future1 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<Response> c1 = new BasicClientTest.TestCallback<Response>(future1) {
            @Override
            protected String process(Response result) {
                return result.readEntity(String.class);
            }
        };
        errorResource.request().async().get(c1);
        Assert.assertEquals("error", future1.get());
        CompletableFuture<String> future2 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<String> c2 = new BasicClientTest.TestCallback<String>(future2) {
            @Override
            protected String process(String result) {
                return result;
            }
        };
        errorResource.request().async().get(c2);
        try {
            future2.get();
            Assert.fail("ExecutionException expected.");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            Assert.assertTrue(WebApplicationException.class.isAssignableFrom(cause.getClass()));
            final Response r = getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
        CompletableFuture<String> future3 = new CompletableFuture<>();
        final BasicClientTest.TestCallback<List<BasicClientTest.JaxbString>> c3 = new BasicClientTest.TestCallback<List<BasicClientTest.JaxbString>>(future3) {
            @Override
            protected String process(List<BasicClientTest.JaxbString> result) {
                return result.stream().map(( jaxbString) -> jaxbString.value).collect(Collectors.toList()).toString();
            }
        };
        target().path("resource").path("errorlist").request().async().get(c3);
        try {
            future3.get();
            Assert.fail("ExecutionException expected.");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            Assert.assertTrue(WebApplicationException.class.isAssignableFrom(cause.getClass()));
            final Response r = getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
    }

    @Test
    public void testSyncClientInvocation() throws InterruptedException, ExecutionException {
        final WebTarget resource = target().path("resource");
        Response r1 = resource.request().post(Entity.text("post1"));
        Assert.assertEquals("post1", r1.readEntity(String.class));
        String r2 = resource.request().post(Entity.text("post2"), String.class);
        Assert.assertEquals("post2", r2);
        List<BasicClientTest.JaxbString> r3 = resource.request().get(new javax.ws.rs.core.GenericType<List<BasicClientTest.JaxbString>>() {});
        Assert.assertEquals(Arrays.asList("a", "b", "c").toString(), r3.stream().map(( input) -> input.value).collect(Collectors.toList()).toString());
    }

    @Test
    public void testSyncClientInvocationErrorResponse() throws InterruptedException, ExecutionException {
        final WebTarget errorResource = target().path("resource").path("error");
        Response r1 = errorResource.request().get();
        Assert.assertEquals(404, r1.getStatus());
        Assert.assertEquals("error", r1.readEntity(String.class));
        try {
            errorResource.request().get(String.class);
            Assert.fail("ExecutionException expected.");
        } catch (WebApplicationException ex) {
            final Response r = ex.getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
        try {
            target().path("resource").path("errorlist").request().get(new javax.ws.rs.core.GenericType<List<String>>() {});
            Assert.fail("ExecutionException expected.");
        } catch (WebApplicationException ex) {
            final Response r = ex.getResponse();
            Assert.assertEquals(404, r.getStatus());
            Assert.assertEquals("error", r.readEntity(String.class));
        }
    }

    // JERSEY-1412
    @Test
    public void testAbortAsyncRequest() throws Exception {
        Invocation invocation = abortingTarget().request().buildPost(Entity.text("entity"));
        Future<String> future = invocation.submit(String.class);
        Assert.assertEquals("aborted", future.get());
    }

    // JERSEY-1412
    @Test
    public void testAbortSyncRequest() throws Exception {
        Invocation invocation = abortingTarget().request().buildPost(Entity.text("entity"));
        String response = invocation.invoke(String.class);
        Assert.assertEquals("aborted", response);
    }

    @ClientAsyncExecutor
    public static class CustomExecutorProvider extends ThreadPoolExecutorProvider {
        /**
         * Create a new instance of the thread pool executor provider.
         */
        public CustomExecutorProvider() {
            super("custom-async-request");
        }
    }

    public static class ThreadInterceptor implements WriterInterceptor {
        @Override
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            final String name = Thread.currentThread().getName();// e.g. custom-async-request-0

            final int lastIndexOfDash = name.lastIndexOf('-');
            context.setEntity((((name.substring(0, (lastIndexOfDash < 0 ? name.length() : lastIndexOfDash))) + "-") + (context.getEntity())));
            context.proceed();
        }
    }

    @Path("resource")
    public static class Resource {
        @GET
        @Path("error")
        public String getError() {
            throw new javax.ws.rs.NotFoundException(Response.status(404).type("text/plain").entity("error").build());
        }

        @GET
        @Path("errorlist")
        @Produces(MediaType.APPLICATION_XML)
        public List<BasicClientTest.JaxbString> getErrorList() {
            throw new javax.ws.rs.NotFoundException(Response.status(404).type("text/plain").entity("error").build());
        }

        @GET
        @Produces(MediaType.APPLICATION_XML)
        public List<BasicClientTest.JaxbString> get() {
            return Arrays.asList(new BasicClientTest.JaxbString("a"), new BasicClientTest.JaxbString("b"), new BasicClientTest.JaxbString("c"));
        }

        @POST
        public String post(String entity) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return entity;
        }
    }

    private abstract static class TestCallback<T> implements InvocationCallback<T> {
        private final CompletableFuture<String> completableFuture;

        TestCallback(CompletableFuture<String> completableFuture) {
            this.completableFuture = completableFuture;
        }

        @Override
        public void completed(T result) {
            try {
                completableFuture.complete(process(result));
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }

        protected abstract String process(T result);

        @Override
        public void failed(Throwable error) {
            if ((error.getCause()) instanceof WebApplicationException) {
                completableFuture.completeExceptionally(error.getCause());
            } else {
                completableFuture.completeExceptionally(error);
            }
        }
    }

    @XmlRootElement
    public static class JaxbString {
        public String value;

        public JaxbString() {
        }

        public JaxbString(String value) {
            this.value = value;
        }
    }
}

