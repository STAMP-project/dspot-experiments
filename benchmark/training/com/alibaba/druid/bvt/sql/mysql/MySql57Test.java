package com.alibaba.druid.bvt.sql.mysql;


import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;


public class MySql57Test extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE t1 ALGORITHM=INPLACE, CHANGE COLUMN c1 c1 VARCHAR(255);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        TestCase.assertEquals(("ALTER TABLE t1"// 
         + ("\n\tALGORITHM = INPLACE,"// 
         + "\n\tCHANGE COLUMN c1 c1 VARCHAR(255);")), SQLUtils.toMySqlString(stmt));
        TestCase.assertEquals(("alter table t1"// 
         + ("\n\tALGORITHM = INPLACE,"// 
         + "\n\tchange column c1 c1 VARCHAR(255);")), SQLUtils.toMySqlString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
    }
}

