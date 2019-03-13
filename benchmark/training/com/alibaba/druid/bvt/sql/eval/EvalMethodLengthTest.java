package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodLengthTest extends TestCase {
    public void test_length() throws Exception {
        Assert.assertEquals(4, SQLEvalVisitorUtils.evalExpr(MYSQL, "length('text')"));
    }
}

