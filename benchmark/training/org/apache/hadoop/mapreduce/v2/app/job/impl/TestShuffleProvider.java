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
import MRJobConfig.APPLICATION_ATTEMPT_ID;
import MRJobConfig.MAPREDUCE_JOB_SHUFFLE_PROVIDER_SERVICES;
import TaskType.MAP;
import YarnConfiguration.NM_AUX_SERVICES;
import YarnConfiguration.NM_AUX_SERVICE_FMT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.WrappedJvmID;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier;
import org.apache.hadoop.mapreduce.split.JobSplit.TaskSplitMetaInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.TaskAttemptListener;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.api.ApplicationInitializationContext;
import org.apache.hadoop.yarn.server.api.ApplicationTerminationContext;
import org.apache.hadoop.yarn.server.api.AuxiliaryService;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestShuffleProvider {
    @Test
    public void testShuffleProviders() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        Path jobFile = Mockito.mock(Path.class);
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestShuffleProvider.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(NM_AUX_SERVICES, (((TestShuffleProvider.TestShuffleHandler1.MAPREDUCE_TEST_SHUFFLE_SERVICEID) + ",") + (TestShuffleProvider.TestShuffleHandler2.MAPREDUCE_TEST_SHUFFLE_SERVICEID)));
        String serviceName = TestShuffleProvider.TestShuffleHandler1.MAPREDUCE_TEST_SHUFFLE_SERVICEID;
        String serviceStr = String.format(NM_AUX_SERVICE_FMT, serviceName);
        jobConf.set(serviceStr, TestShuffleProvider.TestShuffleHandler1.class.getName());
        serviceName = TestShuffleProvider.TestShuffleHandler2.MAPREDUCE_TEST_SHUFFLE_SERVICEID;
        serviceStr = String.format(NM_AUX_SERVICE_FMT, serviceName);
        jobConf.set(serviceStr, TestShuffleProvider.TestShuffleHandler2.class.getName());
        jobConf.set(MAPREDUCE_JOB_SHUFFLE_PROVIDER_SERVICES, (((TestShuffleProvider.TestShuffleHandler1.MAPREDUCE_TEST_SHUFFLE_SERVICEID) + ",") + (TestShuffleProvider.TestShuffleHandler2.MAPREDUCE_TEST_SHUFFLE_SERVICEID)));
        Credentials credentials = new Credentials();
        Token<JobTokenIdentifier> jobToken = new Token<JobTokenIdentifier>("tokenid".getBytes(), "tokenpw".getBytes(), new Text("tokenkind"), new Text("tokenservice"));
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, Mockito.mock(TaskSplitMetaInfo.class), jobConf, taListener, jobToken, credentials, SystemClock.getInstance(), null);
        jobConf.set(APPLICATION_ATTEMPT_ID, taImpl.getID().toString());
        ContainerLaunchContext launchCtx = TaskAttemptImpl.createContainerLaunchContext(null, jobConf, jobToken, taImpl.createRemoteTask(), TypeConverter.fromYarn(jobId), Mockito.mock(WrappedJvmID.class), taListener, credentials);
        Map<String, ByteBuffer> serviceDataMap = launchCtx.getServiceData();
        Assert.assertNotNull("TestShuffleHandler1 is missing", serviceDataMap.get(TestShuffleProvider.TestShuffleHandler1.MAPREDUCE_TEST_SHUFFLE_SERVICEID));
        Assert.assertNotNull("TestShuffleHandler2 is missing", serviceDataMap.get(TestShuffleProvider.TestShuffleHandler2.MAPREDUCE_TEST_SHUFFLE_SERVICEID));
        Assert.assertTrue("mismatch number of services in map", ((serviceDataMap.size()) == 3));// 2 that we entered + 1 for the built-in shuffle-provider

    }

    public static class StubbedFS extends RawLocalFileSystem {
        @Override
        public FileStatus getFileStatus(Path f) throws IOException {
            return new FileStatus(1, false, 1, 1, 1, f);
        }
    }

    public static class TestShuffleHandler1 extends AuxiliaryService {
        public static final String MAPREDUCE_TEST_SHUFFLE_SERVICEID = "test_shuffle1";

        public TestShuffleHandler1() {
            super("testshuffle1");
        }

        @Override
        public void initializeApplication(ApplicationInitializationContext context) {
        }

        @Override
        public void stopApplication(ApplicationTerminationContext context) {
        }

        @Override
        public synchronized ByteBuffer getMetaData() {
            return ByteBuffer.allocate(0);// Don't 'return null' because of YARN-1256

        }
    }

    public static class TestShuffleHandler2 extends AuxiliaryService {
        public static final String MAPREDUCE_TEST_SHUFFLE_SERVICEID = "test_shuffle2";

        public TestShuffleHandler2() {
            super("testshuffle2");
        }

        @Override
        public void initializeApplication(ApplicationInitializationContext context) {
        }

        @Override
        public void stopApplication(ApplicationTerminationContext context) {
        }

        @Override
        public synchronized ByteBuffer getMetaData() {
            return ByteBuffer.allocate(0);// Don't 'return null' because of YARN-1256

        }
    }
}

