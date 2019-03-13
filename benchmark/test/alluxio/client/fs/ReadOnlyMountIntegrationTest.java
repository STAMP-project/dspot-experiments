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


import ExceptionMessage.MOUNT_READONLY;
import WritePType.CACHE_THROUGH;
import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.exception.AccessControlException;
import alluxio.exception.AlluxioException;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.LoadMetadataPOptions;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.io.PathUtils;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for mounting (reuse the {@link LocalAlluxioCluster}).
 */
public class ReadOnlyMountIntegrationTest extends BaseIntegrationTest {
    private static final String MOUNT_PATH = PathUtils.concatPath("/", "mnt", "foo");

    private static final String FILE_PATH = PathUtils.concatPath(ReadOnlyMountIntegrationTest.MOUNT_PATH, "file");

    private static final String SUB_DIR_PATH = PathUtils.concatPath(ReadOnlyMountIntegrationTest.MOUNT_PATH, "sub", "dir");

    private static final String SUB_FILE_PATH = PathUtils.concatPath(ReadOnlyMountIntegrationTest.SUB_DIR_PATH, "subfile");

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    private FileSystem mFileSystem = null;

    private UnderFileSystem mUfs;

    private String mAlternateUfsRoot;

    @Test
    public void createFile() throws AlluxioException, IOException {
        CreateFilePOptions writeBoth = CreateFilePOptions.newBuilder().setWriteType(CACHE_THROUGH).build();
        AlluxioURI uri = new AlluxioURI(((ReadOnlyMountIntegrationTest.FILE_PATH) + "_create"));
        try {
            mFileSystem.createFile(uri, writeBoth).close();
            Assert.fail("createFile should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(uri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        uri = new AlluxioURI(((ReadOnlyMountIntegrationTest.SUB_FILE_PATH) + "_create"));
        try {
            mFileSystem.createFile(uri, writeBoth).close();
            Assert.fail("createFile should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(uri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void createDirectory() throws AlluxioException, IOException {
        AlluxioURI uri = new AlluxioURI(PathUtils.concatPath(ReadOnlyMountIntegrationTest.MOUNT_PATH, "create"));
        try {
            mFileSystem.createDirectory(uri);
            Assert.fail("createDirectory should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(uri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        uri = new AlluxioURI(PathUtils.concatPath(ReadOnlyMountIntegrationTest.SUB_DIR_PATH, "create"));
        try {
            mFileSystem.createDirectory(uri);
            Assert.fail("createDirectory should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(uri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void deleteFile() throws AlluxioException, IOException {
        AlluxioURI fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        mFileSystem.loadMetadata(fileUri);
        try {
            mFileSystem.delete(fileUri);
            Assert.fail("deleteFile should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(fileUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        Assert.assertTrue(mFileSystem.exists(fileUri));
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
        fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        mFileSystem.loadMetadata(fileUri, LoadMetadataPOptions.newBuilder().setRecursive(true).build());
        try {
            mFileSystem.delete(fileUri);
            Assert.fail("deleteFile should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(fileUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        Assert.assertTrue(mFileSystem.exists(fileUri));
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
    }

    @Test
    public void getFileStatus() throws AlluxioException, IOException {
        AlluxioURI fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        mFileSystem.loadMetadata(fileUri);
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
        fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        mFileSystem.loadMetadata(fileUri, LoadMetadataPOptions.newBuilder().setRecursive(true).build());
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
    }

    @Test
    public void renameFile() throws AlluxioException, IOException {
        AlluxioURI srcUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        AlluxioURI dstUri = new AlluxioURI(((ReadOnlyMountIntegrationTest.FILE_PATH) + "_renamed"));
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(srcUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        srcUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        dstUri = new AlluxioURI(((ReadOnlyMountIntegrationTest.SUB_FILE_PATH) + "_renamed"));
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(srcUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void renameFileDst() throws AlluxioException, IOException {
        AlluxioURI srcUri = new AlluxioURI("/tmp");
        AlluxioURI dstUri = new AlluxioURI(((ReadOnlyMountIntegrationTest.FILE_PATH) + "_renamed"));
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(dstUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        dstUri = new AlluxioURI(((ReadOnlyMountIntegrationTest.SUB_FILE_PATH) + "_renamed"));
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(dstUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void renameFileSrc() throws AlluxioException, IOException {
        AlluxioURI srcUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        AlluxioURI dstUri = new AlluxioURI("/tmp");
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(srcUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
        srcUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("rename should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(srcUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void renameDirectory() throws AlluxioException, IOException {
        AlluxioURI srcUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_DIR_PATH);
        AlluxioURI dstUri = new AlluxioURI(((ReadOnlyMountIntegrationTest.SUB_DIR_PATH) + "_renamed"));
        try {
            mFileSystem.rename(srcUri, dstUri);
            Assert.fail("renameDirectory should not succeed under a readonly mount.");
        } catch (AccessControlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(MOUNT_READONLY.getMessage(srcUri, ReadOnlyMountIntegrationTest.MOUNT_PATH)));
        }
    }

    @Test
    public void loadMetadata() throws AlluxioException, IOException {
        AlluxioURI fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        // TODO(jiri) Re-enable this once we support the "check UFS" option for getStatus.
        // try {
        // mFileSystem.getStatus(fileUri);
        // Assert.fail("File should not exist before loading metadata.");
        // } catch (FileDoesNotExistException e) {
        // Assert
        // .assertEquals(e.getMessage(), ExceptionMessage.PATH_DOES_NOT_EXIST.getMessage(FILE_PATH));
        // }
        mFileSystem.loadMetadata(fileUri);
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
        fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        // TODO(jiri) Re-enable this once we support the "check UFS" option for getStatus.
        // try {
        // mFileSystem.getStatus(fileUri);
        // Assert.fail("File should not exist before loading metadata.");
        // } catch (FileDoesNotExistException e) {
        // Assert.assertEquals(e.getMessage(),
        // ExceptionMessage.PATH_DOES_NOT_EXIST.getMessage(SUB_FILE_PATH));
        // }
        mFileSystem.loadMetadata(fileUri, LoadMetadataPOptions.newBuilder().setRecursive(true).build());
        Assert.assertNotNull(mFileSystem.getStatus(fileUri));
    }

    @Test
    public void openFile() throws AlluxioException, IOException {
        AlluxioURI fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.FILE_PATH);
        mFileSystem.loadMetadata(fileUri);
        FileInStream inStream = mFileSystem.openFile(fileUri);
        Assert.assertNotNull(inStream);
        inStream.close();
        fileUri = new AlluxioURI(ReadOnlyMountIntegrationTest.SUB_FILE_PATH);
        mFileSystem.loadMetadata(fileUri, LoadMetadataPOptions.newBuilder().setRecursive(true).build());
        inStream = mFileSystem.openFile(fileUri);
        Assert.assertNotNull(inStream);
        inStream.close();
    }
}

