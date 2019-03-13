package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodBitLengthTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals("1100", SQLEvalVisitorUtils.evalExpr(MYSQL, "BIN(12)"));
    }
}

