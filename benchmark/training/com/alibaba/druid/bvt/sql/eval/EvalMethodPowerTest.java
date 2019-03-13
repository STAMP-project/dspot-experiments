package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodPowerTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.pow(1, 2), SQLEvalVisitorUtils.evalExpr(MYSQL, "power(1, 2)"));
        Assert.assertEquals(Math.pow(3, 4), SQLEvalVisitorUtils.evalExpr(MYSQL, "power(3, 4)"));
        Assert.assertEquals(Math.pow(4, 5), SQLEvalVisitorUtils.evalExpr(MYSQL, "pow(4, 5)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "pow()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "pow(a,b)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

