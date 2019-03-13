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
 * limitations under the License
 */
package com.google.devtools.build.lib.rules.config;


import Mode.TARGET;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.RuleContext;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.skylark.util.SkylarkTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for manual trimming of feature flags with the transitive_configs attribute.
 */
@RunWith(JUnit4.class)
public final class FeatureFlagManualTrimmingTest extends SkylarkTestCase {
    @Test
    public void duplicateTargetsCreatedWithTrimmingDisabled() throws Exception {
        useConfiguration("--noenforce_transitive_configs_for_config_feature_flag");
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'left',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'left',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':common_flag'],", ")", "feature_flag_setter(", "    name = 'right',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'right',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':common_flag'],", ")", "read_flags(", "    name = 'common',", "    flags = [':common_flag'],", "    transitive_configs = [':common_flag'],", ")", "config_feature_flag(", "    name = 'different_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'common_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact leftFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:left")).toList());
        Artifact rightFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:right")).toList());
        assertThat(leftFlags).isNotEqualTo(rightFlags);
    }

    @Test
    public void featureFlagSetAndInTransitiveConfigs_GetsSetValue() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':trimmed_flag': 'left',", "        ':used_flag': 'configured',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact targetFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:target")).toList());
        Label usedFlag = Label.parseAbsolute("//test:used_flag", ImmutableMap.of());
        assertThat(getFlagValuesFromOutputFile(targetFlags)).containsEntry(usedFlag, "configured");
    }

    @Test
    public void featureFlagSetButNotInTransitiveConfigs_IsTrimmedOutAndCollapsesDuplicates() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'left',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'left',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':common_flag'],", ")", "feature_flag_setter(", "    name = 'right',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'right',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':common_flag'],", ")", "read_flags(", "    name = 'common',", "    flags = [':common_flag'],", "    transitive_configs = [':common_flag'],", ")", "config_feature_flag(", "    name = 'different_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'common_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact leftFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:left")).toList());
        Artifact rightFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:right")).toList());
        assertThat(leftFlags).isEqualTo(rightFlags);
        assertThat(leftFlags.getArtifactOwner()).isEqualTo(rightFlags.getArtifactOwner());
    }

    @Test
    public void featureFlagInTransitiveConfigsButNotSet_GetsDefaultValue() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':trimmed_flag': 'left',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact targetFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:target")).toList());
        Label usedFlag = Label.parseAbsolute("//test:used_flag", ImmutableMap.of());
        assertThat(getFlagValuesFromOutputFile(targetFlags)).containsEntry(usedFlag, "default");
    }

    @Test
    public void featureFlagInTransitiveConfigsButNotInTransitiveClosure_IsWastefulButDoesNotError() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'left',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'left',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':different_flag', ':common_flag'],", ")", "feature_flag_setter(", "    name = 'right',", "    deps = [':common'],", "    flag_values = {", "        ':different_flag': 'right',", "        ':common_flag': 'configured',", "    },", "    transitive_configs = [':different_flag', ':common_flag'],", ")", "read_flags(", "    name = 'common',", "    flags = [':common_flag'],", "    transitive_configs = [':different_flag', ':common_flag'],", ")", "config_feature_flag(", "    name = 'different_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'common_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact leftFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:left")).toList());
        Artifact rightFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:right")).toList());
        assertThat(leftFlags).isNotEqualTo(rightFlags);
        assertThat(leftFlags.getArtifactOwner()).isNotEqualTo(rightFlags.getArtifactOwner());
    }

    @Test
    public void emptyTransitiveConfigs_EquivalentRegardlessOfFeatureFlags() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'left',", "    deps = [':reader'],", "    flag_values = {", "        ':used_flag': 'left',", "    },", "    transitive_configs = [':used_flag'],", ")", "feature_flag_setter(", "    name = 'right',", "    deps = [':reader'],", "    flag_values = {", "        ':used_flag': 'right',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    transitive_configs = [],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")");
        Artifact leftFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:left")).toList());
        Artifact rightFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:right")).toList());
        Artifact directFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:reader")).toList());
        assertThat(leftFlags).isEqualTo(rightFlags);
        assertThat(leftFlags).isEqualTo(directFlags);
    }

    @Test
    public void absentTransitiveConfigs_EquivalentRegardlessOfFeatureFlags() throws Exception {
        // no transitive_configs = equivalent to []
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'left',", "    deps = [':reader'],", "    flag_values = {", "        ':used_flag': 'left',", "    },", "    transitive_configs = [':used_flag'],", ")", "feature_flag_setter(", "    name = 'right',", "    deps = [':reader'],", "    flag_values = {", "        ':used_flag': 'right',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")");
        Artifact leftFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:left")).toList());
        Artifact rightFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:right")).toList());
        Artifact directFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:reader")).toList());
        assertThat(leftFlags).isEqualTo(rightFlags);
        assertThat(leftFlags).isEqualTo(directFlags);
    }

    @Test
    public void nonexistentLabelInTransitiveConfigs_DoesNotError() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':trimmed_flag': 'left',", "    },", "    transitive_configs = [':false_flag'],", ")", "read_flags(", "    name = 'reader',", "    transitive_configs = [':false_flag'],", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")");
        getConfiguredTarget("//test:target");
        assertNoEvents();
    }

    @Test
    public void flagSetBySetterButNotInTransitiveConfigs_CanBeUsedByDeps() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':not_actually_trimmed_flag': 'left',", "    },", "    transitive_configs = [],", ")", "read_flags(", "    name = 'reader',", "    flags = [':not_actually_trimmed_flag'],", "    transitive_configs = [':not_actually_trimmed_flag'],", ")", "config_feature_flag(", "    name = 'not_actually_trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")");
        getConfiguredTarget("//test:target");
        assertNoEvents();
    }

    @Test
    public void featureFlagInUnusedSelectBranchButNotInTransitiveConfigs_DoesNotError() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':trimmed_flag': 'left',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    flags = select({':used_flag@other': [':trimmed_flag'], '//conditions:default': []}),", "    transitive_configs = [':used_flag'],", ")", "config_setting(", "    name = 'used_flag@other',", "    flag_values = {':used_flag': 'other'},", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        getConfiguredTarget("//test:target");
        assertNoEvents();
    }

    @Test
    public void featureFlagTarget_IsTrimmedToOnlyItself() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    exports_flag = ':read_flag',", "    flag_values = {", "        ':trimmed_flag': 'left',", "        ':read_flag': 'configured',", "    },", "    transitive_configs = [':trimmed_flag', ':read_flag'],", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'read_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        ConfiguredTarget target = getConfiguredTarget("//test:target");
        RuleContext ruleContext = getRuleContext(target);
        BuildConfiguration childConfiguration = Iterables.getOnlyElement(ruleContext.getPrerequisiteConfiguredTargetAndTargets("exports_flag", TARGET)).getConfiguration();
        Label childLabel = Label.parseAbsoluteUnchecked("//test:read_flag");
        assertThat(getFlagMapFromConfiguration(childConfiguration).keySet()).containsExactly(childLabel);
    }

    @Test
    public void featureFlagAccessedByPathWithMissingLabel_ProducesError() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':broken'],", "    flag_values = {", "        ':used_flag': 'configured',", "    },", "    transitive_configs = [':used_flag'],", ")", "filegroup(", "    name = 'broken',", "    srcs = [':reader'],", "    transitive_configs = [],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:target")).isNull();
        assertContainsEvent(("Feature flag //test:used_flag was accessed in a configuration it is not present in. All " + ("targets which depend on //test:used_flag directly or indirectly must name it in " + "their transitive_configs attribute.")));
    }

    @Test
    public void featureFlagAccessedByPathWithMissingTransitiveConfigs_ProducesError() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expecting an error

        // no transitive_configs = equivalent to []
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':broken'],", "    flag_values = {", "        ':used_flag': 'configured',", "    },", "    transitive_configs = [':used_flag'],", ")", "filegroup(", "    name = 'broken',", "    srcs = [':reader'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(getConfiguredTarget("//test:target")).isNull();
        assertContainsEvent(("Feature flag //test:used_flag was accessed in a configuration it is not present in. All " + ("targets which depend on //test:used_flag directly or indirectly must name it in " + "their transitive_configs attribute.")));
    }

    @Test
    public void featureFlagInHostConfiguration_HasDefaultValue() throws Exception {
        scratch.file("test/BUILD", "load(':host_transition.bzl', 'host_transition')", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':host'],", "    flag_values = {", "        ':used_flag': 'configured',", "    },", "    transitive_configs = [':used_flag'],", ")", "host_transition(", "    name = 'host',", "    srcs = [':reader'],", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact targetFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:target")).toList());
        Label usedFlag = Label.parseAbsolute("//test:used_flag", ImmutableMap.of());
        assertThat(getFlagValuesFromOutputFile(targetFlags)).containsEntry(usedFlag, "default");
    }

    @Test
    public void featureFlagInHostConfiguration_HasNoTransitiveConfigEnforcement() throws Exception {
        // no transitive_configs
        // no transitive_configs
        // no transitive_configs
        scratch.file("test/BUILD", "load(':host_transition.bzl', 'host_transition')", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':host'],", "    flag_values = {", "        ':used_flag': 'configured',", "    },", ")", "host_transition(", "    name = 'host',", "    srcs = [':reader'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        getConfiguredTarget("//test:target");
        assertNoEvents();
    }

    @Test
    public void noDistinctHostConfiguration_DoesNotResultInActionConflicts() throws Exception {
        scratch.file("test/BUILD", "load(':host_transition.bzl', 'host_transition')", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':host', ':reader'],", ")", "host_transition(", "    name = 'host',", "    srcs = [':reader'],", ")", "read_flags(", "    name = 'reader',", "    flags = [],", ")");
        enableManualTrimmingAnd("--nodistinct_host_configuration");
        ConfiguredTarget target = getConfiguredTarget("//test:target");
        assertNoEvents();
        // Note that '//test:reader' is accessed (and creates actions) in both the host and target
        // configurations. If these are different but output to the same path (as was the case before
        // --nodistinct_host_configuration caused --enforce_transitive_configs_for_config_feature_flag
        // to become a no-op), then this causes action conflicts, as described in b/117932061 (for which
        // this test is a regression test).
        assertThat(getFilesToBuild(target).toList()).hasSize(1);
        // Action conflict detection is not enabled for these tests. However, the action conflict comes
        // from the outputs of the two configurations of //test:reader being unequal artifacts;
        // hence, this test checks that the nested set of artifacts reachable from //test:target only
        // contains one artifact, that is, they were deduplicated for being equal.
    }

    @Test
    public void noDistinctHostConfiguration_DisablesEnforcementForBothHostAndTargetConfigs() throws Exception {
        // no transitive_configs
        // no transitive_configs
        // no transitive_configs
        scratch.file("test/BUILD", "load(':host_transition.bzl', 'host_transition')", "load(':read_flags.bzl', 'read_flags')", "feature_flag_setter(", "    name = 'target',", "    deps = [':host', ':reader'],", "    flag_values = {", "        ':used_flag': 'configured',", "    },", ")", "host_transition(", "    name = 'host',", "    srcs = [':reader'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        enableManualTrimmingAnd("--nodistinct_host_configuration");
        getConfiguredTarget("//test:target");
        assertNoEvents();
    }

    @Test
    public void featureFlagAccessedDirectly_ReturnsDefaultValue() throws Exception {
        scratch.file("test/BUILD", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        assertThat(ConfigFeatureFlagProvider.fromTarget(getConfiguredTarget("//test:used_flag")).getFlagValue()).isEqualTo("default");
    }

    @Test
    public void featureFlagAccessedViaTopLevelLibraryTarget_ReturnsDefaultValue() throws Exception {
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact targetFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:reader")).toList());
        Label usedFlag = Label.parseAbsolute("//test:used_flag", ImmutableMap.of());
        assertThat(getFlagValuesFromOutputFile(targetFlags)).containsEntry(usedFlag, "default");
    }

    @Test
    public void featureFlagSettingRules_OverrideFlagsFromReverseTransitiveClosure() throws Exception {
        // In other words: if you have a dependency which sets feature flags itself, you don't need to
        // name any of the feature flags used by that target or its transitive closure, as it sets
        // feature flags itself.
        // This is because the feature flag setting transition (which calls replaceFlagValues) runs
        // before the trimming transition and completely replaces the feature flag set. Thus, when
        // the trimming transition (which calls trimFlagValues) runs, its requests are always satisfied.
        // no transitive_configs
        scratch.file("test/BUILD", "load(':read_flags.bzl', 'read_flags')", "filegroup(", "    name = 'toplevel',", "    srcs = [':target'],", ")", "feature_flag_setter(", "    name = 'target',", "    deps = [':reader'],", "    flag_values = {", "        ':trimmed_flag': 'left',", "        ':used_flag': 'configured',", "    },", "    transitive_configs = [':used_flag'],", ")", "read_flags(", "    name = 'reader',", "    flags = [':used_flag'],", "    transitive_configs = [':used_flag'],", ")", "config_feature_flag(", "    name = 'trimmed_flag',", "    allowed_values = ['default', 'left', 'right'],", "    default_value = 'default',", ")", "config_feature_flag(", "    name = 'used_flag',", "    allowed_values = ['default', 'configured', 'other'],", "    default_value = 'default',", ")");
        Artifact targetFlags = Iterables.getOnlyElement(getFilesToBuild(getConfiguredTarget("//test:toplevel")).toList());
        Label usedFlag = Label.parseAbsolute("//test:used_flag", ImmutableMap.of());
        assertThat(getFlagValuesFromOutputFile(targetFlags)).containsEntry(usedFlag, "configured");
    }
}

