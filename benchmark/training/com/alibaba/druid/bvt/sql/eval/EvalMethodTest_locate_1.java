package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_locate_1 extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(MYSQL, "LOCATE('xbar', 'foobar')"));
    }
}

