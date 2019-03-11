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
package org.apache.flink.contrib.streaming.state;


import CheckpointingOptions.CHECKPOINTS_DIRECTORY;
import CheckpointingOptions.INCREMENTAL_CHECKPOINTS;
import CheckpointingOptions.SAVEPOINT_DIRECTORY;
import CheckpointingOptions.STATE_BACKEND;
import RocksDBOptions.LOCAL_DIRECTORIES;
import java.io.File;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.StateBackendLoader;
import org.apache.flink.runtime.state.filesystem.AbstractFileStateBackend;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the RocksDBStateBackendFactory.
 */
public class RocksDBStateBackendFactoryTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    private final ClassLoader cl = getClass().getClassLoader();

    private final String backendKey = STATE_BACKEND.key();

    // ------------------------------------------------------------------------
    @Test
    public void testFactoryName() {
        // construct the name such that it will not be automatically adjusted on refactorings
        String factoryName = "org.apache.flink.contrib.streaming.state.Roc";
        factoryName += "ksDBStateBackendFactory";
        // !!! if this fails, the code in StateBackendLoader must be adjusted
        Assert.assertEquals(factoryName, RocksDBStateBackendFactory.class.getName());
    }

    /**
     * Validates loading a file system state backend with additional parameters from the cluster configuration.
     */
    @Test
    public void testLoadFileSystemStateBackend() throws Exception {
        final String checkpointDir = new Path(tmp.newFolder().toURI()).toString();
        final String savepointDir = new Path(tmp.newFolder().toURI()).toString();
        final String localDir1 = tmp.newFolder().getAbsolutePath();
        final String localDir2 = tmp.newFolder().getAbsolutePath();
        final String localDirs = (localDir1 + (File.pathSeparator)) + localDir2;
        final boolean incremental = !(INCREMENTAL_CHECKPOINTS.defaultValue());
        final Path expectedCheckpointsPath = new Path(checkpointDir);
        final Path expectedSavepointsPath = new Path(savepointDir);
        // we configure with the explicit string (rather than AbstractStateBackend#X_STATE_BACKEND_NAME)
        // to guard against config-breaking changes of the name
        final Configuration config1 = new Configuration();
        config1.setString(backendKey, "rocksdb");
        config1.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config1.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config1.setString(LOCAL_DIRECTORIES, localDirs);
        config1.setBoolean(INCREMENTAL_CHECKPOINTS, incremental);
        final Configuration config2 = new Configuration();
        config2.setString(backendKey, RocksDBStateBackendFactory.class.getName());
        config2.setString(CHECKPOINTS_DIRECTORY, checkpointDir);
        config2.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config2.setString(LOCAL_DIRECTORIES, localDirs);
        config2.setBoolean(INCREMENTAL_CHECKPOINTS, incremental);
        StateBackend backend1 = StateBackendLoader.loadStateBackendFromConfig(config1, cl, null);
        StateBackend backend2 = StateBackendLoader.loadStateBackendFromConfig(config2, cl, null);
        Assert.assertTrue((backend1 instanceof RocksDBStateBackend));
        Assert.assertTrue((backend2 instanceof RocksDBStateBackend));
        RocksDBStateBackend fs1 = ((RocksDBStateBackend) (backend1));
        RocksDBStateBackend fs2 = ((RocksDBStateBackend) (backend2));
        AbstractFileStateBackend fs1back = ((AbstractFileStateBackend) (fs1.getCheckpointBackend()));
        AbstractFileStateBackend fs2back = ((AbstractFileStateBackend) (fs2.getCheckpointBackend()));
        Assert.assertEquals(expectedCheckpointsPath, fs1back.getCheckpointPath());
        Assert.assertEquals(expectedCheckpointsPath, fs2back.getCheckpointPath());
        Assert.assertEquals(expectedSavepointsPath, fs1back.getSavepointPath());
        Assert.assertEquals(expectedSavepointsPath, fs2back.getSavepointPath());
        Assert.assertEquals(incremental, fs1.isIncrementalCheckpointsEnabled());
        Assert.assertEquals(incremental, fs2.isIncrementalCheckpointsEnabled());
        RocksDBStateBackendFactoryTest.checkPaths(fs1.getDbStoragePaths(), localDir1, localDir2);
        RocksDBStateBackendFactoryTest.checkPaths(fs2.getDbStoragePaths(), localDir1, localDir2);
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
        final String localDir1 = tmp.newFolder().getAbsolutePath();
        final String localDir2 = tmp.newFolder().getAbsolutePath();
        final String localDir3 = tmp.newFolder().getAbsolutePath();
        final String localDir4 = tmp.newFolder().getAbsolutePath();
        final boolean incremental = !(INCREMENTAL_CHECKPOINTS.defaultValue());
        final Path expectedCheckpointsPath = new Path(appCheckpointDir);
        final Path expectedSavepointsPath = new Path(savepointDir);
        final RocksDBStateBackend backend = new RocksDBStateBackend(appCheckpointDir, incremental);
        backend.setDbStoragePaths(localDir1, localDir2);
        final Configuration config = new Configuration();
        config.setString(backendKey, "jobmanager");// this should not be picked up

        config.setString(CHECKPOINTS_DIRECTORY, checkpointDir);// this should not be picked up

        config.setString(SAVEPOINT_DIRECTORY, savepointDir);
        config.setBoolean(INCREMENTAL_CHECKPOINTS, (!incremental));// this should not be picked up

        config.setString(LOCAL_DIRECTORIES, ((localDir3 + ":") + localDir4));// this should not be picked up

        final StateBackend loadedBackend = StateBackendLoader.fromApplicationOrConfigOrDefault(backend, config, cl, null);
        Assert.assertTrue((loadedBackend instanceof RocksDBStateBackend));
        final RocksDBStateBackend loadedRocks = ((RocksDBStateBackend) (loadedBackend));
        Assert.assertEquals(incremental, loadedRocks.isIncrementalCheckpointsEnabled());
        RocksDBStateBackendFactoryTest.checkPaths(loadedRocks.getDbStoragePaths(), localDir1, localDir2);
        AbstractFileStateBackend fsBackend = ((AbstractFileStateBackend) (loadedRocks.getCheckpointBackend()));
        Assert.assertEquals(expectedCheckpointsPath, fsBackend.getCheckpointPath());
        Assert.assertEquals(expectedSavepointsPath, fsBackend.getSavepointPath());
    }
}

