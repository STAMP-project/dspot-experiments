package com.alibaba.druid.bvt.filter.wall;


import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallUpdateCheckHandler;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 13/08/2017.
 */
public class WallUpdateCheckTest extends TestCase {
    private MySqlWallProvider wallProvider = new MySqlWallProvider();

    public void test_update_check_handler() throws Exception {
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            TestCase.assertTrue(((result.getViolations().size()) == 0));
        }
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {
            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                return false;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            TestCase.assertTrue(((result.getViolations().size()) > 0));
        }
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {
            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                return true;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            TestCase.assertTrue(((result.getViolations().size()) == 0));
        }
        TestCase.assertEquals(0, wallProvider.getWhiteListHitCount());
        TestCase.assertEquals(0, wallProvider.getBlackListHitCount());
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {
            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                // ???in?????? status in (1, 2) ?????filterValue?1?2
                TestCase.assertTrue(((filterValues.size()) == 2));
                return true;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status in (1, 2)");
            TestCase.assertTrue(((result.getViolations().size()) == 0));
        }
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 3 and status in (3, 4)");
            TestCase.assertTrue(((result.getViolations().size()) == 0));
        }
    }
}

