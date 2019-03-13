package org.hswebframework.web.service;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
@SpringBootTest(classes = SpringTestApplication.class)
@RunWith(SpringRunner.class)
public class EnableCacheTests {
    @Autowired
    private EnableCacheTestService enableCacheTestService;

    @Autowired
    private EnableCacheAllEvictTestService enableCacheAllEvictTestService;

    @Autowired
    private EnableCacheTreeTestService enableCacheTreeTestService;

    @Autowired
    private EnableCacheAllEvictTreeTestService enableCacheAllEvictTreeTestService;

    private AtomicInteger counter = new AtomicInteger();

    @Test
    public void testSimpleCacheEnableService() {
        doTest(enableCacheTestService);
    }

    @Test
    public void testEnableCacheAllEvictTestService() {
        doTest(enableCacheAllEvictTestService);
    }

    @Test
    public void testEnableCacheTreeTestService() {
        doTest(enableCacheTreeTestService);
    }

    @Test
    public void testEnableCacheAllEvictTreeTestService() {
        doTest(enableCacheAllEvictTreeTestService);
    }
}

