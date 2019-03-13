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


import CommandMetadata.Builder;
import io.bootique.BQCoreModule;
import io.bootique.BQModuleProvider;
import io.bootique.cli.Cli;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.unit.BQInternalTestFactory;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CommandDecoratorIT {
    @Rule
    public BQInternalTestFactory testFactory = new BQInternalTestFactory();

    private CommandDecoratorIT.ThreadTester threadTester = new CommandDecoratorIT.ThreadTester();

    private CommandDecoratorIT.MainCommand mainCommand;

    private CommandDecoratorIT.SuccessfulCommand successfulCommand;

    private CommandDecoratorIT.FailingCommand failingCommand;

    @Test
    public void testAlsoRun_Instance() {
        Command cmd = Mockito.mock(Command.class);
        Mockito.when(cmd.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        new CommandDecoratorIT.AppRunner(CommandDecorator.alsoRun(cmd)).runAndWaitExpectingSuccess();
        Assert.assertTrue(mainCommand.isExecuted());
        Mockito.verify(cmd).run(ArgumentMatchers.any(Cli.class));
    }

    @Test
    public void testAlsoRun_NameRef() {
        CommandDecorator decorator = CommandDecorator.alsoRun("s");
        new CommandDecoratorIT.AppRunner(decorator).runAndWaitExpectingSuccess();
        Assert.assertTrue(successfulCommand.isExecuted());
        Assert.assertFalse(successfulCommand.hasFlagOption());
    }

    @Test
    public void testAlsoRun_NameRef_WithArgs() {
        CommandDecorator decorator = CommandDecorator.alsoRun("s", "--sflag");
        new CommandDecoratorIT.AppRunner(decorator).runAndWaitExpectingSuccess();
        Assert.assertTrue(successfulCommand.isExecuted());
        Assert.assertTrue(successfulCommand.hasFlagOption());
    }

    @Test
    public void testAlsoRun_TypeRef() {
        CommandDecorator decorator = CommandDecorator.alsoRun(CommandDecoratorIT.SuccessfulCommand.class);
        new CommandDecoratorIT.AppRunner(decorator).runAndWaitExpectingSuccess();
        Assert.assertTrue(successfulCommand.isExecuted());
        Assert.assertFalse(successfulCommand.hasFlagOption());
    }

    @Test
    public void testAlsoRun_TypeRef_WithArgs() {
        CommandDecorator decorator = CommandDecorator.alsoRun(CommandDecoratorIT.SuccessfulCommand.class, "--sflag");
        new CommandDecoratorIT.AppRunner(decorator).runAndWaitExpectingSuccess();
        Assert.assertTrue(successfulCommand.isExecuted());
        Assert.assertTrue(successfulCommand.hasFlagOption());
    }

    @Test
    public void testBeforeRun_Instance() {
        Command cmd = Mockito.mock(Command.class);
        Mockito.when(cmd.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        CommandDecorator decorator = CommandDecorator.beforeRun(cmd);
        new CommandDecoratorIT.AppRunner(decorator).runExpectingSuccess();
        Mockito.verify(cmd).run(ArgumentMatchers.any(Cli.class));
    }

    @Test
    public void testBeforeRun_Failure_NameRef() {
        CommandDecorator decorator = CommandDecorator.beforeRun("f");
        new CommandDecoratorIT.AppRunner(decorator).runExpectingFailure();
    }

    @Test
    public void testBeforeRun_Failure_NameRef_WithArgs() {
        CommandDecorator decorator = CommandDecorator.beforeRun("f", "--fflag");
        new CommandDecoratorIT.AppRunner(decorator).runExpectingFailure();
        Assert.assertTrue(failingCommand.hasFlagOption());
    }

    @Test
    public void testBeforeRun_Failure_TypeRef() {
        CommandDecorator decorator = CommandDecorator.beforeRun(CommandDecoratorIT.FailingCommand.class);
        new CommandDecoratorIT.AppRunner(decorator).runExpectingFailure();
    }

    @Test
    public void testBeforeRun_Failure_TypeRef_WithArgs() {
        CommandDecorator decorator = CommandDecorator.beforeRun(CommandDecoratorIT.FailingCommand.class, "--fflag");
        new CommandDecoratorIT.AppRunner(decorator).runExpectingFailure();
        Assert.assertTrue(failingCommand.hasFlagOption());
    }

    @Test
    public void testBeforeAndAlsoRun() {
        Command c1 = Mockito.mock(Command.class);
        Mockito.when(c1.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        Command c2 = Mockito.mock(Command.class);
        Mockito.when(c2.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        Command c3 = Mockito.mock(Command.class);
        Mockito.when(c3.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        Command c4 = Mockito.mock(Command.class);
        Mockito.when(c4.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        CommandDecorator decorator = CommandDecorator.builder().beforeRun(c1).beforeRun(c2).alsoRun(c3).alsoRun(c4).build();
        new CommandDecoratorIT.AppRunner(decorator).runAndWaitExpectingSuccess();
        Assert.assertTrue(mainCommand.isExecuted());
        Mockito.verify(c1).run(ArgumentMatchers.any(Cli.class));
        Mockito.verify(c2).run(ArgumentMatchers.any(Cli.class));
        Mockito.verify(c3).run(ArgumentMatchers.any(Cli.class));
        Mockito.verify(c4).run(ArgumentMatchers.any(Cli.class));
    }

    @Test
    public void testMultipleDecoratorsForTheSameCommand() {
        Command c1 = Mockito.mock(Command.class);
        Mockito.when(c1.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        Command c2 = Mockito.mock(Command.class);
        Mockito.when(c2.run(ArgumentMatchers.any())).thenReturn(CommandOutcome.succeeded());
        testFactory.app("--a").module(( b) -> BQCoreModule.extend(b).addCommand(mainCommand).decorateCommand(mainCommand.getClass(), CommandDecorator.beforeRun(c1)).decorateCommand(mainCommand.getClass(), CommandDecorator.beforeRun(c2))).createRuntime().run();
        Mockito.verify(c1).run(ArgumentMatchers.any(Cli.class));
        Mockito.verify(c2).run(ArgumentMatchers.any(Cli.class));
        Assert.assertTrue(mainCommand.isExecuted());
    }

    private static class MainCommand extends CommandDecoratorIT.ExecutableOnceCommand {
        private static final String NAME = "a";

        MainCommand() {
            super(CommandDecoratorIT.MainCommand.NAME);
        }
    }

    private static class SuccessfulCommand extends CommandDecoratorIT.ExecutableOnceCommand {
        private static final String NAME = "s";

        private static final String FLAG_OPT = "sflag";

        SuccessfulCommand() {
            super(CommandDecoratorIT.SuccessfulCommand.NAME, CommandDecoratorIT.SuccessfulCommand.FLAG_OPT);
        }

        public boolean hasFlagOption() {
            return cliRef.get().hasOption(CommandDecoratorIT.SuccessfulCommand.FLAG_OPT);
        }
    }

    private static class FailingCommand extends CommandDecoratorIT.ExecutableOnceCommand {
        private static final String NAME = "f";

        private static final String FLAG_OPT = "fflag";

        FailingCommand() {
            super(CommandDecoratorIT.FailingCommand.NAME, CommandDecoratorIT.FailingCommand.FLAG_OPT);
        }

        public boolean hasFlagOption() {
            return cliRef.get().hasOption(CommandDecoratorIT.FailingCommand.FLAG_OPT);
        }

        @Override
        public CommandOutcome run(Cli cli) {
            super.run(cli);
            return CommandOutcome.failed(1, CommandDecoratorIT.FailingCommand.NAME);
        }
    }

    abstract static class ExecutableOnceCommand extends CommandWithMetadata {
        protected final AtomicReference<Cli> cliRef;

        public ExecutableOnceCommand(String commandName) {
            this(commandName, Optional.empty());
        }

        public ExecutableOnceCommand(String commandName, String flagOption) {
            this(commandName, Optional.of(flagOption));
        }

        private ExecutableOnceCommand(String commandName, Optional<String> flagOption) {
            super(CommandDecoratorIT.ExecutableOnceCommand.buildMetadata(commandName, flagOption));
            this.cliRef = new AtomicReference();
        }

        private static CommandMetadata buildMetadata(String commandName, Optional<String> flagOption) {
            CommandMetadata.Builder builder = CommandMetadata.builder(commandName);
            flagOption.ifPresent(( o) -> builder.addOption(OptionMetadata.builder(o)));
            return builder.build();
        }

        @Override
        public CommandOutcome run(Cli cli) {
            if (!(cliRef.compareAndSet(null, cli))) {
                throw new IllegalStateException("Already executed");
            }
            return CommandOutcome.succeeded();
        }

        public boolean isExecuted() {
            return (cliRef.get()) != null;
        }
    }

    private static class ThreadTester {
        public void assertPoolSize(int expected) {
            long matched = allThreads().filter(this::isPoolThread).count();
            if (expected < matched) {
                // let shutdown finish...
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // ignore....
                }
            }
            matched = allThreads().filter(this::isPoolThread).count();
            Assert.assertEquals(expected, matched);
        }

        private boolean isPoolThread(Thread t) {
            // the name comes from HeartbeatFactory
            return t.getName().startsWith("bootique-command-");
        }

        private Stream<Thread> allThreads() {
            ThreadGroup tg = Thread.currentThread().getThreadGroup();
            while ((tg.getParent()) != null) {
                tg = tg.getParent();
            } 
            Thread[] active = new Thread[tg.activeCount()];
            tg.enumerate(active);
            return Arrays.stream(active);
        }
    }

    private class AppRunner {
        private CommandDecorator decorator;

        private BQModuleProvider moduleProvider;

        public AppRunner(CommandDecorator decorator) {
            this.decorator = decorator;
        }

        public CommandDecoratorIT.AppRunner module(BQModuleProvider moduleProvider) {
            this.moduleProvider = moduleProvider;
            return this;
        }

        public void runExpectingSuccess() {
            CommandOutcome outcome = run();
            Assert.assertTrue(outcome.getMessage(), outcome.isSuccess());
            Assert.assertTrue(mainCommand.isExecuted());
        }

        public void runAndWaitExpectingSuccess() {
            Assert.assertTrue(runAndWait().isSuccess());
            Assert.assertTrue(mainCommand.isExecuted());
        }

        public void runExpectingFailure() {
            Assert.assertFalse(run().isSuccess());
            Assert.assertFalse(mainCommand.isExecuted());
        }

        private CommandOutcome run() {
            BQInternalTestFactory.Builder builder = testFactory.app("--a").module(( b) -> BQCoreModule.extend(b).addCommand(io.bootique.command.mainCommand).addCommand(io.bootique.command.successfulCommand).addCommand(io.bootique.command.failingCommand).decorateCommand(io.bootique.command.mainCommand.getClass(), decorator));
            if ((moduleProvider) != null) {
                builder.module(moduleProvider);
            }
            return builder.createRuntime().run();
        }

        private CommandOutcome runAndWait() {
            CommandOutcome outcome = run();
            // wait for the parallel commands to finish
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return outcome;
        }
    }
}

