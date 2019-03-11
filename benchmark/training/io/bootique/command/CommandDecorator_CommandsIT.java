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
import io.bootique.unit.BQInternalTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class CommandDecorator_CommandsIT {
    @Rule
    public BQInternalTestFactory testFactory = new BQInternalTestFactory();

    @Test
    public void testAlsoRun_DecorateWithPrivate() {
        // use private "-s" command in decorator
        BQModuleProvider commandsOverride = Commands.builder().add(CommandDecorator_CommandsIT.MainCommand.class).noModuleCommands().build();
        CommandDecorator decorator = CommandDecorator.alsoRun("s");
        BQRuntime runtime = createRuntime(commandsOverride, decorator);
        CommandOutcome outcome = runtime.run();
        waitForAllToFinish();
        Assert.assertTrue(outcome.isSuccess());
        Assert.assertTrue(getCommand(runtime, CommandDecorator_CommandsIT.MainCommand.class).isExecuted());
        Assert.assertTrue(getCommand(runtime, CommandDecorator_CommandsIT.SuccessfulCommand.class).isExecuted());
    }

    @Test
    public void testBeforeRun_DecorateWithPrivate() {
        // use private "-s" command in decorator
        BQModuleProvider commandsOverride = Commands.builder().add(CommandDecorator_CommandsIT.MainCommand.class).noModuleCommands().build();
        CommandDecorator decorator = CommandDecorator.beforeRun("s");
        BQRuntime runtime = createRuntime(commandsOverride, decorator);
        CommandOutcome outcome = runtime.run();
        Assert.assertTrue(outcome.isSuccess());
        Assert.assertTrue(getCommand(runtime, CommandDecorator_CommandsIT.MainCommand.class).isExecuted());
        Assert.assertTrue(getCommand(runtime, CommandDecorator_CommandsIT.SuccessfulCommand.class).isExecuted());
    }

    private static class MainCommand extends CommandDecoratorIT.ExecutableOnceCommand {
        private static final String NAME = "a";

        MainCommand() {
            super(CommandDecorator_CommandsIT.MainCommand.NAME);
        }
    }

    private static class SuccessfulCommand extends CommandDecoratorIT.ExecutableOnceCommand {
        private static final String NAME = "s";

        SuccessfulCommand() {
            super(CommandDecorator_CommandsIT.SuccessfulCommand.NAME);
        }
    }
}

