/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import org.junit.Assert;
import org.junit.Test;


public class ScopeCreationVisitorTest extends STBBaseTst {
    @Test
    public void testScopesAreCreated() {
        parseCode(ScopeCreationVisitorTest.TEST1);
        ASTBlock n = acu.getFirstDescendantOfType(ASTIfStatement.class).getFirstDescendantOfType(ASTBlock.class);
        Assert.assertTrue(((n.getScope()) instanceof LocalScope));
    }

    private static final String TEST1 = (((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  if (x>2) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}") + (PMD.EOL);
}

