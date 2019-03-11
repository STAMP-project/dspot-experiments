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
package org.glassfish.jersey.tests.e2e;


import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientAsyncExecutor;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.server.ManagedAsyncExecutor;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link org.glassfish.jersey.spi.ExecutorServiceProvider} E2E tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ExecutorServiceProviderTest extends JerseyTest {
    @Path("resource")
    @Produces("text/plain")
    public static class TestResourceA {
        @GET
        public String getSync() {
            return "resource";
        }

        @GET
        @Path("async")
        @ManagedAsync
        public String getAsync() {
            return "async-resource-" + (ExecutorServiceProviderTest.getResponseOnThread());
        }
    }

    // A separate resource class for the custom-async sub-path to ensure that
    // the named ExecutorService injection is only performed for the "/resource/custom-async" path.
    @Path("resource")
    @Produces("text/plain")
    public static class TestResourceB {
        @Inject
        @Named("custom")
        ExecutorService executorService;

        @GET
        @Path("custom-async")
        public void getCustomAsync(@Suspended
        final AsyncResponse asyncResponse) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    asyncResponse.resume(("custom-async-resource-" + (ExecutorServiceProviderTest.getResponseOnThread())));
                }
            });
        }
    }

    @ClientAsyncExecutor
    @ManagedAsyncExecutor
    @Named("custom")
    public static class CustomExecutorProvider implements ExecutorServiceProvider {
        private final Set<ExecutorService> executors = Collections.newSetFromMap(new IdentityHashMap<>());

        private volatile int executorCreationCount = 0;

        private volatile int executorReleaseCount = 0;

        public void reset() {
            for (ExecutorService executor : executors) {
                executor.shutdownNow();
            }
            executors.clear();
            executorCreationCount = 0;
            executorReleaseCount = 0;
        }

        @Override
        public ExecutorService getExecutorService() {
            return new ExecutorServiceProviderTest.CustomExecutorProvider.CustomExecutorService();
        }

        @Override
        public void dispose(final ExecutorService executorService) {
            executorService.shutdownNow();
        }

        private class CustomExecutorService implements ExecutorService {
            private final ExecutorService delegate;

            private final AtomicBoolean isCleanedUp;

            public CustomExecutorService() {
                this.isCleanedUp = new AtomicBoolean(false);
                this.delegate = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("async-request-%d").setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler()).build());
                (executorCreationCount)++;
                executors.add(this);
            }

            @Override
            public void shutdown() {
                tryCleanUp();
                delegate.shutdown();
            }

            @Override
            public List<Runnable> shutdownNow() {
                tryCleanUp();
                return delegate.shutdownNow();
            }

            @Override
            public boolean isShutdown() {
                return delegate.isShutdown();
            }

            @Override
            public boolean isTerminated() {
                return delegate.isTerminated();
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return delegate.awaitTermination(timeout, unit);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return delegate.submit(task);
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return delegate.submit(task, result);
            }

            @Override
            public Future<?> submit(Runnable task) {
                return delegate.submit(task);
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                return delegate.invokeAll(tasks);
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
                return delegate.invokeAll(tasks, timeout, unit);
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                return delegate.invokeAny(tasks);
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return delegate.invokeAny(tasks, timeout, unit);
            }

            private void tryCleanUp() {
                if (isCleanedUp.compareAndSet(false, true)) {
                    executors.remove(this);
                    (executorReleaseCount)++;
                }
            }

            @Override
            public void execute(Runnable command) {
                delegate.execute(command);
            }
        }
    }

    private static final ExecutorServiceProviderTest.CustomExecutorProvider serverExecutorProvider = new ExecutorServiceProviderTest.CustomExecutorProvider();

    /**
     * Reproducer for JERSEY-2205 (client-side).
     *
     * @throws Exception
     * 		in case of a test error.
     */
    @Test
    public void testCustomClientExecutorsInjectionAndReleasing() throws Exception {
        final ExecutorServiceProviderTest.CustomExecutorProvider provider = new ExecutorServiceProviderTest.CustomExecutorProvider();
        Client client = ClientBuilder.newClient().register(provider);
        Response response = client.target(getBaseUri()).path("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("resource", response.readEntity(String.class));
        // no executors should be created or released at this point yet
        Assert.assertEquals("Unexpected number of created client executors", 0, provider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released client executors", 0, provider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of client executors stored in the set.", 0, provider.executors.size());
        Future<Response> fr = client.target(getBaseUri()).path("resource").request().async().get();
        response = fr.get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("resource", response.readEntity(String.class));
        // single executor should be created but not released at this point yet
        Assert.assertEquals("Unexpected number of created client executors", 1, provider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released client executors", 0, provider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of client executors stored in the set.", 1, provider.executors.size());
        client.close();
        // the created executor needs to be released by now; no more executors should be created
        Assert.assertEquals("Unexpected number of created client executors", 1, provider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released client executors", 1, provider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of client executors stored in the set.", 0, provider.executors.size());
    }

    /**
     * Reproducer for JERSEY-2205 (server-side).
     *
     * @throws Exception
     * 		in case of a test error.
     */
    @Test
    public void testCustomServerExecutorsInjectionAndReleasing() throws Exception {
        // reset server executor statistics to avoid data pollution from other test methods
        ExecutorServiceProviderTest.serverExecutorProvider.reset();
        Response response = target("resource").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("resource", response.readEntity(String.class));
        // no executors should be created or released at this point yet
        Assert.assertEquals("Unexpected number of created server executors", 0, ExecutorServiceProviderTest.serverExecutorProvider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released server executors", 0, ExecutorServiceProviderTest.serverExecutorProvider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of server executors stored in the set.", 0, ExecutorServiceProviderTest.serverExecutorProvider.executors.size());
        response = target("resource/async").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("async-resource-passed", response.readEntity(String.class));
        // single executor should be created but not released at this point yet
        Assert.assertEquals("Unexpected number of created server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released server executors", 0, ExecutorServiceProviderTest.serverExecutorProvider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of server executors stored in the set.", 1, ExecutorServiceProviderTest.serverExecutorProvider.executors.size());
        tearDown();// stopping test container

        // the created executor needs to be released by now; no more executors should be created
        Assert.assertEquals("Unexpected number of created server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of server executors stored in the set.", 0, ExecutorServiceProviderTest.serverExecutorProvider.executors.size());
        setUp();// re-starting test container to ensure proper post-test tearDown.

    }

    /**
     * Test named custom executor injection and release mechanism.
     *
     * @throws Exception
     * 		in case of a test error.
     */
    @Test
    public void testCustomNamedServerExecutorsInjectionAndReleasing() throws Exception {
        ExecutorServiceProviderTest.serverExecutorProvider.reset();
        Response response = target("resource/custom-async").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("custom-async-resource-passed", response.readEntity(String.class));
        // single executor should be created but not released at this point yet
        Assert.assertEquals("Unexpected number of created server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released server executors", 0, ExecutorServiceProviderTest.serverExecutorProvider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of server executors stored in the set.", 1, ExecutorServiceProviderTest.serverExecutorProvider.executors.size());
        tearDown();// stopping test container

        // the created executor needs to be released by now; no more executors should be created
        Assert.assertEquals("Unexpected number of created server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorCreationCount);
        Assert.assertEquals("Unexpected number of released server executors", 1, ExecutorServiceProviderTest.serverExecutorProvider.executorReleaseCount);
        Assert.assertEquals("Unexpected number of server executors stored in the set.", 0, ExecutorServiceProviderTest.serverExecutorProvider.executors.size());
        setUp();// re-starting test container to ensure proper post-test tearDown.

    }
}

