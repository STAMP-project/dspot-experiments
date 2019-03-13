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
package com.google.devtools.build.lib.sandbox;


import Symlinks.NOFOLLOW;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.sandbox.SandboxHelpers.SandboxOutputs;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SymlinkedSandboxedSpawn}.
 */
@RunWith(JUnit4.class)
public class SymlinkedSandboxedSpawnTest {
    private Path workspaceDir;

    private Path sandboxDir;

    private Path execRoot;

    private Path outputsDir;

    @Test
    public void createFileSystem() throws Exception {
        Path helloTxt = workspaceDir.getRelative("hello.txt");
        FileSystemUtils.createEmptyFile(helloTxt);
        SymlinkedSandboxedSpawn symlinkedExecRoot = new SymlinkedSandboxedSpawn(sandboxDir, execRoot, ImmutableList.of("/bin/true"), ImmutableMap.of(), ImmutableMap.of(PathFragment.create("such/input.txt"), helloTxt), SandboxOutputs.create(ImmutableSet.of(PathFragment.create("very/output.txt")), ImmutableSet.of()), ImmutableSet.of(execRoot.getRelative("wow/writable")));
        symlinkedExecRoot.createFileSystem();
        assertThat(execRoot.getRelative("such/input.txt").isSymbolicLink()).isTrue();
        assertThat(execRoot.getRelative("such/input.txt").resolveSymbolicLinks()).isEqualTo(helloTxt);
        assertThat(execRoot.getRelative("very").isDirectory()).isTrue();
        assertThat(execRoot.getRelative("wow/writable").isDirectory()).isTrue();
    }

    @Test
    public void copyOutputs() throws Exception {
        // These tests are very simple because we just rely on
        // AbstractContainerizingSandboxedSpawnTest.testMoveOutputs to properly verify all corner cases.
        Path outputFile = execRoot.getRelative("very/output.txt");
        SymlinkedSandboxedSpawn symlinkedExecRoot = new SymlinkedSandboxedSpawn(sandboxDir, execRoot, ImmutableList.of("/bin/true"), ImmutableMap.of(), ImmutableMap.of(), SandboxOutputs.create(ImmutableSet.of(outputFile.relativeTo(execRoot)), ImmutableSet.of()), ImmutableSet.of());
        symlinkedExecRoot.createFileSystem();
        FileSystemUtils.createEmptyFile(outputFile);
        outputsDir.getRelative("very").createDirectory();
        symlinkedExecRoot.copyOutputs(outputsDir);
        assertThat(outputsDir.getRelative("very/output.txt").isFile(NOFOLLOW)).isTrue();
    }
}

