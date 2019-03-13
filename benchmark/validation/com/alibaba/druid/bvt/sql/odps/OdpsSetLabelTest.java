package com.alibaba.druid.bvt.sql.odps;


import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsSetLabelTest extends TestCase {
    public void test_odps() throws Exception {
        String sql = "SET LABEL S3 TO USER aliyun$abc@alibaba-inc.com";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        Assert.assertEquals("SET LABEL S3 TO USER aliyun$abc@alibaba-inc.com", SQLUtils.toOdpsString(stmt));
        Assert.assertEquals("set label S3 to user aliyun$abc@alibaba-inc.com", SQLUtils.toOdpsString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
    }
}

