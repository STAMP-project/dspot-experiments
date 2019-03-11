/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jaxen;


import java.lang.reflect.Method;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import org.junit.Assert;
import org.junit.Test;


public class AttributeTest {
    @Test
    public void testConstructor() {
        DummyNode p = new DummyNode(1);
        testingOnlySetBeginLine(5);
        Method[] methods = p.getClass().getMethods();
        Method m = null;
        for (int i = 0; i < (methods.length); i++) {
            if (methods[i].getName().equals("getBeginLine")) {
                m = methods[i];
                break;
            }
        }
        Attribute a = new Attribute(p, "BeginLine", m);
        Assert.assertEquals("BeginLine", a.getName());
        Assert.assertEquals(5, a.getValue());
        Assert.assertEquals("5", a.getStringValue());
        Assert.assertEquals(p, a.getParent());
    }
}

