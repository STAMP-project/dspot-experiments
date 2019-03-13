package com.alibaba.druid.benckmark.sql;


import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import junit.framework.TestCase;


public class MySqlInsertBenchmark extends TestCase {
    static String sql = "INSERT INTO test_table VALUES (1, '1', '2017-10-10', true, false, '2017-10-10 10:10:10', '10:10:10', 1.111, null);";

    List<SQLStatement> stmtList;

    public void test_perf() throws Exception {
        for (int i = 0; i < 5; ++i) {
            perf();
            // perf_toString(); // 425
        }
    }
}

