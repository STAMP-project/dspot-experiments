package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_inquery_mysql extends TestCase {
    public void test_exits() throws Exception {
        String sql = "fstate in (select state from t_status)";
        String sql_c = "fstate_c in (select state from t_status)";
        SQLInSubQueryExpr exprA;
        SQLInSubQueryExpr exprB;
        SQLInSubQueryExpr exprC;
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprA = ((SQLInSubQueryExpr) (parser.expr()));
        }
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprB = ((SQLInSubQueryExpr) (parser.expr()));
        }
        {
            SQLExprParser parser = new SQLExprParser(sql_c);
            exprC = ((SQLInSubQueryExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(new SQLInSubQueryExpr(), new SQLInSubQueryExpr());
        Assert.assertEquals(new SQLInSubQueryExpr().hashCode(), new SQLInSubQueryExpr().hashCode());
    }
}

