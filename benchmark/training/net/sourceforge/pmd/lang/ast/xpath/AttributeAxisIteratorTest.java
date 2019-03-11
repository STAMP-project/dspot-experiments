/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;


import java.util.Map;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithDeprecatedAttribute;
import net.sourceforge.pmd.lang.ast.Node;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {
    @Test
    public void testAttributeDeprecation() {
        Node dummy = new DummyNodeWithDeprecatedAttribute(2);
        MatcherAssert.assertThat(toMap(new AttributeAxisIterator(dummy)), IsMapContaining.hasKey("Size"));
    }

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode(1);
        testingOnlySetBeginLine(1);
        testingOnlySetBeginColumn(1);
        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(7, atts.size());
        Assert.assertTrue(atts.containsKey("BeginColumn"));
        Assert.assertTrue(atts.containsKey("BeginLine"));
        Assert.assertTrue(atts.containsKey("FindBoundary"));
        Assert.assertTrue(atts.containsKey("Image"));
        Assert.assertTrue(atts.containsKey("SingleLine"));
        Assert.assertTrue(atts.containsKey("EndColumn"));
        Assert.assertTrue(atts.containsKey("EndLine"));
    }

    @Test
    public void testAttributeAxisIteratorWithEnum() {
        AttributeAxisIteratorTest.DummyNodeWithEnum dummyNode = new AttributeAxisIteratorTest.DummyNodeWithEnum(1);
        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(8, atts.size());
        Assert.assertTrue(atts.containsKey("Enum"));
        Assert.assertEquals(AttributeAxisIteratorTest.DummyNodeWithEnum.MyEnum.FOO, atts.get("Enum").getValue());
    }

    public static class DummyNodeWithEnum extends DummyNode {
        public DummyNodeWithEnum(int id) {
            super(id);
        }

        public enum MyEnum {

            FOO,
            BAR;}

        public AttributeAxisIteratorTest.DummyNodeWithEnum.MyEnum getEnum() {
            return AttributeAxisIteratorTest.DummyNodeWithEnum.MyEnum.FOO;
        }
    }
}

