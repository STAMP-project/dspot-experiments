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
package org.apache.hadoop.yarn.server.nodemanager;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import FileContext.Util;
import YarnConfiguration.NM_DEFAULT_CONTAINER_EXECUTOR_LOG_DIRS_PERMISSIONS;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.exceptions.ConfigurationException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.api.LocalizationProtocol;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerAction;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerStatus;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerDiagnosticsUpdateEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.MockLocalizerHeartbeatResponse;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.DeletionAsUserContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.LocalizerStartContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static DefaultContainerExecutor.APPCACHE_PERM;
import static DefaultContainerExecutor.APPDIR_PERM;
import static DefaultContainerExecutor.FILECACHE_PERM;
import static DefaultContainerExecutor.USER_PERM;


// @Test
// public void testInit() throws IOException, InterruptedException {
// Configuration conf = new Configuration();
// AbstractFileSystem spylfs =
// spy(FileContext.getLocalFSFileContext().getDefaultFileSystem());
// // don't actually create dirs
// //doNothing().when(spylfs).mkdir(Matchers.<Path>anyObject(),
// //    Matchers.<FsPermission>anyObject(), anyBoolean());
// FileContext lfs = FileContext.getFileContext(spylfs, conf);
// 
// Path basedir = new Path("target",
// TestDefaultContainerExecutor.class.getSimpleName());
// List<String> localDirs = new ArrayList<String>();
// List<Path> localPaths = new ArrayList<Path>();
// for (int i = 0; i < 4; ++i) {
// Path p = new Path(basedir, i + "");
// lfs.mkdir(p, null, true);
// localPaths.add(p);
// localDirs.add(p.toString());
// }
// final String user = "yak";
// final String appId = "app_RM_0";
// final Path logDir = new Path(basedir, "logs");
// final Path nmLocal = new Path(basedir, "nmPrivate/" + user + "/" + appId);
// final InetSocketAddress nmAddr = new InetSocketAddress("foobar", 8040);
// System.out.println("NMLOCAL: " + nmLocal);
// Random r = new Random();
// 
// /*
// // XXX FileContext cannot be reasonably mocked to do this
// // mock jobFiles copy
// long fileSeed = r.nextLong();
// r.setSeed(fileSeed);
// System.out.println("SEED: " + seed);
// Path fileCachePath = new Path(nmLocal, ApplicationLocalizer.FILECACHE_FILE);
// DataOutputBuffer fileCacheBytes = mockStream(spylfs, fileCachePath, r, 512);
// 
// // mock jobTokens copy
// long jobSeed = r.nextLong();
// r.setSeed(jobSeed);
// System.out.println("SEED: " + seed);
// Path jobTokenPath = new Path(nmLocal, ApplicationLocalizer.JOBTOKEN_FILE);
// DataOutputBuffer jobTokenBytes = mockStream(spylfs, jobTokenPath, r, 512);
// */
// 
// // create jobFiles
// long fileSeed = r.nextLong();
// r.setSeed(fileSeed);
// System.out.println("SEED: " + fileSeed);
// Path fileCachePath = new Path(nmLocal, ApplicationLocalizer.FILECACHE_FILE);
// byte[] fileCacheBytes = createTmpFile(fileCachePath, r, 512);
// 
// // create jobTokens
// long jobSeed = r.nextLong();
// r.setSeed(jobSeed);
// System.out.println("SEED: " + jobSeed);
// Path jobTokenPath = new Path(nmLocal, ApplicationLocalizer.JOBTOKEN_FILE);
// byte[] jobTokenBytes = createTmpFile(jobTokenPath, r, 512);
// 
// DefaultContainerExecutor dce = new DefaultContainerExecutor(lfs);
// Localization mockLocalization = mock(Localization.class);
// ApplicationLocalizer spyLocalizer =
// spy(new ApplicationLocalizer(lfs, user, appId, logDir,
// localPaths));
// // ignore cache localization
// doNothing().when(spyLocalizer).localizeFiles(
// Matchers.<Localization>anyObject(), Matchers.<Path>anyObject());
// Path workingDir = lfs.getWorkingDirectory();
// dce.initApplication(spyLocalizer, nmLocal, mockLocalization, localPaths);
// lfs.setWorkingDirectory(workingDir);
// 
// for (Path localdir : localPaths) {
// Path userdir = lfs.makeQualified(new Path(localdir,
// new Path(ApplicationLocalizer.USERCACHE, user)));
// // $localdir/$user
// verify(spylfs).mkdir(userdir,
// new FsPermission(ApplicationLocalizer.USER_PERM), true);
// // $localdir/$user/appcache
// Path jobdir = new Path(userdir, ApplicationLocalizer.appcache);
// verify(spylfs).mkdir(jobdir,
// new FsPermission(ApplicationLocalizer.appcache_PERM), true);
// // $localdir/$user/filecache
// Path filedir = new Path(userdir, ApplicationLocalizer.FILECACHE);
// verify(spylfs).mkdir(filedir,
// new FsPermission(ApplicationLocalizer.FILECACHE_PERM), true);
// // $localdir/$user/appcache/$appId
// Path appdir = new Path(jobdir, appId);
// verify(spylfs).mkdir(appdir,
// new FsPermission(ApplicationLocalizer.APPDIR_PERM), true);
// // $localdir/$user/appcache/$appId/work
// Path workdir = new Path(appdir, ApplicationLocalizer.WORKDIR);
// verify(spylfs, atMost(1)).mkdir(workdir, FsPermission.getDefault(), true);
// }
// // $logdir/$appId
// Path logdir = new Path(lfs.makeQualified(logDir), appId);
// verify(spylfs).mkdir(logdir,
// new FsPermission(ApplicationLocalizer.LOGDIR_PERM), true);
// }
public class TestDefaultContainerExecutor {
    /* // XXX FileContext cannot be mocked to do this
    static FSDataInputStream getRandomStream(Random r, int len)
    throws IOException {
    byte[] bytes = new byte[len];
    r.nextBytes(bytes);
    DataInputBuffer buf = new DataInputBuffer();
    buf.reset(bytes, 0, bytes.length);
    return new FSDataInputStream(new FakeFSDataInputStream(buf));
    }

    class PathEndsWith extends ArgumentMatcher<Path> {
    final String suffix;
    PathEndsWith(String suffix) {
    this.suffix = suffix;
    }
    @Override
    public boolean matches(Object o) {
    return
    suffix.equals(((Path)o).getName());
    }
    }

    DataOutputBuffer mockStream(
    AbstractFileSystem spylfs, Path p, Random r, int len) 
    throws IOException {
    DataOutputBuffer dob = new DataOutputBuffer();
    doReturn(getRandomStream(r, len)).when(spylfs).open(p);
    doReturn(new FileStatus(len, false, -1, -1L, -1L, p)).when(
    spylfs).getFileStatus(argThat(new PathEndsWith(p.getName())));
    doReturn(new FSDataOutputStream(dob)).when(spylfs).createInternal(
    argThat(new PathEndsWith(p.getName())),
    eq(EnumSet.of(OVERWRITE)),
    Matchers.<FsPermission>anyObject(), anyInt(), anyShort(), anyLong(),
    Matchers.<Progressable>anyObject(), anyInt(), anyBoolean());
    return dob;
    }
     */
    private static Path BASE_TMP_PATH = new Path("target", TestDefaultContainerExecutor.class.getSimpleName());

    @Test
    public void testDirPermissions() throws Exception {
        TestDefaultContainerExecutor.deleteTmpFiles();
        final String user = "somebody";
        final String appId = "app_12345_123";
        final FsPermission userCachePerm = new FsPermission(USER_PERM);
        final FsPermission appCachePerm = new FsPermission(APPCACHE_PERM);
        final FsPermission fileCachePerm = new FsPermission(FILECACHE_PERM);
        final FsPermission appDirPerm = new FsPermission(APPDIR_PERM);
        List<String> localDirs = new ArrayList<String>();
        localDirs.add(new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "localDirA").toString());
        localDirs.add(new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "localDirB").toString());
        List<String> logDirs = new ArrayList<String>();
        logDirs.add(new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "logDirA").toString());
        logDirs.add(new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "logDirB").toString());
        Configuration conf = new Configuration();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        FileContext lfs = FileContext.getLocalFSFileContext(conf);
        DefaultContainerExecutor executor = new DefaultContainerExecutor(lfs);
        executor.setConf(conf);
        executor.init(null);
        try {
            executor.createUserLocalDirs(localDirs, user);
            executor.createUserCacheDirs(localDirs, user);
            executor.createAppDirs(localDirs, user, appId);
            for (String dir : localDirs) {
                FileStatus stats = lfs.getFileStatus(new Path(new Path(dir, ContainerLocalizer.USERCACHE), user));
                Assert.assertEquals(userCachePerm, stats.getPermission());
            }
            for (String dir : localDirs) {
                Path userCachePath = new Path(new Path(dir, ContainerLocalizer.USERCACHE), user);
                Path appCachePath = new Path(userCachePath, ContainerLocalizer.APPCACHE);
                FileStatus stats = lfs.getFileStatus(appCachePath);
                Assert.assertEquals(appCachePerm, stats.getPermission());
                stats = lfs.getFileStatus(new Path(userCachePath, ContainerLocalizer.FILECACHE));
                Assert.assertEquals(fileCachePerm, stats.getPermission());
                stats = lfs.getFileStatus(new Path(appCachePath, appId));
                Assert.assertEquals(appDirPerm, stats.getPermission());
            }
            String[] permissionsArray = new String[]{ "000", "111", "555", "710", "777" };
            for (String perm : permissionsArray) {
                conf.set(NM_DEFAULT_CONTAINER_EXECUTOR_LOG_DIRS_PERMISSIONS, perm);
                executor.clearLogDirPermissions();
                FsPermission logDirPerm = new FsPermission(executor.getLogDirPermissions());
                executor.createAppLogDirs(appId, logDirs, user);
                for (String dir : logDirs) {
                    FileStatus stats = lfs.getFileStatus(new Path(dir, appId));
                    Assert.assertEquals(logDirPerm, stats.getPermission());
                    lfs.delete(new Path(dir, appId), true);
                }
            }
        } finally {
            TestDefaultContainerExecutor.deleteTmpFiles();
        }
    }

    @Test
    public void testLaunchContainerCopyFilesWithoutHTTPS() throws Exception {
        testLaunchContainerCopyFiles(false);
    }

    @Test
    public void testLaunchContainerCopyFilesWithHTTPS() throws Exception {
        testLaunchContainerCopyFiles(true);
    }

    @Test
    public void testContainerLaunchError() throws IOException, InterruptedException, ConfigurationException {
        if (Shell.WINDOWS) {
            TestDefaultContainerExecutor.BASE_TMP_PATH = new Path(new File("target").getAbsolutePath(), TestDefaultContainerExecutor.class.getSimpleName());
        }
        Path localDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "localDir");
        List<String> localDirs = new ArrayList<String>();
        localDirs.add(localDir.toString());
        List<String> logDirs = new ArrayList<String>();
        Path logDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "logDir");
        logDirs.add(logDir.toString());
        Configuration conf = new Configuration();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        conf.set(NM_LOCAL_DIRS, localDir.toString());
        conf.set(NM_LOG_DIRS, logDir.toString());
        FileContext lfs = FileContext.getLocalFSFileContext(conf);
        DefaultContainerExecutor mockExec = Mockito.spy(new DefaultContainerExecutor(lfs));
        mockExec.setConf(conf);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String diagnostics = ((String) (invocationOnMock.getArguments()[0]));
                Assert.assertTrue(("Invalid Diagnostics message: " + diagnostics), diagnostics.contains("No such file or directory"));
                return null;
            }
        }).when(mockExec).logOutput(ArgumentMatchers.any(String.class));
        String appSubmitter = "nobody";
        String appId = "APP_ID";
        String containerId = "CONTAINER_ID";
        Container container = Mockito.mock(Container.class);
        ContainerId cId = Mockito.mock(ContainerId.class);
        ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("LANG", "C");
        Mockito.when(container.getContainerId()).thenReturn(cId);
        Mockito.when(container.getLaunchContext()).thenReturn(context);
        try {
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    ContainerDiagnosticsUpdateEvent event = ((ContainerDiagnosticsUpdateEvent) (invocationOnMock.getArguments()[0]));
                    Assert.assertTrue(("Invalid Diagnostics message: " + (event.getDiagnosticsUpdate())), event.getDiagnosticsUpdate().contains("No such file or directory"));
                    return null;
                }
            }).when(container).handle(ArgumentMatchers.any(ContainerDiagnosticsUpdateEvent.class));
            Mockito.when(cId.toString()).thenReturn(containerId);
            Mockito.when(cId.getApplicationAttemptId()).thenReturn(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 0));
            Mockito.when(context.getEnvironment()).thenReturn(env);
            mockExec.createUserLocalDirs(localDirs, appSubmitter);
            mockExec.createUserCacheDirs(localDirs, appSubmitter);
            mockExec.createAppDirs(localDirs, appSubmitter, appId);
            mockExec.createAppLogDirs(appId, logDirs, appSubmitter);
            Path scriptPath = new Path("file:///bin/echo");
            Path tokensPath = new Path("file:///dev/null");
            Path keystorePath = new Path("file:///dev/null");
            Path truststorePath = new Path("file:///dev/null");
            if (Shell.WINDOWS) {
                File tmp = new File(TestDefaultContainerExecutor.BASE_TMP_PATH.toString(), "test_echo.cmd");
                BufferedWriter output = new BufferedWriter(new FileWriter(tmp));
                output.write("Exit 1");
                output.write("Echo No such file or directory 1>&2");
                output.close();
                scriptPath = new Path(tmp.getAbsolutePath());
                tmp = new File(TestDefaultContainerExecutor.BASE_TMP_PATH.toString(), "tokens");
                tmp.createNewFile();
                tokensPath = new Path(tmp.getAbsolutePath());
            }
            Path workDir = localDir;
            Path pidFile = new Path(workDir, "pid.txt");
            mockExec.init(null);
            mockExec.activateContainer(cId, pidFile);
            int ret = mockExec.launchContainer(new ContainerStartContext.Builder().setContainer(container).setNmPrivateContainerScriptPath(scriptPath).setNmPrivateTokensPath(tokensPath).setNmPrivateKeystorePath(keystorePath).setNmPrivateTruststorePath(truststorePath).setUser(appSubmitter).setAppId(appId).setContainerWorkDir(workDir).setLocalDirs(localDirs).setLogDirs(logDirs).build());
            Assert.assertNotSame(0, ret);
        } finally {
            mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(localDir).build());
            mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(logDir).build());
        }
    }

    @Test(timeout = 30000)
    public void testStartLocalizer() throws IOException, InterruptedException, YarnException {
        final Path firstDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "localDir1");
        List<String> localDirs = new ArrayList<String>();
        final Path secondDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "localDir2");
        List<String> logDirs = new ArrayList<String>();
        final Path logDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "logDir");
        final Path tokenDir = new Path(TestDefaultContainerExecutor.BASE_TMP_PATH, "tokenDir");
        FsPermission perms = new FsPermission(((short) (504)));
        Configuration conf = new Configuration();
        final FileContext mockLfs = Mockito.spy(FileContext.getLocalFSFileContext(conf));
        final FileContext.Util mockUtil = Mockito.spy(mockLfs.util());
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return mockUtil;
            }
        }).when(mockLfs).util();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Path dest = ((Path) (invocationOnMock.getArguments()[1]));
                if (dest.toString().contains(firstDir.toString())) {
                    // throw an Exception when copy token to the first local dir
                    // to simulate no space on the first drive
                    throw new IOException(("No space on this drive " + (dest.toString())));
                } else {
                    // copy token to the second local dir
                    DataOutputStream tokenOut = null;
                    try {
                        Credentials credentials = new Credentials();
                        tokenOut = mockLfs.create(dest, EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE));
                        credentials.writeTokenStorageToStream(tokenOut);
                    } finally {
                        if (tokenOut != null) {
                            tokenOut.close();
                        }
                    }
                }
                return null;
            }
        }).when(mockUtil).copy(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Path p = ((Path) (invocationOnMock.getArguments()[0]));
                // let second local directory return more free space than
                // first local directory
                if (p.toString().contains(firstDir.toString())) {
                    return new FsStatus(2000, 2000, 0);
                } else {
                    return new FsStatus(1000, 0, 1000);
                }
            }
        }).when(mockLfs).getFsStatus(ArgumentMatchers.any(Path.class));
        DefaultContainerExecutor mockExec = Mockito.spy(new DefaultContainerExecutor(mockLfs) {
            @Override
            public ContainerLocalizer createContainerLocalizer(String user, String appId, String locId, String tokenFileName, List<String> localDirs, FileContext localizerFc) throws IOException {
                // Spy on the localizer and make it return valid heart-beat
                // responses even though there is no real NodeManager.
                ContainerLocalizer localizer = super.createContainerLocalizer(user, appId, locId, tokenFileName, localDirs, localizerFc);
                ContainerLocalizer spyLocalizer = Mockito.spy(localizer);
                LocalizationProtocol nmProxy = Mockito.mock(LocalizationProtocol.class);
                try {
                    Mockito.when(nmProxy.heartbeat(ArgumentMatchers.isA(LocalizerStatus.class))).thenReturn(new MockLocalizerHeartbeatResponse(LocalizerAction.DIE, new ArrayList<org.apache.hadoop.yarn.server.nodemanager.api.ResourceLocalizationSpec>()));
                } catch (YarnException e) {
                    throw new IOException(e);
                }
                Mockito.when(spyLocalizer.getProxy(ArgumentMatchers.any())).thenReturn(nmProxy);
                return spyLocalizer;
            }
        });
        mockExec.setConf(conf);
        localDirs.add(mockLfs.makeQualified(firstDir).toString());
        localDirs.add(mockLfs.makeQualified(secondDir).toString());
        logDirs.add(mockLfs.makeQualified(logDir).toString());
        conf.setStrings(NM_LOCAL_DIRS, localDirs.toArray(new String[localDirs.size()]));
        conf.set(NM_LOG_DIRS, logDir.toString());
        mockLfs.mkdir(tokenDir, perms, true);
        Path nmPrivateCTokensPath = new Path(tokenDir, "test.tokens");
        String appSubmitter = "nobody";
        String appId = "APP_ID";
        String locId = "LOC_ID";
        LocalDirsHandlerService dirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        Mockito.when(dirsHandler.getLocalDirs()).thenReturn(localDirs);
        Mockito.when(dirsHandler.getLogDirs()).thenReturn(logDirs);
        try {
            mockExec.startLocalizer(new LocalizerStartContext.Builder().setNmPrivateContainerTokens(nmPrivateCTokensPath).setNmAddr(null).setUser(appSubmitter).setAppId(appId).setLocId(locId).setDirsHandler(dirsHandler).build());
        } catch (IOException e) {
            Assert.fail(("StartLocalizer failed to copy token file: " + (StringUtils.stringifyException(e))));
        } finally {
            mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(firstDir).build());
            mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(secondDir).build());
            mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(logDir).build());
            TestDefaultContainerExecutor.deleteTmpFiles();
        }
        // Verify that the calls happen the expected number of times
        Mockito.verify(mockUtil, Mockito.times(1)).copy(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mockLfs, Mockito.times(2)).getFsStatus(ArgumentMatchers.any(Path.class));
    }

    @Test
    public void testPickDirectory() throws Exception {
        Configuration conf = new Configuration();
        FileContext lfs = FileContext.getLocalFSFileContext(conf);
        DefaultContainerExecutor executor = new DefaultContainerExecutor(lfs);
        long[] availableOnDisk = new long[2];
        availableOnDisk[0] = 100;
        availableOnDisk[1] = 100;
        Assert.assertEquals(0, executor.pickDirectory(0L, availableOnDisk));
        Assert.assertEquals(0, executor.pickDirectory(99L, availableOnDisk));
        Assert.assertEquals(1, executor.pickDirectory(100L, availableOnDisk));
        Assert.assertEquals(1, executor.pickDirectory(101L, availableOnDisk));
        Assert.assertEquals(1, executor.pickDirectory(199L, availableOnDisk));
        long[] availableOnDisk2 = new long[5];
        availableOnDisk2[0] = 100;
        availableOnDisk2[1] = 10;
        availableOnDisk2[2] = 400;
        availableOnDisk2[3] = 200;
        availableOnDisk2[4] = 350;
        Assert.assertEquals(0, executor.pickDirectory(0L, availableOnDisk2));
        Assert.assertEquals(0, executor.pickDirectory(99L, availableOnDisk2));
        Assert.assertEquals(1, executor.pickDirectory(100L, availableOnDisk2));
        Assert.assertEquals(1, executor.pickDirectory(105L, availableOnDisk2));
        Assert.assertEquals(2, executor.pickDirectory(110L, availableOnDisk2));
        Assert.assertEquals(2, executor.pickDirectory(259L, availableOnDisk2));
        Assert.assertEquals(3, executor.pickDirectory(700L, availableOnDisk2));
        Assert.assertEquals(4, executor.pickDirectory(710L, availableOnDisk2));
        Assert.assertEquals(4, executor.pickDirectory(910L, availableOnDisk2));
    }
}

