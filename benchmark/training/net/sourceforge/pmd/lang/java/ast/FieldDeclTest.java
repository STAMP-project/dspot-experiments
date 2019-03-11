/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import org.junit.Assert;
import org.junit.Test;


public class FieldDeclTest {
    @Test
    public void testPublic() {
        String[] access = new String[]{ "public" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testProtected() {
        String[] access = new String[]{ "protected" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be protected.", afd.isProtected());
    }

    @Test
    public void testPrivate() {
        String[] access = new String[]{ "private" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testStatic() {
        String[] access = new String[]{ "private", "static" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be static.", afd.isStatic());
        Assert.assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testFinal() {
        String[] access = new String[]{ "public", "final" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be final.", afd.isFinal());
        Assert.assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testTransient() {
        String[] access = new String[]{ "private", "transient" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be private.", afd.isPrivate());
        Assert.assertTrue("Expecting field to be transient.", afd.isTransient());
    }

    @Test
    public void testVolatile() {
        String[] access = new String[]{ "private", "volatile" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        Assert.assertTrue("Expecting field to be volatile.", afd.isVolatile());
        Assert.assertTrue("Expecting field to be private.", afd.isPrivate());
    }
}

