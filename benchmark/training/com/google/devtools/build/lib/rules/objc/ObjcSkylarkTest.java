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


import AppleExecutableBinaryInfo.SKYLARK_CONSTRUCTOR;
import AppleSkylarkCommon.BAD_KEY_ERROR;
import AppleSkylarkCommon.BAD_PROVIDERS_ELEM_ERROR;
import AppleSkylarkCommon.BAD_PROVIDERS_ITER_ERROR;
import AppleSkylarkCommon.BAD_SET_TYPE_ERROR;
import AppleSkylarkCommon.NOT_SET_ERROR;
import ObjcConfiguration.OPT_COPTS;
import ObjcProvider.DEFINE;
import ObjcProvider.Flag.USES_SWIFT;
import ObjcProvider.INCLUDE;
import ObjcProvider.LIBRARY;
import ObjcProvider.LINKOPT;
import ObjcProvider.LINK_INPUTS;
import ObjcProvider.XIB;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.rules.apple.AppleToolchain;
import com.google.devtools.build.lib.rules.apple.DottedVersion;
import com.google.devtools.build.lib.syntax.SkylarkDict;
import com.google.devtools.build.lib.syntax.SkylarkNestedSet;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Skylark interaction with the objc_* rules.
 */
@RunWith(JUnit4.class)
public class ObjcSkylarkTest extends ObjcRuleTestCase {
    @Test
    public void testSkylarkRuleCanDependOnNativeAppleRule() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   return struct(", "      found_libs = dep.objc.library,", "      found_hdrs = dep.objc.header,", "    )", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False, providers = ['objc']),", "})");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "    name = 'my_target',", "    deps = [':lib'],", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    hdrs = ['b.h']", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        SkylarkNestedSet skylarkLibraries = ((SkylarkNestedSet) (skylarkTarget.get("found_libs")));
        SkylarkNestedSet skylarkHdrs = ((SkylarkNestedSet) (skylarkTarget.get("found_hdrs")));
        assertThat(ActionsTestUtil.baseArtifactNames(skylarkLibraries.getSet(Artifact.class))).contains("liblib.a");
        assertThat(ActionsTestUtil.baseArtifactNames(skylarkHdrs.getSet(Artifact.class))).contains("b.h");
    }

    @Test
    public void testSkylarkProviderRetrievalNoneIfNoProvider() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep.objc", "   return struct()", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False),", "})");
        scratch.file("examples/apple_skylark/a.cc");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "    name = 'my_target',", "    deps = [':lib'],", ")", "cc_library(", "    name = 'lib',", "    srcs = ['a.cc'],", "    hdrs = ['b.h']", ")");
        try {
            getConfiguredTarget("//examples/apple_skylark:my_target");
            Assert.fail("Should throw assertion error");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains("File \"/workspace/examples/apple_skylark/BUILD\", line 3");
            assertThat(e).hasMessageThat().contains("my_rule(name = 'my_target')");
            assertThat(e).hasMessageThat().contains("File \"/workspace/examples/rule/apple_rules.bzl\", line 3, in my_rule_impl");
            assertThat(e).hasMessageThat().contains("dep.objc");
            assertThat(e).hasMessageThat().contains(("<target //examples/apple_skylark:lib> (rule 'cc_library') " + "doesn't have provider 'objc'"));
        }
    }

    @Test
    public void testSkylarkProviderCanCheckForExistanceOfObjcProvider() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   cc_has_provider = hasattr(ctx.attr.deps[0], 'objc')", "   objc_has_provider = hasattr(ctx.attr.deps[1], 'objc')", "   return struct(cc_has_provider=cc_has_provider, objc_has_provider=objc_has_provider)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False),", "})");
        scratch.file("examples/apple_skylark/a.cc");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "    name = 'my_target',", "    deps = [':cc_lib', ':objc_lib'],", ")", "objc_library(", "    name = 'objc_lib',", "    srcs = ['a.m'],", ")", "cc_library(", "    name = 'cc_lib',", "    srcs = ['a.cc'],", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        boolean ccResult = ((boolean) (skylarkTarget.get("cc_has_provider")));
        boolean objcResult = ((boolean) (skylarkTarget.get("objc_has_provider")));
        assertThat(ccResult).isFalse();
        assertThat(objcResult).isTrue();
    }

    @Test
    public void testSkylarkExportsObjcProviderToNativeRule() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep.objc", "   return struct(objc=objc_provider)", "swift_library = rule(implementation = my_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False, providers = ['objc'])", "})");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_library')", "swift_library(", "   name='my_target',", "   deps=[':lib'],", ")", "objc_library(", "   name = 'lib',", "   srcs = ['a.m'],", "   defines = ['mock_define']", ")", "apple_binary(", "   name = 'bin',", "   platform_type = 'ios',", "   deps = [':my_target']", ")");
        ConfiguredTarget binaryTarget = getConfiguredTarget("//examples/apple_skylark:bin");
        AppleExecutableBinaryInfo executableProvider = binaryTarget.get(SKYLARK_CONSTRUCTOR);
        ObjcProvider objcProvider = executableProvider.getDepsObjcProvider();
        assertThat(Artifact.toRootRelativePaths(objcProvider.get(LIBRARY))).contains("examples/apple_skylark/liblib.a");
        assertThat(objcProvider.get(DEFINE)).contains("mock_define");
    }

    @Test
    public void testObjcRuleCanDependOnArbitrarySkylarkRuleThatProvidesObjc() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   objc_provider = apple_common.new_objc_provider(define=depset(['mock_define']))", "   return struct(objc=objc_provider)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {})");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target'", ")", "objc_library(", "   name = 'lib',", "   srcs = ['a.m'],", "   deps = [':my_target']", ")", "apple_binary(", "   name = 'bin',", "   platform_type = 'ios',", "   deps = [':lib']", ")");
        ConfiguredTarget binaryTarget = getConfiguredTarget("//examples/apple_skylark:bin");
        AppleExecutableBinaryInfo executableProvider = binaryTarget.get(SKYLARK_CONSTRUCTOR);
        ObjcProvider objcProvider = executableProvider.getDepsObjcProvider();
        assertThat(objcProvider.get(DEFINE)).contains("mock_define");
    }

    @Test
    public void testSkylarkCanAccessAppleConfiguration() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   xcode_config = ctx.attr._xcode_config[apple_common.XcodeVersionConfig]", "   cpu = ctx.fragments.apple.ios_cpu()", "   platform = ctx.fragments.apple.ios_cpu_platform()", "   xcode_config = ctx.attr._xcode_config[apple_common.XcodeVersionConfig]", "   dead_code_report = ctx.attr._dead_code_report", "   env = apple_common.target_apple_env(xcode_config, platform)", "   xcode_version = xcode_config.xcode_version()", "   sdk_version = xcode_config.sdk_version_for_platform(platform)", "   single_arch_platform = ctx.fragments.apple.single_arch_platform", "   single_arch_cpu = ctx.fragments.apple.single_arch_cpu", "   platform_type = single_arch_platform.platform_type", "   bitcode_mode = ctx.fragments.apple.bitcode_mode", "   return struct(", "      cpu=cpu,", "      env=env,", "      xcode_version=str(xcode_version),", "      sdk_version=str(sdk_version),", "      single_arch_platform=str(single_arch_platform),", "      single_arch_cpu=str(single_arch_cpu),", "      platform_type=str(platform_type),", "      bitcode_mode=str(bitcode_mode),", "      dead_code_report=str(dead_code_report),", "   )", "swift_binary = rule(", "    implementation = swift_binary_impl,", "    fragments = ['apple'],", "    attrs = {", "        '_xcode_config': attr.label(", "            default = configuration_field(", "                fragment = 'apple', name = 'xcode_config_label')),", "        '_dead_code_report': attr.label(", "            default = configuration_field(", "                fragment = 'j2objc', name = 'dead_code_report')),", "    },", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration("--apple_platform_type=ios", "--cpu=ios_i386", "--xcode_version=7.3");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Object iosCpu = skylarkTarget.get("cpu");
        @SuppressWarnings("unchecked")
        SkylarkDict<String, String> env = ((SkylarkDict<String, String>) (skylarkTarget.get("env")));
        Object sdkVersion = skylarkTarget.get("sdk_version");
        assertThat(iosCpu).isEqualTo("i386");
        assertThat(env).containsEntry("APPLE_SDK_PLATFORM", "iPhoneSimulator");
        assertThat(env).containsEntry("APPLE_SDK_VERSION_OVERRIDE", "8.4");
        assertThat(sdkVersion).isEqualTo("8.4");
        assertThat(skylarkTarget.get("xcode_version")).isEqualTo("7.3");
        assertThat(skylarkTarget.get("single_arch_platform")).isEqualTo("IOS_SIMULATOR");
        assertThat(skylarkTarget.get("single_arch_cpu")).isEqualTo("i386");
        assertThat(skylarkTarget.get("platform_type")).isEqualTo("ios");
        assertThat(skylarkTarget.get("bitcode_mode")).isEqualTo("none");
        assertThat(skylarkTarget.get("dead_code_report")).isEqualTo("None");
    }

    @Test
    public void testDefaultJ2objcDeadCodeReport() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   dead_code_report = ctx.attr._dead_code_report", "   return struct(", "      dead_code_report=str(dead_code_report),", "   )", "swift_binary = rule(", "    implementation = swift_binary_impl,", "    fragments = ['j2objc'],", "    attrs = {", "        '_dead_code_report': attr.label(", "            default = configuration_field(", "                fragment = 'j2objc', name = 'dead_code_report')),", "    },", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration();
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("dead_code_report")).isEqualTo("None");
    }

    @Test
    public void testCustomJ2objcDeadCodeReport() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def dead_code_report_impl(ctx):", "   return struct(foo='bar')", "def swift_binary_impl(ctx):", "   dead_code_report = ctx.attr._dead_code_report.foo", "   return struct(", "      dead_code_report=dead_code_report,", "   )", "dead_code_report = rule(", "    implementation = dead_code_report_impl,", ")", "swift_binary = rule(", "    implementation = swift_binary_impl,", "    fragments = ['j2objc'],", "    attrs = {", "        '_dead_code_report': attr.label(", "            default = configuration_field(", "                fragment = 'j2objc', name = 'dead_code_report')),", "    },", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'dead_code_report', 'swift_binary')", "swift_binary(", "   name='my_target',", ")", "dead_code_report(name='dead_code_report')");
        useConfiguration("--j2objc_dead_code_report=//examples/apple_skylark:dead_code_report");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("dead_code_report")).isEqualTo("bar");
    }

    @Test
    public void testSkylarkCanAccessJ2objcTranslationFlags() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   j2objc_flags = ctx.fragments.j2objc.translation_flags", "   return struct(", "      j2objc_flags=j2objc_flags,", "   )", "swift_binary = rule(", "    implementation = swift_binary_impl,", "    fragments = ['j2objc'],", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration("--j2objc_translation_flags=-DTestJ2ObjcFlag");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        @SuppressWarnings("unchecked")
        List<String> flags = ((List<String>) (skylarkTarget.get("j2objc_flags")));
        assertThat(flags).contains("-DTestJ2ObjcFlag");
        assertThat(flags).doesNotContain("-unspecifiedFlag");
    }

    @Test
    public void testSkylarkCanAccessApplePlatformNames() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   platform = ctx.fragments.apple.ios_cpu_platform()", "   return struct(", "      name=platform.name_in_plist,", "   )", "test_rule = rule(", "implementation = _test_rule_impl,", "fragments = ['apple']", ")");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "   name='my_target',", ")");
        useConfiguration("--cpu=ios_i386");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Object name = skylarkTarget.get("name");
        assertThat(name).isEqualTo("iPhoneSimulator");
    }

    @Test
    public void testSkylarkCanAccessAppleToolchain() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   apple_toolchain = apple_common.apple_toolchain()", "   sdk_dir = apple_toolchain.sdk_dir()", "   platform_developer_framework_dir = \\", "       apple_toolchain.platform_developer_framework_dir(ctx.fragments.apple)", "   return struct(", "      platform_developer_framework_dir=platform_developer_framework_dir,", "      sdk_dir=sdk_dir,", "   )", "swift_binary = rule(", "implementation = swift_binary_impl,", "fragments = ['apple']", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration("--apple_platform_type=ios", "--cpu=ios_i386");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        String platformDevFrameworksDir = ((String) (skylarkTarget.get("platform_developer_framework_dir")));
        String sdkDir = ((String) (skylarkTarget.get("sdk_dir")));
        assertThat(platformDevFrameworksDir).isEqualTo(((AppleToolchain.developerDir()) + "/Platforms/iPhoneSimulator.platform/Developer/Library/Frameworks"));
        assertThat(sdkDir).isEqualTo(AppleToolchain.sdkDir());
    }

    @Test
    public void testSkylarkCanAccessSdkAndMinimumOs() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   xcode_config = ctx.attr._xcode_config[apple_common.XcodeVersionConfig]", "   ios_sdk_version = xcode_config.sdk_version_for_platform\\", "(apple_common.platform.ios_device)", "   watchos_sdk_version = xcode_config.sdk_version_for_platform\\", "(apple_common.platform.watchos_device)", "   tvos_sdk_version = xcode_config.sdk_version_for_platform\\", "(apple_common.platform.tvos_device)", "   macos_sdk_version = xcode_config.sdk_version_for_platform\\", "(apple_common.platform.macos)", "   ios_minimum_os = xcode_config.minimum_os_for_platform_type\\", "(apple_common.platform_type.ios)", "   watchos_minimum_os = xcode_config.minimum_os_for_platform_type\\", "(apple_common.platform_type.watchos)", "   tvos_minimum_os = xcode_config.minimum_os_for_platform_type\\", "(apple_common.platform_type.tvos)", "   return struct(", "      ios_sdk_version=str(ios_sdk_version),", "      watchos_sdk_version=str(watchos_sdk_version),", "      tvos_sdk_version=str(tvos_sdk_version),", "      macos_sdk_version=str(macos_sdk_version),", "      ios_minimum_os=str(ios_minimum_os),", "      watchos_minimum_os=str(watchos_minimum_os),", "      tvos_minimum_os=str(tvos_minimum_os)", "   )", "swift_binary = rule(", "    implementation = swift_binary_impl,", "    fragments = ['apple'],", "    attrs = { '_xcode_config': ", "        attr.label(default=configuration_field(", "            fragment='apple', name='xcode_config_label')) },", ")");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration("--ios_sdk_version=1.1", "--ios_minimum_os=1.0", "--watchos_sdk_version=2.1", "--watchos_minimum_os=2.0", "--tvos_sdk_version=3.1", "--tvos_minimum_os=3.0", "--macos_sdk_version=4.1");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("ios_sdk_version")).isEqualTo("1.1");
        assertThat(skylarkTarget.get("ios_minimum_os")).isEqualTo("1.0");
        assertThat(skylarkTarget.get("watchos_sdk_version")).isEqualTo("2.1");
        assertThat(skylarkTarget.get("watchos_minimum_os")).isEqualTo("2.0");
        assertThat(skylarkTarget.get("tvos_sdk_version")).isEqualTo("3.1");
        assertThat(skylarkTarget.get("tvos_minimum_os")).isEqualTo("3.0");
        assertThat(skylarkTarget.get("macos_sdk_version")).isEqualTo("4.1");
        useConfiguration("--ios_sdk_version=1.1", "--watchos_sdk_version=2.1", "--tvos_sdk_version=3.1", "--macos_sdk_version=4.1");
        skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("ios_sdk_version")).isEqualTo("1.1");
        assertThat(skylarkTarget.get("ios_minimum_os")).isEqualTo("1.1");
        assertThat(skylarkTarget.get("watchos_sdk_version")).isEqualTo("2.1");
        assertThat(skylarkTarget.get("watchos_minimum_os")).isEqualTo("2.1");
        assertThat(skylarkTarget.get("tvos_sdk_version")).isEqualTo("3.1");
        assertThat(skylarkTarget.get("tvos_minimum_os")).isEqualTo("3.1");
        assertThat(skylarkTarget.get("macos_sdk_version")).isEqualTo("4.1");
    }

    @Test
    public void testSkylarkCanAccessObjcConfiguration() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/objc_rules.bzl", "def swift_binary_impl(ctx):", "   copts = ctx.fragments.objc.copts", "   compilation_mode_copts = ctx.fragments.objc.copts_for_current_compilation_mode", "   ios_simulator_device = ctx.fragments.objc.ios_simulator_device", "   ios_simulator_version = ctx.fragments.objc.ios_simulator_version", "   signing_certificate_name = ctx.fragments.objc.signing_certificate_name", "   generate_dsym = ctx.fragments.objc.generate_dsym", "   return struct(", "      copts=copts,", "      compilation_mode_copts=compilation_mode_copts,", "      ios_simulator_device=ios_simulator_device,", "      ios_simulator_version=str(ios_simulator_version),", "      signing_certificate_name=signing_certificate_name,", "      generate_dsym=generate_dsym,", "   )", "swift_binary = rule(", "implementation = swift_binary_impl,", "fragments = ['objc']", ")");
        scratch.file("examples/objc_skylark/a.m");
        scratch.file("examples/objc_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:objc_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", ")");
        useConfiguration("--compilation_mode=opt", "--objccopt=-DTestObjcCopt", "--ios_simulator_device='iPhone 6'", "--ios_simulator_version=8.4", "--ios_signing_cert_name='Apple Developer'", "--apple_generate_dsym");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/objc_skylark:my_target");
        @SuppressWarnings("unchecked")
        List<String> copts = ((List<String>) (skylarkTarget.get("copts")));
        @SuppressWarnings("unchecked")
        List<String> compilationModeCopts = ((List<String>) (skylarkTarget.get("compilation_mode_copts")));
        Object iosSimulatorDevice = skylarkTarget.get("ios_simulator_device");
        Object iosSimulatorVersion = skylarkTarget.get("ios_simulator_version");
        Object signingCertificateName = skylarkTarget.get("signing_certificate_name");
        Boolean generateDsym = ((Boolean) (skylarkTarget.get("generate_dsym")));
        assertThat(copts).contains("-DTestObjcCopt");
        assertThat(compilationModeCopts).containsExactlyElementsIn(OPT_COPTS);
        assertThat(iosSimulatorDevice).isEqualTo("'iPhone 6'");
        assertThat(iosSimulatorVersion).isEqualTo("8.4");
        assertThat(signingCertificateName).isEqualTo("'Apple Developer'");
        assertThat(generateDsym).isTrue();
    }

    @Test
    public void testSigningCertificateNameCanReturnNone() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/objc_rules.bzl", "def my_rule_impl(ctx):", "   signing_certificate_name = ctx.fragments.objc.signing_certificate_name", "   return struct(", "      signing_certificate_name=str(signing_certificate_name),", "   )", "my_rule = rule(", "implementation = my_rule_impl,", "fragments = ['objc']", ")");
        scratch.file("examples/objc_skylark/a.m");
        scratch.file("examples/objc_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:objc_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target',", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/objc_skylark:my_target");
        Object signingCertificateName = skylarkTarget.get("signing_certificate_name");
        assertThat(signingCertificateName).isEqualTo("None");
    }

    @Test
    public void testUsesDebugEntitlementsIsTrueIfCompilationModeIsNotOpt() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/objc_rules.bzl", "def test_rule_impl(ctx):", "   uses_device_debug_entitlements = ctx.fragments.objc.uses_device_debug_entitlements", "   return struct(", "      uses_device_debug_entitlements=uses_device_debug_entitlements,", "   )", "test_rule = rule(", "implementation = test_rule_impl,", "fragments = ['objc']", ")");
        scratch.file("examples/objc_skylark/a.m");
        scratch.file("examples/objc_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:objc_rules.bzl', 'test_rule')", "test_rule(", "   name='my_target',", ")");
        useConfiguration("--compilation_mode=dbg");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/objc_skylark:my_target");
        boolean usesDeviceDebugEntitlements = ((boolean) (skylarkTarget.get("uses_device_debug_entitlements")));
        assertThat(usesDeviceDebugEntitlements).isTrue();
    }

    @Test
    public void testUsesDebugEntitlementsIsFalseIfFlagIsExplicitlyFalse() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/objc_rules.bzl", "def test_rule_impl(ctx):", "   uses_device_debug_entitlements = ctx.fragments.objc.uses_device_debug_entitlements", "   return struct(", "      uses_device_debug_entitlements=uses_device_debug_entitlements,", "   )", "test_rule = rule(", "implementation = test_rule_impl,", "fragments = ['objc']", ")");
        scratch.file("examples/objc_skylark/a.m");
        scratch.file("examples/objc_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:objc_rules.bzl', 'test_rule')", "test_rule(", "   name='my_target',", ")");
        useConfiguration("--compilation_mode=dbg", "--nodevice_debug_entitlements");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/objc_skylark:my_target");
        boolean usesDeviceDebugEntitlements = ((boolean) (skylarkTarget.get("uses_device_debug_entitlements")));
        assertThat(usesDeviceDebugEntitlements).isFalse();
    }

    @Test
    public void testUsesDebugEntitlementsIsFalseIfCompilationModeIsOpt() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/objc_rules.bzl", "def test_rule_impl(ctx):", "   uses_device_debug_entitlements = ctx.fragments.objc.uses_device_debug_entitlements", "   return struct(", "      uses_device_debug_entitlements=uses_device_debug_entitlements,", "   )", "test_rule = rule(", "implementation = test_rule_impl,", "fragments = ['objc']", ")");
        scratch.file("examples/objc_skylark/a.m");
        scratch.file("examples/objc_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:objc_rules.bzl', 'test_rule')", "test_rule(", "   name='my_target',", ")");
        useConfiguration("--compilation_mode=opt");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/objc_skylark:my_target");
        boolean usesDeviceDebugEntitlements = ((boolean) (skylarkTarget.get("uses_device_debug_entitlements")));
        assertThat(usesDeviceDebugEntitlements).isFalse();
    }

    @Test
    public void testSkylarkCanCreateObjcProviderFromScratch() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   defines = depset(['define1', 'define2'])", "   linkopts = depset(['somelinkopt'])", "   created_provider = apple_common.new_objc_provider\\", "(define=defines, linkopt=linkopts)", "   return struct(objc=created_provider)");
        Iterable<String> foundLinkopts = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(LINKOPT);
        Iterable<String> foundDefines = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(DEFINE);
        boolean usesSwift = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).is(USES_SWIFT);
        assertThat(foundLinkopts).containsExactly("somelinkopt");
        assertThat(foundDefines).containsExactly("define1", "define2");
        assertThat(usesSwift).isFalse();
    }

    @Test
    public void testSkylarkCanPassLinkInputsInObjcProvider() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   file = ctx.actions.declare_file('foo.ast')", "   ctx.actions.run_shell(outputs=[file], command='echo')", "   link_inputs = depset([file])", "   created_provider = apple_common.new_objc_provider\\", "(link_inputs=link_inputs)", "   return struct(objc=created_provider)");
        Iterable<Artifact> foundLinkInputs = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(LINK_INPUTS);
        assertThat(ActionsTestUtil.baseArtifactNames(foundLinkInputs)).contains("foo.ast");
    }

    @Test
    public void testSkylarkCanPassUsesSwiftFlag() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(uses_swift=True)", "   return struct(objc=created_provider)");
        boolean usesSwift = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).is(USES_SWIFT);
        assertThat(usesSwift).isTrue();
    }

    @Test
    public void testSkylarkCanCreateObjcProviderWithPathFragments() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   includes = depset(['path1', 'path_dir/path2', 'path_dir1/path_dir2/path3'])", "   created_provider = apple_common.new_objc_provider\\", "(include=includes)", "   return struct(objc=created_provider)");
        Iterable<PathFragment> foundIncludes = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(INCLUDE);
        assertThat(foundIncludes).containsExactly(PathFragment.create("path1"), PathFragment.create("path_dir/path2"), PathFragment.create("path_dir1/path_dir2/path3"));
    }

    @Test
    public void testSkylarkCanCreateObjcProviderWithStrictDeps() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   strict_includes = depset(['path1'])", "   propagated_includes = depset(['path2'])", "   strict_provider = apple_common.new_objc_provider\\", "(include=strict_includes)", "   created_provider = apple_common.new_objc_provider\\", "(include=propagated_includes, direct_dep_providers=[strict_provider])", "   return struct(objc=created_provider)");
        ObjcProvider skylarkProvider = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR);
        ObjcProvider skylarkProviderDirectDepender = ObjcSkylarkTest.objcProviderBuilder().addTransitiveAndPropagate(skylarkProvider).build();
        ObjcProvider skylarkProviderIndirectDepender = ObjcSkylarkTest.objcProviderBuilder().addTransitiveAndPropagate(skylarkProviderDirectDepender).build();
        assertThat(skylarkProvider.get(INCLUDE)).containsExactly(PathFragment.create("path1"), PathFragment.create("path2"));
        assertThat(skylarkProviderDirectDepender.get(INCLUDE)).containsExactly(PathFragment.create("path1"), PathFragment.create("path2"));
        assertThat(skylarkProviderIndirectDepender.get(INCLUDE)).containsExactly(PathFragment.create("path2"));
    }

    @Test
    public void testSkylarkCanCreateObjcProviderFromObjcProvider() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   dep = ctx.attr.deps[0]", "   define = depset(['define_from_impl'])", "   created_provider = apple_common.new_objc_provider\\", "(providers=[dep.objc], define=define)", "   return struct(objc=created_provider)");
        Iterable<String> foundStrings = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(DEFINE);
        assertThat(foundStrings).containsExactly("define_from_dep", "define_from_impl");
    }

    @Test
    public void testRuleReturnsObjcProviderDirectly() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   dep = ctx.attr.deps[0]", "   define = depset(['define_from_impl'])", "   created_provider = apple_common.new_objc_provider\\", "(providers=[dep.objc], define=define)", "   return created_provider");
        Iterable<String> foundStrings = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(DEFINE);
        assertThat(foundStrings).containsExactly("define_from_dep", "define_from_impl");
    }

    @Test
    public void testRuleReturnsObjcProviderUnderProvidersAttribute() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   dep = ctx.attr.deps[0]", "   define = depset(['define_from_impl'])", "   created_provider = apple_common.new_objc_provider\\", "(providers=[dep.objc], define=define)", "   return struct(providers=[created_provider])");
        Iterable<String> foundStrings = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(DEFINE);
        assertThat(foundStrings).containsExactly("define_from_dep", "define_from_impl");
    }

    @Test
    public void testRuleReturnsObjcProviderInList() throws Exception {
        ConfiguredTarget skylarkTarget = createObjcProviderSkylarkTarget("   dep = ctx.attr.deps[0]", "   define = depset(['define_from_impl'])", "   created_provider = apple_common.new_objc_provider\\", "(providers=[dep.objc], define=define)", "   return [created_provider]");
        Iterable<String> foundStrings = skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR).get(DEFINE);
        assertThat(foundStrings).containsExactly("define_from_dep", "define_from_impl");
    }

    @Test
    public void testSkylarkErrorOnBadObjcProviderInputKey() throws Exception {
        try {
            createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(foo=depset(['bar']))", "   return struct(objc=created_provider)");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(BAD_KEY_ERROR, "foo"));
        }
    }

    @Test
    public void testSkylarkErrorOnNonSetObjcProviderInputValue() throws Exception {
        try {
            createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(library='bar')", "   return struct(objc=created_provider)");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(NOT_SET_ERROR, "library", "string"));
        }
    }

    @Test
    public void testSkylarkErrorOnObjcProviderInputValueWrongSetType() throws Exception {
        try {
            createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(library=depset(['bar']))", "   return struct(objc=created_provider)");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(BAD_SET_TYPE_ERROR, "library", "File", "string"));
        }
    }

    @Test
    public void testSkylarkErrorOnNonIterableObjcProviderProviderValue() throws Exception {
        try {
            createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(providers='bar')", "   return struct(objc=created_provider)");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(BAD_PROVIDERS_ITER_ERROR, "string"));
        }
    }

    @Test
    public void testSkylarkErrorOnBadIterableObjcProviderProviderValue() throws Exception {
        try {
            createObjcProviderSkylarkTarget("   created_provider = apple_common.new_objc_provider(providers=['bar'])", "   return struct(objc=created_provider)");
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains(String.format(BAD_PROVIDERS_ELEM_ERROR, "string"));
        }
    }

    @Test
    public void testEmptyObjcProviderKeysArePresent() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def swift_binary_impl(ctx):", "   objc_provider = ctx.attr.deps[0].objc", "   return struct(", "      empty_value=objc_provider.include,", "   )", "swift_binary = rule(", "implementation = swift_binary_impl,", "fragments = ['apple'],", "attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False, providers = ['objc'])", "})");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'swift_binary')", "swift_binary(", "   name='my_target',", "   deps=[':lib'],", ")", "objc_library(", "   name = 'lib',", "   srcs = ['a.m'],", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        SkylarkNestedSet emptyValue = ((SkylarkNestedSet) (skylarkTarget.get("empty_value")));
        assertThat(emptyValue.toCollection()).isEmpty();
    }

    @Test
    public void testSkylarkCanAccessProvidedBundleFiles() throws Exception {
        // Since the collections of structs with Artifact values are extremely difficult to test with
        // Truth, we fudge them in the Skylark side to return easily comparable dictionaries instead.
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _simplify_bundle_file(bf):", "   return {'file': bf.file.path, 'bundle_path': bf.bundle_path}", "def _test_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep.objc", "   bundle_file = [_simplify_bundle_file(bf) for bf in list(objc_provider.bundle_file)]", "   return struct(", "      bundle_file=bundle_file,", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False, providers = ['objc'])", "})");
        scratch.file("examples/apple_skylark/a.m");
        scratch.file("examples/apple_skylark/flattened/a/a.txt");
        scratch.file("examples/apple_skylark/flattened/b.lproj/b.txt");
        scratch.file("examples/apple_skylark/structured/c/c.txt");
        scratch.file("examples/apple_skylark/structured/d/d.txt");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    resources = glob(['flattened/**']),", "    structured_resources = glob(['structured/**']),", ")", "test_rule(", "    name = 'my_target',", "    deps = [':lib'],", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Iterable<?> bundleFiles = ((Iterable<?>) (skylarkTarget.get("bundle_file")));
        assertThat(bundleFiles).containsAllOf(ImmutableMap.of(BundleableFile.BUNDLE_PATH_FIELD, "a.txt", BundleableFile.BUNDLED_FIELD, "examples/apple_skylark/flattened/a/a.txt"), ImmutableMap.of(BundleableFile.BUNDLE_PATH_FIELD, "b.lproj/b.txt", BundleableFile.BUNDLED_FIELD, "examples/apple_skylark/flattened/b.lproj/b.txt"), ImmutableMap.of(BundleableFile.BUNDLE_PATH_FIELD, "structured/c/c.txt", BundleableFile.BUNDLED_FIELD, "examples/apple_skylark/structured/c/c.txt"), ImmutableMap.of(BundleableFile.BUNDLE_PATH_FIELD, "structured/d/d.txt", BundleableFile.BUNDLED_FIELD, "examples/apple_skylark/structured/d/d.txt"));
    }

    @Test
    public void testSkylarkCanAccessSdkFrameworks() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep.objc", "   return struct(", "      sdk_frameworks=objc_provider.sdk_framework,", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False, providers = ['objc'])", "})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    sdk_frameworks = ['Accelerate', 'GLKit'],", ")", "test_rule(", "    name = 'my_target',", "    deps = [':lib'],", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        SkylarkNestedSet sdkFrameworks = ((SkylarkNestedSet) (skylarkTarget.get("sdk_frameworks")));
        assertThat(sdkFrameworks.toCollection()).containsAllOf("Accelerate", "GLKit");
    }

    @Test
    public void testSkylarkCanAccessAndUseApplePlatformTypes() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   apple = ctx.fragments.apple", "   ios_platform = apple.multi_arch_platform(apple_common.platform_type.ios)", "   watchos_platform = apple.multi_arch_platform(apple_common.platform_type.watchos)", "   tvos_platform = apple.multi_arch_platform(apple_common.platform_type.tvos)", "   return struct(", "      ios_platform=str(ios_platform),", "      watchos_platform=str(watchos_platform),", "      tvos_platform=str(tvos_platform),", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   fragments = ['apple'])");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "    name = 'my_target',", ")");
        useConfiguration("--ios_multi_cpus=arm64,armv7", "--watchos_cpus=armv7k", "--tvos_cpus=arm64");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Object iosPlatform = skylarkTarget.get("ios_platform");
        Object watchosPlatform = skylarkTarget.get("watchos_platform");
        Object tvosPlatform = skylarkTarget.get("tvos_platform");
        assertThat(iosPlatform).isEqualTo("IOS_DEVICE");
        assertThat(watchosPlatform).isEqualTo("WATCHOS_DEVICE");
        assertThat(tvosPlatform).isEqualTo("TVOS_DEVICE");
    }

    @Test
    public void testPlatformIsDeviceReturnsTrueForDevicePlatforms() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   apple = ctx.fragments.apple", "   platform = apple.multi_arch_platform(apple_common.platform_type.ios)", "   return struct(", "      is_device=platform.is_device,", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   fragments = ['apple'])");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "    name = 'my_target',", ")");
        useConfiguration("--ios_multi_cpus=arm64,armv7");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Boolean isDevice = ((Boolean) (skylarkTarget.get("is_device")));
        assertThat(isDevice).isTrue();
    }

    @Test
    public void testPlatformIsDeviceReturnsFalseForSimulatorPlatforms() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   apple = ctx.fragments.apple", "   platform = apple.multi_arch_platform(apple_common.platform_type.ios)", "   return struct(", "      is_device=platform.is_device,", "   )", "test_rule = rule(implementation = _test_rule_impl,", "   fragments = ['apple'])");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "    name = 'my_target',", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        Boolean isDevice = ((Boolean) (skylarkTarget.get("is_device")));
        assertThat(isDevice).isFalse();
    }

    @Test
    public void testSkylarkWithRunMemleaksEnabled() throws Exception {
        useConfiguration("--ios_memleaks");
        checkSkylarkRunMemleaksWithExpectedValue(true);
    }

    @Test
    public void testSkylarkWithRunMemleaksDisabled() throws Exception {
        checkSkylarkRunMemleaksWithExpectedValue(false);
    }

    @Test
    public void testDottedVersion() throws Exception {
        scratch.file("examples/rule/BUILD", "exports_files(['test_artifact'])");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   version = apple_common.dotted_version('5.4')", "   return struct(", "       version=version", "   )", "test_rule = rule(implementation = _test_rule_impl)");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "    name = 'my_target',", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        DottedVersion version = ((DottedVersion) (skylarkTarget.get("version")));
        assertThat(version).isEqualTo(DottedVersion.fromString("5.4"));
    }

    @Test
    public void testDottedVersion_invalid() throws Exception {
        scratch.file("examples/rule/BUILD", "exports_files(['test_artifact'])");
        scratch.file("examples/rule/apple_rules.bzl", "def _test_rule_impl(ctx):", "   version = apple_common.dotted_version('hello')", "   return struct(", "       version=version", "   )", "test_rule = rule(implementation = _test_rule_impl)");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'test_rule')", "test_rule(", "    name = 'my_target',", ")");
        try {
            getConfiguredTarget("//examples/apple_skylark:my_target");
            Assert.fail("Expected an error to be thrown for invalid dotted version string");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains("Dotted version components must all be of the form");
        }
    }

    /**
     * This test verifies that its possible to use the skylark constructor of ObjcProvider as a
     * provider key to obtain the provider. This test only needs to exist as long as there are
     * two methods of retrieving ObjcProvider (which is true for legacy reasons). This is the
     * 'new' method of retrieving ObjcProvider.
     */
    @Test
    public void testObjcProviderSkylarkConstructor() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep[apple_common.Objc]", "   return struct(objc=objc_provider)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "   'deps': attr.label_list(allow_files = False, mandatory = False),", "})");
        scratch.file("examples/apple_skylark/a.cc");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "    name = 'my_target',", "    deps = [':lib'],", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    hdrs = ['a.h']", ")");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR)).isNotNull();
    }

    @Test
    public void testMultiArchSplitTransition() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   return_kwargs = {}", "   for cpu_value in ctx.split_attr.deps:", "     for child_target in ctx.split_attr.deps[cpu_value]:", "       return_kwargs[cpu_value] = struct(objc=child_target.objc)", "   return struct(**return_kwargs)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "       'deps': attr.label_list(cfg=apple_common.multi_arch_split, providers=[['objc']]),", "       'platform_type': attr.string(mandatory=True),", "       'minimum_os_version': attr.string(mandatory=True)},", "   fragments = ['apple'],", ")");
        scratch.file("examples/apple_skylark/a.cc");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "    name = 'my_target',", "    deps = [':lib'],", "    platform_type = 'ios',", "    minimum_os_version='2.2'", ")", "objc_library(", "    name = 'lib',", "    srcs = ['a.m'],", "    hdrs = ['a.h']", ")");
        useConfiguration("--ios_multi_cpus=armv7,arm64");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        ObjcProvider armv7Objc = getValue("objc", ObjcProvider.class);
        ObjcProvider arm64Objc = getValue("objc", ObjcProvider.class);
        assertThat(armv7Objc).isNotNull();
        assertThat(arm64Objc).isNotNull();
        assertThat(Iterables.getOnlyElement(armv7Objc.getObjcLibraries()).getExecPathString()).contains("ios_armv7");
        assertThat(Iterables.getOnlyElement(arm64Objc.getObjcLibraries()).getExecPathString()).contains("ios_arm64");
    }

    @Test
    public void testDisableObjcProviderResourcesWrite() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   file = ctx.actions.declare_file('foo.ast')", "   ctx.actions.run_shell(outputs=[file], command='echo')", "   objc_provider = apple_common.new_objc_provider(xib=depset([file]))", "   return struct(objc=objc_provider)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target',", ")");
        try {
            setSkylarkSemanticsOptions("--incompatible_disable_objc_provider_resources=true");
            getConfiguredTarget("//examples/apple_skylark:my_target");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains("Argument xib not a recognized key");
        }
    }

    @Test
    public void testEnabledObjcProviderResourcesWrite() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   file = ctx.actions.declare_file('foo.ast')", "   ctx.actions.run_shell(outputs=[file], command='echo')", "   objc_provider = apple_common.new_objc_provider(xib=depset([file]))", "   return struct(objc=objc_provider)", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {})");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target',", ")");
        setSkylarkSemanticsOptions("--incompatible_disable_objc_provider_resources=false");
        ConfiguredTarget binaryTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        ObjcProvider objcProvider = binaryTarget.get(ObjcProvider.SKYLARK_CONSTRUCTOR);
        assertThat(objcProvider.get(XIB)).isNotNull();
    }

    @Test
    public void testDisableObjcProviderResourcesRead() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep[apple_common.Objc]", "   return struct(strings=str(objc_provider.strings))", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "      'deps': attr.label_list(providers = ['objc'])})");
        scratch.file("examples/apple_skylark/foo.strings");
        scratch.file("examples/apple_skylark/bar.a");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target',", "   deps=[':bundle_lib'],", ")", "objc_import(", "   name='bundle_lib',", "   archives = ['bar.a'],", "   strings=['foo.strings'],", ")");
        setSkylarkSemanticsOptions("--incompatible_disable_objc_provider_resources=true");
        getConfiguredTarget("//examples/apple_skylark:my_target");
        assertContainsEvent("object of type 'ObjcProvider' has no field 'strings'");
    }

    @Test
    public void testEnabledObjcProviderResourcesRead() throws Exception {
        scratch.file("examples/rule/BUILD");
        scratch.file("examples/rule/apple_rules.bzl", "def my_rule_impl(ctx):", "   dep = ctx.attr.deps[0]", "   objc_provider = dep[apple_common.Objc]", "   return struct(strings=str(objc_provider.strings))", "my_rule = rule(implementation = my_rule_impl,", "   attrs = {", "      'deps': attr.label_list(providers = ['objc'])})");
        scratch.file("examples/apple_skylark/foo.strings");
        scratch.file("examples/apple_skylark/bar.a");
        scratch.file("examples/apple_skylark/BUILD", "package(default_visibility = ['//visibility:public'])", "load('//examples/rule:apple_rules.bzl', 'my_rule')", "my_rule(", "   name='my_target',", "   deps=[':bundle_lib'],", ")", "objc_import(", "   name='bundle_lib',", "   archives = ['bar.a'],", "   strings=['foo.strings'],", ")");
        setSkylarkSemanticsOptions("--incompatible_disable_objc_provider_resources=false");
        ConfiguredTarget skylarkTarget = getConfiguredTarget("//examples/apple_skylark:my_target");
        assertThat(skylarkTarget.get("strings")).isEqualTo("depset([<source file examples/apple_skylark/foo.strings>])");
    }
}

