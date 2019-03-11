/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;


import apex.jorje.semantic.ast.compilation.Compilation;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class ApexQualifiedNameTest {
    @Test
    public void testClass() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse("public class Foo {}");
        ApexQualifiedName qname = ASTUserClass.class.cast(root).getQualifiedName();
        Assert.assertEquals("c__Foo", qname.toString());
        Assert.assertEquals(1, qname.getClasses().length);
        Assert.assertNotNull(qname.getNameSpace());
        Assert.assertNull(qname.getOperation());
    }

    @Test
    public void testNestedClass() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse("public class Foo { class Bar {}}");
        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTUserClass.class).getQualifiedName();
        Assert.assertEquals("c__Foo.Bar", qname.toString());
        Assert.assertEquals(2, qname.getClasses().length);
        Assert.assertNotNull(qname.getNameSpace());
        Assert.assertNull(qname.getOperation());
    }

    @Test
    public void testSimpleMethod() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse("public class Foo { String foo() {}}");
        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTMethod.class).getQualifiedName();
        Assert.assertEquals("c__Foo#foo()", qname.toString());
        Assert.assertEquals(1, qname.getClasses().length);
        Assert.assertNotNull(qname.getNameSpace());
        Assert.assertEquals("foo()", qname.getOperation());
    }

    @Test
    public void testMethodWithArguments() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse("public class Foo { String foo(String h, Foo g) {}}");
        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTMethod.class).getQualifiedName();
        Assert.assertEquals("c__Foo#foo(String,Foo)", qname.toString());
        Assert.assertEquals(1, qname.getClasses().length);
        Assert.assertNotNull(qname.getNameSpace());
        Assert.assertEquals("foo(String,Foo)", qname.getOperation());
    }

    @Test
    public void testOverLoads() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse(("public class Foo { " + (("String foo(String h) {} " + "String foo(int c) {}") + "String foo(Foo c) {}}")));
        List<ASTMethod> methods = root.findDescendantsOfType(ASTMethod.class);
        for (ASTMethod m1 : methods) {
            for (ASTMethod m2 : methods) {
                if (m1 != m2) {
                    Assert.assertNotEquals(m1.getQualifiedName(), m2.getQualifiedName());
                }
            }
        }
    }

    @Test
    public void testTrigger() {
        ApexNode<Compilation> root = ApexParserTestHelpers.parse("trigger myAccountTrigger on Account (before insert, before update) {}");
        List<ASTMethod> methods = root.findDescendantsOfType(ASTMethod.class);
        for (ASTMethod m : methods) {
            Assert.assertEquals("c__trigger.Account#myAccountTrigger", m.getQualifiedName().toString());
        }
    }
}

