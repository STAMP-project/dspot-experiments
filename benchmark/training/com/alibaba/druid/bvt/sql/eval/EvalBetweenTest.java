package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalBetweenTest extends TestCase {
    public void test_between() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? between 1 and 3", 0));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? between 1 and 3", 2));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? between 1 and 3", 4));
    }

    public void test_not_between() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? not between 1 and 3", 0));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? not between 1 and 3", 2));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? not between 1 and 3", 4));
    }
}

