package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import junit.framework.TestCase;
import org.junit.Assert;


public class EqualTest_SQLSelectQueryBlock extends TestCase {
    public void test_eq() throws Exception {
        SQLSelectQueryBlock exprA = new SQLSelectQueryBlock();
        SQLSelectQueryBlock exprB = new SQLSelectQueryBlock();
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(exprA, exprB);
    }
}

