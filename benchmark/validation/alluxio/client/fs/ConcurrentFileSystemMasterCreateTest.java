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


import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.USER_FILE_MASTER_CLIENT_THREADS;
import WritePType.CACHE_THROUGH;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.AuthenticatedUserRule;
import alluxio.Constants;
import alluxio.UnderFileSystemFactoryRegistryRule;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.testutils.underfs.sleeping.SleepingUnderFileSystemFactory;
import alluxio.testutils.underfs.sleeping.SleepingUnderFileSystemOptions;
import com.google.common.io.Files;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static alluxio.client.fs.ConcurrentFileSystemMasterUtils.UnaryOperation.CREATE;


/**
 * Tests to validate the concurrency in {@link FileSystemMaster}. These tests all use a local
 * path as the under storage system.
 *
 * The tests validate the correctness of concurrent operations, ie. no corrupted/partial state is
 * exposed, through a series of concurrent operations followed by verification of the final
 * state, or inspection of the in-progress state as the operations are carried out.
 *
 * The tests also validate that operations are concurrent by injecting a short sleep in the
 * critical code path. Tests will timeout if the critical section is performed serially.
 */
public class ConcurrentFileSystemMasterCreateTest extends BaseIntegrationTest {
    private static final String TEST_USER = "test";

    private static final int CONCURRENCY_FACTOR = 50;

    /**
     * Duration to sleep during the rename call to show the benefits of concurrency.
     */
    private static final long SLEEP_MS = (Constants.SECOND_MS) / 10;

    private FileSystem mFileSystem;

    private String mLocalUfsPath = Files.createTempDir().getAbsolutePath();

    @Rule
    public AuthenticatedUserRule mAuthenticatedUser = new AuthenticatedUserRule(ConcurrentFileSystemMasterCreateTest.TEST_USER, ServerConfiguration.global());

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(MASTER_MOUNT_TABLE_ROOT_UFS, ("sleep://" + (mLocalUfsPath))).setProperty(USER_FILE_MASTER_CLIENT_THREADS, ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR).build();

    @ClassRule
    public static UnderFileSystemFactoryRegistryRule sUnderfilesystemfactoryregistry = new UnderFileSystemFactoryRegistryRule(new SleepingUnderFileSystemFactory(new SleepingUnderFileSystemOptions().setMkdirsMs(ConcurrentFileSystemMasterCreateTest.SLEEP_MS).setIsDirectoryMs(ConcurrentFileSystemMasterCreateTest.SLEEP_MS).setGetFileStatusMs(ConcurrentFileSystemMasterCreateTest.SLEEP_MS).setIsFileMs(ConcurrentFileSystemMasterCreateTest.SLEEP_MS)));

    /**
     * Tests concurrent create of files. Files are created under one shared directory but different
     * sub-directories.
     */
    @Test
    public void concurrentCreate() throws Exception {
        final int numThreads = ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR;
        // 7 nested components to create (2 seconds each).
        final long limitMs = ((14 * (ConcurrentFileSystemMasterCreateTest.SLEEP_MS)) * (ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR)) / 2;
        AlluxioURI[] paths = new AlluxioURI[numThreads];
        for (int i = 0; i < numThreads; i++) {
            paths[i] = new AlluxioURI(((("/existing/path/dir/shared_dir/t_" + i) + "/sub_dir1/sub_dir2/file") + i));
        }
        List<Throwable> errors = ConcurrentFileSystemMasterUtils.unaryOperation(mFileSystem, CREATE, paths, limitMs);
        if (!(errors.isEmpty())) {
            Assert.fail(((("Encountered " + (errors.size())) + " errors, the first one is ") + (errors.get(0))));
        }
    }

    /**
     * Test concurrent create of existing directory. Existing directory is created as CACHE_THROUGH
     * then files are created under that directory.
     */
    @Test
    public void concurrentCreateExistingDir() throws Exception {
        final int numThreads = ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR;
        // 7 nested components to create (2 seconds each).
        final long limitMs = ((14 * (ConcurrentFileSystemMasterCreateTest.SLEEP_MS)) * (ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR)) / 2;
        AlluxioURI[] paths = new AlluxioURI[numThreads];
        // Create the existing path with CACHE_THROUGH that it will be persisted.
        mFileSystem.createDirectory(new AlluxioURI("/existing/path/dir/"), CreateDirectoryPOptions.newBuilder().setRecursive(true).setWriteType(CACHE_THROUGH).build());
        for (int i = 0; i < numThreads; i++) {
            paths[i] = new AlluxioURI(((("/existing/path/dir/shared_dir/t_" + i) + "/sub_dir1/sub_dir2/file") + i));
        }
        List<Throwable> errors = ConcurrentFileSystemMasterUtils.unaryOperation(mFileSystem, CREATE, paths, limitMs);
        if (!(errors.isEmpty())) {
            Assert.fail(((("Encountered " + (errors.size())) + " errors, the first one is ") + (errors.get(0))));
        }
    }

    /**
     * Test concurrent create of non-persisted directory. Directory is created as MUST_CACHE then
     * files are created under that directory.
     */
    @Test
    public void concurrentCreateNonPersistedDir() throws Exception {
        final int numThreads = ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR;
        // 7 nested components to create (2 seconds each).
        final long limitMs = ((14 * (ConcurrentFileSystemMasterCreateTest.SLEEP_MS)) * (ConcurrentFileSystemMasterCreateTest.CONCURRENCY_FACTOR)) / 2;
        AlluxioURI[] paths = new AlluxioURI[numThreads];
        // Create the existing path with MUST_CACHE, so subsequent creates have to persist the dirs.
        mFileSystem.createDirectory(new AlluxioURI("/existing/path/dir/"), CreateDirectoryPOptions.newBuilder().setRecursive(true).setWriteType(MUST_CACHE).build());
        for (int i = 0; i < numThreads; i++) {
            paths[i] = new AlluxioURI(((("/existing/path/dir/shared_dir/t_" + i) + "/sub_dir1/sub_dir2/file") + i));
        }
        List<Throwable> errors = ConcurrentFileSystemMasterUtils.unaryOperation(mFileSystem, CREATE, paths, limitMs);
        if (!(errors.isEmpty())) {
            Assert.fail(((("Encountered " + (errors.size())) + " errors, the first one is ") + (errors.get(0))));
        }
    }

    @Test
    public void concurrentLoadFileMetadata() throws Exception {
        runLoadMetadata(null, false, true, false);
    }

    @Test
    public void concurrentLoadFileMetadataExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, false, true, false);
    }

    @Test
    public void concurrentLoadFileMetadataNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, false, true, false);
    }

    @Test
    public void concurrentLoadSameFileMetadata() throws Exception {
        runLoadMetadata(null, true, true, false);
    }

    @Test
    public void concurrentLoadSameFileMetadataExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, true, true, false);
    }

    @Test
    public void concurrentLoadSameFileMetadataNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, true, true, false);
    }

    @Test
    public void concurrentLoadDirMetadata() throws Exception {
        runLoadMetadata(null, false, false, false);
    }

    @Test
    public void concurrentLoadDirMetadataExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, false, false, false);
    }

    @Test
    public void concurrentLoadDirMetadataNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, false, false, false);
    }

    @Test
    public void concurrentLoadSameDirMetadata() throws Exception {
        runLoadMetadata(null, true, false, false);
    }

    @Test
    public void concurrentLoadSameDirMetadataExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, true, false, false);
    }

    @Test
    public void concurrentLoadSameDirMetadataNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, true, false, false);
    }

    @Test
    public void concurrentListDirs() throws Exception {
        runLoadMetadata(null, false, false, true);
    }

    @Test
    public void concurrentListDirsExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, false, false, true);
    }

    @Test
    public void concurrentListDirsNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, false, false, true);
    }

    @Test
    public void concurrentListFiles() throws Exception {
        runLoadMetadata(null, false, true, true);
    }

    @Test
    public void concurrentListFilesExistingDir() throws Exception {
        runLoadMetadata(CACHE_THROUGH, false, true, true);
    }

    @Test
    public void concurrentListFilesNonPersistedDir() throws Exception {
        runLoadMetadata(MUST_CACHE, false, true, true);
    }
}

