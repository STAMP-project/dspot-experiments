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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.logaggregation;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogKey;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogValue;
import org.apache.hadoop.yarn.logaggregation.filecontroller.LogAggregationDFSException;
import org.apache.hadoop.yarn.logaggregation.filecontroller.tfile.LogAggregationTFileController;
import org.apache.hadoop.yarn.server.api.ContainerType;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.logaggregation.tracker.NMLogAggregationStatusTracker;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests of AppLogAggregatorImpl class.
 */
public class TestAppLogAggregatorImpl {
    private static final File LOCAL_LOG_DIR = new File("target", ((TestAppLogAggregatorImpl.class.getName()) + "-localLogDir"));

    private static final File REMOTE_LOG_FILE = new File("target", ((TestAppLogAggregatorImpl.class.getName()) + "-remoteLogFile"));

    @Test
    public void testAggregatorWithRetentionPolicyDisabledShouldUploadAllFiles() throws Exception {
        final ApplicationId applicationId = ApplicationId.newInstance(System.currentTimeMillis(), 0);
        final ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 0);
        final ContainerId containerId = ContainerId.newContainerId(attemptId, 0);
        // create artificial log files
        final File appLogDir = new File(TestAppLogAggregatorImpl.LOCAL_LOG_DIR, applicationId.toString());
        final File containerLogDir = new File(appLogDir, containerId.toString());
        containerLogDir.mkdirs();
        final Set<File> logFiles = TestAppLogAggregatorImpl.createContainerLogFiles(containerLogDir, 3);
        final long logRetentionSecs = 10000;
        final long recoveredLogInitedTime = -1;
        verifyLogAggregationWithExpectedFiles2DeleteAndUpload(applicationId, containerId, logRetentionSecs, recoveredLogInitedTime, logFiles, logFiles);
    }

    @Test
    public void testAggregatorWhenNoFileOlderThanRetentionPolicyShouldUploadAll() throws IOException {
        final ApplicationId applicationId = ApplicationId.newInstance(System.currentTimeMillis(), 0);
        final ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 0);
        final ContainerId containerId = ContainerId.newContainerId(attemptId, 0);
        // create artificial log files
        final File appLogDir = new File(TestAppLogAggregatorImpl.LOCAL_LOG_DIR, applicationId.toString());
        final File containerLogDir = new File(appLogDir, containerId.toString());
        containerLogDir.mkdirs();
        final Set<File> logFiles = TestAppLogAggregatorImpl.createContainerLogFiles(containerLogDir, 3);
        // set log retention period to 1 week.
        final long logRententionSec = ((7 * 24) * 60) * 60;
        final long recoveredLogInitedTimeMillis = (System.currentTimeMillis()) - (60 * 60);
        verifyLogAggregationWithExpectedFiles2DeleteAndUpload(applicationId, containerId, logRententionSec, recoveredLogInitedTimeMillis, logFiles, logFiles);
    }

    @Test
    public void testAggregatorWhenAllFilesOlderThanRetentionShouldUploadNone() throws IOException {
        final ApplicationId applicationId = ApplicationId.newInstance(System.currentTimeMillis(), 0);
        final ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 0);
        final ContainerId containerId = ContainerId.newContainerId(attemptId, 0);
        // create artificial log files
        final File appLogDir = new File(TestAppLogAggregatorImpl.LOCAL_LOG_DIR, applicationId.toString());
        final File containerLogDir = new File(appLogDir, containerId.toString());
        containerLogDir.mkdirs();
        final Set<File> logFiles = TestAppLogAggregatorImpl.createContainerLogFiles(containerLogDir, 3);
        final long week = ((7 * 24) * 60) * 60;
        final long recoveredLogInitedTimeMillis = (System.currentTimeMillis()) - ((2 * week) * 1000);
        verifyLogAggregationWithExpectedFiles2DeleteAndUpload(applicationId, containerId, week, recoveredLogInitedTimeMillis, logFiles, new HashSet<File>());
    }

    private static final class AppLogAggregatorInTest extends AppLogAggregatorImpl {
        final DeletionService deletionService;

        final ApplicationId applicationId;

        final ArgumentCaptor<LogValue> logValue;

        public AppLogAggregatorInTest(Dispatcher dispatcher, DeletionService deletionService, Configuration conf, ApplicationId appId, UserGroupInformation ugi, NodeId nodeId, LocalDirsHandlerService dirsHandler, Path remoteNodeLogFileForApp, Map<ApplicationAccessType, String> appAcls, LogAggregationContext logAggregationContext, Context context, FileContext lfs, long recoveredLogInitedTime, LogAggregationTFileController format) throws IOException {
            super(dispatcher, deletionService, conf, appId, ugi, nodeId, dirsHandler, remoteNodeLogFileForApp, appAcls, logAggregationContext, context, lfs, (-1), recoveredLogInitedTime, format);
            this.applicationId = appId;
            this.deletionService = deletionService;
            this.logValue = ArgumentCaptor.forClass(LogValue.class);
        }
    }

    @Test
    public void testDFSQuotaExceeded() throws Exception {
        // the expectation is that no log files are deleted if the quota has
        // been exceeded, since that would result in loss of logs
        DeletionService deletionServiceWithExpectedFiles = TestAppLogAggregatorImpl.createDeletionServiceWithExpectedFile2Delete(Collections.emptySet());
        final YarnConfiguration config = new YarnConfiguration();
        ApplicationId appId = ApplicationId.newInstance(1357543L, 1);
        // we need a LogAggregationTFileController that throws a
        // LogAggregationDFSException
        LogAggregationTFileController format = Mockito.mock(LogAggregationTFileController.class);
        Mockito.doThrow(new LogAggregationDFSException()).when(format).closeWriter();
        NodeManager.NMContext context = ((NMContext) (TestAppLogAggregatorImpl.createContext(config)));
        context.setNMLogAggregationStatusTracker(Mockito.mock(NMLogAggregationStatusTracker.class));
        final TestAppLogAggregatorImpl.AppLogAggregatorInTest appLogAggregator = TestAppLogAggregatorImpl.createAppLogAggregator(appId, TestAppLogAggregatorImpl.LOCAL_LOG_DIR.getAbsolutePath(), config, context, 1000L, deletionServiceWithExpectedFiles, format);
        startContainerLogAggregation(new org.apache.hadoop.yarn.server.api.ContainerLogContext(ContainerId.newContainerId(ApplicationAttemptId.newInstance(appId, 0), 0), ContainerType.TASK, 0));
        // set app finished flag first
        finishLogAggregation();
        run();
        // verify that no files have been uploaded
        ArgumentCaptor<LogValue> logValCaptor = ArgumentCaptor.forClass(LogValue.class);
        Mockito.verify(getLogAggregationFileController()).write(ArgumentMatchers.any(LogKey.class), logValCaptor.capture());
        Set<String> filesUploaded = new HashSet<>();
        LogValue logValue = logValCaptor.getValue();
        for (File file : logValue.getPendingLogFilesToUploadForThisContainer()) {
            filesUploaded.add(file.getAbsolutePath());
        }
        TestAppLogAggregatorImpl.verifyFilesUploaded(filesUploaded, Collections.emptySet());
    }
}

