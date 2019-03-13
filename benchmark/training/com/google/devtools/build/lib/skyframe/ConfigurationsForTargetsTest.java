/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import ConfigurationResolver.SPLIT_DEP_ORDERING;
import Transience.PERSISTENT;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.Dependency;
import com.google.devtools.build.lib.analysis.DependencyResolver.DependencyKind;
import com.google.devtools.build.lib.analysis.TargetAndConfiguration;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.ConfigMatchingProvider;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.causes.Cause;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.packages.Aspect;
import com.google.devtools.build.lib.packages.RuleClassProvider;
import com.google.devtools.build.lib.packages.Target;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.OrderedSetMultimap;
import com.google.devtools.build.skyframe.AbstractSkyKey;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyFunctionException;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.devtools.build.lib.analysis.util.AnalysisTestCase.Flag.TRIMMED_CONFIGURATIONS;


/**
 * Tests {@link ConfiguredTargetFunction}'s logic for determining each target's
 * {@link BuildConfiguration}.
 *
 * <p>This is essentially an integration test for
 * {@link ConfiguredTargetFunction#computeDependencies} and {@link DependencyResolver}. These
 * methods form the core logic that figures out what a target's deps are, how their configurations
 * should differ from their parent, and how to instantiate those configurations as tangible
 * {@link BuildConfiguration} objects.
 *
 * <p>{@link ConfiguredTargetFunction} is a complicated class that does a lot of things. This test
 * focuses purely on the task of determining configurations for deps. So instead of evaluating
 * full {@link ConfiguredTargetFunction} instances, it evaluates a mock {@link SkyFunction} that
 * just wraps the {@link ConfiguredTargetFunction#computeDependencies} part. This keeps focus tight
 * and integration dependencies narrow.
 *
 * <p>We can't just call {@link ConfiguredTargetFunction#computeDependencies} directly because that
 * method needs a {@link SkyFunction.Environment} and Blaze's test infrastructure doesn't support
 * direct access to environments.
 */
@TestSpec(size = Suite.SMALL_TESTS)
@RunWith(JUnit4.class)
public class ConfigurationsForTargetsTest extends AnalysisTestCase {
    /**
     * A mock {@link SkyFunction} that just calls {@link ConfiguredTargetFunction#computeDependencies}
     * and returns its results.
     */
    private static class ComputeDependenciesFunction implements SkyFunction {
        static final SkyFunctionName SKYFUNCTION_NAME = SkyFunctionName.createHermetic("CONFIGURED_TARGET_FUNCTION_COMPUTE_DEPENDENCIES");

        private final ConfigurationsForTargetsTest.LateBoundStateProvider stateProvider;

        private final Supplier<BuildOptions> buildOptionsSupplier;

        ComputeDependenciesFunction(ConfigurationsForTargetsTest.LateBoundStateProvider lateBoundStateProvider, Supplier<BuildOptions> buildOptionsSupplier) {
            this.stateProvider = lateBoundStateProvider;
            this.buildOptionsSupplier = buildOptionsSupplier;
        }

        /**
         * Returns a {@link SkyKey} for a given <Target, BuildConfiguration> pair.
         */
        private static ConfigurationsForTargetsTest.ComputeDependenciesFunction.Key key(Target target, BuildConfiguration config) {
            return new ConfigurationsForTargetsTest.ComputeDependenciesFunction.Key(new TargetAndConfiguration(target, config));
        }

        private static class Key extends AbstractSkyKey<TargetAndConfiguration> {
            private Key(TargetAndConfiguration arg) {
                super(arg);
            }

            @Override
            public SkyFunctionName functionName() {
                return ConfigurationsForTargetsTest.ComputeDependenciesFunction.SKYFUNCTION_NAME;
            }
        }

        /**
         * Returns a {@link OrderedSetMultimap<Attribute, ConfiguredTarget>} map representing the
         * deps of given target.
         */
        static class Value implements SkyValue {
            OrderedSetMultimap<DependencyKind, ConfiguredTargetAndData> depMap;

            Value(OrderedSetMultimap<DependencyKind, ConfiguredTargetAndData> depMap) {
                this.depMap = depMap;
            }
        }

        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws ConfigurationsForTargetsTest.ComputeDependenciesFunction.EvalException, InterruptedException {
            try {
                OrderedSetMultimap<DependencyKind, ConfiguredTargetAndData> depMap = /* toolchainLabels= */
                ConfiguredTargetFunction.computeDependencies(env, new SkyframeDependencyResolver(env), ((TargetAndConfiguration) (skyKey.argument())), ImmutableList.<Aspect>of(), ImmutableMap.<Label, ConfigMatchingProvider>of(), ImmutableSet.of(), stateProvider.lateBoundRuleClassProvider(), stateProvider.lateBoundHostConfig(), NestedSetBuilder.<Package>stableOrder(), NestedSetBuilder.<Cause>stableOrder(), buildOptionsSupplier.get());
                return env.valuesMissing() ? null : new ConfigurationsForTargetsTest.ComputeDependenciesFunction.Value(depMap);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new ConfigurationsForTargetsTest.ComputeDependenciesFunction.EvalException(e);
            }
        }

        private static class EvalException extends SkyFunctionException {
            public EvalException(Exception cause) {
                super(cause, PERSISTENT);// We can generalize the transience if/when needed.

            }
        }

        @Override
        public String extractTag(SkyKey skyKey) {
            return getLabel().getName();
        }
    }

    /**
     * Provides build state to {@link ComputeDependenciesFunction}. This needs to be late-bound (i.e.
     * we can't just pass the contents directly) because of the way {@link AnalysisTestCase} works:
     * the {@link AnalysisMock} instance that instantiates the function gets created before the rest
     * of the build state. See {@link AnalysisTestCase#createMocks} for details.
     */
    private class LateBoundStateProvider {
        RuleClassProvider lateBoundRuleClassProvider() {
            return ruleClassProvider;
        }

        BuildConfiguration lateBoundHostConfig() {
            return getHostConfiguration();
        }
    }

    /**
     * An {@link AnalysisMock} that injects {@link ComputeDependenciesFunction} into the Skyframe
     * executor.
     */
    private static final class AnalysisMockWithComputeDepsFunction extends AnalysisMock.Delegate {
        private final ConfigurationsForTargetsTest.LateBoundStateProvider stateProvider;

        private final Supplier<BuildOptions> defaultBuildOptions;

        AnalysisMockWithComputeDepsFunction(ConfigurationsForTargetsTest.LateBoundStateProvider stateProvider, Supplier<BuildOptions> defaultBuildOptions) {
            super(AnalysisMock.get());
            this.stateProvider = stateProvider;
            this.defaultBuildOptions = defaultBuildOptions;
        }

        @Override
        public ImmutableMap<SkyFunctionName, SkyFunction> getSkyFunctions(BlazeDirectories directories) {
            return ImmutableMap.<SkyFunctionName, SkyFunction>builder().putAll(super.getSkyFunctions(directories)).put(ConfigurationsForTargetsTest.ComputeDependenciesFunction.SKYFUNCTION_NAME, new ConfigurationsForTargetsTest.ComputeDependenciesFunction(stateProvider, defaultBuildOptions)).build();
        }
    }

    @Test
    public void putOnlyEntryCorrectWithSetMultimap() throws Exception {
        internalTestPutOnlyEntry(HashMultimap.<String, String>create());
    }

    /**
     * Unlike {@link SetMultimap}, {@link ListMultimap} allows duplicate <Key, value> pairs. Make
     * sure that doesn't fool {@link ConfigurationResolver#putOnlyEntry}.
     */
    @Test
    public void putOnlyEntryCorrectWithListMultimap() throws Exception {
        internalTestPutOnlyEntry(ArrayListMultimap.<String, String>create());
    }

    @Test
    public void nullConfiguredDepsHaveExpectedConfigs() throws Exception {
        scratch.file("a/BUILD", "genrule(name = 'gen', srcs = ['gen.in'], cmd = '', outs = ['gen.out'])");
        ConfiguredTarget genIn = Iterables.getOnlyElement(getConfiguredDeps("//a:gen", "srcs"));
        assertThat(getConfiguration(genIn)).isNull();
    }

    @Test
    public void targetDeps() throws Exception {
        scratch.file("a/BUILD", "cc_library(name = 'dep1', srcs = ['dep1.cc'])", "cc_library(name = 'dep2', srcs = ['dep2.cc'])", "cc_binary(name = 'binary', srcs = ['main.cc'], deps = [':dep1', ':dep2'])");
        List<ConfiguredTarget> deps = getConfiguredDeps("//a:binary", "deps");
        assertThat(deps).hasSize(2);
        BuildConfiguration topLevelConfiguration = getConfiguration(Iterables.getOnlyElement(update("//a:binary").getTargetsToBuild()));
        for (ConfiguredTarget dep : deps) {
            assertThat(topLevelConfiguration.equalsOrIsSupersetOf(getConfiguration(dep))).isTrue();
        }
    }

    @Test
    public void hostDeps() throws Exception {
        scratch.file("a/BUILD", "cc_binary(name = 'host_tool', srcs = ['host_tool.cc'])", "genrule(name = 'gen', srcs = [], cmd = '', outs = ['gen.out'], tools = [':host_tool'])");
        ConfiguredTarget toolDep = Iterables.getOnlyElement(getConfiguredDeps("//a:gen", "tools"));
        assertThat(getConfiguration(toolDep).isHostConfiguration()).isTrue();
    }

    @Test
    public void splitDeps() throws Exception {
        // This test does not pass with trimming because android_binary applies an aspect and aspects
        // are not yet correctly supported with trimming.
        if (defaultFlags().contains(TRIMMED_CONFIGURATIONS)) {
            return;
        }
        /* appendToCurrentToolchain= */
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, false, MockCcSupport.emptyToolchainForCpu("armeabi-v7a"));
        scratch.file("java/a/BUILD", "cc_library(name = 'lib', srcs = ['lib.cc'])", "android_binary(name='a', manifest = 'AndroidManifest.xml', deps = [':lib'])");
        useConfiguration("--fat_apk_cpu=k8,armeabi-v7a");
        List<ConfiguredTarget> deps = getConfiguredDeps("//java/a:a", "deps");
        assertThat(deps).hasSize(2);
        ConfiguredTarget dep1 = deps.get(0);
        ConfiguredTarget dep2 = deps.get(1);
        assertThat(ImmutableList.<String>of(getConfiguration(dep1).getCpu(), getConfiguration(dep2).getCpu())).containsExactly("armeabi-v7a", "k8");
        // We don't care what order split deps are listed, but it must be deterministic.
        assertThat(SPLIT_DEP_ORDERING.compare(Dependency.withConfiguration(dep1.getLabel(), getConfiguration(dep1)), Dependency.withConfiguration(dep2.getLabel(), getConfiguration(dep2)))).isLessThan(0);
    }
}

