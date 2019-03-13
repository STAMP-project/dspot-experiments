/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.testdata.InterfaceWithNestedClass;
import org.junit.Assert;
import org.junit.Test;


public class ASTFieldDeclarationTest {
    @Test
    public void testIsArray() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTFieldDeclarationTest.TEST1);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        Assert.assertTrue(node.isArray());
        Assert.assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultiDimensionalArray() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTFieldDeclarationTest.TEST2);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        Assert.assertEquals(3, node.getArrayDepth());
    }

    @Test
    public void testIsSyntacticallyPublic() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava14(ASTFieldDeclarationTest.TEST3);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        Assert.assertFalse(node.isSyntacticallyPublic());
        Assert.assertFalse(node.isPackagePrivate());
        Assert.assertFalse(node.isPrivate());
        Assert.assertFalse(node.isProtected());
        Assert.assertTrue(node.isFinal());
        Assert.assertTrue(node.isStatic());
        Assert.assertTrue(node.isPublic());
    }

    @Test
    public void testWithEnum() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava15(ASTFieldDeclarationTest.TEST4);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        Assert.assertFalse(node.isInterfaceMember());
    }

    @Test
    public void testWithAnnotation() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava15(ASTFieldDeclarationTest.TEST5);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        Assert.assertFalse(node.isInterfaceMember());
        Assert.assertTrue(node.isAnnotationMember());
    }

    private static final String TEST1 = ((("class Foo {" + (PMD.EOL)) + " String[] foo;") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((("class Foo {" + (PMD.EOL)) + " String[][][] foo;") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((("interface Foo {" + (PMD.EOL)) + " int BAR = 6;") + (PMD.EOL)) + "}";

    private static final String TEST4 = ((((("public enum Foo {" + (PMD.EOL)) + " FOO(1);") + (PMD.EOL)) + " private int x;") + (PMD.EOL)) + "}";

    private static final String TEST5 = ((("public @interface Foo {" + (PMD.EOL)) + " int BAR = 6;") + (PMD.EOL)) + "}";

    @Test
    public void testGetVariableName() {
        int id = 0;
        ASTFieldDeclaration n = new ASTFieldDeclaration((id++));
        ASTType t = new ASTType((id++));
        ASTVariableDeclarator decl = new ASTVariableDeclarator((id++));
        ASTVariableDeclaratorId declid = new ASTVariableDeclaratorId((id++));
        n.jjtAddChild(t, 0);
        t.jjtAddChild(decl, 0);
        decl.jjtAddChild(declid, 0);
        declid.setImage("foo");
        Assert.assertEquals("foo", n.getVariableName());
    }

    @Test
    public void testPrivateFieldInNestedClassInsideInterface() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava10(InterfaceWithNestedClass.class);
        List<ASTFieldDeclaration> fields = cu.findDescendantsOfType(ASTFieldDeclaration.class, true);
        Assert.assertEquals(2, fields.size());
        Assert.assertEquals("MAPPING", fields.get(0).getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage());
        Assert.assertTrue(fields.get(0).isPublic());
        Assert.assertFalse(fields.get(0).isPrivate());
        Assert.assertEquals("serialVersionUID", fields.get(1).getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage());
        Assert.assertFalse(fields.get(1).isPublic());
        Assert.assertTrue(fields.get(1).isPrivate());
    }
}

