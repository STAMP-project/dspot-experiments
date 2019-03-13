package com.alibaba.druid.bvt.sql.odps;


import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsShowTablesTest0 extends TestCase {
    public void test_0() throws Exception {
        String sql = "show tables";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SHOW TABLES", output);
    }

    public void test_from() throws Exception {
        String sql = "show tables from xx";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        Assert.assertEquals("SHOW TABLES FROM xx", SQLUtils.toOdpsString(stmt));
        Assert.assertEquals("show tables from xx", SQLUtils.toOdpsString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
    }

    public void test_from_like() throws Exception {
        String sql = "show tables from xx like '*'";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        Assert.assertEquals("SHOW TABLES FROM xx LIKE '*'", SQLUtils.toOdpsString(stmt));
        Assert.assertEquals("show tables from xx like '*'", SQLUtils.toOdpsString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
    }
}

