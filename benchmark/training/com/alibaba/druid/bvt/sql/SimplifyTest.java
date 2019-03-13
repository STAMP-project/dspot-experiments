package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import junit.framework.TestCase;


public class SimplifyTest extends TestCase {
    public void test_simplify_column() throws Exception {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName("`a`");
        SQLName name_0 = column.getName();
        column.simplify();
        TestCase.assertNotSame(name_0, column.getName());
        TestCase.assertEquals("a", column.getName().getSimpleName());
        name_0 = column.getName();
        column.simplify();
        TestCase.assertSame(name_0, column.getName());
    }
}

