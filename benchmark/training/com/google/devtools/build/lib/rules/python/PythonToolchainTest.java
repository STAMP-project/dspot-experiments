/**
 * Copyright 2019 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.python;


import ToolchainInfo.PROVIDER;
import com.google.devtools.build.lib.analysis.platform.ToolchainInfo;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.syntax.Runtime;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the standard Python toolchain definitions.
 *
 * <p>This covers invariants of the toolchain definitions, and the interaction with a user-defined
 * consuming rule, but not the behavior of {@code py_binary} and {@code py_test}. Those tests are
 * under {@link PyExecutableConfiguredTargetTestBase} instead.
 */
@RunWith(JUnit4.class)
public class PythonToolchainTest extends BuildViewTestCase {
    private static final String TOOLCHAIN_BZL = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:toolchain.bzl";

    private static final String TOOLCHAIN_TYPE = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:toolchain_type";

    private static final String PY2_PATH_CONSTRAINT = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:py2_interpreter_path";

    private static final String PY3_PATH_CONSTRAINT = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:py3_interpreter_path";

    @Test
    public void userDefinedConsumerUsingToolchainResolution() throws Exception {
        // A simple platform with unique constraint values.
        scratch.file("platforms/BUILD", "constraint_value(", "    name = 'my_py2_path',", (("    constraint_setting = '" + (PythonToolchainTest.PY2_PATH_CONSTRAINT)) + "',"), ")", "constraint_value(", "    name = 'my_py3_path',", (("    constraint_setting = '" + (PythonToolchainTest.PY3_PATH_CONSTRAINT)) + "',"), ")", "platform(", "    name = 'my_platform',", "    constraint_values = [':my_py2_path', ':my_py3_path'],", ")");
        // A user rule that requires the Python toolchain type and spits out the resulting info.
        scratch.file("pkg/rules.bzl", "def _myrule_impl(ctx):", (("    info = ctx.toolchains['" + (PythonToolchainTest.TOOLCHAIN_TYPE)) + "']"), "    print('PY2 path: ' + info.py2_runtime.interpreter_path)", "    print('PY3 path: ' + info.py3_runtime.interpreter_path)", "myrule = rule(", "    implementation = _myrule_impl,", (("    toolchains = ['" + (PythonToolchainTest.TOOLCHAIN_TYPE)) + "'],"), ")");
        // A toolchain implementation and an instance of the rule that will use it.
        scratch.file("pkg/BUILD", (("load('" + (PythonToolchainTest.TOOLCHAIN_BZL)) + "', 'py_runtime_pair')"), "load(':rules.bzl', 'myrule')", "py_runtime(", "    name = 'my_py2_runtime',", "    interpreter_path = '/system/python2',", "    python_version = 'PY2',", ")", "py_runtime(", "    name = 'my_py3_runtime',", "    interpreter_path = '/system/python3',", "    python_version = 'PY3',", ")", "py_runtime_pair(", "    name = 'my_py_runtime_pair',", "    py2_runtime = ':my_py2_runtime',", "    py3_runtime = ':my_py3_runtime',", ")", "toolchain(", "    name = 'my_toolchain',", "    target_compatible_with = ['//platforms:my_py2_path', '//platforms:my_py3_path'],", "    toolchain = ':my_py_runtime_pair',", (("    toolchain_type = '" + (PythonToolchainTest.TOOLCHAIN_TYPE)) + "',"), ")", "myrule(", "    name = 'mytarget',", ")");
        // Register the toolchain and ask for the platform.
        useConfiguration("--platforms=//platforms:my_platform", "--extra_toolchains=//pkg:my_toolchain");
        getConfiguredTarget("//pkg:mytarget");
        assertContainsEvent("PY2 path: /system/python2");
        assertContainsEvent("PY3 path: /system/python3");
    }

    @Test
    public void okToOmitRuntimes() throws Exception {
        scratch.file("pkg/BUILD", (("load('" + (PythonToolchainTest.TOOLCHAIN_BZL)) + "', 'py_runtime_pair')"), "py_runtime_pair(", "    name = 'my_py_runtime_pair',", ")");
        ToolchainInfo info = getConfiguredTarget("//pkg:my_py_runtime_pair").get(PROVIDER);
        assertThat(info.getValue("py2_runtime")).isEqualTo(Runtime.NONE);
        assertThat(info.getValue("py3_runtime")).isEqualTo(Runtime.NONE);
    }

    @Test
    public void wrongVersionInToolchainAttribute() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", (("load('" + (PythonToolchainTest.TOOLCHAIN_BZL)) + "', 'py_runtime_pair')"), "py_runtime(", "    name = 'bad_py2_runtime',", "    interpreter_path = '/system/python2',", "    python_version = 'PY3',", ")", "py_runtime(", "    name = 'bad_py3_runtime',", "    interpreter_path = '/system/python3',", "    python_version = 'PY2',", ")", "py_runtime_pair(", "    name = 'with_bad_py2_runtime',", "    py2_runtime = ':bad_py2_runtime',", ")", "py_runtime_pair(", "    name = 'with_bad_py3_runtime',", "    py3_runtime = ':bad_py3_runtime',", ")");
        getConfiguredTarget("//pkg:with_bad_py2_runtime");
        getConfiguredTarget("//pkg:with_bad_py3_runtime");
        assertContainsEvent("The Python runtime in the 'py2_runtime' attribute did not have version 'PY2'");
        assertContainsEvent("The Python runtime in the 'py3_runtime' attribute did not have version 'PY3'");
    }

    @Test
    public void missingProviderInToolchainAttribute() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", (("load('" + (PythonToolchainTest.TOOLCHAIN_BZL)) + "', 'py_runtime_pair')"), "sh_binary(", "    name = 'not_a_runtime',", "    srcs = ['not_a_runtime.sh'],", ")", "py_runtime_pair(", "    name = 'bad_py_runtime_pair',", "    py2_runtime = ':not_a_runtime',", ")");
        getConfiguredTarget("//pkg:bad_py_runtime_pair");
        assertContainsEvent("'//pkg:not_a_runtime' does not have mandatory providers: 'PyRuntimeInfo'");
    }
}

