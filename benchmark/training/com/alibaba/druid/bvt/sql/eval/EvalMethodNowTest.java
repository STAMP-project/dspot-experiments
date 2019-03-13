package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodNowTest extends TestCase {
    public void test_now() throws Exception {
        Assert.assertEquals(true, ((SQLEvalVisitorUtils.evalExpr(MYSQL, "now()")) instanceof Date));
    }
}

