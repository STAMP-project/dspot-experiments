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


import CommandLine.Help.Ansi.OFF;
import CommandLine.InitializationException;
import CommandLine.Range;
import Model.ArgSpec;
import Model.PositionalParamSpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.MethodParam;
import picocli.CommandLine.Model.TypedMember;


/**
 * Tests for {@code @Command} methods.
 */
public class CommandLineCommandMethodTest {
    @Rule
    public final ProvideSystemProperty ansiOFF = new ProvideSystemProperty("picocli.ansi", "false");

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    static class MethodAppBase {
        @CommandLine.Command(name = "run-0")
        public void run0() {
        }
    }

    @CommandLine.Command(name = "method")
    static class MethodApp extends CommandLineCommandMethodTest.MethodAppBase {
        @CommandLine.Command(name = "run-1")
        int run1(int a) {
            return a;
        }

        @CommandLine.Command(name = "run-2")
        int run2(int a, @CommandLine.Option(names = "-b", required = true)
        int b) {
            return a * b;
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAnnotateMethod_noArg() throws Exception {
        HelpTestUtil.setTraceLevel("OFF");
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.MethodApp.class, "run0").get(0);
        CommandLine cmd1 = new CommandLine(m);
        Assert.assertEquals("run-0", cmd1.getCommandName());
        Assert.assertEquals(Arrays.asList(), cmd1.getCommandSpec().args());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cmd1.parseWithHandler(((CommandLine.IParseResultHandler) (null)), new PrintStream(baos), new String[]{ "--y" });
        Assert.assertEquals(Arrays.asList("--y"), cmd1.getUnmatchedArguments());
        // test execute
        Object ret = CommandLine.invoke(m.getName(), CommandLineCommandMethodTest.MethodApp.class, new PrintStream(new ByteArrayOutputStream()));
        Assert.assertNull("return value", ret);
        HelpTestUtil.setTraceLevel("WARN");
    }

    @Test
    public void testAnnotateMethod_unannotatedPositional() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.MethodApp.class, "run1").get(0);
        // test required
        try {
            CommandLine.populateCommand(m);
            Assert.fail("Missing required field should have thrown exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <arg0>", ex.getMessage());
        }
        // test execute
        Object ret = CommandLine.invoke(m.getName(), CommandLineCommandMethodTest.MethodApp.class, new PrintStream(new ByteArrayOutputStream()), "42");
        Assert.assertEquals("return value", 42, ((Number) (ret)).intValue());
    }

    @CommandLine.Command
    static class UnannotatedPositional {
        @CommandLine.Command
        public void x(int a, int b, int c, int[] x, String[] y) {
        }
    }

    @Test
    public void testAnnotateMethod_unannotatedPositional_indexByParameterOrder() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedPositional.class, "x").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        List<CommandLine.Model.PositionalParamSpec> positionals = spec.positionalParameters();
        String[] labels = new String[]{ "<arg0>", "<arg1>", "<arg2>", "<arg3>", "<arg4>" };
        Assert.assertEquals(positionals.size(), labels.length);
        String[] ranges = new String[]{ "0", "1", "2", "3..*", "4..*" };
        for (int i = 0; i < (positionals.size()); i++) {
            CommandLine.Model.PositionalParamSpec positional = positionals.get(i);
            Assert.assertEquals((((positional.paramLabel()) + " at index ") + i), Range.valueOf(ranges[i]), positional.index());
            Assert.assertEquals(labels[i], positional.paramLabel());
        }
    }

    @CommandLine.Command
    static class PositionalsMixedWithOptions {
        @CommandLine.Command
        public void mixed(int a, @CommandLine.Option(names = "-b")
        int b, @CommandLine.Option(names = "-c")
        String c, int[] x, String[] y) {
        }
    }

    @Test
    public void testAnnotateMethod_unannotatedPositionalMixedWithOptions_indexByParameterOrder() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.PositionalsMixedWithOptions.class, "mixed").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        List<CommandLine.Model.PositionalParamSpec> positionals = spec.positionalParameters();
        String[] labels = new String[]{ "<arg0>", "<arg3>", "<arg4>" };
        Assert.assertEquals(positionals.size(), labels.length);
        String[] ranges = new String[]{ "0", "1..*", "2..*" };
        for (int i = 0; i < (positionals.size()); i++) {
            CommandLine.Model.PositionalParamSpec positional = positionals.get(i);
            Assert.assertEquals((((positional.paramLabel()) + " at index ") + i), Range.valueOf(ranges[i]), positional.index());
            Assert.assertEquals(labels[i], positional.paramLabel());
        }
        Assert.assertEquals(2, spec.options().size());
        Assert.assertEquals(int.class, spec.findOption("-b").type());
        Assert.assertEquals(String.class, spec.findOption("-c").type());
    }

    @CommandLine.Command
    static class SomeMixin {
        @CommandLine.Option(names = "-a")
        int a;

        @CommandLine.Option(names = "-b")
        long b;
    }

    static class UnannotatedClassWithMixinParameters {
        @CommandLine.Command
        void withMixin(@Mixin
        CommandLineCommandMethodTest.SomeMixin mixin) {
        }

        @CommandLine.Command
        void posAndMixin(int[] x, @Mixin
        CommandLineCommandMethodTest.SomeMixin mixin) {
        }

        @CommandLine.Command
        void posAndOptAndMixin(int[] x, @CommandLine.Option(names = "-y")
        String[] y, @Mixin
        CommandLineCommandMethodTest.SomeMixin mixin) {
        }

        @CommandLine.Command
        void mixinFirst(@Mixin
        CommandLineCommandMethodTest.SomeMixin mixin, int[] x, @CommandLine.Option(names = "-y")
        String[] y) {
        }
    }

    @Test
    public void testAnnotateMethod_mixinParameter() {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedClassWithMixinParameters.class, "withMixin").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        Assert.assertEquals(1, spec.mixins().size());
        spec = spec.mixins().get("arg0");
        Assert.assertEquals(CommandLineCommandMethodTest.SomeMixin.class, spec.userObject().getClass());
    }

    @Test
    public void testAnnotateMethod_positionalAndMixinParameter() {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedClassWithMixinParameters.class, "posAndMixin").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        Assert.assertEquals(1, spec.mixins().size());
        Assert.assertEquals(1, spec.positionalParameters().size());
        spec = spec.mixins().get("arg1");
        Assert.assertEquals(CommandLineCommandMethodTest.SomeMixin.class, spec.userObject().getClass());
    }

    @Test
    public void testAnnotateMethod_positionalAndOptionsAndMixinParameter() {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedClassWithMixinParameters.class, "posAndOptAndMixin").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        Assert.assertEquals(1, spec.mixins().size());
        Assert.assertEquals(1, spec.positionalParameters().size());
        Assert.assertEquals(3, spec.options().size());
        spec = spec.mixins().get("arg2");
        Assert.assertEquals(CommandLineCommandMethodTest.SomeMixin.class, spec.userObject().getClass());
    }

    @Test
    public void testAnnotateMethod_mixinParameterFirst() {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedClassWithMixinParameters.class, "mixinFirst").get(0);
        CommandLine cmd = new CommandLine(m);
        CommandSpec spec = cmd.getCommandSpec();
        Assert.assertEquals(1, spec.mixins().size());
        Assert.assertEquals(1, spec.positionalParameters().size());
        Assert.assertEquals(3, spec.options().size());
        spec = spec.mixins().get("arg0");
        Assert.assertEquals(CommandLineCommandMethodTest.SomeMixin.class, spec.userObject().getClass());
    }

    static class UnannotatedClassWithMixinAndOptionsAndPositionals {
        @CommandLine.Command(name = "sum")
        long sum(@CommandLine.Option(names = "-y")
        String[] y, @Mixin
        CommandLineCommandMethodTest.SomeMixin subMixin, int[] x) {
            return (((y.length) + (subMixin.a)) + (subMixin.b)) + (x.length);
        }
    }

    @Test
    public void testUnannotatedCommandWithMixin() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.UnannotatedClassWithMixinAndOptionsAndPositionals.class, "sum").get(0);
        CommandLine commandLine = new CommandLine(m);
        List<CommandLine> parsed = commandLine.parse("-y foo -y bar -a 7 -b 11 13 42".split(" "));
        Assert.assertEquals(1, parsed.size());
        // get method args
        Object[] methodArgValues = parsed.get(0).getCommandSpec().argValues();
        Assert.assertNotNull(methodArgValues);
        // verify args
        String[] arg0 = ((String[]) (methodArgValues[0]));
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, arg0);
        CommandLineCommandMethodTest.SomeMixin arg1 = ((CommandLineCommandMethodTest.SomeMixin) (methodArgValues[1]));
        Assert.assertEquals(7, arg1.a);
        Assert.assertEquals(11, arg1.b);
        int[] arg2 = ((int[]) (methodArgValues[2]));
        Assert.assertArrayEquals(new int[]{ 13, 42 }, arg2);
        // verify method is callable with args
        long result = ((Long) (m.invoke(new CommandLineCommandMethodTest.UnannotatedClassWithMixinAndOptionsAndPositionals(), methodArgValues)));
        Assert.assertEquals(22, result);
        // verify same result with result handler
        List<Object> results = new RunLast().handleParseResult(parsed, System.out, OFF);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(22L, results.get(0));
    }

    @CommandLine.Command
    static class AnnotatedClassWithMixinParameters {
        @Mixin
        CommandLineCommandMethodTest.SomeMixin mixin;

        @CommandLine.Command(name = "sum")
        long sum(@CommandLine.Option(names = "-y")
        String[] y, @Mixin
        CommandLineCommandMethodTest.SomeMixin subMixin, int[] x) {
            return (((((mixin.a) + (mixin.b)) + (y.length)) + (subMixin.a)) + (subMixin.b)) + (x.length);
        }
    }

    @Test
    public void testAnnotatedSubcommandWithDoubleMixin() throws Exception {
        CommandLineCommandMethodTest.AnnotatedClassWithMixinParameters command = new CommandLineCommandMethodTest.AnnotatedClassWithMixinParameters();
        CommandLine commandLine = new CommandLine(command);
        List<CommandLine> parsed = commandLine.parse("-a 3 -b 5 sum -y foo -y bar -a 7 -b 11 13 42".split(" "));
        Assert.assertEquals(2, parsed.size());
        // get method args
        Object[] methodArgValues = parsed.get(1).getCommandSpec().argValues();
        Assert.assertNotNull(methodArgValues);
        // verify args
        String[] arg0 = ((String[]) (methodArgValues[0]));
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, arg0);
        CommandLineCommandMethodTest.SomeMixin arg1 = ((CommandLineCommandMethodTest.SomeMixin) (methodArgValues[1]));
        Assert.assertEquals(7, arg1.a);
        Assert.assertEquals(11, arg1.b);
        int[] arg2 = ((int[]) (methodArgValues[2]));
        Assert.assertArrayEquals(new int[]{ 13, 42 }, arg2);
        // verify method is callable with args
        Method m = CommandLineCommandMethodTest.AnnotatedClassWithMixinParameters.class.getDeclaredMethod("sum", String[].class, CommandLineCommandMethodTest.SomeMixin.class, int[].class);
        long result = ((Long) (m.invoke(command, methodArgValues)));
        Assert.assertEquals(30, result);
        // verify same result with result handler
        List<Object> results = new RunLast().handleParseResult(parsed, System.out, OFF);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(30L, results.get(0));
    }

    @CommandLine.Command
    static class OtherMixin {
        @CommandLine.Option(names = "-c")
        int c;
    }

    static class AnnotatedClassWithMultipleMixinParameters {
        @CommandLine.Command(name = "sum")
        long sum(@Mixin
        CommandLineCommandMethodTest.SomeMixin mixin1, @Mixin
        CommandLineCommandMethodTest.OtherMixin mixin2) {
            return ((mixin1.a) + (mixin1.b)) + (mixin2.c);
        }
    }

    @Test
    public void testAnnotatedMethodMultipleMixinsSubcommandWithDoubleMixin() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.AnnotatedClassWithMultipleMixinParameters.class, "sum").get(0);
        CommandLine commandLine = new CommandLine(m);
        List<CommandLine> parsed = commandLine.parse("-a 3 -b 5 -c 7".split(" "));
        Assert.assertEquals(1, parsed.size());
        // get method args
        Object[] methodArgValues = parsed.get(0).getCommandSpec().argValues();
        Assert.assertNotNull(methodArgValues);
        // verify args
        CommandLineCommandMethodTest.SomeMixin arg0 = ((CommandLineCommandMethodTest.SomeMixin) (methodArgValues[0]));
        Assert.assertEquals(3, arg0.a);
        Assert.assertEquals(5, arg0.b);
        CommandLineCommandMethodTest.OtherMixin arg1 = ((CommandLineCommandMethodTest.OtherMixin) (methodArgValues[1]));
        Assert.assertEquals(7, arg1.c);
        // verify method is callable with args
        long result = ((Long) (m.invoke(new CommandLineCommandMethodTest.AnnotatedClassWithMultipleMixinParameters(), methodArgValues)));
        Assert.assertEquals(15, result);
        // verify same result with result handler
        List<Object> results = new RunLast().handleParseResult(parsed, System.out, OFF);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(15L, results.get(0));
    }

    @CommandLine.Command
    static class EmptyMixin {}

    static class AnnotatedClassWithMultipleEmptyParameters {
        @CommandLine.Command(name = "sum")
        long sum(@CommandLine.Option(names = "-a")
        int a, @Mixin
        CommandLineCommandMethodTest.EmptyMixin mixin) {
            return a;
        }
    }

    @Test
    public void testAnnotatedMethodMultipleMixinsSubcommandWithEmptyMixin() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.AnnotatedClassWithMultipleEmptyParameters.class, "sum").get(0);
        CommandLine commandLine = new CommandLine(m);
        List<CommandLine> parsed = commandLine.parse("-a 3".split(" "));
        Assert.assertEquals(1, parsed.size());
        // get method args
        Object[] methodArgValues = parsed.get(0).getCommandSpec().argValues();
        Assert.assertNotNull(methodArgValues);
        // verify args
        int arg0 = ((Integer) (methodArgValues[0]));
        Assert.assertEquals(3, arg0);
        CommandLineCommandMethodTest.EmptyMixin arg1 = ((CommandLineCommandMethodTest.EmptyMixin) (methodArgValues[1]));
        // verify method is callable with args
        long result = ((Long) (m.invoke(new CommandLineCommandMethodTest.AnnotatedClassWithMultipleEmptyParameters(), methodArgValues)));
        Assert.assertEquals(3, result);
        // verify same result with result handler
        List<Object> results = new RunLast().handleParseResult(parsed, System.out, OFF);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(3L, results.get(0));
    }

    @Test
    public void testAnnotateMethod_annotated() throws Exception {
        Method m = CommandLine.getCommandMethods(CommandLineCommandMethodTest.MethodApp.class, "run2").get(0);
        // test required
        try {
            CommandLine.populateCommand(m, "0");
            Assert.fail("Missing required option should have thrown exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '-b=<arg1>'", ex.getMessage());
        }
        // test execute
        Object ret = CommandLine.invoke(m.getName(), CommandLineCommandMethodTest.MethodApp.class, new PrintStream(new ByteArrayOutputStream()), "13", "-b", "-1");
        Assert.assertEquals("return value", (-13), ((Number) (ret)).intValue());
    }

    @Test
    public void testCommandMethodsFromSuperclassAddedToSubcommands() throws Exception {
        CommandLine cmd = new CommandLine(CommandLineCommandMethodTest.MethodApp.class);
        Assert.assertEquals("method", cmd.getCommandName());
        Assert.assertEquals(3, cmd.getSubcommands().size());
        Assert.assertEquals(0, cmd.getSubcommands().get("run-0").getCommandSpec().args().size());
        Assert.assertEquals(1, cmd.getSubcommands().get("run-1").getCommandSpec().args().size());
        Assert.assertEquals(2, cmd.getSubcommands().get("run-2").getCommandSpec().args().size());
        // CommandLine.usage(cmd.getSubcommands().get("run-2"), System.out);
    }

    /**
     *
     *
     * @see CompactFields
     */
    private static class CompactFieldsMethod {
        @CommandLine.Command
        public CommandLineTest.CompactFields run(@CommandLine.Option(names = "-v", paramLabel = "<verbose>"/* useless, but required for Assert.equals() */
        )
        boolean verbose, @CommandLine.Option(names = "-r", paramLabel = "<recursive>"/* useless, but required for Assert.equals() */
        )
        boolean recursive, @CommandLine.Option(names = "-o", paramLabel = "<outputFile>"/* required only for Assert.equals() */
        )
        File outputFile, @CommandLine.Parameters(paramLabel = "<inputFiles>"/* required only for Assert.equals() */
        )
        File[] inputFiles) {
            CommandLineTest.CompactFields ret = new CommandLineTest.CompactFields();
            ret.verbose = verbose;
            ret.recursive = recursive;
            ret.outputFile = outputFile;
            ret.inputFiles = inputFiles;
            return ret;
        }
    }

    @Test
    public void testAnnotateMethod_matchesAnnotatedClass() throws Exception {
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine classCmd = new CommandLine(new CommandLineTest.CompactFields());
        Method m = CommandLineCommandMethodTest.CompactFieldsMethod.class.getDeclaredMethod("run", new Class<?>[]{ boolean.class, boolean.class, File.class, File[].class });
        CommandLine methodCmd = new CommandLine(m);
        Assert.assertEquals("run", methodCmd.getCommandName());
        Assert.assertEquals("argument count", classCmd.getCommandSpec().args().size(), methodCmd.getCommandSpec().args().size());
        for (int i = 0; i < (classCmd.getCommandSpec().args().size()); i++) {
            CommandLine.Model.ArgSpec classArg = classCmd.getCommandSpec().args().get(i);
            CommandLine.Model.ArgSpec methodArg = methodCmd.getCommandSpec().args().get(i);
            Assert.assertEquals(("arg #" + i), classArg, methodArg);
        }
        HelpTestUtil.setTraceLevel("WARN");
    }

    /**
     * replicate {@link CommandLineTest#testCompactFieldsAnyOrder()} but using
     * {@link CompactFieldsMethod#run(boolean, boolean, File, File[])}
     * as source of the {@link Command} annotation.
     */
    @Test
    public void testCompactFieldsAnyOrder_method() throws Exception {
        final Method m = CommandLineCommandMethodTest.CompactFieldsMethod.class.getDeclaredMethod("run", new Class<?>[]{ boolean.class, boolean.class, File.class, File[].class });
        String[] tests = new String[]{ "-rvoout", "-vroout", "-vro=out", "-rv p1 p2", "p1 p2", "-voout p1 p2", "-voout -r p1 p2", "-r -v -oout p1 p2", "-rv -o out p1 p2", "-oout -r -v p1 p2", "-rvo out p1 p2" };
        for (String test : tests) {
            // parse
            CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), test.split(" "));
            List<CommandLine> result = new CommandLine(m).parse(test.split(" "));
            // extract arg values
            Assert.assertEquals(1, result.size());
            Object[] methodArgValues = result.get(0).getCommandSpec().argValues();
            Assert.assertNotNull(methodArgValues);
            // verify parsing had the same result
            CommandLineTest.verifyCompact(compact, ((Boolean) (methodArgValues[0])), ((Boolean) (methodArgValues[1])), ((methodArgValues[2]) == null ? null : String.valueOf(methodArgValues[2])), ((File[]) (methodArgValues[3])));
            // verify method is callable (args have the correct/assignable type)
            CommandLineTest.CompactFields methodCompact = ((CommandLineTest.CompactFields) (m.invoke(new CommandLineCommandMethodTest.CompactFieldsMethod(), methodArgValues)));// should not throw

            // verify passed args are the same
            Assert.assertNotNull(methodCompact);
            Assert.assertEquals(compact.verbose, methodCompact.verbose);
            Assert.assertEquals(compact.recursive, methodCompact.recursive);
            Assert.assertEquals(compact.outputFile, methodCompact.outputFile);
            Assert.assertArrayEquals(compact.inputFiles, methodCompact.inputFiles);
        }
        try {
            CommandLine.populateCommand(m, "-oout -r -vp1 p2".split(" "));
            Assert.fail("should fail: -v does not take an argument");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -p1 (while processing option: '-vp1')", ex.getMessage());
        }
    }

    static class CommandMethod1 {
        @CommandLine.Command(mixinStandardHelpOptions = true, version = "1.2.3")
        public int times(@CommandLine.Option(names = "-l", defaultValue = "2")
        int left, @CommandLine.Option(names = "-r", defaultValue = "3")
        int right) {
            return left * right;
        }
    }

    @Test
    public void testCommandMethodDefaults() {
        Object timesResultBothDefault = CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class);
        Assert.assertEquals("both default", 6, ((Integer) (timesResultBothDefault)).intValue());
        Object timesResultLeftDefault = CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class, "-r", "8");
        Assert.assertEquals("right default", 16, ((Integer) (timesResultLeftDefault)).intValue());
        Object timesResultRightDefault = CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class, "-l", "8");
        Assert.assertEquals("left default", 24, ((Integer) (timesResultRightDefault)).intValue());
        Object timesResultNoDefault = CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class, "-r", "4", "-l", "5");
        Assert.assertEquals("no default", 20, ((Integer) (timesResultNoDefault)).intValue());
    }

    @Test
    public void testCommandMethodMixinHelp() {
        CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class, "-h");
        String expected = String.format(("" + ((((("Usage: times [-hV] [-l=<arg0>] [-r=<arg1>]%n" + "  -h, --help      Show this help message and exit.%n") + "  -l=<arg0>%n") + "  -r=<arg1>%n") + "  -V, --version   Print version information and exit.%n") + "")));
        Assert.assertEquals(expected, systemOutRule.getLog());
    }

    @Test
    public void testCommandMethodMixinVersion() {
        CommandLine.invoke("times", CommandLineCommandMethodTest.CommandMethod1.class, "--version");
        String expected = String.format("1.2.3%n");
        Assert.assertEquals(expected, systemOutRule.getLog());
    }

    static class UnAnnotatedClassWithoutAnnotatedFields {
        @CommandLine.Command
        public void cmd1(@CommandLine.Option(names = "-x")
        int x, File f) {
        }

        @CommandLine.Command
        public void cmd2(@CommandLine.Option(names = "-x")
        int x, File f) {
        }
    }

    @Test
    public void testMethodCommandsAreNotSubcommandsOfNonAnnotatedClass() {
        try {
            new CommandLine(new CommandLineCommandMethodTest.UnAnnotatedClassWithoutAnnotatedFields());
            Assert.fail("expected exception");
        } catch (CommandLine ex) {
            Assert.assertEquals(("picocli.CommandLineCommandMethodTest$UnAnnotatedClassWithoutAnnotatedFields " + ("is not a command: it has no @Command, @Option, " + "@Parameters or @Unmatched annotations")), ex.getMessage());
        }
    }

    static class UnAnnotatedClassWithAnnotatedField {
        @CommandLine.Option(names = "-y")
        int y;

        @CommandLine.Command
        public void cmd1(@CommandLine.Option(names = "-x")
        int x, File f) {
        }

        @CommandLine.Command
        public void cmd2(@CommandLine.Option(names = "-x")
        int x, File f) {
        }
    }

    @Test
    public void testMethodCommandsAreNotSubcommandsOfNonAnnotatedClassWithAnnotatedFields() {
        CommandLine cmd = new CommandLine(new CommandLineCommandMethodTest.UnAnnotatedClassWithAnnotatedField());
        Assert.assertNotNull(cmd.getCommandSpec().findOption('y'));
        Assert.assertTrue(cmd.getSubcommands().isEmpty());
        Assert.assertNull(cmd.getCommandSpec().findOption('x'));
    }

    @CommandLine.Command
    static class AnnotatedClassWithoutAnnotatedFields {
        @CommandLine.Command
        public void cmd1(@CommandLine.Option(names = "-x")
        int x, File f) {
        }

        @CommandLine.Command
        public void cmd2(@CommandLine.Option(names = "-x")
        int x, File f) {
        }
    }

    @Test
    public void testMethodCommandsAreSubcommandsOfAnnotatedClass() {
        CommandLine cmd = new CommandLine(new CommandLineCommandMethodTest.AnnotatedClassWithoutAnnotatedFields());
        Assert.assertNull(cmd.getCommandSpec().findOption('x'));
        Assert.assertEquals(2, cmd.getSubcommands().size());
        Assert.assertEquals(CommandLineCommandMethodTest.set("cmd1", "cmd2"), cmd.getSubcommands().keySet());
        String expected = String.format(("" + ((("Usage: <main class> [COMMAND]%n" + "Commands:%n") + "  cmd1%n") + "  cmd2%n")));
        Assert.assertEquals(expected, cmd.getUsageMessage());
    }

    @CommandLine.Command(addMethodSubcommands = false)
    static class SwitchedOff {
        @CommandLine.Command
        public void cmd1(@CommandLine.Option(names = "-x")
        int x, File f) {
        }

        @CommandLine.Command
        public void cmd2(@CommandLine.Option(names = "-x")
        int x, File f) {
        }
    }

    @Test
    public void testMethodCommandsAreNotAddedAsSubcommandsIfAnnotationSaysSo() {
        CommandLine cmd = new CommandLine(new CommandLineCommandMethodTest.SwitchedOff());
        Assert.assertEquals(0, cmd.getSubcommands().size());
        String expected = String.format(("" + "Usage: <main class>%n"));
        Assert.assertEquals(expected, cmd.getUsageMessage());
    }

    /**
     * Exemple from the documentation.
     */
    static class Cat {
        public static void main(String[] args) {
            CommandLine.invoke("cat", CommandLineCommandMethodTest.Cat.class, args);
        }

        @CommandLine.Command(description = "Concatenate FILE(s) to standard output.", mixinStandardHelpOptions = true, version = "3.6.0")
        void cat(@CommandLine.Option(names = { "-E", "--show-ends" })
        boolean showEnds, @CommandLine.Option(names = { "-n", "--number" })
        boolean number, @CommandLine.Option(names = { "-T", "--show-tabs" })
        boolean showTabs, @CommandLine.Option(names = { "-v", "--show-nonprinting" })
        boolean showNonPrinting, @CommandLine.Parameters(paramLabel = "FILE")
        File[] files) {
            // process files
        }
    }

    @Test
    public void testCatUsageHelpMessage() {
        CommandLine cmd = new CommandLine(CommandLine.getCommandMethods(CommandLineCommandMethodTest.Cat.class, "cat").get(0));
        String expected = String.format(("" + (((((((("Usage: cat [-EhnTvV] [FILE...]%n" + "Concatenate FILE(s) to standard output.%n") + "      [FILE...]%n") + "  -E, --show-ends%n") + "  -h, --help               Show this help message and exit.%n") + "  -n, --number%n") + "  -T, --show-tabs%n") + "  -v, --show-nonprinting%n") + "  -V, --version            Print version information and exit.%n")));
        Assert.assertEquals(expected, cmd.getUsageMessage());
    }

    @CommandLine.Command(name = "git", mixinStandardHelpOptions = true, version = "picocli-3.6.0", description = "Version control system.")
    static class Git {
        @CommandLine.Option(names = "--git-dir", description = "Set the path to the repository")
        File path;

        @CommandLine.Command(description = "Clone a repository into a new directory")
        void clone(@CommandLine.Option(names = { "-l", "--local" })
        boolean local, @CommandLine.Option(names = "-q", description = "Operate quietly.")
        boolean quiet, @CommandLine.Option(names = "-v", description = "Run verbosely.")
        boolean verbose, @CommandLine.Option(names = { "-b", "--branch" })
        String branch, @CommandLine.Parameters(paramLabel = "<repository>")
        String repo) {
            // ... implement business logic
        }

        @CommandLine.Command(description = "Record changes to the repository")
        void commit(@CommandLine.Option(names = { "-m", "--message" })
        String commitMessage, @CommandLine.Option(names = "--squash", paramLabel = "<commit>")
        String squash, @CommandLine.Parameters(paramLabel = "<file>")
        File[] files) {
            // ... implement business logic
        }

        @CommandLine.Command(description = "Update remote refs along with associated objects")
        void push(@CommandLine.Option(names = { "-f", "--force" })
        boolean force, @CommandLine.Option(names = "--tags")
        boolean tags, @CommandLine.Parameters(paramLabel = "<repository>")
        String repo) {
            // ... implement business logic
        }
    }

    @Test
    public void testGitUsageHelpMessage() {
        CommandLine cmd = new CommandLine(new CommandLineCommandMethodTest.Git());
        String expected = String.format(("" + (((((((("Usage: git [-hV] [--git-dir=<path>] [COMMAND]%n" + "Version control system.%n") + "      --git-dir=<path>   Set the path to the repository%n") + "  -h, --help             Show this help message and exit.%n") + "  -V, --version          Print version information and exit.%n") + "Commands:%n") + "  clone   Clone a repository into a new directory%n") + "  commit  Record changes to the repository%n") + "  push    Update remote refs along with associated objects%n")));
        Assert.assertEquals(expected, cmd.getUsageMessage());
    }

    @Test
    public void testParamIndex() {
        CommandLine git = new CommandLine(new CommandLineCommandMethodTest.Git());
        CommandLine clone = git.getSubcommands().get("clone");
        CommandLine.Model.PositionalParamSpec repo = clone.getCommandSpec().positionalParameters().get(0);
        Assert.assertEquals(Range.valueOf("0"), repo.index());
    }

    @CommandLine.Command
    static class AnnotatedParams {
        @CommandLine.Command
        public void method(@CommandLine.Parameters
        int a, @CommandLine.Parameters
        int b, @CommandLine.Parameters
        int c, int x, int y, int z) {
        }
    }

    @Test
    public void testParamIndexAnnotatedAndUnAnnotated() {
        CommandLine git = new CommandLine(new CommandLineCommandMethodTest.AnnotatedParams());
        CommandLine method = git.getSubcommands().get("method");
        List<CommandLine.Model.PositionalParamSpec> positionals = method.getCommandSpec().positionalParameters();
        for (int i = 0; i < (positionals.size()); i++) {
            Assert.assertEquals(Range.valueOf(("" + i)), positionals.get(i).index());
        }
    }

    /**
     * https://github.com/remkop/picocli/issues/538
     */
    static class CommandMethodWithDefaults {
        @CommandLine.Command
        public String cmd(@CommandLine.Option(names = "-a", defaultValue = "2")
        Integer a, @CommandLine.Option(names = "-b")
        Integer b, @CommandLine.Option(names = "-c", defaultValue = "abc")
        String c, @CommandLine.Option(names = "-d")
        String d, @CommandLine.Option(names = "-e", defaultValue = "a=b")
        Map<String, String> e, @CommandLine.Option(names = "-f")
        Map<String, String> f) {
            return String.format("a=%s, b=%s, c=%s, d=%s, e=%s, f=%s", a, b, c, d, e, f);
        }
    }

    // for #538
    @Test
    public void testCommandMethodObjectDefaults() {
        Object s1 = CommandLine.invoke("cmd", CommandLineCommandMethodTest.CommandMethodWithDefaults.class);
        Assert.assertEquals("nothing matched", "a=2, b=null, c=abc, d=null, e={a=b}, f=null", s1);// fails

        Object s2 = CommandLine.invoke("cmd", CommandLineCommandMethodTest.CommandMethodWithDefaults.class, "-a1", "-b2", "-cX", "-dY", "-eX=Y", "-fA=B");
        Assert.assertEquals("all matched", "a=1, b=2, c=X, d=Y, e={X=Y}, f={A=B}", s2);
    }

    private static class PrimitiveWrapper {
        @CommandLine.Option(names = "-0")
        private boolean aBool;

        @CommandLine.Option(names = "-1")
        private Boolean boolWrapper;

        @CommandLine.Option(names = "-b")
        private byte aByte;

        @CommandLine.Option(names = "-B")
        private Byte byteWrapper;

        @CommandLine.Option(names = "-c")
        private char aChar;

        @CommandLine.Option(names = "-C")
        private Character aCharacter;

        @CommandLine.Option(names = "-s")
        private short aShort;

        @CommandLine.Option(names = "-S")
        private Short shortWrapper;

        @CommandLine.Option(names = "-i")
        private int anInt;

        @CommandLine.Option(names = "-I")
        private Integer intWrapper;

        @CommandLine.Option(names = "-l")
        private long aLong;

        @CommandLine.Option(names = "-L")
        private Long longWrapper;

        @CommandLine.Option(names = "-d")
        private double aDouble;

        @CommandLine.Option(names = "-D")
        private Double doubleWrapper;

        @CommandLine.Option(names = "-f")
        private float aFloat;

        @CommandLine.Option(names = "-F")
        private Float floatWrapper;
    }

    // for #538: check no regression
    @Test
    public void testPrimitiveWrappersNotInitializedIfNotMatched() {
        CommandLineCommandMethodTest.PrimitiveWrapper s1 = CommandLine.populateCommand(new CommandLineCommandMethodTest.PrimitiveWrapper());
        Assert.assertEquals(false, s1.aBool);
        Assert.assertNull(s1.boolWrapper);
        Assert.assertEquals(0, s1.aByte);
        Assert.assertNull(s1.byteWrapper);
        Assert.assertEquals(0, s1.aChar);
        Assert.assertNull(s1.aCharacter);
        Assert.assertEquals(0, s1.aShort);
        Assert.assertNull(s1.shortWrapper);
        Assert.assertEquals(0, s1.anInt);
        Assert.assertNull(s1.intWrapper);
        Assert.assertEquals(0, s1.aLong);
        Assert.assertNull(s1.longWrapper);
        Assert.assertEquals(0.0, s1.aDouble, 1.0E-5);
        Assert.assertNull(s1.doubleWrapper);
        Assert.assertEquals(0.0F, s1.aFloat, 1.0E-5F);
        Assert.assertNull(s1.floatWrapper);
    }

    /**
     * Test for https://github.com/remkop/picocli/issues/554
     */
    @CommandLine.Command(name = "maincommand")
    class MainCommand implements Runnable {
        @Spec
        CommandSpec spec;

        public void run() {
            throw new UnsupportedOperationException("must specify a subcommand");
        }

        @CommandLine.Command
        public void subcommand(@CommandLine.Option(names = "-x")
        String x) {
            System.out.println(("x=" + x));
        }

        @CommandLine.Command
        public void explicit(@CommandLine.Option(names = "-v")
        boolean v) {
            CommandLine commandLine = spec.subcommands().get("explicit");
            throw new CommandLine.ParameterException(commandLine, "Validation failed");
        }
    }

    @Test
    public void testSubcommandMethodInvalidInputHandling() {
        String expected = String.format(("" + (("Unknown option: -y%n" + "Usage: maincommand subcommand [-x=<arg0>]%n") + "  -x=<arg0>%n")));
        CommandLine.run(new CommandLineCommandMethodTest.MainCommand(), "subcommand", "-y");
        Assert.assertEquals(expected, this.systemErrRule.getLog());
        Assert.assertEquals("", this.systemOutRule.getLog());
    }

    @Test
    public void testSubcommandMethodThrowingParameterException() {
        String expected = String.format(("" + (("Validation failed%n" + "Usage: maincommand explicit [-v]%n") + "  -v%n")));
        CommandLine.run(new CommandLineCommandMethodTest.MainCommand(), "explicit", "-v");
        Assert.assertEquals(expected, this.systemErrRule.getLog());
        Assert.assertEquals("", this.systemOutRule.getLog());
    }

    // test (1/2) for https://github.com/remkop/picocli/issues/570
    @Test
    public void testOptionalListParameterInCommandClass() {
        @CommandLine.Command
        class TestCommand implements Callable<String> {
            @CommandLine.Parameters(arity = "0..*")
            private List<String> values;

            public String call() throws Exception {
                return (values) == null ? "null" : values.toString();
            }
        }
        // seems to be working for @Command-class @Parameters
        CommandLine commandLine = new CommandLine(new TestCommand());
        List<Object> firstExecutionResultWithParametersGiven = commandLine.parseWithHandlers(new RunLast(), new DefaultExceptionHandler<List<Object>>(), new String[]{ "arg0", "arg1" });
        List<Object> secondExecutionResultWithoutParameters = commandLine.parseWithHandlers(new RunLast(), new DefaultExceptionHandler<List<Object>>(), new String[]{  });
        Assert.assertEquals("[arg0, arg1]", firstExecutionResultWithParametersGiven.get(0));
        Assert.assertEquals("null", secondExecutionResultWithoutParameters.get(0));
    }

    // test (2/2) for https://github.com/remkop/picocli/issues/570
    @Test
    public void testOptionalListParameterShouldNotRememberValuesInCommandMethods() {
        @CommandLine.Command
        class TestCommand {
            @CommandLine.Command(name = "method")
            public String methodCommand(@CommandLine.Parameters(arity = "0..*")
            List<String> methodValues) {
                return methodValues == null ? "null" : methodValues.toString();
            }
        }
        CommandLine commandLine = new CommandLine(new TestCommand());
        // problematic for @Command-method @Parameters
        List<Object> methodFirstExecutionResultWithParametersGiven = commandLine.parseWithHandlers(new RunLast(), new DefaultExceptionHandler<List<Object>>(), new String[]{ "method", "arg0", "arg1" });
        List<Object> methodSecondExecutionResultWithoutParameters = commandLine.parseWithHandlers(new RunLast(), new DefaultExceptionHandler<List<Object>>(), new String[]{ "method" });
        Assert.assertEquals("[arg0, arg1]", methodFirstExecutionResultWithParametersGiven.get(0));
        // fails, still "[arg0, arg1]"
        Assert.assertEquals("null", methodSecondExecutionResultWithoutParameters.get(0));
    }

    @CommandLine.Command(addMethodSubcommands = false)
    static class StaticMethodCommand {
        @Spec
        static CommandSpec spec;

        public StaticMethodCommand(int constructorParam) {
        }

        @CommandLine.Command
        public static int staticCommand(@CommandLine.Option(names = "-x")
        int x) {
            return x * 3;
        }

        @CommandLine.Command
        public void cannotBeCalled(@CommandLine.Option(names = "-v")
        boolean v) {
        }

        @CommandLine.Command
        public static void throwsExecutionException() {
            throw new ExecutionException(new CommandLine(new CommandLineCommandMethodTest.StaticMethodCommand(8)), "abc");
        }

        @CommandLine.Command
        public static void throwsOtherException() {
            throw new IndexOutOfBoundsException();
        }
    }

    @Test
    public void testStaticCommandMethod() {
        Assert.assertEquals(9, CommandLine.invoke("staticCommand", CommandLineCommandMethodTest.StaticMethodCommand.class, "-x", "3"));
    }

    @Test
    public void testInvokeMethodClassPrintStreamAnsi() {
        Assert.assertEquals(9, CommandLine.invoke("staticCommand", CommandLineCommandMethodTest.StaticMethodCommand.class, System.out, Help.Ansi.OFF, "-x", "3"));
    }

    @Test
    public void testCommandMethodsRequireNonArgConstructor() {
        try {
            CommandLine.invoke("cannotBeCalled", CommandLineCommandMethodTest.StaticMethodCommand.class);
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof UnsupportedOperationException));
        }
    }

    @Test
    public void testCommandMethodsThatThrowsExecutionException() {
        try {
            CommandLine.invoke("throwsExecutionException", CommandLineCommandMethodTest.StaticMethodCommand.class);
        } catch (ExecutionException ex) {
            Assert.assertEquals("abc", ex.getMessage());
        }
    }

    @Test
    public void testCommandMethodsThatThrowsException() {
        try {
            CommandLine.invoke("throwsOtherException", CommandLineCommandMethodTest.StaticMethodCommand.class);
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IndexOutOfBoundsException));
        }
    }

    @CommandLine.Command(addMethodSubcommands = false)
    static class ErroringCommand {
        public ErroringCommand() {
            // InvocationTargetException when invoking constructor
            throw new IllegalStateException("boom");
        }

        @CommandLine.Command
        public void cannotBeCalled() {
        }
    }

    @Test
    public void testCommandMethodsWhereConstructorThrowsException() {
        try {
            CommandLine.invoke("cannotBeCalled", CommandLineCommandMethodTest.ErroringCommand.class);
        } catch (ExecutionException ex) {
            // InvocationTargetException when invoking constructor
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
            Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Error while calling command ("));
        }
    }

    @Test
    public void testCommandMethodsUnexpectedError() throws Exception {
        Method method = CommandLineCommandMethodTest.CommandMethod1.class.getDeclaredMethod("times", int.class, int.class);
        CommandLine cmd = new CommandLine(method);
        Method execute = CommandLine.class.getDeclaredMethod("execute", CommandLine.class, List.class);
        execute.setAccessible(true);
        try {
            execute.invoke(null, cmd, null);
        } catch (InvocationTargetException ex) {
            ExecutionException actual = ((ExecutionException) (ex.getCause()));
            Assert.assertTrue(actual.getMessage(), actual.getMessage().startsWith("Unhandled error while calling command ("));
        }
    }

    static class Duplicate {
        @CommandLine.Command
        int mycommand() {
            return 1;
        }

        @CommandLine.Command
        int mycommand(String[] args) {
            return 2;
        }
    }

    @Test
    public void testDuplicateCommandMethodNames() {
        try {
            CommandLine.invoke("mycommand", CommandLineCommandMethodTest.Duplicate.class, System.out, System.out, Help.Ansi.OFF, "abd");
        } catch (InitializationException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("Expected exactly one @Command-annotated method for "));
        }
    }

    @Test
    public void testAddMethodSubcommands() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(new CommandLineCommandMethodTest.StaticMethodCommand(1));
        Assert.assertEquals(0, spec.subcommands().size());
        spec.addMethodSubcommands();
        Assert.assertEquals(4, spec.subcommands().size());
    }

    @Test
    public void testAddMethodSubcommands_DisallowedIfUserObjectIsMethod() throws Exception {
        Method m = CommandLineCommandMethodTest.MethodApp.class.getDeclaredMethod("run1", int.class);
        CommandSpec spec = CommandSpec.wrapWithoutInspection(m);
        try {
            spec.addMethodSubcommands();
        } catch (InitializationException ex) {
            Assert.assertEquals("Cannot discover subcommand methods of this Command Method: int picocli.CommandLineCommandMethodTest$MethodApp.run1(int)", ex.getMessage());
        }
    }

    @Test
    public void testMethodParam_getDeclaringExecutable() throws Exception {
        Method m = CommandLineCommandMethodTest.MethodApp.class.getDeclaredMethod("run1", int.class);
        MethodParam param = new MethodParam(m, 0);
        Assert.assertSame(m, param.getDeclaringExecutable());
    }

    @Test
    public void testMethodParam_isAccessible() throws Exception {
        Method m = CommandLineCommandMethodTest.MethodApp.class.getDeclaredMethod("run1", int.class);
        MethodParam param = new MethodParam(m, 0);
        Assert.assertFalse(param.isAccessible());
        m.setAccessible(true);
        Assert.assertTrue(param.isAccessible());
    }

    static class TypedMemberObj {
        void getterNorSetter1() {
        }

        Void getterNorSetter2() {
            return null;
        }

        int getter() {
            return 0;
        }

        void setter(String str) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void testTypedMemberConstructorRejectsGetterNorSetter() throws Exception {
        Constructor<TypedMember> constructor = TypedMember.class.getDeclaredConstructor(Method.class, Object.class, CommandSpec.class);
        constructor.setAccessible(true);
        Method getterNorSetter1 = CommandLineCommandMethodTest.TypedMemberObj.class.getDeclaredMethod("getterNorSetter1");
        Method getterNorSetter2 = CommandLineCommandMethodTest.TypedMemberObj.class.getDeclaredMethod("getterNorSetter2");
        try {
            constructor.newInstance(getterNorSetter1, new CommandLineCommandMethodTest.TypedMemberObj(), CommandSpec.create());
            Assert.fail("expect exception");
        } catch (InvocationTargetException ex) {
            InitializationException ex2 = ((InitializationException) (ex.getCause()));
            Assert.assertEquals("Invalid method, must be either getter or setter: void picocli.CommandLineCommandMethodTest$TypedMemberObj.getterNorSetter1()", ex2.getMessage());
        }
        try {
            constructor.newInstance(getterNorSetter2, new CommandLineCommandMethodTest.TypedMemberObj(), CommandSpec.create());
            Assert.fail("expect exception");
        } catch (InvocationTargetException ex) {
            InitializationException ex2 = ((InitializationException) (ex.getCause()));
            Assert.assertEquals("Invalid method, must be either getter or setter: java.lang.Void picocli.CommandLineCommandMethodTest$TypedMemberObj.getterNorSetter2()", ex2.getMessage());
        }
    }

    @Test
    public void testTypedMemberConstructorNonProxyObject() throws Exception {
        Constructor<TypedMember> constructor = TypedMember.class.getDeclaredConstructor(Method.class, Object.class, CommandSpec.class);
        constructor.setAccessible(true);
        Method getter = CommandLineCommandMethodTest.TypedMemberObj.class.getDeclaredMethod("getter");
        TypedMember typedMember = constructor.newInstance(getter, new CommandLineCommandMethodTest.TypedMemberObj(), CommandSpec.create());
        Assert.assertSame(typedMember.getter(), typedMember.setter());
        Assert.assertTrue(((typedMember.getter()) instanceof CommandLine.Model.MethodBinding));
    }

    @Test
    public void testTypedMemberInitializeInitialValue() throws Exception {
        Constructor<TypedMember> constructor = TypedMember.class.getDeclaredConstructor(Method.class, Object.class, CommandSpec.class);
        constructor.setAccessible(true);
        Method setter = CommandLineCommandMethodTest.TypedMemberObj.class.getDeclaredMethod("setter", String.class);
        TypedMember typedMember = constructor.newInstance(setter, new CommandLineCommandMethodTest.TypedMemberObj(), CommandSpec.create());
        Method initializeInitialValue = TypedMember.class.getDeclaredMethod("initializeInitialValue", Object.class);
        initializeInitialValue.setAccessible(true);
        try {
            initializeInitialValue.invoke(typedMember, "boom");
        } catch (InvocationTargetException ite) {
            InitializationException ex = ((InitializationException) (ite.getCause()));
            Assert.assertTrue(ex.getMessage().startsWith("Could not set initial value for boom"));
        }
    }

    @Test
    public void testTypedMemberPropertyName() {
        Assert.assertEquals("aBC", TypedMember.propertyName("ABC"));
        Assert.assertEquals("blah", TypedMember.propertyName("setBlah"));
        Assert.assertEquals("blah", TypedMember.propertyName("getBlah"));
        Assert.assertEquals("isBlah", TypedMember.propertyName("isBlah"));
        Assert.assertEquals("isBlah", TypedMember.propertyName("IsBlah"));
        Assert.assertEquals("", TypedMember.propertyName(""));
    }

    @Test
    public void testTypedMemberDecapitalize() throws Exception {
        Method decapitalize = TypedMember.class.getDeclaredMethod("decapitalize", String.class);
        decapitalize.setAccessible(true);
        Assert.assertNull(decapitalize.invoke(null, ((String) (null))));
    }
}

