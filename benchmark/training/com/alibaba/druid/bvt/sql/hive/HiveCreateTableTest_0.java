package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveCreateTableTest_0 extends TestCase {
    public void test_select() throws Exception {
        String sql = "CREATE TABLE students (name VARCHAR(64), age INT, gpa DECIMAL(3, 2))\n" + "  CLUSTERED BY (age) INTO 2 BUCKETS STORED AS ORC;";// 

        TestCase.assertEquals(("CREATE TABLE students (\n" + (((((("\tname VARCHAR(64),\n" + "\tage INT,\n") + "\tgpa DECIMAL(3, 2)\n") + ")\n") + "CLUSTERED BY (age)\n") + "INTO 2 BUCKETS\n") + "STORE AS ORC;")), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("create table students (\n" + (((((("\tname VARCHAR(64),\n" + "\tage INT,\n") + "\tgpa DECIMAL(3, 2)\n") + ")\n") + "clustered by (age)\n") + "into 2 buckets\n") + "store as ORC;")), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        System.out.println(("Tables : " + (visitor.getTables())));
        System.out.println(("fields : " + (visitor.getColumns())));
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(1, visitor.getTables().size());
        TestCase.assertEquals(3, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
        TestCase.assertTrue(visitor.containsColumn("students", "name"));
        TestCase.assertTrue(visitor.containsColumn("students", "age"));
        TestCase.assertTrue(visitor.containsColumn("students", "gpa"));
    }
}

