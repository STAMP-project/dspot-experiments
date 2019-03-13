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
package org.apache.hadoop.yarn.server.sharedcachemanager.store;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.util.concurrent.HadoopExecutors;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.sharedcachemanager.AppChecker;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestInMemorySCMStore extends SCMStoreBaseTest {
    private InMemorySCMStore store;

    private AppChecker checker;

    @Test
    public void testAddResourceConcurrency() throws Exception {
        startEmptyStore();
        final String key = "key1";
        int count = 5;
        ExecutorService exec = HadoopExecutors.newFixedThreadPool(count);
        List<Future<String>> futures = new ArrayList<Future<String>>(count);
        final CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < count; i++) {
            final String fileName = ("foo-" + i) + ".jar";
            Callable<String> task = new Callable<String>() {
                public String call() throws Exception {
                    start.await();
                    String result = store.addResource(key, fileName);
                    System.out.println(((("fileName: " + fileName) + ", result: ") + result));
                    return result;
                }
            };
            futures.add(exec.submit(task));
        }
        // start them all at the same time
        start.countDown();
        // check the result; they should all agree with the value
        Set<String> results = new HashSet<String>();
        for (Future<String> future : futures) {
            results.add(future.get());
        }
        Assert.assertSame(1, results.size());
        exec.shutdown();
    }

    @Test
    public void testAddResourceRefNonExistentResource() throws Exception {
        startEmptyStore();
        String key = "key1";
        ApplicationId id = createAppId(1, 1L);
        // try adding an app id without adding the key first
        Assert.assertNull(store.addResourceReference(key, new SharedCacheResourceReference(id, "user")));
    }

    @Test
    public void testRemoveResourceEmptyRefs() throws Exception {
        startEmptyStore();
        String key = "key1";
        String fileName = "foo.jar";
        // first add resource
        store.addResource(key, fileName);
        // try removing the resource; it should return true
        Assert.assertTrue(store.removeResource(key));
    }

    @Test
    public void testAddResourceRefRemoveResource() throws Exception {
        startEmptyStore();
        String key = "key1";
        ApplicationId id = createAppId(1, 1L);
        String user = "user";
        // add the resource, and then add a resource ref
        store.addResource(key, "foo.jar");
        store.addResourceReference(key, new SharedCacheResourceReference(id, user));
        // removeResource should return false
        Assert.assertTrue((!(store.removeResource(key))));
        // the resource and the ref should be intact
        Collection<SharedCacheResourceReference> refs = store.getResourceReferences(key);
        Assert.assertTrue((refs != null));
        Assert.assertEquals(Collections.singleton(new SharedCacheResourceReference(id, user)), refs);
    }

    @Test
    public void testAddResourceRefConcurrency() throws Exception {
        startEmptyStore();
        final String key = "key1";
        final String user = "user";
        String fileName = "foo.jar";
        // first add the resource
        store.addResource(key, fileName);
        // make concurrent addResourceRef calls (clients)
        int count = 5;
        ExecutorService exec = HadoopExecutors.newFixedThreadPool(count);
        List<Future<String>> futures = new ArrayList<Future<String>>(count);
        final CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < count; i++) {
            final ApplicationId id = createAppId(i, i);
            Callable<String> task = new Callable<String>() {
                public String call() throws Exception {
                    start.await();
                    return store.addResourceReference(key, new SharedCacheResourceReference(id, user));
                }
            };
            futures.add(exec.submit(task));
        }
        // start them all at the same time
        start.countDown();
        // check the result
        Set<String> results = new HashSet<String>();
        for (Future<String> future : futures) {
            results.add(future.get());
        }
        // they should all have the same file name
        Assert.assertSame(1, results.size());
        Assert.assertEquals(Collections.singleton(fileName), results);
        // there should be 5 refs as a result
        Collection<SharedCacheResourceReference> refs = store.getResourceReferences(key);
        Assert.assertSame(count, refs.size());
        exec.shutdown();
    }

    @Test
    public void testAddResourceRefAddResourceConcurrency() throws Exception {
        startEmptyStore();
        final String key = "key1";
        final String fileName = "foo.jar";
        final String user = "user";
        final ApplicationId id = createAppId(1, 1L);
        // add the resource and add the resource ref at the same time
        ExecutorService exec = HadoopExecutors.newFixedThreadPool(2);
        final CountDownLatch start = new CountDownLatch(1);
        Callable<String> addKeyTask = new Callable<String>() {
            public String call() throws Exception {
                start.await();
                return store.addResource(key, fileName);
            }
        };
        Callable<String> addAppIdTask = new Callable<String>() {
            public String call() throws Exception {
                start.await();
                return store.addResourceReference(key, new SharedCacheResourceReference(id, user));
            }
        };
        Future<String> addAppIdFuture = exec.submit(addAppIdTask);
        Future<String> addKeyFuture = exec.submit(addKeyTask);
        // start them at the same time
        start.countDown();
        // get the results
        String addKeyResult = addKeyFuture.get();
        String addAppIdResult = addAppIdFuture.get();
        Assert.assertEquals(fileName, addKeyResult);
        System.out.println(("addAppId() result: " + addAppIdResult));
        // it may be null or the fileName depending on the timing
        Assert.assertTrue(((addAppIdResult == null) || (addAppIdResult.equals(fileName))));
        exec.shutdown();
    }

    @Test
    public void testRemoveRef() throws Exception {
        startEmptyStore();
        String key = "key1";
        String fileName = "foo.jar";
        String user = "user";
        // first add the resource
        store.addResource(key, fileName);
        // add a ref
        ApplicationId id = createAppId(1, 1L);
        SharedCacheResourceReference myRef = new SharedCacheResourceReference(id, user);
        String result = store.addResourceReference(key, myRef);
        Assert.assertEquals(fileName, result);
        Collection<SharedCacheResourceReference> refs = store.getResourceReferences(key);
        Assert.assertSame(1, refs.size());
        Assert.assertEquals(Collections.singleton(myRef), refs);
        // remove the same ref
        store.removeResourceReferences(key, Collections.singleton(myRef), true);
        Collection<SharedCacheResourceReference> newRefs = store.getResourceReferences(key);
        Assert.assertTrue(((newRefs == null) || (newRefs.isEmpty())));
    }

    @Test
    public void testBootstrapping() throws Exception {
        Map<String, String> initialCachedResources = startStoreWithResources();
        int count = initialCachedResources.size();
        ApplicationId id = createAppId(1, 1L);
        // the entries from the cached entries should now exist
        for (int i = 0; i < count; i++) {
            String key = String.valueOf(i);
            String fileName = key + ".jar";
            String result = store.addResourceReference(key, new SharedCacheResourceReference(id, "user"));
            // the value should not be null (i.e. it has the key) and the filename should match
            Assert.assertEquals(fileName, result);
            // the initial input should be emptied
            Assert.assertTrue(initialCachedResources.isEmpty());
        }
    }

    @Test
    public void testEvictableWithInitialApps() throws Exception {
        startStoreWithApps();
        Assert.assertFalse(store.isResourceEvictable("key", Mockito.mock(FileStatus.class)));
    }
}

