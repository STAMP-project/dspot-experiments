/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;


import Role.CONSTRUCTOR;
import Role.GETTER_OR_SETTER;
import Role.METHOD;
import Role.STATIC;
import Visibility.PACKAGE;
import Visibility.PRIVATE;
import Visibility.PROTECTED;
import Visibility.PUBLIC;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.metrics.testdata.GetterDetection;
import net.sourceforge.pmd.lang.java.metrics.testdata.SetterDetection;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link JavaSignature} and its subclasses.
 *
 * @author Cl?ment Fournier
 */
public class SignatureTest {
    // common to operation and field signatures
    @Test
    public void visibilityTest() {
        final String TEST = "class Bzaz{ " + ((((((((("public int bar;" + "String k;") + "protected double d;") + "private int i;") + "protected int x;") + "public Bzaz(){} ") + "void bar(){} ") + "protected void foo(int x){}") + "private Bzaz(int y){}") + "}");
        List<ASTMethodOrConstructorDeclaration> operationDeclarations = ParserTstUtil.getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<ASTFieldDeclaration> fieldDeclarations = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<JavaSignature> sigs = new ArrayList<>();
        for (ASTMethodOrConstructorDeclaration node : operationDeclarations) {
            sigs.add(JavaOperationSignature.buildFor(node));
        }
        // operations
        Assert.assertEquals(PUBLIC, sigs.get(0).visibility);
        Assert.assertEquals(PACKAGE, sigs.get(1).visibility);
        Assert.assertEquals(PROTECTED, sigs.get(2).visibility);
        Assert.assertEquals(PRIVATE, sigs.get(3).visibility);
        sigs.clear();
        for (ASTFieldDeclaration node : fieldDeclarations) {
            sigs.add(JavaFieldSignature.buildFor(node));
        }
        // fields
        Assert.assertEquals(PUBLIC, sigs.get(0).visibility);
        Assert.assertEquals(PACKAGE, sigs.get(1).visibility);
        Assert.assertEquals(PROTECTED, sigs.get(2).visibility);
        Assert.assertEquals(PRIVATE, sigs.get(3).visibility);
    }

    @Test
    public void operationRoleTest() {
        final String TEST = "class Bzaz{ int x; " + (((("public static void foo(){} " + "Bzaz(){} ") + "int getX(){return x;}") + " void setX(int a){x=a;}") + " public void doSomething(){}}");
        List<ASTMethodOrConstructorDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<JavaOperationSignature> sigs = new ArrayList<>();
        for (ASTMethodOrConstructorDeclaration node : nodes) {
            sigs.add(JavaOperationSignature.buildFor(node));
        }
        Assert.assertEquals(STATIC, sigs.get(0).role);
        Assert.assertEquals(CONSTRUCTOR, sigs.get(1).role);
        Assert.assertEquals(GETTER_OR_SETTER, sigs.get(2).role);
        Assert.assertEquals(GETTER_OR_SETTER, sigs.get(3).role);
        Assert.assertEquals(METHOD, sigs.get(4).role);
    }

    @Test
    public void testGetterDetection() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseJava17(GetterDetection.class);
        compilationUnit.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodDeclaration node, Object data) {
                Assert.assertEquals(GETTER_OR_SETTER, Role.get(node));
                return data;
            }
        }, null);
    }

    @Test
    public void testSetterDetection() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseJava17(SetterDetection.class);
        compilationUnit.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodDeclaration node, Object data) {
                Assert.assertEquals(GETTER_OR_SETTER, Role.get(node));
                return data;
            }
        }, null);
    }

    @Test
    public void isAbstractOperationTest() {
        final String TEST = "abstract class Bzaz{ int x; " + (((("public static abstract void foo();" + "protected abstract int bar(int x);") + "int getX(){return x;}") + "void setX(int a){x=a;}") + "public void doSomething(){}}");
        List<ASTMethodOrConstructorDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<JavaOperationSignature> sigs = new ArrayList<>();
        for (ASTMethodOrConstructorDeclaration node : nodes) {
            sigs.add(JavaOperationSignature.buildFor(node));
        }
        Assert.assertTrue(sigs.get(0).isAbstract);
        Assert.assertTrue(sigs.get(1).isAbstract);
        Assert.assertFalse(sigs.get(2).isAbstract);
        Assert.assertFalse(sigs.get(3).isAbstract);
        Assert.assertFalse(sigs.get(4).isAbstract);
    }

    @Test
    public void isFinalFieldTest() {
        final String TEST = "class Bzaz{" + ((((("public String x;" + "private int y;") + "private final int a;") + "protected final double u;") + "final long v;") + "}");
        List<ASTFieldDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<JavaFieldSignature> sigs = new ArrayList<>();
        for (ASTFieldDeclaration node : nodes) {
            sigs.add(JavaFieldSignature.buildFor(node));
        }
        Assert.assertFalse(sigs.get(0).isFinal);
        Assert.assertFalse(sigs.get(1).isFinal);
        Assert.assertTrue(sigs.get(2).isFinal);
        Assert.assertTrue(sigs.get(3).isFinal);
        Assert.assertTrue(sigs.get(4).isFinal);
    }

    @Test
    public void isStaticFieldTest() {
        final String TEST = "class Bzaz{" + ((((("public final String x;" + "private int y;") + "private static int a;") + "protected static final double u;") + "static long v;") + "}");
        List<ASTFieldDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<JavaFieldSignature> sigs = new ArrayList<>();
        for (ASTFieldDeclaration node : nodes) {
            sigs.add(JavaFieldSignature.buildFor(node));
        }
        Assert.assertFalse(sigs.get(0).isStatic);
        Assert.assertFalse(sigs.get(1).isStatic);
        Assert.assertTrue(sigs.get(2).isStatic);
        Assert.assertTrue(sigs.get(3).isStatic);
        Assert.assertTrue(sigs.get(4).isStatic);
    }

    // Ensure only one instance of a signature is created.
    @Test
    public void operationPoolTest() {
        final String TEST = "class Bzaz{ " + (("public static void foo(){} " + "public static void az(){} ") + "public static int getX(){return x;}}");
        final String TEST2 = "class Bzaz{ " + (("void foo(){} " + "void az(){} ") + "int rand(){return x;}}");
        List<ASTMethodOrConstructorDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<ASTMethodOrConstructorDeclaration> nodes2 = ParserTstUtil.getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST2);
        List<JavaOperationSignature> sigs = new ArrayList<>();
        List<JavaOperationSignature> sigs2 = new ArrayList<>();
        for (int i = 0; i < (sigs.size()); i++) {
            sigs.add(JavaOperationSignature.buildFor(nodes.get(i)));
            sigs2.add(JavaOperationSignature.buildFor(nodes2.get(i)));
        }
        for (int i = 0; i < ((sigs.size()) - 1); i++) {
            Assert.assertTrue(((sigs.get(i)) == (sigs.get((i + 1)))));
            Assert.assertTrue(((sigs2.get(i)) == (sigs2.get((i + 1)))));
        }
    }

    // Ensure only one instance of a signature is created.
    @Test
    public void fieldPoolTest() {
        final String TEST = "class Bzaz {" + ((("public int bar;" + "public String k;") + "public double d;") + "}");
        final String TEST2 = "class Foo {" + ((("private final int i;" + "private final int x;") + "private final String k;") + "}");
        List<ASTFieldDeclaration> nodes = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<ASTFieldDeclaration> nodes2 = ParserTstUtil.getOrderedNodes(ASTFieldDeclaration.class, TEST2);
        List<JavaFieldSignature> sigs = new ArrayList<>();
        List<JavaFieldSignature> sigs2 = new ArrayList<>();
        for (int i = 0; i < (sigs.size()); i++) {
            sigs.add(JavaFieldSignature.buildFor(nodes.get(i)));
            sigs2.add(JavaFieldSignature.buildFor(nodes2.get(i)));
        }
        for (int i = 0; i < ((sigs.size()) - 1); i++) {
            Assert.assertTrue(((sigs.get(i)) == (sigs.get((i + 1)))));
            Assert.assertTrue(((sigs2.get(i)) == (sigs2.get((i + 1)))));
        }
    }
}

