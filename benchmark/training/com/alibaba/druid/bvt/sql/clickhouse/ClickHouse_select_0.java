package com.alibaba.druid.bvt.sql.clickhouse;


import JdbcConstants.CLICKHOUSE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class ClickHouse_select_0 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT date, transactionChannel, tranactionType FROM preComp_3All_20180322 limit 1,10";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, CLICKHOUSE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(1, visitor.getTables().size());
        TestCase.assertEquals(3, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
        TestCase.assertTrue(visitor.containsTable("preComp_3All_20180322"));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));
        String output = SQLUtils.toSQLString(stmt, CLICKHOUSE);
        // 
        TestCase.assertEquals(("SELECT date, transactionChannel, tranactionType\n" + ("FROM preComp_3All_20180322\n" + "LIMIT 1, 10")), output);
    }
}

