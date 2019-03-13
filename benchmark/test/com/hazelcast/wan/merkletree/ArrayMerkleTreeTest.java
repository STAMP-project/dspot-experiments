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
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ArrayMerkleTreeTest {
    @Test(expected = IllegalArgumentException.class)
    public void testDepthBelowMinDepthThrows() {
        new ArrayMerkleTree(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDepthAboveMaxDepthThrows() {
        new ArrayMerkleTree(28);
    }

    @Test
    public void testDepth() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        Assert.assertEquals(3, merkleTree.depth());
    }

    @Test
    public void testFootprint() {
        MerkleTree merkleTree1 = new ArrayMerkleTree(3);
        MerkleTree merkleTree2 = new ArrayMerkleTree(3);
        for (int i = 0; i < 10; i++) {
            merkleTree1.updateAdd(i, i);
        }
        for (int i = 0; i < 100; i++) {
            merkleTree2.updateAdd(i, i);
        }
        Assert.assertTrue(((merkleTree2.footprint()) > (merkleTree1.footprint())));
    }

    @Test
    public void testFootprintChanges() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        long footprintBeforeAdd = merkleTree.footprint();
        for (int i = 0; i < 100; i++) {
            merkleTree.updateAdd(i, i);
        }
        long footprintAfterAdd = merkleTree.footprint();
        Assert.assertTrue((footprintAfterAdd > footprintBeforeAdd));
    }

    @Test
    public void testUpdateAdd() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd(1, 1);
        merkleTree.updateAdd(2, 2);
        merkleTree.updateAdd(3, 3);
        int expectedHash = 0;
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 1);
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 2);
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 3);
        int nodeHash = merkleTree.getNodeHash(5);
        Assert.assertEquals(expectedHash, nodeHash);
    }

    @Test
    public void testUpdateAddUpdateBranch() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd((-3), 4);
        merkleTree.updateAdd((-2), 2);
        merkleTree.updateAdd((-1), 1);
        merkleTree.updateAdd(0, 0);
        merkleTree.updateAdd(1, (-1));
        merkleTree.updateAdd(2, (-2));
        merkleTree.updateAdd(3, (-3));
        int expectedHashNode5 = 0;
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-1));
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-2));
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-3));
        int expectedHashNode4 = 0;
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 1);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 2);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 4);
        int expectedHashNode1 = MerkleTreeUtil.sumHash(0, expectedHashNode4);
        int expectedHashNode2 = MerkleTreeUtil.sumHash(0, expectedHashNode5);
        int expectedHashNode0 = MerkleTreeUtil.sumHash(expectedHashNode1, expectedHashNode2);
        Assert.assertEquals(expectedHashNode0, merkleTree.getNodeHash(0));
        Assert.assertEquals(expectedHashNode1, merkleTree.getNodeHash(1));
        Assert.assertEquals(expectedHashNode2, merkleTree.getNodeHash(2));
        Assert.assertEquals(0, merkleTree.getNodeHash(3));
        Assert.assertEquals(expectedHashNode4, merkleTree.getNodeHash(4));
        Assert.assertEquals(expectedHashNode5, merkleTree.getNodeHash(5));
        Assert.assertEquals(0, merkleTree.getNodeHash(6));
    }

    @Test
    public void testUpdateReplaceUpdateBranch() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd((-3), 4);
        merkleTree.updateAdd((-2), 2);
        merkleTree.updateAdd((-1), 1);
        merkleTree.updateAdd(0, 0);
        merkleTree.updateAdd(1, (-1));
        merkleTree.updateAdd(2, (-2));
        merkleTree.updateAdd(3, (-3));
        merkleTree.updateReplace(3, (-3), (-5));
        int expectedHashNode5 = 0;
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-1));
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-2));
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-5));
        int expectedHashNode4 = 0;
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 1);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 2);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 4);
        int expectedHashNode1 = MerkleTreeUtil.sumHash(0, expectedHashNode4);
        int expectedHashNode2 = MerkleTreeUtil.sumHash(0, expectedHashNode5);
        int expectedHashNode0 = MerkleTreeUtil.sumHash(expectedHashNode1, expectedHashNode2);
        Assert.assertEquals(expectedHashNode0, merkleTree.getNodeHash(0));
        Assert.assertEquals(expectedHashNode1, merkleTree.getNodeHash(1));
        Assert.assertEquals(expectedHashNode2, merkleTree.getNodeHash(2));
        Assert.assertEquals(0, merkleTree.getNodeHash(3));
        Assert.assertEquals(expectedHashNode4, merkleTree.getNodeHash(4));
        Assert.assertEquals(expectedHashNode5, merkleTree.getNodeHash(5));
        Assert.assertEquals(0, merkleTree.getNodeHash(6));
    }

    @Test
    public void testUpdateRemoveUpdateBranch() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd((-3), 4);
        merkleTree.updateAdd((-2), 2);
        merkleTree.updateAdd((-1), 1);
        merkleTree.updateAdd(0, 0);
        merkleTree.updateAdd(1, (-1));
        merkleTree.updateAdd(2, (-2));
        merkleTree.updateAdd(3, (-3));
        merkleTree.updateRemove(3, (-3));
        int expectedHashNode5 = 0;
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-1));
        expectedHashNode5 = MerkleTreeUtil.addHash(expectedHashNode5, (-2));
        int expectedHashNode4 = 0;
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 1);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 2);
        expectedHashNode4 = MerkleTreeUtil.addHash(expectedHashNode4, 4);
        int expectedHashNode1 = MerkleTreeUtil.sumHash(0, expectedHashNode4);
        int expectedHashNode2 = MerkleTreeUtil.sumHash(0, expectedHashNode5);
        int expectedHashNode0 = MerkleTreeUtil.sumHash(expectedHashNode1, expectedHashNode2);
        Assert.assertEquals(expectedHashNode0, merkleTree.getNodeHash(0));
        Assert.assertEquals(expectedHashNode1, merkleTree.getNodeHash(1));
        Assert.assertEquals(expectedHashNode2, merkleTree.getNodeHash(2));
        Assert.assertEquals(0, merkleTree.getNodeHash(3));
        Assert.assertEquals(expectedHashNode4, merkleTree.getNodeHash(4));
        Assert.assertEquals(expectedHashNode5, merkleTree.getNodeHash(5));
        Assert.assertEquals(0, merkleTree.getNodeHash(6));
    }

    @Test
    public void testUpdateReplace() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd(1, 1);
        merkleTree.updateAdd(2, 2);
        merkleTree.updateAdd(3, 3);
        merkleTree.updateReplace(2, 2, 4);
        int expectedHash = 0;
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 1);
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 3);
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 4);
        int nodeHash = merkleTree.getNodeHash(5);
        Assert.assertEquals(expectedHash, nodeHash);
    }

    @Test
    public void testUpdateRemove() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd(1, 1);
        merkleTree.updateAdd(2, 2);
        merkleTree.updateAdd(3, 3);
        merkleTree.updateRemove(2, 2);
        int expectedHash = 0;
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 1);
        expectedHash = MerkleTreeUtil.addHash(expectedHash, 3);
        int nodeHash = merkleTree.getNodeHash(5);
        Assert.assertEquals(expectedHash, nodeHash);
    }

    @Test
    public void forEachKeyOfLeaf() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd(-2147483648, 1);// leaf 3

        merkleTree.updateAdd(-1073741824, 2);// leaf 4

        merkleTree.updateAdd(0, 3);// leaf 5

        merkleTree.updateAdd(1073741824, 4);// leaf 6

        // level 0
        verifyKeysUnderNode(merkleTree, 0, -2147483648, -1073741824, 0, 1073741824);
        // level 1
        verifyKeysUnderNode(merkleTree, 1, -2147483648, -1073741824);
        verifyKeysUnderNode(merkleTree, 2, 0, 1073741824);
        // level 2 (leaves)
        verifyKeysUnderNode(merkleTree, 3, -2147483648);
        verifyKeysUnderNode(merkleTree, 4, -1073741824);
        verifyKeysUnderNode(merkleTree, 5, 0);
        verifyKeysUnderNode(merkleTree, 6, 1073741824);
    }

    @Test
    public void getNodeKeyCount() {
        MerkleTree merkleTree = new ArrayMerkleTree(3);
        merkleTree.updateAdd(-2147483648, 1);// leaf 3

        merkleTree.updateAdd(-1073741824, 2);// leaf 4

        merkleTree.updateAdd(0, 3);// leaf 5

        merkleTree.updateAdd(1073741824, 4);// leaf 6

        // level 0
        Assert.assertEquals(4, merkleTree.getNodeKeyCount(0));
        // level 1
        Assert.assertEquals(2, merkleTree.getNodeKeyCount(1));
        Assert.assertEquals(2, merkleTree.getNodeKeyCount(2));
        // level 2 (leaves)
        Assert.assertEquals(1, merkleTree.getNodeKeyCount(3));
        Assert.assertEquals(1, merkleTree.getNodeKeyCount(4));
        Assert.assertEquals(1, merkleTree.getNodeKeyCount(5));
        Assert.assertEquals(1, merkleTree.getNodeKeyCount(6));
    }

    @Test
    public void testTreeDepthsDontImpactNodeHashes() {
        MerkleTree merkleTreeShallow = new ArrayMerkleTree(2);
        MerkleTree merkleTreeDeep = new ArrayMerkleTree(4);
        merkleTreeShallow.updateAdd(-2147483648, 1);// leaf 1

        merkleTreeShallow.updateAdd(-1610612736, 2);// leaf 1

        merkleTreeShallow.updateAdd(-1073741824, 3);// leaf 1

        merkleTreeShallow.updateAdd(-536870912, 4);// leaf 1

        merkleTreeShallow.updateAdd(0, 5);// leaf 2

        merkleTreeShallow.updateAdd(536870912, 6);// leaf 2

        merkleTreeShallow.updateAdd(1073741824, 7);// leaf 2

        merkleTreeShallow.updateAdd(1610612736, 8);// leaf 2

        merkleTreeDeep.updateAdd(-2147483648, 1);// leaf 7

        merkleTreeDeep.updateAdd(-1610612736, 2);// leaf 8

        merkleTreeDeep.updateAdd(-1073741824, 3);// leaf 9

        merkleTreeDeep.updateAdd(-536870912, 4);// leaf 10

        merkleTreeDeep.updateAdd(0, 5);// leaf 11

        merkleTreeDeep.updateAdd(536870912, 6);// leaf 12

        merkleTreeDeep.updateAdd(1073741824, 7);// leaf 13

        merkleTreeDeep.updateAdd(1610612736, 8);// leaf 14

        verifyTreesAreSameOnCommonLevels(merkleTreeShallow, merkleTreeDeep);
        merkleTreeShallow.updateReplace(-536870912, 4, 42);
        merkleTreeDeep.updateReplace(-536870912, 4, 42);
        verifyTreesAreSameOnCommonLevels(merkleTreeShallow, merkleTreeDeep);
        merkleTreeShallow.updateRemove(-536870912, 42);
        merkleTreeDeep.updateRemove(-536870912, 42);
        verifyTreesAreSameOnCommonLevels(merkleTreeShallow, merkleTreeDeep);
    }

    @Test
    public void testClear() {
        MerkleTree merkleTree = new ArrayMerkleTree(4);
        merkleTree.updateAdd(-2147483648, 1);// leaf 7

        merkleTree.updateAdd(-1610612736, 2);// leaf 8

        merkleTree.updateAdd(-1073741824, 3);// leaf 9

        merkleTree.updateAdd(-536870912, 4);// leaf 10

        merkleTree.updateAdd(0, 5);// leaf 11

        merkleTree.updateAdd(536870912, 6);// leaf 12

        merkleTree.updateAdd(1073741824, 7);// leaf 13

        merkleTree.updateAdd(1610612736, 8);// leaf 14

        Assert.assertNotEquals(0, merkleTree.getNodeHash(0));
        merkleTree.clear();
        for (int nodeOrder = 0; nodeOrder < (MerkleTreeUtil.getNumberOfNodes(merkleTree.depth())); nodeOrder++) {
            Assert.assertEquals(0, merkleTree.getNodeHash(nodeOrder));
        }
        merkleTree.forEachKeyOfNode(0, new com.hazelcast.util.function.Consumer<Object>() {
            @Override
            public void accept(Object o) {
                Assert.fail("Consumer is not expected to be invoked. Leaf keys should be empty.");
            }
        });
    }

    private static class KeyCatcherConsumer implements com.hazelcast.util.function.Consumer<Object> {
        private Set<Object> keys = new HashSet<Object>();

        @Override
        public void accept(Object key) {
            keys.add(key);
        }
    }
}

