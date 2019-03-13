package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodRightTest extends TestCase {
    public void test_ascii() throws Exception {
        Assert.assertEquals("rbar", SQLEvalVisitorUtils.evalExpr(MYSQL, "right('foobarbar', 4)"));
    }
}

