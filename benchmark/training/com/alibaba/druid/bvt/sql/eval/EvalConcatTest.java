package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalConcatTest extends TestCase {
    public void test_concat() throws Exception {
        Assert.assertEquals("abcd", SQLEvalVisitorUtils.evalExpr(MYSQL, "concat(?, ?)", "ab", "cd"));
        Assert.assertEquals("abcdef", SQLEvalVisitorUtils.evalExpr(MYSQL, "concat(?, ?, ?)", "ab", "cd", "ef"));
    }
}

