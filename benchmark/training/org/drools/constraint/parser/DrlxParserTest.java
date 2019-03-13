/**
 * Copyright (C) 2007-2010 J?lio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Modified by Red Hat, Inc.
 */
package org.drools.constraint.parser;


import HalfBinaryExpr.Operator.LESS;
import Operator.AND;
import Operator.EQUALS;
import Operator.GREATER;
import Operator.OR;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.drools.constraint.parser.ast.expr.CommaSeparatedMethodCallExpr;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.expr.DrlxExpression;
import org.drools.constraint.parser.ast.expr.HalfBinaryExpr;
import org.drools.constraint.parser.ast.expr.HalfPointFreeExpr;
import org.drools.constraint.parser.ast.expr.OOPathChunk;
import org.drools.constraint.parser.ast.expr.OOPathExpr;
import org.drools.constraint.parser.ast.expr.PointFreeExpr;
import org.drools.constraint.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.constraint.parser.ast.expr.TemporalLiteralExpr;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DrlxParserTest {
    private static final Collection<String> operators = new HashSet<>();

    {
        DrlxParserTest.operators.addAll(Arrays.asList("after", "before", "in", "matches", "includes"));
    }

    final ParseStart<DrlxExpression> parser = DrlxParser.buildDrlxParserWithArguments(DrlxParserTest.operators);

    @Test
    public void testParseSimpleExpr() {
        String expr = "name == \"Mark\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) (expression));
        Assert.assertEquals("name", toString(binaryExpr.getLeft()));
        Assert.assertEquals("\"Mark\"", toString(binaryExpr.getRight()));
        Assert.assertEquals(EQUALS, binaryExpr.getOperator());
    }

    @Test
    public void testParseSafeCastExpr() {
        String expr = "this instanceof Person && ((Person)this).name == \"Mark\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
    }

    @Test
    public void testParseInlineCastExpr() {
        String expr = "this#Person.name == \"Mark\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseNullSafeFieldAccessExpr() {
        String expr = "person!.name == \"Mark\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExpr() {
        String expr = "this after $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeEnclosed() {
        String expr = "(this after $a)";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeEnclosedWithNameExpr() {
        String expr = "(something after $a)";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testLiteral() {
        String bigDecimalLiteral = "bigInteger < (50B)";
        Expression bigDecimalExpr = DrlxParser.parseExpression(parser, bigDecimalLiteral).getExpr();
        Assert.assertEquals(bigDecimalLiteral, printConstraint(bigDecimalExpr));
        String bigIntegerLiteral = "bigInteger == (50I)";
        Expression bigIntegerExpr = DrlxParser.parseExpression(parser, bigIntegerLiteral).getExpr();
        Assert.assertEquals(bigIntegerLiteral, printConstraint(bigIntegerExpr));
    }

    @Test
    public void testBigDecimalLiteral() {
        String bigDecimalLiteralWithDecimals = "12.111B";
        Expression bigDecimalExprWithDecimals = DrlxParser.parseExpression(parser, bigDecimalLiteralWithDecimals).getExpr();
        Assert.assertEquals(bigDecimalLiteralWithDecimals, printConstraint(bigDecimalExprWithDecimals));
    }

    @Test
    public void testDotFreeExprWithOr() {
        String expr = "this after $a || this after $b";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof BinaryExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExprWithArgs() {
        String expr = "this after[5,8] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertFalse(isNegated());
        Assert.assertEquals("this after[5ms,8ms] $a", printConstraint(expression));// please note the parsed expression once normalized would take the time unit for milliseconds.

    }

    @Test
    public void testDotFreeExprWithArgsInfinite() {
        String expr = "this after[5s,*] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertFalse(isNegated());
        Assert.assertEquals("this after[5s,*] $a", printConstraint(expression));// please note the parsed expression once normalized would take the time unit for milliseconds.

    }

    @Test
    public void testDotFreeExprWithThreeArgsInfinite() {
        String expr = "this after[*,*,*,2s] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertFalse(isNegated());
        Assert.assertEquals("this after[*,*,*,2s] $a", printConstraint(expression));// please note the parsed expression once normalized would take the time unit for milliseconds.

    }

    @Test
    public void testDotFreeExprWithArgsNegated() {
        String expr = "this not after[5,8] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertThat(expression, CoreMatchers.instanceOf(PointFreeExpr.class));
        Assert.assertTrue(isNegated());
        Assert.assertEquals("this not after[5ms,8ms] $a", printConstraint(expression));// please note the parsed expression once normalized would take the time unit for milliseconds.

    }

    @Test
    public void testDotFreeExprWithTemporalArgs() {
        String expr = "this after[5ms,8d] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExprWithFourTemporalArgs() {
        String expr = "this includes[1s,1m,1h,1d] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testHalfDotFreeExprWithFourTemporalArgs() {
        String expr = "includes[1s,1m,1h,1d] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertThat(expression, CoreMatchers.instanceOf(HalfPointFreeExpr.class));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test(expected = ParseProblemException.class)
    public void testInvalidTemporalArgs() {
        String expr = "this after[5ms,8f] $a";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
    }

    @Test
    public void testOOPathExpr() {
        String expr = "/wife/children[age > 10]/toys";
        DrlxExpression drlx = DrlxParser.parseExpression(parser, expr);
        Expression expression = drlx.getExpr();
        Assert.assertTrue((expression instanceof OOPathExpr));
        Assert.assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithMultipleCondition() {
        String expr = "$address : /address[street == \"Elm\",city == \"Big City\"]";
        DrlxExpression drlx = DrlxParser.parseExpression(parser, expr);
        Expression expression = drlx.getExpr();
        Assert.assertTrue((expression instanceof OOPathExpr));
        Assert.assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithDeclaration() {
        String expr = "$toy : /wife/children[age > 10]/toys";
        DrlxExpression drlx = DrlxParser.parseExpression(parser, expr);
        Assert.assertEquals("$toy", drlx.getBind().asString());
        Expression expression = drlx.getExpr();
        Assert.assertTrue((expression instanceof OOPathExpr));
        Assert.assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithBackReference() {
        String expr = "$toy : /wife/children/toys[name.length == ../../name.length]";
        DrlxExpression drlx = DrlxParser.parseExpression(parser, expr);
        Assert.assertEquals("$toy", drlx.getBind().asString());
        Expression expression = drlx.getExpr();
        Assert.assertTrue((expression instanceof OOPathExpr));
        final OOPathChunk secondChunk = getChunks().get(2);
        final BinaryExpr secondChunkFirstCondition = ((BinaryExpr) (secondChunk.getConditions().get(0)));
        final DrlNameExpr rightName = ((DrlNameExpr) (getScope()));
        Assert.assertEquals(2, rightName.getBackReferencesCount());
        Assert.assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testParseTemporalLiteral() {
        String expr = "5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        Assert.assertEquals(expr, printConstraint(drlx));
        Assert.assertEquals(1, drlx.getChunks().size());
        TemporalLiteralChunkExpr chunk0 = ((TemporalLiteralChunkExpr) (drlx.getChunks().get(0)));
        Assert.assertEquals(5, chunk0.getValue());
        Assert.assertEquals(TimeUnit.SECONDS, chunk0.getTimeUnit());
    }

    @Test
    public void testParseTemporalLiteralOf2Chunks() {
        String expr = "1m5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        Assert.assertEquals(expr, printConstraint(drlx));
        Assert.assertEquals(2, drlx.getChunks().size());
        TemporalLiteralChunkExpr chunk0 = ((TemporalLiteralChunkExpr) (drlx.getChunks().get(0)));
        Assert.assertEquals(1, chunk0.getValue());
        Assert.assertEquals(TimeUnit.MINUTES, chunk0.getTimeUnit());
        TemporalLiteralChunkExpr chunk1 = ((TemporalLiteralChunkExpr) (drlx.getChunks().get(1)));
        Assert.assertEquals(5, chunk1.getValue());
        Assert.assertEquals(TimeUnit.SECONDS, chunk1.getTimeUnit());
    }

    @Test
    public void testInExpression() {
        String expr = "this in ()";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof PointFreeExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    /* This shouldn't be supported, an HalfBinaryExpr should be valid only after a && or a || */
    @Test
    public void testUnsupportedImplicitParameter() {
        String expr = "== \"Mark\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertTrue((expression instanceof HalfBinaryExpr));
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testAndWithImplicitNegativeParameter() {
        String expr = "value > -2 && < -1";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr comboExpr = ((BinaryExpr) (expression));
        Assert.assertEquals(AND, comboExpr.getOperator());
        BinaryExpr first = ((BinaryExpr) (comboExpr.getLeft()));
        Assert.assertEquals("value", toString(first.getLeft()));
        Assert.assertEquals("-2", toString(first.getRight()));
        Assert.assertEquals(GREATER, first.getOperator());
        HalfBinaryExpr second = ((HalfBinaryExpr) (comboExpr.getRight()));
        Assert.assertEquals("-1", toString(second.getRight()));
        Assert.assertEquals(LESS, second.getOperator());
    }

    @Test
    public void testOrWithImplicitParameter() {
        String expr = "name == \"Mark\" || == \"Mario\" || == \"Luca\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr comboExpr = ((BinaryExpr) (expression));
        Assert.assertEquals(OR, comboExpr.getOperator());
        BinaryExpr first = ((BinaryExpr) (getLeft()));
        Assert.assertEquals("name", toString(first.getLeft()));
        Assert.assertEquals("\"Mark\"", toString(first.getRight()));
        Assert.assertEquals(EQUALS, first.getOperator());
        HalfBinaryExpr second = ((HalfBinaryExpr) (getRight()));
        Assert.assertEquals("\"Mario\"", toString(second.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());
        HalfBinaryExpr third = ((HalfBinaryExpr) (comboExpr.getRight()));
        Assert.assertEquals("\"Luca\"", toString(third.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter() {
        String expr = "name == \"Mark\" && == \"Mario\" && == \"Luca\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr comboExpr = ((BinaryExpr) (expression));
        Assert.assertEquals(AND, comboExpr.getOperator());
        BinaryExpr first = ((BinaryExpr) (getLeft()));
        Assert.assertEquals("name", toString(first.getLeft()));
        Assert.assertEquals("\"Mark\"", toString(first.getRight()));
        Assert.assertEquals(EQUALS, first.getOperator());
        HalfBinaryExpr second = ((HalfBinaryExpr) (getRight()));
        Assert.assertEquals("\"Mario\"", toString(second.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());
        HalfBinaryExpr third = ((HalfBinaryExpr) (comboExpr.getRight()));
        Assert.assertEquals("\"Luca\"", toString(third.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter2() {
        String expr = "name == \"Mark\" && == \"Mario\" || == \"Luca\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr comboExpr = ((BinaryExpr) (expression));
        Assert.assertEquals(OR, comboExpr.getOperator());
        Assert.assertEquals(AND, getOperator());
        BinaryExpr first = ((BinaryExpr) (getLeft()));
        Assert.assertEquals("name", toString(first.getLeft()));
        Assert.assertEquals("\"Mark\"", toString(first.getRight()));
        Assert.assertEquals(EQUALS, first.getOperator());
        HalfBinaryExpr second = ((HalfBinaryExpr) (getRight()));
        Assert.assertEquals("\"Mario\"", toString(second.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());
        HalfBinaryExpr third = ((HalfBinaryExpr) (comboExpr.getRight()));
        Assert.assertEquals("\"Luca\"", toString(third.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter3() {
        String expr = "age == 2 && == 3 || == 4";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        BinaryExpr comboExpr = ((BinaryExpr) (expression));
        Assert.assertEquals(OR, comboExpr.getOperator());
        Assert.assertEquals(AND, getOperator());
        BinaryExpr first = ((BinaryExpr) (getLeft()));
        Assert.assertEquals("age", toString(first.getLeft()));
        Assert.assertEquals("2", toString(first.getRight()));
        Assert.assertEquals(EQUALS, first.getOperator());
        HalfBinaryExpr second = ((HalfBinaryExpr) (getRight()));
        Assert.assertEquals("3", toString(second.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());
        HalfBinaryExpr third = ((HalfBinaryExpr) (comboExpr.getRight()));
        Assert.assertEquals("4", toString(third.getRight()));
        Assert.assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void dotFreeWithRegexp() {
        String expr = "name matches \"[a-z]*\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertThat(expression, CoreMatchers.instanceOf(PointFreeExpr.class));
        Assert.assertEquals("name matches \"[a-z]*\"", printConstraint(expression));
        PointFreeExpr e = ((PointFreeExpr) (expression));
        Assert.assertEquals("matches", e.getOperator().asString());
        Assert.assertEquals("name", toString(e.getLeft()));
        Assert.assertEquals("\"[a-z]*\"", toString(e.getRight().get(0)));
    }

    @Test
    public void implicitOperatorWithRegexps() {
        String expr = "name matches \"[a-z]*\" || matches \"pippo\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals("name matches \"[a-z]*\" || matches \"pippo\"", printConstraint(expression));
    }

    @Test
    public void halfPointFreeExpr() {
        String expr = "matches \"[A-Z]*\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertThat(expression, CoreMatchers.instanceOf(HalfPointFreeExpr.class));
        Assert.assertEquals("matches \"[A-Z]*\"", printConstraint(expression));
    }

    @Test
    public void halfPointFreeExprNegated() {
        String expr = "not matches \"[A-Z]*\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertThat(expression, CoreMatchers.instanceOf(HalfPointFreeExpr.class));
        Assert.assertEquals("not matches \"[A-Z]*\"", printConstraint(expression));
    }

    @Test
    public void regressionTestHalfPointFree() {
        Assert.assertThat(DrlxParser.parseExpression(parser, "getAddress().getAddressName().length() == 5").getExpr(), CoreMatchers.instanceOf(BinaryExpr.class));
        Assert.assertThat(DrlxParser.parseExpression(parser, "isFortyYearsOld(this, true)").getExpr(), CoreMatchers.instanceOf(MethodCallExpr.class));
        Assert.assertThat(DrlxParser.parseExpression(parser, "getName().startsWith(\"M\")").getExpr(), CoreMatchers.instanceOf(MethodCallExpr.class));
        Assert.assertThat(DrlxParser.parseExpression(parser, "isPositive($i.intValue())").getExpr(), CoreMatchers.instanceOf(MethodCallExpr.class));
        Assert.assertThat(DrlxParser.parseExpression(parser, "someEntity.someString in (\"1.500\")").getExpr(), CoreMatchers.instanceOf(PointFreeExpr.class));
    }

    @Test
    public void mvelSquareBracketsOperators() {
        testMvelSquareOperator("this str[startsWith] \"M\"", "str[startsWith]", "this", "\"M\"", false);
        testMvelSquareOperator("this not str[startsWith] \"M\"", "str[startsWith]", "this", "\"M\"", true);
        testMvelSquareOperator("this str[endsWith] \"K\"", "str[endsWith]", "this", "\"K\"", false);
        testMvelSquareOperator("this str[length] 17", "str[length]", "this", "17", false);
    }

    @Test
    public void halfPointFreeMVEL() {
        String expr = "this str[startsWith] \"M\" || str[startsWith] \"E\"";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals("this str[startsWith] \"M\" || str[startsWith] \"E\"", printConstraint(expression));
        Expression expression2 = DrlxParser.parseExpression(parser, "str[startsWith] \"E\"").getExpr();
        Assert.assertThat(expression2, CoreMatchers.instanceOf(HalfPointFreeExpr.class));
        Assert.assertEquals("str[startsWith] \"E\"", printConstraint(expression2));
    }

    @Test
    public void testMethodCallWithComma() {
        System.out.println(("CommaSeparatedMethodCallExpr.class = " + (CommaSeparatedMethodCallExpr.class)));
        System.out.println(("new ClassOrInterfaceDeclaration().getModifiers().getClass() = " + (new ClassOrInterfaceDeclaration().getModifiers().getClass())));
        String expr = "setAge(1), setLikes(\"bread\");";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals("setAge(1), setLikes(\"bread\");", printConstraint(expression));
    }

    @Test
    public void testNewExpression() {
        String expr = "money == new BigInteger(\"3\")";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testArrayCreation() {
        String expr = "new Object[] { \"getMessageId\", ($s != null ? $s : \"42103\") }";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testArrayCreation2() {
        String expr = "functions.arrayContainsInstanceWithParameters((Object[]) $f.getPersons())";
        Expression expression = DrlxParser.parseExpression(parser, expr).getExpr();
        Assert.assertEquals(expr, printConstraint(expression));
    }
}

