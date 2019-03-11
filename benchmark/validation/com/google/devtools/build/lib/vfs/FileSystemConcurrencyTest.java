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
package com.google.devtools.build.lib.vfs;


import Symlinks.NOFOLLOW;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * (Slow) tests of FileSystem under concurrency.
 *
 * These tests are nondeterministic but provide good coverage nonetheless.
 */
@RunWith(JUnit4.class)
public class FileSystemConcurrencyTest {
    Path workingDir;

    @Test
    public void testConcurrentSymlinkModifications() throws Exception {
        final Path xFile = workingDir.getRelative("file");
        FileSystemUtils.createEmptyFile(xFile);
        final Path xLinkToFile = workingDir.getRelative("link");
        // "Boxed" for pass-by-reference.
        final boolean[] run = new boolean[]{ true };
        final IOException[] exception = new IOException[]{ null };
        Thread createThread = new Thread() {
            @Override
            public void run() {
                while (run[0]) {
                    if (!(xLinkToFile.exists())) {
                        try {
                            xLinkToFile.createSymbolicLink(xFile);
                        } catch (IOException e) {
                            exception[0] = e;
                            return;
                        }
                    }
                } 
            }
        };
        Thread deleteThread = new Thread() {
            @Override
            public void run() {
                while (run[0]) {
                    if (xLinkToFile.exists(NOFOLLOW)) {
                        try {
                            xLinkToFile.delete();
                        } catch (IOException e) {
                            exception[0] = e;
                            return;
                        }
                    }
                } 
            }
        };
        createThread.start();
        deleteThread.start();
        Thread.sleep(1000);
        run[0] = false;
        createThread.join(0);
        deleteThread.join(0);
        if ((exception[0]) != null) {
            throw exception[0];
        }
    }
}

