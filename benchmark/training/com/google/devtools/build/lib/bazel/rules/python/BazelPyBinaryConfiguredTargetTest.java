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
package com.google.devtools.build.lib.bazel.rules.python;


import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Bazel-specific tests for {@code py_binary}.
 */
@RunWith(JUnit4.class)
public class BazelPyBinaryConfiguredTargetTest extends BuildViewTestCase {
    private static final String TOOLCHAIN_BZL = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:toolchain.bzl";

    private static final String TOOLCHAIN_TYPE = (TestConstants.TOOLS_REPOSITORY) + "//tools/python:toolchain_type";

    @Test
    public void runtimeSetByPythonTop() throws Exception {
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'my_py_runtime',", "    interpreter_path = '/system/python2',", "    python_version = 'PY2',", ")", "py_binary(", "    name = 'pybin',", "    srcs = ['pybin.py'],", ")");
        String pythonTop = analysisMock.pySupport().createPythonTopEntryPoint(mockToolsConfig, "//pkg:my_py_runtime");
        useConfiguration("--experimental_use_python_toolchains=false", ("--python_top=" + pythonTop));
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:pybin"));
        assertThat(path).isEqualTo("/system/python2");
    }

    @Test
    public void runtimeSetByPythonPath() throws Exception {
        // 
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'pybin',", "    srcs = ['pybin.py'],", ")");
        useConfiguration("--experimental_use_python_toolchains=false", "--python_path=/system/python2");
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:pybin"));
        assertThat(path).isEqualTo("/system/python2");
    }

    @Test
    public void runtimeDefaultsToPythonSystemCommand() throws Exception {
        // 
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'pybin',", "    srcs = ['pybin.py'],", ")");
        useConfiguration("--experimental_use_python_toolchains=false");
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:pybin"));
        assertThat(path).isEqualTo("python");
    }

    @Test
    public void pythonTopTakesPrecedenceOverPythonPath() throws Exception {
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'my_py_runtime',", "    interpreter_path = '/system/python2',", "    python_version = 'PY2',", ")", "py_binary(", "    name = 'pybin',", "    srcs = ['pybin.py'],", ")");
        String pythonTop = analysisMock.pySupport().createPythonTopEntryPoint(mockToolsConfig, "//pkg:my_py_runtime");
        useConfiguration("--experimental_use_python_toolchains=false", ("--python_top=" + pythonTop), "--python_path=/better/not/be/this/one");
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:pybin"));
        assertThat(path).isEqualTo("/system/python2");
    }

    @Test
    public void runtimeObtainedFromToolchain() throws Exception {
        defineToolchains();
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'py2_bin',", "    srcs = ['py2_bin.py'],", "    python_version = 'PY2',", ")", "py_binary(", "    name = 'py3_bin',", "    srcs = ['py3_bin.py'],", "    python_version = 'PY3',", ")");
        useConfiguration("--experimental_use_python_toolchains=true", "--extra_toolchains=//toolchains:py_toolchain");
        String py2Path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:py2_bin"));
        String py3Path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:py3_bin"));
        assertThat(py2Path).isEqualTo("/system/python2");
        assertThat(py3Path).isEqualTo("/system/python3");
    }

    @Test
    public void toolchainCanOmitUnusedRuntimeVersion() throws Exception {
        defineToolchains();
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'py2_bin',", "    srcs = ['py2_bin.py'],", "    python_version = 'PY2',", ")");
        useConfiguration("--experimental_use_python_toolchains=true", "--extra_toolchains=//toolchains:py_toolchain_for_py2_only");
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:py2_bin"));
        assertThat(path).isEqualTo("/system/python2");
    }

    @Test
    public void toolchainTakesPrecedenceOverLegacyFlags() throws Exception {
        defineToolchains();
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'dont_use_this_runtime',", "    interpreter_path = '/dont/use/me',", "    python_version = 'PY2',", ")", "py_binary(", "    name = 'py2_bin',", "    srcs = ['py2_bin.py'],", "    python_version = 'PY2',", ")");
        String pythonTop = analysisMock.pySupport().createPythonTopEntryPoint(mockToolsConfig, "//pkg:dont_use_this_runtime");
        useConfiguration("--experimental_use_python_toolchains=true", "--extra_toolchains=//toolchains:py_toolchain", ("--python_top=" + pythonTop), "--python_path=/better/not/be/this/one");
        String path = getInterpreterPathFromStub(getConfiguredTarget("//pkg:py2_bin"));
        assertThat(path).isEqualTo("/system/python2");
    }

    @Test
    public void toolchainIsMissingNeededRuntime() throws Exception {
        defineToolchains();
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'py3_bin',", "    srcs = ['py3_bin.py'],", "    python_version = 'PY3',", ")");
        useConfiguration("--experimental_use_python_toolchains=true", "--extra_toolchains=//toolchains:py_toolchain_for_py2_only");
        getConfiguredTarget("//pkg:py3_bin");
        assertContainsEvent("The Python toolchain does not provide a runtime for Python version PY3");
    }

    @Test
    public void toolchainInfoFieldIsMissing() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        defineCustomToolchain("return platform_common.ToolchainInfo(", "    py2_runtime = PyRuntimeInfo(", "        interpreter_path = '/system/python2',", "        python_version = 'PY2')", ")");
        // Use PY2 binary to test that we still validate the PY3 field even when it's not needed.
        analyzePy2BinaryTargetUsingCustomToolchain();
        assertContainsEvent("Error parsing the Python toolchain's ToolchainInfo: field 'py3_runtime' is missing");
    }

    @Test
    public void toolchainInfoFieldHasBadType() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        defineCustomToolchain("return platform_common.ToolchainInfo(", "    py2_runtime = PyRuntimeInfo(", "        interpreter_path = '/system/python2',", "        python_version = 'PY2'),", "    py3_runtime = 'abc',", ")");
        // Use PY2 binary to test that we still validate the PY3 field even when it's not needed.
        analyzePy2BinaryTargetUsingCustomToolchain();
        assertContainsEvent(("Error parsing the Python toolchain's ToolchainInfo: Expected a PyRuntimeInfo in field " + "'py3_runtime', but got 'string'"));
    }

    @Test
    public void toolchainInfoFieldHasBadVersion() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        // python_version is erroneously set to PY2 for the PY3 field.
        defineCustomToolchain("return platform_common.ToolchainInfo(", "    py2_runtime = PyRuntimeInfo(", "        interpreter_path = '/system/python2',", "        python_version = 'PY2'),", "    py3_runtime = PyRuntimeInfo(", "        interpreter_path = '/system/python3',", "        python_version = 'PY2'),", ")");
        // Use PY2 binary to test that we still validate the PY3 field even when it's not needed.
        analyzePy2BinaryTargetUsingCustomToolchain();
        assertContainsEvent(("Error retrieving the Python runtime from the toolchain: Expected field 'py3_runtime' to " + "have a runtime with python_version = 'PY3', but got python_version = 'PY2'"));
    }
}

