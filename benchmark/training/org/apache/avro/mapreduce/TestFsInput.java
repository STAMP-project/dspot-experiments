/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro.mapreduce;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.avro.mapred.FsInput;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestFsInput {
    private static File file;

    private static final String FILE_CONTENTS = "abcdefghijklmnopqrstuvwxyz";

    private Configuration conf;

    private FsInput fsInput;

    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    @Test
    public void testConfigurationConstructor() throws Exception {
        try (FsInput in = new FsInput(new Path(TestFsInput.file.getPath()), conf)) {
            int expectedByteCount = 1;
            byte[] readBytes = new byte[expectedByteCount];
            int actualByteCount = fsInput.read(readBytes, 0, expectedByteCount);
            Assert.assertThat(actualByteCount, Matchers.is(Matchers.equalTo(expectedByteCount)));
        }
    }

    @Test
    public void testFileSystemConstructor() throws Exception {
        Path path = new Path(TestFsInput.file.getPath());
        FileSystem fs = path.getFileSystem(conf);
        try (FsInput in = new FsInput(path, fs)) {
            int expectedByteCount = 1;
            byte[] readBytes = new byte[expectedByteCount];
            int actualByteCount = fsInput.read(readBytes, 0, expectedByteCount);
            Assert.assertThat(actualByteCount, Matchers.is(Matchers.equalTo(expectedByteCount)));
        }
    }

    @Test
    public void testLength() throws IOException {
        Assert.assertEquals(fsInput.length(), TestFsInput.FILE_CONTENTS.length());
    }

    @Test
    public void testRead() throws Exception {
        byte[] expectedBytes = TestFsInput.FILE_CONTENTS.getBytes(Charset.forName("UTF-8"));
        byte[] actualBytes = new byte[expectedBytes.length];
        int actualByteCount = fsInput.read(actualBytes, 0, actualBytes.length);
        Assert.assertThat(actualBytes, Matchers.is(Matchers.equalTo(expectedBytes)));
        Assert.assertThat(actualByteCount, Matchers.is(Matchers.equalTo(expectedBytes.length)));
    }

    @Test
    public void testSeek() throws Exception {
        int seekPos = (TestFsInput.FILE_CONTENTS.length()) / 2;
        byte[] fileContentBytes = TestFsInput.FILE_CONTENTS.getBytes(Charset.forName("UTF-8"));
        byte expectedByte = fileContentBytes[seekPos];
        fsInput.seek(seekPos);
        byte[] readBytes = new byte[1];
        fsInput.read(readBytes, 0, 1);
        byte actualByte = readBytes[0];
        Assert.assertThat(actualByte, Matchers.is(Matchers.equalTo(expectedByte)));
    }

    @Test
    public void testTell() throws Exception {
        long expectedTellPos = (TestFsInput.FILE_CONTENTS.length()) / 2;
        fsInput.seek(expectedTellPos);
        long actualTellPos = fsInput.tell();
        Assert.assertThat(actualTellPos, Matchers.is(Matchers.equalTo(expectedTellPos)));
    }
}

