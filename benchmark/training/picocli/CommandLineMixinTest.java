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


import Help.Ansi.OFF;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;


public class CommandLineMixinTest {
    @Test
    public void testMixinAnnotationMustBeValidCommand_CommandAnnotation() {
        @Command
        class ValidMixin {}
        class Receiver {
            // valid command because it has @Mixin annotated field
            @Mixin
            ValidMixin mixMeIn;
        }
        new CommandLine(new Receiver(), new InnerClassFactory(this));// no exception

    }

    @Test
    public void testMixinAnnotationMustBeValidCommand_OptionAnnotatedField() {
        class ValidMixin {
            // valid command because it has @Option annotation
            @Option(names = "-a")
            int a;
        }
        class Receiver {
            // valid command because it has @Mixin annotated field
            @Mixin
            ValidMixin mixMeIn;
        }
        new CommandLine(new Receiver(), new InnerClassFactory(this));// no exception

    }

    @Test
    public void testMixinAnnotationMustBeValidCommand_ParametersAnnotatedField() {
        class ValidMixin {
            // valid command because it has @Parameters annotation
            @Parameters
            int a;
        }
        class Receiver {
            // valid command because it has @Mixin annotated field
            @Mixin
            ValidMixin mixMeIn;
        }
        new CommandLine(new Receiver(), new InnerClassFactory(this));// no exception

    }

    @Test
    public void testAddMixinMustBeValidCommand_CommandAnnotation() {
        @Command
        class ValidMixin {}
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("valid", new ValidMixin());// no exception

    }

    @Test
    public void testAddMixinMustBeValidCommand_OptionAnnotatedField() {
        class ValidMixin {
            // valid command because it has @Option annotation
            @Option(names = "-a")
            int a;
        }
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("valid", new ValidMixin());// no exception

    }

    @Test
    public void testAddMixinMustBeValidCommand_ParametersAnnotatedField() {
        class ValidMixin {
            // valid command because it has @Parameters annotation
            @Parameters
            int a;
        }
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("valid", new ValidMixin());// no exception

    }

    @Test
    public void testAddMixinMustBeValidCommand_SubCommandMethod() {
        // valid command because it has @Command annotation
        @Command
        class ValidMixin {}
        @Command
        class Receiver {
            @Command
            void sub(@Mixin
            ValidMixin mixin) {
            }
        }
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        CommandSpec commandSpec = commandLine.getCommandSpec().subcommands().get("sub").getCommandSpec().mixins().get("arg0");
        Assert.assertEquals(ValidMixin.class, commandSpec.userObject().getClass());
        commandLine.addMixin("valid", new ValidMixin());// no exception

    }

    @Test
    public void testMixinAnnotationRejectedIfNotAValidCommand() {
        class Invalid {}
        class Receiver {
            @Mixin
            Invalid mixMeIn;
        }
        try {
            new CommandLine(new Receiver(), new InnerClassFactory(this));
        } catch (InitializationException ex) {
            Assert.assertEquals(((Invalid.class.getName()) + " is not a command: it has no @Command, @Option, @Parameters or @Unmatched annotations"), ex.getMessage());
        }
    }

    @Test
    public void testAddMixinRejectedIfNotAValidCommand() {
        class Invalid {}
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        try {
            commandLine.addMixin("invalid", new Invalid());
        } catch (InitializationException ex) {
            Assert.assertEquals(((Invalid.class.getName()) + " is not a command: it has no @Command, @Option, @Parameters or @Unmatched annotations"), ex.getMessage());
        }
    }

    static class CommandAttributes {
        @Command(name = "mixmein", version = "Mixin 1.0", separator = ":", description = "description from mixin", descriptionHeading = "Mixin Description Heading%n", header = "Mixin Header", headerHeading = "Mixin Header Heading%n", footer = "Mixin Footer", footerHeading = "Mixin Footer Heading%n", optionListHeading = "Mixin Option List Heading%n", parameterListHeading = "Mixin Parameter List Heading%n", commandListHeading = "Mixin Command List Heading%n", requiredOptionMarker = '%', synopsisHeading = "Mixin Synopsis Heading%n", abbreviateSynopsis = true, customSynopsis = "Mixin custom synopsis", showDefaultValues = true, sortOptions = false)
        static class MixMeIn {}
    }

    @Test
    public void testMixinAnnotationCommandAttributes() throws Exception {
        class Receiver {
            @Mixin
            CommandLineMixinTest.CommandAttributes.MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        verifyMixinCommandAttributes(commandLine);
    }

    @Test
    public void testAddMixinCommandAttributes() throws Exception {
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("mixmein", new CommandLineMixinTest.CommandAttributes.MixMeIn());
        verifyMixinCommandAttributes(commandLine);
    }

    static class CommandAttributesDontOverwriteReceiverAttributes {
        @Command(name = "mixmein", version = "Mixin 1.0", separator = ":", description = "description from mixin", descriptionHeading = "Mixin Description Heading%n", header = "Mixin Header", headerHeading = "Mixin Header Heading%n", footer = "Mixin Footer", footerHeading = "Mixin Footer Heading%n", optionListHeading = "Mixin Option List Heading%n", parameterListHeading = "Mixin Parameter List Heading%n", commandListHeading = "Mixin Command List Heading%n", requiredOptionMarker = '%', synopsisHeading = "Mixin Synopsis Heading%n", abbreviateSynopsis = true, customSynopsis = "Mixin custom synopsis", showDefaultValues = true, sortOptions = false)
        static class MixMeIn {}
    }

    @Test
    public void testMixinAnnotationCommandAttributesDontOverwriteReceiverAttributes() throws Exception {
        @Command(name = "receiver", version = "Receiver 1.0", separator = "~", description = "Receiver description", descriptionHeading = "Receiver Description Heading%n", header = "Receiver Header", headerHeading = "Receiver Header Heading%n", footer = "Receiver Footer", footerHeading = "Receiver Footer Heading%n", optionListHeading = "Receiver Option List Heading%n", parameterListHeading = "Receiver Parameter List Heading%n", commandListHeading = "Receiver Command List Heading%n", requiredOptionMarker = '#', synopsisHeading = "Receiver Synopsis Heading%n", customSynopsis = "Receiver custom synopsis")
        class Receiver {
            @Mixin
            CommandLineMixinTest.CommandAttributesDontOverwriteReceiverAttributes.MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        verifyMixinCommandAttributesDontOverwriteReceiverAttributes(commandLine);
    }

    @Test
    public void testAddMixinCommandAttributesDontOverwriteReceiverAttributes() throws Exception {
        @Command(name = "receiver", version = "Receiver 1.0", separator = "~", description = "Receiver description", descriptionHeading = "Receiver Description Heading%n", header = "Receiver Header", headerHeading = "Receiver Header Heading%n", footer = "Receiver Footer", footerHeading = "Receiver Footer Heading%n", optionListHeading = "Receiver Option List Heading%n", parameterListHeading = "Receiver Parameter List Heading%n", commandListHeading = "Receiver Command List Heading%n", requiredOptionMarker = '#', synopsisHeading = "Receiver Synopsis Heading%n", customSynopsis = "Receiver custom synopsis")
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        commandLine.addMixin("mixMeIn", new CommandLineMixinTest.CommandAttributesDontOverwriteReceiverAttributes.MixMeIn());
        verifyMixinCommandAttributesDontOverwriteReceiverAttributes(commandLine);
    }

    static class SuperClassCommandAttributesDontOverwriteSubclassAttributes {
        @Command(name = "mixmein", version = "Mixin 1.0", separator = ":", description = "description from mixin", descriptionHeading = "Mixin Description Heading%n", header = "Mixin Header", headerHeading = "Mixin Header Heading%n", footer = "Mixin Footer", footerHeading = "Mixin Footer Heading%n", optionListHeading = "Mixin Option List Heading%n", parameterListHeading = "Mixin Parameter List Heading%n", commandListHeading = "Mixin Command List Heading%n", requiredOptionMarker = '%', synopsisHeading = "Mixin Synopsis Heading%n", abbreviateSynopsis = true, customSynopsis = "Mixin custom synopsis", showDefaultValues = true, sortOptions = false)
        static class MixMeInSuper {}

        @Command(name = "mixmeinSub", version = "MixinSub 1.0", separator = "~", description = "description from mixinSub", descriptionHeading = "MixinSub Description Heading%n", header = "MixinSub Header", headerHeading = "MixinSub Header Heading%n", footer = "MixinSub Footer", footerHeading = "MixinSub Footer Heading%n", optionListHeading = "MixinSub Option List Heading%n", parameterListHeading = "MixinSub Parameter List Heading%n", commandListHeading = "MixinSub Command List Heading%n", requiredOptionMarker = '#', synopsisHeading = "MixinSub Synopsis Heading%n", abbreviateSynopsis = true, customSynopsis = "MixinSub custom synopsis", showDefaultValues = true, sortOptions = false)
        static class MixMeInSub extends CommandLineMixinTest.SuperClassCommandAttributesDontOverwriteSubclassAttributes.MixMeInSuper {}
    }

    @Test
    public void testMixinAnnotationSuperClassCommandAttributesDontOverwriteSubclassAttributes() throws Exception {
        class Receiver {
            @Mixin
            CommandLineMixinTest.SuperClassCommandAttributesDontOverwriteSubclassAttributes.MixMeInSub mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        verifyMixinSuperClassCommandAttributesDontOverwriteSubclassAttributes(commandLine);
    }

    @Test
    public void testAddMixinSuperClassCommandAttributesDontOverwriteSubclassAttributes() throws Exception {
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("mixMeIn", new CommandLineMixinTest.SuperClassCommandAttributesDontOverwriteSubclassAttributes.MixMeInSub());
        verifyMixinSuperClassCommandAttributesDontOverwriteSubclassAttributes(commandLine);
    }

    static class CombinesAttributes {
        @Command(name = "superName", version = "MixMeInSuper 1.0", separator = "$", description = "33", descriptionHeading = "333", header = "3333", headerHeading = "33333", parameterListHeading = // footer = "333 3",
        // footerHeading = "333 33",
        // optionListHeading = "333 333",
        "333 333 3", commandListHeading = "333 333 33", requiredOptionMarker = '3', synopsisHeading = "3333 3")
        static class MixMeInSuper {}

        @Command(description = "description from mixinSub", descriptionHeading = "MixinSub Description Heading%n", header = "MixinSub Header", headerHeading = "MixinSub Header Heading%n", parameterListHeading = // footer = "222",
        // footerHeading = "222 222",
        // optionListHeading = "222 222 222",
        "2 22", commandListHeading = "222 2", requiredOptionMarker = '2', synopsisHeading = "22222")
        static class MixMeInSub extends CommandLineMixinTest.CombinesAttributes.MixMeInSuper {}

        @Command(footer = // name = "000 - set by MixinMeInSuper",
        // version = "0.0 - set by MixinMeInSuper",
        // separator = "0 - set by MixinMeInSuper",
        // description = "00 - set by MixMeInSub",
        // descriptionHeading = "000 - set by MixMeInSub",
        // header = "0000 - set by MixMeInSub",
        // headerHeading = "00000 - set by MixMeInSub",
        "ReceiverSuper Footer", footerHeading = "ReceiverSuper Footer Heading%n", optionListHeading = "ReceiverSuper Option List Heading%n", parameterListHeading = "-1-1-1", commandListHeading = "--1--1--1", requiredOptionMarker = '1', synopsisHeading = "---1---1---1")
        static class ReceiverSuper {}
    }

    @Test
    public void testMixinAnnotationCombinesAttributes() throws Exception {
        @Command(parameterListHeading = "Receiver Parameter List Heading%n", commandListHeading = "Receiver Command List Heading%n", requiredOptionMarker = '#', synopsisHeading = "Receiver Synopsis Heading%n", showDefaultValues = // customSynopsis = "Receiver custom synopsis", // use standard generated synopsis
        true, sortOptions = false)
        class Receiver extends CommandLineMixinTest.CombinesAttributes.ReceiverSuper {
            @Mixin
            CommandLineMixinTest.CombinesAttributes.MixMeInSub mixMeIn;

            @Parameters(description = "some files")
            File[] files;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        verifyMixinCombinesAttributes(commandLine);
    }

    @Test
    public void testAddMixinCombinesAttributes() throws Exception {
        @Command(parameterListHeading = "Receiver Parameter List Heading%n", commandListHeading = "Receiver Command List Heading%n", requiredOptionMarker = '#', synopsisHeading = "Receiver Synopsis Heading%n", showDefaultValues = // customSynopsis = "Receiver custom synopsis", // use standard generated synopsis
        true, sortOptions = false)
        class Receiver extends CommandLineMixinTest.CombinesAttributes.ReceiverSuper {
            @Parameters(description = "some files")
            File[] files;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("mixMeIn", new CommandLineMixinTest.CombinesAttributes.MixMeInSub());
        verifyMixinCombinesAttributes(commandLine);
    }

    static class InjectsOptionsAndParameters {
        static class MixMeIn {
            @Option(names = { "-a", "--alpha" }, description = "option from mixin")
            private int alpha;

            @Parameters(description = "parameters from mixin")
            File[] files;
        }
    }

    @Test
    public void testMixinAnnotationInjectsOptionsAndParameters() throws UnsupportedEncodingException {
        @Command(sortOptions = false)
        class Receiver {
            @Option(names = { "-b", "--beta" }, description = "Receiver option")
            private int beta;

            @Parameters(description = "parameters from receiver")
            File[] receiverFiles;

            @Mixin
            CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        verifyMixinInjectsOptionsAndParameters(commandLine);
    }

    @Test
    public void testAddMixinInjectsOptionsAndParameters() throws UnsupportedEncodingException {
        @Command(sortOptions = false)
        class Receiver {
            @Option(names = { "-b", "--beta" }, description = "Receiver option")
            private int beta;

            @Parameters(description = "parameters from receiver")
            File[] receiverFiles;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.addMixin("mixMeIn", new CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn());
        verifyMixinInjectsOptionsAndParameters(commandLine);
    }

    @Test
    public void testMixinAnnotationParsesOptionsAndParameters() throws UnsupportedEncodingException {
        @Command(sortOptions = false)
        class Receiver {
            @Option(names = { "-b", "--beta" }, description = "Receiver option")
            private int beta;

            @Parameters(description = "parameters from receiver")
            File[] receiverFiles;

            @Mixin
            CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        commandLine.parse("-a", "111", "-b", "222", "a", "b");
        Receiver receiver = commandLine.getCommand();
        Assert.assertEquals(222, receiver.beta);
        Assert.assertEquals(111, receiver.mixMeIn.alpha);
        Assert.assertArrayEquals(new File[]{ new File("a"), new File("b") }, receiver.receiverFiles);
        Assert.assertArrayEquals(new File[]{ new File("a"), new File("b") }, receiver.mixMeIn.files);
    }

    @Test
    public void testAddMixinParsesOptionsAndParameters() throws UnsupportedEncodingException {
        @Command(sortOptions = false)
        class Receiver {
            @Option(names = { "-b", "--beta" }, description = "Receiver option")
            private int beta;

            @Parameters(description = "parameters from receiver")
            File[] receiverFiles;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn mixin = new CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn();
        commandLine.addMixin("mixin", mixin);
        commandLine.parse("-a", "111", "-b", "222", "a", "b");
        Receiver receiver = commandLine.getCommand();
        Assert.assertEquals(222, receiver.beta);
        Assert.assertEquals(111, mixin.alpha);
        Assert.assertArrayEquals(new File[]{ new File("a"), new File("b") }, receiver.receiverFiles);
        Assert.assertArrayEquals(new File[]{ new File("a"), new File("b") }, mixin.files);
        Assert.assertSame(mixin, commandLine.getMixins().get("mixin"));
        Assert.assertSame(mixin, commandLine.getCommandSpec().mixins().get("mixin").userObject());
    }

    @Test
    public void testMixinAnnotationInjectsOptionsAndParametersInDeclarationOrder() throws Exception {
        @Command(sortOptions = false)
        class Receiver {
            @Mixin
            CommandLineMixinTest.InjectsOptionsAndParameters.MixMeIn mixMeIn;

            @Option(names = { "-b", "--beta" }, description = "Receiver option")
            private int beta;

            @Parameters(description = "parameters from receiver")
            File[] receiverFiles;
        }
        CommandLine commandLine = new CommandLine(new Receiver());
        CommandSpec commandSpec = commandLine.getCommandSpec();
        Assert.assertEquals(2, commandSpec.options().size());
        Assert.assertArrayEquals(new String[]{ "-a", "--alpha" }, commandSpec.options().get(0).names());
        Assert.assertArrayEquals(new String[]{ "-b", "--beta" }, commandSpec.options().get(1).names());
        Assert.assertEquals(2, commandSpec.positionalParameters().size());
        Assert.assertEquals("<files>", commandSpec.positionalParameters().get(0).paramLabel());
        Assert.assertEquals("<receiverFiles>", commandSpec.positionalParameters().get(1).paramLabel());
        String expects = String.format(("" + (((("Usage: <main class> [-a=<alpha>] [-b=<beta>] [<files>...] [<receiverFiles>...]%n" + "      [<files>...]           parameters from mixin%n") + "      [<receiverFiles>...]   parameters from receiver%n") + "  -a, --alpha=<alpha>        option from mixin%n") + "  -b, --beta=<beta>          Receiver option%n")));
        Assert.assertEquals(expects, HelpTestUtil.usageString(commandLine, OFF));
    }

    @Test
    public void testMixinAnnotationRejectsDuplicateOptions() {
        class MixMeInDuplicate {
            @Option(names = { "-a", "--alpha" }, description = "option from mixin")
            private int alpha;
        }
        class ReceiverDuplicate {
            @Option(names = { "-a" }, description = "Receiver option")
            private int beta;

            @Mixin
            MixMeInDuplicate mixMeIn;
        }
        try {
            new CommandLine(new ReceiverDuplicate(), new InnerClassFactory(this));
            Assert.fail("Expected exception");
        } catch (DuplicateOptionAnnotationsException ex) {
            Assert.assertEquals("Option name '-a' is used by both field int picocli.CommandLineMixinTest$1MixMeInDuplicate.alpha and field int picocli.CommandLineMixinTest$1ReceiverDuplicate.beta", ex.getMessage());
        }
    }

    @Test
    public void testMixinAnnotationWithSubcommands() {
        @Command(name = "mixinsub")
        class MixedInSubCommand {}
        @Command(subcommands = MixedInSubCommand.class)
        class MixMeIn {}
        class Receiver {
            @Mixin
            MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        CommandSpec commandSpec = commandLine.getCommandSpec();
        Assert.assertEquals(1, commandLine.getSubcommands().size());
        Assert.assertEquals(1, commandSpec.subcommands().size());
        CommandLine subcommandLine = commandSpec.subcommands().get("mixinsub");
        Assert.assertSame(subcommandLine, commandLine.getSubcommands().get("mixinsub"));
        Assert.assertTrue(((subcommandLine.getCommand()) instanceof MixedInSubCommand));
    }

    @Test
    public void testMixinAnnotationWithVersionProvider() {
        class MyVersionProvider implements IVersionProvider {
            public String[] getVersion() {
                return new String[]{ "line 1", "line 2" };
            }
        }
        @Command(version = "Mixin 1.0", versionProvider = MyVersionProvider.class)
        class MixMeIn {}
        class Receiver {
            @Mixin
            MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        CommandSpec commandSpec = commandLine.getCommandSpec();
        Assert.assertTrue(((commandSpec.versionProvider()) instanceof MyVersionProvider));
        Assert.assertArrayEquals(new String[]{ "line 1", "line 2" }, commandSpec.version());
    }

    @Test
    public void testMixinAnnotationCanBeRetrievedByAnnotationName() {
        @Command
        class MixMeIn {}
        @Command
        class Receiver {
            @Mixin(name = "aMixin")
            MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        Assert.assertFalse("mixin was registered", commandLine.getMixins().isEmpty());
        Assert.assertTrue(((commandLine.getMixins().get("aMixin")) instanceof MixMeIn));
        Receiver receiver = commandLine.getCommand();
        Assert.assertNotNull(receiver.mixMeIn);
        Assert.assertSame(receiver.mixMeIn, commandLine.getMixins().get("aMixin"));
        Assert.assertSame(receiver.mixMeIn, commandLine.getCommandSpec().mixins().get("aMixin").userObject());
    }

    @Test
    public void testMixinAnnotationCanBeRetrievedByFieldName() {
        @Command
        class MixMeIn {}
        @Command
        class Receiver {
            @Mixin
            MixMeIn mixMeIn;
        }
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        Assert.assertFalse("mixin was registered", commandLine.getMixins().isEmpty());
        Assert.assertTrue(((commandLine.getMixins().get("mixMeIn")) instanceof MixMeIn));
        Receiver receiver = commandLine.getCommand();
        Assert.assertNotNull(receiver.mixMeIn);
        Assert.assertSame(receiver.mixMeIn, commandLine.getMixins().get("mixMeIn"));
        Assert.assertSame(receiver.mixMeIn, commandLine.getCommandSpec().mixins().get("mixMeIn").userObject());
    }

    @Test
    public void testAddMixin_CanBeRetrievedByFieldName() {
        @Command
        class MixMeIn {}
        @Command
        class Receiver {}
        CommandLine commandLine = new CommandLine(new Receiver(), new InnerClassFactory(this));
        commandLine.addMixin("mixin", new MixMeIn());
        Assert.assertFalse("mixin was registered", commandLine.getMixins().isEmpty());
        Assert.assertTrue(((commandLine.getMixins().get("mixin")) instanceof MixMeIn));
    }

    @Test
    public void testMixinStandardHelpOptions_AreAddedLast() {
        @Command(mixinStandardHelpOptions = true, sortOptions = false)
        class App {
            @Option(names = "-a", description = "a option")
            boolean aOpt;

            @Option(names = "-z", description = "z option")
            boolean zOpt;
        }
        CommandLine commandLine = new CommandLine(new App(), new InnerClassFactory(this));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        commandLine.usage(new PrintStream(baos));
        String expected = String.format(("" + (((("Usage: <main class> [-ahVz]%n" + "  -a              a option%n") + "  -z              z option%n") + "  -h, --help      Show this help message and exit.%n") + "  -V, --version   Print version information and exit.%n")));
        Assert.assertEquals(expected, baos.toString());
    }

    static class Issue439Mixin {
        @Spec
        CommandSpec spec;

        @Option(names = "--trex")
        void setTRexFences(final String value) {
            throw new ParameterException(spec.commandLine(), "TREX error");
        }
    }

    static class Issue439Command {
        @Mixin
        CommandLineMixinTest.Issue439Mixin mixin;

        @Spec
        CommandSpec spec;

        @Option(names = "--raptor")
        void setRaptorFences(final String value) {
            throw new ParameterException(spec.commandLine(), "RAPTOR error");
        }
    }

    @Test
    public void testIssue439InjectedSpecInMixinHasNullCommandLineAnnotations() {
        CommandLine cmd = new CommandLine(new CommandLineMixinTest.Issue439Command());
        assertExceptionThrownFromSetter(cmd);
    }

    @Test
    public void testIssue439InjectedSpecInMixinHasNullCommandLineProgrammatic() {
        final CommandSpec mixinSpec = CommandSpec.create();
        ISetter trexSetter = new ISetter() {
            public <T> T set(T value) {
                throw new ParameterException(mixinSpec.commandLine(), "TREX error");
            }
        };
        mixinSpec.addOption(OptionSpec.builder("--trex").type(String.class).setter(trexSetter).build());
        final CommandSpec commandSpec = CommandSpec.create();
        commandSpec.addMixin("mixin", mixinSpec);
        ISetter raptorSetter = new ISetter() {
            public <T> T set(T value) {
                throw new ParameterException(commandSpec.commandLine(), "RAPTOR error");
            }
        };
        commandSpec.addOption(OptionSpec.builder("--raptor").type(String.class).setter(raptorSetter).build());
        CommandLine cmd = new CommandLine(commandSpec);
        assertExceptionThrownFromSetter(cmd);
    }

    @Command(name = "super")
    static class SuperClass {}

    @Command(name = "sub")
    static class SubClass extends CommandLineMixinTest.SuperClass {
        @Command(name = "method")
        public void method() {
        }
    }

    @Command(name = "main", subcommands = { CommandLineMixinTest.SuperClass.class, CommandLineMixinTest.SubClass.class })
    static class Main {}

    @Test
    public void testIssue619MethodSubcommandInSubclassAddedTwice() {
        // setTraceLevel("DEBUG");
        CommandLine commandLine = new CommandLine(new CommandLineMixinTest.Main());
        Assert.assertEquals(2, commandLine.getSubcommands().size());
        CommandLine zuper = commandLine.getSubcommands().get("super");
        Assert.assertEquals(0, zuper.getSubcommands().size());
        CommandLine sub = commandLine.getSubcommands().get("sub");
        Assert.assertEquals(1, sub.getSubcommands().size());
        CommandLine method = sub.getSubcommands().get("method");
        Assert.assertEquals(0, method.getSubcommands().size());
    }
}

