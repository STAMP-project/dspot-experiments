/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import io.grpc.PersistentHashArrayMappedTrie.CollisionLeaf;
import io.grpc.PersistentHashArrayMappedTrie.CompressedIndex;
import io.grpc.PersistentHashArrayMappedTrie.Leaf;
import io.grpc.PersistentHashArrayMappedTrie.Node;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PersistentHashArrayMappedTrieTest {
    @Test
    public void leaf_replace() {
        PersistentHashArrayMappedTrieTest.Key key = new PersistentHashArrayMappedTrieTest.Key(0);
        Object value1 = new Object();
        Object value2 = new Object();
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new Leaf(key, value1);
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(key, value2, key.hashCode(), 0);
        TestCase.assertTrue((ret instanceof Leaf));
        Assert.assertSame(value2, ret.get(key, key.hashCode(), 0));
        Assert.assertSame(value1, leaf.get(key, key.hashCode(), 0));
        Assert.assertEquals(1, leaf.size());
        Assert.assertEquals(1, ret.size());
    }

    @Test
    public void leaf_collision() {
        PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(0);
        PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(0);
        Object value1 = new Object();
        Object value2 = new Object();
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new Leaf(key1, value1);
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(key2, value2, key2.hashCode(), 0);
        TestCase.assertTrue((ret instanceof CollisionLeaf));
        Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
        Assert.assertSame(value1, leaf.get(key1, key1.hashCode(), 0));
        Assert.assertSame(null, leaf.get(key2, key2.hashCode(), 0));
        Assert.assertEquals(1, leaf.size());
        Assert.assertEquals(2, ret.size());
    }

    @Test
    public void leaf_insert() {
        PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(0);
        PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(1);
        Object value1 = new Object();
        Object value2 = new Object();
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new Leaf(key1, value1);
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(key2, value2, key2.hashCode(), 0);
        TestCase.assertTrue((ret instanceof CompressedIndex));
        Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
        Assert.assertSame(value1, leaf.get(key1, key1.hashCode(), 0));
        Assert.assertSame(null, leaf.get(key2, key2.hashCode(), 0));
        Assert.assertEquals(1, leaf.size());
        Assert.assertEquals(2, ret.size());
    }

    @Test(expected = AssertionError.class)
    public void collisionLeaf_assertKeysDifferent() {
        PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(0);
        new CollisionLeaf(key1, new Object(), key1, new Object());
    }

    @Test(expected = AssertionError.class)
    public void collisionLeaf_assertHashesSame() {
        new CollisionLeaf(new PersistentHashArrayMappedTrieTest.Key(0), new Object(), new PersistentHashArrayMappedTrieTest.Key(1), new Object());
    }

    @Test
    public void collisionLeaf_insert() {
        PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(0);
        PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(key1.hashCode());
        PersistentHashArrayMappedTrieTest.Key insertKey = new PersistentHashArrayMappedTrieTest.Key(1);
        Object value1 = new Object();
        Object value2 = new Object();
        Object insertValue = new Object();
        CollisionLeaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new CollisionLeaf(key1, value1, key2, value2);
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(insertKey, insertValue, insertKey.hashCode(), 0);
        TestCase.assertTrue((ret instanceof CompressedIndex));
        Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
        Assert.assertSame(insertValue, ret.get(insertKey, insertKey.hashCode(), 0));
        Assert.assertSame(value1, leaf.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, leaf.get(key2, key2.hashCode(), 0));
        Assert.assertSame(null, leaf.get(insertKey, insertKey.hashCode(), 0));
        Assert.assertEquals(2, leaf.size());
        Assert.assertEquals(3, ret.size());
    }

    @Test
    public void collisionLeaf_replace() {
        PersistentHashArrayMappedTrieTest.Key replaceKey = new PersistentHashArrayMappedTrieTest.Key(0);
        Object originalValue = new Object();
        PersistentHashArrayMappedTrieTest.Key key = new PersistentHashArrayMappedTrieTest.Key(replaceKey.hashCode());
        Object value = new Object();
        CollisionLeaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new CollisionLeaf(replaceKey, originalValue, key, value);
        Object replaceValue = new Object();
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(replaceKey, replaceValue, replaceKey.hashCode(), 0);
        TestCase.assertTrue((ret instanceof CollisionLeaf));
        Assert.assertSame(replaceValue, ret.get(replaceKey, replaceKey.hashCode(), 0));
        Assert.assertSame(value, ret.get(key, key.hashCode(), 0));
        Assert.assertSame(value, leaf.get(key, key.hashCode(), 0));
        Assert.assertSame(originalValue, leaf.get(replaceKey, replaceKey.hashCode(), 0));
        Assert.assertEquals(2, leaf.size());
        Assert.assertEquals(2, ret.size());
    }

    @Test
    public void collisionLeaf_collision() {
        PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(0);
        PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(key1.hashCode());
        PersistentHashArrayMappedTrieTest.Key key3 = new PersistentHashArrayMappedTrieTest.Key(key1.hashCode());
        Object value1 = new Object();
        Object value2 = new Object();
        Object value3 = new Object();
        CollisionLeaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf = new CollisionLeaf(key1, value1, key2, value2);
        Node<PersistentHashArrayMappedTrieTest.Key, Object> ret = leaf.put(key3, value3, key3.hashCode(), 0);
        TestCase.assertTrue((ret instanceof CollisionLeaf));
        Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
        Assert.assertSame(value3, ret.get(key3, key3.hashCode(), 0));
        Assert.assertSame(value1, leaf.get(key1, key1.hashCode(), 0));
        Assert.assertSame(value2, leaf.get(key2, key2.hashCode(), 0));
        Assert.assertSame(null, leaf.get(key3, key3.hashCode(), 0));
        Assert.assertEquals(2, leaf.size());
        Assert.assertEquals(3, ret.size());
    }

    @Test
    public void compressedIndex_combine_differentIndexBit() {
        final PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(7);
        final PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(19);
        final Object value1 = new Object();
        final Object value2 = new Object();
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf1 = new Leaf(key1, value1);
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf2 = new Leaf(key2, value2);
        class Verifier {
            private void verify(Node<PersistentHashArrayMappedTrieTest.Key, Object> ret) {
                CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object> collisionLeaf = ((CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object>) (ret));
                Assert.assertEquals(((1 << 7) | (1 << 19)), collisionLeaf.bitmap);
                Assert.assertEquals(2, collisionLeaf.values.length);
                Assert.assertSame(value1, collisionLeaf.values[0].get(key1, key1.hashCode(), 0));
                Assert.assertSame(value2, collisionLeaf.values[1].get(key2, key2.hashCode(), 0));
                Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
                Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
                Assert.assertEquals(2, ret.size());
            }
        }
        Verifier verifier = new Verifier();
        verifier.verify(CompressedIndex.combine(leaf1, key1.hashCode(), leaf2, key2.hashCode(), 0));
        verifier.verify(CompressedIndex.combine(leaf2, key2.hashCode(), leaf1, key1.hashCode(), 0));
        Assert.assertEquals(1, leaf1.size());
        Assert.assertEquals(1, leaf2.size());
    }

    @Test
    public void compressedIndex_combine_sameIndexBit() {
        final PersistentHashArrayMappedTrieTest.Key key1 = new PersistentHashArrayMappedTrieTest.Key(((17 << 5) | 1));// 5 bit regions: (17, 1)

        final PersistentHashArrayMappedTrieTest.Key key2 = new PersistentHashArrayMappedTrieTest.Key(((31 << 5) | 1));// 5 bit regions: (31, 1)

        final Object value1 = new Object();
        final Object value2 = new Object();
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf1 = new Leaf(key1, value1);
        Leaf<PersistentHashArrayMappedTrieTest.Key, Object> leaf2 = new Leaf(key2, value2);
        class Verifier {
            private void verify(Node<PersistentHashArrayMappedTrieTest.Key, Object> ret) {
                CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object> collisionInternal = ((CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object>) (ret));
                Assert.assertEquals((1 << 1), collisionInternal.bitmap);
                Assert.assertEquals(1, collisionInternal.values.length);
                CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object> collisionLeaf = ((CompressedIndex<PersistentHashArrayMappedTrieTest.Key, Object>) (collisionInternal.values[0]));
                Assert.assertEquals(((1 << 31) | (1 << 17)), collisionLeaf.bitmap);
                Assert.assertSame(value1, ret.get(key1, key1.hashCode(), 0));
                Assert.assertSame(value2, ret.get(key2, key2.hashCode(), 0));
                Assert.assertEquals(2, ret.size());
            }
        }
        Verifier verifier = new Verifier();
        verifier.verify(CompressedIndex.combine(leaf1, key1.hashCode(), leaf2, key2.hashCode, 0));
        verifier.verify(CompressedIndex.combine(leaf2, key2.hashCode(), leaf1, key1.hashCode, 0));
        Assert.assertEquals(1, leaf1.size());
        Assert.assertEquals(1, leaf2.size());
    }

    /**
     * A key with a settable hashcode.
     */
    static final class Key {
        private final int hashCode;

        Key(int hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return String.format("Key(hashCode=%x)", hashCode);
        }
    }
}

