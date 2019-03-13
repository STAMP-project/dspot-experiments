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
package org.apache.hadoop.fs;


import FileSystem.Cache.Key;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestFileSystemCaching {
    @Test
    public void testCacheEnabled() throws Exception {
        Configuration conf = newConf();
        FileSystem fs1 = FileSystem.get(new URI("cachedfile://a"), conf);
        FileSystem fs2 = FileSystem.get(new URI("cachedfile://a"), conf);
        Assert.assertSame(fs1, fs2);
    }

    private static class DefaultFs extends LocalFileSystem {
        URI uri;

        @Override
        public void initialize(URI uri, Configuration conf) {
            this.uri = uri;
        }

        @Override
        public URI getUri() {
            return uri;
        }
    }

    @Test
    public void testDefaultFsUris() throws Exception {
        final Configuration conf = new Configuration();
        conf.set("fs.defaultfs.impl", TestFileSystemCaching.DefaultFs.class.getName());
        final URI defaultUri = URI.create("defaultfs://host");
        FileSystem.setDefaultUri(conf, defaultUri);
        // sanity check default fs
        final FileSystem defaultFs = FileSystem.get(conf);
        Assert.assertEquals(defaultUri, defaultFs.getUri());
        // has scheme, no auth
        Assert.assertSame(defaultFs, FileSystem.get(URI.create("defaultfs:/"), conf));
        Assert.assertSame(defaultFs, FileSystem.get(URI.create("defaultfs:///"), conf));
        // has scheme, same auth
        Assert.assertSame(defaultFs, FileSystem.get(URI.create("defaultfs://host"), conf));
        // has scheme, different auth
        Assert.assertNotSame(defaultFs, FileSystem.get(URI.create("defaultfs://host2"), conf));
        // no scheme, no auth
        Assert.assertSame(defaultFs, FileSystem.get(URI.create("/"), conf));
        // no scheme, same auth
        LambdaTestUtils.intercept(UnsupportedFileSystemException.class, () -> FileSystem.get(URI.create("//host"), conf));
        LambdaTestUtils.intercept(UnsupportedFileSystemException.class, () -> FileSystem.get(URI.create("//host2"), conf));
    }

    public static class InitializeForeverFileSystem extends LocalFileSystem {
        static final Semaphore sem = new Semaphore(0);

        @Override
        public void initialize(URI uri, Configuration conf) throws IOException {
            // notify that InitializeForeverFileSystem started initialization
            TestFileSystemCaching.InitializeForeverFileSystem.sem.release();
            try {
                while (true) {
                    Thread.sleep(1000);
                } 
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Test
    public void testCacheEnabledWithInitializeForeverFS() throws Exception {
        final Configuration conf = new Configuration();
        Thread t = new Thread() {
            @Override
            public void run() {
                conf.set("fs.localfs1.impl", ("org.apache.hadoop.fs." + "TestFileSystemCaching$InitializeForeverFileSystem"));
                try {
                    FileSystem.get(new URI("localfs1://a"), conf);
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        // wait for InitializeForeverFileSystem to start initialization
        TestFileSystemCaching.InitializeForeverFileSystem.sem.acquire();
        conf.set("fs.cachedfile.impl", FileSystem.getFileSystemClass("file", null).getName());
        FileSystem.get(new URI("cachedfile://a"), conf);
        t.interrupt();
        t.join();
    }

    @Test
    public void testCacheDisabled() throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.uncachedfile.impl", FileSystem.getFileSystemClass("file", null).getName());
        conf.setBoolean("fs.uncachedfile.impl.disable.cache", true);
        FileSystem fs1 = FileSystem.get(new URI("uncachedfile://a"), conf);
        FileSystem fs2 = FileSystem.get(new URI("uncachedfile://a"), conf);
        Assert.assertNotSame(fs1, fs2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public <T extends TokenIdentifier> void testCacheForUgi() throws Exception {
        final Configuration conf = newConf();
        UserGroupInformation ugiA = UserGroupInformation.createRemoteUser("foo");
        UserGroupInformation ugiB = UserGroupInformation.createRemoteUser("bar");
        FileSystem fsA = getCachedFS(ugiA, conf);
        FileSystem fsA1 = getCachedFS(ugiA, conf);
        // Since the UGIs are the same, we should have the same filesystem for both
        Assert.assertSame(fsA, fsA1);
        FileSystem fsB = getCachedFS(ugiB, conf);
        // Since the UGIs are different, we should end up with different filesystems
        // corresponding to the two UGIs
        Assert.assertNotSame(fsA, fsB);
        Token<T> t1 = Mockito.mock(Token.class);
        UserGroupInformation ugiA2 = UserGroupInformation.createRemoteUser("foo");
        fsA = getCachedFS(ugiA2, conf);
        // Although the users in the UGI are same, they have different subjects
        // and so are different.
        Assert.assertNotSame(fsA, fsA1);
        ugiA.addToken(t1);
        fsA = getCachedFS(ugiA, conf);
        // Make sure that different UGI's with the same subject lead to the same
        // file system.
        Assert.assertSame(fsA, fsA1);
    }

    @Test
    public void testUserFS() throws Exception {
        final Configuration conf = newConf();
        FileSystem fsU1 = FileSystem.get(new URI("cachedfile://a"), conf, "bar");
        FileSystem fsU2 = FileSystem.get(new URI("cachedfile://a"), conf, "foo");
        Assert.assertNotSame(fsU1, fsU2);
    }

    @Test
    public void testFsUniqueness() throws Exception {
        final Configuration conf = newConf();
        // multiple invocations of FileSystem.get return the same object.
        FileSystem fs1 = FileSystem.get(conf);
        FileSystem fs2 = FileSystem.get(conf);
        Assert.assertSame(fs1, fs2);
        // multiple invocations of FileSystem.newInstance return different objects
        fs1 = FileSystem.newInstance(new URI("cachedfile://a"), conf, "bar");
        fs2 = FileSystem.newInstance(new URI("cachedfile://a"), conf, "bar");
        Assert.assertTrue(((fs1 != fs2) && (!(fs1.equals(fs2)))));
        fs1.close();
        fs2.close();
    }

    @Test
    public void testCloseAllForUGI() throws Exception {
        final Configuration conf = newConf();
        UserGroupInformation ugiA = UserGroupInformation.createRemoteUser("foo");
        FileSystem fsA = getCachedFS(ugiA, conf);
        // Now we should get the cached filesystem
        FileSystem fsA1 = getCachedFS(ugiA, conf);
        Assert.assertSame(fsA, fsA1);
        FileSystem.closeAllForUGI(ugiA);
        // Now we should get a different (newly created) filesystem
        fsA1 = getCachedFS(ugiA, conf);
        Assert.assertNotSame(fsA, fsA1);
    }

    @Test
    public void testDelete() throws IOException {
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        FileSystem fs = new FilterFileSystem(mockFs);
        Path path = new Path("/a");
        fs.delete(path, false);
        Mockito.verify(mockFs).delete(ArgumentMatchers.eq(path), ArgumentMatchers.eq(false));
        Mockito.reset(mockFs);
        fs.delete(path, true);
        Mockito.verify(mockFs).delete(ArgumentMatchers.eq(path), ArgumentMatchers.eq(true));
    }

    @Test
    public void testDeleteOnExit() throws IOException {
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        Path path = new Path("/a");
        try (FileSystem fs = new FilterFileSystem(mockFs)) {
            // delete on close if path does exist
            Mockito.when(mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(new FileStatus());
            Assert.assertTrue(fs.deleteOnExit(path));
            Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
            Mockito.reset(mockFs);
            Mockito.when(mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(new FileStatus());
            fs.close();
        }
        Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
        Mockito.verify(mockFs).delete(ArgumentMatchers.eq(path), ArgumentMatchers.eq(true));
    }

    @Test
    public void testDeleteOnExitFNF() throws IOException {
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        Path path;
        try (FileSystem fs = new FilterFileSystem(mockFs)) {
            path = new Path("/a");
            // don't delete on close if path doesn't exist
            Assert.assertFalse(fs.deleteOnExit(path));
            Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
            Mockito.reset(mockFs);
            fs.close();
        }
        Mockito.verify(mockFs, Mockito.never()).getFileStatus(ArgumentMatchers.eq(path));
        Mockito.verify(mockFs, Mockito.never()).delete(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDeleteOnExitRemoved() throws IOException {
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        Path path;
        try (FileSystem fs = new FilterFileSystem(mockFs)) {
            path = new Path("/a");
            // don't delete on close if path existed, but later removed
            Mockito.when(mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(new FileStatus());
            Assert.assertTrue(fs.deleteOnExit(path));
            Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
            Mockito.reset(mockFs);
            fs.close();
        }
        Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
        Mockito.verify(mockFs, Mockito.never()).delete(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCancelDeleteOnExit() throws IOException {
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        try (FileSystem fs = new FilterFileSystem(mockFs)) {
            Path path = new Path("/a");
            // don't delete on close if path existed, but later cancelled
            Mockito.when(mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(new FileStatus());
            Assert.assertTrue(fs.deleteOnExit(path));
            Mockito.verify(mockFs).getFileStatus(ArgumentMatchers.eq(path));
            Assert.assertTrue(fs.cancelDeleteOnExit(path));
            Assert.assertFalse(fs.cancelDeleteOnExit(path));// false because not registered

            Mockito.reset(mockFs);
            fs.close();
        }
        Mockito.verify(mockFs, Mockito.never()).getFileStatus(ArgumentMatchers.any(Path.class));
        Mockito.verify(mockFs, Mockito.never()).delete(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCacheIncludesURIUserInfo() throws Throwable {
        URI containerA = new URI("wasb://a@account.blob.core.windows.net");
        URI containerB = new URI("wasb://b@account.blob.core.windows.net");
        Configuration conf = new Configuration(false);
        FileSystem.Cache.Key keyA = new FileSystem.Cache.Key(containerA, conf);
        FileSystem.Cache.Key keyB = new FileSystem.Cache.Key(containerB, conf);
        Assert.assertNotEquals(keyA, keyB);
        Assert.assertNotEquals(keyA, new FileSystem.Cache.Key(new URI("wasb://account.blob.core.windows.net"), conf));
        Assert.assertEquals(keyA, new FileSystem.Cache.Key(new URI("wasb://A@account.blob.core.windows.net"), conf));
        Assert.assertNotEquals(keyA, new FileSystem.Cache.Key(new URI("wasb://a:password@account.blob.core.windows.net"), conf));
    }
}

