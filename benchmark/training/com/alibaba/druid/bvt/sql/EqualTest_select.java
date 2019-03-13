package com.alibaba.druid.bvt.sql;


import JdbcConstants.ODPS;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import java.util.List;
import junit.framework.TestCase;


public class EqualTest_select extends TestCase {
    public void test_eq_select() throws Exception {
        List stmtsA = SQLUtils.parseStatements("select * from a", ODPS);
        List stmtsB = SQLUtils.parseStatements("select * from b", ODPS);
        SQLSelect selectA = getSelect();
        SQLSelect selectB = getSelect();
        boolean eq = selectA.equals(selectB);
        TestCase.assertFalse(eq);
    }
}

