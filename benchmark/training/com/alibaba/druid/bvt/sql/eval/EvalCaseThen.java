package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalCaseThen extends TestCase {
    public void test_eval_then() throws Exception {
        Assert.assertEquals(111, SQLEvalVisitorUtils.evalExpr(MYSQL, "case ? when 0 then 111 else 222 end", 0));
        Assert.assertEquals(222, SQLEvalVisitorUtils.evalExpr(MYSQL, "case ? when 0 then 111 else 222 end", 1));
    }
}

