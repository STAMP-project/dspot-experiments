/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTAssignmentOperatorTest {
    @Test
    public void testSimpleAssignmentRecognized() {
        Set<ASTAssignmentOperator> ops = ParserTstUtil.getNodes(ASTAssignmentOperator.class, ASTAssignmentOperatorTest.TEST1);
        Assert.assertFalse(ops.iterator().next().isCompound());
    }

    @Test
    public void testCompoundAssignmentPlusRecognized() {
        Set<ASTAssignmentOperator> ops = ParserTstUtil.getNodes(ASTAssignmentOperator.class, ASTAssignmentOperatorTest.TEST2);
        Assert.assertTrue(ops.iterator().next().isCompound());
    }

    @Test
    public void testCompoundAssignmentMultRecognized() {
        Set<ASTAssignmentOperator> ops = ParserTstUtil.getNodes(ASTAssignmentOperator.class, ASTAssignmentOperatorTest.TEST3);
        Assert.assertTrue(ops.iterator().next().isCompound());
    }

    private static final String TEST1 = ((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + "  x=2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + "  x += 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + "  x *= 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

