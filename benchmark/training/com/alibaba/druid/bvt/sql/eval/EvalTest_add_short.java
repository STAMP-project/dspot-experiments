package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalTest_add_short extends TestCase {
    public void test_byte() throws Exception {
        Assert.assertEquals(3, SQLEvalVisitorUtils.evalExpr(MYSQL, "? + ?", ((short) (1)), ((byte) (2))));
    }

    public void test_byte_1() throws Exception {
        Assert.assertEquals(3, SQLEvalVisitorUtils.evalExpr(MYSQL, "? + ?", ((short) (1)), "2"));
    }

    public void test_byte_2() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "? + ?", ((short) (1)), null));
    }

    public void test_byte_3() throws Exception {
        Assert.assertEquals(3, SQLEvalVisitorUtils.evalExpr(MYSQL, "? + ?", "2", ((short) (1))));
    }
}

