package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest2 extends TestCase {
    public void test_exits() throws Exception {
        String sql = "INTERVAL '30.12345' SECOND(2, 4)";
        String sql_c = "INTERVAL '30.12345' SECOND(2, 3)";
        OracleIntervalExpr exprA;
        OracleIntervalExpr exprB;
        OracleIntervalExpr exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = ((OracleIntervalExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = ((OracleIntervalExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = ((OracleIntervalExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}

