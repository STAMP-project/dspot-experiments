/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;


import junitparams.JUnitParamsRunner;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit test for {@link AbstractNode}.
 */
@RunWith(JUnitParamsRunner.class)
public class AbstractNodeTest {
    private static final int NUM_CHILDREN = 3;

    private static final int NUM_GRAND_CHILDREN = 3;

    @Rule
    public JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(Attribute.class.getName());

    private int id;

    private Node rootNode;

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * This is a border case as the root node does not have any parent.
     */
    @Test
    public void testRemoveRootNode() {
        // Check that the root node has the expected properties
        final Node[] children = new Node[rootNode.jjtGetNumChildren()];
        for (int i = 0; i < (children.length); i++) {
            final Node child = rootNode.jjtGetChild(i);
            children[i] = child;
        }
        // Do the actual removal
        rootNode.remove();
        // Check that conditions have been successfully changed, i.e.,
        // the root node is expected to still have all its children and vice versa
        Assert.assertEquals(AbstractNodeTest.NUM_CHILDREN, rootNode.jjtGetNumChildren());
        Assert.assertNull(rootNode.jjtGetParent());
        for (final Node aChild : children) {
            Assert.assertEquals(rootNode, aChild.jjtGetParent());
        }
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     * Test that invalid indexes cases are handled without exception.
     */
    @Test
    public void testRemoveChildAtIndexWithInvalidIndex() {
        try {
            rootNode.removeChildAtIndex((-1));
            rootNode.removeChildAtIndex(rootNode.jjtGetNumChildren());
        } catch (final Exception e) {
            Assert.fail("No exception was expected.");
        }
    }

    @Test
    public void testDeprecatedAttributeXPathQuery() throws JaxenException {
        class MyRootNode extends DummyNode implements RootNode {
            private MyRootNode(int id) {
                super(id);
            }
        }
        AbstractNodeTest.addChild(new MyRootNode(nextId()), new DummyNodeWithDeprecatedAttribute(2)).findChildNodesWithXPath("//dummyNode[@Size=1]");
        String log = loggingRule.getLog();
        Assert.assertTrue(log.contains("deprecated"));
        Assert.assertTrue(log.contains("attribute"));
        Assert.assertTrue(log.contains("dummyNode/@Size"));
    }
}

