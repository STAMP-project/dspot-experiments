package com.alibaba.druid.bvt.sql.mysql.param;


import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_11 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "/* 72582af814768580067726386d39b6/0// */ select id,uid from mytable";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        TestCase.assertEquals(("SELECT id, uid\n" + "FROM mytable"), psql);
    }
}

