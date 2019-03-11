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
package org.apache.hadoop.hdfs;


import FileSystem.LOG;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the FileStatus API.
 */
public class TestListFilesInFileContext {
    {
        GenericTestUtils.setLogLevel(LOG, Level.ALL);
    }

    static final long seed = 3735928559L;

    private static final Configuration conf = new Configuration();

    private static MiniDFSCluster cluster;

    private static FileContext fc;

    private static final Path TEST_DIR = new Path("/main_");

    private static final int FILE_LEN = 10;

    private static final Path FILE1 = new Path(TestListFilesInFileContext.TEST_DIR, "file1");

    private static final Path DIR1 = new Path(TestListFilesInFileContext.TEST_DIR, "dir1");

    private static final Path FILE2 = new Path(TestListFilesInFileContext.DIR1, "file2");

    private static final Path FILE3 = new Path(TestListFilesInFileContext.DIR1, "file3");

    /**
     * Test when input path is a file
     */
    @Test
    public void testFile() throws IOException {
        TestListFilesInFileContext.fc.mkdir(TestListFilesInFileContext.TEST_DIR, FsPermission.getDefault(), true);
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE1, TestListFilesInFileContext.FILE_LEN);
        RemoteIterator<LocatedFileStatus> itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.FILE1, true);
        LocatedFileStatus stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.FILE1, false);
        stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
    }

    /**
     * Test when input path is a directory
     */
    @Test
    public void testDirectory() throws IOException {
        TestListFilesInFileContext.fc.mkdir(TestListFilesInFileContext.DIR1, FsPermission.getDefault(), true);
        // test empty directory
        RemoteIterator<LocatedFileStatus> itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.DIR1, true);
        Assert.assertFalse(itor.hasNext());
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.DIR1, false);
        Assert.assertFalse(itor.hasNext());
        // testing directory with 1 file
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE2, TestListFilesInFileContext.FILE_LEN);
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.DIR1, true);
        LocatedFileStatus stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE2), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.DIR1, false);
        stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE2), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        // test more complicated directory
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE1, TestListFilesInFileContext.FILE_LEN);
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE3, TestListFilesInFileContext.FILE_LEN);
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.TEST_DIR, true);
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE2), stat.getPath());
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE3), stat.getPath());
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertFalse(itor.hasNext());
        itor = TestListFilesInFileContext.fc.util().listFiles(TestListFilesInFileContext.TEST_DIR, false);
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertFalse(itor.hasNext());
    }

    /**
     * Test when input patch has a symbolic links as its children
     */
    @Test
    public void testSymbolicLinks() throws IOException {
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE1, TestListFilesInFileContext.FILE_LEN);
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE2, TestListFilesInFileContext.FILE_LEN);
        TestListFilesInFileContext.writeFile(TestListFilesInFileContext.fc, TestListFilesInFileContext.FILE3, TestListFilesInFileContext.FILE_LEN);
        Path dir4 = new Path(TestListFilesInFileContext.TEST_DIR, "dir4");
        Path dir5 = new Path(dir4, "dir5");
        Path file4 = new Path(dir4, "file4");
        TestListFilesInFileContext.fc.createSymlink(TestListFilesInFileContext.DIR1, dir5, true);
        TestListFilesInFileContext.fc.createSymlink(TestListFilesInFileContext.FILE1, file4, true);
        RemoteIterator<LocatedFileStatus> itor = TestListFilesInFileContext.fc.util().listFiles(dir4, true);
        LocatedFileStatus stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE2), stat.getPath());
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE3), stat.getPath());
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertFalse(itor.hasNext());
        itor = TestListFilesInFileContext.fc.util().listFiles(dir4, false);
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFilesInFileContext.fc.makeQualified(TestListFilesInFileContext.FILE1), stat.getPath());
        Assert.assertFalse(itor.hasNext());
    }
}

