package com.querydsl.jpa;


import QCat.cat;
import QCat.cat.name;
import com.querydsl.core.types.Expression;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionSerializationTest {
    @Test
    public void serialize1() throws Exception {
        Expression<?> expr = name.eq("test");
        Expression<?> expr2 = serialize(expr);
        Assert.assertEquals(expr, expr2);
        Assert.assertEquals(expr.hashCode(), expr2.hashCode());
    }

    @Test
    public void query() throws IOException, ClassNotFoundException {
        JPAExpressions.selectFrom(cat).where(serialize(name.eq("test")));
    }
}

