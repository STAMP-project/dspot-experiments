/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jaxen;


import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import org.junit.Test;


public class AttributeAxisIteratorTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        DummyNode n = new DummyNode(0);
        testingOnlySetBeginColumn(1);
        testingOnlySetBeginLine(1);
        AttributeAxisIterator iter = new AttributeAxisIterator(n);
        iter.remove();
    }
}

