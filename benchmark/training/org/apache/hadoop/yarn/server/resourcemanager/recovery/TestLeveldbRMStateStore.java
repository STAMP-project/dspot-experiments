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


import YarnConfiguration.RM_EPOCH;
import YarnConfiguration.RM_EPOCH_RANGE;
import YarnConfiguration.RM_LEVELDB_COMPACTION_INTERVAL_SECS;
import java.io.File;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestLeveldbRMStateStore extends RMStateStoreTestBase {
    private static final File TEST_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestLeveldbRMStateStore.class.getName());

    private YarnConfiguration conf;

    private LeveldbRMStateStore stateStore = null;

    @Test(timeout = 60000)
    public void testApps() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testRMAppStateStore(tester);
    }

    @Test(timeout = 60000)
    public void testClientTokens() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testRMDTSecretManagerStateStore(tester);
    }

    @Test(timeout = 60000)
    public void testVersion() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testCheckVersion(tester);
    }

    @Test(timeout = 60000)
    public void testEpoch() throws Exception {
        conf.setLong(RM_EPOCH, epoch);
        conf.setLong(RM_EPOCH_RANGE, getEpochRange());
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testEpoch(tester);
    }

    @Test(timeout = 60000)
    public void testAppDeletion() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testAppDeletion(tester);
    }

    @Test(timeout = 60000)
    public void testDeleteStore() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testDeleteStore(tester);
    }

    @Test(timeout = 60000)
    public void testRemoveApplication() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testRemoveApplication(tester);
    }

    @Test(timeout = 60000)
    public void testRemoveAttempt() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testRemoveAttempt(tester);
    }

    @Test(timeout = 60000)
    public void testAMTokens() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testAMRMTokenSecretManagerStateStore(tester);
    }

    @Test(timeout = 60000)
    public void testReservation() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testReservationStateStore(tester);
    }

    @Test(timeout = 60000)
    public void testProxyCA() throws Exception {
        TestLeveldbRMStateStore.LeveldbStateStoreTester tester = new TestLeveldbRMStateStore.LeveldbStateStoreTester();
        testProxyCA(tester);
    }

    @Test(timeout = 60000)
    public void testCompactionCycle() throws Exception {
        final DB mockdb = Mockito.mock(DB.class);
        conf.setLong(RM_LEVELDB_COMPACTION_INTERVAL_SECS, 1);
        stateStore = new LeveldbRMStateStore() {
            @Override
            protected DB openDatabase() throws Exception {
                return mockdb;
            }
        };
        stateStore.init(conf);
        stateStore.start();
        Mockito.verify(mockdb, Mockito.timeout(10000)).compactRange(((byte[]) (ArgumentMatchers.isNull())), ((byte[]) (ArgumentMatchers.isNull())));
    }

    @Test
    public void testBadKeyIteration() throws Exception {
        stateStore = new LeveldbRMStateStore();
        stateStore.init(conf);
        stateStore.start();
        DB db = stateStore.getDatabase();
        // add an entry that appears at the end of the database when iterating
        db.put(JniDBFactory.bytes("zzz"), JniDBFactory.bytes("z"));
        stateStore.loadState();
    }

    class LeveldbStateStoreTester implements RMStateStoreTestBase.RMStateStoreHelper {
        @Override
        public RMStateStore getRMStateStore() throws Exception {
            if ((stateStore) != null) {
                stateStore.close();
            }
            stateStore = new LeveldbRMStateStore();
            stateStore.init(conf);
            stateStore.start();
            stateStore.dispatcher.disableExitOnDispatchException();
            return stateStore;
        }

        @Override
        public boolean isFinalStateValid() throws Exception {
            // There should be 6 total entries:
            // 1 entry for version
            // 2 entries for app 0010 with one attempt
            // 3 entries for app 0001 with two attempts
            return (stateStore.getNumEntriesInDatabase()) == 6;
        }

        @Override
        public void writeVersion(Version version) throws Exception {
            stateStore.dbStoreVersion(version);
        }

        @Override
        public Version getCurrentVersion() throws Exception {
            return stateStore.getCurrentVersion();
        }

        @Override
        public boolean appExists(RMApp app) throws Exception {
            if (stateStore.isClosed()) {
                getRMStateStore();
            }
            return (stateStore.loadRMAppState(app.getApplicationId())) != null;
        }

        @Override
        public boolean attemptExists(RMAppAttempt attempt) throws Exception {
            if (stateStore.isClosed()) {
                getRMStateStore();
            }
            return (stateStore.loadRMAppAttemptState(attempt.getAppAttemptId())) != null;
        }
    }
}

