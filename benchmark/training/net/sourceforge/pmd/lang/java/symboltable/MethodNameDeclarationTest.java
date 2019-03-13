/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import org.junit.Assert;
import org.junit.Test;


public class MethodNameDeclarationTest extends STBBaseTst {
    @Test
    public void testEquality() {
        // Verify proper number of nodes are not equal
        parseCode15(MethodNameDeclarationTest.SIMILAR);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = getDeclarations();
        Set<NameDeclaration> methodNameDeclarations = m.keySet();
        Assert.assertEquals("Wrong number of method name declarations", methodNameDeclarations.size(), 3);
    }

    private static final String SIMILAR = ((((((((((("public class Foo {" + (PMD.EOL)) + " public void bar() {") + (PMD.EOL)) + "  bar(x, y);") + (PMD.EOL)) + " }") + (PMD.EOL)) + " private void bar(int x, int y) {}") + (PMD.EOL)) + " private void bar(int x, int... y) {}") + (PMD.EOL)) + "}";
}

