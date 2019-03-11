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
package com.google.devtools.build.lib.rules.cpp;


import CcCommon.MINIMUM_OS_VERSION_VARIABLE_NAME;
import CcCommon.SYSROOT_VARIABLE_NAME;
import CompileBuildVariables.IS_USING_FISSION;
import CompileBuildVariables.LEGACY_COMPILE_FLAGS;
import CompileBuildVariables.OUTPUT_FILE;
import CompileBuildVariables.PER_OBJECT_DEBUG_INFO_FILE;
import CompileBuildVariables.SOURCE_FILE;
import CompileBuildVariables.UNFILTERED_COMPILE_FLAGS;
import CompileBuildVariables.USER_COMPILE_FLAGS;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.configuredtargets.RuleConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that {@code CppCompileAction} is populated with the correct build variables.
 */
@RunWith(JUnit4.class)
public class CompileBuildVariablesTest extends BuildViewTestCase {
    @Test
    public void testPresenceOfBasicVariables() throws Exception {
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(SOURCE_FILE.getVariableName())).contains("x/bin.cc");
        assertThat(variables.getStringVariable(OUTPUT_FILE.getVariableName())).contains("_objs/bin/bin");
    }

    @Test
    public void testPresenceOfLegacyCompileFlags() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, "cxx_flag: '-foo'");
        useConfiguration("--noincompatible_disable_legacy_crosstool_fields");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        ImmutableList<String> copts = CcToolchainVariables.toStringList(variables, LEGACY_COMPILE_FLAGS.getVariableName());
        assertThat(copts).contains("-foo");
    }

    @Test
    public void testPresenceOfConfigurationCompileFlags() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig);
        useConfiguration("--copt=-foo");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'], copts = ['-bar'],)");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        ImmutableList<String> userCopts = CcToolchainVariables.toStringList(variables, USER_COMPILE_FLAGS.getVariableName());
        assertThat(userCopts).containsAllIn(ImmutableList.<String>of("-foo", "-bar")).inOrder();
        ImmutableList<String> legacyCopts = CcToolchainVariables.toStringList(variables, LEGACY_COMPILE_FLAGS.getVariableName());
        assertThat(legacyCopts).doesNotContain("-foo");
    }

    @Test
    public void testPresenceOfUserCompileFlags() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig);
        useConfiguration();
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'], copts = ['-foo'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        ImmutableList<String> copts = CcToolchainVariables.toStringList(variables, USER_COMPILE_FLAGS.getVariableName());
        assertThat(copts).contains("-foo");
    }

    @Test
    public void testPresenceOfUnfilteredCompileFlags() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, "unfiltered_cxx_flag: '--i_ll_live_forever'");
        useConfiguration("--noincompatible_disable_legacy_crosstool_fields");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        ImmutableList<String> unfilteredCompileFlags = CcToolchainVariables.toStringList(variables, UNFILTERED_COMPILE_FLAGS.getVariableName());
        assertThat(unfilteredCompileFlags).contains("--i_ll_live_forever");
    }

    @Test
    public void testPerFileCoptsAreInUserCompileFlags() throws Exception {
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        useConfiguration("--per_file_copt=//x:bin@-foo", "--per_file_copt=//x:bar\\.cc@-bar");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        ImmutableList<String> copts = CcToolchainVariables.toStringList(variables, USER_COMPILE_FLAGS.getVariableName());
        assertThat(copts).containsExactly("-foo").inOrder();
    }

    @Test
    public void testPresenceOfSysrootBuildVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, "builtin_sysroot: '/usr/local/custom-sysroot'");
        useConfiguration();
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(SYSROOT_VARIABLE_NAME)).isEqualTo("/usr/local/custom-sysroot");
    }

    @Test
    public void testPresenceOfPerObjectDebugFileBuildVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration("--fission=yes");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(PER_OBJECT_DEBUG_INFO_FILE.getVariableName())).isNotNull();
    }

    @Test
    public void testPresenceOfIsUsingFissionVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration("--fission=yes");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(IS_USING_FISSION.getVariableName())).isNotNull();
    }

    @Test
    public void testPresenceOfIsUsingFissionAndPerDebugObjectFileVariablesWithThinlto() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, "feature {", "  name: 'fission_flags_for_lto_backend'", "  enabled: true", "  flag_set {", "    action: 'lto-backend'", "    flag_group {", "      expand_if_all_available: 'is_using_fission'", "      flag: '-<IS_USING_FISSION>'", "    }", "    flag_group {", "      expand_if_all_available: 'per_object_debug_info_file'", "      flag: '-<PER_OBJECT_DEBUG_INFO_FILE>'", "    }", "  }", "}", MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION, MockCcSupport.SUPPORTS_START_END_LIB_FEATURE, MockCcSupport.THIN_LTO_CONFIGURATION, MockCcSupport.HOST_AND_NONHOST_CONFIGURATION);
        useConfiguration("--fission=yes", "--features=thin_lto");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        RuleConfiguredTarget target = ((RuleConfiguredTarget) (getConfiguredTarget("//x:bin")));
        LtoBackendAction backendAction = ((LtoBackendAction) (target.getActions().stream().filter(( a) -> a.getMnemonic().equals("CcLtoBackendCompile")).findFirst().get()));
        CppCompileAction bitcodeAction = ((CppCompileAction) (target.getActions().stream().filter(( a) -> a.getMnemonic().equals("CppCompile")).findFirst().get()));
        // We don't pass per_object_debug_info_file to bitcode compiles
        assertThat(bitcodeAction.getCompileCommandLine().getVariables().isAvailable(IS_USING_FISSION.getVariableName())).isTrue();
        assertThat(bitcodeAction.getCompileCommandLine().getVariables().isAvailable(PER_OBJECT_DEBUG_INFO_FILE.getVariableName())).isFalse();
        // We do pass per_object_debug_info_file to backend compiles
        assertThat(backendAction.getArguments()).contains("-<PER_OBJECT_DEBUG_INFO_FILE>");
        assertThat(backendAction.getArguments()).contains("-<IS_USING_FISSION>");
    }

    @Test
    public void testPresenceOfPerObjectDebugFileBuildVariableUsingLegacyFields() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration("--fission=yes");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(PER_OBJECT_DEBUG_INFO_FILE.getVariableName())).isNotNull();
    }

    @Test
    public void testPresenceOfMinOsVersionBuildVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, ("feature {" + (((((((("  name: 'min_os_version_flag'" + "  flag_set {") + "    action: 'c++-compile'") + "    flag_group {") + "      expand_if_all_available: 'minimum_os_version'") + "      flag: '-DMIN_OS=%{minimum_os_version}'") + "    }") + "  }") + "}")));
        useConfiguration("--minimum_os_version=6");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['bin.cc'])");
        scratch.file("x/bin.cc");
        CcToolchainVariables variables = getCompileBuildVariables("//x:bin", "bin");
        assertThat(variables.getStringVariable(MINIMUM_OS_VERSION_VARIABLE_NAME)).isEqualTo("6");
    }
}

