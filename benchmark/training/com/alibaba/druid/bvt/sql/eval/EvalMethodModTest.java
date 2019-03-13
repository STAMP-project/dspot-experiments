package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodModTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(MYSQL, "MOD(29,9)"));
    }
}

