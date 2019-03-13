/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link AbstractNode} tree transversal methods
 */
public class AbstractNodeTransversalTest {
    private int id;

    private Node rootNode;

    @Test
    public void testBoundaryIsHonored() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));
        List<DummyNode> descendantsOfType = rootNode.findDescendantsOfType(DummyNode.class);
        Assert.assertEquals(1, descendantsOfType.size());
        Assert.assertTrue(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchFromBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));
        List<DummyNode> descendantsOfType = rootNode.findDescendantsOfType(DummyNode.class).get(0).findDescendantsOfType(DummyNode.class);
        Assert.assertEquals(1, descendantsOfType.size());
        Assert.assertFalse(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchIgnoringBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));
        List<DummyNode> descendantsOfType = new ArrayList<>();
        rootNode.findDescendantsOfType(DummyNode.class, descendantsOfType, true);
        Assert.assertEquals(2, descendantsOfType.size());
        Assert.assertTrue(descendantsOfType.get(0).isFindBoundary());
        Assert.assertFalse(descendantsOfType.get(1).isFindBoundary());
    }
}

