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


import Ansi.OFF;
import UsageMessageSpec.DEFAULT_USAGE_WIDTH;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;


public class CommandLineModelTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Test
    public void testEmptyModelUsageHelp() {
        CommandSpec spec = CommandSpec.create();
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        Assert.assertEquals(String.format("Usage: <main class>%n"), actual);
    }

    @Test
    public void testEmptyModelParse() {
        HelpTestUtil.setTraceLevel("OFF");
        CommandSpec spec = CommandSpec.create();
        CommandLine commandLine = new CommandLine(spec);
        commandLine.setUnmatchedArgumentsAllowed(true);
        commandLine.parse("-p", "123", "abc");
        Assert.assertEquals(Arrays.asList("-p", "123", "abc"), commandLine.getUnmatchedArguments());
    }

    @Test
    public void testModelUsageHelp() {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder("-h", "--help").usageHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-V", "--version").versionHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-c", "--count").paramLabel("COUNT").arity("1").type(int.class).description("number of times to execute").build());
        spec.addOption(OptionSpec.builder("-f", "--fix").paramLabel("FIXED(BOOLEAN)").arity("1").hideParamSyntax(true).required(true).description("run with fixed option").build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + (((("Usage: <main class> [-hV] [-c=COUNT] -f=FIXED(BOOLEAN)%n" + "  -c, --count=COUNT          number of times to execute%n") + "  -f, --fix=FIXED(BOOLEAN)   run with fixed option%n") + "  -h, --help                 show help and exit%n") + "  -V, --version              show help and exit%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testModelUsageHelpWithCustomSeparator() {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder("-h", "--help").usageHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-V", "--version").versionHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-c", "--count").paramLabel("COUNT").arity("1").type(int.class).description("number of times to execute").build());
        spec.addOption(OptionSpec.builder("-f", "--fix").paramLabel("FIXED(=BOOLEAN)").arity("1").hideParamSyntax(true).required(true).description("run with fixed option").build());
        CommandLine commandLine = setSeparator(" ");
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + ((((("Usage: <main class> [-hV] [-c COUNT] -f FIXED(=BOOLEAN)%n" + "  -c, --count COUNT   number of times to execute%n") + "  -f, --fix FIXED(=BOOLEAN)%n") + "                      run with fixed option%n") + "  -h, --help          show help and exit%n") + "  -V, --version       show help and exit%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpPositional_empty() {
        CommandSpec spec = CommandSpec.create();
        spec.addPositional(PositionalParamSpec.builder().build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + ("Usage: <main class> PARAM...%n" + "      PARAM...%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpPositional_withDescription() {
        CommandSpec spec = CommandSpec.create();
        spec.addPositional(PositionalParamSpec.builder().description("positional param").build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + ("Usage: <main class> PARAM...%n" + "      PARAM...   positional param%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelp_emptyWithAutoHelpMixin() {
        CommandSpec spec = CommandSpec.create().addMixin("auto", CommandSpec.forAnnotatedObject(new AutoHelpMixin()));
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + (("Usage: <main class> [-hV]%n" + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelp_CustomizedUsageMessage() {
        CommandSpec spec = CommandSpec.create().addMixin("auto", CommandSpec.forAnnotatedObject(new AutoHelpMixin()));
        spec.name("the awesome util");
        spec.usageMessage().descriptionHeading("Description heading%n").description("description line 1", "description line 2").footerHeading("Footer heading%n").footer("footer line 1", "footer line 2").headerHeading("Header heading%n").header("header line 1", "header line 2").optionListHeading("Options%n").parameterListHeading("Positional Parameters%n");
        spec.addPositional(PositionalParamSpec.builder().description("positional param").build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + (((((((((((((("Header heading%n" + "header line 1%n") + "header line 2%n") + "Usage: the awesome util [-hV] PARAM...%n") + "Description heading%n") + "description line 1%n") + "description line 2%n") + "Positional Parameters%n") + "      PARAM...    positional param%n") + "Options%n") + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n") + "Footer heading%n") + "footer line 1%n") + "footer line 2%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelp_abbreviateSynopsisWithoutPositional() throws UnsupportedEncodingException {
        CommandSpec spec = CommandSpec.create();
        spec.usageMessage().abbreviateSynopsis(true).requiredOptionMarker('!').sortOptions(false);
        spec.addOption(OptionSpec.builder("-x").required(true).description("required").build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + ("Usage: <main class> [OPTIONS]%n" + "! -x     required%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelp_abbreviateSynopsisWithPositional() throws UnsupportedEncodingException {
        CommandSpec spec = CommandSpec.create();
        spec.usageMessage().abbreviateSynopsis(true).requiredOptionMarker('!').sortOptions(false);
        spec.addOption(OptionSpec.builder("-x").required(true).description("required").build());
        spec.addPositional(PositionalParamSpec.builder().arity("1").paramLabel("POSITIONAL").description("positional").build());
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.usageString(commandLine, OFF);
        String expected = String.format(("" + (("Usage: <main class> [OPTIONS] POSITIONAL...%n" + "!     POSITIONAL...   positional%n") + "! -x                  required%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelp_width_default80() {
        Assert.assertEquals(80, DEFAULT_USAGE_WIDTH);
        Assert.assertEquals(DEFAULT_USAGE_WIDTH, new UsageMessageSpec().width());
    }

    @Test
    public void testUsageHelp_width_configurableWithSystemProperty() {
        System.setProperty("picocli.usage.width", "67");
        try {
            Assert.assertEquals(67, new UsageMessageSpec().width());
        } finally {
            System.clearProperty("picocli.usage.width");
        }
    }

    @Test
    public void testUsageHelp_width_SystemPropertyOverrulesSetValue() {
        System.setProperty("picocli.usage.width", "67");
        try {
            Assert.assertEquals(67, new UsageMessageSpec().width(123).width());
        } finally {
            System.clearProperty("picocli.usage.width");
        }
    }

    @Test
    public void testUsageHelp_width_setter() {
        UsageMessageSpec spec = new UsageMessageSpec();
        spec.width(67);
        Assert.assertEquals(67, spec.width());
    }

    @Test(expected = InitializationException.class)
    public void testUsageHelp_width_setterDisallowsValuesBelow55() {
        new UsageMessageSpec().width(54);
    }

    @Test
    public void testUsageHelp_width_setterAllowsValuesAt55OrHigher() {
        Assert.assertEquals(55, new UsageMessageSpec().width(55).width());
        Assert.assertEquals(Integer.MAX_VALUE, new UsageMessageSpec().width(Integer.MAX_VALUE).width());
    }

    @Test
    public void testVersionHelp_basic() {
        CommandSpec spec = CommandSpec.create().version("1.0", "copyright etc");
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.versionString(commandLine, OFF);
        String expected = String.format(("" + ("1.0%n" + "copyright etc%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testVersionHelp_versionProvider() {
        IVersionProvider provider = new IVersionProvider() {
            public String[] getVersion() {
                return new String[]{ "2.0", "by provider" };
            }
        };
        CommandSpec spec = CommandSpec.create().versionProvider(provider);
        CommandLine commandLine = new CommandLine(spec);
        String actual = HelpTestUtil.versionString(commandLine, OFF);
        String expected = String.format(("" + ("2.0%n" + "by provider%n")));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testVersionHelp_helpCommand() {
        CommandSpec helpCommand = CommandSpec.create().helpCommand(true);
        Assert.assertTrue(helpCommand.helpCommand());
        CommandSpec parent = CommandSpec.create().addOption(OptionSpec.builder("-x").type(String.class).required(true).build());
        parent.addSubcommand("help", helpCommand);
        CommandLine commandLine = new CommandLine(parent);
        commandLine.parse("help");// no missing param exception

        try {
            commandLine.parse();
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Missing required option '-x=PARAM'", ex.getMessage());
            Assert.assertEquals(1, ex.getMissing().size());
            Assert.assertSame(ex.getMissing().get(0).toString(), parent.posixOptionsMap().get('x'), ex.getMissing().get(0));
        }
    }

    @Test
    public void testModelParse() {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder("-h", "--help").usageHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-V", "--version").versionHelp(true).description("show help and exit").build());
        spec.addOption(OptionSpec.builder("-c", "--count").paramLabel("COUNT").arity("1").type(int.class).description("number of times to execute").build());
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "33");
        Assert.assertEquals(Integer.valueOf(33), spec.optionsMap().get("-c").getValue());
    }// TODO parse method should return an object offering only the options/positionals that were matched


    @Test
    public void testMultiValueOptionArityAloneIsInsufficient() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(int.class).build();
        Assert.assertFalse(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        try {
            commandLine.parse("-c", "1", "2", "3");
            Assert.fail("Expected exception");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: 2, 3", ex.getMessage());
        }
    }

    @Test
    public void testMultiValuePositionalParamArityAloneIsInsufficient() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(int.class).build();
        Assert.assertFalse(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        try {
            commandLine.parse("1", "2", "3");
            Assert.fail("Expected exception");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: 2, 3", ex.getMessage());
        }
    }

    @Test
    public void testMultiValueOptionWithArray() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(int[].class).build();
        Assert.assertTrue(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "1", "2", "3");
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, ((int[]) (spec.optionsMap().get("-c").getValue())));
    }

    @Test
    public void testMultiValuePositionalParamWithArray() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(int[].class).build();
        Assert.assertTrue(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("1", "2", "3");
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, ((int[]) (spec.positionalParameters().get(0).getValue())));
    }

    @Test
    public void testMultiValueOptionWithListAndAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(List.class).auxiliaryTypes(Integer.class).build();
        Assert.assertTrue(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "1", "2", "3");
        Assert.assertEquals(Arrays.asList(1, 2, 3), spec.optionsMap().get("-c").getValue());
    }

    @Test
    public void testMultiValuePositionalParamWithListAndAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(List.class).auxiliaryTypes(Integer.class).build();
        Assert.assertTrue(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("1", "2", "3");
        Assert.assertEquals(Arrays.asList(1, 2, 3), spec.positionalParameters().get(0).getValue());
    }

    @Test
    public void testMultiValueOptionWithListWithoutAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(List.class).build();
        Assert.assertTrue(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "1", "2", "3");
        Assert.assertEquals(Arrays.asList("1", "2", "3"), spec.optionsMap().get("-c").getValue());
    }

    @Test
    public void testMultiValuePositionalParamWithListWithoutAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(List.class).build();
        Assert.assertTrue(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("1", "2", "3");
        Assert.assertEquals(Arrays.asList("1", "2", "3"), spec.positionalParameters().get(0).getValue());
    }

    @Test
    public void testMultiValueOptionWithMapAndAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(Map.class).auxiliaryTypes(Integer.class, Double.class).build();
        Assert.assertTrue(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "1=1.0", "2=2.0", "3=3.0");
        Map<Integer, Double> expected = new LinkedHashMap<Integer, Double>();
        expected.put(1, 1.0);
        expected.put(2, 2.0);
        expected.put(3, 3.0);
        Assert.assertEquals(expected, spec.optionsMap().get("-c").getValue());
    }

    @Test
    public void testMultiValuePositionalParamWithMapAndAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(Map.class).auxiliaryTypes(Integer.class, Double.class).build();
        Assert.assertTrue(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("1=1.0", "2=2.0", "3=3.0");
        Map<Integer, Double> expected = new LinkedHashMap<Integer, Double>();
        expected.put(1, 1.0);
        expected.put(2, 2.0);
        expected.put(3, 3.0);
        Assert.assertEquals(expected, spec.positionalParameters().get(0).getValue());
    }

    @Test
    public void testMultiValueOptionWithMapWithoutAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        OptionSpec option = OptionSpec.builder("-c", "--count").arity("3").type(Map.class).build();
        Assert.assertTrue(option.isMultiValue());
        spec.addOption(option);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "1=1.0", "2=2.0", "3=3.0");
        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put("1", "1.0");
        expected.put("2", "2.0");
        expected.put("3", "3.0");
        Assert.assertEquals(expected, spec.optionsMap().get("-c").getValue());
    }

    @Test
    public void testMultiValuePositionalParamWithMapWithoutAuxTypes() {
        CommandSpec spec = CommandSpec.create();
        PositionalParamSpec positional = PositionalParamSpec.builder().index("0").arity("3").type(Map.class).build();
        Assert.assertTrue(positional.isMultiValue());
        spec.addPositional(positional);
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("1=1.0", "2=2.0", "3=3.0");
        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put("1", "1.0");
        expected.put("2", "2.0");
        expected.put("3", "3.0");
        Assert.assertEquals(expected, spec.positionalParameters().get(0).getValue());
    }

    @Test
    public void testOptionConvertersOverridesRegisteredTypeConverter() {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder("-c", "--count").paramLabel("COUNT").arity("1").type(int.class).description("number of times to execute").build());
        spec.addOption(OptionSpec.builder("-s", "--sql").paramLabel("SQLTYPE").type(int.class).converters(new CommandLineTypeConversionTest.SqlTypeConverter()).description("sql type converter").build());
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("-c", "33", "-s", "BLOB");
        Assert.assertEquals(Integer.valueOf(33), spec.optionsMap().get("-c").getValue());
        Assert.assertEquals(Integer.valueOf(Types.BLOB), spec.optionsMap().get("-s").getValue());
    }

    @Test
    public void testPositionalConvertersOverridesRegisteredTypeConverter() {
        CommandSpec spec = CommandSpec.create();
        spec.addPositional(PositionalParamSpec.builder().paramLabel("COUNT").index("0").type(int.class).description("number of times to execute").build());
        spec.addPositional(PositionalParamSpec.builder().paramLabel("SQLTYPE").index("1").type(int.class).converters(new CommandLineTypeConversionTest.SqlTypeConverter()).description("sql type converter").build());
        CommandLine commandLine = new CommandLine(spec);
        commandLine.parse("33", "BLOB");
        Assert.assertEquals(Integer.valueOf(33), spec.positionalParameters().get(0).getValue());
        Assert.assertEquals(Integer.valueOf(Types.BLOB), spec.positionalParameters().get(1).getValue());
    }

    @Test
    public void testConversion_TODO() {
        // TODO convertion with aux types (abstract field types, generic map with and without explicit type attribute etc)
    }

    @Test
    public void testTypedValues() {
        class App {
            @Option(names = "-x")
            int x;
        }
        ParseResult result1 = new CommandLine(new App()).parseArgs();// not specified

        Assert.assertFalse(result1.hasMatchedOption('x'));
        Assert.assertTrue(result1.commandSpec().findOption('x').typedValues().isEmpty());
        ParseResult result2 = new CommandLine(new App()).parseArgs("-x", "123");
        Assert.assertTrue(result2.hasMatchedOption('x'));
        Assert.assertEquals(Integer.valueOf(123), result2.matchedOptionValue('x', 0));
        ParseResult result3 = new CommandLine(new App()).setOverwrittenOptionsAllowed(true).parseArgs("-x", "1", "-x", "2", "-x", "3");
        Assert.assertTrue(result3.hasMatchedOption('x'));
        Assert.assertEquals(Integer.valueOf(3), result3.matchedOptionValue('x', 0));
        Assert.assertEquals(Arrays.asList("1", "2", "3"), result3.matchedOption('x').stringValues());
        Assert.assertEquals(Arrays.asList(1, 2, 3), result3.matchedOption('x').typedValues());
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
        List<CommandLine> parsed1 = new CommandLine(new Sample()).parse();// not specified

        OptionSpec option1 = parsed1.get(0).getCommandSpec().optionsMap().get("--foo");
        Assert.assertNull("optional option is null when option not specified", option1.getValue());
        Assert.assertTrue("optional option has no string value when option not specified", option1.stringValues().isEmpty());
        Assert.assertTrue("optional option has no typed value when option not specified", option1.typedValues().isEmpty());
        List<CommandLine> parsed2 = new CommandLine(new Sample()).parse("--foo");// specified without value

        OptionSpec option2 = parsed2.get(0).getCommandSpec().optionsMap().get("--foo");
        Assert.assertEquals("optional option is empty string when specified without args", "", option2.getValue());
        Assert.assertEquals("optional option string value when specified without args", "", option2.stringValues().get(0));
        Assert.assertEquals("optional option typed value when specified without args", "", option2.typedValues().get(0));
        List<CommandLine> parsed3 = new CommandLine(new Sample()).parse("--foo", "value");// specified with value

        OptionSpec option3 = parsed3.get(0).getCommandSpec().optionsMap().get("--foo");
        Assert.assertEquals("optional option is empty string when specified without args", "value", option3.getValue());
        Assert.assertEquals("optional option string value when specified without args", "value", option3.stringValues().get(0));
        Assert.assertEquals("optional option typed value when specified without args", "value", option3.typedValues().get(0));
    }

    /**
     * see <a href="https://github.com/remkop/picocli/issues/279">issue #279</a>
     */
    @Test
    public void testSingleValueFieldWithOptionalParameterFollowedByOption_279() {
        @Command(name = "sample")
        class Sample {
            @Option(names = "-x")
            boolean x;

            @Option(names = "--foo", arity = "0..1")
            String foo;
        }
        Sample sample = new Sample();
        List<CommandLine> parsed3 = new CommandLine(sample).parse("--foo", "-x");// specified without value

        OptionSpec option3 = parsed3.get(0).getCommandSpec().optionsMap().get("--foo");
        Assert.assertEquals("optional option is empty string when specified without args", "", option3.getValue());
        Assert.assertEquals("optional option string value when specified without args", "", option3.stringValues().get(0));
        Assert.assertEquals("optional option typed value when specified without args", "", option3.typedValues().get(0));
        Assert.assertEquals("", sample.foo);
        Assert.assertEquals(true, sample.x);
    }

    @Test
    public void testMixinStandardHelpOptions_FalseByDefault() {
        CommandSpec spec = CommandSpec.create();
        Assert.assertFalse(spec.mixinStandardHelpOptions());
    }

    @Test
    public void testMixinStandardHelpOptions_SettingToTrueAddsHelpOptions() {
        CommandSpec spec = CommandSpec.create();
        Assert.assertTrue(spec.mixins().isEmpty());
        Assert.assertTrue(spec.optionsMap().isEmpty());
        Assert.assertTrue(spec.posixOptionsMap().isEmpty());
        Assert.assertTrue(spec.options().isEmpty());
        spec.mixinStandardHelpOptions(true);
        Assert.assertFalse(spec.mixins().isEmpty());
        Assert.assertFalse(spec.optionsMap().isEmpty());
        Assert.assertFalse(spec.posixOptionsMap().isEmpty());
        Assert.assertFalse(spec.options().isEmpty());
        Assert.assertTrue(spec.mixinStandardHelpOptions());
        OptionSpec usageHelp = spec.posixOptionsMap().get('h');
        Assert.assertSame(usageHelp, spec.optionsMap().get("--help"));
        Assert.assertTrue(usageHelp.usageHelp());
        OptionSpec versionHelp = spec.posixOptionsMap().get('V');
        Assert.assertSame(versionHelp, spec.optionsMap().get("--version"));
        Assert.assertTrue(versionHelp.versionHelp());
    }

    @Test
    public void testMixinStandardHelpOptions_SettingToFalseRemovesHelpOptions() {
        CommandSpec spec = CommandSpec.create();
        spec.mixinStandardHelpOptions(true);
        Assert.assertFalse(spec.mixins().isEmpty());
        Assert.assertFalse(spec.optionsMap().isEmpty());
        Assert.assertFalse(spec.posixOptionsMap().isEmpty());
        Assert.assertFalse(spec.options().isEmpty());
        Assert.assertTrue(spec.mixinStandardHelpOptions());
        Assert.assertNotNull(spec.posixOptionsMap().get('h'));
        Assert.assertNotNull(spec.optionsMap().get("--help"));
        Assert.assertNotNull(spec.posixOptionsMap().get('V'));
        Assert.assertNotNull(spec.optionsMap().get("--version"));
        spec.mixinStandardHelpOptions(false);
        Assert.assertTrue(spec.mixins().isEmpty());
        Assert.assertTrue(spec.optionsMap().isEmpty());
        Assert.assertTrue(spec.posixOptionsMap().isEmpty());
        Assert.assertTrue(spec.options().isEmpty());
        Assert.assertFalse(spec.mixinStandardHelpOptions());
    }

    @Test
    public void testCommandSpec_forAnnotatedObject_requiresPicocliAnnotation() {
        try {
            CommandSpec.forAnnotatedObject(new Object());
            Assert.fail("Expected error");
        } catch (InitializationException ok) {
            Assert.assertEquals("java.lang.Object is not a command: it has no @Command, @Option, @Parameters or @Unmatched annotations", ok.getMessage());
        }
    }

    @Test
    public void testCommandSpec_forAnnotatedObjectLenient_doesNotRequirePicocliAnnotation() {
        CommandSpec.forAnnotatedObjectLenient(new Object());// no error

    }

    @Test
    public void testCommandSpec_forAnnotatedObjectLenient_returnsEmptyCommandSpec() {
        CommandSpec spec = CommandSpec.forAnnotatedObjectLenient(new Object());
        Assert.assertTrue(spec.optionsMap().isEmpty());
        Assert.assertTrue(spec.posixOptionsMap().isEmpty());
        Assert.assertTrue(spec.options().isEmpty());
        Assert.assertTrue(spec.positionalParameters().isEmpty());
        Assert.assertTrue(spec.unmatchedArgsBindings().isEmpty());
        Assert.assertTrue(spec.subcommands().isEmpty());
        Assert.assertTrue(spec.mixins().isEmpty());
        Assert.assertTrue(spec.requiredArgs().isEmpty());
        Assert.assertFalse(spec.mixinStandardHelpOptions());
        Assert.assertFalse(spec.helpCommand());
        Assert.assertEquals("<main class>", spec.name());
        Assert.assertArrayEquals(new String[0], spec.version());
        Assert.assertNull(spec.versionProvider());
    }

    @Test
    public void testOptionSpec_setsDefaultValue_ifNotMatched() {
        CommandSpec cmd = CommandSpec.create().addOption(OptionSpec.builder("-x").defaultValue("123").type(int.class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs();
        Assert.assertFalse(parseResult.hasMatchedOption('x'));
        // TODO this method should be renamed to matchedOptionValue
        Assert.assertEquals(Integer.valueOf((-1)), parseResult.matchedOptionValue('x', (-1)));
        // TODO optionValue should return the value of the option, matched or not
        // assertEquals(Integer.valueOf(123), parseResult.optionValue('x'));
        Assert.assertEquals(Integer.valueOf(123), parseResult.commandSpec().findOption('x').getValue());
    }

    @Test
    public void testPositionalParamSpec_setsDefaultValue_ifNotMatched() {
        CommandSpec cmd = CommandSpec.create().add(PositionalParamSpec.builder().defaultValue("123").type(int.class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs();
        Assert.assertFalse(parseResult.hasMatchedPositional(0));
        // TODO this method should be renamed to matchedPositionalValue
        Assert.assertEquals(Integer.valueOf((-1)), parseResult.matchedPositionalValue(0, (-1)));
        // TODO positionalValue should return the value of the option, matched or not
        // assertEquals(Integer.valueOf(123), parseResult.positionalValue(0));
        Assert.assertEquals(Integer.valueOf(123), parseResult.commandSpec().positionalParameters().get(0).getValue());
    }

    @Test
    public void testOptionSpec_defaultValue_overwritesInitialValue() {
        class Params {
            @Option(names = "-x")
            int num = 12345;
        }
        CommandLine cmd = new CommandLine(new Params());
        OptionSpec x = cmd.getCommandSpec().posixOptionsMap().get('x').toBuilder().defaultValue("54321").build();
        cmd = new CommandLine(CommandSpec.create().addOption(x));
        ParseResult parseResult = cmd.parseArgs();
        Assert.assertFalse(parseResult.hasMatchedOption('x'));
        // TODO this method should be renamed to matchedOptionValue
        Assert.assertEquals(Integer.valueOf((-1)), parseResult.matchedOptionValue('x', (-1)));
        // TODO optionValue should return the value of the option, matched or not
        // assertEquals(Integer.valueOf(54321), parseResult.optionValue('x'));
        Assert.assertEquals(Integer.valueOf(54321), parseResult.commandSpec().findOption('x').getValue());
    }

    @Test
    public void testPositionalParamSpec_defaultValue_overwritesInitialValue() {
        class Params {
            @Parameters
            int num = 12345;
        }
        CommandLine cmd = new CommandLine(new Params());
        PositionalParamSpec x = cmd.getCommandSpec().positionalParameters().get(0).toBuilder().defaultValue("54321").build();
        cmd = new CommandLine(CommandSpec.create().add(x));
        ParseResult parseResult = cmd.parseArgs();
        // default not in the parse result
        Assert.assertFalse(parseResult.hasMatchedPositional(0));
        Assert.assertEquals(Integer.valueOf((-1)), parseResult.matchedPositionalValue(0, (-1)));
        // but positional spec does have the default value
        Assert.assertEquals(Integer.valueOf(54321), parseResult.commandSpec().positionalParameters().get(0).getValue());
    }

    @Test
    public void testOptionSpec_notRequiredIfNonNullDefaultValue() {
        Assert.assertTrue(OptionSpec.builder("-x").required(true).build().required());
        Assert.assertFalse(OptionSpec.builder("-x").defaultValue("123").required(true).build().required());
    }

    @Test
    public void testPositionalParamSpec_notRequiredIfNonNullDefaultValue() {
        Assert.assertTrue(PositionalParamSpec.builder().required(true).build().required());
        Assert.assertFalse(PositionalParamSpec.builder().defaultValue("123").required(true).build().required());
    }

    @Test
    public void testOptionSpec_DefaultValue_single_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().addOption(OptionSpec.builder("-x").defaultValue("123").type(int.class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs("-x", "456");
        Assert.assertEquals(Integer.valueOf(456), parseResult.matchedOptionValue('x', (-1)));
    }

    @Test
    public void testPositionalParamSpec_DefaultValue_single_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().add(PositionalParamSpec.builder().defaultValue("123").type(int.class).build());
        ParseResult parseResult = parseArgs("456");
        Assert.assertEquals(Integer.valueOf(456), parseResult.matchedPositionalValue(0, (-1)));
    }

    @Test
    public void testOptionSpec_DefaultValue_array_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().addOption(OptionSpec.builder("-x").defaultValue("1,2,3").splitRegex(",").type(int[].class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs("-x", "4,5,6");
        Assert.assertArrayEquals(new int[]{ 4, 5, 6 }, parseResult.matchedOptionValue('x', new int[0]));
    }

    @Test
    public void testOptionSpec_DefaultValue_list_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().addOption(OptionSpec.builder("-x").defaultValue("1,2,3").splitRegex(",").type(List.class).auxiliaryTypes(Integer.class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs("-x", "4,5,6");
        Assert.assertEquals(Arrays.asList(4, 5, 6), parseResult.matchedOptionValue('x', Collections.emptyList()));
    }

    @Test
    public void testOptionSpec_DefaultValue_map_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-x").defaultValue("1=A,2=B,3=C").splitRegex(",").type(Map.class).auxiliaryTypes(Integer.class, String.class).build());
        ParseResult parseResult = new CommandLine(cmd).parseArgs("-x", "4=X,5=Y,6=Z");
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(4, "X");
        expected.put(5, "Y");
        expected.put(6, "Z");
        Assert.assertEquals(expected, parseResult.matchedOptionValue('x', Collections.emptyMap()));
    }

    @Test
    public void testPositionalParamSpec_DefaultValue_array_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().add(PositionalParamSpec.builder().defaultValue("1,2,3").splitRegex(",").type(int[].class).build());
        ParseResult parseResult = parseArgs("4,5,6");
        Assert.assertArrayEquals(new int[]{ 4, 5, 6 }, parseResult.matchedPositionalValue(0, new int[0]));
    }

    @Test
    public void testPositionalParamSpec_DefaultValue_list_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().add(PositionalParamSpec.builder().defaultValue("1,2,3").splitRegex(",").type(List.class).auxiliaryTypes(Integer.class).build());
        ParseResult parseResult = parseArgs("4,5,6");
        Assert.assertEquals(Arrays.asList(4, 5, 6), parseResult.matchedPositionalValue(0, Collections.emptyList()));
    }

    @Test
    public void testPositionalParamSpec_DefaultValue_map_replacedByCommandLineValue() {
        CommandSpec cmd = CommandSpec.create().add(PositionalParamSpec.builder().defaultValue("1=A,2=B,3=C").splitRegex(",").type(Map.class).auxiliaryTypes(Integer.class, String.class).build());
        ParseResult parseResult = parseArgs("4=X,5=Y,6=Z");
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(4, "X");
        expected.put(5, "Y");
        expected.put(6, "Z");
        Assert.assertEquals(expected, parseResult.matchedPositionalValue(0, Collections.emptyMap()));
    }

    @Test
    public void testMultipleUsageHelpOptions() {
        HelpTestUtil.setTraceLevel("WARN");
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-x").type(boolean.class).usageHelp(true).build()).add(OptionSpec.builder("-h").type(boolean.class).usageHelp(true).build());
        Assert.assertEquals("", systemErrRule.getLog());
        systemErrRule.clearLog();
        new CommandLine(cmd);
        Assert.assertEquals("", systemOutRule.getLog());
        Assert.assertEquals(String.format("[picocli WARN] Multiple options [-x, -h] are marked as 'usageHelp=true'. Usually a command has only one --help option that triggers display of the usage help message. Alternatively, consider using @Command(mixinStandardHelpOptions = true) on your command instead.%n"), systemErrRule.getLog());
    }

    @Test
    public void testMultipleVersionHelpOptions() {
        HelpTestUtil.setTraceLevel("WARN");
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-x").type(boolean.class).versionHelp(true).build()).add(OptionSpec.builder("-V").type(boolean.class).versionHelp(true).build());
        Assert.assertEquals("", systemErrRule.getLog());
        systemErrRule.clearLog();
        new CommandLine(cmd);
        Assert.assertEquals("", systemOutRule.getLog());
        Assert.assertEquals(String.format("[picocli WARN] Multiple options [-x, -V] are marked as 'versionHelp=true'. Usually a command has only one --version option that triggers display of the version information. Alternatively, consider using @Command(mixinStandardHelpOptions = true) on your command instead.%n"), systemErrRule.getLog());
    }

    @Test
    public void testNonBooleanUsageHelpOptions() {
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-z").type(int.class).usageHelp(true).build());
        try {
            new CommandLine(cmd);
        } catch (InitializationException ex) {
            Assert.assertEquals("Non-boolean options like [-z] should not be marked as 'usageHelp=true'. Usually a command has one --help boolean flag that triggers display of the usage help message. Alternatively, consider using @Command(mixinStandardHelpOptions = true) on your command instead.", ex.getMessage());
        }
    }

    @Test
    public void testNonBooleanVersionHelpOptions() {
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-x").type(int.class).versionHelp(true).build());
        try {
            new CommandLine(cmd);
        } catch (InitializationException ex) {
            Assert.assertEquals("Non-boolean options like [-x] should not be marked as 'versionHelp=true'. Usually a command has one --version boolean flag that triggers display of the version information. Alternatively, consider using @Command(mixinStandardHelpOptions = true) on your command instead.", ex.getMessage());
        }
    }

    @Test
    public void testBooleanObjectUsageHelpOptions() {
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-z").type(Boolean.class).usageHelp(true).build());
        Assert.assertTrue(new CommandLine(cmd).parseArgs("-z").isUsageHelpRequested());
    }

    @Test
    public void testBooleanObjectVersionHelpOptions() {
        CommandSpec cmd = CommandSpec.create().add(OptionSpec.builder("-x").type(Boolean.class).versionHelp(true).build());
        Assert.assertTrue(new CommandLine(cmd).parseArgs("-x").isVersionHelpRequested());
    }

    @Test
    public void testParseResetsRawAndOriginalStringValues() {
        CommandSpec spec = CommandSpec.create().addOption(OptionSpec.builder("-x").type(String.class).build()).addPositional(PositionalParamSpec.builder().build());
        CommandLine cmd = new CommandLine(spec);
        ParseResult parseResult = cmd.parseArgs("-x", "XVAL", "POSITIONAL");
        Assert.assertEquals("XVAL", parseResult.matchedOption('x').getValue());
        Assert.assertEquals(Arrays.asList("XVAL"), parseResult.matchedOption('x').stringValues());
        Assert.assertEquals(Arrays.asList("XVAL"), parseResult.matchedOption('x').originalStringValues());
        Assert.assertEquals("POSITIONAL", parseResult.matchedPositional(0).getValue());
        Assert.assertEquals(Arrays.asList("POSITIONAL"), parseResult.matchedPositional(0).stringValues());
        Assert.assertEquals(Arrays.asList("POSITIONAL"), parseResult.matchedPositional(0).originalStringValues());
        ParseResult parseResult2 = cmd.parseArgs("-x", "222", "$$$$");
        Assert.assertEquals("222", parseResult2.matchedOption('x').getValue());
        Assert.assertEquals(Arrays.asList("222"), parseResult2.matchedOption('x').stringValues());
        Assert.assertEquals(Arrays.asList("222"), parseResult2.matchedOption('x').originalStringValues());
        Assert.assertEquals("$$$$", parseResult2.matchedPositional(0).getValue());
        Assert.assertEquals(Arrays.asList("$$$$"), parseResult2.matchedPositional(0).stringValues());
        Assert.assertEquals(Arrays.asList("$$$$"), parseResult2.matchedPositional(0).originalStringValues());
    }

    @Test
    public void testInitializingDefaultsShouldNotAddOptionToParseResult() {
        CommandSpec spec = CommandSpec.create().addOption(OptionSpec.builder("-x").type(String.class).defaultValue("xyz").build());
        CommandLine cmd = new CommandLine(spec);
        ParseResult parseResult = cmd.parseArgs();
        Assert.assertFalse(parseResult.hasMatchedOption('x'));
    }

    @Test
    public void testInitializingDefaultsShouldNotAddPositionalToParseResult() {
        CommandSpec spec = CommandSpec.create().addPositional(PositionalParamSpec.builder().defaultValue("xyz").build());
        CommandLine cmd = new CommandLine(spec);
        ParseResult parseResult = cmd.parseArgs();
        Assert.assertFalse(parseResult.hasMatchedPositional(0));
    }

    @Test
    public void testOptionLongestName_oneName() {
        Assert.assertEquals("-x", OptionSpec.builder("-x").build().longestName());
    }

    @Test
    public void testOptionLongestName_multipleEqualLength_returnsFirst() {
        Assert.assertEquals("-x", OptionSpec.builder("-x", "-a").build().longestName());
    }

    @Test
    public void testOptionLongestName_returnsLongest() {
        Assert.assertEquals("-xxx", OptionSpec.builder("-x", "-xx", "-xxx").build().longestName());
        Assert.assertEquals("-aaa", OptionSpec.builder("-x", "-xx", "-aaa").build().longestName());
        Assert.assertEquals("-abcd", OptionSpec.builder("-x", "-abcd", "-aaa").build().longestName());
    }

    @Test
    public void testClearArrayOptionOldValueBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").arity("2..3").initialValue(new String[]{ "ABC" }).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1", "2", "3");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, ((String[]) (cmd.findOption("x").getValue())));
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, ((String[]) (cmd.findOption('x').getValue())));
        cl.parseArgs("-x", "4", "5");
        Assert.assertArrayEquals(new String[]{ "4", "5" }, ((String[]) (cmd.findOption("x").getValue())));
        Assert.assertArrayEquals(new String[]{ "4", "5" }, ((String[]) (cmd.findOption('x').getValue())));
        cl.parseArgs();
        Assert.assertArrayEquals(new String[]{ "ABC" }, ((String[]) (cmd.findOption("x").getValue())));
        Assert.assertArrayEquals(new String[]{ "ABC" }, ((String[]) (cmd.findOption('x').getValue())));
    }

    @Test
    public void testDontClearArrayOptionOldValueBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").arity("2..3").initialValue(new String[]{ "ABC" }).hasInitialValue(false).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1", "2", "3");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, ((String[]) (cmd.findOption("x").getValue())));
        cl.parseArgs("-x", "4", "5");
        Assert.assertArrayEquals(new String[]{ "4", "5" }, ((String[]) (cmd.findOption("x").getValue())));
        cl.parseArgs();
        Assert.assertArrayEquals(new String[]{ "4", "5" }, ((String[]) (cmd.findOption("x").getValue())));
    }

    @Test
    public void testClearListOptionOldValueBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").type(List.class).initialValue(Arrays.asList("ABC")).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1", "-x", "2", "-x", "3");
        Assert.assertEquals(Arrays.asList("1", "2", "3"), cmd.findOption("x").getValue());
        cl.parseArgs("-x", "4", "-x", "5");
        Assert.assertEquals(Arrays.asList("4", "5"), cmd.findOption("x").getValue());
        cl.parseArgs();
        Assert.assertEquals(Arrays.asList("ABC"), cmd.findOption("x").getValue());
    }

    @Test
    public void testDontClearListOptionOldValueBeforeParseIfInitialValueFalse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").type(List.class).initialValue(Arrays.asList("ABC")).hasInitialValue(false).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1", "-x", "2", "-x", "3");
        Assert.assertEquals(Arrays.asList("1", "2", "3"), cmd.findOption("x").getValue());
        cl.parseArgs("-x", "4", "-x", "5");
        Assert.assertEquals(Arrays.asList("4", "5"), cmd.findOption("x").getValue());
        cl.parseArgs();
        Assert.assertEquals(Arrays.asList("4", "5"), cmd.findOption("x").getValue());
    }

    @Test
    public void testClearMapOptionOldValueBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        Map<String, String> map = new HashMap<String, String>();
        map.put("ABC", "XYZ");
        cmd.addOption(OptionSpec.builder("-x").type(Map.class).initialValue(map).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "A=1", "-x", "B=2", "-x", "C=3");
        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put("A", "1");
        expected.put("B", "2");
        expected.put("C", "3");
        Assert.assertEquals(expected, cmd.findOption("x").getValue());
        cl.parseArgs("-x", "D=4", "-x", "E=5");
        expected = new LinkedHashMap<String, String>();
        expected.put("D", "4");
        expected.put("E", "5");
        Assert.assertEquals(expected, cmd.findOption("x").getValue());
        cl.parseArgs();
        expected = new LinkedHashMap<String, String>();
        Assert.assertEquals(map, cmd.findOption("x").getValue());
    }

    @Test
    public void testDontClearInitialValueBeforeParseIfInitialValueFalse() {
        CommandSpec cmd = CommandSpec.create();
        Map<String, String> map = new HashMap<String, String>();
        map.put("ABC", "XYZ");
        cmd.addOption(OptionSpec.builder("-x").type(Map.class).initialValue(map).hasInitialValue(true).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "A=1", "-x", "B=2", "-x", "C=3");
        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put("A", "1");
        expected.put("B", "2");
        expected.put("C", "3");
        Assert.assertEquals(expected, cmd.findOption("x").getValue());
        cl.parseArgs("-x", "D=4", "-x", "E=5");
        expected = new LinkedHashMap<String, String>();
        expected.put("D", "4");
        expected.put("E", "5");
        Assert.assertEquals(expected, cmd.findOption("x").getValue());
        cl.parseArgs();
        expected = new LinkedHashMap<String, String>();
        expected.put("ABC", "XYZ");
        Assert.assertEquals(expected, cmd.findOption("x").getValue());
    }

    @Test
    public void testClearScalarOptionOldValueBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").type(String.class).initialValue(null).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1");
        Assert.assertEquals("1", cmd.findOption("x").getValue());
        cl.parseArgs("-x", "2");
        Assert.assertEquals("2", cmd.findOption("x").getValue());
        cl.parseArgs();
        Assert.assertNull(cmd.findOption("x").getValue());
    }

    @Test
    public void testDontClearScalarOptionOldValueBeforeParseIfInitialValueFalse() {
        CommandSpec cmd = CommandSpec.create();
        cmd.addOption(OptionSpec.builder("-x").type(String.class).initialValue(null).hasInitialValue(false).build());
        CommandLine cl = new CommandLine(cmd);
        cl.parseArgs("-x", "1");
        Assert.assertEquals("1", cmd.findOption("x").getValue());
        cl.parseArgs("-x", "2");
        Assert.assertEquals("2", cmd.findOption("x").getValue());
        cl.parseArgs();
        Assert.assertEquals("2", cmd.findOption("x").getValue());
    }

    @Test
    public void testOptionClearCustomSetterBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        final List<Object> values = new ArrayList<Object>();
        ISetter setter = new ISetter() {
            public <T> T set(T value) {
                values.add(value);
                return null;
            }
        };
        cmd.addOption(OptionSpec.builder("-x").type(String.class).setter(setter).build());
        CommandLine cl = new CommandLine(cmd);
        Assert.assertTrue(values.isEmpty());
        cl.parseArgs("-x", "1");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(null, values.get(0));
        Assert.assertEquals("1", values.get(1));
        values.clear();
        cl.parseArgs("-x", "2");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(null, values.get(0));
        Assert.assertEquals("2", values.get(1));
    }

    @Test
    public void testPositionalClearCustomSetterBeforeParse() {
        CommandSpec cmd = CommandSpec.create();
        final List<Object> values = new ArrayList<Object>();
        ISetter setter = new ISetter() {
            public <T> T set(T value) {
                values.add(value);
                return null;
            }
        };
        cmd.add(PositionalParamSpec.builder().type(String.class).setter(setter).build());
        CommandLine cl = new CommandLine(cmd);
        Assert.assertTrue(values.isEmpty());
        cl.parseArgs("1");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(null, values.get(0));
        Assert.assertEquals("1", values.get(1));
        values.clear();
        cl.parseArgs("2");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(null, values.get(0));
        Assert.assertEquals("2", values.get(1));
    }

    @Test
    public void test381_NPE_whenAddingSubcommand() {
        CommandSpec toplevel = CommandSpec.create();
        toplevel.addOption(OptionSpec.builder("-o").description("o option").build());
        CommandSpec sub = CommandSpec.create();
        sub.addOption(OptionSpec.builder("-x").description("x option").build());
        CommandLine commandLine = new CommandLine(toplevel);
        commandLine.addSubcommand("sub", sub);// NPE here

        commandLine.usage(System.out);
        String expected = String.format(("" + ((("Usage: <main class> [-o] [COMMAND]%n" + "  -o     o option%n") + "Commands:%n") + "  sub%n")));
        Assert.assertEquals(expected, systemOutRule.getLog());
    }

    @Test
    public void testSubcommandNameIsInitializedWhenAddedToParent() {
        CommandSpec toplevel = CommandSpec.create();
        toplevel.addOption(OptionSpec.builder("-o").description("o option").build());
        CommandSpec sub = CommandSpec.create();
        sub.addOption(OptionSpec.builder("-x").description("x option").build());
        CommandLine commandLine = new CommandLine(toplevel);
        CommandLine subCommandLine = new CommandLine(sub);
        Assert.assertEquals("<main class>", sub.name());
        Assert.assertEquals("<main class>", subCommandLine.getCommandName());
        commandLine.addSubcommand("sub", subCommandLine);
        Assert.assertEquals("sub", sub.name());
        Assert.assertEquals("sub", subCommandLine.getCommandName());
        subCommandLine.usage(System.out);
        String expected = String.format(("" + ("Usage: <main class> sub [-x]%n" + "  -x     x option%n")));
        Assert.assertEquals(expected, systemOutRule.getLog());
    }

    @Test
    public void testSubcommandNameNotOverwrittenWhenAddedToParent() {
        CommandSpec toplevel = CommandSpec.create();
        toplevel.addOption(OptionSpec.builder("-o").description("o option").build());
        CommandSpec sub = CommandSpec.create().name("SOMECOMMAND");
        sub.addOption(OptionSpec.builder("-x").description("x option").build());
        CommandLine commandLine = new CommandLine(toplevel);
        CommandLine subCommandLine = new CommandLine(sub);
        Assert.assertEquals("SOMECOMMAND", sub.name());
        Assert.assertEquals("SOMECOMMAND", subCommandLine.getCommandName());
        commandLine.addSubcommand("sub", subCommandLine);
        Assert.assertEquals("SOMECOMMAND", sub.name());
        Assert.assertEquals("SOMECOMMAND", subCommandLine.getCommandName());
        subCommandLine.usage(System.out);
        String expected = String.format(("" + ("Usage: <main class> SOMECOMMAND [-x]%n" + "  -x     x option%n")));
        Assert.assertEquals(expected, systemOutRule.getLog());
    }

    @Test
    public void testInject_AnnotatedFieldInjected() {
        class Injected {
            @Spec
            CommandSpec commandSpec;

            @Parameters
            String[] params;
        }
        Injected injected = new Injected();
        Assert.assertNull(injected.commandSpec);
        CommandLine cmd = new CommandLine(injected);
        Assert.assertSame(cmd.getCommandSpec(), injected.commandSpec);
    }

    @Test
    public void testInject_AnnotatedFieldInjectedForSubcommand() {
        class Injected {
            @Spec
            CommandSpec commandSpec;

            @Parameters
            String[] params;
        }
        Injected injected = new Injected();
        Injected sub = new Injected();
        Assert.assertNull(injected.commandSpec);
        Assert.assertNull(sub.commandSpec);
        CommandLine cmd = new CommandLine(injected);
        Assert.assertSame(cmd.getCommandSpec(), injected.commandSpec);
        CommandLine subcommand = new CommandLine(sub);
        Assert.assertSame(subcommand.getCommandSpec(), sub.commandSpec);
    }

    @Test
    public void testInject_FieldMustBeCommandSpec() {
        class Injected {
            @Spec
            CommandLine commandLine;

            @Parameters
            String[] params;
        }
        Injected injected = new Injected();
        try {
            new CommandLine(injected);
            Assert.fail("Expect exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("@picocli.CommandLine.Spec annotation is only supported on fields of type picocli.CommandLine$Model$CommandSpec", ex.getMessage());
        }
    }

    @Test
    public void testCommandSpecQualifiedName_topLevelCommand() {
        CommandLine cmd = new CommandLine(new I18nCommand());
        Assert.assertEquals("i18n-top", cmd.getCommandSpec().qualifiedName("."));
        Map<String, CommandLine> subcommands = cmd.getSubcommands();
        Assert.assertEquals("i18n-top.help", subcommands.get("help").getCommandSpec().qualifiedName("."));
        CommandLine sub = subcommands.get("i18n-sub");
        Assert.assertEquals("i18n-top.i18n-sub", sub.getCommandSpec().qualifiedName("."));
        Assert.assertEquals("i18n-top.i18n-sub.help", sub.getSubcommands().get("help").getCommandSpec().qualifiedName("."));
    }

    @Test
    public void testCommandSpecParserSetter() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        ParserSpec old = spec.parser();
        Assert.assertSame(old, spec.parser());
        Assert.assertFalse(spec.parser().collectErrors());
        Assert.assertFalse(spec.parser().caseInsensitiveEnumValuesAllowed());
        ParserSpec update = new ParserSpec().collectErrors(true).caseInsensitiveEnumValuesAllowed(true);
        spec.parser(update);
        Assert.assertSame(old, spec.parser());
        Assert.assertTrue(spec.parser().collectErrors());
        Assert.assertTrue(spec.parser().caseInsensitiveEnumValuesAllowed());
    }

    @Test
    public void testCommandSpecUsageMessageSetter() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        UsageMessageSpec old = spec.usageMessage();
        Assert.assertSame(old, spec.usageMessage());
        Assert.assertArrayEquals(new String[0], spec.usageMessage().description());
        UsageMessageSpec update = new UsageMessageSpec().description("hi");
        spec.usageMessage(update);
        Assert.assertSame(old, spec.usageMessage());
        Assert.assertArrayEquals(new String[]{ "hi" }, spec.usageMessage().description());
    }

    @Test
    public void testCommandSpecAddSubcommand_DisallowsDuplicateSubcommandNames() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        CommandSpec sub = CommandSpec.wrapWithoutInspection(null);
        spec.addSubcommand("a", new CommandLine(sub));
        try {
            spec.addSubcommand("a", new CommandLine(sub));
        } catch (InitializationException ex) {
            Assert.assertEquals("Another subcommand named 'a' already exists for command '<main class>'", ex.getMessage());
        }
    }

    @Test
    public void testCommandSpecAddSubcommand_DisallowsDuplicateSubcommandAliases() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        CommandSpec sub = CommandSpec.wrapWithoutInspection(null);
        spec.addSubcommand("a", new CommandLine(sub));
        CommandSpec sub2 = CommandSpec.wrapWithoutInspection(null);
        sub2.aliases("a");
        try {
            spec.addSubcommand("x", new CommandLine(sub2));
        } catch (InitializationException ex) {
            Assert.assertEquals("Alias 'a' for subcommand 'x' is already used by another subcommand of '<main class>'", ex.getMessage());
        }
    }

    @Test
    public void testCommandSpecAddSubcommand_SubcommandInheritsResourceBundle() {
        ResourceBundle rb = ResourceBundle.getBundle("picocli.SharedMessages");
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.resourceBundle(rb);
        Assert.assertSame(rb, spec.resourceBundle());
        CommandSpec sub = CommandSpec.wrapWithoutInspection(null);
        spec.addSubcommand("a", new CommandLine(sub));
        Assert.assertSame(rb, sub.resourceBundle());
    }

    @Test
    public void testAliasesWithEmptyArray() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        Assert.assertArrayEquals(new String[0], spec.aliases());
        spec.aliases(((String[]) (null)));
        Assert.assertArrayEquals(new String[0], spec.aliases());
    }

    @Test
    public void testNamesIncludesAliases() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.aliases("a", "b", "d");
        spec.name("c");
        Set<String> all = spec.names();
        Assert.assertArrayEquals(new String[]{ "c", "a", "b", "d" }, all.toArray(new String[0]));
    }

    @Test
    public void testInitHelpCommand() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        Assert.assertFalse(spec.helpCommand());
        CommandSpec mixin = CommandSpec.wrapWithoutInspection(null);
        mixin.helpCommand(true);
        spec.addMixin("helper", mixin);
        Assert.assertTrue(spec.helpCommand());
    }

    @Test
    public void testInitVersionProvider() {
        IVersionProvider versionProvider1 = new IVersionProvider() {
            public String[] getVersion() {
                return new String[0];
            }
        };
        IVersionProvider versionProvider2 = new IVersionProvider() {
            public String[] getVersion() {
                return new String[0];
            }
        };
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.versionProvider(versionProvider1);
        CommandSpec mixin = CommandSpec.wrapWithoutInspection(null);
        mixin.versionProvider(versionProvider2);
        spec.addMixin("helper", mixin);
        Assert.assertSame(versionProvider1, spec.versionProvider());
    }

    @Test
    public void testDefaultValueProvider() {
        IDefaultValueProvider provider1 = new IDefaultValueProvider() {
            public String defaultValue(ArgSpec argSpec) {
                return null;
            }
        };
        IDefaultValueProvider provider2 = new IDefaultValueProvider() {
            public String defaultValue(ArgSpec argSpec) {
                return null;
            }
        };
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.defaultValueProvider(provider1);
        CommandSpec mixin = CommandSpec.wrapWithoutInspection(null);
        mixin.defaultValueProvider(provider2);
        spec.addMixin("helper", mixin);
        Assert.assertSame(provider1, spec.defaultValueProvider());
    }

    @Test
    public void testResemblesOption_WhenUnmatchedArePositional() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.parser().unmatchedOptionsArePositionalParams(true);
        Assert.assertFalse(spec.resemblesOption("blah", null));
        System.setProperty("picocli.trace", "DEBUG");
        Tracer tracer = new Tracer();
        System.clearProperty("picocli.trace");
        Assert.assertFalse(spec.resemblesOption("blah", tracer));
        Tracer tracer2 = new Tracer();
        Assert.assertFalse(spec.resemblesOption("blah", tracer2));
    }

    @Test
    public void testResemblesOption_WithoutOptions() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.parser().unmatchedOptionsArePositionalParams(false);
        Assert.assertFalse(spec.resemblesOption("blah", null));
        System.setProperty("picocli.trace", "DEBUG");
        Tracer tracer = new Tracer();
        System.clearProperty("picocli.trace");
        Assert.assertFalse(spec.resemblesOption("blah", tracer));
        Assert.assertTrue(spec.resemblesOption("-a", tracer));
        Tracer tracer2 = new Tracer();
        Assert.assertFalse(spec.resemblesOption("blah", tracer2));
        Assert.assertTrue(spec.resemblesOption("-a", tracer));
    }

    @Test
    public void testResemblesOption_WithOptionsDash() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.addOption(OptionSpec.builder("-x").build());
        spec.parser().unmatchedOptionsArePositionalParams(false);
        Assert.assertFalse(spec.resemblesOption("blah", null));
        System.setProperty("picocli.trace", "DEBUG");
        Tracer tracer = new Tracer();
        System.clearProperty("picocli.trace");
        Assert.assertFalse(spec.resemblesOption("blah", tracer));
        Assert.assertTrue(spec.resemblesOption("-a", tracer));
        Assert.assertFalse(spec.resemblesOption("/a", tracer));
        Tracer tracer2 = new Tracer();
        Assert.assertFalse(spec.resemblesOption("blah", tracer2));
        Assert.assertTrue(spec.resemblesOption("-a", tracer));
        Assert.assertFalse(spec.resemblesOption("/a", tracer));
    }

    @Test
    public void testResemblesOption_WithOptionsNonDash() {
        CommandSpec spec = CommandSpec.wrapWithoutInspection(null);
        spec.addOption(OptionSpec.builder("/x").build());
        spec.parser().unmatchedOptionsArePositionalParams(false);
        Assert.assertFalse(spec.resemblesOption("blah", null));
        System.setProperty("picocli.trace", "DEBUG");
        Tracer tracer = new Tracer();
        System.clearProperty("picocli.trace");
        Assert.assertFalse(spec.resemblesOption("blah", tracer));
        Assert.assertFalse(spec.resemblesOption("-a", tracer));
        Assert.assertTrue(spec.resemblesOption("/a", tracer));
        Tracer tracer2 = new Tracer();
        Assert.assertFalse(spec.resemblesOption("blah", tracer2));
        Assert.assertFalse(spec.resemblesOption("-a", tracer));
        Assert.assertTrue(spec.resemblesOption("/a", tracer));
    }

    @Test
    public void testUsageSpec_CustomSynopsisSetter() {
        UsageMessageSpec usage = new UsageMessageSpec();
        Assert.assertArrayEquals(new String[0], usage.customSynopsis());
        usage.customSynopsis("abc", "def");
        Assert.assertArrayEquals(new String[]{ "abc", "def" }, usage.customSynopsis());
    }

    @Test
    public void testUsageSpec_HiddenSetter() {
        UsageMessageSpec usage = new UsageMessageSpec();
        Assert.assertFalse(usage.hidden());
        usage.hidden(true);
        Assert.assertTrue(usage.hidden());
    }

    @Test
    public void testUsageSpec_commandListHeading() {
        UsageMessageSpec usage = new UsageMessageSpec();
        Assert.assertEquals("Commands:%n", usage.commandListHeading());
        usage.commandListHeading("abcdef");
        Assert.assertEquals("abcdef", usage.commandListHeading());
    }

    @Test
    public void testUsageSpec_InitFromMixin() {
        UsageMessageSpec usage = new UsageMessageSpec();
        Assert.assertFalse(usage.hidden());
        UsageMessageSpec mixin = new UsageMessageSpec();
        mixin.hidden(true);
        usage.initFromMixin(mixin, null);
        Assert.assertTrue(usage.hidden());
    }

    @Test
    public void testIsAddMethodSubcommandsTrueByDefault() {
        CommandSpec spec = CommandSpec.create();
        Assert.assertTrue(spec.isAddMethodSubcommands());
    }

    @Test
    public void testIsAddMethodSubcommandsReturnsSetValue() {
        CommandSpec spec = CommandSpec.create();
        spec.setAddMethodSubcommands(false);
        Assert.assertFalse(spec.isAddMethodSubcommands());
        spec.setAddMethodSubcommands(true);
        Assert.assertTrue(spec.isAddMethodSubcommands());
        spec.setAddMethodSubcommands(null);
        Assert.assertTrue(spec.isAddMethodSubcommands());
    }
}

