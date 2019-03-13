package cn.hutool.core.util;


import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link IdUtil} ????
 *
 * @author looly
 */
public class IdUtilTest {
    @Test
    public void randomUUIDTest() {
        String simpleUUID = IdUtil.simpleUUID();
        Assert.assertEquals(32, simpleUUID.length());
        String randomUUID = IdUtil.randomUUID();
        Assert.assertEquals(36, randomUUID.length());
    }

    @Test
    public void fastUUIDTest() {
        String simpleUUID = IdUtil.fastSimpleUUID();
        Assert.assertEquals(32, simpleUUID.length());
        String randomUUID = IdUtil.fastUUID();
        Assert.assertEquals(36, randomUUID.length());
    }

    @Test
    public void objectIdTest() {
        String id = IdUtil.objectId();
        Assert.assertEquals(24, id.length());
    }

    @Test
    public void createSnowflakeTest() {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        long id = snowflake.nextId();
        Assert.assertTrue((id > 0));
    }

    @Test
    public void snowflakeBenchTest() {
        final Set<Long> set = new cn.hutool.core.collection.ConcurrentHashSet();
        final Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        // ???
        int threadCount = 100;
        // ???????ID?
        final int idCountPerThread = 10000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < idCountPerThread; i++) {
                        long id = snowflake.nextId();
                        set.add(id);
                        // Console.log("Add new id: {}", id);
                    }
                    latch.countDown();
                }
            });
        }
        // ????????
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new UtilException(e);
        }
        Assert.assertEquals((threadCount * idCountPerThread), set.size());
    }
}

