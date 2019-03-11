/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.util;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.Assert;
import org.junit.Test;


public class DrillFileSystemUtilTest extends FileSystemUtilTestBase {
    @Test
    public void testListDirectoriesWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listDirectories(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, false);
        Assert.assertEquals("Directory count should match", 2, statuses.size());
    }

    @Test
    public void testListDirectoriesWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listDirectories(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, false, ((PathFilter) (( path) -> path.getName().endsWith("a"))));
        Assert.assertEquals("Directory count should match", 1, statuses.size());
        Assert.assertEquals("Directory name should match", "a", statuses.get(0).getPath().getName());
    }

    @Test
    public void testListDirectoriesRecursiveWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listDirectories(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, true);
        Assert.assertEquals("Directory count should match", 3, statuses.size());
    }

    @Test
    public void testListDirectoriesRecursiveWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listDirectories(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, true, ((PathFilter) (( path) -> path.getName().endsWith("a"))));
        Assert.assertEquals("Directory count should match", 2, statuses.size());
        Collections.sort(statuses);
        Assert.assertEquals("Directory name should match", "a", statuses.get(0).getPath().getName());
        Assert.assertEquals("Directory name should match", "aa", statuses.get(1).getPath().getName());
    }

    @Test
    public void testListFilesWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listFiles(FileSystemUtilTestBase.fs, new Path(FileSystemUtilTestBase.base, "a"), false);
        Assert.assertEquals("File count should match", 1, statuses.size());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(0).getPath().getName());
    }

    @Test
    public void testListFilesWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listFiles(FileSystemUtilTestBase.fs, new Path(FileSystemUtilTestBase.base, "a"), false, ((PathFilter) (( path) -> path.getName().endsWith(".txt"))));
        Assert.assertEquals("File count should match", 1, statuses.size());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(0).getPath().getName());
    }

    @Test
    public void testListFilesRecursiveWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listFiles(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, true);
        Assert.assertEquals("File count should match", 3, statuses.size());
    }

    @Test
    public void testListFilesRecursiveWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listFiles(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, true, ((PathFilter) (( path) -> (path.getName().endsWith("a")) || (path.getName().endsWith(".txt")))));
        Assert.assertEquals("File count should match", 2, statuses.size());
        Collections.sort(statuses);
        Assert.assertEquals("File name should match", "f.txt", statuses.get(0).getPath().getName());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(1).getPath().getName());
    }

    @Test
    public void testListAllWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listAll(FileSystemUtilTestBase.fs, new Path(FileSystemUtilTestBase.base, "a"), false);
        Assert.assertEquals("File count should match", 2, statuses.size());
        Collections.sort(statuses);
        Assert.assertEquals("File name should match", "aa", statuses.get(0).getPath().getName());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(1).getPath().getName());
    }

    @Test
    public void testListAllWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listAll(FileSystemUtilTestBase.fs, new Path(FileSystemUtilTestBase.base, "a"), false, ((PathFilter) (( path) -> (path.getName().endsWith("a")) || (path.getName().endsWith(".txt")))));
        Assert.assertEquals("Directory and file count should match", 2, statuses.size());
        Collections.sort(statuses);
        Assert.assertEquals("Directory name should match", "aa", statuses.get(0).getPath().getName());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(1).getPath().getName());
    }

    @Test
    public void testListAllRecursiveWithoutFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listAll(FileSystemUtilTestBase.fs, FileSystemUtilTestBase.base, true);
        Assert.assertEquals("Directory and file count should match", 6, statuses.size());
    }

    @Test
    public void testListAllRecursiveWithFilter() throws IOException {
        List<FileStatus> statuses = DrillFileSystemUtil.listAll(FileSystemUtilTestBase.fs, new Path(FileSystemUtilTestBase.base, "a"), true, ((PathFilter) (( path) -> (path.getName().startsWith("a")) || (path.getName().endsWith(".txt")))));
        Assert.assertEquals("Directory and file count should match", 3, statuses.size());
        Collections.sort(statuses);
        Assert.assertEquals("Directory name should match", "aa", statuses.get(0).getPath().getName());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(1).getPath().getName());
        Assert.assertEquals("File name should match", "f.txt", statuses.get(2).getPath().getName());
    }

    @Test
    public void testListDirectoriesSafe() {
        Path file = new Path(FileSystemUtilTestBase.base, "missing");
        List<FileStatus> fileStatuses = DrillFileSystemUtil.listDirectoriesSafe(FileSystemUtilTestBase.fs, file, true);
        Assert.assertTrue("Should return empty result", fileStatuses.isEmpty());
    }

    @Test
    public void testListFilesSafe() {
        Path file = new Path(FileSystemUtilTestBase.base, "missing.txt");
        List<FileStatus> fileStatuses = DrillFileSystemUtil.listFilesSafe(FileSystemUtilTestBase.fs, file, true);
        Assert.assertTrue("Should return empty result", fileStatuses.isEmpty());
    }

    @Test
    public void testListAllSafe() {
        Path file = new Path(FileSystemUtilTestBase.base, "missing");
        List<FileStatus> fileStatuses = DrillFileSystemUtil.listAllSafe(FileSystemUtilTestBase.fs, file, true);
        Assert.assertTrue("Should return empty result", fileStatuses.isEmpty());
    }
}

