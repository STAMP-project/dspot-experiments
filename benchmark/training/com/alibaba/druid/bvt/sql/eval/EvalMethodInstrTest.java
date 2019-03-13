package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodInstrTest extends TestCase {
    public void test_length() throws Exception {
        Assert.assertEquals(4, SQLEvalVisitorUtils.evalExpr(MYSQL, "instr('foobarbar', 'bar')"));
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(MYSQL, "instr('xbar', 'foobar')"));
    }
}

