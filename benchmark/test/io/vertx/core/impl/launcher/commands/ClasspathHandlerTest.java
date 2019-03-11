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


import io.vertx.core.DeploymentOptions;
import java.io.File;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import org.junit.Test;


/**
 * Check the behavior of the {@link ClasspathHandler} class.
 */
public class ClasspathHandlerTest extends CommandTestBase {
    public static final String VERTICLE = "io.vertx.core.externals.MyVerticle";

    RunCommand run;

    private BareCommand bare;

    @Test
    public void testCPInRunCommand() {
        run = new RunCommand();
        run.setExecutionContext(new io.vertx.core.spi.launcher.ExecutionContext(run, null, null));
        run.setClasspath((("." + (File.pathSeparator)) + "target/externals"));
        run.setMainVerticle(ClasspathHandlerTest.VERTICLE);
        run.setInstances(1);
        run.run();
        assertWaitUntil(() -> {
            try {
                return (getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    public void testCPInBareCommand() {
        bare = new BareCommand();
        bare.setExecutionContext(new io.vertx.core.spi.launcher.ExecutionContext(bare, null, null));
        bare.setClasspath((("." + (File.pathSeparator)) + "target/externals"));
        bare.setQuorum(1);
        bare.run();
        assertWaitUntil(() -> (bare.vertx) != null);
        // Do reproduce the verticle fail-over, set the TCCL
        final ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(bare.createClassloader());
            bare.vertx.deployVerticle(ClasspathHandlerTest.VERTICLE, new DeploymentOptions().setHa(true));
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassloader);
        }
        assertWaitUntil(() -> {
            try {
                return (getHttpCode()) == 200;
            } catch (IOException e) {
                return false;
            }
        });
    }
}

