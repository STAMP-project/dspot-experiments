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
package io.atomix.core.tree;


import DocumentTreeEvent.Type.CREATED;
import DocumentTreeEvent.Type.DELETED;
import DocumentTreeEvent.Type.UPDATED;
import com.google.common.base.Throwables;
import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.utils.time.Versioned;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link io.atomix.core.tree.impl.AtomicDocumentTreeProxy}.
 */
public class AtomicDocumentTreeTest extends AbstractPrimitiveTest {
    /**
     * Tests queries (get and getChildren).
     */
    @Test
    public void testQueries() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        Versioned<String> root = tree.get(AtomicDocumentTreeTest.path("/"));
        Assert.assertEquals(1, root.version());
        Assert.assertNull(root.value());
    }

    /**
     * Tests exception.
     */
    @Test
    public void testException() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        try {
            tree.get(AtomicDocumentTreeTest.path("a"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            tree.getChildren(AtomicDocumentTreeTest.path("a/b"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            tree.set(AtomicDocumentTreeTest.path("a"), "a");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            tree.create(AtomicDocumentTreeTest.path("a/b"), "a");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            tree.createRecursive(AtomicDocumentTreeTest.path("a"), "a");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests string based methods.
     */
    @Test
    public void testStringPaths() throws Exception {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        Assert.assertNull(tree.set("/foo", "Hello world!"));
        Assert.assertEquals("Hello world!", tree.get("/foo").value());
        Assert.assertTrue(tree.create("/bar", "Hello world again!"));
        Assert.assertFalse(tree.create("/bar", "nope"));
        Assert.assertEquals("Hello world again!", tree.get("/bar").value());
        Assert.assertTrue(tree.createRecursive("/baz/foo/bar", null));
        Assert.assertNull(tree.get("/baz/foo/bar").value());
        Assert.assertEquals("Hello world!", tree.remove("/foo").value());
        Assert.assertNull(tree.get("/foo"));
        Assert.assertEquals("Hello world again!", tree.getChildren("/").get("bar").value());
    }

    /**
     * Tests create.
     */
    @Test
    public void testCreate() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        Versioned<String> a = tree.get(AtomicDocumentTreeTest.path("/a"));
        Assert.assertEquals("a", a.value());
        Versioned<String> ab = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals("ab", ab.value());
        Versioned<String> ac = tree.get(AtomicDocumentTreeTest.path("/a/c"));
        Assert.assertEquals("ac", ac.value());
        tree.create(AtomicDocumentTreeTest.path("/x"), null);
        Versioned<String> x = tree.get(AtomicDocumentTreeTest.path("/x"));
        Assert.assertNull(x.value());
    }

    /**
     * Tests recursive create.
     */
    @Test
    public void testRecursiveCreate() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.createRecursive(AtomicDocumentTreeTest.path("/a/b/c"), "abc");
        Versioned<String> a = tree.get(AtomicDocumentTreeTest.path("/a"));
        Assert.assertEquals(null, a.value());
        Versioned<String> ab = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals(null, ab.value());
        Versioned<String> abc = tree.get(AtomicDocumentTreeTest.path("/a/b/c"));
        Assert.assertEquals("abc", abc.value());
    }

    /**
     * Tests set.
     */
    @Test
    public void testSet() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        tree.set(AtomicDocumentTreeTest.path("/a/d"), "ad");
        Versioned<String> ad = tree.get(AtomicDocumentTreeTest.path("/a/d"));
        Assert.assertEquals("ad", ad.value());
        tree.set(AtomicDocumentTreeTest.path("/a"), "newA");
        Versioned<String> newA = tree.get(AtomicDocumentTreeTest.path("/a"));
        Assert.assertEquals("newA", newA.value());
        tree.set(AtomicDocumentTreeTest.path("/a/b"), "newAB");
        Versioned<String> newAB = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals("newAB", newAB.value());
        tree.set(AtomicDocumentTreeTest.path("/x"), null);
        Versioned<String> x = tree.get(AtomicDocumentTreeTest.path("/x"));
        Assert.assertNull(x.value());
    }

    /**
     * Tests replace if version matches.
     */
    @Test
    public void testReplaceVersion() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        Versioned<String> ab = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertTrue(tree.replace(AtomicDocumentTreeTest.path("/a/b"), "newAB", ab.version()));
        Versioned<String> newAB = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals("newAB", newAB.value());
        Assert.assertFalse(tree.replace(AtomicDocumentTreeTest.path("/a/b"), "newestAB", ab.version()));
        Assert.assertEquals("newAB", tree.get(AtomicDocumentTreeTest.path("/a/b")).value());
        Assert.assertFalse(tree.replace(AtomicDocumentTreeTest.path("/a/d"), "foo", 1));
    }

    /**
     * Tests replace if value matches.
     */
    @Test
    public void testReplaceValue() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        Versioned<String> ab = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertTrue(tree.replace(AtomicDocumentTreeTest.path("/a/b"), "newAB", ab.value()));
        Versioned<String> newAB = tree.get(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals("newAB", newAB.value());
        Assert.assertFalse(tree.replace(AtomicDocumentTreeTest.path("/a/b"), "newestAB", ab.value()));
        Assert.assertEquals("newAB", tree.get(AtomicDocumentTreeTest.path("/a/b")).value());
        Assert.assertFalse(tree.replace(AtomicDocumentTreeTest.path("/a/d"), "bar", "foo"));
    }

    /**
     * Tests remove.
     */
    @Test
    public void testRemove() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        Versioned<String> ab = tree.remove(AtomicDocumentTreeTest.path("/a/b"));
        Assert.assertEquals("ab", ab.value());
        Assert.assertNull(tree.get(AtomicDocumentTreeTest.path("/a/b")));
        Versioned<String> ac = tree.remove(AtomicDocumentTreeTest.path("/a/c"));
        Assert.assertEquals("ac", ac.value());
        Assert.assertNull(tree.get(AtomicDocumentTreeTest.path("/a/c")));
        Versioned<String> a = tree.remove(AtomicDocumentTreeTest.path("/a"));
        Assert.assertEquals("a", a.value());
        Assert.assertNull(tree.get(AtomicDocumentTreeTest.path("/a")));
        tree.create(AtomicDocumentTreeTest.path("/x"), null);
        Versioned<String> x = tree.remove(AtomicDocumentTreeTest.path("/x"));
        Assert.assertNull(x.value());
        Assert.assertNull(tree.get(AtomicDocumentTreeTest.path("/a/x")));
    }

    /**
     * Tests invalid removes.
     */
    @Test
    public void testRemoveFailures() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        try {
            tree.remove(AtomicDocumentTreeTest.path("/"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Throwables.getRootCause(e)) instanceof IllegalDocumentModificationException));
        }
        try {
            tree.remove(AtomicDocumentTreeTest.path("/a"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Throwables.getRootCause(e)) instanceof IllegalDocumentModificationException));
        }
        try {
            tree.remove(AtomicDocumentTreeTest.path("/d"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Throwables.getRootCause(e)) instanceof NoSuchDocumentPathException));
        }
    }

    /**
     * Tests invalid create.
     */
    @Test
    public void testCreateFailures() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        try {
            tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Throwables.getRootCause(e)) instanceof IllegalDocumentModificationException));
        }
    }

    /**
     * Tests invalid set.
     */
    @Test
    public void testSetFailures() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        try {
            tree.set(AtomicDocumentTreeTest.path("/a/c"), "ac");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Throwables.getRootCause(e)) instanceof IllegalDocumentModificationException));
        }
    }

    /**
     * Tests getChildren.
     */
    @Test
    public void testGetChildren() throws Throwable {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        tree.create(AtomicDocumentTreeTest.path("/a"), "a");
        tree.create(AtomicDocumentTreeTest.path("/a/b"), "ab");
        tree.create(AtomicDocumentTreeTest.path("/a/c"), "ac");
        Map<String, Versioned<String>> rootChildren = tree.getChildren(AtomicDocumentTreeTest.path("/"));
        Assert.assertEquals(1, rootChildren.size());
        Versioned<String> a = rootChildren.get("a");
        Assert.assertEquals("a", a.value());
        Map<String, Versioned<String>> children = tree.getChildren(AtomicDocumentTreeTest.path("/a"));
        Assert.assertEquals(2, children.size());
        Versioned<String> ab = children.get("b");
        Assert.assertEquals("ab", ab.value());
        Versioned<String> ac = children.get("c");
        Assert.assertEquals("ac", ac.value());
        Assert.assertEquals(0, tree.getChildren(AtomicDocumentTreeTest.path("/a/b")).size());
        Assert.assertEquals(0, tree.getChildren(AtomicDocumentTreeTest.path("/a/c")).size());
    }

    /**
     * Tests listeners.
     */
    @Test(timeout = 45000)
    public void testNotifications() throws Exception {
        AtomicDocumentTree<String> tree = newTree(UUID.randomUUID().toString());
        AtomicDocumentTreeTest.TestEventListener listener = new AtomicDocumentTreeTest.TestEventListener();
        // add listener; create a node in the tree and verify an CREATED event is received.
        tree.addListener(listener);
        tree.set(AtomicDocumentTreeTest.path("/a"), "a");
        DocumentTreeEvent<String> event = listener.event();
        Assert.assertEquals(CREATED, event.type());
        Assert.assertFalse(event.oldValue().isPresent());
        Assert.assertEquals("a", event.newValue().get().value());
        // update a node in the tree and verify an UPDATED event is received.
        tree.set(AtomicDocumentTreeTest.path("/a"), "newA");
        event = listener.event();
        Assert.assertEquals(UPDATED, event.type());
        Assert.assertEquals("newA", event.newValue().get().value());
        Assert.assertEquals("a", event.oldValue().get().value());
        // remove a node in the tree and verify an REMOVED event is received.
        tree.remove(AtomicDocumentTreeTest.path("/a"));
        event = listener.event();
        Assert.assertEquals(DELETED, event.type());
        Assert.assertFalse(event.newValue().isPresent());
        Assert.assertEquals("newA", event.oldValue().get().value());
        // recursively create a node and verify CREATED events for all intermediate nodes.
        tree.createRecursive(AtomicDocumentTreeTest.path("/x/y"), "xy");
        event = listener.event();
        Assert.assertEquals(CREATED, event.type());
        Assert.assertEquals(AtomicDocumentTreeTest.path("/x"), event.path());
        event = listener.event();
        Assert.assertEquals(CREATED, event.type());
        Assert.assertEquals(AtomicDocumentTreeTest.path("/x/y"), event.path());
        Assert.assertEquals("xy", event.newValue().get().value());
    }

    private static class TestEventListener implements DocumentTreeEventListener<String> {
        private final BlockingQueue<DocumentTreeEvent<String>> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(DocumentTreeEvent<String> event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public DocumentTreeEvent<String> event() throws InterruptedException {
            return queue.poll(10, TimeUnit.SECONDS);
        }
    }
}

