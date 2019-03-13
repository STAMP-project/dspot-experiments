/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.common.process.internal;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.spi.ScheduledThreadPoolExecutorProvider;
import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * ExecutorProviders unit tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ExecutorProvidersTest extends AbstractBinder {
    /**
     * Custom scheduler injection qualifier.
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CustomScheduler {}

    /**
     * Custom scheduler provider.
     */
    @ExecutorProvidersTest.CustomScheduler
    public static class CustomSchedulerProvider extends ScheduledThreadPoolExecutorProvider {
        /**
         * Create a new instance of the scheduled thread pool executor provider.
         */
        public CustomSchedulerProvider() {
            super("custom-scheduler");
        }
    }

    /**
     * Custom named scheduler provider.
     */
    @Named("custom-scheduler")
    public static class CustomNamedSchedulerProvider extends ScheduledThreadPoolExecutorProvider {
        /**
         * Create a new instance of the scheduled thread pool executor provider.
         */
        public CustomNamedSchedulerProvider() {
            super("custom-named-scheduler");
        }
    }

    /**
     * Custom executor injection qualifier.
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CustomExecutor {}

    /**
     * Custom executor provider.
     */
    @ExecutorProvidersTest.CustomExecutor
    public static class CustomExecutorProvider extends ThreadPoolExecutorProvider {
        /**
         * Create a new instance of the thread pool executor provider.
         */
        public CustomExecutorProvider() {
            super("custom-executor");
        }
    }

    /**
     * Custom named executor provider.
     */
    @Named("custom-executor")
    public static class CustomNamedExecutorProvider extends ThreadPoolExecutorProvider {
        /**
         * Create a new instance of the thread pool executor provider.
         */
        public CustomNamedExecutorProvider() {
            super("custom-named-executor");
        }
    }

    /**
     * A task to retrieve the current thread name.
     */
    public static class CurrentThreadNameRetrieverTask implements Producer<String> {
        @Override
        public String call() {
            return Thread.currentThread().getName();
        }
    }

    /**
     * Notifier of pre-destroy method invocation.
     */
    public static class PreDestroyNotifier {
        private final CountDownLatch latch = new CountDownLatch(1);

        @PreDestroy
        public void preDestroy() {
            latch.countDown();
        }

        public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }
    }

    /**
     * Injectable executor client class.
     */
    public static class InjectedExecutorClient {
        @Inject
        private ExecutorProvidersTest.PreDestroyNotifier preDestroyNotifier;

        @Inject
        @ExecutorProvidersTest.CustomExecutor
        private ExecutorService customExecutor;

        @Inject
        @Named("custom-executor")
        private ExecutorService customNamedExecutor;

        @Inject
        @ExecutorProvidersTest.CustomScheduler
        private ScheduledExecutorService customScheduler;

        @Inject
        @ExecutorProvidersTest.CustomScheduler
        private ExecutorService customSchedulerAsExecutor;

        @Inject
        @Named("custom-scheduler")
        private ScheduledExecutorService customNamedScheduler;

        @Inject
        @Named("custom-scheduler")
        private ScheduledExecutorService customNamedSchedulerAsExecutor;
    }

    private InjectionManager injectionManager;

    /**
     * Test executor and scheduler injection as well as the proper shutdown when injection manager is closed.
     *
     * @throws Exception
     * 		in case of a test error.
     */
    @Test
    public void testExecutorInjectionAndReleasing() throws Exception {
        final ExecutorProvidersTest.InjectedExecutorClient executorClient = Injections.getOrCreate(injectionManager, ExecutorProvidersTest.InjectedExecutorClient.class);
        Assert.assertThat(executorClient.customExecutor, Matchers.notNullValue());
        Assert.assertThat(executorClient.customNamedExecutor, Matchers.notNullValue());
        Assert.assertThat(executorClient.customScheduler, Matchers.notNullValue());
        Assert.assertThat(executorClient.customNamedScheduler, Matchers.notNullValue());
        Assert.assertThat(executorClient.customSchedulerAsExecutor, Matchers.notNullValue());
        Assert.assertThat(executorClient.customNamedSchedulerAsExecutor, Matchers.notNullValue());
        ExecutorProvidersTest.CurrentThreadNameRetrieverTask nameRetrieverTask = new ExecutorProvidersTest.CurrentThreadNameRetrieverTask();
        // Test authenticity of injected executors
        Assert.assertThat(executorClient.customExecutor.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-executor-"));
        Assert.assertThat(executorClient.customNamedExecutor.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-named-executor-"));
        // Test authenticity of injected schedulers
        Assert.assertThat(executorClient.customScheduler.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-scheduler-"));
        Assert.assertThat(executorClient.customNamedScheduler.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-named-scheduler-"));
        Assert.assertThat(executorClient.customSchedulerAsExecutor.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-scheduler-"));
        Assert.assertThat(executorClient.customNamedSchedulerAsExecutor.submit(nameRetrieverTask).get(), Matchers.startsWith("custom-named-scheduler-"));
        // Test proper executor shutdown when locator is shut down.
        injectionManager.shutdown();
        Assert.assertThat("Waiting for pre-destroy timed out.", executorClient.preDestroyNotifier.await(3, TimeUnit.SECONDS), Matchers.is(true));
        testShutDown("customExecutor", executorClient.customExecutor);
        testShutDown("customNamedExecutor", executorClient.customNamedExecutor);
        testShutDown("customScheduler", executorClient.customScheduler);
        testShutDown("customNamedScheduler", executorClient.customNamedScheduler);
        testShutDown("customSchedulerAsExecutor", executorClient.customSchedulerAsExecutor);
        testShutDown("customNamedSchedulerAsExecutor", executorClient.customNamedSchedulerAsExecutor);
    }
}

