/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import org.junit.Assert;
import org.junit.Test;


public class NameOccurrencesTest extends STBBaseTst {
    @Test
    public void testSuper() {
        parseCode(NameOccurrencesTest.TEST1);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        Assert.assertEquals("super", occs.getNames().get(0).getImage());
    }

    @Test
    public void testThis() {
        parseCode(NameOccurrencesTest.TEST2);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        Assert.assertEquals("this", occs.getNames().get(0).getImage());
        Assert.assertEquals("x", occs.getNames().get(1).getImage());
    }

    @Test
    public void testNameLinkage() {
        parseCode(NameOccurrencesTest.TEST2);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        JavaNameOccurrence thisNameOccurrence = occs.getNames().get(0);
        Assert.assertEquals(thisNameOccurrence.getNameForWhichThisIsAQualifier(), occs.getNames().get(1));
    }

    @Test
    public void testSimpleVariableOccurrence() {
        parseCode(NameOccurrencesTest.TEST3);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        Assert.assertEquals("x", occs.getNames().get(0).getImage());
        Assert.assertFalse(occs.getNames().get(0).isThisOrSuper());
        Assert.assertFalse(occs.getNames().get(0).isMethodOrConstructorInvocation());
        Assert.assertTrue(occs.getNames().get(0).isOnLeftHandSide());
    }

    @Test
    public void testQualifiedOccurrence() {
        parseCode(NameOccurrencesTest.TEST4);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        Assert.assertEquals("b", occs.getNames().get(0).getImage());
        Assert.assertEquals("x", occs.getNames().get(1).getImage());
    }

    @Test
    public void testIsSelfAssignment() {
        parseCode(NameOccurrencesTest.TEST5);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(2));
        Assert.assertTrue(occs.getNames().get(0).isSelfAssignment());
        parseCode(NameOccurrencesTest.TEST6);
        nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        occs = new NameFinder(nodes.get(2));
        Assert.assertTrue(occs.getNames().get(0).isSelfAssignment());
    }

    @Test
    public void testEnumStaticUsage() {
        parseCode(NameOccurrencesTest.TEST_ENUM);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(4));
        List<JavaNameOccurrence> names = occs.getNames();
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("myEnum", names.get(0).getImage());
        Assert.assertFalse(names.get(0).isMethodOrConstructorInvocation());
        Assert.assertEquals("desc", names.get(1).getImage());
        Assert.assertFalse(names.get(1).isMethodOrConstructorInvocation());
        Assert.assertEquals("equals", names.get(2).getImage());
        Assert.assertTrue(names.get(2).isMethodOrConstructorInvocation());
    }

    public static final String TEST1 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  super.x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST2 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  this.x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST3 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST4 = ((((((("public class Foo {" + (PMD.EOL)) + " void foo() {") + (PMD.EOL)) + "  b.x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    public static final String TEST5 = ((((((((((((((((((((("public class Foo{" + (PMD.EOL)) + "    private int counter;") + (PMD.EOL)) + "    private Foo(){") + (PMD.EOL)) + "        counter = 0;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "    private int foo(){") + (PMD.EOL)) + "        if (++counter < 3) {") + (PMD.EOL)) + "            return 0;") + (PMD.EOL)) + "        }") + (PMD.EOL)) + "        return 1;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "}";

    public static final String TEST6 = ((((((((((((((((((((("public class Foo{" + (PMD.EOL)) + "    private int counter;") + (PMD.EOL)) + "    private Foo(){") + (PMD.EOL)) + "        counter = 0;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "    private int foo(){") + (PMD.EOL)) + "        if (++this.counter < 3) {") + (PMD.EOL)) + "            return 0;") + (PMD.EOL)) + "        }") + (PMD.EOL)) + "        return 1;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "}";

    public static final String TEST_ENUM = ((((((((((((((((((((((("public enum MyEnum {" + (PMD.EOL)) + "  A(\"a\");") + (PMD.EOL)) + "  private final String desc;") + (PMD.EOL)) + "  private MyEnum(String desc) {") + (PMD.EOL)) + "    this.desc = desc;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + "  public static MyEnum byDesc(String desc) {") + (PMD.EOL)) + "    for (MyEnum myEnum : value()) {") + (PMD.EOL)) + "      if (myEnum.desc.equals(desc)) return myEnum;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "    return null;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }";
}

