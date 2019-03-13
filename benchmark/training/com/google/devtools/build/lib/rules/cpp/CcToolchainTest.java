/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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


import CppRuleClasses.STATIC_LINK_CPP_RUNTIMES;
import DynamicMode.DEFAULT;
import DynamicMode.FULLY;
import DynamicMode.OFF;
import FeatureConfiguration.EMPTY;
import Tool.AR;
import Tool.CPP;
import ToolchainInfo.PROVIDER;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.rules.cpp.CcToolchainFeatures.FeatureConfiguration;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.common.options.OptionsParsingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for toolchain features.
 */
@RunWith(JUnit4.class)
public class CcToolchainTest extends BuildViewTestCase {
    @Test
    public void testFilesToBuild() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        ConfiguredTarget b = getConfiguredTarget("//a:b");
        assertThat(ActionsTestUtil.baseArtifactNames(getFilesToBuild(b))).isNotNull();
    }

    @Test
    public void testInterfaceSharedObjects() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE);
        useConfiguration("--features=-supports_interface_shared_libraries");
        invalidatePackages();
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(CppHelper.useInterfaceSharedLibraries(getConfiguration(target).getFragment(CppConfiguration.class), toolchainProvider, EMPTY)).isFalse();
        useConfiguration();
        invalidatePackages();
        target = getConfiguredTarget("//a:b");
        toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(CppHelper.useInterfaceSharedLibraries(getConfiguration(target).getFragment(CppConfiguration.class), toolchainProvider, EMPTY)).isFalse();
        useConfiguration("--nointerface_shared_objects");
        invalidatePackages();
        target = getConfiguredTarget("//a:b");
        toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(CppHelper.useInterfaceSharedLibraries(getConfiguration(target).getFragment(CppConfiguration.class), toolchainProvider, EMPTY)).isFalse();
    }

    @Test
    public void testFission() throws Exception {
        scratch.file("a/BUILD", "cc_library(name = 'a', srcs = ['a.cc'])");
        // Default configuration: disabled.
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration();
        assertThat(getCppCompileOutputs()).doesNotContain("yolo");
        // Mode-specific settings.
        useConfiguration("-c", "dbg", "--fission=dbg");
        assertThat(getCppCompileOutputs()).contains("a.dwo");
        useConfiguration("-c", "dbg", "--fission=opt");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
        useConfiguration("-c", "dbg", "--fission=opt,dbg");
        assertThat(getCppCompileOutputs()).contains("a.dwo");
        useConfiguration("-c", "fastbuild", "--fission=opt,dbg");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
        useConfiguration("-c", "fastbuild", "--fission=opt,dbg");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
        // Universally enabled
        useConfiguration("-c", "dbg", "--fission=yes");
        assertThat(getCppCompileOutputs()).contains("a.dwo");
        useConfiguration("-c", "opt", "--fission=yes");
        assertThat(getCppCompileOutputs()).contains("a.dwo");
        useConfiguration("-c", "fastbuild", "--fission=yes");
        assertThat(getCppCompileOutputs()).contains("a.dwo");
        // Universally disabled
        useConfiguration("-c", "dbg", "--fission=no");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
        useConfiguration("-c", "opt", "--fission=no");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
        useConfiguration("-c", "fastbuild", "--fission=no");
        assertThat(getCppCompileOutputs()).doesNotContain("a.dwo");
    }

    @Test
    public void testPic() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        assertThat(usePicForBinariesWithConfiguration("--cpu=k8")).isFalse();
        assertThat(usePicForBinariesWithConfiguration("--cpu=k8", "-c", "opt")).isFalse();
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_PIC_FEATURE);
        invalidatePackages();
        assertThat(usePicForBinariesWithConfiguration("--cpu=k8")).isTrue();
        assertThat(usePicForBinariesWithConfiguration("--cpu=k8", "-c", "opt")).isFalse();
    }

    @Test
    public void testBadDynamicRuntimeLib() throws Exception {
        scratch.file("a/BUILD", "filegroup(name='dynamic', srcs=['not-an-so', 'so.so'])", "filegroup(name='static', srcs=['not-an-a', 'a.a'])", "cc_toolchain(", "    name = 'a',", "    module_map = 'map',", "    ar_files = 'ar-a',", "    as_files = 'as-a',", "    cpu = 'cherry',", "    compiler_files = 'compile-a',", "    dwp_files = 'dwp-a',", "    coverage_files = 'gcov-a',", "    linker_files = 'link-a',", "    strip_files = 'strip-a',", "    objcopy_files = 'objcopy-a',", "    all_files = 'all-a',", "    dynamic_runtime_lib = ':dynamic',", "    static_runtime_lib = ':static')");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.STATIC_LINK_CPP_RUNTIMES_FEATURE);
        useConfiguration();
        getConfiguredTarget("//a:b");
    }

    @Test
    public void testDynamicMode() throws Exception {
        scratch.file("a/BUILD", "filegroup(", "   name='empty')", "filegroup(", "    name = 'banana',", "    srcs = ['banana1', 'banana2'])", "cc_toolchain(", "    name = 'b',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    cpu = 'banana',", "    all_files = ':banana',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", "    dynamic_runtime_lib = ':empty',", "    static_runtime_lib = ':empty')");
        scratch.file("a/CROSSTOOL", MockCcSupport.EMPTY_CROSSTOOL);
        // Check defaults.
        useConfiguration();
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CppConfiguration cppConfiguration = getConfiguration(target).getFragment(CppConfiguration.class);
        assertThat(cppConfiguration.getDynamicModeFlag()).isEqualTo(DEFAULT);
        // Test "off"
        useConfiguration("--dynamic_mode=off");
        target = getConfiguredTarget("//a:b");
        cppConfiguration = getConfiguration(target).getFragment(CppConfiguration.class);
        assertThat(cppConfiguration.getDynamicModeFlag()).isEqualTo(OFF);
        // Test "fully"
        useConfiguration("--dynamic_mode=fully");
        target = getConfiguredTarget("//a:b");
        cppConfiguration = getConfiguration(target).getFragment(CppConfiguration.class);
        assertThat(cppConfiguration.getDynamicModeFlag()).isEqualTo(FULLY);
        // Check an invalid value for disable_dynamic.
        try {
            useConfiguration("--dynamic_mode=very");
            Assert.fail("OptionsParsingException not thrown.");// COV_NF_LINE

        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo(("While parsing option --dynamic_mode=very: Not a valid dynamic mode: 'very' " + "(should be off, default or fully)"));
        }
    }

    @Test
    public void testParamDfDoubleQueueThresholdFactor() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        useConfiguration();
        scratch.file("lib/BUILD", "cc_library(", "   name = 'lib',", "   srcs = ['a.cc'],", ")");
        ConfiguredTarget lib = getConfiguredTarget("//lib");
        CcToolchainProvider toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(getRuleContext(lib));
        MoreAsserts.assertDoesNotContainSublist(toolchain.getLegacyCompileOptionsWithCopts(), "--param", "df-double-quote-threshold-factor=0");
    }

    @Test
    public void testMergesDefaultCoptsWithUserProvidedOnes() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        scratch.file("lib/BUILD", "cc_library(name = 'lib', srcs = ['a.cc'])");
        ConfiguredTarget lib = getConfiguredTarget("//lib");
        CcToolchainProvider toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(getRuleContext(lib));
        List<String> expected = new ArrayList<>();
        expected.addAll(toolchain.getLegacyCompileOptionsWithCopts());
        expected.add("-Dfoo");
        useConfiguration("--copt", "-Dfoo");
        lib = getConfiguredTarget("//lib");
        toolchain = CppHelper.getToolchainUsingDefaultCcToolchainAttribute(getRuleContext(lib));
        assertThat(ImmutableList.copyOf(toolchain.getLegacyCompileOptionsWithCopts())).isEqualTo(ImmutableList.copyOf(expected));
    }

    @Test
    public void testInvalidIncludeDirectory() throws Exception {
        assertInvalidIncludeDirectoryMessage("%package(//a", "has an unrecognized %prefix%");
        assertInvalidIncludeDirectoryMessage("%package(//a:@@a)%", "The package '//a:@@a' is not valid");
        assertInvalidIncludeDirectoryMessage("%package(//a)%foo", "The path in the package.*is not valid");
        assertInvalidIncludeDirectoryMessage("%package(//a)%/../bar", "The include path.*is not normalized");
    }

    @Test
    public void testModuleMapAttribute() throws Exception {
        scratchConfiguredTarget("modules/map", "c", "cc_toolchain(", "    name = 'c',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    module_map = 'map',", "    cpu = 'cherry',", "    ar_files = 'ar-cherry',", "    as_files = 'as-cherry',", "    compiler_files = 'compile-cherry',", "    dwp_files = 'dwp-cherry',", "    coverage_files = 'gcov-cherry',", "    linker_files = 'link-cherry',", "    strip_files = ':every-file',", "    objcopy_files = 'objcopy-cherry',", "    all_files = ':every-file',", "    dynamic_runtime_lib = 'dynamic-runtime-libs-cherry',", "    static_runtime_lib = 'static-runtime-libs-cherry')");
    }

    @Test
    public void testModuleMapAttributeOptional() throws Exception {
        scratchConfiguredTarget("modules/map", "c", "cc_toolchain(", "    name = 'c',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    cpu = 'cherry',", "    ar_files = 'ar-cherry',", "    as_files = 'as-cherry',", "    compiler_files = 'compile-cherry',", "    dwp_files = 'dwp-cherry',", "    linker_files = 'link-cherry',", "    strip_files = ':every-file',", "    objcopy_files = 'objcopy-cherry',", "    all_files = ':every-file',", "    dynamic_runtime_lib = 'dynamic-runtime-libs-cherry',", "    static_runtime_lib = 'static-runtime-libs-cherry')");
    }

    @Test
    public void testFailWithMultipleModuleMaps() throws Exception {
        checkError("modules/multiple", "c", "expected a single artifact", "filegroup(name = 'multiple-maps', srcs = ['a.cppmap', 'b.cppmap'])", "cc_toolchain(", "    name = 'c',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    module_map = ':multiple-maps',", "    cpu = 'cherry',", "    ar_files = 'ar-cherry',", "    as_files = 'as-cherry',", "    compiler_files = 'compile-cherry',", "    dwp_files = 'dwp-cherry',", "    coverage_files = 'gcov-cherry',", "    linker_files = 'link-cherry',", "    strip_files = ':every-file',", "    objcopy_files = 'objcopy-cherry',", "    all_files = ':every-file',", "    dynamic_runtime_lib = 'dynamic-runtime-libs-cherry',", "    static_runtime_lib = 'static-runtime-libs-cherry')");
    }

    @Test
    public void testToolchainAlias() throws Exception {
        ConfiguredTarget reference = scratchConfiguredTarget("a", "ref", "cc_toolchain_alias(name='ref')");
        assertThat(reference.get(PROVIDER.getKey())).isNotNull();
    }

    @Test
    public void testFdoOptimizeInvalidUseGeneratedArtifact() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "genrule(", "    name ='gen_artifact',", "    outs=['profile.profdata'],", "    cmd='touch $@')");
        useConfiguration("-c", "opt", "--fdo_optimize=//a:gen_artifact");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("--fdo_optimize points to a target that is not an input file");
    }

    @Test
    public void testFdoOptimizeUnexpectedExtension() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "exports_files(['profile.unexpected'])");
        scratch.file("a/profile.unexpected", "");
        useConfiguration("-c", "opt", "--fdo_optimize=//a:profile.unexpected");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("invalid extension for FDO profile file");
    }

    @Test
    public void testFdoOptimizeNotInputFile() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "filegroup(", "    name ='profile',", "    srcs=['my_profile.afdo'])");
        scratch.file("my_profile.afdo", "");
        useConfiguration("-c", "opt", "--fdo_optimize=//a:profile");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("--fdo_optimize points to a target that is not an input file");
    }

    @Test
    public void testFdoOptimizeNotCompatibleWithCoverage() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "exports_files(['profile.afdo'])");
        scratch.file("a/profile.afdo", "");
        useConfiguration("-c", "opt", "--fdo_optimize=//a:profile.afdo", "--collect_code_coverage");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("coverage mode is not compatible with FDO optimization");
    }

    @Test
    public void testXFdoOptimizeNotProvider() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "fdo_profile(name='out.xfdo', profile='profile.xfdo')");
        useConfiguration("-c", "opt", "--xbinary_fdo=//a:profile.xfdo");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("--fdo_profile/--xbinary_fdo input needs to be an fdo_profile rule");
    }

    @Test
    public void testXFdoOptimizeRejectAFdoInput() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')", "fdo_profile(name='out.afdo', profile='profile.afdo')");
        useConfiguration("-c", "opt", "--xbinary_fdo=//a:out.afdo");
        assertThat(getConfiguredTarget("//a:b")).isNull();
        assertContainsEvent("--xbinary_fdo cannot accept profile input other than *.xfdo");
    }

    @Test
    public void testZipperInclusionDependsOnFdoOptimization() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "filegroup(", "   name='empty')", "cc_toolchain(", "    name = 'b',", "    toolchain_identifier = 'toolchain-identifier-k8',", "    cpu = 'banana',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty')");
        scratch.file("fdo/my_profile.afdo", "");
        scratch.file("fdo/BUILD", "exports_files(['my_profile.afdo'])", "fdo_profile(name = 'fdo', profile = ':my_profile.profdata')");
        scratch.file("a/CROSSTOOL", MockCcSupport.EMPTY_CROSSTOOL);
        useConfiguration();
        assertThat(getPrerequisites(getConfiguredTarget("//a:b"), ":zipper")).isEmpty();
        useConfiguration("-c", "opt", "--fdo_optimize=//fdo:my_profile.afdo");
        assertThat(getPrerequisites(getConfiguredTarget("//a:b"), ":zipper")).isNotEmpty();
        useConfiguration("-c", "opt", "--fdo_profile=//fdo:fdo");
        assertThat(getPrerequisites(getConfiguredTarget("//a:b"), ":zipper")).isNotEmpty();
    }

    @Test
    public void testInlineCtoolchain_withToolchainResolution() throws Exception {
        scratch.file("a/BUILD", "filegroup(", "   name='empty')", "cc_toolchain(", "    name = 'b',", "    cpu = 'banana',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", "    proto=\"\"\"", "      toolchain_identifier: \"banana\"", "      abi_version: \"banana\"", "      abi_libc_version: \"banana\"", "      compiler: \"banana\"", "      host_system_name: \"banana\"", "      target_system_name: \"banana\"", "      target_cpu: \"banana\"", "      target_libc: \"banana\"", "    \"\"\")");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, "abi_version: 'orange'");
        useConfiguration("--incompatible_enable_cc_toolchain_resolution");
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(toolchainProvider.getAbi()).isEqualTo("banana");
    }

    @Test
    public void testToolchainFromStarlarkRule() throws Exception {
        loadCcToolchainConfigLib();
        writeStarlarkRule();
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, "abi_version: 'orange'");
        useConfiguration("--cpu=k8");
        ConfiguredTarget target = getConfiguredTarget("//a:a");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(toolchainProvider.getAbi()).isEqualTo("banana");
        assertThat(toolchainProvider.getFeatures().getActivatableNames()).containsExactly("simple_action", "simple_feature", "no_legacy_features");
    }

    @Test
    public void testToolPathsInToolchainFromStarlarkRule() throws Exception {
        loadCcToolchainConfigLib();
        writeStarlarkRule();
        getAnalysisMock().ccSupport();
        useConfiguration("--cpu=k8");
        ConfiguredTarget target = getConfiguredTarget("//a:a");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(toolchainProvider.getToolPathFragment(AR).toString()).isEqualTo("/absolute/path");
        assertThat(toolchainProvider.getToolPathFragment(CPP).toString()).isEqualTo("a/relative/path");
    }

    @Test
    public void testSupportsDynamicLinkerIsFalseWhenFeatureNotSet() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig);
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(toolchainProvider.supportsDynamicLinker(EMPTY)).isFalse();
    }

    // Tests CcCommon::enableStaticLinkCppRuntimesFeature when supports_embedded_runtimes is not
    // present at all in the toolchain.
    @Test
    public void testStaticLinkCppRuntimesSetViaSupportsEmbeddedRuntimesUnset() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig);
        useConfiguration();
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        FeatureConfiguration featureConfiguration = CcCommon.configureFeaturesOrReportRuleError(getRuleContext(target), toolchainProvider);
        assertThat(toolchainProvider.supportsEmbeddedRuntimes()).isEqualTo(featureConfiguration.isEnabled(STATIC_LINK_CPP_RUNTIMES));
    }

    // Tests CcCommon::enableStaticLinkCppRuntimesFeature when supports_embedded_runtimes is true in
    // the toolchain and the feature is not present at all.
    @Test
    public void testSupportsEmbeddedRuntimesNoFeatureAtAll() throws Exception {
        FeatureConfiguration featureConfiguration = getFeatureConfigurationForStaticLinkCppRuntimes("supports_embedded_runtimes: true", "--noincompatible_disable_legacy_crosstool_fields");
        assertThat(featureConfiguration.isEnabled(STATIC_LINK_CPP_RUNTIMES)).isTrue();
    }

    // Tests CcCommon::enableStaticLinkCppRuntimesFeature when supports_embedded_runtimes is true in
    // the toolchain and the feature is enabled.
    @Test
    public void testSupportsEmbeddedRuntimesFeatureEnabled() throws Exception {
        FeatureConfiguration featureConfiguration = getFeatureConfigurationForStaticLinkCppRuntimes("supports_embedded_runtimes: true", "--features=static_link_cpp_runtimes", "--noincompatible_disable_legacy_crosstool_fields");
        assertThat(featureConfiguration.isEnabled(STATIC_LINK_CPP_RUNTIMES)).isTrue();
    }

    // Tests CcCommon::enableStaticLinkCppRuntimesFeature when supports_embedded_runtimes is true in
    // the toolchain and the feature is disabled.
    @Test
    public void testStaticLinkCppRuntimesOverridesSupportsEmbeddedRuntimes() throws Exception {
        FeatureConfiguration featureConfiguration = getFeatureConfigurationForStaticLinkCppRuntimes("supports_embedded_runtimes: true", "--features=-static_link_cpp_runtimes", "--noincompatible_disable_legacy_crosstool_fields");
        assertThat(featureConfiguration.isEnabled(STATIC_LINK_CPP_RUNTIMES)).isFalse();
    }

    @Test
    public void testSysroot_fromCrosstool_unset() throws Exception {
        scratch.file("a/BUILD", "cc_toolchain_alias(name = 'b')");
        scratch.file("libc1/BUILD", "filegroup(name = 'everything', srcs = ['header1.h'])");
        scratch.file("libc1/header1.h", "#define FOO 1");
        useConfiguration();
        ConfiguredTarget target = getConfiguredTarget("//a:b");
        CcToolchainProvider toolchainProvider = ((CcToolchainProvider) (target.get(PROVIDER)));
        assertThat(toolchainProvider.getSysroot()).isEqualTo("/usr/grte/v1");
    }

    @Test
    public void testSysroot_fromCcToolchain() throws Exception {
        scratch.file("a/BUILD", "filegroup(", "    name='empty')", "cc_toolchain_suite(", "    name = 'a',", "    toolchains = { 'k8': ':b' },", ")", "cc_toolchain(", "    name = 'b',", "    cpu = 'banana',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", "    proto = \"\"\"", "      toolchain_identifier: \"a\"", "      host_system_name: \"a\"", "      target_system_name: \"a\"", "      target_cpu: \"a\"", "      target_libc: \"a\"", "      compiler: \"a\"", "      abi_version: \"a\"", "      abi_libc_version: \"a\"", "\"\"\",", "    libc_top = '//libc2:everything')");
        scratch.file("libc1/BUILD", "filegroup(name = 'everything', srcs = ['header1.h'])");
        scratch.file("libc1/header1.h", "#define FOO 1");
        scratch.file("libc2/BUILD", "filegroup(name = 'everything', srcs = ['header2.h'])");
        scratch.file("libc2/header2.h", "#define FOO 2");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, "default_grte_top: '//libc1'");
        useConfiguration("--cpu=k8");
        ConfiguredTarget target = getConfiguredTarget("//a:a");
        CcToolchainProvider ccToolchainProvider = ((CcToolchainProvider) (target.get(CcToolchainProvider.PROVIDER)));
        assertThat(ccToolchainProvider.getSysroot()).isEqualTo("libc2");
    }

    @Test
    public void testSysroot_fromFlag() throws Exception {
        scratch.file("a/BUILD", "filegroup(", "    name='empty')", "cc_toolchain_suite(", "    name = 'a',", "    toolchains = { 'k8': ':b' },", ")", "cc_toolchain(", "    name = 'b',", "    cpu = 'banana',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", "    proto = \"\"\"", "      toolchain_identifier: \"a\"", "      host_system_name: \"a\"", "      target_system_name: \"a\"", "      target_cpu: \"a\"", "      target_libc: \"a\"", "      compiler: \"a\"", "      abi_version: \"a\"", "      abi_libc_version: \"a\"", "      builtin_sysroot: \":empty\"", "\"\"\",", "    libc_top = '//libc2:everything')");
        scratch.file("libc1/BUILD", "filegroup(name = 'everything', srcs = ['header.h'])");
        scratch.file("libc1/header.h", "#define FOO 1");
        scratch.file("libc2/BUILD", "filegroup(name = 'everything', srcs = ['header.h'])");
        scratch.file("libc2/header.h", "#define FOO 2");
        scratch.file("libc3/BUILD", "filegroup(name = 'everything', srcs = ['header.h'])");
        scratch.file("libc3/header.h", "#define FOO 3");
        getAnalysisMock().ccSupport().setupCrosstool(mockToolsConfig, "default_grte_top: '//libc1'");
        useConfiguration("--cpu=k8", "--grte_top=//libc3");
        ConfiguredTarget target = getConfiguredTarget("//a:a");
        CcToolchainProvider ccToolchainProvider = ((CcToolchainProvider) (target.get(CcToolchainProvider.PROVIDER)));
        assertThat(ccToolchainProvider.getSysroot()).isEqualTo("libc3");
    }

    @Test
    public void testCrosstoolNeededWhenStarlarkRuleIsNotPresent() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("lib/BUILD", "cc_library(name = 'lib', srcs = ['a.cc'])");
        /* addToolchainConfigAttribute= */
        getSimpleStarlarkRule(false);
        useConfiguration("--cpu=k8", "--crosstool_top=//a:a");
        ConfiguredTarget target = getConfiguredTarget("//lib:lib");
        // Skyframe cannot find the CROSSTOOL file
        assertContainsEvent("errors encountered while analyzing target '//lib:lib'");
        assertThat(target).isNull();
    }

    @Test
    public void testCrosstoolReadWhenStarlarkRuleIsEnabledButNotPresent() throws Exception {
        scratch.file("lib/BUILD", "cc_library(name = 'lib', srcs = ['a.cc'])");
        /* addToolchainConfigAttribute= */
        getSimpleStarlarkRule(false);
        scratch.file("a/CROSSTOOL", MockCcSupport.EMPTY_CROSSTOOL);
        useConfiguration("--cpu=k8", "--crosstool_top=//a:a");
        ConfiguredTarget target = getConfiguredTarget("//lib:lib");
        assertThat(target).isNotNull();
    }

    @Test
    public void testCrosstoolNotNeededWhenStarlarkRuleIsEnabled() throws Exception {
        scratch.file("lib/BUILD", "cc_library(name = 'lib', srcs = ['a.cc'])");
        /* addToolchainConfigAttribute= */
        getSimpleStarlarkRule(true);
        useConfiguration("--cpu=k8", "--crosstool_top=//a:a");
        // We don't have a CROSSTOOL, but we don't need it
        ConfiguredTarget target = getConfiguredTarget("//lib:lib");
        assertThat(target).isNotNull();
    }
}

