package org.drools.modelcompiler.builder.generator.drlxparse;


import CoercedExpression.CoercedExpressionException;
import CoercedExpression.CoercedExpressionResult;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.junit.Assert;
import org.junit.Test;


public class CoercedExpressionTest {
    @Test
    public void avoidCoercing() {
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("10", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("10", int.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercing2() {
        final TypedExpression left = expr("$pr.compareTo(new BigDecimal(\"0.0\"))", int.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("0", int.class), coerce.getCoercedRight());
    }

    @Test
    public void intToDouble() {
        final TypedExpression left = expr("_this.doubleValue()", double.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("0d", int.class), coerce.getCoercedRight());
    }

    @Test
    public void charToString() {
        final TypedExpression left = expr("_this", String.class);
        final TypedExpression right = expr("\'x\'", char.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        final TypedExpression expected = new TypedExpression(new StringLiteralExpr("x"), String.class);
        Assert.assertEquals(expected, coerce.getCoercedRight());
    }

    @Test
    public void stringToInt() {
        final TypedExpression left = expr("_this.getName()", String.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("String.valueOf(40)", String.class), coerce.getCoercedRight());
    }

    @Test
    public void stringToInt2() {
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("_this.getAge()", int.class), coerce.getCoercedLeft());
        Assert.assertEquals(expr("50", String.class), coerce.getCoercedRight());
    }

    @Test
    public void charToStringOnLeft() {
        final TypedExpression left = expr("_this.getCharPrimitive()", char.class);
        final TypedExpression right = expr("$c1", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("String.valueOf(_this.getCharPrimitive())", String.class), coerce.getCoercedLeft());
    }

    @Test
    public void avoidCoercingStrings() {
        final TypedExpression left = expr("_this.getName()", String.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("\"50\"", String.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercingStrings2() {
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("50", String.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercingBinaryExpressions() {
        final TypedExpression left = expr("_this.getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class);
        final TypedExpression right = expr("_this.getAddress().getNumber() == 1", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("_this.getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class), coerce.getCoercedLeft());
        Assert.assertEquals(expr("_this.getAddress().getNumber() == 1", int.class), coerce.getCoercedRight());
    }

    @Test
    public void castToObject() {
        final TypedExpression left = expr("_this.getItems().get((Integer) 1)", Object.class);
        final TypedExpression right = expr("2000", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("(java.lang.Object)2000", int.class), coerce.getCoercedRight());
    }

    @Test
    public void castToShort() {
        final TypedExpression left = expr("_this.getAgeAsShort()", Short.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("(short)40", int.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCast() {
        final TypedExpression left = expr("_this.intValue()", int.class);
        final TypedExpression right = expr("$one << $shift", long.class);
        final CoercedExpression.CoercedExpressionResult coerce = coerce();
        Assert.assertEquals(expr("$one << $shift", long.class), coerce.getCoercedRight());
    }

    @Test(expected = CoercedExpressionException.class)
    public void testException() {
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("rage", Object.class);
        new CoercedExpression(left, right).coerce();
    }
}

