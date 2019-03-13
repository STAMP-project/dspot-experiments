package com.alibaba.druid.test.wall;


import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;


public class ResourceTest extends TestCase {
    private String[] items;

    public void test_xx() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        for (int i = 0; i < (items.length); ++i) {
            String sql = items[i];
            WallCheckResult result = provider.check(sql);
            if ((result.getViolations().size()) > 0) {
                Violation violation = result.getViolations().get(0);
                System.err.println(((("error (" + i) + ") : ") + (violation.getMessage())));
                System.out.println(sql);
                System.out.println();
                // break;
            }
        }
        System.out.println(("violaionCount : " + (provider.getViolationCount())));
    }
}

