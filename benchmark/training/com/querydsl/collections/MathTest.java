package com.querydsl.collections;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Assert;
import org.junit.Test;


public class MathTest {
    private NumberPath<Double> num = Expressions.numberPath(Double.class, "num");

    @Test
    public void math() {
        Expression<Double> expr = num;
        Assert.assertEquals(Math.acos(0.5), unique(MathExpressions.acos(expr)), 0.001);
        Assert.assertEquals(Math.asin(0.5), unique(MathExpressions.asin(expr)), 0.001);
        Assert.assertEquals(Math.atan(0.5), unique(MathExpressions.atan(expr)), 0.001);
        Assert.assertEquals(Math.cos(0.5), unique(MathExpressions.cos(expr)), 0.001);
        Assert.assertEquals(Math.cosh(0.5), unique(MathExpressions.cosh(expr)), 0.001);
        Assert.assertEquals(cot(0.5), unique(MathExpressions.cot(expr)), 0.001);
        Assert.assertEquals(coth(0.5), unique(MathExpressions.coth(expr)), 0.001);
        Assert.assertEquals(degrees(0.5), unique(MathExpressions.degrees(expr)), 0.001);
        Assert.assertEquals(Math.exp(0.5), unique(MathExpressions.exp(expr)), 0.001);
        Assert.assertEquals(Math.log(0.5), unique(MathExpressions.ln(expr)), 0.001);
        Assert.assertEquals(log(0.5, 10), unique(MathExpressions.log(expr, 10)), 0.001);
        Assert.assertEquals(0.25, unique(MathExpressions.power(expr, 2)), 0.001);
        Assert.assertEquals(radians(0.5), unique(MathExpressions.radians(expr)), 0.001);
        Assert.assertEquals(Integer.valueOf(1), unique(MathExpressions.sign(expr)));
        Assert.assertEquals(Math.sin(0.5), unique(MathExpressions.sin(expr)), 0.001);
        Assert.assertEquals(Math.sinh(0.5), unique(MathExpressions.sinh(expr)), 0.001);
        Assert.assertEquals(Math.tan(0.5), unique(MathExpressions.tan(expr)), 0.001);
        Assert.assertEquals(Math.tanh(0.5), unique(MathExpressions.tanh(expr)), 0.001);
    }
}

