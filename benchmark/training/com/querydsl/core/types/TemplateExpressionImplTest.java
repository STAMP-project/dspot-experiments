package com.querydsl.core.types;


import org.junit.Assert;
import org.junit.Test;


public class TemplateExpressionImplTest {
    @Test
    public void equals() {
        Expression<?> expr1 = ExpressionUtils.template(String.class, "abc", "abc");
        Expression<?> expr2 = ExpressionUtils.template(String.class, "abc", "def");
        Assert.assertFalse(expr1.equals(expr2));
    }
}

