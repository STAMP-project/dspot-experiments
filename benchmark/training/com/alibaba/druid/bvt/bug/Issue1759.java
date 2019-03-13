package com.alibaba.druid.bvt.bug;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import junit.framework.TestCase;


/**
 * Created by wenshao on 12/07/2017.
 */
public class Issue1759 extends TestCase {
    public void test_0() throws Exception {
        String sql = "COMMENT ON COLUMN \"TB_CRM_MATERIAL\".\"INVALID_TIME\" IS \'\u751f\u6548\u65f6\u95f4\'";
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLStatement statement = parser.parseStatement();// ?????????

        OracleWallProvider provider = new OracleWallProvider();
        WallCheckResult result1 = provider.check(sql);
        TestCase.assertTrue(((result1.getViolations().size()) == 0));
    }
}

