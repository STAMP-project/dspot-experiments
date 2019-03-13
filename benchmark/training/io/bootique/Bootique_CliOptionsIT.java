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
package io.bootique;


import CliConfigurationSource.CONFIG_OPTION;
import com.google.inject.ProvisionException;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.config.ConfigurationFactory;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.run.Runner;
import io.bootique.unit.BQInternalTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class Bootique_CliOptionsIT {
    @Rule
    public BQInternalTestFactory runtimeFactory = new BQInternalTestFactory();

    @Test
    public void testConfigOption() {
        BQRuntime runtime = runtimeFactory.app("--config=abc.yml").createRuntime();
        assertCollectionsEquals(runtime.getInstance(Cli.class).optionStrings(CONFIG_OPTION), "abc.yml");
    }

    @Test
    public void testConfigOptions() {
        BQRuntime runtime = runtimeFactory.app("--config=abc.yml", "--config=xyz.yml").createRuntime();
        assertCollectionsEquals(runtime.getInstance(Cli.class).optionStrings(CONFIG_OPTION), "abc.yml", "xyz.yml");
    }

    @Test
    public void testHelpOption() {
        BQRuntime runtime = runtimeFactory.app("--help").createRuntime();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("help"));
    }

    @Test
    public void testHelpOption_Short() {
        BQRuntime runtime = runtimeFactory.app("-h").createRuntime();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("help"));
    }

    @Test
    public void testNoHelpOption() {
        BQRuntime runtime = runtimeFactory.app("a", "b").createRuntime();
        Assert.assertFalse(runtime.getInstance(Cli.class).hasOption("help"));
    }

    @Test
    public void testOverlappingOptions() {
        BQRuntime runtime = runtimeFactory.app("--o1").module(( b) -> BQCoreModule.extend(b).addOptions(OptionMetadata.builder("o1").build(), OptionMetadata.builder("o2").build())).createRuntime();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("o1"));
        Assert.assertFalse(runtime.getInstance(Cli.class).hasOption("o2"));
    }

    @Test(expected = ProvisionException.class)
    public void testNameConflict_TwoOptions() {
        runtimeFactory.app().module(( b) -> BQCoreModule.extend(b).addOptions(OptionMetadata.builder("opt1").build(), OptionMetadata.builder("opt1").build())).createRuntime().run();
    }

    @Test(expected = ProvisionException.class)
    public void testNameConflict_TwoCommands() {
        runtimeFactory.app().module(( b) -> BQCoreModule.extend(b).addCommand(.class).addCommand(.class)).createRuntime().run();
    }

    // TODO: Same name of option and command should be disallowed.
    // This test is broken, it is here just to document current behaviour.
    @Test
    public void testCommandWithOptionNameOverlap() {
        BQRuntime runtime = runtimeFactory.app("-x").module(( b) -> BQCoreModule.extend(b).addCommand(.class).addOption(OptionMetadata.builder("xd").build())).createRuntime();
        runtime.run();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("xd"));
    }

    @Test(expected = ProvisionException.class)
    public void testCommand_IllegalShort() {
        BQRuntime runtime = runtimeFactory.app("-x").module(( b) -> BQCoreModule.extend(b).addCommand(.class)).createRuntime();
        runtime.getInstance(Cli.class);
    }

    @Test
    public void testCommand_ExplicitShort() {
        BQRuntime runtime = runtimeFactory.app("-A").module(( b) -> BQCoreModule.extend(b).addCommand(.class)).createRuntime();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("xa"));
    }

    @Test(expected = ProvisionException.class)
    public void testOverlappingCommands_IllegalShort() {
        BQRuntime runtime = runtimeFactory.app("-x").module(( b) -> BQCoreModule.extend(b).addCommand(.class).addCommand(.class)).createRuntime();
        runtime.getInstance(Cli.class);
    }

    @Test(expected = ProvisionException.class)
    public void testIllegalAbbreviation() {
        BQRuntime runtime = runtimeFactory.app("--xc").module(( b) -> BQCoreModule.extend(b).addCommand(.class)).createRuntime();
        runtime.getInstance(Cli.class);
    }

    @Test
    public void testOverlappingCommands_Short() {
        BQRuntime runtime = runtimeFactory.app("-A").module(( b) -> BQCoreModule.extend(b).addCommand(.class).addCommand(.class)).createRuntime();
        Assert.assertTrue(runtime.getInstance(Cli.class).hasOption("xa"));
        Assert.assertFalse(runtime.getInstance(Cli.class).hasOption("xb"));
    }

    @Test
    public void testDefaultCommandOptions() {
        BQRuntime runtime = runtimeFactory.app("-l", "x", "--long=y", "-s").module(( binder) -> BQCoreModule.extend(binder).setDefaultCommand(.class)).createRuntime();
        Cli cli = runtime.getInstance(Cli.class);
        Assert.assertTrue(cli.hasOption("s"));
        Assert.assertEquals("x_y", String.join("_", cli.optionStrings("long")));
    }

    @Test
    public void testOption_OverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x").module(( binder) -> BQCoreModule.extend(binder).addOptions(OptionMetadata.builder("opt-1").valueOptional().build(), OptionMetadata.builder("opt-2").valueOptionalWithDefault("2").build()).mapConfigPath("opt-1", "c.m.l").mapConfigPath("opt-2", "c.m.k")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals("e", bean1.a);
        Assert.assertEquals("x", bean1.c.m.l);
        Assert.assertEquals(1, bean1.c.m.k);
    }

    @Test
    public void testOptionPathAbsentInYAML() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x").module(( binder) -> BQCoreModule.extend(binder).addOption(OptionMetadata.builder("opt-1").valueOptional().build()).mapConfigPath("opt-1", "c.m.f")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals("x", bean1.c.m.f);
    }

    @Test
    public void testOptionsCommandAndModuleOverlapping() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--cmd-1", "--opt-1").module(( binder) -> BQCoreModule.extend(binder).addOption(OptionMetadata.builder("opt-1").valueOptionalWithDefault("2").build()).mapConfigPath("opt-1", "c.m.k").addCommand(new io.bootique.TestOptionCommand1())).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Runner runner = runtime.getInstance(Runner.class);
        runner.run();
        Assert.assertEquals(2, bean1.c.m.k);
    }

    @Test
    public void testOptionsOrder_OnCLI() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt-1", "--opt-2=y", "--opt-1=x").module(( binder) -> BQCoreModule.extend(binder).addConfig("classpath:io/bootique/config/test4Copy.yml").addOptions(OptionMetadata.builder("opt-1").valueOptional().build(), OptionMetadata.builder("opt-2").valueOptional().build(), OptionMetadata.builder("file-opt-1").build()).mapConfigPath("opt-1", "c.m.f").mapConfigPath("opt-2", "c.m.f").mapConfigResource("file-opt-1", "classpath:io/bootique/config/configTest4Opt1.yml").mapConfigResource("file-opt-1", "classpath:io/bootique/config/configTest4Decorate.yml")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals(4, bean1.c.m.k);
        Assert.assertEquals("x", bean1.c.m.f);
        Assert.assertEquals("copy", bean1.c.m.l);
        Assert.assertEquals("e", bean1.a);
    }

    @Test
    public void testOptionsWithOverlappingPath_OverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-2", "--opt-3").module(( binder) -> BQCoreModule.extend(binder).addOptions(OptionMetadata.builder("opt-1").valueOptional().build(), OptionMetadata.builder("opt-2").valueOptionalWithDefault("2").build(), OptionMetadata.builder("opt-3").valueOptionalWithDefault("3").build()).mapConfigPath("opt-1", "c.m.k").mapConfigPath("opt-2", "c.m.k").mapConfigPath("opt-3", "c.m.k")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals(3, bean1.c.m.k);
    }

    @Test(expected = ProvisionException.class)
    public void testOptionWithNotMappedConfigPath() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x").module(( binder) -> BQCoreModule.extend(binder).mapConfigPath("opt-1", "c.m.k.x")).createRuntime();
        runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
    }

    @Test
    public void testOptionConfigFile_OverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt").module(( binder) -> BQCoreModule.extend(binder).addOption(OptionMetadata.builder("file-opt").build()).mapConfigResource("file-opt", "classpath:io/bootique/config/configTest4.yml")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals("x", bean1.c.m.l);
    }

    @Test
    public void testMultipleOptionsConfigFiles_OverrideInCLIOrder() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt-2", "--file-opt-1").module(( binder) -> BQCoreModule.extend(binder).addOptions(OptionMetadata.builder("file-opt-1").build(), OptionMetadata.builder("file-opt-2").build()).mapConfigPath("opt-1", "c.m.f").mapConfigResource("file-opt-1", "classpath:io/bootique/config/configTest4Opt1.yml").mapConfigResource("file-opt-2", "classpath:io/bootique/config/configTest4Opt2.yml")).createRuntime();
        Bootique_CliOptionsIT.Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_CliOptionsIT.Bean1.class, "");
        Assert.assertEquals(2, bean1.c.m.k);
        Assert.assertEquals("f", bean1.c.m.f);
    }

    @Test
    public void testOptionDefaultValue() {
        BQRuntime runtime = runtimeFactory.app("--option").module(( b) -> BQCoreModule.extend(b).addOptions(OptionMetadata.builder("option").valueOptionalWithDefault("val").build())).createRuntime();
        Cli cli = runtime.getInstance(Cli.class);
        Assert.assertTrue(cli.hasOption("option"));
        Assert.assertEquals("val", cli.optionString("option"));
    }

    @Test
    public void testMissingOptionDefaultValue() {
        BQRuntime runtime = runtimeFactory.app().module(( b) -> BQCoreModule.extend(b).addOptions(OptionMetadata.builder("option").valueOptionalWithDefault("val").build())).createRuntime();
        Cli cli = runtime.getInstance(Cli.class);
        // Check that no value is set if option is missing in args
        Assert.assertFalse(cli.hasOption("option"));
        Assert.assertNull(cli.optionString("option"));
    }

    @Test
    public void testCommandWithOptionWithDefaultValue() {
        BQRuntime runtime = runtimeFactory.app("-cmd", "--option").module(( b) -> BQCoreModule.extend(b).addCommand(.class)).createRuntime();
        Cli cli = runtime.getInstance(Cli.class);
        Assert.assertTrue(cli.hasOption("o"));
        Assert.assertEquals("val", cli.optionString("o"));
    }

    static final class TestCommand extends CommandWithMetadata {
        public TestCommand() {
            super(CommandMetadata.builder(Bootique_CliOptionsIT.TestCommand.class).addOption(OptionMetadata.builder("long").valueRequired()).addOption(OptionMetadata.builder("s")));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class XaCommand extends CommandWithMetadata {
        public XaCommand() {
            super(CommandMetadata.builder(Bootique_CliOptionsIT.XaCommand.class).shortName('A'));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class XbCommand extends CommandWithMetadata {
        public XbCommand() {
            super(CommandMetadata.builder(Bootique_CliOptionsIT.XbCommand.class).shortName('B'));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class XccCommand extends CommandWithMetadata {
        public XccCommand() {
            super(CommandMetadata.builder(Bootique_CliOptionsIT.XccCommand.class).shortName('B'));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class Xd1Command extends CommandWithMetadata {
        public Xd1Command() {
            super(CommandMetadata.builder("xd"));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class Xd2Command extends CommandWithMetadata {
        public Xd2Command() {
            super(CommandMetadata.builder("xd"));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class XeCommand extends CommandWithMetadata {
        public XeCommand() {
            super(CommandMetadata.builder("xe").addOption(OptionMetadata.builder("opt1").build()));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class TestOptionCommand1 extends CommandWithMetadata {
        public TestOptionCommand1() {
            super(CommandMetadata.builder(Bootique_CliOptionsIT.TestOptionCommand1.class).name("cmd-1").addOption(OptionMetadata.builder("opt-1").build()));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static final class CommandWithDefaultOptionValue extends CommandWithMetadata {
        public CommandWithDefaultOptionValue() {
            super(CommandMetadata.builder("cmd").addOption(OptionMetadata.builder("option").valueOptionalWithDefault("val").build()));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static class Bean1 {
        private String a;

        private Bootique_CliOptionsIT.Bean2 c;

        public void setA(String a) {
            this.a = a;
        }

        public void setC(Bootique_CliOptionsIT.Bean2 c) {
            this.c = c;
        }
    }

    static class Bean2 {
        private Bootique_CliOptionsIT.Bean3 m;

        public void setM(Bootique_CliOptionsIT.Bean3 m) {
            this.m = m;
        }
    }

    static class Bean3 {
        private int k;

        private String f;

        private String l;

        public void setK(int k) {
            this.k = k;
        }

        public void setF(String f) {
            this.f = f;
        }

        public void setL(String l) {
            this.l = l;
        }
    }
}

