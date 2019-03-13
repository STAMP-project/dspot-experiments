/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Arrays;
import java.util.List;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class JavaQualifiedNameTest {
    @Test
    public void testEmptyPackage() {
        final String TEST = "class Foo {}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            Assert.assertEquals("Foo", qname.toString());
            Assert.assertTrue(qname.getPackageList().isEmpty());
            Assert.assertTrue(qname.isUnnamedPackage());
            Assert.assertEquals(1, qname.getClassList().size());
        }
    }

    @Test
    public void testPackage() {
        final String TEST = "package foo.bar; class Bzaz{}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            Assert.assertEquals("foo.bar.Bzaz", qname.toString());
            Assert.assertEquals(2, qname.getPackageList().size());
            Assert.assertEquals(1, qname.getClassList().size());
        }
    }

    @Test
    public void testNestedClass() {
        final String TEST = "package foo.bar; class Bzaz{ class Bor{ class Foo{}}}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
                case "Foo" :
                    Assert.assertEquals("foo.bar.Bzaz$Bor$Foo", qname.toString());
                    Assert.assertEquals(3, qname.getClassList().size());
                    break;
                default :
                    break;
            }
        }
    }

    @Test
    public void testNestedEnum() {
        final String TEST = "package foo.bar; class Foo { enum Bzaz{HOO;}}";
        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);
        for (ASTEnumDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            Assert.assertEquals("foo.bar.Foo$Bzaz", qname.toString());
            Assert.assertEquals(2, qname.getPackageList().size());
            Assert.assertEquals(2, qname.getClassList().size());
        }
    }

    @Test
    public void testEnum() {
        final String TEST = "package foo.bar; enum Bzaz{HOO;}";
        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);
        for (ASTEnumDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            Assert.assertEquals("foo.bar.Bzaz", qname.toString());
            Assert.assertEquals(2, qname.getPackageList().size());
            Assert.assertEquals(1, qname.getClassList().size());
        }
    }

    @Test
    public void testEnumMethodMember() {
        final String TEST = "package foo.bar; enum Bzaz{HOO; void foo(){}}";
        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);
        for (ASTMethodDeclaration coid : nodes) {
            JavaOperationQualifiedName qname = coid.getQualifiedName();
            Assert.assertEquals("foo.bar.Bzaz#foo()", qname.toString());
            Assert.assertEquals(2, qname.getClassName().getPackageList().size());
            Assert.assertEquals(1, qname.getClassName().getClassList().size());
            Assert.assertEquals("foo()", qname.getOperation());
        }
    }

    @Test
    public void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
                case "Foo" :
                    Assert.assertEquals("Bzaz$Bor$Foo", qname.toString());
                    Assert.assertTrue(qname.getPackageList().isEmpty());
                    Assert.assertTrue(qname.isUnnamedPackage());
                    Assert.assertEquals(3, qname.getClassList().size());
                    break;
                default :
                    break;
            }
        }
    }

    @Test
    public void testMethod() {
        final String TEST = "package bar; class Bzaz{ public void foo(){}}";
        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);
        for (ASTMethodDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            Assert.assertEquals("bar.Bzaz#foo()", qname.toString());
            Assert.assertNotNull(qname.getOperation());
            Assert.assertEquals("foo()", qname.getOperation());
        }
    }

    @Test
    public void testConstructor() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(){}}";
        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);
        for (ASTConstructorDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            Assert.assertEquals("bar.Bzaz#Bzaz()", qname.toString());
            Assert.assertNotNull(qname.getOperation());
            Assert.assertEquals("Bzaz()", qname.getOperation());
        }
    }

    @Test
    public void testConstructorWithParams() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j, String k){}}";
        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);
        for (ASTConstructorDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            Assert.assertEquals("bar.Bzaz#Bzaz(int, String)", qname.toString());
            Assert.assertNotNull(qname.getOperation());
            Assert.assertEquals("Bzaz(int, String)", qname.getOperation());
        }
    }

    @Test
    public void testConstructorOverload() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j) {} public Bzaz(int j, String k){}}";
        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);
        ASTConstructorDeclaration[] arr = nodes.toArray(new ASTConstructorDeclaration[2]);
        Assert.assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
    }

    @Test
    public void testMethodOverload() {
        final String TEST = "package bar; class Bzaz{ public void foo(String j) {} " + "public void foo(int j){} public void foo(double k){}}";
        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);
        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        Assert.assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        Assert.assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }

    @Test
    public void testParseClass() {
        JavaTypeQualifiedName outer = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("foo.bar.Bzaz")));
        JavaTypeQualifiedName nested = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg")));
        Assert.assertEquals(1, outer.getClassList().size());
        Assert.assertEquals("Bzaz", outer.getClassList().get(0));
        Assert.assertEquals(2, nested.getClassList().size());
        Assert.assertEquals("Bzaz", nested.getClassList().get(0));
        Assert.assertEquals("Bolg", nested.getClassList().get(1));
    }

    @Test
    public void testParsePackages() {
        JavaTypeQualifiedName packs = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg")));
        JavaTypeQualifiedName nopacks = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString("Bzaz")));
        Assert.assertNotNull(packs.getPackageList());
        Assert.assertEquals("foo", packs.getPackageList().get(0));
        Assert.assertEquals("bar", packs.getPackageList().get(1));
        Assert.assertTrue(nopacks.getPackageList().isEmpty());
    }

    @Test
    public void testParseOperation() {
        JavaOperationQualifiedName noparams = ((JavaOperationQualifiedName) (QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg#bar()")));
        JavaOperationQualifiedName params = ((JavaOperationQualifiedName) (QualifiedNameFactory.ofString("foo.bar.Bzaz#bar(String, int)")));
        Assert.assertEquals("bar()", noparams.getOperation());
        Assert.assertEquals("bar(String, int)", params.getOperation());
    }

    @Test
    public void testParseLocalClasses() {
        final String SIMPLE = "foo.bar.Bzaz$1Local";
        final String NESTED = "foo.Bar$1Local$Nested";
        JavaTypeQualifiedName simple = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString(SIMPLE)));
        JavaTypeQualifiedName nested = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString(NESTED)));
        Assert.assertNotNull(simple);
        Assert.assertTrue(simple.isLocalClass());
        Assert.assertFalse(simple.isAnonymousClass());
        Assert.assertNotNull(nested);
        Assert.assertFalse(nested.isLocalClass());
        Assert.assertFalse(simple.isAnonymousClass());
        Assert.assertEquals(SIMPLE, simple.toString());
        Assert.assertEquals(NESTED, nested.toString());
    }

    @Test
    public void testParseAnonymousClass() {
        final String SIMPLE = "Bzaz$12$13";
        JavaTypeQualifiedName simple = ((JavaTypeQualifiedName) (QualifiedNameFactory.ofString(SIMPLE)));
        Assert.assertNotNull(simple);
        Assert.assertTrue(simple.isAnonymousClass());
        Assert.assertFalse(simple.isLocalClass());
        Assert.assertEquals("12", simple.getClassList().get(1));
        Assert.assertEquals("13", simple.getClassList().get(2));
        Assert.assertEquals(SIMPLE, simple.toString());
    }

    @Test
    public void testParseLambdaName() {
        final String IN_LAMBDA = "foo.bar.Bzaz$1Local#lambda$null$12";
        final String STATIC = "foo.bar.Bzaz#lambda$static$12";
        final String NEW = "foo.bar.Bzaz#lambda$new$1";
        final String IN_METHOD = "Bzaz#lambda$myMethod$4";
        for (String s : Arrays.asList(IN_LAMBDA, STATIC, NEW, IN_METHOD)) {
            JavaOperationQualifiedName qname = ((JavaOperationQualifiedName) (QualifiedNameFactory.ofString(s)));
            Assert.assertNotNull(qname);
            Assert.assertTrue(qname.isLambda());
            Assert.assertEquals(s, qname.toString());
            Assert.assertEquals(qname, QualifiedNameFactory.ofString(qname.toString()));
        }
    }

    @Test
    public void testParseLambdaInEnumConstant() {
        final String LAMBA_IN_ENUM_CONSTANT = "package foo; import java.util.function.Function; enum Bar { CONST(e -> e); Bar(Function<Object,Object> o) {} }";
        final String QNAME = "foo.Bar#lambda$static$0";
        ASTLambdaExpression node = getNodes(ASTLambdaExpression.class, LAMBA_IN_ENUM_CONSTANT).get(0);
        Assert.assertNotNull(node);
        Assert.assertEquals(QualifiedNameFactory.ofString(QNAME), node.getQualifiedName());
    }

    @Test
    public void testParseMalformed() {
        Assert.assertNull(QualifiedNameFactory.ofString(".foo.bar.Bzaz"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar."));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz()"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String,)"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String , int)"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$23(String)"));
        Assert.assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$"));
    }

    @Test
    public void testSimpleLocalClass() {
        final String TEST = "package bar; class Boron { public void foo(String j) { class Local {} } }";
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        JavaQualifiedName qname = QualifiedNameFactory.ofString("bar.Boron$1Local");
        Assert.assertEquals(qname, classes.get(1).getQualifiedName());
    }

    @Test
    public void testLocalClassNameClash() {
        final String TEST = "package bar; class Bzaz{ void foo() { class Local {} } {// initializer\n class Local {}}}";
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        Assert.assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$1Local"), classes.get(1).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$2Local"), classes.get(2).getQualifiedName());
    }

    @Test
    public void testLocalClassDeepNesting() {
        final String TEST = "class Bzaz{ void foo() { " + ((((((("  class Local { " + "    class Nested {") + "      {") + "        class InnerLocal{}") + "      }") + "    }") + "  }") + "}}");
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        Assert.assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local"), classes.get(1).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested"), classes.get(2).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested$1InnerLocal"), classes.get(3).getQualifiedName());
    }

    @Test
    public void testAnonymousClass() {
        final String TEST = "class Bzaz{ void foo() { " + ((("  new Runnable() {" + "      public void run() {}") + "  };") + "}}");
        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        Assert.assertFalse(classes.get(0).getQualifiedName().isLocalClass());
        Assert.assertTrue(classes.get(0).getQualifiedName().isAnonymousClass());
        Assert.assertTrue("1".equals(classes.get(0).getQualifiedName().getClassSimpleName()));
    }

    @Test
    public void testMultipleAnonymousClasses() {
        final String TEST = "class Bzaz{ void foo() { " + (((((("  new Runnable() {" + "      public void run() {}") + "  };") + "  new Runnable() {") + "      public void run() {}") + "  };") + "}}");
        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);
        Assert.assertNotEquals(classes.get(0), classes.get(1));
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$2"), classes.get(1).getQualifiedName());
    }

    @Test
    public void testNestedAnonymousClass() {
        final String TEST = "class Bzaz{ void foo() {" + ((((((("  new Runnable() {" + "    public void run() {") + "      new Runnable() {") + "        public void run() {}") + "      };") + "    }") + "  };") + "}}");
        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);
        Assert.assertNotEquals(classes.get(0), classes.get(1));
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1"), classes.get(1).getQualifiedName());
    }

    @Test
    public void testLocalInAnonymousClass() {
        final String TEST = "class Bzaz{ void foo() {" + ((((("  new Runnable() {" + "    public void run() {") + "      class FooRunnable {}") + "    }") + "  };") + "}}");
        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        Assert.assertTrue(classes.get(1).isLocal());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1FooRunnable"), classes.get(1).getQualifiedName());
    }

    @Test
    public void testLambdaInStaticInitializer() {
        final String TEST = "import java.util.function.*;" + ((((((("class Bzaz{ " + "  static {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
    }

    @Test
    public void testLambdaInInitializerAndConstructor() {
        final String TEST = "import java.util.function.*;" + ((((((((((((("class Bzaz{ " + "  {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "  public Bzaz() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$0"), lambdas.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$1"), lambdas.get(1).getQualifiedName());
    }

    @Test
    public void testLambdaField() {
        final String TEST = "import java.util.function.*;" + ((((((("public class Bzaz { " + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     public static Consumer<String> k = s -> {") + "         System.out.println(s);") + "     };") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$0"), lambdas.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$1"), lambdas.get(1).getQualifiedName());
    }

    @Test
    public void testLambdaInterfaceField() {
        final String TEST = "import java.util.function.*;" + ((((((("public interface Bzaz { " + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     public static Consumer<String> k = s -> {") + "         System.out.println(s);") + "     };") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$1"), lambdas.get(1).getQualifiedName());
    }

    @Test
    public void testLambdaLocalClassField() {
        final String TEST = "import java.util.function.*;" + (((((((("public class Bzaz { " + "  public void boo() {") + "     class Local {") + "         Consumer<String> l = s -> {") + "             System.out.println(s);") + "         };") + "     }") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local#lambda$Local$0"), lambdas.get(0).getQualifiedName());
    }

    @Test
    public void testLambdaAnonymousClassField() {
        final String TEST = "import java.util.function.*;" + (((((((("public class Bzaz { " + "  public void boo() {") + "     new Anonymous() {") + "         Consumer<String> l = s -> {") + "             System.out.println(s);") + "         };") + "     };") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1#lambda$$0"), lambdas.get(0).getQualifiedName());
        // This is here because of a bug with the regex parsing, which failed on "Bzaz$1#lambda$$0"
        // because the second segment of the lambda name was the empty string
        Assert.assertTrue(lambdas.get(0).getQualifiedName().isLambda());
        Assert.assertEquals("lambda$$0", lambdas.get(0).getQualifiedName().getOperation());
        Assert.assertEquals(2, lambdas.get(0).getQualifiedName().getClassName().getClassList().size());
    }

    @Test
    public void testLambdasInMethod() {
        final String TEST = "import java.util.function.*;" + ((((((((((((((((((("class Bzaz{ " + "  public void bar() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "  public void fooBar() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "  public void gollum() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$bar$0"), lambdas.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$fooBar$1"), lambdas.get(1).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$gollum$2"), lambdas.get(2).getQualifiedName());
    }

    @Test
    public void testLambdaCounterBelongsToClass() {
        final String TEST = "import java.util.function.*;" + ((((((((((((((((((((((((("class Bzaz{ " + "  static {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "  public Bzaz() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "  }") + "  public void gollum() {") + "     Consumer<String> l = s -> {") + "         System.out.println(s);") + "     };") + "     l.accept(\"foo\");") + "     new Runnable() {") + "       public void run() {") + "         Runnable r = () -> {};") + "         r.run();") + "       }") + "     }.run();") + "  }") + "}");
        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$1"), lambdas.get(1).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$gollum$2"), lambdas.get(2).getQualifiedName());
        Assert.assertEquals(QualifiedNameFactory.ofString("Bzaz$1#lambda$run$0"), lambdas.get(3).getQualifiedName());// counter starts over for anon class

        // This is here because of a bug with the regex parsing, which caused "Bzaz$1#lambda$run$0"
        // to be parsed as
        // * classes == List("Bzaz", "#lambda", "run", "0").reverse()
        // * localIndices == List(-1, 1, -1, -1)
        // * operation == null
        Assert.assertTrue(lambdas.get(3).getQualifiedName().isLambda());
        Assert.assertEquals("lambda$run$0", lambdas.get(3).getQualifiedName().getOperation());
        Assert.assertEquals(2, lambdas.get(3).getQualifiedName().getClassName().getClassList().size());
    }

    @Test
    public void testGetType() {
        JavaTypeQualifiedName qname = QualifiedNameFactory.ofClass(ASTAdditiveExpression.class);
        Assert.assertEquals(qname.getType(), ASTAdditiveExpression.class);
    }
}

