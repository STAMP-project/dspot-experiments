/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.io;


import com.cloudera.oryx.common.OryxTest;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link IOUtils}.
 */
public final class IOUtilsTest extends OryxTest {
    private static final byte[] SOME_BYTES = new byte[]{ 1, 2, 3 };

    private static final byte[] NO_BYTES = new byte[]{  };

    @Test
    public void testDeleteRecursively() throws IOException {
        Path testDir = createTestDirs();
        IOUtils.deleteRecursively(testDir);
        Assert.assertFalse(Files.exists(testDir));
        Assert.assertFalse(Files.exists(testDir.resolve("subFile1")));
    }

    @Test
    public void testListFiles() throws IOException {
        Path testDir = createTestDirs();
        List<Path> files = IOUtils.listFiles(testDir, "*");
        Assert.assertEquals(2, files.size());
        OryxTest.assertContains(files, testDir.resolve("subFile1"));
        OryxTest.assertNotContains(files, testDir.resolve(".hidden"));
        OryxTest.assertContains(files, testDir.resolve("subDir1"));
    }

    @Test
    public void testListFiles2() throws IOException {
        Path testDir = createTestDirs();
        List<Path> files = IOUtils.listFiles(testDir, "");
        Assert.assertEquals(2, files.size());
        OryxTest.assertContains(files, testDir.resolve("subFile1"));
        OryxTest.assertNotContains(files, testDir.resolve(".hidden"));
        OryxTest.assertContains(files, testDir.resolve("subDir1"));
    }

    @Test
    public void testListSubdirs() throws IOException {
        Path testDir = createTestDirs();
        List<Path> files = IOUtils.listFiles(testDir, "*/*");
        Assert.assertEquals(2, files.size());
        OryxTest.assertContains(files, testDir.resolve("subDir1").resolve("subFile2"));
        OryxTest.assertContains(files, testDir.resolve("subDir1").resolve("subDir2"));
    }

    @Test
    public void testListSubdirs2() throws IOException {
        Path testDir = createTestDirs();
        List<Path> files = IOUtils.listFiles(testDir, "*/subFile*");
        Assert.assertEquals(1, files.size());
        OryxTest.assertContains(files, testDir.resolve("subDir1").resolve("subFile2"));
    }

    @Test
    public void testOrder() throws IOException {
        Path testDir = createTestDirs();
        List<Path> files = IOUtils.listFiles(testDir, "*/*");
        Assert.assertEquals(testDir.resolve("subDir1").resolve("subDir2"), files.get(0));
        Assert.assertEquals(testDir.resolve("subDir1").resolve("subFile2"), files.get(1));
    }

    @Test
    public void testChooseFreePort() throws IOException {
        int freePort = IOUtils.chooseFreePort();
        OryxTest.assertRange(freePort, 1024, 65535);
        try (ServerSocket socket = new ServerSocket(freePort, 0)) {
            Assert.assertEquals(freePort, socket.getLocalPort());
        }
    }

    @Test
    public void testDistinctFreePorts() throws IOException {
        // This whole thing probably won't work unless successive calls really do return
        // different ports instead of reusing free ephemeral ports.
        Set<Integer> ports = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ports.add(IOUtils.chooseFreePort());
        }
        Assert.assertEquals(10, ports.size());
    }

    @Test
    public void testCloseQuietly() {
        // Shouldn't throw
        IOUtils.closeQuietly(() -> {
            throw LoggingTest.DUMMY_EXCEPTION;
        });
    }
}

