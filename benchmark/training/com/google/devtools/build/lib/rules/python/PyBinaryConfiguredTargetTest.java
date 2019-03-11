/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
import com.google.devtools.build.lib.analysis.configuredtargets.FileConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code py_binary}.
 */
// TODO(brandjon): Add tests for content of stub Python script (particularly for choosing python
// 2 or 3).
@RunWith(JUnit4.class)
public class PyBinaryConfiguredTargetTest extends PyExecutableConfiguredTargetTestBase {
    public PyBinaryConfiguredTargetTest() {
        super("py_binary");
    }

    @Test
    public void python2WithPy3SrcsVersionDependency_OldSemantics() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expect errors

        useConfiguration("--incompatible_allow_python_version_transitions=false");
        declareBinDependingOnLibWithVersions("PY2", "PY3");
        assertThat(view.hasErrors(getConfiguredTarget("//pkg:bin"))).isTrue();
        assertContainsEvent("//pkg:lib: Rule '//pkg:lib' can only be used with Python 3");
    }

    @Test
    public void python2WithPy3SrcsVersionDependency_NewSemantics() throws Exception {
        useConfiguration("--incompatible_allow_python_version_transitions=true");
        declareBinDependingOnLibWithVersions("PY2", "PY3");
        assertThat(getPyExecutableDeferredError("//pkg:bin")).contains("being built for Python 2 but (transitively) includes Python 3-only sources");
    }

    @Test
    public void python2WithPy3OnlySrcsVersionDependency_OldSemantics() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expect errors

        useConfiguration("--incompatible_allow_python_version_transitions=false");
        declareBinDependingOnLibWithVersions("PY2", "PY3ONLY");
        assertThat(view.hasErrors(getConfiguredTarget("//pkg:bin"))).isTrue();
        assertContainsEvent("//pkg:lib: Rule '//pkg:lib' can only be used with Python 3");
    }

    @Test
    public void python2WithPy3OnlySrcsVersionDependency_NewSemantics() throws Exception {
        useConfiguration("--incompatible_allow_python_version_transitions=true");
        declareBinDependingOnLibWithVersions("PY2", "PY3ONLY");
        assertThat(getPyExecutableDeferredError("//pkg:bin")).contains("being built for Python 2 but (transitively) includes Python 3-only sources");
    }

    @Test
    public void python3WithPy2OnlySrcsVersionDependency_OldSemantics() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);// expect errors

        useConfiguration("--incompatible_allow_python_version_transitions=false");
        declareBinDependingOnLibWithVersions("PY3", "PY2ONLY");
        assertThat(view.hasErrors(getConfiguredTarget("//pkg:bin"))).isTrue();
        assertContainsEvent("//pkg:lib: Rule '//pkg:lib' can only be used with Python 2");
    }

    @Test
    public void python3WithPy2OnlySrcsVersionDependency_NewSemantics() throws Exception {
        useConfiguration("--incompatible_allow_python_version_transitions=true");
        declareBinDependingOnLibWithVersions("PY3", "PY2ONLY");
        assertThat(getPyExecutableDeferredError("//pkg:bin")).contains("being built for Python 3 but (transitively) includes Python 2-only sources");
    }

    @Test
    public void filesToBuild() throws Exception {
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'foo',", "    srcs = ['foo.py'])");
        ConfiguredTarget target = getOkPyTarget("//pkg:foo");
        FileConfiguredTarget srcFile = getFileConfiguredTarget("//pkg:foo.py");
        assertThat(getFilesToBuild(target)).containsExactly(getExecutable(target), srcFile.getArtifact());
        assertThat(getExecutable(target).getExecPath().getPathString()).containsMatch(((TestConstants.PRODUCT_NAME) + "-out/.*/bin/pkg/foo"));
    }

    @Test
    public void srcsIsMandatory() throws Exception {
        // This case is somewhat dominated by the test that the default main must be in srcs, but the
        // error message is different here.
        // error:
        // build file:
        checkError("pkg", "foo", "missing value for mandatory attribute 'srcs'", "py_binary(", "    name = 'foo',", "    deps = [':bar'])", "py_library(", "    name = 'bar',", "    srcs = ['bar.py'])");
    }

    @Test
    public void defaultMainMustBeInSrcs() throws Exception {
        // error:
        // build file:
        checkError("pkg", "app", "corresponding default 'app.py' does not appear", "py_binary(", "    name = 'app',", "    srcs = ['foo.py', 'bar.py'])");
    }

    @Test
    public void explicitMain() throws Exception {
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'foo',", "    main = 'foo.py',", "    srcs = ['foo.py', 'bar.py'])");
        getOkPyTarget("//pkg:foo");// should not fail

    }

    @Test
    public void explicitMainMustBeInSrcs() throws Exception {
        // error:
        // build file:
        checkError("pkg", "foo", "could not find 'foo.py'", "py_binary(", "    name = 'foo',", "    main = 'foo.py',", "    srcs = ['bar.py', 'baz.py'])");
    }

    @Test
    public void defaultMainCannotBeAmbiguous() throws Exception {
        scratch.file("pkg1/BUILD", "py_binary(", "    name = 'foo',", "    srcs = ['bar.py'])");
        // error:
        // build file:
        checkError("pkg2", "bar", ("default main file name 'bar.py' matches multiple files.  Perhaps specify an explicit file " + "with 'main' attribute?  Matches were: 'pkg2/bar.py' and 'pkg1/bar.py'"), "py_binary(", "    name = 'bar',", "    srcs = ['bar.py', '//pkg1:bar.py'])");
    }

    @Test
    public void explicitMainCannotBeAmbiguous() throws Exception {
        scratch.file("pkg1/BUILD", "py_binary(", "    name = 'foo',", "    srcs = ['bar.py'])");
        // error:
        // build file:
        checkError("pkg2", "foo", ("file name 'bar.py' specified by 'main' attribute matches multiple files: e.g., " + "'pkg2/bar.py' and 'pkg1/bar.py'"), "py_binary(", "    name = 'foo',", "    main = 'bar.py',", "    srcs = ['bar.py', '//pkg1:bar.py'])");
    }

    @Test
    public void nameCannotEndInPy() throws Exception {
        // error:
        // build file:
        checkError("pkg", "foo.py", "name must not end in '.py'", "py_binary(", "    name = 'foo.py',", "    srcs = ['bar.py'])");
    }

    @Test
    public void defaultMainCanBeGenerated() throws Exception {
        scratch.file("pkg/BUILD", "genrule(", "    name = 'gen_py',", "    cmd = 'touch $(location foo.py)',", "    outs = ['foo.py'])", "py_binary(", "    name = 'foo',", "    srcs = [':gen_py'])");
        getOkPyTarget("//pkg:foo");// should not fail

    }

    @Test
    public void defaultMainCanHaveMultiplePathSegments() throws Exception {
        // Regression test for crash caused by use of getChild on a multi-segment rule name.
        scratch.file("pkg/BUILD", "py_binary(", "    name = 'foo/bar',", "    srcs = ['foo/bar.py'])");
        getOkPyTarget("//pkg:foo/bar");// should not fail

    }
}

