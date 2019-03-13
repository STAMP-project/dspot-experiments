package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalInTest extends TestCase {
    public void test_in() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? IN (1, 2, 3)", 0));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? IN (1, 2, 3)", 1));
    }

    public void test_not_in() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? NOT IN (1, 2, 3)", 0));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? NOT IN (1, 2, 3)", 1));
    }
}

