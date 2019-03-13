package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodAbsTest extends TestCase {
    public void test_abs_int() throws Exception {
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(-12)"));
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(12)"));
    }

    public void test_abs_long() throws Exception {
        Assert.assertEquals(12L, SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(?)", 12L));
        Assert.assertEquals(12L, SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(?)", (-12L)));
    }

    public void test_abs_decimal() throws Exception {
        Assert.assertEquals(new BigDecimal("12"), SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(?)", new BigDecimal("12")));
        Assert.assertEquals(new BigDecimal("12"), SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(?)", new BigDecimal("-12")));
    }

    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "abs()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "abs(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

