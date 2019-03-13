package com.alibaba.druid.bvt.bug;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import junit.framework.TestCase;


public class Issue2015 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "update t set a=1,b=2 where a > 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, MYSQL);
        StringBuffer buf = new StringBuffer();
        stmtList.get(0).output(buf);
        TestCase.assertEquals(("UPDATE t\n" + ("SET a = 1, b = 2\n" + "WHERE a > 1")), buf.toString());
    }
}

