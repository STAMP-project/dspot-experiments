package com.alibaba.druid.bvt.sql;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 18/07/2017.
 */
public class CreateCompareTest_cycle extends TestCase {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE t0 (\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((("\tint bigint\n" + ");\n") + "\n") + "CREATE TABLE t1 (\n") + "\tint bigint\n") + ");\n") + "CREATE TABLE t2 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t1 (id)\n") + ");\n") + "\n") + "CREATE TABLE t3 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t2 (id)\n") + ");\n") + "\n") + "CREATE TABLE t4 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t3 (id),\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t4 (id)\n") + ");\n") + "\n") + "CREATE TABLE t5 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t4 (id)\n") + ");\n") + "\n") + "CREATE TABLE t6 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t5 (id)\n") + ");\n") + "\n") + "CREATE TABLE t7 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t6 (id)\n") + ");\n") + "\n") + "\n") + "CREATE TABLE t8 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t7 (id)\n") + ");\n") + "\n") + "CREATE TABLE t9 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id)\n") + "\t\tREFERENCES t8 (id)\n") + ");");
        List stmtList = SQLUtils.parseStatements(sql, ORACLE);
        SQLCreateTableStatement.sort(stmtList);
        String sortedSql = SQLUtils.toSQLString(stmtList, ORACLE);
        System.out.println(sortedSql);
        TestCase.assertEquals("t0", ((SQLCreateTableStatement) (stmtList.get(9))).getName().getSimpleName());
        TestCase.assertEquals("t9", ((SQLCreateTableStatement) (stmtList.get(8))).getName().getSimpleName());
        TestCase.assertEquals("t8", ((SQLCreateTableStatement) (stmtList.get(7))).getName().getSimpleName());
        TestCase.assertEquals("t7", ((SQLCreateTableStatement) (stmtList.get(6))).getName().getSimpleName());
        TestCase.assertEquals("t6", ((SQLCreateTableStatement) (stmtList.get(5))).getName().getSimpleName());
        TestCase.assertEquals("t5", ((SQLCreateTableStatement) (stmtList.get(4))).getName().getSimpleName());
        TestCase.assertEquals("t4", ((SQLCreateTableStatement) (stmtList.get(3))).getName().getSimpleName());
        TestCase.assertEquals("t3", ((SQLCreateTableStatement) (stmtList.get(2))).getName().getSimpleName());
        TestCase.assertEquals("t2", ((SQLCreateTableStatement) (stmtList.get(1))).getName().getSimpleName());
        TestCase.assertEquals("t1", ((SQLCreateTableStatement) (stmtList.get(0))).getName().getSimpleName());
        TestCase.assertEquals("t4", ((SQLAlterTableStatement) (stmtList.get(10))).getName().getSimpleName());
        TestCase.assertEquals(11, stmtList.size());
    }
}

