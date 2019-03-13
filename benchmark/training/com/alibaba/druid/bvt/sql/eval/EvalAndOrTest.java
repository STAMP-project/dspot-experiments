package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalAndOrTest extends TestCase {
    public void test_and() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?>0 && ?>0", 1, 0));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "2>1 && 100>10"));
    }

    public void test_or() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "10>0 || 2>0"));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(MYSQL, "10>0 || 2<0"));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "2>10 || 100<10"));
    }
}

