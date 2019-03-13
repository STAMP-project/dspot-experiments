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


import OutputGroupInfo.COMPILATION_PREREQUISITES;
import OutputGroupInfo.FILES_TO_COMPILE;
import PythonVersion.PY3;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.configuredtargets.FileConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code py_library}.
 */
@RunWith(JUnit4.class)
public class PyLibraryConfiguredTargetTest extends PyBaseConfiguredTargetTestBase {
    public PyLibraryConfiguredTargetTest() {
        super("py_library");
    }

    @Test
    public void canBuildWithIncompatibleSrcsVersionUnderNewSemantics() throws Exception {
        // See PyBaseConfiguredTargetTestBase for the analogous test under the old semantics, which
        // applies not just to py_library but also to py_binary and py_test.
        useConfiguration("--incompatible_allow_python_version_transitions=true", "--python_version=PY3");
        scratch.file("pkg/BUILD", "py_library(", "    name = 'foo',", "    srcs = [':foo.py'],", "    srcs_version = 'PY2ONLY')");
        // Under the new semantics, errors are only reported at the binary target, not the library, and
        // even then they'd be deferred to execution time, so there should be nothing wrong here.
        assertThat(view.hasErrors(getConfiguredTarget("//pkg:foo"))).isFalse();
    }

    @Test
    public void versionIs3IfSetByFlagUnderNewSemantics() throws Exception {
        // See PyBaseConfiguredTargetTestBase for the analogous test under the old semantics, which
        // applies not just to py_library but also to py_binary and py_test.
        PythonTestUtils.ensureDefaultIsPY2();
        useConfiguration("--incompatible_allow_python_version_transitions=true", "--python_version=PY3");
        scratch.file("pkg/BUILD", "py_library(", "    name = 'foo',", "    srcs = ['foo.py'])");
        assertThat(getPythonVersion(getConfiguredTarget("//pkg:foo"))).isEqualTo(PY3);
    }

    @Test
    public void filesToBuild() throws Exception {
        scratch.file("pkg/BUILD", "py_library(", "    name = 'foo',", "    srcs = ['foo.py'])");
        ConfiguredTarget target = getConfiguredTarget("//pkg:foo");
        FileConfiguredTarget srcFile = getFileConfiguredTarget("//pkg:foo.py");
        assertThat(getFilesToBuild(target)).containsExactly(srcFile.getArtifact());
    }

    @Test
    public void srcsCanContainRuleGeneratingPyAndNonpyFiles() throws Exception {
        // build file:
        scratchConfiguredTarget("pkg", "foo", "py_binary(", "    name = 'foo',", "    srcs = ['foo.py', ':bar'])", "genrule(", "    name = 'bar',", "    outs = ['bar.cc', 'bar.py'],", "    cmd = 'touch $(OUTS)')");
        assertNoEvents();
    }

    @Test
    public void whatIfSrcsContainsRuleGeneratingNoPyFiles() throws Exception {
        // In Bazel it's an error, in Blaze it's a warning.
        String[] lines = new String[]{ "py_binary(", "    name = 'foo',", "    srcs = ['foo.py', ':bar'])", "genrule(", "    name = 'bar',", "    outs = ['bar.cc'],", "    cmd = 'touch $(OUTS)')" };
        if (analysisMock.isThisBazel()) {
            // error:
            // build file:
            checkError("pkg", "foo", "'//pkg:bar' does not produce any py_binary srcs files", lines);
        } else {
            // warning:
            // build file:
            checkWarning("pkg", "foo", "rule '//pkg:bar' does not produce any Python source files", lines);
        }
    }

    @Test
    public void filesToCompile() throws Exception {
        ConfiguredTarget lib = // build file:
        scratchConfiguredTarget("pkg", "lib", "py_library(name = 'lib', srcs = ['lib.py'], deps = [':bar'])", "py_library(name = 'bar', srcs = ['bar.py'], deps = [':baz'])", "py_library(name = 'baz', srcs = ['baz.py'])");
        assertThat(ActionsTestUtil.baseNamesOf(getOutputGroup(lib, COMPILATION_PREREQUISITES))).isEqualTo("baz.py bar.py lib.py");
        // compilationPrerequisites should be included in filesToCompile.
        assertThat(ActionsTestUtil.baseNamesOf(getOutputGroup(lib, FILES_TO_COMPILE))).isEqualTo("baz.py bar.py lib.py");
    }
}

