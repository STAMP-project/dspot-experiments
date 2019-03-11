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
package io.vertx.core.impl.launcher.commands;


import io.vertx.core.Launcher;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.BooleanSupplier;
import org.junit.Test;


/**
 * Check the run command behavior.
 */
public class RunCommandTest extends CommandTestBase {
    private File manifest = new File("target/test-classes/META-INF/MANIFEST.MF");

    @Test
    public void testDeploymentOfJavaVerticle() {
        cli.dispatch(new Launcher(), new String[]{ "run", HttpTestVerticle.class.getName() });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    public void testDeploymentOfJavaVerticleWithCluster() throws IOException {
        cli.dispatch(new Launcher(), new String[]{ "run", HttpTestVerticle.class.getName(), "-cluster" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getBoolean("clustered")).isTrue();
    }

    @Test
    public void testFatJarWithoutMainVerticle() throws IOException {
        setManifest("MANIFEST-Launcher-No-Main-Verticle.MF");
        record();
        cli.dispatch(new Launcher(), new String[0]);
        stop();
        assertThat(output.toString()).contains("Usage:");
    }

    @Test
    public void testFatJarWithMissingMainVerticle() throws IOException, InterruptedException {
        setManifest("MANIFEST-Launcher-Missing-Main-Verticle.MF");
        record();
        cli.dispatch(new Launcher(), new String[]{  });
        assertWaitUntil(() -> error.toString().contains("ClassNotFoundException"));
        stop();
    }

    @Test
    public void testFatJarWithHTTPVerticle() throws IOException, InterruptedException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{  });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getBoolean("clustered")).isFalse();
    }

    @Test
    public void testFatJarWithHTTPVerticleWithCluster() throws IOException, InterruptedException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ "-cluster" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getBoolean("clustered")).isTrue();
    }

    @Test
    public void testThatHADeploysVerticleWhenCombinedWithCluster() throws IOException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ "-ha", "-cluster" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getBoolean("clustered")).isTrue();
    }

    @Test
    public void testThatHADeploysVerticle() throws IOException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ "-ha", "-cluster" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getBoolean("clustered")).isTrue();
    }

    @Test
    public void testWithConfProvidedInline() throws IOException {
        long someNumber = new Random().nextLong();
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ ("--conf={\"random\":" + someNumber) + "}" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getJsonObject("conf").getLong("random")).isEqualTo(someNumber);
    }

    @Test
    public void testWithBrokenConfProvidedInline() throws IOException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        // There is a missing `}` in the json fragment. This is normal, as the test check that the configuration is not
        // read in this case.
        cli.dispatch(new Launcher(), new String[]{ "--conf={\"name\":\"vertx\"" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getJsonObject("conf").toString()).isEqualToIgnoringCase("{}");
    }

    @Test
    public void testWithConfProvidedAsFile() throws IOException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ "--conf", "target/test-classes/conf.json" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        assertThat(RunCommandTest.getContent().getJsonObject("conf").getString("name")).isEqualToIgnoringCase("vertx");
    }

    @Test
    public void testMetricsEnabledFromCommandLine() throws IOException {
        setManifest("MANIFEST-Launcher-Http-Verticle.MF");
        cli.dispatch(new Launcher(), new String[]{ "-Dvertx.metrics.options.enabled=true" });
        assertWaitUntil(() -> {
            try {
                return (RunCommandTest.getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
        // Check that the metrics are enabled
        // We cannot use the response from the verticle as it uses the DymmyVertxMetrics (no metrics provider)
        assertThat(((RunCommand) (cli.getExistingCommandInstance("run"))).options.getMetricsOptions().isEnabled()).isTrue();
    }
}

