/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;


import NodeType.BREAK_STATEMENT;
import NodeType.CASE_LAST_STATEMENT;
import java.util.LinkedList;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;
import org.junit.Assert;
import org.junit.Test;


public class DataFlowNodeTest {
    @Test
    public void testAddPathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);
        Assert.assertEquals(parent.getChildren().size(), 1);
        Assert.assertTrue(child.getParents().contains(parent));
        Assert.assertTrue(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);
        Assert.assertTrue(parent.removePathToChild(child));
        Assert.assertFalse(child.getParents().contains(parent));
        Assert.assertFalse(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathWithNonChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        Assert.assertFalse(parent.removePathToChild(child));
    }

    @Test
    public void testReverseParentPathsTo() {
        DataFlowNode parent1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode parent2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        DataFlowNode child1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        DataFlowNode child2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        parent1.addPathToChild(child1);
        parent2.addPathToChild(child1);
        Assert.assertTrue(parent1.getChildren().contains(child1));
        child1.reverseParentPathsTo(child2);
        Assert.assertTrue(parent1.getChildren().contains(child2));
        Assert.assertFalse(parent1.getChildren().contains(child1));
        Assert.assertTrue(parent2.getChildren().contains(child2));
        Assert.assertFalse(parent2.getChildren().contains(child1));
        Assert.assertEquals(0, child1.getParents().size());
        Assert.assertEquals(2, child2.getParents().size());
    }

    @Test
    public void testSetType() {
        DataFlowNode node = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        node.setType(BREAK_STATEMENT);
        Assert.assertTrue(node.isType(BREAK_STATEMENT));
        Assert.assertFalse(node.isType(CASE_LAST_STATEMENT));
    }
}

