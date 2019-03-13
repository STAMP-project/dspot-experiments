/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.command;


import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.cli.Cli;
import io.bootique.help.HelpCommand;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.unit.BQInternalTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


public class CommandManagerIT {
    @Rule
    public BQInternalTestFactory runtimeFactory = new BQInternalTestFactory();

    @Test
    public void testHelpAndModuleCommands() {
        BQRuntime runtime = runtimeFactory.app().modules(CommandManagerIT.M0.class, CommandManagerIT.M1.class).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Assert.assertEquals("help, helpconfig and module commands must be present", 4, commandManager.getAllCommands().size());
        Assert.assertSame(CommandManagerIT.M0.mockCommand, commandManager.lookupByName("m0command").getCommand());
        Assert.assertSame(CommandManagerIT.M1.mockCommand, commandManager.lookupByName("m1command").getCommand());
        Assert.assertFalse(commandManager.getPublicDefaultCommand().isPresent());
        Assert.assertSame(runtime.getInstance(HelpCommand.class), commandManager.getPublicHelpCommand().get());
    }

    @Test
    public void testDefaultAndHelpAndModuleCommands() {
        Command defaultCommand = ( cli) -> CommandOutcome.succeeded();
        Module defaultCommandModule = ( binder) -> BQCoreModule.extend(binder).setDefaultCommand(defaultCommand);
        BQRuntime runtime = runtimeFactory.app().modules(CommandManagerIT.M0.class, CommandManagerIT.M1.class).module(defaultCommandModule).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Assert.assertEquals(5, commandManager.getAllCommands().size());
        Assert.assertSame(CommandManagerIT.M0.mockCommand, commandManager.lookupByName("m0command").getCommand());
        Assert.assertSame(CommandManagerIT.M1.mockCommand, commandManager.lookupByName("m1command").getCommand());
        Assert.assertSame(defaultCommand, commandManager.getPublicDefaultCommand().get());
        Assert.assertSame(runtime.getInstance(HelpCommand.class), commandManager.getPublicHelpCommand().get());
    }

    @Test
    public void testHiddenCommands() {
        Command hiddenCommand = new Command() {
            @Override
            public CommandOutcome run(Cli cli) {
                return CommandOutcome.succeeded();
            }

            @Override
            public CommandMetadata getMetadata() {
                return CommandMetadata.builder("xyz").hidden().build();
            }
        };
        BQRuntime runtime = runtimeFactory.app().module(( binder) -> BQCoreModule.extend(binder).addCommand(hiddenCommand)).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Assert.assertEquals(3, commandManager.getAllCommands().size());
        ManagedCommand hiddenManaged = commandManager.getAllCommands().get("xyz");
        Assert.assertSame(hiddenCommand, hiddenManaged.getCommand());
        Assert.assertTrue(hiddenManaged.isHidden());
    }

    @Test
    public void testDefaultCommandWithMetadata() {
        Command defaultCommand = new Command() {
            @Override
            public CommandOutcome run(Cli cli) {
                return CommandOutcome.succeeded();
            }

            @Override
            public CommandMetadata getMetadata() {
                // note how this name intentionally matches one of the existing commands
                return CommandMetadata.builder("m0command").build();
            }
        };
        Module defaultCommandModule = ( binder) -> BQCoreModule.extend(binder).setDefaultCommand(defaultCommand);
        BQRuntime runtime = runtimeFactory.app().modules(CommandManagerIT.M0.class, CommandManagerIT.M1.class).module(defaultCommandModule).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        // the main assertion we care about...
        Assert.assertSame("Default command did not override another command with same name", defaultCommand, commandManager.lookupByName("m0command").getCommand());
        // sanity check
        Assert.assertEquals(4, commandManager.getAllCommands().size());
        Assert.assertSame(CommandManagerIT.M1.mockCommand, commandManager.lookupByName("m1command").getCommand());
        Assert.assertSame(runtime.getInstance(HelpCommand.class), commandManager.lookupByName("help").getCommand());
        Assert.assertSame(defaultCommand, commandManager.getPublicDefaultCommand().get());
    }

    public static class M0 implements Module {
        static final Command mockCommand;

        static {
            mockCommand = Mockito.mock(Command.class);
            Mockito.when(CommandManagerIT.M0.mockCommand.getMetadata()).thenReturn(CommandMetadata.builder("m0command").build());
        }

        @Override
        public void configure(Binder binder) {
            BQCoreModule.extend(binder).addCommand(CommandManagerIT.M0.mockCommand);
        }
    }

    public static class M1 implements Module {
        static final Command mockCommand;

        static {
            mockCommand = Mockito.mock(Command.class);
            Mockito.when(CommandManagerIT.M1.mockCommand.getMetadata()).thenReturn(CommandMetadata.builder("m1command").build());
        }

        @Override
        public void configure(Binder binder) {
            BQCoreModule.extend(binder).addCommand(CommandManagerIT.M1.mockCommand);
        }
    }
}

