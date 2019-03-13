package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalRlikeTest extends TestCase {
    public void test_rlike() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' REGEXP '^[a-d]'"));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' RLIKE '^[a-d]'"));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "'1' RLIKE '^[a-d]'"));
    }

    public void test_not_rlike() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' NOT REGEXP '^[a-d]'"));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' NOT RLIKE '^[a-d]'"));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "'1' NOT RLIKE '^[a-d]'"));
    }
}

