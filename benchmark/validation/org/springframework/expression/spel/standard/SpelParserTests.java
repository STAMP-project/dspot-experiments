/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.expression.spel.standard;


import SpelMessage.MISSING_CONSTRUCTOR_ARGS;
import SpelMessage.NON_TERMINATING_DOUBLE_QUOTED_STRING;
import SpelMessage.NON_TERMINATING_QUOTED_STRING;
import SpelMessage.NOT_AN_INTEGER;
import SpelMessage.NOT_A_LONG;
import SpelMessage.REAL_CANNOT_BE_LONG;
import SpelMessage.RUN_OUT_OF_ARGUMENTS;
import SpelMessage.UNEXPECTED_DATA_AFTER_DOT;
import SpelMessage.UNEXPECTED_ESCAPE_CHAR;
import TokenKind.LITERAL_STRING;
import TokenKind.NOT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static TokenKind.LITERAL_STRING;
import static TokenKind.MINUS;
import static TokenKind.NOT;


/**
 *
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 */
public class SpelParserTests {
    @Test
    public void theMostBasic() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2");
        Assert.assertNotNull(expr);
        Assert.assertNotNull(expr.getAST());
        Assert.assertEquals(2, expr.getValue());
        Assert.assertEquals(Integer.class, expr.getValueType());
        Assert.assertEquals(2, expr.getAST().getValue(null));
    }

    @Test
    public void valueType() {
        SpelExpressionParser parser = new SpelExpressionParser();
        EvaluationContext ctx = new StandardEvaluationContext();
        Class<?> c = parser.parseRaw("2").getValueType();
        Assert.assertEquals(Integer.class, c);
        c = parser.parseRaw("12").getValueType(ctx);
        Assert.assertEquals(Integer.class, c);
        c = parser.parseRaw("null").getValueType();
        Assert.assertNull(c);
        c = parser.parseRaw("null").getValueType(ctx);
        Assert.assertNull(c);
        Object o = parser.parseRaw("null").getValue(ctx, Integer.class);
        Assert.assertNull(o);
    }

    @Test
    public void whitespace() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2      +    3");
        Assert.assertEquals(5, expr.getValue());
        expr = parser.parseRaw("2	+	3");
        Assert.assertEquals(5, expr.getValue());
        expr = parser.parseRaw("2\n+\t3");
        Assert.assertEquals(5, expr.getValue());
        expr = parser.parseRaw("2\r\n+\t3");
        Assert.assertEquals(5, expr.getValue());
    }

    @Test
    public void arithmeticPlus1() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2+2");
        Assert.assertNotNull(expr);
        Assert.assertNotNull(expr.getAST());
        Assert.assertEquals(4, expr.getValue());
    }

    @Test
    public void arithmeticPlus2() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("37+41");
        Assert.assertEquals(78, expr.getValue());
    }

    @Test
    public void arithmeticMultiply1() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2*3");
        Assert.assertNotNull(expr);
        Assert.assertNotNull(expr.getAST());
        // printAst(expr.getAST(),0);
        Assert.assertEquals(6, expr.getValue());
    }

    @Test
    public void arithmeticPrecedence1() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2*3+5");
        Assert.assertEquals(11, expr.getValue());
    }

    @Test
    public void generalExpressions() {
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("new String");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(MISSING_CONSTRUCTOR_ARGS, spe.getMessageCode());
            Assert.assertEquals(10, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("new String(3,");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(RUN_OUT_OF_ARGUMENTS, spe.getMessageCode());
            Assert.assertEquals(10, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("new String(3");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(RUN_OUT_OF_ARGUMENTS, spe.getMessageCode());
            Assert.assertEquals(10, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("new String(");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(RUN_OUT_OF_ARGUMENTS, spe.getMessageCode());
            Assert.assertEquals(10, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("\"abc");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(NON_TERMINATING_DOUBLE_QUOTED_STRING, spe.getMessageCode());
            Assert.assertEquals(0, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            parser.parseRaw("'abc");
            Assert.fail();
        } catch (ParseException ex) {
            Assert.assertTrue((ex instanceof SpelParseException));
            SpelParseException spe = ((SpelParseException) (ex));
            Assert.assertEquals(NON_TERMINATING_QUOTED_STRING, spe.getMessageCode());
            Assert.assertEquals(0, spe.getPosition());
            Assert.assertTrue(ex.getMessage().contains(ex.getExpressionString()));
        }
    }

    @Test
    public void arithmeticPrecedence2() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expr = parser.parseRaw("2+3*5");
        Assert.assertEquals(17, expr.getValue());
    }

    @Test
    public void arithmeticPrecedence3() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("3+10/2");
        Assert.assertEquals(8, expr.getValue());
    }

    @Test
    public void arithmeticPrecedence4() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("10/2+3");
        Assert.assertEquals(8, expr.getValue());
    }

    @Test
    public void arithmeticPrecedence5() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("(4+10)/2");
        Assert.assertEquals(7, expr.getValue());
    }

    @Test
    public void arithmeticPrecedence6() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("(3+2)*2");
        Assert.assertEquals(10, expr.getValue());
    }

    @Test
    public void booleanOperators() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("true");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("false");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("false and false");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("true and (true or false)");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("true and true or false");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("!true");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("!(false or true)");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
    }

    @Test
    public void booleanOperators_symbolic_spr9614() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("true");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("false");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("false && false");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("true && (true || false)");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("true && true || false");
        Assert.assertEquals(Boolean.TRUE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("!true");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
        expr = new SpelExpressionParser().parseRaw("!(false || true)");
        Assert.assertEquals(Boolean.FALSE, expr.getValue(Boolean.class));
    }

    @Test
    public void stringLiterals() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("'howdy'");
        Assert.assertEquals("howdy", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("'hello '' world'");
        Assert.assertEquals("hello ' world", expr.getValue());
    }

    @Test
    public void stringLiterals2() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("'howdy'.substring(0,2)");
        Assert.assertEquals("ho", expr.getValue());
    }

    @Test
    public void testStringLiterals_DoubleQuotes_spr9620() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("\"double quote: \"\".\"");
        Assert.assertEquals("double quote: \".", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("\"hello \"\" world\"");
        Assert.assertEquals("hello \" world", expr.getValue());
    }

    @Test
    public void testStringLiterals_DoubleQuotes_spr9620_2() {
        try {
            new SpelExpressionParser().parseRaw("\"double quote: \\\"\\\".\"");
            Assert.fail("Should have failed");
        } catch (SpelParseException spe) {
            Assert.assertEquals(17, spe.getPosition());
            Assert.assertEquals(UNEXPECTED_ESCAPE_CHAR, spe.getMessageCode());
        }
    }

    @Test
    public void positionalInformation() {
        SpelExpression expr = new SpelExpressionParser().parseRaw("true and true or false");
        SpelNode rootAst = expr.getAST();
        OpOr operatorOr = ((OpOr) (rootAst));
        OpAnd operatorAnd = ((OpAnd) (operatorOr.getLeftOperand()));
        SpelNode rightOrOperand = operatorOr.getRightOperand();
        // check position for final 'false'
        Assert.assertEquals(17, rightOrOperand.getStartPosition());
        Assert.assertEquals(22, rightOrOperand.getEndPosition());
        // check position for first 'true'
        Assert.assertEquals(0, operatorAnd.getLeftOperand().getStartPosition());
        Assert.assertEquals(4, operatorAnd.getLeftOperand().getEndPosition());
        // check position for second 'true'
        Assert.assertEquals(9, operatorAnd.getRightOperand().getStartPosition());
        Assert.assertEquals(13, operatorAnd.getRightOperand().getEndPosition());
        // check position for OperatorAnd
        Assert.assertEquals(5, operatorAnd.getStartPosition());
        Assert.assertEquals(8, operatorAnd.getEndPosition());
        // check position for OperatorOr
        Assert.assertEquals(14, operatorOr.getStartPosition());
        Assert.assertEquals(16, operatorOr.getEndPosition());
    }

    @Test
    public void tokenKind() {
        TokenKind tk = NOT;
        Assert.assertFalse(tk.hasPayload());
        Assert.assertEquals("NOT(!)", tk.toString());
        tk = MINUS;
        Assert.assertFalse(tk.hasPayload());
        Assert.assertEquals("MINUS(-)", tk.toString());
        tk = LITERAL_STRING;
        Assert.assertEquals("LITERAL_STRING", tk.toString());
        Assert.assertTrue(tk.hasPayload());
    }

    @Test
    public void token() {
        Token token = new Token(NOT, 0, 3);
        Assert.assertEquals(NOT, token.kind);
        Assert.assertEquals(0, token.startPos);
        Assert.assertEquals(3, token.endPos);
        Assert.assertEquals("[NOT(!)](0,3)", token.toString());
        token = new Token(LITERAL_STRING, "abc".toCharArray(), 0, 3);
        Assert.assertEquals(LITERAL_STRING, token.kind);
        Assert.assertEquals(0, token.startPos);
        Assert.assertEquals(3, token.endPos);
        Assert.assertEquals("[LITERAL_STRING:abc](0,3)", token.toString());
    }

    @Test
    public void exceptions() {
        ExpressionException exprEx = new ExpressionException("test");
        Assert.assertEquals("test", exprEx.getSimpleMessage());
        Assert.assertEquals("test", exprEx.toDetailedString());
        Assert.assertEquals("test", exprEx.getMessage());
        exprEx = new ExpressionException("wibble", "test");
        Assert.assertEquals("test", exprEx.getSimpleMessage());
        Assert.assertEquals("Expression [wibble]: test", exprEx.toDetailedString());
        Assert.assertEquals("Expression [wibble]: test", exprEx.getMessage());
        exprEx = new ExpressionException("wibble", 3, "test");
        Assert.assertEquals("test", exprEx.getSimpleMessage());
        Assert.assertEquals("Expression [wibble] @3: test", exprEx.toDetailedString());
        Assert.assertEquals("Expression [wibble] @3: test", exprEx.getMessage());
    }

    @Test
    public void parseMethodsOnNumbers() {
        checkNumber("3.14.toString()", "3.14", String.class);
        checkNumber("3.toString()", "3", String.class);
    }

    @Test
    public void numerics() {
        checkNumber("2", 2, Integer.class);
        checkNumber("22", 22, Integer.class);
        checkNumber("+22", 22, Integer.class);
        checkNumber("-22", (-22), Integer.class);
        checkNumber("2L", 2L, Long.class);
        checkNumber("22l", 22L, Long.class);
        checkNumber("0x1", 1, Integer.class);
        checkNumber("0x1L", 1L, Long.class);
        checkNumber("0xa", 10, Integer.class);
        checkNumber("0xAL", 10L, Long.class);
        checkNumberError("0x", NOT_AN_INTEGER);
        checkNumberError("0xL", NOT_A_LONG);
        checkNumberError(".324", UNEXPECTED_DATA_AFTER_DOT);
        checkNumberError("3.4L", REAL_CANNOT_BE_LONG);
        checkNumber("3.5f", 3.5F, Float.class);
        checkNumber("1.2e3", 1200.0, Double.class);
        checkNumber("1.2e+3", 1200.0, Double.class);
        checkNumber("1.2e-3", 0.0012, Double.class);
        checkNumber("1.2e3", 1200.0, Double.class);
        checkNumber("1e+3", 1000.0, Double.class);
    }
}

