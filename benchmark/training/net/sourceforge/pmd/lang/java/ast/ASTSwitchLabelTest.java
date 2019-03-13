/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTSwitchLabelTest {
    @Test
    public void testDefaultOff() {
        Set<ASTSwitchLabel> ops = ParserTstUtil.getNodes(ASTSwitchLabel.class, ASTSwitchLabelTest.TEST1);
        Assert.assertFalse(ops.iterator().next().isDefault());
    }

    @Test
    public void testDefaultSet() {
        Set<ASTSwitchLabel> ops = ParserTstUtil.getNodes(ASTSwitchLabel.class, ASTSwitchLabelTest.TEST2);
        Assert.assertTrue(ops.iterator().next().isDefault());
    }

    private static final String TEST1 = ((((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  switch (x) {") + (PMD.EOL)) + "   case 1: y = 2;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  switch (x) {") + (PMD.EOL)) + "   default: y = 2;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

