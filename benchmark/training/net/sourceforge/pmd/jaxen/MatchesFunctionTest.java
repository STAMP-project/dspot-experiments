/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jaxen;


import java.util.List;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import org.jaxen.FunctionCallException;
import org.junit.Assert;
import org.junit.Test;


public class MatchesFunctionTest {
    public static class MyNode extends AbstractNode {
        private String className;

        public MyNode() {
            super(1);
        }

        @Override
        public String toString() {
            return "MyNode";
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public String getXPathNodeName() {
            return "MyNode";
        }
    }

    @Test
    public void testMatch() throws NoSuchMethodException, FunctionCallException {
        MatchesFunctionTest.MyNode myNode = new MatchesFunctionTest.MyNode();
        myNode.setClassName("Foo");
        Assert.assertTrue(((tryRegexp(myNode, "Foo")) instanceof List));
    }

    @Test
    public void testNoMatch() throws NoSuchMethodException, FunctionCallException {
        MatchesFunctionTest.MyNode myNode = new MatchesFunctionTest.MyNode();
        myNode.setClassName("bar");
        Assert.assertTrue(((tryRegexp(myNode, "Foo")) instanceof Boolean));
        myNode.setClassName("FobboBar");
        Assert.assertTrue(((tryRegexp(myNode, "Foo")) instanceof Boolean));
    }
}

