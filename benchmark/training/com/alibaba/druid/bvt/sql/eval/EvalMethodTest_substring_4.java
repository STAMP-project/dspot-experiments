package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_substring_4 extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals("ki", SQLEvalVisitorUtils.evalExpr(MYSQL, "SUBSTRING('Sakila' FROM -4 FOR 2)"));
    }
}

