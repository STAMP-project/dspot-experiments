package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_lcase extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals("quadratically", SQLEvalVisitorUtils.evalExpr(MYSQL, "LOWER('QUADRATICALLY')"));
    }
}

