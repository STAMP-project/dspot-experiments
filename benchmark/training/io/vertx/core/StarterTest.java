/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.metrics.MetricsOptionsTest;
import io.vertx.test.core.AsyncTestBase;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.verticles.TestVerticle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static Starter.METRICS_OPTIONS_PROP_PREFIX;
import static Starter.VERTX_OPTIONS_PROP_PREFIX;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@SuppressWarnings("deprecation")
public class StarterTest extends VertxTestBase {
    Vertx vertx;

    @Test
    public void testVersion() throws Exception {
        String[] args = new String[]{ "-version" };
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        starter.run(args);
        assertEquals(System.getProperty("vertx.version"), getVersion());
        cleanup(starter);
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
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-cluster" };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        starter.assertHooksInvoked();
        cleanup(starter);
    }

    @Test
    public void testRunVerticleHA() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-ha" };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        starter.assertHooksInvoked();
        cleanup(starter);
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestNoArgs() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{  };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        cleanup(starter);
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestWithHA() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "-ha" };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        cleanup(starter);
    }

    @Test
    public void testRunVerticleWithMainVerticleInManifestWithArgs() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "-cluster", "-worker" };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(Arrays.asList(args), TestVerticle.processArgs);
        cleanup(starter);
    }

    @Test
    public void testRunVerticleWithConfString() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        JsonObject conf = new JsonObject().put("foo", "bar").put("wibble", 123);
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-conf", conf.encode() };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(conf, TestVerticle.conf);
        cleanup(starter);
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testRunVerticleWithConfFile() throws Exception {
        Path tempDir = testFolder.newFolder().toPath();
        Path tempFile = Files.createTempFile(tempDir, "conf", "json");
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        JsonObject conf = new JsonObject().put("foo", "bar").put("wibble", 123);
        Files.write(tempFile, conf.encode().getBytes());
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()), "-conf", tempFile.toString() };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        assertEquals(conf, TestVerticle.conf);
        cleanup(starter);
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
    public void testCustomMetricsOptions() throws Exception {
        System.setProperty(((METRICS_OPTIONS_PROP_PREFIX) + "enabled"), "true");
        System.setProperty(((METRICS_OPTIONS_PROP_PREFIX) + "customProperty"), "customPropertyValue");
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(MetricsOptionsTest.createMetricsFromMetaInfLoader("io.vertx.core.CustomMetricsFactory"));
        try {
            starter.run(args);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = starter.getVertxOptions();
        CustomMetricsOptions custom = ((CustomMetricsOptions) (opts.getMetricsOptions()));
        assertEquals("customPropertyValue", custom.getCustomProperty());
        cleanup(starter);
    }

    @Test
    public void testConfigureFromSystemPropertiesInvalidPropertyName() throws Exception {
        System.setProperty(((VERTX_OPTIONS_PROP_PREFIX) + "nosuchproperty"), "123");
        // Should be ignored
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = starter.getVertxOptions();
        VertxOptions def = new VertxOptions();
        if (opts.getMetricsOptions().isEnabled()) {
            def.getMetricsOptions().setEnabled(true);
        }
        assertEquals(def.toJson(), opts.toJson());
        cleanup(starter);
    }

    @Test
    public void testConfigureFromSystemPropertiesInvalidPropertyType() throws Exception {
        // One for each type that we support
        System.setProperty(((VERTX_OPTIONS_PROP_PREFIX) + "eventLoopPoolSize"), "sausages");
        // Should be ignored
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        String[] args = new String[]{ "run", "java:" + (TestVerticle.class.getCanonicalName()) };
        starter.run(args);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == 1);
        VertxOptions opts = starter.getVertxOptions();
        VertxOptions def = new VertxOptions();
        if (opts.getMetricsOptions().isEnabled()) {
            def.getMetricsOptions().setEnabled(true);
        }
        assertEquals(def.toJson(), opts.toJson());
        cleanup(starter);
    }

    @Test
    public void testRunWithCommandLine() throws Exception {
        StarterTest.MyStarter starter = new StarterTest.MyStarter();
        int instances = 10;
        String cl = (("run java:" + (TestVerticle.class.getCanonicalName())) + " -instances ") + instances;
        starter.run(cl);
        AsyncTestBase.assertWaitUntil(() -> (TestVerticle.instanceCount.get()) == instances);
        cleanup(starter);
    }

    class MyStarter extends Starter {
        boolean beforeStartingVertxInvoked = false;

        boolean afterStartingVertxInvoked = false;

        boolean beforeDeployingVerticle = false;

        public Vertx getVertx() {
            return vertx;
        }

        public VertxOptions getVertxOptions() {
            return options;
        }

        public DeploymentOptions getDeploymentOptions() {
            return deploymentOptions;
        }

        @Override
        public void run(String[] sargs) {
            super.run(sargs);
        }

        @Override
        public void run(String commandLine) {
            super.run(commandLine);
        }

        @Override
        public void beforeStartingVertx(VertxOptions options) {
            beforeStartingVertxInvoked = true;
        }

        @Override
        public void afterStartingVertx() {
            afterStartingVertxInvoked = true;
        }

        @Override
        protected void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
            beforeDeployingVerticle = true;
        }

        public void assertHooksInvoked() {
            StarterTest.this.vertx = vertx;
            assertTrue(beforeStartingVertxInvoked);
            assertTrue(afterStartingVertxInvoked);
            assertTrue(beforeDeployingVerticle);
        }
    }
}

