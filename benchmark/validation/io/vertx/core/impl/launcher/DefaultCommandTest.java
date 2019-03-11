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
package io.vertx.core.impl.launcher;


import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.annotations.CLIConfigurator;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.impl.launcher.commands.HelloCommand;
import io.vertx.core.spi.launcher.DefaultCommand;
import java.io.File;
import org.junit.Test;


public class DefaultCommandTest {
    private HelloCommand command = new HelloCommand();

    @Test
    public void testCWD() throws CLIException {
        CLI cli = CLIConfigurator.define(command.getClass());
        CommandLine evaluatedCLI = parse(cli, "--name=vert.x");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(getCwd()).isEqualTo(new File("."));
        evaluatedCLI = parse(cli, "--cwd=target", "--name=vert.x");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(getCwd()).isEqualTo(new File("target"));
    }

    @Test
    public void testSystemProperties() throws CLIException {
        CLI cli = CLIConfigurator.define(command.getClass());
        VertxCommandLauncher launcher = new VertxCommandLauncher();
        CommandLine evaluatedCLI = parse(cli, "--name=vert.x", "-Dfoo=bar", "--systemProp=x=y");
        CLIConfigurator.inject(evaluatedCLI, command);
        setUp(new io.vertx.core.spi.launcher.ExecutionContext(command, launcher, evaluatedCLI));
        assertThat(System.getProperty("foo")).isEqualToIgnoringCase("bar");
        assertThat(System.getProperty("x")).isEqualToIgnoringCase("y");
        tearDown();
        // System properties are not removed by the tearDown.
        assertThat(System.getProperty("foo")).isEqualToIgnoringCase("bar");
        assertThat(System.getProperty("x")).isEqualToIgnoringCase("y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatCLINeedsAName() {
        CLIConfigurator.define(DefaultCommandTest.MyCommandWithoutName.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatCLINeedsANonEmptyName() {
        CLIConfigurator.define(DefaultCommandTest.MyCommandWithoutEmptyName.class);
    }

    public static class MyCommandWithoutName extends DefaultCommand {
        @Override
        public void run() throws CLIException {
        }
    }

    @Name("")
    public static class MyCommandWithoutEmptyName extends DefaultCommand {
        @Override
        public void run() throws CLIException {
        }
    }
}

