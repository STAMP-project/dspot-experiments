package com.alibaba.druid.bvt;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import junit.framework.TestCase;


public class OrTest extends TestCase {
    public void test_xx() throws Exception {
        String sql = "select * from t where not match('', '') or (c > 0 and d >0)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, null);
        SQLStatement stmt = stmtList.get(0);
        System.out.println(stmt.toString());
    }
}

