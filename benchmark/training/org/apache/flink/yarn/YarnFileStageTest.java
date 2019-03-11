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
package org.apache.flink.yarn;


import org.apache.flink.util.TestLogger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for verifying file staging during submission to YARN works.
 */
public class YarnFileStageTest extends TestLogger {
    @ClassRule
    public static final TemporaryFolder CLASS_TEMP_DIR = new TemporaryFolder();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static MiniDFSCluster hdfsCluster;

    private static Path hdfsRootPath;

    private Configuration hadoopConfig;

    /**
     * Verifies that nested directories are properly copied with a <tt>hdfs://</tt> file
     * system (from a <tt>file:///absolute/path</tt> source path).
     */
    @Test
    public void testCopyFromLocalRecursiveWithScheme() throws Exception {
        final FileSystem targetFileSystem = YarnFileStageTest.hdfsRootPath.getFileSystem(hadoopConfig);
        final Path targetDir = targetFileSystem.getWorkingDirectory();
        YarnFileStageTest.testCopyFromLocalRecursive(targetFileSystem, targetDir, tempFolder, true);
    }

    /**
     * Verifies that nested directories are properly copied with a <tt>hdfs://</tt> file
     * system (from a <tt>/absolute/path</tt> source path).
     */
    @Test
    public void testCopyFromLocalRecursiveWithoutScheme() throws Exception {
        final FileSystem targetFileSystem = YarnFileStageTest.hdfsRootPath.getFileSystem(hadoopConfig);
        final Path targetDir = targetFileSystem.getWorkingDirectory();
        YarnFileStageTest.testCopyFromLocalRecursive(targetFileSystem, targetDir, tempFolder, false);
    }
}

