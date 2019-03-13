package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;
import junit.framework.TestCase;


public class SQLSelectQueryBlockTest extends TestCase {
    private final String dbType = JdbcConstants.MYSQL;

    private SchemaRepository repository;

    public void test_findTableSource() throws Exception {
        repository.console("create table t_emp(emp_id bigint, name varchar(20));");
        repository.console("create table t_org(org_id bigint, name varchar(20));");
        String sql = "SELECT emp_id, a.name AS emp_name, org_id, b.name AS org_name\n" + ("FROM t_emp a\n" + "\tINNER JOIN t_org b ON a.emp_id = b.org_id");
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        TestCase.assertEquals(1, stmtList.size());
        SQLSelectStatement stmt = ((SQLSelectStatement) (stmtList.get(0)));
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        // ??????
        TestCase.assertNotNull(queryBlock.findTableSource("A"));
        TestCase.assertSame(queryBlock.findTableSource("a"), queryBlock.findTableSource("A"));
        TestCase.assertNull(queryBlock.findTableSourceWithColumn("emp_id"));
        // ??repository?column resolve
        repository.resolve(stmt);
        TestCase.assertNotNull(queryBlock.findTableSourceWithColumn("emp_id"));
        SQLExprTableSource tableSource = ((SQLExprTableSource) (queryBlock.findTableSourceWithColumn("emp_id")));
        TestCase.assertNotNull(tableSource.getSchemaObject());
        SQLCreateTableStatement createTableStmt = ((SQLCreateTableStatement) (tableSource.getSchemaObject().getStatement()));
        TestCase.assertNotNull(createTableStmt);
        SQLSelectItem selectItem = queryBlock.findSelectItem("org_name");
        TestCase.assertNotNull(selectItem);
        SQLPropertyExpr selectItemExpr = ((SQLPropertyExpr) (selectItem.getExpr()));
        SQLColumnDefinition column = selectItemExpr.getResolvedColumn();
        TestCase.assertNotNull(column);
        TestCase.assertEquals("name", column.getName().toString());
        TestCase.assertEquals("t_org", ((SQLCreateTableStatement) (column.getParent())).getName().toString());
        TestCase.assertSame(queryBlock.findTableSource("B"), selectItemExpr.getResolvedTableSource());
    }
}

