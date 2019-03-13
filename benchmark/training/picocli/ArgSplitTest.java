package picocli;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import picocli.CommandLine.MissingParameterException;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.ParserSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Range;
import picocli.CommandLine.UnmatchedArgumentException;


public class ArgSplitTest {
    @Test
    public void testSplitInOptionArray() {
        class Args {
            @Option(names = "-a", split = ",")
            String[] values;
        }
        Args args = CommandLine.populateCommand(new Args(), "-a=a,b,c");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "-a=B", "-a", "C");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a", "a,b,c", "-a", "B", "-a", "C");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "-a", "B", "-a", "C", "-a", "D,E,F");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C", "D", "E", "F" }, args.values);
        try {
            CommandLine.populateCommand(new Args(), "-a=a,b,c", "B", "C");
            Assert.fail("Expected UnmatchedArgEx");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched arguments: B, C", ok.getMessage());
        }
        try {
            CommandLine.populateCommand(new Args(), "-a=a,b,c", "B", "-a=C");
            Assert.fail("Expected UnmatchedArgEx");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched argument: B", ok.getMessage());
        }
    }

    @Test
    public void testSplitInOptionArrayWithSpaces() {
        class Args {
            @Option(names = "-a", split = " ")
            String[] values;
        }
        Args args = CommandLine.populateCommand(new Args(), "-a=\"a b c\"");
        Assert.assertArrayEquals(new String[]{ "\"a b c\"" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a=a b c", "-a", "B", "-a", "C");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a", "\"a b c\"", "-a=B", "-a=C");
        Assert.assertArrayEquals(new String[]{ "\"a b c\"", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a=\"a b c\"", "-a=B", "-a", "C", "-a=D E F");
        Assert.assertArrayEquals(new String[]{ "\"a b c\"", "B", "C", "D", "E", "F" }, args.values);
        try {
            CommandLine.populateCommand(new Args(), "-a=a b c", "B", "C");
            Assert.fail("Expected UnmatchedArgEx");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched arguments: B, C", ok.getMessage());
        }
        try {
            CommandLine.populateCommand(new Args(), "-a=a b c", "B", "-a=C");
            Assert.fail("Expected UnmatchedArgEx");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched argument: B", ok.getMessage());
        }
    }

    @Test
    public void testSplitInOptionArrayWithArity() {
        class Args {
            @Option(names = "-a", split = ",", arity = "0..4")
            String[] values;

            @Parameters
            String[] params;
        }
        Args args = CommandLine.populateCommand(new Args(), "-a=a,b,c");// 1 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, args.values);
        args = CommandLine.populateCommand(new Args(), "-a");// 0 args

        Assert.assertArrayEquals(new String[0], args.values);
        Assert.assertNull(args.params);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "B", "C");// 3 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        Assert.assertNull(args.params);
        args = CommandLine.populateCommand(new Args(), "-a", "a,b,c", "B", "C");// 3 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        Assert.assertNull(args.params);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "B", "C", "D,E,F");// 4 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C", "D", "E", "F" }, args.values);
        Assert.assertNull(args.params);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c,d", "B", "C", "D", "E,F");// 5 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "d", "B", "C", "D" }, args.values);
        Assert.assertArrayEquals(new String[]{ "E,F" }, args.params);
    }

    @Test
    public void testSplitInOptionCollection() {
        class Args {
            @Option(names = "-a", split = ",")
            List<String> values;
        }
        Args args = CommandLine.populateCommand(new Args(), "-a=a,b,c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), args.values);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "-a", "B", "-a=C");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C"), args.values);
        args = CommandLine.populateCommand(new Args(), "-a", "a,b,c", "-a", "B", "-a", "C");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C"), args.values);
        args = CommandLine.populateCommand(new Args(), "-a=a,b,c", "-a", "B", "-a", "C", "-a", "D,E,F");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C", "D", "E", "F"), args.values);
        try {
            CommandLine.populateCommand(new Args(), "-a=a,b,c", "B", "C");
            Assert.fail("Expected UnmatchedArgumentException");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched arguments: B, C", ok.getMessage());
        }
    }

    @Test
    public void testSplitInParametersArray() {
        class Args {
            @Parameters(split = ",")
            String[] values;
        }
        Args args = CommandLine.populateCommand(new Args(), "a,b,c");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C" }, args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C", "D,E,F");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "C", "D", "E", "F" }, args.values);
    }

    @Test
    public void testSplitInParametersArrayWithArity() {
        class Args {
            @Parameters(arity = "2..4", split = ",")
            String[] values;
        }
        Args args = CommandLine.populateCommand(new Args(), "a,b", "c,d");// 2 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "d" }, args.values);
        args = CommandLine.populateCommand(new Args(), "a,b", "c,d", "e,f");// 3 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "d", "e", "f" }, args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "d", "e,f");// 4 args

        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "B", "d", "e", "f" }, args.values);
        try {
            CommandLine.populateCommand(new Args(), "a,b,c,d,e");// 1 arg: should fail

            Assert.fail("MissingParameterException expected");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<values>) requires at least 2 values, but only 1 were specified: [a,b,c,d,e]", ex.getMessage());
            Assert.assertEquals(1, ex.getMissing().size());
            Assert.assertTrue(ex.getMissing().get(0).toString(), ((ex.getMissing().get(0)) instanceof CommandLine.Model.PositionalParamSpec));
        }
        try {
            CommandLine.populateCommand(new Args());// 0 arg: should fail

            Assert.fail("MissingParameterException expected");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<values>) requires at least 2 values, but none were specified.", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new Args(), "a,b,c", "B,C", "d", "e", "f,g");// 5 args

            Assert.fail("MissingParameterException expected");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<values>) requires at least 2 values, but only 1 were specified: [f,g]", ex.getMessage());
        }
    }

    @Test
    public void testSplitInParametersCollection() {
        class Args {
            @Parameters(split = ",")
            List<String> values;
        }
        Args args = CommandLine.populateCommand(new Args(), "a,b,c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C"), args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C"), args.values);
        args = CommandLine.populateCommand(new Args(), "a,b,c", "B", "C", "D,E,F");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "B", "C", "D", "E", "F"), args.values);
    }

    @Test
    public void testSplitIgnoredInOptionSingleValueField() {
        class Args {
            @Option(names = "-a", split = ",")
            String value;
        }
        Args args = CommandLine.populateCommand(new Args(), "-a=a,b,c");
        Assert.assertEquals("a,b,c", args.value);
    }

    @Test
    public void testSplitIgnoredInParameterSingleValueField() {
        class Args {
            @Parameters(split = ",")
            String value;
        }
        Args args = CommandLine.populateCommand(new Args(), "a,b,c");
        Assert.assertEquals("a,b,c", args.value);
    }

    @Test
    public void testMapFieldWithSplitRegex() {
        class App {
            @Option(names = "-fix", split = "\\|", type = { Integer.class, String.class })
            Map<Integer, String> message;

            private void validate() {
                Assert.assertEquals(10, message.size());
                Assert.assertEquals(LinkedHashMap.class, message.getClass());
                Assert.assertEquals("FIX.4.4", message.get(8));
                Assert.assertEquals("69", message.get(9));
                Assert.assertEquals("A", message.get(35));
                Assert.assertEquals("MBT", message.get(49));
                Assert.assertEquals("TargetCompID", message.get(56));
                Assert.assertEquals("9", message.get(34));
                Assert.assertEquals("20130625-04:05:32.682", message.get(52));
                Assert.assertEquals("0", message.get(98));
                Assert.assertEquals("30", message.get(108));
                Assert.assertEquals("052", message.get(10));
            }
        }
        CommandLine.populateCommand(new App(), "-fix", "8=FIX.4.4|9=69|35=A|49=MBT|56=TargetCompID|34=9|52=20130625-04:05:32.682|98=0|108=30|10=052").validate();
    }

    @Test
    public void testMapFieldArityWithSplitRegex() {
        class App {
            @Option(names = "-fix", arity = "2", split = "\\|", type = { Integer.class, String.class })
            Map<Integer, String> message;

            private void validate() {
                Assert.assertEquals(message.toString(), 4, message.size());
                Assert.assertEquals(LinkedHashMap.class, message.getClass());
                Assert.assertEquals("a", message.get(1));
                Assert.assertEquals("b", message.get(2));
                Assert.assertEquals("c", message.get(3));
                Assert.assertEquals("d", message.get(4));
            }
        }
        CommandLine.populateCommand(new App(), "-fix", "1=a", "2=b|3=c|4=d").validate();// 2 args

        // Arity should not limit the total number of values put in an array or collection #191
        CommandLine.populateCommand(new App(), "-fix", "1=a", "2=b", "-fix", "3=c", "4=d").validate();// 2 args

        try {
            CommandLine.populateCommand(new App(), "-fix", "1=a|2=b|3=c|4=d");// 1 arg

            Assert.fail("MissingParameterException expected");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("option '-fix' at index 0 (<Integer=String>) requires at least 2 values, but only 1 were specified: [1=a|2=b|3=c|4=d]", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-fix", "1=a", "2=b", "3=c|4=d");// 3 args

            Assert.fail("UnmatchedArgumentException expected");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched argument: 3=c|4=d", ex.getMessage());
        }
    }

    @Test
    public void testArgSpecSplitValue_SplitsQuotedValuesIfConfigured() {
        ParserSpec parser = new ParserSpec().splitQuotedStrings(true);
        ArgSpec spec = PositionalParamSpec.builder().splitRegex(",").build();
        String[] actual = spec.splitValue("a,b,\"c,d,e\",f", parser, Range.valueOf("0"), 0);
        Assert.assertArrayEquals(new String[]{ "a", "b", "\"c", "d", "e\"", "f" }, actual);
    }

    @Test
    public void testArgSpecSplitValue_RespectsQuotedValuesByDefault() {
        ParserSpec parser = new ParserSpec();
        ArgSpec spec = PositionalParamSpec.builder().splitRegex(",").build();
        String[] actual = spec.splitValue("a,b,\"c,d,e\",f", parser, Range.valueOf("0"), 0);
        Assert.assertArrayEquals(new String[]{ "a", "b", "\"c,d,e\"", "f" }, actual);
    }

    @Test
    public void testArgSpecSplitValue_MultipleQuotedValues() {
        ParserSpec parser = new ParserSpec();
        ArgSpec spec = PositionalParamSpec.builder().splitRegex(",").build();
        String[] actual = spec.splitValue("a,b,\"c,d,e\",f,\"xxx,yyy\"", parser, Range.valueOf("0"), 0);
        Assert.assertArrayEquals(new String[]{ "a", "b", "\"c,d,e\"", "f", "\"xxx,yyy\"" }, actual);
    }

    @Test
    public void testArgSpecSplitValue_MultipleQuotedValues_QuotesTrimmedIfRequested() {
        ParserSpec parser = new ParserSpec().trimQuotes(true);
        ArgSpec spec = PositionalParamSpec.builder().splitRegex(",").build();
        String[] actual = spec.splitValue("a,b,\"c,d,e\",f,\"xxx,yyy\"", parser, Range.valueOf("0"), 0);
        Assert.assertArrayEquals(new String[]{ "a", "b", "c,d,e", "f", "xxx,yyy" }, actual);
    }

    @Test
    public void testParseQuotedArgumentWithNestedQuotes() {
        class Example {
            @Option(names = "-x", split = ",")
            String[] parts;
        }
        String[] args = new String[]{ "-x", "a,b,\"c,d,e\",f,\"xxx,yyy\"" };
        Example example = new Example();
        new CommandLine(example).parseArgs(args);
        Assert.assertArrayEquals(new String[]{ "a", "b", "\"c,d,e\"", "f", "\"xxx,yyy\"" }, example.parts);
    }

    @Test
    public void testParseQuotedArgumentWithNestedQuotes2() {
        class Example {
            @Option(names = "-x", split = ",")
            String[] parts;
        }
        String[] args = new String[]{ "-x", "\"-Dvalues=a,b,c\",\"-Dother=1,2\"" };
        Example example = new Example();
        new CommandLine(example).parseArgs(args);
        Assert.assertArrayEquals(new String[]{ "\"-Dvalues=a,b,c\"", "\"-Dother=1,2\"" }, example.parts);
    }

    // test https://github.com/remkop/picocli/issues/379
    @Test
    public void test379() {
        String[] args = new String[]{ "-p", "AppOptions=\"-Dspring.profiles.active=foo,bar -Dspring.mail.host=smtp.mailtrap.io\",OtherOptions=\"\"" };
        class App {
            @Option(names = { "-p", "--parameter" }, split = ",")
            Map<String, String> parameters;
        }
        App app = CommandLine.populateCommand(new App(), args);
        Assert.assertEquals(2, app.parameters.size());
        Assert.assertEquals("\"-Dspring.profiles.active=foo,bar -Dspring.mail.host=smtp.mailtrap.io\"", app.parameters.get("AppOptions"));
        Assert.assertEquals("\"\"", app.parameters.get("OtherOptions"));
    }

    @Test
    public void test379WithTrimQuotes() {
        String[] args = new String[]{ "-p", "AppOptions=\"-Dspring.profiles.active=foo,bar -Dspring.mail.host=smtp.mailtrap.io\",OtherOptions=\"\"" };
        class App {
            @Option(names = { "-p", "--parameter" }, split = ",")
            Map<String, String> parameters;
        }
        App app = new App();
        new CommandLine(app).setTrimQuotes(true).parse(args);
        Assert.assertEquals(2, app.parameters.size());
        Assert.assertEquals("-Dspring.profiles.active=foo,bar -Dspring.mail.host=smtp.mailtrap.io", app.parameters.get("AppOptions"));
        Assert.assertEquals("", app.parameters.get("OtherOptions"));
    }

    @Test
    public void testArgSpecSplitValueDebug() {
        PositionalParamSpec positional = PositionalParamSpec.builder().splitRegex("b").build();
        System.setProperty("picocli.trace", "DEBUG");
        String[] values = positional.splitValue("abc", new CommandLine.Model.ParserSpec().splitQuotedStrings(true), CommandLine.Range.valueOf("1"), 1);
        System.clearProperty("picocli.trace");
        Assert.assertArrayEquals(new String[]{ "a", "c" }, values);
    }

    @Test
    public void testArgSpecSplitWithEscapedBackslashInsideQuote() {
        PositionalParamSpec positional = PositionalParamSpec.builder().splitRegex(";").build();
        System.setProperty("picocli.trace", "DEBUG");
        String value = "\"abc\\\\\\\";def\"";
        String[] values = positional.splitValue(value, new CommandLine.Model.ParserSpec().splitQuotedStrings(false), CommandLine.Range.valueOf("1"), 1);
        System.clearProperty("picocli.trace");
        Assert.assertArrayEquals(new String[]{ "\"abc\\\\\\\";def\"" }, values);
    }

    @Test
    public void testArgSpecSplitWithEscapedBackslashOutsideQuote() {
        PositionalParamSpec positional = PositionalParamSpec.builder().splitRegex(";").build();
        System.setProperty("picocli.trace", "DEBUG");
        String value = "\\\\\"abc\\\";def\";\\\"a\\";
        String[] values = positional.splitValue(value, new CommandLine.Model.ParserSpec().splitQuotedStrings(false), CommandLine.Range.valueOf("1"), 1);
        System.clearProperty("picocli.trace");
        Assert.assertArrayEquals(new String[]{ "\\\\\"abc\\\";def\"", "\\\"a\\" }, values);
    }

    @Test
    public void testArgSpecSplitBalancedQuotedValueDebug() {
        PositionalParamSpec positional = PositionalParamSpec.builder().splitRegex(";").build();
        System.setProperty("picocli.trace", "DEBUG");
        String value = "\"abc\\\";def\"";
        String[] values = positional.splitValue(value, new CommandLine.Model.ParserSpec().splitQuotedStrings(false), CommandLine.Range.valueOf("1"), 1);
        System.clearProperty("picocli.trace");
        Assert.assertArrayEquals(new String[]{ "\"abc\\\";def\"" }, values);
    }

    @Test
    public void testArgSpecSplitUnbalancedQuotedValueDebug() {
        PositionalParamSpec positional = PositionalParamSpec.builder().splitRegex(";").build();
        System.setProperty("picocli.trace", "DEBUG");
        String value = "\"abc\\\";def";
        String[] values = positional.splitValue(value, new CommandLine.Model.ParserSpec().splitQuotedStrings(false), CommandLine.Range.valueOf("1"), 1);
        System.clearProperty("picocli.trace");
        Assert.assertArrayEquals(new String[]{ "\"abc\\\"", "def" }, values);
    }

    @Test
    public void testQuotedMapKeysDefault() {
        class App {
            @Option(names = "-e")
            Map<String, String> runtimeParams = new HashMap<String, String>();
        }
        App app = new App();
        new CommandLine(app).parseArgs("-e", "\"a=b=c\"=foo");
        Assert.assertTrue(app.runtimeParams.containsKey("\"a=b=c\""));
        Assert.assertEquals("foo", app.runtimeParams.get("\"a=b=c\""));
        new CommandLine(app).parseArgs("-e", "\"a=b=c\"=\"x=y=z\"");
        Assert.assertTrue(app.runtimeParams.containsKey("\"a=b=c\""));
        Assert.assertEquals("\"x=y=z\"", app.runtimeParams.get("\"a=b=c\""));
    }

    @Test
    public void testQuotedMapKeysDefaultWithSplit() {
        class App {
            @Option(names = "-e", split = ",")
            Map<String, String> map = new HashMap<String, String>();
        }
        App app = new App();
        new CommandLine(app).parseArgs("-e", "\"a=b=c\"=foo,\"d=e=f\"=bar");
        Assert.assertTrue(app.map.containsKey("\"a=b=c\""));
        Assert.assertTrue(app.map.containsKey("\"d=e=f\""));
        Assert.assertEquals("foo", app.map.get("\"a=b=c\""));
        Assert.assertEquals("bar", app.map.get("\"d=e=f\""));
        new CommandLine(app).parseArgs("-e", "\"a=b=c\"=\"x=y=z\",\"d=e=f\"=\"x2=y2\"");
        Assert.assertTrue(app.map.containsKey("\"a=b=c\""));
        Assert.assertTrue(app.map.containsKey("\"d=e=f\""));
        Assert.assertEquals("\"x=y=z\"", app.map.get("\"a=b=c\""));
        Assert.assertEquals("\"x2=y2\"", app.map.get("\"d=e=f\""));
    }

    @Test
    public void testQuotedMapKeysTrimQuotes() {
        class App {
            @Option(names = "-e")
            Map<String, String> map = new HashMap<String, String>();
        }
        App app = new App();
        new CommandLine(app).setTrimQuotes(true).parseArgs("-e", "\"a=b=c\"=foo");
        Assert.assertTrue(app.map.containsKey("a=b=c"));
        Assert.assertEquals("foo", app.map.get("a=b=c"));
        new CommandLine(app).setTrimQuotes(true).parseArgs("-e", "\"a=b=c\"=x=y=z");
        Assert.assertTrue(app.map.keySet().toString(), app.map.containsKey("a=b=c"));
        Assert.assertEquals("x=y=z", app.map.get("a=b=c"));
    }

    @Test
    public void testQuotedMapKeysAndQuotedMapValuesNeedExtraQuotes() {
        class App {
            @Option(names = "-e")
            Map<String, String> map = new HashMap<String, String>();
        }
        App app = new App();
        new CommandLine(app).setTrimQuotes(true).parseArgs("-e", "\"\"a=b=c\"=\"x y z\"\"");
        Assert.assertTrue(app.map.keySet().toString(), app.map.containsKey("a=b=c"));
        Assert.assertEquals("x y z", app.map.get("a=b=c"));
    }
}

