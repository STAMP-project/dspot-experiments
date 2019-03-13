/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTLocalVariableDeclarationTest {
    @Test
    public void testSingleDimArray() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTLocalVariableDeclarationTest.TEST1);
        ASTLocalVariableDeclaration node = cu.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        Assert.assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultDimArray() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTLocalVariableDeclarationTest.TEST2);
        ASTLocalVariableDeclaration node = cu.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        Assert.assertEquals(2, node.getArrayDepth());
    }

    @Test
    public void testMultDimArraySplitBraces() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTLocalVariableDeclarationTest.TEST3);
        ASTLocalVariableDeclaration node = cu.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        Assert.assertEquals(3, node.getArrayDepth());
    }

    private static final String TEST1 = ((("class Foo {" + (PMD.EOL)) + " void bar() {int x[] = null;}") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((("class Foo {" + (PMD.EOL)) + " void bar() {int x[][] = null;}") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((("class Foo {" + (PMD.EOL)) + " void bar() {int[] x[][] = null;}") + (PMD.EOL)) + "}";
}

