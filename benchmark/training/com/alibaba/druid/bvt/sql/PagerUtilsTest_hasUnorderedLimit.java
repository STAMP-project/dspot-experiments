package com.alibaba.druid.bvt.sql;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;


public class PagerUtilsTest_hasUnorderedLimit extends TestCase {
    public void test_false() throws Exception {
        String sql = " select * from test t order by id limit 3";
        TestCase.assertFalse(PagerUtils.hasUnorderedLimit(sql, MYSQL));
    }

    public void test_false_1() throws Exception {
        String sql = " select * from test t";
        TestCase.assertFalse(PagerUtils.hasUnorderedLimit(sql, MYSQL));
    }

    public void test_true() throws Exception {
        String sql = " select * from test t limit 3";
        TestCase.assertTrue(PagerUtils.hasUnorderedLimit(sql, MYSQL));
    }

    public void test_true_subquery() throws Exception {
        String sql = "select * from(select * from test t limit 3) x";
        TestCase.assertTrue(PagerUtils.hasUnorderedLimit(sql, MYSQL));
    }

    public void test_true_subquery_2() throws Exception {
        String sql = "select * from (select * from test t order by id desc) z limit 100";
        TestCase.assertFalse(PagerUtils.hasUnorderedLimit(sql, MYSQL));
    }
}

