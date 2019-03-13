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
package alluxio.client.fs;


import PropertyKey.SECURITY_LOGIN_USERNAME;
import UnderFileSystem.Factory;
import WritePType.CACHE_THROUGH;
import alluxio.AlluxioURI;
import alluxio.AuthenticatedUserRule;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.CreateFilePOptions;
import alluxio.master.file.FileSystemMaster;
import alluxio.master.file.contexts.CheckConsistencyContext;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.underfs.UnderFileSystem;
import alluxio.underfs.options.DeleteOptions;
import com.google.common.collect.Lists;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration test for
 * {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)}.
 */
public class CheckConsistencyIntegrationTest extends BaseIntegrationTest {
    private static final AlluxioURI DIRECTORY = new AlluxioURI("/dir");

    private static final AlluxioURI FILE = new AlluxioURI("/dir/file");

    private static final String TEST_USER = "test";

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(SECURITY_LOGIN_USERNAME, CheckConsistencyIntegrationTest.TEST_USER).build();

    @Rule
    public AuthenticatedUserRule mAuthenticatedUser = new AuthenticatedUserRule(CheckConsistencyIntegrationTest.TEST_USER, ServerConfiguration.global());

    private FileSystemMaster mFileSystemMaster;

    private FileSystem mFileSystem;

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when all files are consistent.
     */
    @Test
    public void consistent() throws Exception {
        Assert.assertEquals(new ArrayList<AlluxioURI>(), mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults()));
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when no files are consistent.
     */
    @Test
    public void inconsistent() throws Exception {
        String ufsDirectory = mFileSystem.getStatus(CheckConsistencyIntegrationTest.DIRECTORY).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsDirectory, ServerConfiguration.global());
        ufs.deleteDirectory(ufsDirectory, DeleteOptions.defaults().setRecursive(true));
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.FILE, CheckConsistencyIntegrationTest.DIRECTORY);
        List<AlluxioURI> result = mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults());
        Collections.sort(expected);
        Collections.sort(result);
        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when some files are consistent.
     */
    @Test
    public void partiallyInconsistent() throws Exception {
        String ufsFile = mFileSystem.getStatus(CheckConsistencyIntegrationTest.FILE).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsFile, ServerConfiguration.global());
        ufs.deleteFile(ufsFile);
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.FILE);
        Assert.assertEquals(expected, mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults()));
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when some files are consistent in a larger inode tree.
     */
    @Test
    public void largeTree() throws Exception {
        CreateDirectoryPOptions dirOptions = CreateDirectoryPOptions.newBuilder().setWriteType(CACHE_THROUGH).build();
        CreateFilePOptions fileOptions = CreateFilePOptions.newBuilder().setWriteType(CACHE_THROUGH).build();
        AlluxioURI nestedDir = CheckConsistencyIntegrationTest.DIRECTORY.join("/dir2");
        AlluxioURI topLevelFile = new AlluxioURI("/file");
        AlluxioURI thirdLevelFile = nestedDir.join("/file");
        mFileSystem.createDirectory(nestedDir, dirOptions);
        mFileSystem.createFile(topLevelFile, fileOptions).close();
        mFileSystem.createFile(thirdLevelFile, fileOptions).close();
        String ufsDirectory = mFileSystem.getStatus(nestedDir).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsDirectory, ServerConfiguration.global());
        ufs.deleteDirectory(ufsDirectory, DeleteOptions.defaults().setRecursive(true));
        List<AlluxioURI> expected = Lists.newArrayList(nestedDir, thirdLevelFile);
        List<AlluxioURI> result = mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults());
        Collections.sort(expected);
        Collections.sort(result);
        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when a file is not the correct size.
     */
    @Test
    public void incorrectFileSize() throws Exception {
        String ufsFile = mFileSystem.getStatus(CheckConsistencyIntegrationTest.FILE).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsFile, ServerConfiguration.global());
        ufs.deleteFile(ufsFile);
        OutputStream out = ufs.create(ufsFile);
        out.write(1);
        out.close();
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.FILE);
        Assert.assertEquals(expected, mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults()));
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when a directory does not exist as a directory in the under storage.
     */
    @Test
    public void notADirectory() throws Exception {
        String ufsDirectory = mFileSystem.getStatus(CheckConsistencyIntegrationTest.DIRECTORY).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsDirectory, ServerConfiguration.global());
        ufs.deleteDirectory(ufsDirectory, DeleteOptions.defaults().setRecursive(true));
        ufs.create(ufsDirectory).close();
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.DIRECTORY, CheckConsistencyIntegrationTest.FILE);
        List<AlluxioURI> result = mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults());
        Collections.sort(expected);
        Collections.sort(result);
        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when a file does not exist as a file in the under storage.
     */
    @Test
    public void notAFile() throws Exception {
        String ufsFile = mFileSystem.getStatus(CheckConsistencyIntegrationTest.FILE).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsFile, ServerConfiguration.global());
        ufs.deleteFile(ufsFile);
        ufs.mkdirs(ufsFile);
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.FILE);
        Assert.assertEquals(expected, mFileSystemMaster.checkConsistency(new AlluxioURI("/"), CheckConsistencyContext.defaults()));
    }

    /**
     * Tests the {@link FileSystemMaster#checkConsistency(AlluxioURI, CheckConsistencyContext)} method
     * when running on a file that is inconsistent.
     */
    @Test
    public void inconsistentFile() throws Exception {
        String ufsFile = mFileSystem.getStatus(CheckConsistencyIntegrationTest.FILE).getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsFile, ServerConfiguration.global());
        ufs.deleteFile(ufsFile);
        List<AlluxioURI> expected = Lists.newArrayList(CheckConsistencyIntegrationTest.FILE);
        Assert.assertEquals(expected, mFileSystemMaster.checkConsistency(CheckConsistencyIntegrationTest.FILE, CheckConsistencyContext.defaults()));
    }
}

