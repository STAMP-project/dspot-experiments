/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;


import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link DocumentNavigator}
 */
public class DocumentNavigatorTest {
    private static class DummyRootNode extends DummyNode implements RootNode {
        DummyRootNode(int id) {
            super(id);
        }
    }

    @Test
    public void getDocumentNode() {
        DocumentNavigator nav = new DocumentNavigator();
        try {
            nav.getDocumentNode(null);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertNotNull(e);
        }
        Node root = new DocumentNavigatorTest.DummyRootNode(1);
        Node n = new DummyNode(1);
        root.jjtAddChild(n, 0);
        n.jjtSetParent(root);
        Assert.assertSame(root, nav.getDocumentNode(n));
    }
}

