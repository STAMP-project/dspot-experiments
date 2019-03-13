package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTrimTest extends TestCase {
    public void test_trim() throws Exception {
        Assert.assertEquals("bar", SQLEvalVisitorUtils.evalExpr(MYSQL, "TRIM('  bar   ')"));
    }
}

