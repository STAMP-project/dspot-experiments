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
package com.google.devtools.build.lib.analysis.test;


import Order.STABLE_ORDER;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.MutableActionGraph.ActionConflictException;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.RuleConfiguredTargetFactory;
import com.google.devtools.build.lib.analysis.RuleContext;
import com.google.devtools.build.lib.analysis.RuleDefinition;
import com.google.devtools.build.lib.analysis.Runfiles;
import com.google.devtools.build.lib.analysis.RunfilesProvider;
import com.google.devtools.build.lib.analysis.RunfilesSupport;
import com.google.devtools.build.lib.analysis.ViewCreationFailedException;
import com.google.devtools.build.lib.analysis.actions.FileWriteAction;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.analysis.util.MockRule;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.skyframe.SkyKey;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the trimming of the test configuration fragment.
 */
@RunWith(JUnit4.class)
public final class TestTrimmingTransitionTest extends AnalysisTestCase {
    /**
     * Simple native test rule.
     */
    public static final class NativeTest implements RuleConfiguredTargetFactory {
        @Override
        public ConfiguredTarget create(RuleContext context) throws ActionConflictException {
            Artifact executable = context.getBinArtifact(context.getLabel().getName());
            context.registerAction(FileWriteAction.create(context, executable, "#!/bin/true", true));
            Runfiles runfiles = new Runfiles.Builder(context.getWorkspaceName()).addArtifact(executable).build();
            return new com.google.devtools.build.lib.analysis.RuleConfiguredTargetBuilder(context).setFilesToBuild(NestedSetBuilder.create(STABLE_ORDER, executable)).add(RunfilesProvider.class, RunfilesProvider.simple(runfiles)).setRunfilesSupport(RunfilesSupport.withExecutable(context, runfiles, executable), executable).build();
        }
    }

    private static final RuleDefinition NATIVE_TEST_RULE = ((MockRule) (() -> MockRule.ancestor(.class, .class).factory(.class).type(RuleClassType.TEST).define("native_test", attr("deps", LABEL_LIST).allowedFileTypes(), attr("host_deps", LABEL_LIST).cfg(HostTransition.INSTANCE).allowedFileTypes())));

    private static final RuleDefinition NATIVE_LIB_RULE = ((MockRule) (() -> MockRule.ancestor(.class).define("native_lib", attr("deps", LABEL_LIST).allowedFileTypes(), attr("host_deps", LABEL_LIST).cfg(HostTransition.INSTANCE).allowedFileTypes())));

    @Test
    public void flagOffDifferentTestOptions_ResultsInDifferentCTs() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        LinkedHashSet<SkyKey> visitedTargets = new LinkedHashSet(getSkyframeEvaluatedTargetKeys());
        // asserting that the top-level targets are the same as the ones in the diamond starting at
        // //test:suite
        assertNumberOfConfigurationsOfTargets(visitedTargets, new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeB");
        update("//test:suite", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        visitedTargets.addAll(getSkyframeEvaluatedTargetKeys());
        // asserting that we got no overlap between the two runs, we had to build different versions of
        // all seven targets
        assertNumberOfConfigurationsOfTargets(visitedTargets, new ImmutableMap.Builder<String, Integer>().put("//test:suite", 2).put("//test:native_test", 2).put("//test:skylark_test", 2).put("//test:native_dep", 2).put("//test:skylark_dep", 2).put("//test:native_shared_dep", 2).put("//test:skylark_shared_dep", 2).build());
    }

    @Test
    public void flagOffDifferentTestOptions_CacheCleared() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeB");
        update("//test:suite");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite");
        // asserting that we got no overlap between the first and third runs, we had to reanalyze all
        // seven targets
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
    }

    @Test
    public void flagOnDifferentTestOptions_SharesCTsForNonTestRules() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        LinkedHashSet<SkyKey> visitedTargets = new LinkedHashSet(getSkyframeEvaluatedTargetKeys());
        // asserting that the top-level targets are the same as the ones in the diamond starting at
        // //test:suite
        assertNumberOfConfigurationsOfTargets(visitedTargets, new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeB");
        update("//test:suite", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        visitedTargets.addAll(getSkyframeEvaluatedTargetKeys());
        // asserting that our non-test rules matched between the two runs, we had to build different
        // versions of the three test targets but not the four non-test targets
        assertNumberOfConfigurationsOfTargets(visitedTargets, new ImmutableMap.Builder<String, Integer>().put("//test:suite", 2).put("//test:native_test", 2).put("//test:skylark_test", 2).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
    }

    @Test
    public void flagOnDifferentTestOptions_CacheKeptBetweenRuns() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeB");
        update("//test:suite");
        // asserting that the non-test rules were cached from the last run and did not need to be run
        // again
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:native_dep", 0).put("//test:skylark_dep", 0).put("//test:native_shared_dep", 0).put("//test:skylark_shared_dep", 0).build());
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite");
        // asserting that the test rules were cached from the first run and did not need to be run again
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:suite", 0).put("//test:native_test", 0).put("//test:skylark_test", 0).build());
    }

    @Test
    public void flagOnDifferentNonTestOptions_CacheCleared() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--define=Test=TypeA");
        update("//test:suite");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--define=Test=TypeB");
        update("//test:suite");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--define=Test=TypeA");
        update("//test:suite");
        // asserting that we got no overlap between the first and third runs, we had to reanalyze all
        // seven targets
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
    }

    @Test
    public void flagOffToOn_CacheCleared() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites");
        update("//test:suite");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites");
        update("//test:suite");
        // asserting that we got no overlap between the first and second runs, we had to reanalyze all
        // seven targets
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
    }

    @Test
    public void flagOnToOff_CacheCleared() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites");
        update("//test:suite");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites");
        update("//test:suite");
        // asserting that we got no overlap between the first and second runs, we had to reanalyze all
        // seven targets
        assertNumberOfConfigurationsOfTargets(getSkyframeEvaluatedTargetKeys(), new ImmutableMap.Builder<String, Integer>().put("//test:suite", 1).put("//test:native_test", 1).put("//test:skylark_test", 1).put("//test:native_dep", 1).put("//test:skylark_dep", 1).put("//test:native_shared_dep", 1).put("//test:skylark_shared_dep", 1).build());
    }

    @Test
    public void flagOnDynamicConfigsNotrimHostDeps_AreNotAnalyzedAnyExtraTimes() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "native_test(", "    name = 'native_outer_test',", "    deps = [':native_test', ':skylark_test'],", "    host_deps = [':native_test', ':skylark_test'],", ")", "skylark_test(", "    name = 'skylark_outer_test',", "    deps = [':native_test', ':skylark_test'],", "    host_deps = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", "    host_deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", "    host_deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", "    host_deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", "    host_deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--experimental_dynamic_configs=notrim");
        update("//test:native_outer_test", "//test:skylark_outer_test", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        LinkedHashSet<SkyKey> visitedTargets = new LinkedHashSet(getSkyframeEvaluatedTargetKeys());
        assertNumberOfConfigurationsOfTargets(visitedTargets, // each target should be analyzed in two and only two configurations: target and host
        // there should not be a "host trimmed" and "host untrimmed" version
        new ImmutableMap.Builder<String, Integer>().put("//test:native_test", 2).put("//test:skylark_test", 2).put("//test:native_dep", 2).put("//test:skylark_dep", 2).put("//test:native_shared_dep", 2).put("//test:skylark_shared_dep", 2).build());
    }

    @Test
    public void flagOnDynamicConfigsTrimHostDeps_AreNotAnalyzedAnyExtraTimes() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "native_test(", "    name = 'native_outer_test',", "    deps = [':native_test', ':skylark_test'],", "    host_deps = [':native_test', ':skylark_test'],", ")", "skylark_test(", "    name = 'skylark_outer_test',", "    deps = [':native_test', ':skylark_test'],", "    host_deps = [':native_test', ':skylark_test'],", ")", "native_test(", "    name = 'native_test',", "    deps = [':native_dep', ':skylark_dep'],", "    host_deps = [':native_dep', ':skylark_dep'],", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = [':native_dep', ':skylark_dep'],", "    host_deps = [':native_dep', ':skylark_dep'],", ")", "native_lib(", "    name = 'native_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", "    host_deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':native_shared_dep', 'skylark_shared_dep'],", "    host_deps = [':native_shared_dep', 'skylark_shared_dep'],", ")", "native_lib(", "    name = 'native_shared_dep',", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--experimental_dynamic_configs=on");
        update("//test:native_outer_test", "//test:skylark_outer_test", "//test:native_test", "//test:skylark_test", "//test:native_dep", "//test:skylark_dep", "//test:native_shared_dep", "//test:skylark_shared_dep");
        LinkedHashSet<SkyKey> visitedTargets = new LinkedHashSet(getSkyframeEvaluatedTargetKeys());
        assertNumberOfConfigurationsOfTargets(visitedTargets, // each target should be analyzed in two and only two configurations: target and host
        // there should not be a "host trimmed" and "host untrimmed" version
        new ImmutableMap.Builder<String, Integer>().put("//test:native_test", 2).put("//test:skylark_test", 2).put("//test:native_dep", 2).put("//test:skylark_dep", 2).put("//test:native_shared_dep", 2).put("//test:skylark_shared_dep", 2).build());
    }

    @Test
    public void flagOffConfigSetting_CanInspectTestOptions() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "config_setting(", "    name = 'test_mode',", "    values = {'test_arg': 'TypeA'},", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = select({':test_mode': [':skylark_shared_dep'], '//conditions:default': []})", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = select({':test_mode': [':skylark_shared_dep'], '//conditions:default': []})", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:test_mode", "//test:skylark_test", "//test:skylark_dep");
        // All 3 targets (top level, under a test, under a non-test) should successfully analyze.
        assertThat(getAnalysisResult().getTargetsToBuild()).hasSize(3);
    }

    @Test
    public void flagOnConfigSetting_FailsTryingToInspectTestOptions() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "config_setting(", "    name = 'test_mode',", "    values = {'test_arg': 'TypeA'},", ")", "skylark_test(", "    name = 'skylark_test',", "    deps = select({':test_mode': [':skylark_shared_dep'], '//conditions:default': []})", ")", "skylark_lib(", "    name = 'skylark_dep',", "    deps = select({':test_mode': [':skylark_shared_dep'], '//conditions:default': []})", ")", "skylark_lib(", "    name = 'skylark_shared_dep',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        try {
            // When reached through a non-test target analysis should fail
            update("//test:skylark_dep");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
        }
        assertContainsEvent("unknown option: 'test_arg'");
        update("//test:test_mode", "//test:skylark_test");
        // When reached through only test targets (top level, under a test) analysis should succeed
        assertThat(getAnalysisResult().getTargetsToBuild()).hasSize(2);
    }

    @Test
    public void flagOffNonTestTargetWithTestDependencies_IsPermitted() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':skylark_test'],", "    testonly = 1,", ")", "skylark_test(", "    name = 'skylark_test',", ")");
        useConfiguration("--notrim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:skylark_dep");
        assertThat(getAnalysisResult().getTargetsToBuild()).isNotEmpty();
    }

    @Test
    public void flagOnNonTestTargetWithTestDependencies_FailsAnalysis() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "skylark_lib(", "    name = 'skylark_dep',", "    deps = [':skylark_test'],", "    testonly = 1,", ")", "skylark_test(", "    name = 'skylark_test',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        try {
            update("//test:skylark_dep");
            Assert.fail();
        } catch (ViewCreationFailedException expected) {
        }
        assertContainsEvent(("all rules of type skylark_test require the presence of all of " + "[TestConfiguration], but these were all disabled"));
    }

    @Test
    public void flagOnTestSuiteWithTestDependencies_CanBeAnalyzed() throws Exception {
        scratch.file("test/BUILD", "load(':test.bzl', 'skylark_test')", "load(':lib.bzl', 'skylark_lib')", "test_suite(", "    name = 'suite',", "    tests = [':skylark_test', ':suite_2'],", ")", "test_suite(", "    name = 'suite_2',", "    tests = [':skylark_test_2', ':skylark_test_3'],", ")", "skylark_test(", "    name = 'skylark_test',", ")", "skylark_test(", "    name = 'skylark_test_2',", ")", "skylark_test(", "    name = 'skylark_test_3',", ")");
        useConfiguration("--trim_test_configuration", "--noexpand_test_suites", "--test_arg=TypeA");
        update("//test:suite", "//test:suite_2");
        assertThat(getAnalysisResult().getTargetsToBuild()).hasSize(2);
    }
}

