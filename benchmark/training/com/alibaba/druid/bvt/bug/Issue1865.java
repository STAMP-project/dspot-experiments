package com.alibaba.druid.bvt.bug;


import SQLParserFeature.EnableSQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;


public class Issue1865 extends TestCase {
    public void test_for_select() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "select * from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLSelectStatement stmt = ((SQLSelectStatement) (parser.parseStatement()));
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        TestCase.assertTrue(queryBlock.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("SELECT *\n" + ("FROM t\n" + "WHERE id = 2")), stmt.toString());
        TestCase.assertTrue(queryBlock.removeCondition("id = 2"));
        TestCase.assertEquals(("SELECT *\n" + "FROM t"), stmt.toString());
        queryBlock.addCondition("id = 3");
        TestCase.assertEquals(("SELECT *\n" + ("FROM t\n" + "WHERE id = 3")), stmt.toString());
    }

    public void test_for_select_group() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "select * from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, EnableSQLBinaryOpExprGroup);
        SQLSelectStatement stmt = ((SQLSelectStatement) (parser.parseStatement()));
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        TestCase.assertTrue(queryBlock.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("SELECT *\n" + ("FROM t\n" + "WHERE id = 2")), stmt.toString());
        TestCase.assertTrue(queryBlock.removeCondition("id = 2"));
        TestCase.assertEquals(("SELECT *\n" + "FROM t"), stmt.toString());
    }

    public void test_for_delete() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "delete from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLDeleteStatement stmt = ((SQLDeleteStatement) (parser.parseStatement()));
        TestCase.assertTrue(stmt.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("DELETE FROM t\n" + "WHERE id = 2"), stmt.toString());
        TestCase.assertTrue(stmt.removeCondition("id = 2"));
        TestCase.assertEquals("DELETE FROM t", stmt.toString());
        stmt.addCondition("id = 3");
        TestCase.assertEquals(("DELETE FROM t\n" + "WHERE id = 3"), stmt.toString());
    }

    public void test_for_delete_group() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "delete from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, EnableSQLBinaryOpExprGroup);
        SQLDeleteStatement stmt = ((SQLDeleteStatement) (parser.parseStatement()));
        TestCase.assertTrue(stmt.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("DELETE FROM t\n" + "WHERE id = 2"), stmt.toString());
        TestCase.assertTrue(stmt.removeCondition("id = 2"));
        TestCase.assertEquals("DELETE FROM t", stmt.toString());
        stmt.addCondition("id = 3");
        TestCase.assertEquals(("DELETE FROM t\n" + "WHERE id = 3"), stmt.toString());
    }

    public void test_for_update() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "update t set val = ? where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLUpdateStatement stmt = ((SQLUpdateStatement) (parser.parseStatement()));
        TestCase.assertTrue(stmt.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("UPDATE t\n" + ("SET val = ?\n" + "WHERE id = 2")), stmt.toString());
        TestCase.assertTrue(stmt.removeCondition("id = 2"));
        TestCase.assertEquals(("UPDATE t\n" + "SET val = ?"), stmt.toString());
        stmt.addCondition("id = 3");
        TestCase.assertEquals(("UPDATE t\n" + ("SET val = ?\n" + "WHERE id = 3")), stmt.toString());
    }

    public void test_for_update_group() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "update t set val = ? where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, EnableSQLBinaryOpExprGroup);
        SQLUpdateStatement stmt = ((SQLUpdateStatement) (parser.parseStatement()));
        TestCase.assertTrue(stmt.removeCondition("name = 'wenshao'"));
        TestCase.assertEquals(("UPDATE t\n" + ("SET val = ?\n" + "WHERE id = 2")), stmt.toString());
        TestCase.assertTrue(stmt.removeCondition("id = 2"));
        TestCase.assertEquals(("UPDATE t\n" + "SET val = ?"), stmt.toString());
        stmt.addCondition("id = 3");
        TestCase.assertEquals(("UPDATE t\n" + ("SET val = ?\n" + "WHERE id = 3")), stmt.toString());
    }
}

