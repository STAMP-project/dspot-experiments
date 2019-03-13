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


import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that {@code <tools repo>//tools/python:python_version} works, and that users cannot {@code select()} on the native flags.
 */
@RunWith(JUnit4.class)
public class PythonVersionSelectTest extends BuildViewTestCase {
    @Test
    public void cannotSelectOnNativePythonVersionFlag() throws Exception {
        // error:
        // build file:
        checkError("pkg", "foo", "option 'python_version' cannot be used in a config_setting", PythonVersionSelectTest.makeFooThatSelectsOnFlag("python_version", "PY2"));
    }

    @Test
    public void canSelectOnForcePythonFlagsUnderOldApi() throws Exception {
        // For backwards compatibility purposes, select()-ing on --force_python and --host_force_python
        // is allowed while the old API is still enabled.
        useConfiguration("--incompatible_remove_old_python_version_api=false");
        scratch.file("fp/BUILD", PythonVersionSelectTest.makeFooThatSelectsOnFlag("force_python", "PY2"));
        scratch.file("hfp/BUILD", PythonVersionSelectTest.makeFooThatSelectsOnFlag("host_force_python", "PY2"));
        assertThat(getConfiguredTarget("//fp:foo")).isNotNull();
        assertThat(getConfiguredTarget("//hfp:foo")).isNotNull();
    }

    @Test
    public void cannotSelectOnForcePythonFlagsWithoutOldApi() throws Exception {
        useConfiguration("--incompatible_remove_old_python_version_api=true");
        // error:
        // build file:
        checkError("fp", "foo", "option 'force_python' cannot be used in a config_setting", PythonVersionSelectTest.makeFooThatSelectsOnFlag("force_python", "PY2"));
        // error:
        // build file:
        checkError("hfp", "foo", "option 'host_force_python' cannot be used in a config_setting", PythonVersionSelectTest.makeFooThatSelectsOnFlag("host_force_python", "PY2"));
    }

    /**
     * Tests the python_version selectable target, which is the canonical way of determining the
     * Python version from within a select().
     */
    @Test
    public void selectOnPythonVersionTarget() throws Exception {
        // getRuleContext() doesn't populate the information needed to resolve select()s, and
        // ConfiguredTarget doesn't allow us to test an end-to-end view of the behavior of a select().
        // So this test has the select() control srcs and asserts on which one's in the files to build.
        Artifact py2 = getSourceArtifact("pkg/py2");
        Artifact py3 = getSourceArtifact("pkg/py3");
        scratch.file("pkg/BUILD", "sh_binary(", "    name = 'foo',", "    srcs = select({", (("        '" + (TestConstants.TOOLS_REPOSITORY)) + "//tools/python:PY2': ['py2'],"), (("        '" + (TestConstants.TOOLS_REPOSITORY)) + "//tools/python:PY3': ['py3'],"), "    }),", ")");
        // Neither --python_version nor --force_python, use default value.
        doTestSelectOnPythonVersionTarget(py2, "--incompatible_py3_is_default=false");
        // PythonConfiguration has a validation check requiring that the new transition semantics be
        // enabled before we're allowed to set the default to PY3.
        doTestSelectOnPythonVersionTarget(py3, "--incompatible_py3_is_default=true", "--incompatible_allow_python_version_transitions=true");
        // No --python_version, trust --force_python.
        doTestSelectOnPythonVersionTarget(py2, "--force_python=PY2");
        doTestSelectOnPythonVersionTarget(py3, "--force_python=PY3");
        // --python_version overrides --force_python.
        doTestSelectOnPythonVersionTarget(py2, "--python_version=PY2");
        doTestSelectOnPythonVersionTarget(py2, "--python_version=PY2", "--force_python=PY2");
        doTestSelectOnPythonVersionTarget(py2, "--python_version=PY2", "--force_python=PY3");
        doTestSelectOnPythonVersionTarget(py3, "--python_version=PY3");
        doTestSelectOnPythonVersionTarget(py3, "--python_version=PY3", "--force_python=PY2");
        doTestSelectOnPythonVersionTarget(py3, "--python_version=PY3", "--force_python=PY3");
    }
}

