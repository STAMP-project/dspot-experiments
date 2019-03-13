package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodCeilTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(MYSQL, "ceil(1.23)"));
        Assert.assertEquals((-1), SQLEvalVisitorUtils.evalExpr(MYSQL, "ceil(-1.23)"));
        Assert.assertEquals((-1), SQLEvalVisitorUtils.evalExpr(MYSQL, "ceiling(-1.24)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "ceil()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "ceil(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

