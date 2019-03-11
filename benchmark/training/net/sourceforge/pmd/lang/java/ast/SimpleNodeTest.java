/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;


public class SimpleNodeTest {
    @Test
    public void testMethodDiffLines() {
        Set<ASTMethodDeclaration> methods = ParserTstUtil.getNodes(ASTMethodDeclaration.class, SimpleNodeTest.METHOD_DIFF_LINES);
        verifyNode(methods.iterator().next(), 2, 9, 4, 2);
    }

    @Test
    public void testMethodSameLine() {
        Set<ASTMethodDeclaration> methods = ParserTstUtil.getNodes(ASTMethodDeclaration.class, SimpleNodeTest.METHOD_SAME_LINE);
        verifyNode(methods.iterator().next(), 2, 9, 2, 21);
    }

    @Test
    public void testNoLookahead() {
        String code = SimpleNodeTest.NO_LOOKAHEAD;// 1, 8 -> 1, 20

        Set<ASTClassOrInterfaceDeclaration> uCD = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, code);
        verifyNode(uCD.iterator().next(), 1, 8, 1, 20);
    }

    @Test
    public void testHasExplicitExtends() {
        String code = SimpleNodeTest.HAS_EXPLICIT_EXTENDS;
        ASTClassOrInterfaceDeclaration ucd = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        Assert.assertTrue(((ucd.jjtGetChild(0)) instanceof ASTExtendsList));
    }

    @Test
    public void testNoExplicitExtends() {
        String code = SimpleNodeTest.NO_EXPLICIT_EXTENDS;
        ASTClassOrInterfaceDeclaration ucd = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        Assert.assertFalse(((ucd.jjtGetChild(0)) instanceof ASTExtendsList));
    }

    @Test
    public void testHasExplicitImplements() {
        String code = SimpleNodeTest.HAS_EXPLICIT_IMPLEMENTS;
        ASTClassOrInterfaceDeclaration ucd = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        Assert.assertTrue(((ucd.jjtGetChild(0)) instanceof ASTImplementsList));
    }

    @Test
    public void testNoExplicitImplements() {
        String code = SimpleNodeTest.NO_EXPLICIT_IMPLEMENTS;
        ASTClassOrInterfaceDeclaration ucd = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        Assert.assertFalse(((ucd.jjtGetChild(0)) instanceof ASTImplementsList));
    }

    @Test
    public void testColumnsOnQualifiedName() {
        Set<ASTName> name = ParserTstUtil.getNodes(ASTName.class, SimpleNodeTest.QUALIFIED_NAME);
        Iterator<ASTName> i = name.iterator();
        while (i.hasNext()) {
            Node node = i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 1, 19);
            }
        } 
    }

    @Test
    public void testLineNumbersForNameSplitOverTwoLines() {
        Set<ASTName> name = ParserTstUtil.getNodes(ASTName.class, SimpleNodeTest.BROKEN_LINE_IN_NAME);
        Iterator<ASTName> i = name.iterator();
        while (i.hasNext()) {
            Node node = i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 2, 4);
            }
            if (node.getImage().equals("Foo")) {
                verifyNode(node, 2, 15, 2, 18);
            }
        } 
    }

    @Test
    public void testLineNumbersAreSetOnAllSiblings() {
        for (ASTBlock b : ParserTstUtil.getNodes(ASTBlock.class, SimpleNodeTest.LINE_NUMBERS_ON_SIBLINGS)) {
            Assert.assertTrue(((b.getBeginLine()) > 0));
        }
        for (ASTVariableInitializer b : ParserTstUtil.getNodes(ASTVariableInitializer.class, SimpleNodeTest.LINE_NUMBERS_ON_SIBLINGS)) {
            Assert.assertTrue(((b.getBeginLine()) > 0));
        }
        for (ASTExpression b : ParserTstUtil.getNodes(ASTExpression.class, SimpleNodeTest.LINE_NUMBERS_ON_SIBLINGS)) {
            Assert.assertTrue(((b.getBeginLine()) > 0));
        }
    }

    @Test
    public void testFindDescendantsOfType() {
        ASTBlock block = new ASTBlock(2);
        block.jjtAddChild(new ASTReturnStatement(1), 0);
        Assert.assertEquals(1, block.findDescendantsOfType(ASTReturnStatement.class).size());
    }

    @Test
    public void testFindDescendantsOfTypeMultiple() {
        ASTBlock block = new ASTBlock(1);
        block.jjtAddChild(new ASTBlockStatement(2), 0);
        block.jjtAddChild(new ASTBlockStatement(3), 1);
        List<ASTBlockStatement> nodes = block.findDescendantsOfType(ASTBlockStatement.class);
        Assert.assertEquals(2, nodes.size());
    }

    @Test
    public void testFindDescendantsOfTypeRecurse() {
        ASTBlock block = new ASTBlock(1);
        ASTBlock childBlock = new ASTBlock(2);
        block.jjtAddChild(childBlock, 0);
        childBlock.jjtAddChild(new ASTMethodDeclaration(3), 0);
        List<ASTMethodDeclaration> nodes = block.findDescendantsOfType(ASTMethodDeclaration.class);
        Assert.assertEquals(1, nodes.size());
    }

    @Test
    public void testGetFirstChild() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);
        Node n = block.getFirstDescendantOfType(ASTStatement.class);
        Assert.assertNotNull(n);
        Assert.assertTrue((n instanceof ASTStatement));
        Assert.assertEquals(x, n);
    }

    @Test
    public void testGetFirstChildNested() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        ASTAssignmentOperator x1 = new ASTAssignmentOperator(4);
        x.jjtAddChild(x1, 0);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);
        Node n = block.getFirstDescendantOfType(ASTAssignmentOperator.class);
        Assert.assertNotNull(n);
        Assert.assertTrue((n instanceof ASTAssignmentOperator));
        Assert.assertEquals(x1, n);
    }

    @Test
    public void testGetFirstChildNestedDeeper() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        ASTAssignmentOperator x1 = new ASTAssignmentOperator(4);
        ASTName x2 = new ASTName(5);
        x.jjtAddChild(x1, 0);
        x1.jjtAddChild(x2, 0);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);
        Node n = block.getFirstDescendantOfType(ASTName.class);
        Assert.assertNotNull(n);
        Assert.assertTrue((n instanceof ASTName));
        Assert.assertEquals(x2, n);
    }

    @Test
    public void testParentMethods() {
        ASTCompilationUnit u = ParserTstUtil.parseJava14(SimpleNodeTest.TEST1);
        ASTMethodDeclarator d = u.getFirstDescendantOfType(ASTMethodDeclarator.class);
        Assert.assertSame("getFirstParentOfType ASTMethodDeclaration", d.jjtGetParent(), d.getFirstParentOfType(ASTMethodDeclaration.class));
        Assert.assertNull("getFirstParentOfType ASTName", d.getFirstParentOfType(ASTName.class));
        Assert.assertSame("getNthParent 1", d.jjtGetParent(), d.getNthParent(1));
        Assert.assertSame("getNthParent 2", d.jjtGetParent().jjtGetParent(), d.getNthParent(2));
        Assert.assertSame("getNthParent 6", u, d.getNthParent(6));
        Assert.assertNull("getNthParent 7", d.getNthParent(7));
        Assert.assertNull("getNthParent 8", d.getNthParent(8));
    }

    private static final String TEST1 = ((((((("public class Test {" + (PMD.EOL)) + "  void bar(String s) {") + (PMD.EOL)) + "   s = s.toLowerCase();") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}";

    @Test
    public void testContainsNoInnerWithAnonInner() {
        ASTCompilationUnit c = ParserTstUtil.getNodes(ASTCompilationUnit.class, SimpleNodeTest.CONTAINS_NO_INNER_WITH_ANON_INNER).iterator().next();
        List<ASTFieldDeclaration> res = c.findDescendantsOfType(ASTFieldDeclaration.class);
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void testContainsChildOfType() {
        ASTClassOrInterfaceDeclaration c = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, SimpleNodeTest.CONTAINS_CHILDREN_OF_TYPE).iterator().next();
        Assert.assertTrue(c.hasDescendantOfType(ASTFieldDeclaration.class));
    }

    @Test
    public void testXPathNodeSelect() throws JaxenException {
        ASTClassOrInterfaceDeclaration c = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, SimpleNodeTest.TEST_XPATH).iterator().next();
        List<Node> nodes = c.findChildNodesWithXPath("//FieldDeclaration");
        Assert.assertEquals(2, nodes.size());
        Assert.assertTrue(((nodes.get(0)) instanceof ASTFieldDeclaration));
        Assert.assertTrue(c.hasDescendantMatchingXPath("//FieldDeclaration"));
        Assert.assertFalse(c.hasDescendantMatchingXPath("//MethodDeclaration"));
    }

    @Test
    public void testUserData() {
        ASTClassOrInterfaceDeclaration c = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, SimpleNodeTest.HAS_EXPLICIT_EXTENDS).iterator().next();
        Assert.assertNull(c.getUserData());
        c.setUserData("foo");
        Assert.assertEquals("foo", c.getUserData());
        c.setUserData(null);
        Assert.assertNull(c.getUserData());
    }

    private static final String HAS_EXPLICIT_EXTENDS = "public class Test extends Foo {}";

    private static final String NO_EXPLICIT_EXTENDS = "public class Test {}";

    private static final String HAS_EXPLICIT_IMPLEMENTS = "public class Test implements Foo {}";

    private static final String NO_EXPLICIT_IMPLEMENTS = "public class Test {}";

    private static final String METHOD_SAME_LINE = ((("public class Test {" + (PMD.EOL)) + " public void foo() {}") + (PMD.EOL)) + "}";

    private static final String QUALIFIED_NAME = ("import java.io.File;" + (PMD.EOL)) + "public class Foo{}";

    private static final String BROKEN_LINE_IN_NAME = ((("import java.io." + (PMD.EOL)) + "File;") + (PMD.EOL)) + "public class Foo{}";

    private static final String LINE_NUMBERS_ON_SIBLINGS = ((((((((((((((("public class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  try {") + (PMD.EOL)) + "  } catch (Exception1 e) {") + (PMD.EOL)) + "   int x =2;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " if (x != null) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String NO_LOOKAHEAD = "public class Foo { }";

    private static final String METHOD_DIFF_LINES = ((((((("public class Test {" + (PMD.EOL)) + " public void foo() {") + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String CONTAINS_CHILDREN_OF_TYPE = ((("public class Test {" + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + "}";

    private static final String CONTAINS_NO_INNER = ((((((("public class Test {" + (PMD.EOL)) + "  public class Inner {") + (PMD.EOL)) + "   int foo;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}";

    private static final String CONTAINS_NO_INNER_WITH_ANON_INNER = ((((((("public class Test {" + (PMD.EOL)) + "  void bar() {") + (PMD.EOL)) + "   foo(new Fuz() { int x = 2;});") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "}";

    private static final String TEST_XPATH = ((((("public class Test {" + (PMD.EOL)) + "  int x = 2;") + (PMD.EOL)) + "  int y = 42;") + (PMD.EOL)) + "}";
}

