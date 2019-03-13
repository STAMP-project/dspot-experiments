package com.alibaba.druid.bvt.sql.mysql.param;


import JdbcConstants.MYSQL;
import SerializerFeature.WriteClassName;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_41 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "select * from db_00.t_00_00 where id > 1";
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
        TestCase.assertEquals(("SELECT *\n" + ("FROM db.t\n" + "WHERE id > ?")), psql);
        String params_json = JSONArray.toJSONString(parameters, WriteClassName);
        System.out.println(params_json);
        JSONArray jsonArray = JSON.parseArray(params_json);
        String json = JSONArray.toJSONString(jsonArray, WriteClassName);
        TestCase.assertEquals("[1]", json);
        String rsql = SQLUtils.toSQLString(SQLUtils.parseStatements(psql, dbType), dbType, jsonArray);
        TestCase.assertEquals(("SELECT *\n" + ("FROM db.t\n" + "WHERE id > 1")), rsql);
    }
}

