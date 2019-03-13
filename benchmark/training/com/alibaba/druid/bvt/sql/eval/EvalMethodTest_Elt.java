package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_Elt extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals("ej", SQLEvalVisitorUtils.evalExpr(MYSQL, "ELT(1, 'ej', 'Heja', 'hej', 'foo')"));
    }
}

