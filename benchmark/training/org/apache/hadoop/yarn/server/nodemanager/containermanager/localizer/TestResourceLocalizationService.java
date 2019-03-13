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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import ContainerLocalizer.APPCACHE;
import ContainerLocalizer.FILECACHE;
import ContainerLocalizer.USERCACHE;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import LocalResourceVisibility.PRIVATE;
import LocalResourceVisibility.PUBLIC;
import LocalizerAction.DIE;
import LocalizerAction.LIVE;
import Path.SEPARATOR;
import ResourceLocalizationService.NM_PRIVATE_PERM;
import ResourceState.DOWNLOADING;
import ResourceState.FAILED;
import ResourceState.LOCALIZED;
import ResourceStatusType.FETCH_PENDING;
import ResourceStatusType.FETCH_SUCCESS;
import YarnConfiguration.NM_DISK_HEALTH_CHECK_INTERVAL_MS;
import YarnConfiguration.NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_RECOVERY_ENABLED;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSError;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Options.ChecksumOpt;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.DefaultContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.api.ResourceLocalizationSpec;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalResourceStatus;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerHeartbeatResponse;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerStatus;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerResourceFailedEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task.FileDeletionMatcher;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService.LocalizerRunner;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService.LocalizerTracker;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService.PublicLocalizer;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizationEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerResourceRequestEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceFailedLocalizationEvent;
import org.apache.hadoop.yarn.server.nodemanager.executor.LocalizerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.metrics.NodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMMemoryStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.VarargMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static ContainerLocalizer.APPCACHE;
import static ContainerLocalizer.FILECACHE;
import static ContainerLocalizer.USERCACHE;
import static ResourceLocalizationService.NM_PRIVATE_DIR;


public class TestResourceLocalizationService {
    static final Path basedir = new Path("target", TestResourceLocalizationService.class.getName());

    static Server mockServer;

    private Configuration conf;

    private AbstractFileSystem spylfs;

    private FileContext lfs;

    private NMContext nmContext;

    private NodeManagerMetrics metrics;

    @Test
    public void testLocalizationInit() throws Exception {
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(new Configuration());
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.spy(new DeletionService(exec));
        delService.init(conf);
        delService.start();
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        LocalDirsHandlerService diskhandler = new LocalDirsHandlerService();
        diskhandler.init(conf);
        ResourceLocalizationService locService = Mockito.spy(new ResourceLocalizationService(dispatcher, exec, delService, diskhandler, nmContext, metrics));
        Mockito.doReturn(lfs).when(locService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        try {
            dispatcher.start();
            // initialize ResourceLocalizationService
            locService.init(conf);
            final FsPermission defaultPerm = new FsPermission(((short) (493)));
            // verify directory creation
            for (Path p : localDirs) {
                p = new Path(new URI(p.toString()).getPath());
                Path usercache = new Path(p, USERCACHE);
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(usercache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
                Path publicCache = new Path(p, FILECACHE);
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(publicCache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
                Path nmPriv = new Path(p, NM_PRIVATE_DIR);
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(nmPriv), ArgumentMatchers.eq(NM_PRIVATE_PERM), ArgumentMatchers.eq(true));
            }
        } finally {
            dispatcher.stop();
            delService.stop();
        }
    }

    @Test
    public void testDirectoryCleanupOnNewlyCreatedStateStore() throws IOException, URISyntaxException {
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(new Configuration());
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.spy(new DeletionService(exec));
        delService.init(conf);
        delService.start();
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        LocalDirsHandlerService diskhandler = new LocalDirsHandlerService();
        diskhandler.init(conf);
        NMStateStoreService nmStateStoreService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(nmStateStoreService.canRecover()).thenReturn(true);
        Mockito.when(nmStateStoreService.isNewlyCreated()).thenReturn(true);
        ResourceLocalizationService locService = Mockito.spy(new ResourceLocalizationService(dispatcher, exec, delService, diskhandler, nmContext, metrics));
        Mockito.doReturn(lfs).when(locService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        try {
            dispatcher.start();
            // initialize ResourceLocalizationService
            locService.init(conf);
            final FsPermission defaultPerm = new FsPermission(((short) (493)));
            // verify directory creation
            for (Path p : localDirs) {
                p = new Path(new URI(p.toString()).getPath());
                Path usercache = new Path(p, USERCACHE);
                Mockito.verify(spylfs).rename(ArgumentMatchers.eq(usercache), ArgumentMatchers.any(Path.class), ArgumentMatchers.any());
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(usercache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
                Path publicCache = new Path(p, FILECACHE);
                Mockito.verify(spylfs).rename(ArgumentMatchers.eq(usercache), ArgumentMatchers.any(Path.class), ArgumentMatchers.any());
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(publicCache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
                Path nmPriv = new Path(p, NM_PRIVATE_DIR);
                Mockito.verify(spylfs).rename(ArgumentMatchers.eq(usercache), ArgumentMatchers.any(Path.class), ArgumentMatchers.any());
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(nmPriv), ArgumentMatchers.eq(NM_PRIVATE_PERM), ArgumentMatchers.eq(true));
            }
        } finally {
            dispatcher.stop();
            delService.stop();
        }
    }

    // mocked generics
    @Test
    @SuppressWarnings("unchecked")
    public void testResourceRelease() throws Exception {
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        LocalizerTracker mockLocallilzerTracker = Mockito.mock(LocalizerTracker.class);
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        // Ignore actual localization
        EventHandler<LocalizerEvent> localizerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(LocalizerEventType.class, localizerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        DeletionService delService = new DeletionService(exec);
        delService.init(new Configuration());
        delService.start();
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(mockLocallilzerTracker).when(spyService).createLocalizerTracker(ArgumentMatchers.isA(Configuration.class));
        Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        try {
            spyService.init(conf);
            spyService.start();
            final String user = "user0";
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn(user);
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // Get a handle on the trackers after they're setup with INIT_APP_RESOURCES
            LocalResourcesTracker appTracker = spyService.getLocalResourcesTracker(APPLICATION, user, appId);
            LocalResourcesTracker privTracker = spyService.getLocalResourcesTracker(PRIVATE, user, appId);
            LocalResourcesTracker pubTracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            // init container.
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            // Send localization requests for one resource of each type.
            final LocalResource privResource = TestResourceLocalizationService.getPrivateMockedResource(r);
            final LocalResourceRequest privReq = new LocalResourceRequest(privResource);
            final LocalResource pubResource = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq = new LocalResourceRequest(pubResource);
            final LocalResource pubResource2 = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq2 = new LocalResourceRequest(pubResource2);
            final LocalResource appResource = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResourceRequest appReq = new LocalResourceRequest(appResource);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PRIVATE, Collections.singletonList(privReq));
            req.put(PUBLIC, Collections.singletonList(pubReq));
            req.put(APPLICATION, Collections.singletonList(appReq));
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req2 = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req2.put(PRIVATE, Collections.singletonList(privReq));
            req2.put(PUBLIC, Collections.singletonList(pubReq2));
            Set<LocalResourceRequest> pubRsrcs = new HashSet<LocalResourceRequest>();
            pubRsrcs.add(pubReq);
            pubRsrcs.add(pubReq2);
            // Send Request event
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req2));
            dispatcher.await();
            int privRsrcCount = 0;
            for (LocalizedResource lr : privTracker) {
                privRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 2, lr.getRefCount());
                Assert.assertEquals(privReq, lr.getRequest());
            }
            Assert.assertEquals(1, privRsrcCount);
            int pubRsrcCount = 0;
            for (LocalizedResource lr : pubTracker) {
                pubRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 1, lr.getRefCount());
                pubRsrcs.remove(lr.getRequest());
            }
            Assert.assertEquals(0, pubRsrcs.size());
            Assert.assertEquals(2, pubRsrcCount);
            int appRsrcCount = 0;
            for (LocalizedResource lr : appTracker) {
                appRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 1, lr.getRefCount());
                Assert.assertEquals(appReq, lr.getRequest());
            }
            Assert.assertEquals(1, appRsrcCount);
            // Send Cleanup Event
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationCleanupEvent(c, req));
            Mockito.verify(mockLocallilzerTracker).cleanupPrivLocalizers("container_314159265358979_0003_01_000042");
            req2.remove(PRIVATE);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationCleanupEvent(c, req2));
            dispatcher.await();
            pubRsrcs.add(pubReq);
            pubRsrcs.add(pubReq2);
            privRsrcCount = 0;
            for (LocalizedResource lr : privTracker) {
                privRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 1, lr.getRefCount());
                Assert.assertEquals(privReq, lr.getRequest());
            }
            Assert.assertEquals(1, privRsrcCount);
            pubRsrcCount = 0;
            for (LocalizedResource lr : pubTracker) {
                pubRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 0, lr.getRefCount());
                pubRsrcs.remove(lr.getRequest());
            }
            Assert.assertEquals(0, pubRsrcs.size());
            Assert.assertEquals(2, pubRsrcCount);
            appRsrcCount = 0;
            for (LocalizedResource lr : appTracker) {
                appRsrcCount++;
            }
            Assert.assertEquals(0, appRsrcCount);
        } finally {
            dispatcher.stop();
            delService.stop();
        }
    }

    // mocked generics
    @Test
    @SuppressWarnings("unchecked")
    public void testRecovery() throws Exception {
        final String user1 = "user1";
        final String user2 = "user2";
        final ApplicationId appId1 = ApplicationId.newInstance(1, 1);
        final ApplicationId appId2 = ApplicationId.newInstance(1, 2);
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        NMMemoryStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        start();
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        // Ignore actual localization
        EventHandler<LocalizerEvent> localizerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(LocalizerEventType.class, localizerBus);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        ResourceLocalizationService spyService = createSpyService(dispatcher, dirsHandler, stateStore);
        try {
            spyService.init(conf);
            spyService.start();
            final Application app1 = Mockito.mock(Application.class);
            Mockito.when(app1.getUser()).thenReturn(user1);
            Mockito.when(app1.getAppId()).thenReturn(appId1);
            final Application app2 = Mockito.mock(Application.class);
            Mockito.when(app2.getUser()).thenReturn(user2);
            Mockito.when(app2.getAppId()).thenReturn(appId2);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app1));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app2));
            dispatcher.await();
            // Get a handle on the trackers after they're setup with INIT_APP_RESOURCES
            LocalResourcesTracker appTracker1 = spyService.getLocalResourcesTracker(APPLICATION, user1, appId1);
            LocalResourcesTracker privTracker1 = spyService.getLocalResourcesTracker(PRIVATE, user1, null);
            LocalResourcesTracker appTracker2 = spyService.getLocalResourcesTracker(APPLICATION, user2, appId2);
            LocalResourcesTracker pubTracker = spyService.getLocalResourcesTracker(PUBLIC, null, null);
            // init containers
            final Container c1 = TestResourceLocalizationService.getMockContainer(appId1, 1, user1);
            final Container c2 = TestResourceLocalizationService.getMockContainer(appId2, 2, user2);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            // Send localization requests of each type.
            final LocalResource privResource1 = TestResourceLocalizationService.getPrivateMockedResource(r);
            final LocalResourceRequest privReq1 = new LocalResourceRequest(privResource1);
            final LocalResource privResource2 = TestResourceLocalizationService.getPrivateMockedResource(r);
            final LocalResourceRequest privReq2 = new LocalResourceRequest(privResource2);
            final LocalResource pubResource1 = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq1 = new LocalResourceRequest(pubResource1);
            final LocalResource pubResource2 = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq2 = new LocalResourceRequest(pubResource2);
            final LocalResource appResource1 = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResourceRequest appReq1 = new LocalResourceRequest(appResource1);
            final LocalResource appResource2 = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResourceRequest appReq2 = new LocalResourceRequest(appResource2);
            final LocalResource appResource3 = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResourceRequest appReq3 = new LocalResourceRequest(appResource3);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req1 = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req1.put(PRIVATE, Arrays.asList(new LocalResourceRequest[]{ privReq1, privReq2 }));
            req1.put(PUBLIC, Collections.singletonList(pubReq1));
            req1.put(APPLICATION, Collections.singletonList(appReq1));
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req2 = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req2.put(APPLICATION, Arrays.asList(new LocalResourceRequest[]{ appReq2, appReq3 }));
            req2.put(PUBLIC, Collections.singletonList(pubReq2));
            // Send Request event
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c1, req1));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c2, req2));
            dispatcher.await();
            // Simulate start of localization for all resources
            privTracker1.getPathForLocalization(privReq1, dirsHandler.getLocalPathForWrite(((USERCACHE) + user1)), null);
            privTracker1.getPathForLocalization(privReq2, dirsHandler.getLocalPathForWrite(((USERCACHE) + user1)), null);
            LocalizedResource privLr1 = privTracker1.getLocalizedResource(privReq1);
            LocalizedResource privLr2 = privTracker1.getLocalizedResource(privReq2);
            appTracker1.getPathForLocalization(appReq1, dirsHandler.getLocalPathForWrite(((APPCACHE) + appId1)), null);
            LocalizedResource appLr1 = appTracker1.getLocalizedResource(appReq1);
            appTracker2.getPathForLocalization(appReq2, dirsHandler.getLocalPathForWrite(((APPCACHE) + appId2)), null);
            LocalizedResource appLr2 = appTracker2.getLocalizedResource(appReq2);
            appTracker2.getPathForLocalization(appReq3, dirsHandler.getLocalPathForWrite(((APPCACHE) + appId2)), null);
            LocalizedResource appLr3 = appTracker2.getLocalizedResource(appReq3);
            pubTracker.getPathForLocalization(pubReq1, dirsHandler.getLocalPathForWrite(FILECACHE), null);
            LocalizedResource pubLr1 = pubTracker.getLocalizedResource(pubReq1);
            pubTracker.getPathForLocalization(pubReq2, dirsHandler.getLocalPathForWrite(FILECACHE), null);
            LocalizedResource pubLr2 = pubTracker.getLocalizedResource(pubReq2);
            // Simulate completion of localization for most resources with
            // possibly different sizes than in the request
            Assert.assertNotNull("Localization not started", privLr1.getLocalPath());
            privTracker1.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(privReq1, privLr1.getLocalPath(), ((privLr1.getSize()) + 5)));
            Assert.assertNotNull("Localization not started", privLr2.getLocalPath());
            privTracker1.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(privReq2, privLr2.getLocalPath(), ((privLr2.getSize()) + 10)));
            Assert.assertNotNull("Localization not started", appLr1.getLocalPath());
            appTracker1.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(appReq1, appLr1.getLocalPath(), appLr1.getSize()));
            Assert.assertNotNull("Localization not started", appLr3.getLocalPath());
            appTracker2.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(appReq3, appLr3.getLocalPath(), ((appLr3.getSize()) + 7)));
            Assert.assertNotNull("Localization not started", pubLr1.getLocalPath());
            pubTracker.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(pubReq1, pubLr1.getLocalPath(), ((pubLr1.getSize()) + 1000)));
            Assert.assertNotNull("Localization not started", pubLr2.getLocalPath());
            pubTracker.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(pubReq2, pubLr2.getLocalPath(), ((pubLr2.getSize()) + 99999)));
            dispatcher.await();
            Assert.assertEquals(LOCALIZED, privLr1.getState());
            Assert.assertEquals(LOCALIZED, privLr2.getState());
            Assert.assertEquals(LOCALIZED, appLr1.getState());
            Assert.assertEquals(DOWNLOADING, appLr2.getState());
            Assert.assertEquals(LOCALIZED, appLr3.getState());
            Assert.assertEquals(LOCALIZED, pubLr1.getState());
            Assert.assertEquals(LOCALIZED, pubLr2.getState());
            // restart and recover
            spyService = createSpyService(dispatcher, dirsHandler, stateStore);
            spyService.init(conf);
            spyService.recoverLocalizedResources(stateStore.loadLocalizationState());
            dispatcher.await();
            appTracker1 = spyService.getLocalResourcesTracker(APPLICATION, user1, appId1);
            privTracker1 = spyService.getLocalResourcesTracker(PRIVATE, user1, null);
            appTracker2 = spyService.getLocalResourcesTracker(APPLICATION, user2, appId2);
            pubTracker = spyService.getLocalResourcesTracker(PUBLIC, null, null);
            LocalizedResource recoveredRsrc = privTracker1.getLocalizedResource(privReq1);
            Assert.assertEquals(privReq1, recoveredRsrc.getRequest());
            Assert.assertEquals(privLr1.getLocalPath(), recoveredRsrc.getLocalPath());
            Assert.assertEquals(privLr1.getSize(), recoveredRsrc.getSize());
            Assert.assertEquals(LOCALIZED, recoveredRsrc.getState());
            recoveredRsrc = privTracker1.getLocalizedResource(privReq2);
            Assert.assertEquals(privReq2, recoveredRsrc.getRequest());
            Assert.assertEquals(privLr2.getLocalPath(), recoveredRsrc.getLocalPath());
            Assert.assertEquals(privLr2.getSize(), recoveredRsrc.getSize());
            Assert.assertEquals(LOCALIZED, recoveredRsrc.getState());
            recoveredRsrc = appTracker1.getLocalizedResource(appReq1);
            Assert.assertEquals(appReq1, recoveredRsrc.getRequest());
            Assert.assertEquals(appLr1.getLocalPath(), recoveredRsrc.getLocalPath());
            Assert.assertEquals(appLr1.getSize(), recoveredRsrc.getSize());
            Assert.assertEquals(LOCALIZED, recoveredRsrc.getState());
            recoveredRsrc = appTracker2.getLocalizedResource(appReq2);
            Assert.assertNull("in-progress resource should not be present", recoveredRsrc);
            recoveredRsrc = appTracker2.getLocalizedResource(appReq3);
            Assert.assertEquals(appReq3, recoveredRsrc.getRequest());
            Assert.assertEquals(appLr3.getLocalPath(), recoveredRsrc.getLocalPath());
            Assert.assertEquals(appLr3.getSize(), recoveredRsrc.getSize());
            Assert.assertEquals(LOCALIZED, recoveredRsrc.getState());
        } finally {
            dispatcher.stop();
            close();
        }
    }

    // mocked generics
    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testLocalizerRunnerException() throws Exception {
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        LocalDirsHandlerService dirsHandlerSpy = Mockito.spy(dirsHandler);
        dirsHandlerSpy.init(conf);
        DeletionService delServiceReal = new DeletionService(exec);
        DeletionService delService = Mockito.spy(delServiceReal);
        delService.init(new Configuration());
        delService.start();
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandlerSpy, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        try {
            spyService.init(conf);
            spyService.start();
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn("user0");
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, "user0");
            final LocalResource resource1 = TestResourceLocalizationService.getPrivateMockedResource(r);
            System.out.println("Here 4");
            final LocalResourceRequest req1 = new LocalResourceRequest(resource1);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> rsrcs = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            List<LocalResourceRequest> privateResourceList = new ArrayList<LocalResourceRequest>();
            privateResourceList.add(req1);
            rsrcs.put(PRIVATE, privateResourceList);
            final Constructor<?>[] constructors = FSError.class.getDeclaredConstructors();
            constructors[0].setAccessible(true);
            FSError fsError = ((FSError) (constructors[0].newInstance(new IOException("Disk Error"))));
            Mockito.doThrow(fsError).when(dirsHandlerSpy).getLocalPathForWrite(ArgumentMatchers.isA(String.class));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, rsrcs));
            Thread.sleep(1000);
            dispatcher.await();
            // Verify if ContainerResourceFailedEvent is invoked on FSError
            Mockito.verify(containerBus).handle(ArgumentMatchers.isA(ContainerResourceFailedEvent.class));
        } finally {
            spyService.stop();
            dispatcher.stop();
            delService.stop();
        }
    }

    // mocked generics
    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testLocalizationHeartbeat() throws Exception {
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[1];
        // Making sure that we have only one local disk so that it will only be
        // selected for consecutive resource localization calls.  This is required
        // to test LocalCacheDirectoryManager.
        localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (0 + ""))));
        sDirs[0] = localDirs.get(0).toString();
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        // Adding configuration to make sure there is only one file per
        // directory
        conf.set(NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY, "37");
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        DeletionService delServiceReal = new DeletionService(exec);
        DeletionService delService = Mockito.spy(delServiceReal);
        delService.init(new Configuration());
        delService.start();
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        FsPermission defaultPermission = FsPermission.getDirDefault().applyUMask(lfs.getUMask());
        FsPermission nmPermission = NM_PRIVATE_PERM.applyUMask(lfs.getUMask());
        final Path userDir = new Path(sDirs[0].substring("file:".length()), USERCACHE);
        final Path fileDir = new Path(sDirs[0].substring("file:".length()), FILECACHE);
        final Path sysDir = new Path(sDirs[0].substring("file:".length()), NM_PRIVATE_DIR);
        final FileStatus fs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, defaultPermission, "", "", new Path(sDirs[0]));
        final FileStatus nmFs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, nmPermission, "", "", sysDir);
        Mockito.doAnswer(new Answer<FileStatus>() {
            @Override
            public FileStatus answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                if ((args.length) > 0) {
                    if ((args[0].equals(userDir)) || (args[0].equals(fileDir))) {
                        return fs;
                    }
                }
                return nmFs;
            }
        }).when(spylfs).getFileStatus(ArgumentMatchers.isA(Path.class));
        try {
            spyService.init(conf);
            spyService.start();
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn("user0");
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            ArgumentMatcher<ApplicationEvent> matchesAppInit = ( evt) -> ((evt.getType()) == (ApplicationEventType.APPLICATION_INITED)) && (appId == (evt.getApplicationID()));
            dispatcher.await();
            Mockito.verify(applicationBus).handle(ArgumentMatchers.argThat(matchesAppInit));
            // init container rsrc, localizer
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, "user0");
            FSDataOutputStream out = new FSDataOutputStream(new DataOutputBuffer(), null);
            Mockito.doReturn(out).when(spylfs).createInternal(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(EnumSet.class), ArgumentMatchers.isA(FsPermission.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyLong(), ArgumentMatchers.isA(Progressable.class), ArgumentMatchers.isA(ChecksumOpt.class), ArgumentMatchers.anyBoolean());
            final LocalResource resource1 = TestResourceLocalizationService.getPrivateMockedResource(r);
            LocalResource resource2 = null;
            do {
                resource2 = TestResourceLocalizationService.getPrivateMockedResource(r);
            } while ((resource2 == null) || (resource2.equals(resource1)) );
            LocalResource resource3 = null;
            do {
                resource3 = TestResourceLocalizationService.getPrivateMockedResource(r);
            } while (((resource3 == null) || (resource3.equals(resource1))) || (resource3.equals(resource2)) );
            // above call to make sure we don't get identical resources.
            final LocalResourceRequest req1 = new LocalResourceRequest(resource1);
            final LocalResourceRequest req2 = new LocalResourceRequest(resource2);
            final LocalResourceRequest req3 = new LocalResourceRequest(resource3);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> rsrcs = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            List<LocalResourceRequest> privateResourceList = new ArrayList<LocalResourceRequest>();
            privateResourceList.add(req1);
            privateResourceList.add(req2);
            privateResourceList.add(req3);
            rsrcs.put(PRIVATE, privateResourceList);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, rsrcs));
            // Sigh. Thread init of private localizer not accessible
            Thread.sleep(1000);
            dispatcher.await();
            String appStr = appId.toString();
            String ctnrStr = c.getContainerId().toString();
            ArgumentCaptor<LocalizerStartContext> contextCaptor = ArgumentCaptor.forClass(LocalizerStartContext.class);
            Mockito.verify(exec).startLocalizer(contextCaptor.capture());
            LocalizerStartContext context = contextCaptor.getValue();
            Path localizationTokenPath = context.getNmPrivateContainerTokens();
            Assert.assertEquals("user0", context.getUser());
            Assert.assertEquals(appStr, context.getAppId());
            Assert.assertEquals(ctnrStr, context.getLocId());
            // heartbeat from localizer
            LocalResourceStatus rsrc1success = Mockito.mock(LocalResourceStatus.class);
            LocalResourceStatus rsrc2pending = Mockito.mock(LocalResourceStatus.class);
            LocalResourceStatus rsrc2success = Mockito.mock(LocalResourceStatus.class);
            LocalResourceStatus rsrc3success = Mockito.mock(LocalResourceStatus.class);
            LocalizerStatus stat = Mockito.mock(LocalizerStatus.class);
            Mockito.when(stat.getLocalizerId()).thenReturn(ctnrStr);
            Mockito.when(rsrc1success.getResource()).thenReturn(resource1);
            Mockito.when(rsrc2pending.getResource()).thenReturn(resource2);
            Mockito.when(rsrc2success.getResource()).thenReturn(resource2);
            Mockito.when(rsrc3success.getResource()).thenReturn(resource3);
            Mockito.when(rsrc1success.getLocalSize()).thenReturn(4344L);
            Mockito.when(rsrc2success.getLocalSize()).thenReturn(2342L);
            Mockito.when(rsrc3success.getLocalSize()).thenReturn(5345L);
            URL locPath = TestResourceLocalizationService.getPath("/cache/private/blah");
            Mockito.when(rsrc1success.getLocalPath()).thenReturn(locPath);
            Mockito.when(rsrc2success.getLocalPath()).thenReturn(locPath);
            Mockito.when(rsrc3success.getLocalPath()).thenReturn(locPath);
            Mockito.when(rsrc1success.getStatus()).thenReturn(FETCH_SUCCESS);
            Mockito.when(rsrc2pending.getStatus()).thenReturn(FETCH_PENDING);
            Mockito.when(rsrc2success.getStatus()).thenReturn(FETCH_SUCCESS);
            Mockito.when(rsrc3success.getStatus()).thenReturn(FETCH_SUCCESS);
            // Four heartbeats with sending:
            // 1 - empty
            // 2 - resource1 FETCH_SUCCESS
            // 3 - resource2 FETCH_PENDING
            // 4 - resource2 FETCH_SUCCESS, resource3 FETCH_SUCCESS
            List<LocalResourceStatus> rsrcs4 = new ArrayList<LocalResourceStatus>();
            rsrcs4.add(rsrc2success);
            rsrcs4.add(rsrc3success);
            Mockito.when(stat.getResources()).thenReturn(Collections.<LocalResourceStatus>emptyList()).thenReturn(Collections.singletonList(rsrc1success)).thenReturn(Collections.singletonList(rsrc2pending)).thenReturn(rsrcs4).thenReturn(Collections.<LocalResourceStatus>emptyList());
            String localPath = (((((Path.SEPARATOR) + (USERCACHE)) + (Path.SEPARATOR)) + "user0") + (Path.SEPARATOR)) + (FILECACHE);
            // First heartbeat
            LocalizerHeartbeatResponse response = spyService.heartbeat(stat);
            Assert.assertEquals(LIVE, response.getLocalizerAction());
            Assert.assertEquals(1, response.getResourceSpecs().size());
            Assert.assertEquals(req1, new LocalResourceRequest(response.getResourceSpecs().get(0).getResource()));
            URL localizedPath = response.getResourceSpecs().get(0).getDestinationDirectory();
            // Appending to local path unique number(10) generated as a part of
            // LocalResourcesTracker
            Assert.assertTrue(localizedPath.getFile().endsWith(((localPath + (Path.SEPARATOR)) + "10")));
            // Second heartbeat
            response = spyService.heartbeat(stat);
            Assert.assertEquals(LIVE, response.getLocalizerAction());
            Assert.assertEquals(1, response.getResourceSpecs().size());
            Assert.assertEquals(req2, new LocalResourceRequest(response.getResourceSpecs().get(0).getResource()));
            localizedPath = response.getResourceSpecs().get(0).getDestinationDirectory();
            // Resource's destination path should be now inside sub directory 0 as
            // LocalCacheDirectoryManager will be used and we have restricted number
            // of files per directory to 1.
            Assert.assertTrue(localizedPath.getFile().endsWith(((((localPath + (Path.SEPARATOR)) + "0") + (Path.SEPARATOR)) + "11")));
            // Third heartbeat
            response = spyService.heartbeat(stat);
            Assert.assertEquals(LIVE, response.getLocalizerAction());
            Assert.assertEquals(1, response.getResourceSpecs().size());
            Assert.assertEquals(req3, new LocalResourceRequest(response.getResourceSpecs().get(0).getResource()));
            localizedPath = response.getResourceSpecs().get(0).getDestinationDirectory();
            Assert.assertTrue(localizedPath.getFile().endsWith(((((localPath + (Path.SEPARATOR)) + "1") + (Path.SEPARATOR)) + "12")));
            response = spyService.heartbeat(stat);
            Assert.assertEquals(LIVE, response.getLocalizerAction());
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationEvent(LocalizationEventType.CONTAINER_RESOURCES_LOCALIZED, c));
            // get shutdown after receive CONTAINER_RESOURCES_LOCALIZED event
            response = spyService.heartbeat(stat);
            Assert.assertEquals(DIE, response.getLocalizerAction());
            dispatcher.await();
            // verify container notification
            ArgumentMatcher<ContainerEvent> matchesContainerLoc = ( evt) -> ((evt.getType()) == (ContainerEventType.RESOURCE_LOCALIZED)) && ((c.getContainerId()) == (evt.getContainerID()));
            // total 3 resource localzation calls. one for each resource.
            Mockito.verify(containerBus, Mockito.times(3)).handle(ArgumentMatchers.argThat(matchesContainerLoc));
            // Verify deletion of localization token.
            Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, localizationTokenPath, null)));
        } finally {
            spyService.stop();
            dispatcher.stop();
            delService.stop();
        }
    }

    private static class DownloadingPathsMatcher implements ArgumentMatcher<Path[]> , VarargMatcher {
        static final long serialVersionUID = 0;

        private transient Set<Path> matchPaths;

        DownloadingPathsMatcher(Set<Path> matchPaths) {
            this.matchPaths = matchPaths;
        }

        @Override
        public boolean matches(Path[] downloadingPaths) {
            if ((matchPaths.size()) != (downloadingPaths.length)) {
                return false;
            }
            for (Path downloadingPath : downloadingPaths) {
                if (!(matchPaths.contains(downloadingPath))) {
                    return false;
                }
            }
            return true;
        }

        private void readObject(ObjectInputStream os) throws NotSerializableException {
            throw new NotSerializableException(this.getClass().getName());
        }
    }

    private static class DummyExecutor extends DefaultContainerExecutor {
        private volatile boolean stopLocalization = false;

        private AtomicInteger numLocalizers = new AtomicInteger(0);

        @Override
        public void startLocalizer(LocalizerStartContext ctx) throws IOException, InterruptedException {
            numLocalizers.incrementAndGet();
            while (!(stopLocalization)) {
                Thread.yield();
            } 
        }

        private void waitForLocalizers(int num) {
            while ((numLocalizers.intValue()) < num) {
                Thread.yield();
            } 
        }

        private void setStopLocalization() {
            stopLocalization = true;
        }
    }

    @Test(timeout = 20000)
    public void testDownloadingResourcesOnContainerKill() throws Exception {
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[1];
        localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (0 + ""))));
        sDirs[0] = localDirs.get(0).toString();
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        TestResourceLocalizationService.DummyExecutor exec = new TestResourceLocalizationService.DummyExecutor();
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        DeletionService delServiceReal = new DeletionService(exec);
        DeletionService delService = Mockito.spy(delServiceReal);
        delService.init(new Configuration());
        delService.start();
        DrainDispatcher dispatcher = getDispatcher(conf);
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        FsPermission defaultPermission = FsPermission.getDirDefault().applyUMask(lfs.getUMask());
        FsPermission nmPermission = NM_PRIVATE_PERM.applyUMask(lfs.getUMask());
        final Path userDir = new Path(sDirs[0].substring("file:".length()), USERCACHE);
        final Path fileDir = new Path(sDirs[0].substring("file:".length()), FILECACHE);
        final Path sysDir = new Path(sDirs[0].substring("file:".length()), NM_PRIVATE_DIR);
        final FileStatus fs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, defaultPermission, "", "", new Path(sDirs[0]));
        final FileStatus nmFs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, nmPermission, "", "", sysDir);
        Mockito.doAnswer(new Answer<FileStatus>() {
            @Override
            public FileStatus answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                if ((args.length) > 0) {
                    if ((args[0].equals(userDir)) || (args[0].equals(fileDir))) {
                        return fs;
                    }
                }
                return nmFs;
            }
        }).when(spylfs).getFileStatus(ArgumentMatchers.isA(Path.class));
        try {
            spyService.init(conf);
            spyService.start();
            doLocalization(spyService, dispatcher, exec, delService);
        } finally {
            spyService.stop();
            dispatcher.stop();
            delService.stop();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPublicResourceInitializesLocalDir() throws Exception {
        // Setup state to simulate restart NM with existing state meaning no
        // directory creation during initialization
        NMStateStoreService spyStateStore = Mockito.spy(nmContext.getNMStateStore());
        Mockito.when(spyStateStore.canRecover()).thenReturn(true);
        NMContext spyContext = Mockito.spy(nmContext);
        Mockito.when(spyContext.getNMStateStore()).thenReturn(spyStateStore);
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        DrainDispatcher dispatcher = new DrainDispatcher();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.mock(DeletionService.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        dispatcher.init(conf);
        dispatcher.start();
        try {
            ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, spyContext, metrics);
            ResourceLocalizationService spyService = Mockito.spy(rawService);
            Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
            Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
            spyService.init(conf);
            final FsPermission defaultPerm = new FsPermission(((short) (493)));
            // verify directory is not created at initialization
            for (Path p : localDirs) {
                p = new Path(new URI(p.toString()).getPath());
                Path publicCache = new Path(p, FILECACHE);
                Mockito.verify(spylfs, Mockito.never()).mkdir(ArgumentMatchers.eq(publicCache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
            }
            spyService.start();
            final String user = "user0";
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn(user);
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // init container.
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            // Queue up public resource localization
            final LocalResource pubResource1 = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq1 = new LocalResourceRequest(pubResource1);
            LocalResource pubResource2 = null;
            do {
                pubResource2 = TestResourceLocalizationService.getPublicMockedResource(r);
            } while ((pubResource2 == null) || (pubResource2.equals(pubResource1)) );
            // above call to make sure we don't get identical resources.
            final LocalResourceRequest pubReq2 = new LocalResourceRequest(pubResource2);
            Set<LocalResourceRequest> pubRsrcs = new HashSet<LocalResourceRequest>();
            pubRsrcs.add(pubReq1);
            pubRsrcs.add(pubReq2);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PUBLIC, pubRsrcs);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            dispatcher.await();
            Mockito.verify(spyService, Mockito.times(1)).checkAndInitializeLocalDirs();
            // verify directory creation
            for (Path p : localDirs) {
                p = new Path(new URI(p.toString()).getPath());
                Path publicCache = new Path(p, FILECACHE);
                Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(publicCache), ArgumentMatchers.eq(defaultPerm), ArgumentMatchers.eq(true));
            }
        } finally {
            dispatcher.stop();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPublicCacheDirPermission() throws Exception {
        // Setup state to simulate restart NM with existing state meaning no
        // directory creation during initialization
        NMStateStoreService spyStateStore = Mockito.spy(nmContext.getNMStateStore());
        Mockito.when(spyStateStore.canRecover()).thenReturn(true);
        NMContext spyContext = Mockito.spy(nmContext);
        Mockito.when(spyContext.getNMStateStore()).thenReturn(spyStateStore);
        Path localDir = new Path("target", "testPublicCacheDirPermission");
        String sDir = lfs.makeQualified(localDir).toString();
        conf.setStrings(NM_LOCAL_DIRS, sDir);
        conf.setInt(NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY, 38);
        DrainDispatcher dispatcher = new DrainDispatcher();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.mock(DeletionService.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        dispatcher.init(conf);
        dispatcher.start();
        try {
            ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, spyContext, null);
            ResourceLocalizationService spyService = Mockito.spy(rawService);
            Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
            Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
            spyService.init(conf);
            spyService.start();
            final FsPermission expectedPerm = new FsPermission(((short) (493)));
            Path publicCache = new Path(localDir, FILECACHE);
            FsPermission wrongPerm = new FsPermission(((short) (448)));
            Path overflowFolder = new Path(publicCache, "0");
            lfs.mkdir(overflowFolder, wrongPerm, false);
            spyService.lfs.setUMask(new FsPermission(((short) (511))));
            final String user = "user0";
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn(user);
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // init container.
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            Set<LocalResourceRequest> pubRsrcs = new HashSet<LocalResourceRequest>();
            for (int i = 0; i < 3; i++) {
                LocalResource pubResource = TestResourceLocalizationService.getPublicMockedResource(r, true, conf, sDir);
                LocalResourceRequest pubReq = new LocalResourceRequest(pubResource);
                pubRsrcs.add(pubReq);
            }
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PUBLIC, pubRsrcs);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            dispatcher.await();
            // verify directory creation
            Assert.assertEquals("Cache directory permissions filecache/0 is incorrect", expectedPerm, lfs.getFileStatus(overflowFolder).getPermission());
        } finally {
            dispatcher.stop();
        }
    }

    @Test(timeout = 20000)
    @SuppressWarnings("unchecked")
    public void testLocalizerHeartbeatWhenAppCleaningUp() throws Exception {
        conf.set(NM_LOCAL_DIRS, lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (0 + ""))).toString());
        // Start dispatcher.
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        dispatcher.register(ApplicationEventType.class, Mockito.mock(EventHandler.class));
        dispatcher.register(ContainerEventType.class, Mockito.mock(EventHandler.class));
        TestResourceLocalizationService.DummyExecutor exec = new TestResourceLocalizationService.DummyExecutor();
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        // Start resource localization service.
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, Mockito.mock(DeletionService.class), dirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        try {
            spyService.init(conf);
            spyService.start();
            // Init application resources.
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(1234567890L, 3);
            Mockito.when(app.getUser()).thenReturn("user0");
            Mockito.when(app.getAppId()).thenReturn(appId);
            Mockito.when(app.toString()).thenReturn(appId.toString());
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // Initialize localizer.
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 46, "user0");
            FSDataOutputStream out = new FSDataOutputStream(new DataOutputBuffer(), null);
            Mockito.doReturn(out).when(spylfs).createInternal(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(EnumSet.class), ArgumentMatchers.isA(FsPermission.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyLong(), ArgumentMatchers.isA(Progressable.class), ArgumentMatchers.isA(ChecksumOpt.class), ArgumentMatchers.anyBoolean());
            final LocalResource resource1 = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResource resource2 = TestResourceLocalizationService.getAppMockedResource(r);
            // Send localization requests for container.
            // 2 resources generated with APPLICATION visibility.
            final LocalResourceRequest req1 = new LocalResourceRequest(resource1);
            final LocalResourceRequest req2 = new LocalResourceRequest(resource2);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> rsrcs = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            List<LocalResourceRequest> appResourceList = Arrays.asList(req1, req2);
            rsrcs.put(APPLICATION, appResourceList);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, rsrcs));
            dispatcher.await();
            // Wait for localization to begin.
            exec.waitForLocalizers(1);
            final String containerIdStr = c.getContainerId().toString();
            LocalizerRunner locRunnerForContainer = spyService.getLocalizerRunner(containerIdStr);
            // Heartbeats from container localizer
            LocalResourceStatus rsrcSuccess = Mockito.mock(LocalResourceStatus.class);
            LocalizerStatus stat = Mockito.mock(LocalizerStatus.class);
            Mockito.when(stat.getLocalizerId()).thenReturn(containerIdStr);
            Mockito.when(rsrcSuccess.getResource()).thenReturn(resource1);
            Mockito.when(rsrcSuccess.getLocalSize()).thenReturn(4344L);
            Mockito.when(rsrcSuccess.getLocalPath()).thenReturn(TestResourceLocalizationService.getPath("/some/path"));
            Mockito.when(rsrcSuccess.getStatus()).thenReturn(FETCH_SUCCESS);
            Mockito.when(stat.getResources()).thenReturn(Collections.<LocalResourceStatus>emptyList());
            // First heartbeat which schedules first resource.
            LocalizerHeartbeatResponse response = spyService.heartbeat(stat);
            Assert.assertEquals("NM should tell localizer to be LIVE in Heartbeat.", LIVE, response.getLocalizerAction());
            // Cleanup container.
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationCleanupEvent(c, rsrcs));
            dispatcher.await();
            try {
                /* Directly send heartbeat to introduce race as container
                is being cleaned up.
                 */
                locRunnerForContainer.processHeartbeat(Collections.singletonList(rsrcSuccess));
            } catch (Exception e) {
                Assert.fail("Exception should not have been thrown on processing heartbeat");
            }
            // Cleanup application.
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.DESTROY_APPLICATION_RESOURCES, app));
            dispatcher.await();
            try {
                // Directly send heartbeat to introduce race as app is being cleaned up.
                locRunnerForContainer.processHeartbeat(Collections.singletonList(rsrcSuccess));
            } catch (Exception e) {
                Assert.fail("Exception should not have been thrown on processing heartbeat");
            }
            // Send another heartbeat.
            response = spyService.heartbeat(stat);
            Assert.assertEquals("NM should tell localizer to DIE in Heartbeat.", DIE, response.getLocalizerAction());
            exec.setStopLocalization();
        } finally {
            spyService.stop();
            dispatcher.stop();
        }
    }

    // mocked generics
    @Test(timeout = 20000)
    @SuppressWarnings("unchecked")
    public void testFailedPublicResource() throws Exception {
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        DrainDispatcher dispatcher = new DrainDispatcher();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.mock(DeletionService.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        dirsHandler.init(conf);
        dispatcher.init(conf);
        dispatcher.start();
        try {
            ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandler, nmContext, metrics);
            ResourceLocalizationService spyService = Mockito.spy(rawService);
            Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
            Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
            spyService.init(conf);
            spyService.start();
            final String user = "user0";
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn(user);
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // init container.
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            System.out.println(("SEED: " + seed));
            r.setSeed(seed);
            // cause chmod to fail after a delay
            final CyclicBarrier barrier = new CyclicBarrier(2);
            Mockito.doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws IOException {
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                    } catch (BrokenBarrierException e) {
                    }
                    throw new IOException("forced failure");
                }
            }).when(spylfs).setPermission(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(FsPermission.class));
            // Queue up two localization requests for the same public resource
            final LocalResource pubResource = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq = new LocalResourceRequest(pubResource);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PUBLIC, Collections.singletonList(pubReq));
            Set<LocalResourceRequest> pubRsrcs = new HashSet<LocalResourceRequest>();
            pubRsrcs.add(pubReq);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            dispatcher.await();
            // allow the chmod to fail now that both requests have been queued
            barrier.await();
            Mockito.verify(containerBus, Mockito.timeout(5000).times(2)).handle(ArgumentMatchers.isA(ContainerResourceFailedEvent.class));
        } finally {
            dispatcher.stop();
        }
    }

    /* Test case for handling RejectedExecutionException and IOException which can
    be thrown when adding public resources to the pending queue.
    RejectedExecutionException can be thrown either due to the incoming queue
    being full or if the ExecutorCompletionService threadpool is shutdown.
    Since it's hard to simulate the queue being full, this test just shuts down
    the threadpool and makes sure the exception is handled. If anything is
    messed up the async dispatcher thread will cause a system exit causing the
    test to fail.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPublicResourceAddResourceExceptions() throws Exception {
        List<Path> localDirs = new ArrayList<Path>();
        String[] sDirs = new String[4];
        for (int i = 0; i < 4; ++i) {
            localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
            sDirs[i] = localDirs.get(i).toString();
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        DrainDispatcher dispatcher = new DrainDispatcher();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        DeletionService delService = Mockito.mock(DeletionService.class);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        LocalDirsHandlerService dirsHandlerSpy = Mockito.spy(dirsHandler);
        dirsHandlerSpy.init(conf);
        dispatcher.init(conf);
        dispatcher.start();
        try {
            ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, dirsHandlerSpy, nmContext, metrics);
            ResourceLocalizationService spyService = Mockito.spy(rawService);
            Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
            Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
            spyService.init(conf);
            spyService.start();
            final String user = "user0";
            // init application
            final Application app = Mockito.mock(Application.class);
            final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
            Mockito.when(app.getUser()).thenReturn(user);
            Mockito.when(app.getAppId()).thenReturn(appId);
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // init resources
            Random r = new Random();
            r.setSeed(r.nextLong());
            // Queue localization request for the public resource
            final LocalResource pubResource = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq = new LocalResourceRequest(pubResource);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PUBLIC, Collections.singletonList(pubReq));
            // init container.
            final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
            // first test ioexception
            Mockito.doThrow(new IOException()).when(dirsHandlerSpy).getLocalPathForWrite(ArgumentMatchers.isA(String.class), Mockito.anyLong(), Mockito.anyBoolean());
            // send request
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            dispatcher.await();
            LocalResourcesTracker tracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            Assert.assertNull(tracker.getLocalizedResource(pubReq));
            // test IllegalArgumentException
            String name = Long.toHexString(r.nextLong());
            URL url = TestResourceLocalizationService.getPath((("/local/PRIVATE/" + name) + "/"));
            final LocalResource rsrc = BuilderUtils.newLocalResource(url, FILE, PUBLIC, ((r.nextInt(1024)) + 1024L), ((r.nextInt(1024)) + 2048L), false);
            final LocalResourceRequest pubReq1 = new LocalResourceRequest(rsrc);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req1 = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req1.put(PUBLIC, Collections.singletonList(pubReq1));
            Mockito.doCallRealMethod().when(dirsHandlerSpy).getLocalPathForWrite(ArgumentMatchers.isA(String.class), Mockito.anyLong(), Mockito.anyBoolean());
            // send request
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req1));
            dispatcher.await();
            tracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            Assert.assertNull(tracker.getLocalizedResource(pubReq));
            // test RejectedExecutionException by shutting down the thread pool
            PublicLocalizer publicLocalizer = spyService.getPublicLocalizer();
            publicLocalizer.threadPool.shutdown();
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            dispatcher.await();
            tracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            Assert.assertNull(tracker.getLocalizedResource(pubReq));
        } finally {
            // if we call stop with events in the queue, an InterruptedException gets
            // thrown resulting in the dispatcher thread causing a system exit
            dispatcher.await();
            dispatcher.stop();
        }
    }

    @Test(timeout = 100000)
    @SuppressWarnings("unchecked")
    public void testParallelDownloadAttemptsForPrivateResource() throws Exception {
        DrainDispatcher dispatcher1 = null;
        try {
            dispatcher1 = new DrainDispatcher();
            String user = "testuser";
            ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
            // creating one local directory
            List<Path> localDirs = new ArrayList<Path>();
            String[] sDirs = new String[1];
            for (int i = 0; i < 1; ++i) {
                localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
                sDirs[i] = localDirs.get(i).toString();
            }
            conf.setStrings(NM_LOCAL_DIRS, sDirs);
            LocalDirsHandlerService localDirHandler = new LocalDirsHandlerService();
            localDirHandler.init(conf);
            // Registering event handlers
            EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
            dispatcher1.register(ApplicationEventType.class, applicationBus);
            EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
            dispatcher1.register(ContainerEventType.class, containerBus);
            ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
            DeletionService delService = Mockito.mock(DeletionService.class);
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            // initializing directory handler.
            dirsHandler.init(conf);
            dispatcher1.init(conf);
            dispatcher1.start();
            ResourceLocalizationService rls = new ResourceLocalizationService(dispatcher1, exec, delService, localDirHandler, nmContext, metrics);
            dispatcher1.register(LocalizationEventType.class, rls);
            rls.init(conf);
            rls.handle(createApplicationLocalizationEvent(user, appId));
            LocalResourceRequest req = new LocalResourceRequest(new Path("file:///tmp"), 123L, LocalResourceType.FILE, LocalResourceVisibility.PRIVATE, "");
            // We need to pre-populate the LocalizerRunner as the
            // Resource Localization Service code internally starts them which
            // definitely we don't want.
            // creating new containers and populating corresponding localizer runners
            // Container - 1
            Container container1 = createMockContainer(user, 1);
            String localizerId1 = container1.getContainerId().toString();
            rls.getPrivateLocalizers().put(localizerId1, rls.new LocalizerRunner(new LocalizerContext(user, container1.getContainerId(), null), localizerId1));
            LocalizerRunner localizerRunner1 = rls.getLocalizerRunner(localizerId1);
            dispatcher1.getEventHandler().handle(createContainerLocalizationEvent(container1, PRIVATE, req));
            Assert.assertTrue(waitForPrivateDownloadToStart(rls, localizerId1, 1, 5000));
            // Container - 2 now makes the request.
            ContainerImpl container2 = createMockContainer(user, 2);
            String localizerId2 = container2.getContainerId().toString();
            rls.getPrivateLocalizers().put(localizerId2, rls.new LocalizerRunner(new LocalizerContext(user, container2.getContainerId(), null), localizerId2));
            LocalizerRunner localizerRunner2 = rls.getLocalizerRunner(localizerId2);
            dispatcher1.getEventHandler().handle(createContainerLocalizationEvent(container2, PRIVATE, req));
            Assert.assertTrue(waitForPrivateDownloadToStart(rls, localizerId2, 1, 5000));
            // Retrieving localized resource.
            LocalResourcesTracker tracker = rls.getLocalResourcesTracker(PRIVATE, user, appId);
            LocalizedResource lr = tracker.getLocalizedResource(req);
            // Resource would now have moved into DOWNLOADING state
            Assert.assertEquals(DOWNLOADING, lr.getState());
            // Resource should have one permit
            Assert.assertEquals(1, lr.sem.availablePermits());
            // Resource Localization Service receives first heart beat from
            // ContainerLocalizer for container1
            LocalizerHeartbeatResponse response1 = rls.heartbeat(createLocalizerStatus(localizerId1));
            // Resource must have been added to scheduled map
            Assert.assertEquals(1, localizerRunner1.scheduled.size());
            // Checking resource in the response and also available permits for it.
            Assert.assertEquals(req.getResource(), response1.getResourceSpecs().get(0).getResource().getResource());
            Assert.assertEquals(0, lr.sem.availablePermits());
            // Resource Localization Service now receives first heart beat from
            // ContainerLocalizer for container2
            LocalizerHeartbeatResponse response2 = rls.heartbeat(createLocalizerStatus(localizerId2));
            // Resource must not have been added to scheduled map
            Assert.assertEquals(0, localizerRunner2.scheduled.size());
            // No resource is returned in response
            Assert.assertEquals(0, response2.getResourceSpecs().size());
            // ContainerLocalizer - 1 now sends failed resource heartbeat.
            rls.heartbeat(createLocalizerStatusForFailedResource(localizerId1, req));
            // Resource Localization should fail and state is modified accordingly.
            // Also Local should be release on the LocalizedResource.
            Assert.assertTrue(waitForResourceState(lr, rls, req, PRIVATE, user, appId, FAILED, 5000));
            Assert.assertTrue(lr.getState().equals(FAILED));
            Assert.assertEquals(0, localizerRunner1.scheduled.size());
            // Now Container-2 once again sends heart beat to resource localization
            // service
            // Now container-2 again try to download the resource it should still
            // not get the resource as the resource is now not in DOWNLOADING state.
            response2 = rls.heartbeat(createLocalizerStatus(localizerId2));
            // Resource must not have been added to scheduled map.
            // Also as the resource has failed download it will be removed from
            // pending list.
            Assert.assertEquals(0, localizerRunner2.scheduled.size());
            Assert.assertEquals(0, localizerRunner2.pending.size());
            Assert.assertEquals(0, response2.getResourceSpecs().size());
        } finally {
            if (dispatcher1 != null) {
                dispatcher1.stop();
            }
        }
    }

    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testLocalResourcePath() throws Exception {
        // test the local path where application and user cache files will be
        // localized.
        DrainDispatcher dispatcher1 = null;
        try {
            dispatcher1 = new DrainDispatcher();
            String user = "testuser";
            ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
            // creating one local directory
            List<Path> localDirs = new ArrayList<Path>();
            String[] sDirs = new String[1];
            for (int i = 0; i < 1; ++i) {
                localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
                sDirs[i] = localDirs.get(i).toString();
            }
            conf.setStrings(NM_LOCAL_DIRS, sDirs);
            LocalDirsHandlerService localDirHandler = new LocalDirsHandlerService();
            localDirHandler.init(conf);
            // Registering event handlers
            EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
            dispatcher1.register(ApplicationEventType.class, applicationBus);
            EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
            dispatcher1.register(ContainerEventType.class, containerBus);
            ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
            DeletionService delService = Mockito.mock(DeletionService.class);
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            // initializing directory handler.
            dirsHandler.init(conf);
            dispatcher1.init(conf);
            dispatcher1.start();
            ResourceLocalizationService rls = new ResourceLocalizationService(dispatcher1, exec, delService, localDirHandler, nmContext, metrics);
            dispatcher1.register(LocalizationEventType.class, rls);
            rls.init(conf);
            rls.handle(createApplicationLocalizationEvent(user, appId));
            // We need to pre-populate the LocalizerRunner as the
            // Resource Localization Service code internally starts them which
            // definitely we don't want.
            // creating new container and populating corresponding localizer runner
            // Container - 1
            Container container1 = createMockContainer(user, 1);
            String localizerId1 = container1.getContainerId().toString();
            rls.getPrivateLocalizers().put(localizerId1, rls.new LocalizerRunner(new LocalizerContext(user, container1.getContainerId(), null), localizerId1));
            // Creating two requests for container
            // 1) Private resource
            // 2) Application resource
            LocalResourceRequest reqPriv = new LocalResourceRequest(new Path("file:///tmp1"), 123L, LocalResourceType.FILE, LocalResourceVisibility.PRIVATE, "");
            List<LocalResourceRequest> privList = new ArrayList<LocalResourceRequest>();
            privList.add(reqPriv);
            LocalResourceRequest reqApp = new LocalResourceRequest(new Path("file:///tmp2"), 123L, LocalResourceType.FILE, LocalResourceVisibility.APPLICATION, "");
            List<LocalResourceRequest> appList = new ArrayList<LocalResourceRequest>();
            appList.add(reqApp);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> rsrcs = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            rsrcs.put(APPLICATION, appList);
            rsrcs.put(PRIVATE, privList);
            dispatcher1.getEventHandler().handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(container1, rsrcs));
            // Now waiting for resource download to start. Here actual will not start
            // Only the resources will be populated into pending list.
            Assert.assertTrue(waitForPrivateDownloadToStart(rls, localizerId1, 2, 5000));
            // Validating user and application cache paths
            String userCachePath = StringUtils.join(SEPARATOR, Arrays.asList(localDirs.get(0).toUri().getRawPath(), USERCACHE, user, FILECACHE));
            String userAppCachePath = StringUtils.join(SEPARATOR, Arrays.asList(localDirs.get(0).toUri().getRawPath(), USERCACHE, user, APPCACHE, appId.toString(), FILECACHE));
            // Now the Application and private resources may come in any order
            // for download.
            // For User cahce :
            // returned destinationPath = user cache path + random number
            // For App cache :
            // returned destinationPath = user app cache path + random number
            int returnedResources = 0;
            boolean appRsrc = false;
            boolean privRsrc = false;
            while (returnedResources < 2) {
                LocalizerHeartbeatResponse response = rls.heartbeat(createLocalizerStatus(localizerId1));
                for (ResourceLocalizationSpec resourceSpec : response.getResourceSpecs()) {
                    returnedResources++;
                    Path destinationDirectory = new Path(resourceSpec.getDestinationDirectory().getFile());
                    if ((resourceSpec.getResource().getVisibility()) == (LocalResourceVisibility.APPLICATION)) {
                        appRsrc = true;
                        Assert.assertEquals(userAppCachePath, destinationDirectory.getParent().toUri().toString());
                    } else
                        if ((resourceSpec.getResource().getVisibility()) == (LocalResourceVisibility.PRIVATE)) {
                            privRsrc = true;
                            Assert.assertEquals(userCachePath, destinationDirectory.getParent().toUri().toString());
                        } else {
                            throw new Exception("Unexpected resource received.");
                        }

                }
            } 
            // We should receive both the resources (Application and Private)
            Assert.assertTrue((appRsrc && privRsrc));
        } finally {
            if (dispatcher1 != null) {
                dispatcher1.stop();
            }
        }
    }

    @Test(timeout = 100000)
    @SuppressWarnings("unchecked")
    public void testParallelDownloadAttemptsForPublicResource() throws Exception {
        DrainDispatcher dispatcher1 = null;
        String user = "testuser";
        try {
            // creating one local directory
            List<Path> localDirs = new ArrayList<Path>();
            String[] sDirs = new String[1];
            for (int i = 0; i < 1; ++i) {
                localDirs.add(lfs.makeQualified(new Path(TestResourceLocalizationService.basedir, (i + ""))));
                sDirs[i] = localDirs.get(i).toString();
            }
            conf.setStrings(NM_LOCAL_DIRS, sDirs);
            // Registering event handlers
            EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
            dispatcher1 = new DrainDispatcher();
            dispatcher1.register(ApplicationEventType.class, applicationBus);
            EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
            dispatcher1.register(ContainerEventType.class, containerBus);
            ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
            DeletionService delService = Mockito.mock(DeletionService.class);
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            // initializing directory handler.
            dirsHandler.init(conf);
            dispatcher1.init(conf);
            dispatcher1.start();
            // Creating and initializing ResourceLocalizationService but not starting
            // it as otherwise it will remove requests from pending queue.
            ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher1, exec, delService, dirsHandler, nmContext, metrics);
            ResourceLocalizationService spyService = Mockito.spy(rawService);
            dispatcher1.register(LocalizationEventType.class, spyService);
            spyService.init(conf);
            // Initially pending map should be empty for public localizer
            Assert.assertEquals(0, spyService.getPublicLocalizer().pending.size());
            LocalResourceRequest req = new LocalResourceRequest(new Path("/tmp"), 123L, LocalResourceType.FILE, LocalResourceVisibility.PUBLIC, "");
            // Initializing application
            ApplicationImpl app = Mockito.mock(ApplicationImpl.class);
            ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
            Mockito.when(app.getAppId()).thenReturn(appId);
            Mockito.when(app.getUser()).thenReturn(user);
            dispatcher1.getEventHandler().handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            // Container - 1
            // container requesting the resource
            ContainerImpl container1 = createMockContainer(user, 1);
            dispatcher1.getEventHandler().handle(createContainerLocalizationEvent(container1, PUBLIC, req));
            // Waiting for resource to change into DOWNLOADING state.
            Assert.assertTrue(waitForResourceState(null, spyService, req, PUBLIC, user, null, DOWNLOADING, 5000));
            // Waiting for download to start.
            Assert.assertTrue(waitForPublicDownloadToStart(spyService, 1, 5000));
            LocalizedResource lr = getLocalizedResource(spyService, req, PUBLIC, user, null);
            // Resource would now have moved into DOWNLOADING state
            Assert.assertEquals(DOWNLOADING, lr.getState());
            // pending should have this resource now.
            Assert.assertEquals(1, spyService.getPublicLocalizer().pending.size());
            // Now resource should have 0 permit.
            Assert.assertEquals(0, lr.sem.availablePermits());
            // Container - 2
            // Container requesting the same resource.
            ContainerImpl container2 = createMockContainer(user, 2);
            dispatcher1.getEventHandler().handle(createContainerLocalizationEvent(container2, PUBLIC, req));
            // Waiting for download to start. This should return false as new download
            // will not start
            Assert.assertFalse(waitForPublicDownloadToStart(spyService, 2, 5000));
            // Now Failing the resource download. As a part of it
            // resource state is changed and then lock is released.
            ResourceFailedLocalizationEvent locFailedEvent = new ResourceFailedLocalizationEvent(req, new Exception("test").toString());
            spyService.getLocalResourcesTracker(PUBLIC, user, null).handle(locFailedEvent);
            // Waiting for resource to change into FAILED state.
            Assert.assertTrue(waitForResourceState(lr, spyService, req, PUBLIC, user, null, FAILED, 5000));
            Assert.assertTrue(waitForResourceState(lr, spyService, req, APPLICATION, user, appId, FAILED, 5000));
            // releasing lock as a part of download failed process.
            lr.unlock();
            // removing pending download request.
            spyService.getPublicLocalizer().pending.clear();
            LocalizerContext lc = Mockito.mock(LocalizerContext.class);
            Mockito.when(lc.getContainerId()).thenReturn(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(1L, 1), 1), 1L));
            // Now I need to simulate a race condition wherein Event is added to
            // dispatcher before resource state changes to either FAILED or LOCALIZED
            // Hence sending event directly to dispatcher.
            LocalizerResourceRequestEvent localizerEvent = new LocalizerResourceRequestEvent(lr, null, lc, null);
            dispatcher1.getEventHandler().handle(localizerEvent);
            // Waiting for download to start. This should return false as new download
            // will not start
            Assert.assertFalse(waitForPublicDownloadToStart(spyService, 1, 5000));
            // Checking available permits now.
            Assert.assertEquals(1, lr.sem.availablePermits());
        } finally {
            if (dispatcher1 != null) {
                dispatcher1.stop();
            }
        }
    }

    /* Test to ensure ResourceLocalizationService can handle local dirs going bad.
    Test first sets up all the components required, then sends events to fetch
    a private, app and public resource. It then sends events to clean up the
    container and the app and ensures the right delete calls were made.
     */
    // mocked generics
    @Test
    @SuppressWarnings("unchecked")
    public void testFailedDirsResourceRelease() throws Exception {
        // setup components
        File f = new File(TestResourceLocalizationService.basedir.toString());
        String[] sDirs = new String[4];
        List<Path> localDirs = new ArrayList<Path>(sDirs.length);
        for (int i = 0; i < 4; ++i) {
            sDirs[i] = (f.getAbsolutePath()) + i;
            localDirs.add(new Path(sDirs[i]));
        }
        List<Path> containerLocalDirs = new ArrayList<Path>(localDirs.size());
        List<Path> appLocalDirs = new ArrayList<Path>(localDirs.size());
        List<Path> nmLocalContainerDirs = new ArrayList<Path>(localDirs.size());
        List<Path> nmLocalAppDirs = new ArrayList<Path>(localDirs.size());
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        conf.setLong(NM_DISK_HEALTH_CHECK_INTERVAL_MS, 500);
        LocalizerTracker mockLocallilzerTracker = Mockito.mock(LocalizerTracker.class);
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ContainerEventType.class, containerBus);
        // Ignore actual localization
        EventHandler<LocalizerEvent> localizerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(LocalizerEventType.class, localizerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService mockDirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        Mockito.doReturn(new ArrayList<String>(Arrays.asList(sDirs))).when(mockDirsHandler).getLocalDirsForCleanup();
        DeletionService delService = Mockito.mock(DeletionService.class);
        // setup mocks
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, mockDirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(mockLocallilzerTracker).when(spyService).createLocalizerTracker(ArgumentMatchers.isA(Configuration.class));
        Mockito.doReturn(lfs).when(spyService).getLocalFileContext(ArgumentMatchers.isA(Configuration.class));
        FsPermission defaultPermission = FsPermission.getDirDefault().applyUMask(lfs.getUMask());
        FsPermission nmPermission = NM_PRIVATE_PERM.applyUMask(lfs.getUMask());
        final FileStatus fs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, defaultPermission, "", "", localDirs.get(0));
        final FileStatus nmFs = new FileStatus(0, true, 1, 0, System.currentTimeMillis(), 0, nmPermission, "", "", localDirs.get(0));
        final String user = "user0";
        // init application
        final Application app = Mockito.mock(Application.class);
        final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
        Mockito.when(app.getUser()).thenReturn(user);
        Mockito.when(app.getAppId()).thenReturn(appId);
        Mockito.when(app.toString()).thenReturn(appId.toString());
        // init container.
        final Container c = TestResourceLocalizationService.getMockContainer(appId, 42, user);
        // setup local app dirs
        List<String> tmpDirs = mockDirsHandler.getLocalDirs();
        for (int i = 0; i < (tmpDirs.size()); ++i) {
            Path usersdir = new Path(tmpDirs.get(i), USERCACHE);
            Path userdir = new Path(usersdir, user);
            Path allAppsdir = new Path(userdir, APPCACHE);
            Path appDir = new Path(allAppsdir, appId.toString());
            Path containerDir = new Path(appDir, c.getContainerId().toString());
            containerLocalDirs.add(containerDir);
            appLocalDirs.add(appDir);
            Path sysDir = new Path(tmpDirs.get(i), NM_PRIVATE_DIR);
            Path appSysDir = new Path(sysDir, appId.toString());
            Path containerSysDir = new Path(appSysDir, c.getContainerId().toString());
            nmLocalContainerDirs.add(containerSysDir);
            nmLocalAppDirs.add(appSysDir);
        }
        try {
            spyService.init(conf);
            spyService.start();
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            // Get a handle on the trackers after they're setup with
            // INIT_APP_RESOURCES
            LocalResourcesTracker appTracker = spyService.getLocalResourcesTracker(APPLICATION, user, appId);
            LocalResourcesTracker privTracker = spyService.getLocalResourcesTracker(PRIVATE, user, appId);
            LocalResourcesTracker pubTracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            // init resources
            Random r = new Random();
            long seed = r.nextLong();
            r.setSeed(seed);
            // Send localization requests, one for each type of resource
            final LocalResource privResource = TestResourceLocalizationService.getPrivateMockedResource(r);
            final LocalResourceRequest privReq = new LocalResourceRequest(privResource);
            final LocalResource appResource = TestResourceLocalizationService.getAppMockedResource(r);
            final LocalResourceRequest appReq = new LocalResourceRequest(appResource);
            final LocalResource pubResource = TestResourceLocalizationService.getPublicMockedResource(r);
            final LocalResourceRequest pubReq = new LocalResourceRequest(pubResource);
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req.put(PRIVATE, Collections.singletonList(privReq));
            req.put(APPLICATION, Collections.singletonList(appReq));
            req.put(PUBLIC, Collections.singletonList(pubReq));
            Map<LocalResourceVisibility, Collection<LocalResourceRequest>> req2 = new HashMap<LocalResourceVisibility, Collection<LocalResourceRequest>>();
            req2.put(PRIVATE, Collections.singletonList(privReq));
            // Send Request event
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req));
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent(c, req2));
            dispatcher.await();
            int privRsrcCount = 0;
            for (LocalizedResource lr : privTracker) {
                privRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 2, lr.getRefCount());
                Assert.assertEquals(privReq, lr.getRequest());
            }
            Assert.assertEquals(1, privRsrcCount);
            int appRsrcCount = 0;
            for (LocalizedResource lr : appTracker) {
                appRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 1, lr.getRefCount());
                Assert.assertEquals(appReq, lr.getRequest());
            }
            Assert.assertEquals(1, appRsrcCount);
            int pubRsrcCount = 0;
            for (LocalizedResource lr : pubTracker) {
                pubRsrcCount++;
                Assert.assertEquals("Incorrect reference count", 1, lr.getRefCount());
                Assert.assertEquals(pubReq, lr.getRequest());
            }
            Assert.assertEquals(1, pubRsrcCount);
            // setup mocks for test, a set of dirs with IOExceptions and let the rest
            // go through
            for (int i = 0; i < (containerLocalDirs.size()); ++i) {
                if (i == 2) {
                    Mockito.doThrow(new IOException()).when(spylfs).getFileStatus(ArgumentMatchers.eq(containerLocalDirs.get(i)));
                    Mockito.doThrow(new IOException()).when(spylfs).getFileStatus(ArgumentMatchers.eq(nmLocalContainerDirs.get(i)));
                } else {
                    Mockito.doReturn(fs).when(spylfs).getFileStatus(ArgumentMatchers.eq(containerLocalDirs.get(i)));
                    Mockito.doReturn(nmFs).when(spylfs).getFileStatus(ArgumentMatchers.eq(nmLocalContainerDirs.get(i)));
                }
            }
            // Send Cleanup Event
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationCleanupEvent(c, req));
            Mockito.verify(mockLocallilzerTracker).cleanupPrivLocalizers("container_314159265358979_0003_01_000042");
            // match cleanup events with the mocks we setup earlier
            for (int i = 0; i < (containerLocalDirs.size()); ++i) {
                if (i == 2) {
                    try {
                        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, user, containerLocalDirs.get(i), null)));
                        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, nmLocalContainerDirs.get(i), null)));
                        Assert.fail("deletion attempts for invalid dirs");
                    } catch (Throwable e) {
                        continue;
                    }
                } else {
                    Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, user, containerLocalDirs.get(i), null)));
                    Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, nmLocalContainerDirs.get(i), null)));
                }
            }
            ArgumentMatcher<ApplicationEvent> matchesAppDestroy = ( evt) -> ((evt.getType()) == (ApplicationEventType.APPLICATION_RESOURCES_CLEANEDUP)) && (appId == (evt.getApplicationID()));
            dispatcher.await();
            // setup mocks again, this time throw UnsupportedFileSystemException and
            // IOExceptions
            for (int i = 0; i < (containerLocalDirs.size()); ++i) {
                if (i == 3) {
                    Mockito.doThrow(new IOException()).when(spylfs).getFileStatus(ArgumentMatchers.eq(appLocalDirs.get(i)));
                    Mockito.doThrow(new UnsupportedFileSystemException("test")).when(spylfs).getFileStatus(ArgumentMatchers.eq(nmLocalAppDirs.get(i)));
                } else {
                    Mockito.doReturn(fs).when(spylfs).getFileStatus(ArgumentMatchers.eq(appLocalDirs.get(i)));
                    Mockito.doReturn(nmFs).when(spylfs).getFileStatus(ArgumentMatchers.eq(nmLocalAppDirs.get(i)));
                }
            }
            LocalizationEvent destroyApp = new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.DESTROY_APPLICATION_RESOURCES, app);
            spyService.handle(destroyApp);
            // Waits for APPLICATION_RESOURCES_CLEANEDUP event to be handled.
            dispatcher.await();
            Mockito.verify(applicationBus).handle(ArgumentMatchers.argThat(matchesAppDestroy));
            // verify we got the right delete calls
            for (int i = 0; i < (containerLocalDirs.size()); ++i) {
                if (i == 3) {
                    try {
                        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, user, containerLocalDirs.get(i), null)));
                        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, nmLocalContainerDirs.get(i), null)));
                        Assert.fail("deletion attempts for invalid dirs");
                    } catch (Throwable e) {
                        continue;
                    }
                } else {
                    Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, user, containerLocalDirs.get(i), null)));
                    Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, nmLocalContainerDirs.get(i), null)));
                }
            }
        } finally {
            dispatcher.stop();
            delService.stop();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDirHandler() throws Exception {
        File f = new File(TestResourceLocalizationService.basedir.toString());
        String[] sDirs = new String[4];
        List<Path> localDirs = new ArrayList<Path>(sDirs.length);
        for (int i = 0; i < 4; ++i) {
            sDirs[i] = (f.getAbsolutePath()) + i;
            localDirs.add(new Path(sDirs[i]));
        }
        conf.setStrings(NM_LOCAL_DIRS, sDirs);
        LocalizerTracker mockLocalizerTracker = Mockito.mock(LocalizerTracker.class);
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        EventHandler<ApplicationEvent> applicationBus = Mockito.mock(EventHandler.class);
        dispatcher.register(ApplicationEventType.class, applicationBus);
        EventHandler<LocalizerEvent> localizerBus = Mockito.mock(EventHandler.class);
        dispatcher.register(LocalizerEventType.class, localizerBus);
        ContainerExecutor exec = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService mockDirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        Mockito.doReturn(new ArrayList<String>(Arrays.asList(sDirs))).when(mockDirsHandler).getLocalDirsForCleanup();
        // setup mocks
        DeletionService delService = Mockito.mock(DeletionService.class);
        ResourceLocalizationService rawService = new ResourceLocalizationService(dispatcher, exec, delService, mockDirsHandler, nmContext, metrics);
        ResourceLocalizationService spyService = Mockito.spy(rawService);
        Mockito.doReturn(TestResourceLocalizationService.mockServer).when(spyService).createServer();
        Mockito.doReturn(mockLocalizerTracker).when(spyService).createLocalizerTracker(ArgumentMatchers.isA(Configuration.class));
        final String user = "user0";
        // init application
        final Application app = Mockito.mock(Application.class);
        final ApplicationId appId = BuilderUtils.newApplicationId(314159265358979L, 3);
        Mockito.when(app.getUser()).thenReturn(user);
        Mockito.when(app.getAppId()).thenReturn(appId);
        Mockito.when(app.toString()).thenReturn(appId.toString());
        try {
            spyService.init(conf);
            spyService.start();
            spyService.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ApplicationLocalizationEvent(LocalizationEventType.INIT_APPLICATION_RESOURCES, app));
            dispatcher.await();
            LocalResourcesTracker appTracker = spyService.getLocalResourcesTracker(APPLICATION, user, appId);
            LocalResourcesTracker privTracker = spyService.getLocalResourcesTracker(PRIVATE, user, appId);
            LocalResourcesTracker pubTracker = spyService.getLocalResourcesTracker(PUBLIC, user, appId);
            Assert.assertNotNull("dirHandler for appTracker is null!", getDirsHandler());
            Assert.assertNotNull("dirHandler for privTracker is null!", getDirsHandler());
            Assert.assertNotNull("dirHandler for pubTracker is null!", getDirsHandler());
        } finally {
            dispatcher.stop();
            delService.stop();
        }
    }
}

