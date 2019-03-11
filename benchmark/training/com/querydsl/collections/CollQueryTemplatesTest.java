package com.querydsl.collections;


import CollQueryTemplates.DEFAULT;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplatesTestUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;

import static CollQueryTemplates.DEFAULT;


public class CollQueryTemplatesTest {
    @Test
    public void generic_precedence() {
        TemplatesTestUtils.testPrecedence(DEFAULT);
    }

    @Test
    public void concat() {
        StringPath a = Expressions.stringPath("a");
        StringPath b = Expressions.stringPath("b");
        Expression<?> expr = a.append(b).toLowerCase();
        String str = new CollQuerySerializer(DEFAULT).handle(expr).toString();
        Assert.assertEquals("(a + b).toLowerCase()", str);
    }
}

