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
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * This class tests the FileStatus API.
 */
public class TestListFiles {
    static {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
    }

    static final long seed = 3735928559L;

    protected static final Configuration conf = new Configuration();

    protected static FileSystem fs;

    protected static Path TEST_DIR;

    private static final int FILE_LEN = 10;

    private static Path FILE1;

    private static Path DIR1;

    private static Path FILE2;

    private static Path FILE3;

    static {
        TestListFiles.setTestPaths(new Path(GenericTestUtils.getTempPath("testlistfiles"), "main_"));
    }

    /**
     * Test when input path is a file
     */
    @Test
    public void testFile() throws IOException {
        TestListFiles.fs.mkdirs(TestListFiles.TEST_DIR);
        TestListFiles.writeFile(TestListFiles.fs, TestListFiles.FILE1, TestListFiles.FILE_LEN);
        RemoteIterator<LocatedFileStatus> itor = TestListFiles.fs.listFiles(TestListFiles.FILE1, true);
        LocatedFileStatus stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFiles.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFiles.fs.makeQualified(TestListFiles.FILE1), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        itor = TestListFiles.fs.listFiles(TestListFiles.FILE1, false);
        stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFiles.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFiles.fs.makeQualified(TestListFiles.FILE1), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        TestListFiles.fs.delete(TestListFiles.FILE1, true);
    }

    /**
     * Test when input path is a directory
     */
    @Test
    public void testDirectory() throws IOException {
        TestListFiles.fs.mkdirs(TestListFiles.DIR1);
        // test empty directory
        RemoteIterator<LocatedFileStatus> itor = TestListFiles.fs.listFiles(TestListFiles.DIR1, true);
        Assert.assertFalse(itor.hasNext());
        itor = TestListFiles.fs.listFiles(TestListFiles.DIR1, false);
        Assert.assertFalse(itor.hasNext());
        // testing directory with 1 file
        TestListFiles.writeFile(TestListFiles.fs, TestListFiles.FILE2, TestListFiles.FILE_LEN);
        itor = TestListFiles.fs.listFiles(TestListFiles.DIR1, true);
        LocatedFileStatus stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFiles.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFiles.fs.makeQualified(TestListFiles.FILE2), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        itor = TestListFiles.fs.listFiles(TestListFiles.DIR1, false);
        stat = itor.next();
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFiles.FILE_LEN, stat.getLen());
        Assert.assertEquals(TestListFiles.fs.makeQualified(TestListFiles.FILE2), stat.getPath());
        Assert.assertEquals(1, stat.getBlockLocations().length);
        // test more complicated directory
        TestListFiles.writeFile(TestListFiles.fs, TestListFiles.FILE1, TestListFiles.FILE_LEN);
        TestListFiles.writeFile(TestListFiles.fs, TestListFiles.FILE3, TestListFiles.FILE_LEN);
        Set<Path> filesToFind = new HashSet<Path>();
        filesToFind.add(TestListFiles.fs.makeQualified(TestListFiles.FILE1));
        filesToFind.add(TestListFiles.fs.makeQualified(TestListFiles.FILE2));
        filesToFind.add(TestListFiles.fs.makeQualified(TestListFiles.FILE3));
        itor = TestListFiles.fs.listFiles(TestListFiles.TEST_DIR, true);
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertTrue((("Path " + (stat.getPath())) + " unexpected"), filesToFind.remove(stat.getPath()));
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertTrue((("Path " + (stat.getPath())) + " unexpected"), filesToFind.remove(stat.getPath()));
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertTrue((("Path " + (stat.getPath())) + " unexpected"), filesToFind.remove(stat.getPath()));
        Assert.assertFalse(itor.hasNext());
        Assert.assertTrue(filesToFind.isEmpty());
        itor = TestListFiles.fs.listFiles(TestListFiles.TEST_DIR, false);
        stat = itor.next();
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(TestListFiles.fs.makeQualified(TestListFiles.FILE1), stat.getPath());
        Assert.assertFalse(itor.hasNext());
        TestListFiles.fs.delete(TestListFiles.TEST_DIR, true);
    }
}

