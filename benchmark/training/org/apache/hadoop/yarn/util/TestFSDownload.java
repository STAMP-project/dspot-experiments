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
package org.apache.hadoop.yarn.util;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import FSDownload.PRIVATE_FILE_PERMS;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalDirAllocator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.concurrent.HadoopExecutors;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for the FSDownload class.
 */
public class TestFSDownload {
    private static final Logger LOG = LoggerFactory.getLogger(TestFSDownload.class);

    private static AtomicLong uniqueNumberGenerator = new AtomicLong(System.currentTimeMillis());

    private enum TEST_FILE_TYPE {

        TAR,
        JAR,
        ZIP,
        TGZ;}

    private Configuration conf = new Configuration();

    static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test(timeout = 10000)
    public void testDownloadBadPublic() throws IOException, InterruptedException, URISyntaxException {
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        FileContext files = FileContext.getLocalFSFileContext(conf);
        final Path basedir = files.makeQualified(new Path("target", TestFSDownload.class.getSimpleName()));
        files.mkdir(basedir, null, true);
        conf.setStrings(TestFSDownload.class.getName(), basedir.toString());
        Map<LocalResource, LocalResourceVisibility> rsrcVis = new HashMap<LocalResource, LocalResourceVisibility>();
        Random rand = new Random();
        long sharedSeed = rand.nextLong();
        rand.setSeed(sharedSeed);
        System.out.println(("SEED: " + sharedSeed));
        Map<LocalResource, Future<Path>> pending = new HashMap<LocalResource, Future<Path>>();
        ExecutorService exec = HadoopExecutors.newSingleThreadExecutor();
        LocalDirAllocator dirs = new LocalDirAllocator(TestFSDownload.class.getName());
        int size = 512;
        LocalResourceVisibility vis = LocalResourceVisibility.PUBLIC;
        Path path = new Path(basedir, "test-file");
        LocalResource rsrc = TestFSDownload.createFile(files, path, size, rand, vis);
        rsrcVis.put(rsrc, vis);
        Path destPath = dirs.getLocalPathForWrite(basedir.toString(), size, conf);
        destPath = new Path(destPath, Long.toString(TestFSDownload.uniqueNumberGenerator.incrementAndGet()));
        FSDownload fsd = new FSDownload(files, UserGroupInformation.getCurrentUser(), conf, destPath, rsrc);
        pending.put(rsrc, exec.submit(fsd));
        exec.shutdown();
        while (!(exec.awaitTermination(1000, TimeUnit.MILLISECONDS)));
        Assert.assertTrue(pending.get(rsrc).isDone());
        try {
            for (Map.Entry<LocalResource, Future<Path>> p : pending.entrySet()) {
                p.getValue().get();
                Assert.fail("We localized a file that is not public.");
            }
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
    }

    @Test(timeout = 60000)
    public void testDownloadPublicWithStatCache() throws IOException, InterruptedException, URISyntaxException, ExecutionException {
        FileContext files = FileContext.getLocalFSFileContext(conf);
        Path basedir = files.makeQualified(new Path("target", TestFSDownload.class.getSimpleName()));
        // if test directory doesn't have ancestor permission, skip this test
        FileSystem f = basedir.getFileSystem(conf);
        Assume.assumeTrue(FSDownload.ancestorsHaveExecutePermissions(f, basedir, null));
        files.mkdir(basedir, null, true);
        conf.setStrings(TestFSDownload.class.getName(), basedir.toString());
        int size = 512;
        final ConcurrentMap<Path, AtomicInteger> counts = new ConcurrentHashMap<Path, AtomicInteger>();
        final CacheLoader<Path, Future<FileStatus>> loader = FSDownload.createStatusCacheLoader(conf);
        final LoadingCache<Path, Future<FileStatus>> statCache = CacheBuilder.newBuilder().build(new CacheLoader<Path, Future<FileStatus>>() {
            public Future<FileStatus> load(Path path) throws Exception {
                // increment the count
                AtomicInteger count = counts.get(path);
                if (count == null) {
                    count = new AtomicInteger(0);
                    AtomicInteger existing = counts.putIfAbsent(path, count);
                    if (existing != null) {
                        count = existing;
                    }
                }
                count.incrementAndGet();
                // use the default loader
                return loader.load(path);
            }
        });
        // test FSDownload.isPublic() concurrently
        final int fileCount = 3;
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < fileCount; i++) {
            Random rand = new Random();
            long sharedSeed = rand.nextLong();
            rand.setSeed(sharedSeed);
            System.out.println(("SEED: " + sharedSeed));
            final Path path = new Path(basedir, ("test-file-" + i));
            TestFSDownload.createFile(files, path, size, rand);
            final FileSystem fs = path.getFileSystem(conf);
            final FileStatus sStat = fs.getFileStatus(path);
            tasks.add(new Callable<Boolean>() {
                public Boolean call() throws IOException {
                    return FSDownload.isPublic(fs, path, sStat, statCache);
                }
            });
        }
        ExecutorService exec = HadoopExecutors.newFixedThreadPool(fileCount);
        try {
            List<Future<Boolean>> futures = exec.invokeAll(tasks);
            // files should be public
            for (Future<Boolean> future : futures) {
                Assert.assertTrue(future.get());
            }
            // for each path exactly one file status call should be made
            for (AtomicInteger count : counts.values()) {
                Assert.assertSame(count.get(), 1);
            }
        } finally {
            exec.shutdown();
        }
    }

    @Test(timeout = 10000)
    public void testDownload() throws IOException, InterruptedException, URISyntaxException {
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        FileContext files = FileContext.getLocalFSFileContext(conf);
        final Path basedir = files.makeQualified(new Path("target", TestFSDownload.class.getSimpleName()));
        files.mkdir(basedir, null, true);
        conf.setStrings(TestFSDownload.class.getName(), basedir.toString());
        Map<LocalResource, LocalResourceVisibility> rsrcVis = new HashMap<LocalResource, LocalResourceVisibility>();
        Random rand = new Random();
        long sharedSeed = rand.nextLong();
        rand.setSeed(sharedSeed);
        System.out.println(("SEED: " + sharedSeed));
        Map<LocalResource, Future<Path>> pending = new HashMap<LocalResource, Future<Path>>();
        ExecutorService exec = HadoopExecutors.newSingleThreadExecutor();
        LocalDirAllocator dirs = new LocalDirAllocator(TestFSDownload.class.getName());
        int[] sizes = new int[10];
        for (int i = 0; i < 10; ++i) {
            sizes[i] = (rand.nextInt(512)) + 512;
            LocalResourceVisibility vis = LocalResourceVisibility.PRIVATE;
            if ((i % 2) == 1) {
                vis = LocalResourceVisibility.APPLICATION;
            }
            Path p = new Path(basedir, ("" + i));
            LocalResource rsrc = TestFSDownload.createFile(files, p, sizes[i], rand, vis);
            rsrcVis.put(rsrc, vis);
            Path destPath = dirs.getLocalPathForWrite(basedir.toString(), sizes[i], conf);
            destPath = new Path(destPath, Long.toString(TestFSDownload.uniqueNumberGenerator.incrementAndGet()));
            FSDownload fsd = new FSDownload(files, UserGroupInformation.getCurrentUser(), conf, destPath, rsrc);
            pending.put(rsrc, exec.submit(fsd));
        }
        exec.shutdown();
        while (!(exec.awaitTermination(1000, TimeUnit.MILLISECONDS)));
        for (Future<Path> path : pending.values()) {
            Assert.assertTrue(path.isDone());
        }
        try {
            for (Map.Entry<LocalResource, Future<Path>> p : pending.entrySet()) {
                Path localized = p.getValue().get();
                Assert.assertEquals(sizes[Integer.parseInt(localized.getName())], p.getKey().getSize());
                FileStatus status = files.getFileStatus(localized.getParent());
                FsPermission perm = status.getPermission();
                Assert.assertEquals("Cache directory permissions are incorrect", new FsPermission(((short) (493))), perm);
                status = files.getFileStatus(localized);
                perm = status.getPermission();
                System.out.println(((("File permission " + perm) + " for rsrc vis ") + (p.getKey().getVisibility().name())));
                assert rsrcVis.containsKey(p.getKey());
                Assert.assertTrue("Private file should be 500", ((perm.toShort()) == (PRIVATE_FILE_PERMS.toShort())));
            }
        } catch (ExecutionException e) {
            throw new IOException("Failed exec", e);
        }
    }

    @Test(timeout = 10000)
    public void testDownloadArchive() throws IOException, InterruptedException, URISyntaxException {
        downloadWithFileType(TestFSDownload.TEST_FILE_TYPE.TAR);
    }

    @Test(timeout = 10000)
    public void testDownloadPatternJar() throws IOException, InterruptedException, URISyntaxException {
        downloadWithFileType(TestFSDownload.TEST_FILE_TYPE.JAR);
    }

    @Test(timeout = 10000)
    public void testDownloadArchiveZip() throws IOException, InterruptedException, URISyntaxException {
        downloadWithFileType(TestFSDownload.TEST_FILE_TYPE.ZIP);
    }

    /* To test fix for YARN-3029 */
    @Test(timeout = 10000)
    public void testDownloadArchiveZipWithTurkishLocale() throws IOException, InterruptedException, URISyntaxException {
        Locale defaultLocale = Locale.getDefault();
        // Set to Turkish
        Locale turkishLocale = new Locale("tr", "TR");
        Locale.setDefault(turkishLocale);
        downloadWithFileType(TestFSDownload.TEST_FILE_TYPE.ZIP);
        // Set the locale back to original default locale
        Locale.setDefault(defaultLocale);
    }

    @Test(timeout = 10000)
    public void testDownloadArchiveTgz() throws IOException, InterruptedException, URISyntaxException {
        downloadWithFileType(TestFSDownload.TEST_FILE_TYPE.TGZ);
    }

    @Test(timeout = 10000)
    public void testDirDownload() throws IOException, InterruptedException {
        FileContext files = FileContext.getLocalFSFileContext(conf);
        final Path basedir = files.makeQualified(new Path("target", TestFSDownload.class.getSimpleName()));
        files.mkdir(basedir, null, true);
        conf.setStrings(TestFSDownload.class.getName(), basedir.toString());
        Map<LocalResource, LocalResourceVisibility> rsrcVis = new HashMap<LocalResource, LocalResourceVisibility>();
        Random rand = new Random();
        long sharedSeed = rand.nextLong();
        rand.setSeed(sharedSeed);
        System.out.println(("SEED: " + sharedSeed));
        Map<LocalResource, Future<Path>> pending = new HashMap<LocalResource, Future<Path>>();
        ExecutorService exec = HadoopExecutors.newSingleThreadExecutor();
        LocalDirAllocator dirs = new LocalDirAllocator(TestFSDownload.class.getName());
        for (int i = 0; i < 5; ++i) {
            LocalResourceVisibility vis = LocalResourceVisibility.PRIVATE;
            if ((i % 2) == 1) {
                vis = LocalResourceVisibility.APPLICATION;
            }
            Path p = new Path(basedir, (("dir" + i) + ".jar"));
            LocalResource rsrc = TestFSDownload.createJar(files, p, vis);
            rsrcVis.put(rsrc, vis);
            Path destPath = dirs.getLocalPathForWrite(basedir.toString(), conf);
            destPath = new Path(destPath, Long.toString(TestFSDownload.uniqueNumberGenerator.incrementAndGet()));
            FSDownload fsd = new FSDownload(files, UserGroupInformation.getCurrentUser(), conf, destPath, rsrc);
            pending.put(rsrc, exec.submit(fsd));
        }
        exec.shutdown();
        while (!(exec.awaitTermination(1000, TimeUnit.MILLISECONDS)));
        for (Future<Path> path : pending.values()) {
            Assert.assertTrue(path.isDone());
        }
        try {
            for (Map.Entry<LocalResource, Future<Path>> p : pending.entrySet()) {
                Path localized = p.getValue().get();
                FileStatus status = files.getFileStatus(localized);
                System.out.println(("Testing path " + localized));
                assert status.isDirectory();
                assert rsrcVis.containsKey(p.getKey());
                verifyPermsRecursively(localized.getFileSystem(conf), files, localized, rsrcVis.get(p.getKey()));
            }
        } catch (ExecutionException e) {
            throw new IOException("Failed exec", e);
        }
    }

    @Test(timeout = 10000)
    public void testUniqueDestinationPath() throws Exception {
        FileContext files = FileContext.getLocalFSFileContext(conf);
        final Path basedir = files.makeQualified(new Path("target", TestFSDownload.class.getSimpleName()));
        files.mkdir(basedir, null, true);
        conf.setStrings(TestFSDownload.class.getName(), basedir.toString());
        ExecutorService singleThreadedExec = HadoopExecutors.newSingleThreadExecutor();
        LocalDirAllocator dirs = new LocalDirAllocator(TestFSDownload.class.getName());
        Path destPath = dirs.getLocalPathForWrite(basedir.toString(), conf);
        destPath = new Path(destPath, Long.toString(TestFSDownload.uniqueNumberGenerator.incrementAndGet()));
        Path p = new Path(basedir, (("dir" + 0) + ".jar"));
        LocalResourceVisibility vis = LocalResourceVisibility.PRIVATE;
        LocalResource rsrc = TestFSDownload.createJar(files, p, vis);
        FSDownload fsd = new FSDownload(files, UserGroupInformation.getCurrentUser(), conf, destPath, rsrc);
        Future<Path> rPath = singleThreadedExec.submit(fsd);
        singleThreadedExec.shutdown();
        while (!(singleThreadedExec.awaitTermination(1000, TimeUnit.MILLISECONDS)));
        Assert.assertTrue(rPath.isDone());
        // Now FSDownload will not create a random directory to localize the
        // resource. Therefore the final localizedPath for the resource should be
        // destination directory (passed as an argument) + file name.
        Assert.assertEquals(destPath, rPath.get().getParent());
    }
}

