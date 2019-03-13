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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler;


import YarnConfiguration.DEFAULT_NM_LOG_RETAIN_SECONDS;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.NM_LOG_RETAIN_SECONDS;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.server.api.ContainerType;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMMemoryStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.VarargMatcher;


public class TestNonAggregatingLogHandler {
    DeletionService mockDelService;

    Configuration conf;

    DrainDispatcher dispatcher;

    private TestNonAggregatingLogHandler.ApplicationEventHandler appEventHandler;

    String user = "testuser";

    ApplicationId appId;

    ApplicationAttemptId appAttemptId;

    ContainerId container11;

    LocalDirsHandlerService dirsHandler;

    @Test
    public void testLogDeletion() throws IOException {
        File[] localLogDirs = TestNonAggregatingLogHandler.getLocalLogDirFiles(this.getClass().getName(), 2);
        String localLogDirsString = ((localLogDirs[0].getAbsolutePath()) + ",") + (localLogDirs[1].getAbsolutePath());
        conf.set(NM_LOG_DIRS, localLogDirsString);
        conf.setBoolean(LOG_AGGREGATION_ENABLED, false);
        conf.setLong(NM_LOG_RETAIN_SECONDS, 0L);
        dirsHandler.init(conf);
        NonAggregatingLogHandler rawLogHandler = new NonAggregatingLogHandler(dispatcher, mockDelService, dirsHandler, new NMNullStateStoreService());
        NonAggregatingLogHandler logHandler = Mockito.spy(rawLogHandler);
        AbstractFileSystem spylfs = Mockito.spy(FileContext.getLocalFSFileContext().getDefaultFileSystem());
        FileContext lfs = FileContext.getFileContext(spylfs, conf);
        Mockito.doReturn(lfs).when(logHandler).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        FsPermission defaultPermission = FsPermission.getDirDefault().applyUMask(lfs.getUMask());
        final FileStatus fs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, defaultPermission, "", "", new Path(localLogDirs[0].getAbsolutePath()));
        Mockito.doReturn(fs).when(spylfs).getFileStatus(ArgumentMatchers.isA(Path.class));
        logHandler.init(conf);
        logHandler.start();
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, user, null, null));
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container11, ContainerType.APPLICATION_MASTER, 0));
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        Path[] localAppLogDirs = new Path[2];
        localAppLogDirs[0] = new Path(localLogDirs[0].getAbsolutePath(), appId.toString());
        localAppLogDirs[1] = new Path(localLogDirs[1].getAbsolutePath(), appId.toString());
        TestNonAggregatingLogHandler.testDeletionServiceCall(mockDelService, user, 5000, localAppLogDirs);
        logHandler.close();
        for (int i = 0; i < (localLogDirs.length); i++) {
            FileUtils.deleteDirectory(localLogDirs[i]);
        }
    }

    @Test
    public void testDelayedDelete() throws IOException {
        File[] localLogDirs = TestNonAggregatingLogHandler.getLocalLogDirFiles(this.getClass().getName(), 2);
        String localLogDirsString = ((localLogDirs[0].getAbsolutePath()) + ",") + (localLogDirs[1].getAbsolutePath());
        conf.set(NM_LOG_DIRS, localLogDirsString);
        conf.setBoolean(LOG_AGGREGATION_ENABLED, false);
        conf.setLong(NM_LOG_RETAIN_SECONDS, DEFAULT_NM_LOG_RETAIN_SECONDS);
        dirsHandler.init(conf);
        NonAggregatingLogHandler logHandler = new TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor(dispatcher, mockDelService, dirsHandler);
        logHandler.init(conf);
        logHandler.start();
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, user, null, null));
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container11, ContainerType.APPLICATION_MASTER, 0));
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        Path[] localAppLogDirs = new Path[2];
        localAppLogDirs[0] = new Path(localLogDirs[0].getAbsolutePath(), appId.toString());
        localAppLogDirs[1] = new Path(localLogDirs[1].getAbsolutePath(), appId.toString());
        ScheduledThreadPoolExecutor mockSched = ((TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor) (logHandler)).mockSched;
        Mockito.verify(mockSched).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(10800L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        logHandler.close();
        for (int i = 0; i < (localLogDirs.length); i++) {
            FileUtils.deleteDirectory(localLogDirs[i]);
        }
    }

    @Test
    public void testStop() throws Exception {
        NonAggregatingLogHandler aggregatingLogHandler = new NonAggregatingLogHandler(null, null, null, new NMNullStateStoreService());
        // It should not throw NullPointerException
        aggregatingLogHandler.stop();
        TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor logHandler = new TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor(null, null, null);
        logHandler.init(new Configuration());
        stop();
        Mockito.verify(logHandler.mockSched).shutdown();
        Mockito.verify(logHandler.mockSched).awaitTermination(ArgumentMatchers.eq(10L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Mockito.verify(logHandler.mockSched).shutdownNow();
        close();
        aggregatingLogHandler.close();
    }

    @Test
    public void testHandlingApplicationFinishedEvent() throws IOException {
        DeletionService delService = new DeletionService(null);
        NonAggregatingLogHandler aggregatingLogHandler = new NonAggregatingLogHandler(new InlineDispatcher(), delService, dirsHandler, new NMNullStateStoreService());
        dirsHandler.init(conf);
        dirsHandler.start();
        delService.init(conf);
        delService.start();
        aggregatingLogHandler.init(conf);
        aggregatingLogHandler.start();
        // It should NOT throw RejectedExecutionException
        aggregatingLogHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        aggregatingLogHandler.stop();
        // It should NOT throw RejectedExecutionException after stopping
        // handler service.
        aggregatingLogHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        aggregatingLogHandler.close();
    }

    private class NonAggregatingLogHandlerWithMockExecutor extends NonAggregatingLogHandler {
        private ScheduledThreadPoolExecutor mockSched;

        public NonAggregatingLogHandlerWithMockExecutor(Dispatcher dispatcher, DeletionService delService, LocalDirsHandlerService dirsHandler) {
            this(dispatcher, delService, dirsHandler, new NMNullStateStoreService());
        }

        public NonAggregatingLogHandlerWithMockExecutor(Dispatcher dispatcher, DeletionService delService, LocalDirsHandlerService dirsHandler, NMStateStoreService stateStore) {
            super(dispatcher, delService, dirsHandler, stateStore);
        }

        @Override
        ScheduledThreadPoolExecutor createScheduledThreadPoolExecutor(Configuration conf) {
            mockSched = Mockito.mock(ScheduledThreadPoolExecutor.class);
            return mockSched;
        }
    }

    /* Test to ensure that we handle the cleanup of directories that may not have
    the application log dirs we're trying to delete or may have other problems.
    Test creates 7 log dirs, and fails the directory check for 4 of them and
    then checks to ensure we tried to delete only the ones that passed the
    check.
     */
    @Test
    public void testFailedDirLogDeletion() throws Exception {
        File[] localLogDirs = TestNonAggregatingLogHandler.getLocalLogDirFiles(this.getClass().getName(), 7);
        final List<String> localLogDirPaths = new ArrayList<String>(localLogDirs.length);
        for (int i = 0; i < (localLogDirs.length); i++) {
            localLogDirPaths.add(localLogDirs[i].getAbsolutePath());
        }
        String localLogDirsString = StringUtils.join(localLogDirPaths, ",");
        conf.set(NM_LOG_DIRS, localLogDirsString);
        conf.setBoolean(LOG_AGGREGATION_ENABLED, false);
        conf.setLong(NM_LOG_RETAIN_SECONDS, 0L);
        LocalDirsHandlerService mockDirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        NonAggregatingLogHandler rawLogHandler = new NonAggregatingLogHandler(dispatcher, mockDelService, mockDirsHandler, new NMNullStateStoreService());
        NonAggregatingLogHandler logHandler = Mockito.spy(rawLogHandler);
        AbstractFileSystem spylfs = Mockito.spy(FileContext.getLocalFSFileContext().getDefaultFileSystem());
        FileContext lfs = FileContext.getFileContext(spylfs, conf);
        Mockito.doReturn(lfs).when(logHandler).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        logHandler.init(conf);
        logHandler.start();
        TestNonAggregatingLogHandler.runMockedFailedDirs(logHandler, appId, user, mockDelService, mockDirsHandler, conf, spylfs, lfs, localLogDirs);
        logHandler.close();
    }

    @Test
    public void testRecovery() throws Exception {
        File[] localLogDirs = TestNonAggregatingLogHandler.getLocalLogDirFiles(this.getClass().getName(), 2);
        String localLogDirsString = ((localLogDirs[0].getAbsolutePath()) + ",") + (localLogDirs[1].getAbsolutePath());
        conf.set(NM_LOG_DIRS, localLogDirsString);
        conf.setBoolean(LOG_AGGREGATION_ENABLED, false);
        conf.setLong(NM_LOG_RETAIN_SECONDS, DEFAULT_NM_LOG_RETAIN_SECONDS);
        dirsHandler.init(conf);
        appEventHandler.resetLogHandlingEvent();
        Assert.assertFalse(appEventHandler.receiveLogHandlingFinishEvent());
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor logHandler = new TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor(dispatcher, mockDelService, dirsHandler, stateStore);
        logHandler.init(conf);
        start();
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppStartedEvent(appId, user, null, null));
        logHandler.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerContainerFinishedEvent(container11, ContainerType.APPLICATION_MASTER, 0));
        handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        // simulate a restart and verify deletion is rescheduled
        close();
        logHandler = new TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor(dispatcher, mockDelService, dirsHandler, stateStore);
        logHandler.init(conf);
        start();
        ArgumentCaptor<Runnable> schedArg = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(logHandler.mockSched).schedule(schedArg.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
        // execute the runnable and verify another restart has nothing scheduled
        schedArg.getValue().run();
        close();
        logHandler = new TestNonAggregatingLogHandler.NonAggregatingLogHandlerWithMockExecutor(dispatcher, mockDelService, dirsHandler, stateStore);
        logHandler.init(conf);
        start();
        Mockito.verify(logHandler.mockSched, Mockito.never()).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        // wait events get drained.
        this.dispatcher.await();
        Assert.assertTrue(appEventHandler.receiveLogHandlingFinishEvent());
        appEventHandler.resetLogHandlingEvent();
        Assert.assertFalse(appEventHandler.receiveLogHandlingFailedEvent());
        // send an app finish event against a removed app
        handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerAppFinishedEvent(appId));
        this.dispatcher.await();
        // verify to receive a log failed event.
        Assert.assertTrue(appEventHandler.receiveLogHandlingFailedEvent());
        Assert.assertFalse(appEventHandler.receiveLogHandlingFinishEvent());
        close();
    }

    static class DeletePathsMatcher implements ArgumentMatcher<Path[]> , VarargMatcher {
        // to get rid of serialization warning
        static final long serialVersionUID = 0;

        private transient Path[] matchPaths;

        DeletePathsMatcher(Path... matchPaths) {
            this.matchPaths = matchPaths;
        }

        @Override
        public boolean matches(Path[] varargs) {
            return new EqualsBuilder().append(matchPaths, varargs).isEquals();
        }

        // function to get rid of FindBugs warning
        private void readObject(ObjectInputStream os) throws NotSerializableException {
            throw new NotSerializableException(this.getClass().getName());
        }
    }

    class ApplicationEventHandler implements EventHandler<ApplicationEvent> {
        private boolean logHandlingFinished = false;

        private boolean logHandlingFailed = false;

        @Override
        public void handle(ApplicationEvent event) {
            switch (event.getType()) {
                case APPLICATION_LOG_HANDLING_FINISHED :
                    logHandlingFinished = true;
                    break;
                case APPLICATION_LOG_HANDLING_FAILED :
                    logHandlingFailed = true;
                default :
                    // do nothing.
            }
        }

        public boolean receiveLogHandlingFinishEvent() {
            return logHandlingFinished;
        }

        public boolean receiveLogHandlingFailedEvent() {
            return logHandlingFailed;
        }

        public void resetLogHandlingEvent() {
            logHandlingFinished = false;
            logHandlingFailed = false;
        }
    }
}

