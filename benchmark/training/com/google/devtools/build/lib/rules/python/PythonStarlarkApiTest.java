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


import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the ability of Starlark code to interact with the Python rules via the py provider.
 */
@RunWith(JUnit4.class)
public class PythonStarlarkApiTest extends BuildViewTestCase {
    @Test
    public void librarySandwich_LegacyProviderAllowed() throws Exception {
        /* legacyProviderAllowed= */
        doLibrarySandwichTest(true);
    }

    @Test
    public void librarySandwich_LegacyProviderDisallowed() throws Exception {
        /* legacyProviderAllowed= */
        doLibrarySandwichTest(false);
    }

    @Test
    public void runtimeSandwich() throws Exception {
        scratch.file("pkg/rules.bzl", "def _userruntime_impl(ctx):", "    info = ctx.attr.runtime[PyRuntimeInfo]", "    return [PyRuntimeInfo(", "        interpreter = ctx.file.interpreter,", "        files = depset(direct = ctx.files.files, transitive=[info.files]),", "        python_version = info.python_version)]", "", "userruntime = rule(", "    implementation = _userruntime_impl,", "    attrs = {", "        'runtime': attr.label(),", "        'interpreter': attr.label(allow_single_file=True),", "        'files': attr.label_list(allow_files=True),", "    },", ")");
        scratch.file("pkg/BUILD", "load(':rules.bzl', 'userruntime')", "py_runtime(", "    name = 'pyruntime',", "    interpreter = ':intr',", "    files = ['data.txt'],", "    python_version = 'PY2',", ")", "userruntime(", "    name = 'userruntime',", "    runtime = ':pyruntime',", "    interpreter = ':userintr',", "    files = ['userdata.txt'],", ")", "py_binary(", "    name = 'pybin',", "    srcs = ['pybin.py'],", ")");
        String pythonTopLabel = analysisMock.pySupport().createPythonTopEntryPoint(mockToolsConfig, "//pkg:userruntime");
        useConfiguration(("--python_top=" + pythonTopLabel));
        ConfiguredTarget target = getConfiguredTarget("//pkg:pybin");
        assertThat(collectRunfiles(target)).containsAllOf(getSourceArtifact("pkg/data.txt"), getSourceArtifact("pkg/userdata.txt"));
    }
}

