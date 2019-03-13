/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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


import Converters.BooleanConverter;
import InvocationPolicy.Builder;
import OptionsParser.ConstructionException;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.runtime.proto.InvocationPolicyOuterClass.InvocationPolicy;
import com.google.devtools.build.lib.runtime.proto.InvocationPolicyOuterClass.UseDefault;
import com.google.devtools.common.options.ExpansionFunction;
import com.google.devtools.common.options.InvocationPolicyEnforcer;
import com.google.devtools.common.options.IsolatedOptionsData;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionMetadataTag;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the Incompatible Changes system (--incompatible_* flags). These go in their own suite
 * because the options parser doesn't know the business logic for incompatible changes.
 */
@RunWith(JUnit4.class)
public class AllIncompatibleChangesExpansionTest {
    /**
     * Dummy comment (linter suppression)
     */
    public static class ExampleOptions extends OptionsBase {
        @Option(name = "all", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", expansionFunction = AllIncompatibleChangesExpansion.class)
        public Void all;

        @Option(name = "X", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean x;

        @Option(name = "Y", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true")
        public boolean y;

        @Option(name = "incompatible_A", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "Migrate to A")
        public boolean incompatibleA;

        @Option(name = "incompatible_B", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "Migrate to B")
        public boolean incompatibleB;

        @Option(name = "incompatible_C", oldName = "experimental_C", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true", help = "Migrate to C")
        public boolean incompatibleC;
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class ExampleExpansionOptions extends OptionsBase {
        @Option(name = "incompatible_expX", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", expansion = { "--X" }, help = "Start using X")
        public Void incompatibleExpX;

        /**
         * Dummy comment (linter suppression)
         */
        public static class NoYExpansion implements ExpansionFunction {
            @Override
            public ImmutableList<String> getExpansion(IsolatedOptionsData optionsData) {
                return ImmutableList.of("--noY");
            }
        }

        @Option(name = "incompatible_expY", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", expansionFunction = AllIncompatibleChangesExpansionTest.ExampleExpansionOptions.NoYExpansion.class, help = "Stop using Y")
        public Void incompatibleExpY;
    }

    @Test
    public void noChangesSelected() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        parser.parse("");
        AllIncompatibleChangesExpansionTest.ExampleOptions opts = parser.getOptions(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        assertThat(opts.x).isFalse();
        assertThat(opts.y).isTrue();
        assertThat(opts.incompatibleA).isFalse();
        assertThat(opts.incompatibleB).isFalse();
        assertThat(opts.incompatibleC).isTrue();
    }

    @Test
    public void allChangesSelected() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        parser.parse("--all");
        AllIncompatibleChangesExpansionTest.ExampleOptions opts = parser.getOptions(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        assertThat(opts.x).isFalse();
        assertThat(opts.y).isTrue();
        assertThat(opts.incompatibleA).isTrue();
        assertThat(opts.incompatibleB).isTrue();
        assertThat(opts.incompatibleC).isTrue();
    }

    @Test
    public void rightmostOverrides() throws OptionsParsingException {
        // Check that all-expansion behaves just like any other expansion flag:
        // the rightmost setting of any individual option wins.
        OptionsParser parser = OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        parser.parse("--noincompatible_A", "--all", "--noincompatible_B");
        AllIncompatibleChangesExpansionTest.ExampleOptions opts = parser.getOptions(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        assertThat(opts.incompatibleA).isTrue();
        assertThat(opts.incompatibleB).isFalse();
    }

    @Test
    public void expansionOptions() throws OptionsParsingException {
        // Check that all-expansion behaves just like any other expansion flag:
        // the rightmost setting of any individual option wins.
        OptionsParser parser = OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class, AllIncompatibleChangesExpansionTest.ExampleExpansionOptions.class);
        parser.parse("--all");
        AllIncompatibleChangesExpansionTest.ExampleOptions opts = parser.getOptions(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        assertThat(opts.x).isTrue();
        assertThat(opts.y).isFalse();
        assertThat(opts.incompatibleA).isTrue();
        assertThat(opts.incompatibleB).isTrue();
    }

    @Test
    public void invocationPolicy() throws OptionsParsingException {
        // Check that all-expansion behaves just like any other expansion flag and can be filtered
        // by invocation policy.
        InvocationPolicy.Builder invocationPolicyBuilder = InvocationPolicy.newBuilder();
        invocationPolicyBuilder.addFlagPoliciesBuilder().setFlagName("incompatible_A").setUseDefault(UseDefault.getDefaultInstance()).build();
        InvocationPolicy policy = invocationPolicyBuilder.build();
        InvocationPolicyEnforcer enforcer = new InvocationPolicyEnforcer(policy);
        OptionsParser parser = OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        parser.parse("--all");
        enforcer.enforce(parser);
        AllIncompatibleChangesExpansionTest.ExampleOptions opts = parser.getOptions(AllIncompatibleChangesExpansionTest.ExampleOptions.class);
        assertThat(opts.x).isFalse();
        assertThat(opts.y).isTrue();
        assertThat(opts.incompatibleA).isFalse();// A should have been removed from the expansion.

        assertThat(opts.incompatibleB).isTrue();// B, without a policy, should have been left alone.

    }

    /**
     * Option with the right prefix, but the wrong metadata tag.
     */
    public static class IncompatibleChangeTagOption extends OptionsBase {
        @Option(name = "some_option_with_a_tag", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE }, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp")
        public boolean opt;
    }

    @Test
    public void incompatibleChangeTagDoesNotTriggerAllIncompatibleChangesCheck() {
        try {
            OptionsParser.newOptionsParser(AllIncompatibleChangesExpansionTest.ExampleOptions.class, AllIncompatibleChangesExpansionTest.IncompatibleChangeTagOption.class);
        } catch (OptionsParser e) {
            Assert.fail((("some_option_with_a_tag should not trigger the expansion, so there should be no checks " + ("on it having the right prefix and metadata tags. Instead, the following exception " + "was thrown: ")) + (e.getMessage())));
        }
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadNameOptions extends OptionsBase {
        @Option(name = "badname", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp")
        public boolean bad;
    }

    @Test
    public void badName() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadNameOptions.class, ("Incompatible change option '--badname' must have name " + "starting with \"incompatible_\""));
    }

    /**
     * Option with the right prefix, but the wrong metadata tag.
     */
    public static class MissingTriggeredByTagOptions extends OptionsBase {
        @Option(name = "incompatible_bad", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE }, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp")
        public boolean bad;
    }

    @Test
    public void badTag() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.MissingTriggeredByTagOptions.class, "must have metadata tag OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES");
    }

    /**
     * Option with the right prefix, but the wrong metadata tag.
     */
    public static class MissingIncompatibleTagOptions extends OptionsBase {
        @Option(name = "incompatible_bad", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, metadataTags = { OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp")
        public boolean bad;
    }

    @Test
    public void otherBadTag() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.MissingIncompatibleTagOptions.class, "must have metadata tag OptionMetadataTag.INCOMPATIBLE_CHANGE");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadTypeOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0", help = "nohelp")
        public int bad;
    }

    @Test
    public void badType() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadTypeOptions.class, "must have boolean type");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadHelpOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean bad;
    }

    @Test
    public void badHelp() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadHelpOptions.class, "must have a \"help\" string");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadAbbrevOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp", abbrev = 'x')
        public boolean bad;
    }

    @Test
    public void badAbbrev() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadAbbrevOptions.class, "must not use the abbrev field");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadValueHelpOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp", valueHelp = "x")
        public boolean bad;
    }

    @Test
    public void badValueHelp() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadValueHelpOptions.class, "must not use the valueHelp field");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadConverterOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp", converter = BooleanConverter.class)
        public boolean bad;
    }

    @Test
    public void badConverter() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadConverterOptions.class, "must not use the converter field");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadAllowMultipleOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", help = "nohelp", allowMultiple = true)
        public List<String> bad;
    }

    @Test
    public void badAllowMutliple() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadAllowMultipleOptions.class, "must not use the allowMultiple field");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadImplicitRequirementsOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp", implicitRequirements = "--x")
        public boolean bad;
    }

    @Test
    public void badImplicitRequirements() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadImplicitRequirementsOptions.class, "must not use the implicitRequirements field");
    }

    /**
     * Dummy comment (linter suppression)
     */
    public static class BadOldNameOptions extends OptionsBase {
        @Option(name = "incompatible_bad", metadataTags = { OptionMetadataTag.INCOMPATIBLE_CHANGE, OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES }, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false", help = "nohelp", oldName = "x")
        public boolean bad;
    }

    @Test
    public void badOldName() {
        AllIncompatibleChangesExpansionTest.assertBadness(AllIncompatibleChangesExpansionTest.BadOldNameOptions.class, "must not use the oldName field");
    }
}

