package cn.edu.hfut.dmic.webcollector.util;


import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BerkeleyDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.RocksDBManager;
import org.junit.Test;


public class DBManagerTest {
    String tempCrawlPath = "temp_test_crawl";

    @Test
    public void testBerkeleyDBInjector() throws Exception {
        BerkeleyDBManager dbManager = new BerkeleyDBManager(tempCrawlPath);
        testInject(dbManager);
    }

    @Test
    public void testRocksDBInjector() throws Exception {
        RocksDBManager dbManager = new RocksDBManager(tempCrawlPath);
        testInject(dbManager);
    }
}

