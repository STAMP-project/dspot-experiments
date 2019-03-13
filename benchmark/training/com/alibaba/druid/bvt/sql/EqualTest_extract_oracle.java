package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_extract_oracle extends TestCase {
    public void test_exits() throws Exception {
        String sql = "EXTRACT(MONTH FROM x)";
        String sql_c = "EXTRACT(MONTH FROM 7)";
        SQLMethodInvokeExpr exprA;
        SQLMethodInvokeExpr exprB;
        SQLMethodInvokeExpr exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = ((SQLMethodInvokeExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = ((SQLMethodInvokeExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = ((SQLMethodInvokeExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(new SQLMethodInvokeExpr(), new SQLMethodInvokeExpr());
        Assert.assertEquals(new SQLMethodInvokeExpr().hashCode(), new SQLMethodInvokeExpr().hashCode());
    }
}

