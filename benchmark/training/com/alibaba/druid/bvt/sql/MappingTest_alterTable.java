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
public class MappingTest_alterTable extends TestCase {
    String sql = "ALTER TABLE user DROP INDEX pk_user;";

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping_createTable() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }

    public void test_mapping_createTable_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, MYSQL, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }

    public void test_mapping_createTable_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, ORACLE, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }

    public void test_mapping_createTable_pg() throws Exception {
        String result = SQLUtils.refactor(sql, POSTGRESQL, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }

    public void test_mapping_createTable_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, SQL_SERVER, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }

    public void test_mapping_createTable_db2() throws Exception {
        String result = SQLUtils.refactor(sql, DB2, mapping);
        TestCase.assertEquals(("ALTER TABLE user_01\n" + "\tDROP INDEX pk_user;"), result);
    }
}

