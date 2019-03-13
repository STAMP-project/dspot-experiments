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
package com.google.devtools.build.lib.exec;


import com.google.common.io.BaseEncoding;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.DigestOfDirectoryException;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests SingleBuildFileCache.
 */
@RunWith(JUnit4.class)
@TestSpec(size = Suite.SMALL_TESTS)
public class SingleBuildFileCacheTest {
    private static final String EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

    private FileSystem fs;

    private Map<String, Integer> calls;

    private Map<String, byte[]> md5Overrides;

    private SingleBuildFileCache underTest;

    @Test
    public void testNonExistentPath() throws Exception {
        ActionInput empty = ActionInputHelper.fromPath("/noexist");
        try {
            underTest.getMetadata(empty);
            Assert.fail("non existent file should raise exception");
        } catch (IOException expected) {
        }
    }

    @Test
    public void testDirectory() throws Exception {
        Path file = fs.getPath("/directory");
        file.createDirectory();
        ActionInput input = ActionInputHelper.fromPath(file.getPathString());
        try {
            underTest.getMetadata(input);
            Assert.fail("directory should raise exception");
        } catch (DigestOfDirectoryException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("Input is a directory: /directory");
        }
    }

    @Test
    public void testCache() throws Exception {
        ActionInput empty = ActionInputHelper.fromPath("/empty");
        underTest.getMetadata(empty).getDigest();
        assertThat(calls).containsKey("/empty");
        assertThat(((int) (calls.get("/empty")))).isEqualTo(1);
        underTest.getMetadata(empty).getDigest();
        assertThat(((int) (calls.get("/empty")))).isEqualTo(1);
    }

    @Test
    public void testBasic() throws Exception {
        ActionInput empty = ActionInputHelper.fromPath("/empty");
        assertThat(underTest.getMetadata(empty).getSize()).isEqualTo(0);
        byte[] digestBytes = underTest.getMetadata(empty).getDigest();
        ByteString digest = ByteString.copyFromUtf8(BaseEncoding.base16().lowerCase().encode(digestBytes));
        assertThat(digest.toStringUtf8()).isEqualTo(SingleBuildFileCacheTest.EMPTY_MD5);
    }

    @Test
    public void testUnreadableFileWhenFileSystemSupportsDigest() throws Exception {
        byte[] expectedDigestRaw = MessageDigest.getInstance("md5").digest("randomtext".getBytes(StandardCharsets.UTF_8));
        ByteString expectedDigest = ByteString.copyFrom(expectedDigestRaw);
        md5Overrides.put("/unreadable", expectedDigestRaw);
        ActionInput input = ActionInputHelper.fromPath("/unreadable");
        Path file = fs.getPath("/unreadable");
        FileSystemUtils.createEmptyFile(file);
        file.chmod(0);
        ByteString actualDigest = ByteString.copyFrom(underTest.getMetadata(input).getDigest());
        assertThat(actualDigest).isEqualTo(expectedDigest);
    }
}

