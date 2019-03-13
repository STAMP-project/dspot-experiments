package com.alibaba.druid.bvt.sql.mysql.param;


import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_43 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "UPDATE `feel_07`.feed_item_receive SET `attributes` = ?, `gmt_modified` = ?, `lock_version` = ? WHERE `feed_id` = ?";
        String params = "[\"enableTime:1498682416713,src:top,importFrom:0\",\"2017-06-29 04:40:20\",1,313825887478L]";
        String table = "[\"`feel_07`.`feed_item_receive_0502`\"]";
        String restoredSql = MySqlParameterizedOutputVisitorTest_43.restore(sql, table, params);
        TestCase.assertEquals(("UPDATE `feel_07`.`feed_item_receive_0502`\n" + ("SET `attributes` = \'enableTime:1498682416713,src:top,importFrom:0\', `gmt_modified` = \'2017-06-29 04:40:20\', `lock_version` = 1\n" + "WHERE `feed_id` = 313825887478")), restoredSql);
    }
}

