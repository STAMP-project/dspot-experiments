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
public class MappingTest_update extends TestCase {
    private String sql = "update user set f1 = 1 where id = 3";

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }

    public void test_mapping_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, MYSQL, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }

    public void test_mapping_pg() throws Exception {
        String result = SQLUtils.refactor(sql, POSTGRESQL, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }

    public void test_mapping_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, ORACLE, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }

    public void test_mapping_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, SQL_SERVER, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }

    public void test_mapping_db2() throws Exception {
        String result = SQLUtils.refactor(sql, DB2, mapping);
        TestCase.assertEquals(("UPDATE user_01\n" + ("SET f1 = 1\n" + "WHERE id = 3")), result);
    }
}

