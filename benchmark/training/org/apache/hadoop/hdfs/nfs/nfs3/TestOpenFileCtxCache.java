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
package org.apache.hadoop.hdfs.nfs.nfs3;


import NfsConfigKeys.DFS_NFS_MAX_OPEN_FILES_KEY;
import NfsConfigKeys.DFS_NFS_STREAM_TIMEOUT_DEFAULT;
import NfsConfigKeys.DFS_NFS_STREAM_TIMEOUT_MIN_DEFAULT;
import java.io.IOException;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfigKeys;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.nfs.nfs3.FileHandle;
import org.apache.hadoop.nfs.nfs3.Nfs3FileAttributes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestOpenFileCtxCache {
    static boolean cleaned = false;

    @Test
    public void testEviction() throws IOException, InterruptedException {
        NfsConfiguration conf = new NfsConfiguration();
        // Only two entries will be in the cache
        conf.setInt(DFS_NFS_MAX_OPEN_FILES_KEY, 2);
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        OpenFileCtx context1 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context2 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context3 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context4 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context5 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtxCache cache = new OpenFileCtxCache(conf, ((10 * 60) * 100));
        boolean ret = cache.put(new FileHandle(1), context1);
        Assert.assertTrue(ret);
        Thread.sleep(1000);
        ret = cache.put(new FileHandle(2), context2);
        Assert.assertTrue(ret);
        ret = cache.put(new FileHandle(3), context3);
        Assert.assertFalse(ret);
        Assert.assertTrue(((cache.size()) == 2));
        // Wait for the oldest stream to be evict-able, insert again
        Thread.sleep(DFS_NFS_STREAM_TIMEOUT_MIN_DEFAULT);
        Assert.assertTrue(((cache.size()) == 2));
        ret = cache.put(new FileHandle(3), context3);
        Assert.assertTrue(ret);
        Assert.assertTrue(((cache.size()) == 2));
        Assert.assertTrue(((cache.get(new FileHandle(1))) == null));
        // Test inactive entry is evicted immediately
        context3.setActiveStatusForTest(false);
        ret = cache.put(new FileHandle(4), context4);
        Assert.assertTrue(ret);
        // Now the cache has context2 and context4
        // Test eviction failure if all entries have pending work.
        context2.getPendingWritesForTest().put(new OffsetRange(0, 100), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        context4.getPendingCommitsForTest().put(new Long(100), new org.apache.hadoop.hdfs.nfs.nfs3.OpenFileCtx.CommitCtx(0, null, 0, attr));
        Thread.sleep(DFS_NFS_STREAM_TIMEOUT_MIN_DEFAULT);
        ret = cache.put(new FileHandle(5), context5);
        Assert.assertFalse(ret);
    }

    @Test
    public void testScan() throws IOException, InterruptedException {
        NfsConfiguration conf = new NfsConfiguration();
        // Only two entries will be in the cache
        conf.setInt(DFS_NFS_MAX_OPEN_FILES_KEY, 2);
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        OpenFileCtx context1 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context2 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context3 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtx context4 = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()));
        OpenFileCtxCache cache = new OpenFileCtxCache(conf, ((10 * 60) * 100));
        // Test cleaning expired entry
        boolean ret = cache.put(new FileHandle(1), context1);
        Assert.assertTrue(ret);
        ret = cache.put(new FileHandle(2), context2);
        Assert.assertTrue(ret);
        Thread.sleep(((NfsConfigKeys.DFS_NFS_STREAM_TIMEOUT_MIN_DEFAULT) + 1));
        cache.scan(DFS_NFS_STREAM_TIMEOUT_MIN_DEFAULT);
        Assert.assertTrue(((cache.size()) == 0));
        // Test cleaning inactive entry
        ret = cache.put(new FileHandle(3), context3);
        Assert.assertTrue(ret);
        ret = cache.put(new FileHandle(4), context4);
        Assert.assertTrue(ret);
        context3.setActiveStatusForTest(false);
        cache.scan(DFS_NFS_STREAM_TIMEOUT_DEFAULT);
        Assert.assertTrue(((cache.size()) == 1));
        Assert.assertTrue(((cache.get(new FileHandle(3))) == null));
        Assert.assertTrue(((cache.get(new FileHandle(4))) != null));
    }
}

