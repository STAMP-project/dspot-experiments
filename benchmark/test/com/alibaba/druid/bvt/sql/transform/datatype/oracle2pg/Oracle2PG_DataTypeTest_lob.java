package com.alibaba.druid.bvt.sql.transform.datatype.oracle2pg;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import junit.framework.TestCase;


public class Oracle2PG_DataTypeTest_lob extends TestCase {
    public void test_oracle2pg_char() throws Exception {
        String sql = "blob";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("BYTEA", pgDataType.toString());
    }

    public void test_oracle2pg_long() throws Exception {
        String sql = "long";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("TEXT", pgDataType.toString());
    }

    public void test_oracle2pg_long_raw() throws Exception {
        String sql = "long raw";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("BYTEA", pgDataType.toString());
    }

    public void test_oracle2pg_raw() throws Exception {
        String sql = "raw(100)";
        SQLDataType dataType = SQLParserUtils.createExprParser(sql, ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToPostgresql(dataType);
        TestCase.assertEquals("BYTEA", pgDataType.toString());
    }
}

