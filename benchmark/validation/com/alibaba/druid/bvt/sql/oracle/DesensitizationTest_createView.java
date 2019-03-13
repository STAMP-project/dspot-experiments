package com.alibaba.druid.bvt.sql.oracle;


import JdbcConstants.ORACLE;
import SQLUtils.FormatOption;
import com.alibaba.druid.sql.SQLUtils;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/06/2017.
 */
public class DesensitizationTest_createView extends TestCase {
    public void test_for_desensitization() throws Exception {
        String sql = "CREATE VIEW sup_orders AS\n" + (((("  SELECT suppliers.supplier_id, orders.quantity, orders.price\n" + "  FROM suppliers\n") + "  INNER JOIN orders\n") + "  ON suppliers.supplier_id = orders.supplier_id\n") + "  WHERE suppliers.supplier_name = 'Microsoft';");
        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);
        String desens_Sql = SQLUtils.format(sql, ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);
        TestCase.assertEquals(("CREATE VIEW T_2BB49C83CE16E971\n" + (((("AS\n" + "SELECT suppliers.supplier_id, orders.quantity, orders.price\n") + "FROM T_2EBB0E6843F14AEE\n") + "\tINNER JOIN T_00125D9250BE8B4C ON suppliers.supplier_id = orders.supplier_id \n") + "WHERE suppliers.supplier_name = ?;")), desens_Sql);
        Map<String, String> tableMapping = new HashMap<String, String>();
        tableMapping.put(SQLUtils.desensitizeTable("sup_orders"), "sup_orders");
        tableMapping.put(SQLUtils.desensitizeTable("suppliers"), "suppliers");
        String restore_sql = SQLUtils.refactor(desens_Sql, ORACLE, tableMapping);
        System.out.println("-------------------");
        System.out.println(restore_sql);
    }
}

