/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import org.junit.Assert;
import org.junit.Test;


public class AcceptanceTest extends STBBaseTst {
    @Test
    public void testClashingSymbols() {
        parseCode(AcceptanceTest.TEST1);
    }

    @Test
    public void testInitializer() {
        parseCode(AcceptanceTest.TEST_INITIALIZERS);
        ASTInitializer a = acu.findDescendantsOfType(ASTInitializer.class).get(0);
        Assert.assertFalse(a.isStatic());
        a = acu.findDescendantsOfType(ASTInitializer.class).get(1);
        Assert.assertTrue(a.isStatic());
    }

    @Test
    public void testCatchBlocks() {
        parseCode(AcceptanceTest.TEST_CATCH_BLOCKS);
        ASTCatchStatement c = acu.findDescendantsOfType(ASTCatchStatement.class).get(0);
        ASTBlock a = c.findDescendantsOfType(ASTBlock.class).get(0);
        Scope s = a.getScope();
        Map<NameDeclaration, List<NameOccurrence>> vars = s.getDeclarations();
        Assert.assertEquals(1, vars.size());
        NameDeclaration v = vars.keySet().iterator().next();
        Assert.assertEquals("e", v.getImage());
        Assert.assertEquals(1, vars.get(v).size());
    }

    @Test
    public void testEq() {
        parseCode(AcceptanceTest.TEST_EQ);
        ASTEqualityExpression e = acu.findDescendantsOfType(ASTEqualityExpression.class).get(0);
        ASTMethodDeclaration method = e.getFirstParentOfType(ASTMethodDeclaration.class);
        Scope s = method.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = s.getDeclarations();
        Assert.assertEquals(2, m.size());
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> entry : m.entrySet()) {
            NameDeclaration vnd = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();
            if ((vnd.getImage().equals("a")) || (vnd.getImage().equals("b"))) {
                Assert.assertEquals(1, usages.size());
                Assert.assertEquals(3, usages.get(0).getLocation().getBeginLine());
            } else {
                Assert.fail(("Unkown variable " + vnd));
            }
        }
    }

    @Test
    public void testFieldFinder() {
        parseCode(AcceptanceTest.TEST_FIELD);
        // System.out.println(TEST_FIELD);
        ASTVariableDeclaratorId declaration = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(1);
        Assert.assertEquals(3, declaration.getBeginLine());
        Assert.assertEquals("bbbbbbbbbb", declaration.getImage());
        Assert.assertEquals(1, declaration.getUsages().size());
        NameOccurrence no = declaration.getUsages().get(0);
        Node location = no.getLocation();
        Assert.assertEquals(6, location.getBeginLine());
        // System.out.println("variable " + declaration.getImage() + " is used
        // here: " + location.getImage());
    }

    @Test
    public void testDemo() {
        parseCode(AcceptanceTest.TEST_DEMO);
        // System.out.println(TEST_DEMO);
        ASTMethodDeclaration node = acu.findDescendantsOfType(ASTMethodDeclaration.class).get(0);
        Scope s = node.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = s.getDeclarations();
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> entry : m.entrySet()) {
            Assert.assertEquals("buz", entry.getKey().getImage());
            Assert.assertEquals("ArrayList", getTypeImage());
            List<NameOccurrence> u = entry.getValue();
            Assert.assertEquals(1, u.size());
            NameOccurrence o = u.get(0);
            int beginLine = o.getLocation().getBeginLine();
            Assert.assertEquals(3, beginLine);
            // System.out.println("Variable: " + d.getImage());
            // System.out.println("Type: " + d.getTypeImage());
            // System.out.println("Usages: " + u.size());
            // System.out.println("Used in line " + beginLine);
        }
    }

    @Test
    public void testEnum() {
        parseCode(NameOccurrencesTest.TEST_ENUM);
        ASTVariableDeclaratorId vdi = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);
        List<NameOccurrence> usages = vdi.getUsages();
        Assert.assertEquals(2, usages.size());
        Assert.assertEquals(5, usages.get(0).getLocation().getBeginLine());
        Assert.assertEquals(9, usages.get(1).getLocation().getBeginLine());
    }

    @Test
    public void testInnerOuterClass() {
        parseCode(AcceptanceTest.TEST_INNER_CLASS);
        ASTVariableDeclaratorId vdi = // get inner class
        acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(1).getFirstDescendantOfType(ASTVariableDeclaratorId.class);// get first declaration

        List<NameOccurrence> usages = vdi.getUsages();
        Assert.assertEquals(2, usages.size());
        Assert.assertEquals(5, usages.get(0).getLocation().getBeginLine());
        Assert.assertEquals(10, usages.get(1).getLocation().getBeginLine());
    }

    /**
     * Unit test for bug #1490
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1490/">#1490 [java] PMD
    Error while processing - NullPointerException</a>
     */
    @Test
    public void testNullPointerEnumValueOfOverloaded() {
        parseCode(("public enum EsmDcVoltageSensor {\n" + (((((((("    A;\n" + "    void bar(int ... args) {\n") + "        int idx;\n") + "        int startIdx;\n") + "        String name = EsmDcVoltageSensor.valueOf((byte) (idx - startIdx)).getName();\n") + "    }\n") + // that's the overloaded method
        "    public EsmDCVoltageSensor valueOf(byte b) {\n") + "    }\n") + "}\n")));
    }

    private static final String TEST_DEMO = (((((((("public class Foo  {" + (PMD.EOL)) + " void bar(ArrayList buz) { ") + (PMD.EOL)) + "  buz.add(\"foo\");") + (PMD.EOL)) + " } ") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST_EQ = (((((((("public class Foo  {" + (PMD.EOL)) + " boolean foo(String a, String b) { ") + (PMD.EOL)) + "  return a == b; ") + (PMD.EOL)) + " } ") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST1 = (((((((((((("import java.io.*;" + (PMD.EOL)) + "public class Foo  {") + (PMD.EOL)) + " void buz( ) {") + (PMD.EOL)) + "  Object o = new Serializable() { int x; };") + (PMD.EOL)) + "  Object o1 = new Serializable() { int x; };") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST_INITIALIZERS = (((((("public class Foo  {" + (PMD.EOL)) + " {} ") + (PMD.EOL)) + " static {} ") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST_CATCH_BLOCKS = (((((((((((((("public class Foo  {" + (PMD.EOL)) + " void foo() { ") + (PMD.EOL)) + "  try { ") + (PMD.EOL)) + "  } catch (Exception e) { ") + (PMD.EOL)) + "   e.printStackTrace(); ") + (PMD.EOL)) + "  } ") + (PMD.EOL)) + " } ") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST_FIELD = (((((((((((((((("public class MyClass {" + (PMD.EOL)) + " private int aaaaaaaaaa; ") + (PMD.EOL)) + " boolean bbbbbbbbbb = MyClass.ASCENDING; ") + (PMD.EOL)) + " private int zzzzzzzzzz;") + (PMD.EOL)) + " private void doIt() {") + (PMD.EOL)) + "  if (bbbbbbbbbb) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}") + (PMD.EOL);

    public static final String TEST_INNER_CLASS = (((((((((((((((((((((("public class Outer {" + (PMD.EOL)) + "  private static class Inner {") + (PMD.EOL)) + "    private int i;") + (PMD.EOL)) + "    private Inner(int i) {") + (PMD.EOL)) + "      this.i = i;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "  public int modify(int i) {") + (PMD.EOL)) + "    Inner in = new Inner(i);") + (PMD.EOL)) + "    return in.i;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}") + (PMD.EOL);
}

