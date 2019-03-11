package com.querydsl.core.support;


import Expressions.FALSE;
import Expressions.TRUE;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Assert;
import org.junit.Test;


public class ConstantHidingExpressionTest {
    @Test
    public void constants_hidden() {
        FactoryExpression<Tuple> tuple = Projections.tuple(Expressions.stringPath("str"), TRUE, FALSE.as("false"), Expressions.constant(1));
        FactoryExpression<Tuple> wrapped = new ConstantHidingExpression<Tuple>(tuple);
        Assert.assertEquals(1, wrapped.getArgs().size());
        Tuple t = wrapped.newInstance("s");
        Assert.assertEquals("s", t.get(Expressions.stringPath("str")));
        Assert.assertEquals(Boolean.TRUE, t.get(TRUE));
        Assert.assertEquals(Boolean.FALSE, t.get(FALSE.as("false")));
        Assert.assertEquals(Integer.valueOf(1), t.get(Expressions.constant(1)));
    }
}

