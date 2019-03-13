package com.alibaba.druid.bvt.sql;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 23/11/2016.
 */
public class ExportParameterShardingTest extends TestCase {
    String dbType = JdbcConstants.MYSQL;

    public void test_exportParameter() throws Exception {
        String sql = "select * from t_user_0000 where oid = 1001";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        TestCase.assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        List<Object> parameters = visitor.getParameters();
        // visitor.setParameters(parameters);
        stmt.accept(visitor);
        System.out.println(out);
        System.out.println(JSON.toJSONString(parameters));
        String restoredSql = restore(out.toString(), parameters);
        TestCase.assertEquals(("SELECT *\n" + ("FROM t_user_0000\n" + "WHERE oid = 1001")), restoredSql);
    }
}

