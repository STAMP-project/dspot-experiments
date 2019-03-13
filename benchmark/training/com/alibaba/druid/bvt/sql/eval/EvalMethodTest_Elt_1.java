package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodTest_Elt_1 extends TestCase {
    public void test_method() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(MYSQL, "ELT(11, 'ej', 'Heja', 'hej', 'foo')"));
    }
}

