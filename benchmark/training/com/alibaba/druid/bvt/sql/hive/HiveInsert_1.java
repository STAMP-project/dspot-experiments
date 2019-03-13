package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveInsert_1 extends TestCase {
    public void test_select() throws Exception {
        String sql = "FROM page_view_stg pvs\n" + ("INSERT OVERWRITE TABLE page_view PARTITION(dt=\'2008-06-08\', country)\n" + "       SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null, null, pvs.ip, pvs.cnt");// 

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        TestCase.assertEquals(("FROM page_view_stg pvs\n" + (("INSERT OVERWRITE TABLE page_view PARTITION (dt=\'2008-06-08\', country)\n" + "SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, NULL\n") + "\t, NULL, pvs.ip, pvs.cnt")), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("from page_view_stg pvs\n" + (("insert overwrite table page_view partition (dt=\'2008-06-08\', country)\n" + "select pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null\n") + "\t, null, pvs.ip, pvs.cnt")), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(2, visitor.getTables().size());
        TestCase.assertEquals(8, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
    }
}

