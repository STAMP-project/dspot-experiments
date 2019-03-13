package com.alibaba.druid.bvt.sql.schemaStat;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import junit.framework.TestCase;


public class SchemaStatTest19 extends TestCase {
    public void test_schemaStat() throws Exception {
        String sql = "select * from table1 a left outer join table2 b on a.id=b.id";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(MYSQL);
        stmt.accept(statVisitor);
        System.out.println(("Tables : " + (statVisitor.getTables())));
        System.out.println(("columns : " + (statVisitor.getColumns())));
        // System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println(("relationships : " + (statVisitor.getRelationships())));// group by

        System.out.println(statVisitor.getConditions());
        TestCase.assertEquals(4, statVisitor.getColumns().size());
        TestCase.assertEquals(2, statVisitor.getConditions().size());
        TestCase.assertEquals(0, statVisitor.getFunctions().size());
        TestCase.assertTrue(statVisitor.containsTable("table1"));
        TestCase.assertTrue(statVisitor.containsColumn("table1", "*"));
        TestCase.assertTrue(statVisitor.containsColumn("table1", "id"));
        TestCase.assertTrue(statVisitor.containsColumn("table2", "id"));
        TestCase.assertTrue(statVisitor.containsColumn("table2", "*"));
    }
}

