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
package org.apache.flink.runtime.fs.hdfs;


import FileSystem.WriteMode.OVERWRITE;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Abstract integration test class for implementations of hadoop file system.
 */
public abstract class AbstractHadoopFileSystemITTest extends TestLogger {
    protected static FileSystem fs;

    protected static Path basePath;

    protected static long deadline;

    @Test
    public void testSimpleFileWriteAndRead() throws Exception {
        final String testLine = "Hello Upload!";
        final Path path = new Path(AbstractHadoopFileSystemITTest.basePath, "test.txt");
        try {
            try (FSDataOutputStream out = AbstractHadoopFileSystemITTest.fs.create(path, OVERWRITE);OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                writer.write(testLine);
            }
            // just in case, wait for the path to exist
            AbstractHadoopFileSystemITTest.checkPathExistence(path, true, AbstractHadoopFileSystemITTest.deadline);
            try (FSDataInputStream in = AbstractHadoopFileSystemITTest.fs.open(path);InputStreamReader ir = new InputStreamReader(in, StandardCharsets.UTF_8);BufferedReader reader = new BufferedReader(ir)) {
                String line = reader.readLine();
                TestCase.assertEquals(testLine, line);
            }
        } finally {
            AbstractHadoopFileSystemITTest.fs.delete(path, false);
        }
        AbstractHadoopFileSystemITTest.checkPathExistence(path, false, AbstractHadoopFileSystemITTest.deadline);
    }

    @Test
    public void testDirectoryListing() throws Exception {
        final Path directory = new Path(AbstractHadoopFileSystemITTest.basePath, "testdir/");
        // directory must not yet exist
        TestCase.assertFalse(AbstractHadoopFileSystemITTest.fs.exists(directory));
        try {
            // create directory
            TestCase.assertTrue(AbstractHadoopFileSystemITTest.fs.mkdirs(directory));
            checkEmptyDirectory(directory);
            // directory empty
            TestCase.assertEquals(0, AbstractHadoopFileSystemITTest.fs.listStatus(directory).length);
            // create some files
            final int numFiles = 3;
            for (int i = 0; i < numFiles; i++) {
                Path file = new Path(directory, ("/file-" + i));
                try (FSDataOutputStream out = AbstractHadoopFileSystemITTest.fs.create(file, OVERWRITE);OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                    writer.write((("hello-" + i) + "\n"));
                }
                // just in case, wait for the file to exist (should then also be reflected in the
                // directory's file list below)
                AbstractHadoopFileSystemITTest.checkPathExistence(file, true, AbstractHadoopFileSystemITTest.deadline);
            }
            FileStatus[] files = AbstractHadoopFileSystemITTest.fs.listStatus(directory);
            TestCase.assertNotNull(files);
            TestCase.assertEquals(3, files.length);
            for (FileStatus status : files) {
                TestCase.assertFalse(status.isDir());
            }
            // now that there are files, the directory must exist
            TestCase.assertTrue(AbstractHadoopFileSystemITTest.fs.exists(directory));
        } finally {
            // clean up
            AbstractHadoopFileSystemITTest.fs.delete(directory, true);
        }
        // now directory must be gone
        AbstractHadoopFileSystemITTest.checkPathExistence(directory, false, AbstractHadoopFileSystemITTest.deadline);
    }
}

