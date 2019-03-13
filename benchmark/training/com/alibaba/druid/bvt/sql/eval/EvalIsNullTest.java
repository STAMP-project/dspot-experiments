package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalIsNullTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? is null", 0));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "? is null", ((Object) (null))));
    }
}

