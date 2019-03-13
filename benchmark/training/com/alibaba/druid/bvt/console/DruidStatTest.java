package com.alibaba.druid.bvt.console;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.console.DruidStat;
import com.alibaba.druid.support.console.Option;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import junit.framework.TestCase;


// public static void main(String[] args) {
// Result result = JUnitCore.runClasses(DruidStatTest.class);
// for (Failure failure : result.getFailures()) {
// System.out.println(failure.toString());
// }
// }
public class DruidStatTest extends TestCase {
    private DruidDataSource dataSource;

    private DruidDataSource dataSource2;

    public void test_printDruidStat() throws Exception {
        String pid = DruidStatTest.getSelfPid();
        String[] cmdArray = new String[]{ "-sql", pid };
        Option opt = Option.parseOptions(cmdArray);
        try {
            DruidStat.printDruidStat(opt);
        } catch (IOException ex) {
            // skip
            return;
        }
        cmdArray = new String[]{ "-sql", "-id", "1", pid };
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);
        cmdArray = new String[]{ "-sql", "-detail", "-id", "1", pid };
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);
        cmdArray = new String[]{ "-ds", pid };
        opt = Option.parseOptions(cmdArray);
        List<Integer> ids = DruidStat.getDataSourceIds(opt);
        opt.setDetailPrint(true);
        opt.setId(ids.get(0).intValue());
        try {
            DruidStat.printDruidStat(opt);
        } catch (IOException ex) {
            // skip
            return;
        }
    }

    public void test_printDruidStat2() throws Exception {
        String pid = DruidStatTest.getSelfPid();
        String[] cmdArray = new String[]{ "-act", pid };
        Option opt = Option.parseOptions(cmdArray);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            stmt.execute("insert into user values(30,'name2')");
            DruidStat.printDruidStat(opt);
        } catch (IOException ex) {
            // skip
            return;
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }

            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                }

        }
    }
}

