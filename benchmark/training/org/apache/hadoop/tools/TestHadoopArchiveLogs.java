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
package org.apache.hadoop.tools;


import HadoopArchiveLogs.AppInfo;
import LogAggregationStatus.DISABLED;
import LogAggregationStatus.FAILED;
import LogAggregationStatus.NOT_START;
import LogAggregationStatus.RUNNING;
import LogAggregationStatus.RUNNING_WITH_FAILURE;
import LogAggregationStatus.SUCCEEDED;
import LogAggregationStatus.TIME_OUT;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.LogAggregationStatus;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.junit.Assert;
import org.junit.Test;


public class TestHadoopArchiveLogs {
    private static final long CLUSTER_TIMESTAMP = System.currentTimeMillis();

    private static final String USER = System.getProperty("user.name");

    private static final int FILE_SIZE_INCREMENT = 4096;

    private static final byte[] DUMMY_DATA = new byte[TestHadoopArchiveLogs.FILE_SIZE_INCREMENT];

    static {
        new Random().nextBytes(TestHadoopArchiveLogs.DUMMY_DATA);
    }

    @Test(timeout = 10000)
    public void testCheckFilesAndSeedApps() throws Exception {
        Configuration conf = new Configuration();
        HadoopArchiveLogs hal = new HadoopArchiveLogs(conf);
        FileSystem fs = FileSystem.getLocal(conf);
        Path rootLogDir = new Path("target", "logs");
        String suffix = "logs";
        Path logDir = new Path(rootLogDir, new Path(TestHadoopArchiveLogs.USER, suffix));
        fs.delete(logDir, true);
        Assert.assertFalse(fs.exists(logDir));
        fs.mkdirs(logDir);
        // no files found
        ApplicationId appId1 = ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 1);
        Path app1Path = new Path(logDir, appId1.toString());
        fs.mkdirs(app1Path);
        // too few files
        ApplicationId appId2 = ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 2);
        Path app2Path = new Path(logDir, appId2.toString());
        fs.mkdirs(app2Path);
        TestHadoopArchiveLogs.createFile(fs, new Path(app2Path, "file1"), 1);
        hal.minNumLogFiles = 2;
        // too large
        ApplicationId appId3 = ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 3);
        Path app3Path = new Path(logDir, appId3.toString());
        fs.mkdirs(app3Path);
        TestHadoopArchiveLogs.createFile(fs, new Path(app3Path, "file1"), 2);
        TestHadoopArchiveLogs.createFile(fs, new Path(app3Path, "file2"), 5);
        hal.maxTotalLogsSize = (TestHadoopArchiveLogs.FILE_SIZE_INCREMENT) * 6;
        // has har already
        ApplicationId appId4 = ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 4);
        Path app4Path = new Path(logDir, appId4.toString());
        fs.mkdirs(app4Path);
        TestHadoopArchiveLogs.createFile(fs, new Path(app4Path, (appId4 + ".har")), 1);
        // just right
        ApplicationId appId5 = ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 5);
        Path app5Path = new Path(logDir, appId5.toString());
        fs.mkdirs(app5Path);
        TestHadoopArchiveLogs.createFile(fs, new Path(app5Path, "file1"), 2);
        TestHadoopArchiveLogs.createFile(fs, new Path(app5Path, "file2"), 3);
        Assert.assertEquals(0, hal.eligibleApplications.size());
        hal.checkFilesAndSeedApps(fs, rootLogDir, suffix, new Path(rootLogDir, "archive-logs-work"));
        Assert.assertEquals(1, hal.eligibleApplications.size());
        Assert.assertEquals(appId5.toString(), hal.eligibleApplications.iterator().next().getAppId());
    }

    @Test(timeout = 10000)
    public void testCheckMaxEligible() throws Exception {
        Configuration conf = new Configuration();
        HadoopArchiveLogs.AppInfo app1 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 1).toString(), TestHadoopArchiveLogs.USER);
        app1.setFinishTime(((TestHadoopArchiveLogs.CLUSTER_TIMESTAMP) - 5));
        HadoopArchiveLogs.AppInfo app2 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 2).toString(), TestHadoopArchiveLogs.USER);
        app2.setFinishTime(((TestHadoopArchiveLogs.CLUSTER_TIMESTAMP) - 10));
        HadoopArchiveLogs.AppInfo app3 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 3).toString(), TestHadoopArchiveLogs.USER);
        // app3 has no finish time set
        HadoopArchiveLogs.AppInfo app4 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 4).toString(), TestHadoopArchiveLogs.USER);
        app4.setFinishTime(((TestHadoopArchiveLogs.CLUSTER_TIMESTAMP) + 5));
        HadoopArchiveLogs.AppInfo app5 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 5).toString(), TestHadoopArchiveLogs.USER);
        app5.setFinishTime(((TestHadoopArchiveLogs.CLUSTER_TIMESTAMP) + 10));
        HadoopArchiveLogs.AppInfo app6 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 6).toString(), TestHadoopArchiveLogs.USER);
        // app6 has no finish time set
        HadoopArchiveLogs.AppInfo app7 = new HadoopArchiveLogs.AppInfo(ApplicationId.newInstance(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP, 7).toString(), TestHadoopArchiveLogs.USER);
        app7.setFinishTime(TestHadoopArchiveLogs.CLUSTER_TIMESTAMP);
        HadoopArchiveLogs hal = new HadoopArchiveLogs(conf);
        Assert.assertEquals(0, hal.eligibleApplications.size());
        hal.eligibleApplications.add(app1);
        hal.eligibleApplications.add(app2);
        hal.eligibleApplications.add(app3);
        hal.eligibleApplications.add(app4);
        hal.eligibleApplications.add(app5);
        hal.eligibleApplications.add(app6);
        hal.eligibleApplications.add(app7);
        Assert.assertEquals(7, hal.eligibleApplications.size());
        hal.maxEligible = -1;
        hal.checkMaxEligible();
        Assert.assertEquals(7, hal.eligibleApplications.size());
        hal.maxEligible = 6;
        hal.checkMaxEligible();
        Assert.assertEquals(6, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app5));
        hal.maxEligible = 5;
        hal.checkMaxEligible();
        Assert.assertEquals(5, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app4));
        hal.maxEligible = 4;
        hal.checkMaxEligible();
        Assert.assertEquals(4, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app7));
        hal.maxEligible = 3;
        hal.checkMaxEligible();
        Assert.assertEquals(3, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app1));
        hal.maxEligible = 2;
        hal.checkMaxEligible();
        Assert.assertEquals(2, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app2));
        hal.maxEligible = 1;
        hal.checkMaxEligible();
        Assert.assertEquals(1, hal.eligibleApplications.size());
        Assert.assertFalse(hal.eligibleApplications.contains(app6));
        Assert.assertTrue(hal.eligibleApplications.contains(app3));
    }

    @Test(timeout = 30000)
    public void testFilterAppsByAggregatedStatus() throws Exception {
        try (MiniYARNCluster yarnCluster = new MiniYARNCluster(TestHadoopArchiveLogs.class.getSimpleName(), 1, 1, 1, 1)) {
            Configuration conf = new Configuration();
            conf.setBoolean(LOG_AGGREGATION_ENABLED, true);
            yarnCluster.init(conf);
            yarnCluster.start();
            conf = yarnCluster.getConfig();
            RMContext rmContext = yarnCluster.getResourceManager().getRMContext();
            RMAppImpl appImpl1 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(1, conf, rmContext, DISABLED)));
            RMAppImpl appImpl2 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(2, conf, rmContext, FAILED)));
            RMAppImpl appImpl3 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(3, conf, rmContext, NOT_START)));
            RMAppImpl appImpl4 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(4, conf, rmContext, SUCCEEDED)));
            RMAppImpl appImpl5 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(5, conf, rmContext, RUNNING)));
            RMAppImpl appImpl6 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(6, conf, rmContext, RUNNING_WITH_FAILURE)));
            RMAppImpl appImpl7 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(7, conf, rmContext, TIME_OUT)));
            RMAppImpl appImpl8 = ((RMAppImpl) (TestHadoopArchiveLogs.createRMApp(8, conf, rmContext, SUCCEEDED)));
            rmContext.getRMApps().put(appImpl1.getApplicationId(), appImpl1);
            rmContext.getRMApps().put(appImpl2.getApplicationId(), appImpl2);
            rmContext.getRMApps().put(appImpl3.getApplicationId(), appImpl3);
            rmContext.getRMApps().put(appImpl4.getApplicationId(), appImpl4);
            rmContext.getRMApps().put(appImpl5.getApplicationId(), appImpl5);
            rmContext.getRMApps().put(appImpl6.getApplicationId(), appImpl6);
            rmContext.getRMApps().put(appImpl7.getApplicationId(), appImpl7);
            // appImpl8 is not in the RM
            HadoopArchiveLogs hal = new HadoopArchiveLogs(conf);
            Assert.assertEquals(0, hal.eligibleApplications.size());
            hal.eligibleApplications.add(new HadoopArchiveLogs.AppInfo(appImpl1.getApplicationId().toString(), TestHadoopArchiveLogs.USER));
            hal.eligibleApplications.add(new HadoopArchiveLogs.AppInfo(appImpl2.getApplicationId().toString(), TestHadoopArchiveLogs.USER));
            hal.eligibleApplications.add(new HadoopArchiveLogs.AppInfo(appImpl3.getApplicationId().toString(), TestHadoopArchiveLogs.USER));
            HadoopArchiveLogs.AppInfo app4 = new HadoopArchiveLogs.AppInfo(appImpl4.getApplicationId().toString(), TestHadoopArchiveLogs.USER);
            hal.eligibleApplications.add(app4);
            hal.eligibleApplications.add(new HadoopArchiveLogs.AppInfo(appImpl5.getApplicationId().toString(), TestHadoopArchiveLogs.USER));
            hal.eligibleApplications.add(new HadoopArchiveLogs.AppInfo(appImpl6.getApplicationId().toString(), TestHadoopArchiveLogs.USER));
            HadoopArchiveLogs.AppInfo app7 = new HadoopArchiveLogs.AppInfo(appImpl7.getApplicationId().toString(), TestHadoopArchiveLogs.USER);
            hal.eligibleApplications.add(app7);
            HadoopArchiveLogs.AppInfo app8 = new HadoopArchiveLogs.AppInfo(appImpl8.getApplicationId().toString(), TestHadoopArchiveLogs.USER);
            hal.eligibleApplications.add(app8);
            Assert.assertEquals(8, hal.eligibleApplications.size());
            hal.filterAppsByAggregatedStatus();
            Assert.assertEquals(3, hal.eligibleApplications.size());
            Assert.assertTrue(hal.eligibleApplications.contains(app4));
            Assert.assertTrue(hal.eligibleApplications.contains(app7));
            Assert.assertTrue(hal.eligibleApplications.contains(app8));
        }
    }

    @Test(timeout = 10000)
    public void testGenerateScript() throws Exception {
        _testGenerateScript(false);
        _testGenerateScript(true);
    }

    /**
     * If this test failes, then a new Log Aggregation Status was added.  Make
     * sure that {@link HadoopArchiveLogs#filterAppsByAggregatedStatus()} and this test
     * are updated as well, if necessary.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 5000)
    public void testStatuses() throws Exception {
        LogAggregationStatus[] statuses = new LogAggregationStatus[7];
        statuses[0] = LogAggregationStatus.DISABLED;
        statuses[1] = LogAggregationStatus.NOT_START;
        statuses[2] = LogAggregationStatus.RUNNING;
        statuses[3] = LogAggregationStatus.RUNNING_WITH_FAILURE;
        statuses[4] = LogAggregationStatus.SUCCEEDED;
        statuses[5] = LogAggregationStatus.FAILED;
        statuses[6] = LogAggregationStatus.TIME_OUT;
        Assert.assertArrayEquals(statuses, LogAggregationStatus.values());
    }

    @Test(timeout = 5000)
    public void testPrepareWorkingDir() throws Exception {
        Configuration conf = new Configuration();
        HadoopArchiveLogs hal = new HadoopArchiveLogs(conf);
        FileSystem fs = FileSystem.getLocal(conf);
        Path workingDir = new Path("target", "testPrepareWorkingDir");
        fs.delete(workingDir, true);
        Assert.assertFalse(fs.exists(workingDir));
        // -force is false and the dir doesn't exist so it will create one
        hal.force = false;
        boolean dirPrepared = hal.prepareWorkingDir(fs, workingDir);
        Assert.assertTrue(dirPrepared);
        Assert.assertTrue(fs.exists(workingDir));
        Assert.assertEquals(new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL, (!(Shell.WINDOWS))), fs.getFileStatus(workingDir).getPermission());
        // Throw a file in the dir
        Path dummyFile = new Path(workingDir, "dummy.txt");
        fs.createNewFile(dummyFile);
        Assert.assertTrue(fs.exists(dummyFile));
        // -force is false and the dir exists, so nothing will happen and the dummy
        // still exists
        dirPrepared = hal.prepareWorkingDir(fs, workingDir);
        Assert.assertFalse(dirPrepared);
        Assert.assertTrue(fs.exists(workingDir));
        Assert.assertTrue(fs.exists(dummyFile));
        Assert.assertEquals(new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL, (!(Shell.WINDOWS))), fs.getFileStatus(workingDir).getPermission());
        // -force is true and the dir exists, so it will recreate it and the dummy
        // won't exist anymore
        hal.force = true;
        dirPrepared = hal.prepareWorkingDir(fs, workingDir);
        Assert.assertTrue(dirPrepared);
        Assert.assertTrue(fs.exists(workingDir));
        Assert.assertEquals(new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL, (!(Shell.WINDOWS))), fs.getFileStatus(workingDir).getPermission());
        Assert.assertFalse(fs.exists(dummyFile));
    }
}

