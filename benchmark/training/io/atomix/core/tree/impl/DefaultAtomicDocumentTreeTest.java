/**
 * Copyright 2016-present Open Networking Foundation
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
package io.atomix.core.tree.impl;


import io.atomix.core.tree.AtomicDocumentTree;
import io.atomix.core.tree.IllegalDocumentModificationException;
import io.atomix.core.tree.NoSuchDocumentPathException;
import io.atomix.utils.time.Versioned;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@code DefaultDocumentTree}.
 */
public class DefaultAtomicDocumentTreeTest {
    @Test
    public void testTreeConstructor() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        Assert.assertEquals(tree.root(), DefaultAtomicDocumentTreeTest.path("/"));
    }

    @Test
    public void testCreateNodeAtRoot() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        Assert.assertTrue(tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar"));
        Assert.assertFalse(tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "baz"));
    }

    @Test
    public void testCreateNodeAtNonRoot() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        Assert.assertTrue(tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "baz"));
    }

    @Test
    public void testCreateRecursive() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.createRecursive(DefaultAtomicDocumentTreeTest.path("/a/b/c"), "bar");
        Assert.assertEquals("bar", tree.get(DefaultAtomicDocumentTreeTest.path("/a/b/c")).value());
        Assert.assertNull(tree.get(DefaultAtomicDocumentTreeTest.path("/a/b")).value());
        Assert.assertNull(tree.get(DefaultAtomicDocumentTreeTest.path("/a")).value());
    }

    @Test(expected = IllegalDocumentModificationException.class)
    public void testCreateRecursiveRoot() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.createRecursive(DefaultAtomicDocumentTreeTest.path("/"), "bar");
    }

    @Test(expected = IllegalDocumentModificationException.class)
    public void testCreateNodeFailure() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "bar");
    }

    @Test
    public void testGetRootValue() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "baz");
        Versioned<String> root = tree.get(DefaultAtomicDocumentTreeTest.path("/"));
        Assert.assertNotNull(root);
        Assert.assertNull(root.value());
    }

    @Test
    public void testGetInnerNode() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "baz");
        Versioned<String> nodeValue = tree.get(DefaultAtomicDocumentTreeTest.path("/a"));
        Assert.assertNotNull(nodeValue);
        Assert.assertEquals("bar", nodeValue.value());
    }

    @Test
    public void testGetLeafNode() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "baz");
        Versioned<String> nodeValue = tree.get(DefaultAtomicDocumentTreeTest.path("/a/b"));
        Assert.assertNotNull(nodeValue);
        Assert.assertEquals("baz", nodeValue.value());
    }

    @Test
    public void getMissingNode() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "baz");
        Assert.assertNull(tree.get(DefaultAtomicDocumentTreeTest.path("/x")));
        Assert.assertNull(tree.get(DefaultAtomicDocumentTreeTest.path("/a/x")));
        Assert.assertNull(tree.get(DefaultAtomicDocumentTreeTest.path("/a/b/x")));
    }

    @Test
    public void testGetChildren() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/c"), "beta");
        Assert.assertEquals(2, tree.getChildren(DefaultAtomicDocumentTreeTest.path("/a")).size());
        Assert.assertEquals(0, tree.getChildren(DefaultAtomicDocumentTreeTest.path("/a/b")).size());
    }

    @Test(expected = NoSuchDocumentPathException.class)
    public void testGetChildrenFailure() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.getChildren(DefaultAtomicDocumentTreeTest.path("/a/b"));
    }

    @Test(expected = IllegalDocumentModificationException.class)
    public void testSetRootFailure() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.set(tree.root(), "bar");
    }

    @Test
    public void testSet() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        Assert.assertNull(tree.set(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha"));
        Assert.assertEquals("alpha", tree.set(DefaultAtomicDocumentTreeTest.path("/a/b"), "beta").value());
        Assert.assertEquals("beta", tree.get(DefaultAtomicDocumentTreeTest.path("/a/b")).value());
    }

    @Test(expected = IllegalDocumentModificationException.class)
    public void testSetInvalidNode() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.set(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha");
    }

    @Test
    public void testReplaceWithVersion() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha");
        Versioned<String> value = tree.get(DefaultAtomicDocumentTreeTest.path("/a/b"));
        Assert.assertTrue(tree.replace(DefaultAtomicDocumentTreeTest.path("/a/b"), "beta", value.version()));
        Assert.assertFalse(tree.replace(DefaultAtomicDocumentTreeTest.path("/a/b"), "beta", value.version()));
        Assert.assertFalse(tree.replace(DefaultAtomicDocumentTreeTest.path("/x"), "beta", 1));
    }

    @Test
    public void testReplaceWithValue() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha");
        Assert.assertTrue(tree.replace(DefaultAtomicDocumentTreeTest.path("/a/b"), "beta", "alpha"));
        Assert.assertFalse(tree.replace(DefaultAtomicDocumentTreeTest.path("/a/b"), "beta", "alpha"));
        Assert.assertFalse(tree.replace(DefaultAtomicDocumentTreeTest.path("/x"), "beta", "bar"));
        Assert.assertTrue(tree.replace(DefaultAtomicDocumentTreeTest.path("/x"), "beta", null));
    }

    @Test(expected = IllegalDocumentModificationException.class)
    public void testRemoveRoot() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.remove(tree.root());
    }

    @Test
    public void testRemove() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.create(DefaultAtomicDocumentTreeTest.path("/a"), "bar");
        tree.create(DefaultAtomicDocumentTreeTest.path("/a/b"), "alpha");
        Assert.assertEquals("alpha", tree.remove(DefaultAtomicDocumentTreeTest.path("/a/b")).value());
        Assert.assertEquals(0, tree.getChildren(DefaultAtomicDocumentTreeTest.path("/a")).size());
    }

    @Test(expected = NoSuchDocumentPathException.class)
    public void testRemoveInvalidNode() {
        AtomicDocumentTree<String> tree = new DefaultAtomicDocumentTree();
        tree.remove(DefaultAtomicDocumentTreeTest.path("/a"));
    }
}

