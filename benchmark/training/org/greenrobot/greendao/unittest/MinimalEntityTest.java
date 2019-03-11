package org.greenrobot.greendao.unittest;


import java.util.concurrent.CountDownLatch;
import org.greenrobot.greendao.daotest.dummyapp.BuildConfig;
import org.greenrobot.greendao.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class MinimalEntityTest {
    private DaoSession daoSession;

    private MinimalEntityDao minimalEntityDao;

    @Test
    public void testBasics() {
        MinimalEntity entity = new MinimalEntity();
        insert(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(load(entity.getId()));
        Assert.assertEquals(1, count());
        Assert.assertEquals(1, loadAll(MinimalEntity.class).size());
        update(entity);
        delete(entity);
        Assert.assertNull(load(entity.getId()));
    }

    // Testing the work around for Process.myTid() being always 0 in Robolectric
    @Test
    public void testQueryForCurrentThread() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Query<MinimalEntity>[] queryHolder = new Query[1];
        new Thread() {
            @Override
            public void run() {
                try {
                    queryHolder[0] = queryBuilder().build();
                    queryHolder[0].list();
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latch.await();
        Query<MinimalEntity> query = queryHolder[0].forCurrentThread();
        query.list();
    }
}

