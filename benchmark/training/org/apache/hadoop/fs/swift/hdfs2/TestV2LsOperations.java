/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.swift.hdfs2;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.swift.SwiftFileSystemBaseTest;
import org.apache.hadoop.fs.swift.SwiftTestConstants;
import org.apache.hadoop.fs.swift.util.SwiftTestUtils;
import org.junit.Test;


public class TestV2LsOperations extends SwiftFileSystemBaseTest {
    private Path[] testDirs;

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testListFilesRootDir() throws Throwable {
        Path dir = path("/");
        Path child = new Path(dir, "test");
        fs.delete(child, true);
        SwiftTestUtils.writeTextFile(fs, child, "text", false);
        TestV2LsOperations.assertListFilesFinds(fs, dir, child, false);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testListFilesSubDir() throws Throwable {
        createTestSubdirs();
        Path dir = path("/test/subdir");
        Path child = new Path(dir, "text.txt");
        SwiftTestUtils.writeTextFile(fs, child, "text", false);
        TestV2LsOperations.assertListFilesFinds(fs, dir, child, false);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testListFilesRecursive() throws Throwable {
        createTestSubdirs();
        Path dir = path("/test/recursive");
        Path child = new Path(dir, "hadoop/a/a.txt");
        SwiftTestUtils.writeTextFile(fs, child, "text", false);
        TestV2LsOperations.assertListFilesFinds(fs, dir, child, true);
    }
}

