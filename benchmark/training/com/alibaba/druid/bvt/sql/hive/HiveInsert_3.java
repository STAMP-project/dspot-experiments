package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveInsert_3 extends TestCase {
    public void test_select() throws Exception {
        String sql = "FROM sale_detail\n" + ((("INSERT OVERWRITE TABLE sale_detail_multi partition (sale_date=\'2010\', region=\'china\' )\n" + "SELECT shop_name, customer_id, total_price\n") + "INSERT OVERWRITE TABLE sale_detail_multi partition (sale_date=\'2010\', region=\'china\' )\n") + "SELECT shop_name, customer_id, total_price;");// 

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        TestCase.assertEquals(("FROM sale_detail\n" + ((("INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date=\'2010\', region=\'china\')\n" + "SELECT shop_name, customer_id, total_price\n") + "INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date=\'2010\', region=\'china\')\n") + "SELECT shop_name, customer_id, total_price;")), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("from sale_detail\n" + ((("insert overwrite table sale_detail_multi partition (sale_date=\'2010\', region=\'china\')\n" + "select shop_name, customer_id, total_price\n") + "insert overwrite table sale_detail_multi partition (sale_date=\'2010\', region=\'china\')\n") + "select shop_name, customer_id, total_price;")), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        System.out.println(("Tables : " + (visitor.getTables())));
        System.out.println(("fields : " + (visitor.getColumns())));
        System.out.println(("coditions : " + (visitor.getConditions())));
        System.out.println(("orderBy : " + (visitor.getOrderByColumns())));
        TestCase.assertEquals(2, visitor.getTables().size());
        TestCase.assertEquals(5, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
        TestCase.assertTrue(visitor.containsColumn("sale_detail_multi", "sale_date"));
        TestCase.assertTrue(visitor.containsColumn("sale_detail_multi", "region"));
        TestCase.assertTrue(visitor.containsColumn("sale_detail", "shop_name"));
        TestCase.assertTrue(visitor.containsColumn("sale_detail", "customer_id"));
        TestCase.assertTrue(visitor.containsColumn("sale_detail", "total_price"));
    }
}

