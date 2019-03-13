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
package org.apache.hadoop.fs;


import FileSystem.LOG;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Level;
import org.junit.Test;


public class TestFcHdfsSetUMask {
    private static final FileContextTestHelper.FileContextTestHelper fileContextTestHelper = new FileContextTestHelper.FileContextTestHelper("/tmp/TestFcHdfsSetUMask");

    private static MiniDFSCluster cluster;

    private static Path defaultWorkingDirectory;

    private static FileContext fc;

    // rwxrwx---
    private static final FsPermission USER_GROUP_OPEN_PERMISSIONS = FsPermission.createImmutable(((short) (504)));

    private static final FsPermission USER_GROUP_OPEN_FILE_PERMISSIONS = FsPermission.createImmutable(((short) (432)));

    private static final FsPermission USER_GROUP_OPEN_TEST_UMASK = FsPermission.createImmutable(((short) (504 ^ 511)));

    // ---------
    private static final FsPermission BLANK_PERMISSIONS = FsPermission.createImmutable(((short) (0)));

    // parent directory permissions when creating a directory with blank (000)
    // permissions - it always add the -wx------ bits to the parent so that
    // it can create the child
    private static final FsPermission PARENT_PERMS_FOR_BLANK_PERMISSIONS = FsPermission.createImmutable(((short) (192)));

    private static final FsPermission BLANK_TEST_UMASK = FsPermission.createImmutable(((short) (0 ^ 511)));

    // rwxrwxrwx
    private static final FsPermission WIDE_OPEN_PERMISSIONS = FsPermission.createImmutable(((short) (511)));

    private static final FsPermission WIDE_OPEN_FILE_PERMISSIONS = FsPermission.createImmutable(((short) (438)));

    private static final FsPermission WIDE_OPEN_TEST_UMASK = FsPermission.createImmutable(((short) (511 ^ 511)));

    {
        try {
            GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
        } catch (Exception e) {
            System.out.println(("Cannot change log level\n" + (StringUtils.stringifyException(e))));
        }
    }

    @Test
    public void testMkdirWithExistingDirClear() throws IOException {
        testMkdirWithExistingDir(TestFcHdfsSetUMask.BLANK_TEST_UMASK, TestFcHdfsSetUMask.BLANK_PERMISSIONS);
    }

    @Test
    public void testMkdirWithExistingDirOpen() throws IOException {
        testMkdirWithExistingDir(TestFcHdfsSetUMask.WIDE_OPEN_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS);
    }

    @Test
    public void testMkdirWithExistingDirMiddle() throws IOException {
        testMkdirWithExistingDir(TestFcHdfsSetUMask.USER_GROUP_OPEN_TEST_UMASK, TestFcHdfsSetUMask.USER_GROUP_OPEN_PERMISSIONS);
    }

    @Test
    public void testMkdirRecursiveWithNonExistingDirClear() throws IOException {
        // by default parent directories have -wx------ bits set
        testMkdirRecursiveWithNonExistingDir(TestFcHdfsSetUMask.BLANK_TEST_UMASK, TestFcHdfsSetUMask.BLANK_PERMISSIONS, TestFcHdfsSetUMask.PARENT_PERMS_FOR_BLANK_PERMISSIONS);
    }

    @Test
    public void testMkdirRecursiveWithNonExistingDirOpen() throws IOException {
        testMkdirRecursiveWithNonExistingDir(TestFcHdfsSetUMask.WIDE_OPEN_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS);
    }

    @Test
    public void testMkdirRecursiveWithNonExistingDirMiddle() throws IOException {
        testMkdirRecursiveWithNonExistingDir(TestFcHdfsSetUMask.USER_GROUP_OPEN_TEST_UMASK, TestFcHdfsSetUMask.USER_GROUP_OPEN_PERMISSIONS, TestFcHdfsSetUMask.USER_GROUP_OPEN_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithExistingDirClear() throws IOException {
        testCreateRecursiveWithExistingDir(TestFcHdfsSetUMask.BLANK_TEST_UMASK, TestFcHdfsSetUMask.BLANK_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithExistingDirOpen() throws IOException {
        testCreateRecursiveWithExistingDir(TestFcHdfsSetUMask.WIDE_OPEN_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_FILE_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithExistingDirMiddle() throws IOException {
        testCreateRecursiveWithExistingDir(TestFcHdfsSetUMask.USER_GROUP_OPEN_TEST_UMASK, TestFcHdfsSetUMask.USER_GROUP_OPEN_FILE_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithNonExistingDirClear() throws IOException {
        // directory permission inherited from parent so this must match the @Before
        // set of umask
        testCreateRecursiveWithNonExistingDir(TestFcHdfsSetUMask.BLANK_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS, TestFcHdfsSetUMask.BLANK_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithNonExistingDirOpen() throws IOException {
        // directory permission inherited from parent so this must match the @Before
        // set of umask
        testCreateRecursiveWithNonExistingDir(TestFcHdfsSetUMask.WIDE_OPEN_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS, TestFcHdfsSetUMask.WIDE_OPEN_FILE_PERMISSIONS);
    }

    @Test
    public void testCreateRecursiveWithNonExistingDirMiddle() throws IOException {
        // directory permission inherited from parent so this must match the @Before
        // set of umask
        testCreateRecursiveWithNonExistingDir(TestFcHdfsSetUMask.USER_GROUP_OPEN_TEST_UMASK, TestFcHdfsSetUMask.WIDE_OPEN_PERMISSIONS, TestFcHdfsSetUMask.USER_GROUP_OPEN_FILE_PERMISSIONS);
    }
}

