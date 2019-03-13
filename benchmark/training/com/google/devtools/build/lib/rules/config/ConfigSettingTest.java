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
package com.google.devtools.build.lib.rules.config;


import BuildConfiguration.Fragment;
import LicenseType.NONE;
import LicenseType.RESTRICTED;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.ConfigurationFragmentFactory;
import com.google.devtools.build.lib.analysis.config.FragmentOptions;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDefinition;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionMetadataTag;
import com.google.devtools.common.options.OptionsParser;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ConfigSetting}.
 */
@RunWith(JUnit4.class)
public class ConfigSettingTest extends BuildViewTestCase {
    /**
     * Extra options for this test.
     */
    public static class DummyTestOptions extends FragmentOptions {
        public DummyTestOptions() {
        }

        @Option(name = "internal_option", documentationCategory = OptionDocumentationCategory.UNDOCUMENTED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "super secret", metadataTags = { OptionMetadataTag.INTERNAL })
        public String internalOption;

        @Option(name = "nonselectable_option", documentationCategory = OptionDocumentationCategory.UNDOCUMENTED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true")
        public boolean nonselectableOption;

        private static final OptionDefinition NONSELECTABLE_OPTION_DEFINITION = OptionsParser.getOptionDefinitionByName(ConfigSettingTest.DummyTestOptions.class, "nonselectable_option");

        @Option(name = "nonselectable_whitelisted_option", documentationCategory = OptionDocumentationCategory.UNDOCUMENTED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true")
        public boolean nonselectableWhitelistedOption;

        private static final OptionDefinition NONSELECTABLE_WHITELISTED_OPTION_DEFINITION = OptionsParser.getOptionDefinitionByName(ConfigSettingTest.DummyTestOptions.class, "nonselectable_whitelisted_option");

        @Option(name = "nonselectable_custom_message_option", documentationCategory = OptionDocumentationCategory.UNDOCUMENTED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true")
        public boolean nonselectableCustomMessageOption;

        private static final OptionDefinition NONSELECTABLE_CUSTOM_MESSAGE_OPTION_DEFINITION = OptionsParser.getOptionDefinitionByName(ConfigSettingTest.DummyTestOptions.class, "nonselectable_custom_message_option");

        @Override
        public Map<OptionDefinition, SelectRestriction> getSelectRestrictions() {
            return ImmutableMap.of(ConfigSettingTest.DummyTestOptions.NONSELECTABLE_OPTION_DEFINITION, /* visibleWithinToolsPackage= */
            /* errorMessage= */
            new SelectRestriction(false, null), ConfigSettingTest.DummyTestOptions.NONSELECTABLE_WHITELISTED_OPTION_DEFINITION, /* visibleWithinToolsPackage= */
            /* errorMessage= */
            new SelectRestriction(true, null), ConfigSettingTest.DummyTestOptions.NONSELECTABLE_CUSTOM_MESSAGE_OPTION_DEFINITION, /* visibleWithinToolsPackage= */
            /* errorMessage= */
            new SelectRestriction(false, "For very important reasons."));
        }
    }

    @AutoCodec
    static class DummyTestOptionsFragment extends BuildConfiguration.Fragment {}

    private static class DummyTestOptionsLoader implements ConfigurationFragmentFactory {
        @Override
        public Fragment create(BuildOptions buildOptions) {
            return new ConfigSettingTest.DummyTestOptionsFragment();
        }

        @Override
        public Class<? extends BuildConfiguration.Fragment> creates() {
            return ConfigSettingTest.DummyTestOptionsFragment.class;
        }

        @Override
        public ImmutableSet<Class<? extends FragmentOptions>> requiredOptions() {
            return ImmutableSet.<Class<? extends FragmentOptions>>of(ConfigSettingTest.DummyTestOptions.class);
        }
    }

    /**
     * Checks the behavior of {@link ConfigSetting#isUnderToolsPackage}.
     */
    @Test
    public void isUnderToolsPackage() throws Exception {
        RepositoryName toolsRepo = RepositoryName.create("@tools");
        // Subpackage of the tools package.
        assertThat(ConfigSetting.isUnderToolsPackage(Label.parseAbsoluteUnchecked("@tools//tools/subpkg:foo"), toolsRepo)).isTrue();
        // The tools package itself.
        assertThat(ConfigSetting.isUnderToolsPackage(Label.parseAbsoluteUnchecked("@tools//tools:foo"), toolsRepo)).isTrue();
        // The tools repo, but wrong package.
        assertThat(ConfigSetting.isUnderToolsPackage(Label.parseAbsoluteUnchecked("@tools//nottools:foo"), toolsRepo)).isFalse();
        // Not even the tools repo.
        assertThat(ConfigSetting.isUnderToolsPackage(Label.parseAbsoluteUnchecked("@nottools//nottools:foo"), toolsRepo)).isFalse();
        // A tools package but in the wrong repo.
        assertThat(ConfigSetting.isUnderToolsPackage(Label.parseAbsoluteUnchecked("@nottools//tools:foo"), toolsRepo)).isFalse();
    }

    /**
     * Tests that a config_setting only matches build configurations where *all* of
     * its flag specifications match.
     */
    @Test
    public void matchingCriteria() throws Exception {
        writeSimpleExample();
        // First flag mismatches:
        useConfiguration("-c", "opt", "--stamp");
        assertThat(getConfigMatchingProvider("//pkg:foo").matches()).isFalse();
        // Second flag mismatches:
        useConfiguration("-c", "dbg", "--nostamp");
        assertThat(getConfigMatchingProvider("//pkg:foo").matches()).isFalse();
        // Both flags mismatch:
        useConfiguration("-c", "opt", "--nostamp");
        assertThat(getConfigMatchingProvider("//pkg:foo").matches()).isFalse();
        // Both flags match:
        useConfiguration("-c", "dbg", "--stamp");
        assertThat(getConfigMatchingProvider("//pkg:foo").matches()).isTrue();
    }

    /**
     * Tests that {@link ConfigMatchingProvider#label} is correct.
     */
    @Test
    public void labelGetter() throws Exception {
        writeSimpleExample();
        assertThat(getConfigMatchingProvider("//pkg:foo").label()).isEqualTo(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()));
    }

    /**
     * Tests that rule analysis fails on unknown options.
     */
    @Test
    public void unknownOption() throws Exception {
        checkError("foo", "badoption", "unknown option: 'not_an_option'", "config_setting(", "    name = 'badoption',", "    values = {'not_an_option': 'bar'})");
    }

    /**
     * Tests that rule analysis fails on internal options.
     */
    @Test
    public void internalOption() throws Exception {
        checkError("foo", "badoption", "unknown option: 'internal_option'", "config_setting(", "    name = 'badoption',", "    values = {'internal_option': 'bar'})");
    }

    /**
     * Tests that rule analysis fails on invalid option values.
     */
    @Test
    public void invalidOptionValue() throws Exception {
        checkError("foo", "badvalue", "Not a valid compilation mode: 'baz'", "config_setting(", "    name = 'badvalue',", "    values = {'compilation_mode': 'baz'})");
    }

    /**
     * Tests that when the first option is valid but the config_setting doesn't match,
     * remaining options are still validity-checked.
     */
    @Test
    public void invalidOptionFartherDown() throws Exception {
        checkError("foo", "badoption", "unknown option: 'not_an_option'", "config_setting(", "    name = 'badoption',", "    values = {", "        'compilation_mode': 'opt',", "        'not_an_option': 'bar',", "    })");
    }

    /**
     * Tests that analysis fails on non-selectable options.
     */
    @Test
    public void nonselectableOption() throws Exception {
        checkError("foo", "badoption", "option 'nonselectable_option' cannot be used in a config_setting", "config_setting(", "    name = 'badoption',", "    values = {", "        'nonselectable_option': 'true',", "    },", ")");
    }

    /**
     * Tests that whitelisted non-selectable options can't be accessed outside of the tools package.
     */
    @Test
    public void nonselectableWhitelistedOption_OutOfToolsPackage() throws Exception {
        checkError("foo", "badoption", String.format(("option 'nonselectable_whitelisted_option' cannot be used in a config_setting (it is " + "whitelisted to %s//tools/... only)"), RepositoryName.create(TestConstants.TOOLS_REPOSITORY).getDefaultCanonicalForm()), "config_setting(", "    name = 'badoption',", "    values = {", "        'nonselectable_whitelisted_option': 'true',", "    },", ")");
    }

    /**
     * Tests that whitelisted non-selectable options can be accessed within the tools package.
     */
    @Test
    public void nonselectableWhitelistedOption_InToolsPackage() throws Exception {
        scratch.file(((TestConstants.TOOLS_REPOSITORY_SCRATCH) + "tools/pkg/BUILD"), "config_setting(", "    name = 'foo',", "    values = {", "        'nonselectable_whitelisted_option': 'true',", "    })");
        String fooLabel = (TestConstants.TOOLS_REPOSITORY) + "//tools/pkg:foo";
        assertThat(getConfigMatchingProvider(fooLabel).matches()).isTrue();
    }

    /**
     * Tests that custom error messages are displayed for non-selectable options.
     */
    @Test
    public void nonselectableCustomMessageOption() throws Exception {
        checkError("foo", "badoption", ("option 'nonselectable_custom_message_option' cannot be used in a config_setting. " + "For very important reasons."), "config_setting(", "    name = 'badoption',", "    values = {", "        'nonselectable_custom_message_option': 'true',", "    },", ")");
    }

    /**
     * Tests that *some* settings (values or flag_values) must be specified.
     */
    @Test
    public void emptySettings() throws Exception {
        checkError("foo", "empty", ("in config_setting rule //foo:empty: " + "Either values, flag_values or constraint_values must be specified and non-empty"), "config_setting(", "    name = 'empty',", "    values = {})");
    }

    /**
     * Tests matching on multi-value attributes with key=value entries (e.g. --define).
     */
    @Test
    public void multiValueDict() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'define': 'foo=bar',", "    })");
        useConfiguration("");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo=bar");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--define", "foo=baz");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo=bar", "--define", "bar=baz");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--define", "foo=bar", "--define", "bar=baz", "--define", "foo=nope");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo=nope", "--define", "bar=baz", "--define", "foo=bar");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void multipleDefines() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    define_values = {", "        'foo1': 'bar',", "        'foo2': 'baz',", "    })");
        useConfiguration("");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo1=bar");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo2=baz");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "foo1=bar", "--define", "foo2=baz");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void definesCrossAttributes() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'define': 'a=c'", "    },", "    define_values = {", "        'b': 'd',", "    })");
        useConfiguration("");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "a=c");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "b=d");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "a=c", "--define", "b=d");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    /**
     * Tests matching on multi-value attributes with primitive values.
     */
    @Test
    public void multiValueList() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'copt': '-Dfoo',", "    })");
        useConfiguration("");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--copt", "-Dfoo");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--copt", "-Dbar");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--copt", "-Dfoo", "--copt", "-Dbar");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--copt", "-Dbar", "--copt", "-Dfoo");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void selectForDefaultCrosstoolTop() throws Exception {
        String crosstoolTop = (TestConstants.TOOLS_REPOSITORY) + "//tools/cpp:toolchain";
        scratchConfiguredTarget("a", "a", (("config_setting(name='cs', values={'crosstool_top': '" + crosstoolTop) + "'})"), "sh_library(name='a', srcs=['a.sh'], deps=select({':cs': []}))");
    }

    @Test
    public void selectForDefaultGrteTop() throws Exception {
        scratchConfiguredTarget("a", "a", "config_setting(name='cs', values={'grte_top': 'default'})", "sh_library(name='a', srcs=['a.sh'], deps=select({':cs': []}))");
    }

    @Test
    public void requiredConfigFragmentMatcher() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'copt': '-Dfoo',", "        'javacopt': '-Dbar'", "    })");
        Rule target = ((Rule) (getTarget("//test:match")));
        assertThat(target.getRuleClassObject().getOptionReferenceFunction().apply(target)).containsExactly("copt", "javacopt");
    }

    @Test
    public void matchesIfFlagValuesAndValuesBothMatch() throws Exception {
        useConfiguration("--copt=-Dright", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void matchesIfFlagValuesMatchAndValuesAreEmpty() throws Exception {
        useConfiguration("--copt=-Dright", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {},", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void matchesIfValuesMatchAndFlagValuesAreEmpty() throws Exception {
        useConfiguration("--copt=-Dright");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {},", "    values = {", "        'copt': '-Dright',", "    },", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void doesNotMatchIfNeitherFlagValuesNorValuesMatches() throws Exception {
        useConfiguration("--copt=-Dright", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'wrong',", "    },", "    values = {", "        'copt': '-Dwrong',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void doesNotMatchIfFlagValuesDoNotMatchAndValuesAreEmpty() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'wrong',", "    },", "    values = {},", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void doesNotMatchIfFlagValuesDoNotMatchButValuesDo() throws Exception {
        useConfiguration("--copt=-Dright", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'wrong',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void doesNotMatchIfValuesDoNotMatchAndFlagValuesAreEmpty() throws Exception {
        useConfiguration("--copt=-Dright");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {},", "    values = {", "        'copt': '-Dwrong',", "    },", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void doesNotMatchIfValuesDoNotMatchButFlagValuesDo() throws Exception {
        useConfiguration("--copt=-Dright", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dwrong',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void doesNotMatchIfEvenOneFlagValueDoesNotMatch() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'bad',", "    },", "    values = {},", "    transitive_configs = [':flag', ':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void matchesIfNonDefaultIsSpecifiedAndFlagValueIsThatValue() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'setter',", "    exports_setting = ':match',", "    flag_values = {':flag': 'actual'},", "    transitive_configs = [':flag'],", ")", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'actual',", "    },", "    values = {},", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'actual'],", "    default_value = 'default',", ")");
        assertThat(getConfigMatchingProvider("//test:setter").matches()).isTrue();
    }

    @Test
    public void doesNotMatchIfDefaultIsSpecifiedAndFlagValueIsNotDefault() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'setter',", "    exports_setting = ':match',", "    flag_values = {':flag': 'actual'},", "    transitive_configs = [':flag'],", ")", "config_setting(", "    name = 'match',", "    flag_values = {", "        ':flag': 'default',", "    },", "    values = {},", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'actual'],", "    default_value = 'default',", ")");
        assertThat(getConfigMatchingProvider("//test:setter").matches()).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithSameValuesAndSameFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithDifferentValuesAndSameFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithSameValuesAndDifferentFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithDifferentValuesAndDifferentFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag2': 'good',", "    },", "    values = {", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithDifferentValuesAndSubsetFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void doesNotRefineSettingWithSubsetValuesAndDifferentFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isFalse();
    }

    @Test
    public void refinesSettingWithSubsetValuesAndSameFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isTrue();
    }

    @Test
    public void refinesSettingWithSameValuesAndSubsetFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isTrue();
    }

    @Test
    public void refinesSettingWithSubsetValuesAndSubsetFlagValues() throws Exception {
        useConfiguration("--copt=-Dright", "--javacopt=-Dgood", "--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':flag': 'right',", "        ':flag2': 'good',", "    },", "    values = {", "        'copt': '-Dright',", "        'javacopt': '-Dgood',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "    },", "    values = {", "        'copt': '-Dright',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isTrue();
    }

    @Test
    public void matchesAliasedFlagsInFlagValues() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'alias_matcher',", "    flag_values = {", "        ':alias': 'right',", "    },", "    transitive_configs = [':flag'],", ")", "alias(", "    name = 'alias',", "    actual = 'flag',", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
        assertThat(getConfigMatchingProvider("//test:alias_matcher").matches()).isTrue();
    }

    @Test
    public void aliasedFlagsAreCountedInRefining() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "config_setting(", "    name = 'refined',", "    flag_values = {", "        ':alias': 'right',", "        ':flag2': 'good',", "    },", "    transitive_configs = [':flag', ':flag2'],", ")", "config_setting(", "    name = 'other',", "    flag_values = {", "        ':flag': 'right',", "    },", "    transitive_configs = [':flag'],", ")", "alias(", "    name = 'alias',", "    actual = 'flag',", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['good', 'bad'],", "    default_value = 'good',", ")");
        assertThat(getConfigMatchingProvider("//test:refined").refines(getConfigMatchingProvider("//test:other"))).isTrue();
    }

    @Test
    public void referencingSameFlagViaMultipleAliasesFails() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        checkError("test", "multialias", ("in flag_values attribute of config_setting rule //test:multialias: " + "flag '//test:direct' referenced multiple times as ['//test:alias', '//test:direct']"), "config_setting(", "    name = 'multialias',", "    flag_values = {", "        ':alias': 'right',", "        ':direct': 'right',", "    },", "    transitive_configs = [':direct'],", ")", "alias(", "    name = 'alias',", "    actual = 'direct',", "    transitive_configs = [':direct'],", ")", "config_feature_flag(", "    name = 'direct',", "    allowed_values = ['right', 'wrong'],", "    default_value = 'right',", ")");
    }

    @Test
    public void forbidsNonConfigFeatureFlagRulesForFlagValues() throws Exception {
        checkError("test", "invalid_flag", ("in flag_values attribute of config_setting rule //test:invalid_flag: " + "'//test:genrule' does not have mandatory providers: 'FeatureFlagInfo'"), "config_setting(", "    name = 'invalid_flag',", "    flag_values = {", "        ':genrule': 'lolz',", "    })", "genrule(", "    name = 'genrule',", "    outs = ['output'],", "    cmd = 'echo >$@',", "    )");
    }

    @Test
    public void requiresValidValueForFlagValues() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        checkError("test", "invalid_flag", ("in flag_values attribute of config_setting rule //test:invalid_flag: " + ("error while parsing user-defined configuration values: " + "'invalid' is not a valid value for '//test:flag'")), "config_setting(", "    name = 'invalid_flag',", "    flag_values = {", "        ':flag': 'invalid',", "    },", "    transitive_configs = [':flag'])", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'valid'],", "    default_value = 'valid',", ")");
    }

    @Test
    public void usesAliasLabelWhenReportingErrorInFlagValues() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        checkError("test", "invalid_flag", ("in flag_values attribute of config_setting rule //test:invalid_flag: " + ("error while parsing user-defined configuration values: " + "'invalid' is not a valid value for '//test:alias'")), "config_setting(", "    name = 'invalid_flag',", "    flag_values = {", "        ':alias': 'invalid',", "    },", "    transitive_configs = [':flag'])", "alias(", "    name = 'alias',", "    actual = ':flag',", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['right', 'valid'],", "    default_value = 'valid',", ")");
    }

    @Test
    public void constraintValue() throws Exception {
        scratch.file("test/BUILD", "constraint_setting(name = 'notable_building')", "constraint_value(name = 'empire_state', constraint_setting = 'notable_building')", "constraint_value(name = 'space_needle', constraint_setting = 'notable_building')", "platform(", "    name = 'new_york_platform',", "    constraint_values = [':empire_state'],", ")", "platform(", "    name = 'seattle_platform',", "    constraint_values = [':space_needle'],", ")", "config_setting(", "    name = 'match',", "    constraint_values = [':empire_state'],", ");");
        useConfiguration("--experimental_platforms=//test:new_york_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--experimental_platforms=//test:seattle_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    @Test
    public void multipleConstraintValues() throws Exception {
        scratch.file("test/BUILD", "constraint_setting(name = 'notable_building')", "constraint_value(name = 'empire_state', constraint_setting = 'notable_building')", "constraint_setting(name = 'museum')", "constraint_value(name = 'cloisters', constraint_setting = 'museum')", "constraint_setting(name = 'theme_park')", "constraint_value(name = 'coney_island', constraint_setting = 'theme_park')", "platform(", "    name = 'manhattan_platform',", "    constraint_values = [", "        ':empire_state',", "        ':cloisters',", "    ],", ")", "platform(", "    name = 'museum_platform',", "    constraint_values = [':cloisters'],", ")", "platform(", "    name = 'new_york_platform',", "    constraint_values = [", "        ':empire_state',", "        ':cloisters',", "        ':coney_island',", "    ],", ")", "config_setting(", "    name = 'match',", "    constraint_values = [':empire_state', ':cloisters'],", ");");
        useConfiguration("--experimental_platforms=//test:manhattan_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--experimental_platforms=//test:museum_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--experimental_platforms=//test:new_york_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
    }

    @Test
    public void definesAndConstraints() throws Exception {
        scratch.file("test/BUILD", "constraint_setting(name = 'notable_building')", "constraint_value(name = 'empire_state', constraint_setting = 'notable_building')", "constraint_value(name = 'space_needle', constraint_setting = 'notable_building')", "platform(", "    name = 'new_york_platform',", "    constraint_values = [':empire_state'],", ")", "platform(", "    name = 'seattle_platform',", "    constraint_values = [':space_needle'],", ")", "config_setting(", "    name = 'match',", "    constraint_values = [':empire_state'],", "    values = {", "        'define': 'a=c',", "    },", "    define_values = {", "        'b': 'd',", "    },", ");");
        useConfiguration("--experimental_platforms=//test:new_york_platform", "--define", "a=c", "--define", "b=d");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isTrue();
        useConfiguration("--experimental_platforms=//test:new_york_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "a=c");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
        useConfiguration("--define", "a=c", "--experimental_platforms=//test:new_york_platform");
        assertThat(getConfigMatchingProvider("//test:match").matches()).isFalse();
    }

    /**
     * Tests that a config_setting doesn't allow a constraint_values list with more than one
     * constraint value per constraint setting.
     */
    @Test
    public void multipleValuesPerSetting() throws Exception {
        checkError("foo", "bad", ("in config_setting rule //foo:bad: " + (((("Duplicate constraint values detected: " + "constraint_setting //foo:notable_building has ") + "[//foo:empire_state, //foo:space_needle], ") + "constraint_setting //foo:museum has ") + "[//foo:moma, //foo:sam]")), "constraint_setting(name = 'notable_building')", "constraint_value(name = 'empire_state', constraint_setting = 'notable_building')", "constraint_value(name = 'space_needle', constraint_setting = 'notable_building')", "constraint_value(name = 'peace_arch', constraint_setting = 'notable_building')", "constraint_setting(name = 'museum')", "constraint_value(name = 'moma', constraint_setting = 'museum')", "constraint_value(name = 'sam', constraint_setting = 'museum')", "config_setting(", "    name = 'bad',", "    constraint_values = [", "        ':empire_state',", "        ':space_needle',", "        ':moma',", "        ':sam',", "    ],", ");");
    }

    /**
     * Tests that default license behavior is unaffected.
     */
    @Test
    public void licensesDefault() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'copt': '-Dfoo',", "    })");
        useConfiguration("--copt", "-Dfoo");
        assertThat(getLicenses("//test:match")).containsExactly(NONE);
    }

    /**
     * Tests that third-party doesn't require a license from config_setting.
     */
    @Test
    public void thirdPartyLicenseRequirement() throws Exception {
        scratch.file("third_party/test/BUILD", "config_setting(", "    name = 'match',", "    values = {", "        'copt': '-Dfoo',", "    })");
        useConfiguration("--copt", "-Dfoo");
        assertThat(getLicenses("//third_party/test:match")).containsExactly(NONE);
    }

    /**
     * Tests that package-wide licenses are ignored by config_setting.
     */
    @Test
    public void packageLicensesIgnored() throws Exception {
        scratch.file("test/BUILD", "licenses(['restricted'])", "config_setting(", "    name = 'match',", "    values = {", "        'copt': '-Dfoo',", "    })");
        useConfiguration("--copt", "-Dfoo");
        assertThat(getLicenses("//test:match")).containsExactly(NONE);
    }

    /**
     * Tests that rule-specific licenses are still used by config_setting.
     */
    @Test
    public void ruleLicensesUsed() throws Exception {
        scratch.file("test/BUILD", "config_setting(", "    name = 'match',", "    licenses = ['restricted'],", "    values = {", "        'copt': '-Dfoo',", "    })");
        useConfiguration("--copt", "-Dfoo");
        assertThat(getLicenses("//test:match")).containsExactly(RESTRICTED);
    }
}

