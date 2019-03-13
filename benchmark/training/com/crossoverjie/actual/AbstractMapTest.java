package com.crossoverjie.actual;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbstractMapTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMapTest.class);

    @Test
    public void test() {
        LRUAbstractMap map = new LRUAbstractMap();
        map.put(1, 1);
        map.put(2, 2);
        Object o = map.get(1);
        AbstractMapTest.LOGGER.info("getSize={}", map.size());
        map.remove(1);
        AbstractMapTest.LOGGER.info(("getSize" + (map.size())));
    }
}

