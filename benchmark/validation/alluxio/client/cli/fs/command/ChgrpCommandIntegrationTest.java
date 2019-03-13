/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.cli.fs.command;


import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.exception.AlluxioException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for chgrp command.
 */
public final class ChgrpCommandIntegrationTest extends AbstractFileSystemShellTest {
    @Test
    public void chgrp() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile", MUST_CACHE, 10);
        mFsShell.run("chgrp", "group1", "/testFile");
        String group = mFileSystem.getStatus(new AlluxioURI("/testFile")).getGroup();
        Assert.assertEquals("group1", group);
        mFsShell.run("chgrp", "group2", "/testFile");
        group = mFileSystem.getStatus(new AlluxioURI("/testFile")).getGroup();
        Assert.assertEquals("group2", group);
    }

    /**
     * Tests -R option for chgrp recursively.
     */
    @Test
    public void chgrpRecursive() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/testFile", MUST_CACHE, 10);
        // "chgrp -R group1 /testDir" should apply to both dir and child file
        mFsShell.run("chgrp", "-R", "group1", "/testDir");
        String group = mFileSystem.getStatus(new AlluxioURI("/testDir")).getGroup();
        Assert.assertEquals("group1", group);
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile")).getGroup();
        Assert.assertEquals("group1", group);
        // chgrp to another group.
        mFsShell.run("chgrp", "-R", "group2", "/testDir");
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile")).getGroup();
        Assert.assertEquals("group2", group);
    }

    /**
     * Tests wildcard functionality.
     */
    @Test
    public void chgrpWildcard() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/foo/testFile1", MUST_CACHE, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/foo/testFile2", MUST_CACHE, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/bar/testFile3", MUST_CACHE, 10);
        mFsShell.run("chgrp", "group1", "/testDir/*/testFile*");
        String group = mFileSystem.getStatus(new AlluxioURI("/testDir/foo/testFile1")).getGroup();
        Assert.assertEquals("group1", group);
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/foo/testFile2")).getGroup();
        Assert.assertEquals("group1", group);
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/bar/testFile3")).getGroup();
        Assert.assertEquals("group1", group);
        // chgrp to another group.
        mFsShell.run("chgrp", "group2", "/testDir/*/testFile*");
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/foo/testFile1")).getGroup();
        Assert.assertEquals("group2", group);
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/foo/testFile2")).getGroup();
        Assert.assertEquals("group2", group);
        group = mFileSystem.getStatus(new AlluxioURI("/testDir/bar/testFile3")).getGroup();
        Assert.assertEquals("group2", group);
    }
}

