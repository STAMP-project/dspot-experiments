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


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link Path}.
 */
@RunWith(JUnit4.class)
public class PathGetParentTest {
    private FileSystem fs;

    private Path testRoot;

    @Test
    public void testAbsoluteRootHasNoParent() {
        assertThat(getParent("/")).isNull();
    }

    @Test
    public void testParentOfSimpleDirectory() {
        assertThat(getParent("/foo/bar").getPathString()).isEqualTo("/foo");
    }

    @Test
    public void testParentOfDotDotInMiddleOfPathname() {
        assertThat(getParent("/foo/../bar").getPathString()).isEqualTo("/");
    }

    @Test
    public void testGetPathDoesNormalizationWithoutIO() throws IOException {
        Path tmp = testRoot.getChild("tmp");
        Path tmpWiz = tmp.getChild("wiz");
        tmp.createDirectory();
        // ln -sf /tmp /tmp/wiz
        tmpWiz.createSymbolicLink(tmp);
        assertThat(tmp.getParentDirectory()).isEqualTo(testRoot);
        assertThat(tmpWiz.getParentDirectory()).isEqualTo(tmp);
        // Under UNIX, inode(/tmp/wiz/..) == inode(/).  However getPath() does not
        // perform I/O, only string operations, so it disagrees:
        assertThat(tmp.getRelative(PathFragment.create("wiz/.."))).isEqualTo(tmp);
    }
}

