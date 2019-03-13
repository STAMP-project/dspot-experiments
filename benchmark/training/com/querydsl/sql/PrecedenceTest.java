package com.querydsl.sql;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;

import static Configuration.DEFAULT;


public class PrecedenceTest {
    @Test
    public void test() {
        StringPath str1 = Expressions.stringPath("str1");
        StringPath str2 = Expressions.stringPath("str2");
        BooleanExpression pending = str1.eq("3").and(str2.eq("1"));
        BooleanExpression notNew = str1.ne("1").and(str2.in("2", "3"));
        BooleanExpression whereClause = str1.eq("a").and(pending.or(notNew));
        String str = new SQLSerializer(DEFAULT).handle(whereClause).toString();
        Assert.assertEquals("str1 = ? and (str1 = ? and str2 = ? or str1 != ? and str2 in (?, ?))", str);
    }
}

