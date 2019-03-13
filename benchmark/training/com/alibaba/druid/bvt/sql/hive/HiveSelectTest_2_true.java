package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveSelectTest_2_true extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT * FROM customers where isvalid = true or isxx = false";// 

        TestCase.assertEquals(("SELECT *\n" + (("FROM customers\n" + "WHERE isvalid = true\n") + "\tOR isxx = false")), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("select *\n" + (("from customers\n" + "where isvalid = true\n") + "\tor isxx = false")), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(1, visitor.getTables().size());
        TestCase.assertEquals(3, visitor.getColumns().size());
        TestCase.assertEquals(2, visitor.getConditions().size());
        TestCase.assertTrue(visitor.containsColumn("customers", "isvalid"));
        TestCase.assertTrue(visitor.containsColumn("customers", "isxx"));
        TestCase.assertTrue(visitor.containsColumn("customers", "*"));
        SQLBinaryOpExpr where = ((SQLBinaryOpExpr) (getSelect().getQueryBlock().getWhere()));
        SQLBinaryOpExpr left = ((SQLBinaryOpExpr) (where.getLeft()));
        SQLBinaryOpExpr right = ((SQLBinaryOpExpr) (where.getRight()));
        TestCase.assertEquals(SQLBooleanExpr.class, left.getRight().getClass());
        TestCase.assertEquals(SQLBooleanExpr.class, right.getRight().getClass());
    }
}

