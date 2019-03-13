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


import Location.BUILTIN;
import Order.STABLE_ORDER;
import PythonVersion.PY2;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.skylark.util.SkylarkTestCase;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PyRuntimeInfo}.
 */
@RunWith(JUnit4.class)
public class PyRuntimeInfoTest extends SkylarkTestCase {
    private Artifact dummyInterpreter;

    private Artifact dummyFile;

    @Test
    public void factoryMethod_InBuildRuntime() {
        NestedSet<Artifact> files = NestedSetBuilder.create(STABLE_ORDER, dummyFile);
        PyRuntimeInfo inBuildRuntime = PyRuntimeInfo.createForInBuildRuntime(dummyInterpreter, files, PY2);
        assertThat(inBuildRuntime.getCreationLoc()).isEqualTo(BUILTIN);
        assertThat(inBuildRuntime.getInterpreterPath()).isNull();
        assertThat(inBuildRuntime.getInterpreterPathString()).isNull();
        assertThat(inBuildRuntime.getInterpreter()).isEqualTo(dummyInterpreter);
        assertThat(inBuildRuntime.getFiles()).isEqualTo(files);
        assertThat(inBuildRuntime.getFilesForStarlark().getSet(Artifact.class)).isEqualTo(files);
        assertThat(inBuildRuntime.getPythonVersion()).isEqualTo(PY2);
        assertThat(inBuildRuntime.getPythonVersionForStarlark()).isEqualTo("PY2");
    }

    @Test
    public void factoryMethod_PlatformRuntime() {
        PathFragment path = PathFragment.create("/system/interpreter");
        PyRuntimeInfo platformRuntime = PyRuntimeInfo.createForPlatformRuntime(path, PY2);
        assertThat(platformRuntime.getCreationLoc()).isEqualTo(BUILTIN);
        assertThat(platformRuntime.getInterpreterPath()).isEqualTo(path);
        assertThat(platformRuntime.getInterpreterPathString()).isEqualTo("/system/interpreter");
        assertThat(platformRuntime.getInterpreter()).isNull();
        assertThat(platformRuntime.getFiles()).isNull();
        assertThat(platformRuntime.getFilesForStarlark()).isNull();
        assertThat(platformRuntime.getPythonVersion()).isEqualTo(PY2);
        assertThat(platformRuntime.getPythonVersionForStarlark()).isEqualTo("PY2");
    }

    @Test
    public void starlarkConstructor_InBuildRuntime() throws Exception {
        eval("info = PyRuntimeInfo(", "    interpreter = dummy_interpreter,", "    files = depset([dummy_file]),", "    python_version = 'PY2',", ")");
        PyRuntimeInfo info = ((PyRuntimeInfo) (lookup("info")));
        assertThat(info.getCreationLoc().getStartOffset()).isEqualTo(7);
        assertThat(info.getInterpreterPath()).isNull();
        assertThat(info.getInterpreter()).isEqualTo(dummyInterpreter);
        PyRuntimeInfoTest.assertHasOrderAndContainsExactly(info.getFiles(), STABLE_ORDER, dummyFile);
        assertThat(info.getPythonVersion()).isEqualTo(PY2);
    }

    @Test
    public void starlarkConstructor_PlatformRuntime() throws Exception {
        // 
        eval("info = PyRuntimeInfo(", "    interpreter_path = '/system/interpreter',", "    python_version = 'PY2',", ")");
        PyRuntimeInfo info = ((PyRuntimeInfo) (lookup("info")));
        assertThat(info.getCreationLoc().getStartOffset()).isEqualTo(7);
        assertThat(info.getInterpreterPath()).isEqualTo(PathFragment.create("/system/interpreter"));
        assertThat(info.getInterpreter()).isNull();
        assertThat(info.getFiles()).isNull();
        assertThat(info.getPythonVersion()).isEqualTo(PY2);
    }

    @Test
    public void starlarkConstructor_FilesDefaultsToEmpty() throws Exception {
        // 
        eval("info = PyRuntimeInfo(", "    interpreter = dummy_interpreter,", "    python_version = 'PY2',", ")");
        PyRuntimeInfo info = ((PyRuntimeInfo) (lookup("info")));
        PyRuntimeInfoTest.assertHasOrderAndContainsExactly(info.getFiles(), STABLE_ORDER);
    }

    @Test
    public void starlarkConstructorErrors_InBuildXorPlatform() throws Exception {
        checkEvalErrorContains("exactly one of the 'interpreter' or 'interpreter_path' arguments must be specified", "PyRuntimeInfo(", "    python_version = 'PY2',", ")");
        checkEvalErrorContains("exactly one of the 'interpreter' or 'interpreter_path' arguments must be specified", "PyRuntimeInfo(", "    interpreter_path = '/system/interpreter',", "    interpreter = dummy_interpreter,", "    python_version = 'PY2',", ")");
    }

    @Test
    public void starlarkConstructorErrors_Files() throws Exception {
        checkEvalErrorContains("expected value of type 'depset of Files or NoneType' for parameter 'files'", "PyRuntimeInfo(", "    interpreter = dummy_interpreter,", "    files = 'abc',", "    python_version = 'PY2',", ")");
        checkEvalErrorContains("expected value of type 'depset of Files or NoneType' for parameter 'files'", "PyRuntimeInfo(", "    interpreter = dummy_interpreter,", "    files = depset(['abc']),", "    python_version = 'PY2',", ")");
        checkEvalErrorContains("cannot specify 'files' if 'interpreter_path' is given", "PyRuntimeInfo(", "    interpreter_path = '/system/interpreter',", "    files = depset([dummy_file]),", "    python_version = 'PY2',", ")");
    }

    @Test
    public void starlarkConstructorErrors_PythonVersion() throws Exception {
        checkEvalErrorContains("parameter 'python_version' has no default value", "PyRuntimeInfo(", "    interpreter_path = '/system/interpreter',", ")");
        checkEvalErrorContains(("illegal value for 'python_version': 'not a Python version' is not a valid Python major " + "version. Expected 'PY2' or 'PY3'."), "PyRuntimeInfo(", "    interpreter_path = '/system/interpreter',", "    python_version = 'not a Python version',", ")");
    }
}

