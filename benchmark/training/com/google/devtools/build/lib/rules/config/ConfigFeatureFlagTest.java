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
 * limitations under the License
 */
package com.google.devtools.build.lib.rules.config;


import Type.STRING;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.skylark.SkylarkRuleContext;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.ConfiguredAttributeMapper;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.skylark.util.SkylarkTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the config_feature_flag rule.
 */
@RunWith(JUnit4.class)
public final class ConfigFeatureFlagTest extends SkylarkTestCase {
    @Test
    public void configFeatureFlagProvider_fromTargetReturnsNullIfTargetDoesNotExportProvider() throws Exception {
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    flag_values = {", "    },", ")");
        assertThat(ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:top"))).isNull();
    }

    @Test
    public void configFeatureFlagProvider_containsValueFromConfiguration() throws Exception {
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':flag': 'configured',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", ")");
        assertThat(ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:top")).getFlagValue()).isEqualTo("configured");
    }

    @Test
    public void configFeatureFlagProvider_usesConfiguredValueOverDefault() throws Exception {
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':flag': 'configured',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:top")).getFlagValue()).isEqualTo("configured");
    }

    @Test
    public void configFeatureFlagProvider_skylarkConstructor() throws Exception {
        scratch.file("test/wrapper.bzl", "def _flag_reading_wrapper_impl(ctx):", "  pass", "flag_reading_wrapper = rule(", "  implementation = _flag_reading_wrapper_impl,", "  attrs = {'flag': attr.label()},", ")", "def _flag_propagating_wrapper_impl(ctx):", "  return struct(providers = [config_common.FeatureFlagInfo(value='hello')])", "flag_propagating_wrapper = rule(", "  implementation = _flag_propagating_wrapper_impl,", ")");
        scratch.file("test/BUILD", "load(':wrapper.bzl', 'flag_propagating_wrapper')", "flag_propagating_wrapper(", "    name = 'propagator',", ")", "config_setting(name = 'hello_setting',", "    flag_values = {':propagator': 'hello'})", "genrule(", "    name = 'gen',", "    srcs = [],", "    outs = ['out'],", "    cmd = select({", "       ':hello_setting': 'hello',", "       '//conditions:default': 'error'", "    }))");
        ConfiguredTargetAndData ctad = getConfiguredTargetAndData("//test:gen");
        ConfiguredAttributeMapper attributeMapper = BuildViewTestCase.getMapperFromConfiguredTargetAndTarget(ctad);
        assertThat(attributeMapper.get("cmd", STRING)).isEqualTo("hello");
    }

    @Test
    public void configFeatureFlagProvider_valueIsAccessibleFromSkylark() throws Exception {
        scratch.file("test/wrapper.bzl", "def _flag_reading_wrapper_impl(ctx):", "  pass", "flag_reading_wrapper = rule(", "  implementation = _flag_reading_wrapper_impl,", "  attrs = {'flag': attr.label()},", ")");
        scratch.file("test/BUILD", "load(':wrapper.bzl', 'flag_reading_wrapper')", "feature_flag_setter(", "    name = 'top',", "    deps = [':wrapper'],", "    flag_values = {", "        ':flag': 'configured',", "    },", "    transitive_configs = [':flag'],", ")", "flag_reading_wrapper(", "    name = 'wrapper',", "    flag = ':flag',", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        ConfiguredTarget top = getConfiguredTarget("//test:top");
        ConfiguredTarget wrapper = ((ConfiguredTarget) (Iterables.getOnlyElement(getPrerequisites(top, "deps"))));
        SkylarkRuleContext ctx = new SkylarkRuleContext(getRuleContextForSkylark(wrapper), null, getSkylarkSemantics());
        update("ruleContext", ctx);
        update("config_common", new ConfigSkylarkCommon());
        String value = ((String) (eval("ruleContext.attr.flag[config_common.FeatureFlagInfo].value")));
        assertThat(value).isEqualTo("configured");
    }

    @Test
    public void configFeatureFlagProvider_validatesValuesUsingAllowedValuesAttribute() throws Exception {
        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        ConfigFeatureFlagProvider provider = ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:flag"));
        assertThat(provider.isValidValue("default")).isTrue();
        assertThat(provider.isValidValue("configured")).isTrue();
        assertThat(provider.isValidValue("other")).isTrue();
        assertThat(provider.isValidValue("absent")).isFalse();
        assertThat(provider.isValidValue("conFigured")).isFalse();
        assertThat(provider.isValidValue("  other")).isFalse();
    }

    @Test
    public void configFeatureFlagProvider_valueValidationIsPossibleFromSkylark() throws Exception {
        scratch.file("test/wrapper.bzl", "def _flag_reading_wrapper_impl(ctx):", "  pass", "flag_reading_wrapper = rule(", "  implementation = _flag_reading_wrapper_impl,", "  attrs = {'flag': attr.label()},", ")");
        scratch.file("test/BUILD", "load(':wrapper.bzl', 'flag_reading_wrapper')", "flag_reading_wrapper(", "    name = 'wrapper',", "    flag = ':flag',", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        SkylarkRuleContext ctx = createRuleContext("//test:wrapper");
        update("ruleContext", ctx);
        update("config_common", new ConfigSkylarkCommon());
        String provider = "ruleContext.attr.flag[config_common.FeatureFlagInfo]";
        Boolean isDefaultValid = ((Boolean) (eval((provider + ".is_valid_value('default')"))));
        Boolean isConfiguredValid = ((Boolean) (eval((provider + ".is_valid_value('configured')"))));
        Boolean isOtherValid = ((Boolean) (eval((provider + ".is_valid_value('other')"))));
        Boolean isAbsentValid = ((Boolean) (eval((provider + ".is_valid_value('absent')"))));
        Boolean isIncorrectCapitalizationValid = ((Boolean) (eval((provider + ".is_valid_value('conFigured')"))));
        Boolean isIncorrectSpacingValid = ((Boolean) (eval((provider + ".is_valid_value('  other')"))));
        assertThat(isDefaultValid).isTrue();
        assertThat(isConfiguredValid).isTrue();
        assertThat(isOtherValid).isTrue();
        assertThat(isAbsentValid).isFalse();
        assertThat(isIncorrectCapitalizationValid).isFalse();
        assertThat(isIncorrectSpacingValid).isFalse();
    }

    @Test
    public void configFeatureFlagProvider_usesDefaultValueIfConfigurationDoesntSetValue() throws Exception {
        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':other': 'configured',", "    },", "    transitive_configs = [':flag', ':other'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['other', 'default', 'configured'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'other',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:top")).getFlagValue()).isEqualTo("default");
    }

    @Test
    public void configFeatureFlagProvider_throwsErrorIfNeitherDefaultNorConfiguredValueSet() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':other': 'configured',", "    },", "    transitive_configs = [':flag', ':other'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['other', 'configured'],", ")", "config_feature_flag(", "    name = 'other',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:flag")).isNull();
        assertContainsEvent(("in config_feature_flag rule //test:flag: " + "flag has no default and must be set, but was not set"));
    }

    @Test
    public void allowedValuesAttribute_cannotBeEmpty() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'flag',", "    allowed_values = [],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:flag")).isNull();
        assertContainsEvent(("in allowed_values attribute of config_feature_flag rule //test:flag: " + "attribute must be non empty"));
    }

    @Test
    public void allowedValuesAttribute_cannotContainDuplicates() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['double', 'double', 'toil', 'trouble'],", "    default_value = 'trouble',", ")");
        assertThat(getConfiguredTarget("//test:flag")).isNull();
        assertContainsEvent(("in allowed_values attribute of config_feature_flag rule //test:flag: " + "cannot contain duplicates, but contained multiple of [\"double\"]"));
    }

    @Test
    public void defaultValueAttribute_mustBeMemberOfAllowedValuesIfPresent() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':flag': 'legal',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['legal', 'eagle'],", "    default_value = 'beagle',", ")");
        assertThat(getConfiguredTarget("//test:top")).isNull();
        assertContainsEvent(("in default_value attribute of config_feature_flag rule //test:flag: " + "must be one of [\"eagle\", \"legal\"], but was \"beagle\""));
    }

    @Test
    public void configurationValue_mustBeMemberOfAllowedValuesIfPresent() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "feature_flag_setter(", "    name = 'top',", "    exports_flag = ':flag',", "    flag_values = {", "        ':flag': 'invalid',", "    },", "    transitive_configs = [':flag'],", ")", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:top")).isNull();
        // TODO(mstaib): when configurationError is implemented, switch to testing for that
        assertContainsEvent(("in config_feature_flag rule //test:flag: " + "value must be one of [\"configured\", \"default\", \"other\"], but was \"invalid\""));
    }

    @Test
    public void policy_mustContainRulesPackage() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.overwriteFile("tools/whitelists/config_feature_flag/BUILD", "package_group(name = 'config_feature_flag', packages = ['//some/other'])");
        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:flag")).isNull();
        assertContainsEvent(("in config_feature_flag rule //test:flag: the config_feature_flag rule is not available in " + "package 'test'"));
    }

    @Test
    public void policy_doesNotBlockRuleIfInPackageGroup() throws Exception {
        scratch.overwriteFile("tools/whitelists/config_feature_flag/BUILD", "package_group(name = 'config_feature_flag', packages = ['//test'])");
        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:flag")).isNotNull();
        assertNoEvents();
    }

    @Test
    public void equalsTester() {
        // Change the value, still distinct from the above.
        // Will be distinct from the first group because CFFP instances are all distinct.
        // Basic case.
        new EqualsTester().addEqualityGroup(ConfigFeatureFlagProvider.create("flag1", Predicates.<String>alwaysTrue())).addEqualityGroup(ConfigFeatureFlagProvider.create("flag1", Predicates.<String>alwaysTrue())).addEqualityGroup(ConfigFeatureFlagProvider.create("flag2", Predicates.<String>alwaysTrue())).testEquals();
    }
}

