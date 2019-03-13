package com.alibaba.druid.bvt.sql.odps;


import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsSetLabelTest2 extends TestCase {
    public void test_odps() throws Exception {
        String sql = "SET LABEL S3 TO TABLE xx(f1,f2)";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SET LABEL S3 TO TABLE xx(f1, f2)", output);
    }
}

