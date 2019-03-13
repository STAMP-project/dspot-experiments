package com.alibaba.druid.bvt.sql.mysql.insert;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;


public class UseInsertColumnsCacheTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "insert into tc_biz_mytable (f1, f2,   f3, f4, f5, f6, f7, f8) values (1, 2, 3, 4, 5, 6, 7, 8)";
        SQLParserFeature[] features = new SQLParserFeature[]{ SQLParserFeature.EnableSQLBinaryOpExprGroup, SQLParserFeature.OptimizedForParameterized, SQLParserFeature.UseInsertColumnsCache };
        String psql1 = ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL);
        String psql2 = ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL);
        TestCase.assertEquals(("INSERT INTO tc_biz_mytable (f1, f2, f3, f4, f5\n" + (("\t, f6, f7, f8)\n" + "VALUES (?, ?, ?, ?, ?\n") + "\t\t, ?, ?, ?)")), psql1);
        TestCase.assertEquals(("INSERT INTO tc_biz_mytable (f1, f2, f3, f4, f5\n" + (("\t, f6, f7, f8)\n" + "VALUES (?, ?, ?, ?, ?\n") + "\t\t, ?, ?, ?)")), psql2);
    }
}

