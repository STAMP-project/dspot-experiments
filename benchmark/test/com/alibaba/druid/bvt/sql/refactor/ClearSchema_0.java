package com.alibaba.druid.bvt.sql.refactor;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;


public class ClearSchema_0 extends TestCase {
    public void test_insert_0() throws Exception {
        String sql = "INSERT INTO testdb.Websites (name, country)\n" + "SELECT app_name, country FROM testdb.apps;";
        SQLStatement stmt = SQLUtils.parseStatements(sql, MYSQL).get(0);
        SQLASTVisitor v = new SQLASTVisitorAdapter() {
            @Override
            public boolean visit(SQLPropertyExpr x) {
                if (SQLUtils.replaceInParent(x, new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr(x.getName()))) {
                    return false;
                }
                return super.visit(x);
            }
        };
        stmt.accept(v);
        TestCase.assertEquals(("INSERT INTO Websites (name, country)\n" + ("SELECT app_name, country\n" + "FROM apps;")), stmt.toString());
    }
}

