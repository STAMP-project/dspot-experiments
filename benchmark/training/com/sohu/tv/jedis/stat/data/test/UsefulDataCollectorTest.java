package com.sohu.tv.jedis.stat.data.test;


import com.sohu.tv.jedis.stat.data.UsefulDataCollector;
import com.sohu.tv.jedis.stat.model.CostTimeDetailStatModel;
import com.sohu.tv.jedis.stat.utils.AtomicLongMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author leifu
 * @unknown 2015?1?23?
 * @unknown ??3:23:34
 */
public class UsefulDataCollectorTest {
    private static final Logger logger = LoggerFactory.getLogger(UsefulDataCollectorTest.class);

    @Test
    public void testGenerateCostTimeDetailStatKey() {
        AtomicLongMap<Integer> map = AtomicLongMap.create();
        map.addAndGet(5, 300);
        map.addAndGet(2, 100);
        map.addAndGet(1, 500);
        map.addAndGet(4, 300);
        map.addAndGet(10, 30);
        map.addAndGet(30, 2);
        CostTimeDetailStatModel model = UsefulDataCollector.generateCostTimeDetailStatKey(map);
        UsefulDataCollectorTest.logger.info(model.toString());
    }
}

