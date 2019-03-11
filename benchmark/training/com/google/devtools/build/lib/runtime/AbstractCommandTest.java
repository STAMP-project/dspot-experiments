/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.runtime;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link BlazeCommand}.
 */
@RunWith(JUnit4.class)
public class AbstractCommandTest {
    public static class FooOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int foo;
    }

    public static class BarOptions extends OptionsBase {
        @Option(name = "bar", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "42")
        public int foo;

        @Option(name = "baz", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "oops")
        public String baz;
    }

    private static class ConcreteCommand implements BlazeCommand {
        @Override
        public BlazeCommandResult exec(CommandEnvironment env, OptionsParsingResult options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void editOptions(OptionsParser optionsParser) {
        }
    }

    @Command(name = "test_name", help = "Usage: some funny usage for %{command} ...;\n\n%{options}; end", options = { AbstractCommandTest.FooOptions.class, AbstractCommandTest.BarOptions.class }, shortDescription = "a short description", allowResidue = false)
    private static class TestCommand extends AbstractCommandTest.ConcreteCommand {}

    @Test
    public void testGetNameYieldsAnnotatedName() {
        assertThat(new AbstractCommandTest.TestCommand().getClass().getAnnotation(Command.class).name()).isEqualTo("test_name");
    }

    @Test
    public void testGetOptionsYieldsAnnotatedOptions() {
        ConfiguredRuleClassProvider ruleClassProvider = new ConfiguredRuleClassProvider.Builder().setToolsRepository(TestConstants.TOOLS_REPOSITORY).build();
        assertThat(BlazeCommandUtils.getOptions(AbstractCommandTest.TestCommand.class, ImmutableList.<BlazeModule>of(), ruleClassProvider)).containsExactlyElementsIn(optionClassesWithDefault(AbstractCommandTest.FooOptions.class, AbstractCommandTest.BarOptions.class));
    }

    /**
     * *************************************************************************
     * The tests below test how a command interacts with the dispatcher except *
     * for execution, which is tested in {@link BlazeCommandDispatcherTest}.   *
     * *************************************************************************
     */
    @Command(name = "a", options = { AbstractCommandTest.FooOptions.class }, shortDescription = "", help = "")
    private static class CommandA extends AbstractCommandTest.ConcreteCommand {}

    @Command(name = "b", options = { AbstractCommandTest.BarOptions.class }, inherits = { AbstractCommandTest.CommandA.class }, shortDescription = "", help = "")
    private static class CommandB extends AbstractCommandTest.ConcreteCommand {}

    @Test
    public void testOptionsAreInherited() {
        ConfiguredRuleClassProvider ruleClassProvider = new ConfiguredRuleClassProvider.Builder().setToolsRepository(TestConstants.TOOLS_REPOSITORY).build();
        assertThat(BlazeCommandUtils.getOptions(AbstractCommandTest.CommandA.class, ImmutableList.<BlazeModule>of(), ruleClassProvider)).containsExactlyElementsIn(optionClassesWithDefault(AbstractCommandTest.FooOptions.class));
        assertThat(BlazeCommandUtils.getOptions(AbstractCommandTest.CommandB.class, ImmutableList.<BlazeModule>of(), ruleClassProvider)).containsExactlyElementsIn(optionClassesWithDefault(AbstractCommandTest.FooOptions.class, AbstractCommandTest.BarOptions.class));
    }
}

