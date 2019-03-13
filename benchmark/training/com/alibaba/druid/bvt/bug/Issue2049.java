package com.alibaba.druid.bvt.bug;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 12/07/2017.
 */
public class Issue2049 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from emp a,dmp b;";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, MYSQL);
        SQLSelectStatement stmt = ((SQLSelectStatement) (stmtList.get(0)));
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        SQLJoinTableSource joinTableSource = ((SQLJoinTableSource) (queryBlock.getFrom()));
        TestCase.assertEquals("a", joinTableSource.getLeft().getAlias());
        TestCase.assertEquals("b", joinTableSource.getRight().getAlias());
    }
}

