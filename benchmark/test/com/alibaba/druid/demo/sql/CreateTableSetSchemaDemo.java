package com.alibaba.druid.demo.sql;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;
import junit.framework.TestCase;


public class CreateTableSetSchemaDemo extends TestCase {
    public void test_schemaStat() throws Exception {
        String sql = "create table t(fid varchar(20))";
        String dbType = JdbcConstants.ORACLE;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        for (SQLStatement stmt : stmtList) {
            SQLCreateTableStatement createTable = ((SQLCreateTableStatement) (stmt));
            createTable.setSchema("sc001");
        }
        String sql2 = SQLUtils.toSQLString(stmtList, ORACLE);
        System.out.println(sql2);
    }
}

