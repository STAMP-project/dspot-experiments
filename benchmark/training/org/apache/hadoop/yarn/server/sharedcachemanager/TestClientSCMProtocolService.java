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


import java.io.File;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ClientSCMProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.ReleaseSharedCacheResourceRequest;
import org.apache.hadoop.yarn.api.protocolrecords.UseSharedCacheResourceRequest;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.sharedcachemanager.metrics.ClientSCMMetrics;
import org.apache.hadoop.yarn.server.sharedcachemanager.store.SCMStore;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic unit tests for the Client to SCM Protocol Service.
 */
public class TestClientSCMProtocolService {
    private static File testDir = null;

    private ClientProtocolService service;

    private ClientSCMProtocol clientSCMProxy;

    private SCMStore store;

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test
    public void testUse_MissingEntry() throws Exception {
        long misses = ClientSCMMetrics.getInstance().getCacheMisses();
        UseSharedCacheResourceRequest request = recordFactory.newRecordInstance(UseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(1, 1L));
        Assert.assertNull(clientSCMProxy.use(request).getPath());
        Assert.assertEquals("Client SCM metrics aren't updated.", 1, ((ClientSCMMetrics.getInstance().getCacheMisses()) - misses));
    }

    @Test
    public void testUse_ExistingEntry_NoAppIds() throws Exception {
        // Pre-populate the SCM with one cache entry
        store.addResource("key1", "foo.jar");
        long hits = ClientSCMMetrics.getInstance().getCacheHits();
        UseSharedCacheResourceRequest request = recordFactory.newRecordInstance(UseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(2, 2L));
        // Expecting default depth of 3 and under the shared cache root dir
        String expectedPath = (TestClientSCMProtocolService.testDir.getAbsolutePath()) + "/k/e/y/key1/foo.jar";
        Assert.assertEquals(expectedPath, clientSCMProxy.use(request).getPath());
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        Assert.assertEquals("Client SCM metrics aren't updated.", 1, ((ClientSCMMetrics.getInstance().getCacheHits()) - hits));
    }

    @Test
    public void testUse_ExistingEntry_OneId() throws Exception {
        // Pre-populate the SCM with one cache entry
        store.addResource("key1", "foo.jar");
        store.addResourceReference("key1", new org.apache.hadoop.yarn.server.sharedcachemanager.store.SharedCacheResourceReference(createAppId(1, 1L), "user"));
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        long hits = ClientSCMMetrics.getInstance().getCacheHits();
        // Add a new distinct appId
        UseSharedCacheResourceRequest request = recordFactory.newRecordInstance(UseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(2, 2L));
        // Expecting default depth of 3 under the shared cache root dir
        String expectedPath = (TestClientSCMProtocolService.testDir.getAbsolutePath()) + "/k/e/y/key1/foo.jar";
        Assert.assertEquals(expectedPath, clientSCMProxy.use(request).getPath());
        Assert.assertEquals(2, store.getResourceReferences("key1").size());
        Assert.assertEquals("Client SCM metrics aren't updated.", 1, ((ClientSCMMetrics.getInstance().getCacheHits()) - hits));
    }

    @Test
    public void testUse_ExistingEntry_DupId() throws Exception {
        // Pre-populate the SCM with one cache entry
        store.addResource("key1", "foo.jar");
        UserGroupInformation testUGI = UserGroupInformation.getCurrentUser();
        store.addResourceReference("key1", new org.apache.hadoop.yarn.server.sharedcachemanager.store.SharedCacheResourceReference(createAppId(1, 1L), testUGI.getShortUserName()));
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        long hits = ClientSCMMetrics.getInstance().getCacheHits();
        // Add a new duplicate appId
        UseSharedCacheResourceRequest request = recordFactory.newRecordInstance(UseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(1, 1L));
        // Expecting default depth of 3 under the shared cache root dir
        String expectedPath = (TestClientSCMProtocolService.testDir.getAbsolutePath()) + "/k/e/y/key1/foo.jar";
        Assert.assertEquals(expectedPath, clientSCMProxy.use(request).getPath());
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        Assert.assertEquals("Client SCM metrics aren't updated.", 1, ((ClientSCMMetrics.getInstance().getCacheHits()) - hits));
    }

    @Test
    public void testRelease_ExistingEntry_NonExistantAppId() throws Exception {
        // Pre-populate the SCM with one cache entry
        store.addResource("key1", "foo.jar");
        store.addResourceReference("key1", new org.apache.hadoop.yarn.server.sharedcachemanager.store.SharedCacheResourceReference(createAppId(1, 1L), "user"));
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        long releases = ClientSCMMetrics.getInstance().getCacheReleases();
        ReleaseSharedCacheResourceRequest request = recordFactory.newRecordInstance(ReleaseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(2, 2L));
        clientSCMProxy.release(request);
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        Assert.assertEquals("Client SCM metrics were updated when a release did not happen", 0, ((ClientSCMMetrics.getInstance().getCacheReleases()) - releases));
    }

    @Test
    public void testRelease_ExistingEntry_WithAppId() throws Exception {
        // Pre-populate the SCM with one cache entry
        store.addResource("key1", "foo.jar");
        UserGroupInformation testUGI = UserGroupInformation.getCurrentUser();
        store.addResourceReference("key1", new org.apache.hadoop.yarn.server.sharedcachemanager.store.SharedCacheResourceReference(createAppId(1, 1L), testUGI.getShortUserName()));
        Assert.assertEquals(1, store.getResourceReferences("key1").size());
        long releases = ClientSCMMetrics.getInstance().getCacheReleases();
        ReleaseSharedCacheResourceRequest request = recordFactory.newRecordInstance(ReleaseSharedCacheResourceRequest.class);
        request.setResourceKey("key1");
        request.setAppId(createAppId(1, 1L));
        clientSCMProxy.release(request);
        Assert.assertEquals(0, store.getResourceReferences("key1").size());
        Assert.assertEquals("Client SCM metrics aren't updated.", 1, ((ClientSCMMetrics.getInstance().getCacheReleases()) - releases));
    }

    @Test
    public void testRelease_MissingEntry() throws Exception {
        long releases = ClientSCMMetrics.getInstance().getCacheReleases();
        ReleaseSharedCacheResourceRequest request = recordFactory.newRecordInstance(ReleaseSharedCacheResourceRequest.class);
        request.setResourceKey("key2");
        request.setAppId(createAppId(2, 2L));
        clientSCMProxy.release(request);
        Assert.assertNotNull(store.getResourceReferences("key2"));
        Assert.assertEquals(0, store.getResourceReferences("key2").size());
        Assert.assertEquals("Client SCM metrics were updated when a release did not happen.", 0, ((ClientSCMMetrics.getInstance().getCacheReleases()) - releases));
    }
}

