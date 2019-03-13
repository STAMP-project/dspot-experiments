/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis;


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.test.TestConfiguration.TestOptions;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for StarlarkRuleTransitionProvider.
 */
@RunWith(JUnit4.class)
public class StarlarkRuleTransitionProviderTest extends BuildViewTestCase {
    @Test
    public void testOutputOnlyTransition() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  return {'//command_line_option:test_arg': ['post-transition']}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(implementation = _impl, cfg = my_transition)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        useConfiguration("--test_arg=pre-transition");
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().get(TestOptions.class).testArguments).containsExactly("post-transition");
    }

    @Test
    public void testInputAndOutputTransition() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  return {'//command_line_option:test_arg': ", "    [settings['//command_line_option:test_arg'][0]+'->post-transition']}", "my_transition = transition(", "  implementation = _impl,", "  inputs = ['//command_line_option:test_arg'],", "  outputs = ['//command_line_option:test_arg'],", ")");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(implementation = _impl, cfg = my_transition)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        useConfiguration("--test_arg=pre-transition");
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().get(TestOptions.class).testArguments).containsExactly("pre-transition->post-transition");
    }

    @Test
    public void testBuildSettingCannotTransition() throws Exception {
        setSkylarkSemanticsOptions("--experimental_build_setting_api=true");
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  return {'//command_line_option:test_arg': ['post-transition']}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(", "  implementation = _impl,", "  cfg = my_transition,", "  build_setting = config.string()", ")");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("Build setting rules cannot use the `cfg` param to apply transitions to themselves");
    }

    @Test
    public void testBadCfgInput() throws Exception {
        scratch.file("test/rules.bzl", "def _impl(ctx):", "  return []", "my_rule = rule(", "  implementation = _impl,", "  cfg = 'my_transition',", ")");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("`cfg` must be set to a transition object initialized by the transition() function.");
    }

    @Test
    public void testMultipleReturnConfigs() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  return {", "      't0': {'//command_line_option:test_arg': ['split_one']},", "      't1': {'//command_line_option:test_arg': ['split_two']},", "  }", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(implementation = _impl, cfg = my_transition)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("Rule transition only allowed to return a single transitioned configuration.");
    }

    @Test
    public void testCanDoBadStuffWithParameterizedTransitionsAndSelects() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  if (attr.my_configurable_attr):", "    return {'//command_line_option:test_arg': ['true']}", "  else:", "    return {'//command_line_option:test_arg': ['false']}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(", "  implementation = _impl,", "  cfg = my_transition,", "  attrs = {'my_configurable_attr': attr.bool(default = False)},", ")");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(", "  name = 'test',", "  my_configurable_attr = select({", "    '//conditions:default': False,", "    ':true-config': True,", "  })", ")", "config_setting(", "  name = 'true-config',", "  values = {'test_arg': 'true'},", ")");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent(("No attribute 'my_configurable_attr'. " + ("Either this attribute does not exist for this rule or is set by a select. " + "Starlark rule transitions currently cannot read attributes behind selects.")));
    }

    @Test
    public void testLabelTypedAttrReturnsLabelNotDep() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  if attr.dict_attr[Label('//test:key')] == 'value':", "    return {'//command_line_option:test_arg': ['post-transition']}", "  else:", "    return {'//command_line_option:test_arg': ['uh-oh']}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(", "  implementation = _impl,", "  cfg = my_transition,", "  attrs = {'dict_attr': attr.label_keyed_string_dict()},", ")", "simple_rule = rule(_impl)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule', 'simple_rule')", "my_rule(", "  name = 'test',", "  dict_attr = {':key': 'value'},", ")", "simple_rule(name = 'key')");
        useConfiguration("--test_arg=pre-transition");
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().get(TestOptions.class).testArguments).containsExactly("post-transition");
    }

    @Test
    public void testCannotTransitionOnBuildSettingWithoutFlag() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=false", "--experimental_build_setting_api");
        scratch.file("test/transitions.bzl", "def _transition_impl(settings, attr):", "  return {'//test:cute-animal-fact': 'puffins mate for life'}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = [],", "  outputs = ['//test:cute-animal-fact']", ")");
        writeRulesBuildSettingsAndBUILDforBuildSettingTransitionTests();
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("transitions on Starlark-defined build settings is experimental");
    }

    @Test
    public void testTransitionOnBuildSetting() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true", "--experimental_build_setting_api");
        scratch.file("test/transitions.bzl", "def _transition_impl(settings, attr):", "  return {'//test:cute-animal-fact': 'puffins mate for life'}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = [],", "  outputs = ['//test:cute-animal-fact']", ")");
        writeRulesBuildSettingsAndBUILDforBuildSettingTransitionTests();
        useConfiguration(ImmutableMap.of("//test:cute-animal-fact", "cats can't taste sugar"));
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().getStarlarkOptions().get(Label.parseAbsoluteUnchecked("//test:cute-animal-fact"))).isEqualTo("puffins mate for life");
    }

    // TODO(juliexxia): flip this test when post-transition validation is implemented to prevent
    // transitions from setting (for example) string-typed build settings to ints.
    @Test
    public void testCanSetBuildSettingToBadValue() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true", "--experimental_build_setting_api");
        scratch.file("test/transitions.bzl", "def _transition_impl(settings, attr):", "  return {'//test:cute-animal-fact': 24}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = [],", "  outputs = ['//test:cute-animal-fact']", ")");
        writeRulesBuildSettingsAndBUILDforBuildSettingTransitionTests();
        useConfiguration(ImmutableMap.of("//test:cute-animal-fact", "cats can't taste sugar"));
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().getStarlarkOptions().get(Label.parseAbsoluteUnchecked("//test:cute-animal-fact"))).isEqualTo(24);
    }

    // TODO(juliexxia): flip this test when post-transition validation is implemented to prevent
    // transitions from setting non-existent build settings (note - we'll need to verify we can't
    // set non-build setting targets either).
    @Test
    public void testCanSetNonexistantBuildSetting() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true", "--experimental_build_setting_api");
        scratch.file("test/transitions.bzl", "def _transition_impl(settings, attr):", "  return {'//test:i-am-not-real': 'imaginary-friend'}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = [],", "  outputs = ['//test:i-am-not-real']", ")");
        writeRulesBuildSettingsAndBUILDforBuildSettingTransitionTests();
        BuildConfiguration configuration = getConfiguration(getConfiguredTarget("//test"));
        assertThat(configuration.getOptions().getStarlarkOptions().get(Label.parseAbsoluteUnchecked("//test:i-am-not-real"))).isEqualTo("imaginary-friend");
    }

    // TODO(juliexxia): flip this test when we can read build settings.
    @Test
    public void testCantReadNonNativeBuildSetting() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true", "--experimental_build_setting_api");
        scratch.file("test/transitions.bzl", "def _transition_impl(settings, attr):", "  return {'//test:cute-animal-fact': settings['//test:cute-animal-fact']+' ADDED'}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = ['//test:cute-animal-fact'],", "  outputs = ['//test:i-am-not-real']", ")");
        writeRulesBuildSettingsAndBUILDforBuildSettingTransitionTests();
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("transition inputs [//test:cute-animal-fact] do not correspond to valid settings");
    }

    @Test
    public void testOneParamTransitionFunctionApiFails() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        scratch.file("test/transitions.bzl", "def _impl(settings):", "  return {'//command_line_option:test_arg': ['post-transition']}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:test_arg'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(implementation = _impl, cfg = my_transition)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("too many (2) positional arguments in call to _impl(settings)");
    }

    @Test
    public void testCannotTransitionOnExperimentalFlag() throws Exception {
        scratch.file("test/transitions.bzl", "def _impl(settings, attr):", "  return {'//command_line_option:experimental_build_setting_api': True}", "my_transition = transition(implementation = _impl, inputs = [],", "  outputs = ['//command_line_option:experimental_build_setting_api'])");
        scratch.file("test/rules.bzl", "load('//test:transitions.bzl', 'my_transition')", "def _impl(ctx):", "  return []", "my_rule = rule(implementation = _impl, cfg = my_transition)");
        scratch.file("test/BUILD", "load('//test:rules.bzl', 'my_rule')", "my_rule(name = 'test')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test");
        assertContainsEvent("Cannot transition on --experimental_* or --incompatible_* options");
    }
}

