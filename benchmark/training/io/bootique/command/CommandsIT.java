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


import io.bootique.BQModuleProvider;
import io.bootique.BQRuntime;
import io.bootique.cli.Cli;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.unit.BQInternalTestFactory;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class CommandsIT {
    @Rule
    public BQInternalTestFactory testFactory = new BQInternalTestFactory();

    private String[] args;

    @Test
    public void testModuleCommands() {
        BQModuleProvider provider = Commands.builder().build();
        BQRuntime runtime = testFactory.app(args).module(provider).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Map<String, ManagedCommand> commands = commandManager.getAllCommands();
        CommandsIT.assertCommandKeys(commands, "help", "help-config");
        Assert.assertFalse(commands.get("help").isHidden());
        Assert.assertFalse(commands.get("help").isDefault());
        Assert.assertTrue(commands.get("help").isHelp());
        Assert.assertFalse(commands.get("help-config").isHidden());
        Assert.assertFalse(commands.get("help-config").isDefault());
    }

    @Test
    public void testNoModuleCommands() {
        BQModuleProvider provider = Commands.builder().noModuleCommands().build();
        BQRuntime runtime = testFactory.app(args).module(provider).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Map<String, ManagedCommand> commands = commandManager.getAllCommands();
        CommandsIT.assertCommandKeys(commands, "help", "help-config");
        Assert.assertTrue(commands.get("help").isHidden());
        Assert.assertFalse(commands.get("help").isDefault());
        Assert.assertTrue(commands.get("help").isHelp());
        Assert.assertTrue(commands.get("help-config").isHidden());
        Assert.assertFalse(commands.get("help-config").isDefault());
    }

    @Test
    public void testModule_ExtraCommandAsType() {
        BQModuleProvider provider = Commands.builder(CommandsIT.C1.class).build();
        BQRuntime runtime = testFactory.app(args).module(provider).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Map<String, ManagedCommand> commands = commandManager.getAllCommands();
        CommandsIT.assertCommandKeys(commands, "c1", "help", "help-config");
        Assert.assertFalse(commands.get("help").isHidden());
        Assert.assertFalse(commands.get("help").isDefault());
        Assert.assertTrue(commands.get("help").isHelp());
        Assert.assertFalse(commands.get("help-config").isHidden());
        Assert.assertFalse(commands.get("help-config").isDefault());
        Assert.assertTrue(commands.containsKey("c1"));
        Assert.assertFalse(commands.get("c1").isDefault());
    }

    @Test
    public void testModule_ExtraCommandAsInstance() {
        BQModuleProvider provider = Commands.builder().add(new CommandsIT.C1()).build();
        BQRuntime runtime = testFactory.app(args).module(provider).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Map<String, ManagedCommand> commands = commandManager.getAllCommands();
        CommandsIT.assertCommandKeys(commands, "c1", "help", "help-config");
    }

    @Test
    public void testModule_ExtraCommandOverride() {
        BQModuleProvider provider = Commands.builder().add(CommandsIT.C2_Help.class).build();
        BQRuntime runtime = testFactory.app(args).module(provider).createRuntime();
        CommandManager commandManager = runtime.getInstance(CommandManager.class);
        Map<String, ManagedCommand> commands = commandManager.getAllCommands();
        CommandsIT.assertCommandKeys(commands, "help", "help-config");
    }

    static class C1 implements Command {
        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static class C2_Help implements Command {
        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }

        @Override
        public CommandMetadata getMetadata() {
            return CommandMetadata.builder("help").build();
        }
    }
}

