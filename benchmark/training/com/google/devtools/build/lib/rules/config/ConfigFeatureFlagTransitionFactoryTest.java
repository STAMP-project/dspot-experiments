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
package com.google.devtools.build.lib.rules.config;


import NoTransition.INSTANCE;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.transitions.PatchTransition;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the ConfigFeatureFlagTransitionFactory.
 */
@RunWith(JUnit4.class)
public final class ConfigFeatureFlagTransitionFactoryTest extends BuildViewTestCase {
    @Test
    public void emptyTransition_returnsOriginalOptionsIfFragmentNotPresent() throws Exception {
        Rule rule = scratchRule("a", "empty", "feature_flag_setter(name = 'empty', flag_values = {})");
        PatchTransition transition = new ConfigFeatureFlagTransitionFactory("flag_values").buildTransitionFor(rule);
        BuildOptions original = ConfigFeatureFlagTransitionFactoryTest.getOptionsWithoutFlagFragment();
        BuildOptions converted = transition.patch(original);
        assertThat(converted).isSameAs(original);
        assertThat(original.contains(ConfigFeatureFlagOptions.class)).isFalse();
    }

    @Test
    public void populatedTransition_returnsOriginalOptionsIfFragmentNotPresent() throws Exception {
        Rule rule = scratchRule("a", "flag_setter_a", "feature_flag_setter(", "    name = 'flag_setter_a',", "    flag_values = {':flag': 'a'})", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['a', 'b'],", "    default_value = 'a')");
        PatchTransition transition = new ConfigFeatureFlagTransitionFactory("flag_values").buildTransitionFor(rule);
        BuildOptions original = ConfigFeatureFlagTransitionFactoryTest.getOptionsWithoutFlagFragment();
        BuildOptions converted = transition.patch(original);
        assertThat(converted).isSameAs(original);
        assertThat(original.contains(ConfigFeatureFlagOptions.class)).isFalse();
    }

    @Test
    public void emptyTransition_returnsClearedOptionsIfFragmentPresent() throws Exception {
        Rule rule = scratchRule("a", "empty", "feature_flag_setter(name = 'empty', flag_values = {})");
        PatchTransition transition = new ConfigFeatureFlagTransitionFactory("flag_values").buildTransitionFor(rule);
        Map<Label, String> originalFlagMap = ImmutableMap.of(Label.parseAbsolute("//a:flag", ImmutableMap.of()), "value");
        BuildOptions original = ConfigFeatureFlagTransitionFactoryTest.getOptionsWithFlagFragment(originalFlagMap);
        BuildOptions converted = transition.patch(original);
        assertThat(converted).isNotSameAs(original);
        assertThat(FeatureFlagValue.getFlagValues(original)).containsExactlyEntriesIn(originalFlagMap);
        assertThat(FeatureFlagValue.getFlagValues(converted)).isEmpty();
    }

    @Test
    public void populatedTransition_setsOptionsAndClearsNonPresentOptionsIfFragmentPresent() throws Exception {
        Rule rule = scratchRule("a", "flag_setter_a", "feature_flag_setter(", "    name = 'flag_setter_a',", "    flag_values = {':flag': 'a'})", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['a', 'b'],", "    default_value = 'a')");
        PatchTransition transition = new ConfigFeatureFlagTransitionFactory("flag_values").buildTransitionFor(rule);
        Map<Label, String> originalFlagMap = ImmutableMap.of(Label.parseAbsolute("//a:old", ImmutableMap.of()), "value");
        Map<Label, String> expectedFlagMap = ImmutableMap.of(Label.parseAbsolute("//a:flag", ImmutableMap.of()), "a");
        BuildOptions original = ConfigFeatureFlagTransitionFactoryTest.getOptionsWithFlagFragment(originalFlagMap);
        BuildOptions converted = transition.patch(original);
        assertThat(converted).isNotSameAs(original);
        assertThat(FeatureFlagValue.getFlagValues(original)).containsExactlyEntriesIn(originalFlagMap);
        assertThat(FeatureFlagValue.getFlagValues(converted)).containsExactlyEntriesIn(expectedFlagMap);
    }

    @Test
    public void transition_equalsTester() throws Exception {
        scratch.file("a/BUILD", "filegroup(", "    name = 'not_a_flagsetter',", "    srcs = [])", "feature_flag_setter(", "    name = 'empty',", "    flag_values = {})", "feature_flag_setter(", "    name = 'empty2',", "    flag_values = {})", "feature_flag_setter(", "    name = 'flag_setter_a',", "    flag_values = {':flag': 'a'})", "feature_flag_setter(", "    name = 'flag_setter_a2',", "    flag_values = {':flag': 'a'})", "feature_flag_setter(", "    name = 'flag_setter_b',", "    flag_values = {':flag': 'b'})", "feature_flag_setter(", "    name = 'flag2_setter',", "    flag_values = {':flag2': 'a'})", "feature_flag_setter(", "    name = 'both_setter',", "    flag_values = {':flag': 'a', ':flag2': 'a'})", "config_feature_flag(", "    name = 'flag',", "    allowed_values = ['a', 'b'],", "    default_value = 'a')", "config_feature_flag(", "    name = 'flag2',", "    allowed_values = ['a', 'b'],", "    default_value = 'a')");
        Rule nonflag = ((Rule) (getTarget("//a:not_a_flagsetter")));
        Rule empty = ((Rule) (getTarget("//a:empty")));
        Rule empty2 = ((Rule) (getTarget("//a:empty2")));
        Rule flagSetterA = ((Rule) (getTarget("//a:flag_setter_a")));
        Rule flagSetterA2 = ((Rule) (getTarget("//a:flag_setter_a2")));
        Rule flagSetterB = ((Rule) (getTarget("//a:flag_setter_b")));
        Rule flag2Setter = ((Rule) (getTarget("//a:flag2_setter")));
        Rule bothSetter = ((Rule) (getTarget("//a:both_setter")));
        ConfigFeatureFlagTransitionFactory factory = new ConfigFeatureFlagTransitionFactory("flag_values");
        ConfigFeatureFlagTransitionFactory factory2 = new ConfigFeatureFlagTransitionFactory("flag_values");
        // transition with more flags set
        // transition with different flag set to same value
        // transition with flag set to different value
        // transition with flag -> a
        // same map, different rule
        // same map, different factory
        // transition with empty map
        // transition produced by same factory on same rule
        // transition produced by similar factory on same rule
        // transition produced by same factory on similar rule
        // transition produced by similar factory on similar rule
        // transition for non flags target
        new EqualsTester().addEqualityGroup(factory.buildTransitionFor(nonflag), INSTANCE).addEqualityGroup(factory.buildTransitionFor(empty), factory.buildTransitionFor(empty), factory2.buildTransitionFor(empty), factory.buildTransitionFor(empty2), factory2.buildTransitionFor(empty2)).addEqualityGroup(factory.buildTransitionFor(flagSetterA), factory.buildTransitionFor(flagSetterA2), factory2.buildTransitionFor(flagSetterA)).addEqualityGroup(factory.buildTransitionFor(flagSetterB)).addEqualityGroup(factory.buildTransitionFor(flag2Setter)).addEqualityGroup(factory.buildTransitionFor(bothSetter)).testEquals();
    }

    @Test
    public void factory_equalsTester() throws Exception {
        new EqualsTester().addEqualityGroup(new ConfigFeatureFlagTransitionFactory("flag_values"), new ConfigFeatureFlagTransitionFactory("flag_values")).addEqualityGroup(new ConfigFeatureFlagTransitionFactory("other_flag_values")).testEquals();
    }
}

