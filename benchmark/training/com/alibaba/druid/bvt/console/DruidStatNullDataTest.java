package com.alibaba.druid.bvt.console;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.console.DruidStat;
import com.alibaba.druid.support.console.Option;
import java.io.IOException;
import junit.framework.TestCase;


// public static void main(String[] args) {
// Result result = JUnitCore.runClasses(DruidStatNullDataTest.class);
// for (Failure failure : result.getFailures()) {
// System.out.println(failure.toString());
// }
// }
public class DruidStatNullDataTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_printDruidStat() throws Exception {
        createDs();
        String pid = DruidStatNullDataTest.getSelfPid();
        String[] cmdArray = new String[]{ "-sql", pid };
        Option opt = Option.parseOptions(cmdArray);
        try {
            DruidStat.printDruidStat(opt);
        } catch (IOException ex) {
            // skip
            return;
        }
        cmdArray = new String[]{ "-act", pid };
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);
        dispose();
    }
}

