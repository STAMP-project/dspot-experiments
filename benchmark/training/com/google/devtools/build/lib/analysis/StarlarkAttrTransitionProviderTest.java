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
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.util.BazelMockAndroidSupport;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for StarlarkAttributeTransitionProvider.
 */
@RunWith(JUnit4.class)
public class StarlarkAttrTransitionProviderTest extends BuildViewTestCase {
    @Test
    public void testFunctionSplitTransitionCheckSplitAttrDeps() throws Exception {
        writeBasicTestFiles();
        testSplitTransitionCheckSplitAttrDeps(getConfiguredTarget("//test/skylark:test"));
    }

    @Test
    public void testFunctionSplitTransitionCheckSplitAttrDep() throws Exception {
        writeBasicTestFiles();
        testSplitTransitionCheckSplitAttrDep(getConfiguredTarget("//test/skylark:test"));
    }

    @Test
    public void testFunctionSplitTransitionCheckAttrDeps() throws Exception {
        writeBasicTestFiles();
        testSplitTransitionCheckAttrDeps(getConfiguredTarget("//test/skylark:test"));
    }

    @Test
    public void testFunctionSplitTransitionCheckAttrDep() throws Exception {
        writeBasicTestFiles();
        testSplitTransitionCheckAttrDep(getConfiguredTarget("//test/skylark:test"));
    }

    @Test
    public void testFunctionSplitTransitionCheckK8Deps() throws Exception {
        writeBasicTestFiles();
        testSplitTransitionCheckK8Deps(getConfiguredTarget("//test/skylark:test"));
    }

    @Test
    public void testTargetNotInWhitelist() throws Exception {
        writeBasicTestFiles();
        scratch.file("test/not_whitelisted/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main')", "cc_binary(name = 'main', srcs = ['main.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/not_whitelisted:test");
        assertContainsEvent("Non-whitelisted use of function-base split transition");
    }

    @Test
    public void testReadSettingsSplitDepAttrDep() throws Exception {
        // Check that ctx.split_attr.dep has this structure:
        // {
        // "k8": ConfiguredTarget,
        // "armeabi-v7a": ConfiguredTarget,
        // }
        /* appendToCurrentToolchain= */
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, false, MockCcSupport.emptyToolchainForCpu("armeabi-v7a"));
        writeReadSettingsTestFiles();
        useConfiguration("--fat_apk_cpu=k8,armeabi-v7a");
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:test");
        @SuppressWarnings("unchecked")
        Map<String, ConfiguredTarget> splitDep = ((Map<String, ConfiguredTarget>) (target.get("split_attr_dep")));
        assertThat(splitDep).containsKey("k8");
        assertThat(splitDep).containsKey("armeabi-v7a");
        assertThat(getConfiguration(splitDep.get("k8")).getCpu()).isEqualTo("k8");
        assertThat(getConfiguration(splitDep.get("armeabi-v7a")).getCpu()).isEqualTo("armeabi-v7a");
    }

    @Test
    public void testOptionConversionCpu() throws Exception {
        writeOptionConversionTestFiles();
        BazelMockAndroidSupport.setupNdk(mockToolsConfig);
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:test");
        @SuppressWarnings("unchecked")
        Map<String, ConfiguredTarget> splitDep = ((Map<String, ConfiguredTarget>) (target.get("split_attr_dep")));
        assertThat(splitDep).containsKey("armeabi-v7a");
        assertThat(getConfiguration(splitDep.get("armeabi-v7a")).getCpu()).isEqualTo("armeabi-v7a");
    }

    @Test
    public void testUndeclaredOptionKey() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 'k8'}", "my_transition = transition(implementation = transition_func, inputs = [], outputs = [])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent("transition function returned undeclared output '//command_line_option:cpu'");
    }

    @Test
    public void testDeclaredOutputNotReturned() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = [],", "  outputs = ['//command_line_option:cpu',", "             '//command_line_option:host_cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("transition outputs [//command_line_option:host_cpu] were not " + "defined by transition function"));
    }

    @Test
    public void testSettingsContainOnlyInputs() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  if (len(settings) != 2", "      or (not settings['//command_line_option:host_cpu'])", "      or (not settings['//command_line_option:cpu'])):", "    fail()", "  return {'//command_line_option:cpu': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = ['//command_line_option:host_cpu',", "            '//command_line_option:cpu'],", "  outputs = ['//command_line_option:cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        assertThat(getConfiguredTarget("//test/skylark:test")).isNotNull();
    }

    @Test
    public void testInvalidInputKey() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = ['cpu'], outputs = ['//command_line_option:cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("invalid transition input 'cpu'. If this is intended as a native option, " + "it must begin with //command_line_option:"));
    }

    @Test
    public void testInvalidNativeOptionInput() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = ['//command_line_option:foo', '//command_line_option:bar'],", "  outputs = ['//command_line_option:cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("transition inputs [//command_line_option:foo, //command_line_option:bar] " + "do not correspond to valid settings"));
    }

    @Test
    public void testInvalidNativeOptionOutput() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:foobarbaz': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = ['//command_line_option:cpu'], outputs = ['//command_line_option:foobarbaz'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("transition output '//command_line_option:foobarbaz' " + "does not correspond to a valid setting"));
    }

    @Test
    public void testInvalidOutputKey() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'cpu': 'k8'}", "my_transition = transition(implementation = transition_func,", "  inputs = [], outputs = ['cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("invalid transition output 'cpu'. If this is intended as a native option, " + "it must begin with //command_line_option:"));
    }

    @Test
    public void testInvalidOptionValue() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 1}", "my_transition = transition(implementation = transition_func,", "  inputs = [], outputs = ['//command_line_option:cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent("Invalid value type for option 'cpu'");
    }

    @Test
    public void testDuplicateOutputs() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true");
        writeWhitelistFile();
        scratch.file("test/skylark/my_rule.bzl", "def transition_func(settings, attr):", "  return {'//command_line_option:cpu': 1}", "my_transition = transition(implementation = transition_func,", "  inputs = [],", "  outputs = ['//command_line_option:cpu',", "             '//command_line_option:foo',", "             '//command_line_option:cpu'])", "def impl(ctx): ", "  return []", "my_rule = rule(", "  implementation = impl,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "        default = '//tools/whitelists/function_transition_whitelist',", "    ),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule')", "my_rule(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent("duplicate transition output '//command_line_option:cpu'");
    }

    @Test
    public void testInvalidNativeOptionOutput_analysisTest() throws Exception {
        scratch.file("test/skylark/my_rule.bzl", "my_transition = analysis_test_transition(", "  settings = {'//command_line_option:foobarbaz': 'k8'})", "def impl(ctx): ", "  return []", "my_rule_test = rule(", "  implementation = impl,", "  analysis_test = True,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule_test')", "my_rule_test(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("transition output '//command_line_option:foobarbaz' " + "does not correspond to a valid setting"));
    }

    @Test
    public void testInvalidOutputKey_analysisTest() throws Exception {
        scratch.file("test/skylark/my_rule.bzl", "my_transition = analysis_test_transition(", "  settings = {'cpu': 'k8'})", "def impl(ctx): ", "  return []", "my_rule_test = rule(", "  implementation = impl,", "  analysis_test = True,", "  attrs = {", "    'dep':  attr.label(cfg = my_transition),", "  })");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:my_rule.bzl', 'my_rule_test')", "my_rule_test(name = 'test', dep = ':main1')", "cc_binary(name = 'main1', srcs = ['main1.c'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent(("invalid transition output 'cpu'. If this is intended as a native option, " + "it must begin with //command_line_option:"));
    }

    @Test
    public void testTransitionOnBuildSetting() throws Exception {
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=true", "--experimental_build_setting_api");
        writeWhitelistFile();
        scratch.file("test/skylark/build_settings.bzl", "BuildSettingInfo = provider(fields = ['value'])", "def _impl(ctx):", "  return [BuildSettingInfo(value = ctx.build_setting_value)]", "string_flag = rule(implementation = _impl, build_setting = config.string(flag=True))");
        scratch.file("test/skylark/rules.bzl", "load('//test/skylark:build_settings.bzl', 'BuildSettingInfo')", "def _transition_impl(settings, attr):", "  return {'//test:cute-animal-fact': 'puffins mate for life'}", "my_transition = transition(", "  implementation = _transition_impl,", "  inputs = [],", "  outputs = ['//test:cute-animal-fact']", ")", "def _rule_impl(ctx):", "  return struct(dep = ctx.attr.dep)", "my_rule = rule(", "  implementation = _rule_impl,", "  attrs = {", "    'dep': attr.label(cfg = my_transition),", "    '_whitelist_function_transition': attr.label(", "      default = '//tools/whitelists/function_transition_whitelist'),", "  }", ")", "def _dep_rule_impl(ctx):", "  return [BuildSettingInfo(value = ctx.attr.fact[BuildSettingInfo].value)]", "dep_rule_impl = rule(", "  implementation = _dep_rule_impl,", "  attrs = {", "    'fact': attr.label(default = '//test/skylark:cute-animal-fact'),", "  }", ")");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:rules.bzl', 'my_rule')", "load('//test/skylark:build_settings.bzl', 'string_flag')", "my_rule(name = 'test', dep = ':dep')", "my_rule(name = 'dep')", "string_flag(", "  name = 'cute-animal-fact',", "  build_setting_default = 'cows produce more milk when they listen to soothing music',", ")");
        useConfiguration(ImmutableMap.of("//test:cute-animal-fact", "cats can't taste sugar"));
        getConfiguredTarget("//test/skylark:test");
        @SuppressWarnings("unchecked")
        BuildConfiguration depConfiguration = getConfiguration(Iterables.getOnlyElement(((List<ConfiguredTarget>) (getConfiguredTarget("//test/skylark:test").get("dep")))));
        assertThat(depConfiguration.getOptions().getStarlarkOptions().get(Label.parseAbsoluteUnchecked("//test:cute-animal-fact"))).isEqualTo("puffins mate for life");
    }

    @Test
    public void testCannotTransitionWithoutFlag() throws Exception {
        writeBasicTestFiles();
        setSkylarkSemanticsOptions("--experimental_starlark_config_transitions=false");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//test/skylark:test");
        assertContainsEvent("Starlark-defined transitions on rule attributes is experimental and disabled by default");
    }

    @Test
    public void testOptionConversionDynamicMode() throws Exception {
        // TODO(waltl): check that dynamic_mode is parsed properly.
    }

    @Test
    public void testOptionConversionCrosstoolTop() throws Exception {
        // TODO(waltl): check that crosstool_top is parsed properly.
    }
}

