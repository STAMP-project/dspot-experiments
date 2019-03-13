/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.partitioned;


import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.HeapDataOutputStream;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.partitioned.FetchEntriesMessage.FetchEntriesReplyMessage;
import org.apache.geode.internal.cache.partitioned.FetchEntriesMessage.FetchEntriesResponse;
import org.apache.geode.test.fake.Fakes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FetchEntriesMessageJUnitTest {
    private GemFireCacheImpl cache;

    @Test
    public void testProcessChunk() throws Exception {
        cache = Fakes.cache();
        PartitionedRegion pr = Mockito.mock(PartitionedRegion.class);
        InternalDistributedSystem system = cache.getInternalDistributedSystem();
        FetchEntriesResponse response = new FetchEntriesResponse(system, pr, null, 0);
        HeapDataOutputStream chunkStream = createDummyChunk();
        FetchEntriesReplyMessage reply = new FetchEntriesReplyMessage(null, 0, 0, chunkStream, 0, 0, 0, false, false);
        reply.chunk = chunkStream.toByteArray();
        response.processChunk(reply);
        Assert.assertNull(response.returnRVV);
        Assert.assertEquals(2, response.returnValue.size());
        Assert.assertTrue(response.returnValue.get("keyWithOutVersionTag").equals("valueWithOutVersionTag"));
        Assert.assertTrue(response.returnValue.get("keyWithVersionTag").equals("valueWithVersionTag"));
        Assert.assertNull(response.returnVersions.get("keyWithOutVersionTag"));
        Assert.assertNotNull(response.returnVersions.get("keyWithVersionTag"));
    }
}

