/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.List;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 * @since 6.1.0
 */
public class ASTClassOrInterfaceDeclarationTest {
    private static final String LOCAL_CLASS_IN_METHOD = "class Foo { void bar() { class Local {}}}";

    private static final String NESTED_CLASS_IS_NOT_LOCAL = "class Foo { class Nested {} void bar() {}}";

    private static final String LOCAL_CLASS_IN_INITIALIZER = "class Foo { { class Local {} } }";

    private static final String LOCAL_CHILDREN_ARE_NOT_ALWAYS_LOCAL = "class Foo { { class Local { class Nested {} void bar() {class Local2 {}}}}}";

    @Test
    public void testLocalInMethod() {
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, ASTClassOrInterfaceDeclarationTest.LOCAL_CLASS_IN_METHOD);
        Assert.assertTrue(((classes.size()) == 2));
        Assert.assertFalse("Local class false-positive", classes.get(0).isLocal());
        Assert.assertTrue("Local class false-negative", classes.get(1).isLocal());
    }

    @Test
    public void testLocalInInitializer() {
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, ASTClassOrInterfaceDeclarationTest.LOCAL_CLASS_IN_INITIALIZER);
        Assert.assertTrue(((classes.size()) == 2));
        Assert.assertFalse("Local class false-positive", classes.get(0).isLocal());
        Assert.assertTrue("Local class false-negative", classes.get(1).isLocal());
    }

    @Test
    public void testNestedClassIsNotLocal() {
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, ASTClassOrInterfaceDeclarationTest.NESTED_CLASS_IS_NOT_LOCAL);
        Assert.assertTrue(((classes.size()) == 2));
        Assert.assertFalse("Local class false-positive", classes.get(0).isLocal());
        Assert.assertFalse("Local class false-positive", classes.get(1).isLocal());
    }

    @Test
    public void testLocalChildrenAreNotAlwaysLocal() {
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, ASTClassOrInterfaceDeclarationTest.LOCAL_CHILDREN_ARE_NOT_ALWAYS_LOCAL);
        Assert.assertTrue(((classes.size()) == 4));
        Assert.assertFalse("Local class false-positive", classes.get(0).isLocal());// class Foo

        Assert.assertTrue("Local class false-negative", classes.get(1).isLocal());// class Local

        Assert.assertFalse("Local class false-positive", classes.get(2).isLocal());// class Nested

        Assert.assertTrue("Local class false-negative", classes.get(3).isLocal());// class Local2

    }
}

