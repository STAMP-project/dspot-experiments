/**
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.impl.launcher.commands.HelloCommand;
import io.vertx.core.impl.launcher.commands.RunCommand;
import io.vertx.core.impl.launcher.commands.VersionCommand;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.metrics.MetricsOptionsTest;
import io.vertx.test.core.AsyncTestBase;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.verticles.TestVerticle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class LauncherTest extends VertxTestBase {
    private String expectedVersion;

    private ByteArrayOutputStream out;

    private PrintStream stream;

    private Vertx vertx;

    @Test
    public void testVersion() throws Exception {
        String[] args = new String[]{ "-version" };
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        dispatch(args);
        final VersionCommand version = ((VersionCommand) (getExistingCommandInstance("version")));
        assertNotNull(version);
        assertEquals(version.getVersion(), expectedVersion);
    }

    @Test
    public void testRunVerticleWithoutArgs() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        launcher.assertHooksInvoked();
    }

    @Test
    public void testRunWithoutArgs() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher() {
            @Override
            public PrintStream getPrintStream() {
                return stream;
            }
        };
        String[] args = new String[]{ "run" };
        dispatch(args);
        assertTrue(out.toString().contains("The argument 'main-verticle' is required"));
    }

    @Test
    public void testNoArgsAndNoMainVerticle() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher() {
            @Override
            public PrintStream getPrintStream() {
                return stream;
            }
        };
        String[] args = new String[]{  };
        dispatch(args);
        assertTrue(out.toString().contains("Usage:"));
        assertTrue(out.toString().contains("bare"));
        assertTrue(out.toString().contains("run"));
        assertTrue(out.toString().contains("hello"));
    }

    @Test
    public void testRunVerticle() throws Exception {
        testRunVerticleMultiple(1);
    }

    @Test
    public void testRunVerticleMultipleInstances() throws Exception {
        testRunVerticleMultiple(10);
    }

    @Test
    public void testRunVerticleClustered() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster" };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        launcher.assertHooksInvoked();
    }

    @Test
    public void testRunVerticleHA() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-ha" };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        launcher.assertHooksInvoked();
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestNoArgs() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher();
        String[] args = new String[0];
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        cleanup(launcher);
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestWithArgs() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher();
        String[] args = new String[]{ "-cluster", "-worker", "-instances=10" };
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 10);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        cleanup(launcher);
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestWithCustomCommand() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher-hello.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher-hello.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher();
        HelloCommand.called = false;
        String[] args = new String[]{ "--name=vert.x" };
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> HelloCommand.called);
    }

    @Test
    public void testRunVerticleWithoutMainVerticleInManifestButWithCustomCommand() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher-Default-Command.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Default-Command.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher();
        HelloCommand.called = false;
        String[] args = new String[]{ "--name=vert.x" };
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> HelloCommand.called);
    }

    @Test
    public void testRunWithOverriddenDefaultCommand() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher-hello.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher-hello.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        HelloCommand.called = false;
        String[] args = new String[]{ "run", TestVerticle.class.getName(), "--name=vert.x" };
        Launcher launcher = new Launcher();
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        cleanup(launcher);
    }

    @Test
    public void testRunWithOverriddenDefaultCommandRequiringArgs() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher-run.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher-run.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String[] args = new String[]{ TestVerticle.class.getName() };
        Launcher launcher = new Launcher();
        launcher.dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        cleanup(launcher);
    }

    @Test
    public void testRunVerticleWithExtendedMainVerticleNoArgs() throws Exception {
        LauncherTest.MySecondLauncher launcher = new LauncherTest.MySecondLauncher();
        String[] args = new String[0];
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
    }

    @Test
    public void testRunVerticleWithExtendedMainVerticleWithArgs() throws Exception {
        LauncherTest.MySecondLauncher launcher = new LauncherTest.MySecondLauncher();
        String[] args = new String[]{ "-cluster", "-worker" };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
    }

    @Test
    public void testFatJarWithHelp() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher() {
            @Override
            public PrintStream getPrintStream() {
                return stream;
            }
        };
        String[] args = new String[]{ "--help" };
        launcher.dispatch(args);
        assertTrue(out.toString().contains("Usage"));
        assertTrue(out.toString().contains("run"));
        assertTrue(out.toString().contains("version"));
        assertTrue(out.toString().contains("bare"));
    }

    @Test
    public void testFatJarWithCommandHelp() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher() {
            @Override
            public PrintStream getPrintStream() {
                return stream;
            }
        };
        String[] args = new String[]{ "hello", "--help" };
        launcher.dispatch(args);
        assertTrue(out.toString().contains("Usage"));
        assertTrue(out.toString().contains("hello"));
        assertTrue(out.toString().contains("A simple command to wish you a good day."));// Description text.

    }

    @Test
    public void testFatJarWithMissingCommandHelp() throws Exception {
        // Copy the right manifest
        File manifest = new File("target/test-classes/META-INF/MANIFEST-Launcher.MF");
        if (!(manifest.isFile())) {
            throw new IllegalStateException("Cannot find the MANIFEST-Launcher.MF file");
        }
        File target = new File("target/test-classes/META-INF/MANIFEST.MF");
        Files.copy(manifest.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Launcher launcher = new Launcher() {
            @Override
            public PrintStream getPrintStream() {
                return stream;
            }
        };
        String[] args = new String[]{ "not-a-command", "--help" };
        launcher.dispatch(args);
        assertTrue(out.toString().contains("The command 'not-a-command' is not a valid command."));
    }

    @Test
    public void testRunVerticleWithConfString() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        JsonObject conf = new JsonObject().put("foo", "bar").put("wibble", 123);
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-conf", conf.encode() };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(conf, TestVerticle.conf);
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testRunVerticleWithConfFile() throws Exception {
        Path tempDir = testFolder.newFolder().toPath();
        Path tempFile = Files.createTempFile(tempDir, "conf", "json");
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        JsonObject conf = new JsonObject().put("foo", "bar").put("wibble", 123);
        Files.write(tempFile, conf.encode().getBytes());
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-conf", tempFile.toString() };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(conf, TestVerticle.conf);
    }

    @Test
    public void testConfigureFromSystemProperties() throws Exception {
        testConfigureFromSystemProperties(false);
    }

    @Test
    public void testConfigureFromSystemPropertiesClustered() throws Exception {
        testConfigureFromSystemProperties(true);
    }

    @Test
    public void testConfigureFromJsonFile() throws Exception {
        testConfigureFromJson(true);
    }

    @Test
    public void testConfigureFromJsonString() throws Exception {
        testConfigureFromJson(false);
    }

    @Test
    public void testCustomMetricsOptions() throws Exception {
        System.setProperty(((RunCommand.METRICS_OPTIONS_PROP_PREFIX) + "enabled"), "true");
        System.setProperty(((RunCommand.METRICS_OPTIONS_PROP_PREFIX) + "customProperty"), "customPropertyValue");
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(MetricsOptionsTest.createMetricsFromMetaInfLoader("io.vertx.core.CustomMetricsFactory"));
        try {
            dispatch(args);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = launcher.getVertxOptions();
        CustomMetricsOptions custom = ((CustomMetricsOptions) (opts.getMetricsOptions()));
        assertEquals("customPropertyValue", custom.getCustomProperty());
    }

    @Test
    public void testConfigureFromSystemPropertiesInvalidPropertyName() throws Exception {
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "nosuchproperty"), "123");
        // Should be ignored
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = launcher.getVertxOptions();
        VertxOptions def = new VertxOptions();
        if (opts.getMetricsOptions().isEnabled()) {
            def.getMetricsOptions().setEnabled(true);
        }
        assertEquals(def.toJson(), opts.toJson());
    }

    @Test
    public void testConfigureFromSystemPropertiesInvalidPropertyType() throws Exception {
        // One for each type that we support
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "eventLoopPoolSize"), "sausages");
        // Should be ignored
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = launcher.getVertxOptions();
        VertxOptions def = new VertxOptions();
        if (opts.getMetricsOptions().isEnabled()) {
            def.getMetricsOptions().setEnabled(true);
        }
        assertEquals(def.toJson(), opts.toJson());
    }

    @Test
    public void testCustomMetricsOptionsFromJsonFile() throws Exception {
        testCustomMetricsOptionsFromJson(true);
    }

    @Test
    public void testCustomMetricsOptionsFromJsonString() throws Exception {
        testCustomMetricsOptionsFromJson(false);
    }

    @Test
    public void testWhenPassingTheMainObject() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        int instances = 10;
        launcher.dispatch(launcher, new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-instances", "10" });
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == instances);
    }

    @Test
    public void testBare() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        dispatch(new String[]{ "bare" });
        AsyncTestBase.assertWaitUntil(() -> launcher.afterStartingVertxInvoked);
    }

    @Test
    public void testBareAlias() throws Exception {
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        dispatch(new String[]{ "-ha" });
        AsyncTestBase.assertWaitUntil(() -> launcher.afterStartingVertxInvoked);
    }

    @Test
    public void testConfigureClusterHostPortFromProperties() throws Exception {
        int clusterPort = TestUtils.randomHighPortInt();
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "clusterHost"), "127.0.0.1");
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "clusterPort"), Integer.toString(clusterPort));
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster" };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals("127.0.0.1", launcher.options.getClusterHost());
        assertEquals(clusterPort, launcher.options.getClusterPort());
        assertNull(launcher.options.getClusterPublicHost());
        assertEquals((-1), launcher.options.getClusterPublicPort());
    }

    @Test
    public void testConfigureClusterHostPortFromCommandLine() throws Exception {
        int clusterPort = TestUtils.randomHighPortInt();
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster", "--cluster-host", "127.0.0.1", "--cluster-port", Integer.toString(clusterPort) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals("127.0.0.1", launcher.options.getClusterHost());
        assertEquals(clusterPort, launcher.options.getClusterPort());
        assertNull(launcher.options.getClusterPublicHost());
        assertEquals((-1), launcher.options.getClusterPublicPort());
    }

    @Test
    public void testConfigureClusterPublicHostPortFromCommandLine() throws Exception {
        int clusterPublicPort = TestUtils.randomHighPortInt();
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster", "--cluster-public-host", "127.0.0.1", "--cluster-public-port", Integer.toString(clusterPublicPort) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals("127.0.0.1", launcher.options.getClusterPublicHost());
        assertEquals(clusterPublicPort, launcher.options.getClusterPublicPort());
    }

    @Test
    public void testOverrideClusterHostPortFromProperties() throws Exception {
        int clusterPort = TestUtils.randomHighPortInt();
        int newClusterPort = TestUtils.randomHighPortInt();
        int newClusterPublicPort = TestUtils.randomHighPortInt();
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "clusterHost"), "127.0.0.2");
        System.setProperty(((RunCommand.VERTX_OPTIONS_PROP_PREFIX) + "clusterPort"), Integer.toString(clusterPort));
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        launcher.clusterHost = "127.0.0.1";
        launcher.clusterPort = newClusterPort;
        launcher.clusterPublicHost = "127.0.0.3";
        launcher.clusterPublicPort = newClusterPublicPort;
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster" };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals("127.0.0.1", launcher.options.getClusterHost());
        assertEquals(newClusterPort, launcher.options.getClusterPort());
        assertEquals("127.0.0.3", launcher.options.getClusterPublicHost());
        assertEquals(newClusterPublicPort, launcher.options.getClusterPublicPort());
    }

    @Test
    public void testOverrideClusterHostPortFromCommandLine() throws Exception {
        int clusterPort = TestUtils.randomHighPortInt();
        int clusterPublicPort = TestUtils.randomHighPortInt();
        int newClusterPort = TestUtils.randomHighPortInt();
        int newClusterPublicPort = TestUtils.randomHighPortInt();
        LauncherTest.MyLauncher launcher = new LauncherTest.MyLauncher();
        launcher.clusterHost = "127.0.0.1";
        launcher.clusterPort = newClusterPort;
        launcher.clusterPublicHost = "127.0.0.3";
        launcher.clusterPublicPort = newClusterPublicPort;
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster", "--cluster-host", "127.0.0.2", "--cluster-port", Integer.toString(clusterPort), "--cluster-public-host", "127.0.0.4", "--cluster-public-port", Integer.toString(clusterPublicPort) };
        dispatch(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals("127.0.0.1", launcher.options.getClusterHost());
        assertEquals(newClusterPort, launcher.options.getClusterPort());
        assertEquals("127.0.0.3", launcher.options.getClusterPublicHost());
        assertEquals(newClusterPublicPort, launcher.options.getClusterPublicPort());
    }

    class MyLauncher extends Launcher {
        boolean afterConfigParsed = false;

        boolean beforeStartingVertxInvoked = false;

        boolean afterStartingVertxInvoked = false;

        boolean beforeDeployingVerticle = false;

        VertxOptions options;

        DeploymentOptions deploymentOptions;

        JsonObject config;

        String clusterHost;

        int clusterPort;

        String clusterPublicHost;

        int clusterPublicPort;

        PrintStream stream = new PrintStream(out);

        /**
         *
         *
         * @return the printer used to write the messages. Defaults to {@link System#out}.
         */
        @Override
        public PrintStream getPrintStream() {
            return stream;
        }

        public Vertx getVertx() {
            return vertx;
        }

        public VertxOptions getVertxOptions() {
            return options;
        }

        @Override
        public void afterConfigParsed(JsonObject config) {
            afterConfigParsed = true;
            this.config = config;
        }

        @Override
        public void beforeStartingVertx(VertxOptions options) {
            beforeStartingVertxInvoked = true;
            this.options = options;
            if ((clusterHost) != null) {
                options.setClusterHost(clusterHost);
                options.setClusterPort(clusterPort);
                options.setClusterPublicHost(clusterPublicHost);
                options.setClusterPublicPort(clusterPublicPort);
                super.beforeStartingVertx(options);
            }
        }

        @Override
        public void afterStartingVertx(Vertx vertx) {
            afterStartingVertxInvoked = true;
            LauncherTest.this.vertx = vertx;
        }

        @Override
        public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
            beforeDeployingVerticle = true;
            this.deploymentOptions = deploymentOptions;
        }

        public void assertHooksInvoked() {
            assertTrue(afterConfigParsed);
            assertTrue(beforeStartingVertxInvoked);
            assertTrue(afterStartingVertxInvoked);
            assertTrue(beforeDeployingVerticle);
            assertNotNull(vertx);
        }
    }

    class MySecondLauncher extends LauncherTest.MyLauncher {
        @Override
        public String getMainVerticle() {
            return "java:io.vertx.test.verticles.TestVerticle";
        }
    }
}

