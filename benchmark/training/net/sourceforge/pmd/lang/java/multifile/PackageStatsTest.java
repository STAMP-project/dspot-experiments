/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.multifile;


import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests functionality of PackageStats
 *
 * @author Cl?ment Fournier
 */
public class PackageStatsTest {
    private PackageStats pack;

    @Test
    public void testAddClass() {
        JavaTypeQualifiedName qname = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("org.foo.Boo")));
        Assert.assertNull(pack.getClassStats(qname, false));
        Assert.assertNotNull(pack.getClassStats(qname, true));
        // now it's added, this shouldn't return null
        Assert.assertNotNull(pack.getClassStats(qname, false));
    }

    @Test
    public void testAddOperation() {
        final String TEST = "package org.foo; class Boo{ " + "public void foo(){}}";
        ASTMethodOrConstructorDeclaration node = ParserTstUtil.getOrderedNodes(ASTMethodDeclaration.class, TEST).get(0);
        JavaOperationQualifiedName qname = node.getQualifiedName();
        JavaOperationSignature signature = JavaOperationSignature.buildFor(node);
        Assert.assertFalse(pack.hasMatchingSig(qname, new JavaOperationSigMask()));
        ClassStats clazz = pack.getClassStats(qname.getClassName(), true);
        clazz.addOperation("foo()", signature);
        Assert.assertTrue(pack.hasMatchingSig(qname, new JavaOperationSigMask()));
    }

    @Test
    public void testAddField() {
        final String TEST = "package org.foo; class Boo{ " + "public String bar;}";
        ASTFieldDeclaration node = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST).get(0);
        JavaTypeQualifiedName qname = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("org.foo.Boo")));
        String fieldName = "bar";
        JavaFieldSignature signature = JavaFieldSignature.buildFor(node);
        Assert.assertFalse(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));
        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addField(fieldName, signature);
        Assert.assertTrue(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));
    }
}

