package com.alibaba.druid.benckmark.sql;


import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import junit.framework.TestCase;


public class MySqlInsertBenchmark_2 extends TestCase {
    static String sql = "INSERT INTO test_table VALUES (1, '1', '2017-10-10', true, false, '2017-10-10 10:10:10', '10:10:10', 1.111, NULL), (2, '2', '2017-10-10', true, false, '2017-10-10 10:10:10', '10:10:10', 2.222, NULL)" + (((", (2, '2', '2017-09-09', true, false, '2017-10-10 10:10:10', '10:10:10', 3.333, NULL)" + ", (3, '3', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL)") + ", (4, '4', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL)") + ", (5, '5', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL);");

    List<SQLStatement> stmtList;

    public void test_perf() throws Exception {
        System.out.println(MySqlInsertBenchmark_2.sql);
        for (int i = 0; i < 5; ++i) {
            // perf(); // 5043
            perf_toString();// 2101

            // perf_toString_featured(); // 7493
        }
    }
}

