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
package com.google.devtools.build.lib.skyframe;


import FsUtils.TEST_FILESYSTEM;
import FsUtils.TEST_ROOT;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.skyframe.serialization.testutils.FsUtils;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FileValue}.
 */
@RunWith(JUnit4.class)
public class FileValueTest {
    @Test
    public void testCodec() throws Exception {
        // Assume we have adequate coverage for FileStateValue serialization.
        new SerializationTester(new com.google.devtools.build.lib.actions.FileValue.RegularFileValue(FsUtils.TEST_ROOT, FileStateValue.NONEXISTENT_FILE_STATE_NODE), new com.google.devtools.build.lib.actions.FileValue.DifferentRealPathFileValueWithStoredChain(FsUtils.TEST_ROOT, FileStateValue.DIRECTORY_FILE_STATE_NODE, ImmutableList.of(TEST_ROOT)), new com.google.devtools.build.lib.actions.FileValue.DifferentRealPathFileValueWithoutStoredChain(FsUtils.TEST_ROOT, FileStateValue.DIRECTORY_FILE_STATE_NODE), new com.google.devtools.build.lib.actions.FileValue.SymlinkFileValueWithStoredChain(FsUtils.TEST_ROOT, /* size= */
        /* digest= */
        /* contentsProxy= */
        new FileStateValue.RegularFileStateValue(100, new byte[]{ 1, 2, 3, 4, 5 }, null), ImmutableList.of(TEST_ROOT), PathFragment.create("somewhere/else")), new com.google.devtools.build.lib.actions.FileValue.SymlinkFileValueWithoutStoredChain(FsUtils.TEST_ROOT, /* size= */
        /* digest= */
        /* contentsProxy= */
        new FileStateValue.RegularFileStateValue(100, new byte[]{ 1, 2, 3, 4, 5 }, null), PathFragment.create("somewhere/else"))).addDependency(FileSystem.class, TEST_FILESYSTEM).runTests();
    }
}

