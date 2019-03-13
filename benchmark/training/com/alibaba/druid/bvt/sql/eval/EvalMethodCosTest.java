package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodCosTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.cos(1), SQLEvalVisitorUtils.evalExpr(MYSQL, "cos(1)"));
        Assert.assertEquals(Math.cos(1.001), SQLEvalVisitorUtils.evalExpr(MYSQL, "cos(1.001)"));
        Assert.assertEquals(Math.cos(0), SQLEvalVisitorUtils.evalExpr(MYSQL, "cos(0)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "cos()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "cos(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

