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
package org.apache.hadoop.mapreduce.v2.app.job.impl;


import JobConf.MAPRED_MAP_TASK_ENV;
import JobState.SUCCEEDED;
import MRConfig.MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM;
import MRJobConfig.MAPREDUCE_JOB_LOG4J_PROPERTIES_FILE;
import MRJobConfig.MAP_LOG_LEVEL;
import MRJobConfig.REDUCE_SEPARATE_SHUFFLE_LOG;
import MRJobConfig.SHUFFLE_LOG_BACKUPS;
import MRJobConfig.SHUFFLE_LOG_KB;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerLauncher;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerLauncherEvent;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerRemoteLaunchEvent;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static EventType.CONTAINER_REMOTE_LAUNCH;


public class TestMapReduceChildJVM {
    private static final Logger LOG = LoggerFactory.getLogger(TestMapReduceChildJVM.class);

    @Test(timeout = 30000)
    public void testCommandLine() throws Exception {
        TestMapReduceChildJVM.MyMRApp app = new TestMapReduceChildJVM.MyMRApp(1, 0, true, this.getClass().getName(), true);
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        Job job = app.submit(conf);
        app.waitForState(job, SUCCEEDED);
        app.verifyCompleted();
        Assert.assertEquals(((((((((((((((((("[" + (MRApps.crossPlatformify("JAVA_HOME"))) + "/bin/java") + " -Djava.net.preferIPv4Stack=true") + " -Dhadoop.metrics.log.level=WARN ") + "  -Xmx820m -Djava.io.tmpdir=") + (MRApps.crossPlatformify("PWD"))) + "/tmp") + " -Dlog4j.configuration=container-log4j.properties") + " -Dyarn.app.container.log.dir=<LOG_DIR>") + " -Dyarn.app.container.log.filesize=0") + " -Dhadoop.root.logger=INFO,CLA -Dhadoop.root.logfile=syslog") + " org.apache.hadoop.mapred.YarnChild 127.0.0.1") + " 54321") + " attempt_0_0000_m_000000_0") + " 0") + " 1><LOG_DIR>/stdout") + " 2><LOG_DIR>/stderr ]"), app.launchCmdList.get(0));
        Assert.assertTrue("HADOOP_ROOT_LOGGER not set for job", app.cmdEnvironment.containsKey("HADOOP_ROOT_LOGGER"));
        Assert.assertEquals("INFO,console", app.cmdEnvironment.get("HADOOP_ROOT_LOGGER"));
        Assert.assertTrue("HADOOP_CLIENT_OPTS not set for job", app.cmdEnvironment.containsKey("HADOOP_CLIENT_OPTS"));
        Assert.assertEquals("", app.cmdEnvironment.get("HADOOP_CLIENT_OPTS"));
    }

    @Test(timeout = 30000)
    public void testReduceCommandLineWithSeparateShuffle() throws Exception {
        final Configuration conf = new Configuration();
        conf.setBoolean(REDUCE_SEPARATE_SHUFFLE_LOG, true);
        testReduceCommandLine(conf);
    }

    @Test(timeout = 30000)
    public void testReduceCommandLineWithSeparateCRLAShuffle() throws Exception {
        final Configuration conf = new Configuration();
        conf.setBoolean(REDUCE_SEPARATE_SHUFFLE_LOG, true);
        conf.setLong(SHUFFLE_LOG_KB, 1L);
        conf.setInt(SHUFFLE_LOG_BACKUPS, 3);
        testReduceCommandLine(conf);
    }

    @Test(timeout = 30000)
    public void testReduceCommandLine() throws Exception {
        final Configuration conf = new Configuration();
        testReduceCommandLine(conf);
    }

    @Test(timeout = 30000)
    public void testCommandLineWithLog4JConifg() throws Exception {
        TestMapReduceChildJVM.MyMRApp app = new TestMapReduceChildJVM.MyMRApp(1, 0, true, this.getClass().getName(), true);
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        String testLogPropertieFile = "test-log4j.properties";
        String testLogPropertiePath = "../" + "test-log4j.properties";
        conf.set(MAPREDUCE_JOB_LOG4J_PROPERTIES_FILE, testLogPropertiePath);
        Job job = app.submit(conf);
        app.waitForState(job, SUCCEEDED);
        app.verifyCompleted();
        Assert.assertEquals((((((((((((((((((("[" + (MRApps.crossPlatformify("JAVA_HOME"))) + "/bin/java") + " -Djava.net.preferIPv4Stack=true") + " -Dhadoop.metrics.log.level=WARN ") + "  -Xmx820m -Djava.io.tmpdir=") + (MRApps.crossPlatformify("PWD"))) + "/tmp") + " -Dlog4j.configuration=") + testLogPropertieFile) + " -Dyarn.app.container.log.dir=<LOG_DIR>") + " -Dyarn.app.container.log.filesize=0") + " -Dhadoop.root.logger=INFO,CLA -Dhadoop.root.logfile=syslog") + " org.apache.hadoop.mapred.YarnChild 127.0.0.1") + " 54321") + " attempt_0_0000_m_000000_0") + " 0") + " 1><LOG_DIR>/stdout") + " 2><LOG_DIR>/stderr ]"), app.launchCmdList.get(0));
    }

    @Test
    public void testAutoHeapSizes() throws Exception {
        // Don't specify heap size or memory-mb
        testAutoHeapSize((-1), (-1), null);
        // Don't specify heap size
        testAutoHeapSize(512, 768, null);
        testAutoHeapSize(100, 768, null);
        testAutoHeapSize(512, 100, null);
        // Specify heap size
        testAutoHeapSize(512, 768, "-Xmx100m");
        testAutoHeapSize(512, 768, "-Xmx500m");
        // Specify heap size but not the memory
        testAutoHeapSize((-1), (-1), "-Xmx100m");
        testAutoHeapSize((-1), (-1), "-Xmx500m");
    }

    private static final class MyMRApp extends MRApp {
        private ArrayList<String> launchCmdList = new ArrayList<>();

        private Map<String, String> cmdEnvironment;

        public MyMRApp(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @Override
        protected ContainerLauncher createContainerLauncher(AppContext context) {
            return new MRApp.MockContainerLauncher() {
                @Override
                public void handle(ContainerLauncherEvent event) {
                    if ((event.getType()) == (CONTAINER_REMOTE_LAUNCH)) {
                        ContainerRemoteLaunchEvent launchEvent = ((ContainerRemoteLaunchEvent) (event));
                        ContainerLaunchContext launchContext = launchEvent.getContainerLaunchContext();
                        String cmdString = launchContext.getCommands().toString();
                        TestMapReduceChildJVM.LOG.info(("launchContext " + cmdString));
                        launchCmdList.add(cmdString);
                        cmdEnvironment = launchContext.getEnvironment();
                    }
                    super.handle(event);
                }
            };
        }
    }

    @Test
    public void testEnvironmentVariables() throws Exception {
        TestMapReduceChildJVM.MyMRApp app = new TestMapReduceChildJVM.MyMRApp(1, 0, true, this.getClass().getName(), true);
        Configuration conf = new Configuration();
        conf.set(MAPRED_MAP_TASK_ENV, "HADOOP_CLIENT_OPTS=test");
        conf.setStrings(MAP_LOG_LEVEL, "WARN");
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, false);
        Job job = app.submit(conf);
        app.waitForState(job, SUCCEEDED);
        app.verifyCompleted();
        Assert.assertTrue("HADOOP_ROOT_LOGGER not set for job", app.cmdEnvironment.containsKey("HADOOP_ROOT_LOGGER"));
        Assert.assertEquals("WARN,console", app.cmdEnvironment.get("HADOOP_ROOT_LOGGER"));
        Assert.assertTrue("HADOOP_CLIENT_OPTS not set for job", app.cmdEnvironment.containsKey("HADOOP_CLIENT_OPTS"));
        Assert.assertEquals("test", app.cmdEnvironment.get("HADOOP_CLIENT_OPTS"));
        // Try one more.
        app = new TestMapReduceChildJVM.MyMRApp(1, 0, true, this.getClass().getName(), true);
        conf = new Configuration();
        conf.set(MAPRED_MAP_TASK_ENV, "HADOOP_ROOT_LOGGER=trace");
        job = app.submit(conf);
        app.waitForState(job, SUCCEEDED);
        app.verifyCompleted();
        Assert.assertTrue("HADOOP_ROOT_LOGGER not set for job", app.cmdEnvironment.containsKey("HADOOP_ROOT_LOGGER"));
        Assert.assertEquals("trace", app.cmdEnvironment.get("HADOOP_ROOT_LOGGER"));
        // Try one using the mapreduce.task.env.var=value syntax
        app = new TestMapReduceChildJVM.MyMRApp(1, 0, true, this.getClass().getName(), true);
        conf = new Configuration();
        conf.set(((JobConf.MAPRED_MAP_TASK_ENV) + ".HADOOP_ROOT_LOGGER"), "DEBUG,console");
        job = app.submit(conf);
        app.waitForState(job, SUCCEEDED);
        app.verifyCompleted();
        Assert.assertTrue("HADOOP_ROOT_LOGGER not set for job", app.cmdEnvironment.containsKey("HADOOP_ROOT_LOGGER"));
        Assert.assertEquals("DEBUG,console", app.cmdEnvironment.get("HADOOP_ROOT_LOGGER"));
    }
}

