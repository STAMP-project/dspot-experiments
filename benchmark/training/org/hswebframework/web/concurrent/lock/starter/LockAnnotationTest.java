package org.hswebframework.web.concurrent.lock.starter;


import org.hswebframework.web.concurrent.lock.LockManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * TODO ????
 *
 * @author zhouhao
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class LockAnnotationTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private LockService lockService;

    @Autowired
    private LockManager lockManager;

    @Test
    public void testLock() throws InterruptedException {
        new Thread(() -> {
            System.out.println("??");
            lockService.testLockSleep("test", 2000);
            System.out.println("??");
        }).start();
        Thread.sleep(200);
        System.out.println("????1");
        lockService.testLock("test");
        System.out.println("??1??");
        for (int i = 0; i < 100; i++) {
            new Thread(() -> lockService.testLock("test")).start();
        }
        Thread.sleep(5000);
        Assert.assertEquals(lockService.getCounter(), 101);
        lockService.reset();
    }

    @Test
    public void testReadLock() throws InterruptedException {
        new Thread(() -> {
            try {
                System.out.println("??");
                lockManager.getReadWriteLock("lock_test").writeLock().lock();
                Thread.sleep(2000);// ??2?

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("??");
            lockManager.getReadWriteLock("lock_test").writeLock().unlock();
        }).start();
        Thread.sleep(200);
        System.out.println("????1");
        lockService.testReadLock("test");
        System.out.println("??1??");
        for (int i = 0; i < 100; i++) {
            new Thread(() -> lockService.testWriteLock("test")).start();
        }
        Thread.sleep(5000);
        Assert.assertEquals(lockService.getCounter(), 101);
        lockService.reset();
    }
}

