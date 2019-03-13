/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.unix;


import Symlinks.FOLLOW;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.SymlinkAwareFileSystemTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link com.google.devtools.build.lib.unix.UnixFileSystem} class.
 */
public class UnixFileSystemTest extends SymlinkAwareFileSystemTest {
    // Most tests are just inherited from FileSystemTest.
    @Test
    public void testCircularSymlinkFound() throws Exception {
        Path linkA = absolutize("link-a");
        Path linkB = absolutize("link-b");
        linkA.createSymbolicLink(linkB);
        linkB.createSymbolicLink(linkA);
        assertThat(linkA.exists(FOLLOW)).isFalse();
        try {
            linkA.statIfFound(FOLLOW);
            Assert.fail();
        } catch (IOException expected) {
            // Expected.
        }
    }

    @Test
    public void testIsSpecialFile() throws Exception {
        Path regular = absolutize("regular");
        Path fifo = absolutize("fifo");
        FileSystemUtils.createEmptyFile(regular);
        NativePosixFiles.mkfifo(fifo.toString(), 511);
        assertThat(regular.isFile()).isTrue();
        assertThat(regular.isSpecialFile()).isFalse();
        assertThat(regular.stat().isFile()).isTrue();
        assertThat(regular.stat().isSpecialFile()).isFalse();
        assertThat(fifo.isFile()).isTrue();
        assertThat(fifo.isSpecialFile()).isTrue();
        assertThat(fifo.stat().isFile()).isTrue();
        assertThat(fifo.stat().isSpecialFile()).isTrue();
    }
}

