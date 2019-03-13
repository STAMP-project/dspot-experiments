package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_Char extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals("MySQL", SQLEvalVisitorUtils.evalExpr(MYSQL, "CHAR(77,121,83,81,'76')"));
    }
}

