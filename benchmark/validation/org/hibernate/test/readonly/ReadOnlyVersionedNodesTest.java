/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.readonly;


import org.hibernate.Session;
import org.hibernate.testing.FailureExpected;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class ReadOnlyVersionedNodesTest extends AbstractReadOnlyTest {
    @Test
    public void testSetReadOnlyTrueAndFalse() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode node = new VersionedNode("node", "node");
        s.persist(node);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        s.setReadOnly(node, true);
        node.setName("node-name");
        s.getTransaction().commit();
        assertUpdateCount(0);
        assertInsertCount(0);
        // the changed name is still in node
        Assert.assertEquals("node-name", node.getName());
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        // the changed name is still in the session
        Assert.assertEquals("node-name", node.getName());
        s.refresh(node);
        // after refresh, the name reverts to the original value
        Assert.assertEquals("node", node.getName());
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node", node.getName());
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(0);
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node", node.getName());
        s.setReadOnly(node, true);
        node.setName("diff-node-name");
        s.flush();
        Assert.assertEquals("diff-node-name", node.getName());
        s.refresh(node);
        Assert.assertEquals("node", node.getName());
        s.setReadOnly(node, false);
        node.setName("diff-node-name");
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("diff-node-name", node.getName());
        Assert.assertEquals(1, node.getVersion());
        s.setReadOnly(node, true);
        s.delete(node);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testUpdateSetReadOnlyTwice() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode node = new VersionedNode("node", "node");
        s.persist(node);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        node.setName("node-name");
        s.setReadOnly(node, true);
        s.setReadOnly(node, true);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(0);
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node", node.getName());
        Assert.assertEquals(0, node.getVersion());
        s.setReadOnly(node, true);
        s.delete(node);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testUpdateSetModifiable() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode node = new VersionedNode("node", "node");
        s.persist(node);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        node.setName("node-name");
        s.setReadOnly(node, false);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node-name", node.getName());
        Assert.assertEquals(1, node.getVersion());
        s.setReadOnly(node, true);
        s.delete(node);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testUpdateSetReadOnlySetModifiable() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode node = new VersionedNode("node", "node");
        s.persist(node);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        node.setName("node-name");
        s.setReadOnly(node, true);
        s.setReadOnly(node, false);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(0);
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node-name", node.getName());
        Assert.assertEquals(1, node.getVersion());
        s.delete(node);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testSetReadOnlyUpdateSetModifiable() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode node = new VersionedNode("node", "node");
        s.persist(node);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        s.setReadOnly(node, true);
        node.setName("node-name");
        s.setReadOnly(node, false);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(0);
        s = openSession();
        s.beginTransaction();
        node = ((VersionedNode) (s.get(VersionedNode.class, node.getId())));
        Assert.assertEquals("node-name", node.getName());
        Assert.assertEquals(1, node.getVersion());
        s.delete(node);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testAddNewChildToReadOnlyParent() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode parent = new VersionedNode("parent", "parent");
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        VersionedNode parentManaged = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        s.setReadOnly(parentManaged, true);
        parentManaged.setName("new parent name");
        VersionedNode child = new VersionedNode("child", "child");
        parentManaged.addChild(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        Assert.assertEquals("parent", parent.getName());
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertNotNull(child);
        s.delete(parent);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testUpdateParentWithNewChildCommitWithReadOnlyParent() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode parent = new VersionedNode("parent", "parent");
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        parent.setName("new parent name");
        VersionedNode child = new VersionedNode("child", "child");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        s.update(parent);
        s.setReadOnly(parent, true);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(parent.getName(), "parent");
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());
        Assert.assertSame(parent, child.getParent());
        Assert.assertSame(child, parent.getChildren().iterator().next());
        Assert.assertEquals(0, child.getVersion());
        s.setReadOnly(parent, true);
        s.setReadOnly(child, true);
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testMergeDetachedParentWithNewChildCommitWithReadOnlyParent() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode parent = new VersionedNode("parent", "parent");
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        parent.setName("new parent name");
        VersionedNode child = new VersionedNode("child", "child");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.merge(parent)));
        s.setReadOnly(parent, true);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(parent.getName(), "parent");
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());
        Assert.assertSame(parent, child.getParent());
        Assert.assertSame(child, parent.getChildren().iterator().next());
        Assert.assertEquals(0, child.getVersion());
        s.setReadOnly(parent, true);
        s.setReadOnly(child, true);
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testGetParentMakeReadOnlyThenMergeDetachedParentWithNewChildC() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode parent = new VersionedNode("parent", "parent");
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        parent.setName("new parent name");
        VersionedNode child = new VersionedNode("child", "child");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        VersionedNode parentManaged = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        s.setReadOnly(parentManaged, true);
        VersionedNode parentMerged = ((VersionedNode) (s.merge(parent)));
        Assert.assertSame(parentManaged, parentMerged);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(parent.getName(), "parent");
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());
        Assert.assertSame(parent, child.getParent());
        Assert.assertSame(child, parent.getChildren().iterator().next());
        Assert.assertEquals(0, child.getVersion());
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testMergeUnchangedDetachedParentChildren() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode parent = new VersionedNode("parent", "parent");
        VersionedNode child = new VersionedNode("child", "child");
        parent.addChild(child);
        s.persist(parent);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.merge(parent)));
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        VersionedNode parentGet = ((VersionedNode) (s.get(parent.getClass(), parent.getId())));
        s.merge(parent);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        VersionedNode parentLoad = ((VersionedNode) (s.load(parent.getClass(), parent.getId())));
        s.merge(parent);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(parent.getName(), "parent");
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(0, parent.getVersion());
        Assert.assertSame(parent, child.getParent());
        Assert.assertSame(child, parent.getChildren().iterator().next());
        Assert.assertEquals(0, child.getVersion());
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testAddNewParentToReadOnlyChild() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode child = new VersionedNode("child", "child");
        s.persist(child);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        s = openSession();
        s.beginTransaction();
        VersionedNode childManaged = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        s.setReadOnly(childManaged, true);
        childManaged.setName("new child name");
        VersionedNode parent = new VersionedNode("parent", "parent");
        parent.addChild(childManaged);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(1);
        s = openSession();
        s.beginTransaction();
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals("child", child.getName());
        Assert.assertNull(child.getParent());
        Assert.assertEquals(0, child.getVersion());
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        Assert.assertNotNull(parent);
        s.setReadOnly(child, true);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testUpdateChildWithNewParentCommitWithReadOnlyChild() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode child = new VersionedNode("child", "child");
        s.persist(child);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        child.setName("new child name");
        VersionedNode parent = new VersionedNode("parent", "parent");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        s.update(child);
        s.setReadOnly(child, true);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(child.getName(), "child");
        Assert.assertNull(child.getParent());
        Assert.assertEquals(0, child.getVersion());
        Assert.assertNotNull(parent);
        Assert.assertEquals(0, parent.getChildren().size());
        Assert.assertEquals(0, parent.getVersion());
        s.setReadOnly(parent, true);
        s.setReadOnly(child, true);
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testMergeDetachedChildWithNewParentCommitWithReadOnlyChild() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode child = new VersionedNode("child", "child");
        s.persist(child);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        child.setName("new child name");
        VersionedNode parent = new VersionedNode("parent", "parent");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        child = ((VersionedNode) (s.merge(child)));
        s.setReadOnly(child, true);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(child.getName(), "child");
        Assert.assertNull(child.getParent());
        Assert.assertEquals(0, child.getVersion());
        Assert.assertNotNull(parent);
        Assert.assertEquals(0, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());// hmmm, why is was version updated?

        s.setReadOnly(parent, true);
        s.setReadOnly(child, true);
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testGetChildMakeReadOnlyThenMergeDetachedChildWithNewParent() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        VersionedNode child = new VersionedNode("child", "child");
        s.persist(child);
        s.getTransaction().commit();
        s.close();
        clearCounts();
        child.setName("new child name");
        VersionedNode parent = new VersionedNode("parent", "parent");
        parent.addChild(child);
        s = openSession();
        s.beginTransaction();
        VersionedNode childManaged = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        s.setReadOnly(childManaged, true);
        VersionedNode childMerged = ((VersionedNode) (s.merge(child)));
        Assert.assertSame(childManaged, childMerged);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(1);
        assertInsertCount(1);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        parent = ((VersionedNode) (s.get(VersionedNode.class, parent.getId())));
        child = ((VersionedNode) (s.get(VersionedNode.class, child.getId())));
        Assert.assertEquals(child.getName(), "child");
        Assert.assertNull(child.getParent());
        Assert.assertEquals(0, child.getVersion());
        Assert.assertNotNull(parent);
        Assert.assertEquals(0, parent.getChildren().size());
        Assert.assertEquals(1, parent.getVersion());// / hmmm, why is was version updated?

        s.setReadOnly(parent, true);
        s.setReadOnly(child, true);
        s.delete(parent);
        s.delete(child);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }
}

