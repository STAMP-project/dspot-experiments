package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_locate_2 extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals(7, SQLEvalVisitorUtils.evalExpr(MYSQL, "LOCATE('bar', 'foobarbar', 5)"));
    }
}

