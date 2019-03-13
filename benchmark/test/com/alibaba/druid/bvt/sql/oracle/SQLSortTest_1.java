package com.alibaba.druid.bvt.sql.oracle;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;


public class SQLSortTest_1 extends TestCase {
    public void test_sort() throws Exception {
        String sql = "create view v1 as select * from v0; create view v0 as select * from t;";
        String sortedSql = SQLUtils.sort(sql, ORACLE);
        TestCase.assertEquals(("CREATE VIEW v0\n" + (((((("AS\n" + "SELECT *\n") + "FROM t;\n") + "CREATE VIEW v1\n") + "AS\n") + "SELECT *\n") + "FROM v0;")), sortedSql);
    }
}

