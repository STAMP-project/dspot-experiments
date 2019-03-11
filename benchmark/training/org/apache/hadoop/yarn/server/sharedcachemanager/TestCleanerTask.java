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
package org.apache.hadoop.yarn.server.sharedcachemanager;


import java.util.concurrent.locks.ReentrantLock;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.sharedcachemanager.metrics.CleanerMetrics;
import org.apache.hadoop.yarn.server.sharedcachemanager.store.SCMStore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestCleanerTask {
    private static final String ROOT = YarnConfiguration.DEFAULT_SHARED_CACHE_ROOT;

    private static final long SLEEP_TIME = YarnConfiguration.DEFAULT_SCM_CLEANER_RESOURCE_SLEEP_MS;

    private static final int NESTED_LEVEL = YarnConfiguration.DEFAULT_SHARED_CACHE_NESTED_LEVEL;

    @Test
    public void testNonExistentRoot() throws Exception {
        FileSystem fs = Mockito.mock(FileSystem.class);
        CleanerMetrics metrics = Mockito.mock(CleanerMetrics.class);
        SCMStore store = Mockito.mock(SCMStore.class);
        CleanerTask task = createSpiedTask(fs, store, metrics, new ReentrantLock());
        // the shared cache root does not exist
        Mockito.when(fs.exists(task.getRootPath())).thenReturn(false);
        task.run();
        // process() should not be called
        Mockito.verify(task, Mockito.never()).process();
    }

    @Test
    public void testProcessFreshResource() throws Exception {
        FileSystem fs = Mockito.mock(FileSystem.class);
        CleanerMetrics metrics = Mockito.mock(CleanerMetrics.class);
        SCMStore store = Mockito.mock(SCMStore.class);
        CleanerTask task = createSpiedTask(fs, store, metrics, new ReentrantLock());
        // mock a resource that is not evictable
        Mockito.when(store.isResourceEvictable(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(FileStatus.class))).thenReturn(false);
        FileStatus status = Mockito.mock(FileStatus.class);
        Mockito.when(status.getPath()).thenReturn(new Path(((TestCleanerTask.ROOT) + "/a/b/c/abc")));
        // process the resource
        task.processSingleResource(status);
        // the directory should not be renamed
        Mockito.verify(fs, Mockito.never()).rename(ArgumentMatchers.eq(status.getPath()), ArgumentMatchers.isA(Path.class));
        // metrics should record a processed file (but not delete)
        Mockito.verify(metrics).reportAFileProcess();
        Mockito.verify(metrics, Mockito.never()).reportAFileDelete();
    }

    @Test
    public void testProcessEvictableResource() throws Exception {
        FileSystem fs = Mockito.mock(FileSystem.class);
        CleanerMetrics metrics = Mockito.mock(CleanerMetrics.class);
        SCMStore store = Mockito.mock(SCMStore.class);
        CleanerTask task = createSpiedTask(fs, store, metrics, new ReentrantLock());
        // mock an evictable resource
        Mockito.when(store.isResourceEvictable(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(FileStatus.class))).thenReturn(true);
        FileStatus status = Mockito.mock(FileStatus.class);
        Mockito.when(status.getPath()).thenReturn(new Path(((TestCleanerTask.ROOT) + "/a/b/c/abc")));
        Mockito.when(store.removeResource(ArgumentMatchers.isA(String.class))).thenReturn(true);
        // rename succeeds
        Mockito.when(fs.rename(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class))).thenReturn(true);
        // delete returns true
        Mockito.when(fs.delete(ArgumentMatchers.isA(Path.class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        // process the resource
        task.processSingleResource(status);
        // the directory should be renamed
        Mockito.verify(fs).rename(ArgumentMatchers.eq(status.getPath()), ArgumentMatchers.isA(Path.class));
        // metrics should record a deleted file
        Mockito.verify(metrics).reportAFileDelete();
        Mockito.verify(metrics, Mockito.never()).reportAFileProcess();
    }

    @Test
    public void testResourceIsInUseHasAnActiveApp() throws Exception {
        FileSystem fs = Mockito.mock(FileSystem.class);
        CleanerMetrics metrics = Mockito.mock(CleanerMetrics.class);
        SCMStore store = Mockito.mock(SCMStore.class);
        FileStatus resource = Mockito.mock(FileStatus.class);
        Mockito.when(resource.getPath()).thenReturn(new Path(((TestCleanerTask.ROOT) + "/a/b/c/abc")));
        // resource is stale
        Mockito.when(store.isResourceEvictable(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(FileStatus.class))).thenReturn(true);
        // but still has appIds
        Mockito.when(store.removeResource(ArgumentMatchers.isA(String.class))).thenReturn(false);
        CleanerTask task = createSpiedTask(fs, store, metrics, new ReentrantLock());
        // process the resource
        task.processSingleResource(resource);
        // metrics should record a processed file (but not delete)
        Mockito.verify(metrics).reportAFileProcess();
        Mockito.verify(metrics, Mockito.never()).reportAFileDelete();
    }
}

