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
package com.google.devtools.build.lib.rules.cpp;


import Link.LinkTargetType.EXECUTABLE;
import Link.LinkingMode.DYNAMIC;
import LinkBuildVariables.RUNTIME_LIBRARY_SEARCH_DIRECTORIES;
import LinkTargetType.ALWAYS_LINK_PIC_STATIC_LIBRARY;
import LinkTargetType.ALWAYS_LINK_STATIC_LIBRARY;
import LinkTargetType.DYNAMIC_LIBRARY;
import LinkTargetType.INTERFACE_DYNAMIC_LIBRARY;
import LinkTargetType.NODEPS_DYNAMIC_LIBRARY;
import LinkTargetType.PIC_STATIC_LIBRARY;
import LinkTargetType.STATIC_LIBRARY;
import LinkingMode.STATIC;
import RepositoryName.MAIN;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.ArtifactExpander;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.RuleContext;
import com.google.devtools.build.lib.analysis.RunfilesProvider;
import com.google.devtools.build.lib.analysis.util.ActionTester;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.rules.cpp.CcToolchainFeatures.FeatureConfiguration;
import com.google.devtools.build.lib.rules.cpp.CcToolchainVariables.VariableValue;
import com.google.devtools.build.lib.rules.cpp.Link.LinkTargetType;
import com.google.devtools.build.lib.rules.cpp.LinkerInputs.LibraryToLink;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CppLinkAction}.
 */
@RunWith(JUnit4.class)
public class CppLinkActionTest extends BuildViewTestCase {
    @Test
    public void testToolchainFeatureFlags() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = CcToolchainFeaturesTest.buildFeatures(ruleContext, MockCcSupport.EMPTY_EXECUTABLE_ACTION_CONFIG, "feature {", "   name: 'a'", "   flag_set {", (("      action: '" + (EXECUTABLE.getActionName())) + "'"), "      flag_group { flag: 'some_flag' }", "   }", "}").getFeatureConfiguration(ImmutableSet.of("a", EXECUTABLE.getActionName()));
        CppLinkAction linkAction = createLinkBuilder(ruleContext, EXECUTABLE, "dummyRuleContext/out", ImmutableList.<Artifact>of(), ImmutableList.<LibraryToLink>of(), featureConfiguration).build();
        assertThat(linkAction.getArguments()).contains("some_flag");
    }

    @Test
    public void testExecutionRequirementsFromCrosstool() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = CcToolchainFeaturesTest.buildFeatures(ruleContext, "action_config {", (("   config_name: '" + (LinkTargetType.EXECUTABLE.getActionName())) + "'"), (("   action_name: '" + (LinkTargetType.EXECUTABLE.getActionName())) + "'"), "   tool {", "      tool_path: 'DUMMY_TOOL'", "      execution_requirement: 'dummy-exec-requirement'", "   }", "}").getFeatureConfiguration(ImmutableSet.of(LinkTargetType.EXECUTABLE.getActionName()));
        CppLinkAction linkAction = createLinkBuilder(ruleContext, LinkTargetType.EXECUTABLE, "dummyRuleContext/out", ImmutableList.of(), ImmutableList.of(), featureConfiguration).build();
        assertThat(linkAction.getExecutionInfo()).containsEntry("dummy-exec-requirement", "");
    }

    @Test
    public void testLibOptsAndLibSrcsAreInCorrectOrder() throws Exception {
        scratch.file("x/BUILD", "cc_binary(", "  name = 'foo',", "  srcs = ['some-dir/bar.so', 'some-other-dir/qux.so'],", "  linkopts = [", "    '-ldl',", "    '-lutil',", "  ],", ")");
        scratch.file("x/some-dir/bar.so");
        scratch.file("x/some-other-dir/qux.so");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//x:foo");
        CppLinkAction linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/foo")));
        List<String> arguments = linkAction.getLinkCommandLine().arguments();
        assertThat(Joiner.on(" ").join(arguments)).matches((".* -L[^ ]*some-dir(?= ).* -L[^ ]*some-other-dir(?= ).* " + "-lbar -lqux(?= ).* -ldl -lutil .*"));
        assertThat(Joiner.on(" ").join(arguments)).matches(".* -Wl,-rpath[^ ]*some-dir(?= ).* -Wl,-rpath[^ ]*some-other-dir .*");
    }

    @Test
    public void testLegacyWholeArchiveHasNoEffectOnDynamicModeDynamicLibraries() throws Exception {
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        scratch.file("x/BUILD", "cc_binary(", "  name = 'libfoo.so',", "  srcs = ['foo.cc'],", "  linkshared = 1,", "  linkstatic = 0,", ")");
        useConfiguration("--legacy_whole_archive");
        assertThat(getLibfooArguments()).doesNotContain("-Wl,-whole-archive");
    }

    @Test
    public void testLegacyWholeArchive() throws Exception {
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        scratch.file("x/BUILD", "cc_binary(", "  name = 'libfoo.so',", "  srcs = ['foo.cc'],", "  linkshared = 1,", ")");
        // --incompatible_remove_legacy_whole_archive not flipped, --legacy_whole_archive wins.
        useConfiguration("--legacy_whole_archive", "--noincompatible_remove_legacy_whole_archive");
        assertThat(getLibfooArguments()).contains("-Wl,-whole-archive");
        useConfiguration("--nolegacy_whole_archive", "--noincompatible_remove_legacy_whole_archive");
        assertThat(getLibfooArguments()).doesNotContain("-Wl,-whole-archive");
        // --incompatible_remove_legacy_whole_archive flipped, --legacy_whole_archive ignored.
        useConfiguration("--legacy_whole_archive", "--incompatible_remove_legacy_whole_archive");
        assertThat(getLibfooArguments()).doesNotContain("-Wl,-whole-archive");
        useConfiguration("--nolegacy_whole_archive", "--incompatible_remove_legacy_whole_archive");
        assertThat(getLibfooArguments()).doesNotContain("-Wl,-whole-archive");
        // Even when --nolegacy_whole_archive, features can still add the behavior back.
        useConfiguration("--nolegacy_whole_archive", "--noincompatible_remove_legacy_whole_archive", "--features=legacy_whole_archive");
        assertThat(getLibfooArguments()).contains("-Wl,-whole-archive");
        // Even when --nolegacy_whole_archive, features can still add the behavior, but not when
        // --incompatible_remove_legacy_whole_archive is flipped.
        useConfiguration("--incompatible_remove_legacy_whole_archive", "--features=legacy_whole_archive");
        assertThat(getLibfooArguments()).doesNotContain("-Wl,-whole-archive");
    }

    @Test
    public void testExposesRuntimeLibrarySearchDirectoriesVariable() throws Exception {
        scratch.file("x/BUILD", "cc_binary(", "  name = 'foo',", "  srcs = ['some-dir/bar.so', 'some-other-dir/qux.so'],", ")");
        scratch.file("x/some-dir/bar.so");
        scratch.file("x/some-other-dir/qux.so");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//x:foo");
        CppLinkAction linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/foo")));
        Iterable<? extends VariableValue> runtimeLibrarySearchDirectories = linkAction.getLinkCommandLine().getBuildVariables().getSequenceVariable(RUNTIME_LIBRARY_SEARCH_DIRECTORIES.getVariableName());
        List<String> directories = new ArrayList<>();
        for (VariableValue value : runtimeLibrarySearchDirectories) {
            directories.add(value.getStringValue("runtime_library_search_directory"));
        }
        assertThat(Joiner.on(" ").join(directories)).matches(".*some-dir .*some-other-dir");
    }

    @Test
    public void testCompilesTestSourcesIntoDynamicLibrary() throws Exception {
        if ((OS.getCurrent()) == (OS.WINDOWS)) {
            // Skip the test on Windows.
            // TODO(#7524): This test should work on Windows just fine, investigate and fix.
            return;
        }
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_PIC_FEATURE, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE);
        scratch.file("x/BUILD", "cc_test(name = 'a', srcs = ['a.cc'])", "cc_binary(name = 'b', srcs = ['a.cc'], linkstatic = 0)");
        scratch.file("x/a.cc", "int main() {}");
        useConfiguration("--experimental_link_compile_output_separately", "--force_pic");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//x:a");
        CppLinkAction linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/a")));
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin _solib_k8/libx_Sliba.ifso");
        assertThat(linkAction.getArguments()).contains(getBinArtifactWithNoOwner("_solib_k8/libx_Sliba.ifso").getExecPathString());
        RunfilesProvider runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).contains("bin _solib_k8/libx_Sliba.so");
        configuredTarget = getConfiguredTarget("//x:b");
        linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/b")));
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin x/_objs/b/a.pic.o");
        runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).containsExactly("bin x/b");
    }

    @Test
    public void testCompilesDynamicModeTestSourcesWithFeatureIntoDynamicLibrary() throws Exception {
        if ((OS.getCurrent()) == (OS.WINDOWS)) {
            // Skip the test on Windows.
            // TODO(#7524): This test should work on Windows just fine, investigate and fix.
            return;
        }
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_PIC_FEATURE, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE);
        scratch.file("x/BUILD", "cc_test(name='a', srcs=['a.cc'], features=['dynamic_link_test_srcs'])", "cc_binary(name='b', srcs=['a.cc'])", "cc_test(name='c', srcs=['a.cc'], features=['dynamic_link_test_srcs'], linkstatic=1)");
        scratch.file("x/a.cc", "int main() {}");
        useConfiguration("--force_pic");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//x:a");
        CppLinkAction linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/a")));
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin _solib_k8/libx_Sliba.ifso");
        assertThat(linkAction.getArguments()).contains(getBinArtifactWithNoOwner("_solib_k8/libx_Sliba.ifso").getExecPathString());
        RunfilesProvider runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).contains("bin _solib_k8/libx_Sliba.so");
        configuredTarget = getConfiguredTarget("//x:b");
        linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/b")));
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin x/_objs/b/a.pic.o");
        runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).containsExactly("bin x/b");
        configuredTarget = getConfiguredTarget("//x:c");
        linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/c")));
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin x/_objs/c/a.pic.o");
        runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).containsExactly("bin x/c");
    }

    @Test
    public void testCompilesDynamicModeBinarySourcesWithoutFeatureIntoDynamicLibrary() throws Exception {
        if ((OS.getCurrent()) == (OS.WINDOWS)) {
            // Skip the test on Windows.
            // TODO(#7524): This test should work on Windows just fine, investigate and fix.
            return;
        }
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_PIC_FEATURE);
        scratch.file("x/BUILD", "cc_binary(name = 'a', srcs = ['a.cc'], features = ['-static_link_test_srcs'])");
        scratch.file("x/a.cc", "int main() {}");
        useConfiguration("--force_pic", "--dynamic_mode=default");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//x:a");
        CppLinkAction linkAction = ((CppLinkAction) (getGeneratingAction(configuredTarget, "x/a")));
        assertThat(artifactsToStrings(linkAction.getInputs())).doesNotContain("bin _solib_k8/libx_Sliba.ifso");
        assertThat(artifactsToStrings(linkAction.getInputs())).contains("bin x/_objs/a/a.pic.o");
        RunfilesProvider runfilesProvider = configuredTarget.getProvider(RunfilesProvider.class);
        assertThat(artifactsToStrings(runfilesProvider.getDefaultRunfiles().getArtifacts())).containsExactly("bin x/a");
    }

    @Test
    public void testToolchainFeatureEnv() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = CcToolchainFeaturesTest.buildFeatures(ruleContext, MockCcSupport.EMPTY_EXECUTABLE_ACTION_CONFIG, "feature {", "   name: 'a'", "   env_set {", (("      action: '" + (EXECUTABLE.getActionName())) + "'"), "      env_entry { key: 'foo', value: 'bar' }", "   }", "}").getFeatureConfiguration(ImmutableSet.of(EXECUTABLE.getActionName(), "a"));
        CppLinkAction linkAction = createLinkBuilder(ruleContext, EXECUTABLE, "dummyRuleContext/out", ImmutableList.<Artifact>of(), ImmutableList.<LibraryToLink>of(), featureConfiguration).build();
        assertThat(linkAction.getIncompleteEnvironmentForTesting()).containsEntry("foo", "bar");
    }

    private enum NonStaticAttributes {

        OUTPUT_FILE,
        NATIVE_DEPS,
        USE_TEST_ONLY_FLAGS,
        FAKE,
        RUNTIME_SOLIB_DIR;}

    /**
     * This mainly checks that non-static links don't have identical keys. Many options are only
     * allowed on non-static links, and we test several of them here.
     */
    @Test
    public void testComputeKeyNonStatic() throws Exception {
        final RuleContext ruleContext = createDummyRuleContext();
        final PathFragment exeOutputPath = PathFragment.create("dummyRuleContext/output/path");
        final PathFragment dynamicOutputPath = PathFragment.create("dummyRuleContext/output/path.so");
        final Artifact staticOutputFile = getBinArtifactWithNoOwner(exeOutputPath.getPathString());
        final Artifact dynamicOutputFile = getBinArtifactWithNoOwner(dynamicOutputPath.getPathString());
        final FeatureConfiguration featureConfiguration = getMockFeatureConfiguration(ruleContext);
        ActionTester.runTest(CppLinkActionTest.NonStaticAttributes.class, new ActionTester.ActionCombinationFactory<CppLinkActionTest.NonStaticAttributes>() {
            @Override
            public Action generate(ImmutableSet<CppLinkActionTest.NonStaticAttributes> attributesToFlip) throws InterruptedException {
                CcToolchainProvider toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(ruleContext);
                CppLinkActionBuilder builder = new CppLinkActionBuilder(ruleContext, (attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.OUTPUT_FILE) ? dynamicOutputFile : staticOutputFile), toolchain, toolchain.getFdoContext(), featureConfiguration, MockCppSemantics.INSTANCE) {};
                if (attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.OUTPUT_FILE)) {
                    builder.setLinkType(NODEPS_DYNAMIC_LIBRARY);
                    builder.setLibraryIdentifier("foo");
                } else {
                    builder.setLinkType(LinkTargetType.EXECUTABLE);
                }
                builder.setLinkingMode(DYNAMIC);
                builder.setNativeDeps(attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.NATIVE_DEPS));
                builder.setUseTestOnlyFlags(attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.USE_TEST_ONLY_FLAGS));
                builder.setFake(attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.FAKE));
                builder.setToolchainLibrariesSolibDir((attributesToFlip.contains(CppLinkActionTest.NonStaticAttributes.RUNTIME_SOLIB_DIR) ? null : PathFragment.create("so1")));
                return builder.build();
            }
        }, actionKeyContext);
    }

    private enum StaticKeyAttributes {

        OUTPUT_FILE;}

    /**
     * This mainly checks that static library links don't have identical keys, and it also compares
     * them with simple dynamic library links.
     */
    @Test
    public void testComputeKeyStatic() throws Exception {
        final RuleContext ruleContext = createDummyRuleContext();
        final PathFragment staticOutputPath = PathFragment.create("dummyRuleContext/output/path.a");
        final PathFragment dynamicOutputPath = PathFragment.create("dummyRuleContext/output/path.so");
        final Artifact staticOutputFile = getBinArtifactWithNoOwner(staticOutputPath.getPathString());
        final Artifact dynamicOutputFile = getBinArtifactWithNoOwner(dynamicOutputPath.getPathString());
        final FeatureConfiguration featureConfiguration = getMockFeatureConfiguration(ruleContext);
        ActionTester.runTest(CppLinkActionTest.StaticKeyAttributes.class, new ActionTester.ActionCombinationFactory<CppLinkActionTest.StaticKeyAttributes>() {
            @Override
            public Action generate(ImmutableSet<CppLinkActionTest.StaticKeyAttributes> attributes) throws InterruptedException {
                CcToolchainProvider toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(ruleContext);
                CppLinkActionBuilder builder = new CppLinkActionBuilder(ruleContext, (attributes.contains(CppLinkActionTest.StaticKeyAttributes.OUTPUT_FILE) ? staticOutputFile : dynamicOutputFile), toolchain, toolchain.getFdoContext(), featureConfiguration, MockCppSemantics.INSTANCE) {};
                builder.setLinkType((attributes.contains(CppLinkActionTest.StaticKeyAttributes.OUTPUT_FILE) ? LinkTargetType.STATIC_LIBRARY : LinkTargetType.NODEPS_DYNAMIC_LIBRARY));
                builder.setLibraryIdentifier("foo");
                return builder.build();
            }
        }, actionKeyContext);
    }

    @Test
    public void testCommandLineSplitting() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        Artifact output = getDerivedArtifact(PathFragment.create("output/path.xyz"), getTargetConfiguration().getBinDirectory(MAIN), ActionsTestUtil.NULL_ARTIFACT_OWNER);
        final Artifact outputIfso = getDerivedArtifact(PathFragment.create("output/path.ifso"), getTargetConfiguration().getBinDirectory(MAIN), ActionsTestUtil.NULL_ARTIFACT_OWNER);
        CcToolchainProvider toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(ruleContext);
        CppLinkActionBuilder builder = new CppLinkActionBuilder(ruleContext, output, toolchain, toolchain.getFdoContext(), FeatureConfiguration.EMPTY, MockCppSemantics.INSTANCE);
        builder.setLinkType(STATIC_LIBRARY);
        assertThat(builder.canSplitCommandLine()).isTrue();
        builder.setLinkType(NODEPS_DYNAMIC_LIBRARY);
        assertThat(builder.canSplitCommandLine()).isTrue();
        builder.setInterfaceOutput(outputIfso);
        assertThat(builder.canSplitCommandLine()).isFalse();
        builder.setInterfaceOutput(null);
        builder.setLinkType(INTERFACE_DYNAMIC_LIBRARY);
        assertThat(builder.canSplitCommandLine()).isFalse();
    }

    /**
     * Links a small target. Checks that resource estimates are above the minimum and scale correctly.
     */
    @Test
    public void testSmallLocalLinkResourceEstimate() throws Exception {
        assertLinkSizeAccuracy(3);
    }

    /**
     * Fake links a large target. Checks that resource estimates are above the minimum and scale
     * correctly. The actual link action is irrelevant; we are just checking the estimate.
     */
    @Test
    public void testLargeLocalLinkResourceEstimate() throws Exception {
        assertLinkSizeAccuracy(7000);
    }

    @Test
    public void testInterfaceOutputWithoutBuildingDynamicLibraryIsError() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, LinkTargetType.EXECUTABLE).setInterfaceOutput(scratchArtifact("FakeInterfaceOutput"));
        CppLinkActionTest.assertError("Interface output can only be used with non-fake DYNAMIC_LIBRARY targets", builder);
    }

    @Test
    public void testInterfaceOutputForDynamicLibrary() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        useConfiguration();
        scratch.file("foo/BUILD", "cc_library(name = 'foo', srcs = ['foo.cc'])");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:foo");
        assertThat(configuredTarget).isNotNull();
        ImmutableList<String> inputs = ImmutableList.copyOf(getGeneratingAction(configuredTarget, "foo/libfoo.so").getInputs()).stream().map(Artifact::getExecPathString).collect(ImmutableList.toImmutableList(ImmutableList));
        assertThat(inputs.stream().anyMatch(( i) -> i.contains("tools/cpp/link_dynamic_library"))).isTrue();
    }

    @Test
    public void testInterfaceOutputForDynamicLibraryLegacy() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = CcToolchainFeaturesTest.buildFeatures(ruleContext, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE, "feature {", "   name: 'build_interface_libraries'", "   flag_set {", (("       action: '" + (NODEPS_DYNAMIC_LIBRARY.getActionName())) + "',"), "       flag_group {", "           flag: '%{generate_interface_library}'", "           flag: '%{interface_library_builder_path}'", "           flag: '%{interface_library_input_path}'", "           flag: '%{interface_library_output_path}'", "       }", "   }", "}", "feature {", "   name: 'dynamic_library_linker_tool'", "   flag_set {", "       action: 'c++-link-nodeps-dynamic-library'", "       flag_group {", "           flag: 'dynamic_library_linker_tool'", "       }", "   }", "}", "feature {", "    name: 'has_configured_linker_path'", "}", "action_config {", (("   config_name: '" + (NODEPS_DYNAMIC_LIBRARY.getActionName())) + "'"), (("   action_name: '" + (NODEPS_DYNAMIC_LIBRARY.getActionName())) + "'"), "   tool {", "       tool_path: 'custom/crosstool/scripts/link_dynamic_library.sh'", "   }", "   implies: 'has_configured_linker_path'", "   implies: 'build_interface_libraries'", "   implies: 'dynamic_library_linker_tool'", "}").getFeatureConfiguration(ImmutableSet.of("build_interface_libraries", "dynamic_library_linker_tool", NODEPS_DYNAMIC_LIBRARY.getActionName()));
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, NODEPS_DYNAMIC_LIBRARY, "foo.so", ImmutableList.<Artifact>of(), ImmutableList.<LibraryToLink>of(), featureConfiguration).setLibraryIdentifier("foo").setInterfaceOutput(scratchArtifact("FakeInterfaceOutput.ifso"));
        List<String> commandLine = builder.build().getCommandLine(null);
        assertThat(commandLine).hasSize(6);
        assertThat(commandLine.get(0)).endsWith("custom/crosstool/scripts/link_dynamic_library.sh");
        assertThat(commandLine.get(1)).isEqualTo("yes");
        assertThat(commandLine.get(2)).endsWith("tools/cpp/build_interface_so");
        assertThat(commandLine.get(3)).endsWith("foo.so");
        assertThat(commandLine.get(4)).isEqualTo("out/FakeInterfaceOutput.ifso");
        assertThat(commandLine.get(5)).isEqualTo("dynamic_library_linker_tool");
    }

    @Test
    public void testStaticLinkWithDynamicIsError() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, STATIC_LIBRARY).setLinkingMode(DYNAMIC).setLibraryIdentifier("foo");
        CppLinkActionTest.assertError("static library link must be static", builder);
    }

    @Test
    public void testStaticLinkWithNativeDepsIsError() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, STATIC_LIBRARY).setLinkingMode(STATIC).setLibraryIdentifier("foo").setNativeDeps(true);
        CppLinkActionTest.assertError("the native deps flag must be false for static links", builder);
    }

    @Test
    public void testStaticLinkWithWholeArchiveIsError() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, STATIC_LIBRARY).setLinkingMode(STATIC).setLibraryIdentifier("foo").setWholeArchive(true);
        CppLinkActionTest.assertError("the need whole archive flag must be false for static links", builder);
    }

    @Test
    public void testLinksTreeArtifactLibraries() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        SpecialArtifact testTreeArtifact = createTreeArtifact("library_directory");
        TreeFileArtifact library0 = ActionInputHelper.treeFileArtifact(testTreeArtifact, "library0.o");
        TreeFileArtifact library1 = ActionInputHelper.treeFileArtifact(testTreeArtifact, "library1.o");
        ArtifactExpander expander = new ArtifactExpander() {
            @Override
            public void expand(Artifact artifact, Collection<? super Artifact> output) {
                if (artifact.equals(testTreeArtifact)) {
                    output.add(library0);
                    output.add(library1);
                }
            }
        };
        CppLinkActionBuilder builder = createLinkBuilder(ruleContext, STATIC_LIBRARY).setLibraryIdentifier("foo").addObjectFiles(ImmutableList.of(testTreeArtifact));
        CppLinkAction linkAction = builder.build();
        Iterable<String> treeArtifactsPaths = ImmutableList.of(testTreeArtifact.getExecPathString());
        Iterable<String> treeFileArtifactsPaths = ImmutableList.of(library0.getExecPathString(), library1.getExecPathString());
        // Should only reference the tree artifact.
        verifyArguments(linkAction.getLinkCommandLine().getRawLinkArgv(), treeArtifactsPaths, treeFileArtifactsPaths);
        // Should only reference tree file artifacts.
        verifyArguments(linkAction.getLinkCommandLine().getRawLinkArgv(expander), treeFileArtifactsPaths, treeArtifactsPaths);
    }

    @Test
    public void testStaticLinking() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        ImmutableList<LinkTargetType> targetTypesToTest = ImmutableList.of(STATIC_LIBRARY, PIC_STATIC_LIBRARY, ALWAYS_LINK_STATIC_LIBRARY, ALWAYS_LINK_PIC_STATIC_LIBRARY);
        SpecialArtifact testTreeArtifact = createTreeArtifact("library_directory");
        TreeFileArtifact library0 = ActionInputHelper.treeFileArtifact(testTreeArtifact, "library0.o");
        TreeFileArtifact library1 = ActionInputHelper.treeFileArtifact(testTreeArtifact, "library1.o");
        ArtifactExpander expander = ( artifact, output) -> {
            if (artifact.equals(testTreeArtifact)) {
                output.add(library0);
                output.add(library1);
            }
        };
        Artifact objectFile = scratchArtifact("objectFile.o");
        for (LinkTargetType linkType : targetTypesToTest) {
            scratch.deleteFile("dummyRuleContext/BUILD");
            Artifact output = scratchArtifact(("output." + (linkType.getDefaultExtension())));
            CppLinkActionBuilder builder = // Makes sure this doesn't use a params file.
            createLinkBuilder(ruleContext, linkType, output.getExecPathString(), ImmutableList.<Artifact>of(), ImmutableList.<LibraryToLink>of(), getMockFeatureConfiguration(ruleContext)).setLibraryIdentifier("foo").addObjectFiles(ImmutableList.of(testTreeArtifact)).addObjectFile(objectFile).setFake(true);
            CppLinkAction linkAction = builder.build();
            assertThat(linkAction.getCommandLine(expander)).containsAllOf(library0.getExecPathString(), library1.getExecPathString(), objectFile.getExecPathString()).inOrder();
        }
    }

    /**
     * Tests that -pie is removed when -shared is also present (http://b/5611891#).
     */
    @Test
    public void testPieOptionDisabledForSharedLibraries() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkAction linkAction = createLinkBuilder(ruleContext, DYNAMIC_LIBRARY, "dummyRuleContext/out.so", ImmutableList.of(), ImmutableList.of(), getMockFeatureConfiguration(ruleContext)).setLinkingMode(Link.LinkingMode.STATIC).addLinkopts(ImmutableList.of("-pie", "-other", "-pie")).setLibraryIdentifier("foo").build();
        List<String> argv = linkAction.getLinkCommandLine().getRawLinkArgv();
        assertThat(argv).doesNotContain("-pie");
        assertThat(argv).contains("-other");
    }

    /**
     * Tests that -pie is removed when -shared is also present (http://b/5611891#).
     */
    @Test
    public void testPieOptionKeptForExecutables() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkAction linkAction = createLinkBuilder(ruleContext, LinkTargetType.EXECUTABLE, "dummyRuleContext/out", ImmutableList.of(), ImmutableList.of(), getMockFeatureConfiguration(ruleContext)).setLinkingMode(Link.LinkingMode.STATIC).addLinkopts(ImmutableList.of("-pie", "-other", "-pie")).build();
        List<String> argv = linkAction.getLinkCommandLine().getRawLinkArgv();
        assertThat(argv).contains("-pie");
        assertThat(argv).contains("-other");
    }

    @Test
    public void testLinkoptsComeAfterLinkerInputs() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        String solibPrefix = "_solib_k8";
        Iterable<LibraryToLink> linkerInputs = LinkerInputs.opaqueLibrariesToLink(ArtifactCategory.DYNAMIC_LIBRARY, ImmutableList.of(getOutputArtifact((solibPrefix + "/FakeLinkerInput1.so")), getOutputArtifact((solibPrefix + "/FakeLinkerInput2.so")), getOutputArtifact((solibPrefix + "/FakeLinkerInput3.so")), getOutputArtifact((solibPrefix + "/FakeLinkerInput4.so"))));
        CppLinkAction linkAction = createLinkBuilder(ruleContext, LinkTargetType.EXECUTABLE, "dummyRuleContext/out", ImmutableList.of(), ImmutableList.copyOf(linkerInputs), getMockFeatureConfiguration(ruleContext)).addLinkopts(ImmutableList.of("FakeLinkopt1", "FakeLinkopt2")).build();
        List<String> argv = linkAction.getLinkCommandLine().getRawLinkArgv();
        int lastLinkerInputIndex = Ints.max(argv.indexOf("FakeLinkerInput1"), argv.indexOf("FakeLinkerInput2"), argv.indexOf("FakeLinkerInput3"), argv.indexOf("FakeLinkerInput4"));
        int firstLinkoptIndex = Math.min(argv.indexOf("FakeLinkopt1"), argv.indexOf("FakeLinkopt2"));
        assertThat(lastLinkerInputIndex).isLessThan(firstLinkoptIndex);
    }

    @Test
    public void testLinkoptsAreOmittedForStaticLibrary() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkAction linkAction = createLinkBuilder(ruleContext, STATIC_LIBRARY).addLinkopt("FakeLinkopt1").setLibraryIdentifier("foo").build();
        assertThat(linkAction.getLinkCommandLine().getLinkopts()).isEmpty();
    }

    @Test
    public void testSplitExecutableLinkCommandStatic() throws Exception {
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.DO_NOT_SPLIT_LINKING_CMDLINE_FEATURE);
        RuleContext ruleContext = createDummyRuleContext();
        CppLinkAction linkAction = createLinkBuilder(ruleContext, LinkTargetType.EXECUTABLE).build();
        Pair<List<String>, List<String>> result = linkAction.getLinkCommandLine().splitCommandline();
        String linkCommandLine = Joiner.on(" ").join(result.first);
        assertThat(linkCommandLine).contains("gcc_tool");
        assertThat(linkCommandLine).contains("-o");
        assertThat(linkCommandLine).contains("output/path.a");
        assertThat(linkCommandLine).contains("path.a-2.params");
        assertThat(result.second).contains("-lcpp_standard_library");
    }

    @Test
    public void testSplitExecutableLinkCommandDynamicWithNoSplitting() throws Exception {
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.DO_NOT_SPLIT_LINKING_CMDLINE_FEATURE);
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = getMockFeatureConfiguration(ruleContext);
        CppLinkAction linkAction = createLinkBuilder(ruleContext, DYNAMIC_LIBRARY, "dummyRuleContext/out.so", ImmutableList.of(), ImmutableList.of(), featureConfiguration).setLibraryIdentifier("library").build();
        Pair<List<String>, List<String>> result = linkAction.getLinkCommandLine().splitCommandline();
        assertThat(result.first.stream().map(( x) -> removeOutDirectory(x)).collect(ImmutableList.toImmutableList(ImmutableList))).containsExactly("crosstool/gcc_tool", "@/k8-fastbuild/bin/dummyRuleContext/out.so-2.params").inOrder();
        assertThat(result.second.stream().map(( x) -> removeOutDirectory(x)).collect(ImmutableList.toImmutableList(ImmutableList))).containsExactly("-shared", "-o", "/k8-fastbuild/bin/dummyRuleContext/out.so", "-Wl,-S", "--sysroot=/usr/grte/v1").inOrder();
    }

    // TODO(b/113358321): Remove once #7670 is finished.
    @Test
    @Deprecated
    public void testSplitExecutableLinkCommandDynamicWithSplitting() throws Exception {
        RuleContext ruleContext = createDummyRuleContext();
        FeatureConfiguration featureConfiguration = getMockFeatureConfiguration(ruleContext);
        CppLinkAction linkAction = createLinkBuilder(ruleContext, DYNAMIC_LIBRARY, "dummyRuleContext/out.so", ImmutableList.of(), ImmutableList.of(), featureConfiguration).setLibraryIdentifier("library").build();
        Pair<List<String>, List<String>> result = linkAction.getLinkCommandLine().splitCommandline();
        assertThat(result.first.stream().map(( x) -> removeOutDirectory(x)).collect(ImmutableList.toImmutableList(ImmutableList))).containsExactly("crosstool/gcc_tool", "-shared", "-o", "/k8-fastbuild/bin/dummyRuleContext/out.so", "-Wl,-S", "--sysroot=/usr/grte/v1", "@/k8-fastbuild/bin/dummyRuleContext/out.so-2.params").inOrder();
        assertThat(result.second).isEmpty();
    }
}

