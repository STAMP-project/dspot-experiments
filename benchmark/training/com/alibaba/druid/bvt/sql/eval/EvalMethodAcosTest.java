package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodAcosTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(0.0, SQLEvalVisitorUtils.evalExpr(MYSQL, "acos(1)"));
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "acos(1.001)"));
        Assert.assertEquals(Math.acos(0), SQLEvalVisitorUtils.evalExpr(MYSQL, "acos(0)"));
    }

    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "acos()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "acos(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

