package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalLikeTest extends TestCase {
    public void test_like() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' LIKE '[a-d]'"));
    }

    public void test_not_like() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "'a' NOT LIKE '[a-d]'"));
    }
}

