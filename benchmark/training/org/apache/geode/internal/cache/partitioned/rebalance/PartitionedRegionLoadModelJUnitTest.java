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
package org.apache.geode.internal.cache.partitioned.rebalance;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.DataSerializer;
import org.apache.geode.cache.partition.PartitionMemberInfo;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.partitioned.OfflineMemberDetails;
import org.apache.geode.internal.cache.partitioned.OfflineMemberDetailsImpl;
import org.apache.geode.internal.cache.partitioned.PartitionMemberInfoImpl;
import org.apache.geode.internal.cache.partitioned.rebalance.BucketOperator.Completion;
import org.apache.geode.internal.cache.partitioned.rebalance.model.PartitionedRegionLoadModel;
import org.apache.geode.internal.cache.persistence.PersistentMemberID;
import org.junit.Assert;
import org.junit.Test;


public class PartitionedRegionLoadModelJUnitTest {
    private static final int MAX_MOVES = 5000;

    private static final boolean DEBUG = true;

    private PartitionedRegionLoadModelJUnitTest.MyBucketOperator bucketOperator;

    /**
     * This test checks basic redundancy satisfaction. It creates two buckets with low redundancy and
     * 1 bucket with full redundancy and excepts copies of the low redundancy buckets to be made.
     */
    @Test
    public void testRedundancySatisfaction() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 0 }, new long[]{ 1, 1, 1, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 1, 0, 1 }, new long[]{ 0, 0, 0, 1 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Set<PartitionMemberInfo> details = model.getPartitionedMemberDetails("a");
        Assert.assertEquals(2, details.size());
        // TODO - make some assertions about what's in the details
        // we expect three moves
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, false, false), model));
        // TODO - make some assertions about what's in the details
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 3));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    /**
     * This test creates buckets with low redundancy, but only 1 of the buckets is small enough to be
     * copied. The other bucket should be rejected because it is too big..
     */
    @Test
    public void testRedundancySatisfactionWithSizeLimit() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // A member with 1 bucket with low redundancy, but it is too big to copy anywhere
        PartitionMemberInfoImpl details1 = buildDetails(member1, 50, 50, new long[]{ 30, 0, 0 }, new long[]{ 1, 0, 0 });
        // A member with 2 buckets with low redundancy that can be copied
        PartitionMemberInfoImpl details2 = buildDetails(member2, 40, 40, new long[]{ 0, 10, 10 }, new long[]{ 0, 1, 1 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 2 moves
        Assert.assertEquals(2, doMoves(new CompositeDirector(true, true, false, false), model));
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 2));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    @Test
    public void testRedundancySatisfactionWithCriticalMember() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>singleton(member1), null);
        // this member has critical heap
        PartitionMemberInfoImpl details1 = buildDetails(member1, 50, 50, new long[]{ 10, 0, 0 }, new long[]{ 1, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 40, 40, new long[]{ 0, 10, 10 }, new long[]{ 0, 1, 1 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 2 moves
        Assert.assertEquals(1, doMoves(new CompositeDirector(true, true, false, false), model));
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    /**
     * This test makes sure we ignore the size limit if requested
     */
    @Test
    public void testRedundancySatisfactionDoNotEnforceLocalMaxMemory() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // A member with 1 bucket with low redundancy, but it is too big to copy anywhere
        PartitionMemberInfoImpl details1 = buildDetails(member1, 50, 50, new long[]{ 30, 0, 0 }, new long[]{ 1, 0, 0 });
        // A member with 2 buckets with low redundancy that can be copied
        PartitionMemberInfoImpl details2 = buildDetails(member2, 40, 40, new long[]{ 0, 10, 10 }, new long[]{ 0, 1, 1 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), false);
        // we expect 2 moves
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, false, false), model));
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 2));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
        Set<PartitionMemberInfo> afterDetails = model.getPartitionedMemberDetails("a");
        Assert.assertEquals(afterDetails.size(), 2);
        for (PartitionMemberInfo member : afterDetails) {
            if (member.getDistributedMember().equals(member1)) {
                Assert.assertEquals(details1.getConfiguredMaxMemory(), member.getConfiguredMaxMemory());
            } else {
                Assert.assertEquals(details2.getConfiguredMaxMemory(), member.getConfiguredMaxMemory());
            }
        }
    }

    /**
     * Tests to make sure that redundancy satisfaction prefers to make redundant copies on members
     * with remote IP addresses.
     */
    @Test
    public void testRedundancySatisfactionPreferRemoteIp() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getLocalHost(), 3);
        // Create some buckets with low redundancy on members 1 and 2
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 30, 0, 0 }, new long[]{ 1, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 30, 30 }, new long[]{ 0, 1, 1 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 0, 0, 0 }, new long[]{ 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 3 moves
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, false, false), model));
        // The buckets should all be created on member 3 because
        // it has a different ip address
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 2));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    @Test
    public void testRedundancySatisfactionEnforceRemoteIp() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(true), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some buckets with low redundancy on members 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 30, 30, 30 }, new long[]{ 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0 }, new long[]{ 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 0 moves, because we're enforcing that we can't create
        // copies on the same IP.
        Assert.assertEquals(0, doMoves(new CompositeDirector(true, true, false, false), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
    }

    @Test
    public void testMoveBucketsEnforceRemoteIp() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 3, PartitionedRegionLoadModelJUnitTest.getAddressComparor(true), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some buckets with low redundancy on members 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 30, 30, 30 }, new long[]{ 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0 }, new long[]{ 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 0 moves, because we're enforcing that we can't create
        // copies on the same IP.
        Assert.assertEquals(1, doMoves(new CompositeDirector(true, true, true, true), model));
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.bucketMoves);
    }

    /**
     * Tests to make sure that redundancy satisfaction balances between nodes to ensure an even load.
     */
    @Test
    public void testRedundancySatisfactionBalanced() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 4 moves
        Assert.assertEquals(4, doMoves(new CompositeDirector(true, true, false, false), model));
        // The bucket creates should alternate between members
        // so that the load is balanced.
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 3));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    /**
     * Tests to make sure that redundancy satisfaction balances between nodes to ensure an even load.
     */
    @Test
    public void testColocatedRegions() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 12, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 250, 250, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 250, 250, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        model.addRegion("b", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 4 moves
        Assert.assertEquals(18, doMoves(new CompositeDirector(true, true, false, true), model));
        // The bucket creates should alternate between members
        // so that the load is balanced.
        Set<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new HashSet<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 3));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 4));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 5));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 6));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 7));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 8));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 9));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 10));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 11));
        Assert.assertEquals(expectedCreates, new HashSet(bucketOperator.creates));
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        Assert.assertEquals(expectedMoves, new HashSet(bucketOperator.primaryMoves));
    }

    @Test
    public void testIncompleteColocation() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 1, 1, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        model.addRegion("b", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        model.addRegion("c", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // Add a region which is not created on all of the members.
        // Member 3 should not be considered a target for any moves.
        Assert.assertEquals(6, doMoves(new CompositeDirector(true, true, false, true), model));
        // Everything should be creatd on member2
        Set<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new HashSet<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 3));
        Assert.assertEquals(expectedCreates, new HashSet(bucketOperator.creates));
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, new HashSet(bucketOperator.primaryMoves));
    }

    /**
     * Test that we enforce local max memory on a per region basis IE if one of the regions has a low
     * lmm, it will prevent a bucket move
     */
    @Test
    public void testColocationEnforceLocalMaxMemory() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // Member 2 has a lmm of 2, so it should only accept 2 buckets
        PartitionMemberInfoImpl bDetails1 = buildDetails(member1, 2, 2, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl bDetails2 = buildDetails(member2, 2, 2, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("b", Arrays.asList(bDetails1, bDetails2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(4, doMoves(new CompositeDirector(true, true, false, true), model));
        // Everything should be create on member2
        Set<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new HashSet<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 1));
        Assert.assertEquals(expectedCreates, new HashSet(bucketOperator.creates));
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, new HashSet(bucketOperator.primaryMoves));
    }

    /**
     * Test that each region indivually honors it's enforce local max memory flag.
     */
    @Test
    public void testColocationIgnoreEnforceLocalMaxMemory() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // Member 2 has a lmm of 2, so it should only accept 2 buckets
        PartitionMemberInfoImpl bDetails1 = buildDetails(member1, 2, 2, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl bDetails2 = buildDetails(member2, 2, 2, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("b", Arrays.asList(bDetails1, bDetails2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), false);
        Assert.assertEquals(6, doMoves(new CompositeDirector(true, true, false, true), model));
        // Everything should be created on member2
        Set<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new HashSet<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 3));
        Assert.assertEquals(expectedCreates, new HashSet(bucketOperator.creates));
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, new HashSet(bucketOperator.primaryMoves));
    }

    /**
     * Tests to make sure that redundancy satisfaction balances between nodes to ensure an even load.
     */
    @Test
    public void testRedundancySatisfactionWithFailures() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        final InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionedRegionLoadModelJUnitTest.MyBucketOperator op = new PartitionedRegionLoadModelJUnitTest.MyBucketOperator() {
            @Override
            public void createRedundantBucket(InternalDistributedMember targetMember, int i, Map<String, Long> colocatedRegionBytes, Completion completion) {
                if (targetMember.equals(member2)) {
                    completion.onFailure();
                } else {
                    super.createRedundantBucket(targetMember, i, colocatedRegionBytes, completion);
                }
            }
        };
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(op, 1, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        // Create some buckets with low redundancy on member 1
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        // we expect 8 moves
        Assert.assertEquals(8, doMoves(new CompositeDirector(true, true, false, false), model));
        // The bucket creates should do all of the creates on member 3
        // because member2 failed.
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 3));
        Assert.assertEquals(expectedCreates, op.creates);
    }

    /**
     * Test that redundancy satisfation can handle asynchronous failures and complete the job
     * correctly.
     */
    @Test
    public void testRedundancySatisfactionWithAsyncFailures() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionedRegionLoadModelJUnitTest.BucketOperatorWithFailures operator = new PartitionedRegionLoadModelJUnitTest.BucketOperatorWithFailures();
        operator.addBadMember(member2);
        bucketOperator = operator;
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Set<PartitionMemberInfo> details = model.getPartitionedMemberDetails("a");
        Assert.assertEquals(3, details.size());
        // TODO - make some assertions about what's in the details
        // we expect 6 moves (3 of these will fail)
        Assert.assertEquals(6, doMoves(new CompositeDirector(true, true, false, false), model));
        Assert.assertEquals(3, bucketOperator.creates.size());
        for (Completion completion : operator.pendingSuccesses) {
            completion.onSuccess();
        }
        for (Completion completion : operator.pendingFailures) {
            completion.onFailure();
        }
        // Now the last two moves will get reattempted to a new location (because the last location
        // failed)
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, false, false), model));
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 3));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 5));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 2));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member3, 4));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    /**
     * Very basic test of moving primaries. Creates two nodes and four buckets, with a copy of each
     * bucket on both nodes. All of the primaries are on one node. It expects half the primaries to
     * move to the other node.
     */
    @Test
    public void testMovePrimaries() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some imbalanced primaries
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(2, doMoves(new CompositeDirector(false, false, false, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // Two of the primaries should move to member2
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.primaryMoves);
    }

    /**
     * Test that we can move primaries if failures occur during the move. In this case member2 is bad,
     * so primaries should move to member3 instead.
     */
    @Test
    public void testMovePrimariesWithFailures() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        final InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionedRegionLoadModelJUnitTest.MyBucketOperator op = new PartitionedRegionLoadModelJUnitTest.MyBucketOperator() {
            @Override
            public boolean movePrimary(InternalDistributedMember source, InternalDistributedMember target, int bucketId) {
                if (target.equals(member2)) {
                    return false;
                }
                return super.movePrimary(source, target, bucketId);
            }
        };
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(op, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        // Create some imbalanced primaries
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(8, doMoves(new CompositeDirector(false, false, false, true), model));
        Assert.assertEquals(Collections.emptyList(), op.creates);
        // Two of the primaries should move to member2
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        Assert.assertEquals(expectedMoves, op.primaryMoves);
    }

    /**
     * Test of moving primaries when nodes are weighted relative to each other
     */
    @Test
    public void testMovePrimariesWithWeights() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // member 1 has a lower weight, and all of the primaries
        PartitionMemberInfoImpl details1 = buildDetails(member1, 1, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        // member 2 has a higher weight
        PartitionMemberInfoImpl details2 = buildDetails(member2, 3, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(3, doMoves(new CompositeDirector(false, false, false, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // Three of the primaries should move to member2, because it has a higher weight
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.primaryMoves);
    }

    /**
     * This is a more complicated test of primary moving where moving primaries for some buckets then
     * forces other primaries to move.
     *
     * P = primary R = redundant X = not hosting
     *
     * Bucket 0 1 2 3 4 5 6 7 8 Member1 P P P P P P X X X Member2 R R R R R R P P R Member3 X X X X X
     * X R R P
     */
    @Test
    public void testPrimaryShuffle() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 9, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionMemberInfoImpl details1 = buildDetails(member1, 1, 500, new long[]{ 1, 1, 1, 1, 1, 1, 0, 0, 0 }, new long[]{ 1, 1, 1, 1, 1, 1, 0, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 1, 500, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0, 0, 0, 1, 1, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 1, 500, new long[]{ 0, 0, 0, 0, 0, 0, 1, 1, 1 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 1 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        int moves = doMoves(new CompositeDirector(false, false, false, true), model);
        Assert.assertEquals(("Actual Moves" + (bucketOperator.primaryMoves)), 3, moves);
        // Two of the primaries should move off of member1 to member2. And
        // One primaries should move from member 2 to member 3.
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member2, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, new HashSet(bucketOperator.primaryMoves));
    }

    /**
     * Test a case where we seem to get into an infinite loop while balancing primaries.
     */
    @Test
    public void testBug39953() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 113, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        InternalDistributedMember member4 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 4);
        // Create some imbalanced primaries
        PartitionMemberInfoImpl details1 = buildDetails(member1, 216, 216, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 216, 216, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 216, 216, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1 });
        PartitionMemberInfoImpl details4 = buildDetails(member4, 216, 216, new long[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3, details4), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(0, doMoves(new CompositeDirector(false, false, false, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.primaryMoves);
    }

    /**
     * Very basic test of moving buckets. Creates two nodes and four buckets, with buckets only on one
     * node. Half of the buckets should move to the other node.
     */
    @Test
    public void testMoveBuckets() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(2, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.primaryMoves);
        // Two of the buckets should move to member2
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.bucketMoves);
    }

    /**
     * Test that moving buckets will work if there are failures while moving buckets member2 refuses
     * the buckets, so the buckets should move to member3
     */
    @Test
    public void testMoveBucketsWithFailures() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        final InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionedRegionLoadModelJUnitTest.MyBucketOperator op = new PartitionedRegionLoadModelJUnitTest.MyBucketOperator() {
            @Override
            public boolean moveBucket(InternalDistributedMember source, InternalDistributedMember target, int id, Map<String, Long> colocatedRegionBytes) {
                if (target.equals(member2)) {
                    return false;
                }
                return super.moveBucket(source, target, id, colocatedRegionBytes);
            }
        };
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(op, 0, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(8, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), op.creates);
        Assert.assertEquals(Collections.emptyList(), op.primaryMoves);
        // Two of the buckets should move to member2
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member3));
        Assert.assertEquals(expectedMoves, op.bucketMoves);
    }

    /**
     * Test to make sure that we honor the weight of a node while moving buckets.
     */
    @Test
    public void testMoveBucketsWithWeights() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 250, 250, new long[]{ 1, 1, 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(4, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.primaryMoves);
        // Four of the buckets should move to member2, because
        // member2 has twice the weight as member1.
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.bucketMoves);
    }

    /**
     * Test to make sure we honor the size of buckets when choosing which buckets to move.
     */
    @Test
    public void testMoveBucketsWithSizes() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 3, 1, 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(4, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.primaryMoves);
        // Four of the buckets should move to member2, because
        // member1 has 1 bucket that is size 3.
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.bucketMoves);
    }

    /**
     * Test to move buckets with redundancy. Makes sure that buckets and primaries are balanced
     */
    @Test
    public void testMoveBucketsWithRedundancy() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        InternalDistributedMember member4 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 4);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 1, 1, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 1, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, 500, new long[]{ 1, 1, 1, 1 }, new long[]{ 0, 0, 0, 1 });
        PartitionMemberInfoImpl details4 = buildDetails(member4, 500, 500, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3, details4), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        doMoves(new CompositeDirector(false, false, true, true), model);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // One bucket should move from each member to member4
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member4));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member2, member4));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member3, member4));
        Assert.assertEquals(expectedMoves, new HashSet<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.bucketMoves));
        // We don't know how many primaries will move, because
        // the move buckets algorithm could move the primary or
        // it could move a redundant copy. But after we're done, we should
        // only have one primary per member.
        Set<PartitionMemberInfo> detailSet = model.getPartitionedMemberDetails("a");
        for (PartitionMemberInfo member : detailSet) {
            Assert.assertEquals(1, member.getPrimaryCount());
            Assert.assertEquals(3, member.getBucketCount());
        }
    }

    /**
     * Test to move buckets with some large buckets (to make sure there are no issues with buffer
     * overflow); Makes sure that buckets and primaries are balanced
     */
    @Test
    public void testMoveLargeBucketsWithRedundancy() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 2, 4, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        InternalDistributedMember member4 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 4);
        // Create some imbalanced nodes
        long bigBucket = ((Integer.MAX_VALUE) * 5L) + 10L;
        PartitionMemberInfoImpl details1 = buildDetails(member1, 500, Long.MAX_VALUE, new long[]{ bigBucket, bigBucket, bigBucket, bigBucket }, new long[]{ 1, 1, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 500, Long.MAX_VALUE, new long[]{ bigBucket, bigBucket, bigBucket, bigBucket }, new long[]{ 0, 0, 1, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, 500, Long.MAX_VALUE, new long[]{ bigBucket, bigBucket, bigBucket, bigBucket }, new long[]{ 0, 0, 0, 1 });
        PartitionMemberInfoImpl details4 = buildDetails(member4, 500, Long.MAX_VALUE, new long[]{ 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3, details4), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        doMoves(new CompositeDirector(false, false, true, true), model);
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // One bucket should move from each member to member4
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member4));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member2, member4));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member3, member4));
        Assert.assertEquals(expectedMoves, new HashSet<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.bucketMoves));
        // We don't know how many primaries will move, because
        // the move buckets algorithm could move the primary or
        // it could move a redundant copy. But after we're done, we should
        // only have one primary per member.
        Set<PartitionMemberInfo> detailSet = model.getPartitionedMemberDetails("a");
        for (PartitionMemberInfo member : detailSet) {
            Assert.assertEquals(1, member.getPrimaryCount());
            Assert.assertEquals(3, member.getBucketCount());
        }
    }

    /**
     * Test to make sure that moving buckets honors size restrictions for VMs.
     */
    @Test
    public void testMoveBucketsWithSizeLimits() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 50, 50, new long[]{ 30, 30, 30, 0, 0, 0 }, new long[]{ 1, 1, 1, 0, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 50, 50, new long[]{ 0, 0, 0, 10, 10, 10 }, new long[]{ 0, 0, 0, 1, 1, 1 });
        // this member has a lower size that can't fit buckets of size 30
        PartitionMemberInfoImpl details3 = buildDetails(member3, 50, 20, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(3, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // One bucket should move from each member to member4
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member2, member3));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member2, member3));
        Assert.assertEquals(expectedMoves, new HashSet<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.bucketMoves));
        Set<PartitionMemberInfo> detailSet = model.getPartitionedMemberDetails("a");
        for (PartitionMemberInfo member : detailSet) {
            Assert.assertEquals(2, member.getPrimaryCount());
            Assert.assertEquals(2, member.getBucketCount());
        }
    }

    /**
     * Test to make sure that moving buckets honors size critical members
     */
    @Test
    public void testMoveBucketsWithCriticalMember() throws Exception {
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 3);
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>singleton(member3), null);
        // Create some imbalanced nodes
        PartitionMemberInfoImpl details1 = buildDetails(member1, 50, 50, new long[]{ 10, 10, 10, 10, 10, 10 }, new long[]{ 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 50, 50, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        // this member has a critical heap
        PartitionMemberInfoImpl details3 = buildDetails(member3, 50, 50, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        model.addRegion("a", Arrays.asList(details1, details2, details3), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        Assert.assertEquals(3, doMoves(new CompositeDirector(false, false, true, true), model));
        Assert.assertEquals(Collections.emptyList(), bucketOperator.creates);
        // The buckets should only move to member2, because member3 is critical
        Set<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new HashSet<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, new HashSet<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.bucketMoves));
    }

    /**
     * Test to make sure two runs with the same information perform the same moves.
     */
    @Test
    public void testRepeatableRuns() throws UnknownHostException {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 0, 113, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 22893);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 25655);
        InternalDistributedMember member3 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 22959);
        InternalDistributedMember member4 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 22984);
        InternalDistributedMember member5 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 28609);
        InternalDistributedMember member6 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 22911);
        InternalDistributedMember member7 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 29562);
        PartitionMemberInfoImpl details1 = buildDetails(member1, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 23706, 0, 23347, 23344, 0, 0, 0, 11386, 0, 0, 0, 0, 0, 10338, 0, 9078, 6413, 10411, 5297, 1226, 0, 2594, 2523, 0, 1297, 0, 3891, 2523, 0, 0, 2594, 0, 1297, 0, 1297, 2594, 1, 0, 10375, 5188, 9078, 0, 1297, 0, 0, 1226, 1, 1, 0, 0, 1297, 11672, 0, 0, 0, 0, 7782, 0, 11673, 0, 2594, 1, 0, 2593, 3891, 1, 0, 7711, 7710, 2594, 0, 6485, 0, 1, 7711, 6485, 7711, 3891, 1297, 0, 10303, 2594, 3820, 0, 2523, 3999, 0, 1, 0, 2522, 1, 5188, 5188, 0, 2594, 3891, 2523, 2594, 0, 1297, 1, 1, 1226, 0, 1297, 0, 3891, 1226, 2522, 11601, 10376, 0, 2594 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 0, 24674, 0, 23344, 0, 19312, 19421, 11386, 7889, 0, 0, 6413, 12933, 10338, 18088, 9078, 0, 0, 0, 1226, 0, 2594, 0, 0, 0, 2594, 0, 2523, 0, 1, 0, 0, 1297, 0, 0, 0, 0, 2594, 0, 5188, 9078, 0, 0, 0, 1, 1226, 1, 0, 1297, 5187, 0, 0, 0, 0, 0, 1, 0, 11602, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 7710, 0, 10304, 6485, 0, 0, 0, 0, 0, 3891, 0, 0, 10303, 0, 0, 1, 2523, 3999, 0, 0, 1, 0, 0, 5188, 0, 5116, 2594, 3891, 2523, 0, 2522, 1297, 1, 0, 0, 1297, 0, 1297, 3891, 1226, 2522, 0, 10376, 0, 0 }, new long[]{ 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 });
        PartitionMemberInfoImpl details3 = buildDetails(member3, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 23706, 24674, 0, 0, 20901, 0, 19421, 0, 7889, 11708, 0, 0, 12933, 10338, 18088, 0, 6413, 10411, 5297, 0, 7782, 2594, 0, 1297, 0, 2594, 3891, 0, 2523, 1, 0, 2523, 1297, 1297, 1297, 0, 1, 2594, 0, 0, 0, 1297, 0, 1297, 1, 0, 0, 1, 1297, 5187, 0, 0, 13007, 0, 11672, 0, 7782, 11602, 0, 0, 0, 0, 2594, 2593, 3891, 1, 7782, 7711, 0, 0, 10304, 0, 7711, 0, 7711, 6485, 7711, 0, 1297, 1297, 10303, 2594, 3820, 1, 2523, 0, 1, 0, 1, 2522, 1, 5188, 5188, 5116, 2594, 3891, 2523, 2594, 0, 0, 0, 1, 1226, 1297, 1297, 1297, 0, 0, 2522, 0, 0, 2523, 0 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details4 = buildDetails(member4, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 23706, 24674, 23347, 0, 20901, 19312, 0, 0, 7889, 11708, 12933, 6413, 0, 0, 0, 9078, 6413, 10411, 5297, 1226, 7782, 0, 2523, 1297, 0, 0, 0, 2523, 0, 0, 2594, 2523, 0, 1297, 0, 2594, 1, 0, 10375, 0, 0, 1297, 1297, 1297, 1, 1226, 1, 0, 1297, 0, 1297, 0, 13007, 7781, 11672, 1, 7782, 11602, 11673, 5225, 2594, 1, 2594, 2593, 3891, 0, 7782, 0, 7710, 0, 10304, 0, 0, 1, 7711, 6485, 7711, 0, 0, 0, 0, 0, 3820, 1, 0, 3999, 1, 1, 1, 0, 1, 0, 5188, 0, 0, 3891, 0, 0, 2522, 1297, 1, 0, 0, 0, 1297, 1297, 0, 0, 2522, 11601, 10376, 2523, 2594 }, new long[]{ 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details5 = buildDetails(member5, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 23706, 24674, 0, 23344, 0, 0, 19421, 0, 0, 11708, 12933, 6413, 12933, 10338, 18088, 0, 0, 10411, 0, 1226, 7782, 2594, 2523, 1297, 1297, 2594, 3891, 0, 2523, 1, 2594, 2523, 0, 1297, 1297, 2594, 0, 2594, 10375, 0, 0, 1297, 0, 1297, 0, 1226, 1, 1, 0, 5187, 1297, 11672, 13007, 7781, 11672, 1, 0, 11602, 11673, 5225, 2594, 1, 0, 2593, 3891, 1, 7782, 0, 0, 2594, 0, 6485, 7711, 1, 7711, 0, 7711, 3891, 0, 1297, 0, 2594, 3820, 0, 2523, 0, 1, 1, 0, 2522, 0, 0, 0, 5116, 0, 0, 0, 0, 2522, 0, 0, 1, 0, 1297, 1297, 1297, 3891, 0, 0, 0, 0, 0, 2594 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 });
        PartitionMemberInfoImpl details6 = buildDetails(member6, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 0, 0, 23347, 0, 20901, 19312, 0, 11386, 7889, 0, 12933, 6413, 0, 0, 18088, 0, 6413, 0, 5297, 0, 7782, 0, 2523, 0, 1297, 2594, 0, 0, 2523, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5188, 9078, 0, 1297, 0, 1, 0, 0, 1, 0, 0, 1297, 11672, 13007, 7781, 0, 0, 0, 0, 0, 5225, 0, 0, 2594, 0, 0, 0, 7782, 7711, 0, 2594, 0, 0, 7711, 0, 0, 0, 0, 0, 1297, 1297, 0, 0, 0, 1, 0, 3999, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2523, 2594, 0, 0, 0, 0, 1226, 1297, 0, 0, 3891, 1226, 0, 11601, 10376, 2523, 0 }, new long[]{ 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 });
        PartitionMemberInfoImpl details7 = buildDetails(member7, ((50 * 1024) * 1024), ((50 * 1024) * 1024), new long[]{ 0, 0, 23347, 23344, 20901, 19312, 19421, 11386, 0, 11708, 12933, 0, 12933, 0, 0, 9078, 0, 0, 0, 0, 0, 0, 0, 1297, 1297, 0, 3891, 2523, 2523, 1, 2594, 2523, 1297, 1297, 1297, 2594, 1, 2594, 10375, 5188, 9078, 1297, 1297, 1297, 0, 0, 0, 0, 1297, 5187, 0, 11672, 0, 7781, 11672, 1, 7782, 0, 11673, 5225, 2594, 0, 2594, 0, 0, 1, 0, 7711, 7710, 2594, 10304, 6485, 7711, 1, 0, 6485, 0, 3891, 1297, 1297, 10303, 2594, 0, 0, 0, 0, 0, 1, 0, 2522, 0, 5188, 5188, 5116, 2594, 0, 0, 2594, 2522, 1297, 1, 1, 1226, 0, 0, 0, 0, 1226, 0, 11601, 0, 2523, 2594 }, new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 });
        model.addRegion("a", Arrays.asList(details1, details2, details3, details4, details5, details6, details7), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        doMoves(new CompositeDirector(false, false, true, true), model);
        List<PartitionedRegionLoadModelJUnitTest.Move> bucketMoves1 = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.bucketMoves);
        List<PartitionedRegionLoadModelJUnitTest.Create> bucketCreates1 = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>(bucketOperator.creates);
        List<PartitionedRegionLoadModelJUnitTest.Move> primaryMoves1 = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>(bucketOperator.primaryMoves);
        bucketOperator.bucketMoves.clear();
        bucketOperator.creates.clear();
        bucketOperator.primaryMoves.clear();
        model = new PartitionedRegionLoadModel(bucketOperator, 0, 113, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        model.addRegion("a", Arrays.asList(details1, details2, details4, details3, details5, details6, details7), new PartitionedRegionLoadModelJUnitTest.FakeOfflineDetails(), true);
        doMoves(new CompositeDirector(false, false, true, true), model);
        Assert.assertEquals(bucketCreates1, bucketOperator.creates);
        Assert.assertEquals(bucketMoves1, bucketOperator.bucketMoves);
        Assert.assertEquals(primaryMoves1, bucketOperator.primaryMoves);
    }

    /**
     * This test makes sure that we rebalance correctly with multiple levels of colocation. See bug
     * #40943
     */
    @Test
    public void testManyColocatedRegions() throws Exception {
        for (int i = 0; i < 10; i++) {
            try {
                doManyColocatedRegionsTest(i);
            } catch (Throwable t) {
                throw new RuntimeException((("With " + i) + " colocated regions, we failed"), t);
            }
        }
    }

    /**
     * Test to make sure than redundancy satisfaction ignores offline members
     */
    @Test
    public void testRedundancySatisficationWithOfflineMembers() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 5, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        PartitionMemberInfoImpl details1 = buildDetails(member1, 200, 200, new long[]{ 30, 0, 28, 30, 23 }, new long[]{ 1, 0, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 200, 200, new long[]{ 0, 23, 0, 0, 0 }, new long[]{ 0, 1, 0, 0, 0 });
        // Two buckets have an offline members
        Set<PersistentMemberID> o = Collections.singleton(new PersistentMemberID());
        Set<PersistentMemberID> x = Collections.emptySet();
        final OfflineMemberDetailsImpl offlineDetails = new OfflineMemberDetailsImpl(new Set[]{ x, x, o, o, x });
        model.addRegion("primary", Arrays.asList(details1, details2), offlineDetails, true);
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, false, false), model));
        List<PartitionedRegionLoadModelJUnitTest.Create> expectedCreates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 0));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member1, 1));
        expectedCreates.add(new PartitionedRegionLoadModelJUnitTest.Create(member2, 4));
        Assert.assertEquals(expectedCreates, bucketOperator.creates);
    }

    @Test
    public void testRebalancingWithOfflineMembers() throws Exception {
        PartitionedRegionLoadModel model = new PartitionedRegionLoadModel(bucketOperator, 1, 6, PartitionedRegionLoadModelJUnitTest.getAddressComparor(false), Collections.<InternalDistributedMember>emptySet(), null);
        InternalDistributedMember member1 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 1);
        InternalDistributedMember member2 = new InternalDistributedMember(InetAddress.getByName("127.0.0.1"), 2);
        PartitionMemberInfoImpl details1 = buildDetails(member1, 480, 480, new long[]{ 1, 1, 1, 1, 1, 1 }, new long[]{ 1, 1, 1, 1, 1, 1 });
        PartitionMemberInfoImpl details2 = buildDetails(member2, 480, 480, new long[]{ 0, 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 0, 0, 0 });
        // Each bucket has an offline member
        Set<PersistentMemberID> o = Collections.singleton(new PersistentMemberID());
        Set<PersistentMemberID> x = Collections.emptySet();
        final OfflineMemberDetailsImpl offlineDetails = new OfflineMemberDetailsImpl(new Set[]{ o, o, o, o, o, o });
        model.addRegion("primary", Arrays.asList(details1, details2), offlineDetails, true);
        Assert.assertEquals(3, doMoves(new CompositeDirector(true, true, true, true), model));
        List<PartitionedRegionLoadModelJUnitTest.Move> expectedMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        expectedMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(member1, member2));
        Assert.assertEquals(expectedMoves, bucketOperator.bucketMoves);
    }

    private static class Create {
        private final InternalDistributedMember targetMember;

        private final int bucketId;

        public Create(InternalDistributedMember targetMember, int bucketId) {
            this.targetMember = targetMember;
            this.bucketId = bucketId;
        }

        /* (non-Javadoc)

        @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (this.bucketId);
            result = (prime * result) + ((this.targetMember) == null ? 0 : this.targetMember.hashCode());
            return result;
        }

        /* (non-Javadoc)

        @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            PartitionedRegionLoadModelJUnitTest.Create other = ((PartitionedRegionLoadModelJUnitTest.Create) (obj));
            if ((this.bucketId) != (other.bucketId))
                return false;

            if ((this.targetMember) == null) {
                if ((other.targetMember) != null)
                    return false;

            } else
                if (!(this.targetMember.equals(other.targetMember)))
                    return false;


            return true;
        }

        @Override
        public String toString() {
            return ((("Create[member=" + (targetMember)) + ",bucketId=") + (bucketId)) + "]";
        }
    }

    private static class Remove {
        private final InternalDistributedMember targetMember;

        private final int bucketId;

        public Remove(InternalDistributedMember targetMember, int bucketId) {
            this.targetMember = targetMember;
            this.bucketId = bucketId;
        }

        /* (non-Javadoc)

        @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (this.bucketId);
            result = (prime * result) + ((this.targetMember) == null ? 0 : this.targetMember.hashCode());
            return result;
        }

        /* (non-Javadoc)

        @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            PartitionedRegionLoadModelJUnitTest.Create other = ((PartitionedRegionLoadModelJUnitTest.Create) (obj));
            if ((this.bucketId) != (other.bucketId))
                return false;

            if ((this.targetMember) == null) {
                if ((other.targetMember) != null)
                    return false;

            } else
                if (!(this.targetMember.equals(other.targetMember)))
                    return false;


            return true;
        }

        @Override
        public String toString() {
            return ((("Remove[member=" + (targetMember)) + ",bucketId=") + (bucketId)) + "]";
        }
    }

    private static class Move {
        private final InternalDistributedMember sourceMember;

        private final InternalDistributedMember targetMember;

        public Move(InternalDistributedMember sourceMember, InternalDistributedMember targetMember) {
            this.sourceMember = sourceMember;
            this.targetMember = targetMember;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((this.sourceMember) == null ? 0 : this.sourceMember.hashCode());
            result = (prime * result) + ((this.targetMember) == null ? 0 : this.targetMember.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            PartitionedRegionLoadModelJUnitTest.Move other = ((PartitionedRegionLoadModelJUnitTest.Move) (obj));
            if ((this.sourceMember) == null) {
                if ((other.sourceMember) != null)
                    return false;

            } else
                if (!(this.sourceMember.equals(other.sourceMember)))
                    return false;


            if ((this.targetMember) == null) {
                if ((other.targetMember) != null)
                    return false;

            } else
                if (!(this.targetMember.equals(other.targetMember)))
                    return false;


            return true;
        }

        @Override
        public String toString() {
            return ((("Move[source=" + (sourceMember)) + ",target=") + (targetMember)) + "]";
        }
    }

    private static class MyBucketOperator extends SimulatedBucketOperator {
        private List<PartitionedRegionLoadModelJUnitTest.Create> creates = new ArrayList<PartitionedRegionLoadModelJUnitTest.Create>();

        private List<PartitionedRegionLoadModelJUnitTest.Remove> removes = new ArrayList<PartitionedRegionLoadModelJUnitTest.Remove>();

        private List<PartitionedRegionLoadModelJUnitTest.Move> primaryMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();

        private List<PartitionedRegionLoadModelJUnitTest.Move> bucketMoves = new ArrayList<PartitionedRegionLoadModelJUnitTest.Move>();

        private PartitionedRegionLoadModelJUnitTest.MoveType lastMove = null;

        @Override
        public void createRedundantBucket(InternalDistributedMember targetMember, int i, Map<String, Long> colocatedRegionBytes, Completion completion) {
            creates.add(new PartitionedRegionLoadModelJUnitTest.Create(targetMember, i));
            if (PartitionedRegionLoadModelJUnitTest.DEBUG) {
                System.out.println(((("Created bucket " + i) + " on ") + targetMember));
            }
            lastMove = PartitionedRegionLoadModelJUnitTest.MoveType.CREATE;
            completion.onSuccess();
        }

        @Override
        public boolean movePrimary(InternalDistributedMember source, InternalDistributedMember target, int bucketId) {
            primaryMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(source, target));
            if (PartitionedRegionLoadModelJUnitTest.DEBUG) {
                System.out.println(((((("Moved primary " + bucketId) + " from ") + source) + " to ") + target));
            }
            lastMove = PartitionedRegionLoadModelJUnitTest.MoveType.MOVE_PRIMARY;
            return true;
        }

        @Override
        public boolean moveBucket(InternalDistributedMember source, InternalDistributedMember target, int id, Map<String, Long> colocatedRegionBytes) {
            bucketMoves.add(new PartitionedRegionLoadModelJUnitTest.Move(source, target));
            if (PartitionedRegionLoadModelJUnitTest.DEBUG) {
                System.out.println(((((("Moved bucket " + id) + " from ") + source) + " to ") + target));
            }
            lastMove = PartitionedRegionLoadModelJUnitTest.MoveType.MOVE_BUCKET;
            return true;
        }

        @Override
        public boolean removeBucket(InternalDistributedMember memberId, int id, Map<String, Long> colocatedRegionSizes) {
            removes.add(new PartitionedRegionLoadModelJUnitTest.Remove(memberId, id));
            if (PartitionedRegionLoadModelJUnitTest.DEBUG) {
                System.out.println(((("Moved bucket " + id) + " from ") + memberId));
            }
            lastMove = PartitionedRegionLoadModelJUnitTest.MoveType.REMOVE;
            return true;
        }
    }

    private static class BucketOperatorWithFailures extends PartitionedRegionLoadModelJUnitTest.MyBucketOperator {
        private List<Completion> pendingSuccesses = new ArrayList<Completion>();

        private List<Completion> pendingFailures = new ArrayList<Completion>();

        private Set<InternalDistributedMember> badMembers = new HashSet<InternalDistributedMember>();

        public void addBadMember(InternalDistributedMember member) {
            this.badMembers.add(member);
        }

        @Override
        public void createRedundantBucket(InternalDistributedMember targetMember, int i, Map<String, Long> colocatedRegionBytes, Completion completion) {
            if (badMembers.contains(targetMember)) {
                pendingFailures.add(completion);
            } else {
                super.createRedundantBucket(targetMember, i, colocatedRegionBytes, new Completion() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure() {
                    }
                });
                pendingSuccesses.add(completion);
            }
        }
    }

    private enum MoveType {

        CREATE,
        MOVE_PRIMARY,
        MOVE_BUCKET,
        REMOVE;}

    private static class FakeOfflineDetails implements OfflineMemberDetails {
        private Set<PersistentMemberID> offlineMembers;

        public FakeOfflineDetails() {
            this(Collections.<PersistentMemberID>emptySet());
        }

        public FakeOfflineDetails(Set<PersistentMemberID> offlineMembers) {
            this.offlineMembers = offlineMembers;
        }

        @Override
        public Set<PersistentMemberID> getOfflineMembers(int bucketId) {
            return this.offlineMembers;
        }

        @Override
        public void fromData(DataInput in) throws IOException, ClassNotFoundException {
            offlineMembers = DataSerializer.readObject(in);
        }

        @Override
        public void toData(DataOutput out) throws IOException {
            DataSerializer.writeObject(offlineMembers, out);
        }
    }
}

