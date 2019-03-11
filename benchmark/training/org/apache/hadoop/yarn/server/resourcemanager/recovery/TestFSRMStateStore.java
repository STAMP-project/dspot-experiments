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
package org.apache.hadoop.yarn.server.resourcemanager.recovery;


import RMAppState.ACCEPTED;
import YarnConfiguration.FS_RM_STATE_STORE_NUM_RETRIES;
import YarnConfiguration.FS_RM_STATE_STORE_RETRY_INTERVAL_MS;
import YarnConfiguration.FS_RM_STATE_STORE_URI;
import YarnConfiguration.RM_EPOCH;
import YarnConfiguration.RM_EPOCH_RANGE;
import YarnConfiguration.YARN_INTERMEDIATE_DATA_ENCRYPTION;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFSRMStateStore extends RMStateStoreTestBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestFSRMStateStore.class);

    private TestFSRMStateStore.TestFSRMStateStoreTester fsTester;

    class TestFSRMStateStoreTester implements RMStateStoreTestBase.RMStateStoreHelper {
        Path workingDirPathURI;

        TestFSRMStateStore.TestFSRMStateStoreTester.TestFileSystemRMStore store;

        MiniDFSCluster cluster;

        boolean adminCheckEnable;

        class TestFileSystemRMStore extends FileSystemRMStateStore {
            TestFileSystemRMStore(Configuration conf) throws Exception {
                init(conf);
                Assert.assertNull(fs);
                Assert.assertTrue(workingDirPathURI.equals(fsWorkingPath));
                dispatcher.disableExitOnDispatchException();
                start();
                Assert.assertNotNull(fs);
            }

            public Path getVersionNode() {
                return new Path(new Path(workingDirPathURI, ROOT_DIR_NAME), VERSION_NODE);
            }

            public Version getCurrentVersion() {
                return CURRENT_VERSION_INFO;
            }

            public Path getAppDir(String appId) {
                Path rootDir = new Path(workingDirPathURI, ROOT_DIR_NAME);
                Path appRootDir = new Path(rootDir, RM_APP_ROOT);
                Path appDir = new Path(appRootDir, appId);
                return appDir;
            }

            public Path getAttemptDir(String appId, String attemptId) {
                Path appDir = getAppDir(appId);
                Path attemptDir = new Path(appDir, attemptId);
                return attemptDir;
            }
        }

        public TestFSRMStateStoreTester(MiniDFSCluster cluster, boolean adminCheckEnable) throws Exception {
            Path workingDirPath = new Path("/yarn/Test");
            this.adminCheckEnable = adminCheckEnable;
            this.cluster = cluster;
            FileSystem fs = cluster.getFileSystem();
            fs.mkdirs(workingDirPath);
            Path clusterURI = new Path(cluster.getURI());
            workingDirPathURI = new Path(clusterURI, workingDirPath);
            fs.close();
        }

        @Override
        public RMStateStore getRMStateStore() throws Exception {
            YarnConfiguration conf = new YarnConfiguration();
            conf.set(FS_RM_STATE_STORE_URI, workingDirPathURI.toString());
            conf.setInt(FS_RM_STATE_STORE_NUM_RETRIES, 8);
            conf.setLong(FS_RM_STATE_STORE_RETRY_INTERVAL_MS, 900L);
            conf.setLong(RM_EPOCH, epoch);
            conf.setLong(RM_EPOCH_RANGE, getEpochRange());
            if (adminCheckEnable) {
                conf.setBoolean(YARN_INTERMEDIATE_DATA_ENCRYPTION, true);
            }
            this.store = new TestFSRMStateStore.TestFSRMStateStoreTester.TestFileSystemRMStore(conf);
            Assert.assertEquals(getNumRetries(), 8);
            Assert.assertEquals(getRetryInterval(), 900L);
            Assert.assertTrue(((store.fs.getConf()) == (store.fsConf)));
            FileSystem previousFs = store.fs;
            startInternal();
            Assert.assertTrue(((store.fs) != previousFs));
            Assert.assertTrue(((store.fs.getConf()) == (store.fsConf)));
            return store;
        }

        @Override
        public boolean isFinalStateValid() throws Exception {
            FileSystem fs = cluster.getFileSystem();
            FileStatus[] files = fs.listStatus(workingDirPathURI);
            return (files.length) == 1;
        }

        @Override
        public void writeVersion(Version version) throws Exception {
            store.updateFile(store.getVersionNode(), getProto().toByteArray(), false);
        }

        @Override
        public Version getCurrentVersion() throws Exception {
            return store.getCurrentVersion();
        }

        public boolean appExists(RMApp app) throws IOException {
            FileSystem fs = cluster.getFileSystem();
            Path nodePath = store.getAppDir(app.getApplicationId().toString());
            return fs.exists(nodePath);
        }

        public boolean attemptExists(RMAppAttempt attempt) throws IOException {
            FileSystem fs = cluster.getFileSystem();
            ApplicationAttemptId attemptId = attempt.getAppAttemptId();
            Path nodePath = store.getAttemptDir(attemptId.getApplicationId().toString(), attemptId.toString());
            return fs.exists(nodePath);
        }
    }

    @Test(timeout = 60000)
    public void testFSRMStateStore() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = numDataNodes(1).build();
        try {
            fsTester = new TestFSRMStateStore.TestFSRMStateStoreTester(cluster, false);
            // If the state store is FileSystemRMStateStore then add corrupted entry.
            // It should discard the entry and remove it from file system.
            FSDataOutputStream fsOut = null;
            FileSystemRMStateStore fileSystemRMStateStore = ((FileSystemRMStateStore) (fsTester.getRMStateStore()));
            String appAttemptIdStr3 = "appattempt_1352994193343_0001_000003";
            ApplicationAttemptId attemptId3 = ApplicationAttemptId.fromString(appAttemptIdStr3);
            Path appDir = fsTester.store.getAppDir(attemptId3.getApplicationId().toString());
            Path tempAppAttemptFile = new Path(appDir, ((attemptId3.toString()) + ".tmp"));
            fsOut = fileSystemRMStateStore.fs.create(tempAppAttemptFile, false);
            fsOut.write("Some random data ".getBytes());
            fsOut.close();
            testRMAppStateStore(fsTester);
            Assert.assertFalse(fsTester.workingDirPathURI.getFileSystem(conf).exists(tempAppAttemptFile));
            testRMDTSecretManagerStateStore(fsTester);
            testCheckVersion(fsTester);
            testEpoch(fsTester);
            testAppDeletion(fsTester);
            testDeleteStore(fsTester);
            testRemoveApplication(fsTester);
            testRemoveAttempt(fsTester);
            testAMRMTokenSecretManagerStateStore(fsTester);
            testReservationStateStore(fsTester);
            testProxyCA(fsTester);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testHDFSRMStateStore() throws Exception {
        final HdfsConfiguration conf = new HdfsConfiguration();
        UserGroupInformation yarnAdmin = UserGroupInformation.createUserForTesting("yarn", new String[]{ "admin" });
        final MiniDFSCluster cluster = numDataNodes(1).build();
        cluster.getFileSystem().mkdir(new Path("/yarn"), FsPermission.valueOf("-rwxrwxrwx"));
        cluster.getFileSystem().setOwner(new Path("/yarn"), "yarn", "admin");
        final UserGroupInformation hdfsAdmin = UserGroupInformation.getCurrentUser();
        final RMStateStoreTestBase.StoreStateVerifier verifier = new RMStateStoreTestBase.StoreStateVerifier() {
            @Override
            void afterStoreApp(final RMStateStore store, final ApplicationId appId) {
                try {
                    // Wait for things to settle
                    Thread.sleep(5000);
                    hdfsAdmin.doAs(new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            verifyFilesUnreadablebyHDFS(cluster, ((FileSystemRMStateStore) (store)).getAppDir(appId));
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            void afterStoreAppAttempt(final RMStateStore store, final ApplicationAttemptId appAttId) {
                try {
                    // Wait for things to settle
                    Thread.sleep(5000);
                    hdfsAdmin.doAs(new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            verifyFilesUnreadablebyHDFS(cluster, ((FileSystemRMStateStore) (store)).getAppAttemptDir(appAttId));
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        try {
            yarnAdmin.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    fsTester = new TestFSRMStateStore.TestFSRMStateStoreTester(cluster, true);
                    testRMAppStateStore(fsTester, verifier);
                    return null;
                }
            });
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testCheckMajorVersionChange() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = numDataNodes(1).build();
        try {
            fsTester = new TestFSRMStateStore.TestFSRMStateStoreTester(cluster, false) {
                Version VERSION_INFO = Version.newInstance(Integer.MAX_VALUE, 0);

                @Override
                public Version getCurrentVersion() throws Exception {
                    return VERSION_INFO;
                }

                @Override
                public RMStateStore getRMStateStore() throws Exception {
                    YarnConfiguration conf = new YarnConfiguration();
                    conf.set(FS_RM_STATE_STORE_URI, workingDirPathURI.toString());
                    this.store = new TestFSRMStateStore.TestFSRMStateStoreTester.TestFileSystemRMStore(conf) {
                        Version storedVersion = null;

                        @Override
                        public Version getCurrentVersion() {
                            return VERSION_INFO;
                        }

                        @Override
                        protected synchronized Version loadVersion() throws Exception {
                            return storedVersion;
                        }

                        @Override
                        protected synchronized void storeVersion() throws Exception {
                            storedVersion = VERSION_INFO;
                        }
                    };
                    return store;
                }
            };
            // default version
            RMStateStore store = fsTester.getRMStateStore();
            Version defaultVersion = fsTester.getCurrentVersion();
            store.checkVersion();
            Assert.assertEquals(defaultVersion, store.loadVersion());
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testFSRMStateStoreClientRetry() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = numDataNodes(2).build();
        cluster.waitActive();
        try {
            TestFSRMStateStore.TestFSRMStateStoreTester fsTester = new TestFSRMStateStore.TestFSRMStateStoreTester(cluster, false);
            final RMStateStore store = fsTester.getRMStateStore();
            store.setRMDispatcher(new RMStateStoreTestBase.TestDispatcher());
            final AtomicBoolean assertionFailedInThread = new AtomicBoolean(false);
            cluster.shutdownNameNodes();
            Thread clientThread = new Thread() {
                @Override
                public void run() {
                    try {
                        store.storeApplicationStateInternal(ApplicationId.newInstance(100L, 1), ApplicationStateData.newInstance(111, 111, "user", null, ACCEPTED, "diagnostics", 222, 333, null));
                    } catch (Exception e) {
                        assertionFailedInThread.set(true);
                        e.printStackTrace();
                    }
                }
            };
            Thread.sleep(2000);
            clientThread.start();
            cluster.restartNameNode();
            clientThread.join();
            Assert.assertFalse(assertionFailedInThread.get());
        } finally {
            cluster.shutdown();
        }
    }
}

