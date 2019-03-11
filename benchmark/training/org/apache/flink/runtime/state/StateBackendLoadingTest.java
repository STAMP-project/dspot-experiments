/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import CheckpointingOptions.ASYNC_SNAPSHOTS;
import CheckpointingOptions.CHECKPOINTS_DIRECTORY;
import CheckpointingOptions.FS_SMALL_FILE_THRESHOLD;
import CheckpointingOptions.SAVEPOINT_DIRECTORY;
import CheckpointingOptions.STATE_BACKEND;
import HighAvailabilityOptions.HA_CLUSTER_ID;
import HighAvailabilityOptions.HA_MODE;
import HighAvailabilityOptions.HA_STORAGE_PATH;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackendFactory;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackendFactory;
import org.apache.flink.util.DynamicCodeLoadingException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


/**
 * This test validates that state backends are properly loaded from configuration.
 */
public class StateBackendLoadingTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    private final ClassLoader cl = getClass().getClassLoader();

    private final String backendKey = STATE_BACKEND.key();

    // ------------------------------------------------------------------------
    // defaults
    // ------------------------------------------------------------------------
    @Test
    public void testNoStateBackendDefined() throws Exception {
        Assert.assertNull(StateBackendLoader.loadStateBackendFromConfig(new Configuration(), cl, null));
    }

    @Test
    public void testInstantiateMemoryBackendByDefault() throws Exception {
        StateBackend backend = StateBackendLoader.fromApplicationOrConfigOrDefault(null, new Configuration(), cl, null);
        Assert.assertTrue((backend instanceof MemoryStateBackend));
    }

    @Test
    public void testApplicationDefinedHasPrecedence() throws Exception {
        final StateBackend appBackend = Mockito.mock(StateBackend.class);
        final Configuration config = new Configuration();
        config.setString(backendKey, "jobmanager");
        StateBackend backend = StateBackendLoader.fromApplicationOrConfigOrDefault(appBackend, config, cl, null);
        Assert.assertEquals(appBackend, backend);
    }

    // ------------------------------------------------------------------------
    // Memory State Backend
    // ------------------------------------------------------------------------
    /**
     * Validates loading a memory state backend from the cluster configuration.
     */
    @Test
    public void testLoadMemoryStateBackendNoParameters() throws Exception {
        // we configure with the explicit string (rather than AbstractStateBackend#X_STATE_BACKEND_NAME)
        // to guard against config-breaking changes of the name
        final Configuration config1 = new Configuration();
        config1.setString(backendKey, "jobmanager");
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, MemoryStateBackendFactory.class.getName());
        StateBackend backend1 = StateBackendLoader.loadStateBackendFromConfig(config1, cl, null);
        StateBackend backend2 = StateBackendLoader.loadStateBackendFromConfig(config2, cl, null);
        Assert.assertTrue((backend1 instanceof MemoryStateBackend));
        Assert.assertTrue((backend2 instanceof MemoryStateBackend));
    }

    /**
     * Validates loading a memory state backend with additional parameters from the cluster configuration.
     */
    @Test
    public void testLoadMemoryStateWithParameters() throws Exception {
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointPath = new Path(checkpointDir);
        final Path expectedSavepointPath = new Path(savepointDir);
        final boolean async = !(ASYNC_SNAPSHOTS.defaultValue());
        // we configure with the explicit string (rather than AbstractStateBackend#X_STATE_BACKEND_NAME)
        // to guard against config-breaking changes of the name
        final Configuration config1 = new Configuration();
        config1.setString(backendKey, "jobmanager");
        config1.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config1.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config1.setBoolean(ASYNC_SNAPSHOTS, async);
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, MemoryStateBackendFactory.class.getName());
        config2.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config2.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config2.setBoolean(ASYNC_SNAPSHOTS, async);
        MemoryStateBackend backend1 = ((MemoryStateBackend) (StateBackendLoader.loadStateBackendFromConfig(config1, cl, null)));
        MemoryStateBackend backend2 = ((MemoryStateBackend) (StateBackendLoader.loadStateBackendFromConfig(config2, cl, null)));
        Assert.assertNotNull(backend1);
        Assert.assertNotNull(backend2);
        Assert.assertEquals(expectedCheckpointPath, backend1.getCheckpointPath());
        Assert.assertEquals(expectedCheckpointPath, backend2.getCheckpointPath());
        Assert.assertEquals(expectedSavepointPath, backend1.getSavepointPath());
        Assert.assertEquals(expectedSavepointPath, backend2.getSavepointPath());
        Assert.assertEquals(async, backend1.isUsingAsynchronousSnapshots());
        Assert.assertEquals(async, backend2.isUsingAsynchronousSnapshots());
    }

    /**
     * Validates taking the application-defined memory state backend and adding additional
     * parameters from the cluster configuration.
     */
    @Test
    public void testConfigureMemoryStateBackend() throws Exception {
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointPath = new Path(checkpointDir);
        final Path expectedSavepointPath = new Path(savepointDir);
        final int maxSize = 100;
        final boolean async = !(ASYNC_SNAPSHOTS.defaultValue());
        final MemoryStateBackend backend = new MemoryStateBackend(maxSize, async);
        final Configuration config = new Configuration();
        config.setString(backendKey, "filesystem");// check that this is not accidentally picked up

        config.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config.setBoolean(ASYNC_SNAPSHOTS, (!async));
        StateBackend loadedBackend = StateBackendLoader.fromApplicationOrConfigOrDefault(backend, config, cl, null);
        Assert.assertTrue((loadedBackend instanceof MemoryStateBackend));
        final MemoryStateBackend memBackend = ((MemoryStateBackend) (loadedBackend));
        Assert.assertEquals(expectedCheckpointPath, memBackend.getCheckpointPath());
        Assert.assertEquals(expectedSavepointPath, memBackend.getSavepointPath());
        Assert.assertEquals(maxSize, memBackend.getMaxStateSize());
        Assert.assertEquals(async, memBackend.isUsingAsynchronousSnapshots());
    }

    /**
     * Validates taking the application-defined memory state backend and adding additional
     * parameters from the cluster configuration, but giving precedence to application-defined
     * parameters over configuration-defined parameters.
     */
    @Test
    public void testConfigureMemoryStateBackendMixed() throws Exception {
        final String appCheckpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointPath = new Path(appCheckpointDir);
        final Path expectedSavepointPath = new Path(savepointDir);
        final MemoryStateBackend backend = new MemoryStateBackend(appCheckpointDir, null);
        final Configuration config = new Configuration();
        config.setString(backendKey, "filesystem");// check that this is not accidentally picked up

        config.setString(CHECKPOINTS_DIRECTORY, checkpointDir);// this parameter should not be picked up

        config.setString(SAVEPOINT_DIRECTORY, savepointDir);
        StateBackend loadedBackend = StateBackendLoader.fromApplicationOrConfigOrDefault(backend, config, cl, null);
        Assert.assertTrue((loadedBackend instanceof MemoryStateBackend));
        final MemoryStateBackend memBackend = ((MemoryStateBackend) (loadedBackend));
        Assert.assertEquals(expectedCheckpointPath, memBackend.getCheckpointPath());
        Assert.assertEquals(expectedSavepointPath, memBackend.getSavepointPath());
    }

    // ------------------------------------------------------------------------
    // File System State Backend
    // ------------------------------------------------------------------------
    /**
     * Validates loading a file system state backend with additional parameters from the cluster configuration.
     */
    @Test
    public void testLoadFileSystemStateBackend() throws Exception {
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointsPath = new Path(checkpointDir);
        final Path expectedSavepointsPath = new Path(savepointDir);
        final int threshold = 1000000;
        final boolean async = !(ASYNC_SNAPSHOTS.defaultValue());
        // we configure with the explicit string (rather than AbstractStateBackend#X_STATE_BACKEND_NAME)
        // to guard against config-breaking changes of the name
        final Configuration config1 = new Configuration();
        config1.setString(backendKey, "filesystem");
        config1.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config1.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config1.setInteger(FS_SMALL_FILE_THRESHOLD, threshold);
        config1.setBoolean(ASYNC_SNAPSHOTS, async);
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, FsStateBackendFactory.class.getName());
        config2.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config2.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config2.setInteger(FS_SMALL_FILE_THRESHOLD, threshold);
        config2.setBoolean(ASYNC_SNAPSHOTS, async);
        StateBackend backend1 = StateBackendLoader.loadStateBackendFromConfig(config1, cl, null);
        StateBackend backend2 = StateBackendLoader.loadStateBackendFromConfig(config2, cl, null);
        Assert.assertTrue((backend1 instanceof FsStateBackend));
        Assert.assertTrue((backend2 instanceof FsStateBackend));
        FsStateBackend fs1 = ((FsStateBackend) (backend1));
        FsStateBackend fs2 = ((FsStateBackend) (backend2));
        Assert.assertEquals(expectedCheckpointsPath, fs1.getCheckpointPath());
        Assert.assertEquals(expectedCheckpointsPath, fs2.getCheckpointPath());
        Assert.assertEquals(expectedSavepointsPath, fs1.getSavepointPath());
        Assert.assertEquals(expectedSavepointsPath, fs2.getSavepointPath());
        Assert.assertEquals(threshold, fs1.getMinFileSizeThreshold());
        Assert.assertEquals(threshold, fs2.getMinFileSizeThreshold());
        Assert.assertEquals(async, fs1.isUsingAsynchronousSnapshots());
        Assert.assertEquals(async, fs2.isUsingAsynchronousSnapshots());
    }

    /**
     * Validates taking the application-defined file system state backend and adding with additional
     * parameters from the cluster configuration, but giving precedence to application-defined
     * parameters over configuration-defined parameters.
     */
    @Test
    public void testLoadFileSystemStateBackendMixed() throws Exception {
        final String appCheckpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointsPath = new Path(new URI(appCheckpointDir));
        final Path expectedSavepointsPath = new Path(savepointDir);
        final int threshold = 1000000;
        final FsStateBackend backend = new FsStateBackend(new URI(appCheckpointDir), threshold);
        final Configuration config = new Configuration();
        config.setString(backendKey, "jobmanager");// this should not be picked up

        config.setString(CHECKPOINTS_DIRECTORY, checkpointDir);// this should not be picked up

        config.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config.setInteger(FS_SMALL_FILE_THRESHOLD, 20);// this should not be picked up

        final StateBackend loadedBackend = StateBackendLoader.fromApplicationOrConfigOrDefault(backend, config, cl, null);
        Assert.assertTrue((loadedBackend instanceof FsStateBackend));
        final FsStateBackend fs = ((FsStateBackend) (loadedBackend));
        Assert.assertEquals(expectedCheckpointsPath, fs.getCheckpointPath());
        Assert.assertEquals(expectedSavepointsPath, fs.getSavepointPath());
        Assert.assertEquals(threshold, fs.getMinFileSizeThreshold());
    }

    // ------------------------------------------------------------------------
    // Failures
    // ------------------------------------------------------------------------
    /**
     * This test makes sure that failures properly manifest when the state backend could not be loaded.
     */
    @Test
    public void testLoadingFails() throws Exception {
        final Configuration config = new Configuration();
        // try a value that is neither recognized as a name, nor corresponds to a class
        config.setString(backendKey, "does.not.exist");
        try {
            StateBackendLoader.fromApplicationOrConfigOrDefault(null, config, cl, null);
            Assert.fail("should fail with an exception");
        } catch (DynamicCodeLoadingException ignored) {
            // expected
        }
        // try a class that is not a factory
        config.setString(backendKey, File.class.getName());
        try {
            StateBackendLoader.fromApplicationOrConfigOrDefault(null, config, cl, null);
            Assert.fail("should fail with an exception");
        } catch (DynamicCodeLoadingException ignored) {
            // expected
        }
        // a factory that fails
        config.setString(backendKey, StateBackendLoadingTest.FailingFactory.class.getName());
        try {
            StateBackendLoader.fromApplicationOrConfigOrDefault(null, config, cl, null);
            Assert.fail("should fail with an exception");
        } catch (IOException ignored) {
            // expected
        }
    }

    // ------------------------------------------------------------------------
    // High-availability default
    // ------------------------------------------------------------------------
    /**
     * This tests that in the case of configured high-availability, the memory state backend
     * automatically grabs the HA persistence directory.
     */
    @Test
    public void testHighAvailabilityDefaultFallback() throws Exception {
        final String haPersistenceDir = new Path(tmp.newFolder().toURI()).toString();
        final Path expectedCheckpointPath = new Path(haPersistenceDir);
        final Configuration config1 = new Configuration();
        config1.setString(HA_MODE, "zookeeper");
        config1.setString(HA_CLUSTER_ID, "myCluster");
        config1.setString(HA_STORAGE_PATH, haPersistenceDir);
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, "jobmanager");
        config2.setString(HA_MODE, "zookeeper");
        config2.setString(HA_CLUSTER_ID, "myCluster");
        config2.setString(HA_STORAGE_PATH, haPersistenceDir);
        final MemoryStateBackend appBackend = new MemoryStateBackend();
        final StateBackend loaded1 = StateBackendLoader.fromApplicationOrConfigOrDefault(appBackend, config1, cl, null);
        final StateBackend loaded2 = StateBackendLoader.fromApplicationOrConfigOrDefault(null, config1, cl, null);
        final StateBackend loaded3 = StateBackendLoader.fromApplicationOrConfigOrDefault(null, config2, cl, null);
        Assert.assertTrue((loaded1 instanceof MemoryStateBackend));
        Assert.assertTrue((loaded2 instanceof MemoryStateBackend));
        Assert.assertTrue((loaded3 instanceof MemoryStateBackend));
        final MemoryStateBackend memBackend1 = ((MemoryStateBackend) (loaded1));
        final MemoryStateBackend memBackend2 = ((MemoryStateBackend) (loaded2));
        final MemoryStateBackend memBackend3 = ((MemoryStateBackend) (loaded3));
        Assert.assertNotNull(memBackend1.getCheckpointPath());
        Assert.assertNotNull(memBackend2.getCheckpointPath());
        Assert.assertNotNull(memBackend3.getCheckpointPath());
        Assert.assertNull(memBackend1.getSavepointPath());
        Assert.assertNull(memBackend2.getSavepointPath());
        Assert.assertNull(memBackend3.getSavepointPath());
        Assert.assertEquals(expectedCheckpointPath, memBackend1.getCheckpointPath().getParent());
        Assert.assertEquals(expectedCheckpointPath, memBackend2.getCheckpointPath().getParent());
        Assert.assertEquals(expectedCheckpointPath, memBackend3.getCheckpointPath().getParent());
    }

    @Test
    public void testHighAvailabilityDefaultFallbackLocalPaths() throws Exception {
        final String haPersistenceDir = new Path(tmp.newFolder().getAbsolutePath()).toString();
        final Path expectedCheckpointPath = new Path(haPersistenceDir).makeQualified(FileSystem.getLocalFileSystem());
        final Configuration config1 = new Configuration();
        config1.setString(HA_MODE, "zookeeper");
        config1.setString(HA_CLUSTER_ID, "myCluster");
        config1.setString(HA_STORAGE_PATH, haPersistenceDir);
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, "jobmanager");
        config2.setString(HA_MODE, "zookeeper");
        config2.setString(HA_CLUSTER_ID, "myCluster");
        config2.setString(HA_STORAGE_PATH, haPersistenceDir);
        final MemoryStateBackend appBackend = new MemoryStateBackend();
        final StateBackend loaded1 = StateBackendLoader.fromApplicationOrConfigOrDefault(appBackend, config1, cl, null);
        final StateBackend loaded2 = StateBackendLoader.fromApplicationOrConfigOrDefault(null, config1, cl, null);
        final StateBackend loaded3 = StateBackendLoader.fromApplicationOrConfigOrDefault(null, config2, cl, null);
        Assert.assertTrue((loaded1 instanceof MemoryStateBackend));
        Assert.assertTrue((loaded2 instanceof MemoryStateBackend));
        Assert.assertTrue((loaded3 instanceof MemoryStateBackend));
        final MemoryStateBackend memBackend1 = ((MemoryStateBackend) (loaded1));
        final MemoryStateBackend memBackend2 = ((MemoryStateBackend) (loaded2));
        final MemoryStateBackend memBackend3 = ((MemoryStateBackend) (loaded3));
        Assert.assertNotNull(memBackend1.getCheckpointPath());
        Assert.assertNotNull(memBackend2.getCheckpointPath());
        Assert.assertNotNull(memBackend3.getCheckpointPath());
        Assert.assertNull(memBackend1.getSavepointPath());
        Assert.assertNull(memBackend2.getSavepointPath());
        Assert.assertNull(memBackend3.getSavepointPath());
        Assert.assertEquals(expectedCheckpointPath, memBackend1.getCheckpointPath().getParent());
        Assert.assertEquals(expectedCheckpointPath, memBackend2.getCheckpointPath().getParent());
        Assert.assertEquals(expectedCheckpointPath, memBackend3.getCheckpointPath().getParent());
    }

    // ------------------------------------------------------------------------
    static final class FailingFactory implements StateBackendFactory<StateBackend> {
        @Override
        public StateBackend createFromConfig(Configuration config, ClassLoader classLoader) throws IOException {
            throw new IOException("fail!");
        }
    }
}

