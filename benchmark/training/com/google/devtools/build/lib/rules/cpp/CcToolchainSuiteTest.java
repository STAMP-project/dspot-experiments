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


import ToolchainInfo.PROVIDER;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for the {@code cc_toolchain_suite} rule.
 */
@RunWith(JUnit4.class)
public class CcToolchainSuiteTest extends BuildViewTestCase {
    @Test
    public void testCcToolchainLabelFromCpuCompilerAttributes() throws Exception {
        scratch.file("cc/BUILD", "filegroup(name='empty')", "filegroup(name='everything')", "cc_toolchain(", "    name = 'cc-compiler-fruitie',", "    cpu = 'banana',", "    compiler = 'avocado',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", ")", "cc_toolchain_suite(", "    name = 'suite',", "    toolchains = {", "       'k8': ':cc-compiler-fruitie',", "    },", "    proto = \"\"\"", "major_version: 'v1'", "minor_version: '0'", "toolchain {", "  compiler: 'avocado'", "  target_cpu: 'banana'", "  toolchain_identifier: 'not-used-identifier'", "  host_system_name: 'linux'", "  target_system_name: 'linux'", "  abi_version: 'cpu-abi'", "  abi_libc_version: ''", "  target_libc: 'local'", "  builtin_sysroot: 'sysroot'", "}", "\"\"\"", ")");
        useConfiguration("--crosstool_top=//cc:suite", "--cpu=k8", "--host_cpu=k8");
        ConfiguredTarget c = getConfiguredTarget(((ruleClassProvider.getToolsRepository()) + "//tools/cpp:current_cc_toolchain"));
        CppConfiguration config = getConfiguration(c).getFragment(CppConfiguration.class);
        assertThat(config.getRuleProvidingCcToolchainProvider().toString()).isEqualTo("//cc:suite");
    }

    @Test
    public void testCcToolchainFromToolchainIdentifierOverridesCpuCompiler() throws Exception {
        scratch.file("cc/BUILD", "filegroup(name='empty')", "filegroup(name='everything')", "cc_toolchain(", "    name = 'cc-compiler-fruitie',", "    toolchain_identifier = 'toolchain-identifier-fruitie',", "    cpu = 'banana',", "    compiler = 'avocado',", "    all_files = ':empty',", "    ar_files = ':empty',", "    as_files = ':empty',", "    compiler_files = ':empty',", "    dwp_files = ':empty',", "    linker_files = ':empty',", "    strip_files = ':empty',", "    objcopy_files = ':empty',", ")", "cc_toolchain_suite(", "    name = 'suite',", "    toolchains = {", "       'k8': ':cc-compiler-fruitie',", "    },", "    proto = \"\"\"", "major_version: 'v1'", "minor_version: '0'", "toolchain {", "  compiler: 'avocado'", "  target_cpu: 'banana'", "  toolchain_identifier: 'boring-non-fuitie-identifier'", "  host_system_name: 'linux'", "  target_system_name: 'linux'", "  abi_version: 'cpu-abi'", "  abi_libc_version: ''", "  target_libc: 'local'", "  builtin_sysroot: 'sysroot'", "}", "toolchain {", "  compiler: 'orange'", "  target_cpu: 'banana'", "  toolchain_identifier: 'toolchain-identifier-fruitie'", "  host_system_name: 'linux'", "  target_system_name: 'linux'", "  abi_version: 'cpu-abi'", "  abi_libc_version: ''", "  target_libc: 'local'", "  builtin_sysroot: 'sysroot'", "}", "\"\"\"", ")");
        useConfiguration("--crosstool_top=//cc:suite", "--cpu=k8", "--host_cpu=k8");
        ConfiguredTarget c = getConfiguredTarget(((ruleClassProvider.getToolsRepository()) + "//tools/cpp:current_cc_toolchain"));
        CcToolchainProvider ccToolchainProvider = ((CcToolchainProvider) (c.get(PROVIDER)));
        assertThat(ccToolchainProvider.getToolchainIdentifier()).isEqualTo("toolchain-identifier-fruitie");
    }

    @Test
    public void testInvalidCpu() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        useConfiguration("--cpu=bogus");
        getConfiguredTarget(((ruleClassProvider.getToolsRepository()) + "//tools/cpp:current_cc_toolchain"));
        assertContainsEvent("does not contain a toolchain for cpu 'bogus'");
    }
}

