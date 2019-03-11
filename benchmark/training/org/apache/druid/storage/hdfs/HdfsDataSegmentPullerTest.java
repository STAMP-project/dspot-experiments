/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.storage.hdfs;


import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.GZIPOutputStream;
import org.apache.druid.java.util.common.CompressionUtils;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;

import static java.nio.file.Files.readAllBytes;


/**
 *
 */
public class HdfsDataSegmentPullerTest {
    private static MiniDFSCluster miniCluster;

    private static File hdfsTmpDir;

    private static URI uriBase;

    private static Path filePath = new Path("/tmp/foo");

    private static Path perTestPath = new Path("/tmp/tmp2");

    private static String pathContents = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";

    private static byte[] pathByteContents = StringUtils.toUtf8(HdfsDataSegmentPullerTest.pathContents);

    private static Configuration conf;

    private HdfsDataSegmentPuller puller;

    @Test
    public void testZip() throws IOException, SegmentLoadingException {
        final File tmpDir = Files.createTempDir();
        final File tmpFile = File.createTempFile("zipContents", ".txt", tmpDir);
        final Path zipPath = new Path("/tmp/testZip.zip");
        final File outTmpDir = Files.createTempDir();
        final URI uri = URI.create(((HdfsDataSegmentPullerTest.uriBase.toString()) + zipPath));
        try (final OutputStream stream = new FileOutputStream(tmpFile)) {
            ByteStreams.copy(new ByteArrayInputStream(HdfsDataSegmentPullerTest.pathByteContents), stream);
        }
        Assert.assertTrue(tmpFile.exists());
        final File outFile = new File(outTmpDir, tmpFile.getName());
        outFile.delete();
        try (final OutputStream stream = HdfsDataSegmentPullerTest.miniCluster.getFileSystem().create(zipPath)) {
            CompressionUtils.zip(tmpDir, stream);
        }
        try {
            Assert.assertFalse(outFile.exists());
            puller.getSegmentFiles(new Path(uri), outTmpDir);
            Assert.assertTrue(outFile.exists());
            Assert.assertArrayEquals(HdfsDataSegmentPullerTest.pathByteContents, readAllBytes(outFile.toPath()));
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            if (outFile.exists()) {
                outFile.delete();
            }
            if (outTmpDir.exists()) {
                outTmpDir.delete();
            }
            if (tmpDir.exists()) {
                tmpDir.delete();
            }
        }
    }

    @Test
    public void testGZ() throws IOException, SegmentLoadingException {
        final Path zipPath = new Path("/tmp/testZip.gz");
        final File outTmpDir = Files.createTempDir();
        final File outFile = new File(outTmpDir, "testZip");
        outFile.delete();
        final URI uri = URI.create(((HdfsDataSegmentPullerTest.uriBase.toString()) + zipPath));
        try (final OutputStream outputStream = HdfsDataSegmentPullerTest.miniCluster.getFileSystem().create(zipPath);final OutputStream gzStream = new GZIPOutputStream(outputStream);final InputStream inputStream = new ByteArrayInputStream(HdfsDataSegmentPullerTest.pathByteContents)) {
            ByteStreams.copy(inputStream, gzStream);
        }
        try {
            Assert.assertFalse(outFile.exists());
            puller.getSegmentFiles(new Path(uri), outTmpDir);
            Assert.assertTrue(outFile.exists());
            Assert.assertArrayEquals(HdfsDataSegmentPullerTest.pathByteContents, readAllBytes(outFile.toPath()));
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
            if (outTmpDir.exists()) {
                outTmpDir.delete();
            }
        }
    }

    @Test
    public void testDir() throws IOException, SegmentLoadingException {
        final Path zipPath = new Path(HdfsDataSegmentPullerTest.perTestPath, "test.txt");
        final File outTmpDir = Files.createTempDir();
        final File outFile = new File(outTmpDir, "test.txt");
        outFile.delete();
        final URI uri = URI.create(((HdfsDataSegmentPullerTest.uriBase.toString()) + (HdfsDataSegmentPullerTest.perTestPath)));
        try (final OutputStream outputStream = HdfsDataSegmentPullerTest.miniCluster.getFileSystem().create(zipPath);final InputStream inputStream = new ByteArrayInputStream(HdfsDataSegmentPullerTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        try {
            Assert.assertFalse(outFile.exists());
            puller.getSegmentFiles(new Path(uri), outTmpDir);
            Assert.assertTrue(outFile.exists());
            Assert.assertArrayEquals(HdfsDataSegmentPullerTest.pathByteContents, readAllBytes(outFile.toPath()));
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
            if (outTmpDir.exists()) {
                outTmpDir.delete();
            }
        }
    }
}

