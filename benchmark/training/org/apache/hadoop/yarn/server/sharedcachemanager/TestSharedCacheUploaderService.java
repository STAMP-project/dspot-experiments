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
import java.util.Collection;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.SCMUploaderProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.SCMUploaderNotifyRequest;
import org.apache.hadoop.yarn.server.sharedcachemanager.metrics.SharedCacheUploaderMetrics;
import org.apache.hadoop.yarn.server.sharedcachemanager.store.SCMStore;
import org.apache.hadoop.yarn.server.sharedcachemanager.store.SharedCacheResourceReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic unit tests for the NodeManger to SCM Protocol Service.
 */
public class TestSharedCacheUploaderService {
    private static File testDir = null;

    private SharedCacheUploaderService service;

    private SCMUploaderProtocol proxy;

    private SCMStore store;

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test
    public void testNotify_noEntry() throws Exception {
        long accepted = SharedCacheUploaderMetrics.getInstance().getAcceptedUploads();
        SCMUploaderNotifyRequest request = recordFactory.newRecordInstance(SCMUploaderNotifyRequest.class);
        request.setResourceKey("key1");
        request.setFilename("foo.jar");
        Assert.assertTrue(proxy.notify(request).getAccepted());
        Collection<SharedCacheResourceReference> set = store.getResourceReferences("key1");
        Assert.assertNotNull(set);
        Assert.assertEquals(0, set.size());
        Assert.assertEquals("NM upload metrics aren't updated.", 1, ((SharedCacheUploaderMetrics.getInstance().getAcceptedUploads()) - accepted));
    }

    @Test
    public void testNotify_entryExists_differentName() throws Exception {
        long rejected = SharedCacheUploaderMetrics.getInstance().getRejectUploads();
        store.addResource("key1", "foo.jar");
        SCMUploaderNotifyRequest request = recordFactory.newRecordInstance(SCMUploaderNotifyRequest.class);
        request.setResourceKey("key1");
        request.setFilename("foobar.jar");
        Assert.assertFalse(proxy.notify(request).getAccepted());
        Collection<SharedCacheResourceReference> set = store.getResourceReferences("key1");
        Assert.assertNotNull(set);
        Assert.assertEquals(0, set.size());
        Assert.assertEquals("NM upload metrics aren't updated.", 1, ((SharedCacheUploaderMetrics.getInstance().getRejectUploads()) - rejected));
    }

    @Test
    public void testNotify_entryExists_sameName() throws Exception {
        long accepted = SharedCacheUploaderMetrics.getInstance().getAcceptedUploads();
        store.addResource("key1", "foo.jar");
        SCMUploaderNotifyRequest request = recordFactory.newRecordInstance(SCMUploaderNotifyRequest.class);
        request.setResourceKey("key1");
        request.setFilename("foo.jar");
        Assert.assertTrue(proxy.notify(request).getAccepted());
        Collection<SharedCacheResourceReference> set = store.getResourceReferences("key1");
        Assert.assertNotNull(set);
        Assert.assertEquals(0, set.size());
        Assert.assertEquals("NM upload metrics aren't updated.", 1, ((SharedCacheUploaderMetrics.getInstance().getAcceptedUploads()) - accepted));
    }
}

