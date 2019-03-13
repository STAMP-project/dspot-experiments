package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_Char_1 extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals("MMM", SQLEvalVisitorUtils.evalExpr(MYSQL, "CHAR(77,77.3,'77.3')"));
    }
}

