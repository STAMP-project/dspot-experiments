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
public class MappingTest_createTable extends TestCase {
    String sql = "create table user (\n" + ((("source_key int,\n" + "source_value varchar(32),\n") + "primary key(source_key)\n") + ");");

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping_createTable() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }

    public void test_mapping_createTable_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, MYSQL, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }

    public void test_mapping_createTable_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, ORACLE, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }

    public void test_mapping_createTable_pg() throws Exception {
        String result = SQLUtils.refactor(sql, POSTGRESQL, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }

    public void test_mapping_createTable_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, SQL_SERVER, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }

    public void test_mapping_createTable_db2() throws Exception {
        String result = SQLUtils.refactor(sql, DB2, mapping);
        TestCase.assertEquals(("CREATE TABLE user_01 (\n" + ((("\tsource_key int,\n" + "\tsource_value varchar(32),\n") + "\tPRIMARY KEY (source_key)\n") + ");")), result);
    }
}

