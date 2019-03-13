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
package com.google.devtools.build.lib.worker;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.sandbox.SandboxHelpers.SandboxOutputs;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WorkerExecRoot}.
 */
@RunWith(JUnit4.class)
public class WorkerExecRootTest {
    private FileSystem fileSystem;

    private Path testRoot;

    private Path workspaceDir;

    private Path sandboxDir;

    private Path execRoot;

    @Test
    public void cleanFileSystem() throws Exception {
        Path workerSh = workspaceDir.getRelative("worker.sh");
        FileSystemUtils.writeContentAsLatin1(workerSh, "#!/bin/bash");
        WorkerExecRoot workerExecRoot = new WorkerExecRoot(execRoot, ImmutableMap.of(PathFragment.create("worker.sh"), workerSh), SandboxOutputs.create(ImmutableSet.of(PathFragment.create("very/output.txt")), ImmutableSet.of()), ImmutableSet.of(PathFragment.create("worker.sh")));
        workerExecRoot.createFileSystem();
        // Pretend to do some work inside the execRoot.
        execRoot.getRelative("tempdir").createDirectory();
        FileSystemUtils.createEmptyFile(execRoot.getRelative("very/output.txt"));
        FileSystemUtils.createEmptyFile(execRoot.getRelative("temp.txt"));
        // Modify the worker.sh so that we're able to detect whether it gets rewritten or not.
        FileSystemUtils.writeContentAsLatin1(workerSh, "#!/bin/sh");
        // Reuse the same execRoot.
        workerExecRoot.createFileSystem();
        assertThat(execRoot.getRelative("worker.sh").exists()).isTrue();
        assertThat(FileSystemUtils.readContent(execRoot.getRelative("worker.sh"), Charset.defaultCharset())).isEqualTo("#!/bin/sh");
        assertThat(execRoot.getRelative("tempdir").exists()).isFalse();
        assertThat(execRoot.getRelative("very/output.txt").exists()).isFalse();
        assertThat(execRoot.getRelative("temp.txt").exists()).isFalse();
    }
}

