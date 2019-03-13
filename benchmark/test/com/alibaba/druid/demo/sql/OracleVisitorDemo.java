package com.alibaba.druid.demo.sql;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class OracleVisitorDemo extends TestCase {
    public void test_for_demo() throws Exception {
        String sql = "select * from mytable a where a.id = 3";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, ORACLE);
        OracleVisitorDemo.ExportTableAliasVisitor visitor = new OracleVisitorDemo.ExportTableAliasVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        SQLTableSource tableSource = visitor.getAliasMap().get("a");
        System.out.println(tableSource);
    }

    public static class ExportTableAliasVisitor extends OracleASTVisitorAdapter {
        private Map<String, SQLTableSource> aliasMap = new HashMap<String, SQLTableSource>();

        public boolean visit(OracleSelectTableReference x) {
            String alias = x.getAlias();
            aliasMap.put(alias, x);
            return true;
        }

        public Map<String, SQLTableSource> getAliasMap() {
            return aliasMap;
        }
    }
}

