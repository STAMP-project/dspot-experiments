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
package org.apache.druid.segment.loading;


import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.storage.hdfs.HdfsFileTimestampVersionFinder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


public class HdfsFileTimestampVersionFinderTest {
    private static MiniDFSCluster miniCluster;

    private static File hdfsTmpDir;

    private static Path filePath = new Path("/tmp/foo");

    private static Path perTestPath = new Path("/tmp/tmp2");

    private static String pathContents = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";

    private static byte[] pathByteContents = StringUtils.toUtf8(HdfsFileTimestampVersionFinderTest.pathContents);

    private static Configuration conf;

    private HdfsFileTimestampVersionFinder finder;

    @Test
    public void testSimpleLatestVersion() throws IOException, InterruptedException {
        final Path oldPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "555test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(oldPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(oldPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Thread.sleep(10);
        final Path newPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "666test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(newPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(newPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Assert.assertEquals(newPath.toString(), finder.getLatestVersion(oldPath.toUri(), Pattern.compile(".*")).getPath());
    }

    @Test
    public void testAlreadyLatestVersion() throws IOException, InterruptedException {
        final Path oldPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "555test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(oldPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(oldPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Thread.sleep(10);
        final Path newPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "666test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(newPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(newPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Assert.assertEquals(newPath.toString(), finder.getLatestVersion(newPath.toUri(), Pattern.compile(".*")).getPath());
    }

    @Test
    public void testNoLatestVersion() throws IOException {
        final Path oldPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "555test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(oldPath));
        Assert.assertNull(finder.getLatestVersion(oldPath.toUri(), Pattern.compile(".*")));
    }

    @Test
    public void testSimpleLatestVersionInDir() throws IOException, InterruptedException {
        final Path oldPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "555test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(oldPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(oldPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Thread.sleep(10);
        final Path newPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "666test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(newPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(newPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Assert.assertEquals(newPath.toString(), finder.getLatestVersion(HdfsFileTimestampVersionFinderTest.perTestPath.toUri(), Pattern.compile(".*test\\.txt")).getPath());
    }

    @Test
    public void testSkipMismatch() throws IOException, InterruptedException {
        final Path oldPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "555test.txt");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(oldPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(oldPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Thread.sleep(10);
        final Path newPath = new Path(HdfsFileTimestampVersionFinderTest.perTestPath, "666test.txt2");
        Assert.assertFalse(HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().exists(newPath));
        try (final OutputStream outputStream = HdfsFileTimestampVersionFinderTest.miniCluster.getFileSystem().create(newPath);final InputStream inputStream = new ByteArrayInputStream(HdfsFileTimestampVersionFinderTest.pathByteContents)) {
            ByteStreams.copy(inputStream, outputStream);
        }
        Assert.assertEquals(oldPath.toString(), finder.getLatestVersion(HdfsFileTimestampVersionFinderTest.perTestPath.toUri(), Pattern.compile(".*test\\.txt")).getPath());
    }
}

