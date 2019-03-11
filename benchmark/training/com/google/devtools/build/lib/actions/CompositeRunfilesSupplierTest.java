/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.actions;


import ArtifactPathResolver.IDENTITY;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for CompositeRunfilesSupplier
 */
@RunWith(JUnit4.class)
public class CompositeRunfilesSupplierTest {
    private RunfilesSupplier mockFirst;

    private RunfilesSupplier mockSecond;

    private Path execRoot;

    private ArtifactRoot rootDir;

    @Test
    public void testGetArtifactsReturnsCombinedArtifacts() {
        Mockito.when(mockFirst.getArtifacts()).thenReturn(CompositeRunfilesSupplierTest.mkArtifacts(rootDir, "first", "shared"));
        Mockito.when(mockSecond.getArtifacts()).thenReturn(CompositeRunfilesSupplierTest.mkArtifacts(rootDir, "second", "shared"));
        CompositeRunfilesSupplier underTest = new CompositeRunfilesSupplier(mockFirst, mockSecond);
        assertThat(underTest.getArtifacts()).containsExactlyElementsIn(CompositeRunfilesSupplierTest.mkArtifacts(rootDir, "first", "second", "shared"));
    }

    @Test
    public void testGetRunfilesDirsReturnsCombinedPaths() {
        PathFragment first = PathFragment.create("first");
        PathFragment second = PathFragment.create("second");
        PathFragment shared = PathFragment.create("shared");
        Mockito.when(mockFirst.getRunfilesDirs()).thenReturn(ImmutableSet.of(first, shared));
        Mockito.when(mockSecond.getRunfilesDirs()).thenReturn(ImmutableSet.of(second, shared));
        CompositeRunfilesSupplier underTest = new CompositeRunfilesSupplier(mockFirst, mockSecond);
        assertThat(underTest.getRunfilesDirs()).containsExactly(first, second, shared);
    }

    @Test
    public void testGetMappingsReturnsMappingsWithFirstPrecedenceOverSecond() throws IOException {
        PathFragment first = PathFragment.create("first");
        Map<PathFragment, Artifact> firstMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "first1", "first2");
        PathFragment second = PathFragment.create("second");
        Map<PathFragment, Artifact> secondMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "second1", "second2");
        PathFragment shared = PathFragment.create("shared");
        Map<PathFragment, Artifact> firstSharedMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "shared1", "shared2");
        Map<PathFragment, Artifact> secondSharedMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "lost1", "lost2");
        Mockito.when(mockFirst.getMappings(IDENTITY)).thenReturn(ImmutableMap.of(first, firstMappings, shared, firstSharedMappings));
        Mockito.when(mockSecond.getMappings(IDENTITY)).thenReturn(ImmutableMap.of(second, secondMappings, shared, secondSharedMappings));
        // We expect the mappings for shared added by mockSecond to be dropped.
        CompositeRunfilesSupplier underTest = new CompositeRunfilesSupplier(mockFirst, mockSecond);
        assertThat(underTest.getMappings(IDENTITY)).containsExactly(first, firstMappings, second, secondMappings, shared, firstSharedMappings);
    }

    @Test
    public void testGetMappingsViaListConstructorReturnsMappingsWithFirstPrecedenceOverSecond() throws IOException {
        PathFragment first = PathFragment.create("first");
        Map<PathFragment, Artifact> firstMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "first1", "first2");
        PathFragment second = PathFragment.create("second");
        Map<PathFragment, Artifact> secondMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "second1", "second2");
        PathFragment shared = PathFragment.create("shared");
        Map<PathFragment, Artifact> firstSharedMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "shared1", "shared2");
        Map<PathFragment, Artifact> secondSharedMappings = CompositeRunfilesSupplierTest.mkMappings(rootDir, "lost1", "lost2");
        Mockito.when(mockFirst.getMappings(IDENTITY)).thenReturn(ImmutableMap.of(first, firstMappings, shared, firstSharedMappings));
        Mockito.when(mockSecond.getMappings(IDENTITY)).thenReturn(ImmutableMap.of(second, secondMappings, shared, secondSharedMappings));
        // We expect the mappings for shared added by mockSecond to be dropped.
        CompositeRunfilesSupplier underTest = new CompositeRunfilesSupplier(ImmutableList.of(mockFirst, mockSecond));
        assertThat(underTest.getMappings(IDENTITY)).containsExactly(first, firstMappings, second, secondMappings, shared, firstSharedMappings);
    }
}

