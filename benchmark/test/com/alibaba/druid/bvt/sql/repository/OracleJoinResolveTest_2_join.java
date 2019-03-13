package com.alibaba.druid.bvt.sql.repository;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 03/08/2017.
 */
public class OracleJoinResolveTest_2_join extends TestCase {
    protected SchemaRepository repository = new SchemaRepository(JdbcConstants.ORACLE);

    public void test_for_issue() throws Exception {
        TestCase.assertEquals(("SELECT a.uid, a.gid, a.name, b.id, b.name\n" + (("FROM t_user a\n" + "\tINNER JOIN t_group b\n") + "WHERE a.uid = b.id")), repository.resolve("select * from t_user a inner join t_group b where a.uid = id"));
        String sql = "select a.* from t_user a inner join t_group b where a.uid = id";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, ORACLE);
        SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(ORACLE);
        schemaStatVisitor.setRepository(repository);
        for (SQLStatement stmt : statementList) {
            stmt.accept(schemaStatVisitor);
        }
        TestCase.assertTrue(schemaStatVisitor.containsColumn("t_user", "*"));
        TestCase.assertTrue(schemaStatVisitor.containsColumn("t_user", "uid"));
        TestCase.assertTrue(schemaStatVisitor.containsColumn("t_group", "id"));
    }
}

