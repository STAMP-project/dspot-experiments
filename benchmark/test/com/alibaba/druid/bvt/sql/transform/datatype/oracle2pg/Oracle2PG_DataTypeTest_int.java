package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import junit.framework.TestCase;


public class Oracle2PG_DataTypeTest_int extends TestCase {
    public void test_oracle2pg_int() throws Exception {
        String sql = "int";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("DECIMAL(38)", pgDataType.toString());
    }

    public void test_oracle2pg_integer() throws Exception {
        String sql = "integer";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("DECIMAL(38)", pgDataType.toString());
    }

    public void test_oracle2pg_smallint() throws Exception {
        String sql = "smallint";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("SMALLINT", pgDataType.toString());
    }
}

