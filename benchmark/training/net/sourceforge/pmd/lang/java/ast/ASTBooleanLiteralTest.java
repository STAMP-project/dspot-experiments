/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTBooleanLiteralTest {
    @Test
    public void testTrue() {
        Set<ASTBooleanLiteral> ops = ParserTstUtil.getNodes(ASTBooleanLiteral.class, ASTBooleanLiteralTest.TEST1);
        ASTBooleanLiteral b = ops.iterator().next();
        Assert.assertTrue(b.isTrue());
    }

    @Test
    public void testFalse() {
        Set<ASTBooleanLiteral> ops = ParserTstUtil.getNodes(ASTBooleanLiteral.class, ASTBooleanLiteralTest.TEST2);
        ASTBooleanLiteral b = ops.iterator().next();
        Assert.assertFalse(b.isTrue());
    }

    private static final String TEST1 = ((("class Foo { " + (PMD.EOL)) + " boolean bar = true; ") + (PMD.EOL)) + "} ";

    private static final String TEST2 = ((("class Foo { " + (PMD.EOL)) + " boolean bar = false; ") + (PMD.EOL)) + "} ";
}

