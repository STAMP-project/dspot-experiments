package com.alibaba.druid.bvt.sql.mysql.param;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_49_createTable extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "CREATE TABLE projects ("// 
         + ("long_name int(3) NOT NULL default 1 + 2" + ") ");
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /* visitor.setPrettyFormat(false); */
        statement.accept(visitor);
        /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
        array.add(table.replaceAll("`",""));
        }
         */
        String psql = out.toString();
        System.out.println(psql);
        TestCase.assertEquals(("CREATE TABLE projects (\n" + ("\tlong_name int(3) NOT NULL DEFAULT 1 + 2\n" + ")")), psql);
    }
}

