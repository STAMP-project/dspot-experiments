package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;


import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import junit.framework.TestCase;


public class Oracle2MySql_method_1_trunc extends TestCase {
    public void test_oracle2pg_int_1() throws Exception {
        String sql = "trunc(CURRENT_TIMESTAMP)";
        SQLMethodInvokeExpr expr = ((SQLMethodInvokeExpr) (com.alibaba.druid.sql.parser.SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).expr()));
        SQLExpr targetExpr = SQLTransformUtils.transformOracleToPostgresql(expr);
        TestCase.assertEquals("CURRENT_TIMESTAMP(0)", targetExpr.toString());
    }
}

