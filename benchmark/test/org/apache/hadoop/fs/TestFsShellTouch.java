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


import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFsShellTouch {
    static final Logger LOG = LoggerFactory.getLogger(TestFsShellTouch.class);

    static FsShell shell;

    static LocalFileSystem lfs;

    static Path testRootDir;

    @Test
    public void testTouchz() throws Exception {
        // Ensure newFile does not exist
        final String newFileName = "newFile";
        final Path newFile = new Path(newFileName);
        TestFsShellTouch.lfs.delete(newFile, true);
        Assert.assertThat(TestFsShellTouch.lfs.exists(newFile), CoreMatchers.is(false));
        Assert.assertThat("Expected successful touchz on a new file", shellRun("-touchz", newFileName), CoreMatchers.is(0));
        shellRun("-ls", newFileName);
        Assert.assertThat("Expected successful touchz on an existing zero-length file", shellRun("-touchz", newFileName), CoreMatchers.is(0));
        // Ensure noDir does not exist
        final String noDirName = "noDir";
        final Path noDir = new Path(noDirName);
        TestFsShellTouch.lfs.delete(noDir, true);
        Assert.assertThat(TestFsShellTouch.lfs.exists(noDir), CoreMatchers.is(false));
        Assert.assertThat("Expected failed touchz in a non-existent directory", shellRun("-touchz", (noDirName + "/foo")), CoreMatchers.is(CoreMatchers.not(0)));
    }

    @Test
    public void testTouch() throws Exception {
        // Ensure newFile2 does not exist
        final String newFileName = "newFile2";
        final Path newFile = new Path(newFileName);
        TestFsShellTouch.lfs.delete(newFile, true);
        Assert.assertThat(TestFsShellTouch.lfs.exists(newFile), CoreMatchers.is(false));
        {
            Assert.assertThat("Expected successful touch on a non-existent file with -c option", shellRun("-touch", "-c", newFileName), CoreMatchers.is(CoreMatchers.not(0)));
            Assert.assertThat(TestFsShellTouch.lfs.exists(newFile), CoreMatchers.is(false));
        }
        {
            String strTime = formatTimestamp(System.currentTimeMillis());
            Date dateObj = parseTimestamp(strTime);
            Assert.assertThat("Expected successful touch on a new file with a specified timestamp", shellRun("-touch", "-t", strTime, newFileName), CoreMatchers.is(0));
            FileStatus new_status = TestFsShellTouch.lfs.getFileStatus(newFile);
            Assert.assertThat(new_status.getAccessTime(), CoreMatchers.is(dateObj.getTime()));
            Assert.assertThat(new_status.getModificationTime(), CoreMatchers.is(dateObj.getTime()));
        }
        FileStatus fstatus = TestFsShellTouch.lfs.getFileStatus(newFile);
        {
            String strTime = formatTimestamp(System.currentTimeMillis());
            Date dateObj = parseTimestamp(strTime);
            Assert.assertThat("Expected successful touch with a specified access time", shellRun("-touch", "-a", "-t", strTime, newFileName), CoreMatchers.is(0));
            FileStatus new_status = TestFsShellTouch.lfs.getFileStatus(newFile);
            // Verify if access time is recorded correctly (and modification time
            // remains unchanged).
            Assert.assertThat(new_status.getAccessTime(), CoreMatchers.is(dateObj.getTime()));
            Assert.assertThat(new_status.getModificationTime(), CoreMatchers.is(fstatus.getModificationTime()));
        }
        fstatus = TestFsShellTouch.lfs.getFileStatus(newFile);
        {
            String strTime = formatTimestamp(System.currentTimeMillis());
            Date dateObj = parseTimestamp(strTime);
            Assert.assertThat("Expected successful touch with a specified modificatiom time", shellRun("-touch", "-m", "-t", strTime, newFileName), CoreMatchers.is(0));
            // Verify if modification time is recorded correctly (and access time
            // remains unchanged).
            FileStatus new_status = TestFsShellTouch.lfs.getFileStatus(newFile);
            Assert.assertThat(new_status.getAccessTime(), CoreMatchers.is(fstatus.getAccessTime()));
            Assert.assertThat(new_status.getModificationTime(), CoreMatchers.is(dateObj.getTime()));
        }
        {
            String strTime = formatTimestamp(System.currentTimeMillis());
            Date dateObj = parseTimestamp(strTime);
            Assert.assertThat("Expected successful touch with a specified timestamp", shellRun("-touch", "-t", strTime, newFileName), CoreMatchers.is(0));
            // Verify if both modification and access times are recorded correctly
            FileStatus new_status = TestFsShellTouch.lfs.getFileStatus(newFile);
            Assert.assertThat(new_status.getAccessTime(), CoreMatchers.is(dateObj.getTime()));
            Assert.assertThat(new_status.getModificationTime(), CoreMatchers.is(dateObj.getTime()));
        }
        {
            String strTime = formatTimestamp(System.currentTimeMillis());
            Date dateObj = parseTimestamp(strTime);
            Assert.assertThat("Expected successful touch with a specified timestamp", shellRun("-touch", "-a", "-m", "-t", strTime, newFileName), CoreMatchers.is(0));
            // Verify if both modification and access times are recorded correctly
            FileStatus new_status = TestFsShellTouch.lfs.getFileStatus(newFile);
            Assert.assertThat(new_status.getAccessTime(), CoreMatchers.is(dateObj.getTime()));
            Assert.assertThat(new_status.getModificationTime(), CoreMatchers.is(dateObj.getTime()));
        }
        {
            Assert.assertThat("Expected failed touch with a missing timestamp", shellRun("-touch", "-t", newFileName), CoreMatchers.is(CoreMatchers.not(0)));
        }
    }
}

