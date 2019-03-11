/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on Jan 19, 2005
 *
 * @author mgriffa
 */
public class ASTThrowStatementTest {
    @Test
    public final void testGetFirstASTNameImageNull() {
        ASTThrowStatement t = ParserTstUtil.getNodes(ASTThrowStatement.class, ASTThrowStatementTest.NULL_NAME).iterator().next();
        Assert.assertNull(t.getFirstClassOrInterfaceTypeImage());
    }

    @Test
    public final void testGetFirstASTNameImageNew() {
        ASTThrowStatement t = ParserTstUtil.getNodes(ASTThrowStatement.class, ASTThrowStatementTest.OK_NAME).iterator().next();
        Assert.assertEquals("FooException", t.getFirstClassOrInterfaceTypeImage());
    }

    private static final String NULL_NAME = ((((((("public class Test {" + (PMD.EOL)) + "  void bar() {") + (PMD.EOL)) + "   throw e;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}";

    private static final String OK_NAME = ((((((("public class Test {" + (PMD.EOL)) + "  void bar() {") + (PMD.EOL)) + "   throw new FooException();") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}";
}

