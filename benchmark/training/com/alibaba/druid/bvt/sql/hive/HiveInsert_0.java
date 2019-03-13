package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveInsert_0 extends TestCase {
    public void test_select() throws Exception {
        String sql = "INSERT INTO TABLE students\n" + "  VALUES ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);";// 

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        TestCase.assertEquals(("INSERT INTO TABLE students\n" + "VALUES ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);"), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("insert into table students\n" + "values ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);"), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(1, visitor.getTables().size());
        TestCase.assertEquals(0, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
    }
}

