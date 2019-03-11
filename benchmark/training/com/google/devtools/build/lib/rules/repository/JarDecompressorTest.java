/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.repository;


import DecompressorDescriptor.Builder;
import com.google.common.base.Optional;
import com.google.devtools.build.lib.bazel.repository.JarDecompressor;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests expanding external jars into external repositories.
 */
@RunWith(JUnit4.class)
public class JarDecompressorTest {
    private Builder jarDescriptorBuilder;

    private Builder srcjarDescriptorBuilder;

    private JarDecompressor decompressor;

    @Test
    public void testTargetsWithSources() throws Exception {
        Path outputDir = decompressor.decompressWithSrcjar(jarDescriptorBuilder.build(), Optional.fromNullable(srcjarDescriptorBuilder.build()));
        assertThat(outputDir.exists()).isTrue();
        assertThat(outputDir.getRelative("jar/foo.jar").exists()).isTrue();
        assertThat(outputDir.getRelative("jar/foo-sources.jar").exists()).isTrue();
        String buildContent = new String(FileSystemUtils.readContentAsLatin1(outputDir.getRelative("jar/BUILD.bazel")));
        assertThat(buildContent).contains("java_import");
        assertThat(buildContent).contains("srcjar = 'foo-sources.jar'");
        assertThat(buildContent).contains("filegroup");
    }

    @Test
    public void testTargetsWithoutSources() throws Exception {
        Path outputDir = decompressor.decompressWithSrcjar(jarDescriptorBuilder.build(), Optional.absent());
        assertThat(outputDir.exists()).isTrue();
        assertThat(outputDir.getRelative("jar/foo.jar").exists()).isTrue();
        assertThat(outputDir.getRelative("jar/foo-sources.jar").exists()).isFalse();
        String buildContent = new String(FileSystemUtils.readContentAsLatin1(outputDir.getRelative("jar/BUILD.bazel")));
        assertThat(buildContent).contains("java_import");
        assertThat(buildContent).doesNotContain("srcjar = 'foo-sources.jar'");
        assertThat(buildContent).contains("filegroup");
    }

    @Test
    public void testTargetIsSource() throws Exception {
        Path outputDir = decompressor.decompressWithSrcjar(srcjarDescriptorBuilder.build(), Optional.fromNullable(srcjarDescriptorBuilder.build()));
        assertThat(outputDir.exists()).isTrue();
        assertThat(outputDir.getRelative("jar/foo.jar").exists()).isFalse();
        assertThat(outputDir.getRelative("jar/foo-sources.jar").exists()).isTrue();
        String buildContent = new String(FileSystemUtils.readContentAsLatin1(outputDir.getRelative("jar/BUILD.bazel")));
        assertThat(buildContent).contains("java_import");
        assertThat(buildContent).contains("srcjar = 'foo-sources.jar'");
        assertThat(buildContent).contains("filegroup");
        assertThat(buildContent).contains("srcs = [\n        \'foo-sources.jar\',\n    ],");
    }

    // Note: WORKSPACE gen is not affected by presence or absence of Optional arg to
    // decompressWithSrcjar
    @Test
    public void testWorkspaceGen() throws Exception {
        Path outputDir = decompressor.decompressWithSrcjar(jarDescriptorBuilder.build(), Optional.absent());
        assertThat(outputDir.exists()).isTrue();
        String workspaceContent = new String(FileSystemUtils.readContentAsLatin1(outputDir.getRelative("WORKSPACE")));
        assertThat(workspaceContent).contains("workspace(name = \"tester\")");
    }
}

