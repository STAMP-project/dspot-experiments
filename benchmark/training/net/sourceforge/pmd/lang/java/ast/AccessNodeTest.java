/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class AccessNodeTest {
    public static class MyAccessNode extends AbstractJavaAccessNode {
        public MyAccessNode(int i) {
            super(i);
        }

        public MyAccessNode(JavaParser parser, int i) {
            super(parser, i);
        }
    }

    @Test
    public void testModifiersOnClassDecl() {
        Set<ASTClassOrInterfaceDeclaration> ops = ParserTstUtil.getNodes(ASTClassOrInterfaceDeclaration.class, AccessNodeTest.TEST1);
        Assert.assertTrue(ops.iterator().next().isPublic());
    }

    private static final String TEST1 = "public class Foo {}";

    @Test
    public void testStatic() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not static.", node.isStatic());
        node.setStatic(true);
        Assert.assertTrue("Node set to static, not static.", node.isStatic());
    }

    @Test
    public void testPublic() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not public.", node.isPublic());
        node.setPublic(true);
        Assert.assertTrue("Node set to public, not public.", node.isPublic());
    }

    @Test
    public void testProtected() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not protected.", node.isProtected());
        node.setProtected(true);
        Assert.assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    @Test
    public void testPrivate() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not private.", node.isPrivate());
        node.setPrivate(true);
        Assert.assertTrue("Node set to private, not private.", node.isPrivate());
    }

    @Test
    public void testFinal() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not final.", node.isFinal());
        node.setFinal(true);
        Assert.assertTrue("Node set to final, not final.", node.isFinal());
    }

    @Test
    public void testSynchronized() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not synchronized.", node.isSynchronized());
        node.setSynchronized(true);
        Assert.assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    @Test
    public void testVolatile() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not volatile.", node.isVolatile());
        node.setVolatile(true);
        Assert.assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    @Test
    public void testTransient() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not transient.", node.isTransient());
        node.setTransient(true);
        Assert.assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    @Test
    public void testNative() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not native.", node.isNative());
        node.setNative(true);
        Assert.assertTrue("Node set to native, not native.", node.isNative());
    }

    @Test
    public void testAbstract() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not abstract.", node.isAbstract());
        node.setAbstract(true);
        Assert.assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    @Test
    public void testStrict() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertFalse("Node should default to not strict.", node.isStrictfp());
        node.setStrictfp(true);
        Assert.assertTrue("Node set to strict, not strict.", node.isStrictfp());
    }

    @Test
    public void testPackagePrivate() {
        AccessNode node = new AccessNodeTest.MyAccessNode(1);
        Assert.assertTrue("Node should default to package private.", node.isPackagePrivate());
        node.setPrivate(true);
        Assert.assertFalse("Node set to private, still package private.", node.isPackagePrivate());
        node = new AccessNodeTest.MyAccessNode(1);
        node.setPublic(true);
        Assert.assertFalse("Node set to public, still package private.", node.isPackagePrivate());
        node = new AccessNodeTest.MyAccessNode(1);
        node.setProtected(true);
        Assert.assertFalse("Node set to protected, still package private.", node.isPackagePrivate());
    }
}

