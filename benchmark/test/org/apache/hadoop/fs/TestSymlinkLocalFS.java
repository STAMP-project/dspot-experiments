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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.PlatformAssumptions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test symbolic links using LocalFs.
 */
public abstract class TestSymlinkLocalFS extends SymlinkBaseTest {
    // Workaround for HADOOP-9652
    static {
        RawLocalFileSystem.useStatIfAvailable();
    }

    /**
     * lstat a non-existant file using a partially qualified path
     */
    @Test(timeout = 10000)
    public void testDanglingLinkFilePartQual() throws IOException {
        Path filePartQual = new Path(((getScheme()) + ":///doesNotExist"));
        try {
            SymlinkBaseTest.wrapper.getFileLinkStatus(filePartQual);
            Assert.fail("Got FileStatus for non-existant file");
        } catch (FileNotFoundException f) {
            // Expected
        }
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(filePartQual);
            Assert.fail("Got link target for non-existant file");
        } catch (FileNotFoundException f) {
            // Expected
        }
    }

    /**
     * Stat and lstat a dangling link
     */
    @Test(timeout = 10000)
    public void testDanglingLink() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Path fileAbs = new Path(((testBaseDir1()) + "/file"));
        Path fileQual = new Path(testURI().toString(), fileAbs);
        Path link = new Path(((testBaseDir1()) + "/linkToFile"));
        Path linkQual = new Path(testURI().toString(), link.toString());
        SymlinkBaseTest.wrapper.createSymlink(fileAbs, link, false);
        // Deleting the link using FileContext currently fails because
        // resolve looks up LocalFs rather than RawLocalFs for the path
        // so we call ChecksumFs delete (which doesn't delete dangling
        // links) instead of delegating to delete in RawLocalFileSystem
        // which deletes via fullyDelete. testDeleteLink above works
        // because the link is not dangling.
        // assertTrue(fc.delete(link, false));
        FileUtil.fullyDelete(new File(link.toUri().getPath()));
        SymlinkBaseTest.wrapper.createSymlink(fileAbs, link, false);
        try {
            SymlinkBaseTest.wrapper.getFileStatus(link);
            Assert.fail("Got FileStatus for dangling link");
        } catch (FileNotFoundException f) {
            // Expected. File's exists method returns false for dangling links
        }
        // We can stat a dangling link
        UserGroupInformation user = UserGroupInformation.getCurrentUser();
        FileStatus fsd = SymlinkBaseTest.wrapper.getFileLinkStatus(link);
        Assert.assertEquals(fileQual, fsd.getSymlink());
        Assert.assertTrue(fsd.isSymlink());
        Assert.assertFalse(fsd.isDirectory());
        Assert.assertEquals(user.getUserName(), fsd.getOwner());
        // Compare against user's primary group
        Assert.assertEquals(user.getGroupNames()[0], fsd.getGroup());
        Assert.assertEquals(linkQual, fsd.getPath());
        // Accessing the link
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("Got FileStatus for dangling link");
        } catch (FileNotFoundException f) {
            // Ditto.
        }
        // Creating the file makes the link work
        SymlinkBaseTest.createAndWriteFile(fileAbs);
        SymlinkBaseTest.wrapper.getFileStatus(link);
    }

    /**
     * Test getLinkTarget with a partially qualified target.
     * NB: Hadoop does not support fully qualified URIs for the
     * file scheme (eg file://host/tmp/test).
     */
    @Test(timeout = 10000)
    public void testGetLinkStatusPartQualTarget() throws IOException {
        Path fileAbs = new Path(((testBaseDir1()) + "/file"));
        Path fileQual = new Path(testURI().toString(), fileAbs);
        Path dir = new Path(testBaseDir1());
        Path link = new Path(((testBaseDir1()) + "/linkToFile"));
        Path dirNew = new Path(testBaseDir2());
        Path linkNew = new Path(((testBaseDir2()) + "/linkToFile"));
        SymlinkBaseTest.wrapper.delete(dirNew, true);
        SymlinkBaseTest.createAndWriteFile(fileQual);
        SymlinkBaseTest.wrapper.setWorkingDirectory(dir);
        // Link target is partially qualified, we get the same back.
        SymlinkBaseTest.wrapper.createSymlink(fileQual, link, false);
        Assert.assertEquals(fileQual, SymlinkBaseTest.wrapper.getFileLinkStatus(link).getSymlink());
        // Because the target was specified with an absolute path the
        // link fails to resolve after moving the parent directory.
        SymlinkBaseTest.wrapper.rename(dir, dirNew);
        // The target is still the old path
        Assert.assertEquals(fileQual, SymlinkBaseTest.wrapper.getFileLinkStatus(linkNew).getSymlink());
        try {
            SymlinkBaseTest.readFile(linkNew);
            Assert.fail("The link should be dangling now.");
        } catch (FileNotFoundException x) {
            // Expected.
        }
        // RawLocalFs only maintains the path part, not the URI, and
        // therefore does not support links to other file systems.
        Path anotherFs = new Path("hdfs://host:1000/dir/file");
        FileUtil.fullyDelete(new File(linkNew.toString()));
        try {
            SymlinkBaseTest.wrapper.createSymlink(anotherFs, linkNew, false);
            Assert.fail("Created a local fs link to a non-local fs");
        } catch (IOException x) {
            // Excpected.
        }
    }
}

