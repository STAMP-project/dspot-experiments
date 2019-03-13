package com.alibaba.druid.demo.sql;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/07/2017.
 */
public class Demo_for_issue_1815 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from t1;select * from t2;";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, ORACLE);
        TestCase.assertEquals(("SELECT *\n" + "FROM t1;"), stmtList.get(0).toString());
        SQLASTOutputVisitor.defaultPrintStatementAfterSemi = false;
        TestCase.assertEquals(("SELECT *\n" + "FROM t1"), stmtList.get(0).toString());
        SQLASTOutputVisitor.defaultPrintStatementAfterSemi = null;
    }
}

