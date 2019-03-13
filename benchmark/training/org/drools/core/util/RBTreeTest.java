/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util;


import RBTree.Boundary.LOWER;
import RBTree.Boundary.UPPER;
import java.util.Random;
import org.drools.core.util.RBTree.Node;
import org.junit.Assert;
import org.junit.Test;


public class RBTreeTest {
    @Test
    public void testFindNearestNode() {
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        tree.insert(10, ("" + 10));
        tree.insert(20, ("" + 20));
        tree.insert(25, ("" + 25));
        tree.insert(15, ("" + 15));
        tree.insert(5, ("" + 5));
        Assert.assertEquals(5, ((int) (tree.findNearestNode(2, false, LOWER).key)));
        Assert.assertEquals(null, tree.findNearestNode(2, false, UPPER));
        Assert.assertEquals(5, ((int) (tree.findNearestNode(2, true, LOWER).key)));
        Assert.assertEquals(null, tree.findNearestNode(2, true, UPPER));
        Assert.assertEquals(10, ((int) (tree.findNearestNode(5, false, LOWER).key)));
        Assert.assertEquals(null, tree.findNearestNode(5, false, UPPER));
        Assert.assertEquals(5, ((int) (tree.findNearestNode(5, true, LOWER).key)));
        Assert.assertEquals(5, ((int) (tree.findNearestNode(5, true, UPPER).key)));
        Assert.assertEquals(15, ((int) (tree.findNearestNode(12, false, LOWER).key)));
        Assert.assertEquals(10, ((int) (tree.findNearestNode(12, false, UPPER).key)));
        Assert.assertEquals(20, ((int) (tree.findNearestNode(15, false, LOWER).key)));
        Assert.assertEquals(10, ((int) (tree.findNearestNode(15, false, UPPER).key)));
        Assert.assertEquals(15, ((int) (tree.findNearestNode(15, true, UPPER).key)));
        Assert.assertEquals(15, ((int) (tree.findNearestNode(15, true, LOWER).key)));
        Assert.assertEquals(20, ((int) (tree.findNearestNode(25, false, UPPER).key)));
        Assert.assertEquals(null, tree.findNearestNode(25, false, LOWER));
        Assert.assertEquals(25, ((int) (tree.findNearestNode(25, true, LOWER).key)));
        Assert.assertEquals(25, ((int) (tree.findNearestNode(25, true, UPPER).key)));
        Assert.assertEquals(25, ((int) (tree.findNearestNode(27, false, UPPER).key)));
        Assert.assertEquals(null, tree.findNearestNode(27, false, LOWER));
        Assert.assertEquals(25, ((int) (tree.findNearestNode(27, true, UPPER).key)));
        Assert.assertEquals(null, tree.findNearestNode(27, true, LOWER));
    }

    @Test
    public void testRange() {
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        tree.insert(10, ("" + 10));
        tree.insert(20, ("" + 20));
        tree.insert(25, ("" + 25));
        tree.insert(15, ("" + 15));
        tree.insert(5, ("" + 5));
        FastIterator fastIterator = tree.range(2, true, 15, false);
        Node<Integer, String> node = ((Node<Integer, String>) (fastIterator.next(null)));
        Assert.assertEquals(5, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertEquals(10, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertNull(node);
        fastIterator = tree.range(2, true, 5, false);
        node = ((Node<Integer, String>) (fastIterator.next(null)));
        Assert.assertNull(node);
        fastIterator = tree.range(5, false, 35, false);
        node = ((Node<Integer, String>) (fastIterator.next(null)));
        Assert.assertEquals(10, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertEquals(15, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertEquals(20, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertEquals(25, ((int) (node.key)));
        node = ((Node<Integer, String>) (fastIterator.next(node)));
        Assert.assertNull(node);
    }

    @Test
    public void testIterator() {
        final int ITEMS = 10000;
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        Random random = new Random(0);
        for (int i = 0; i < ITEMS; i++) {
            int key = random.nextInt();
            tree.insert(key, ("" + key));
        }
        int i = 0;
        FastIterator fastIterator = tree.fastIterator();
        int lastKey = Integer.MIN_VALUE;
        for (Node<Integer, String> node = ((Node<Integer, String>) (fastIterator.next(null))); node != null; node = ((Node<Integer, String>) (fastIterator.next(node)))) {
            int currentKey = node.key;
            if (currentKey < lastKey) {
                Assert.fail(((currentKey + " should be greater than ") + lastKey));
            }
            lastKey = currentKey;
            i++;
        }
        Assert.assertEquals(ITEMS, i);
    }
}

