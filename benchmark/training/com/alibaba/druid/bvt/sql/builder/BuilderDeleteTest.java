package com.alibaba.druid.bvt.sql.builder;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderDeleteTest extends TestCase {
    public void test_0() throws Exception {
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(MYSQL);
        // 
        // 
        // 
        builder.from("mytable").whereAnd("f1 > 0");
        String sql = builder.toString();
        System.out.println(sql);
        Assert.assertEquals(("DELETE FROM mytable"// 
         + "\nWHERE f1 > 0"), sql);
    }
}

