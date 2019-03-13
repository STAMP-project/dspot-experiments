/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.localizer;


import Config.STORM_BLOBSTORE_ACL_VALIDATION_ENABLED;
import Config.STORM_CLUSTER_MODE;
import Config.STORM_LOCAL_DIR;
import Config.STORM_PRINCIPAL_TO_LOCAL_PLUGIN;
import Config.TOPOLOGY_BLOBSTORE_MAP;
import Config.TOPOLOGY_NAME;
import DaemonConfig.SUPERVISOR_BLOBSTORE;
import DaemonConfig.SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS;
import LocalizedResource.FILECACHE;
import LocalizedResource.FILESDIR;
import com.codahale.metrics.Timer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;
import org.apache.storm.blobstore.BlobStoreAclHandler;
import org.apache.storm.blobstore.ClientBlobStore;
import org.apache.storm.blobstore.InputStreamWithMeta;
import org.apache.storm.blobstore.LocalFsBlobStore;
import org.apache.storm.daemon.supervisor.AdvancedFSOps;
import org.apache.storm.generated.AccessControl;
import org.apache.storm.generated.AccessControlType;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.ExecutorInfo;
import org.apache.storm.generated.KeyNotFoundException;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.generated.ReadableBlobMeta;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.security.auth.DefaultPrincipalToLocal;
import org.apache.storm.utils.ConfigUtils;
import org.apache.storm.utils.ReflectionUtils;
import org.apache.storm.utils.ServerUtils;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static LocalizedResource.BLOB_VERSION_SUFFIX;
import static LocalizedResource.CURRENT_BLOB_SUFFIX;


public class AsyncLocalizerTest {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncLocalizerTest.class);

    private final String user1 = "user1";

    private final String user2 = "user2";

    private final String user3 = "user3";

    // From LocalizerTest
    private File baseDir;

    private ClientBlobStore mockblobstore = Mockito.mock(ClientBlobStore.class);

    @Test
    public void testRequestDownloadBaseTopologyBlobs() throws Exception {
        final String topoId = "TOPO";
        final String user = "user";
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        la.set_owner(user);
        ExecutorInfo ei = new ExecutorInfo();
        ei.set_task_start(1);
        ei.set_task_end(1);
        la.add_to_executors(ei);
        final int port = 8080;
        final String stormLocal = "./target/DOWNLOAD-TEST/storm-local/";
        ClientBlobStore blobStore = Mockito.mock(ClientBlobStore.class);
        Map<String, Object> conf = new HashMap<>();
        conf.put(SUPERVISOR_BLOBSTORE, ClientBlobStore.class.getName());
        conf.put(STORM_PRINCIPAL_TO_LOCAL_PLUGIN, DefaultPrincipalToLocal.class.getName());
        conf.put(STORM_CLUSTER_MODE, "distributed");
        conf.put(STORM_LOCAL_DIR, stormLocal);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        ReflectionUtils mockedRU = Mockito.mock(ReflectionUtils.class);
        ServerUtils mockedU = Mockito.mock(ServerUtils.class);
        AsyncLocalizer bl = Mockito.spy(new AsyncLocalizer(conf, ops, AsyncLocalizerTest.getTestLocalizerRoot(), new StormMetricsRegistry()));
        LocallyCachedTopologyBlob jarBlob = Mockito.mock(LocallyCachedTopologyBlob.class);
        Mockito.doReturn(jarBlob).when(bl).getTopoJar(topoId, la.get_owner());
        Mockito.when(jarBlob.getLocalVersion()).thenReturn((-1L));
        Mockito.when(jarBlob.getRemoteVersion(ArgumentMatchers.any())).thenReturn(100L);
        Mockito.when(jarBlob.fetchUnzipToTemp(ArgumentMatchers.any())).thenReturn(100L);
        LocallyCachedTopologyBlob codeBlob = Mockito.mock(LocallyCachedTopologyBlob.class);
        Mockito.doReturn(codeBlob).when(bl).getTopoCode(topoId, la.get_owner());
        Mockito.when(codeBlob.getLocalVersion()).thenReturn((-1L));
        Mockito.when(codeBlob.getRemoteVersion(ArgumentMatchers.any())).thenReturn(200L);
        Mockito.when(codeBlob.fetchUnzipToTemp(ArgumentMatchers.any())).thenReturn(200L);
        LocallyCachedTopologyBlob confBlob = Mockito.mock(LocallyCachedTopologyBlob.class);
        Mockito.doReturn(confBlob).when(bl).getTopoConf(topoId, la.get_owner());
        Mockito.when(confBlob.getLocalVersion()).thenReturn((-1L));
        Mockito.when(confBlob.getRemoteVersion(ArgumentMatchers.any())).thenReturn(300L);
        Mockito.when(confBlob.fetchUnzipToTemp(ArgumentMatchers.any())).thenReturn(300L);
        ReflectionUtils origRU = ReflectionUtils.setInstance(mockedRU);
        ServerUtils origUtils = ServerUtils.setInstance(mockedU);
        try {
            Mockito.when(mockedRU.newInstanceImpl(ClientBlobStore.class)).thenReturn(blobStore);
            PortAndAssignment pna = new PortAndAssignmentImpl(port, la);
            Future<Void> f = bl.requestDownloadBaseTopologyBlobs(pna, null);
            f.get(20, TimeUnit.SECONDS);
            Mockito.verify(jarBlob).fetchUnzipToTemp(ArgumentMatchers.any());
            Mockito.verify(jarBlob).informAllOfChangeAndWaitForConsensus();
            Mockito.verify(jarBlob).commitNewVersion(100L);
            Mockito.verify(jarBlob).informAllChangeComplete();
            Mockito.verify(jarBlob).cleanupOrphanedData();
            Mockito.verify(codeBlob).fetchUnzipToTemp(ArgumentMatchers.any());
            Mockito.verify(codeBlob).informAllOfChangeAndWaitForConsensus();
            Mockito.verify(codeBlob).commitNewVersion(200L);
            Mockito.verify(codeBlob).informAllChangeComplete();
            Mockito.verify(codeBlob).cleanupOrphanedData();
            Mockito.verify(confBlob).fetchUnzipToTemp(ArgumentMatchers.any());
            Mockito.verify(confBlob).informAllOfChangeAndWaitForConsensus();
            Mockito.verify(confBlob).commitNewVersion(300L);
            Mockito.verify(confBlob).informAllChangeComplete();
            Mockito.verify(confBlob).cleanupOrphanedData();
        } finally {
            bl.close();
            ReflectionUtils.setInstance(origRU);
            ServerUtils.setInstance(origUtils);
        }
    }

    @Test
    public void testRequestDownloadTopologyBlobs() throws Exception {
        final String topoId = "TOPO-12345";
        final String user = "user";
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        la.set_owner(user);
        ExecutorInfo ei = new ExecutorInfo();
        ei.set_task_start(1);
        ei.set_task_end(1);
        la.add_to_executors(ei);
        final String topoName = "TOPO";
        final int port = 8080;
        final String simpleLocalName = "simple.txt";
        final String simpleKey = "simple";
        final String stormLocal = "/tmp/storm-local/";
        final File userDir = new File(stormLocal, user);
        final String stormRoot = (stormLocal + topoId) + "/";
        final String localizerRoot = AsyncLocalizerTest.getTestLocalizerRoot();
        final String simpleCurrentLocalFile = ((localizerRoot + "/usercache/") + user) + "/filecache/files/simple.current";
        final StormTopology st = new StormTopology();
        st.set_spouts(new HashMap());
        st.set_bolts(new HashMap());
        st.set_state_spouts(new HashMap());
        Map<String, Map<String, Object>> topoBlobMap = new HashMap<>();
        Map<String, Object> simple = new HashMap<>();
        simple.put("localname", simpleLocalName);
        simple.put("uncompress", false);
        topoBlobMap.put(simpleKey, simple);
        Map<String, Object> conf = new HashMap<>();
        conf.put(STORM_LOCAL_DIR, stormLocal);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        ConfigUtils mockedCU = Mockito.mock(ConfigUtils.class);
        Map<String, Object> topoConf = new HashMap<>(conf);
        topoConf.put(TOPOLOGY_BLOBSTORE_MAP, topoBlobMap);
        topoConf.put(TOPOLOGY_NAME, topoName);
        List<LocalizedResource> localizedList = new ArrayList<>();
        StormMetricsRegistry metricsRegistry = new StormMetricsRegistry();
        LocalizedResource simpleLocal = new LocalizedResource(simpleKey, Paths.get(localizerRoot), false, ops, conf, user, metricsRegistry);
        localizedList.add(simpleLocal);
        AsyncLocalizer bl = Mockito.spy(new AsyncLocalizer(conf, ops, localizerRoot, metricsRegistry));
        ConfigUtils orig = ConfigUtils.setInstance(mockedCU);
        try {
            Mockito.when(mockedCU.supervisorStormDistRootImpl(conf, topoId)).thenReturn(stormRoot);
            Mockito.when(mockedCU.readSupervisorStormConfImpl(conf, topoId)).thenReturn(topoConf);
            Mockito.when(mockedCU.readSupervisorTopologyImpl(conf, topoId, ops)).thenReturn(st);
            // Write the mocking backwards so the actual method is not called on the spy object
            Mockito.doReturn(CompletableFuture.supplyAsync(() -> null)).when(bl).requestDownloadBaseTopologyBlobs(ArgumentMatchers.any(), ArgumentMatchers.eq(null));
            Mockito.doReturn(userDir).when(bl).getLocalUserFileCacheDir(user);
            Mockito.doReturn(localizedList).when(bl).getBlobs(ArgumentMatchers.any(List.class), ArgumentMatchers.any(), ArgumentMatchers.any());
            Future<Void> f = bl.requestDownloadTopologyBlobs(la, port, null);
            f.get(20, TimeUnit.SECONDS);
            // We should be done now...
            Mockito.verify(bl).getLocalUserFileCacheDir(user);
            Mockito.verify(ops).fileExists(userDir);
            Mockito.verify(ops).forceMkdir(userDir);
            Mockito.verify(bl).getBlobs(ArgumentMatchers.any(List.class), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(ops).createSymlink(new File(stormRoot, simpleLocalName), new File(simpleCurrentLocalFile));
        } finally {
            try {
                ConfigUtils.setInstance(orig);
                bl.close();
            } catch (Throwable e) {
                AsyncLocalizerTest.LOG.error("ERROR trying to close an object", e);
            }
        }
    }

    @Test
    public void testDirPaths() throws Exception {
        Map<String, Object> conf = new HashMap();
        AsyncLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
        String expectedDir = constructUserCacheDir(baseDir.toString(), user1);
        Assert.assertEquals("get local user dir doesn't return right value", expectedDir, localizer.getLocalUserDir(user1).toString());
        String expectedFileDir = joinPath(expectedDir, FILECACHE);
        Assert.assertEquals("get local user file dir doesn't return right value", expectedFileDir, localizer.getLocalUserFileCacheDir(user1).toString());
    }

    @Test
    public void testReconstruct() throws Exception {
        Map<String, Object> conf = new HashMap<>();
        String expectedFileDir1 = constructExpectedFilesDir(baseDir.toString(), user1);
        String expectedArchiveDir1 = constructExpectedArchivesDir(baseDir.toString(), user1);
        String expectedFileDir2 = constructExpectedFilesDir(baseDir.toString(), user2);
        String expectedArchiveDir2 = constructExpectedArchivesDir(baseDir.toString(), user2);
        String key1 = "testfile1.txt";
        String key2 = "testfile2.txt";
        String key3 = "testfile3.txt";
        String key4 = "testfile4.txt";
        String archive1 = "archive1";
        String archive2 = "archive2";
        File user1file1 = new File(expectedFileDir1, (key1 + (CURRENT_BLOB_SUFFIX)));
        File user1file2 = new File(expectedFileDir1, (key2 + (CURRENT_BLOB_SUFFIX)));
        File user2file3 = new File(expectedFileDir2, (key3 + (CURRENT_BLOB_SUFFIX)));
        File user2file4 = new File(expectedFileDir2, (key4 + (CURRENT_BLOB_SUFFIX)));
        File user1archive1 = new File(expectedArchiveDir1, (archive1 + (CURRENT_BLOB_SUFFIX)));
        File user2archive2 = new File(expectedArchiveDir2, (archive2 + (CURRENT_BLOB_SUFFIX)));
        File user1archive1file = new File(user1archive1, "file1");
        File user2archive2file = new File(user2archive2, "file2");
        // setup some files/dirs to emulate supervisor restart
        Assert.assertTrue("Failed setup filecache dir1", new File(expectedFileDir1).mkdirs());
        Assert.assertTrue("Failed setup filecache dir2", new File(expectedFileDir2).mkdirs());
        Assert.assertTrue("Failed setup file1", user1file1.createNewFile());
        Assert.assertTrue("Failed setup file2", user1file2.createNewFile());
        Assert.assertTrue("Failed setup file3", user2file3.createNewFile());
        Assert.assertTrue("Failed setup file4", user2file4.createNewFile());
        Assert.assertTrue("Failed setup archive dir1", user1archive1.mkdirs());
        Assert.assertTrue("Failed setup archive dir2", user2archive2.mkdirs());
        Assert.assertTrue("Failed setup file in archivedir1", user1archive1file.createNewFile());
        Assert.assertTrue("Failed setup file in archivedir2", user2archive2file.createNewFile());
        AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
        ArrayList<LocalResource> arrUser1Keys = new ArrayList<>();
        arrUser1Keys.add(new LocalResource(key1, false, false));
        arrUser1Keys.add(new LocalResource(archive1, true, false));
        LocalAssignment topo1 = new LocalAssignment("topo1", Collections.emptyList());
        topo1.set_owner(user1);
        localizer.addReferences(arrUser1Keys, new PortAndAssignmentImpl(1, topo1), null);
        ConcurrentMap<String, LocalizedResource> lrsrcFiles = localizer.getUserFiles().get(user1);
        ConcurrentMap<String, LocalizedResource> lrsrcArchives = localizer.getUserArchives().get(user1);
        Assert.assertEquals("local resource set size wrong", 3, ((lrsrcFiles.size()) + (lrsrcArchives.size())));
        LocalizedResource key1rsrc = lrsrcFiles.get(key1);
        Assert.assertNotNull("Local resource doesn't exist but should", key1rsrc);
        Assert.assertEquals("key doesn't match", key1, key1rsrc.getKey());
        Assert.assertEquals(("references doesn't match " + (key1rsrc.getDependencies())), true, key1rsrc.isUsed());
        LocalizedResource key2rsrc = lrsrcFiles.get(key2);
        Assert.assertNotNull("Local resource doesn't exist but should", key2rsrc);
        Assert.assertEquals("key doesn't match", key2, key2rsrc.getKey());
        Assert.assertEquals(("refcount doesn't match " + (key2rsrc.getDependencies())), false, key2rsrc.isUsed());
        LocalizedResource archive1rsrc = lrsrcArchives.get(archive1);
        Assert.assertNotNull("Local resource doesn't exist but should", archive1rsrc);
        Assert.assertEquals("key doesn't match", archive1, archive1rsrc.getKey());
        Assert.assertEquals(("refcount doesn't match " + (archive1rsrc.getDependencies())), true, archive1rsrc.isUsed());
        ConcurrentMap<String, LocalizedResource> lrsrcFiles2 = localizer.getUserFiles().get(user2);
        ConcurrentMap<String, LocalizedResource> lrsrcArchives2 = localizer.getUserArchives().get(user2);
        Assert.assertEquals("local resource set size wrong", 3, ((lrsrcFiles2.size()) + (lrsrcArchives2.size())));
        LocalizedResource key3rsrc = lrsrcFiles2.get(key3);
        Assert.assertNotNull("Local resource doesn't exist but should", key3rsrc);
        Assert.assertEquals("key doesn't match", key3, key3rsrc.getKey());
        Assert.assertEquals(("refcount doesn't match " + (key3rsrc.getDependencies())), false, key3rsrc.isUsed());
        LocalizedResource key4rsrc = lrsrcFiles2.get(key4);
        Assert.assertNotNull("Local resource doesn't exist but should", key4rsrc);
        Assert.assertEquals("key doesn't match", key4, key4rsrc.getKey());
        Assert.assertEquals(("refcount doesn't match " + (key4rsrc.getDependencies())), false, key4rsrc.isUsed());
        LocalizedResource archive2rsrc = lrsrcArchives2.get(archive2);
        Assert.assertNotNull("Local resource doesn't exist but should", archive2rsrc);
        Assert.assertEquals("key doesn't match", archive2, archive2rsrc.getKey());
        Assert.assertEquals(("refcount doesn't match " + (archive2rsrc.getDependencies())), false, archive2rsrc.isUsed());
    }

    @Test
    public void testArchivesTgz() throws Exception {
        testArchives(getFileFromResource(joinPath("localizer", "localtestwithsymlink.tgz")), true, 21344);
    }

    @Test
    public void testArchivesZip() throws Exception {
        testArchives(getFileFromResource(joinPath("localizer", "localtest.zip")), false, 21348);
    }

    @Test
    public void testArchivesTarGz() throws Exception {
        testArchives(getFileFromResource(joinPath("localizer", "localtestwithsymlink.tar.gz")), true, 21344);
    }

    @Test
    public void testArchivesTar() throws Exception {
        testArchives(getFileFromResource(joinPath("localizer", "localtestwithsymlink.tar")), true, 21344);
    }

    @Test
    public void testArchivesJar() throws Exception {
        testArchives(getFileFromResource(joinPath("localizer", "localtestwithsymlink.jar")), false, 21416);
    }

    @Test
    public void testBasic() throws Exception {
        try (Time.SimulatedTime st = new Time.SimulatedTime()) {
            Map<String, Object> conf = new HashMap();
            // set clean time really high so doesn't kick in
            conf.put(SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS, ((60 * 60) * 1000));
            String key1 = "key1";
            String topo1 = "topo1";
            AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
            // set really small so will do cleanup
            localizer.setTargetCacheSize(1);
            ReadableBlobMeta rbm = new ReadableBlobMeta();
            rbm.set_settable(new org.apache.storm.generated.SettableBlobMeta(BlobStoreAclHandler.WORLD_EVERYTHING));
            Mockito.when(mockblobstore.getBlobMeta(key1)).thenReturn(rbm);
            Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(1));
            long timeBefore = Time.currentTimeMillis();
            Time.advanceTime(10);
            File user1Dir = getLocalUserFileCacheDir(user1);
            Assert.assertTrue("failed to create user dir", user1Dir.mkdirs());
            Time.advanceTime(10);
            LocalAssignment topo1Assignment = new LocalAssignment(topo1, Collections.emptyList());
            topo1Assignment.set_owner(user1);
            PortAndAssignment topo1Pna = new PortAndAssignmentImpl(1, topo1Assignment);
            LocalizedResource lrsrc = localizer.getBlob(new LocalResource(key1, false, false), topo1Pna, null);
            long timeAfter = Time.currentTimeMillis();
            Time.advanceTime(10);
            String expectedUserDir = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user1);
            String expectedFileDir = joinPath(expectedUserDir, FILECACHE, FILESDIR);
            Assert.assertTrue("user filecache dir not created", new File(expectedFileDir).exists());
            File keyFile = new File(expectedFileDir, (key1 + ".current"));
            File keyFileCurrentSymlink = new File(expectedFileDir, (key1 + (CURRENT_BLOB_SUFFIX)));
            Assert.assertTrue("blob not created", keyFileCurrentSymlink.exists());
            ConcurrentMap<String, LocalizedResource> lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertEquals("local resource set size wrong", 1, lrsrcSet.size());
            LocalizedResource key1rsrc = lrsrcSet.get(key1);
            Assert.assertNotNull("Local resource doesn't exist but should", key1rsrc);
            Assert.assertEquals("key doesn't match", key1, key1rsrc.getKey());
            Assert.assertEquals(("refcount doesn't match " + (key1rsrc.getDependencies())), true, key1rsrc.isUsed());
            Assert.assertEquals("file path doesn't match", keyFile.toPath(), key1rsrc.getCurrentSymlinkPath());
            Assert.assertEquals("size doesn't match", 34, key1rsrc.getSizeOnDisk());
            Assert.assertTrue("timestamp not within range", (((key1rsrc.getLastUsed()) >= timeBefore) && ((key1rsrc.getLastUsed()) <= timeAfter)));
            timeBefore = Time.currentTimeMillis();
            Time.advanceTime(10);
            localizer.removeBlobReference(lrsrc.getKey(), topo1Pna, false);
            Time.advanceTime(10);
            timeAfter = Time.currentTimeMillis();
            Time.advanceTime(10);
            lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertEquals("local resource set size wrong", 1, lrsrcSet.size());
            key1rsrc = lrsrcSet.get(key1);
            Assert.assertNotNull("Local resource doesn't exist but should", key1rsrc);
            Assert.assertEquals(("refcount doesn't match " + (key1rsrc.getDependencies())), false, key1rsrc.isUsed());
            Assert.assertTrue(((((("timestamp not within range " + timeBefore) + " ") + (key1rsrc.getLastUsed())) + " ") + timeAfter), (((key1rsrc.getLastUsed()) >= timeBefore) && ((key1rsrc.getLastUsed()) <= timeAfter)));
            // should remove the blob since cache size set really small
            cleanup();
            lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertNull("user set should be null", lrsrcSet);
            Assert.assertFalse("blob not deleted", keyFile.exists());
            Assert.assertFalse("blob dir not deleted", new File(expectedFileDir).exists());
            Assert.assertFalse("blob dir not deleted", new File(expectedUserDir).exists());
        }
    }

    @Test
    public void testMultipleKeysOneUser() throws Exception {
        try (Time.SimulatedTime st = new Time.SimulatedTime()) {
            Map<String, Object> conf = new HashMap<>();
            // set clean time really high so doesn't kick in
            conf.put(SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS, ((60 * 60) * 1000));
            String key1 = "key1";
            String topo1 = "topo1";
            String key2 = "key2";
            String key3 = "key3";
            AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
            // set to keep 2 blobs (each of size 34)
            localizer.setTargetCacheSize(68);
            ReadableBlobMeta rbm = new ReadableBlobMeta();
            rbm.set_settable(new org.apache.storm.generated.SettableBlobMeta(BlobStoreAclHandler.WORLD_EVERYTHING));
            Mockito.when(mockblobstore.getBlobMeta(ArgumentMatchers.anyString())).thenReturn(rbm);
            Mockito.when(mockblobstore.isRemoteBlobExists(ArgumentMatchers.anyString())).thenReturn(true);
            Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(0));
            Mockito.when(mockblobstore.getBlob(key2)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(0));
            Mockito.when(mockblobstore.getBlob(key3)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(0));
            List<LocalResource> keys = Arrays.asList(new LocalResource(key1, false, false), new LocalResource(key2, false, false), new LocalResource(key3, false, false));
            File user1Dir = getLocalUserFileCacheDir(user1);
            Assert.assertTrue("failed to create user dir", user1Dir.mkdirs());
            LocalAssignment topo1Assignment = new LocalAssignment(topo1, Collections.emptyList());
            topo1Assignment.set_owner(user1);
            PortAndAssignment topo1Pna = new PortAndAssignmentImpl(1, topo1Assignment);
            List<LocalizedResource> lrsrcs = localizer.getBlobs(keys, topo1Pna, null);
            LocalizedResource lrsrc = lrsrcs.get(0);
            LocalizedResource lrsrc2 = lrsrcs.get(1);
            LocalizedResource lrsrc3 = lrsrcs.get(2);
            String expectedFileDir = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user1, FILECACHE, FILESDIR);
            Assert.assertTrue("user filecache dir not created", new File(expectedFileDir).exists());
            File keyFile = new File(expectedFileDir, (key1 + (CURRENT_BLOB_SUFFIX)));
            File keyFile2 = new File(expectedFileDir, (key2 + (CURRENT_BLOB_SUFFIX)));
            File keyFile3 = new File(expectedFileDir, (key3 + (CURRENT_BLOB_SUFFIX)));
            Assert.assertTrue("blob not created", keyFile.exists());
            Assert.assertTrue("blob not created", keyFile2.exists());
            Assert.assertTrue("blob not created", keyFile3.exists());
            Assert.assertEquals("size doesn't match", 34, keyFile.length());
            Assert.assertEquals("size doesn't match", 34, keyFile2.length());
            Assert.assertEquals("size doesn't match", 34, keyFile3.length());
            Assert.assertEquals("size doesn't match", 34, lrsrc.getSizeOnDisk());
            Assert.assertEquals("size doesn't match", 34, lrsrc3.getSizeOnDisk());
            Assert.assertEquals("size doesn't match", 34, lrsrc2.getSizeOnDisk());
            ConcurrentMap<String, LocalizedResource> lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertEquals("local resource set size wrong", 3, lrsrcSet.size());
            AsyncLocalizerTest.LOG.info("Removing blob references...");
            long timeBefore = Time.nanoTime();
            Time.advanceTime(10);
            localizer.removeBlobReference(lrsrc.getKey(), topo1Pna, false);
            Time.advanceTime(10);
            localizer.removeBlobReference(lrsrc2.getKey(), topo1Pna, false);
            Time.advanceTime(10);
            localizer.removeBlobReference(lrsrc3.getKey(), topo1Pna, false);
            Time.advanceTime(10);
            long timeAfter = Time.nanoTime();
            AsyncLocalizerTest.LOG.info("Done removing blob references...");
            // add reference to one and then remove reference again so it has newer timestamp
            AsyncLocalizerTest.LOG.info("Get Blob...");
            lrsrc = localizer.getBlob(new LocalResource(key1, false, false), topo1Pna, null);
            AsyncLocalizerTest.LOG.info("Got Blob...");
            Assert.assertTrue(((((("timestamp not within range " + timeBefore) + " <= ") + (lrsrc.getLastUsed())) + " <= ") + timeAfter), (((lrsrc.getLastUsed()) >= timeBefore) && ((lrsrc.getLastUsed()) <= timeAfter)));
            // Resets the last access time for key1
            localizer.removeBlobReference(lrsrc.getKey(), topo1Pna, false);
            // should remove the second blob first
            cleanup();
            lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertEquals("local resource set size wrong", 2, lrsrcSet.size());
            long end = (System.currentTimeMillis()) + 100;
            while (((end - (System.currentTimeMillis())) >= 0) && (keyFile2.exists())) {
                Thread.sleep(1);
            } 
            Assert.assertTrue("blob deleted", keyFile.exists());
            Assert.assertFalse("blob not deleted", keyFile2.exists());
            Assert.assertTrue("blob deleted", keyFile3.exists());
            // set size to cleanup another one
            localizer.setTargetCacheSize(34);
            // should remove the third blob, because the first has the reset timestamp
            cleanup();
            lrsrcSet = localizer.getUserFiles().get(user1);
            Assert.assertEquals("local resource set size wrong", 1, lrsrcSet.size());
            Assert.assertTrue("blob deleted", keyFile.exists());
            Assert.assertFalse("blob not deleted", keyFile2.exists());
            Assert.assertFalse("blob not deleted", keyFile3.exists());
        }
    }

    @Test(expected = AuthorizationException.class)
    public void testFailAcls() throws Exception {
        Map<String, Object> conf = new HashMap();
        // set clean time really high so doesn't kick in
        conf.put(SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS, ((60 * 60) * 1000));
        // enable blobstore acl validation
        conf.put(STORM_BLOBSTORE_ACL_VALIDATION_ENABLED, true);
        String topo1 = "topo1";
        String key1 = "key1";
        AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
        ReadableBlobMeta rbm = new ReadableBlobMeta();
        // set acl so user doesn't have read access
        AccessControl acl = new AccessControl(AccessControlType.USER, BlobStoreAclHandler.ADMIN);
        acl.set_name(user1);
        rbm.set_settable(new org.apache.storm.generated.SettableBlobMeta(Arrays.asList(acl)));
        Mockito.when(mockblobstore.getBlobMeta(ArgumentMatchers.anyString())).thenReturn(rbm);
        Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(1));
        File user1Dir = getLocalUserFileCacheDir(user1);
        Assert.assertTrue("failed to create user dir", user1Dir.mkdirs());
        LocalAssignment topo1Assignment = new LocalAssignment(topo1, Collections.emptyList());
        topo1Assignment.set_owner(user1);
        PortAndAssignment topo1Pna = new PortAndAssignmentImpl(1, topo1Assignment);
        // This should throw AuthorizationException because auth failed
        localizer.getBlob(new LocalResource(key1, false, false), topo1Pna, null);
    }

    @Test(expected = KeyNotFoundException.class)
    public void testKeyNotFoundException() throws Exception {
        Map<String, Object> conf = Utils.readStormConfig();
        String key1 = "key1";
        conf.put(STORM_LOCAL_DIR, "target");
        LocalFsBlobStore bs = new LocalFsBlobStore();
        LocalFsBlobStore spy = Mockito.spy(bs);
        Mockito.doReturn(true).when(spy).checkForBlobOrDownload(key1);
        Mockito.doNothing().when(spy).checkForBlobUpdate(key1);
        spy.prepare(conf, null, null, null);
        spy.getBlob(key1, null);
    }

    @Test
    public void testMultipleUsers() throws Exception {
        Map<String, Object> conf = new HashMap<>();
        // set clean time really high so doesn't kick in
        conf.put(SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS, ((60 * 60) * 1000));
        String topo1 = "topo1";
        String topo2 = "topo2";
        String topo3 = "topo3";
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
        // set to keep 2 blobs (each of size 34)
        localizer.setTargetCacheSize(68);
        ReadableBlobMeta rbm = new ReadableBlobMeta();
        rbm.set_settable(new org.apache.storm.generated.SettableBlobMeta(BlobStoreAclHandler.WORLD_EVERYTHING));
        Mockito.when(mockblobstore.getBlobMeta(ArgumentMatchers.anyString())).thenReturn(rbm);
        // thenReturn always returns the same object, which is already consumed by the time User3 tries to getBlob!
        Mockito.when(mockblobstore.getBlob(key1)).thenAnswer(( i) -> new org.apache.storm.localizer.TestInputStreamWithMeta(1));
        Mockito.when(mockblobstore.getBlob(key2)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(1));
        Mockito.when(mockblobstore.getBlob(key3)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(1));
        File user1Dir = getLocalUserFileCacheDir(user1);
        Assert.assertTrue("failed to create user dir", user1Dir.mkdirs());
        File user2Dir = getLocalUserFileCacheDir(user2);
        Assert.assertTrue("failed to create user dir", user2Dir.mkdirs());
        File user3Dir = getLocalUserFileCacheDir(user3);
        Assert.assertTrue("failed to create user dir", user3Dir.mkdirs());
        LocalAssignment topo1Assignment = new LocalAssignment(topo1, Collections.emptyList());
        topo1Assignment.set_owner(user1);
        PortAndAssignment topo1Pna = new PortAndAssignmentImpl(1, topo1Assignment);
        LocalizedResource lrsrc = localizer.getBlob(new LocalResource(key1, false, false), topo1Pna, null);
        LocalAssignment topo2Assignment = new LocalAssignment(topo2, Collections.emptyList());
        topo2Assignment.set_owner(user2);
        PortAndAssignment topo2Pna = new PortAndAssignmentImpl(2, topo2Assignment);
        LocalizedResource lrsrc2 = localizer.getBlob(new LocalResource(key2, false, false), topo2Pna, null);
        LocalAssignment topo3Assignment = new LocalAssignment(topo3, Collections.emptyList());
        topo3Assignment.set_owner(user3);
        PortAndAssignment topo3Pna = new PortAndAssignmentImpl(3, topo3Assignment);
        LocalizedResource lrsrc3 = localizer.getBlob(new LocalResource(key3, false, false), topo3Pna, null);
        // make sure we support different user reading same blob
        LocalizedResource lrsrc1_user3 = localizer.getBlob(new LocalResource(key1, false, false), topo3Pna, null);
        String expectedUserDir1 = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user1);
        String expectedFileDirUser1 = joinPath(expectedUserDir1, FILECACHE, FILESDIR);
        String expectedFileDirUser2 = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user2, FILECACHE, FILESDIR);
        String expectedFileDirUser3 = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user3, FILECACHE, FILESDIR);
        Assert.assertTrue("user filecache dir user1 not created", new File(expectedFileDirUser1).exists());
        Assert.assertTrue("user filecache dir user2 not created", new File(expectedFileDirUser2).exists());
        Assert.assertTrue("user filecache dir user3 not created", new File(expectedFileDirUser3).exists());
        File keyFile = new File(expectedFileDirUser1, (key1 + (CURRENT_BLOB_SUFFIX)));
        File keyFile2 = new File(expectedFileDirUser2, (key2 + (CURRENT_BLOB_SUFFIX)));
        File keyFile3 = new File(expectedFileDirUser3, (key3 + (CURRENT_BLOB_SUFFIX)));
        File keyFile1user3 = new File(expectedFileDirUser3, (key1 + (CURRENT_BLOB_SUFFIX)));
        Assert.assertTrue("blob not created", keyFile.exists());
        Assert.assertTrue("blob not created", keyFile2.exists());
        Assert.assertTrue("blob not created", keyFile3.exists());
        Assert.assertTrue("blob not created", keyFile1user3.exists());
        // Should assert file size
        Assert.assertEquals("size doesn't match", 34, lrsrc.getSizeOnDisk());
        Assert.assertEquals("size doesn't match", 34, lrsrc2.getSizeOnDisk());
        Assert.assertEquals("size doesn't match", 34, lrsrc3.getSizeOnDisk());
        // This was 0 byte in test
        Assert.assertEquals("size doesn't match", 34, lrsrc1_user3.getSizeOnDisk());
        ConcurrentMap<String, LocalizedResource> lrsrcSet = localizer.getUserFiles().get(user1);
        Assert.assertEquals("local resource set size wrong", 1, lrsrcSet.size());
        ConcurrentMap<String, LocalizedResource> lrsrcSet2 = localizer.getUserFiles().get(user2);
        Assert.assertEquals("local resource set size wrong", 1, lrsrcSet2.size());
        ConcurrentMap<String, LocalizedResource> lrsrcSet3 = localizer.getUserFiles().get(user3);
        Assert.assertEquals("local resource set size wrong", 2, lrsrcSet3.size());
        localizer.removeBlobReference(lrsrc.getKey(), topo1Pna, false);
        // should remove key1
        cleanup();
        lrsrcSet = localizer.getUserFiles().get(user1);
        lrsrcSet3 = localizer.getUserFiles().get(user3);
        Assert.assertNull("user set should be null", lrsrcSet);
        Assert.assertFalse("blob dir not deleted", new File(expectedFileDirUser1).exists());
        Assert.assertFalse("blob dir not deleted", new File(expectedUserDir1).exists());
        Assert.assertEquals("local resource set size wrong", 2, lrsrcSet3.size());
        Assert.assertTrue("blob deleted", keyFile2.exists());
        Assert.assertFalse("blob not deleted", keyFile.exists());
        Assert.assertTrue("blob deleted", keyFile3.exists());
        Assert.assertTrue("blob deleted", keyFile1user3.exists());
    }

    @Test
    public void testUpdate() throws Exception {
        Map<String, Object> conf = new HashMap<>();
        // set clean time really high so doesn't kick in
        conf.put(SUPERVISOR_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS, ((60 * 60) * 1000));
        String key1 = "key1";
        String topo1 = "topo1";
        String topo2 = "topo2";
        AsyncLocalizerTest.TestLocalizer localizer = new AsyncLocalizerTest.TestLocalizer(conf, baseDir.toString());
        ReadableBlobMeta rbm = new ReadableBlobMeta();
        rbm.set_version(1);
        rbm.set_settable(new org.apache.storm.generated.SettableBlobMeta(BlobStoreAclHandler.WORLD_EVERYTHING));
        Mockito.when(mockblobstore.getBlobMeta(key1)).thenReturn(rbm);
        Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(1));
        File user1Dir = getLocalUserFileCacheDir(user1);
        Assert.assertTrue("failed to create user dir", user1Dir.mkdirs());
        LocalAssignment topo1Assignment = new LocalAssignment(topo1, Collections.emptyList());
        topo1Assignment.set_owner(user1);
        PortAndAssignment topo1Pna = new PortAndAssignmentImpl(1, topo1Assignment);
        LocalizedResource lrsrc = localizer.getBlob(new LocalResource(key1, false, false), topo1Pna, null);
        String expectedUserDir = joinPath(baseDir.toString(), LocalizedResource.USERCACHE, user1);
        String expectedFileDir = joinPath(expectedUserDir, FILECACHE, FILESDIR);
        Assert.assertTrue("user filecache dir not created", new File(expectedFileDir).exists());
        Path keyVersionFile = Paths.get(expectedFileDir, (key1 + ".version"));
        File keyFileCurrentSymlink = new File(expectedFileDir, (key1 + (CURRENT_BLOB_SUFFIX)));
        Assert.assertTrue("blob not created", keyFileCurrentSymlink.exists());
        File versionFile = new File(expectedFileDir, (key1 + (BLOB_VERSION_SUFFIX)));
        Assert.assertTrue("blob version file not created", versionFile.exists());
        Assert.assertEquals("blob version not correct", 1, LocalizedResource.localVersionOfBlob(keyVersionFile));
        ConcurrentMap<String, LocalizedResource> lrsrcSet = localizer.getUserFiles().get(user1);
        Assert.assertEquals("local resource set size wrong", 1, lrsrcSet.size());
        // test another topology getting blob with updated version - it should update version now
        rbm.set_version(2);
        Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(2));
        LocalAssignment topo2Assignment = new LocalAssignment(topo2, Collections.emptyList());
        topo2Assignment.set_owner(user1);
        PortAndAssignment topo2Pna = new PortAndAssignmentImpl(1, topo2Assignment);
        localizer.getBlob(new LocalResource(key1, false, false), topo2Pna, null);
        Assert.assertTrue("blob version file not created", versionFile.exists());
        Assert.assertEquals("blob version not correct", 2, LocalizedResource.localVersionOfBlob(keyVersionFile));
        Assert.assertTrue("blob file with version 2 not created", new File(expectedFileDir, (key1 + ".2")).exists());
        // now test regular updateBlob
        rbm.set_version(3);
        Mockito.when(mockblobstore.getBlob(key1)).thenReturn(new AsyncLocalizerTest.TestInputStreamWithMeta(3));
        ArrayList<LocalResource> arr = new ArrayList<>();
        arr.add(new LocalResource(key1, false, false));
        updateBlobs();
        Assert.assertTrue("blob version file not created", versionFile.exists());
        Assert.assertEquals("blob version not correct", 3, LocalizedResource.localVersionOfBlob(keyVersionFile));
        Assert.assertTrue("blob file with version 3 not created", new File(expectedFileDir, (key1 + ".3")).exists());
    }

    @Test
    public void validatePNAImplementationsMatch() {
        LocalAssignment la = new LocalAssignment("Topology1", null);
        PortAndAssignment pna = new PortAndAssignmentImpl(1, la);
        PortAndAssignment tpna = new TimePortAndAssignment(pna, new Timer());
        Assert.assertTrue(pna.equals(tpna));
        Assert.assertTrue(tpna.equals(pna));
        Assert.assertTrue(((pna.hashCode()) == (tpna.hashCode())));
    }

    class TestLocalizer extends AsyncLocalizer {
        TestLocalizer(Map<String, Object> conf, String baseDir) throws IOException {
            super(conf, AdvancedFSOps.make(conf), baseDir, new StormMetricsRegistry());
        }

        @Override
        protected ClientBlobStore getClientBlobStore() {
            return mockblobstore;
        }

        synchronized void addReferences(List<LocalResource> localresource, PortAndAssignment pna, BlobChangingCallback cb) {
            String user = pna.getOwner();
            for (LocalResource blob : localresource) {
                ConcurrentMap<String, LocalizedResource> lrsrcSet = (blob.shouldUncompress()) ? userArchives.get(user) : userFiles.get(user);
                if (lrsrcSet != null) {
                    LocalizedResource lrsrc = lrsrcSet.get(blob.getBlobName());
                    if (lrsrc != null) {
                        lrsrc.addReference(pna, (blob.needsCallback() ? cb : null));
                        AsyncLocalizerTest.LOG.debug("added reference for topo: {} key: {}", pna, blob);
                    } else {
                        AsyncLocalizerTest.LOG.warn("trying to add reference to non-existent blob, key: {} topo: {}", blob, pna);
                    }
                } else {
                    AsyncLocalizerTest.LOG.warn("trying to add reference to non-existent local resource set, user: {} topo: {}", user, pna);
                }
            }
        }

        void setTargetCacheSize(long size) {
            cacheTargetSize = size;
        }

        // For testing, be careful as it doesn't clone
        ConcurrentHashMap<String, ConcurrentHashMap<String, LocalizedResource>> getUserFiles() {
            return userFiles;
        }

        ConcurrentHashMap<String, ConcurrentHashMap<String, LocalizedResource>> getUserArchives() {
            return userArchives;
        }

        /**
         * This function either returns the blob in the existing cache or if it doesn't exist in the
         * cache, it will download the blob and will block until the download is complete.
         */
        LocalizedResource getBlob(LocalResource localResource, PortAndAssignment pna, BlobChangingCallback cb) throws IOException, AuthorizationException, KeyNotFoundException {
            ArrayList<LocalResource> arr = new ArrayList<>();
            arr.add(localResource);
            List<LocalizedResource> results = getBlobs(arr, pna, cb);
            if ((results.isEmpty()) || ((results.size()) != 1)) {
                throw new IOException(((((("Unknown error getting blob: " + localResource) + ", for user: ") + (pna.getOwner())) + ", topo: ") + pna));
            }
            return results.get(0);
        }
    }

    class TestInputStreamWithMeta extends InputStreamWithMeta {
        private final long version;

        private final long fileLength;

        private InputStream iostream;

        public TestInputStreamWithMeta(long version) {
            final String DEFAULT_DATA = "some test data for my input stream";
            iostream = IOUtils.toInputStream(DEFAULT_DATA);
            this.version = version;
            this.fileLength = DEFAULT_DATA.length();
        }

        public TestInputStreamWithMeta(InputStream istream, long version, long fileLength) {
            iostream = istream;
            this.version = version;
            this.fileLength = fileLength;
        }

        @Override
        public long getVersion() throws IOException {
            return version;
        }

        @Override
        public synchronized int read() {
            return 0;
        }

        @Override
        public synchronized int read(byte[] b) throws IOException {
            int length = iostream.read(b);
            if (length == 0) {
                return -1;
            }
            return length;
        }

        @Override
        public long getFileLength() {
            return fileLength;
        }
    }
}

