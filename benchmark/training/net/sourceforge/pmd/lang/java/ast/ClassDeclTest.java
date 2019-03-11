/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import org.junit.Test;


public class ClassDeclTest {
    @Test
    public void testPublic() {
        String[] access = new String[]{ "public" };
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, false, false);
    }

    @Test
    public void testAbstract() {
        String[] access = new String[]{ "abstract" };
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, true, false, false);
    }

    @Test
    public void testFinal() {
        String[] access = new String[]{ "final" };
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, true, false);
    }

    @Test
    public void testStrict() {
        String[] access = new String[]{ "strictfp" };
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, false, true);
    }

    @Test
    public void testPublicFinal() {
        String[] access = new String[]{ "public", "final" };
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, true, false);
    }
}

