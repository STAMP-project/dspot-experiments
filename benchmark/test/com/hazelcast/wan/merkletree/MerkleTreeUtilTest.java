/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.wan.merkletree;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MerkleTreeUtilTest {
    @Test
    public void testGetLevelOfNode() {
        Assert.assertEquals(0, MerkleTreeUtil.getLevelOfNode(0));
        Assert.assertEquals(1, MerkleTreeUtil.getLevelOfNode(1));
        Assert.assertEquals(1, MerkleTreeUtil.getLevelOfNode(2));
        Assert.assertEquals(2, MerkleTreeUtil.getLevelOfNode(3));
        Assert.assertEquals(2, MerkleTreeUtil.getLevelOfNode(6));
        Assert.assertEquals(3, MerkleTreeUtil.getLevelOfNode(7));
        Assert.assertEquals(3, MerkleTreeUtil.getLevelOfNode(14));
        Assert.assertEquals(4, MerkleTreeUtil.getLevelOfNode(15));
        Assert.assertEquals(4, MerkleTreeUtil.getLevelOfNode(30));
    }

    @Test
    public void testGetNodesOnLevel() {
        Assert.assertEquals(1, MerkleTreeUtil.getNodesOnLevel(0));
        Assert.assertEquals(2, MerkleTreeUtil.getNodesOnLevel(1));
        Assert.assertEquals(4, MerkleTreeUtil.getNodesOnLevel(2));
        Assert.assertEquals(8, MerkleTreeUtil.getNodesOnLevel(3));
        Assert.assertEquals(16, MerkleTreeUtil.getNodesOnLevel(4));
    }

    @Test
    public void testGetLeftMostNodeOnLevel() {
        Assert.assertEquals(0, MerkleTreeUtil.getLeftMostNodeOrderOnLevel(0));
        Assert.assertEquals(1, MerkleTreeUtil.getLeftMostNodeOrderOnLevel(1));
        Assert.assertEquals(3, MerkleTreeUtil.getLeftMostNodeOrderOnLevel(2));
        Assert.assertEquals(7, MerkleTreeUtil.getLeftMostNodeOrderOnLevel(3));
        Assert.assertEquals(15, MerkleTreeUtil.getLeftMostNodeOrderOnLevel(4));
    }

    @Test
    public void testGetParentOrder() {
        Assert.assertEquals(0, MerkleTreeUtil.getParentOrder(1));
        Assert.assertEquals(0, MerkleTreeUtil.getParentOrder(2));
        Assert.assertEquals(1, MerkleTreeUtil.getParentOrder(3));
        Assert.assertEquals(1, MerkleTreeUtil.getParentOrder(4));
        Assert.assertEquals(2, MerkleTreeUtil.getParentOrder(5));
        Assert.assertEquals(2, MerkleTreeUtil.getParentOrder(6));
    }

    @Test
    public void testLeftChildOrder() {
        Assert.assertEquals(1, MerkleTreeUtil.getLeftChildOrder(0));
        Assert.assertEquals(3, MerkleTreeUtil.getLeftChildOrder(1));
        Assert.assertEquals(5, MerkleTreeUtil.getLeftChildOrder(2));
        Assert.assertEquals(7, MerkleTreeUtil.getLeftChildOrder(3));
        Assert.assertEquals(11, MerkleTreeUtil.getLeftChildOrder(5));
    }

    @Test
    public void testRightChildOrder() {
        Assert.assertEquals(2, MerkleTreeUtil.getRightChildOrder(0));
        Assert.assertEquals(4, MerkleTreeUtil.getRightChildOrder(1));
        Assert.assertEquals(6, MerkleTreeUtil.getRightChildOrder(2));
        Assert.assertEquals(8, MerkleTreeUtil.getRightChildOrder(3));
        Assert.assertEquals(12, MerkleTreeUtil.getRightChildOrder(5));
    }

    @Test
    public void testGetHashStepForLevel() {
        Assert.assertEquals((1L << 32), MerkleTreeUtil.getNodeHashRangeOnLevel(0));
        Assert.assertEquals((1L << 31), MerkleTreeUtil.getNodeHashRangeOnLevel(1));
        Assert.assertEquals((1L << 30), MerkleTreeUtil.getNodeHashRangeOnLevel(2));
        Assert.assertEquals((1L << 29), MerkleTreeUtil.getNodeHashRangeOnLevel(3));
        Assert.assertEquals((1L << 28), MerkleTreeUtil.getNodeHashRangeOnLevel(4));
    }

    @Test
    public void testGetLeafOrderForHash() {
        // Integer.MIN_VALUE
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(-2147483648, 0));
        Assert.assertEquals(1, MerkleTreeUtil.getLeafOrderForHash(-2147483648, 1));
        Assert.assertEquals(3, MerkleTreeUtil.getLeafOrderForHash(-2147483648, 2));
        Assert.assertEquals(7, MerkleTreeUtil.getLeafOrderForHash(-2147483648, 3));
        // Integer.MAX_VALUE
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(2147483647, 0));
        Assert.assertEquals(2, MerkleTreeUtil.getLeafOrderForHash(2147483647, 1));
        Assert.assertEquals(6, MerkleTreeUtil.getLeafOrderForHash(2147483647, 2));
        Assert.assertEquals(14, MerkleTreeUtil.getLeafOrderForHash(2147483647, 3));
        // 0
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(0, 0));
        Assert.assertEquals(2, MerkleTreeUtil.getLeafOrderForHash(0, 1));
        Assert.assertEquals(5, MerkleTreeUtil.getLeafOrderForHash(0, 2));
        Assert.assertEquals(11, MerkleTreeUtil.getLeafOrderForHash(0, 3));
        // 1
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(1, 0));
        Assert.assertEquals(2, MerkleTreeUtil.getLeafOrderForHash(1, 1));
        Assert.assertEquals(5, MerkleTreeUtil.getLeafOrderForHash(1, 2));
        Assert.assertEquals(11, MerkleTreeUtil.getLeafOrderForHash(1, 3));
        // -1
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(-1, 0));
        Assert.assertEquals(1, MerkleTreeUtil.getLeafOrderForHash(-1, 1));
        Assert.assertEquals(4, MerkleTreeUtil.getLeafOrderForHash(-1, 2));
        Assert.assertEquals(10, MerkleTreeUtil.getLeafOrderForHash(-1, 3));
        // Integer.MIN_VALUE / 4 * 2 - 1
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(-1073741825, 0));
        Assert.assertEquals(1, MerkleTreeUtil.getLeafOrderForHash(-1073741825, 1));
        Assert.assertEquals(3, MerkleTreeUtil.getLeafOrderForHash(-1073741825, 2));
        Assert.assertEquals(8, MerkleTreeUtil.getLeafOrderForHash(-1073741825, 3));
        // Integer.MIN_VALUE / 4 * 2
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(-1073741824, 0));
        Assert.assertEquals(1, MerkleTreeUtil.getLeafOrderForHash(-1073741824, 1));
        Assert.assertEquals(4, MerkleTreeUtil.getLeafOrderForHash(-1073741824, 2));
        Assert.assertEquals(9, MerkleTreeUtil.getLeafOrderForHash(-1073741824, 3));
        // Integer.MAX_VALUE / 4 * 2 + 1
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(1073741823, 0));
        Assert.assertEquals(2, MerkleTreeUtil.getLeafOrderForHash(1073741823, 1));
        Assert.assertEquals(5, MerkleTreeUtil.getLeafOrderForHash(1073741823, 2));
        Assert.assertEquals(12, MerkleTreeUtil.getLeafOrderForHash(1073741823, 3));
        // Integer.MAX_VALUE / 4 * 2 + 2
        Assert.assertEquals(0, MerkleTreeUtil.getLeafOrderForHash(1073741824, 0));
        Assert.assertEquals(2, MerkleTreeUtil.getLeafOrderForHash(1073741824, 1));
        Assert.assertEquals(6, MerkleTreeUtil.getLeafOrderForHash(1073741824, 2));
        Assert.assertEquals(13, MerkleTreeUtil.getLeafOrderForHash(1073741824, 3));
    }

    @Test
    public void testGetNodeRanges() {
        // level 0
        Assert.assertEquals(-2147483648, MerkleTreeUtil.getNodeRangeLow(0));
        Assert.assertEquals(2147483647, MerkleTreeUtil.getNodeRangeHigh(0));
        // level 1
        Assert.assertEquals(-2147483648, MerkleTreeUtil.getNodeRangeLow(1));
        Assert.assertEquals(-1, MerkleTreeUtil.getNodeRangeHigh(1));
        Assert.assertEquals(0, MerkleTreeUtil.getNodeRangeLow(2));
        Assert.assertEquals(2147483647, MerkleTreeUtil.getNodeRangeHigh(2));
        // level 2
        Assert.assertEquals(-2147483648, MerkleTreeUtil.getNodeRangeLow(3));
        Assert.assertEquals(-1073741825, MerkleTreeUtil.getNodeRangeHigh(3));
        Assert.assertEquals(-1073741824, MerkleTreeUtil.getNodeRangeLow(4));
        Assert.assertEquals(-1, MerkleTreeUtil.getNodeRangeHigh(4));
        Assert.assertEquals(0, MerkleTreeUtil.getNodeRangeLow(5));
        Assert.assertEquals(1073741823, MerkleTreeUtil.getNodeRangeHigh(5));
        Assert.assertEquals(1073741824, MerkleTreeUtil.getNodeRangeLow(6));
        Assert.assertEquals(2147483647, MerkleTreeUtil.getNodeRangeHigh(6));
        // level 3
        Assert.assertEquals(-2147483648, MerkleTreeUtil.getNodeRangeLow(7));
        Assert.assertEquals(-1610612737, MerkleTreeUtil.getNodeRangeHigh(7));
        Assert.assertEquals(-1610612736, MerkleTreeUtil.getNodeRangeLow(8));
        Assert.assertEquals(-1073741825, MerkleTreeUtil.getNodeRangeHigh(8));
        Assert.assertEquals(-1073741824, MerkleTreeUtil.getNodeRangeLow(9));
        Assert.assertEquals(-536870913, MerkleTreeUtil.getNodeRangeHigh(9));
        Assert.assertEquals(-536870912, MerkleTreeUtil.getNodeRangeLow(10));
        Assert.assertEquals(-1, MerkleTreeUtil.getNodeRangeHigh(10));
        Assert.assertEquals(0, MerkleTreeUtil.getNodeRangeLow(11));
        Assert.assertEquals(536870911, MerkleTreeUtil.getNodeRangeHigh(11));
        Assert.assertEquals(536870912, MerkleTreeUtil.getNodeRangeLow(12));
        Assert.assertEquals(1073741823, MerkleTreeUtil.getNodeRangeHigh(12));
        Assert.assertEquals(1073741824, MerkleTreeUtil.getNodeRangeLow(13));
        Assert.assertEquals(1610612735, MerkleTreeUtil.getNodeRangeHigh(13));
        Assert.assertEquals(1610612736, MerkleTreeUtil.getNodeRangeLow(14));
        Assert.assertEquals(2147483647, MerkleTreeUtil.getNodeRangeHigh(14));
    }

    @Test
    public void testAddHashIsAssociative() {
        int hash = MerkleTreeUtil.addHash(0, 1);
        hash = MerkleTreeUtil.addHash(hash, 2);
        hash = MerkleTreeUtil.addHash(hash, 3);
        int hash2 = MerkleTreeUtil.addHash(0, 3);
        hash2 = MerkleTreeUtil.addHash(hash2, 1);
        hash2 = MerkleTreeUtil.addHash(hash2, 2);
        Assert.assertEquals(hash2, hash);
    }

    @Test
    public void testRemoveHashIsAssociative() {
        int hash = MerkleTreeUtil.addHash(0, 1);
        hash = MerkleTreeUtil.addHash(hash, 2);
        hash = MerkleTreeUtil.addHash(hash, 3);
        hash = MerkleTreeUtil.addHash(hash, 4);
        hash = MerkleTreeUtil.addHash(hash, 5);
        int hash2 = hash;
        hash = MerkleTreeUtil.removeHash(hash, 5);
        hash = MerkleTreeUtil.removeHash(hash, 4);
        hash = MerkleTreeUtil.removeHash(hash, 3);
        hash = MerkleTreeUtil.removeHash(hash, 2);
        hash2 = MerkleTreeUtil.removeHash(hash2, 2);
        hash2 = MerkleTreeUtil.removeHash(hash2, 5);
        hash2 = MerkleTreeUtil.removeHash(hash2, 3);
        hash2 = MerkleTreeUtil.removeHash(hash2, 4);
        Assert.assertEquals(hash2, hash);
    }

    @Test
    public void testRemovingHashAndNotAddingHashResultsTheSame() {
        int hash = MerkleTreeUtil.addHash(0, 1);
        hash = MerkleTreeUtil.addHash(hash, 2);
        hash = MerkleTreeUtil.addHash(hash, 3);
        hash = MerkleTreeUtil.addHash(hash, 4);
        hash = MerkleTreeUtil.addHash(hash, 5);
        int hash2 = MerkleTreeUtil.addHash(0, 1);
        hash2 = MerkleTreeUtil.addHash(hash2, 2);
        // not adding 3 here
        hash2 = MerkleTreeUtil.addHash(hash2, 4);
        hash2 = MerkleTreeUtil.addHash(hash2, 5);
        // removing hash of 3
        hash = MerkleTreeUtil.removeHash(hash, 3);
        Assert.assertEquals(hash2, hash);
    }

    @Test
    public void testIsLeaf() {
        Assert.assertTrue(MerkleTreeUtil.isLeaf(0, 1));
        Assert.assertFalse(MerkleTreeUtil.isLeaf(0, 2));
        Assert.assertTrue(MerkleTreeUtil.isLeaf(1, 2));
        Assert.assertTrue(MerkleTreeUtil.isLeaf(2, 2));
        Assert.assertFalse(MerkleTreeUtil.isLeaf(1, 3));
        Assert.assertFalse(MerkleTreeUtil.isLeaf(2, 3));
        Assert.assertTrue(MerkleTreeUtil.isLeaf(3, 3));
        Assert.assertTrue(MerkleTreeUtil.isLeaf(6, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsLeafThrowsOnInvalidDepth() {
        MerkleTreeUtil.isLeaf(0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsLeafThrowsOnNegativeNodeOrder() {
        MerkleTreeUtil.isLeaf((-1), 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsLeafThrowsOnNodeOrderBeyondMaxNodeOrder() {
        MerkleTreeUtil.isLeaf(7, 3);
    }

    @Test
    public void testGetLeftMostLeafUnderNode() {
        // depth 1
        Assert.assertEquals(0, MerkleTreeUtil.getLeftMostLeafUnderNode(0, 1));
        // depth 2
        Assert.assertEquals(1, MerkleTreeUtil.getLeftMostLeafUnderNode(0, 2));
        Assert.assertEquals(1, MerkleTreeUtil.getLeftMostLeafUnderNode(1, 2));
        Assert.assertEquals(2, MerkleTreeUtil.getLeftMostLeafUnderNode(2, 2));
        // depth 3
        Assert.assertEquals(3, MerkleTreeUtil.getLeftMostLeafUnderNode(0, 3));
        Assert.assertEquals(3, MerkleTreeUtil.getLeftMostLeafUnderNode(1, 3));
        Assert.assertEquals(5, MerkleTreeUtil.getLeftMostLeafUnderNode(2, 3));
        // depth 4
        Assert.assertEquals(7, MerkleTreeUtil.getLeftMostLeafUnderNode(0, 4));
        Assert.assertEquals(7, MerkleTreeUtil.getLeftMostLeafUnderNode(1, 4));
        Assert.assertEquals(11, MerkleTreeUtil.getLeftMostLeafUnderNode(2, 4));
        Assert.assertEquals(7, MerkleTreeUtil.getLeftMostLeafUnderNode(3, 4));
        Assert.assertEquals(9, MerkleTreeUtil.getLeftMostLeafUnderNode(4, 4));
        Assert.assertEquals(11, MerkleTreeUtil.getLeftMostLeafUnderNode(5, 4));
        Assert.assertEquals(13, MerkleTreeUtil.getLeftMostLeafUnderNode(6, 4));
    }

    @Test
    public void testGetRightMostLeafUnderNode() {
        // depth 1
        Assert.assertEquals(0, MerkleTreeUtil.getRightMostLeafUnderNode(0, 1));
        // depth 2
        Assert.assertEquals(2, MerkleTreeUtil.getRightMostLeafUnderNode(0, 2));
        Assert.assertEquals(1, MerkleTreeUtil.getRightMostLeafUnderNode(1, 2));
        Assert.assertEquals(2, MerkleTreeUtil.getRightMostLeafUnderNode(2, 2));
        // depth 3
        Assert.assertEquals(6, MerkleTreeUtil.getRightMostLeafUnderNode(0, 3));
        Assert.assertEquals(4, MerkleTreeUtil.getRightMostLeafUnderNode(1, 3));
        Assert.assertEquals(6, MerkleTreeUtil.getRightMostLeafUnderNode(2, 3));
        // depth 4
        Assert.assertEquals(14, MerkleTreeUtil.getRightMostLeafUnderNode(0, 4));
        Assert.assertEquals(10, MerkleTreeUtil.getRightMostLeafUnderNode(1, 4));
        Assert.assertEquals(14, MerkleTreeUtil.getRightMostLeafUnderNode(2, 4));
        Assert.assertEquals(8, MerkleTreeUtil.getRightMostLeafUnderNode(3, 4));
        Assert.assertEquals(10, MerkleTreeUtil.getRightMostLeafUnderNode(4, 4));
        Assert.assertEquals(12, MerkleTreeUtil.getRightMostLeafUnderNode(5, 4));
        Assert.assertEquals(14, MerkleTreeUtil.getRightMostLeafUnderNode(6, 4));
    }

    @Test
    public void testCompareIdenticalTrees() {
        int[] tree = new int[4];
        for (int i = 0; i < 4; i++) {
            tree[i] = 4 - i;
        }
        MerkleTreeView localTree = new RemoteMerkleTreeView(tree, 3);
        MerkleTreeView remoteTree = new RemoteMerkleTreeView(tree, 3);
        Collection<Integer> deltaOrders = MerkleTreeUtil.compareTrees(localTree, remoteTree);
        Assert.assertTrue(deltaOrders.isEmpty());
    }

    @Test
    public void testCompareDifferentTreesLocalDeeper() {
        int numberOfLocalTreeLeaves = 15;
        int[] localTreeLeaves = new int[numberOfLocalTreeLeaves];
        for (int i = 0; i < numberOfLocalTreeLeaves; i++) {
            localTreeLeaves[i] = i;
        }
        int numberOfRemoteTreeNodes = 7;
        int[] remoteTreeLeaves = new int[numberOfRemoteTreeNodes];
        remoteTreeLeaves[0] = MerkleTreeUtil.sumHash(0, 1);
        remoteTreeLeaves[1] = MerkleTreeUtil.sumHash(2, 3);
        remoteTreeLeaves[2] = MerkleTreeUtil.sumHash(42, 5);
        remoteTreeLeaves[3] = MerkleTreeUtil.sumHash(6, 7);
        MerkleTreeView localTreeView = new RemoteMerkleTreeView(localTreeLeaves, 4);
        MerkleTreeView remoteTreeView = new RemoteMerkleTreeView(remoteTreeLeaves, 3);
        Collection<Integer> deltaOrders = MerkleTreeUtil.compareTrees(localTreeView, remoteTreeView);
        Assert.assertEquals(1, deltaOrders.size());
        Assert.assertTrue(deltaOrders.contains(5));
    }

    @Test
    public void testCompareDifferentTreesRemoteDeeper() {
        int numberOfLocalTreeLeaves = 4;
        int[] localTreeLeaves = new int[numberOfLocalTreeLeaves];
        localTreeLeaves[0] = MerkleTreeUtil.sumHash(0, 1);
        localTreeLeaves[1] = MerkleTreeUtil.sumHash(2, 3);
        localTreeLeaves[2] = MerkleTreeUtil.sumHash(42, 5);
        localTreeLeaves[3] = MerkleTreeUtil.sumHash(6, 7);
        int numberOfRemoteTreeNodes = 8;
        int[] remoteTreeLeaves = new int[numberOfRemoteTreeNodes];
        for (int i = 0; i < numberOfRemoteTreeNodes; i++) {
            remoteTreeLeaves[i] = i;
        }
        MerkleTreeView localTreeView = new RemoteMerkleTreeView(localTreeLeaves, 3);
        MerkleTreeView remoteTreeView = new RemoteMerkleTreeView(remoteTreeLeaves, 4);
        Collection<Integer> deltaOrders = MerkleTreeUtil.compareTrees(localTreeView, remoteTreeView);
        Assert.assertEquals(1, deltaOrders.size());
        Assert.assertTrue(deltaOrders.contains(5));
    }

    @Test
    public void testCompareTreesCatchesCollision() {
        int numberOfLocalTreeLeaves = 4;
        int[] localTreeLeaves = new int[numberOfLocalTreeLeaves];
        for (int i = 0; i < numberOfLocalTreeLeaves; i++) {
            localTreeLeaves[i] = i;
        }
        int numberOfRemoteTreeNodes = 4;
        int[] remoteTreeLeaves = new int[numberOfRemoteTreeNodes];
        for (int i = 0; i < numberOfRemoteTreeNodes; i++) {
            remoteTreeLeaves[i] = i;
        }
        // we cause a collision here that compareTrees() will notice
        // hash(node5,node6) will produce the same hash for node2 in both trees
        // localTreeLeaves: hash(2,3)=5
        // remoteTreeLeaves: hash(1,4)=5
        localTreeLeaves[2] = 1;
        localTreeLeaves[3] = 4;
        MerkleTreeView localTreeView = new RemoteMerkleTreeView(localTreeLeaves, 3);
        MerkleTreeView remoteTreeView = new RemoteMerkleTreeView(remoteTreeLeaves, 3);
        Collection<Integer> deltaOrders = MerkleTreeUtil.compareTrees(localTreeView, remoteTreeView);
        Assert.assertEquals(localTreeView.getNodeHash(0), remoteTreeView.getNodeHash(0));
        Assert.assertEquals(localTreeView.getNodeHash(2), remoteTreeView.getNodeHash(2));
        Assert.assertEquals(2, deltaOrders.size());
        Assert.assertTrue(deltaOrders.containsAll(Arrays.asList(5, 6)));
    }

    @Test
    public void testSerialization() throws IOException {
        MerkleTree merkleTree = new ArrayMerkleTree(4);
        merkleTree.updateAdd(-2147483648, 1);// leaf 7

        merkleTree.updateAdd(-1610612736, 2);// leaf 8

        merkleTree.updateAdd(-1073741824, 3);// leaf 9

        merkleTree.updateAdd(-536870912, 4);// leaf 10

        merkleTree.updateAdd(0, 5);// leaf 11

        merkleTree.updateAdd(536870912, 6);// leaf 12

        merkleTree.updateAdd(1073741824, 7);// leaf 13

        merkleTree.updateAdd(1610612736, 8);// leaf 14

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(outputStream);
        MerkleTreeUtil.writeLeaves(out, merkleTree);
        byte[] bytes = outputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(inputStream);
        RemoteMerkleTreeView remoteMerkleTreeView = MerkleTreeUtil.createRemoteMerkleTreeView(in);
        Collection<Integer> deltaOrders = MerkleTreeUtil.compareTrees(merkleTree, remoteMerkleTreeView);
        Assert.assertTrue(deltaOrders.isEmpty());
    }
}

