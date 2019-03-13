package com.alibaba.druid.bvt.sql.odps;


import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsShowGrantsTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "show grants for aliyun$DXP_XXXX@aliyun.com";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("SHOW GRANTS FOR aliyun$DXP_XXXX@aliyun.com", output);
    }

    public void test_1() throws Exception {
        String sql = "show grants for aliyun$DXP_XXXX@aliyun.com on type table";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("SHOW GRANTS FOR aliyun$DXP_XXXX@aliyun.com ON TYPE table", output);
    }
}

