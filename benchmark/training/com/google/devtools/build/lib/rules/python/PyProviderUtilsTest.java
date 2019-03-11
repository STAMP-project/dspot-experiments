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


import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PyProviderUtils}.
 */
@RunWith(JUnit4.class)
public class PyProviderUtilsTest extends BuildViewTestCase {
    @Test
    public void getAndHasModernProvider_Present() throws Exception {
        // 
        declareTargetWithImplementation("return [PyInfo(transitive_sources=depset([]))]");
        assertThat(PyProviderUtils.hasModernProvider(getTarget())).isTrue();
        assertThat(PyProviderUtils.getModernProvider(getTarget())).isNotNull();
    }

    @Test
    public void getAndHasModernProvider_Absent() throws Exception {
        // 
        declareTargetWithImplementation("return []");
        assertThat(PyProviderUtils.hasModernProvider(getTarget())).isFalse();
        assertThat(PyProviderUtils.getModernProvider(getTarget())).isNull();
    }

    @Test
    public void getAndHasLegacyProvider_Present() throws Exception {
        // 
        declareTargetWithImplementation("return struct(py=struct())");// Accessor doesn't actually check fields

        assertThat(PyProviderUtils.hasLegacyProvider(getTarget())).isTrue();
        assertThat(PyProviderUtils.getLegacyProvider(getTarget())).isNotNull();
    }

    @Test
    public void getAndHasLegacyProvider_Absent() throws Exception {
        // 
        declareTargetWithImplementation("return []");
        assertThat(PyProviderUtils.hasLegacyProvider(getTarget())).isFalse();
        PyProviderUtilsTest.assertThrowsEvalExceptionContaining(() -> PyProviderUtils.getLegacyProvider(getTarget()), "Target does not have 'py' provider");
    }

    @Test
    public void getLegacyProvider_NotAStruct() throws Exception {
        // 
        declareTargetWithImplementation("return struct(py=123)");
        PyProviderUtilsTest.assertThrowsEvalExceptionContaining(() -> PyProviderUtils.getLegacyProvider(getTarget()), "'py' provider should be a struct");
    }

    private static final String TRANSITIVE_SOURCES_SETUP_CODE = PyProviderUtilsTest.join("afile = ctx.actions.declare_file('a.py')", "ctx.actions.write(output=afile, content='a')", "bfile = ctx.actions.declare_file('b.py')", "ctx.actions.write(output=bfile, content='b')", "modern_info = PyInfo(transitive_sources = depset(direct=[afile]))", "legacy_info = struct(transitive_sources = depset(direct=[bfile]))");

    @Test
    public void getTransitiveSources_ModernProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.TRANSITIVE_SOURCES_SETUP_CODE, "return [modern_info]");
        assertThat(PyProviderUtils.getTransitiveSources(getTarget())).containsExactly(getBinArtifact("a.py", getTarget()));
    }

    @Test
    public void getTransitiveSources_LegacyProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.TRANSITIVE_SOURCES_SETUP_CODE, "return struct(py=legacy_info)");
        assertThat(PyProviderUtils.getTransitiveSources(getTarget())).containsExactly(getBinArtifact("b.py", getTarget()));
    }

    @Test
    public void getTransitiveSources_BothProviders() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.TRANSITIVE_SOURCES_SETUP_CODE, "return struct(py=legacy_info, providers=[modern_info])");
        assertThat(PyProviderUtils.getTransitiveSources(getTarget())).containsExactly(getBinArtifact("a.py", getTarget()));
    }

    @Test
    public void getTransitiveSources_NoProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.TRANSITIVE_SOURCES_SETUP_CODE, "return [DefaultInfo(files=depset(direct=[afile]))]");
        assertThat(PyProviderUtils.getTransitiveSources(getTarget())).containsExactly(getBinArtifact("a.py", getTarget()));
    }

    private static final String USES_SHARED_LIBRARIES_SETUP_CODE = PyProviderUtilsTest.join("modern_info = PyInfo(transitive_sources = depset([]), uses_shared_libraries=False)", "legacy_info = struct(transitive_sources = depset([]), uses_shared_libraries=True)");

    @Test
    public void getUsesSharedLibraries_ModernProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.USES_SHARED_LIBRARIES_SETUP_CODE, "return [modern_info]");
        assertThat(PyProviderUtils.getUsesSharedLibraries(getTarget())).isFalse();
    }

    @Test
    public void getUsesSharedLibraries_LegacyProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.USES_SHARED_LIBRARIES_SETUP_CODE, "return struct(py=legacy_info)");
        assertThat(PyProviderUtils.getUsesSharedLibraries(getTarget())).isTrue();
    }

    @Test
    public void getUsesSharedLibraries_BothProviders() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.USES_SHARED_LIBRARIES_SETUP_CODE, "return struct(py=legacy_info, providers=[modern_info])");
        assertThat(PyProviderUtils.getUsesSharedLibraries(getTarget())).isFalse();
    }

    @Test
    public void getUsesSharedLibraries_NoProvider() throws Exception {
        declareTargetWithImplementation("afile = ctx.actions.declare_file('a.so')", "ctx.actions.write(output=afile, content='a')", "return [DefaultInfo(files=depset(direct=[afile]))]");
        assertThat(PyProviderUtils.getUsesSharedLibraries(getTarget())).isTrue();
    }

    private static final String IMPORTS_SETUP_CODE = PyProviderUtilsTest.join("modern_info = PyInfo(transitive_sources = depset([]), imports = depset(direct=['abc']))", "legacy_info = struct(", "    transitive_sources = depset([]),", "    imports = depset(direct=['def']))");

    @Test
    public void getImports_ModernProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.IMPORTS_SETUP_CODE, "return [modern_info]");
        assertThat(PyProviderUtils.getImports(getTarget())).containsExactly("abc");
    }

    @Test
    public void getImports_LegacyProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.IMPORTS_SETUP_CODE, "return struct(py=legacy_info)");
        assertThat(PyProviderUtils.getImports(getTarget())).containsExactly("def");
    }

    @Test
    public void getImports_BothProviders() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.IMPORTS_SETUP_CODE, "return struct(py=legacy_info, providers=[modern_info])");
        assertThat(PyProviderUtils.getImports(getTarget())).containsExactly("abc");
    }

    @Test
    public void getImports_NoProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.IMPORTS_SETUP_CODE, "return []");
        assertThat(PyProviderUtils.getImports(getTarget())).isEmpty();
    }

    private static final String HAS_PY2_ONLY_SOURCES_SETUP_CODE = PyProviderUtilsTest.join("modern_info = PyInfo(transitive_sources = depset([]), has_py2_only_sources = False)", "legacy_info = struct(transitive_sources = depset([]), has_py2_only_sources = True)");

    @Test
    public void getHasPy2OnlySources_ModernProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY2_ONLY_SOURCES_SETUP_CODE, "return [modern_info]");
        assertThat(PyProviderUtils.getHasPy2OnlySources(getTarget())).isFalse();
    }

    @Test
    public void getHasPy2OnlySources_LegacyProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY2_ONLY_SOURCES_SETUP_CODE, "return struct(py=legacy_info)");
        assertThat(PyProviderUtils.getHasPy2OnlySources(getTarget())).isTrue();
    }

    @Test
    public void getHasPy2OnlySources_BothProviders() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY2_ONLY_SOURCES_SETUP_CODE, "return struct(py=legacy_info, providers=[modern_info])");
        assertThat(PyProviderUtils.getHasPy2OnlySources(getTarget())).isFalse();
    }

    @Test
    public void getHasPy2OnlySources_NoProvider() throws Exception {
        // 
        declareTargetWithImplementation("return []");
        assertThat(PyProviderUtils.getHasPy2OnlySources(getTarget())).isFalse();
    }

    private static final String HAS_PY3_ONLY_SOURCES_SETUP_CODE = PyProviderUtilsTest.join("modern_info = PyInfo(transitive_sources = depset([]), has_py3_only_sources = False)", "legacy_info = struct(transitive_sources = depset([]), has_py3_only_sources = True)");

    @Test
    public void getHasPy3OnlySources_ModernProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY3_ONLY_SOURCES_SETUP_CODE, "return [modern_info]");
        assertThat(PyProviderUtils.getHasPy3OnlySources(getTarget())).isFalse();
    }

    @Test
    public void getHasPy3OnlySources_LegacyProvider() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY3_ONLY_SOURCES_SETUP_CODE, "return struct(py=legacy_info)");
        assertThat(PyProviderUtils.getHasPy3OnlySources(getTarget())).isTrue();
    }

    @Test
    public void getHasPy3OnlySources_BothProviders() throws Exception {
        // 
        // 
        declareTargetWithImplementation(PyProviderUtilsTest.HAS_PY3_ONLY_SOURCES_SETUP_CODE, "return struct(py=legacy_info, providers=[modern_info])");
        assertThat(PyProviderUtils.getHasPy3OnlySources(getTarget())).isFalse();
    }

    @Test
    public void getHasPy3OnlySources_NoProvider() throws Exception {
        // 
        declareTargetWithImplementation("return []");
        assertThat(PyProviderUtils.getHasPy3OnlySources(getTarget())).isFalse();
    }
}

