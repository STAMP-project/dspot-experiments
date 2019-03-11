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


import ExceptionMessage.MIGRATE_CANNOT_BE_TO_SUBDIRECTORY;
import ExceptionMessage.MIGRATE_DIRECTORY_TO_FILE;
import ExceptionMessage.MIGRATE_FILE_TO_DIRECTORY;
import ExceptionMessage.MIGRATE_NEED_OVERWRITE;
import ExceptionMessage.MIGRATE_TO_FILE_AS_DIRECTORY;
import ExceptionMessage.PATH_DOES_NOT_EXIST;
import alluxio.AlluxioURI;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.exception.FileAlreadyExistsException;
import alluxio.exception.FileDoesNotExistException;
import alluxio.underfs.UfsManager;
import alluxio.wire.FileInfo;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for {@link MigrateDefinition#selectExecutors(MigrateConfig, List, JobMasterContext)}.
 * No matter whether to delete source, selectExecutors should have the same behavior.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AlluxioBlockStore.class, FileSystemContext.class })
public final class MigrateDefinitionSelectExecutorsTest {
    private static final List<BlockWorkerInfo> BLOCK_WORKERS = new ImmutableList.Builder<BlockWorkerInfo>().add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host0"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host1"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host2"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host3"), 0, 0)).build();

    private static final WorkerInfo JOB_WORKER_0 = new WorkerInfo().setAddress(new WorkerNetAddress().setHost("host0"));

    private static final WorkerInfo JOB_WORKER_1 = new WorkerInfo().setAddress(new WorkerNetAddress().setHost("host1"));

    private static final WorkerInfo JOB_WORKER_2 = new WorkerInfo().setAddress(new WorkerNetAddress().setHost("host2"));

    private static final WorkerInfo JOB_WORKER_3 = new WorkerInfo().setAddress(new WorkerNetAddress().setHost("host3"));

    private static final List<WorkerInfo> JOB_WORKERS = ImmutableList.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKER_0, MigrateDefinitionSelectExecutorsTest.JOB_WORKER_1, MigrateDefinitionSelectExecutorsTest.JOB_WORKER_2, MigrateDefinitionSelectExecutorsTest.JOB_WORKER_3);

    private FileSystem mMockFileSystem;

    private FileSystemContext mMockFileSystemContext;

    private AlluxioBlockStore mMockBlockStore;

    private UfsManager mMockUfsManager;

    @Test
    public void migrateToSelf() throws Exception {
        createDirectory("/src");
        Assert.assertEquals(Maps.newHashMap(), assignMigrates("/src", "/src"));
    }

    @Test
    public void assignToLocalWorker() throws Exception {
        createFileWithBlocksOnWorkers("/src", 0);
        setPathToNotExist("/dst");
        Map<WorkerInfo, List<MigrateCommand>> expected = ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(0), Collections.singletonList(new MigrateCommand("/src", "/dst")));
        Assert.assertEquals(expected, assignMigrates("/src", "/dst"));
    }

    @Test
    public void assignToWorkerWithMostBlocks() throws Exception {
        createFileWithBlocksOnWorkers("/src", 3, 1, 1, 3, 1);
        setPathToNotExist("/dst");
        Map<WorkerInfo, List<MigrateCommand>> expected = ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(1), Collections.singletonList(new MigrateCommand("/src", "/dst")));
        Assert.assertEquals(expected, assignMigrates("/src", "/dst"));
    }

    @Test
    public void assignToLocalWorkerWithMostBlocksMultipleFiles() throws Exception {
        createDirectory("/dir");
        // Should go to worker 0.
        FileInfo info1 = createFileWithBlocksOnWorkers("/dir/src1", 0, 1, 2, 3, 0);
        // Should go to worker 2.
        FileInfo info2 = createFileWithBlocksOnWorkers("/dir/src2", 1, 1, 2, 2, 2);
        // Should go to worker 0.
        FileInfo info3 = createFileWithBlocksOnWorkers("/dir/src3", 2, 0, 0, 1, 1, 0);
        setChildren("/dir", info1, info2, info3);
        // Say the destination doesn't exist.
        setPathToNotExist("/dst");
        List<MigrateCommand> migrateCommandsWorker0 = Lists.newArrayList(new MigrateCommand("/dir/src1", "/dst/src1"), new MigrateCommand("/dir/src3", "/dst/src3"));
        List<MigrateCommand> migrateCommandsWorker2 = Lists.newArrayList(new MigrateCommand("/dir/src2", "/dst/src2"));
        ImmutableMap<WorkerInfo, List<MigrateCommand>> expected = ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(0), migrateCommandsWorker0, MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(2), migrateCommandsWorker2);
        Assert.assertEquals(expected, assignMigrates("/dir", "/dst"));
    }

    @Test
    public void migrateEmptyDirectory() throws Exception {
        createDirectory("/src");
        createDirectory("/dst");
        setPathToNotExist("/dst/src");
        assignMigrates("/src", "/dst/src");
        Mockito.verify(mMockFileSystem).createDirectory(ArgumentMatchers.eq(new AlluxioURI("/dst/src")));
    }

    @Test
    public void migrateNestedEmptyDirectory() throws Exception {
        createDirectory("/src");
        FileInfo nested = createDirectory("/src/nested");
        setChildren("/src", nested);
        createDirectory("/dst");
        setPathToNotExist("/dst/src");
        assignMigrates("/src", "/dst/src");
        Mockito.verify(mMockFileSystem).createDirectory(ArgumentMatchers.eq(new AlluxioURI("/dst/src/nested")));
    }

    @Test
    public void migrateToSubpath() throws Exception {
        try {
            assignMigratesFail("/src", "/src/dst");
        } catch (RuntimeException e) {
            Assert.assertEquals(MIGRATE_CANNOT_BE_TO_SUBDIRECTORY.getMessage("/src", "/src/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateMissingSource() throws Exception {
        setPathToNotExist("/notExist");
        try {
            assignMigratesFail("/notExist", "/dst");
        } catch (FileDoesNotExistException e) {
            Assert.assertEquals(PATH_DOES_NOT_EXIST.getMessage("/notExist"), e.getMessage());
        }
    }

    @Test
    public void migrateMissingDestinationParent() throws Exception {
        createDirectory("/src");
        setPathToNotExist("/dst");
        setPathToNotExist("/dst/missing");
        try {
            assignMigratesFail("/src", "/dst/missing");
        } catch (Exception e) {
            Assert.assertEquals(PATH_DOES_NOT_EXIST.getMessage("/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateIntoFile() throws Exception {
        createFile("/src");
        createFile("/dst");
        setPathToNotExist("/dst/src");
        try {
            assignMigratesFail("/src", "/dst/src");
        } catch (Exception e) {
            Assert.assertEquals(MIGRATE_TO_FILE_AS_DIRECTORY.getMessage("/dst/src", "/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateFileToDirectory() throws Exception {
        createFile("/src");
        createDirectory("/dst");
        try {
            assignMigratesFail("/src", "/dst");
        } catch (Exception e) {
            Assert.assertEquals(MIGRATE_FILE_TO_DIRECTORY.getMessage("/src", "/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateDirectoryToFile() throws Exception {
        createDirectory("/src");
        createFile("/dst");
        try {
            assignMigratesFail("/src", "/dst");
        } catch (Exception e) {
            Assert.assertEquals(MIGRATE_DIRECTORY_TO_FILE.getMessage("/src", "/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateFileToExistingDestinationWithoutOverwrite() throws Exception {
        createFile("/src");
        createFile("/dst");
        // Test with source being a file.
        try {
            assignMigratesFail("/src", "/dst");
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(MIGRATE_NEED_OVERWRITE.getMessage("/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateDirectoryToExistingDestinationWithoutOverwrite() throws Exception {
        // Test with the source being a folder.
        createDirectory("/src");
        createDirectory("/dst");
        try {
            assignMigratesFail("/src", "/dst");
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(MIGRATE_NEED_OVERWRITE.getMessage("/dst"), e.getMessage());
        }
    }

    @Test
    public void migrateFileToExistingDestinationWithOverwrite() throws Exception {
        createFileWithBlocksOnWorkers("/src", 0);
        createFile("/dst");
        Map<WorkerInfo, List<MigrateCommand>> expected = ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(0), Collections.singletonList(new MigrateCommand("/src", "/dst")));
        // Set overwrite to true.
        Assert.assertEquals(expected, assignMigrates(new MigrateConfig("/src", "/dst", "THROUGH", true, false)));
    }

    @Test
    public void migrateDirectoryIntoDirectoryWithOverwrite() throws Exception {
        createDirectory("/src");
        FileInfo nested = createDirectory("/src/nested");
        FileInfo moreNested = createDirectory("/src/nested/moreNested");
        FileInfo file1 = createFileWithBlocksOnWorkers("/src/file1", 2);
        FileInfo file2 = createFileWithBlocksOnWorkers("/src/nested/file2", 1);
        FileInfo file3 = createFileWithBlocksOnWorkers("/src/nested/moreNested/file3", 1);
        setChildren("/src", nested, file1);
        setChildren("/src/nested", moreNested, file2);
        setChildren("/src/nested/moreNested", file3);
        createDirectory("/dst");
        List<MigrateCommand> migrateCommandsWorker1 = Lists.newArrayList(new MigrateCommand("/src/nested/file2", "/dst/nested/file2"), new MigrateCommand("/src/nested/moreNested/file3", "/dst/nested/moreNested/file3"));
        List<MigrateCommand> migrateCommandsWorker2 = Lists.newArrayList(new MigrateCommand("/src/file1", "/dst/file1"));
        ImmutableMap<WorkerInfo, List<MigrateCommand>> expected = ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(1), migrateCommandsWorker1, MigrateDefinitionSelectExecutorsTest.JOB_WORKERS.get(2), migrateCommandsWorker2);
        Assert.assertEquals(expected, assignMigrates(new MigrateConfig("/src", "/dst", "THROUGH", true, false)));
    }

    @Test
    public void migrateUncachedFile() throws Exception {
        createFileWithBlocksOnWorkers("/src");
        setPathToNotExist("/dst");
        Assert.assertEquals(1, assignMigrates("/src", "/dst").size());
    }

    @Test
    public void migrateNoLocalJobWorker() throws Exception {
        createFileWithBlocksOnWorkers("/src", 0);
        setPathToNotExist("/dst");
        Map<WorkerInfo, ArrayList<MigrateCommand>> assignments = new MigrateDefinition(mMockFileSystemContext, mMockFileSystem).selectExecutors(new MigrateConfig("/src", "/dst", "THROUGH", true, false), ImmutableList.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKER_3), new alluxio.job.JobMasterContext(1, mMockUfsManager));
        Assert.assertEquals(ImmutableMap.of(MigrateDefinitionSelectExecutorsTest.JOB_WORKER_3, new ArrayList(Arrays.asList(new MigrateCommand("/src", "/dst")))), assignments);
    }
}

