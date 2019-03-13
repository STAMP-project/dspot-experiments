package com.alibaba.druid.bvt.sql.odps;


import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsShowPartitionsTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "show partitions secods.xxx";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SHOW PARTITIONS secods.xxx", output);
    }
}

