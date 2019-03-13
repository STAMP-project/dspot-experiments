package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_OracleDate extends TestCase {
    public void test_eq() throws Exception {
        SQLDateExpr exprA = new SQLDateExpr();
        SQLDateExpr exprB = new SQLDateExpr();
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(exprA, exprB);
    }
}

