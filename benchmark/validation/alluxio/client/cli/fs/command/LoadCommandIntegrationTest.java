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
import WritePType.THROUGH;
import alluxio.AlluxioURI;
import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.cli.fs.AbstractShellIntegrationTest;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.exception.AlluxioException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for load command.
 */
public final class LoadCommandIntegrationTest extends AbstractFileSystemShellTest {
    @Test
    public void loadDir() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", THROUGH, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileB", MUST_CACHE, 10);
        AlluxioURI uriA = new AlluxioURI("/testRoot/testFileA");
        AlluxioURI uriB = new AlluxioURI("/testRoot/testFileB");
        URIStatus statusA = mFileSystem.getStatus(uriA);
        URIStatus statusB = mFileSystem.getStatus(uriB);
        Assert.assertFalse(((statusA.getInAlluxioPercentage()) == 100));
        Assert.assertTrue(((statusB.getInAlluxioPercentage()) == 100));
        // Testing loading of a directory
        mFsShell.run("load", "/testRoot");
        statusA = mFileSystem.getStatus(uriA);
        statusB = mFileSystem.getStatus(uriB);
        Assert.assertTrue(((statusA.getInAlluxioPercentage()) == 100));
        Assert.assertTrue(((statusB.getInAlluxioPercentage()) == 100));
    }

    @Test
    public void loadFile() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile", THROUGH, 10);
        AlluxioURI uri = new AlluxioURI("/testFile");
        URIStatus status = mFileSystem.getStatus(uri);
        Assert.assertFalse(((status.getInAlluxioPercentage()) == 100));
        // Testing loading of a single file
        mFsShell.run("load", "/testFile");
        status = mFileSystem.getStatus(uri);
        Assert.assertTrue(((status.getInAlluxioPercentage()) == 100));
    }

    @Test
    public void loadFileWithLocalOption() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile", CACHE_THROUGH, 10);
        AlluxioURI uri = new AlluxioURI("/testFile");
        URIStatus status = mFileSystem.getStatus(uri);
        Assert.assertTrue(((status.getInAlluxioPercentage()) == 100));
        // Testing loading a file has been loaded fully
        mFsShell.run("load", "--local", "/testFile");
        Assert.assertEquals(("/testFile" + (" loaded" + "\n")), mOutput.toString());
        // Testing "load --local" works when the file isn't already loaded
        FileSystemTestUtils.createByteFile(mFileSystem, "/testFile2", THROUGH, 10);
        uri = new AlluxioURI("/testFile2");
        status = mFileSystem.getStatus(uri);
        Assert.assertFalse(((status.getInAlluxioPercentage()) == 100));
        mFsShell.run("load", "--local", "/testFile2");
        status = mFileSystem.getStatus(uri);
        Assert.assertTrue(((status.getInAlluxioPercentage()) == 100));
    }

    @Test
    public void loadFileWithWildcard() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir1/testFile1", THROUGH, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testDir2/testFile2", THROUGH, 10);
        AlluxioURI uri = new AlluxioURI("/testDir1/testFile1");
        URIStatus status = mFileSystem.getStatus(uri);
        Assert.assertFalse(((status.getInAlluxioPercentage()) == 100));
        uri = new AlluxioURI("/testDir2/testFile2");
        status = mFileSystem.getStatus(uri);
        Assert.assertFalse(((status.getInAlluxioPercentage()) == 100));
        // Testing loading with wild card
        mFsShell.run("load", "/*/testFile*");
        uri = new AlluxioURI("/testDir1/testFile1");
        status = mFileSystem.getStatus(uri);
        Assert.assertTrue(((status.getInAlluxioPercentage()) == 100));
        uri = new AlluxioURI("/testDir2/testFile2");
        status = mFileSystem.getStatus(uri);
        Assert.assertTrue(((status.getInAlluxioPercentage()) == 100));
    }
}

