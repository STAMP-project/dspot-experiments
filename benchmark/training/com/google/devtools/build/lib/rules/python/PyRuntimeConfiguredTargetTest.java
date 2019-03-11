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


import PyRuntimeInfo.PROVIDER;
import PythonVersion.PY2;
import PythonVersion.PY3;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code py_runtime}.
 */
@RunWith(JUnit4.class)
public class PyRuntimeConfiguredTargetTest extends BuildViewTestCase {
    @Test
    public void hermeticRuntime() throws Exception {
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    files = [':myfile'],", "    interpreter = ':myinterpreter',", "    python_version = 'PY2',", ")");
        PyRuntimeInfo info = getConfiguredTarget("//pkg:myruntime").get(PROVIDER);
        assertThat(info.isInBuild()).isTrue();
        assertThat(info.getInterpreterPath()).isNull();
        assertThat(info.getInterpreter().getExecPathString()).isEqualTo("pkg/myinterpreter");
        assertThat(ActionsTestUtil.baseArtifactNames(info.getFiles())).containsExactly("myfile");
        assertThat(info.getPythonVersion()).isEqualTo(PY2);
    }

    @Test
    public void nonhermeticRuntime() throws Exception {
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    interpreter_path = '/system/interpreter',", "    python_version = 'PY2',", ")");
        PyRuntimeInfo info = getConfiguredTarget("//pkg:myruntime").get(PROVIDER);
        assertThat(info.isInBuild()).isFalse();
        assertThat(info.getInterpreterPath().getPathString()).isEqualTo("/system/interpreter");
        assertThat(info.getInterpreter()).isNull();
        assertThat(info.getFiles()).isNull();
        assertThat(info.getPythonVersion()).isEqualTo(PY2);
    }

    @Test
    public void pythonVersionDefault() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime_default',", "    interpreter_path = '/system/interpreter',", ")", "py_runtime(", "    name = 'myruntime_explicit',", "    interpreter_path = '/system/interpreter',", "    python_version = 'PY3',", ")");
        PyRuntimeInfo infoDefault = getConfiguredTarget("//pkg:myruntime_default").get(PROVIDER);
        PyRuntimeInfo infoExplicit = getConfiguredTarget("//pkg:myruntime_explicit").get(PROVIDER);
        assertThat(infoDefault.getPythonVersion()).isEqualTo(PY2);
        assertThat(infoExplicit.getPythonVersion()).isEqualTo(PY3);
    }

    @Test
    public void cannotUseBothInterpreterAndPath() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    interpreter = ':myinterpreter',", "    interpreter_path = '/system/interpreter',", "    python_version = 'PY2',", ")");
        getConfiguredTarget("//pkg:myruntime");
        assertContainsEvent("exactly one of the 'interpreter' or 'interpreter_path' attributes must be specified");
    }

    @Test
    public void mustUseEitherInterpreterOrPath() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        // 
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    python_version = 'PY2',", ")");
        getConfiguredTarget("//pkg:myruntime");
        assertContainsEvent("exactly one of the 'interpreter' or 'interpreter_path' attributes must be specified");
    }

    @Test
    public void interpreterPathMustBeAbsolute() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    interpreter_path = 'some/relative/path',", "    python_version = 'PY2',", ")");
        getConfiguredTarget("//pkg:myruntime");
        assertContainsEvent("must be an absolute path");
    }

    @Test
    public void cannotSpecifyFilesForNonhermeticRuntime() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    files = [':myfile'],", "    interpreter_path = '/system/interpreter',", "    python_version = 'PY2',", ")");
        getConfiguredTarget("//pkg:myruntime");
        assertContainsEvent("if 'interpreter_path' is given then 'files' must be empty");
    }

    @Test
    public void badPythonVersionAttribute() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "py_runtime(", "    name = 'myruntime',", "    interpreter_path = '/system/interpreter',", "    python_version = 'not a Python version',", ")");
        getConfiguredTarget("//pkg:myruntime");
        assertContainsEvent("invalid value in 'python_version' attribute");
    }
}

