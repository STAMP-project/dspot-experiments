/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTLiteralTest {
    @Test
    public void testIsStringLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST1);
        Assert.assertTrue(literals.iterator().next().isStringLiteral());
    }

    @Test
    public void testIsNotStringLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST2);
        Assert.assertFalse(literals.iterator().next().isStringLiteral());
    }

    @Test
    public void testIsIntIntLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST3);
        Assert.assertTrue(literals.iterator().next().isIntLiteral());
    }

    @Test
    public void testIsIntLongLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST4);
        Assert.assertTrue(literals.iterator().next().isLongLiteral());
    }

    @Test
    public void testIsFloatFloatLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST5);
        Assert.assertTrue(literals.iterator().next().isFloatLiteral());
    }

    @Test
    public void testIsFloatDoubleLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST6);
        Assert.assertTrue(literals.iterator().next().isDoubleLiteral());
    }

    @Test
    public void testIsCharLiteral() {
        Set<ASTLiteral> literals = ParserTstUtil.getNodes(ASTLiteral.class, ASTLiteralTest.TEST7);
        Assert.assertTrue(literals.iterator().next().isCharLiteral());
    }

    @Test
    public void testIntValueParsing() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("1___234");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        Assert.assertEquals(1234, literal.getValueAsInt());
    }

    @Test
    public void testIntValueParsingBinary() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("0b0000_0010");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        Assert.assertEquals(2, literal.getValueAsInt());
    }

    @Test
    public void testIntValueParsingNegativeHexa() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-0X0000_000f");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        Assert.assertEquals((-15), literal.getValueAsInt());
    }

    @Test
    public void testFloatValueParsingNegative() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-3_456.123_456");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        Assert.assertEquals((-3456.1235F), literal.getValueAsFloat(), 0);
    }

    @Test
    public void testStringUnicodeEscapesNotEscaped() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc?abc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        Assert.assertEquals("abc?abc", literal.getEscapedStringLiteral());
        Assert.assertEquals("abc?abc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesInvalid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\\uXYZAabc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        Assert.assertEquals("abc\\uXYZAabc", literal.getEscapedStringLiteral());
        Assert.assertEquals("abc\\uXYZAabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\u1234abc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        Assert.assertEquals("abc\\u1234abc", literal.getEscapedStringLiteral());
        Assert.assertEquals("abc?abc", literal.getImage());
    }

    @Test
    public void testCharacterUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setCharLiteral();
        literal.setImage("0");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(6);
        Assert.assertEquals("\\u0030", literal.getEscapedStringLiteral());
        Assert.assertEquals("0", literal.getImage());
    }

    private static final String TEST1 = ((("public class Foo {" + (PMD.EOL)) + "  String x = \"foo\";") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((("public class Foo {" + (PMD.EOL)) + "  int x = 42;") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((("public class Foo {" + (PMD.EOL)) + "  int x = 42;") + (PMD.EOL)) + "}";

    private static final String TEST4 = ((("public class Foo {" + (PMD.EOL)) + "  long x = 42L;") + (PMD.EOL)) + "}";

    private static final String TEST5 = ((("public class Foo {" + (PMD.EOL)) + "  float x = 3.14159f;") + (PMD.EOL)) + "}";

    private static final String TEST6 = ((("public class Foo {" + (PMD.EOL)) + "  double x = 3.14159;") + (PMD.EOL)) + "}";

    private static final String TEST7 = ((("public class Foo {" + (PMD.EOL)) + "  char x = 'x';") + (PMD.EOL)) + "}";
}

