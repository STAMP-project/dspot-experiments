/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import org.junit.Assert;
import org.junit.Test;


public class VariableNameDeclarationTest extends STBBaseTst {
    @Test
    public void testConstructor() {
        parseCode(VariableNameDeclarationTest.TEST1);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        Scope s = nodes.get(0).getScope();
        NameDeclaration decl = s.getDeclarations().keySet().iterator().next();
        Assert.assertEquals("bar", decl.getImage());
        Assert.assertEquals(3, decl.getNode().getBeginLine());
    }

    @Test
    public void testExceptionBlkParam() {
        ASTCompilationUnit acu = ParserTstUtil.getNodes(ASTCompilationUnit.class, VariableNameDeclarationTest.EXCEPTION_PARAMETER).iterator().next();
        ASTVariableDeclaratorId id = acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        Assert.assertTrue(isExceptionBlockParameter());
    }

    @Test
    public void testIsArray() {
        parseCode(VariableNameDeclarationTest.TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        Assert.assertTrue(decl.isArray());
    }

    @Test
    public void testPrimitiveType() {
        parseCode(VariableNameDeclarationTest.TEST1);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        Assert.assertTrue(decl.isPrimitiveType());
    }

    @Test
    public void testArrayIsReferenceType() {
        parseCode(VariableNameDeclarationTest.TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        Assert.assertTrue(decl.isReferenceType());
    }

    @Test
    public void testPrimitiveTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST3);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator().next();
        Assert.assertEquals("int", getTypeImage());
    }

    @Test
    public void testRefTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST4);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator().next();
        Assert.assertEquals("String", getTypeImage());
    }

    @Test
    public void testParamTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST5);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator().next();
        Assert.assertEquals("String", getTypeImage());
    }

    @Test
    public void testVarKeywordTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST6);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator().next();
        Assert.assertEquals("java.util.ArrayList", getType().getName());
        // since the type is inferred, there is no type image
        Assert.assertEquals(null, getTypeImage());
    }

    @Test
    public void testVarKeywordWithPrimitiveTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST7);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator().next();
        Assert.assertEquals("long", getType().getName());
        // since the type is inferred, there is no type image
        Assert.assertEquals(null, getTypeImage());
    }

    @Test
    public void testVarKeywordWithIndirectReference() {
        parseCode(VariableNameDeclarationTest.TEST8);
        Iterator<NameDeclaration> nameDeclarationIterator = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getDeclarations().keySet().iterator();
        nameDeclarationIterator.next();// first variable 'bar'

        NameDeclaration decl = nameDeclarationIterator.next();// second variable 'foo'

        Assert.assertEquals("java.lang.String", getType().getName());
        // since the type is inferred, there is no type image
        Assert.assertEquals(null, getTypeImage());
    }

    @Test
    public void testLamdaParameterTypeImage() {
        parseCode(VariableNameDeclarationTest.TEST9);
        List<ASTVariableDeclaratorId> variableDeclaratorIds = acu.findDescendantsOfType(ASTVariableDeclaratorId.class, true);
        List<VariableNameDeclaration> nameDeclarations = new ArrayList<>();
        for (ASTVariableDeclaratorId variableDeclaratorId : variableDeclaratorIds) {
            nameDeclarations.add(variableDeclaratorId.getNameDeclaration());
        }
        Assert.assertEquals("Map", getTypeImage());// variable 'bar'

        Assert.assertEquals(null, getTypeImage());// variable 'key'

        Assert.assertEquals(null, getTypeImage());// variable 'value'

        // variable 'foo'
        Assert.assertEquals("foo", nameDeclarations.get(3).getName());
        Assert.assertEquals("long", getType().getName());
        // since the type is inferred, there is no type image
        Assert.assertEquals(null, getTypeImage());
    }

    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";

    public static final String TEST1 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  int bar = 42;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST2 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  try {} catch(Exception e) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST3 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  int[] x;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST4 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  String x;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST5 = ((("public class Foo {" + (PMD.EOL)) + " void foo(String x) {}") + (PMD.EOL)) + "}";

    public static final String TEST6 = ((((((("import java.util.ArrayList; public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  var bar = new ArrayList<String>(\"param\");") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST7 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  var bar = 42L;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST8 = ((((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  var bar = \"test\";") + (PMD.EOL)) + "  var foo = bar;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST9 = ((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  Map<String, Object> bar = new HashMap<>();") + (PMD.EOL)) + "  bar.forEach((key, value) -> {") + (PMD.EOL)) + "   if (value instanceof String) {") + (PMD.EOL)) + "    var foo = 42L;") + (PMD.EOL)) + "    System.out.println(value);") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  });") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

