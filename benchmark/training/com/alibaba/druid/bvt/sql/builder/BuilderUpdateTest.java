package com.alibaba.druid.bvt.sql.builder;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderUpdateTest extends TestCase {
    public void test_0() throws Exception {
        SQLUpdateBuilder builder = SQLBuilderFactory.createUpdateBuilder(MYSQL);
        // 
        // 
        // 
        builder.from("mytable").whereAnd("f1 > 0").set("f1 = f1 + 1", "f2 = ?");
        String sql = builder.toString();
        System.out.println(sql);
        Assert.assertEquals(("UPDATE mytable"// 
         + ("\nSET f1 = f1 + 1, f2 = ?"// 
         + "\nWHERE f1 > 0")), sql);
    }
}

