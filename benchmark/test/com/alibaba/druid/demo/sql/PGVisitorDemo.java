package com.alibaba.druid.demo.sql;


import JdbcConstants.POSTGRESQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class PGVisitorDemo extends TestCase {
    public void test_for_demo() throws Exception {
        String sql = "select * from mytable a where a.id = 3";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, POSTGRESQL);
        PGVisitorDemo.ExportTableAliasVisitor visitor = new PGVisitorDemo.ExportTableAliasVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        SQLTableSource tableSource = visitor.getAliasMap().get("a");
        System.out.println(tableSource);
    }

    public static class ExportTableAliasVisitor extends PGASTVisitorAdapter {
        private Map<String, SQLTableSource> aliasMap = new HashMap<String, SQLTableSource>();

        public boolean visit(SQLExprTableSource x) {
            String alias = x.getAlias();
            aliasMap.put(alias, x);
            return true;
        }

        public Map<String, SQLTableSource> getAliasMap() {
            return aliasMap;
        }
    }
}

