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
package org.apache.geode.internal.cache.ha;


import Version.GFE_90;
import WanType.PARALLEL;
import WanType.PRIMARY;
import WanType.SECONDARY;
import java.net.InetAddress;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.EventID;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class ThreadIdentifierJUnitTest {
    @Test
    public void testEqualsIgnoresUUIDBytes() throws Exception {
        InternalDistributedMember id = new InternalDistributedMember(InetAddress.getLocalHost(), 1234);
        id.setVersionObjectForTest(GFE_90);
        byte[] memberIdBytes = EventID.getMembershipId(new org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID(id));
        byte[] memberIdBytesWithoutUUID = new byte[(memberIdBytes.length) - ((2 * 8) + 1)];// UUID bytes +

        // weight byte
        System.arraycopy(memberIdBytes, 0, memberIdBytesWithoutUUID, 0, memberIdBytesWithoutUUID.length);
        ThreadIdentifier threadIdWithUUID = new ThreadIdentifier(memberIdBytes, 1);
        ThreadIdentifier threadIdWithoutUUID = new ThreadIdentifier(memberIdBytesWithoutUUID, 1);
        Assert.assertEquals(threadIdWithoutUUID, threadIdWithUUID);
        Assert.assertEquals(threadIdWithUUID, threadIdWithoutUUID);
        Assert.assertEquals(threadIdWithoutUUID.hashCode(), threadIdWithUUID.hashCode());
        EventID eventIDWithUUID = new EventID(memberIdBytes, 1, 1);
        EventID eventIDWithoutUUID = new EventID(memberIdBytesWithoutUUID, 1, 1);
        Assert.assertEquals(eventIDWithUUID, eventIDWithoutUUID);
        Assert.assertEquals(eventIDWithoutUUID, eventIDWithUUID);
        Assert.assertEquals(eventIDWithoutUUID.hashCode(), eventIDWithUUID.hashCode());
    }

    @Test
    public void testPutAllId() {
        int id = 42;
        int bucketNumber = 113;
        long putAll = ThreadIdentifier.createFakeThreadIDForBulkOp(bucketNumber, id);
        Assert.assertTrue(ThreadIdentifier.isPutAllFakeThreadID(putAll));
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(putAll));
    }

    @Test
    public void testWanId() {
        int id = 42;
        long wan1 = ThreadIdentifier.createFakeThreadIDForParallelGSPrimaryBucket(1, id, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan1));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan1));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan1);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(PRIMARY.matches(real_tid_with_wan));
        }
        long wan2 = ThreadIdentifier.createFakeThreadIDForParallelGSSecondaryBucket(1, id, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan2));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan2));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan2);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(SECONDARY.matches(real_tid_with_wan));
        }
        long wan3 = ThreadIdentifier.createFakeThreadIDForParallelGateway(1, id, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan3));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan3));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan3);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(PARALLEL.matches(real_tid_with_wan));
        }
    }

    @Test
    public void testWanAndPutAllId() {
        int id = 42;
        int bucketNumber = 113;
        long putAll = ThreadIdentifier.createFakeThreadIDForBulkOp(bucketNumber, id);
        long wan1 = ThreadIdentifier.createFakeThreadIDForParallelGSPrimaryBucket(1, putAll, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan1));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan1));
        Assert.assertTrue(ThreadIdentifier.isPutAllFakeThreadID(wan1));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan1);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(PRIMARY.matches(real_tid_with_wan));
        }
        long wan2 = ThreadIdentifier.createFakeThreadIDForParallelGSSecondaryBucket(1, putAll, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan2));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan2));
        Assert.assertTrue(ThreadIdentifier.isPutAllFakeThreadID(wan2));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan2);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(SECONDARY.matches(real_tid_with_wan));
        }
        long wan3 = ThreadIdentifier.createFakeThreadIDForParallelGateway(1, putAll, 0);
        Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(wan3));
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(wan3));
        Assert.assertTrue(ThreadIdentifier.isPutAllFakeThreadID(wan3));
        {
            long real_tid_with_wan = ThreadIdentifier.getRealThreadIDIncludingWan(wan3);
            Assert.assertEquals(42, ThreadIdentifier.getRealThreadID(real_tid_with_wan));
            Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(real_tid_with_wan));
            Assert.assertTrue(PARALLEL.matches(real_tid_with_wan));
        }
        long tid = 4054000001L;
        Assert.assertTrue(ThreadIdentifier.isParallelWANThreadID(tid));
        Assert.assertFalse(ThreadIdentifier.isParallelWANThreadID(putAll));
    }
}

