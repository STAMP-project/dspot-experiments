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
package org.apache.hadoop.hbase.util;


import java.util.Random;
import java.util.TreeMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.AvlUtil.AvlIterableList;
import org.apache.hadoop.hbase.util.AvlUtil.AvlKeyComparator;
import org.apache.hadoop.hbase.util.AvlUtil.AvlLinkedNode;
import org.apache.hadoop.hbase.util.AvlUtil.AvlNode;
import org.apache.hadoop.hbase.util.AvlUtil.AvlTree;
import org.apache.hadoop.hbase.util.AvlUtil.AvlTreeIterator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestAvlUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAvlUtil.class);

    private static final TestAvlUtil.TestAvlKeyComparator KEY_COMPARATOR = new TestAvlUtil.TestAvlKeyComparator();

    @Test
    public void testAvlTreeCrud() {
        final int MAX_KEY = 99999999;
        final int NELEM = 10000;
        final TreeMap<Integer, Object> treeMap = new TreeMap<>();
        TestAvlUtil.TestAvlNode root = null;
        final Random rand = new Random();
        for (int i = 0; i < NELEM; ++i) {
            int key = rand.nextInt(MAX_KEY);
            if ((AvlTree.get(root, key, TestAvlUtil.KEY_COMPARATOR)) != null) {
                i--;
                continue;
            }
            root = AvlTree.insert(root, new TestAvlUtil.TestAvlNode(key));
            treeMap.put(key, null);
            for (Integer keyX : treeMap.keySet()) {
                TestAvlUtil.TestAvlNode node = AvlTree.get(root, keyX, TestAvlUtil.KEY_COMPARATOR);
                Assert.assertNotNull(node);
                Assert.assertEquals(keyX.intValue(), node.getKey());
            }
        }
        for (int i = 0; i < NELEM; ++i) {
            int key = rand.nextInt(MAX_KEY);
            TestAvlUtil.TestAvlNode node = AvlTree.get(root, key, TestAvlUtil.KEY_COMPARATOR);
            if (!(treeMap.containsKey(key))) {
                assert node == null;
                continue;
            }
            treeMap.remove(key);
            Assert.assertEquals(key, node.getKey());
            root = AvlTree.remove(root, key, TestAvlUtil.KEY_COMPARATOR);
            for (Integer keyX : treeMap.keySet()) {
                node = AvlTree.get(root, keyX, TestAvlUtil.KEY_COMPARATOR);
                Assert.assertNotNull(node);
                Assert.assertEquals(keyX.intValue(), node.getKey());
            }
        }
    }

    @Test
    public void testAvlTreeVisitor() {
        final int MIN_KEY = 0;
        final int MAX_KEY = 50;
        TestAvlUtil.TestAvlNode root = null;
        for (int i = MAX_KEY; i >= MIN_KEY; --i) {
            root = AvlTree.insert(root, new TestAvlUtil.TestAvlNode(i));
        }
        AvlTree.visit(root, new org.apache.hadoop.hbase.util.AvlUtil.AvlNodeVisitor<TestAvlUtil.TestAvlNode>() {
            private int prevKey = -1;

            @Override
            public boolean visitNode(TestAvlUtil.TestAvlNode node) {
                Assert.assertEquals(prevKey, ((node.getKey()) - 1));
                Assert.assertTrue(((node.getKey()) >= MIN_KEY));
                Assert.assertTrue(((node.getKey()) <= MAX_KEY));
                prevKey = node.getKey();
                return (node.getKey()) <= MAX_KEY;
            }
        });
    }

    @Test
    public void testAvlTreeIterSeekFirst() {
        final int MIN_KEY = 1;
        final int MAX_KEY = 50;
        TestAvlUtil.TestAvlNode root = null;
        for (int i = MIN_KEY; i < MAX_KEY; ++i) {
            root = AvlTree.insert(root, new TestAvlUtil.TestAvlNode(i));
        }
        AvlTreeIterator<TestAvlUtil.TestAvlNode> iter = new AvlTreeIterator(root);
        Assert.assertTrue(iter.hasNext());
        long prevKey = 0;
        while (iter.hasNext()) {
            TestAvlUtil.TestAvlNode node = iter.next();
            Assert.assertEquals((prevKey + 1), node.getKey());
            prevKey = node.getKey();
        } 
        Assert.assertEquals((MAX_KEY - 1), prevKey);
    }

    @Test
    public void testAvlTreeIterSeekTo() {
        final int MIN_KEY = 1;
        final int MAX_KEY = 50;
        TestAvlUtil.TestAvlNode root = null;
        for (int i = MIN_KEY; i < MAX_KEY; i += 2) {
            root = AvlTree.insert(root, new TestAvlUtil.TestAvlNode(i));
        }
        for (int i = MIN_KEY - 1; i < (MAX_KEY + 1); ++i) {
            AvlTreeIterator<TestAvlUtil.TestAvlNode> iter = new AvlTreeIterator(root, i, TestAvlUtil.KEY_COMPARATOR);
            if (i < MAX_KEY) {
                Assert.assertTrue(iter.hasNext());
            } else {
                // searching for something greater than the last node
                Assert.assertFalse(iter.hasNext());
                break;
            }
            TestAvlUtil.TestAvlNode node = iter.next();
            Assert.assertEquals(((i % 2) == 0 ? i + 1 : i), node.getKey());
            long prevKey = node.getKey();
            while (iter.hasNext()) {
                node = iter.next();
                Assert.assertTrue(((node.getKey()) > prevKey));
                prevKey = node.getKey();
            } 
        }
    }

    @Test
    public void testAvlIterableListCrud() {
        final int NITEMS = 10;
        TestAvlUtil.TestLinkedAvlNode prependHead = null;
        TestAvlUtil.TestLinkedAvlNode appendHead = null;
        // prepend()/append()
        for (int i = 0; i <= NITEMS; ++i) {
            TestAvlUtil.TestLinkedAvlNode pNode = new TestAvlUtil.TestLinkedAvlNode(i);
            Assert.assertFalse(AvlIterableList.isLinked(pNode));
            prependHead = AvlIterableList.prepend(prependHead, pNode);
            Assert.assertTrue(AvlIterableList.isLinked(pNode));
            TestAvlUtil.TestLinkedAvlNode aNode = new TestAvlUtil.TestLinkedAvlNode(i);
            Assert.assertFalse(AvlIterableList.isLinked(aNode));
            appendHead = AvlIterableList.append(appendHead, aNode);
            Assert.assertTrue(AvlIterableList.isLinked(aNode));
        }
        // readNext()
        TestAvlUtil.TestLinkedAvlNode pNode = prependHead;
        TestAvlUtil.TestLinkedAvlNode aNode = appendHead;
        for (int i = 0; i <= NITEMS; ++i) {
            Assert.assertEquals((NITEMS - i), pNode.getKey());
            pNode = AvlIterableList.readNext(pNode);
            Assert.assertEquals(i, aNode.getKey());
            aNode = AvlIterableList.readNext(aNode);
        }
        // readPrev()
        pNode = AvlIterableList.readPrev(prependHead);
        aNode = AvlIterableList.readPrev(appendHead);
        for (int i = 0; i <= NITEMS; ++i) {
            Assert.assertEquals(i, pNode.getKey());
            pNode = AvlIterableList.readPrev(pNode);
            Assert.assertEquals((NITEMS - i), aNode.getKey());
            aNode = AvlIterableList.readPrev(aNode);
        }
        // appendList()
        TestAvlUtil.TestLinkedAvlNode node = AvlIterableList.appendList(prependHead, appendHead);
        for (int i = NITEMS; i >= 0; --i) {
            Assert.assertEquals(i, node.getKey());
            node = AvlIterableList.readNext(node);
        }
        for (int i = 0; i <= NITEMS; ++i) {
            Assert.assertEquals(i, node.getKey());
            node = AvlIterableList.readNext(node);
        }
    }

    private static class TestAvlNode extends AvlNode<TestAvlUtil.TestAvlNode> {
        private final int key;

        public TestAvlNode(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }

        @Override
        public int compareTo(TestAvlUtil.TestAvlNode other) {
            return (this.key) - (other.key);
        }

        @Override
        public String toString() {
            return String.format("TestAvlNode(%d)", key);
        }
    }

    private static class TestLinkedAvlNode extends AvlLinkedNode<TestAvlUtil.TestLinkedAvlNode> {
        private final int key;

        public TestLinkedAvlNode(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }

        @Override
        public int compareTo(TestAvlUtil.TestLinkedAvlNode other) {
            return (this.key) - (other.key);
        }

        @Override
        public String toString() {
            return String.format("TestLinkedAvlNode(%d)", key);
        }
    }

    private static class TestAvlKeyComparator implements AvlKeyComparator<TestAvlUtil.TestAvlNode> {
        @Override
        public int compareKey(TestAvlUtil.TestAvlNode node, Object key) {
            return (node.getKey()) - ((int) (key));
        }
    }
}

