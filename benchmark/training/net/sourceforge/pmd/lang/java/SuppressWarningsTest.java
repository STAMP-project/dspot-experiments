/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;


import JavaLanguageModule.NAME;
import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.testframework.RuleTst;
import org.junit.Assert;
import org.junit.Test;


public class SuppressWarningsTest extends RuleTst {
    private static class BarRule extends AbstractJavaRule {
        @Override
        public Object visit(ASTCompilationUnit cu, Object ctx) {
            // Convoluted rule to make sure the violation is reported for the
            // ASTCompilationUnit node
            for (ASTClassOrInterfaceDeclaration c : cu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class)) {
                if (c.getImage().equalsIgnoreCase("bar")) {
                    addViolation(ctx, cu);
                }
            }
            return super.visit(cu, ctx);
        }

        @Override
        public String getName() {
            return "NoBar";
        }
    }

    @Test
    public void testClassLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST1, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
        runTestFromString(SuppressWarningsTest.TEST2, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testInheritedSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST3, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testMethodLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST4, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testConstructorLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST5, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testFieldLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST6, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testParameterLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST7, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testLocalVariableLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST8, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppression() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue1() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9_VALUE1, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue2() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9_VALUE2, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue3() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9_VALUE3, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionMulitpleValues1() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9_MULTIPLE_VALUES_1, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testSpecificSuppressionMulitpleValues2() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST9_MULTIPLE_VALUES_2, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testNoSuppressionBlank() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST10, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(2, rpt.size());
    }

    @Test
    public void testNoSuppressionSomethingElseS() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST11, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(2, rpt.size());
    }

    @Test
    public void testSuppressAll() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST12, new FooRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    @Test
    public void testSpecificSuppressionAtTopLevel() {
        Report rpt = new Report();
        runTestFromString(SuppressWarningsTest.TEST13, new SuppressWarningsTest.BarRule(), rpt, LanguageRegistry.getLanguage(NAME).getVersion("1.5"));
        Assert.assertEquals(0, rpt.size());
    }

    private static final String TEST1 = ("@SuppressWarnings(\"PMD\")" + (PMD.EOL)) + "public class Foo {}";

    private static final String TEST2 = ((((((((("@SuppressWarnings(\"PMD\")" + (PMD.EOL)) + "public class Foo {") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((((((((((((("public class Baz {" + (PMD.EOL)) + " @SuppressWarnings(\"PMD\")") + (PMD.EOL)) + " public class Bar {") + (PMD.EOL)) + "  void bar() {") + (PMD.EOL)) + "   int foo;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST4 = ((((((((("public class Foo {" + (PMD.EOL)) + " @SuppressWarnings(\"PMD\")") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST5 = ((((((((("public class Bar {" + (PMD.EOL)) + " @SuppressWarnings(\"PMD\")") + (PMD.EOL)) + " public Bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST6 = ((((((((((("public class Bar {" + (PMD.EOL)) + " @SuppressWarnings(\"PMD\")") + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST7 = ((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar(@SuppressWarnings(\"PMD\") int foo) {}") + (PMD.EOL)) + "}";

    private static final String TEST8 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(\"PMD\") int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(\"PMD.NoFoo\") int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9_VALUE1 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(value = \"PMD.NoFoo\") int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9_VALUE2 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings({\"PMD.NoFoo\"}) int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9_VALUE3 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(value = {\"PMD.NoFoo\"}) int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9_MULTIPLE_VALUES_1 = ((((((((((("@SuppressWarnings({\"PMD.NoFoo\", \"PMD.NoBar\"})" + (PMD.EOL)) + "public class Bar {") + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST9_MULTIPLE_VALUES_2 = ((((((((((("@SuppressWarnings(value = {\"PMD.NoFoo\", \"PMD.NoBar\"})" + (PMD.EOL)) + "public class Bar {") + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST10 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(\"\") int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST11 = ((((((((("public class Bar {" + (PMD.EOL)) + " int foo;") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  @SuppressWarnings(\"SomethingElse\") int foo;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST12 = ((("public class Bar {" + (PMD.EOL)) + " @SuppressWarnings(\"all\") int foo;") + (PMD.EOL)) + "}";

    private static final String TEST13 = ((("@SuppressWarnings(\"PMD.NoBar\")" + (PMD.EOL)) + "public class Bar {") + (PMD.EOL)) + "}";
}

