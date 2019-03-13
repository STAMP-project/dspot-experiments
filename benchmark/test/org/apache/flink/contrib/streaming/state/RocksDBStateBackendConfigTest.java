/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.contrib.streaming.state;


import CheckpointingOptions.INCREMENTAL_CHECKPOINTS;
import CompactionStyle.FIFO;
import CompactionStyle.LEVEL;
import CompactionStyle.UNIVERSAL;
import CoreOptions.DEFAULT_FILESYSTEM_SCHEME;
import IntSerializer.INSTANCE;
import PredefinedOptions.FLASH_SSD_OPTIMIZED;
import PredefinedOptions.SPINNING_DISK_OPTIMIZED;
import RocksDBConfigurableOptions.BLOCK_CACHE_SIZE;
import RocksDBConfigurableOptions.BLOCK_SIZE;
import RocksDBConfigurableOptions.COMPACTION_STYLE;
import RocksDBConfigurableOptions.MAX_BACKGROUND_THREADS;
import RocksDBConfigurableOptions.MAX_SIZE_LEVEL_BASE;
import RocksDBConfigurableOptions.MAX_WRITE_BUFFER_NUMBER;
import RocksDBConfigurableOptions.MIN_WRITE_BUFFER_NUMBER_TO_MERGE;
import RocksDBConfigurableOptions.TARGET_FILE_SIZE_BASE;
import RocksDBConfigurableOptions.USE_DYNAMIC_LEVEL_SIZE;
import RocksDBConfigurableOptions.WRITE_BUFFER_SIZE;
import RocksDBOptions.OPTIONS_FACTORY;
import RocksDBOptions.PREDEFINED_OPTIONS;
import RocksDBOptions.TIMER_SERVICE_FACTORY;
import RocksDBStateBackend.PriorityQueueStateType;
import RocksDBStateBackend.PriorityQueueStateType.HEAP;
import RocksDBStateBackend.PriorityQueueStateType.ROCKSDB;
import TtlTimeProvider.DEFAULT;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.query.KvStateRegistry;
import org.apache.flink.runtime.state.AbstractKeyedStateBackend;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.heap.HeapPriorityQueueSetFactory;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.util.IOUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.util.SizeUnit;

import static PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH_MEM;


/**
 * Tests for configuring the RocksDB State Backend.
 */
@SuppressWarnings("serial")
public class RocksDBStateBackendConfigTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    // ------------------------------------------------------------------------
    // default values
    // ------------------------------------------------------------------------
    @Test
    public void testDefaultsInSync() throws Exception {
        final boolean defaultIncremental = INCREMENTAL_CHECKPOINTS.defaultValue();
        RocksDBStateBackend backend = new RocksDBStateBackend(tempFolder.newFolder().toURI());
        Assert.assertEquals(defaultIncremental, backend.isIncrementalCheckpointsEnabled());
    }

    // ------------------------------------------------------------------------
    // RocksDB local file directory
    // ------------------------------------------------------------------------
    /**
     * This test checks the behavior for basic setting of local DB directories.
     */
    @Test
    public void testSetDbPath() throws Exception {
        final RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(tempFolder.newFolder().toURI().toString());
        final String testDir1 = tempFolder.newFolder().getAbsolutePath();
        final String testDir2 = tempFolder.newFolder().getAbsolutePath();
        Assert.assertNull(rocksDbBackend.getDbStoragePaths());
        rocksDbBackend.setDbStoragePath(testDir1);
        Assert.assertArrayEquals(new String[]{ testDir1 }, rocksDbBackend.getDbStoragePaths());
        rocksDbBackend.setDbStoragePath(null);
        Assert.assertNull(rocksDbBackend.getDbStoragePaths());
        rocksDbBackend.setDbStoragePaths(testDir1, testDir2);
        Assert.assertArrayEquals(new String[]{ testDir1, testDir2 }, rocksDbBackend.getDbStoragePaths());
        final Environment env = RocksDBStateBackendConfigTest.getMockEnvironment(tempFolder.newFolder());
        final RocksDBKeyedStateBackend<Integer> keyedBackend = RocksDBStateBackendConfigTest.createKeyedStateBackend(rocksDbBackend, env);
        try {
            File instanceBasePath = keyedBackend.getInstanceBasePath();
            MatcherAssert.assertThat(instanceBasePath.getAbsolutePath(), CoreMatchers.anyOf(Matchers.startsWith(testDir1), Matchers.startsWith(testDir2)));
            // noinspection NullArgumentToVariableArgMethod
            rocksDbBackend.setDbStoragePaths(null);
            Assert.assertNull(rocksDbBackend.getDbStoragePaths());
        } finally {
            IOUtils.closeQuietly(keyedBackend);
            keyedBackend.dispose();
        }
    }

    @Test
    public void testConfigureTimerService() throws Exception {
        final Environment env = RocksDBStateBackendConfigTest.getMockEnvironment(tempFolder.newFolder());
        // Fix the option key string
        Assert.assertEquals("state.backend.rocksdb.timer-service.factory", TIMER_SERVICE_FACTORY.key());
        // Fix the option value string and ensure all are covered
        Assert.assertEquals(2, PriorityQueueStateType.values().length);
        Assert.assertEquals("ROCKSDB", ROCKSDB.toString());
        Assert.assertEquals("HEAP", HEAP.toString());
        // Fix the default
        Assert.assertEquals(HEAP.toString(), TIMER_SERVICE_FACTORY.defaultValue());
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(tempFolder.newFolder().toURI().toString());
        RocksDBKeyedStateBackend<Integer> keyedBackend = RocksDBStateBackendConfigTest.createKeyedStateBackend(rocksDbBackend, env);
        Assert.assertEquals(HeapPriorityQueueSetFactory.class, keyedBackend.getPriorityQueueFactory().getClass());
        keyedBackend.dispose();
        Configuration conf = new Configuration();
        conf.setString(TIMER_SERVICE_FACTORY, ROCKSDB.toString());
        rocksDbBackend = rocksDbBackend.configure(conf, Thread.currentThread().getContextClassLoader());
        keyedBackend = RocksDBStateBackendConfigTest.createKeyedStateBackend(rocksDbBackend, env);
        Assert.assertEquals(RocksDBPriorityQueueSetFactory.class, keyedBackend.getPriorityQueueFactory().getClass());
        keyedBackend.dispose();
    }

    @Test
    public void testStoragePathWithFilePrefix() throws Exception {
        final File folder = tempFolder.newFolder();
        final String dbStoragePath = new Path(folder.toURI().toString()).toString();
        Assert.assertTrue(dbStoragePath.startsWith("file:"));
        testLocalDbPaths(dbStoragePath, folder);
    }

    @Test
    public void testWithDefaultFsSchemeNoStoragePath() throws Exception {
        try {
            // set the default file system scheme
            Configuration config = new Configuration();
            config.setString(DEFAULT_FILESYSTEM_SCHEME, "s3://mydomain.com:8020/flink");
            FileSystem.initialize(config);
            testLocalDbPaths(null, tempFolder.getRoot());
        } finally {
            FileSystem.initialize(new Configuration());
        }
    }

    @Test
    public void testWithDefaultFsSchemeAbsoluteStoragePath() throws Exception {
        final File folder = tempFolder.newFolder();
        final String dbStoragePath = folder.getAbsolutePath();
        try {
            // set the default file system scheme
            Configuration config = new Configuration();
            config.setString(DEFAULT_FILESYSTEM_SCHEME, "s3://mydomain.com:8020/flink");
            FileSystem.initialize(config);
            testLocalDbPaths(dbStoragePath, folder);
        } finally {
            FileSystem.initialize(new Configuration());
        }
    }

    /**
     * Validates that empty arguments for the local DB path are invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyPaths() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        rocksDbBackend.setDbStoragePaths();
    }

    /**
     * Validates that schemes other than 'file:/' are not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNonFileSchemePath() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        rocksDbBackend.setDbStoragePath("hdfs:///some/path/to/perdition");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDbPathRelativePaths() throws Exception {
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(tempFolder.newFolder().toURI().toString());
        rocksDbBackend.setDbStoragePath("relative/path");
    }

    // ------------------------------------------------------------------------
    // RocksDB local file automatic from temp directories
    // ------------------------------------------------------------------------
    /**
     * This tests whether the RocksDB backends uses the temp directories that are provided
     * from the {@link Environment} when no db storage path is set.
     */
    @Test
    public void testUseTempDirectories() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        File dir1 = tempFolder.newFolder();
        File dir2 = tempFolder.newFolder();
        Assert.assertNull(rocksDbBackend.getDbStoragePaths());
        Environment env = RocksDBStateBackendConfigTest.getMockEnvironment(dir1, dir2);
        RocksDBKeyedStateBackend<Integer> keyedBackend = ((RocksDBKeyedStateBackend<Integer>) (rocksDbBackend.createKeyedStateBackend(env, env.getJobID(), "test_op", INSTANCE, 1, new KeyGroupRange(0, 0), env.getTaskKvStateRegistry(), DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), new CloseableRegistry())));
        try {
            File instanceBasePath = keyedBackend.getInstanceBasePath();
            MatcherAssert.assertThat(instanceBasePath.getAbsolutePath(), CoreMatchers.anyOf(Matchers.startsWith(dir1.getAbsolutePath()), Matchers.startsWith(dir2.getAbsolutePath())));
        } finally {
            IOUtils.closeQuietly(keyedBackend);
            keyedBackend.dispose();
        }
    }

    // ------------------------------------------------------------------------
    // RocksDB local file directory initialization
    // ------------------------------------------------------------------------
    @Test
    public void testFailWhenNoLocalStorageDir() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        File targetDir = tempFolder.newFolder();
        try {
            if (!(targetDir.setWritable(false, false))) {
                System.err.println("Cannot execute 'testFailWhenNoLocalStorageDir' because cannot mark directory non-writable");
                return;
            }
            rocksDbBackend.setDbStoragePath(targetDir.getAbsolutePath());
            boolean hasFailure = false;
            try {
                Environment env = RocksDBStateBackendConfigTest.getMockEnvironment(tempFolder.newFolder());
                rocksDbBackend.createKeyedStateBackend(env, env.getJobID(), "foobar", INSTANCE, 1, new KeyGroupRange(0, 0), new KvStateRegistry().createTaskRegistry(env.getJobID(), new JobVertexID()), DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), new CloseableRegistry());
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("No local storage directories available"));
                Assert.assertTrue(e.getMessage().contains(targetDir.getAbsolutePath()));
                hasFailure = true;
            }
            Assert.assertTrue("We must see a failure because no storaged directory is feasible.", hasFailure);
        } finally {
            // noinspection ResultOfMethodCallIgnored
            targetDir.setWritable(true, false);
        }
    }

    @Test
    public void testContinueOnSomeDbDirectoriesMissing() throws Exception {
        File targetDir1 = tempFolder.newFolder();
        File targetDir2 = tempFolder.newFolder();
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        try {
            if (!(targetDir1.setWritable(false, false))) {
                System.err.println("Cannot execute 'testContinueOnSomeDbDirectoriesMissing' because cannot mark directory non-writable");
                return;
            }
            rocksDbBackend.setDbStoragePaths(targetDir1.getAbsolutePath(), targetDir2.getAbsolutePath());
            try {
                Environment env = RocksDBStateBackendConfigTest.getMockEnvironment(tempFolder.newFolder());
                AbstractKeyedStateBackend<Integer> keyedStateBackend = rocksDbBackend.createKeyedStateBackend(env, env.getJobID(), "foobar", INSTANCE, 1, new KeyGroupRange(0, 0), new KvStateRegistry().createTaskRegistry(env.getJobID(), new JobVertexID()), DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), new CloseableRegistry());
                IOUtils.closeQuietly(keyedStateBackend);
                keyedStateBackend.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Backend initialization failed even though some paths were available");
            }
        } finally {
            // noinspection ResultOfMethodCallIgnored
            targetDir1.setWritable(true, false);
        }
    }

    // ------------------------------------------------------------------------
    // RocksDB Options
    // ------------------------------------------------------------------------
    @Test
    public void testPredefinedOptions() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        // verify that we would use PredefinedOptions.DEFAULT by default.
        Assert.assertEquals(PredefinedOptions.DEFAULT, rocksDbBackend.getPredefinedOptions());
        // verify that user could configure predefined options via flink-conf.yaml
        Configuration configuration = new Configuration();
        configuration.setString(PREDEFINED_OPTIONS, FLASH_SSD_OPTIMIZED.name());
        rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        rocksDbBackend = rocksDbBackend.configure(configuration, getClass().getClassLoader());
        Assert.assertEquals(FLASH_SSD_OPTIMIZED, rocksDbBackend.getPredefinedOptions());
        // verify that predefined options could be set programmatically and override pre-configured one.
        rocksDbBackend.setPredefinedOptions(SPINNING_DISK_OPTIMIZED);
        Assert.assertEquals(SPINNING_DISK_OPTIMIZED, rocksDbBackend.getPredefinedOptions());
        try (ColumnFamilyOptions colCreated = rocksDbBackend.getColumnOptions()) {
            Assert.assertEquals(LEVEL, colCreated.compactionStyle());
        }
    }

    @Test
    public void testSetConfigurableOptions() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        Assert.assertNull(rocksDbBackend.getOptions());
        DefaultConfigurableOptionsFactory customizedOptions = new DefaultConfigurableOptionsFactory().setMaxBackgroundThreads(4).setMaxOpenFiles((-1)).setCompactionStyle(LEVEL).setUseDynamicLevelSize(true).setTargetFileSizeBase("4MB").setMaxSizeLevelBase("128 mb").setWriteBufferSize("128 MB").setMaxWriteBufferNumber(4).setMinWriteBufferNumberToMerge(3).setBlockSize("64KB").setBlockCacheSize("512mb");
        rocksDbBackend.setOptions(customizedOptions);
        try (DBOptions dbOptions = rocksDbBackend.getDbOptions()) {
            Assert.assertEquals((-1), dbOptions.maxOpenFiles());
        }
        try (ColumnFamilyOptions columnOptions = rocksDbBackend.getColumnOptions()) {
            Assert.assertEquals(LEVEL, columnOptions.compactionStyle());
            Assert.assertTrue(columnOptions.levelCompactionDynamicLevelBytes());
            Assert.assertEquals((4 * (SizeUnit.MB)), columnOptions.targetFileSizeBase());
            Assert.assertEquals((128 * (SizeUnit.MB)), columnOptions.maxBytesForLevelBase());
            Assert.assertEquals(4, columnOptions.maxWriteBufferNumber());
            Assert.assertEquals(3, columnOptions.minWriteBufferNumberToMerge());
            BlockBasedTableConfig tableConfig = ((BlockBasedTableConfig) (columnOptions.tableFormatConfig()));
            Assert.assertEquals((64 * (SizeUnit.KB)), tableConfig.blockSize());
            Assert.assertEquals((512 * (SizeUnit.MB)), tableConfig.blockCacheSize());
        }
    }

    @Test
    public void testConfigurableOptionsFromConfig() throws IOException {
        Configuration configuration = new Configuration();
        DefaultConfigurableOptionsFactory defaultOptionsFactory = new DefaultConfigurableOptionsFactory();
        Assert.assertTrue(defaultOptionsFactory.configure(configuration).getConfiguredOptions().isEmpty());
        // verify illegal configuration
        {
            verifyIllegalArgument(MAX_BACKGROUND_THREADS, "-1");
            verifyIllegalArgument(MAX_WRITE_BUFFER_NUMBER, "-1");
            verifyIllegalArgument(MIN_WRITE_BUFFER_NUMBER_TO_MERGE, "-1");
            verifyIllegalArgument(TARGET_FILE_SIZE_BASE, "0KB");
            verifyIllegalArgument(MAX_SIZE_LEVEL_BASE, "1BB");
            verifyIllegalArgument(WRITE_BUFFER_SIZE, "-1KB");
            verifyIllegalArgument(BLOCK_SIZE, "0MB");
            verifyIllegalArgument(BLOCK_CACHE_SIZE, "0");
            verifyIllegalArgument(USE_DYNAMIC_LEVEL_SIZE, "1");
            verifyIllegalArgument(COMPACTION_STYLE, "LEV");
        }
        // verify legal configuration
        {
            configuration.setString(COMPACTION_STYLE, "level");
            configuration.setString(USE_DYNAMIC_LEVEL_SIZE, "TRUE");
            configuration.setString(TARGET_FILE_SIZE_BASE, "8 mb");
            configuration.setString(MAX_SIZE_LEVEL_BASE, "128MB");
            configuration.setString(MAX_BACKGROUND_THREADS, "4");
            configuration.setString(MAX_WRITE_BUFFER_NUMBER, "4");
            configuration.setString(MIN_WRITE_BUFFER_NUMBER_TO_MERGE, "2");
            configuration.setString(WRITE_BUFFER_SIZE, "64 MB");
            configuration.setString(BLOCK_SIZE, "4 kb");
            configuration.setString(BLOCK_CACHE_SIZE, "512 mb");
            DefaultConfigurableOptionsFactory optionsFactory = new DefaultConfigurableOptionsFactory();
            optionsFactory.configure(configuration);
            String checkpointPath = tempFolder.newFolder().toURI().toString();
            RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
            rocksDbBackend.setOptions(optionsFactory);
            try (DBOptions dbOptions = rocksDbBackend.getDbOptions()) {
                Assert.assertEquals((-1), dbOptions.maxOpenFiles());
            }
            try (ColumnFamilyOptions columnOptions = rocksDbBackend.getColumnOptions()) {
                Assert.assertEquals(LEVEL, columnOptions.compactionStyle());
                Assert.assertTrue(columnOptions.levelCompactionDynamicLevelBytes());
                Assert.assertEquals((8 * (SizeUnit.MB)), columnOptions.targetFileSizeBase());
                Assert.assertEquals((128 * (SizeUnit.MB)), columnOptions.maxBytesForLevelBase());
                Assert.assertEquals(4, columnOptions.maxWriteBufferNumber());
                Assert.assertEquals(2, columnOptions.minWriteBufferNumberToMerge());
                Assert.assertEquals((64 * (SizeUnit.MB)), columnOptions.writeBufferSize());
                BlockBasedTableConfig tableConfig = ((BlockBasedTableConfig) (columnOptions.tableFormatConfig()));
                Assert.assertEquals((4 * (SizeUnit.KB)), tableConfig.blockSize());
                Assert.assertEquals((512 * (SizeUnit.MB)), tableConfig.blockCacheSize());
            }
        }
    }

    @Test
    public void testOptionsFactory() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        // verify that user-defined options factory could be configured via flink-conf.yaml
        Configuration config = new Configuration();
        config.setString(OPTIONS_FACTORY.key(), RocksDBStateBackendConfigTest.TestOptionsFactory.class.getName());
        config.setInteger(RocksDBStateBackendConfigTest.TestOptionsFactory.BACKGROUND_JOBS_OPTION, 4);
        rocksDbBackend = rocksDbBackend.configure(config, getClass().getClassLoader());
        Assert.assertTrue(((rocksDbBackend.getOptions()) instanceof RocksDBStateBackendConfigTest.TestOptionsFactory));
        try (DBOptions dbOptions = rocksDbBackend.getDbOptions()) {
            Assert.assertEquals(4, dbOptions.maxBackgroundJobs());
        }
        // verify that user-defined options factory could be set programmatically and override pre-configured one.
        rocksDbBackend.setOptions(new OptionsFactory() {
            @Override
            public DBOptions createDBOptions(DBOptions currentOptions) {
                return currentOptions;
            }

            @Override
            public ColumnFamilyOptions createColumnOptions(ColumnFamilyOptions currentOptions) {
                return currentOptions.setCompactionStyle(FIFO);
            }
        });
        Assert.assertNotNull(rocksDbBackend.getOptions());
        try (ColumnFamilyOptions colCreated = rocksDbBackend.getColumnOptions()) {
            Assert.assertEquals(FIFO, colCreated.compactionStyle());
        }
    }

    @Test
    public void testPredefinedAndOptionsFactory() throws Exception {
        String checkpointPath = tempFolder.newFolder().toURI().toString();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(checkpointPath);
        Assert.assertEquals(PredefinedOptions.DEFAULT, rocksDbBackend.getPredefinedOptions());
        rocksDbBackend.setPredefinedOptions(SPINNING_DISK_OPTIMIZED);
        rocksDbBackend.setOptions(new OptionsFactory() {
            @Override
            public DBOptions createDBOptions(DBOptions currentOptions) {
                return currentOptions;
            }

            @Override
            public ColumnFamilyOptions createColumnOptions(ColumnFamilyOptions currentOptions) {
                return currentOptions.setCompactionStyle(UNIVERSAL);
            }
        });
        Assert.assertEquals(SPINNING_DISK_OPTIMIZED, rocksDbBackend.getPredefinedOptions());
        Assert.assertNotNull(rocksDbBackend.getOptions());
        try (ColumnFamilyOptions colCreated = rocksDbBackend.getColumnOptions()) {
            Assert.assertEquals(UNIVERSAL, colCreated.compactionStyle());
        }
    }

    @Test
    public void testPredefinedOptionsEnum() {
        for (PredefinedOptions o : PredefinedOptions.values()) {
            try (DBOptions opt = o.createDBOptions()) {
                Assert.assertNotNull(opt);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Reconfiguration
    // ------------------------------------------------------------------------
    @Test
    public void testRocksDbReconfigurationCopiesExistingValues() throws Exception {
        final FsStateBackend checkpointBackend = new FsStateBackend(tempFolder.newFolder().toURI().toString());
        final boolean incremental = !(INCREMENTAL_CHECKPOINTS.defaultValue());
        final RocksDBStateBackend original = new RocksDBStateBackend(checkpointBackend, incremental);
        // these must not be the default options
        final PredefinedOptions predOptions = SPINNING_DISK_OPTIMIZED_HIGH_MEM;
        Assert.assertNotEquals(predOptions, original.getPredefinedOptions());
        original.setPredefinedOptions(predOptions);
        final OptionsFactory optionsFactory = Mockito.mock(OptionsFactory.class);
        original.setOptions(optionsFactory);
        final String[] localDirs = new String[]{ tempFolder.newFolder().getAbsolutePath(), tempFolder.newFolder().getAbsolutePath() };
        original.setDbStoragePaths(localDirs);
        RocksDBStateBackend copy = original.configure(new Configuration(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(original.isIncrementalCheckpointsEnabled(), copy.isIncrementalCheckpointsEnabled());
        Assert.assertArrayEquals(original.getDbStoragePaths(), copy.getDbStoragePaths());
        Assert.assertEquals(original.getOptions(), copy.getOptions());
        Assert.assertEquals(original.getPredefinedOptions(), copy.getPredefinedOptions());
        FsStateBackend copyCheckpointBackend = ((FsStateBackend) (copy.getCheckpointBackend()));
        Assert.assertEquals(checkpointBackend.getCheckpointPath(), copyCheckpointBackend.getCheckpointPath());
        Assert.assertEquals(checkpointBackend.getSavepointPath(), copyCheckpointBackend.getSavepointPath());
    }

    // ------------------------------------------------------------------------
    // Contained Non-partitioned State Backend
    // ------------------------------------------------------------------------
    @Test
    public void testCallsForwardedToNonPartitionedBackend() throws Exception {
        StateBackend storageBackend = new MemoryStateBackend();
        RocksDBStateBackend rocksDbBackend = new RocksDBStateBackend(storageBackend);
        Assert.assertEquals(storageBackend, rocksDbBackend.getCheckpointBackend());
    }

    /**
     * An implementation of options factory for testing.
     */
    public static class TestOptionsFactory implements ConfigurableOptionsFactory {
        public static final String BACKGROUND_JOBS_OPTION = "my.custom.rocksdb.backgroundJobs";

        private static final int DEFAULT_BACKGROUND_JOBS = 2;

        private int backgroundJobs = RocksDBStateBackendConfigTest.TestOptionsFactory.DEFAULT_BACKGROUND_JOBS;

        @Override
        public DBOptions createDBOptions(DBOptions currentOptions) {
            return currentOptions.setMaxBackgroundJobs(backgroundJobs);
        }

        @Override
        public ColumnFamilyOptions createColumnOptions(ColumnFamilyOptions currentOptions) {
            return currentOptions.setCompactionStyle(UNIVERSAL);
        }

        @Override
        public OptionsFactory configure(Configuration configuration) {
            this.backgroundJobs = configuration.getInteger(RocksDBStateBackendConfigTest.TestOptionsFactory.BACKGROUND_JOBS_OPTION, RocksDBStateBackendConfigTest.TestOptionsFactory.DEFAULT_BACKGROUND_JOBS);
            return this;
        }
    }
}

