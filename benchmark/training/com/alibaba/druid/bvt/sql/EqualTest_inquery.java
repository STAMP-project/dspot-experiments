package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_inquery extends TestCase {
    public void test_exits() throws Exception {
        String sql = "fstate in (select state from t_status)";
        String sql_c = "fstate_c in (select state from t_status)";
        SQLInSubQueryExpr exprA;
        SQLInSubQueryExpr exprB;
        SQLInSubQueryExpr exprC;
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprA = ((SQLInSubQueryExpr) (parser.expr()));
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprB = ((SQLInSubQueryExpr) (parser.expr()));
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql_c);
            exprC = ((SQLInSubQueryExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}

