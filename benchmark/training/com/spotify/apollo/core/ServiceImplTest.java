/**
 * -\-\-
 * Spotify Apollo Service Core (aka Leto)
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.core;


import Service.Instance;
import Service.Signaller;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.IntMath;
import com.spotify.apollo.module.AbstractApolloModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ServiceImplTest {
    @Test
    public void testEmpty() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        Assert.assertThat(service.getServiceName(), Matchers.is("test"));
        try (Service.Instance instance = service.start()) {
            Assert.assertThat(instance.getService(), Matchers.is(service));
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
        }
    }

    @Test
    public void testConfig() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dconfig=value")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getString("config"), Matchers.is("value"));
        }
    }

    @Test
    public void testEnvConfig() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start(new String[]{  }, ImmutableMap.of("APOLLO_A_B", "value"))) {
            Assert.assertThat(instance.getConfig().getString("a.b"), Matchers.is("value"));
        }
    }

    @Test
    public void testEnvConfigCustomPrefix() throws Exception {
        Service service = ServiceImpl.builder("test").withEnvVarPrefix("horse").build();
        try (Service.Instance instance = service.start(new String[]{  }, ImmutableMap.of("horse_A_B", "value"))) {
            Assert.assertThat(instance.getConfig().getString("a.b"), Matchers.is("value"));
        }
    }

    @Test
    public void testEnvConfigWithLeadingUnderscores() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start(new String[]{  }, ImmutableMap.of("APOLLO___A_B", "value"))) {
            Assert.assertThat(instance.getConfig().getString("_a.b"), Matchers.is("value"));
        }
    }

    @Test
    public void testEnvConfigWithUnderscoresEverywhere() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start(new String[]{  }, ImmutableMap.of("APOLLO_A___B__C______D_____E__", "value"))) {
            Assert.assertThat(instance.getConfig().getString("a._b_c___d.__e_"), Matchers.is("value"));
        }
    }

    @Test
    public void testOverlaysExplicitConfigFile() throws IOException {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--config", "src/test/files/overlay.conf")) {
            Config config = instance.getConfig();
            Assert.assertThat(config.getString("bundled.value"), Matchers.is("is loaded"));
            Assert.assertThat(config.getString("bundled.shadowed"), Matchers.is("overlayed"));
            Assert.assertThat(config.getString("some.key"), Matchers.is("has a value"));
        }
    }

    @Test
    public void testUsesConfig() throws IOException {
        Service service = ServiceImpl.builder("test").build();
        Config config = ConfigFactory.empty().withValue("this.key", ConfigValueFactory.fromAnyRef("value for this.key"));
        try (Service.Instance instance = service.start(new String[]{  }, config)) {
            Assert.assertThat(instance.getConfig().getString("this.key"), Matchers.is("value for this.key"));
        }
    }

    @Test
    public void testConfigSupportsColonValues() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dconfig=value:more")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getString("config"), Matchers.is("value:more"));
        }
    }

    @Test
    public void testConfigSupportsEqualsValues() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dconfig=value=more")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getString("config"), Matchers.is("value=more"));
        }
    }

    @Test
    public void testUnresolved() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dfoo=bar", "hello")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.contains("hello"));
            Assert.assertThat(instance.getConfig().getString("foo"), Matchers.is("bar"));
        }
    }

    @Test
    public void testArgsDone() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dfoo=bar", "--", "xyz", "-Dbar=baz", "abc")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.contains("xyz", "-Dbar=baz", "abc"));
            Assert.assertThat(instance.getConfig().getString("foo"), Matchers.is("bar"));
            Assert.assertThat(instance.getConfig().hasPath("bar"), Matchers.is(false));
        }
    }

    @Test
    public void testArgsAreInConfig() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-Dfoo=bar", "--", "xyz", "-Dbar=baz", "abc")) {
            Assert.assertThat(instance.getConfig().hasPath(CommonConfigKeys.APOLLO_ARGS_CORE.getKey()), Matchers.is(true));
            Assert.assertThat(instance.getConfig().hasPath(CommonConfigKeys.APOLLO_ARGS_UNPARSED.getKey()), Matchers.is(true));
            Assert.assertThat(instance.getConfig().getStringList(CommonConfigKeys.APOLLO_ARGS_CORE.getKey()), Matchers.contains("-Dfoo=bar", "--", "xyz", "-Dbar=baz", "abc"));
            Assert.assertThat(instance.getConfig().getStringList(CommonConfigKeys.APOLLO_ARGS_UNPARSED.getKey()), Matchers.contains("xyz", "-Dbar=baz", "abc"));
        }
    }

    @Test
    public void testVerbosity() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-v")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is(1));
        }
    }

    @Test
    public void testVerbosityLong() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--verbose")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is(1));
        }
    }

    @Test
    public void testVerbosityLongMany() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--verbose", "--verbose")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is(2));
        }
    }

    @Test
    public void testVerbosityStacked() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-vvvv")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is(4));
        }
    }

    @Test
    public void testVerbosityMany() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-vv", "-vv")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is(4));
        }
    }

    @Test
    public void testVerbosityQuiet() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-q")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-3)));
        }
    }

    @Test
    public void testVerbosityQuietLong() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--quiet")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-3)));
        }
    }

    @Test
    public void testVerbosityQuietThenVerbose() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--quiet", "--verbose")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-2)));
        }
    }

    @Test
    public void testVerbosityConcise() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-c")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-1)));
        }
    }

    @Test
    public void testVerbosityConciseLong() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--concise")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-1)));
        }
    }

    @Test
    public void testVerbosityConciseLongMany() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--concise", "--concise")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-2)));
        }
    }

    @Test
    public void testVerbosityConciseStacked() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-cccc")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-4)));
        }
    }

    @Test
    public void testVerbosityConciseMany() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("-cc", "-cc")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getInt("logging.verbosity"), Matchers.is((-4)));
        }
    }

    @Test
    public void testSyslog() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--syslog")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getBoolean("logging.syslog"), Matchers.is(true));
        }
    }

    @Test
    public void testSyslogTrue() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--syslog=true")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getBoolean("logging.syslog"), Matchers.is(true));
        }
    }

    @Test
    public void testSyslogFalse() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--syslog=false")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.is(Matchers.empty()));
            Assert.assertThat(instance.getConfig().getBoolean("logging.syslog"), Matchers.is(false));
        }
    }

    @Test(expected = ApolloHelpException.class)
    public void testHelp() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        Services.run(service, "--help");
    }

    @Test(timeout = 1000)
    public void testSignalShutdown() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (final Service.Instance instance = service.start("--syslog=false")) {
            // Force predictable concurrency of signal and main loop start
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            instance.getExecutorService().submit(() -> {
                countDownLatch.await();
                Thread.sleep(200);
                instance.getSignaller().signalShutdown();
                return null;
            });
            countDownLatch.countDown();
            instance.waitForShutdown();
        }
    }

    @Test
    public void testUnresolvedMixed() throws Exception {
        // Issue #8
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start("--hello", "-Dfoo=bar", "bye")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.contains("--hello", "bye"));
            Assert.assertThat(instance.getConfig().getString("foo"), Matchers.is("bar"));
        }
    }

    @Test(timeout = 1000, expected = InterruptedException.class)
    public void testInterrupt() throws Exception {
        Service service = ServiceImpl.builder("test").withShutdownInterrupt(true).build();
        try (Service.Instance instance = service.start()) {
            instance.getScheduledExecutorService().schedule(new ServiceImplTest.Shutdowner(instance.getSignaller()), 5, TimeUnit.MILLISECONDS);
            new CountDownLatch(1).await();// Wait forever

        }
    }

    @Test
    public void testHelpFallthrough() throws Exception {
        Service service = ServiceImpl.builder("test").withCliHelp(false).build();
        try (Service.Instance instance = service.start("--help", "-h")) {
            Assert.assertThat(instance.getUnprocessedArgs(), Matchers.contains("--help", "-h"));
        }
    }

    @Test
    public void testMultipleModules() throws Exception {
        AtomicInteger count = new AtomicInteger(0);
        Service service = ServiceImpl.builder("test").withModule(new CountingModuleWithPriority(1.0, count)).withModule(new CountingModuleWithPriority(1.0, count)).build();
        try (Service.Instance instance = service.start()) {
            instance.getSignaller().signalShutdown();
            instance.waitForShutdown();
        }
        Assert.assertThat(count.get(), Matchers.is(2));
    }

    @Test
    public void testModuleClassesAreLifecycleManaged() throws Exception {
        AtomicBoolean created = new AtomicBoolean(false);
        AtomicBoolean closed = new AtomicBoolean(false);
        ModuleWithLifecycledKeys lifecycleModule = new ModuleWithLifecycledKeys(created, closed);
        Service service = ServiceImpl.builder("test").withModule(lifecycleModule).build();
        try (Service.Instance instance = service.start()) {
            instance.getSignaller().signalShutdown();
            instance.waitForShutdown();
        }
        Assert.assertThat(created.get(), Matchers.is(true));
        Assert.assertThat(closed.get(), Matchers.is(true));
    }

    @Test
    public void testResolve() throws Exception {
        AtomicBoolean created = new AtomicBoolean(false);
        AtomicBoolean closed = new AtomicBoolean(false);
        ModuleWithLifecycledKeys lifecycleModule = new ModuleWithLifecycledKeys(created, closed);
        Service service = ServiceImpl.builder("test").withModule(lifecycleModule).build();
        try (Service.Instance instance = service.start()) {
            ModuleWithLifecycledKeys.Foo foo = instance.resolve(ModuleWithLifecycledKeys.Foo.class);
            Assert.assertNotNull(foo);
            instance.getSignaller().signalShutdown();
            instance.waitForShutdown();
        }
    }

    @Test(expected = ApolloConfigurationException.class)
    public void testResolveInvalid() throws Exception {
        final Class<?> unusedClass = IntMath.class;
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start()) {
            instance.resolve(unusedClass);
        }
    }

    @Test(timeout = 1000)
    public void testCleanShutdown() throws Exception {
        Runtime runtime = Mockito.mock(Runtime.class);
        Service service = ServiceImpl.builder("test").withRuntime(runtime).build();
        // noinspection EmptyTryBlock
        try (Service.Instance ignored = service.start()) {
            // Do nothing
        }
        // Simulate JVM shutdown hook
        ArgumentCaptor<Thread> threadCaptor = ArgumentCaptor.forClass(Thread.class);
        Mockito.verify(runtime).addShutdownHook(threadCaptor.capture());
        // Should not block for more than 1 sec (will be interrupted by test timeout if it blocks)
        threadCaptor.getValue().run();
    }

    @Test
    public void testHasExecutors() throws Exception {
        Service service = ServiceImpl.builder("test").build();
        try (Service.Instance instance = service.start()) {
            Assert.assertNotNull(instance.getExecutorService());
            Assert.assertNotNull(instance.getScheduledExecutorService());
        }
    }

    @Test(timeout = 1000)
    public void testExceptionDuringInit() throws Exception {
        Runtime runtime = Mockito.mock(Runtime.class);
        Service service = ServiceImpl.builder("test").withModule(new AbstractApolloModule() {
            @Override
            protected void configure() {
                throw new RuntimeException("Fail foobar");
            }

            @Override
            public String getId() {
                return "fail";
            }
        }).withRuntime(runtime).build();
        try (Service.Instance instance = service.start()) {
            // Should not be reached
            Assert.fail("Service configuration should have failed due to 'fail' module but didn't.");
        } catch (Throwable ex) {
            // Should be due to the 'fail' module above
            Assert.assertTrue((ex instanceof RuntimeException));
            Assert.assertThat(ex.getMessage(), Matchers.containsString("Fail foobar"));
        }
        // Simulate JVM shutdown hook
        ArgumentCaptor<Thread> threadCaptor = ArgumentCaptor.forClass(Thread.class);
        Mockito.verify(runtime).addShutdownHook(threadCaptor.capture());
        // Should not block for more than 1 sec (will be interrupted by test timeout if it blocks)
        threadCaptor.getValue().run();
    }

    @Test(timeout = 1000, expected = InterruptedException.class)
    public void testSimulateCtrlCInterrupted() throws Exception {
        final Runtime runtime = Mockito.mock(Runtime.class);
        Service service = ServiceImpl.builder("test").withRuntime(runtime).withShutdownInterrupt(true).build();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (Service.Instance instance = service.start()) {
            executorService.submit(new ServiceImplTest.ShutdownHookSim(runtime));
            new CountDownLatch(1).await();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test(timeout = 1000)
    public void testSimulateCtrlCWaitingForShutdown() throws Exception {
        final Runtime runtime = Mockito.mock(Runtime.class);
        Service service = ServiceImpl.builder("test").withRuntime(runtime).build();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (Service.Instance instance = service.start()) {
            executorService.submit(new ServiceImplTest.ShutdownHookSim(runtime));
            instance.waitForShutdown();
        } catch (Throwable ex) {
            Assert.fail("Not clean shutdown");
        } finally {
            executorService.shutdownNow();
        }
    }

    static class Shutdowner implements Callable<Void> {
        private final Signaller signaller;

        public Shutdowner(Service.Signaller signaller) {
            this.signaller = signaller;
        }

        @Override
        public Void call() throws Exception {
            signaller.signalShutdown();
            return null;
        }
    }

    private static class ShutdownHookSim implements Callable<Void> {
        private final Runtime runtime;

        public ShutdownHookSim(Runtime runtime) {
            this.runtime = runtime;
        }

        @Override
        public Void call() throws Exception {
            // Simulate JVM shutdown hook
            ArgumentCaptor<Thread> threadCaptor = ArgumentCaptor.forClass(Thread.class);
            Mockito.verify(runtime).addShutdownHook(threadCaptor.capture());
            // This is what simulates the shutdown hook
            threadCaptor.getValue().run();
            return null;
        }
    }
}

