package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodAsciiTest extends TestCase {
    public void test_ascii() throws Exception {
        Assert.assertEquals(50, SQLEvalVisitorUtils.evalExpr(MYSQL, "ascii('2')"));
    }
}

