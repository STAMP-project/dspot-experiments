package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_exists extends TestCase {
    public void test_exits() throws Exception {
        String sql = "exists (select 1)";
        String sql_c = "not exists (select 1)";
        SQLExistsExpr exprA;
        SQLExistsExpr exprB;
        SQLExistsExpr exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = ((SQLExistsExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = ((SQLExistsExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = ((SQLExistsExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(new SQLExistsExpr(), new SQLExistsExpr());
        Assert.assertEquals(new SQLExistsExpr().hashCode(), new SQLExistsExpr().hashCode());
    }
}

