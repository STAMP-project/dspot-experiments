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
package alluxio.job.migrate;


import WriteType.THROUGH;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.MockFileInStream;
import alluxio.client.file.MockFileOutStream;
import alluxio.grpc.DeletePOptions;
import alluxio.underfs.UfsManager;
import alluxio.util.io.BufferUtils;
import alluxio.wire.FileInfo;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;


/**
 * Unit tests for {@link MigrateDefinition#runTask(MigrateConfig, ArrayList, JobWorkerContext)}.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({ FileSystemContext.class })
public final class MigrateDefinitionRunTaskTest {
    private static final String TEST_DIR = "/DIR";

    private static final String TEST_SOURCE = "/DIR/TEST_SOURCE";

    private static final String TEST_DESTINATION = "/DIR/TEST_DESTINATION";

    private static final byte[] TEST_SOURCE_CONTENTS = BufferUtils.getIncreasingByteArray(100);

    private FileSystem mMockFileSystem;

    private FileSystemContext mMockFileSystemContext;

    private MockFileInStream mMockInStream;

    private MockFileOutStream mMockOutStream;

    private UfsManager mMockUfsManager;

    @Parameterized.Parameter
    public boolean mDeleteSource;

    /**
     * Tests that the bytes of the file to migrate are written to the destination stream.
     * When deleteSource is true, the source is deleted, otherwise, it is kept.
     */
    @Test
    public void basicMigrateTest() throws Exception {
        runTask(MigrateDefinitionRunTaskTest.TEST_SOURCE, MigrateDefinitionRunTaskTest.TEST_SOURCE, MigrateDefinitionRunTaskTest.TEST_DESTINATION, THROUGH);
        Assert.assertArrayEquals(MigrateDefinitionRunTaskTest.TEST_SOURCE_CONTENTS, mMockOutStream.toByteArray());
        if (mDeleteSource) {
            Mockito.verify(mMockFileSystem).delete(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_SOURCE));
        } else {
            Mockito.verify(mMockFileSystem, Mockito.never()).delete(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_SOURCE));
        }
    }

    /**
     * Tests that when deleteSource is true,
     * the worker will delete the source directory if the directory contains nothing,
     * otherwise, the source directory is kept.
     */
    @Test
    public void deleteEmptySourceDir() throws Exception {
        Mockito.when(mMockFileSystem.listStatus(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR))).thenReturn(Lists.newArrayList());
        runTask(MigrateDefinitionRunTaskTest.TEST_DIR, MigrateDefinitionRunTaskTest.TEST_SOURCE, MigrateDefinitionRunTaskTest.TEST_DESTINATION, THROUGH);
        if (mDeleteSource) {
            Mockito.verify(mMockFileSystem).delete(ArgumentMatchers.eq(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR)), ArgumentMatchers.any(DeletePOptions.class));
        } else {
            Mockito.verify(mMockFileSystem, Mockito.never()).delete(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR));
        }
    }

    /**
     * Tests that when deleteSource is true,
     * the worker will delete the source directory if the directory contains only directories,
     * otherwise, the source directory is kept.
     */
    @Test
    public void deleteDirsOnlySourceDir() throws Exception {
        String inner = (MigrateDefinitionRunTaskTest.TEST_DIR) + "/innerDir";
        Mockito.when(mMockFileSystem.listStatus(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR))).thenReturn(Lists.newArrayList(new alluxio.client.file.URIStatus(new FileInfo().setPath(inner).setFolder(true))));
        Mockito.when(mMockFileSystem.listStatus(new AlluxioURI(inner))).thenReturn(Lists.newArrayList());
        runTask(MigrateDefinitionRunTaskTest.TEST_DIR, MigrateDefinitionRunTaskTest.TEST_SOURCE, MigrateDefinitionRunTaskTest.TEST_DESTINATION, THROUGH);
        if (mDeleteSource) {
            Mockito.verify(mMockFileSystem).delete(ArgumentMatchers.eq(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR)), ArgumentMatchers.any(DeletePOptions.class));
        } else {
            Mockito.verify(mMockFileSystem, Mockito.never()).delete(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR));
        }
    }

    /**
     * Tests that the worker will not delete the source directory if the directory still contains
     * files because this means not all files have been migrated.
     */
    @Test
    public void dontDeleteNonEmptySourceTest() throws Exception {
        Mockito.when(mMockFileSystem.listStatus(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR))).thenReturn(Lists.newArrayList(new alluxio.client.file.URIStatus(new FileInfo())));
        runTask(MigrateDefinitionRunTaskTest.TEST_DIR, MigrateDefinitionRunTaskTest.TEST_SOURCE, MigrateDefinitionRunTaskTest.TEST_DESTINATION, THROUGH);
        Mockito.verify(mMockFileSystem, Mockito.never()).delete(ArgumentMatchers.eq(new AlluxioURI(MigrateDefinitionRunTaskTest.TEST_DIR)), ArgumentMatchers.any(DeletePOptions.class));
    }
}

