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


import CMgrCompletedAppsEvent.Reason;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import ContainerExecutor.ExitCode.FORCE_KILLED;
import ContainerState.COMPLETE;
import ContainerType.APPLICATION_MASTER;
import ContainerType.TASK;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import SampleContainerLogAggregationPolicy.DEFAULT_SAMPLE_MIN_THRESHOLD;
import SampleContainerLogAggregationPolicy.DEFAULT_SAMPLE_RATE;
import YarnConfiguration.DEBUG_NM_DELETE_DELAY_SEC;
import YarnConfiguration.DEFAULT_NM_LOG_AGGREGATION_THREAD_POOL_SIZE;
import YarnConfiguration.NM_DISK_HEALTH_CHECK_INTERVAL_MS;
import YarnConfiguration.NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS;
import YarnConfiguration.NM_LOG_AGGREGATION_THREAD_POOL_SIZE;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.NM_REMOTE_APP_LOG_DIR;
import YarnConfiguration.NM_REMOTE_APP_LOG_DIR_SUFFIX;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.logaggregation.filecontroller.LogAggregationFileController;
import org.apache.hadoop.yarn.logaggregation.filecontroller.LogAggregationFileControllerFactory;
import org.apache.hadoop.yarn.logaggregation.filecontroller.tfile.LogAggregationTFileController;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.api.ContainerType;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.BaseContainerManagerTest;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.TestContainerManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task.FileDeletionTask;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.TestNonAggregatingLogHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerTokenUpdatedEvent;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SampleContainerLogAggregationPolicy.DEFAULT_SAMPLE_MIN_THRESHOLD;


public class TestLogAggregationService extends BaseContainerManagerTest {
    private Map<ApplicationAccessType, String> acls = createAppAcls();

    static {
        BaseContainerManagerTest.LOG = LoggerFactory.getLogger(TestLogAggregationService.class);
    }

    private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private File remoteRootLogDir = new File("target", ((this.getClass().getName()) + "-remoteLogDir"));

    public TestLogAggregationService() throws UnsupportedFileSystemException {
        super();
        this.remoteRootLogDir.mkdir();
    }

    DrainDispatcher dispatcher;

    EventHandler<Event> appEventHandler;

    private NodeId nodeId = NodeId.newInstance("0.0.0.0", 5555);

    @Test
    public void testLocalFileDeletionAfterUpload() throws Exception {
        this.delSrvc = new DeletionService(createContainerExecutor());
        delSrvc = Mockito.spy(delSrvc);
        this.delSrvc.init(conf);
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler));
        verifyLocalFileDeletion(logAggregationService);
    }

    @Test
    public void testLocalFileDeletionOnDiskFull() throws Exception {
        this.delSrvc = new DeletionService(createContainerExecutor());
        delSrvc = Mockito.spy(delSrvc);
        this.delSrvc.init(conf);
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        List<String> logDirs = super.dirsHandler.getLogDirs();
        LocalDirsHandlerService dirsHandler = Mockito.spy(super.dirsHandler);
        // Simulate disk being full by returning no good log dirs but having a
        // directory in full log dirs.
        Mockito.when(dirsHandler.getLogDirs()).thenReturn(new ArrayList<String>());
        Mockito.when(dirsHandler.getLogDirsForRead()).thenReturn(logDirs);
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, this.delSrvc, dirsHandler));
        verifyLocalFileDeletion(logAggregationService);
    }

    /* Test to verify fix for YARN-3793 */
    @Test
    public void testNoLogsUploadedOnAppFinish() throws Exception {
        this.delSrvc = new DeletionService(createContainerExecutor());
        delSrvc = Mockito.spy(delSrvc);
        this.delSrvc.init(conf);
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId app = BuilderUtils.newApplicationId(1234, 1);
        File appLogDir = new File(BaseContainerManagerTest.localLogDir, app.toString());
        appLogDir.mkdir();
        LogAggregationContext context = LogAggregationContext.newInstance("HOST*", "sys*");
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(app, this.user, null, this.acls, context));
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(app, 1);
        ContainerId cont = ContainerId.newContainerId(appAttemptId, 1);
        writeContainerLogs(appLogDir, cont, new String[]{ "stdout", "stderr", "syslog" });
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(cont, ContainerType.APPLICATION_MASTER, 0));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(app));
        logAggregationService.stop();
        delSrvc.stop();
        // Aggregated logs should not be deleted if not uploaded.
        FileDeletionTask deletionTask = new FileDeletionTask(delSrvc, user, null, null);
        Mockito.verify(delSrvc, Mockito.times(0)).delete(deletionTask);
    }

    @Test
    public void testNoContainerOnNode() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        // AppLogDir should be created
        File app1LogDir = new File(BaseContainerManagerTest.localLogDir, application1.toString());
        app1LogDir.mkdir();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application1));
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        LogAggregationFileController format1 = logAggregationService.getLogAggregationFileController(conf);
        Assert.assertFalse(new File(format1.getRemoteNodeLogFileForApp(application1, this.user, this.nodeId).toUri().getPath()).exists());
        dispatcher.await();
        ApplicationEvent[] expectedEvents = new ApplicationEvent[]{ new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, true, "getType", "getApplicationID");
        logAggregationService.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleAppsLogAggregation() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        String[] fileNames = new String[]{ "stdout", "stderr", "syslog" };
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        // AppLogDir should be created
        File app1LogDir = new File(BaseContainerManagerTest.localLogDir, application1.toString());
        app1LogDir.mkdir();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls));
        ApplicationAttemptId appAttemptId1 = BuilderUtils.newApplicationAttemptId(application1, 1);
        ContainerId container11 = ContainerId.newContainerId(appAttemptId1, 1);
        // Simulate log-file creation
        writeContainerLogs(app1LogDir, container11, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container11, ContainerType.APPLICATION_MASTER, 0));
        ApplicationId application2 = BuilderUtils.newApplicationId(1234, 2);
        ApplicationAttemptId appAttemptId2 = BuilderUtils.newApplicationAttemptId(application2, 1);
        File app2LogDir = new File(BaseContainerManagerTest.localLogDir, application2.toString());
        app2LogDir.mkdir();
        LogAggregationContext contextWithAMOnly = Records.newRecord(LogAggregationContext.class);
        contextWithAMOnly.setLogAggregationPolicyClassName(AMOnlyLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application2, this.user, null, this.acls, contextWithAMOnly));
        ContainerId container21 = ContainerId.newContainerId(appAttemptId2, 1);
        writeContainerLogs(app2LogDir, container21, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container21, ContainerType.APPLICATION_MASTER, 0));
        ContainerId container12 = ContainerId.newContainerId(appAttemptId1, 2);
        writeContainerLogs(app1LogDir, container12, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container12, ContainerType.TASK, 0));
        ApplicationId application3 = BuilderUtils.newApplicationId(1234, 3);
        ApplicationAttemptId appAttemptId3 = BuilderUtils.newApplicationAttemptId(application3, 1);
        File app3LogDir = new File(BaseContainerManagerTest.localLogDir, application3.toString());
        app3LogDir.mkdir();
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application3, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        ApplicationEvent[] expectedInitEvents = new ApplicationEvent[]{ new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application2, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application3, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedInitEvents, false, "getType", "getApplicationID");
        Mockito.reset(appEventHandler);
        ContainerId container31 = ContainerId.newContainerId(appAttemptId3, 1);
        writeContainerLogs(app3LogDir, container31, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container31, ContainerType.APPLICATION_MASTER, 0));
        ContainerId container32 = ContainerId.newContainerId(appAttemptId3, 2);
        writeContainerLogs(app3LogDir, container32, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container32, ContainerType.TASK, 1));// Failed

        ContainerId container22 = ContainerId.newContainerId(appAttemptId2, 2);
        writeContainerLogs(app2LogDir, container22, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container22, ContainerType.TASK, 0));
        ContainerId container33 = ContainerId.newContainerId(appAttemptId3, 3);
        writeContainerLogs(app3LogDir, container33, fileNames);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container33, ContainerType.TASK, 0));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application2));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application3));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application1));
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        verifyContainerLogs(logAggregationService, application1, new ContainerId[]{ container11, container12 }, fileNames, 3, false);
        verifyContainerLogs(logAggregationService, application2, new ContainerId[]{ container21 }, fileNames, 3, false);
        verifyContainerLogs(logAggregationService, application3, new ContainerId[]{ container31, container32 }, fileNames, 3, false);
        dispatcher.await();
        ApplicationEvent[] expectedFinishedEvents = new ApplicationEvent[]{ new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED), new ApplicationEvent(application2, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED), new ApplicationEvent(application3, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedFinishedEvents, false, "getType", "getApplicationID");
    }

    @Test
    public void testVerifyAndCreateRemoteDirsFailure() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        LogAggregationFileControllerFactory factory = new LogAggregationFileControllerFactory(conf);
        LogAggregationFileController logAggregationFileFormat = factory.getFileControllerForWrite();
        LogAggregationFileController spyLogAggregationFileFormat = Mockito.spy(logAggregationFileFormat);
        YarnRuntimeException e = new YarnRuntimeException("KABOOM!");
        Mockito.doThrow(e).doNothing().when(spyLogAggregationFileFormat).verifyAndCreateRemoteLogDir();
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler) {
            @Override
            public LogAggregationFileController getLogAggregationFileController(Configuration conf) {
                return spyLogAggregationFileFormat;
            }
        });
        logAggregationService.init(this.conf);
        logAggregationService.start();
        // Now try to start an application
        ApplicationId appId = BuilderUtils.newApplicationId(System.currentTimeMillis(), ((int) ((Math.random()) * 1000)));
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        // Verify that it failed
        ApplicationEvent[] expectedEvents = new ApplicationEvent[]{ new ApplicationEvent(appId, ApplicationEventType.APPLICATION_LOG_HANDLING_FAILED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, false, "getType", "getApplicationID", "getDiagnostic");
        Mockito.reset(logAggregationService);
        // Now try to start another one
        ApplicationId appId2 = BuilderUtils.newApplicationId(System.currentTimeMillis(), ((int) ((Math.random()) * 1000)));
        File appLogDir = new File(BaseContainerManagerTest.localLogDir, appId2.toString());
        appLogDir.mkdir();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId2, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        // Verify that it worked
        expectedEvents = new ApplicationEvent[]{ // original failure
        new ApplicationEvent(appId, ApplicationEventType.APPLICATION_LOG_HANDLING_FAILED), // success
        new ApplicationEvent(appId2, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, false, "getType", "getApplicationID", "getDiagnostic");
        logAggregationService.stop();
    }

    @Test
    public void testVerifyAndCreateRemoteDirNonExistence() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        File aNewFile = new File(String.valueOf(("tmp" + (System.currentTimeMillis()))));
        this.conf.set(NM_REMOTE_APP_LOG_DIR, aNewFile.getAbsolutePath());
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler));
        logAggregationService.init(this.conf);
        logAggregationService.start();
        boolean existsBefore = aNewFile.exists();
        Assert.assertTrue("The new file already exists!", (!existsBefore));
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        boolean existsAfter = aNewFile.exists();
        Assert.assertTrue("The new aggregate file is not successfully created", existsAfter);
        aNewFile.delete();// housekeeping

        logAggregationService.stop();
    }

    @Test
    public void testRemoteRootLogDirIsCreatedWithCorrectGroupOwner() throws IOException {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        Path aNewFile = new Path(String.valueOf(("tmp" + (System.currentTimeMillis()))));
        this.conf.set(NM_REMOTE_APP_LOG_DIR, aNewFile.getName());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        String targetGroup = UserGroupInformation.getLoginUser().getPrimaryGroupName();
        FileSystem fs = FileSystem.get(this.conf);
        FileStatus fileStatus = fs.getFileStatus(aNewFile);
        Assert.assertEquals("The new aggregate file is not successfully created", fileStatus.getGroup(), targetGroup);
        fs.delete(aNewFile, true);
        logAggregationService.stop();
    }

    @Test
    public void testAppLogDirCreation() throws Exception {
        final String logSuffix = "logs";
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR_SUFFIX, logSuffix);
        InlineDispatcher dispatcher = new InlineDispatcher();
        dispatcher.init(this.conf);
        dispatcher.start();
        FileSystem fs = FileSystem.get(this.conf);
        final FileSystem spyFs = Mockito.spy(FileSystem.get(this.conf));
        final LogAggregationTFileController spyFileFormat = new LogAggregationTFileController() {
            @Override
            public FileSystem getFileSystem(Configuration conf) throws IOException {
                return spyFs;
            }
        };
        spyFileFormat.initialize(conf, "TFile");
        LogAggregationService aggSvc = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler) {
            @Override
            public LogAggregationFileController getLogAggregationFileController(Configuration conf) {
                return spyFileFormat;
            }
        };
        aggSvc.init(this.conf);
        aggSvc.start();
        // start an application and verify user, suffix, and app dirs created
        ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
        Path userDir = fs.makeQualified(new Path(remoteRootLogDir.getAbsolutePath(), this.user));
        Path suffixDir = new Path(userDir, logSuffix);
        Path appDir = new Path(suffixDir, appId.toString());
        LogAggregationContext contextWithAllContainers = Records.newRecord(LogAggregationContext.class);
        contextWithAllContainers.setLogAggregationPolicyClassName(AllContainerLogAggregationPolicy.class.getName());
        aggSvc.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAllContainers));
        Mockito.verify(spyFs).mkdirs(ArgumentMatchers.eq(userDir), ArgumentMatchers.isA(FsPermission.class));
        Mockito.verify(spyFs).mkdirs(ArgumentMatchers.eq(suffixDir), ArgumentMatchers.isA(FsPermission.class));
        Mockito.verify(spyFs).mkdirs(ArgumentMatchers.eq(appDir), ArgumentMatchers.isA(FsPermission.class));
        // start another application and verify only app dir created
        ApplicationId appId2 = BuilderUtils.newApplicationId(1, 2);
        Path appDir2 = new Path(suffixDir, appId2.toString());
        aggSvc.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId2, this.user, null, this.acls, contextWithAllContainers));
        Mockito.verify(spyFs).mkdirs(ArgumentMatchers.eq(appDir2), ArgumentMatchers.isA(FsPermission.class));
        // start another application with the app dir already created and verify
        // we do not try to create it again
        ApplicationId appId3 = BuilderUtils.newApplicationId(1, 3);
        Path appDir3 = new Path(suffixDir, appId3.toString());
        new File(appDir3.toUri().getPath()).mkdir();
        aggSvc.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId3, this.user, null, this.acls, contextWithAllContainers));
        Mockito.verify(spyFs, Mockito.never()).mkdirs(ArgumentMatchers.eq(appDir3), ArgumentMatchers.isA(FsPermission.class));
        aggSvc.stop();
        aggSvc.close();
        dispatcher.stop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLogAggregationInitAppFailsWithoutKillingNM() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler));
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId appId = BuilderUtils.newApplicationId(System.currentTimeMillis(), ((int) ((Math.random()) * 1000)));
        Mockito.doThrow(new YarnRuntimeException("KABOOM!")).when(logAggregationService).initAppAggregator(ArgumentMatchers.eq(appId), ArgumentMatchers.eq(user), ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any(LogAggregationContext.class), ArgumentMatchers.anyLong());
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        ApplicationEvent[] expectedEvents = new ApplicationEvent[]{ new ApplicationEvent(appId, ApplicationEventType.APPLICATION_LOG_HANDLING_FAILED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, false, "getType", "getApplicationID", "getDiagnostic");
        // no filesystems instantiated yet
        Mockito.verify(logAggregationService, Mockito.never()).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        // verify trying to collect logs for containers/apps we don't know about
        // doesn't blow up and tear down the NM
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(BuilderUtils.newContainerId(4, 1, 1, 1), ContainerType.APPLICATION_MASTER, 0));
        dispatcher.await();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(BuilderUtils.newApplicationId(1, 5)));
        dispatcher.await();
    }

    @Test
    public void testLogAggregationCreateDirsFailsWithoutKillingNM() throws Exception {
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        DeletionService spyDelSrvc = Mockito.spy(this.delSrvc);
        LogAggregationFileControllerFactory factory = new LogAggregationFileControllerFactory(conf);
        LogAggregationFileController logAggregationFileFormat = factory.getFileControllerForWrite();
        LogAggregationFileController spyLogAggregationFileFormat = Mockito.spy(logAggregationFileFormat);
        Exception e = new YarnRuntimeException(new SecretManager.InvalidToken("KABOOM!"));
        Mockito.doThrow(e).when(spyLogAggregationFileFormat).createAppDir(ArgumentMatchers.any(String.class), ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(UserGroupInformation.class));
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, spyDelSrvc, super.dirsHandler) {
            @Override
            public LogAggregationFileController getLogAggregationFileController(Configuration conf) {
                return spyLogAggregationFileFormat;
            }
        });
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId appId = BuilderUtils.newApplicationId(System.currentTimeMillis(), ((int) ((Math.random()) * 1000)));
        File appLogDir = new File(BaseContainerManagerTest.localLogDir, appId.toString());
        appLogDir.mkdir();
        LogAggregationContext contextWithAMAndFailed = Records.newRecord(LogAggregationContext.class);
        contextWithAMAndFailed.setLogAggregationPolicyClassName(AMOrFailedContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, this.user, null, this.acls, contextWithAMAndFailed));
        dispatcher.await();
        ApplicationEvent[] expectedEvents = new ApplicationEvent[]{ new ApplicationEvent(appId, ApplicationEventType.APPLICATION_LOG_HANDLING_FAILED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, false, "getType", "getApplicationID", "getDiagnostic");
        Assert.assertEquals(logAggregationService.getInvalidTokenApps().size(), 1);
        // verify trying to collect logs for containers/apps we don't know about
        // doesn't blow up and tear down the NM
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(BuilderUtils.newContainerId(4, 1, 1, 1), ContainerType.APPLICATION_MASTER, 0));
        dispatcher.await();
        AppLogAggregator appAgg = logAggregationService.getAppLogAggregators().get(appId);
        Assert.assertFalse("Aggregation should be disabled", appAgg.isAggregationEnabled());
        // Enabled aggregation
        logAggregationService.handle(new LogHandlerTokenUpdatedEvent());
        dispatcher.await();
        appAgg = logAggregationService.getAppLogAggregators().get(appId);
        Assert.assertFalse("Aggregation should be enabled", appAgg.isAggregationEnabled());
        // Check disabled apps are cleared
        Assert.assertEquals(0, logAggregationService.getInvalidTokenApps().size());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(BuilderUtils.newApplicationId(1, 5)));
        dispatcher.await();
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        Mockito.verify(spyDelSrvc).delete(ArgumentMatchers.any(FileDeletionTask.class));
        Mockito.verify(logAggregationService).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
    }

    @Test
    public void testLogAggregationForRealContainerLaunch() throws IOException, InterruptedException, YarnException {
        this.containerManager.start();
        File scriptFile = new File(BaseContainerManagerTest.tmpDir, "scriptFile.sh");
        PrintWriter fileWriter = new PrintWriter(scriptFile);
        fileWriter.write(("\necho Hello World! Stdout! > " + (new File(BaseContainerManagerTest.localLogDir, "stdout"))));
        fileWriter.write(("\necho Hello World! Stderr! > " + (new File(BaseContainerManagerTest.localLogDir, "stderr"))));
        fileWriter.write(("\necho Hello World! Syslog! > " + (new File(BaseContainerManagerTest.localLogDir, "syslog"))));
        fileWriter.close();
        ContainerLaunchContext containerLaunchContext = TestLogAggregationService.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        // ////// Construct the Container-id
        ApplicationId appId = ApplicationId.newInstance(0, 0);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId cId = BuilderUtils.newContainerId(appAttemptId, 0);
        URL resource_alpha = URL.fromPath(BaseContainerManagerTest.localFS.makeQualified(new Path(scriptFile.getAbsolutePath())));
        LocalResource rsrc_alpha = TestLogAggregationService.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(scriptFile.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/bash");
        commands.add(scriptFile.getAbsolutePath());
        containerLaunchContext.setCommands(commands);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, TestContainerManager.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<StartContainerRequest>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        this.containerManager.startContainers(allRequests);
        BaseContainerManagerTest.waitForContainerState(this.containerManager, cId, COMPLETE);
        this.containerManager.handle(new org.apache.hadoop.yarn.server.nodemanager.CMgrCompletedAppsEvent(Arrays.asList(appId), Reason.ON_SHUTDOWN));
        this.containerManager.stop();
    }

    @Test(timeout = 30000)
    public void testFixedSizeThreadPool() throws Exception {
        // store configured thread pool size temporarily for restoration
        int initThreadPoolSize = conf.getInt(NM_LOG_AGGREGATION_THREAD_POOL_SIZE, DEFAULT_NM_LOG_AGGREGATION_THREAD_POOL_SIZE);
        int threadPoolSize = 3;
        conf.setInt(NM_LOG_AGGREGATION_THREAD_POOL_SIZE, threadPoolSize);
        DeletionService delSrvc = Mockito.mock(DeletionService.class);
        LocalDirsHandlerService dirSvc = Mockito.mock(LocalDirsHandlerService.class);
        Mockito.when(dirSvc.getLogDirs()).thenThrow(new RuntimeException());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, delSrvc, dirSvc);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ExecutorService executorService = logAggregationService.threadPool;
        // used to block threads in the thread pool because main thread always
        // acquires the write lock first.
        final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        final Lock rLock = rwLock.readLock();
        final Lock wLock = rwLock.writeLock();
        wLock.lock();
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // threads in the thread pool running this will be blocked
                        rLock.tryLock(35000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        rLock.unlock();
                    }
                }
            };
            // submit $(threadPoolSize + 1) runnables to the thread pool. If the thread
            // pool size is set properly, only $(threadPoolSize) threads will be
            // created in the thread pool, each of which is blocked on the read lock.
            for (int i = 0; i < (threadPoolSize + 1); i++) {
                executorService.submit(runnable);
            }
            // count the number of current running LogAggregationService threads
            int runningThread = ((ThreadPoolExecutor) (executorService)).getActiveCount();
            Assert.assertEquals(threadPoolSize, runningThread);
        } finally {
            wLock.unlock();
        }
        logAggregationService.stop();
        logAggregationService.close();
        // restore the original configurations to avoid side effects
        conf.setInt(NM_LOG_AGGREGATION_THREAD_POOL_SIZE, initThreadPoolSize);
    }

    @Test
    public void testInvalidThreadPoolSizeNaN() throws IOException {
        testInvalidThreadPoolSizeValue("NaN");
    }

    @Test
    public void testInvalidThreadPoolSizeNegative() throws IOException {
        testInvalidThreadPoolSizeValue("-100");
    }

    @Test
    public void testInvalidThreadPoolSizeXLarge() throws IOException {
        testInvalidThreadPoolSizeValue("11111111111");
    }

    @Test(timeout = 20000)
    public void testStopAfterError() throws Exception {
        DeletionService delSrvc = Mockito.mock(DeletionService.class);
        // get the AppLogAggregationImpl thread to crash
        LocalDirsHandlerService mockedDirSvc = Mockito.mock(LocalDirsHandlerService.class);
        Mockito.when(mockedDirSvc.getLogDirs()).thenThrow(new RuntimeException());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, delSrvc, mockedDirSvc);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        LogAggregationContext contextWithAllContainers = Records.newRecord(LogAggregationContext.class);
        contextWithAllContainers.setLogAggregationPolicyClassName(AllContainerLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls, contextWithAllContainers));
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        logAggregationService.close();
    }

    @Test
    public void testLogAggregatorCleanup() throws Exception {
        DeletionService delSrvc = Mockito.mock(DeletionService.class);
        // get the AppLogAggregationImpl thread to crash
        LocalDirsHandlerService mockedDirSvc = Mockito.mock(LocalDirsHandlerService.class);
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, delSrvc, mockedDirSvc);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application1));
        dispatcher.await();
        int timeToWait = 20 * 1000;
        while ((timeToWait > 0) && ((logAggregationService.getNumAggregators()) > 0)) {
            Thread.sleep(100);
            timeToWait -= 100;
        } 
        Assert.assertEquals("Log aggregator failed to cleanup!", 0, logAggregationService.getNumAggregators());
        logAggregationService.stop();
        logAggregationService.close();
    }

    /* Test to make sure we handle cases where the directories we get back from
    the LocalDirsHandler may have issues including the log dir not being
    present as well as other issues. The test uses helper functions from
    TestNonAggregatingLogHandler.
     */
    @Test
    public void testFailedDirsLocalFileDeletionAfterUpload() throws Exception {
        // setup conf and services
        DeletionService mockDelService = Mockito.mock(DeletionService.class);
        File[] localLogDirs = TestNonAggregatingLogHandler.getLocalLogDirFiles(this.getClass().getName(), 7);
        final List<String> localLogDirPaths = new ArrayList<String>(localLogDirs.length);
        for (int i = 0; i < (localLogDirs.length); i++) {
            localLogDirPaths.add(localLogDirs[i].getAbsolutePath());
        }
        String localLogDirsString = StringUtils.join(localLogDirPaths, ",");
        this.conf.set(NM_LOG_DIRS, localLogDirsString);
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        this.conf.setLong(NM_DISK_HEALTH_CHECK_INTERVAL_MS, 500);
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(application1, 1);
        this.dirsHandler = new LocalDirsHandlerService();
        LocalDirsHandlerService mockDirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        LogAggregationService logAggregationService = Mockito.spy(new LogAggregationService(dispatcher, this.context, mockDelService, mockDirsHandler));
        AbstractFileSystem spylfs = Mockito.spy(FileContext.getLocalFSFileContext().getDefaultFileSystem());
        FileContext lfs = FileContext.getFileContext(spylfs, conf);
        Mockito.doReturn(lfs).when(logAggregationService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        logAggregationService.init(this.conf);
        logAggregationService.start();
        TestNonAggregatingLogHandler.runMockedFailedDirs(logAggregationService, application1, user, mockDelService, mockDirsHandler, conf, spylfs, lfs, localLogDirs);
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        Mockito.verify(logAggregationService).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        ApplicationEvent[] expectedEvents = new ApplicationEvent[]{ new ApplicationEvent(appAttemptId.getApplicationId(), ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(appAttemptId.getApplicationId(), ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedEvents, true, "getType", "getApplicationID");
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testLogAggregationServiceWithPatterns() throws Exception {
        LogAggregationContext logAggregationContextWithIncludePatterns = Records.newRecord(LogAggregationContext.class);
        String includePattern = "stdout|syslog";
        logAggregationContextWithIncludePatterns.setIncludePattern(includePattern);
        LogAggregationContext LogAggregationContextWithExcludePatterns = Records.newRecord(LogAggregationContext.class);
        String excludePattern = "stdout|syslog";
        LogAggregationContextWithExcludePatterns.setExcludePattern(excludePattern);
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.getAbsolutePath());
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        ApplicationId application2 = BuilderUtils.newApplicationId(1234, 2);
        ApplicationId application3 = BuilderUtils.newApplicationId(1234, 3);
        ApplicationId application4 = BuilderUtils.newApplicationId(1234, 4);
        Application mockApp = Mockito.mock(Application.class);
        Mockito.when(mockApp.getContainers()).thenReturn(new HashMap<ContainerId, org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container>());
        this.context.getApplications().put(application1, mockApp);
        this.context.getApplications().put(application2, mockApp);
        this.context.getApplications().put(application3, mockApp);
        this.context.getApplications().put(application4, mockApp);
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        // LogContext for application1 has includePatten which includes
        // stdout and syslog.
        // After logAggregation is finished, we expect the logs for application1
        // has only logs from stdout and syslog
        // AppLogDir should be created
        File appLogDir1 = new File(BaseContainerManagerTest.localLogDir, application1.toString());
        appLogDir1.mkdir();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls, logAggregationContextWithIncludePatterns));
        ApplicationAttemptId appAttemptId1 = BuilderUtils.newApplicationAttemptId(application1, 1);
        ContainerId container1 = ContainerId.newContainerId(appAttemptId1, 1);
        // Simulate log-file creation
        writeContainerLogs(appLogDir1, container1, new String[]{ "stdout", "stderr", "syslog" });
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container1, ContainerType.APPLICATION_MASTER, 0));
        // LogContext for application2 has excludePatten which includes
        // stdout and syslog.
        // After logAggregation is finished, we expect the logs for application2
        // has only logs from stderr
        ApplicationAttemptId appAttemptId2 = BuilderUtils.newApplicationAttemptId(application2, 1);
        File app2LogDir = new File(BaseContainerManagerTest.localLogDir, application2.toString());
        app2LogDir.mkdir();
        LogAggregationContextWithExcludePatterns.setLogAggregationPolicyClassName(AMOnlyLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application2, this.user, null, this.acls, LogAggregationContextWithExcludePatterns));
        ContainerId container2 = ContainerId.newContainerId(appAttemptId2, 1);
        writeContainerLogs(app2LogDir, container2, new String[]{ "stdout", "stderr", "syslog" });
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container2, ContainerType.APPLICATION_MASTER, 0));
        // LogContext for application3 has includePattern which is *.log and
        // excludePatten which includes std.log and sys.log.
        // After logAggregation is finished, we expect the logs for application3
        // has all logs whose suffix is .log but excluding sys.log and std.log
        LogAggregationContext context1 = Records.newRecord(LogAggregationContext.class);
        context1.setIncludePattern(".*.log");
        context1.setExcludePattern("sys.log|std.log");
        ApplicationAttemptId appAttemptId3 = BuilderUtils.newApplicationAttemptId(application3, 1);
        File app3LogDir = new File(BaseContainerManagerTest.localLogDir, application3.toString());
        app3LogDir.mkdir();
        context1.setLogAggregationPolicyClassName(AMOnlyLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application3, this.user, null, this.acls, context1));
        ContainerId container3 = ContainerId.newContainerId(appAttemptId3, 1);
        writeContainerLogs(app3LogDir, container3, new String[]{ "stdout", "sys.log", "std.log", "out.log", "err.log", "log" });
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container3, ContainerType.APPLICATION_MASTER, 0));
        // LogContext for application4 has includePattern
        // which includes std.log and sys.log and
        // excludePatten which includes std.log.
        // After logAggregation is finished, we expect the logs for application4
        // only has sys.log
        LogAggregationContext context2 = Records.newRecord(LogAggregationContext.class);
        context2.setIncludePattern("sys.log|std.log");
        context2.setExcludePattern("std.log");
        ApplicationAttemptId appAttemptId4 = BuilderUtils.newApplicationAttemptId(application4, 1);
        File app4LogDir = new File(BaseContainerManagerTest.localLogDir, application4.toString());
        app4LogDir.mkdir();
        context2.setLogAggregationPolicyClassName(AMOnlyLogAggregationPolicy.class.getName());
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application4, this.user, null, this.acls, context2));
        ContainerId container4 = ContainerId.newContainerId(appAttemptId4, 1);
        writeContainerLogs(app4LogDir, container4, new String[]{ "stdout", "sys.log", "std.log", "out.log", "err.log", "log" });
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container4, ContainerType.APPLICATION_MASTER, 0));
        dispatcher.await();
        ApplicationEvent[] expectedInitEvents = new ApplicationEvent[]{ new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application2, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application3, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED), new ApplicationEvent(application4, ApplicationEventType.APPLICATION_LOG_HANDLING_INITED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedInitEvents, false, "getType", "getApplicationID");
        Mockito.reset(appEventHandler);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application1));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application2));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application3));
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application4));
        logAggregationService.stop();
        Assert.assertEquals(0, logAggregationService.getNumAggregators());
        String[] logFiles = new String[]{ "stdout", "syslog" };
        verifyContainerLogs(logAggregationService, application1, new ContainerId[]{ container1 }, logFiles, 2, false);
        logFiles = new String[]{ "stderr" };
        verifyContainerLogs(logAggregationService, application2, new ContainerId[]{ container2 }, logFiles, 1, false);
        logFiles = new String[]{ "out.log", "err.log" };
        verifyContainerLogs(logAggregationService, application3, new ContainerId[]{ container3 }, logFiles, 2, false);
        logFiles = new String[]{ "sys.log" };
        verifyContainerLogs(logAggregationService, application4, new ContainerId[]{ container4 }, logFiles, 1, false);
        dispatcher.await();
        ApplicationEvent[] expectedFinishedEvents = new ApplicationEvent[]{ new ApplicationEvent(application1, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED), new ApplicationEvent(application2, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED), new ApplicationEvent(application3, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED), new ApplicationEvent(application4, ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED) };
        TestLogAggregationService.checkEvents(appEventHandler, expectedFinishedEvents, false, "getType", "getApplicationID");
    }

    @SuppressWarnings("resource")
    @Test(timeout = 50000)
    public void testLogAggregationServiceWithPatternsAndIntervals() throws Exception {
        LogAggregationContext logAggregationContext = Records.newRecord(LogAggregationContext.class);
        // set IncludePattern and RolledLogsIncludePattern.
        // When the app is running, we only aggregate the log with
        // the name stdout. After the app finishes, we only aggregate
        // the log with the name std_final.
        logAggregationContext.setRolledLogsIncludePattern("stdout");
        logAggregationContext.setIncludePattern("std_final");
        this.conf.set(NM_LOG_DIRS, BaseContainerManagerTest.localLogDir.getAbsolutePath());
        // configure YarnConfiguration.NM_REMOTE_APP_LOG_DIR to
        // have fully qualified path
        this.conf.set(NM_REMOTE_APP_LOG_DIR, this.remoteRootLogDir.toURI().toString());
        this.conf.setLong(NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS, 3600);
        this.conf.setLong(DEBUG_NM_DELETE_DELAY_SEC, 3600);
        ApplicationId application = BuilderUtils.newApplicationId(System.currentTimeMillis(), 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(application, 1);
        ContainerId container = createContainer(appAttemptId, 1, APPLICATION_MASTER);
        ConcurrentMap<ApplicationId, Application> maps = this.context.getApplications();
        Application app = Mockito.mock(Application.class);
        maps.put(application, app);
        Mockito.when(app.getContainers()).thenReturn(this.context.getContainers());
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        // AppLogDir should be created
        File appLogDir = new File(BaseContainerManagerTest.localLogDir, ConverterUtils.toString(application));
        appLogDir.mkdir();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application, this.user, null, this.acls, logAggregationContext));
        // Simulate log-file creation
        // create std_final in log directory which will not be aggregated
        // until the app finishes.
        String[] logFilesWithFinalLog = new String[]{ "stdout", "std_final" };
        writeContainerLogs(appLogDir, container, logFilesWithFinalLog);
        // Do log aggregation
        AppLogAggregatorImpl aggregator = ((AppLogAggregatorImpl) (logAggregationService.getAppLogAggregators().get(application)));
        aggregator.doLogAggregationOutOfBand();
        Assert.assertTrue(waitAndCheckLogNum(logAggregationService, application, 50, 1, false, null));
        String[] logFiles = new String[]{ "stdout" };
        verifyContainerLogs(logAggregationService, application, new ContainerId[]{ container }, logFiles, 1, true);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container, ContainerType.APPLICATION_MASTER, 0));
        dispatcher.await();
        // Do the log aggregation after ContainerFinishedEvent but before
        // AppFinishedEvent. The std_final is expected to be aggregated this time
        // even if the app is running but the container finishes.
        aggregator.doLogAggregationOutOfBand();
        Assert.assertTrue(waitAndCheckLogNum(logAggregationService, application, 50, 2, false, null));
        // This container finishes.
        // The log "std_final" should be aggregated this time.
        String[] logFinalLog = new String[]{ "std_final" };
        verifyContainerLogs(logAggregationService, application, new ContainerId[]{ container }, logFinalLog, 1, true);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application));
        logAggregationService.stop();
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testNoneContainerPolicy() throws Exception {
        ApplicationId appId = createApplication();
        // LogContext specifies policy to not aggregate any container logs
        LogAggregationService logAggregationService = createLogAggregationService(appId, NoneContainerLogAggregationPolicy.class, null);
        String[] logFiles = new String[]{ "stdout" };
        ContainerId container1 = finishContainer(appId, logAggregationService, APPLICATION_MASTER, 1, 0, logFiles);
        finishApplication(appId, logAggregationService);
        verifyContainerLogs(logAggregationService, appId, new ContainerId[]{ container1 }, logFiles, 0, false);
        verifyLogAggFinishEvent(appId);
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testFailedContainerPolicy() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, FailedContainerLogAggregationPolicy.class, null);
        String[] logFiles = new String[]{ "stdout" };
        ContainerId container1 = finishContainer(appId, logAggregationService, APPLICATION_MASTER, 1, 1, logFiles);
        finishContainer(appId, logAggregationService, TASK, 2, 0, logFiles);
        finishContainer(appId, logAggregationService, TASK, 3, FORCE_KILLED.getExitCode(), logFiles);
        finishApplication(appId, logAggregationService);
        verifyContainerLogs(logAggregationService, appId, new ContainerId[]{ container1 }, logFiles, 1, false);
        verifyLogAggFinishEvent(appId);
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testAMOrFailedContainerPolicy() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, AMOrFailedContainerLogAggregationPolicy.class, null);
        String[] logFiles = new String[]{ "stdout" };
        ContainerId container1 = finishContainer(appId, logAggregationService, APPLICATION_MASTER, 1, 0, logFiles);
        ContainerId container2 = finishContainer(appId, logAggregationService, TASK, 2, 1, logFiles);
        finishContainer(appId, logAggregationService, TASK, 3, FORCE_KILLED.getExitCode(), logFiles);
        finishApplication(appId, logAggregationService);
        verifyContainerLogs(logAggregationService, appId, new ContainerId[]{ container1, container2 }, logFiles, 1, false);
        verifyLogAggFinishEvent(appId);
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testFailedOrKilledContainerPolicy() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, FailedOrKilledContainerLogAggregationPolicy.class, null);
        String[] logFiles = new String[]{ "stdout" };
        finishContainer(appId, logAggregationService, APPLICATION_MASTER, 1, 0, logFiles);
        ContainerId container2 = finishContainer(appId, logAggregationService, TASK, 2, 1, logFiles);
        ContainerId container3 = finishContainer(appId, logAggregationService, TASK, 3, FORCE_KILLED.getExitCode(), logFiles);
        finishApplication(appId, logAggregationService);
        verifyContainerLogs(logAggregationService, appId, new ContainerId[]{ container2, container3 }, logFiles, 1, false);
        verifyLogAggFinishEvent(appId);
    }

    @Test(timeout = 50000)
    public void testLogAggregationAbsentContainer() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, FailedOrKilledContainerLogAggregationPolicy.class, null);
        ApplicationAttemptId appAttemptId1 = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId1, 2L);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(containerId, ContainerType.APPLICATION_MASTER, 100));
    }

    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testAMOnlyContainerPolicy() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, AMOnlyLogAggregationPolicy.class, null);
        String[] logFiles = new String[]{ "stdout" };
        ContainerId container1 = finishContainer(appId, logAggregationService, APPLICATION_MASTER, 1, 0, logFiles);
        finishContainer(appId, logAggregationService, TASK, 2, 1, logFiles);
        finishContainer(appId, logAggregationService, TASK, 3, 0, logFiles);
        finishApplication(appId, logAggregationService);
        verifyContainerLogs(logAggregationService, appId, new ContainerId[]{ container1 }, logFiles, 1, false);
        verifyLogAggFinishEvent(appId);
    }

    // Test sample container policy with an app that has
    // the same number of successful containers as
    // SampleContainerLogAggregationPolicy.DEFAULT_SAMPLE_MIN_THRESHOLD.
    // and verify all those containers' logs are aggregated.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWithSmallApp() throws Exception {
        setupAndTestSampleContainerPolicy(DEFAULT_SAMPLE_MIN_THRESHOLD, DEFAULT_SAMPLE_RATE, DEFAULT_SAMPLE_MIN_THRESHOLD, false);
    }

    // Test sample container policy with an app that has
    // more successful containers than
    // SampleContainerLogAggregationPolicy.DEFAULT_SAMPLE_MIN_THRESHOLD.
    // and verify some of those containers' logs are aggregated.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWithLargeApp() throws Exception {
        setupAndTestSampleContainerPolicy(((DEFAULT_SAMPLE_MIN_THRESHOLD) * 10), DEFAULT_SAMPLE_RATE, DEFAULT_SAMPLE_MIN_THRESHOLD, false);
    }

    // Test sample container policy with zero sample rate.
    // and verify there is no sampling beyond the MIN_THRESHOLD containers.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWithZeroSampleRate() throws Exception {
        setupAndTestSampleContainerPolicy(((DEFAULT_SAMPLE_MIN_THRESHOLD) * 10), 0, DEFAULT_SAMPLE_MIN_THRESHOLD, false);
    }

    // Test sample container policy with 100 percent sample rate.
    // and verify all containers' logs are aggregated.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWith100PercentSampleRate() throws Exception {
        setupAndTestSampleContainerPolicy(((DEFAULT_SAMPLE_MIN_THRESHOLD) * 10), 1.0F, DEFAULT_SAMPLE_MIN_THRESHOLD, false);
    }

    // Test sample container policy with zero min threshold.
    // and verify some containers' logs are aggregated.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWithZeroMinThreshold() throws Exception {
        setupAndTestSampleContainerPolicy(((DEFAULT_SAMPLE_MIN_THRESHOLD) * 10), DEFAULT_SAMPLE_RATE, 0, false);
    }

    // Test sample container policy with customized settings.
    // and verify some containers' logs are aggregated.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testSampleContainerPolicyWithCustomizedSettings() throws Exception {
        setupAndTestSampleContainerPolicy(500, 0.5F, 50, false);
    }

    // Test cluster-wide sample container policy.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testClusterSampleContainerPolicy() throws Exception {
        setupAndTestSampleContainerPolicy(500, 0.5F, 50, true);
    }

    // Test the default cluster-wide sample container policy.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testDefaultClusterSampleContainerPolicy() throws Exception {
        setupAndTestSampleContainerPolicy(((DEFAULT_SAMPLE_MIN_THRESHOLD) * 10), DEFAULT_SAMPLE_RATE, DEFAULT_SAMPLE_MIN_THRESHOLD, true);
    }

    // The application specifies invalid policy class
    // NM should fallback to the default policy which is to aggregate all
    // containers.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testInvalidPolicyClassName() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, "foo", null, true);
        verifyDefaultPolicy(appId, logAggregationService);
    }

    // The application specifies LogAggregationContext, but not policy class.
    // NM should fallback to the default policy which is to aggregate all
    // containers.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testNullPolicyClassName() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, null, null, true);
        verifyDefaultPolicy(appId, logAggregationService);
    }

    // The application doesn't specifies LogAggregationContext.
    // NM should fallback to the default policy which is to aggregate all
    // containers.
    @Test(timeout = 50000)
    @SuppressWarnings("unchecked")
    public void testDefaultPolicyWithoutLogAggregationContext() throws Exception {
        ApplicationId appId = createApplication();
        LogAggregationService logAggregationService = createLogAggregationService(appId, null, null, false);
        verifyDefaultPolicy(appId, logAggregationService);
    }

    @Test(timeout = 50000)
    public void testLogAggregationServiceWithInterval() throws Exception {
        testLogAggregationService(false);
    }

    @Test(timeout = 50000)
    public void testLogAggregationServiceWithRetention() throws Exception {
        testLogAggregationService(true);
    }

    @Test(timeout = 20000)
    public void testAddNewTokenSentFromRMForLogAggregation() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        ApplicationId application1 = BuilderUtils.newApplicationId(1234, 1);
        Application mockApp = Mockito.mock(Application.class);
        Mockito.when(mockApp.getContainers()).thenReturn(new HashMap<ContainerId, org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container>());
        this.context.getApplications().put(application1, mockApp);
        @SuppressWarnings("resource")
        LogAggregationService logAggregationService = new LogAggregationService(dispatcher, this.context, this.delSrvc, super.dirsHandler);
        logAggregationService.init(this.conf);
        logAggregationService.start();
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(application1, this.user, null, this.acls, Records.newRecord(LogAggregationContext.class)));
        // Inject new token for log-aggregation after app log-aggregator init
        Text userText1 = new Text("user1");
        RMDelegationTokenIdentifier dtId1 = new RMDelegationTokenIdentifier(userText1, new Text("renewer1"), userText1);
        final Token<RMDelegationTokenIdentifier> token1 = new Token<RMDelegationTokenIdentifier>(dtId1.getBytes(), "password1".getBytes(), dtId1.getKind(), new Text("service1"));
        Credentials credentials = new Credentials();
        credentials.addToken(userText1, token1);
        this.context.getSystemCredentialsForApps().put(application1, credentials);
        logAggregationService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(application1));
        final UserGroupInformation ugi = getUgi();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            public Boolean get() {
                boolean hasNewToken = false;
                for (Token<?> token : ugi.getCredentials().getAllTokens()) {
                    if (token.equals(token1)) {
                        hasNewToken = true;
                    }
                }
                return hasNewToken;
            }
        }, 1000, 20000);
        logAggregationService.stop();
    }

    @Test(timeout = 20000)
    public void testSkipUnnecessaryNNOperationsForShortJob() throws Exception {
        LogAggregationContext logAggregationContext = Records.newRecord(LogAggregationContext.class);
        logAggregationContext.setLogAggregationPolicyClassName(FailedOrKilledContainerLogAggregationPolicy.class.getName());
        verifySkipUnnecessaryNNOperations(logAggregationContext, 0, 2, 0);
    }

    @Test(timeout = 20000)
    public void testSkipUnnecessaryNNOperationsForService() throws Exception {
        this.conf.setLong(NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS, 3600);
        LogAggregationContext contextWithAMOnly = Records.newRecord(LogAggregationContext.class);
        contextWithAMOnly.setLogAggregationPolicyClassName(AMOnlyLogAggregationPolicy.class.getName());
        contextWithAMOnly.setRolledLogsIncludePattern("sys*");
        contextWithAMOnly.setRolledLogsExcludePattern("std_final");
        verifySkipUnnecessaryNNOperations(contextWithAMOnly, 1, 4, 1);
    }

    private static class LogFileStatusInLastCycle {
        private String logFilePathInLastCycle;

        private List<String> logFileTypesInLastCycle;

        public LogFileStatusInLastCycle(String logFilePathInLastCycle, List<String> logFileTypesInLastCycle) {
            this.logFilePathInLastCycle = logFilePathInLastCycle;
            this.logFileTypesInLastCycle = logFileTypesInLastCycle;
        }

        public String getLogFilePathInLastCycle() {
            return this.logFilePathInLastCycle;
        }

        public List<String> getLogFileTypesInLastCycle() {
            return this.logFileTypesInLastCycle;
        }
    }
}

