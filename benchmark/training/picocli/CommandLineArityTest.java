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


import CommandLine.ParameterException;
import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import picocli.CommandLine.Model.PositionalParamSpec;


public class CommandLineArityTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Test
    public void testArityConstructor_fixedRange() {
        Range arity = new Range(1, 23, false, false, null);
        Assert.assertEquals("min", 1, arity.min);
        Assert.assertEquals("max", 23, arity.max);
        Assert.assertEquals("1..23", arity.toString());
        Assert.assertEquals(Range.valueOf("1..23"), arity);
    }

    @Test
    public void testArityConstructor_variableRange() {
        Range arity = new Range(1, Integer.MAX_VALUE, true, false, null);
        Assert.assertEquals("min", 1, arity.min);
        Assert.assertEquals("max", Integer.MAX_VALUE, arity.max);
        Assert.assertEquals("1..*", arity.toString());
        Assert.assertEquals(Range.valueOf("1..*"), arity);
    }

    @Test
    public void testValueOfDisallowsInvalidRange() {
        try {
            Range.valueOf("1..0");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid range (min=1, max=0)", ex.getMessage());
        }
        try {
            Range.valueOf("3..1");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid range (min=3, max=1)", ex.getMessage());
        }
    }

    @Test
    public void testValueOfDisallowsNegativeRange() {
        try {
            Range.valueOf("-1..0");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid negative range (min=-1, max=0)", ex.getMessage());
        }
        try {
            Range.valueOf("-3..1");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid negative range (min=-3, max=1)", ex.getMessage());
        }
    }

    @Test
    public void testConstructorDisallowsNegativeRange() {
        try {
            new Range((-1), 0, true, true, "");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid negative range (min=-1, max=0)", ex.getMessage());
        }
        try {
            new Range((-3), (-1), true, true, "");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid negative range (min=-3, max=-1)", ex.getMessage());
        }
    }

    @Test
    public void testConstructorDisallowsInvalidRange() {
        try {
            new Range(1, 0, true, true, "");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid range (min=1, max=0)", ex.getMessage());
        }
        try {
            new Range(3, 1, true, true, "");
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid range (min=3, max=1)", ex.getMessage());
        }
    }

    private static class SupportedTypes2 {
        String nonOptionField;

        @Option(names = "-boolean")
        boolean booleanField;

        @Option(names = "-int")
        int intField;
    }

    @Test
    public void testOptionArity_forNonAnnotatedField() throws Exception {
        Range arity = Range.optionArity(CommandLineArityTest.SupportedTypes2.class.getDeclaredField("nonOptionField"));
        Assert.assertEquals(0, arity.max);
        Assert.assertEquals(0, arity.min);
        Assert.assertEquals(false, arity.isVariable);
        Assert.assertEquals("0", arity.toString());
    }

    @Test
    public void testArityForOption_booleanFieldImplicitArity0() throws Exception {
        Range arity = Range.optionArity(CommandLineArityTest.SupportedTypes2.class.getDeclaredField("booleanField"));
        Assert.assertEquals(Range.valueOf("0"), arity);
        Assert.assertEquals("0", arity.toString());
    }

    @Test
    public void testArityForOption_intFieldImplicitArity1() throws Exception {
        Range arity = Range.optionArity(CommandLineArityTest.SupportedTypes2.class.getDeclaredField("intField"));
        Assert.assertEquals(Range.valueOf("1"), arity);
        Assert.assertEquals("1", arity.toString());
    }

    @Test
    public void testArityForOption_isExplicitlyDeclaredValue() throws Exception {
        class Params {
            @Option(names = "-timeUnitList", type = TimeUnit.class, arity = "3")
            List<TimeUnit> timeUnitList;
        }
        Range arity = Range.optionArity(Params.class.getDeclaredField("timeUnitList"));
        Assert.assertEquals(Range.valueOf("3"), arity);
        Assert.assertEquals("3", arity.toString());
    }

    @Test
    public void testArityForOption_listFieldImplicitArity1() throws Exception {
        class ImplicitList {
            @Option(names = "-a")
            List<Integer> listIntegers;
        }
        Range arity = Range.optionArity(ImplicitList.class.getDeclaredField("listIntegers"));
        Assert.assertEquals(Range.valueOf("1"), arity);
        Assert.assertEquals("1", arity.toString());
    }

    @Test
    public void testArityForOption_arrayFieldImplicitArity1() throws Exception {
        class ImplicitList {
            @Option(names = "-a")
            int[] intArray;
        }
        Range arity = Range.optionArity(ImplicitList.class.getDeclaredField("intArray"));
        Assert.assertEquals(Range.valueOf("1"), arity);
        Assert.assertEquals("1", arity.toString());
    }

    @Test
    public void testParameterArityWithOptionMember() throws Exception {
        class ImplicitBoolField {
            @Option(names = "-x")
            boolean boolSingleValue;
        }
        Range arity = Range.parameterArity(ImplicitBoolField.class.getDeclaredField("boolSingleValue"));
        Assert.assertEquals(0, arity.max);
        Assert.assertEquals(0, arity.min);
        Assert.assertEquals(false, arity.isVariable);
        Assert.assertEquals("0", arity.toString());
    }

    @Test
    public void testParameterIndex_WhenUndefined() throws Exception {
        class ImplicitBoolField {
            @Parameters
            boolean boolSingleValue;
        }
        Range arity = Range.parameterIndex(ImplicitBoolField.class.getDeclaredField("boolSingleValue"));
        Assert.assertEquals(Integer.MAX_VALUE, arity.max);
        Assert.assertEquals(0, arity.min);
        Assert.assertEquals(true, arity.isVariable);
        Assert.assertEquals("0..*", arity.toString());
    }

    @Test
    public void testParameterIndex_WhenDefined() throws Exception {
        class ImplicitBoolField {
            @Parameters(index = "2..3")
            boolean boolSingleValue;
        }
        Range arity = Range.parameterIndex(ImplicitBoolField.class.getDeclaredField("boolSingleValue"));
        Assert.assertEquals(3, arity.max);
        Assert.assertEquals(2, arity.min);
        Assert.assertEquals(false, arity.isVariable);
        Assert.assertEquals("2..3", arity.toString());
    }

    @Test
    public void testDefaultArity_Field() throws Exception {
        class ImplicitBoolField {
            @Option(names = "-x")
            boolean x;

            @Option(names = "-y")
            int y;

            @Option(names = "-z")
            List<String> z;

            @Parameters
            boolean a;

            @Parameters
            int b;

            @Parameters
            List<String> c;
        }
        Assert.assertEquals(Range.valueOf("0"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("x")));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("y")));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("z")));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("a")));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("b")));
        Assert.assertEquals(Range.valueOf("0..1"), Range.defaultArity(ImplicitBoolField.class.getDeclaredField("c")));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDefaultArity_Class() {
        Assert.assertEquals(Range.valueOf("0"), Range.defaultArity(Boolean.TYPE));
        Assert.assertEquals(Range.valueOf("0"), Range.defaultArity(Boolean.class));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(Integer.TYPE));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(Integer.class));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(List.class));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(String[].class));
        Assert.assertEquals(Range.valueOf("1"), Range.defaultArity(Map.class));
    }

    @Test
    public void testParameterCapacity() throws Exception {
        PositionalParamSpec paramSpec = PositionalParamSpec.builder().index(Range.valueOf("1..2")).arity(Range.valueOf("*")).build();
        Method capacity = PositionalParamSpec.class.getDeclaredMethod("capacity");
        capacity.setAccessible(true);
        Range c = ((Range) (capacity.invoke(paramSpec)));
        Assert.assertEquals(Range.valueOf("*"), c);
    }

    @Test
    public void testValueOf_EmptyString() throws Exception {
        Assert.assertEquals(Range.valueOf("*"), Range.valueOf(""));
    }

    @Test
    public void testValueOf_Invalid() throws Exception {
        Assert.assertEquals(Range.valueOf("0..3"), Range.valueOf("..3"));
    }

    @Test
    public void testMaxSetter() {
        Assert.assertEquals(Range.valueOf("0..3"), Range.valueOf("0").max(3));
    }

    @Test
    public void testIsUnspecified() {
        class App {
            @Parameters
            List<String> unspecified;

            @Parameters(arity = "2")
            List<String> specified;
        }
        CommandLine cmd = new CommandLine(new App());
        Assert.assertTrue(cmd.getCommandSpec().positionalParameters().get(0).arity().isUnspecified());
        Assert.assertFalse(cmd.getCommandSpec().positionalParameters().get(1).arity().isUnspecified());
    }

    @Test
    public void testRangeEquals_OtherType() {
        Assert.assertFalse(Range.valueOf("0").equals(123));
        Assert.assertNotEquals(Range.valueOf("0"), 123);
    }

    @Test
    public void testRangeEquals_MinMaxVariable() {
        Assert.assertNotEquals("different max", Range.valueOf("1..1"), Range.valueOf("1..2"));
        Assert.assertNotEquals("different min", Range.valueOf("2..2"), Range.valueOf("1..2"));
        Assert.assertNotEquals("different isVariable", Range.valueOf("1..*"), Range.valueOf("1..2"));
        Assert.assertNotEquals("different min and isVariable", Range.valueOf("1..*"), Range.valueOf("2..2"));
        Assert.assertEquals("same", Range.valueOf("1..*"), Range.valueOf("1..*"));
    }

    @Test
    public void testArityForParameters_booleanFieldImplicitArity1() throws Exception {
        class ImplicitBoolField {
            @Parameters
            boolean boolSingleValue;
        }
        Range arity = Range.parameterArity(ImplicitBoolField.class.getDeclaredField("boolSingleValue"));
        Assert.assertEquals(Range.valueOf("1"), arity);
        Assert.assertEquals("1", arity.toString());
    }

    @Test
    public void testArityForParameters_intFieldImplicitArity1() throws Exception {
        class ImplicitSingleField {
            @Parameters
            int intSingleValue;
        }
        Range arity = Range.parameterArity(ImplicitSingleField.class.getDeclaredField("intSingleValue"));
        Assert.assertEquals(Range.valueOf("1"), arity);
        Assert.assertEquals("1", arity.toString());
    }

    @Test
    public void testArityForParameters_listFieldImplicitArity0_1() throws Exception {
        class Params {
            @Parameters(type = Integer.class)
            List<Integer> list;
        }
        Range arity = Range.parameterArity(Params.class.getDeclaredField("list"));
        Assert.assertEquals(Range.valueOf("0..1"), arity);
        Assert.assertEquals("0..1", arity.toString());
    }

    @Test
    public void testArityForParameters_arrayFieldImplicitArity0_1() throws Exception {
        class Args {
            @Parameters
            File[] inputFiles;
        }
        Range arity = Range.parameterArity(Args.class.getDeclaredField("inputFiles"));
        Assert.assertEquals(Range.valueOf("0..1"), arity);
        Assert.assertEquals("0..1", arity.toString());
    }

    @Test
    public void testArrayOptionsWithArity0_nConsumeAllArguments() {
        final double[] DEFAULT_PARAMS = new double[]{ 1, 2 };
        class ArrayOptionsArity0_nAndParameters {
            @Parameters
            double[] doubleParams = DEFAULT_PARAMS;

            @Option(names = "-doubles", arity = "0..*")
            double[] doubleOptions;
        }
        ArrayOptionsArity0_nAndParameters params = CommandLine.populateCommand(new ArrayOptionsArity0_nAndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(DEFAULT_PARAMS, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionsWithArity1_nConsumeAllArguments() {
        class ArrayOptionsArity1_nAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-doubles", arity = "1..*")
            double[] doubleOptions;
        }
        ArrayOptionsArity1_nAndParameters params = CommandLine.populateCommand(new ArrayOptionsArity1_nAndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(null, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionsWithArity2_nConsumeAllArguments() {
        class ArrayOptionsArity2_nAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-doubles", arity = "2..*")
            double[] doubleOptions;
        }
        ArrayOptionsArity2_nAndParameters params = CommandLine.populateCommand(new ArrayOptionsArity2_nAndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(null, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionArity2_nConsumesAllArgumentsUpToClusteredOption() {
        class ArrayOptionsArity2_nAndParameters {
            @Parameters
            String[] stringParams;

            @Option(names = "-s", arity = "2..*")
            String[] stringOptions;

            @Option(names = "-v")
            boolean verbose;

            @Option(names = "-f")
            File file;
        }
        ArrayOptionsArity2_nAndParameters params = CommandLine.populateCommand(new ArrayOptionsArity2_nAndParameters(), "-s 1.1 2.2 3.3 4.4 -vfFILE 5.5".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.stringOptions), new String[]{ "1.1", "2.2", "3.3", "4.4" }, params.stringOptions);
        Assert.assertTrue(params.verbose);
        Assert.assertEquals(new File("FILE"), params.file);
        Assert.assertArrayEquals(new String[]{ "5.5" }, params.stringParams);
    }

    @Test
    public void testArrayOptionArity2_nConsumesAllArgumentIncludingQuotedSimpleOption() {
        class ArrayOptionArity2_nAndParameters {
            @Parameters
            String[] stringParams;

            @Option(names = "-s", arity = "2..*")
            String[] stringOptions;

            @Option(names = "-v")
            boolean verbose;

            @Option(names = "-f")
            File file;
        }
        ArrayOptionArity2_nAndParameters params = new ArrayOptionArity2_nAndParameters();
        new CommandLine(params).setTrimQuotes(true).parseArgs("-s 1.1 2.2 3.3 4.4 \"-v\" \"-f\" \"FILE\" 5.5".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.stringOptions), new String[]{ "1.1", "2.2", "3.3", "4.4", "-v", "-f", "FILE", "5.5" }, params.stringOptions);
        Assert.assertFalse("verbose", params.verbose);
        Assert.assertNull("file", params.file);
        Assert.assertArrayEquals(null, params.stringParams);
    }

    @Test
    public void testArrayOptionArity2_nConsumesAllArgumentIncludingQuotedClusteredOption() {
        class ArrayOptionArity2_nAndParameters {
            @Parameters
            String[] stringParams;

            @Option(names = "-s", arity = "2..*")
            String[] stringOptions;

            @Option(names = "-v")
            boolean verbose;

            @Option(names = "-f")
            File file;
        }
        ArrayOptionArity2_nAndParameters params = new ArrayOptionArity2_nAndParameters();
        new CommandLine(params).setTrimQuotes(true).parseArgs("-s 1.1 2.2 3.3 4.4 \"-vfFILE\" 5.5".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.stringOptions), new String[]{ "1.1", "2.2", "3.3", "4.4", "-vfFILE", "5.5" }, params.stringOptions);
        Assert.assertFalse("verbose", params.verbose);
        Assert.assertNull("file", params.file);
        Assert.assertArrayEquals(null, params.stringParams);
    }

    @Test
    public void testArrayOptionArity2_nConsumesAllArgumentsUpToNextSimpleOption() {
        class ArrayOptionArity2_nAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-s", arity = "2..*")
            String[] stringOptions;

            @Option(names = "-v")
            boolean verbose;

            @Option(names = "-f")
            File file;
        }
        ArrayOptionArity2_nAndParameters params = CommandLine.populateCommand(new ArrayOptionArity2_nAndParameters(), "-s 1.1 2.2 3.3 4.4 -v -f=FILE 5.5".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.stringOptions), new String[]{ "1.1", "2.2", "3.3", "4.4" }, params.stringOptions);
        Assert.assertTrue(params.verbose);
        Assert.assertEquals(new File("FILE"), params.file);
        Assert.assertArrayEquals(new double[]{ 5.5 }, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionArity2_nConsumesAllArgumentsUpToNextOptionWithAttachment() {
        class ArrayOptionArity2_nAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-s", arity = "2..*")
            String[] stringOptions;

            @Option(names = "-v")
            boolean verbose;

            @Option(names = "-f")
            File file;
        }
        ArrayOptionArity2_nAndParameters params = CommandLine.populateCommand(new ArrayOptionArity2_nAndParameters(), "-s 1.1 2.2 3.3 4.4 -f=FILE -v 5.5".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.stringOptions), new String[]{ "1.1", "2.2", "3.3", "4.4" }, params.stringOptions);
        Assert.assertTrue(params.verbose);
        Assert.assertEquals(new File("FILE"), params.file);
        Assert.assertArrayEquals(new double[]{ 5.5 }, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionArityNConsumeAllArguments() {
        class ArrayOptionArityNAndParameters {
            @Parameters
            char[] charParams;

            @Option(names = "-chars", arity = "*")
            char[] charOptions;
        }
        ArrayOptionArityNAndParameters params = CommandLine.populateCommand(new ArrayOptionArityNAndParameters(), "-chars a b c d".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.charOptions), new char[]{ 'a', 'b', 'c', 'd' }, params.charOptions);
        Assert.assertArrayEquals(null, params.charParams);
    }

    @Test
    public void testMissingRequiredParams() {
        class Example {
            @Parameters(index = "1", arity = "0..1")
            String optional;

            @Parameters(index = "0")
            String mandatory;
        }
        try {
            CommandLine.populateCommand(new Example(), new String[]{ "mandatory" });
        } catch (MissingParameterException ex) {
            Assert.fail();
        }
        try {
            CommandLine.populateCommand(new Example(), new String[0]);
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <mandatory>", ex.getMessage());
            Assert.assertEquals(1, ex.getMissing().size());
            Assert.assertEquals("<mandatory>", ex.getMissing().get(0).paramLabel());
        }
    }

    @Test
    public void testMissingRequiredParams1() {
        class Tricky1 {
            @Parameters(index = "2")
            String anotherMandatory;

            @Parameters(index = "1", arity = "0..1")
            String optional;

            @Parameters(index = "0")
            String mandatory;
        }
        try {
            CommandLine.populateCommand(new Tricky1(), new String[0]);
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: <mandatory>, <anotherMandatory>", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new Tricky1(), new String[]{ "firstonly" });
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <anotherMandatory>", ex.getMessage());
        }
    }

    @Test
    public void testMissingRequiredParams2() {
        class Tricky2 {
            @Parameters(index = "2", arity = "0..1")
            String anotherOptional;

            @Parameters(index = "1", arity = "0..1")
            String optional;

            @Parameters(index = "0")
            String mandatory;
        }
        try {
            CommandLine.populateCommand(new Tricky2(), new String[]{ "mandatory" });
        } catch (MissingParameterException ex) {
            Assert.fail();
        }
        try {
            CommandLine.populateCommand(new Tricky2(), new String[0]);
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <mandatory>", ex.getMessage());
        }
    }

    @Test
    public void testMissingRequiredParamsWithOptions() {
        class Tricky3 {
            @Option(names = "-v")
            boolean more;

            @Option(names = "-t")
            boolean any;

            @Parameters(index = "1")
            String alsoMandatory;

            @Parameters(index = "0")
            String mandatory;
        }
        try {
            CommandLine.populateCommand(new Tricky3(), new String[]{ "-t", "-v", "mandatory" });
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <alsoMandatory>", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new Tricky3(), new String[]{ "-t", "-v" });
            Assert.fail("Should not accept missing two mandatory parameters");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: <mandatory>, <alsoMandatory>", ex.getMessage());
        }
    }

    @Test
    public void testMissingRequiredParamWithOption() {
        class Tricky3 {
            @Option(names = "-t")
            boolean any;

            @Parameters(index = "0")
            String mandatory;
        }
        try {
            CommandLine.populateCommand(new Tricky3(), new String[]{ "-t" });
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <mandatory>", ex.getMessage());
        }
    }

    @Test
    public void testNoMissingRequiredParamErrorIfHelpOptionSpecified() {
        class App {
            // "hidden": don't show this parameter in usage help message
            @Parameters(hidden = true)
            List<String> allParameters;// no "index" attribute: captures _all_ arguments (as Strings)


            @Parameters(index = "0")
            InetAddress host;

            @Parameters(index = "1")
            int port;

            @Parameters(index = "2..*")
            File[] files;

            @Option(names = "-?", help = true)
            boolean help;
        }
        CommandLine.populateCommand(new App(), new String[]{ "-?" });
        try {
            CommandLine.populateCommand(new App(), new String[0]);
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: <host>, <port>", ex.getMessage());
        }
    }

    @Test
    public void testNoMissingRequiredParamErrorWithLabelIfHelpOptionSpecified() {
        class App {
            // "hidden": don't show this parameter in usage help message
            @Parameters(hidden = true)
            List<String> allParameters;// no "index" attribute: captures _all_ arguments (as Strings)


            @Parameters(index = "0", paramLabel = "HOST")
            InetAddress host;

            @Parameters(index = "1", paramLabel = "PORT")
            int port;

            @Parameters(index = "2..*", paramLabel = "FILES")
            File[] files;

            @Option(names = "-?", help = true)
            boolean help;
        }
        CommandLine.populateCommand(new App(), new String[]{ "-?" });
        try {
            CommandLine.populateCommand(new App(), new String[0]);
            Assert.fail("Should not accept missing mandatory parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: HOST, PORT", ex.getMessage());
        }
    }

    static class BooleanOptionsArity0_nAndParameters {
        @Parameters
        String[] params;

        @Option(names = "-bool", arity = "0..*")
        boolean bool;

        @Option(names = { "-v", "-other" }, arity = "0..*")
        boolean vOrOther;

        @Option(names = "-r")
        boolean rBoolean;
    }

    @Test
    public void testBooleanOptionsArity0_nFalse() {
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool false".split(" "));
        Assert.assertFalse(params.bool);
    }

    @Test
    public void testBooleanOptionsArity0_nTrue() {
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool true".split(" "));
        Assert.assertTrue(params.bool);
        Assert.assertNull(params.params);
    }

    @Test
    public void testBooleanOptionsArity0_nX() {
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool x".split(" "));
        Assert.assertTrue(params.bool);
        Assert.assertArrayEquals(new String[]{ "x" }, params.params);
    }

    @Test
    public void testBooleanOptionsArity0_nConsume1ArgumentIfPossible() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool=false false true".split(" "));
        Assert.assertFalse(params.bool);
        Assert.assertArrayEquals(new String[]{ "false", "true" }, params.params);
    }

    @Test
    public void testBooleanOptionsArity0_nRequiresNoArgument() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool".split(" "));
        Assert.assertTrue(params.bool);
    }

    @Test
    public void testBooleanOptionsArity0_nConsume0ArgumentsIfNextArgIsOption() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool -other".split(" "));
        Assert.assertTrue(params.bool);
        Assert.assertTrue(params.vOrOther);
    }

    @Test
    public void testBooleanOptionsArity0_nConsume0ArgumentsIfNextArgIsParameter() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity0_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool 123 -other".split(" "));
        Assert.assertTrue(params.bool);
        Assert.assertTrue(params.vOrOther);
        Assert.assertArrayEquals(new String[]{ "123" }, params.params);
    }

    @Test
    public void testBooleanOptionsArity0_nFailsIfAttachedParamNotABoolean() {
        // ignores varargs
        try {
            CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-bool=123 -other".split(" "));
            Assert.fail("was able to assign 123 to boolean");
        } catch (CommandLine ex) {
            Assert.assertEquals("Invalid value for option '-bool': '123' is not a boolean", ex.getMessage());
        }
    }

    @Test
    public void testBooleanOptionsArity0_nShortFormFailsIfAttachedParamNotABoolean() {
        // ignores varargs
        try {
            CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-rv234 -bool".split(" "));
            Assert.fail("Expected exception");
        } catch (UnmatchedArgumentException ok) {
            Assert.assertEquals("Unknown option: -234 (while processing option: '-rv234')", ok.getMessage());
        }
    }

    @Test
    public void testBooleanOptionsArity0_nShortFormFailsIfAttachedParamNotABooleanWithUnmatchedArgsAllowed() {
        // ignores varargs
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("-rv234 -bool".split(" "));
        Assert.assertEquals(Arrays.asList("-234"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testBooleanOptionsArity0_nShortFormFailsIfAttachedWithSepParamNotABoolean() {
        // ignores varargs
        try {
            CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity0_nAndParameters(), "-rv=234 -bool".split(" "));
            Assert.fail("was able to assign 234 to boolean");
        } catch (CommandLine ex) {
            Assert.assertEquals("Invalid value for option '-other': '234' is not a boolean", ex.getMessage());
        }
    }

    private static class BooleanOptionsArity1_nAndParameters {
        @Parameters
        boolean[] boolParams;

        @Option(names = "-bool", arity = "1..*")
        boolean aBoolean;
    }

    @Test
    public void testBooleanOptionsArity1_nConsume1Argument() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity1_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool false false true".split(" "));
        Assert.assertFalse(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ false, true }, params.boolParams);
        params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool true false true".split(" "));
        Assert.assertTrue(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ false, true }, params.boolParams);
    }

    @Test
    public void testBooleanOptionsArity1_nCaseInsensitive() {
        // ignores varargs
        CommandLineArityTest.BooleanOptionsArity1_nAndParameters params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool fAlsE false true".split(" "));
        Assert.assertFalse(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ false, true }, params.boolParams);
        params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool FaLsE false true".split(" "));
        Assert.assertFalse(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ false, true }, params.boolParams);
        params = CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool tRuE false true".split(" "));
        Assert.assertTrue(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ false, true }, params.boolParams);
    }

    @Test
    public void testBooleanOptionsArity1_nErrorIfValueNotTrueOrFalse() {
        // ignores varargs
        try {
            CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool abc".split(" "));
            Assert.fail("Invalid format abc was accepted for boolean");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-bool': 'abc' is not a boolean", expected.getMessage());
        }
    }

    @Test
    public void testBooleanOptionsArity1_nErrorIfValueMissing() {
        try {
            CommandLine.populateCommand(new CommandLineArityTest.BooleanOptionsArity1_nAndParameters(), "-bool".split(" "));
            Assert.fail("Missing param was accepted for boolean with arity=1");
        } catch (CommandLine expected) {
            Assert.assertEquals("Missing required parameter for option '-bool' at index 0 (<aBoolean>)", expected.getMessage());
        }
    }

    @Test
    public void testBooleanOptionArity0Consumes0Arguments() {
        class BooleanOptionArity0AndParameters {
            @Parameters
            boolean[] boolParams;

            @Option(names = "-bool", arity = "0")
            boolean aBoolean;
        }
        BooleanOptionArity0AndParameters params = CommandLine.populateCommand(new BooleanOptionArity0AndParameters(), "-bool true false true".split(" "));
        Assert.assertTrue(params.aBoolean);
        Assert.assertArrayEquals(new boolean[]{ true, false, true }, params.boolParams);
    }

    @Test(expected = MissingParameterException.class)
    public void testSingleValueFieldDefaultMinArityIs1() {
        class App {
            @Option(names = "-Long")
            Long aLongField;
        }
        CommandLine.populateCommand(new App(), "-Long");
    }

    @Test
    public void testSingleValueFieldDefaultMinArityIsOne() {
        class App {
            @Option(names = "-boolean")
            boolean booleanField;

            @Option(names = "-Long")
            Long aLongField;
        }
        try {
            CommandLine.populateCommand(new App(), "-Long", "-boolean");
            Assert.fail("should fail");
        } catch (CommandLine ex) {
            Assert.assertEquals("Invalid value for option '-Long': '-boolean' is not a long", ex.getMessage());
        }
    }

    /**
     * see <a href="https://github.com/remkop/picocli/issues/279">issue #279</a>
     */
    @Test
    public void testSingleValueFieldWithOptionalParameter_279() {
        @Command(name = "sample")
        class Sample {
            @Option(names = "--foo", arity = "0..1")
            String foo;
        }
        Sample sample1 = CommandLine.populateCommand(new Sample());// not specified

        Assert.assertNull("optional option is null when option not specified", sample1.foo);
        Sample sample2 = CommandLine.populateCommand(new Sample(), "--foo");// no arguments

        Assert.assertEquals("optional option is empty string when specified without args", "", sample2.foo);
        Sample sample3 = CommandLine.populateCommand(new Sample(), "--foo", "value");// no arguments

        Assert.assertEquals("optional option has value when specified", "value", sample3.foo);
    }

    @Test
    public void testIntOptionArity1_nConsumes1Argument() {
        // ignores varargs
        class IntOptionArity1_nAndParameters {
            @Parameters
            int[] intParams;

            @Option(names = "-int", arity = "1..*")
            int anInt;
        }
        IntOptionArity1_nAndParameters params = CommandLine.populateCommand(new IntOptionArity1_nAndParameters(), "-int 23 42 7".split(" "));
        Assert.assertEquals(23, params.anInt);
        Assert.assertArrayEquals(new int[]{ 42, 7 }, params.intParams);
    }

    @Test
    public void testArrayOptionsWithArity0Consume0Arguments() {
        class OptionsArray0ArityAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-doubles", arity = "0")
            double[] doubleOptions;
        }
        OptionsArray0ArityAndParameters params = CommandLine.populateCommand(new OptionsArray0ArityAndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[0], params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionWithArity1Consumes1Argument() {
        class Options1ArityAndParameters {
            @Parameters
            double[] doubleParams;

            @Option(names = "-doubles", arity = "1")
            double[] doubleOptions;
        }
        Options1ArityAndParameters params = CommandLine.populateCommand(new Options1ArityAndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 2.2, 3.3, 4.4 }, params.doubleParams, 1.0E-6);
        // repeated occurrence
        params = CommandLine.populateCommand(new Options1ArityAndParameters(), "-doubles 1.1 -doubles 2.2 -doubles 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 4.4 }, params.doubleParams, 1.0E-6);
    }

    private static class ArrayOptionArity2AndParameters {
        @Parameters
        double[] doubleParams;

        @Option(names = "-doubles", arity = "2")
        double[] doubleOptions;
    }

    @Test
    public void testArrayOptionWithArity2Consumes2Arguments() {
        CommandLineArityTest.ArrayOptionArity2AndParameters params = CommandLine.populateCommand(new CommandLineArityTest.ArrayOptionArity2AndParameters(), "-doubles 1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 3.3, 4.4 }, params.doubleParams, 1.0E-6);
        // repeated occurrence
        params = CommandLine.populateCommand(new CommandLineArityTest.ArrayOptionArity2AndParameters(), "-doubles 1.1 2.2 -doubles 3.3 4.4 0".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 0.0 }, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionsWithArity2Consume2ArgumentsEvenIfFirstIsAttached() {
        CommandLineArityTest.ArrayOptionArity2AndParameters params = CommandLine.populateCommand(new CommandLineArityTest.ArrayOptionArity2AndParameters(), "-doubles=1.1 2.2 3.3 4.4".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 3.3, 4.4 }, params.doubleParams, 1.0E-6);
        // repeated occurrence
        params = CommandLine.populateCommand(new CommandLineArityTest.ArrayOptionArity2AndParameters(), "-doubles=1.1 2.2 -doubles=3.3 4.4 0".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1.1, 2.2, 3.3, 4.4 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(new double[]{ 0 }, params.doubleParams, 1.0E-6);
    }

    /**
     * Arity should not limit the total number of values put in an array or collection #191
     */
    @Test
    public void testArrayOptionsWithArity2MayContainMoreThan2Values() {
        CommandLineArityTest.ArrayOptionArity2AndParameters params = CommandLine.populateCommand(new CommandLineArityTest.ArrayOptionArity2AndParameters(), "-doubles=1 2 -doubles 3 4 -doubles 5 6".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.doubleOptions), new double[]{ 1, 2, 3, 4, 5, 6 }, params.doubleOptions, 1.0E-6);
        Assert.assertArrayEquals(null, params.doubleParams, 1.0E-6);
    }

    @Test
    public void testArrayOptionWithoutArityConsumesOneArgument() {
        // #192
        class OptionsNoArityAndParameters {
            @Parameters
            char[] charParams;

            @Option(names = "-chars")
            char[] charOptions;
        }
        OptionsNoArityAndParameters params = CommandLine.populateCommand(new OptionsNoArityAndParameters(), "-chars a b c d".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.charOptions), new char[]{ 'a' }, params.charOptions);
        Assert.assertArrayEquals(Arrays.toString(params.charParams), new char[]{ 'b', 'c', 'd' }, params.charParams);
        // repeated occurrence
        params = CommandLine.populateCommand(new OptionsNoArityAndParameters(), "-chars a -chars b c d".split(" "));
        Assert.assertArrayEquals(Arrays.toString(params.charOptions), new char[]{ 'a', 'b' }, params.charOptions);
        Assert.assertArrayEquals(Arrays.toString(params.charParams), new char[]{ 'c', 'd' }, params.charParams);
        try {
            CommandLine.populateCommand(new OptionsNoArityAndParameters(), "-chars".split(" "));
            Assert.fail("expected MissingParameterException");
        } catch (MissingParameterException ok) {
            Assert.assertEquals("Missing required parameter for option '-chars' (<charOptions>)", ok.getMessage());
            Assert.assertEquals(1, ok.getMissing().size());
            Assert.assertTrue(ok.getMissing().get(0).toString(), ((ok.getMissing().get(0)) instanceof Model.OptionSpec));
        }
    }

    @Test
    public void testArrayParametersWithDefaultArity() {
        class ArrayParamsDefaultArity {
            @Parameters
            List<String> params;
        }
        ArrayParamsDefaultArity params = CommandLine.populateCommand(new ArrayParamsDefaultArity(), "a", "b", "c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), params.params);
        params = CommandLine.populateCommand(new ArrayParamsDefaultArity(), "a");
        Assert.assertEquals(Arrays.asList("a"), params.params);
        params = CommandLine.populateCommand(new ArrayParamsDefaultArity());
        Assert.assertEquals(null, params.params);
    }

    @Test
    public void testArrayParametersWithArityMinusOneToN() {
        class ArrayParamsNegativeArity {
            @Parameters(arity = "-1..*")
            List<String> params;
        }
        try {
            new CommandLine(new ArrayParamsNegativeArity());
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Invalid negative range (min=-1, max=2147483647)", ex.getMessage());
        }
    }

    @Test
    public void testArrayParametersArity0_n() {
        class ArrayParamsArity0_n {
            @Parameters(arity = "0..*")
            List<String> params;
        }
        ArrayParamsArity0_n params = CommandLine.populateCommand(new ArrayParamsArity0_n(), "a", "b", "c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), params.params);
        params = CommandLine.populateCommand(new ArrayParamsArity0_n(), "a");
        Assert.assertEquals(Arrays.asList("a"), params.params);
        params = CommandLine.populateCommand(new ArrayParamsArity0_n());
        Assert.assertEquals(null, params.params);
    }

    @Test
    public void testArrayParametersArity1_n() {
        class ArrayParamsArity1_n {
            @Parameters(arity = "1..*")
            List<String> params;
        }
        ArrayParamsArity1_n params = CommandLine.populateCommand(new ArrayParamsArity1_n(), "a", "b", "c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), params.params);
        params = CommandLine.populateCommand(new ArrayParamsArity1_n(), "a");
        Assert.assertEquals(Arrays.asList("a"), params.params);
        try {
            params = CommandLine.populateCommand(new ArrayParamsArity1_n());
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <params>", ex.getMessage());
        }
    }

    @Test
    public void testMissingPositionalParameters() {
        class App {
            @Parameters(index = "0", paramLabel = "PARAM1")
            String p1;

            @Parameters(index = "1", paramLabel = "PARAM2")
            String p2;

            @Parameters(index = "2", paramLabel = "PARAM3")
            String p3;
        }
        try {
            CommandLine.populateCommand(new App(), "a");
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: PARAM2, PARAM3", ex.getMessage());
        }
    }

    @Test
    public void testArrayParametersArity2_n() {
        class ArrayParamsArity2_n {
            @Parameters(arity = "2..*")
            List<String> params;
        }
        ArrayParamsArity2_n params = CommandLine.populateCommand(new ArrayParamsArity2_n(), "a", "b", "c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), params.params);
        try {
            params = CommandLine.populateCommand(new ArrayParamsArity2_n(), "a");
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<params>) requires at least 2 values, but only 1 were specified: [a]", ex.getMessage());
        }
        try {
            params = CommandLine.populateCommand(new ArrayParamsArity2_n());
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<params>) requires at least 2 values, but none were specified.", ex.getMessage());
        }
    }

    @Test
    public void testNonVarargArrayParametersWithArity0() {
        class NonVarArgArrayParamsZeroArity {
            @Parameters(arity = "0")
            List<String> params;
        }
        try {
            CommandLine.populateCommand(new NonVarArgArrayParamsZeroArity(), "a", "b", "c");
            Assert.fail("Expected UnmatchedArgumentException");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: a, b, c", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new NonVarArgArrayParamsZeroArity(), "a");
            Assert.fail("Expected UnmatchedArgumentException");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched argument: a", ex.getMessage());
        }
        NonVarArgArrayParamsZeroArity params = CommandLine.populateCommand(new NonVarArgArrayParamsZeroArity());
        Assert.assertEquals(null, params.params);
    }

    @Test
    public void testNonVarargArrayParametersWithArity1() {
        class NonVarArgArrayParamsArity1 {
            @Parameters(arity = "1")
            List<String> params;
        }
        NonVarArgArrayParamsArity1 actual = CommandLine.populateCommand(new NonVarArgArrayParamsArity1(), "a", "b", "c");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), actual.params);
        NonVarArgArrayParamsArity1 params = CommandLine.populateCommand(new NonVarArgArrayParamsArity1(), "a");
        Assert.assertEquals(Arrays.asList("a"), params.params);
        try {
            params = CommandLine.populateCommand(new NonVarArgArrayParamsArity1());
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <params>", ex.getMessage());
        }
    }

    @Test
    public void testNonVarargArrayParametersWithArity2() {
        class NonVarArgArrayParamsArity2 {
            @Parameters(arity = "2")
            List<String> params;
        }
        NonVarArgArrayParamsArity2 params = null;
        try {
            CommandLine.populateCommand(new NonVarArgArrayParamsArity2(), "a", "b", "c");
            Assert.fail("expected MissingParameterException");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<params>) requires at least 2 values, but only 1 were specified: [c]", ex.getMessage());
        }
        try {
            params = CommandLine.populateCommand(new NonVarArgArrayParamsArity2(), "a");
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<params>) requires at least 2 values, but only 1 were specified: [a]", ex.getMessage());
        }
        try {
            params = CommandLine.populateCommand(new NonVarArgArrayParamsArity2());
            Assert.fail("Should not accept input with missing parameter");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<params>) requires at least 2 values, but none were specified.", ex.getMessage());
        }
    }

    @Test
    public void testMixPositionalParamsWithOptions_ParamsUnboundedArity() {
        class Arg {
            @Parameters(arity = "1..*")
            List<String> parameters;

            @Option(names = "-o")
            List<String> options;
        }
        Arg result = CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "p2", "-o", "v2", "p3", "p4");
        Assert.assertEquals(Arrays.asList("p1", "p2", "p3", "p4"), result.parameters);
        Assert.assertEquals(Arrays.asList("v1", "v2"), result.options);
        Arg result2 = CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "-o", "v2", "p3");
        Assert.assertEquals(Arrays.asList("p1", "p3"), result2.parameters);
        Assert.assertEquals(Arrays.asList("v1", "v2"), result2.options);
        try {
            CommandLine.populateCommand(new Arg(), "-o", "v1", "-o", "v2");
            Assert.fail("Expected MissingParameterException");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <parameters>", ex.getMessage());
        }
    }

    @Test
    public void test130MixPositionalParamsWithOptions() {
        @Command(name = "test-command", description = "tests help from a command script")
        class Arg {
            @Parameters(description = "some parameters")
            List<String> parameters;

            @Option(names = { "-cp", "--codepath" }, description = "the codepath")
            List<String> codepath;
        }
        Arg result = CommandLine.populateCommand(new Arg(), "--codepath", "/usr/x.jar", "placeholder", "-cp", "/bin/y.jar", "another");
        Assert.assertEquals(Arrays.asList("/usr/x.jar", "/bin/y.jar"), result.codepath);
        Assert.assertEquals(Arrays.asList("placeholder", "another"), result.parameters);
    }

    @Test
    public void test130MixPositionalParamsWithOptions1() {
        class Arg {
            @Parameters
            List<String> parameters;

            @Option(names = "-o")
            List<String> options;
        }
        Arg result = CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "p2", "-o", "v2", "p3");
        Assert.assertEquals(Arrays.asList("v1", "v2"), result.options);
        Assert.assertEquals(Arrays.asList("p1", "p2", "p3"), result.parameters);
    }

    @Test
    public void test130MixPositionalParamsWithOptionsArity() {
        class Arg {
            @Parameters(arity = "2")
            List<String> parameters;

            @Option(names = "-o")
            List<String> options;
        }
        Arg result = CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "p2", "-o", "v2", "p3", "p4");
        Assert.assertEquals(Arrays.asList("v1", "v2"), result.options);
        Assert.assertEquals(Arrays.asList("p1", "p2", "p3", "p4"), result.parameters);
        try {
            CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "-o", "v2", "p3");
            Assert.fail("Expected MissingParameterException");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Expected parameter 2 (of 2 mandatory parameters) for positional parameter at index 0..* (<parameters>) but found '-o'", ex.getMessage());
            Assert.assertEquals(1, ex.getMissing().size());
            Assert.assertTrue(ex.getMissing().get(0).toString(), ((ex.getMissing().get(0)) instanceof PositionalParamSpec));
        }
        try {
            CommandLine.populateCommand(new Arg(), "-o", "v1", "p1", "p2", "-o", "v2", "p3");
            Assert.fail("Expected MissingParameterException");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<parameters>) requires at least 2 values, but only 1 were specified: [p3]", ex.getMessage());
            Assert.assertEquals(1, ex.getMissing().size());
            Assert.assertTrue(ex.getMissing().get(0).toString(), ((ex.getMissing().get(0)) instanceof PositionalParamSpec));
        }
    }

    @Test
    public void test365_StricterArityValidation() {
        class Cmd {
            @Option(names = "-a", arity = "2")
            String[] a;

            @Option(names = "-b", arity = "1..2")
            String[] b;

            @Option(names = "-c", arity = "2..3")
            String[] c;

            @Option(names = "-v")
            boolean verbose;
        }
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-a' but found '-a'", new Cmd(), "-a", "1", "-a", "2");
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-a' but found '-v'", new Cmd(), "-a", "1", "-v");
        assertMissing("Expected parameter for option '-b' but found '-v'", new Cmd(), "-b", "-v");
        assertMissing("option '-c' at index 0 (<c>) requires at least 2 values, but only 1 were specified: [-a]", new Cmd(), "-c", "-a");
        assertMissing("Expected parameter 1 (of 2 mandatory parameters) for option '-c' but found '-a'", new Cmd(), "-c", "-a", "1", "2");
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-c' but found '-a'", new Cmd(), "-c", "1", "-a");
    }

    @Test
    public void test365_StricterArityValidationWithMaps() {
        class Cmd {
            @Option(names = "-a", arity = "2")
            Map<String, String> a;

            @Option(names = "-b", arity = "1..2")
            Map<String, String> b;

            @Option(names = "-c", arity = "2..3")
            Map<String, String> c;

            @Option(names = "-v")
            boolean verbose;
        }
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-a' but found '-a'", new Cmd(), "-a", "A=B", "-a", "C=D");
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-a' but found '-v'", new Cmd(), "-a", "A=B", "-v");
        assertMissing("Expected parameter for option '-b' but found '-v'", new Cmd(), "-b", "-v");
        assertMissing("option '-c' at index 0 (<String=String>) requires at least 2 values, but only 1 were specified: [-a]", new Cmd(), "-c", "-a");
        assertMissing("Expected parameter 1 (of 2 mandatory parameters) for option '-c' but found '-a'", new Cmd(), "-c", "-a", "A=B", "C=D");
        assertMissing("Expected parameter 2 (of 2 mandatory parameters) for option '-c' but found '-a'", new Cmd(), "-c", "A=B", "-a");
    }

    @Test
    public void test285VarargPositionalShouldNotConsumeOptions() {
        class Cmd {
            @Option(names = "--alpha")
            String alpha;

            @Parameters(index = "0", arity = "1")
            String foo;

            @Parameters(index = "1..*", arity = "*")
            List<String> params;
        }
        Cmd cmd = CommandLine.populateCommand(new Cmd(), "foo", "xx", "--alpha", "--beta");
        Assert.assertEquals("foo", cmd.foo);
        Assert.assertEquals("--beta", cmd.alpha);
        Assert.assertEquals(Arrays.asList("xx"), cmd.params);
    }

    @Test
    public void test285VarargPositionalShouldConsumeOptionsAfterDoubleDash() {
        class Cmd {
            @Option(names = "--alpha")
            String alpha;

            @Parameters(index = "0", arity = "1")
            String foo;

            @Parameters(index = "1..*", arity = "*")
            List<String> params;
        }
        Cmd cmd = CommandLine.populateCommand(new Cmd(), "foo", "--", "xx", "--alpha", "--beta");
        Assert.assertEquals("foo", cmd.foo);
        Assert.assertEquals(null, cmd.alpha);
        Assert.assertEquals(Arrays.asList("xx", "--alpha", "--beta"), cmd.params);
    }

    @Test
    public void testPositionalShouldCaptureDoubleDashAfterDoubleDash() {
        class Cmd {
            @Parameters
            List<String> params;
        }
        Cmd cmd = CommandLine.populateCommand(new Cmd(), "foo", "--", "--", "--");
        Assert.assertEquals(Arrays.asList("foo", "--", "--"), cmd.params);
    }

    @Test
    public void testVarargPositionalShouldCaptureDoubleDashAfterDoubleDash() {
        class Cmd {
            @Parameters(index = "0..*", arity = "*")
            List<String> params;
        }
        Cmd cmd = CommandLine.populateCommand(new Cmd(), "foo", "--", "--", "--");
        Assert.assertEquals(Arrays.asList("foo", "--", "--"), cmd.params);
    }

    @Test
    public void testIfStopAtPositional_VarargPositionalShouldConsumeOptions() {
        class Cmd {
            @Option(names = "--alpha")
            String alpha;

            @Parameters(index = "0", arity = "1")
            String foo;

            @Parameters(index = "1..*", arity = "*")
            List<String> params;
        }
        Cmd cmd = new Cmd();
        System.setProperty("picocli.trace", "DEBUG");
        new CommandLine(cmd).setStopAtPositional(true).parse("foo", "xx", "--alpha", "--beta");
        Assert.assertEquals("foo", cmd.foo);
        Assert.assertEquals(null, cmd.alpha);
        Assert.assertEquals(Arrays.asList("xx", "--alpha", "--beta"), cmd.params);
        Assert.assertTrue(systemErrRule.getLog().contains("Parser was configured with stopAtPositional=true, treating remaining arguments as positional parameters."));
    }

    @Test
    public void testIfStopAtPositional_PositionalShouldConsumeOptions() {
        class Cmd {
            @Option(names = "--alpha")
            String alpha;

            @Parameters(index = "0")
            String foo;

            @Parameters(index = "1..*")
            List<String> params;
        }
        Cmd cmd = new Cmd();
        new CommandLine(cmd).setStopAtPositional(true).parse("foo", "xx", "--alpha", "--beta");
        Assert.assertEquals("foo", cmd.foo);
        Assert.assertEquals(null, cmd.alpha);
        Assert.assertEquals(Arrays.asList("xx", "--alpha", "--beta"), cmd.params);
    }

    @Test
    public void testPosixAttachedOnly1() {
        class ValSepC {
            @Option(names = "-a", arity = "2")
            String[] a;

            @Option(names = "-b", arity = "2", split = ",")
            String[] b;

            @Option(names = "-c", arity = "*", split = ",")
            String[] c;

            @Option(names = "-d")
            boolean d;

            @Option(names = "-e", arity = "1", split = ",")
            boolean e;

            @Unmatched
            String[] remaining;
        }
        ValSepC val1 = parseCommonsCliCompatible(new ValSepC(), "-a 1 2 3 4".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2" }, val1.a);
        Assert.assertArrayEquals(new String[]{ "3", "4" }, val1.remaining);
        ValSepC val2 = parseCommonsCliCompatible(new ValSepC(), "-a1 -a2 3".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2" }, val2.a);
        Assert.assertArrayEquals(new String[]{ "3" }, val2.remaining);
        ValSepC val3 = parseCommonsCliCompatible(new ValSepC(), "-b1,2".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2" }, val3.b);
        ValSepC val4 = parseCommonsCliCompatible(new ValSepC(), "-c 1".split(" "));
        Assert.assertArrayEquals(new String[]{ "1" }, val4.c);
        ValSepC val5 = parseCommonsCliCompatible(new ValSepC(), "-c1".split(" "));
        Assert.assertArrayEquals(new String[]{ "1" }, val5.c);
        ValSepC val6 = parseCommonsCliCompatible(new ValSepC(), "-c1,2,3".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, val6.c);
        ValSepC val7 = parseCommonsCliCompatible(new ValSepC(), "-d".split(" "));
        Assert.assertTrue(val7.d);
        Assert.assertFalse(val7.e);
        ValSepC val8 = parseCommonsCliCompatible(new ValSepC(), "-e true".split(" "));
        Assert.assertFalse(val8.d);
        Assert.assertTrue(val8.e);
    }

    @Test
    public void testPosixClusteredBooleansAttached() {
        class App {
            @Option(names = "-a")
            boolean a;

            @Option(names = "-b")
            boolean b;

            @Option(names = "-c")
            boolean c;

            @Unmatched
            String[] remaining;
        }
        App app = parseCommonsCliCompatible(new App(), "-abc".split(" "));
        Assert.assertTrue("a", app.a);
        Assert.assertTrue("b", app.b);
        Assert.assertTrue("c", app.c);
        Assert.assertNull(app.remaining);
        app = parseCommonsCliCompatible(new App(), "-abc -d".split(" "));
        Assert.assertTrue("a", app.a);
        Assert.assertTrue("b", app.b);
        Assert.assertTrue("c", app.c);
        Assert.assertArrayEquals(new String[]{ "-d" }, app.remaining);
    }

    @Test
    public void testPosixClusteredBooleanArraysAttached() {
        class App {
            @Option(names = "-a")
            boolean[] a;

            @Option(names = "-b")
            boolean[] b;

            @Option(names = "-c")
            boolean[] c;

            @Unmatched
            String[] remaining;
        }
        App app = parseCommonsCliCompatible(new App(), "-abc".split(" "));
        Assert.assertArrayEquals("a", new boolean[]{ true }, app.a);
        Assert.assertArrayEquals("b", new boolean[]{ true }, app.b);
        Assert.assertArrayEquals("c", new boolean[]{ true }, app.c);
        Assert.assertNull(app.remaining);
        app = parseCommonsCliCompatible(new App(), "-abc -d".split(" "));
        Assert.assertArrayEquals("a", new boolean[]{ true }, app.a);
        Assert.assertArrayEquals("b", new boolean[]{ true }, app.b);
        Assert.assertArrayEquals("c", new boolean[]{ true }, app.c);
        Assert.assertArrayEquals(new String[]{ "-d" }, app.remaining);
        app = parseCommonsCliCompatible(new App(), "-aaabbccc -d".split(" "));
        Assert.assertArrayEquals("a", new boolean[]{ true, true, true }, app.a);
        Assert.assertArrayEquals("b", new boolean[]{ true, true }, app.b);
        Assert.assertArrayEquals("c", new boolean[]{ true, true, true }, app.c);
        Assert.assertArrayEquals(new String[]{ "-d" }, app.remaining);
    }

    @Test
    public void testPosixAttachedOnly3() {
        class ValSepC {
            @Option(names = "-a", arity = "2")
            String[] a;

            @Unmatched
            String[] remaining;
        }
        try {
            parseCommonsCliCompatible(new ValSepC(), "-a 1 -a 2".split(" "));
            Assert.fail("Expected exception: Arity not satisfied");
        } catch (Exception ok) {
            Assert.assertEquals("Expected parameter 2 (of 2 mandatory parameters) for option '-a' but found '-a'", ok.getMessage());
        }
    }

    @Test
    public void testPosixAttachedOnly2() {
        class ValSepC {
            @Option(names = "-a", arity = "2")
            String[] a;

            @Unmatched
            String[] remaining;
        }
        try {
            parseCommonsCliCompatible(new ValSepC(), "-a 1".split(" "));
            Assert.fail();
        } catch (Exception ok) {
        }
        ValSepC val1 = parseCommonsCliCompatible(new ValSepC(), "-a1".split(" "));
        Assert.assertArrayEquals(new String[]{ "1" }, val1.a);
        val1 = parseCommonsCliCompatible(new ValSepC(), "-a1 -a2".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2" }, val1.a);
        val1 = parseCommonsCliCompatible(new ValSepC(), "-a1 -a2 -a3".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, val1.a);
        try {
            parseCommonsCliCompatible(new ValSepC(), "-a 1 -a 2 -a 3".split(" "));
            Assert.fail();
        } catch (Exception ok) {
        }
        val1 = parseCommonsCliCompatible(new ValSepC(), "-a 1 2".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2" }, val1.a);
        val1 = parseCommonsCliCompatible(new ValSepC(), "-a1 2".split(" "));
        Assert.assertArrayEquals(new String[]{ "1" }, val1.a);
        Assert.assertArrayEquals(new String[]{ "2" }, val1.remaining);
    }

    @Test
    public void testCommonsCliCompatibleSeparatorHandling() {
        class ValSepC {
            @Option(names = "-a", arity = "1..2")
            String[] a;

            @Option(names = "-b", arity = "1..2", split = ",")
            String[] b;

            @Option(names = "-c", arity = "1..*", split = ",")
            String[] c;

            @Unmatched
            String[] remaining;
        }
        ValSepC val3a = parseCommonsCliCompatible(new ValSepC(), "-b1,2,3".split(" "));
        Assert.assertArrayEquals(new String[]{ "1", "2,3" }, val3a.b);
    }

    @Test
    public void testCommonsCliCompatibleSeparatorHandlingForMaps() {
        class ValSepC {
            @Option(names = "-b", arity = "1..2", split = ",")
            Map<String, String> b;
        }
        ValSepC val3a = parseCommonsCliCompatible(new ValSepC(), "-ba=1,b=2,c=3".split(" "));
        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put("a", "1");
        expected.put("b", "2,c=3");
        Assert.assertEquals(expected, val3a.b);
    }

    @Test
    public void testArityZeroForBooleanOption() {
        class App {
            @Option(names = "--explicit", arity = "0")
            boolean explicit;

            @Option(names = "--implicit")
            boolean implicit;
        }
        try {
            new CommandLine(new App()).parseArgs("--implicit=false --explicit=false".split(" "));
            Assert.fail("--explicit option should not accept parameters");
        } catch (ParameterException ex) {
            Assert.assertEquals("option '--explicit' (<explicit>) should be specified without 'false' parameter", ex.getMessage());
        }
    }

    @Test(expected = InitializationException.class)
    public void testRangeConstructorDisallowsNegativeMin() {
        new Range((-1), 2, false, false, "");
    }

    @Test(expected = InitializationException.class)
    public void testRangeConstructorDisallowsNegativeMax() {
        new Range(0, (-2), false, false, "");
    }

    @Test
    public void testRangeArityMismatchingTypeCount() {
        class InvalidMapTypes {
            // invalid: only one type
            @Option(names = "-D", arity = "1..3", type = Integer.class)
            TreeMap<Integer, String> map;
        }
        try {
            CommandLine.populateCommand(new InvalidMapTypes(), "-D", "1=a");
            Assert.fail("expect exception");
        } catch (ParameterException ex) {
            Assert.assertEquals("field java.util.TreeMap<Integer, String> picocli.CommandLineArityTest$1InvalidMapTypes.map needs two types (one for the map key, one for the value) but only has 1 types configured.", ex.getMessage());
        }
    }

    @Test
    public void testVariableArityMap() {
        class App {
            @Option(names = "-D", arity = "1..*")
            TreeMap<Integer, String> map;
        }
        App app = CommandLine.populateCommand(new App(), "-D", "1=a", "2=b", "3=c");
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        Assert.assertEquals(map, app.map);
    }

    @Test
    public void testRangeArity0To3Map0() {
        class App {
            @Option(names = "-D", arity = "0..3")
            TreeMap<Integer, String> map;
        }
        App app = CommandLine.populateCommand(new App(), "-D");
        Assert.assertEquals(new TreeMap<Integer, String>(), app.map);
    }

    @Test
    public void testRangeArity1To3Map1() {
        class App {
            @Option(names = "-D", arity = "1..3")
            TreeMap<Integer, String> map;

            @Option(names = "-x")
            int x;
        }
        App app = CommandLine.populateCommand(new App(), "-D", "1=a", "-x", "123");
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(1, "a");
        Assert.assertEquals(map, app.map);
        Assert.assertEquals(123, app.x);
    }

    @Test
    public void testRangeArity1To3Map2() {
        class App {
            @Option(names = "-D", arity = "1..3")
            TreeMap<Integer, String> map;
        }
        App app = CommandLine.populateCommand(new App(), "-D", "1=a", "2=b");
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(1, "a");
        map.put(2, "b");
        Assert.assertEquals(map, app.map);
    }

    @Test
    public void testRangeArity1To3Map3() {
        class App {
            @Option(names = "-D", arity = "1..3")
            TreeMap<Integer, String> map;
        }
        App app = CommandLine.populateCommand(new App(), "-D", "1=a", "2=b", "3=c");
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        Assert.assertEquals(map, app.map);
    }

    @Test
    public void testMapArgumentsArity() {
        class App {
            @Parameters(arity = "2")
            Map<String, String> map;
        }
        try {
            CommandLine.populateCommand(new App(), "a=c");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<String=String>) requires at least 2 values, but only 1 were specified: [a=c]", ex.getMessage());
        }
    }
}

