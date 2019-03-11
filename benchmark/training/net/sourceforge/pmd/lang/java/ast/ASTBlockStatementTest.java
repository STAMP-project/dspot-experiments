/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import org.junit.Assert;
import org.junit.Test;


public class ASTBlockStatementTest {
    @Test
    public void testIsAllocation() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAllocationExpression(1), 0);
        Assert.assertTrue(bs.isAllocation());
    }

    @Test
    public void testIsAllocation2() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAssertStatement(1), 0);
        Assert.assertFalse(bs.isAllocation());
    }
}

