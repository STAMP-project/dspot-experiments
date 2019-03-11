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


import WritePType.CACHE_THROUGH;
import WritePType.MUST_CACHE;
import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.cli.fs.FileSystemShellUtilsTest;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.exception.AlluxioException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for free command.
 */
public final class FreeCommandIntegrationTest extends AbstractFileSystemShellTest {
    @Test
    public void freeNonPersistedFile() throws AlluxioException, IOException {
        String fileName = "/testFile";
        FileSystemTestUtils.createByteFile(mFileSystem, fileName, MUST_CACHE, 10);
        // freeing non persisted files is expected to fail
        Assert.assertEquals((-1), mFsShell.run("free", fileName));
        Assert.assertTrue(isInMemoryTest(fileName));
    }

    @Test
    public void freePinnedFile() throws AlluxioException, IOException {
        String fileName = "/testFile";
        FileSystemTestUtils.createByteFile(mFileSystem, fileName, CACHE_THROUGH, 10);
        mFsShell.run("pin", fileName);
        // freeing non persisted files is expected to fail
        Assert.assertEquals((-1), mFsShell.run("free", fileName));
        Assert.assertTrue(isInMemoryTest(fileName));
    }

    @Test
    public void freePinnedFileForced() throws AlluxioException, IOException {
        String fileName = "/testFile";
        FileSystemTestUtils.createByteFile(mFileSystem, fileName, CACHE_THROUGH, 10);
        mFsShell.run("pin", fileName);
        Assert.assertEquals(0, mFsShell.run("free", "-f", fileName));
        Assert.assertFalse(isInMemoryTest(fileName));
    }

    @Test
    public void free() throws AlluxioException, IOException {
        String fileName = "/testFile";
        FileSystemTestUtils.createByteFile(mFileSystem, fileName, CACHE_THROUGH, 10);
        Assert.assertEquals(0, mFsShell.run("free", fileName));
        Assert.assertFalse(isInMemoryTest(fileName));
    }

    @Test
    public void freeWildCardNonPersistedFile() throws AlluxioException, IOException {
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem, MUST_CACHE);
        Assert.assertEquals((-1), mFsShell.run("free", (testDir + "/foo/*")));
        // freeing non persisted files is expected to fail
        Assert.assertTrue(isInMemoryTest((testDir + "/foo/foobar1")));
        Assert.assertTrue(isInMemoryTest((testDir + "/foo/foobar2")));
        Assert.assertTrue(isInMemoryTest((testDir + "/bar/foobar3")));
        Assert.assertTrue(isInMemoryTest((testDir + "/foobar4")));
    }

    @Test
    public void freeWildCardPinnedFile() throws AlluxioException, IOException {
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem, CACHE_THROUGH);
        mFsShell.run("pin", (testDir + "/foo/*"));
        Assert.assertEquals((-1), mFsShell.run("free", (testDir + "/foo/*")));
        // freeing non pinned files is expected to fail without "-f"
        Assert.assertTrue(isInMemoryTest((testDir + "/foo/foobar1")));
        Assert.assertTrue(isInMemoryTest((testDir + "/foo/foobar2")));
    }

    @Test
    public void freeWildCardPinnedFileForced() throws AlluxioException, IOException {
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem, CACHE_THROUGH);
        mFsShell.run("pin", (testDir + "/foo/foobar1"));
        Assert.assertEquals(0, mFsShell.run("free", "-f", (testDir + "/foo/*")));
        Assert.assertFalse(isInMemoryTest((testDir + "/foo/foobar1")));
        Assert.assertFalse(isInMemoryTest((testDir + "/foo/foobar2")));
        Assert.assertTrue(isInMemoryTest((testDir + "/bar/foobar3")));
        Assert.assertTrue(isInMemoryTest((testDir + "/foobar4")));
    }

    @Test
    public void freeWildCard() throws AlluxioException, IOException {
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem, CACHE_THROUGH);
        int ret = mFsShell.run("free", (testDir + "/foo/*"));
        Assert.assertEquals(0, ret);
        Assert.assertFalse(isInMemoryTest((testDir + "/foo/foobar1")));
        Assert.assertFalse(isInMemoryTest((testDir + "/foo/foobar2")));
        Assert.assertTrue(isInMemoryTest((testDir + "/bar/foobar3")));
        Assert.assertTrue(isInMemoryTest((testDir + "/foobar4")));
        ret = mFsShell.run("free", (testDir + "/*/"));
        Assert.assertEquals(0, ret);
        Assert.assertFalse(isInMemoryTest((testDir + "/bar/foobar3")));
        Assert.assertFalse(isInMemoryTest((testDir + "/foobar4")));
    }
}

