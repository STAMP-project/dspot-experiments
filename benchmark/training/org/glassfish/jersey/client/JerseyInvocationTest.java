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
package org.glassfish.jersey.client;


import ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION;
import Invocation.Builder;
import MediaType.TEXT_PLAIN_TYPE;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Matula
 */
public class JerseyInvocationTest {
    /**
     * Regression test for JERSEY-1257
     */
    @Test
    public void testOverrideHeadersWithMap() {
        final MultivaluedMap<String, Object> map = new javax.ws.rs.core.MultivaluedHashMap<String, Object>();
        map.add("a-header", "b-header");
        final JerseyInvocation invocation = buildInvocationWithHeaders(map);
        Assert.assertEquals(1, invocation.request().getHeaders().size());
        Assert.assertEquals("b-header", invocation.request().getHeaders().getFirst("a-header"));
    }

    /**
     * Regression test for JERSEY-1257
     */
    @Test
    public void testClearHeaders() {
        final JerseyInvocation invocation = buildInvocationWithHeaders(null);
        Assert.assertTrue(invocation.request().getHeaders().isEmpty());
    }

    /**
     * Regression test for JERSEY-2562.
     */
    @Test
    public void testClearHeader() {
        final Client client = ClientBuilder.newClient();
        final Invocation.Builder builder = client.target("http://localhost:8080/mypath").request();
        final JerseyInvocation invocation = ((JerseyInvocation) (builder.header("foo", "bar").header("foo", null).header("bar", "foo").buildGet()));
        final MultivaluedMap<String, Object> headers = invocation.request().getHeaders();
        Assert.assertThat(headers.size(), CoreMatchers.is(1));
        Assert.assertThat(headers.keySet(), CoreMatchers.hasItem("bar"));
    }

    /**
     * Checks that presence of request entity fo HTTP DELETE method does not fail in Jersey.
     * Instead, the request is propagated up to HttpURLConnection, where it fails with
     * {@code ProtocolException}.
     * <p/>
     * See also JERSEY-1711.
     *
     * @see #overrideHttpMethodBasedComplianceCheckNegativeTest()
     */
    @Test
    public void overrideHttpMethodBasedComplianceCheckTest() {
        final Client c1 = ClientBuilder.newClient().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        try {
            c1.target("http://localhost:8080/myPath").request().method("DELETE", Entity.text("body"));
            Assert.fail("ProcessingException expected.");
        } catch (final ProcessingException ex) {
            Assert.assertThat(ex.getCause().getClass(), CoreMatchers.anyOf(CoreMatchers.<Class<?>>equalTo(ProtocolException.class), CoreMatchers.<Class<?>>equalTo(ConnectException.class)));
        }
        final Client c2 = ClientBuilder.newClient();
        try {
            c2.target("http://localhost:8080/myPath").request().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).method("DELETE", Entity.text("body"));
            Assert.fail("ProcessingException expected.");
        } catch (final ProcessingException ex) {
            Assert.assertThat(ex.getCause().getClass(), CoreMatchers.anyOf(CoreMatchers.<Class<?>>equalTo(ProtocolException.class), CoreMatchers.<Class<?>>equalTo(ConnectException.class)));
        }
        final Client c3 = ClientBuilder.newClient().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, false);
        try {
            c3.target("http://localhost:8080/myPath").request().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).method("DELETE", Entity.text("body"));
            Assert.fail("ProcessingException expected.");
        } catch (final ProcessingException ex) {
            Assert.assertThat(ex.getCause().getClass(), CoreMatchers.anyOf(CoreMatchers.<Class<?>>equalTo(ProtocolException.class), CoreMatchers.<Class<?>>equalTo(ConnectException.class)));
        }
    }

    /**
     * Checks that presence of request entity fo HTTP DELETE method fails in Jersey with {@code IllegalStateException}
     * if HTTP spec compliance is not suppressed by {@link ClientProperties#SUPPRESS_HTTP_COMPLIANCE_VALIDATION} property.
     * <p/>
     * See also JERSEY-1711.
     *
     * @see #overrideHttpMethodBasedComplianceCheckTest()
     */
    @Test
    public void overrideHttpMethodBasedComplianceCheckNegativeTest() {
        final Client c1 = ClientBuilder.newClient();
        try {
            c1.target("http://localhost:8080/myPath").request().method("DELETE", Entity.text("body"));
            Assert.fail("IllegalStateException expected.");
        } catch (final IllegalStateException expected) {
            // pass
        }
        final Client c2 = ClientBuilder.newClient();
        try {
            c2.target("http://localhost:8080/myPath").request().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, false).method("DELETE", Entity.text("body"));
            Assert.fail("IllegalStateException expected.");
        } catch (final IllegalStateException expected) {
            // pass
        }
        final Client c3 = ClientBuilder.newClient().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        try {
            c3.target("http://localhost:8080/myPath").request().property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, false).method("DELETE", Entity.text("body"));
            Assert.fail("IllegalStateException expected.");
        } catch (final IllegalStateException expected) {
            // pass
        }
    }

    @Test
    public void testNullResponseType() throws Exception {
        final Client client = ClientBuilder.newClient();
        client.register(new ClientRequestFilter() {
            @Override
            public void filter(final ClientRequestContext requestContext) throws IOException {
                requestContext.abortWith(Response.ok().build());
            }
        });
        final WebTarget target = client.target("http://localhost:8080/mypath");
        final Class<Response> responseType = null;
        final String[] methods = new String[]{ "GET", "PUT", "POST", "DELETE", "OPTIONS" };
        for (final String method : methods) {
            final Invocation.Builder request = target.request();
            try {
                request.method(method, responseType);
                Assert.fail("IllegalArgumentException expected.");
            } catch (final IllegalArgumentException iae) {
                // OK.
            }
            final Invocation build = ("PUT".equals(method)) ? request.build(method, Entity.entity("", TEXT_PLAIN_TYPE)) : request.build(method);
            try {
                build.submit(responseType);
                Assert.fail("IllegalArgumentException expected.");
            } catch (final IllegalArgumentException iae) {
                // OK.
            }
            try {
                build.invoke(responseType);
                Assert.fail("IllegalArgumentException expected.");
            } catch (final IllegalArgumentException iae) {
                // OK.
            }
            try {
                request.async().method(method, responseType);
                Assert.fail("IllegalArgumentException expected.");
            } catch (final IllegalArgumentException iae) {
                // OK.
            }
        }
    }

    @Test
    public void failedCallbackTest() throws InterruptedException {
        final Invocation.Builder builder = ClientBuilder.newClient().target("http://localhost:888/").request();
        for (int i = 0; i < 1; i++) {
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicInteger ai = new AtomicInteger(0);
            final InvocationCallback<String> callback = new InvocationCallback<String>() {
                @Override
                public void completed(final String arg0) {
                    try {
                        ai.set(((ai.get()) + 1));
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void failed(final Throwable throwable) {
                    try {
                        int result = 10;
                        if (throwable instanceof ProcessingException) {
                            result += 100;
                        }
                        final Throwable ioe = throwable.getCause();
                        if (ioe instanceof IOException) {
                            result += 1000;
                        }
                        ai.set(((ai.get()) + result));
                    } finally {
                        latch.countDown();
                    }
                }
            };
            final Invocation invocation = builder.buildGet();
            final Future<String> future = invocation.submit(callback);
            try {
                future.get();
                Assert.fail("future.get() should have failed.");
            } catch (final ExecutionException e) {
                final Throwable pe = e.getCause();
                Assert.assertTrue(("Execution exception cause is not a ProcessingException: " + (pe.toString())), (pe instanceof ProcessingException));
                final Throwable ioe = pe.getCause();
                Assert.assertTrue(("Execution exception cause is not an IOException: " + (ioe.toString())), (ioe instanceof IOException));
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.await(1, TimeUnit.SECONDS);
            Assert.assertEquals(1110, ai.get());
        }
    }

    public static class MyUnboundCallback<V> implements InvocationCallback<V> {
        private final CountDownLatch latch;

        private volatile Throwable throwable;

        public MyUnboundCallback(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void completed(final V v) {
            latch.countDown();
        }

        @Override
        public void failed(final Throwable throwable) {
            this.throwable = throwable;
            latch.countDown();
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }

    @Test
    public void failedUnboundGenericCallback() throws InterruptedException {
        final Invocation invocation = ClientBuilder.newClient().target("http://localhost:888/").request().buildGet();
        final CountDownLatch latch = new CountDownLatch(1);
        final JerseyInvocationTest.MyUnboundCallback<String> callback = new JerseyInvocationTest.MyUnboundCallback<String>(latch);
        invocation.submit(callback);
        latch.await(1, TimeUnit.SECONDS);
        Assert.assertThat(callback.getThrowable(), CoreMatchers.instanceOf(ProcessingException.class));
        Assert.assertThat(callback.getThrowable().getCause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
        Assert.assertThat(callback.getThrowable().getCause().getMessage(), CoreMatchers.allOf(CoreMatchers.containsString(JerseyInvocationTest.MyUnboundCallback.class.getName()), CoreMatchers.containsString(InvocationCallback.class.getName())));
    }

    @Test
    public void testSubmitWithGenericType() throws Exception {
        _submitWithGenericType(new javax.ws.rs.core.GenericType<String>() {});
    }

    @Test
    public void testSubmitWithGenericTypeParam() throws Exception {
        _submitWithGenericType(new javax.ws.rs.core.GenericType(String.class) {});
    }

    public static class TerminatingFilter implements ClientRequestFilter {
        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            requestContext.abortWith(Response.ok("ENTITY").build());
        }
    }

    @Test
    public void runtimeExceptionInAsyncInvocation() throws InterruptedException, ExecutionException {
        final AsyncInvoker ai = ClientBuilder.newClient().register(new JerseyInvocationTest.ExceptionInvokerFilter()).target("http://localhost:888/").request().async();
        try {
            ai.get().get();
            Assert.fail("ExecutionException should be thrown");
        } catch (ExecutionException ee) {
            Assert.assertEquals(ProcessingException.class, ee.getCause().getClass());
            Assert.assertEquals(RuntimeException.class, ee.getCause().getCause().getClass());
        }
    }

    public static class ExceptionInvokerFilter implements ClientRequestFilter {
        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            throw new RuntimeException("ExceptionInvokerFilter RuntimeException");
        }
    }
}

