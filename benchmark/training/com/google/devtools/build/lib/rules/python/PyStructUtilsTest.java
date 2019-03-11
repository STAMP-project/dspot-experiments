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


import Order.COMPILE_ORDER;
import Order.NAIVE_LINK_ORDER;
import PyStructUtils.HAS_PY2_ONLY_SOURCES;
import PyStructUtils.HAS_PY3_ONLY_SOURCES;
import PyStructUtils.IMPORTS;
import PyStructUtils.TRANSITIVE_SOURCES;
import PyStructUtils.USES_SHARED_LIBRARIES;
import StructProvider.STRUCT;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.packages.StructImpl;
import com.google.devtools.build.lib.syntax.SkylarkNestedSet;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PyStructUtils}.
 */
@RunWith(JUnit4.class)
public class PyStructUtilsTest extends FoundationTestCase {
    private Artifact dummyArtifact;

    @Test
    public void getTransitiveSources_Good() throws Exception {
        NestedSet<Artifact> sources = NestedSetBuilder.create(COMPILE_ORDER, dummyArtifact);
        StructImpl info = makeStruct(ImmutableMap.of(TRANSITIVE_SOURCES, SkylarkNestedSet.of(Artifact.class, sources)));
        assertThat(PyStructUtils.getTransitiveSources(info)).isSameAs(sources);
    }

    @Test
    public void getTransitiveSources_Missing() {
        StructImpl info = STRUCT.createEmpty(null);
        PyStructUtilsTest.assertHasMissingFieldMessage(() -> PyStructUtils.getTransitiveSources(info), "transitive_sources");
    }

    @Test
    public void getTransitiveSources_WrongType() {
        StructImpl info = makeStruct(ImmutableMap.of(TRANSITIVE_SOURCES, 123));
        PyStructUtilsTest.assertHasWrongTypeMessage(() -> PyStructUtils.getTransitiveSources(info), "transitive_sources", "depset of Files");
    }

    @Test
    public void getTransitiveSources_OrderMismatch() throws Exception {
        NestedSet<Artifact> sources = NestedSetBuilder.emptySet(NAIVE_LINK_ORDER);
        StructImpl info = makeStruct(ImmutableMap.of(TRANSITIVE_SOURCES, SkylarkNestedSet.of(Artifact.class, sources)));
        PyStructUtilsTest.assertThrowsEvalExceptionContaining(() -> PyStructUtils.getTransitiveSources(info), ("Incompatible depset order for 'transitive_sources': expected 'default' or 'postorder', " + "but got 'preorder'"));
    }

    @Test
    public void getUsesSharedLibraries_Good() throws Exception {
        StructImpl info = makeStruct(ImmutableMap.of(USES_SHARED_LIBRARIES, true));
        assertThat(PyStructUtils.getUsesSharedLibraries(info)).isTrue();
    }

    @Test
    public void getUsesSharedLibraries_Missing() throws Exception {
        StructImpl info = STRUCT.createEmpty(null);
        assertThat(PyStructUtils.getUsesSharedLibraries(info)).isFalse();
    }

    @Test
    public void getUsesSharedLibraries_WrongType() {
        StructImpl info = makeStruct(ImmutableMap.of(USES_SHARED_LIBRARIES, 123));
        PyStructUtilsTest.assertHasWrongTypeMessage(() -> PyStructUtils.getUsesSharedLibraries(info), "uses_shared_libraries", "boolean");
    }

    @Test
    public void getImports_Good() throws Exception {
        NestedSet<String> imports = NestedSetBuilder.create(COMPILE_ORDER, "abc");
        StructImpl info = makeStruct(ImmutableMap.of(IMPORTS, SkylarkNestedSet.of(String.class, imports)));
        assertThat(PyStructUtils.getImports(info)).isSameAs(imports);
    }

    @Test
    public void getImports_Missing() throws Exception {
        StructImpl info = STRUCT.createEmpty(null);
        PyStructUtilsTest.assertHasOrderAndContainsExactly(PyStructUtils.getImports(info), COMPILE_ORDER);
    }

    @Test
    public void getImports_WrongType() {
        StructImpl info = makeStruct(ImmutableMap.of(IMPORTS, 123));
        PyStructUtilsTest.assertHasWrongTypeMessage(() -> PyStructUtils.getImports(info), "imports", "depset of strings");
    }

    @Test
    public void getHasPy2OnlySources_Good() throws Exception {
        StructImpl info = makeStruct(ImmutableMap.of(HAS_PY2_ONLY_SOURCES, true));
        assertThat(PyStructUtils.getHasPy2OnlySources(info)).isTrue();
    }

    @Test
    public void getHasPy2OnlySources_Missing() throws Exception {
        StructImpl info = STRUCT.createEmpty(null);
        assertThat(PyStructUtils.getHasPy2OnlySources(info)).isFalse();
    }

    @Test
    public void getHasPy2OnlySources_WrongType() {
        StructImpl info = makeStruct(ImmutableMap.of(HAS_PY2_ONLY_SOURCES, 123));
        PyStructUtilsTest.assertHasWrongTypeMessage(() -> PyStructUtils.getHasPy2OnlySources(info), "has_py2_only_sources", "boolean");
    }

    @Test
    public void getHasPy3OnlySources_Good() throws Exception {
        StructImpl info = makeStruct(ImmutableMap.of(HAS_PY3_ONLY_SOURCES, true));
        assertThat(PyStructUtils.getHasPy3OnlySources(info)).isTrue();
    }

    @Test
    public void getHasPy3OnlySources_Missing() throws Exception {
        StructImpl info = STRUCT.createEmpty(null);
        assertThat(PyStructUtils.getHasPy3OnlySources(info)).isFalse();
    }

    @Test
    public void getHasPy3OnlySources_WrongType() {
        StructImpl info = makeStruct(ImmutableMap.of(HAS_PY3_ONLY_SOURCES, 123));
        PyStructUtilsTest.assertHasWrongTypeMessage(() -> PyStructUtils.getHasPy3OnlySources(info), "has_py3_only_sources", "boolean");
    }

    /**
     * Checks values set by the builder.
     */
    @Test
    public void builder() throws Exception {
        NestedSet<Artifact> sources = NestedSetBuilder.create(COMPILE_ORDER, dummyArtifact);
        NestedSet<String> imports = NestedSetBuilder.create(COMPILE_ORDER, "abc");
        StructImpl info = PyStructUtils.builder().setTransitiveSources(sources).setUsesSharedLibraries(true).setImports(imports).setHasPy2OnlySources(true).setHasPy3OnlySources(true).build();
        // Assert using struct operations, not PyStructUtils accessors, which aren't necessarily trusted
        // to be correct.
        PyStructUtilsTest.assertHasOrderAndContainsExactly(getSet(Artifact.class), COMPILE_ORDER, dummyArtifact);
        assertThat(((Boolean) (info.getValue(USES_SHARED_LIBRARIES)))).isTrue();
        PyStructUtilsTest.assertHasOrderAndContainsExactly(getSet(String.class), COMPILE_ORDER, "abc");
        assertThat(((Boolean) (info.getValue(HAS_PY2_ONLY_SOURCES)))).isTrue();
        assertThat(((Boolean) (info.getValue(HAS_PY3_ONLY_SOURCES)))).isTrue();
    }

    /**
     * Checks the defaults set by the builder.
     */
    @Test
    public void builderDefaults() throws Exception {
        // transitive_sources is mandatory, so create a dummy value but no need to assert on it.
        NestedSet<Artifact> sources = NestedSetBuilder.create(COMPILE_ORDER, dummyArtifact);
        StructImpl info = PyStructUtils.builder().setTransitiveSources(sources).build();
        // Assert using struct operations, not PyStructUtils accessors, which aren't necessarily trusted
        // to be correct.
        assertThat(((Boolean) (info.getValue(USES_SHARED_LIBRARIES)))).isFalse();
        PyStructUtilsTest.assertHasOrderAndContainsExactly(getSet(String.class), COMPILE_ORDER);
        assertThat(((Boolean) (info.getValue(HAS_PY2_ONLY_SOURCES)))).isFalse();
        assertThat(((Boolean) (info.getValue(HAS_PY3_ONLY_SOURCES)))).isFalse();
    }
}

