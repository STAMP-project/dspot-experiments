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
import ContainerExecutor.TOKEN_FILE_NAME_FMT;
import LocalResourceVisibility.APPLICATION;
import LocalResourceVisibility.PRIVATE;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.Shell.ShellCommandExecutor;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.server.nodemanager.api.LocalizationProtocol;
import org.apache.hadoop.yarn.server.nodemanager.api.ResourceLocalizationSpec;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalResourceStatus;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerAction;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ContainerLocalizer.APPCACHE;
import static ContainerLocalizer.FILECACHE;
import static ContainerLocalizer.USERCACHE;


public class TestContainerLocalizer {
    static final Logger LOG = LoggerFactory.getLogger(TestContainerLocalizer.class);

    static final Path basedir = new Path("target", TestContainerLocalizer.class.getName());

    static final FsPermission CACHE_DIR_PERM = new FsPermission(((short) (456)));

    static final FsPermission USERCACHE_DIR_PERM = new FsPermission(((short) (493)));

    static final String appUser = "yak";

    static final String appId = "app_RM_0";

    static final String containerId = "container_0";

    static final InetSocketAddress nmAddr = new InetSocketAddress("foobar", 8040);

    @Test
    public void testMain() throws Exception {
        TestContainerLocalizer.ContainerLocalizerWrapper wrapper = new TestContainerLocalizer.ContainerLocalizerWrapper();
        ContainerLocalizer localizer = wrapper.setupContainerLocalizerForTest();
        Random random = wrapper.random;
        List<Path> localDirs = wrapper.localDirs;
        Path tokenPath = wrapper.tokenPath;
        LocalizationProtocol nmProxy = wrapper.nmProxy;
        AbstractFileSystem spylfs = wrapper.spylfs;
        mockOutDownloads(localizer);
        // verify created cache
        List<Path> privCacheList = new ArrayList<Path>();
        List<Path> appCacheList = new ArrayList<Path>();
        for (Path p : localDirs) {
            Path base = new Path(new Path(p, USERCACHE), TestContainerLocalizer.appUser);
            Path privcache = new Path(base, FILECACHE);
            privCacheList.add(privcache);
            Path appDir = new Path(base, new Path(APPCACHE, TestContainerLocalizer.appId));
            Path appcache = new Path(appDir, FILECACHE);
            appCacheList.add(appcache);
        }
        // mock heartbeat responses from NM
        ResourceLocalizationSpec rsrcA = TestContainerLocalizer.getMockRsrc(random, PRIVATE, privCacheList.get(0));
        ResourceLocalizationSpec rsrcB = TestContainerLocalizer.getMockRsrc(random, PRIVATE, privCacheList.get(0));
        ResourceLocalizationSpec rsrcC = TestContainerLocalizer.getMockRsrc(random, APPLICATION, appCacheList.get(0));
        ResourceLocalizationSpec rsrcD = TestContainerLocalizer.getMockRsrc(random, PRIVATE, privCacheList.get(0));
        Mockito.when(nmProxy.heartbeat(ArgumentMatchers.isA(LocalizerStatus.class))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.singletonList(rsrcA))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.singletonList(rsrcB))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.singletonList(rsrcC))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.singletonList(rsrcD))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.<ResourceLocalizationSpec>emptyList())).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.DIE, null));
        LocalResource tRsrcA = rsrcA.getResource();
        LocalResource tRsrcB = rsrcB.getResource();
        LocalResource tRsrcC = rsrcC.getResource();
        LocalResource tRsrcD = rsrcD.getResource();
        Mockito.doReturn(new TestContainerLocalizer.FakeDownload(rsrcA.getResource().getResource().getFile(), true)).when(localizer).download(ArgumentMatchers.isA(Path.class), ArgumentMatchers.eq(tRsrcA), ArgumentMatchers.isA(UserGroupInformation.class));
        Mockito.doReturn(new TestContainerLocalizer.FakeDownload(rsrcB.getResource().getResource().getFile(), true)).when(localizer).download(ArgumentMatchers.isA(Path.class), ArgumentMatchers.eq(tRsrcB), ArgumentMatchers.isA(UserGroupInformation.class));
        Mockito.doReturn(new TestContainerLocalizer.FakeDownload(rsrcC.getResource().getResource().getFile(), true)).when(localizer).download(ArgumentMatchers.isA(Path.class), ArgumentMatchers.eq(tRsrcC), ArgumentMatchers.isA(UserGroupInformation.class));
        Mockito.doReturn(new TestContainerLocalizer.FakeDownload(rsrcD.getResource().getResource().getFile(), true)).when(localizer).download(ArgumentMatchers.isA(Path.class), ArgumentMatchers.eq(tRsrcD), ArgumentMatchers.isA(UserGroupInformation.class));
        // run localization
        localizer.runLocalization(TestContainerLocalizer.nmAddr);
        for (Path p : localDirs) {
            Path base = new Path(new Path(p, USERCACHE), TestContainerLocalizer.appUser);
            Path privcache = new Path(base, FILECACHE);
            // $x/usercache/$user/filecache
            Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(privcache), ArgumentMatchers.eq(TestContainerLocalizer.CACHE_DIR_PERM), ArgumentMatchers.eq(false));
            Path appDir = new Path(base, new Path(APPCACHE, TestContainerLocalizer.appId));
            // $x/usercache/$user/appcache/$appId/filecache
            Path appcache = new Path(appDir, FILECACHE);
            Mockito.verify(spylfs).mkdir(ArgumentMatchers.eq(appcache), ArgumentMatchers.eq(TestContainerLocalizer.CACHE_DIR_PERM), ArgumentMatchers.eq(false));
        }
        // verify tokens read at expected location
        Mockito.verify(spylfs).open(tokenPath);
        // verify downloaded resources reported to NM
        Mockito.verify(nmProxy).heartbeat(ArgumentMatchers.argThat(new TestContainerLocalizer.HBMatches(rsrcA.getResource())));
        Mockito.verify(nmProxy).heartbeat(ArgumentMatchers.argThat(new TestContainerLocalizer.HBMatches(rsrcB.getResource())));
        Mockito.verify(nmProxy).heartbeat(ArgumentMatchers.argThat(new TestContainerLocalizer.HBMatches(rsrcC.getResource())));
        Mockito.verify(nmProxy).heartbeat(ArgumentMatchers.argThat(new TestContainerLocalizer.HBMatches(rsrcD.getResource())));
        // verify all HB use localizerID provided
        Mockito.verify(nmProxy, Mockito.never()).heartbeat(ArgumentMatchers.argThat(( status) -> !(containerId.equals(status.getLocalizerId()))));
    }

    @Test(timeout = 15000)
    public void testMainFailure() throws Exception {
        TestContainerLocalizer.ContainerLocalizerWrapper wrapper = new TestContainerLocalizer.ContainerLocalizerWrapper();
        ContainerLocalizer localizer = wrapper.setupContainerLocalizerForTest();
        LocalizationProtocol nmProxy = wrapper.nmProxy;
        mockOutDownloads(localizer);
        // Assume the NM heartbeat fails say because of absent tokens.
        Mockito.when(nmProxy.heartbeat(ArgumentMatchers.isA(LocalizerStatus.class))).thenThrow(new YarnException("Sigh, no token!"));
        // run localization, it should fail
        try {
            localizer.runLocalization(TestContainerLocalizer.nmAddr);
            Assert.fail("Localization succeeded unexpectedly!");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Sigh, no token!"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLocalizerTokenIsGettingRemoved() throws Exception {
        TestContainerLocalizer.ContainerLocalizerWrapper wrapper = new TestContainerLocalizer.ContainerLocalizerWrapper();
        ContainerLocalizer localizer = wrapper.setupContainerLocalizerForTest();
        Path tokenPath = wrapper.tokenPath;
        AbstractFileSystem spylfs = wrapper.spylfs;
        mockOutDownloads(localizer);
        Mockito.doNothing().when(localizer).localizeFiles(ArgumentMatchers.any(LocalizationProtocol.class), ArgumentMatchers.any(CompletionService.class), ArgumentMatchers.any(UserGroupInformation.class));
        localizer.runLocalization(TestContainerLocalizer.nmAddr);
        Mockito.verify(spylfs, Mockito.times(1)).delete(tokenPath, false);
    }

    // mocked generics
    @Test
    @SuppressWarnings("unchecked")
    public void testContainerLocalizerClosesFilesystems() throws Exception {
        // verify filesystems are closed when localizer doesn't fail
        TestContainerLocalizer.ContainerLocalizerWrapper wrapper = new TestContainerLocalizer.ContainerLocalizerWrapper();
        ContainerLocalizer localizer = wrapper.setupContainerLocalizerForTest();
        mockOutDownloads(localizer);
        Mockito.doNothing().when(localizer).localizeFiles(ArgumentMatchers.any(LocalizationProtocol.class), ArgumentMatchers.any(CompletionService.class), ArgumentMatchers.any(UserGroupInformation.class));
        Mockito.verify(localizer, Mockito.never()).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        localizer.runLocalization(TestContainerLocalizer.nmAddr);
        Mockito.verify(localizer).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        // verify filesystems are closed when localizer fails
        localizer = wrapper.setupContainerLocalizerForTest();
        Mockito.doThrow(new YarnRuntimeException("Forced Failure")).when(localizer).localizeFiles(ArgumentMatchers.any(LocalizationProtocol.class), ArgumentMatchers.any(CompletionService.class), ArgumentMatchers.any(UserGroupInformation.class));
        Mockito.verify(localizer, Mockito.never()).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        try {
            localizer.runLocalization(TestContainerLocalizer.nmAddr);
            Assert.fail("Localization succeeded unexpectedly!");
        } catch (IOException e) {
            Mockito.verify(localizer).closeFileSystems(ArgumentMatchers.any(UserGroupInformation.class));
        }
    }

    @Test
    public void testMultipleLocalizers() throws Exception {
        TestContainerLocalizer.FakeContainerLocalizerWrapper testA = new TestContainerLocalizer.FakeContainerLocalizerWrapper();
        TestContainerLocalizer.FakeContainerLocalizerWrapper testB = new TestContainerLocalizer.FakeContainerLocalizerWrapper();
        TestContainerLocalizer.FakeContainerLocalizer localizerA = testA.init();
        TestContainerLocalizer.FakeContainerLocalizer localizerB = testB.init();
        // run localization
        Thread threadA = new Thread() {
            @Override
            public void run() {
                try {
                    runLocalization(TestContainerLocalizer.nmAddr);
                } catch (Exception e) {
                    TestContainerLocalizer.LOG.warn(e.toString());
                }
            }
        };
        Thread threadB = new Thread() {
            @Override
            public void run() {
                try {
                    runLocalization(TestContainerLocalizer.nmAddr);
                } catch (Exception e) {
                    TestContainerLocalizer.LOG.warn(e.toString());
                }
            }
        };
        ShellCommandExecutor shexcA = null;
        ShellCommandExecutor shexcB = null;
        try {
            threadA.start();
            threadB.start();
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    TestContainerLocalizer.FakeContainerLocalizer.FakeLongDownload downloader = localizerA.getDownloader();
                    return ((downloader != null) && ((downloader.getShexc()) != null)) && ((downloader.getShexc().getProcess()) != null);
                }
            }, 10, 30000);
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    TestContainerLocalizer.FakeContainerLocalizer.FakeLongDownload downloader = localizerB.getDownloader();
                    return ((downloader != null) && ((downloader.getShexc()) != null)) && ((downloader.getShexc().getProcess()) != null);
                }
            }, 10, 30000);
            shexcA = localizerA.getDownloader().getShexc();
            shexcB = localizerB.getDownloader().getShexc();
            Assert.assertTrue("Localizer A process not running, but should be", shexcA.getProcess().isAlive());
            Assert.assertTrue("Localizer B process not running, but should be", shexcB.getProcess().isAlive());
            // Stop heartbeat from giving anymore resources to download
            (testA.heartbeatResponse)++;
            (testB.heartbeatResponse)++;
            // Send DIE to localizerA. This should kill its subprocesses
            (testA.heartbeatResponse)++;
            threadA.join();
            shexcA.getProcess().waitFor(10000, TimeUnit.MILLISECONDS);
            TestCase.assertFalse("Localizer A process is still running, but shouldn't be", shexcA.getProcess().isAlive());
            Assert.assertTrue("Localizer B process not running, but should be", shexcB.getProcess().isAlive());
        } finally {
            // Make sure everything gets cleaned up
            // Process A should already be dead
            shexcA.getProcess().destroy();
            shexcB.getProcess().destroy();
            shexcA.getProcess().waitFor(10000, TimeUnit.MILLISECONDS);
            shexcB.getProcess().waitFor(10000, TimeUnit.MILLISECONDS);
            threadA.join();
            // Send DIE to localizer B
            (testB.heartbeatResponse)++;
            threadB.join();
        }
    }

    static class HBMatches implements ArgumentMatcher<LocalizerStatus> {
        final LocalResource rsrc;

        HBMatches(LocalResource rsrc) {
            this.rsrc = rsrc;
        }

        @Override
        public boolean matches(LocalizerStatus status) {
            for (LocalResourceStatus localized : status.getResources()) {
                switch (localized.getStatus()) {
                    case FETCH_SUCCESS :
                        if (localized.getLocalPath().getFile().contains(rsrc.getResource().getFile())) {
                            return true;
                        }
                        break;
                    default :
                        Assert.fail(("Unexpected: " + (localized.getStatus())));
                        break;
                }
            }
            return false;
        }
    }

    static class FakeDownload implements Callable<Path> {
        private final Path localPath;

        private final boolean succeed;

        FakeDownload(String absPath, boolean succeed) {
            this.localPath = new Path(("file:///localcache" + absPath));
            this.succeed = succeed;
        }

        @Override
        public Path call() throws IOException {
            if (!(succeed)) {
                throw new IOException(("FAIL " + (localPath)));
            }
            return localPath;
        }
    }

    class FakeContainerLocalizer extends ContainerLocalizer {
        private TestContainerLocalizer.FakeContainerLocalizer.FakeLongDownload downloader;

        FakeContainerLocalizer(FileContext lfs, String user, String appId, String localizerId, List<Path> localDirs, RecordFactory recordFactory) throws IOException {
            super(lfs, user, appId, localizerId, String.format(TOKEN_FILE_NAME_FMT, TestContainerLocalizer.containerId), localDirs, recordFactory);
        }

        TestContainerLocalizer.FakeContainerLocalizer.FakeLongDownload getDownloader() {
            return downloader;
        }

        @Override
        Callable<Path> download(Path path, LocalResource rsrc, UserGroupInformation ugi) throws IOException {
            downloader = new TestContainerLocalizer.FakeContainerLocalizer.FakeLongDownload(Mockito.mock(FileContext.class), ugi, new Configuration(), path, rsrc);
            return downloader;
        }

        class FakeLongDownload extends ContainerLocalizer.FSDownloadWrapper {
            private final Path localPath;

            private ShellCommandExecutor shexc;

            FakeLongDownload(FileContext files, UserGroupInformation ugi, Configuration conf, Path destDirPath, LocalResource resource) {
                super(files, ugi, conf, destDirPath, resource);
                this.localPath = new Path("file:///localcache");
            }

            ShellCommandExecutor getShexc() {
                return shexc;
            }

            @Override
            public Path doDownloadCall() throws IOException {
                String sleepCommand = "sleep 30";
                String[] shellCmd = new String[]{ "bash", "-c", sleepCommand };
                shexc = new Shell.ShellCommandExecutor(shellCmd);
                shexc.execute();
                return localPath;
            }
        }
    }

    class ContainerLocalizerWrapper {
        AbstractFileSystem spylfs;

        Random random;

        List<Path> localDirs;

        Path tokenPath;

        LocalizationProtocol nmProxy;

        // mocked generics
        @SuppressWarnings("unchecked")
        TestContainerLocalizer.FakeContainerLocalizer setupContainerLocalizerForTest() throws Exception {
            FileContext fs = FileContext.getLocalFSFileContext();
            spylfs = Mockito.spy(fs.getDefaultFileSystem());
            // don't actually create dirs
            Mockito.doNothing().when(spylfs).mkdir(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(FsPermission.class), ArgumentMatchers.anyBoolean());
            Configuration conf = new Configuration();
            FileContext lfs = FileContext.getFileContext(spylfs, conf);
            localDirs = new ArrayList<Path>();
            for (int i = 0; i < 4; ++i) {
                localDirs.add(lfs.makeQualified(new Path(TestContainerLocalizer.basedir, (i + ""))));
            }
            RecordFactory mockRF = TestContainerLocalizer.getMockLocalizerRecordFactory();
            TestContainerLocalizer.FakeContainerLocalizer concreteLoc = new TestContainerLocalizer.FakeContainerLocalizer(lfs, TestContainerLocalizer.appUser, TestContainerLocalizer.appId, TestContainerLocalizer.containerId, localDirs, mockRF);
            TestContainerLocalizer.FakeContainerLocalizer localizer = Mockito.spy(concreteLoc);
            // return credential stream instead of opening local file
            random = new Random();
            long seed = random.nextLong();
            System.out.println(("SEED: " + seed));
            random.setSeed(seed);
            DataInputBuffer appTokens = TestContainerLocalizer.createFakeCredentials(random, 10);
            tokenPath = lfs.makeQualified(new Path(String.format(TOKEN_FILE_NAME_FMT, TestContainerLocalizer.containerId)));
            Mockito.doReturn(new FSDataInputStream(new FakeFSDataInputStream(appTokens))).when(spylfs).open(tokenPath);
            nmProxy = Mockito.mock(LocalizationProtocol.class);
            Mockito.doReturn(nmProxy).when(localizer).getProxy(TestContainerLocalizer.nmAddr);
            sleep(ArgumentMatchers.anyInt());
            return localizer;
        }
    }

    class FakeContainerLocalizerWrapper extends TestContainerLocalizer.ContainerLocalizerWrapper {
        private int heartbeatResponse = 0;

        public TestContainerLocalizer.FakeContainerLocalizer init() throws Exception {
            FileContext fs = FileContext.getLocalFSFileContext();
            TestContainerLocalizer.FakeContainerLocalizer localizer = setupContainerLocalizerForTest();
            // verify created cache
            List<Path> privCacheList = new ArrayList<Path>();
            for (Path p : localDirs) {
                Path base = new Path(new Path(p, ContainerLocalizer.USERCACHE), TestContainerLocalizer.appUser);
                Path privcache = new Path(base, ContainerLocalizer.FILECACHE);
                privCacheList.add(privcache);
            }
            ResourceLocalizationSpec rsrc = TestContainerLocalizer.getMockRsrc(random, PRIVATE, privCacheList.get(0));
            // mock heartbeat responses from NM
            Mockito.doAnswer(new Answer<MockLocalizerHeartbeatResponse>() {
                @Override
                public MockLocalizerHeartbeatResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                    if ((heartbeatResponse) == 0) {
                        return new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.singletonList(rsrc));
                    } else
                        if ((heartbeatResponse) < 2) {
                            return new MockLocalizerHeartbeatResponse(LocalizerAction.LIVE, Collections.<ResourceLocalizationSpec>emptyList());
                        } else {
                            return new MockLocalizerHeartbeatResponse(LocalizerAction.DIE, null);
                        }

                }
            }).when(nmProxy).heartbeat(ArgumentMatchers.isA(LocalizerStatus.class));
            return localizer;
        }
    }

    @Test(timeout = 10000)
    public void testUserCacheDirPermission() throws Exception {
        Configuration conf = new Configuration();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        FileContext lfs = FileContext.getLocalFSFileContext(conf);
        Path fileCacheDir = lfs.makeQualified(new Path(TestContainerLocalizer.basedir, "filecache"));
        lfs.mkdir(fileCacheDir, FsPermission.getDefault(), true);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        ContainerLocalizer localizer = new ContainerLocalizer(lfs, UserGroupInformation.getCurrentUser().getUserName(), "application_01", "container_01", String.format(TOKEN_FILE_NAME_FMT, "container_01"), new ArrayList(), recordFactory);
        LocalResource rsrc = Mockito.mock(LocalResource.class);
        Mockito.when(rsrc.getVisibility()).thenReturn(PRIVATE);
        Path destDirPath = new Path(fileCacheDir, "0/0/85");
        // create one of the parent directories with the wrong permissions first
        FsPermission wrongPerm = new FsPermission(((short) (448)));
        lfs.mkdir(destDirPath.getParent().getParent(), wrongPerm, false);
        lfs.mkdir(destDirPath.getParent(), wrongPerm, false);
        // Localize and check the directory permission are correct.
        localizer.download(destDirPath, rsrc, UserGroupInformation.getCurrentUser());
        Assert.assertEquals("Cache directory permissions filecache/0/0 is incorrect", TestContainerLocalizer.USERCACHE_DIR_PERM, lfs.getFileStatus(destDirPath.getParent()).getPermission());
        Assert.assertEquals("Cache directory permissions filecache/0 is incorrect", TestContainerLocalizer.USERCACHE_DIR_PERM, lfs.getFileStatus(destDirPath.getParent().getParent()).getPermission());
    }
}

