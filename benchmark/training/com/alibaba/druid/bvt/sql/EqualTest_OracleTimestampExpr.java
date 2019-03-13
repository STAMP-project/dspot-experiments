package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_OracleTimestampExpr extends TestCase {
    public void test_exits() throws Exception {
        String sql = "TIMESTAMP '' AT TIME ZONE ''";
        String sql_c = "TIMESTAMP '' AT TIME ZONE 'a'";
        SQLTimestampExpr exprA;
        SQLTimestampExpr exprB;
        SQLTimestampExpr exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = ((SQLTimestampExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = ((SQLTimestampExpr) (parser.expr()));
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = ((SQLTimestampExpr) (parser.expr()));
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(new SQLTimestampExpr(), new SQLTimestampExpr());
        Assert.assertEquals(new SQLTimestampExpr().hashCode(), new SQLTimestampExpr().hashCode());
    }
}

