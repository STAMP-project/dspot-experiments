package com.alibaba.druid.bvt.bug;


import JdbcConstants.MYSQL;
import JdbcConstants.ORACLE;
import JdbcConstants.POSTGRESQL;
import JdbcConstants.SQL_SERVER;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;


public class Issue1695 extends TestCase {
    public void test_for_mysql() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, MYSQL);
        TestCase.assertEquals(("SELECT COUNT(*)\n" + "FROM t_books ht"), result);
    }

    public void test_for_pg() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, POSTGRESQL);
        TestCase.assertEquals(("SELECT COUNT(*)\n" + "FROM t_books ht"), result);
    }

    public void test_for_oracle() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, ORACLE);
        TestCase.assertEquals(("SELECT COUNT(*)\n" + "FROM t_books ht"), result);
    }

    public void test_for_sqlserver() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, SQL_SERVER);
        TestCase.assertEquals(("SELECT COUNT(*)\n" + "FROM t_books ht"), result);
    }
}

