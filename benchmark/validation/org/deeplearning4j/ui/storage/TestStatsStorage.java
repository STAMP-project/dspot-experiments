/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.ui.storage;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.deeplearning4j.api.storage.Persistable;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.api.storage.StatsStorageEvent;
import org.deeplearning4j.api.storage.StatsStorageListener;
import org.deeplearning4j.ui.storage.mapdb.MapDBStatsStorage;
import org.deeplearning4j.ui.storage.sqlite.J7FileStatsStorage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Created by Alex on 03/10/2016.
 */
public class TestStatsStorage {
    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testStatsStorage() throws IOException {
        for (boolean useJ7Storage : new boolean[]{ false, true }) {
            for (int i = 0; i < 3; i++) {
                StatsStorage ss;
                switch (i) {
                    case 0 :
                        File f = createTempFile("TestMapDbStatsStore", ".db");
                        f.delete();// Don't want file to exist...

                        ss = new MapDBStatsStorage.Builder().file(f).build();
                        break;
                    case 1 :
                        File f2 = createTempFile("TestJ7FileStatsStore", ".db");
                        f2.delete();// Don't want file to exist...

                        ss = new J7FileStatsStorage(f2);
                        break;
                    case 2 :
                        ss = new InMemoryStatsStorage();
                        break;
                    default :
                        throw new RuntimeException();
                }
                TestStatsStorage.CountingListener l = new TestStatsStorage.CountingListener();
                ss.registerStatsStorageListener(l);
                Assert.assertEquals(1, ss.getListeners().size());
                Assert.assertEquals(0, ss.listSessionIDs().size());
                Assert.assertNull(ss.getLatestUpdate("sessionID", "typeID", "workerID"));
                Assert.assertEquals(0, ss.listSessionIDs().size());
                ss.putStaticInfo(TestStatsStorage.getInitReport(0, 0, 0, useJ7Storage));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(1, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(0, l.countUpdate);
                Assert.assertEquals(Collections.singletonList("sid0"), ss.listSessionIDs());
                Assert.assertTrue(ss.sessionExists("sid0"));
                Assert.assertFalse(ss.sessionExists("sid1"));
                Persistable expected = TestStatsStorage.getInitReport(0, 0, 0, useJ7Storage);
                Persistable p = ss.getStaticInfo("sid0", "tid0", "wid0");
                Assert.assertEquals(expected, p);
                List<Persistable> allStatic = ss.getAllStaticInfos("sid0", "tid0");
                Assert.assertEquals(Collections.singletonList(expected), allStatic);
                Assert.assertNull(ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(0, ss.getAllUpdatesAfter("sid0", "tid0", "wid0", 0).size());
                Assert.assertEquals(0, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(0, ss.getNumUpdateRecordsFor("sid0", "tid0", "wid0"));
                // Put first update
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(Collections.singletonList(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage)), ss.getAllUpdatesAfter("sid0", "tid0", "wid0", 0));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0", "tid0", "wid0"));
                List<Persistable> list = ss.getLatestUpdateAllWorkers("sid0", "tid0");
                Assert.assertEquals(1, list.size());
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12345));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(1, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(1, l.countUpdate);
                // Put second update
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage));
                Assert.assertEquals(1, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12346));
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage));
                Assert.assertEquals(2, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid1"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid1", 12345));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(2, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(3, l.countUpdate);
                // Put static info and update with different session, type and worker IDs
                ss.putStaticInfo(TestStatsStorage.getInitReport(100, 200, 300, useJ7Storage));
                Assert.assertEquals(2, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                ss.putUpdate(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage));
                Assert.assertEquals(Collections.singletonList(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage)), ss.getLatestUpdateAllWorkers("sid100", "tid200"));
                Assert.assertEquals(Collections.singletonList("tid200"), ss.listTypeIDsForSession("sid100"));
                List<String> temp = ss.listWorkerIDsForSession("sid100");
                System.out.println(("temp: " + temp));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSession("sid100"));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSessionAndType("sid100", "tid200"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getLatestUpdate("sid100", "tid200", "wid300"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getUpdate("sid100", "tid200", "wid300", 12346));
                Assert.assertEquals(2, l.countNewSession);
                Assert.assertEquals(3, l.countNewWorkerId);
                Assert.assertEquals(2, l.countStaticInfo);
                Assert.assertEquals(4, l.countUpdate);
                // Test get updates times:
                long[] expTSWid0 = new long[]{ 12345, 12346 };
                long[] actTSWid0 = ss.getAllUpdateTimes("sid0", "tid0", "wid0");
                Assert.assertArrayEquals(expTSWid0, actTSWid0);
                long[] expTSWid1 = new long[]{ 12345 };
                long[] actTSWid1 = ss.getAllUpdateTimes("sid0", "tid0", "wid1");
                Assert.assertArrayEquals(expTSWid1, actTSWid1);
                ss.putUpdate(TestStatsStorage.getReport(100, 200, 300, 12347, useJ7Storage));
                ss.putUpdate(TestStatsStorage.getReport(100, 200, 300, 12348, useJ7Storage));
                ss.putUpdate(TestStatsStorage.getReport(100, 200, 300, 12349, useJ7Storage));
                long[] expTSWid300 = new long[]{ 12346, 12347, 12348, 12349 };
                long[] actTSWid300 = ss.getAllUpdateTimes("sid100", "tid200", "wid300");
                Assert.assertArrayEquals(expTSWid300, actTSWid300);
                // Test subset query:
                List<Persistable> subset = ss.getUpdates("sid100", "tid200", "wid300", new long[]{ 12346, 12349 });
                Assert.assertEquals(2, subset.size());
                Assert.assertEquals(Arrays.asList(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), TestStatsStorage.getReport(100, 200, 300, 12349, useJ7Storage)), subset);
            }
        }
    }

    @Test
    public void testFileStatsStore() throws IOException {
        for (boolean useJ7Storage : new boolean[]{ false, true }) {
            for (int i = 0; i < 2; i++) {
                File f;
                if (i == 0) {
                    f = createTempFile("TestMapDbStatsStore", ".db");
                } else {
                    f = createTempFile("TestSqliteStatsStore", ".db");
                }
                f.delete();// Don't want file to exist...

                StatsStorage ss;
                if (i == 0) {
                    ss = new MapDBStatsStorage.Builder().file(f).build();
                } else {
                    ss = new J7FileStatsStorage(f);
                }
                TestStatsStorage.CountingListener l = new TestStatsStorage.CountingListener();
                ss.registerStatsStorageListener(l);
                Assert.assertEquals(1, ss.getListeners().size());
                Assert.assertEquals(0, ss.listSessionIDs().size());
                Assert.assertNull(ss.getLatestUpdate("sessionID", "typeID", "workerID"));
                Assert.assertEquals(0, ss.listSessionIDs().size());
                ss.putStaticInfo(TestStatsStorage.getInitReport(0, 0, 0, useJ7Storage));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(1, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(0, l.countUpdate);
                Assert.assertEquals(Collections.singletonList("sid0"), ss.listSessionIDs());
                Assert.assertTrue(ss.sessionExists("sid0"));
                Assert.assertFalse(ss.sessionExists("sid1"));
                Persistable expected = TestStatsStorage.getInitReport(0, 0, 0, useJ7Storage);
                Persistable p = ss.getStaticInfo("sid0", "tid0", "wid0");
                Assert.assertEquals(expected, p);
                List<Persistable> allStatic = ss.getAllStaticInfos("sid0", "tid0");
                Assert.assertEquals(Collections.singletonList(expected), allStatic);
                Assert.assertNull(ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(0, ss.getAllUpdatesAfter("sid0", "tid0", "wid0", 0).size());
                Assert.assertEquals(0, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(0, ss.getNumUpdateRecordsFor("sid0", "tid0", "wid0"));
                // Put first update
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(Collections.singletonList(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage)), ss.getAllUpdatesAfter("sid0", "tid0", "wid0", 0));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0"));
                Assert.assertEquals(1, ss.getNumUpdateRecordsFor("sid0", "tid0", "wid0"));
                List<Persistable> list = ss.getLatestUpdateAllWorkers("sid0", "tid0");
                Assert.assertEquals(1, list.size());
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12345));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(1, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(1, l.countUpdate);
                // Put second update
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage));
                Assert.assertEquals(1, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                Assert.assertEquals(Collections.singletonList("tid0"), ss.listTypeIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSession("sid0"));
                Assert.assertEquals(Collections.singletonList("wid0"), ss.listWorkerIDsForSessionAndType("sid0", "tid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12346));
                ss.putUpdate(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage));
                Assert.assertEquals(2, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid1"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid1", 12345));
                Assert.assertEquals(1, l.countNewSession);
                Assert.assertEquals(2, l.countNewWorkerId);
                Assert.assertEquals(1, l.countStaticInfo);
                Assert.assertEquals(3, l.countUpdate);
                // Put static info and update with different session, type and worker IDs
                ss.putStaticInfo(TestStatsStorage.getInitReport(100, 200, 300, useJ7Storage));
                Assert.assertEquals(2, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                ss.putUpdate(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage));
                Assert.assertEquals(Collections.singletonList(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage)), ss.getLatestUpdateAllWorkers("sid100", "tid200"));
                Assert.assertEquals(Collections.singletonList("tid200"), ss.listTypeIDsForSession("sid100"));
                List<String> temp = ss.listWorkerIDsForSession("sid100");
                System.out.println(("temp: " + temp));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSession("sid100"));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSessionAndType("sid100", "tid200"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getLatestUpdate("sid100", "tid200", "wid300"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getUpdate("sid100", "tid200", "wid300", 12346));
                Assert.assertEquals(2, l.countNewSession);
                Assert.assertEquals(3, l.countNewWorkerId);
                Assert.assertEquals(2, l.countStaticInfo);
                Assert.assertEquals(4, l.countUpdate);
                // Close and re-open
                ss.close();
                Assert.assertTrue(ss.isClosed());
                if (i == 0) {
                    ss = new MapDBStatsStorage.Builder().file(f).build();
                } else {
                    ss = new J7FileStatsStorage(f);
                }
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12345));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid0"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 0, 12346, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid0", 12346));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getLatestUpdate("sid0", "tid0", "wid1"));
                Assert.assertEquals(TestStatsStorage.getReport(0, 0, 1, 12345, useJ7Storage), ss.getUpdate("sid0", "tid0", "wid1", 12345));
                Assert.assertEquals(2, ss.getLatestUpdateAllWorkers("sid0", "tid0").size());
                Assert.assertEquals(1, ss.getLatestUpdateAllWorkers("sid100", "tid200").size());
                Assert.assertEquals(Collections.singletonList("tid200"), ss.listTypeIDsForSession("sid100"));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSession("sid100"));
                Assert.assertEquals(Collections.singletonList("wid300"), ss.listWorkerIDsForSessionAndType("sid100", "tid200"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getLatestUpdate("sid100", "tid200", "wid300"));
                Assert.assertEquals(TestStatsStorage.getReport(100, 200, 300, 12346, useJ7Storage), ss.getUpdate("sid100", "tid200", "wid300", 12346));
            }
        }
    }

    @NoArgsConstructor
    @Data
    private static class CountingListener implements StatsStorageListener {
        private int countNewSession;

        private int countNewTypeID;

        private int countNewWorkerId;

        private int countStaticInfo;

        private int countUpdate;

        private int countMetaData;

        @Override
        public void notify(StatsStorageEvent event) {
            System.out.println(("Event: " + event));
            switch (event.getEventType()) {
                case NewSessionID :
                    (countNewSession)++;
                    break;
                case NewTypeID :
                    (countNewTypeID)++;
                    break;
                case NewWorkerID :
                    (countNewWorkerId)++;
                    break;
                case PostMetaData :
                    (countMetaData)++;
                    break;
                case PostStaticInfo :
                    (countStaticInfo)++;
                    break;
                case PostUpdate :
                    (countUpdate)++;
                    break;
            }
        }
    }
}

