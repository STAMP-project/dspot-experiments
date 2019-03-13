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
package com.google.devtools.build.lib.rules.objc;


import AppleBinary.BUNDLE_LOADER_NOT_IN_BUNDLE_ERROR;
import BinaryType.DYLIB;
import BinaryType.LOADABLE_BUNDLE;
import CompilationMode.DBG;
import CompilationMode.FASTBUILD;
import InstrumentedFilesInfo.SKYLARK_CONSTRUCTOR;
import MultiArchSplitTransitionProvider.UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT;
import PlatformType.MACOS;
import RepositoryName.MAIN;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.CommandAction;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.actions.SymlinkAction;
import com.google.devtools.build.lib.analysis.test.InstrumentedFilesInfo;
import com.google.devtools.build.lib.rules.apple.ApplePlatform;
import com.google.devtools.build.lib.rules.apple.AppleToolchain;
import com.google.devtools.build.lib.rules.objc.AppleBinary.BinaryType;
import com.google.devtools.build.lib.rules.objc.CompilationSupport.ExtraLinkArgs;
import com.google.devtools.build.lib.syntax.SkylarkDict;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for apple_binary.
 */
@RunWith(JUnit4.class)
public class AppleBinaryTest extends ObjcRuleTestCase {
    static final RuleType RULE_TYPE = new RuleType("apple_binary") {
        @Override
        Iterable<String> requiredAttributes(Scratch scratch, String packageDir, Set<String> alreadyAdded) throws IOException {
            ImmutableList.Builder<String> attributes = new ImmutableList.Builder<>();
            if (!(alreadyAdded.contains("deps"))) {
                String depPackageDir = packageDir + "_defaultDep";
                scratch.file((depPackageDir + "/a.m"));
                scratch.file((depPackageDir + "/private.h"));
                scratch.file((depPackageDir + "/BUILD"), "objc_library(name = 'lib_dep', srcs = ['a.m', 'private.h'])");
                attributes.add(((("deps = ['//" + depPackageDir) + ":") + "lib_dep']"));
            }
            if (!(alreadyAdded.contains("platform_type"))) {
                attributes.add("platform_type = 'ios'");
            }
            if (!(alreadyAdded.contains("binary_type"))) {
                attributes.add("binary_type = 'executable'");
            }
            return attributes.build();
        }
    };

    private static final String COCOA_FRAMEWORK_FLAG = "-framework Cocoa";

    private static final String FOUNDATION_FRAMEWORK_FLAG = "-framework Foundation";

    private static final String UIKIT_FRAMEWORK_FLAG = "-framework UIKit";

    private static final ImmutableSet<String> IMPLICIT_NON_MAC_FRAMEWORK_FLAGS = ImmutableSet.of(AppleBinaryTest.FOUNDATION_FRAMEWORK_FLAG, AppleBinaryTest.UIKIT_FRAMEWORK_FLAG);

    private static final ImmutableSet<String> IMPLICIT_MAC_FRAMEWORK_FLAGS = ImmutableSet.of(AppleBinaryTest.FOUNDATION_FRAMEWORK_FLAG);

    private static final ImmutableSet<String> COCOA_FEATURE_FLAGS = ImmutableSet.of(AppleBinaryTest.COCOA_FRAMEWORK_FLAG);

    @Test
    public void testOutputDirectoryWithMandatoryMinimumVersion() throws Exception {
        scratch.file("a/BUILD", "apple_binary(name='a', platform_type='ios', deps=['b'], minimum_os_version='7.0')", "objc_library(name='b', srcs=['b.c'])");
        useConfiguration("--experimental_apple_mandatory_minimum_version", "ios_cpus=i386");
        ConfiguredTarget a = getConfiguredTarget("//a:a");
        ConfiguredTarget b = getDirectPrerequisite(a, "//a:b");
        PathFragment aPath = getConfiguration(a).getOutputDirectory(MAIN).getExecPath();
        PathFragment bPath = getConfiguration(b).getOutputDirectory(MAIN).getExecPath();
        assertThat(aPath.getPathString()).doesNotMatch("-min[0-9]");
        assertThat(bPath.getPathString()).contains("-min7.0-");
    }

    @Test
    public void testMandatoryMinimumVersionEnforced() throws Exception {
        scratch.file("a/BUILD", "apple_binary(name='a', platform_type='ios')");
        useConfiguration("--experimental_apple_mandatory_minimum_version");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//a:a");
        assertContainsEvent("This attribute must be explicitly specified");
    }

    @Test
    public void testMandatoryMinimumOsVersionUnset() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--experimental_apple_mandatory_minimum_version");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//x:x");
        assertContainsEvent("must be explicitly specified");
    }

    @Test
    public void testMandatoryMinimumOsVersionSet() throws Exception {
        getRuleType().scratchTarget(scratch, "minimum_os_version", "'8.0'", "platform_type", "'watchos'");
        useConfiguration("--experimental_apple_mandatory_minimum_version");
        getConfiguredTarget("//x:x");
    }

    @Test
    public void testLipoActionEnv() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=i386,armv7k", "--xcode_version=7.3", "--watchos_sdk_version=2.1");
        CommandAction action = ((CommandAction) (lipoBinAction("//x:x")));
        assertAppleSdkVersionEnv(action, "2.1");
        assertAppleSdkPlatformEnv(action, "WatchOS");
        assertXcodeVersionEnv(action, "7.3");
    }

    @Test
    public void testSymlinkInsteadOfLipoSingleArch() throws Exception {
        getRuleType().scratchTarget(scratch);
        SymlinkAction action = ((SymlinkAction) (lipoBinAction("//x:x")));
        CommandAction linkAction = linkAction("//x:x");
        assertThat(action.getInputs()).containsExactly(Iterables.getOnlyElement(linkAction.getOutputs()));
    }

    @Test
    public void testLipoActionEnv_sdkVersionPadding() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=i386,armv7k", "--xcode_version=7.3", "--watchos_sdk_version=2");
        CommandAction action = ((CommandAction) (lipoBinAction("//x:x")));
        assertAppleSdkVersionEnv(action, "2.0");
    }

    @Test
    public void testCcDependencyLinkoptsArePropagatedToLinkAction() throws Exception {
        checkCcDependencyLinkoptsArePropagatedToLinkAction(getRuleType());
    }

    @Test
    public void testUnknownPlatformType() throws Exception {
        checkError("package", "test", String.format(UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT, "meow_meow_os"), "apple_binary(name = 'test', platform_type = 'meow_meow_os')");
    }

    @Test
    public void testProtoDylibDeps() throws Exception {
        checkProtoDedupingDeps(DYLIB);
    }

    @Test
    public void testProtoBundleLoaderDeps() throws Exception {
        checkProtoDedupingDeps(LOADABLE_BUNDLE);
    }

    @Test
    public void testProtoDylibDepsPartial() throws Exception {
        checkProtoDedupingDepsPartial(AppleBinary.BinaryType.DYLIB);
    }

    @Test
    public void testProtoBundleLoaderDepsPartial() throws Exception {
        checkProtoDedupingDepsPartial(AppleBinary.BinaryType.LOADABLE_BUNDLE);
    }

    @Test
    public void testProtoDepsViaDylib() throws Exception {
        checkProtoDisjointDeps(DYLIB);
    }

    @Test
    public void testProtoDepsViaBundleLoader() throws Exception {
        checkProtoDisjointDeps(LOADABLE_BUNDLE);
    }

    @Test
    public void testProtoBundlingWithTargetsWithNoDeps() throws Exception {
        checkProtoBundlingWithTargetsWithNoDeps(getRuleType());
    }

    @Test
    public void testProtoBundlingDoesNotHappen() throws Exception {
        useConfiguration("--noenable_apple_binary_native_protos");
        checkProtoBundlingDoesNotHappen(getRuleType());
    }

    @Test
    public void testAvoidDepsObjectsWithCrosstool() throws Exception {
        checkAvoidDepsObjectsWithCrosstool(getRuleType());
    }

    @Test
    public void testBundleLoaderCantBeSetWithoutBundleBinaryType() throws Exception {
        getRuleType().scratchTarget(scratch);
        checkError("bundle", "bundle", BUNDLE_LOADER_NOT_IN_BUNDLE_ERROR, "apple_binary(", "    name = 'bundle',", "    bundle_loader = '//x:x',", "    platform_type = 'ios',", ")");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvider_dylib() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   provider = dep[apple_common.AppleDylibBinary]", "   return struct(", "      binary = provider.binary,", "      objc = provider.objc,", "      dep_dir = dir(dep),", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False,)", "})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "apple_binary(", "    name = 'bin',", "    deps = [':lib'],", (("    binary_type = '" + (BinaryType.DYLIB)) + "',"), "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")", "test_rule(", "    name = 'my_target',", "    deps = [':bin'],", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("binary")).isInstanceOf(Artifact.class);
        assertThat(skylarkTarget.get("objc")).isInstanceOf(ObjcProvider.class);
        List<String> depProviders = ((List<String>) (skylarkTarget.getValue("dep_dir")));
        assertThat(depProviders).doesNotContain("AppleExecutableBinary");
        assertThat(depProviders).doesNotContain("AppleLoadableBundleBinary");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvider_executable() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   provider = dep[apple_common.AppleExecutableBinary]", "   return struct(", "      binary = provider.binary,", "      objc = provider.objc,", "      dep_dir = dir(dep),", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False,)", "})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "apple_binary(", "    name = 'bin',", "    deps = [':lib'],", (("    binary_type = '" + (BinaryType.EXECUTABLE)) + "',"), "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")", "test_rule(", "    name = 'my_target',", "    deps = [':bin'],", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("binary")).isInstanceOf(Artifact.class);
        assertThat(skylarkTarget.get("objc")).isInstanceOf(ObjcProvider.class);
        List<String> depProviders = ((List<String>) (skylarkTarget.get("dep_dir")));
        assertThat(depProviders).doesNotContain("AppleDylibBinary");
        assertThat(depProviders).doesNotContain("AppleLoadableBundleBinary");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvider_loadableBundle() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   provider = dep[apple_common.AppleLoadableBundleBinary]", "   return struct(", "      binary = provider.binary,", "      dep_dir = dir(dep),", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False,)", "})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "apple_binary(", "    name = 'bin',", "    deps = ['lib'],", (("    binary_type = '" + (BinaryType.LOADABLE_BUNDLE)) + "',"), "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")", "test_rule(", "    name = 'my_target',", "    deps = [':bin'],", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(((Artifact) (skylarkTarget.get("binary")))).isNotNull();
        List<String> depProviders = ((List<String>) (skylarkTarget.get("dep_dir")));
        assertThat(depProviders).doesNotContain("AppleExecutableBinary");
        assertThat(depProviders).doesNotContain("AppleDylibBinary");
    }

    @Test
    public void testDuplicateLinkopts() throws Exception {
        getRuleType().scratchTarget(scratch, "linkopts", "['-foo', 'bar', '-foo', 'baz']");
        CommandAction linkAction = linkAction("//x:x");
        String linkArgs = Joiner.on(" ").join(linkAction.getArguments());
        assertThat(linkArgs).contains("-Wl,-foo -Wl,bar");
        assertThat(linkArgs).contains("-Wl,-foo -Wl,baz");
    }

    @Test
    public void testCanUseCrosstool_singleArch() throws Exception {
        checkLinkingRuleCanUseCrosstool_singleArch(getRuleType());
    }

    @Test
    public void testCanUseCrosstool_multiArch() throws Exception {
        checkLinkingRuleCanUseCrosstool_multiArch(getRuleType());
    }

    @Test
    public void testAppleSdkIphoneosPlatformEnv() throws Exception {
        checkAppleSdkIphoneosPlatformEnv(getRuleType());
    }

    @Test
    public void testXcodeVersionEnv() throws Exception {
        checkXcodeVersionEnv(getRuleType());
    }

    @Test
    public void testLinksImplicitFrameworksWithCrosstoolIos() throws Exception {
        useConfiguration("--ios_multi_cpus=x86_64", "--ios_sdk_version=10.0", "--ios_minimum_os=8.0");
        getRuleType().scratchTarget(scratch, "platform_type", "'ios'");
        Action lipoAction = actionProducingArtifact("//x:x", "_lipobin");
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), "x/x_bin");
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.IMPLICIT_NON_MAC_FRAMEWORK_FLAGS);
    }

    @Test
    public void testLinksImplicitFrameworksWithCrosstoolWatchos() throws Exception {
        useConfiguration("--watchos_cpus=i386", "--watchos_sdk_version=3.0", "--watchos_minimum_os=2.0");
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        Action lipoAction = actionProducingArtifact("//x:x", "_lipobin");
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), "x/x_bin");
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.IMPLICIT_NON_MAC_FRAMEWORK_FLAGS);
    }

    @Test
    public void testLinksImplicitFrameworksWithCrosstoolTvos() throws Exception {
        useConfiguration("--tvos_cpus=x86_64", "--tvos_sdk_version=10.1", "--tvos_minimum_os=10.0");
        getRuleType().scratchTarget(scratch, "platform_type", "'tvos'");
        Action lipoAction = actionProducingArtifact("//x:x", "_lipobin");
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), "x/x_bin");
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.IMPLICIT_NON_MAC_FRAMEWORK_FLAGS);
    }

    @Test
    public void testLinksImplicitFrameworksWithCrosstoolMacos() throws Exception {
        useConfiguration("--macos_cpus=x86_64", "--macos_sdk_version=10.11", "--macos_minimum_os=10.11");
        getRuleType().scratchTarget(scratch, "platform_type", "'macos'");
        Action lipoAction = actionProducingArtifact("//x:x", "_lipobin");
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), "x/x_bin");
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.IMPLICIT_MAC_FRAMEWORK_FLAGS);
        assertThat(linkAction.getArguments()).containsNoneOf(AppleBinaryTest.COCOA_FRAMEWORK_FLAG, AppleBinaryTest.UIKIT_FRAMEWORK_FLAG);
    }

    @Test
    public void testLinkCocoaFeatureWithCrosstoolMacos() throws Exception {
        useConfiguration("--macos_cpus=x86_64", "--macos_sdk_version=10.11", "--macos_minimum_os=10.11");
        getRuleType().scratchTarget(scratch, "platform_type", "'macos'", "features", "['link_cocoa']");
        Action lipoAction = actionProducingArtifact("//x:x", "_lipobin");
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), "x/x_bin");
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.IMPLICIT_MAC_FRAMEWORK_FLAGS);
        assertThat(linkAction.getArguments()).containsAllIn(AppleBinaryTest.COCOA_FEATURE_FLAGS);
        assertThat(linkAction.getArguments()).doesNotContain(AppleBinaryTest.UIKIT_FRAMEWORK_FLAG);
    }

    @Test
    public void testAliasedLinkoptsThroughObjcLibrary() throws Exception {
        checkAliasedLinkoptsThroughObjcLibrary(getRuleType());
    }

    @Test
    public void testObjcProviderLinkInputsInLinkAction() throws Exception {
        checkObjcProviderLinkInputsInLinkAction(getRuleType());
    }

    @Test
    public void testAppleSdkVersionEnv() throws Exception {
        checkAppleSdkVersionEnv(getRuleType());
    }

    @Test
    public void testNonDefaultAppleSdkVersionEnv() throws Exception {
        checkNonDefaultAppleSdkVersionEnv(getRuleType());
    }

    @Test
    public void testAppleSdkDefaultPlatformEnv() throws Exception {
        checkAppleSdkDefaultPlatformEnv(getRuleType());
    }

    @Test
    public void testAvoidDepsThroughDylib() throws Exception {
        checkAvoidDepsThroughDylib(getRuleType());
    }

    @Test
    public void testAvoidDepsObjects_avoidViaCcLibrary() throws Exception {
        checkAvoidDepsObjects_avoidViaCcLibrary(getRuleType());
    }

    @Test
    public void testBundleLoaderIsCorrectlyPassedToTheLinker() throws Exception {
        checkBundleLoaderIsCorrectlyPassedToTheLinker(getRuleType());
    }

    @Test
    public void testLipoBinaryAction() throws Exception {
        checkLipoBinaryAction(getRuleType());
    }

    @Test
    public void testLinkActionHasCorrectIosSimulatorMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--ios_multi_cpus=x86_64", "--ios_sdk_version=10.0", "--ios_minimum_os=8.0");
        checkLinkMinimumOSVersion("-mios-simulator-version-min=8.0");
    }

    @Test
    public void testLinkActionHasCorrectIosMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--ios_multi_cpus=arm64", "--ios_sdk_version=10.0", "--ios_minimum_os=8.0");
        checkLinkMinimumOSVersion("-miphoneos-version-min=8.0");
    }

    @Test
    public void testWatchSimulatorDepCompile() throws Exception {
        checkWatchSimulatorDepCompile(getRuleType());
    }

    @Test
    public void testDylibBinaryType() throws Exception {
        getRuleType().scratchTarget(scratch, "binary_type", "'dylib'");
        CommandAction linkAction = linkAction("//x:x");
        assertThat(Joiner.on(" ").join(linkAction.getArguments())).contains("-dynamiclib");
    }

    @Test
    public void testBinaryTypeIsCorrectlySetToBundle() throws Exception {
        getRuleType().scratchTarget(scratch, "binary_type", "'loadable_bundle'");
        CommandAction linkAction = linkAction("//x:x");
        assertThat(Joiner.on(" ").join(linkAction.getArguments())).contains("-bundle");
    }

    @Test
    public void testMultiarchCcDep() throws Exception {
        checkMultiarchCcDep(getRuleType());
    }

    @Test
    public void testWatchSimulatorLipoAction() throws Exception {
        checkWatchSimulatorLipoAction(getRuleType());
    }

    @Test
    public void testFrameworkDepLinkFlags() throws Exception {
        checkFrameworkDepLinkFlags(getRuleType(), new ExtraLinkArgs());
    }

    @Test
    public void testDylibDependencies() throws Exception {
        checkDylibDependencies(getRuleType(), new ExtraLinkArgs());
    }

    @Test
    public void testMinimumOs() throws Exception {
        checkMinimumOsLinkAndCompileArg(getRuleType());
    }

    @Test
    public void testMinimumOs_watchos() throws Exception {
        checkMinimumOsLinkAndCompileArg_watchos(getRuleType());
    }

    @Test
    public void testMinimumOs_invalid_nonVersion() throws Exception {
        checkMinimumOs_invalid_nonVersion(getRuleType());
    }

    @Test
    public void testMinimumOs_invalid_containsAlphabetic() throws Exception {
        checkMinimumOs_invalid_containsAlphabetic(getRuleType());
    }

    @Test
    public void testMinimumOs_invalid_tooManyComponents() throws Exception {
        checkMinimumOs_invalid_tooManyComponents(getRuleType());
    }

    @Test
    public void testGenfilesProtoGetsCorrectPath() throws Exception {
        scratch.file("examples/BUILD", "package(default_visibility = ['//visibility:public'])", "apple_binary(", "    name = 'bin',", "    deps = [':objc_protos'],", "    platform_type = 'ios',", ")", "objc_proto_library(", "    name = 'objc_protos',", "    portable_proto_filters = ['filter.pbascii'],", "    deps = [':protos'],", ")", "proto_library(", "    name = 'protos',", "    srcs = ['genfile.proto'],", ")", "genrule(", "    name = 'copy_proto',", "    srcs = ['original.proto'],", "    outs = ['genfile.proto'],", "    cmd = '/bin/cp $< $@',", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        Action lipoAction = actionProducingArtifact("//examples:bin", "_lipobin");
        ArrayList<String> genfileRoots = new ArrayList<>();
        for (Artifact archBinary : lipoAction.getInputs()) {
            if (archBinary.getExecPathString().endsWith("bin_bin")) {
                Artifact protoLib = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(archBinary).getInputs(), "BundledProtos.a");
                Artifact protoObject = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(protoLib).getInputs(), "Genfile.pbobjc.o");
                Artifact protoObjcSource = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(protoObject).getInputs(), "Genfile.pbobjc.m");
                Artifact protoSource = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(protoObjcSource).getInputs(), "genfile.proto");
                genfileRoots.add(protoSource.getRoot().getExecPathString());
            }
        }
        // Make sure there are genrules for both arm64 and armv7 configurations.
        Collections.sort(genfileRoots);
        assertThat(genfileRoots).hasSize(2);
        assertThat(genfileRoots.get(0)).contains("arm64");
        assertThat(genfileRoots.get(1)).contains("armv7");
    }

    @Test
    public void testDifferingProtoDepsPerArchitecture() throws Exception {
        scratch.file("examples/BUILD", "package(default_visibility = ['//visibility:public'])", "apple_binary(", "    name = 'bin',", "    deps = [':objc_protos'],", "    platform_type = 'ios',", ")", "objc_proto_library(", "    name = 'objc_protos',", "    portable_proto_filters = ['filter.pbascii'],", "    deps = [':protos'],", ")", "proto_library(", "    name = 'protos',", "    srcs = select({", "        ':armv7': [ 'one.proto', ],", "        '//conditions:default': [ 'two.proto', ],", "    }),", ")", "config_setting(", "    name = 'armv7',", "    values = {'apple_split_cpu': 'armv7'},", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        Action lipoAction = actionProducingArtifact("//examples:bin", "_lipobin");
        Artifact armv7Binary = getSingleArchBinary(lipoAction, "armv7");
        Artifact arm64Binary = getSingleArchBinary(lipoAction, "arm64");
        Artifact armv7ProtoLib = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(armv7Binary).getInputs(), "BundledProtos.a");
        Artifact armv7ProtoObject = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(armv7ProtoLib).getInputs(), "One.pbobjc.o");
        Artifact armv7ProtoObjcSource = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(armv7ProtoObject).getInputs(), "One.pbobjc.m");
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(armv7ProtoObjcSource).getInputs(), "one.proto")).isNotNull();
        Artifact arm64ProtoLib = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(arm64Binary).getInputs(), "BundledProtos.a");
        Artifact arm64ProtoObject = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(arm64ProtoLib).getInputs(), "Two.pbobjc.o");
        Artifact arm64ProtoObjcSource = ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(arm64ProtoObject).getInputs(), "Two.pbobjc.m");
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(getGeneratingAction(arm64ProtoObjcSource).getInputs(), "two.proto")).isNotNull();
    }

    @Test
    public void testPlatformTypeIsConfigurable() throws Exception {
        scratch.file("examples/BUILD", "package(default_visibility = ['//visibility:public'])", "apple_binary(", "    name = 'bin',", "    deps = [':objc_lib'],", "    platform_type = select({", "        ':watch_setting': 'watchos',", "        '//conditions:default': 'ios',", "    }),", ")", "objc_library(", "    name = 'objc_lib',", "    srcs = ['a.m'],", ")", "config_setting(", "    name = 'watch_setting',", "    values = {'define': 'use_watch=1'},", ")");
        useConfiguration("--define=use_watch=1", "--ios_multi_cpus=armv7,arm64", "--watchos_cpus=armv7k");
        Action lipoAction = actionProducingArtifact("//examples:bin", "_lipobin");
        assertThat(getSingleArchBinary(lipoAction, "armv7k")).isNotNull();
    }

    @Test
    public void testAppleDebugSymbolProviderWithDsymsExposedToSkylark() throws Exception {
        useConfiguration("--apple_bitcode=embedded", "--apple_generate_dsym", "--ios_multi_cpus=armv7,arm64,x86_64");
        checkAppleDebugSymbolProvider_DsymEntries(generateAppleDebugOutputsSkylarkProviderMap(), FASTBUILD);
    }

    @Test
    public void testAppleDebugSymbolProviderWithAutoDsymDbgAndDsymsExposedToSkylark() throws Exception {
        useConfiguration("--apple_bitcode=embedded", "--compilation_mode=dbg", "--apple_enable_auto_dsym_dbg", "--ios_multi_cpus=armv7,arm64,x86_64");
        checkAppleDebugSymbolProvider_DsymEntries(generateAppleDebugOutputsSkylarkProviderMap(), DBG);
    }

    @Test
    public void testAppleDebugSymbolProviderWithLinkMapsExposedToSkylark() throws Exception {
        useConfiguration("--apple_bitcode=embedded", "--objc_generate_linkmap", "--ios_multi_cpus=armv7,arm64,x86_64");
        checkAppleDebugSymbolProvider_LinkMapEntries(generateAppleDebugOutputsSkylarkProviderMap());
    }

    @Test
    public void testAppleDebugSymbolProviderWithDsymsAndLinkMapsExposedToSkylark() throws Exception {
        useConfiguration("--apple_bitcode=embedded", "--objc_generate_linkmap", "--apple_generate_dsym", "--ios_multi_cpus=armv7,arm64,x86_64");
        SkylarkDict<String, SkylarkDict<String, Artifact>> outputMap = generateAppleDebugOutputsSkylarkProviderMap();
        checkAppleDebugSymbolProvider_DsymEntries(outputMap, FASTBUILD);
        checkAppleDebugSymbolProvider_LinkMapEntries(outputMap);
    }

    @Test
    public void testInstrumentedFilesProviderContainsDepsAndBundleLoaderFiles() throws Exception {
        useConfiguration("--collect_code_coverage");
        scratch.file("examples/BUILD", "package(default_visibility = ['//visibility:public'])", "apple_binary(", "    name = 'bin',", "    deps = [':lib'],", "    platform_type = 'ios',", ")", "apple_binary(", "    name = 'bundle',", "    deps = [':bundle_lib'],", (("    binary_type = '" + (BinaryType.LOADABLE_BUNDLE)) + "',"), "    bundle_loader = ':bin',", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['lib.m'],", ")", "objc_library(", "    name = 'bundle_lib',", "    srcs = ['bundle_lib.m'],", ")");
        ConfiguredTarget bundleTarget = getConfiguredTarget("//examples:bundle");
        InstrumentedFilesInfo instrumentedFilesProvider = bundleTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(instrumentedFilesProvider).isNotNull();
        assertThat(Artifact.toRootRelativePaths(instrumentedFilesProvider.getInstrumentedFiles())).containsAllOf("examples/lib.m", "examples/bundle_lib.m");
    }

    @Test
    public void testAppleSdkWatchsimulatorPlatformEnv() throws Exception {
        checkAppleSdkWatchsimulatorPlatformEnv(getRuleType());
    }

    @Test
    public void testAppleSdkWatchosPlatformEnv() throws Exception {
        checkAppleSdkWatchosPlatformEnv(getRuleType());
    }

    @Test
    public void testAppleSdkTvsimulatorPlatformEnv() throws Exception {
        checkAppleSdkTvsimulatorPlatformEnv(getRuleType());
    }

    @Test
    public void testAppleSdkTvosPlatformEnv() throws Exception {
        checkAppleSdkTvosPlatformEnv(getRuleType());
    }

    @Test
    public void testLinkActionHasCorrectWatchosSimulatorMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=i386", "--watchos_sdk_version=3.0", "--watchos_minimum_os=2.0");
        checkLinkMinimumOSVersion("-mwatchos-simulator-version-min=2.0");
    }

    @Test
    public void testLinkActionHasCorrectWatchosMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=armv7k", "--watchos_sdk_version=3.0", "--watchos_minimum_os=2.0");
        checkLinkMinimumOSVersion("-mwatchos-version-min=2.0");
    }

    @Test
    public void testLinkActionHasCorrectTvosSimulatorMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'tvos'");
        useConfiguration("--tvos_cpus=x86_64", "--tvos_sdk_version=10.1", "--tvos_minimum_os=10.0");
        checkLinkMinimumOSVersion("-mtvos-simulator-version-min=10.0");
    }

    @Test
    public void testLinkActionHasCorrectTvosMinVersion() throws Exception {
        getRuleType().scratchTarget(scratch, "platform_type", "'tvos'");
        useConfiguration("--tvos_cpus=arm64", "--tvos_sdk_version=10.1", "--tvos_minimum_os=10.0");
        checkLinkMinimumOSVersion("-mtvos-version-min=10.0");
    }

    @Test
    public void testWatchSimulatorLinkAction() throws Exception {
        checkWatchSimulatorLinkAction(getRuleType());
    }

    @Test
    public void testProtoBundlingAndLinking() throws Exception {
        checkProtoBundlingAndLinking(getRuleType());
    }

    @Test
    public void testAvoidDepsObjects() throws Exception {
        checkAvoidDepsObjects(getRuleType());
    }

    @Test
    public void testBundleLoaderPropagatesAppleExecutableBinaryProvider() throws Exception {
        scratch.file("bin/BUILD", "apple_binary(", "    name = 'bin',", "    deps = [':lib'],", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        scratch.file("test/BUILD", "apple_binary(", "    name = 'test',", "    deps = [':lib'],", "    binary_type = 'loadable_bundle',", "    bundle_loader = '//bin:bin',", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        ConfiguredTarget binTarget = getConfiguredTarget("//bin:bin");
        AppleExecutableBinaryInfo executableBinaryProvider = binTarget.get(AppleExecutableBinaryInfo.SKYLARK_CONSTRUCTOR);
        assertThat(executableBinaryProvider).isNotNull();
        CommandAction testLinkAction = linkAction("//test:test");
        assertThat(testLinkAction.getInputs()).contains(executableBinaryProvider.getAppleExecutableBinary());
    }

    @Test
    public void testLoadableBundleBinaryAddsRpathLinkOptWithNoBundleLoader() throws Exception {
        scratch.file("test/BUILD", "apple_binary(", "    name = 'test',", "    deps = [':lib'],", "    binary_type = 'loadable_bundle',", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        CommandAction testLinkAction = linkAction("//test:test");
        assertThat(Joiner.on(" ").join(testLinkAction.getArguments())).contains("@loader_path/Frameworks");
    }

    @Test
    public void testLoadableBundleBinaryAddsRpathLinkOptWithBundleLoader() throws Exception {
        scratch.file("bin/BUILD", "apple_binary(", "    name = 'bin',", "    deps = [':lib'],", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        scratch.file("test/BUILD", "apple_binary(", "    name = 'test',", "    deps = [':lib'],", "    binary_type = 'loadable_bundle',", "    bundle_loader = '//bin:bin',", "    platform_type = 'ios',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        CommandAction testLinkAction = linkAction("//test:test");
        assertThat(Joiner.on(" ").join(testLinkAction.getArguments())).contains("@loader_path/Frameworks");
    }

    @Test
    public void testCustomModuleMap() throws Exception {
        checkCustomModuleMapNotPropagatedByTargetUnderTest(getRuleType());
    }

    @Test
    public void testMinimumOsDifferentTargets() throws Exception {
        checkMinimumOsDifferentTargets(getRuleType(), "_lipobin", "_bin");
    }

    @Test
    public void testMacosFrameworkDirectories() throws Exception {
        scratch.file("test/BUILD", "apple_binary(", "    name = 'test',", "    deps = [':lib'],", "    platform_type = 'macos',", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", ")");
        CommandAction linkAction = linkAction("//test:test");
        ImmutableList<String> expectedCommandLineFragments = ImmutableList.<String>builder().add(((AppleToolchain.sdkDir()) + (AppleToolchain.SYSTEM_FRAMEWORK_PATH))).add(ObjcRuleTestCase.frameworkDir(ApplePlatform.forTarget(MACOS, "x86_64"))).build();
        String linkArgs = Joiner.on(" ").join(linkAction.getArguments());
        for (String expectedCommandLineFragment : expectedCommandLineFragments) {
            assertThat(linkArgs).contains(expectedCommandLineFragment);
        }
    }

    @Test
    public void testDrops32BitArchitecture() throws Exception {
        verifyDrops32BitArchitecture(getRuleType());
    }

    @Test
    public void testFeatureFlags_offByDefault() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratchFeatureFlagTestLib();
        scratch.file("test/BUILD", "apple_binary(", "    name = 'bin',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkAction("//test:bin");
        CommandAction objcLibArchiveAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        CommandAction flag1offCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag1off.o"))));
        CommandAction flag2offCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag2off.o"))));
        String compileArgs1 = Joiner.on(" ").join(flag1offCompileAction.getArguments());
        String compileArgs2 = Joiner.on(" ").join(flag2offCompileAction.getArguments());
        assertThat(compileArgs1).contains("FLAG_1_OFF");
        assertThat(compileArgs1).contains("FLAG_2_OFF");
        assertThat(compileArgs2).contains("FLAG_1_OFF");
        assertThat(compileArgs2).contains("FLAG_2_OFF");
    }

    @Test
    public void testFeatureFlags_oneFlagOn() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratchFeatureFlagTestLib();
        scratch.file("test/BUILD", "apple_binary(", "    name = 'bin',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    feature_flags = {", "      '//lib:flag2': 'on',", "    },", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkAction("//test:bin");
        CommandAction objcLibArchiveAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        CommandAction flag1offCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag1off.o"))));
        CommandAction flag2onCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag2on.o"))));
        String compileArgs1 = Joiner.on(" ").join(flag1offCompileAction.getArguments());
        String compileArgs2 = Joiner.on(" ").join(flag2onCompileAction.getArguments());
        assertThat(compileArgs1).contains("FLAG_1_OFF");
        assertThat(compileArgs1).contains("FLAG_2_ON");
        assertThat(compileArgs2).contains("FLAG_1_OFF");
        assertThat(compileArgs2).contains("FLAG_2_ON");
    }

    @Test
    public void testFeatureFlags_allFlagsOn() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratchFeatureFlagTestLib();
        scratch.file("test/BUILD", "apple_binary(", "    name = 'bin',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    feature_flags = {", "      '//lib:flag1': 'on',", "      '//lib:flag2': 'on',", "    },", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkAction("//test:bin");
        CommandAction objcLibArchiveAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        CommandAction flag1onCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag1on.o"))));
        CommandAction flag2onCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "flag2on.o"))));
        String compileArgs1 = Joiner.on(" ").join(flag1onCompileAction.getArguments());
        String compileArgs2 = Joiner.on(" ").join(flag2onCompileAction.getArguments());
        assertThat(compileArgs1).contains("FLAG_1_ON");
        assertThat(compileArgs1).contains("FLAG_2_ON");
        assertThat(compileArgs2).contains("FLAG_1_ON");
        assertThat(compileArgs2).contains("FLAG_2_ON");
    }

    @Test
    public void testLoadableBundleObjcProvider() throws Exception {
        scratch.file("testlib/BUILD", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    sdk_frameworks = ['TestFramework'],", ")");
        getRuleType().scratchTarget(scratch, "binary_type", "'loadable_bundle'", "deps", "['//testlib:lib']");
        ObjcProvider objcProvider = providerForTarget("//x:x");
        assertThat(objcProvider.sdkFramework().toCollection()).contains("TestFramework");
    }
}

