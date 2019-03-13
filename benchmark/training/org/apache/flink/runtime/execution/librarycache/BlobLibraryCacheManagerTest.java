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
package org.apache.flink.runtime.execution.librarycache;


import BlobServerOptions.CLEANUP_INTERVAL;
import BlobServerOptions.STORAGE_DIRECTORY;
import FlinkUserCodeClassLoaders.ResolveOrder;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.blob.BlobServerCleanupTest;
import org.apache.flink.runtime.blob.PermanentBlobCache;
import org.apache.flink.runtime.blob.PermanentBlobKey;
import org.apache.flink.runtime.blob.VoidBlobStore;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.util.OperatingSystem;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link BlobLibraryCacheManager}.
 */
public class BlobLibraryCacheManagerTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that the {@link BlobLibraryCacheManager} cleans up after calling {@link BlobLibraryCacheManager#unregisterJob(JobID)}.
     */
    @Test
    public void testLibraryCacheManagerJobCleanup() throws IOException, InterruptedException {
        JobID jobId1 = new JobID();
        JobID jobId2 = new JobID();
        List<PermanentBlobKey> keys1 = new ArrayList<>();
        List<PermanentBlobKey> keys2 = new ArrayList<>();
        BlobServer server = null;
        PermanentBlobCache cache = null;
        BlobLibraryCacheManager libCache = null;
        final byte[] buf = new byte[128];
        try {
            Configuration config = new Configuration();
            config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
            config.setLong(CLEANUP_INTERVAL, 1L);
            server = new BlobServer(config, new VoidBlobStore());
            server.start();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", server.getPort());
            cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
            keys1.add(server.putPermanent(jobId1, buf));
            buf[0] += 1;
            keys1.add(server.putPermanent(jobId1, buf));
            keys2.add(server.putPermanent(jobId2, buf));
            libCache = new BlobLibraryCacheManager(cache, ResolveOrder.CHILD_FIRST, new String[0]);
            cache.registerJob(jobId1);
            cache.registerJob(jobId2);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId1));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId1, cache);
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId2, cache);
            libCache.registerJob(jobId1, keys1, Collections.<URL>emptyList());
            ClassLoader classLoader1 = libCache.getClassLoader(jobId1);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId1));
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId2));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId1, keys1, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, cache);
            Assert.assertEquals(0, BlobServerCleanupTest.checkFilesExist(jobId2, keys2, cache, false));
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId2, cache);
            libCache.registerJob(jobId2, keys2, Collections.<URL>emptyList());
            ClassLoader classLoader2 = libCache.getClassLoader(jobId2);
            Assert.assertNotEquals(classLoader1, classLoader2);
            try {
                libCache.registerJob(jobId2, keys1, Collections.<URL>emptyList());
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            try {
                libCache.registerJob(jobId2, keys2, Collections.singletonList(new URL("file:///tmp/does-not-exist")));
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            Assert.assertEquals(2, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId1));
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId2));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId1, keys1, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, cache);
            Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId2, keys2, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, server);
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, cache);
            libCache.unregisterJob(jobId1);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId1));
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId2));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId1, keys1, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, cache);
            Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId2, keys2, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, server);
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, cache);
            libCache.unregisterJob(jobId2);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId1));
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId2));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId1, keys1, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId1, cache);
            Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId2, keys2, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, server);
            BlobServerCleanupTest.checkFileCountForJob(1, jobId2, cache);
            // only PermanentBlobCache#releaseJob() calls clean up files (tested in BlobCacheCleanupTest etc.
        } finally {
            if (libCache != null) {
                libCache.shutdown();
            }
            // should have been closed by the libraryCacheManager, but just in case
            if (cache != null) {
                cache.close();
            }
            if (server != null) {
                server.close();
            }
        }
    }

    /**
     * Tests that the {@link BlobLibraryCacheManager} cleans up after calling {@link BlobLibraryCacheManager#unregisterTask(JobID, ExecutionAttemptID)}.
     */
    @Test
    public void testLibraryCacheManagerTaskCleanup() throws IOException, InterruptedException {
        JobID jobId = new JobID();
        ExecutionAttemptID attempt1 = new ExecutionAttemptID();
        ExecutionAttemptID attempt2 = new ExecutionAttemptID();
        List<PermanentBlobKey> keys = new ArrayList<>();
        BlobServer server = null;
        PermanentBlobCache cache = null;
        BlobLibraryCacheManager libCache = null;
        final byte[] buf = new byte[128];
        try {
            Configuration config = new Configuration();
            config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
            config.setLong(CLEANUP_INTERVAL, 1L);
            server = new BlobServer(config, new VoidBlobStore());
            server.start();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", server.getPort());
            cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
            keys.add(server.putPermanent(jobId, buf));
            buf[0] += 1;
            keys.add(server.putPermanent(jobId, buf));
            libCache = new BlobLibraryCacheManager(cache, ResolveOrder.CHILD_FIRST, new String[0]);
            cache.registerJob(jobId);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, cache);
            libCache.registerTask(jobId, attempt1, keys, Collections.<URL>emptyList());
            ClassLoader classLoader1 = libCache.getClassLoader(jobId);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.registerTask(jobId, attempt2, keys, Collections.<URL>emptyList());
            ClassLoader classLoader2 = libCache.getClassLoader(jobId);
            Assert.assertEquals(classLoader1, classLoader2);
            try {
                libCache.registerTask(jobId, new ExecutionAttemptID(), Collections.emptyList(), Collections.emptyList());
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            try {
                libCache.registerTask(jobId, new ExecutionAttemptID(), keys, Collections.singletonList(new URL("file:///tmp/does-not-exist")));
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(2, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.unregisterTask(jobId, attempt1);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.unregisterTask(jobId, attempt2);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            // only PermanentBlobCache#releaseJob() calls clean up files (tested in BlobCacheCleanupTest etc.
        } finally {
            if (libCache != null) {
                libCache.shutdown();
            }
            // should have been closed by the libraryCacheManager, but just in case
            if (cache != null) {
                cache.close();
            }
            if (server != null) {
                server.close();
            }
        }
    }

    /**
     * Tests that the {@link BlobLibraryCacheManager} cleans up after calling {@link BlobLibraryCacheManager#unregisterTask(JobID, ExecutionAttemptID)}.
     */
    @Test
    public void testLibraryCacheManagerMixedJobTaskCleanup() throws IOException, InterruptedException {
        JobID jobId = new JobID();
        ExecutionAttemptID attempt1 = new ExecutionAttemptID();
        List<PermanentBlobKey> keys = new ArrayList<>();
        BlobServer server = null;
        PermanentBlobCache cache = null;
        BlobLibraryCacheManager libCache = null;
        final byte[] buf = new byte[128];
        try {
            Configuration config = new Configuration();
            config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
            config.setLong(CLEANUP_INTERVAL, 1L);
            server = new BlobServer(config, new VoidBlobStore());
            server.start();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", server.getPort());
            cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
            keys.add(server.putPermanent(jobId, buf));
            buf[0] += 1;
            keys.add(server.putPermanent(jobId, buf));
            libCache = new BlobLibraryCacheManager(cache, ResolveOrder.CHILD_FIRST, new String[0]);
            cache.registerJob(jobId);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, cache);
            libCache.registerJob(jobId, keys, Collections.<URL>emptyList());
            ClassLoader classLoader1 = libCache.getClassLoader(jobId);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.registerTask(jobId, attempt1, keys, Collections.<URL>emptyList());
            ClassLoader classLoader2 = libCache.getClassLoader(jobId);
            Assert.assertEquals(classLoader1, classLoader2);
            try {
                libCache.registerTask(jobId, new ExecutionAttemptID(), Collections.emptyList(), Collections.emptyList());
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            try {
                libCache.registerTask(jobId, new ExecutionAttemptID(), keys, Collections.singletonList(new URL("file:///tmp/does-not-exist")));
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(2, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.unregisterJob(jobId);
            Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            libCache.unregisterTask(jobId, attempt1);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            // only PermanentBlobCache#releaseJob() calls clean up files (tested in BlobCacheCleanupTest etc.
        } finally {
            if (libCache != null) {
                libCache.shutdown();
            }
            // should have been closed by the libraryCacheManager, but just in case
            if (cache != null) {
                cache.close();
            }
            if (server != null) {
                server.close();
            }
        }
    }

    @Test
    public void testRegisterAndDownload() throws IOException {
        Assume.assumeTrue((!(OperatingSystem.isWindows())));// setWritable doesn't work on Windows.

        JobID jobId = new JobID();
        BlobServer server = null;
        PermanentBlobCache cache = null;
        BlobLibraryCacheManager libCache = null;
        File cacheDir = null;
        try {
            // create the blob transfer services
            Configuration config = new Configuration();
            config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
            config.setLong(CLEANUP_INTERVAL, 1000000L);
            server = new BlobServer(config, new VoidBlobStore());
            server.start();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", server.getPort());
            cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
            // upload some meaningless data to the server
            PermanentBlobKey dataKey1 = server.putPermanent(jobId, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
            PermanentBlobKey dataKey2 = server.putPermanent(jobId, new byte[]{ 11, 12, 13, 14, 15, 16, 17, 18 });
            libCache = new BlobLibraryCacheManager(cache, ResolveOrder.CHILD_FIRST, new String[0]);
            Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, cache);
            // first try to access a non-existing entry
            Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(new JobID()));
            try {
                libCache.getClassLoader(new JobID());
                Assert.fail("Should fail with an IllegalStateException");
            } catch (IllegalStateException e) {
                // that's what we want
            }
            // register some BLOBs as libraries
            {
                Collection<PermanentBlobKey> keys = Collections.singleton(dataKey1);
                cache.registerJob(jobId);
                ExecutionAttemptID executionId = new ExecutionAttemptID();
                libCache.registerTask(jobId, executionId, keys, Collections.<URL>emptyList());
                ClassLoader classLoader1 = libCache.getClassLoader(jobId);
                Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
                Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
                BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
                BlobServerCleanupTest.checkFileCountForJob(1, jobId, cache);
                Assert.assertNotNull(libCache.getClassLoader(jobId));
                libCache.registerJob(jobId, keys, Collections.<URL>emptyList());
                ClassLoader classLoader2 = libCache.getClassLoader(jobId);
                Assert.assertEquals(classLoader1, classLoader2);
                Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(2, libCache.getNumberOfReferenceHolders(jobId));
                Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
                BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
                BlobServerCleanupTest.checkFileCountForJob(1, jobId, cache);
                Assert.assertNotNull(libCache.getClassLoader(jobId));
                // un-register the job
                libCache.unregisterJob(jobId);
                // still one task
                Assert.assertEquals(1, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(1, libCache.getNumberOfReferenceHolders(jobId));
                Assert.assertEquals(1, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
                BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
                BlobServerCleanupTest.checkFileCountForJob(1, jobId, cache);
                // unregister the task registration
                libCache.unregisterTask(jobId, executionId);
                Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
                // changing the libCache registration does not influence the BLOB stores...
                BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
                BlobServerCleanupTest.checkFileCountForJob(1, jobId, cache);
                // Don't fail if called again
                libCache.unregisterJob(jobId);
                Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
                libCache.unregisterTask(jobId, executionId);
                Assert.assertEquals(0, libCache.getNumberOfManagedJobs());
                Assert.assertEquals(0, libCache.getNumberOfReferenceHolders(jobId));
                cache.releaseJob(jobId);
                // library is still cached (but not associated with job any more)
                BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
                BlobServerCleanupTest.checkFileCountForJob(1, jobId, cache);
            }
            // see BlobUtils for the directory layout
            cacheDir = cache.getStorageLocation(jobId, new PermanentBlobKey()).getParentFile();
            Assert.assertTrue(cacheDir.exists());
            // make sure no further blobs can be downloaded by removing the write
            // permissions from the directory
            Assert.assertTrue("Could not remove write permissions from cache directory", cacheDir.setWritable(false, false));
            // since we cannot download this library any more, this call should fail
            try {
                cache.registerJob(jobId);
                libCache.registerTask(jobId, new ExecutionAttemptID(), Collections.singleton(dataKey2), Collections.<URL>emptyList());
                Assert.fail("This should fail with an IOException");
            } catch (IOException e) {
                // splendid!
                cache.releaseJob(jobId);
            }
        } finally {
            if (cacheDir != null) {
                if (!(cacheDir.setWritable(true, false))) {
                    System.err.println("Could not re-add write permissions to cache directory.");
                }
            }
            if (cache != null) {
                cache.close();
            }
            if (libCache != null) {
                libCache.shutdown();
            }
            if (server != null) {
                server.close();
            }
        }
    }
}

