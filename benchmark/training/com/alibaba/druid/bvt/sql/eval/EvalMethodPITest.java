package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodPITest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.PI, SQLEvalVisitorUtils.evalExpr(MYSQL, "pi()"));
    }
}

