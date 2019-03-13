package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalTest_div extends TestCase {
    public void test_long() throws Exception {
        Assert.assertEquals(0L, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((long) (1)), ((byte) (2))));
    }

    public void test_int() throws Exception {
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((int) (1)), ((byte) (2))));
    }

    public void test_short() throws Exception {
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((short) (1)), ((byte) (2))));
    }

    public void test_byte() throws Exception {
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((byte) (1)), ((byte) (2))));
    }

    public void test_BigInteger() throws Exception {
        Assert.assertEquals(BigInteger.ZERO, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", BigInteger.ONE, ((byte) (2))));
    }

    public void test_BigDecimal() throws Exception {
        Assert.assertEquals(new BigDecimal("0.5"), SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", BigDecimal.ONE, ((byte) (2))));
    }

    public void test_float() throws Exception {
        Assert.assertEquals(0.5F, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((float) (1)), ((byte) (2))));
    }

    public void test_double() throws Exception {
        Assert.assertEquals(0.5, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((double) (1)), ((byte) (2))));
    }

    public void test_double_zero() throws Exception {
        Assert.assertEquals(Double.POSITIVE_INFINITY, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((double) (1)), 0));
    }

    public void test_double_zero_1() throws Exception {
        Assert.assertEquals(Double.NEGATIVE_INFINITY, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((double) (-1.0)), 0));
    }

    public void test_double_zero_2() throws Exception {
        Assert.assertEquals(Double.NaN, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((double) (0.0)), 0));
    }

    public void test_double_null() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((double) (1)), null));
    }

    public void test_double_null_1() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", null, ((double) (1))));
    }

    // 
    public void test_float_zero() throws Exception {
        Assert.assertEquals(Float.POSITIVE_INFINITY, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((float) (1)), 0));
    }

    public void test_float_zero_1() throws Exception {
        Assert.assertEquals(Float.NEGATIVE_INFINITY, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((float) (-1.0F)), 0));
    }

    public void test_float_zero_2() throws Exception {
        Assert.assertEquals(Float.NaN, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((float) (0.0F)), 0));
    }

    public void test_float_null() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", ((float) (1)), null));
    }

    public void test_float_null_1() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "?/?", null, ((float) (1))));
    }
}

