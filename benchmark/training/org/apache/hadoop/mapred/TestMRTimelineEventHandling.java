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
package org.apache.hadoop.mapred;


import EventType.AM_STARTED;
import EventType.JOB_FAILED;
import EventType.JOB_FINISHED;
import FileSystemTimelineWriterImpl.TIMELINE_SERVICE_STORAGE_DIR_ROOT;
import JobStatus.FAILED;
import JobStatus.SUCCEEDED;
import MRJobConfig.MAPREDUCE_JOB_EMIT_TIMELINE_DATA;
import YarnConfiguration.NM_AUX_SERVICES;
import YarnConfiguration.SYSTEM_METRICS_PUBLISHER_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS;
import YarnConfiguration.TIMELINE_SERVICE_WRITER_CLASS;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.jobhistory.TestJobHistoryEventHandler;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.apache.hadoop.yarn.server.timelineservice.collector.PerNodeTimelineCollectorsAuxService;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineWriterImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineWriter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRTimelineEventHandling {
    private static final String TIMELINE_AUX_SERVICE_NAME = "timeline_collector";

    private static final Logger LOG = LoggerFactory.getLogger(TestMRTimelineEventHandling.class);

    @Test
    public void testTimelineServiceStartInMiniCluster() throws Exception {
        Configuration conf = new YarnConfiguration();
        /* Timeline service should not start if the config is set to false
        Regardless to the value of MAPREDUCE_JOB_EMIT_TIMELINE_DATA
         */
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, false);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
        MiniMRYarnCluster cluster = null;
        try {
            cluster = new MiniMRYarnCluster(TestMRTimelineEventHandling.class.getSimpleName(), 1);
            cluster.init(conf);
            start();
            // verify that the timeline service is not started.
            Assert.assertNull("Timeline Service should not have been started", getApplicationHistoryServer());
        } finally {
            if (cluster != null) {
                stop();
            }
        }
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, false);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, false);
        cluster = null;
        try {
            cluster = new MiniMRYarnCluster(TestJobHistoryEventHandler.class.getSimpleName(), 1);
            cluster.init(conf);
            start();
            // verify that the timeline service is not started.
            Assert.assertNull("Timeline Service should not have been started", getApplicationHistoryServer());
        } finally {
            if (cluster != null) {
                stop();
            }
        }
    }

    @Test
    public void testMRTimelineEventHandling() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
        MiniMRYarnCluster cluster = null;
        try {
            cluster = new MiniMRYarnCluster(TestMRTimelineEventHandling.class.getSimpleName(), 1);
            cluster.init(conf);
            start();
            conf.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, (((MiniYARNCluster.getHostname()) + ":") + (getApplicationHistoryServer().getPort())));
            TimelineStore ts = getApplicationHistoryServer().getTimelineStore();
            String localPathRoot = System.getProperty("test.build.data", "build/test/data");
            Path inDir = new Path(localPathRoot, "input");
            Path outDir = new Path(localPathRoot, "output");
            RunningJob job = UtilsForTests.runJobSucceed(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            TimelineEntities entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            TimelineEntity tEntity = entities.getEntities().get(0);
            Assert.assertEquals(job.getID().toString(), tEntity.getEntityId());
            Assert.assertEquals("MAPREDUCE_JOB", tEntity.getEntityType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(((tEntity.getEvents().size()) - 1)).getEventType());
            Assert.assertEquals(JOB_FINISHED.toString(), tEntity.getEvents().get(0).getEventType());
            job = UtilsForTests.runJobFail(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(FAILED, job.getJobStatus().getState().getValue());
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(2, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(job.getID().toString(), tEntity.getEntityId());
            Assert.assertEquals("MAPREDUCE_JOB", tEntity.getEntityType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(((tEntity.getEvents().size()) - 1)).getEventType());
            Assert.assertEquals(JOB_FAILED.toString(), tEntity.getEvents().get(0).getEventType());
        } finally {
            if (cluster != null) {
                stop();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testMRNewTimelineServiceEventHandling() throws Exception {
        TestMRTimelineEventHandling.LOG.info("testMRNewTimelineServiceEventHandling start.");
        String testDir = new File("target", ((getClass().getSimpleName()) + "-test_dir")).getAbsolutePath();
        String storageDir = (testDir + (File.separator)) + "timeline_service_data";
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        // enable new timeline service
        conf.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        conf.setClass(TIMELINE_SERVICE_WRITER_CLASS, FileSystemTimelineWriterImpl.class, TimelineWriter.class);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
        // set the file system root directory
        conf.set(TIMELINE_SERVICE_STORAGE_DIR_ROOT, storageDir);
        // enable aux-service based timeline collectors
        conf.set(NM_AUX_SERVICES, TestMRTimelineEventHandling.TIMELINE_AUX_SERVICE_NAME);
        conf.set(((((YarnConfiguration.NM_AUX_SERVICES) + ".") + (TestMRTimelineEventHandling.TIMELINE_AUX_SERVICE_NAME)) + ".class"), PerNodeTimelineCollectorsAuxService.class.getName());
        conf.setBoolean(SYSTEM_METRICS_PUBLISHER_ENABLED, true);
        MiniMRYarnCluster cluster = null;
        try {
            cluster = new MiniMRYarnCluster(TestMRTimelineEventHandling.class.getSimpleName(), 1, true);
            cluster.init(conf);
            start();
            TestMRTimelineEventHandling.LOG.info("A MiniMRYarnCluster get start.");
            Path inDir = new Path(testDir, "input");
            Path outDir = new Path(testDir, "output");
            TestMRTimelineEventHandling.LOG.info("Run 1st job which should be successful.");
            JobConf successConf = new JobConf(conf);
            successConf.set("dummy_conf1", UtilsForTests.createConfigValue((51 * 1024)));
            successConf.set("dummy_conf2", UtilsForTests.createConfigValue((51 * 1024)));
            successConf.set("huge_dummy_conf1", UtilsForTests.createConfigValue((101 * 1024)));
            successConf.set("huge_dummy_conf2", UtilsForTests.createConfigValue((101 * 1024)));
            RunningJob job = UtilsForTests.runJobSucceed(successConf, inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            YarnClient yarnClient = YarnClient.createYarnClient();
            yarnClient.init(new Configuration(getConfig()));
            yarnClient.start();
            EnumSet<YarnApplicationState> appStates = EnumSet.allOf(YarnApplicationState.class);
            ApplicationId firstAppId = null;
            List<ApplicationReport> apps = yarnClient.getApplications(appStates);
            Assert.assertEquals(apps.size(), 1);
            ApplicationReport appReport = apps.get(0);
            firstAppId = appReport.getApplicationId();
            UtilsForTests.waitForAppFinished(job, cluster);
            checkNewTimelineEvent(firstAppId, appReport, storageDir);
            TestMRTimelineEventHandling.LOG.info("Run 2nd job which should be failed.");
            job = UtilsForTests.runJobFail(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(FAILED, job.getJobStatus().getState().getValue());
            apps = yarnClient.getApplications(appStates);
            Assert.assertEquals(apps.size(), 2);
            appReport = (apps.get(0).getApplicationId().equals(firstAppId)) ? apps.get(0) : apps.get(1);
            checkNewTimelineEvent(firstAppId, appReport, storageDir);
        } finally {
            if (cluster != null) {
                stop();
            }
            // Cleanup test file
            File testDirFolder = new File(testDir);
            if (testDirFolder.isDirectory()) {
                FileUtils.deleteDirectory(testDirFolder);
            }
        }
    }

    @Test
    public void testMapreduceJobTimelineServiceEnabled() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, false);
        MiniMRYarnCluster cluster = null;
        FileSystem fs = null;
        Path inDir = new Path(GenericTestUtils.getTempPath("input"));
        Path outDir = new Path(GenericTestUtils.getTempPath("output"));
        try {
            fs = FileSystem.get(conf);
            cluster = new MiniMRYarnCluster(TestMRTimelineEventHandling.class.getSimpleName(), 1);
            cluster.init(conf);
            start();
            conf.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, (((MiniYARNCluster.getHostname()) + ":") + (getApplicationHistoryServer().getPort())));
            TimelineStore ts = getApplicationHistoryServer().getTimelineStore();
            RunningJob job = UtilsForTests.runJobSucceed(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            TimelineEntities entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(0, entities.getEntities().size());
            conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
            job = UtilsForTests.runJobSucceed(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            TimelineEntity tEntity = entities.getEntities().get(0);
            Assert.assertEquals(job.getID().toString(), tEntity.getEntityId());
        } finally {
            if (cluster != null) {
                stop();
            }
            deletePaths(fs, inDir, outDir);
        }
        conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
        cluster = null;
        try {
            cluster = new MiniMRYarnCluster(TestJobHistoryEventHandler.class.getSimpleName(), 1);
            cluster.init(conf);
            start();
            conf.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, (((MiniYARNCluster.getHostname()) + ":") + (getApplicationHistoryServer().getPort())));
            TimelineStore ts = getApplicationHistoryServer().getTimelineStore();
            conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, false);
            RunningJob job = UtilsForTests.runJobSucceed(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            TimelineEntities entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(0, entities.getEntities().size());
            conf.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
            job = UtilsForTests.runJobSucceed(new JobConf(conf), inDir, outDir);
            Assert.assertEquals(SUCCEEDED, job.getJobStatus().getState().getValue());
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            TimelineEntity tEntity = entities.getEntities().get(0);
            Assert.assertEquals(job.getID().toString(), tEntity.getEntityId());
        } finally {
            if (cluster != null) {
                stop();
            }
            deletePaths(fs, inDir, outDir);
        }
    }
}

