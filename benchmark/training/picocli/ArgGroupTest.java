package picocli;


import ArgGroupSpec.DEFAULT_ORDER;
import CommandLine.Range;
import Help.Ansi.OFF;
import Help.Ansi.ON;
import OptionSpec.Builder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.InitializationException;
import picocli.CommandLine.MissingParameterException;
import picocli.CommandLine.Model.ArgGroupSpec;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.MutuallyExclusiveArgsException;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


public class ArgGroupTest {
    static OptionSpec OPTION = OptionSpec.builder("-x").groupNames("AAA", "BBB", "A", "B").build();

    @Test
    public void testArgSpecHaveNoGroupsByDefault() {
        Assert.assertTrue(OptionSpec.builder("-x").build().groupNames().isEmpty());
        Assert.assertTrue(PositionalParamSpec.builder().build().groupNames().isEmpty());
    }

    @Test
    public void testArgSpecBuilderHasNoGroupsByDefault() {
        Assert.assertTrue(OptionSpec.builder("-x").groupNames().isEmpty());
        Assert.assertTrue(PositionalParamSpec.builder().groupNames().isEmpty());
    }

    @Test
    public void testOptionSpecBuilderGroupNamesMutable() {
        OptionSpec.Builder builder = OptionSpec.builder("-x");
        Assert.assertTrue(builder.groupNames().isEmpty());
        builder.groupNames("AAA").build();
        Assert.assertEquals(1, builder.groupNames().size());
        Assert.assertEquals("AAA", builder.groupNames().get(0));
    }

    @Test
    public void testPositionalParamSpecBuilderGroupNamesMutable() {
        PositionalParamSpec.Builder builder = PositionalParamSpec.builder();
        Assert.assertTrue(builder.groupNames().isEmpty());
        builder.groupNames("AAA").build();
        Assert.assertEquals(1, builder.groupNames().size());
        Assert.assertEquals("AAA", builder.groupNames().get(0));
    }

    @Test
    public void testGroupSpec_builderFactoryMethodSetsName() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("AAA");
        builder.addArg(ArgGroupTest.OPTION);
        Assert.assertEquals("AAA", builder.name());
        Assert.assertEquals("AAA", builder.build().name());
    }

    @Test
    public void testGroupSpecBuilderNameMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("AAA");
        Assert.assertEquals("AAA", builder.name());
        builder.name("BBB");
        Assert.assertEquals("BBB", builder.name());
        builder.addArg(ArgGroupTest.OPTION);
        Assert.assertEquals("BBB", builder.build().name());
    }

    @Test
    public void testGroupSpecBuilderNullNameDisallowed() {
        try {
            ArgGroupSpec.builder(((String) (null)));
            Assert.fail("Expected exception");
        } catch (NullPointerException ok) {
        }
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("");// TODO empty name ok?

        try {
            builder.name(null);
            Assert.fail("Expected exception");
        } catch (NullPointerException ok) {
        }
    }

    @Test
    public void testGroupSpecBuilderNullAnnotationDisallowed() {
        try {
            ArgGroupSpec.builder(((ArgGroup) (null)));
            Assert.fail("Expected exception");
        } catch (NullPointerException ok) {
        }
    }

    @Test
    public void testGroupSpecBuilderFromAnnotationFailsIfNoOptionsOrSubgroups() {
        @Command(argGroups = @ArgGroup(name = "abc", exclusive = false, validate = false, multiplicity = "1", headingKey = "headingKeyXXX", heading = "headingXXX", order = 123))
        class App {}
        Command command = App.class.getAnnotation(Command.class);
        ArgGroup annotation = command.argGroups()[0];
        try {
            ArgGroupSpec.builder(annotation).build();
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("ArgGroup 'abc' has no options or positional parameters, and no subgroups", ex.getMessage());
        }
    }

    @Test
    public void testGroupSpecBuilderFromAnnotation() {
        @Command(argGroups = @ArgGroup(name = "abc", exclusive = false, validate = false, multiplicity = "1", headingKey = "headingKeyXXX", heading = "headingXXX", order = 123))
        class App {
            @Option(names = "-x", groups = "abc")
            int x;
        }
        CommandLine commandLine = new CommandLine(new App());
        Assert.assertEquals(1, commandLine.getCommandSpec().argGroups().size());
        ArgGroupSpec group = commandLine.getCommandSpec().argGroups().get("abc");
        Assert.assertNotNull(group);
        Assert.assertEquals("abc", group.name());
        Assert.assertEquals(false, group.exclusive());
        Assert.assertEquals(false, group.validate());
        Assert.assertEquals(Range.valueOf("1"), group.multiplicity());
        Assert.assertEquals("headingKeyXXX", group.headingKey());
        Assert.assertEquals("headingXXX", group.heading());
        Assert.assertEquals(123, group.order());
        Assert.assertTrue(group.subgroups().isEmpty());
        Assert.assertEquals(1, group.args().size());
        OptionSpec option = ((OptionSpec) (group.args().iterator().next()));
        Assert.assertEquals("-x", option.shortestName());
        Assert.assertEquals(Arrays.asList("abc"), option.groupNames());
        Assert.assertEquals(1, option.groupNames().size());
        Assert.assertEquals(Arrays.asList("abc"), option.groupNames());
    }

    @Test
    public void testGroupSpecBuilderExclusiveTrueByDefault() {
        Assert.assertTrue(ArgGroupSpec.builder("A").exclusive());
    }

    @Test
    public void testGroupSpecBuilderExclusiveMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertTrue(builder.exclusive());
        builder.exclusive(false);
        Assert.assertFalse(builder.exclusive());
    }

    @Test
    public void testGroupSpecBuilderRequiredFalseByDefault() {
        Assert.assertEquals(Range.valueOf("0..1"), ArgGroupSpec.builder("A").multiplicity());
    }

    @Test
    public void testGroupSpecBuilderRequiredMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertEquals(Range.valueOf("0..1"), builder.multiplicity());
        builder.multiplicity("1");
        Assert.assertEquals(Range.valueOf("1"), builder.multiplicity());
    }

    @Test
    public void testGroupSpecBuilderValidatesTrueByDefault() {
        Assert.assertTrue(ArgGroupSpec.builder("A").validate());
    }

    @Test
    public void testGroupSpecBuilderValidateMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertTrue(builder.validate());
        builder.validate(false);
        Assert.assertFalse(builder.validate());
    }

    @Test
    public void testGroupSpecBuilderGroupNamesEmptyByDefault() {
        Assert.assertTrue(ArgGroupSpec.builder("A").subgroupNames().isEmpty());
    }

    @Test
    public void testGroupSpecBuilderGroupNamesMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertTrue(builder.subgroupNames().isEmpty());
        builder.subgroupNames().add("B");
        builder.subgroupNames().add("C");
        Assert.assertEquals(Arrays.asList("B", "C"), builder.subgroupNames());
        builder.subgroupNames(Arrays.asList("X", "Y"));
        Assert.assertEquals(Arrays.asList("X", "Y"), builder.subgroupNames());
    }

    // TODO
    // @Test
    // public void testGroupSpecBuilderGroupsEmptyByDefault() {
    // assertTrue(ArgGroupSpec.builder("A").groups().isEmpty());
    // }
    // 
    // @Test
    // public void testGroupSpecBuilderGroupsMutable() {
    // ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
    // assertTrue(builder.groups().isEmpty());
    // builder.groups().add(ArgGroupSpec.builder("B").build());
    // builder.groups().add(ArgGroupSpec.builder("C").build());
    // assertEquals(2, builder.groups().size());
    // }
    @Test
    public void testGroupSpecBuilderOrderMinusOneByDefault() {
        Assert.assertEquals(DEFAULT_ORDER, ArgGroupSpec.builder("A").order());
    }

    @Test
    public void testGroupSpecBuilderOrderMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertEquals(DEFAULT_ORDER, builder.order());
        builder.order(34);
        Assert.assertEquals(34, builder.order());
    }

    @Test
    public void testGroupSpecBuilderHeadingNullByDefault() {
        Assert.assertNull(ArgGroupSpec.builder("A").heading());
    }

    @Test
    public void testGroupSpecBuilderHeadingMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertNull(builder.heading());
        builder.heading("This is a header");
        Assert.assertEquals("This is a header", builder.heading());
    }

    @Test
    public void testGroupSpecBuilderHeadingKeyNullByDefault() {
        Assert.assertNull(ArgGroupSpec.builder("A").headingKey());
    }

    @Test
    public void testGroupSpecBuilderHeadingKeyMutable() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        Assert.assertNull(builder.headingKey());
        builder.headingKey("KEY");
        Assert.assertEquals("KEY", builder.headingKey());
    }

    // TODO
    // @Test
    // public void testGroupSpecBuilderBuildDisallowsDuplidateGroups() {
    // ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
    // builder.groups().add(ArgGroupSpec.builder("B").build());
    // builder.groups().add(ArgGroupSpec.builder("B").heading("x").build());
    // 
    // try {
    // builder.build();
    // fail("Expected exception");
    // } catch (DuplicateNameException ex) {
    // assertEquals("Different ArgGroups should not use the same name 'B'", ex.getMessage());
    // }
    // }
    @Test
    public void testGroupSpecBuilderBuildCopiesBuilderAttributes() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        builder.addArg(ArgGroupTest.OPTION);
        ArgGroupSpec group = builder.build();
        Assert.assertEquals("A", group.name());
        Assert.assertEquals(builder.name(), group.name());
        Assert.assertTrue(group.exclusive());
        Assert.assertEquals(builder.exclusive(), group.exclusive());
        Assert.assertEquals(Range.valueOf("0..1"), group.multiplicity());
        Assert.assertEquals(builder.multiplicity(), group.multiplicity());
        Assert.assertTrue(group.validate());
        Assert.assertEquals(builder.validate(), group.validate());
        Assert.assertEquals(DEFAULT_ORDER, group.order());
        Assert.assertEquals(builder.order(), group.order());
        Assert.assertNull(group.heading());
        Assert.assertEquals(builder.heading(), group.heading());
        Assert.assertNull(group.headingKey());
        Assert.assertEquals(builder.headingKey(), group.headingKey());
        // TODO
        // assertTrue(group.groups().isEmpty());
        // assertEquals(builder.groupNames(), group.groupNames());
        Assert.assertTrue(group.subgroups().isEmpty());
        // TODO assertEquals(builder.groups().isEmpty(), group.groups().isEmpty());
    }

    @Test
    public void testGroupSpecBuilderBuildCopiesBuilderAttributesNonDefault() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        builder.heading("my heading");
        builder.headingKey("my headingKey");
        builder.order(123);
        builder.exclusive(false);
        builder.validate(false);
        builder.multiplicity("1");
        builder.addSubgroup(ArgGroupSpec.builder("B").addArg(ArgGroupTest.OPTION).subgroupNames("A").build());
        builder.addArg(ArgGroupTest.OPTION);
        ArgGroupSpec group = builder.build();
        Assert.assertEquals("A", group.name());
        Assert.assertEquals(builder.name(), group.name());
        Assert.assertFalse(group.exclusive());
        Assert.assertEquals(builder.exclusive(), group.exclusive());
        Assert.assertEquals(Range.valueOf("1"), group.multiplicity());
        Assert.assertEquals(builder.multiplicity(), group.multiplicity());
        Assert.assertFalse(group.validate());
        Assert.assertEquals(builder.validate(), group.validate());
        Assert.assertEquals(123, group.order());
        Assert.assertEquals(builder.order(), group.order());
        Assert.assertEquals("my heading", group.heading());
        Assert.assertEquals(builder.heading(), group.heading());
        Assert.assertEquals("my headingKey", group.headingKey());
        Assert.assertEquals(builder.headingKey(), group.headingKey());
        Assert.assertEquals(1, group.subgroups().size());
        Assert.assertEquals("B", group.subgroups().get("B").name());
        // TODO assertEquals(builder.groups(), new ArrayList<ArgGroupSpec>(group.groups().values()));
    }

    @Test
    public void testGroupSpecToString() {
        String expected = "ArgGroup[A, exclusive=true, multiplicity=0..1, validate=true, order=-1, args=[-x], subgroups=[], headingKey=null, heading=null]";
        Assert.assertEquals(expected, ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).build().toString());
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        builder.heading("my heading");
        builder.headingKey("my headingKey");
        builder.order(123);
        builder.exclusive(false);
        builder.validate(false);
        builder.multiplicity("1");
        builder.addSubgroup(ArgGroupSpec.builder("B").subgroupNames("A").addArg(ArgGroupTest.OPTION).build());
        builder.addArg(PositionalParamSpec.builder().index("0..1").paramLabel("FILE").groupNames("A").build());
        String expected2 = "ArgGroup[A, exclusive=false, multiplicity=1, validate=false, order=123, args=[params[0..1]=FILE], subgroups=[B], headingKey='my headingKey', heading='my heading']";
        Assert.assertEquals(expected2, builder.build().toString());
    }

    @Test
    public void testGroupSpecEquals() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        builder.addArg(ArgGroupTest.OPTION);
        ArgGroupSpec a = builder.build();
        Assert.assertEquals(a, a);
        Assert.assertNotSame(a, ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).build());
        Assert.assertEquals(a, ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).build());
        OptionSpec otherOption = OptionSpec.builder("-y").groupNames("A").build();
        Assert.assertNotEquals(a, ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).addArg(otherOption).build());
        builder.heading("my heading");
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.headingKey("my headingKey");
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.order(123);
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.exclusive(false);
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.validate(false);
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.multiplicity("1");
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
        builder.addSubgroup(ArgGroupSpec.builder("B").addArg(ArgGroupTest.OPTION).build());
        Assert.assertNotEquals(a, builder.build());
        Assert.assertEquals(builder.build(), builder.build());
    }

    @Test
    public void testGroupSpecHashCode() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("A");
        builder.addArg(ArgGroupTest.OPTION);
        ArgGroupSpec a = builder.build();
        Assert.assertEquals(a.hashCode(), a.hashCode());
        Assert.assertEquals(a.hashCode(), ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).build().hashCode());
        OptionSpec otherOption = OptionSpec.builder("-y").build();
        Assert.assertNotEquals(a.hashCode(), ArgGroupSpec.builder("A").addArg(ArgGroupTest.OPTION).addArg(otherOption).build().hashCode());
        builder.heading("my heading");
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.headingKey("my headingKey");
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.order(123);
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.exclusive(false);
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.validate(false);
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.multiplicity("1");
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
        builder.subgroups().add(ArgGroupSpec.builder("B").addArg(ArgGroupTest.OPTION).build());
        Assert.assertNotEquals(a.hashCode(), builder.build().hashCode());
        Assert.assertEquals(builder.build().hashCode(), builder.build().hashCode());
    }

    @Test
    public void testReflection() {
        @Command(argGroups = @ArgGroup(name = "AAA"))
        class App {
            @Option(names = "-x", groups = "AAA")
            int x;

            @Option(names = "-y", groups = "AAA")
            int y;
        }
        CommandLine cmd = new CommandLine(new App());
        CommandSpec spec = cmd.getCommandSpec();
        Map<String, ArgGroupSpec> groups = spec.argGroups();
        Assert.assertEquals(1, groups.size());
        ArgGroupSpec group = groups.get("AAA");
        Assert.assertNotNull(group);
        List<ArgSpec> options = new ArrayList<ArgSpec>(group.args());
        Assert.assertEquals(2, options.size());
        Assert.assertEquals("-x", shortestName());
        Assert.assertEquals("-y", shortestName());
        Assert.assertEquals(1, options.get(0).groupNames().size());
        Assert.assertEquals(Arrays.asList("AAA"), options.get(0).groupNames());
        Assert.assertEquals(1, options.get(1).groupNames().size());
        Assert.assertEquals(Arrays.asList("AAA"), options.get(1).groupNames());
    }

    @Test
    public void testProgrammatic() {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder("-x").build());
        spec.addOption(OptionSpec.builder("-y").build());
        spec.addOption(OptionSpec.builder("-z").build());
        ArgGroupSpec exclusive = ArgGroupSpec.builder("EXCL").addArg(spec.findOption("-x")).addArg(spec.findOption("-y")).build();
        ArgGroupSpec cooccur = ArgGroupSpec.builder("ALL").addArg(spec.findOption("-z")).addSubgroup(exclusive).build();
        spec.addArgGroup(exclusive);
        spec.addArgGroup(cooccur);
        Map<String, ArgGroupSpec> groups = spec.argGroups();
    }

    static final OptionSpec OPTION_A = OptionSpec.builder("-a").build();

    static final OptionSpec OPTION_B = OptionSpec.builder("-b").build();

    static final OptionSpec OPTION_C = OptionSpec.builder("-c").build();

    @Test
    public void testValidationNonRequiredExclusive_ActualTwo() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            group.validateConstraints(cmd, Arrays.<ArgSpec>asList(ArgGroupTest.OPTION_A, ArgGroupTest.OPTION_B));
            Assert.fail("Expected exception");
        } catch (MutuallyExclusiveArgsException ex) {
            Assert.assertEquals("Error: -a, -b are mutually exclusive (specify only one)", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationNonRequiredExclusive_ActualTwo() {
        @Command(argGroups = @ArgGroup(name = "X"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;
        }
        try {
            CommandLine.populateCommand(new App(), "-a", "-b");
            Assert.fail("Expected exception");
        } catch (MutuallyExclusiveArgsException ex) {
            Assert.assertEquals("Error: -a, -b are mutually exclusive (specify only one)", ex.getMessage());
        }
    }

    @Test
    public void testValidationNonRequiredExclusive_ActualZero() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        group.validateConstraints(cmd, Collections.<ArgSpec>emptyList());
    }

    @Test
    public void testReflectionValidationNonRequiredExclusive_ActualZero() {
        @Command(argGroups = @ArgGroup(name = "X"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;
        }
        CommandLine.populateCommand(new App());
    }

    @Test
    public void testValidationRequiredExclusive_ActualZero() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").multiplicity("1").addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            group.validateConstraints(cmd, Collections.<ArgSpec>emptyList());
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument (specify one of these): -a, -b", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationRequiredExclusive_ActualZero() {
        @Command(argGroups = @ArgGroup(name = "X", multiplicity = "1"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;
        }
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument (specify one of these): -a, -b", ex.getMessage());
        }
    }

    @Test
    public void testValidationNonRequiredNonExclusive_All() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        // no error
        group.validateConstraints(cmd, Arrays.<ArgSpec>asList(ArgGroupTest.OPTION_A, ArgGroupTest.OPTION_B, ArgGroupTest.OPTION_C));
    }

    @Test
    public void testReflectionValidationNonRequiredNonExclusive_All() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        CommandLine.populateCommand(new App(), "-a", "-b", "-c");
    }

    @Test
    public void testValidationNonRequiredNonExclusive_Partial() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            group.validateConstraints(cmd, Arrays.<ArgSpec>asList(ArgGroupTest.OPTION_A, ArgGroupTest.OPTION_B));
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -c", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationNonRequiredNonExclusive_Partial() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        try {
            CommandLine.populateCommand(new App(), "-a", "-b");
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -c", ex.getMessage());
        }
    }

    @Test
    public void testValidationNonRequiredNonExclusive_Zero() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        // no error
        group.validateConstraints(cmd, Collections.<ArgSpec>emptyList());
    }

    @Test
    public void testReflectionValidationNonRequiredNonExclusive_Zero() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        CommandLine.populateCommand(new App());
    }

    @Test
    public void testValidationRequiredNonExclusive_All() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").multiplicity("1").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        // no error
        group.validateConstraints(cmd, Arrays.<ArgSpec>asList(ArgGroupTest.OPTION_A, ArgGroupTest.OPTION_B, ArgGroupTest.OPTION_C));
    }

    @Test
    public void testReflectionValidationRequiredNonExclusive_All() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false, multiplicity = "1"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        CommandLine.populateCommand(new App(), "-a", "-b", "-c");
    }

    @Test
    public void testValidationRequiredNonExclusive_Partial() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").multiplicity("1").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            group.validateConstraints(cmd, Arrays.<ArgSpec>asList(ArgGroupTest.OPTION_B));
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -a, -c", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationRequiredNonExclusive_Partial() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false, multiplicity = "1"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        try {
            CommandLine.populateCommand(new App(), "-b");
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -a, -c", ex.getMessage());
        }
    }

    @Test
    public void testValidationRequiredNonExclusive_Zero() {
        ArgGroupSpec group = ArgGroupSpec.builder("blah").multiplicity("1").exclusive(false).addArg(ArgGroupTest.OPTION_A).addArg(ArgGroupTest.OPTION_B).addArg(ArgGroupTest.OPTION_C).build();
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            group.validateConstraints(cmd, Collections.<ArgSpec>emptyList());
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -a, -b, -c", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationRequiredNonExclusive_Zero() {
        @Command(argGroups = @ArgGroup(name = "X", exclusive = false, multiplicity = "1"))
        class App {
            @Option(names = "-a", groups = "X")
            boolean a;

            @Option(names = "-b", groups = "X")
            boolean b;

            @Option(names = "-c", groups = "X")
            boolean c;
        }
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("Expected exception");
        } catch (MissingParameterException ex) {
            Assert.assertEquals("Error: Missing required argument(s): -a, -b, -c", ex.getMessage());
        }
    }

    @Test
    public void testReflectionValidationCompositeRequiredGroup() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "1", subgroups = { "ALL", "EXCL" }) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            boolean x;

            @Option(names = "-y", groups = "EXCL")
            boolean y;

            @Option(names = "-a", groups = "ALL")
            boolean a;

            @Option(names = "-b", groups = "ALL")
            boolean b;
        }
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -b", "-a");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -a", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: -x, -y are mutually exclusive (specify only one)", "-x", "-y");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -b", "-x", "-a");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -a", "-x", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: ([-a -b] | (-x | -y)) are mutually exclusive (specify only one)", "-a", "-x", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: ([-a -b] | (-x | -y)) are mutually exclusive (specify only one)", "-a", "-y", "-b");
        // no input
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument (specify one of these): ([-a -b] | (-x | -y))");
        // no error
        validateInput(new App(), null, null, "-a", "-b");
        validateInput(new App(), null, null, "-x");
        validateInput(new App(), null, null, "-y");
    }

    @Test
    public void testReflectionValidationCompositeNonRequiredGroup() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            boolean x;

            @Option(names = "-y", groups = "EXCL")
            boolean y;

            @Option(names = "-a", groups = "ALL")
            boolean a;

            @Option(names = "-b", groups = "ALL")
            boolean b;
        }
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -b", "-a");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -a", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: -x, -y are mutually exclusive (specify only one)", "-x", "-y");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -b", "-x", "-a");
        validateInput(new App(), MissingParameterException.class, "Error: Missing required argument(s): -a", "-x", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: [[-a -b] | (-x | -y)] are mutually exclusive (specify only one)", "-a", "-x", "-b");
        validateInput(new App(), MutuallyExclusiveArgsException.class, "Error: [[-a -b] | (-x | -y)] are mutually exclusive (specify only one)", "-a", "-y", "-b");
        // no input: ok because composite as a whole is optional
        validateInput(new App(), null, null);
        // no error
        validateInput(new App(), null, null, "-a", "-b");
        validateInput(new App(), null, null, "-x");
        validateInput(new App(), null, null, "-y");
    }

    @Test
    public void testTopologicalSortSimple() {
        ArgGroupSpec[] all = new ArgGroupSpec.Builder[]{ ArgGroupSpec.builder("A"), ArgGroupSpec.builder("B").subgroupNames("A"), ArgGroupSpec.builder("C").subgroupNames("B") };
        List<ArgGroupSpec.Builder> builders = ArgGroupSpec.topologicalSort(Arrays.asList(all));
        Assert.assertSame(all[0], builders.get(0));
        Assert.assertSame(all[1], builders.get(1));
        Assert.assertSame(all[2], builders.get(2));
        List<ArgGroupSpec.Builder> randomized = new ArrayList<ArgGroupSpec.Builder>(Arrays.asList(all));
        Collections.shuffle(randomized);
        builders = ArgGroupSpec.topologicalSort(randomized);
        Assert.assertSame(all[0], builders.get(0));
        Assert.assertSame(all[1], builders.get(1));
        Assert.assertSame(all[2], builders.get(2));
    }

    @Test
    public void testTopologicalSortComplex() {
        ArgGroupSpec[] all = new ArgGroupSpec.Builder[]{ ArgGroupSpec.builder("5"), ArgGroupSpec.builder("11").subgroupNames("5", "7"), ArgGroupSpec.builder("2").subgroupNames("11"), ArgGroupSpec.builder("8").subgroupNames("3", "7"), ArgGroupSpec.builder("9").subgroupNames("8", "11"), ArgGroupSpec.builder("7"), ArgGroupSpec.builder("3"), ArgGroupSpec.builder("10").subgroupNames("3", "11") };
        List<ArgGroupSpec.Builder> builders = ArgGroupSpec.topologicalSort(Arrays.asList(all));
        List<String> names = new ArrayList<String>();
        for (ArgGroupSpec.Builder b : builders) {
            names.add(b.name());
        }
        Assert.assertEquals(Arrays.asList("7", "5", "3", "8", "11", "10", "9", "2"), names);
    }

    @Test
    public void testTopologicalSortCyclicalGroupIsIllegal() {
        try {
            ArgGroupSpec.topologicalSort(Arrays.asList(ArgGroupSpec.builder("A").subgroupNames("B"), ArgGroupSpec.builder("B").subgroupNames("C"), ArgGroupSpec.builder("C").subgroupNames("D"), ArgGroupSpec.builder("D").subgroupNames("E", "G"), ArgGroupSpec.builder("E").subgroupNames("F", "A"), ArgGroupSpec.builder("G").subgroupNames("H"), ArgGroupSpec.builder("F"), ArgGroupSpec.builder("H")));
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Cyclic group dependency: ArgGroupSpec.Builder[A, -> [B]] in [A, B, C, D, E, A]", ex.getMessage());
        }
    }

    @Test
    public void testTopologicalSortSimpleCyclicalGroupIsIllegal() {
        try {
            ArgGroupSpec.topologicalSort(Arrays.asList(ArgGroupSpec.builder("X").subgroupNames("X")));
            Assert.fail("Expected exception");
        } catch (InitializationException ex) {
            Assert.assertEquals("Cyclic group dependency: ArgGroupSpec.Builder[X, -> [X]] in [X, X]", ex.getMessage());
        }
    }

    @Test
    public void testSynopsisOnlyOptions() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("G").addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        Assert.assertEquals("[-a | -b | -c]", builder.build().synopsis());
        builder.multiplicity("1");
        Assert.assertEquals("(-a | -b | -c)", builder.build().synopsis());
        builder.multiplicity("2");
        Assert.assertEquals("(-a | -b | -c) (-a | -b | -c)", builder.build().synopsis());
        builder.multiplicity("1..3");
        Assert.assertEquals("(-a | -b | -c) [-a | -b | -c] [-a | -b | -c]", builder.build().synopsis());
        builder.multiplicity("1..*");
        Assert.assertEquals("(-a | -b | -c)...", builder.build().synopsis());
        builder.multiplicity("1");
        builder.exclusive(false);
        Assert.assertEquals("(-a -b -c)", builder.build().synopsis());
        builder.multiplicity("0..1");
        Assert.assertEquals("[-a -b -c]", builder.build().synopsis());
        builder.multiplicity("0..2");
        Assert.assertEquals("[-a -b -c] [-a -b -c]", builder.build().synopsis());
        builder.multiplicity("0..3");
        Assert.assertEquals("[-a -b -c] [-a -b -c] [-a -b -c]", builder.build().synopsis());
        builder.multiplicity("0..*");
        Assert.assertEquals("[-a -b -c]...", builder.build().synopsis());
    }

    @Test
    public void testSynopsisOnlyPositionals() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("G").addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG1").build()).addArg(PositionalParamSpec.builder().index("1").paramLabel("ARG2").build()).addArg(PositionalParamSpec.builder().index("2").paramLabel("ARG3").build());
        Assert.assertEquals("[ARG1 | ARG2 | ARG3]", builder.build().synopsis());
        builder.multiplicity("1");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3)", builder.build().synopsis());
        builder.multiplicity("2");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3) (ARG1 | ARG2 | ARG3)", builder.build().synopsis());
        builder.multiplicity("1..3");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3) [ARG1 | ARG2 | ARG3] [ARG1 | ARG2 | ARG3]", builder.build().synopsis());
        builder.multiplicity("1..*");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3)...", builder.build().synopsis());
        builder.multiplicity("1");
        builder.exclusive(false);
        Assert.assertEquals("(ARG1 ARG2 ARG3)", builder.build().synopsis());
        builder.multiplicity("0..1");
        Assert.assertEquals("[ARG1 ARG2 ARG3]", builder.build().synopsis());
        builder.multiplicity("0..2");
        Assert.assertEquals("[ARG1 ARG2 ARG3] [ARG1 ARG2 ARG3]", builder.build().synopsis());
        builder.multiplicity("0..*");
        Assert.assertEquals("[ARG1 ARG2 ARG3]...", builder.build().synopsis());
    }

    @Test
    public void testSynopsisMixOptionsPositionals() {
        ArgGroupSpec.Builder builder = ArgGroupSpec.builder("G").addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG1").build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG2").build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG3").build()).addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        Assert.assertEquals("[ARG1 | ARG2 | ARG3 | -a | -b | -c]", builder.build().synopsis());
        builder.multiplicity("1");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3 | -a | -b | -c)", builder.build().synopsis());
        builder.exclusive(false);
        Assert.assertEquals("(ARG1 ARG2 ARG3 -a -b -c)", builder.build().synopsis());
        builder.multiplicity("0..1");
        Assert.assertEquals("[ARG1 ARG2 ARG3 -a -b -c]", builder.build().synopsis());
    }

    @Test
    public void testSynopsisOnlyGroups() {
        ArgGroupSpec.Builder b1 = ArgGroupSpec.builder("B1").addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        ArgGroupSpec.Builder b2 = ArgGroupSpec.builder("B2").addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-f").build()).multiplicity("1");
        ArgGroupSpec.Builder b3 = ArgGroupSpec.builder("B3").addArg(OptionSpec.builder("-g").build()).addArg(OptionSpec.builder("-h").build()).addArg(OptionSpec.builder("-i").build()).multiplicity("1").exclusive(false);
        ArgGroupSpec.Builder composite = ArgGroupSpec.builder("COMPOSITE").addSubgroup(b1.build()).addSubgroup(b2.build()).addSubgroup(b3.build());
        Assert.assertEquals("[[-a | -b | -c] | (-e | -f) | (-g -h -i)]", composite.build().synopsis());
        composite.multiplicity("1");
        Assert.assertEquals("([-a | -b | -c] | (-e | -f) | (-g -h -i))", composite.build().synopsis());
        composite.multiplicity("1..*");
        Assert.assertEquals("([-a | -b | -c] | (-e | -f) | (-g -h -i))...", composite.build().synopsis());
        composite.multiplicity("1");
        composite.exclusive(false);
        Assert.assertEquals("([-a | -b | -c] (-e | -f) (-g -h -i))", composite.build().synopsis());
        composite.multiplicity("0..1");
        Assert.assertEquals("[[-a | -b | -c] (-e | -f) (-g -h -i)]", composite.build().synopsis());
        composite.multiplicity("0..*");
        Assert.assertEquals("[[-a | -b | -c] (-e | -f) (-g -h -i)]...", composite.build().synopsis());
    }

    @Test
    public void testSynopsisMixGroupsOptions() {
        ArgGroupSpec.Builder b1 = ArgGroupSpec.builder("B1").addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        ArgGroupSpec.Builder b2 = ArgGroupSpec.builder("B2").addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-f").build()).multiplicity("1");
        ArgGroupSpec.Builder composite = ArgGroupSpec.builder("COMPOSITE").addSubgroup(b1.build()).addSubgroup(b2.build()).addArg(OptionSpec.builder("-x").build()).addArg(OptionSpec.builder("-y").build()).addArg(OptionSpec.builder("-z").build());
        Assert.assertEquals("[-x | -y | -z | [-a | -b | -c] | (-e | -f)]", composite.build().synopsis());
        composite.multiplicity("1");
        Assert.assertEquals("(-x | -y | -z | [-a | -b | -c] | (-e | -f))", composite.build().synopsis());
        composite.exclusive(false);
        Assert.assertEquals("(-x -y -z [-a | -b | -c] (-e | -f))", composite.build().synopsis());
        composite.multiplicity("0..1");
        Assert.assertEquals("[-x -y -z [-a | -b | -c] (-e | -f)]", composite.build().synopsis());
    }

    @Test
    public void testSynopsisMixGroupsPositionals() {
        ArgGroupSpec.Builder b1 = ArgGroupSpec.builder("B1").addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        ArgGroupSpec.Builder b2 = ArgGroupSpec.builder("B2").addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-f").build()).multiplicity("1");
        ArgGroupSpec.Builder composite = ArgGroupSpec.builder("COMPOSITE").addSubgroup(b1.build()).addSubgroup(b2.build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG1").build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG2").build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG3").build());
        Assert.assertEquals("[ARG1 | ARG2 | ARG3 | [-a | -b | -c] | (-e | -f)]", composite.build().synopsis());
        composite.multiplicity("1");
        Assert.assertEquals("(ARG1 | ARG2 | ARG3 | [-a | -b | -c] | (-e | -f))", composite.build().synopsis());
        composite.exclusive(false);
        Assert.assertEquals("(ARG1 ARG2 ARG3 [-a | -b | -c] (-e | -f))", composite.build().synopsis());
        composite.multiplicity("0..1");
        Assert.assertEquals("[ARG1 ARG2 ARG3 [-a | -b | -c] (-e | -f)]", composite.build().synopsis());
    }

    @Test
    public void testSynopsisMixGroupsOptionsPositionals() {
        ArgGroupSpec.Builder b1 = ArgGroupSpec.builder("B1").addArg(OptionSpec.builder("-a").build()).addArg(OptionSpec.builder("-b").build()).addArg(OptionSpec.builder("-c").build());
        ArgGroupSpec.Builder b2 = ArgGroupSpec.builder("B2").addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-e").build()).addArg(OptionSpec.builder("-f").build()).multiplicity("1");
        ArgGroupSpec.Builder composite = ArgGroupSpec.builder("COMPOSITE").addSubgroup(b1.build()).addSubgroup(b2.build()).addArg(OptionSpec.builder("-x").build()).addArg(OptionSpec.builder("-y").build()).addArg(OptionSpec.builder("-z").build()).addArg(PositionalParamSpec.builder().index("0").paramLabel("ARG1").build()).addArg(PositionalParamSpec.builder().index("1").paramLabel("ARG2").build()).addArg(PositionalParamSpec.builder().index("2").paramLabel("ARG3").build());
        Assert.assertEquals("[-x | -y | -z | ARG1 | ARG2 | ARG3 | [-a | -b | -c] | (-e | -f)]", composite.build().synopsis());
        composite.multiplicity("1");
        Assert.assertEquals("(-x | -y | -z | ARG1 | ARG2 | ARG3 | [-a | -b | -c] | (-e | -f))", composite.build().synopsis());
        composite.exclusive(false);
        Assert.assertEquals("(-x -y -z ARG1 ARG2 ARG3 [-a | -b | -c] (-e | -f))", composite.build().synopsis());
        composite.multiplicity("0..1");
        Assert.assertEquals("[-x -y -z ARG1 ARG2 ARG3 [-a | -b | -c] (-e | -f)]", composite.build().synopsis());
    }

    @Test
    public void testUsageHelpRequiredExclusiveGroup() {
        @Command(argGroups = { @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1") })
        class App {
            @Option(names = "-x", groups = "EXCL")
            boolean x;

            @Option(names = "-y", groups = "EXCL")
            boolean y;
        }
        String expected = String.format(("" + (("Usage: <main class> (-x | -y)%n" + "  -x%n") + "  -y%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpNonRequiredExclusiveGroup() {
        @Command(argGroups = { @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "0..1") })
        class App {
            @Option(names = "-x", groups = "EXCL")
            boolean x;

            @Option(names = "-y", groups = "EXCL")
            boolean y;
        }
        String expected = String.format(("" + (("Usage: <main class> [-x | -y]%n" + "  -x%n") + "  -y%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpRequiredNonExclusiveGroup() {
        @Command(argGroups = { @ArgGroup(name = "G", exclusive = false, multiplicity = "1") })
        class App {
            @Option(names = "-x", groups = "G")
            boolean x;

            @Option(names = "-y", groups = "G")
            boolean y;
        }
        String expected = String.format(("" + (("Usage: <main class> (-x -y)%n" + "  -x%n") + "  -y%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpNonRequiredNonExclusiveGroup() {
        @Command(argGroups = { @ArgGroup(name = "G", exclusive = false, multiplicity = "0..1") })
        class App {
            @Option(names = "-x", groups = "G")
            boolean x;

            @Option(names = "-y", groups = "G")
            boolean y;
        }
        String expected = String.format(("" + (("Usage: <main class> [-x -y]%n" + "  -x%n") + "  -y%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUsageHelpNonValidatingGroupDoesNotImpactSynopsis() {
        @Command(argGroups = { @ArgGroup(name = "G", validate = false) })
        class App {
            @Option(names = "-x", groups = "G")
            boolean x;

            @Option(names = "-y", groups = "G")
            boolean y;
        }
        String expected = String.format(("" + (("Usage: <main class> [-xy]%n" + "  -x%n") + "  -y%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCompositeGroupSynopsis() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            int x;

            @Option(names = "-y", groups = "EXCL")
            int y;

            @Option(names = "-a", groups = "ALL")
            int a;

            @Option(names = "-b", groups = "ALL")
            int b;

            @Option(names = "-c", groups = "ALL")
            int c;
        }
        String expected = String.format(("" + ((((("Usage: <main class> [[-a=<a> -b=<b> -c=<c>] | (-x=<x> | -y=<y>)]%n" + "  -a=<a>%n") + "  -b=<b>%n") + "  -c=<c>%n") + "  -x=<x>%n") + "  -y=<y>%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCompositeGroupSynopsisAnsi() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            int x;

            @Option(names = "-y", groups = "EXCL")
            int y;

            @Option(names = "-a", groups = "ALL")
            int a;

            @Option(names = "-b", groups = "ALL")
            int b;

            @Option(names = "-c", groups = "ALL")
            int c;
        }
        String expected = String.format(("" + ((((("Usage: @|bold <main class>|@ [[@|yellow -a|@=@|italic <a>|@ @|yellow -b|@=@|italic <b>|@ @|yellow -c|@=@|italic <c>|@] | (@|yellow -x|@=@|italic <x>|@ | @|yellow -y|@=@|italic <y>|@)]%n" + "  @|yellow -a|@=@|italic <|@@|italic a>|@%n") + "  @|yellow -b|@=@|italic <|@@|italic b>|@%n") + "  @|yellow -c|@=@|italic <|@@|italic c>|@%n") + "  @|yellow -x|@=@|italic <|@@|italic x>|@%n") + "  @|yellow -y|@=@|italic <|@@|italic y>|@%n")));
        expected = ON.string(expected);
        String actual = new CommandLine(new App()).getUsageMessage(ON);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGroupUsageHelpOptionList() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1", order = 10, heading = "Co-occurring options:%nThese options must appear together, or not at all.%n"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1", order = 20, heading = "Exclusive options:%n"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }), @ArgGroup(name = "INITIAL", validate = false, heading = "", order = 0), @ArgGroup(name = "REMAINDER", validate = false, heading = "Remaining options:%n", order = 100) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            int x;

            @Option(names = "-y", groups = "EXCL")
            int y;

            @Option(names = "-a", groups = "ALL")
            int a;

            @Option(names = "-b", groups = "ALL")
            int b;

            @Option(names = "-c", groups = "ALL")
            int c;

            @Option(names = "-A", groups = "INITIAL")
            int A;

            @Option(names = "-B", groups = "INITIAL")
            boolean B;

            @Option(names = "-C", groups = "INITIAL")
            boolean C;

            @Option(names = "-D", groups = "REMAINDER")
            int D;

            @Option(names = "-E", groups = "REMAINDER")
            boolean E;

            @Option(names = "-F", groups = "REMAINDER")
            boolean F;
        }
        String expected = String.format(("" + (((((((((((((((("Usage: <main class> [[-a=<a> -b=<b> -c=<c>] | (-x=<x> | -y=<y>)] [-BCEF]%n" + "                    [-A=<A>] [-D=<D>]%n") + "  -A=<A>%n") + "  -B%n") + "  -C%n") + "Co-occurring options:%n") + "These options must appear together, or not at all.%n") + "  -a=<a>%n") + "  -b=<b>%n") + "  -c=<c>%n") + "Exclusive options:%n") + "  -x=<x>%n") + "  -y=<y>%n") + "Remaining options:%n") + "  -D=<D>%n") + "  -E%n") + "  -F%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGroupUsageHelpOptionListOptionsWithoutGroupsPrecedeGroups() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1", order = 10, heading = "Co-occurring options:%nThese options must appear together, or not at all.%n"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1", order = 20, heading = "Exclusive options:%n"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }), @ArgGroup(name = "REMAINDER", validate = false, heading = "Remaining options:%n", order = 100) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            int x;

            @Option(names = "-y", groups = "EXCL")
            int y;

            @Option(names = "-a", groups = "ALL")
            int a;

            @Option(names = "-b", groups = "ALL")
            int b;

            @Option(names = "-c", groups = "ALL")
            int c;

            @Option(names = "-A")
            int A;

            @Option(names = "-B")
            boolean B;

            @Option(names = "-C")
            boolean C;

            @Option(names = "-D", groups = "REMAINDER")
            int D;

            @Option(names = "-E", groups = "REMAINDER")
            boolean E;

            @Option(names = "-F", groups = "REMAINDER")
            boolean F;
        }
        String expected = String.format(("" + (((((((((((((((("Usage: <main class> [[-a=<a> -b=<b> -c=<c>] | (-x=<x> | -y=<y>)] [-BCEF]%n" + "                    [-A=<A>] [-D=<D>]%n") + "  -A=<A>%n") + "  -B%n") + "  -C%n") + "Co-occurring options:%n") + "These options must appear together, or not at all.%n") + "  -a=<a>%n") + "  -b=<b>%n") + "  -c=<c>%n") + "Exclusive options:%n") + "  -x=<x>%n") + "  -y=<y>%n") + "Remaining options:%n") + "  -D=<D>%n") + "  -E%n") + "  -F%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGroupUsageHelpOptionListOptionsGroupWithMixedOptionsAndPositionals() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "0..1", order = 10, heading = "Co-occurring options:%nThese options must appear together, or not at all.%n"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "1", order = 20, heading = "Exclusive options:%n"), @ArgGroup(name = "COMPOSITE", exclusive = true, multiplicity = "0..1", subgroups = { "ALL", "EXCL" }), @ArgGroup(name = "REMAINDER", validate = false, heading = "Remaining options:%n", order = 100) })
        class App {
            @Option(names = "-x", groups = "EXCL")
            int x;

            @Option(names = "-y", groups = "EXCL")
            int y;

            @Option(names = "-a", groups = "ALL")
            int a;

            @Parameters(index = "0", groups = "ALL")
            File f0;

            @Parameters(index = "1", groups = "ALL")
            File f1;

            @Parameters(index = "2")
            File f2;

            @Option(names = "-A")
            int A;

            @Option(names = "-B")
            boolean B;

            @Option(names = "-C")
            boolean C;

            @Option(names = "-D", groups = "REMAINDER")
            int D;

            @Option(names = "-E", groups = "REMAINDER")
            boolean E;

            @Option(names = "-F", groups = "REMAINDER")
            boolean F;
        }
        String expected = String.format(("" + ((((((((((((((((("Usage: <main class> [[-a=<a> <f0> <f1>] | (-x=<x> | -y=<y>)] [-BCEF] [-A=<A>]%n" + "                    [-D=<D>] <f2>%n") + "      <f2>%n") + "  -A=<A>%n") + "  -B%n") + "  -C%n") + "Co-occurring options:%n") + "These options must appear together, or not at all.%n") + "      <f0>%n") + "      <f1>%n") + "  -a=<a>%n") + "Exclusive options:%n") + "  -x=<x>%n") + "  -y=<y>%n") + "Remaining options:%n") + "  -D=<D>%n") + "  -E%n") + "  -F%n")));
        String actual = new CommandLine(new App()).getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRequiredArgsInAGroupAreNotValidated() {
        @Command(argGroups = { @ArgGroup(name = "ALL", exclusive = false, multiplicity = "1"), @ArgGroup(name = "EXCL", exclusive = true, multiplicity = "0..1", subgroups = "ALL") })
        class App {
            @Option(names = "-a", required = true, groups = "ALL")
            int a;

            @Parameters(index = "0", groups = "ALL")
            File f0;

            @Option(names = "-x", required = true, groups = "EXCL")
            boolean x;
        }
        String expected = String.format(("" + ((("Usage: <main class> [-x | (-a=<a> <f0>)]%n" + "      <f0>%n") + "  -a=<a>%n") + "  -x%n")));
        CommandLine cmd = new CommandLine(new App());
        String actual = cmd.getUsageMessage(OFF);
        Assert.assertEquals(expected, actual);
        cmd.parseArgs();// no error

    }
}

