/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;


import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.Ecmascript3Parser;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;


public class EcmascriptParserTest extends EcmascriptParserTestBase {
    /**
     * https://sourceforge.net/p/pmd/bugs/1043/
     */
    @Test
    public void testLineNumbers() {
        final String SOURCE_CODE = (((("function a() {" + (PMD.EOL)) + "  alert('hello');") + (PMD.EOL)) + "}") + (PMD.EOL);
        EcmascriptNode<AstRoot> node = parse(SOURCE_CODE);
        Assert.assertEquals(1, node.getBeginLine());
        Assert.assertEquals(1, node.getBeginColumn());
        Assert.assertEquals(3, node.getEndLine());
        Assert.assertEquals(1, node.getEndColumn());
        Node child = node.getFirstChildOfType(ASTFunctionNode.class);
        Assert.assertEquals(1, child.getBeginLine());
        Assert.assertEquals(1, child.getBeginColumn());
        Assert.assertEquals(3, child.getEndLine());
        Assert.assertEquals(1, child.getEndColumn());
        child = node.getFirstDescendantOfType(ASTFunctionCall.class);
        Assert.assertEquals(2, child.getBeginLine());
        Assert.assertEquals(3, child.getBeginColumn());
        Assert.assertEquals(2, child.getEndLine());
        Assert.assertEquals(16, child.getEndColumn());
    }

    /**
     * https://sourceforge.net/p/pmd/bugs/1149/
     */
    @Test
    public void testLineNumbersWithinEcmascriptRules() {
        String source = "function f(x){\n" + ((((("   if (x) {\n" + "       return 1;\n") + "   } else {\n") + "       return 0;\n") + "   }\n") + "}");
        final List<String> output = new ArrayList<>();
        class MyEcmascriptRule extends AbstractEcmascriptRule {
            public Object visit(ASTScope node, Object data) {
                output.add(((("Scope from " + (node.getBeginLine())) + " to ") + (node.getEndLine())));
                return super.visit(node, data);
            }
        }
        MyEcmascriptRule rule = new MyEcmascriptRule();
        RuleContext ctx = new RuleContext();
        rule.apply(Arrays.asList(parse(source)), ctx);
        Assert.assertEquals("Scope from 2 to 4", output.get(0));
        Assert.assertEquals("Scope from 4 to 6", output.get(1));
    }

    /**
     * Test bug https://sourceforge.net/p/pmd/bugs/1118/
     */
    @Test
    public void testArrayAccess() {
        EcmascriptNode<AstRoot> node = parse("function a() { b['a'] = 1; c[1] = 2; }");
        List<ASTElementGet> arrays = node.findDescendantsOfType(ASTElementGet.class);
        Assert.assertEquals("b", arrays.get(0).getTarget().getImage());
        Assert.assertEquals("a", arrays.get(0).getElement().getImage());
        Assert.assertEquals("c", arrays.get(1).getTarget().getImage());
        Assert.assertEquals("1", arrays.get(1).getElement().getImage());
    }

    /**
     * Test for bug #1136 ECAMScript: NullPointerException in getLeft() and
     * getRight()
     */
    @Test
    public void testArrayMethod() {
        EcmascriptNode<AstRoot> rootNode = parse(("function test(){\n" + ((((("  a();      // OK\n" + "  b.c();    // OK\n") + "  d[0]();   // OK\n") + "  e[0].f(); // OK\n") + "  y.z[0](); // FAIL ==> java.lang.NullPointerException\n") + "}")));
        List<ASTFunctionCall> calls = rootNode.findDescendantsOfType(ASTFunctionCall.class);
        List<String> results = new ArrayList<>();
        for (ASTFunctionCall f : calls) {
            Node node = f.getTarget();
            results.add(getName(node));
        }
        Assert.assertEquals("[a, b.c, d[], e[].f, y.z[]]", results.toString());
    }

    /**
     * https://sourceforge.net/p/pmd/bugs/1150/ #1150 "EmptyExpression" for
     * valid statements!
     */
    @Test
    public void testCaseAsIdentifier() {
        ASTAstRoot rootNode = parse(("function f(a){\n" + (("    a.case.flag = 1;\n" + "    return;\n") + "}")));
        ASTBlock block = rootNode.getFirstDescendantOfType(ASTBlock.class);
        Assert.assertFalse(((block.jjtGetChild(0)) instanceof ASTEmptyExpression));
        Assert.assertTrue(((block.jjtGetChild(0)) instanceof ASTExpressionStatement));
        Assert.assertTrue(((block.jjtGetChild(0).jjtGetChild(0)) instanceof ASTAssignment));
    }

    /**
     * https://sourceforge.net/p/pmd/bugs/1045/ #1045 //NOPMD not working (or
     * not implemented) with ECMAscript
     */
    @Test
    public void testSuppresionComment() {
        Ecmascript3Parser parser = new Ecmascript3Parser(new EcmascriptParserOptions());
        Reader sourceCode = new StringReader(("function(x) {\n" + ("x = x; //NOPMD I know what I\'m doing\n" + "}\n")));
        parser.parse("foo", sourceCode);
        Assert.assertEquals(" I know what I'm doing", parser.getSuppressMap().get(2));
        Assert.assertEquals(1, parser.getSuppressMap().size());
        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        parserOptions.setSuppressMarker("FOOOO");
        parser = new Ecmascript3Parser(parserOptions);
        sourceCode = new StringReader(("function(x) {\n" + (("y = y; //NOPMD xyz\n" + "x = x; //FOOOO I know what I\'m doing\n") + "}\n")));
        parser.parse("foo", sourceCode);
        Assert.assertEquals(" I know what I'm doing", parser.getSuppressMap().get(3));
        Assert.assertEquals(1, parser.getSuppressMap().size());
    }

    /**
     * #1191 Ecmascript fails to parse "void(0)"
     */
    @Test
    public void testVoidKeyword() {
        ASTAstRoot rootNode = parse(("function f(matchFn, fieldval, n){\n" + ("    return (matchFn)?(matcharray = eval(matchFn+\"(\'\"+fieldval+\"\',\'\"+n.id+\"\')\")):void(0);\n" + "}\n")));
        ASTUnaryExpression unary = rootNode.getFirstDescendantOfType(ASTUnaryExpression.class);
        Assert.assertEquals("void", unary.getImage());
    }

    /**
     * #1192 Ecmascript fails to parse this operator " ^= "
     */
    @Test
    public void testXorAssignment() {
        ASTAstRoot rootNode = parse(("function f() { var x = 2; x ^= 2; x &= 2; x |= 2; " + ("x &&= true; x ||= false; x *= 2; x /= 2; x %= 2; x += 2; x -= 2; " + "x <<= 2; x >>= 2; x >>>= 2; }")));
        ASTAssignment infix = rootNode.getFirstDescendantOfType(ASTAssignment.class);
        Assert.assertEquals("^=", infix.getImage());
    }
}

