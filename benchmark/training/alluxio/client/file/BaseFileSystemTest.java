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
package alluxio.client.file;


import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.MASTER_RPC_PORT;
import alluxio.AlluxioURI;
import alluxio.ClientContext;
import alluxio.ConfigurationTestUtils;
import alluxio.TestLoggerRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.DeletePOptions;
import alluxio.grpc.FreePOptions;
import alluxio.grpc.GetStatusPOptions;
import alluxio.grpc.ListStatusPOptions;
import alluxio.grpc.MountPOptions;
import alluxio.grpc.OpenFilePOptions;
import alluxio.grpc.RenamePOptions;
import alluxio.grpc.SetAttributePOptions;
import alluxio.grpc.UnmountPOptions;
import alluxio.util.FileSystemOptions;
import alluxio.wire.FileInfo;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit test for functionality in {@link BaseFileSystem}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystemContext.class, FileSystemMasterClient.class })
public final class BaseFileSystemTest {
    private static final RuntimeException EXCEPTION = new RuntimeException("test exception");

    private static final String SHOULD_HAVE_PROPAGATED_MESSAGE = "Exception should have been propagated";

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Rule
    private TestLoggerRule mTestLogger = new TestLoggerRule();

    private FileSystem mFileSystem;

    private FileSystemContext mFileContext;

    private ClientContext mClientContext;

    private FileSystemMasterClient mFileSystemMasterClient;

    private class DummyAlluxioFileSystem extends BaseFileSystem {
        public DummyAlluxioFileSystem(FileSystemContext fsContext) {
            super(fsContext, false);
        }
    }

    /**
     * Tests the creation of a file via the
     * {@link BaseFileSystem#createFile(AlluxioURI, CreateFilePOptions)} method.
     */
    @Test
    public void createFile() throws Exception {
        URIStatus status = new URIStatus(new FileInfo());
        AlluxioURI file = new AlluxioURI("/file");
        Mockito.when(mFileSystemMasterClient.createFile(ArgumentMatchers.any(AlluxioURI.class), ArgumentMatchers.any(CreateFilePOptions.class))).thenReturn(status);
        FileOutStream out = mFileSystem.createFile(file, CreateFilePOptions.getDefaultInstance());
        Mockito.verify(mFileSystemMasterClient).createFile(file, FileSystemOptions.createFileDefaults(mConf).toBuilder().mergeFrom(CreateFilePOptions.getDefaultInstance()).build());
        Assert.assertEquals(out.mUri, file);
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when creating a file system.
     */
    @Test
    public void createException() throws Exception {
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).createFile(ArgumentMatchers.any(AlluxioURI.class), ArgumentMatchers.any(CreateFilePOptions.class));
        try {
            mFileSystem.createFile(new AlluxioURI("/"), CreateFilePOptions.getDefaultInstance());
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#delete(AlluxioURI, DeletePOptions)} method.
     */
    @Test
    public void delete() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        DeletePOptions deleteOptions = DeletePOptions.newBuilder().setRecursive(true).build();
        mFileSystem.delete(file, deleteOptions);
        Mockito.verify(mFileSystemMasterClient).delete(file, FileSystemOptions.deleteDefaults(mConf).toBuilder().mergeFrom(deleteOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when deleting a file.
     */
    @Test
    public void deleteException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        DeletePOptions deleteOptions = DeletePOptions.newBuilder().setRecursive(true).build();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).delete(file, FileSystemOptions.deleteDefaults(mConf).toBuilder().mergeFrom(deleteOptions).build());
        try {
            mFileSystem.delete(file, deleteOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#free(AlluxioURI, FreePOptions)} method.
     */
    @Test
    public void free() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        FreePOptions freeOptions = FreePOptions.newBuilder().setRecursive(true).build();
        mFileSystem.free(file, freeOptions);
        Mockito.verify(mFileSystemMasterClient).free(file, FileSystemOptions.freeDefaults(mConf).toBuilder().mergeFrom(freeOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when freeing a file.
     */
    @Test
    public void freeException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        FreePOptions freeOptions = FreePOptions.newBuilder().setRecursive(true).build();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).free(file, FileSystemOptions.freeDefaults(mConf).toBuilder().mergeFrom(freeOptions).build());
        try {
            mFileSystem.free(file, freeOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#getStatus(AlluxioURI, GetStatusPOptions)} method.
     */
    @Test
    public void getStatus() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        URIStatus status = new URIStatus(new FileInfo());
        GetStatusPOptions getStatusOptions = GetStatusPOptions.getDefaultInstance();
        Mockito.when(mFileSystemMasterClient.getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build())).thenReturn(status);
        Assert.assertSame(status, mFileSystem.getStatus(file, getStatusOptions));
        Mockito.verify(mFileSystemMasterClient).getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when retrieving information.
     */
    @Test
    public void getStatusException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        GetStatusPOptions getStatusOptions = GetStatusPOptions.getDefaultInstance();
        Mockito.when(mFileSystemMasterClient.getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build())).thenThrow(BaseFileSystemTest.EXCEPTION);
        try {
            mFileSystem.getStatus(file, getStatusOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#listStatus(AlluxioURI, ListStatusPOptions)} method.
     */
    @Test
    public void listStatus() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        List<URIStatus> infos = new ArrayList<>();
        infos.add(new URIStatus(new FileInfo()));
        ListStatusPOptions listStatusOptions = ListStatusPOptions.getDefaultInstance();
        Mockito.when(mFileSystemMasterClient.listStatus(file, FileSystemOptions.listStatusDefaults(mConf).toBuilder().mergeFrom(listStatusOptions).build())).thenReturn(infos);
        Assert.assertSame(infos, mFileSystem.listStatus(file, listStatusOptions));
        Mockito.verify(mFileSystemMasterClient).listStatus(file, FileSystemOptions.listStatusDefaults(mConf).toBuilder().mergeFrom(listStatusOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when listing the status.
     */
    @Test
    public void listStatusException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        Mockito.when(mFileSystemMasterClient.listStatus(file, FileSystemOptions.listStatusDefaults(mConf).toBuilder().mergeFrom(ListStatusPOptions.getDefaultInstance()).build())).thenThrow(BaseFileSystemTest.EXCEPTION);
        try {
            mFileSystem.listStatus(file, ListStatusPOptions.getDefaultInstance());
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    // /**
    // * Tests for the {@link BaseFileSystem#loadMetadata(AlluxioURI, LoadMetadataOptions)}
    // * method.
    // */
    // @Test
    // public void loadMetadata() throws Exception {
    // AlluxioURI file = new AlluxioURI("/file");
    // LoadMetadataOptions loadMetadataOptions = LoadMetadataOptions.defaults().setRecursive(true);
    // doNothing().when(mFileSystemMasterClient).loadMetadata(file, loadMetadataOptions);
    // mFileSystem.loadMetadata(file, loadMetadataOptions);
    // verify(mFileSystemMasterClient).loadMetadata(file, loadMetadataOptions);
    // 
    // verifyFilesystemContextAcquiredAndReleased();
    // }
    // 
    // /**
    // * Ensures that an exception is propagated correctly when loading the metadata.
    // */
    // @Test
    // public void loadMetadataException() throws Exception {
    // AlluxioURI file = new AlluxioURI("/file");
    // LoadMetadataOptions loadMetadataOptions = LoadMetadataOptions.defaults().setRecursive(true);
    // doThrow(EXCEPTION).when(mFileSystemMasterClient)
    // .loadMetadata(file, loadMetadataOptions);
    // try {
    // mFileSystem.loadMetadata(file, loadMetadataOptions);
    // fail(SHOULD_HAVE_PROPAGATED_MESSAGE);
    // } catch (Exception e) {
    // assertSame(EXCEPTION, e);
    // }
    // 
    // verifyFilesystemContextAcquiredAndReleased();
    // }
    /**
     * Tests for the {@link BaseFileSystem#createDirectory(AlluxioURI, CreateDirectoryPOptions)}
     * method.
     */
    @Test
    public void createDirectory() throws Exception {
        AlluxioURI dir = new AlluxioURI("/dir");
        CreateDirectoryPOptions createDirectoryOptions = CreateDirectoryPOptions.getDefaultInstance();
        Mockito.doNothing().when(mFileSystemMasterClient).createDirectory(dir, FileSystemOptions.createDirectoryDefaults(mConf).toBuilder().mergeFrom(createDirectoryOptions).build());
        mFileSystem.createDirectory(dir, createDirectoryOptions);
        Mockito.verify(mFileSystemMasterClient).createDirectory(dir, FileSystemOptions.createDirectoryDefaults(mConf).toBuilder().mergeFrom(createDirectoryOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when creating a directory.
     */
    @Test
    public void createDirectoryException() throws Exception {
        AlluxioURI dir = new AlluxioURI("/dir");
        CreateDirectoryPOptions createDirectoryOptions = CreateDirectoryPOptions.getDefaultInstance();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).createDirectory(dir, FileSystemOptions.createDirectoryDefaults(mConf).toBuilder().mergeFrom(createDirectoryOptions).build());
        try {
            mFileSystem.createDirectory(dir, createDirectoryOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#mount(AlluxioURI, AlluxioURI, MountPOptions)} method.
     */
    @Test
    public void mount() throws Exception {
        AlluxioURI alluxioPath = new AlluxioURI("/t");
        AlluxioURI ufsPath = new AlluxioURI("/u");
        MountPOptions mountOptions = MountPOptions.getDefaultInstance();
        Mockito.doNothing().when(mFileSystemMasterClient).mount(alluxioPath, ufsPath, FileSystemOptions.mountDefaults(mConf).toBuilder().mergeFrom(mountOptions).build());
        mFileSystem.mount(alluxioPath, ufsPath, mountOptions);
        Mockito.verify(mFileSystemMasterClient).mount(alluxioPath, ufsPath, FileSystemOptions.mountDefaults(mConf).toBuilder().mergeFrom(mountOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated correctly when mounting a path.
     */
    @Test
    public void mountException() throws Exception {
        AlluxioURI alluxioPath = new AlluxioURI("/t");
        AlluxioURI ufsPath = new AlluxioURI("/u");
        MountPOptions mountOptions = MountPOptions.getDefaultInstance();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).mount(alluxioPath, ufsPath, FileSystemOptions.mountDefaults(mConf).toBuilder().mergeFrom(mountOptions).build());
        try {
            mFileSystem.mount(alluxioPath, ufsPath, mountOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#openFile(AlluxioURI, OpenFilePOptions)} method to
     * complete successfully.
     */
    @Test
    public void openFile() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        URIStatus status = new URIStatus(new FileInfo());
        GetStatusPOptions getStatusOptions = GetStatusPOptions.getDefaultInstance();
        Mockito.when(mFileSystemMasterClient.getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build())).thenReturn(status);
        mFileSystem.openFile(file, OpenFilePOptions.getDefaultInstance());
        Mockito.verify(mFileSystemMasterClient).getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build());
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Ensures that an exception is propagated successfully when opening a file.
     */
    @Test
    public void openException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        GetStatusPOptions getStatusOptions = GetStatusPOptions.getDefaultInstance();
        Mockito.when(mFileSystemMasterClient.getStatus(file, FileSystemOptions.getStatusDefaults(mConf).toBuilder().mergeFrom(getStatusOptions).build())).thenThrow(BaseFileSystemTest.EXCEPTION);
        try {
            mFileSystem.openFile(file, OpenFilePOptions.getDefaultInstance());
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
        verifyFilesystemContextAcquiredAndReleased();
    }

    /**
     * Tests for the {@link BaseFileSystem#rename(AlluxioURI, AlluxioURI, RenamePOptions)}
     * method.
     */
    @Test
    public void rename() throws Exception {
        AlluxioURI src = new AlluxioURI("/file");
        AlluxioURI dst = new AlluxioURI("/file2");
        RenamePOptions renameOptions = RenamePOptions.getDefaultInstance();
        Mockito.doNothing().when(mFileSystemMasterClient).rename(src, dst, FileSystemOptions.renameDefaults(mConf).toBuilder().mergeFrom(renameOptions).build());
        mFileSystem.rename(src, dst, renameOptions);
        Mockito.verify(mFileSystemMasterClient).rename(src, dst, FileSystemOptions.renameDefaults(mConf).toBuilder().mergeFrom(renameOptions).build());
    }

    /**
     * Ensures that an exception is propagated successfully when renaming a file.
     */
    @Test
    public void renameException() throws Exception {
        AlluxioURI src = new AlluxioURI("/file");
        AlluxioURI dst = new AlluxioURI("/file2");
        RenamePOptions renameOptions = RenamePOptions.getDefaultInstance();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).rename(src, dst, FileSystemOptions.renameDefaults(mConf).toBuilder().mergeFrom(renameOptions).build());
        try {
            mFileSystem.rename(src, dst, renameOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
    }

    /**
     * Tests for the {@link BaseFileSystem#setAttribute(AlluxioURI, SetAttributePOptions)} method.
     */
    @Test
    public void setAttribute() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        SetAttributePOptions setAttributeOptions = SetAttributePOptions.getDefaultInstance();
        mFileSystem.setAttribute(file, setAttributeOptions);
        Mockito.verify(mFileSystemMasterClient).setAttribute(file, setAttributeOptions);
    }

    /**
     * Ensures that an exception is propagated successfully when setting the state.
     */
    @Test
    public void setStateException() throws Exception {
        AlluxioURI file = new AlluxioURI("/file");
        SetAttributePOptions setAttributeOptions = SetAttributePOptions.getDefaultInstance();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).setAttribute(file, setAttributeOptions);
        try {
            mFileSystem.setAttribute(file, setAttributeOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
    }

    /**
     * Tests for the {@link BaseFileSystem#unmount(AlluxioURI, UnmountPOptions)} method.
     */
    @Test
    public void unmount() throws Exception {
        AlluxioURI path = new AlluxioURI("/");
        UnmountPOptions unmountOptions = UnmountPOptions.getDefaultInstance();
        Mockito.doNothing().when(mFileSystemMasterClient).unmount(path);
        mFileSystem.unmount(path, unmountOptions);
        Mockito.verify(mFileSystemMasterClient).unmount(path);
    }

    /**
     * Ensures that an exception is propagated successfully when unmounting a path.
     */
    @Test
    public void unmountException() throws Exception {
        AlluxioURI path = new AlluxioURI("/");
        UnmountPOptions unmountOptions = UnmountPOptions.getDefaultInstance();
        Mockito.doThrow(BaseFileSystemTest.EXCEPTION).when(mFileSystemMasterClient).unmount(path);
        try {
            mFileSystem.unmount(path, unmountOptions);
            Assert.fail(BaseFileSystemTest.SHOULD_HAVE_PROPAGATED_MESSAGE);
        } catch (Exception e) {
            Assert.assertSame(BaseFileSystemTest.EXCEPTION, e);
        }
    }

    /**
     * Ensures warnings are logged and an exception is thrown when an {@link AlluxioURI} with an
     * invalid authority is passed.
     */
    @Test
    public void uriCheckBadAuthority() throws Exception {
        mConf.set(MASTER_HOSTNAME, "localhost");
        mConf.set(MASTER_RPC_PORT, "19998");
        assertBadAuthority("localhost:1234", "Should fail on bad host and port");
        assertBadAuthority("zk@localhost:19998", "Should fail on zk authority");
        Assert.assertTrue(loggedAuthorityWarning());
        Assert.assertTrue(loggedSchemeWarning());
    }

    /**
     * Ensures an exception is thrown when an invalid scheme is passed.
     */
    @Test
    public void uriCheckBadScheme() throws Exception {
        mConf.set(MASTER_HOSTNAME, "localhost");
        mConf.set(MASTER_RPC_PORT, "19998");
        AlluxioURI uri = new AlluxioURI("hdfs://localhost:19998/root");
        try {
            mFileSystem.createDirectory(uri);
            Assert.fail("Should have failed on bad host and port");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Scheme hdfs:// in AlluxioURI is invalid"));
        }
    }

    /**
     * Ensures there is one warning when a URI with a valid scheme and authority is passed.
     */
    @Test
    public void uriCheckGoodSchemeAndAuthority() throws Exception {
        mConf.set(MASTER_HOSTNAME, "localhost");
        mConf.set(MASTER_RPC_PORT, "19998");
        before();// Resets the filesystem and contexts to use proper configuration.

        useUriWithAuthority("localhost:19998");
        Assert.assertTrue(loggedAuthorityWarning());
        Assert.assertTrue(loggedSchemeWarning());
    }

    /**
     * Ensures there is no warnings or errors when an {@link AlluxioURI} without a scheme and
     * authority is passed.
     */
    @Test
    public void uriCheckNoSchemeAuthority() throws Exception {
        mConf.set(MASTER_HOSTNAME, "localhost");
        mConf.set(MASTER_RPC_PORT, "19998");
        AlluxioURI uri = new AlluxioURI("/root");
        mFileSystem.createDirectory(uri);
        Assert.assertFalse(loggedAuthorityWarning());
        Assert.assertFalse(loggedSchemeWarning());
    }

    @Test
    public void uriCheckZkAuthorityMatch() throws Exception {
        configureZk("a:0,b:0,c:0");
        useUriWithAuthority("zk@a:0,b:0,c:0");// Same authority

        useUriWithAuthority("zk@a:0;b:0+c:0");// Same authority, but different delimiters

    }

    @Test
    public void uriCheckZkAuthorityMismatch() throws Exception {
        configureZk("a:0,b:0,c:0");
        assertBadAuthority("a:0,b:0,c:0", "Should fail on non-zk authority");
        assertBadAuthority("zk@a:0", "Should fail on zk authority with different addresses");
        assertBadAuthority("zk@a:0,b:0,c:1", "Should fail on zk authority with different addresses");
    }
}

