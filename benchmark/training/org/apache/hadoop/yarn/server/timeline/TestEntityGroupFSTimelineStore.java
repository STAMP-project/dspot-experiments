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
package org.apache.hadoop.yarn.server.timeline;


import AppState.ACTIVE;
import AppState.COMPLETED;
import YarnConfiguration.TIMELINE_SERVICE_ENTITY_GROUP_PLUGIN_CLASSES;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.EnumSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileContextTestHelper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MutableStat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.ApplicationClassLoader;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.timeline.EntityGroupFSTimelineStore.AppState;
import org.apache.hadoop.yarn.server.timeline.TimelineReader.Field;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static EntityGroupFSTimelineStore.DOMAIN_LOG_PREFIX;
import static EntityGroupFSTimelineStore.ENTITY_LOG_PREFIX;
import static EntityGroupFSTimelineStore.SUMMARY_LOG_PREFIX;


public class TestEntityGroupFSTimelineStore extends TimelineStoreTestUtils {
    private static final String SAMPLE_APP_PREFIX_CACHE_TEST = "1234_000";

    private static final int CACHE_TEST_CACHE_SIZE = 5;

    private static final String TEST_SUMMARY_LOG_FILE_NAME = (SUMMARY_LOG_PREFIX) + "test";

    private static final String TEST_DOMAIN_LOG_FILE_NAME = (DOMAIN_LOG_PREFIX) + "test";

    private static final Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestEntityGroupFSTimelineStore.class.getSimpleName());

    private static Configuration config = new YarnConfiguration();

    private static MiniDFSCluster hdfsCluster;

    private static FileSystem fs;

    private static FileContext fc;

    private static FileContextTestHelper fileContextTestHelper = new FileContextTestHelper("/tmp/TestEntityGroupFSTimelineStore");

    private static List<ApplicationId> sampleAppIds;

    private static ApplicationId mainTestAppId;

    private static Path mainTestAppDirPath;

    private static Path testDoneDirPath;

    private static Path testActiveDirPath;

    private static String mainEntityLogFileName;

    private EntityGroupFSTimelineStore store;

    private TimelineEntity entityNew;

    @Rule
    public TestName currTestName = new TestName();

    private File rootDir;

    private File testJar;

    @Test
    public void testAppLogsScanLogs() throws Exception {
        EntityGroupFSTimelineStore.AppLogs appLogs = store.new AppLogs(TestEntityGroupFSTimelineStore.mainTestAppId, TestEntityGroupFSTimelineStore.mainTestAppDirPath, AppState.COMPLETED);
        appLogs.scanForLogs();
        List<LogInfo> summaryLogs = appLogs.getSummaryLogs();
        List<LogInfo> detailLogs = appLogs.getDetailLogs();
        Assert.assertEquals(2, summaryLogs.size());
        Assert.assertEquals(1, detailLogs.size());
        for (LogInfo log : summaryLogs) {
            String fileName = log.getFilename();
            Assert.assertTrue(((fileName.equals(TestEntityGroupFSTimelineStore.TEST_SUMMARY_LOG_FILE_NAME)) || (fileName.equals(TestEntityGroupFSTimelineStore.TEST_DOMAIN_LOG_FILE_NAME))));
        }
        for (LogInfo log : detailLogs) {
            String fileName = log.getFilename();
            Assert.assertEquals(fileName, TestEntityGroupFSTimelineStore.mainEntityLogFileName);
        }
    }

    @Test
    public void testMoveToDone() throws Exception {
        EntityGroupFSTimelineStore.AppLogs appLogs = store.new AppLogs(TestEntityGroupFSTimelineStore.mainTestAppId, TestEntityGroupFSTimelineStore.mainTestAppDirPath, AppState.COMPLETED);
        Path pathBefore = appLogs.getAppDirPath();
        appLogs.moveToDone();
        Path pathAfter = appLogs.getAppDirPath();
        Assert.assertNotEquals(pathBefore, pathAfter);
        Assert.assertTrue(pathAfter.toString().contains(TestEntityGroupFSTimelineStore.testDoneDirPath.toString()));
        TestEntityGroupFSTimelineStore.fs.delete(pathAfter, true);
    }

    @Test
    public void testParseSummaryLogs() throws Exception {
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithMemStore(TestEntityGroupFSTimelineStore.config);
        MutableCounterLong scanned = store.metrics.getEntitiesReadToSummary();
        long beforeScan = scanned.value();
        EntityGroupFSTimelineStore.AppLogs appLogs = store.new AppLogs(TestEntityGroupFSTimelineStore.mainTestAppId, TestEntityGroupFSTimelineStore.mainTestAppDirPath, AppState.COMPLETED);
        appLogs.scanForLogs();
        appLogs.parseSummaryLogs(tdm);
        PluginStoreTestUtils.verifyTestEntities(tdm);
        Assert.assertEquals((beforeScan + 2L), scanned.value());
    }

    @Test
    public void testCleanLogs() throws Exception {
        // Create test dirs and files
        // Irrelevant file, should not be reclaimed
        String appDirName = TestEntityGroupFSTimelineStore.mainTestAppId.toString();
        String attemptDirName = ((ApplicationAttemptId.appAttemptIdStrPrefix) + appDirName) + "_1";
        Path irrelevantFilePath = new Path(TestEntityGroupFSTimelineStore.testDoneDirPath, "irrelevant.log");
        FSDataOutputStream stream = TestEntityGroupFSTimelineStore.fs.create(irrelevantFilePath);
        stream.close();
        // Irrelevant directory, should not be reclaimed
        Path irrelevantDirPath = new Path(TestEntityGroupFSTimelineStore.testDoneDirPath, "irrelevant");
        TestEntityGroupFSTimelineStore.fs.mkdirs(irrelevantDirPath);
        Path doneAppHomeDir = new Path(new Path(TestEntityGroupFSTimelineStore.testDoneDirPath, "0000"), "001");
        // First application, untouched after creation
        Path appDirClean = new Path(doneAppHomeDir, appDirName);
        Path attemptDirClean = new Path(appDirClean, attemptDirName);
        TestEntityGroupFSTimelineStore.fs.mkdirs(attemptDirClean);
        Path filePath = new Path(attemptDirClean, "test.log");
        stream = TestEntityGroupFSTimelineStore.fs.create(filePath);
        stream.close();
        // Second application, one file touched after creation
        Path appDirHoldByFile = new Path(doneAppHomeDir, (appDirName + "1"));
        Path attemptDirHoldByFile = new Path(appDirHoldByFile, attemptDirName);
        TestEntityGroupFSTimelineStore.fs.mkdirs(attemptDirHoldByFile);
        Path filePathHold = new Path(attemptDirHoldByFile, "test1.log");
        stream = TestEntityGroupFSTimelineStore.fs.create(filePathHold);
        stream.close();
        // Third application, one dir touched after creation
        Path appDirHoldByDir = new Path(doneAppHomeDir, (appDirName + "2"));
        Path attemptDirHoldByDir = new Path(appDirHoldByDir, attemptDirName);
        TestEntityGroupFSTimelineStore.fs.mkdirs(attemptDirHoldByDir);
        Path dirPathHold = new Path(attemptDirHoldByDir, "hold");
        TestEntityGroupFSTimelineStore.fs.mkdirs(dirPathHold);
        // Fourth application, empty dirs
        Path appDirEmpty = new Path(doneAppHomeDir, (appDirName + "3"));
        Path attemptDirEmpty = new Path(appDirEmpty, attemptDirName);
        TestEntityGroupFSTimelineStore.fs.mkdirs(attemptDirEmpty);
        Path dirPathEmpty = new Path(attemptDirEmpty, "empty");
        TestEntityGroupFSTimelineStore.fs.mkdirs(dirPathEmpty);
        // Should retain all logs after this run
        MutableCounterLong dirsCleaned = store.metrics.getLogsDirsCleaned();
        long before = dirsCleaned.value();
        store.cleanLogs(TestEntityGroupFSTimelineStore.testDoneDirPath, TestEntityGroupFSTimelineStore.fs, 10000);
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(irrelevantDirPath));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(irrelevantFilePath));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(filePath));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(filePathHold));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(dirPathHold));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(dirPathEmpty));
        // Make sure the created dir is old enough
        Thread.sleep(2000);
        // Touch the second application
        stream = TestEntityGroupFSTimelineStore.fs.append(filePathHold);
        stream.writeBytes("append");
        stream.close();
        // Touch the third application by creating a new dir
        TestEntityGroupFSTimelineStore.fs.mkdirs(new Path(dirPathHold, "holdByMe"));
        store.cleanLogs(TestEntityGroupFSTimelineStore.testDoneDirPath, TestEntityGroupFSTimelineStore.fs, 1000);
        // Verification after the second cleaner call
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(irrelevantDirPath));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(irrelevantFilePath));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(filePathHold));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(dirPathHold));
        Assert.assertTrue(TestEntityGroupFSTimelineStore.fs.exists(doneAppHomeDir));
        // appDirClean and appDirEmpty should be cleaned up
        Assert.assertFalse(TestEntityGroupFSTimelineStore.fs.exists(appDirClean));
        Assert.assertFalse(TestEntityGroupFSTimelineStore.fs.exists(appDirEmpty));
        Assert.assertEquals((before + 2L), dirsCleaned.value());
    }

    @Test
    public void testPluginRead() throws Exception {
        // Verify precondition
        Assert.assertEquals(EntityGroupPlugInForTest.class.getName(), store.getConfig().get(TIMELINE_SERVICE_ENTITY_GROUP_PLUGIN_CLASSES));
        List<TimelineEntityGroupPlugin> currPlugins = store.getPlugins();
        for (TimelineEntityGroupPlugin plugin : currPlugins) {
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Assert.assertTrue("Should set up ApplicationClassLoader", (pluginClassLoader instanceof ApplicationClassLoader));
            URL[] paths = ((URLClassLoader) (pluginClassLoader)).getURLs();
            boolean foundJAR = false;
            for (URL path : paths) {
                if (path.toString().contains(testJar.getAbsolutePath())) {
                    foundJAR = true;
                }
            }
            Assert.assertTrue(((("Not found path " + (testJar.getAbsolutePath())) + " for plugin ") + (plugin.getClass().getName())), foundJAR);
        }
        // Load data and cache item, prepare timeline store by making a cache item
        EntityGroupFSTimelineStore.AppLogs appLogs = store.new AppLogs(TestEntityGroupFSTimelineStore.mainTestAppId, TestEntityGroupFSTimelineStore.mainTestAppDirPath, AppState.COMPLETED);
        EntityCacheItem cacheItem = new EntityCacheItem(EntityGroupPlugInForTest.getStandardTimelineGroupId(TestEntityGroupFSTimelineStore.mainTestAppId), TestEntityGroupFSTimelineStore.config);
        cacheItem.setAppLogs(appLogs);
        store.setCachedLogs(EntityGroupPlugInForTest.getStandardTimelineGroupId(TestEntityGroupFSTimelineStore.mainTestAppId), cacheItem);
        MutableCounterLong detailLogEntityRead = store.metrics.getGetEntityToDetailOps();
        MutableStat cacheRefresh = store.metrics.getCacheRefresh();
        long numEntityReadBefore = detailLogEntityRead.value();
        long cacheRefreshBefore = cacheRefresh.lastStat().numSamples();
        // Generate TDM
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithStore(TestEntityGroupFSTimelineStore.config, store);
        // Verify single entity read
        TimelineEntity entity3 = tdm.getEntity("type_3", TestEntityGroupFSTimelineStore.mainTestAppId.toString(), EnumSet.allOf(Field.class), UserGroupInformation.getLoginUser());
        Assert.assertNotNull(entity3);
        Assert.assertEquals(entityNew.getStartTime(), entity3.getStartTime());
        // Verify multiple entities read
        NameValuePair primaryFilter = new NameValuePair(EntityGroupPlugInForTest.APP_ID_FILTER_NAME, TestEntityGroupFSTimelineStore.mainTestAppId.toString());
        TimelineEntities entities = tdm.getEntities("type_3", primaryFilter, null, null, null, null, null, null, EnumSet.allOf(Field.class), UserGroupInformation.getLoginUser());
        Assert.assertEquals(1, entities.getEntities().size());
        for (TimelineEntity entity : entities.getEntities()) {
            Assert.assertEquals(entityNew.getStartTime(), entity.getStartTime());
        }
        // Verify metrics
        Assert.assertEquals((numEntityReadBefore + 2L), detailLogEntityRead.value());
        Assert.assertEquals((cacheRefreshBefore + 1L), cacheRefresh.lastStat().numSamples());
    }

    @Test
    public void testSummaryRead() throws Exception {
        // Load data
        EntityGroupFSTimelineStore.AppLogs appLogs = store.new AppLogs(TestEntityGroupFSTimelineStore.mainTestAppId, TestEntityGroupFSTimelineStore.mainTestAppDirPath, AppState.COMPLETED);
        MutableCounterLong summaryLogEntityRead = store.metrics.getGetEntityToSummaryOps();
        long numEntityReadBefore = summaryLogEntityRead.value();
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithStore(TestEntityGroupFSTimelineStore.config, store);
        appLogs.scanForLogs();
        appLogs.parseSummaryLogs(tdm);
        // Verify single entity read
        PluginStoreTestUtils.verifyTestEntities(tdm);
        // Verify multiple entities read
        TimelineEntities entities = tdm.getEntities("type_1", null, null, null, null, null, null, null, EnumSet.allOf(Field.class), UserGroupInformation.getLoginUser());
        Assert.assertEquals(entities.getEntities().size(), 1);
        for (TimelineEntity entity : entities.getEntities()) {
            Assert.assertEquals(((Long) (123L)), entity.getStartTime());
        }
        // Verify metrics
        Assert.assertEquals((numEntityReadBefore + 5L), summaryLogEntityRead.value());
    }

    @Test
    public void testGetEntityPluginRead() throws Exception {
        EntityGroupFSTimelineStore store = null;
        ApplicationId appId = ApplicationId.fromString("application_1501509265053_0001");
        String user = UserGroupInformation.getCurrentUser().getShortUserName();
        Path userBase = new Path(TestEntityGroupFSTimelineStore.testActiveDirPath, user);
        Path userAppRoot = new Path(userBase, appId.toString());
        Path attemotDirPath = new Path(userAppRoot, TestEntityGroupFSTimelineStore.getAttemptDirName(appId));
        try {
            store = createAndStartTimelineStore(ACTIVE);
            String logFileName = (ENTITY_LOG_PREFIX) + (EntityGroupPlugInForTest.getStandardTimelineGroupId(appId));
            createTestFiles(appId, attemotDirPath, logFileName);
            TimelineEntity entity = store.getEntity(entityNew.getEntityId(), entityNew.getEntityType(), EnumSet.allOf(Field.class));
            Assert.assertNotNull(entity);
            Assert.assertEquals(entityNew.getEntityId(), entity.getEntityId());
            Assert.assertEquals(entityNew.getEntityType(), entity.getEntityType());
        } finally {
            if (store != null) {
                store.stop();
            }
            TestEntityGroupFSTimelineStore.fs.delete(userBase, true);
        }
    }

    @Test
    public void testScanActiveLogsAndMoveToDonePluginRead() throws Exception {
        EntityGroupFSTimelineStore store = null;
        ApplicationId appId = ApplicationId.fromString("application_1501509265053_0002");
        String user = UserGroupInformation.getCurrentUser().getShortUserName();
        Path userBase = new Path(TestEntityGroupFSTimelineStore.testActiveDirPath, user);
        Path userAppRoot = new Path(userBase, appId.toString());
        Path attemotDirPath = new Path(userAppRoot, TestEntityGroupFSTimelineStore.getAttemptDirName(appId));
        try {
            store = createAndStartTimelineStore(COMPLETED);
            String logFileName = (ENTITY_LOG_PREFIX) + (EntityGroupPlugInForTest.getStandardTimelineGroupId(appId));
            createTestFiles(appId, attemotDirPath, logFileName);
            store.scanActiveLogs();
            TimelineEntity entity = store.getEntity(entityNew.getEntityId(), entityNew.getEntityType(), EnumSet.allOf(Field.class));
            Assert.assertNotNull(entity);
            Assert.assertEquals(entityNew.getEntityId(), entity.getEntityId());
            Assert.assertEquals(entityNew.getEntityType(), entity.getEntityType());
        } finally {
            if (store != null) {
                store.stop();
            }
            TestEntityGroupFSTimelineStore.fs.delete(userBase, true);
        }
    }
}

