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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.fs.openstackhadoop;


import FileSystem.WriteMode.NO_OVERWRITE;
import WriteMode.OVERWRITE;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for the Swift file system support.
 */
public class HadoopSwiftFileSystemITCase extends TestLogger {
    private static final String SERVICENAME = "privatecloud";

    private static final String CONTAINER = System.getenv("ARTIFACTS_OS_CONTAINER");

    private static final String TEST_DATA_DIR = "tests-" + (UUID.randomUUID());

    private static final String AUTH_URL = System.getenv("ARTIFACTS_OS_AUTH_URL");

    private static final String USERNAME = System.getenv("ARTIFACTS_OS_USERNAME");

    private static final String PASSWORD = System.getenv("ARTIFACTS_OS_PASSWORD");

    private static final String TENANT = System.getenv("ARTIFACTS_OS_TENANT");

    private static final String REGION = System.getenv("ARTIFACTS_OS_REGION");

    /**
     * Will be updated by {@link #checkCredentialsAndSetup()} if the test is not skipped.
     */
    private static boolean skipTest = true;

    @Test
    public void testSimpleFileWriteAndRead() throws Exception {
        final Configuration conf = HadoopSwiftFileSystemITCase.createConfiguration();
        final String testLine = "Hello Upload!";
        FileSystem.initialize(conf);
        final Path path = new Path((((((("swift://" + (HadoopSwiftFileSystemITCase.CONTAINER)) + '.') + (HadoopSwiftFileSystemITCase.SERVICENAME)) + '/') + (HadoopSwiftFileSystemITCase.TEST_DATA_DIR)) + "/test.txt"));
        final FileSystem fs = path.getFileSystem();
        try {
            try (FSDataOutputStream out = fs.create(path, OVERWRITE);OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                writer.write(testLine);
            }
            try (FSDataInputStream in = fs.open(path);InputStreamReader ir = new InputStreamReader(in, StandardCharsets.UTF_8);BufferedReader reader = new BufferedReader(ir)) {
                String line = reader.readLine();
                Assert.assertEquals(testLine, line);
            }
        } finally {
            fs.delete(path, false);
        }
    }

    @Test
    public void testDirectoryListing() throws Exception {
        final Configuration conf = HadoopSwiftFileSystemITCase.createConfiguration();
        FileSystem.initialize(conf);
        final Path directory = new Path((((((("swift://" + (HadoopSwiftFileSystemITCase.CONTAINER)) + '.') + (HadoopSwiftFileSystemITCase.SERVICENAME)) + '/') + (HadoopSwiftFileSystemITCase.TEST_DATA_DIR)) + "/testdir/"));
        final FileSystem fs = directory.getFileSystem();
        // directory must not yet exist
        Assert.assertFalse(fs.exists(directory));
        try {
            // create directory
            Assert.assertTrue(fs.mkdirs(directory));
            // seems the file system does not assume existence of empty directories
            Assert.assertTrue(fs.exists(directory));
            // directory empty
            Assert.assertEquals(0, fs.listStatus(directory).length);
            // create some files
            final int numFiles = 3;
            for (int i = 0; i < numFiles; i++) {
                Path file = new Path(directory, ("/file-" + i));
                try (FSDataOutputStream out = fs.create(file, NO_OVERWRITE);OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                    writer.write((("hello-" + i) + "\n"));
                }
            }
            FileStatus[] files = fs.listStatus(directory);
            Assert.assertNotNull(files);
            Assert.assertEquals(3, files.length);
            for (FileStatus status : files) {
                Assert.assertFalse(status.isDir());
            }
            // now that there are files, the directory must exist
            Assert.assertTrue(fs.exists(directory));
        } finally {
            // clean up
            fs.delete(directory, true);
        }
        // now directory must be gone
        Assert.assertFalse(fs.exists(directory));
    }
}

