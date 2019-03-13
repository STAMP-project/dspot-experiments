package com.alibaba.druid.bvt.sql;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 18/07/2017.
 */
public class CreateCompareTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "create table t7 (\n" + (((((((((((((((((((((((((((((((((((((((((((("\tint bigint,\n" + "\tFOREIGN KEY (id) REFERENCES t6 (id)\n") + ");\n") + "\n") + "create table t2 (\n") + "\tint bigint\n") + ");\n") + "\n") + "create table t9 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t8 (id)\n") + ");\n") + "\n") + "create table t6 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t5 (id)\n") + ");\n") + "\n") + "create table t8 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t7 (id)\n") + ");\n") + "\n") + "create table t4 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t2 (id)\n") + ");\n") + "\n") + "create table t5 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t4 (id)\n") + ");\n") + "\n") + "create table t3 (\n") + "\tint bigint,\n") + "\tFOREIGN KEY (id) REFERENCES t2 (id)\n") + ");\n") + "\n") + "create table t0 (\n") + "\tint bigint\n") + ");\n") + "\n") + "create table t1 (\n") + "\tint bigint\n") + ");");
        List stmtList = SQLUtils.parseStatements(sql, ORACLE);
        SQLCreateTableStatement.sort(stmtList);
        TestCase.assertEquals("t9", ((SQLCreateTableStatement) (stmtList.get(9))).getName().getSimpleName());
        TestCase.assertEquals("t3", ((SQLCreateTableStatement) (stmtList.get(8))).getName().getSimpleName());
        TestCase.assertEquals("t0", ((SQLCreateTableStatement) (stmtList.get(7))).getName().getSimpleName());
        TestCase.assertEquals("t1", ((SQLCreateTableStatement) (stmtList.get(6))).getName().getSimpleName());
        TestCase.assertEquals("t8", ((SQLCreateTableStatement) (stmtList.get(5))).getName().getSimpleName());
        TestCase.assertEquals("t7", ((SQLCreateTableStatement) (stmtList.get(4))).getName().getSimpleName());
        TestCase.assertEquals("t6", ((SQLCreateTableStatement) (stmtList.get(3))).getName().getSimpleName());
        TestCase.assertEquals("t5", ((SQLCreateTableStatement) (stmtList.get(2))).getName().getSimpleName());
        TestCase.assertEquals("t4", ((SQLCreateTableStatement) (stmtList.get(1))).getName().getSimpleName());
        TestCase.assertEquals("t2", ((SQLCreateTableStatement) (stmtList.get(0))).getName().getSimpleName());
        String sortedSql = SQLUtils.toSQLString(stmtList, ORACLE);
        System.out.println(sortedSql);
    }
}

