/**
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.maven;


import LibraryScope.COMPILE;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.loader.tools.Library;
import org.springframework.boot.loader.tools.LibraryCallback;

import static org.mockito.Mockito.verify;


/**
 * Tests for {@link ArtifactsLibraries}.
 *
 * @author Phillip Webb
 */
public class ArtifactsLibrariesTests {
    @Mock
    private Artifact artifact;

    @Mock
    private ArtifactHandler artifactHandler;

    private Set<Artifact> artifacts;

    private File file = new File(".");

    private ArtifactsLibraries libs;

    @Mock
    private LibraryCallback callback;

    @Captor
    private ArgumentCaptor<Library> libraryCaptor;

    @Test
    public void callbackForJars() throws Exception {
        BDDMockito.given(this.artifact.getType()).willReturn("jar");
        BDDMockito.given(this.artifact.getScope()).willReturn("compile");
        this.libs.doWithLibraries(this.callback);
        verify(this.callback).library(this.libraryCaptor.capture());
        Library library = this.libraryCaptor.getValue();
        assertThat(library.getFile()).isEqualTo(this.file);
        assertThat(library.getScope()).isEqualTo(COMPILE);
        assertThat(library.isUnpackRequired()).isFalse();
    }

    @Test
    public void callbackWithUnpack() throws Exception {
        BDDMockito.given(this.artifact.getGroupId()).willReturn("gid");
        BDDMockito.given(this.artifact.getArtifactId()).willReturn("aid");
        BDDMockito.given(this.artifact.getType()).willReturn("jar");
        BDDMockito.given(this.artifact.getScope()).willReturn("compile");
        Dependency unpack = new Dependency();
        unpack.setGroupId("gid");
        unpack.setArtifactId("aid");
        this.libs = new ArtifactsLibraries(this.artifacts, Collections.singleton(unpack), Mockito.mock(Log.class));
        this.libs.doWithLibraries(this.callback);
        verify(this.callback).library(this.libraryCaptor.capture());
        assertThat(this.libraryCaptor.getValue().isUnpackRequired()).isTrue();
    }

    @Test
    public void renamesDuplicates() throws Exception {
        Artifact artifact1 = Mockito.mock(Artifact.class);
        Artifact artifact2 = Mockito.mock(Artifact.class);
        BDDMockito.given(artifact1.getType()).willReturn("jar");
        BDDMockito.given(artifact1.getScope()).willReturn("compile");
        BDDMockito.given(artifact1.getGroupId()).willReturn("g1");
        BDDMockito.given(artifact1.getArtifactId()).willReturn("artifact");
        BDDMockito.given(artifact1.getBaseVersion()).willReturn("1.0");
        BDDMockito.given(artifact1.getFile()).willReturn(new File("a"));
        BDDMockito.given(artifact1.getArtifactHandler()).willReturn(this.artifactHandler);
        BDDMockito.given(artifact2.getType()).willReturn("jar");
        BDDMockito.given(artifact2.getScope()).willReturn("compile");
        BDDMockito.given(artifact2.getGroupId()).willReturn("g2");
        BDDMockito.given(artifact2.getArtifactId()).willReturn("artifact");
        BDDMockito.given(artifact2.getBaseVersion()).willReturn("1.0");
        BDDMockito.given(artifact2.getFile()).willReturn(new File("a"));
        BDDMockito.given(artifact2.getArtifactHandler()).willReturn(this.artifactHandler);
        this.artifacts = new java.util.LinkedHashSet(Arrays.asList(artifact1, artifact2));
        this.libs = new ArtifactsLibraries(this.artifacts, null, Mockito.mock(Log.class));
        this.libs.doWithLibraries(this.callback);
        Mockito.verify(this.callback, Mockito.times(2)).library(this.libraryCaptor.capture());
        assertThat(this.libraryCaptor.getAllValues().get(0).getName()).isEqualTo("g1-artifact-1.0.jar");
        assertThat(this.libraryCaptor.getAllValues().get(1).getName()).isEqualTo("g2-artifact-1.0.jar");
    }
}

