package com.sohu.test.instance;


import com.sohu.cache.entity.InstanceCommandStats;
import com.sohu.cache.entity.InstanceStats;
import com.sohu.cache.stats.instance.InstanceStatsCenter;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * Created by yijunzhang on 14-9-18.
 */
public class InstanceStatsCenterTest extends BaseTest {
    @Resource
    private InstanceStatsCenter instanceStatsCenter;

    @Test
    public void getCommandStatsList() {
        watch.start("getCommandStatsList");
        List<InstanceCommandStats> list = instanceStatsCenter.getCommandStatsList(523L, 201506170000L, 201506172359L, "get");
        watch.stop();
        logger.info(watch.prettyPrint());
        logger.info("list={}", list);
    }

    @Test
    public void getInstanceStats() {
        InstanceStats instanceStats = instanceStatsCenter.getInstanceStats(51L);
        logger.info("{}", instanceStats);
        logger.info("{}", instanceStats.getInfoMap());
        logger.info("{}", instanceStats.isRun());
        instanceStats = instanceStatsCenter.getInstanceStats(139L);
        logger.info("{}", instanceStats);
        logger.info("{}", instanceStats.getInfoMap());
        logger.info("{}", instanceStats.isRun());
    }
}

