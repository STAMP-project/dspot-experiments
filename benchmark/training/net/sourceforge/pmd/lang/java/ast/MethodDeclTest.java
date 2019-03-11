/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import org.junit.Assert;
import org.junit.Test;


public class MethodDeclTest {
    @Test
    public void testPublic() {
        String[] access = new String[]{ "public" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testPrivate() {
        String[] access = new String[]{ "private" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testProtected() {
        String[] access = new String[]{ "protected" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be protected.", amd.isProtected());
    }

    @Test
    public void testFinal() {
        String[] access = new String[]{ "public", "final" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be final.", amd.isFinal());
        Assert.assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testSynchronized() {
        String[] access = new String[]{ "public", "synchronized" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be synchronized.", amd.isSynchronized());
        Assert.assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testAbstract() {
        String[] access = new String[]{ "public", "abstract" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be abstract.", amd.isAbstract());
        Assert.assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testNative() {
        String[] access = new String[]{ "private", "native" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be native.", amd.isNative());
        Assert.assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testStrict() {
        String[] access = new String[]{ "public", "strictfp" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        Assert.assertTrue("Expecting method to be strict.", amd.isStrictfp());
        Assert.assertTrue("Expecting method to be public.", amd.isPublic());
    }
}

