package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodLog10Test extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.log10(1), SQLEvalVisitorUtils.evalExpr(MYSQL, "log10(1)"));
        Assert.assertEquals(Math.log10(1.001), SQLEvalVisitorUtils.evalExpr(MYSQL, "log10(1.001)"));
        Assert.assertEquals(Math.log10(0), SQLEvalVisitorUtils.evalExpr(MYSQL, "log10(0)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "log10()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "log10(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

