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


import AppleStaticLibraryInfo.SKYLARK_CONSTRUCTOR;
import ConfigurationDistinguisher.APPLEBIN_IOS;
import ConfigurationDistinguisher.APPLEBIN_WATCHOS;
import MultiArchSplitTransitionProvider.UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT;
import ObjcProvider.MULTI_ARCH_LINKED_ARCHIVES;
import ObjcProvider.SDK_FRAMEWORK;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.CommandAction;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.actions.SymlinkAction;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.Scratch;
import java.io.IOException;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for apple_static_library.
 */
@RunWith(JUnit4.class)
public class AppleStaticLibraryTest extends ObjcRuleTestCase {
    static final RuleType RULE_TYPE = new RuleType("apple_static_library") {
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
            return attributes.build();
        }
    };

    @Test
    public void testMandatoryMinimumOsVersionUnset() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--experimental_apple_mandatory_minimum_version");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//x:x");
        assertContainsEvent("must be explicitly specified");
    }

    @Test
    public void testMandatoryMinimumOsVersionSet() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "minimum_os_version", "'8.0'", "platform_type", "'watchos'");
        useConfiguration("--experimental_apple_mandatory_minimum_version");
        getConfiguredTarget("//x:x");
    }

    @Test
    public void testUnknownPlatformType() throws Exception {
        checkError("package", "test", String.format(UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT, "meow_meow_os"), "apple_static_library(name = 'test', platform_type = 'meow_meow_os')");
    }

    @Test
    public void testSymlinkInsteadOfLipoSingleArch() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch);
        SymlinkAction action = ((SymlinkAction) (lipoLibAction("//x:x")));
        CommandAction linkAction = linkLibAction("//x:x");
        assertThat(action.getInputs()).containsExactly(Iterables.getOnlyElement(linkAction.getOutputs()));
    }

    @Test
    public void testAvoidDepsProviders() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    platform_type = 'ios',", "    avoid_deps = [':avoidLib'],", ")", "objc_library(name = 'objcLib', srcs = [ 'b.m' ], deps = [':avoidLib', ':baseLib'])", "objc_library(", "    name = 'baseLib',", "    srcs = [ 'base.m' ],", "    sdk_frameworks = ['BaseSDK'],", "    resources = [':base.png']", ")", "objc_library(", "    name = 'avoidLib',", "    srcs = [ 'c.m' ],", "    sdk_frameworks = ['AvoidSDK'],", "    resources = [':avoid.png']", ")");
        ObjcProvider provider = getConfiguredTarget("//package:test").get(SKYLARK_CONSTRUCTOR).getDepsObjcProvider();
        // Do not remove SDK_FRAMEWORK values in avoid_deps.
        assertThat(provider.get(SDK_FRAMEWORK)).containsAllOf(new SdkFramework("AvoidSDK"), new SdkFramework("BaseSDK"));
    }

    @Test
    public void testNoSrcs() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    platform_type = 'ios',", ")", "objc_library(name = 'objcLib', srcs = [ 'b.m' ])");
        useConfiguration("--xcode_version=5.8");
        CommandAction action = linkLibAction("//package:test");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).contains("package/libobjcLib.a");
    }

    @Test
    public void testLipoAction() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--ios_multi_cpus=i386,x86_64");
        CommandAction action = ((CommandAction) (lipoLibAction("//x:x")));
        String i386Lib = (configurationBin("i386", APPLEBIN_IOS)) + "x/x-fl.a";
        String x8664Lib = (configurationBin("x86_64", APPLEBIN_IOS)) + "x/x-fl.a";
        assertThat(Artifact.toExecPaths(action.getInputs())).containsExactly(i386Lib, x8664Lib, ObjcRuleTestCase.MOCK_XCRUNWRAPPER_PATH, ObjcRuleTestCase.MOCK_XCRUNWRAPPER_EXECUTABLE_PATH);
        assertThat(action.getArguments()).containsExactly(ObjcRuleTestCase.MOCK_XCRUNWRAPPER_EXECUTABLE_PATH, ObjcRuleClasses.LIPO, "-create", i386Lib, x8664Lib, "-o", execPathEndingWith(action.getOutputs(), "x_lipo.a")).inOrder();
        assertThat(Artifact.toRootRelativePaths(action.getOutputs())).containsExactly("x/x_lipo.a");
        assertRequiresDarwin(action);
    }

    @Test
    public void testWatchSimulatorDepCompile() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    platform_type = 'watchos'", ")", "objc_library(name = 'objcLib', srcs = [ 'a.m' ])");
        Action lipoAction = lipoLibAction("//package:test");
        String i386Bin = (configurationBin("i386", APPLEBIN_WATCHOS)) + "package/test-fl.a";
        Artifact libArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), i386Bin);
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(libArtifact)));
        CommandAction objcLibCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        assertAppleSdkPlatformEnv(objcLibCompileAction, "WatchSimulator");
        assertThat(objcLibCompileAction.getArguments()).containsAllOf("-arch_only", "i386").inOrder();
    }

    @Test
    public void testMultiarchCcDep() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(name = 'test',", "    deps = [ ':cclib' ],", "    platform_type = 'ios')", "cc_library(name = 'cclib', srcs = ['dep.c'])");
        useConfiguration("--ios_multi_cpus=i386,x86_64", "--crosstool_top=//tools/osx/crosstool:crosstool");
        CommandAction action = ((CommandAction) (lipoLibAction("//package:test")));
        String i386Prefix = configurationBin("i386", APPLEBIN_IOS);
        String x8664Prefix = configurationBin("x86_64", APPLEBIN_IOS);
        CommandAction i386BinAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(action.getInputs(), (i386Prefix + "package/test-fl.a")))));
        CommandAction x8664BinAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(action.getInputs(), (x8664Prefix + "package/test-fl.a")))));
        assertThat(Artifact.toExecPaths(i386BinAction.getInputs())).contains((i386Prefix + "package/libcclib.a"));
        assertThat(Artifact.toExecPaths(x8664BinAction.getInputs())).contains((x8664Prefix + "package/libcclib.a"));
    }

    @Test
    public void testWatchSimulatorLipoAction() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'watchos'");
        // Tests that ios_multi_cpus and ios_cpu are completely ignored.
        useConfiguration("--ios_multi_cpus=x86_64", "--ios_cpu=x86_64", "--watchos_cpus=i386,armv7k");
        CommandAction action = ((CommandAction) (lipoLibAction("//x:x")));
        String i386Bin = (configurationBin("i386", APPLEBIN_WATCHOS)) + "x/x-fl.a";
        String armv7kBin = (configurationBin("armv7k", APPLEBIN_WATCHOS)) + "x/x-fl.a";
        assertThat(Artifact.toExecPaths(action.getInputs())).containsExactly(i386Bin, armv7kBin, ObjcRuleTestCase.MOCK_XCRUNWRAPPER_PATH, ObjcRuleTestCase.MOCK_XCRUNWRAPPER_EXECUTABLE_PATH);
        assertContainsSublist(action.getArguments(), ImmutableList.of(ObjcRuleTestCase.MOCK_XCRUNWRAPPER_EXECUTABLE_PATH, ObjcRuleClasses.LIPO, "-create"));
        assertThat(action.getArguments()).containsAllOf(armv7kBin, i386Bin);
        assertContainsSublist(action.getArguments(), ImmutableList.of("-o", execPathEndingWith(action.getOutputs(), "x_lipo.a")));
        assertThat(Artifact.toRootRelativePaths(action.getOutputs())).containsExactly("x/x_lipo.a");
        assertAppleSdkPlatformEnv(action, "WatchOS");
        assertRequiresDarwin(action);
    }

    @Test
    public void testProtoDeps() throws Exception {
        scratch.file("protos/BUILD", "proto_library(", "    name = 'protos_main',", "    srcs = ['data_a.proto', 'data_b.proto'],", ")", "proto_library(", "    name = 'protos_low_level',", "    srcs = ['data_b.proto'],", ")", "objc_proto_library(", "    name = 'objc_protos_main',", "    portable_proto_filters = ['filter_a.pbascii'],", "    deps = [':protos_main'],", ")", "objc_proto_library(", "    name = 'objc_protos_low_level',", "    portable_proto_filters = ['filter_b.pbascii'],", "    deps = [':protos_low_level'],", ")");
        scratch.file("libs/BUILD", "objc_library(", "    name = 'main_lib',", "    srcs = ['a.m'],", "    deps = ['//protos:objc_protos_main',]", ")", "objc_library(", "    name = 'apple_low_level_lib',", "    srcs = ['a.m'],", "    deps = ['//protos:objc_protos_low_level',]", ")");
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "deps", "['//libs:main_lib']", "avoid_deps", "['//libs:apple_low_level_lib']");
        CommandAction linkAction = linkLibAction("//x:x");
        Iterable<Artifact> linkActionInputs = linkAction.getInputs();
        ImmutableList.Builder<Artifact> objects = ImmutableList.builder();
        for (Artifact binActionArtifact : linkActionInputs) {
            if (binActionArtifact.getRootRelativePath().getPathString().endsWith(".a")) {
                CommandAction subLinkAction = ((CommandAction) (getGeneratingAction(binActionArtifact)));
                for (Artifact linkActionArtifact : subLinkAction.getInputs()) {
                    if (linkActionArtifact.getRootRelativePath().getPathString().endsWith(".o")) {
                        objects.add(linkActionArtifact);
                    }
                }
            }
        }
        ImmutableList<Artifact> objectFiles = objects.build();
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(objectFiles, "DataA.pbobjc.o")).isNotNull();
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(objectFiles, "DataB.pbobjc.o")).isNull();
    }

    @Test
    public void testMinimumOs() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "deps", "['//package:objcLib']", "minimum_os_version", "'5.4'");
        scratch.file("package/BUILD", "objc_library(name = 'objcLib', srcs = [ 'b.m' ])");
        useConfiguration("--xcode_version=5.8");
        CommandAction linkAction = linkLibAction("//x:x");
        CommandAction objcLibArchiveAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        CommandAction objcLibCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "b.o"))));
        String compileArgs = Joiner.on(" ").join(objcLibCompileAction.getArguments());
        assertThat(compileArgs).contains("-mios-simulator-version-min=5.4");
    }

    @Test
    public void testMinimumOs_watchos() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "deps", "['//package:objcLib']", "platform_type", "'watchos'", "minimum_os_version", "'5.4'");
        scratch.file("package/BUILD", "objc_library(name = 'objcLib', srcs = [ 'b.m' ])");
        useConfiguration("--xcode_version=5.8");
        CommandAction linkAction = linkLibAction("//x:x");
        CommandAction objcLibArchiveAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(linkAction.getInputs(), "libobjcLib.a"))));
        CommandAction objcLibCompileAction = ((CommandAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(objcLibArchiveAction.getInputs(), "b.o"))));
        String compileArgs = Joiner.on(" ").join(objcLibCompileAction.getArguments());
        assertThat(compileArgs).contains("-mwatchos-simulator-version-min=5.4");
    }

    @Test
    public void testMinimumOs_invalid_nonVersion() throws Exception {
        checkMinimumOs_invalid_nonVersion(AppleStaticLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMinimumOs_invalid_containsAlphabetic() throws Exception {
        checkMinimumOs_invalid_containsAlphabetic(AppleStaticLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMinimumOs_invalid_tooManyComponents() throws Exception {
        checkMinimumOs_invalid_tooManyComponents(AppleStaticLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkVersionEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        CommandAction action = linkLibAction("//x:x");
        assertAppleSdkVersionEnv(action);
    }

    @Test
    public void testNonDefaultAppleSdkVersionEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--ios_sdk_version=8.1");
        CommandAction action = linkLibAction("//x:x");
        assertAppleSdkVersionEnv(action, "8.1");
    }

    @Test
    public void testAppleSdkDefaultPlatformEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        CommandAction action = linkLibAction("//x:x");
        assertAppleSdkPlatformEnv(action, "iPhoneSimulator");
    }

    @Test
    public void testAppleSdkIphoneosPlatformEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--apple_platform_type=ios", "--cpu=ios_arm64");
        CommandAction action = linkLibAction("//x:x");
        assertAppleSdkPlatformEnv(action, "iPhoneOS");
    }

    @Test
    public void testAppleSdkWatchsimulatorPlatformEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=i386");
        Action lipoAction = lipoLibAction("//x:x");
        String i386Lib = (configurationBin("i386", APPLEBIN_WATCHOS)) + "x/x-fl.a";
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), i386Lib);
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertAppleSdkPlatformEnv(linkAction, "WatchSimulator");
    }

    @Test
    public void testAppleSdkWatchosPlatformEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'watchos'");
        useConfiguration("--watchos_cpus=armv7k");
        Action lipoAction = lipoLibAction("//x:x");
        String armv7kLib = (configurationBin("armv7k", APPLEBIN_WATCHOS)) + "x/x-fl.a";
        Artifact libArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), armv7kLib);
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(libArtifact)));
        assertAppleSdkPlatformEnv(linkAction, "WatchOS");
    }

    @Test
    public void testXcodeVersionEnv() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        useConfiguration("--xcode_version=5.8");
        CommandAction action = linkLibAction("//x:x");
        assertXcodeVersionEnv(action, "5.8");
    }

    @Test
    public void testWatchSimulatorLinkAction() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    platform_type = 'watchos'", ")", "objc_library(name = 'objcLib', srcs = [ 'b.m' ])");
        // Tests that ios_multi_cpus and ios_cpu are completely ignored.
        useConfiguration("--ios_multi_cpus=x86_64", "--ios_cpu=x86_64", "--watchos_cpus=i386");
        Action lipoAction = lipoLibAction("//package:test");
        String i386Bin = (configurationBin("i386", APPLEBIN_WATCHOS)) + "package/test-fl.a";
        Artifact binArtifact = ActionsTestUtil.getFirstArtifactEndingWith(lipoAction.getInputs(), i386Bin);
        CommandAction linkAction = ((CommandAction) (getGeneratingAction(binArtifact)));
        assertAppleSdkPlatformEnv(linkAction, "WatchSimulator");
        assertThat(normalizeBashArgs(linkAction.getArguments())).containsAllOf("-arch_only", "i386").inOrder();
    }

    @Test
    public void testAppleStaticLibraryInfo() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        ConfiguredTarget binTarget = getConfiguredTarget("//x:x");
        AppleStaticLibraryInfo provider = binTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(provider).isNotNull();
        assertThat(provider.getMultiArchArchive()).isNotNull();
        assertThat(provider.getDepsObjcProvider()).isNotNull();
        assertThat(provider.getMultiArchArchive()).isEqualTo(Iterables.getOnlyElement(provider.getDepsObjcProvider().get(MULTI_ARCH_LINKED_ARCHIVES)));
    }

    @Test
    public void testMinimumOsDifferentTargets() throws Exception {
        checkMinimumOsDifferentTargets(AppleStaticLibraryTest.RULE_TYPE, "_lipo.a", "-fl.a");
    }

    @Test
    public void testAvoidDepsObjects() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    avoid_deps = [':avoidLib'],", "    platform_type = 'ios',", ")", "objc_library(name = 'objcLib', srcs = [ 'b.m' ], deps = [':avoidLib', ':baseLib'])", "objc_library(name = 'baseLib', srcs = [ 'base.m' ])", "objc_library(name = 'avoidLib', srcs = [ 'c.m' ])");
        CommandAction action = linkLibAction("//package:test");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).containsAllOf("package/libobjcLib.a", "package/libbaseLib.a");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).doesNotContain("package/libavoidLib.a");
    }

    // Tests that if there is a cc_library in avoid_deps, all of its dependencies are
    // transitively avoided, even if it is not present in deps.
    @Test
    public void testAvoidDepsObjects_avoidViaCcLibrary() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib'],", "    avoid_deps = [':avoidCclib'],", "    platform_type = 'ios',", ")", "cc_library(name = 'avoidCclib', srcs = ['cclib.c'], deps = [':avoidLib'])", "objc_library(name = 'objcLib', srcs = [ 'b.m' ], deps = [':avoidLib'])", "objc_library(name = 'avoidLib', srcs = [ 'c.m' ])");
        CommandAction action = linkLibAction("//package:test");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).contains("package/libobjcLib.a");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).doesNotContain("package/libavoidCcLib.a");
    }

    // Tests that if there is a cc_library in avoid_deps, and it is present in deps, it will
    // be avoided, as well as its transitive dependencies.
    @Test
    public void testAvoidDepsObjects_avoidCcLibrary() throws Exception {
        scratch.file("package/BUILD", "apple_static_library(", "    name = 'test',", "    deps = [':objcLib', ':avoidCclib'],", "    avoid_deps = [':avoidCclib'],", "    platform_type = 'ios',", ")", "cc_library(name = 'avoidCclib', srcs = ['cclib.c'], deps = [':avoidLib'])", "objc_library(name = 'objcLib', srcs = [ 'b.m' ])", "objc_library(name = 'avoidLib', srcs = [ 'c.m' ])");
        CommandAction action = linkLibAction("//package:test");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).contains("package/libobjcLib.a");
        assertThat(Artifact.toRootRelativePaths(action.getInputs())).doesNotContain("package/libavoidCcLib.a");
    }

    @Test
    public void testFeatureFlags_offByDefault() throws Exception {
        useConfiguration("--enforce_transitive_configs_for_config_feature_flag");
        scratchFeatureFlagTestLib();
        scratch.file("test/BUILD", "apple_static_library(", "    name = 'static_lib',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkLibAction("//test:static_lib");
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
        scratch.file("test/BUILD", "apple_static_library(", "    name = 'static_lib',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    feature_flags = {", "      '//lib:flag2': 'on',", "    },", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkLibAction("//test:static_lib");
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
        scratch.file("test/BUILD", "apple_static_library(", "    name = 'static_lib',", "    deps = ['//lib:objcLib'],", "    platform_type = 'ios',", "    feature_flags = {", "      '//lib:flag1': 'on',", "      '//lib:flag2': 'on',", "    },", "    transitive_configs = ['//lib:flag1', '//lib:flag2'],", ")");
        CommandAction linkAction = linkLibAction("//test:static_lib");
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
    public void testAppleStaticLibraryProvider() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def skylark_static_lib_impl(ctx):", "   dep_provider = ctx.attr.proxy[apple_common.AppleStaticLibrary]", "   my_provider = apple_common.AppleStaticLibrary(archive = dep_provider.archive,", "       objc = dep_provider.objc)", "   return struct(", "      providers = [my_provider]", "   )", "", "skylark_static_lib = rule(", "  implementation = skylark_static_lib_impl,", "  attrs = {'proxy': attr.label()},", ")");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'skylark_static_lib')", "skylark_static_lib(", "   name='my_target',", "   proxy='//x:x'", ")");
        ConfiguredTarget binTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        AppleStaticLibraryInfo provider = binTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(provider).isNotNull();
        assertThat(provider.getMultiArchArchive()).isNotNull();
        assertThat(provider.getDepsObjcProvider()).isNotNull();
        assertThat(provider.getMultiArchArchive()).isEqualTo(Iterables.getOnlyElement(provider.getDepsObjcProvider().get(MULTI_ARCH_LINKED_ARCHIVES)));
    }

    @Test
    public void testAppleStaticLibraryProvider_invalidArgs() throws Exception {
        AppleStaticLibraryTest.RULE_TYPE.scratchTarget(scratch, "platform_type", "'ios'");
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def skylark_static_lib_impl(ctx):", "   dep_provider = ctx.attr.proxy[apple_common.AppleStaticLibrary]", "   my_provider = apple_common.AppleStaticLibrary(archive = dep_provider.archive,", "       objc = dep_provider.objc, foo = 'bar')", "   return struct(", "      providers = [my_provider]", "   )", "", "skylark_static_lib = rule(", "  implementation = skylark_static_lib_impl,", "  attrs = {'proxy': attr.label()},", ")");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'skylark_static_lib')", "skylark_static_lib(", "   name='my_target',", "   proxy='//x:x'", ")");
        AssertionError expected = MoreAsserts.assertThrows(AssertionError.class, () -> getConfiguredTarget("//examples/apple_skylark:my_target"));
        assertThat(expected).hasMessageThat().contains("unexpected keyword 'foo', for call to function AppleStaticLibrary");
    }
}

