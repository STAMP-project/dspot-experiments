package com.alibaba.druid.bvt.sql;


import JdbcConstants.DB2;
import JdbcConstants.MYSQL;
import JdbcConstants.ORACLE;
import JdbcConstants.POSTGRESQL;
import JdbcConstants.SQL_SERVER;
import com.alibaba.druid.sql.SQLUtils;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_delete extends TestCase {
    private String sql = "delete from user";

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }

    public void test_mapping_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, MYSQL, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }

    public void test_mapping_pg() throws Exception {
        String result = SQLUtils.refactor(sql, POSTGRESQL, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }

    public void test_mapping_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, ORACLE, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }

    public void test_mapping_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, SQL_SERVER, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }

    public void test_mapping_db2() throws Exception {
        String result = SQLUtils.refactor(sql, DB2, mapping);
        TestCase.assertEquals("DELETE FROM user_01", result);
    }
}

