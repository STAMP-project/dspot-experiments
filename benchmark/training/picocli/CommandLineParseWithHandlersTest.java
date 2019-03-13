/**
 * Copyright 2017 Remko Popma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package picocli;


import Help.Ansi;
import Help.Ansi.OFF;
import Help.Ansi.ON;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import picocli.CommandLine.Model.CommandSpec;


public class CommandLineParseWithHandlersTest {
    @Rule
    public final ProvideSystemProperty ansiOFF = new ProvideSystemProperty("picocli.ansi", "false");

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    interface Factory {
        Object create();
    }

    @Test
    public void testParseWithHandlerRunXxxFailsIfNotRunnableOrCallable() {
        @Command
        class App {
            @Parameters
            String[] params;
        }
        CommandLineParseWithHandlersTest.Factory factory = new CommandLineParseWithHandlersTest.Factory() {
            public Object create() {
                return new App();
            }
        };
        String[] args = new String[]{ "abc" };
        verifyAllFail(factory, "Parsed command (picocli.CommandLineParseWithHandlersTest$", ") is not Method, Runnable or Callable", args);
    }

    @Test
    public void testParseWithHandlerRunXxxCatchesAndRethrowsExceptionFromRunnable() {
        @Command
        class App implements Runnable {
            @Parameters
            String[] params;

            public void run() {
                throw new IllegalStateException("TEST EXCEPTION");
            }
        }
        CommandLineParseWithHandlersTest.Factory factory = new CommandLineParseWithHandlersTest.Factory() {
            public Object create() {
                return new App();
            }
        };
        verifyAllFail(factory, "Error while running command (picocli.CommandLineParseWithHandlersTest$", "): java.lang.IllegalStateException: TEST EXCEPTION", new String[0]);
    }

    @Test
    public void testParseWithHandlerRunXxxCatchesAndRethrowsExceptionFromCallable() {
        @Command
        class App implements Callable<Object> {
            @Parameters
            String[] params;

            public Object call() {
                throw new IllegalStateException("TEST EXCEPTION2");
            }
        }
        CommandLineParseWithHandlersTest.Factory factory = new CommandLineParseWithHandlersTest.Factory() {
            public Object create() {
                return new App();
            }
        };
        verifyAllFail(factory, "Error while calling command (picocli.CommandLineParseWithHandlersTest$", "): java.lang.IllegalStateException: TEST EXCEPTION2", new String[0]);
    }

    @Test
    public void testParseWithHandlerRunXxxReturnsEmptyListIfHelpRequested() {
        @Command(version = "abc 1.3.4")
        class App implements Callable<Object> {
            @Option(names = "-h", usageHelp = true)
            boolean requestHelp;

            @Option(names = "-V", versionHelp = true)
            boolean requestVersion;

            public Object call() {
                return "RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App());
            }
        };
        verifyReturnValueForBuiltInHandlers(factory, Collections.emptyList(), new String[]{ "-h" });
        verifyReturnValueForBuiltInHandlers(factory, Collections.emptyList(), new String[]{ "-V" });
    }

    @Test
    public void testParseWithHandlerRunXxxReturnsCallableResult() {
        @Command
        class App implements Callable<Object> {
            public Object call() {
                return "RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App());
            }
        };
        verifyReturnValueForBuiltInHandlers(factory, Arrays.asList("RETURN VALUE"), new String[0]);
    }

    interface CommandLineFactory {
        CommandLine.CommandLine create();
    }

    @Test
    public void testParseWithHandler2RunXxxReturnsNullIfHelpRequested() {
        @Command(version = "abc 1.3.4")
        class App implements Callable<Object> {
            @Option(names = "-h", usageHelp = true)
            boolean requestHelp;

            @Option(names = "-V", versionHelp = true)
            boolean requestVersion;

            public Object call() {
                return "RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App());
            }
        };
        verifyReturnValueForBuiltInHandlers2(factory, null, new String[]{ "-h" });
        verifyReturnValueForBuiltInHandlers2(factory, null, new String[]{ "-V" });
    }

    @Test
    public void testParseWithHandle2rRunXxxReturnsCallableResult() {
        @Command
        class App implements Callable<Object> {
            public Object call() {
                return "RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App());
            }
        };
        verifyReturnValueForBuiltInHandlers2(factory, Arrays.asList("RETURN VALUE"), new String[0]);
    }

    @Test
    public void testParseWithHandlerRunXxxReturnsCallableResultWithSubcommand() {
        @Command
        class App implements Callable<Object> {
            public Object call() {
                return "RETURN VALUE";
            }
        }
        @Command(name = "sub")
        class Sub implements Callable<Object> {
            public Object call() {
                return "SUB RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App()).addSubcommand("sub", new Sub());
            }
        };
        Object actual1 = factory.create().parseWithHandler(new RunFirst(), new String[]{ "sub" });
        Assert.assertEquals("RunFirst: return value", Arrays.asList("RETURN VALUE"), actual1);
        Object actual2 = factory.create().parseWithHandler(new RunLast(), new String[]{ "sub" });
        Assert.assertEquals("RunLast: return value", Arrays.asList("SUB RETURN VALUE"), actual2);
        Object actual3 = factory.create().parseWithHandler(new RunAll(), new String[]{ "sub" });
        Assert.assertEquals("RunAll: return value", Arrays.asList("RETURN VALUE", "SUB RETURN VALUE"), actual3);
    }

    @Test
    public void testParseWithHandler2RunXxxReturnsCallableResultWithSubcommand() {
        @Command
        class App implements Callable<Object> {
            public Object call() {
                return "RETURN VALUE";
            }
        }
        @Command(name = "sub")
        class Sub implements Callable<Object> {
            public Object call() {
                return "SUB RETURN VALUE";
            }
        }
        CommandLineParseWithHandlersTest.CommandLineFactory factory = new CommandLineParseWithHandlersTest.CommandLineFactory() {
            public CommandLine.CommandLine create() {
                return new CommandLine.CommandLine(new App()).addSubcommand("sub", new Sub());
            }
        };
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        Object actual1 = factory.create().parseWithHandler(new RunFirst(), new String[]{ "sub" });
        Assert.assertEquals("RunFirst: return value", Arrays.asList("RETURN VALUE"), actual1);
        Object actual2 = factory.create().parseWithHandler(new RunLast(), new String[]{ "sub" });
        Assert.assertEquals("RunLast: return value", Arrays.asList("SUB RETURN VALUE"), actual2);
        Object actual3 = factory.create().parseWithHandler(new RunAll(), new String[]{ "sub" });
        Assert.assertEquals("RunAll: return value", Arrays.asList("RETURN VALUE", "SUB RETURN VALUE"), actual3);
    }

    @Test
    public void testRunCallsRunnableIfParseSucceeds() {
        final boolean[] runWasCalled = new boolean[]{ false };
        @Command
        class App implements Runnable {
            public void run() {
                runWasCalled[0] = true;
            }
        }
        CommandLine.CommandLine.run(new App(), System.err);
        Assert.assertTrue(runWasCalled[0]);
    }

    @Test
    public void testRunPrintsErrorIfParseFails() throws UnsupportedEncodingException {
        final boolean[] runWasCalled = new boolean[]{ false };
        class App implements Runnable {
            @Option(names = "-number")
            int number;

            public void run() {
                runWasCalled[0] = true;
            }
        }
        PrintStream oldErr = System.err;
        StringPrintStream sps = new StringPrintStream();
        System.setErr(sps.stream());
        CommandLine.CommandLine.run(new App(), System.err, "-number", "not a number");
        System.setErr(oldErr);
        Assert.assertFalse(runWasCalled[0]);
        Assert.assertEquals(String.format(("Invalid value for option '-number': 'not a number' is not an int%n" + ("Usage: <main class> [-number=<number>]%n" + "      -number=<number>%n"))), sps.toString());
    }

    @Test(expected = InitializationException.class)
    public void testRunRequiresAnnotatedCommand() {
        class App implements Runnable {
            public void run() {
            }
        }
        CommandLine.CommandLine.run(new App(), System.err);
    }

    @Test
    public void testCallReturnsCallableResultParseSucceeds() throws Exception {
        @Command
        class App implements Callable<Boolean> {
            public Boolean call() {
                return true;
            }
        }
        Assert.assertTrue(CommandLine.CommandLine.call(new App(), System.err));
    }

    @Test
    public void testCallReturnsNullAndPrintsErrorIfParseFails() throws Exception {
        class App implements Callable<Boolean> {
            @Option(names = "-number")
            int number;

            public Boolean call() {
                return true;
            }
        }
        PrintStream oldErr = System.err;
        StringPrintStream sps = new StringPrintStream();
        System.setErr(sps.stream());
        Boolean callResult = CommandLine.CommandLine.call(new App(), System.err, "-number", "not a number");
        System.setErr(oldErr);
        Assert.assertNull(callResult);
        Assert.assertEquals(String.format(("Invalid value for option '-number': 'not a number' is not an int%n" + ("Usage: <main class> [-number=<number>]%n" + "      -number=<number>%n"))), sps.toString());
    }

    @Test(expected = InitializationException.class)
    public void testCallRequiresAnnotatedCommand() throws Exception {
        class App implements Callable<Object> {
            public Object call() {
                return null;
            }
        }
        CommandLine.CommandLine.call(new App(), System.err);
    }

    @Test
    public void testExitCodeFromParseResultHandler() {
        @Command
        class App implements Runnable {
            public void run() {
            }
        }
        exit.expectSystemExitWithStatus(23);
        new CommandLine.CommandLine(new App()).parseWithHandler(new RunFirst().andExit(23), new String[]{  });
    }

    @Test
    public void testExitCodeFromParseResultHandler2() {
        @Command
        class App implements Runnable {
            public void run() {
            }
        }
        CommandLineParseWithHandlersTest.MyHandler handler = new CommandLineParseWithHandlersTest.MyHandler();
        new CommandLine.CommandLine(new App()).parseWithHandler(andExit(23), new String[]{  });
        Assert.assertEquals(23, handler.exitCode);
    }

    static class MyHandler extends RunLast {
        int exitCode;

        @Override
        protected void exit(int exitCode) {
            this.exitCode = exitCode;
        }
    }

    @Test
    public void testExitCodeFromExceptionHandler() {
        @Command
        class App implements Runnable {
            public void run() {
                throw new ParameterException(new CommandLine.CommandLine(this), "blah");
            }
        }
        exit.expectSystemExitWithStatus(25);
        new CommandLine.CommandLine(new App()).parseWithHandlers(new RunFirst().andExit(23), defaultExceptionHandler().andExit(25));
        Assert.assertEquals(String.format(("" + "blah%n"), "<main command>"), systemErrRule.getLog());
    }

    @Test
    public void testExitCodeFromExceptionHandler2() {
        @Command
        class App implements Runnable {
            public void run() {
                throw new ParameterException(new CommandLine.CommandLine(this), "blah");
            }
        }
        CommandLineParseWithHandlersTest.CustomExceptionHandler<List<Object>> handler = new CommandLineParseWithHandlersTest.CustomExceptionHandler<List<Object>>();
        new CommandLine.CommandLine(new App()).parseWithHandlers(new RunFirst().andExit(23), handler.andExit(25));
        Assert.assertEquals(String.format(("" + ("blah%n" + "Usage: <main class>%n"))), systemErrRule.getLog());
        Assert.assertEquals(25, handler.exitCode);
    }

    static class CustomExceptionHandler<R> extends DefaultExceptionHandler<R> {
        int exitCode;

        @Override
        protected void exit(int exitCode) {
            this.exitCode = exitCode;
        }
    }

    @Test
    public void testExitCodeFromExceptionHandler3() {
        @Command
        class App implements Runnable {
            public void run() {
                throw new ParameterException(new CommandLine.CommandLine(this), "blah");
            }
        }
        CommandLineParseWithHandlersTest.CustomNoThrowExceptionHandler<List<Object>> handler = new CommandLineParseWithHandlersTest.CustomNoThrowExceptionHandler<List<Object>>();
        new CommandLine.CommandLine(new App()).parseWithHandlers(new RunFirst().andExit(23), handler.andExit(25));
        Assert.assertEquals(String.format(("" + ("blah%n" + "Usage: <main class>%n"))), systemErrRule.getLog());
        Assert.assertEquals(25, handler.exitCode);
    }

    static class CustomNoThrowExceptionHandler<R> extends DefaultExceptionHandler<R> {
        int exitCode;

        @Override
        protected R throwOrExit(ExecutionException ex) {
            try {
                super.throwOrExit(ex);
            } catch (ExecutionException caught) {
            }
            return null;
        }

        @Override
        protected void exit(int exitCode) {
            this.exitCode = exitCode;
        }
    }

    @Test
    public void testSystemExitForOtherExceptions() {
        @Command
        class App implements Runnable {
            public void run() {
                throw new RuntimeException("blah");
            }
        }
        exit.expectSystemExitWithStatus(25);
        exit.checkAssertionAfterwards(new Assertion() {
            public void checkAssertion() {
                String actual = systemErrRule.getLog();
                Assert.assertTrue(actual.startsWith("picocli.CommandLine$ExecutionException: Error while running command (picocli.CommandLineParseWithHandlersTest"));
                Assert.assertTrue(actual.contains("java.lang.RuntimeException: blah"));
            }
        });
        new CommandLine.CommandLine(new App()).parseWithHandlers(new RunFirst().andExit(23), defaultExceptionHandler().andExit(25));
    }

    @Test(expected = InternalError.class)
    public void testNoSystemExitForErrors() {
        @Command
        class App implements Runnable {
            public void run() {
                throw new InternalError("blah");
            }
        }
        new CommandLine.CommandLine(new App()).parseWithHandlers(new RunFirst().andExit(23), defaultExceptionHandler().andExit(25));
    }

    @Command(name = "mycmd", mixinStandardHelpOptions = true, version = "MyCallable-1.0")
    static class MyCallable implements Callable<Object> {
        @Option(names = "-x", description = "this is an option")
        String option;

        public Object call() {
            throw new IllegalStateException("this is a test");
        }
    }

    @Command(name = "mycmd", mixinStandardHelpOptions = true, version = "MyRunnable-1.0")
    static class MyRunnable implements Runnable {
        @Option(names = "-x", description = "this is an option")
        String option;

        public void run() {
            throw new IllegalStateException("this is a test");
        }
    }

    private static final String MYCALLABLE_USAGE = String.format(("" + ((("Usage: mycmd [-hV] [-x=<option>]%n" + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n") + "  -x=<option>     this is an option%n")));

    private static final String INVALID_INPUT = String.format(("" + "Unmatched argument: invalid input%n"));

    private static final String MYCALLABLE_INVALID_INPUT = (CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE);

    private static final String MYCALLABLE_USAGE_ANSI = Ansi.ON.new Text(String.format(("" + ((("Usage: @|bold mycmd|@ [@|yellow -hV|@] [@|yellow -x|@=@|italic <option>|@]%n" + "  @|yellow -h|@, @|yellow --help|@      Show this help message and exit.%n") + "  @|yellow -V|@, @|yellow --version|@   Print version information and exit.%n") + "  @|yellow -x|@=@|italic <|@@|italic option>|@     this is an option%n")))).toString();

    @Test
    public void testCall1WithInvalidInput() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), "invalid input");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_INVALID_INPUT, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall2WithInvalidInput() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, "invalid input");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_INVALID_INPUT, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall3WithInvalidInput() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, ON, "invalid input");
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall4WithInvalidInput() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, System.err, ON, "invalid input");
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall4WithInvalidInput_ToStdout() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, System.out, ON, "invalid input");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemOutRule.getLog());
    }

    @Test
    public void testCall1DefaultExceptionHandlerRethrows() {
        try {
            CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), "-x abc");
        } catch (ExecutionException ex) {
            String cmd = ex.getCommandLine().getCommand().toString();
            String msg = ("Error while calling command (" + cmd) + "): java.lang.IllegalStateException: this is a test";
            Assert.assertEquals(msg, ex.getMessage());
        }
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall1WithHelpRequest() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE, systemOutRule.getLog());
    }

    @Test
    public void testCall2WithHelpRequest() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE, systemOutRule.getLog());
    }

    @Test
    public void testCall3WithHelpRequest() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, ON, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemOutRule.getLog());
    }

    @Test
    public void testCall3WithHelpRequest_ToStderr() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.err, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall3WithHelpRequest_ToCustomStream() {
        StringPrintStream sps = new StringPrintStream();
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), sps.stream(), ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, sps.toString());
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall4WithHelpRequest() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.out, System.err, ON, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemOutRule.getLog());
    }

    @Test
    public void testCall4WithHelpRequest_ToStderr() {
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), System.err, System.out, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCall4WithHelpRequest_ToCustomStream() {
        StringPrintStream sps = new StringPrintStream();
        CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.MyCallable(), sps.stream(), System.out, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, sps.toString());
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    // ---
    @Test
    public void testRun1WithInvalidInput() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), "invalid input");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_INVALID_INPUT, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun2WithInvalidInput() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, "invalid input");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_INVALID_INPUT, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun3WithInvalidInput() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, ON, "invalid input");
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun4WithInvalidInput() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, System.err, ON, "invalid input");
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun4WithInvalidInput_ToStdout() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, System.out, ON, "invalid input");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(((CommandLineParseWithHandlersTest.INVALID_INPUT) + (CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI)), systemOutRule.getLog());
    }

    @Test
    public void testRun1WithHelpRequest() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE, systemOutRule.getLog());
    }

    @Test
    public void testRun2WithHelpRequest() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE, systemOutRule.getLog());
    }

    @Test
    public void testRun3WithHelpRequest() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, ON, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemOutRule.getLog());
    }

    @Test
    public void testRun3WithHelpRequest_ToStderr() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.err, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun3WithHelpRequest_ToCustomStream() {
        StringPrintStream sps = new StringPrintStream();
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), sps.stream(), ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, sps.toString());
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun4WithHelpRequest() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.out, System.err, ON, "--help");
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemOutRule.getLog());
    }

    @Test
    public void testRun4WithHelpRequest_ToStderr() {
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), System.err, System.out, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testRun4WithHelpRequest_ToCustomStream() {
        StringPrintStream sps = new StringPrintStream();
        CommandLine.CommandLine.run(new CommandLineParseWithHandlersTest.MyRunnable(), sps.stream(), System.out, ON, "--help");
        Assert.assertEquals(CommandLineParseWithHandlersTest.MYCALLABLE_USAGE_ANSI, sps.toString());
        Assert.assertEquals("", systemErrRule.getLog());
        Assert.assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void testCallWithFactory() {
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, OFF, "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, System.out, OFF, "-x", "a");
            }
        } };
        for (Runnable r : variations) {
            try {
                r.run();
            } catch (ExecutionException ex) {
                Assert.assertTrue(ex.getMessage().startsWith("Error while calling command (picocli.CommandLineParseWithHandlersTest$MyCallable"));
                Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
                Assert.assertEquals("this is a test", ex.getCause().getMessage());
            }
        }
    }

    @Test
    public void testCallWithFactoryVersionHelp() {
        CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), "--version");
        Assert.assertEquals(String.format("MyCallable-1.0%n"), systemOutRule.getLog());
        Assert.assertEquals("", systemErrRule.getLog());
        systemOutRule.clearLog();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), ps, "--version");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), ps, OFF, "--version");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), ps, System.out, OFF, "--version");
            }
        } };
        for (Runnable r : variations) {
            Assert.assertEquals("", baos.toString());
            r.run();
            Assert.assertEquals(String.format("MyCallable-1.0%n"), baos.toString());
            baos.reset();
            Assert.assertEquals("", systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
        }
    }

    @Test
    public void testCallWithFactoryInvalidInput() {
        String expected = String.format(("" + (((("Missing required parameter for option '-x' (<option>)%n" + "Usage: mycmd [-hV] [-x=<option>]%n") + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n") + "  -x=<option>     this is an option%n")));
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), "-x");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, "-x");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, OFF, "-x");
            }
        } };
        for (Runnable r : variations) {
            Assert.assertEquals("", systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
            systemErrRule.clearLog();
            systemOutRule.clearLog();
            r.run();
            Assert.assertEquals(expected, systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
            systemErrRule.clearLog();
            systemOutRule.clearLog();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.MyCallable.class, new InnerClassFactory(this), System.out, ps, OFF, "-x");
        Assert.assertEquals(expected, baos.toString());
        Assert.assertEquals("", systemOutRule.getLog());
        Assert.assertEquals("", systemErrRule.getLog());
    }

    @Test
    public void testRunWithFactory() {
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, OFF, "-x", "a");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, System.out, OFF, "-x", "a");
            }
        } };
        for (Runnable r : variations) {
            try {
                r.run();
            } catch (ExecutionException ex) {
                Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Error while running command (picocli.CommandLineParseWithHandlersTest$MyRunnable"));
                Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
                Assert.assertEquals("this is a test", ex.getCause().getMessage());
            }
        }
    }

    @Test
    public void testRunWithFactoryVersionHelp() {
        CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), "--version");
        Assert.assertEquals(String.format("MyRunnable-1.0%n"), systemOutRule.getLog());
        Assert.assertEquals("", systemErrRule.getLog());
        systemOutRule.clearLog();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), ps, "--version");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), ps, OFF, "--version");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), ps, System.out, OFF, "--version");
            }
        } };
        for (Runnable r : variations) {
            Assert.assertEquals("", baos.toString());
            r.run();
            Assert.assertEquals(String.format("MyRunnable-1.0%n"), baos.toString());
            baos.reset();
            Assert.assertEquals("", systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
        }
    }

    @Test
    public void testRunWithFactoryInvalidInput() {
        String expected = String.format(("" + (((("Missing required parameter for option '-x' (<option>)%n" + "Usage: mycmd [-hV] [-x=<option>]%n") + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n") + "  -x=<option>     this is an option%n")));
        Runnable[] variations = new Runnable[]{ new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), "-x");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, "-x");
            }
        }, new Runnable() {
            public void run() {
                CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, OFF, "-x");
            }
        } };
        for (Runnable r : variations) {
            Assert.assertEquals("", systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
            systemErrRule.clearLog();
            systemOutRule.clearLog();
            r.run();
            Assert.assertEquals(expected, systemErrRule.getLog());
            Assert.assertEquals("", systemOutRule.getLog());
            systemErrRule.clearLog();
            systemOutRule.clearLog();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        CommandLine.CommandLine.run(CommandLineParseWithHandlersTest.MyRunnable.class, new InnerClassFactory(this), System.out, ps, OFF, "-x");
        Assert.assertEquals(expected, baos.toString());
        Assert.assertEquals("", systemOutRule.getLog());
        Assert.assertEquals("", systemErrRule.getLog());
    }

    @Test
    public void testExecutionExceptionIfRunnableThrowsExecutionException() {
        @Command
        class App implements Runnable {
            @Spec
            CommandSpec spec;

            public void run() {
                throw new ExecutionException(spec.commandLine(), "abc");
            }
        }
        try {
            CommandLine.CommandLine.run(new App());
        } catch (ExecutionException ex) {
            Assert.assertEquals("abc", ex.getMessage());
        }
    }

    @Test
    public void testExecutionExceptionIfCallableThrowsExecutionException() {
        @Command
        class App implements Callable<Void> {
            @Spec
            CommandSpec spec;

            public Void call() {
                throw new ExecutionException(spec.commandLine(), "abc");
            }
        }
        try {
            CommandLine.CommandLine.call(new App());
        } catch (ExecutionException ex) {
            Assert.assertEquals("abc", ex.getMessage());
        }
    }

    @Test
    public void testParameterExceptionIfCallableThrowsParameterException() {
        @Command
        class App implements Callable<Void> {
            @Spec
            CommandSpec spec;

            public Void call() {
                throw new ParameterException(spec.commandLine(), "xxx");
            }
        }
        try {
            CommandLine.CommandLine.call(new App());
        } catch (ParameterException ex) {
            Assert.assertEquals("xxx", ex.getMessage());
        }
    }

    @Test
    public void testRunAllSelf() {
        RunAll runAll = new RunAll();
        Assert.assertSame(runAll, runAll.self());
    }

    @Test
    public void testHandlerThrowsOtherException() {
        @Command
        class App {}
        try {
            new CommandLine.CommandLine(new App()).parseWithHandler(new IParseResultHandler2<Object>() {
                public Object handleParseResult(ParseResult parseResult) throws ExecutionException {
                    throw new IllegalArgumentException("abc");
                }
            }, new String[0]);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("abc", ex.getMessage());
        }
    }

    @Test
    public void testHandlerThrowsExecutionException() {
        @Command
        class App {}
        try {
            new CommandLine.CommandLine(new App()).parseWithHandler(new IParseResultHandler2<Object>() {
                public Object handleParseResult(ParseResult parseResult) throws ExecutionException {
                    throw new ExecutionException(new CommandLine.CommandLine(new App()), "xyz");
                }
            }, new String[0]);
        } catch (ExecutionException ex) {
            Assert.assertEquals("xyz", ex.getMessage());
        }
    }

    @Test
    public void testHandlerThrowsExecutionException2() {
        @Command
        class App {}
        IParseResultHandler2<Void> handler = new IParseResultHandler2<Void>() {
            public Void handleParseResult(ParseResult parseResult) throws ExecutionException {
                throw new ExecutionException(new CommandLine.CommandLine(new App()), "xyz");
            }
        };
        IExceptionHandler2<Void> exceptionHandler = new IExceptionHandler2<Void>() {
            public Void handleParseException(ParameterException ex, String[] args) {
                return null;
            }

            public Void handleExecutionException(ExecutionException ex, ParseResult parseResult) {
                return null;
            }
        };
        try {
            new CommandLine.CommandLine(new App()).parseWithHandlers(handler, exceptionHandler, new String[0]);
        } catch (ExecutionException ex) {
            Assert.assertEquals("xyz", ex.getMessage());
        }
    }

    @Command
    static class Executable implements Runnable , Callable<Void> {
        @Option(names = "-x")
        int x;

        public void run() {
        }

        public Void call() throws Exception {
            return null;
        }
    }

    @Test
    public void testCallNullResult() {
        Object result = CommandLine.CommandLine.call(new CommandLineParseWithHandlersTest.Executable(), "-x");
        Assert.assertNull(result);
    }

    @Test
    public void testCallableClassNullResult() {
        Object result = CommandLine.CommandLine.call(CommandLineParseWithHandlersTest.Executable.class, CommandLine.CommandLine.defaultFactory(), "-x");
        Assert.assertNull(result);
    }
}

