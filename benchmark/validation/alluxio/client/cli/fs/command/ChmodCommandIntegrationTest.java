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
 * Tests for chmod command.
 */
public final class ChmodCommandIntegrationTest extends AbstractFileSystemShellTest {
    @Test
    public void chmod() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile", MUST_CACHE, 10);
        mFsShell.run("chmod", "777", "/testFile");
        int permission = mFileSystem.getStatus(new AlluxioURI("/testFile")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        mFsShell.run("chmod", "755", "/testFile");
        permission = mFileSystem.getStatus(new AlluxioURI("/testFile")).getMode();
        Assert.assertEquals(((short) (493)), permission);
    }

    /**
     * Tests -R option for chmod recursively.
     */
    @Test
    public void chmodRecursively() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/testFile", MUST_CACHE, 10);
        mFsShell.run("chmod", "-R", "777", "/testDir");
        int permission = mFileSystem.getStatus(new AlluxioURI("/testDir")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        permission = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        mFsShell.run("chmod", "-R", "755", "/testDir");
        permission = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile")).getMode();
        Assert.assertEquals(((short) (493)), permission);
    }

    @Test
    public void chmodSymbolic() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile", MUST_CACHE, 10);
        mFsShell.run("chmod", "a=rwx", "/testFile");
        int permission = mFileSystem.getStatus(new AlluxioURI("/testFile")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        mFsShell.run("chmod", "u=rwx,go=rx", "/testFile");
        permission = mFileSystem.getStatus(new AlluxioURI("/testFile")).getMode();
        Assert.assertEquals(((short) (493)), permission);
    }

    /**
     * Tests wildcard entries for chmod.
     */
    @Test
    public void chmodWildCard() throws AlluxioException, IOException {
        clearLoginUser();
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir/testFile1", MUST_CACHE, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir2/testFile2", MUST_CACHE, 10);
        mFsShell.run("chmod", "a=rwx", "/testDir*/testFile*");
        int permission = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile1")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        permission = mFileSystem.getStatus(new AlluxioURI("/testDir2/testFile2")).getMode();
        Assert.assertEquals(((short) (511)), permission);
        mFsShell.run("chmod", "u=rwx,go=rx", "/testDir*/testFile*");
        permission = mFileSystem.getStatus(new AlluxioURI("/testDir/testFile1")).getMode();
        Assert.assertEquals(((short) (493)), permission);
        permission = mFileSystem.getStatus(new AlluxioURI("/testDir2/testFile2")).getMode();
        Assert.assertEquals(((short) (493)), permission);
    }
}

