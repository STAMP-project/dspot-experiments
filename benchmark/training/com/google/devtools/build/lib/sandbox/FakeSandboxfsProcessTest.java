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
package com.google.devtools.build.lib.sandbox;


import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FakeSandboxfsProcess}.
 */
@RunWith(JUnit4.class)
public class FakeSandboxfsProcessTest extends BaseSandboxfsProcessTest {
    @Test
    public void testMount_NotADirectory() throws IOException {
        FileSystemUtils.createEmptyFile(tmpDir.getRelative("file"));
        IOException expected = MoreAsserts.assertThrows(IOException.class, () -> mount(tmpDir.getRelative("file")));
        assertThat(expected).hasMessageThat().matches(".*/file.*not a directory");
    }
}

