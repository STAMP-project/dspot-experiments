package com.alibaba.druid.bvt.bug;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;


/**
 * Created by wenshao on 22/03/2017.
 */
public class Issue1645 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "explain extended select * from foo";
        String formatedSql = SQLUtils.format(sql, MYSQL);
        TestCase.assertEquals(("EXPLAIN extended SELECT *\n" + "FROM foo"), formatedSql);
    }
}

