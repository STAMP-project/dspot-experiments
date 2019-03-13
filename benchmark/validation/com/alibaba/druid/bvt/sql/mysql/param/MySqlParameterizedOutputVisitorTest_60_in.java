package com.alibaba.druid.bvt.sql.mysql.param;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_60_in extends TestCase {
    public void test_for_parameterize() throws Exception {
        String sql = "select * from a where id in (1, 2,3)";
        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL, params);
        TestCase.assertEquals(("SELECT *\n" + ("FROM a\n" + "WHERE id IN (?)")), psql);
        TestCase.assertEquals(3, params.size());
        TestCase.assertEquals(1, params.get(0));
        TestCase.assertEquals(2, params.get(1));
        TestCase.assertEquals(3, params.get(2));
    }

    public void test_for_parameterize_1() throws Exception {
        String sql = "select * from a where (id,userId) in ((1,2), (2,3),(3,4))";
        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL, params);
        TestCase.assertEquals(("SELECT *\n" + ("FROM a\n" + "WHERE (id, userId) IN (?)")), psql);
        TestCase.assertEquals(3, params.size());
        TestCase.assertEquals("[1, 2]", params.get(0).toString());
        TestCase.assertEquals("[2, 3]", params.get(1).toString());
        TestCase.assertEquals("[3, 4]", params.get(2).toString());
    }
}

