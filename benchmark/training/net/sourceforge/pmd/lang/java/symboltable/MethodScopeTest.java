/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import org.junit.Assert;
import org.junit.Test;


public class MethodScopeTest extends STBBaseTst {
    @Test
    public void testMethodParameterOccurrenceRecorded() {
        parseCode(MethodScopeTest.TEST1);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.findDescendantsOfType(ASTMethodDeclaration.class).get(0).getScope().getDeclarations();
        NameDeclaration vnd = m.keySet().iterator().next();
        Assert.assertEquals("bar", vnd.getImage());
        List<NameOccurrence> occs = m.get(vnd);
        NameOccurrence occ = occs.get(0);
        Assert.assertEquals(3, occ.getLocation().getBeginLine());
    }

    @Test
    public void testMethodName() {
        parseCode(MethodScopeTest.TEST1);
        ASTMethodDeclaration meth = acu.findDescendantsOfType(ASTMethodDeclaration.class).get(0);
        MethodScope ms = ((MethodScope) (meth.getScope()));
        Assert.assertEquals(ms.getName(), "foo");
    }

    @Test
    public void testGenerics() {
        parseCode(MethodScopeTest.TEST_GENERICS);
    }

    public static final String TEST1 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo(int bar) {") + (PMD.EOL)) + "  bar = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST_GENERICS = (((((((("public class Tree {" + (PMD.EOL)) + "  private List<Object> subForest;") + (PMD.EOL)) + "  public <B> Tree<B> fmap(final F<B> f) { return Tree.<B>foo(); }") + (PMD.EOL)) + "  public List<Object> subForest() { return null; }") + (PMD.EOL)) + "}") + (PMD.EOL);
}

