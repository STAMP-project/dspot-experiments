package cc.blynk.server.application.handlers.main.auth;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.04.18.
 */
public class RegistrationLimitCheckerTest {
    @Test
    public void testBasicFlow() throws Exception {
        LimitChecker limitChecker = new LimitChecker(10, 200);
        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(limitChecker.isLimitReached());
        }
        Assert.assertFalse(limitChecker.isLimitReached());
        Thread.sleep(200L);
        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(limitChecker.isLimitReached());
        }
        Assert.assertFalse(limitChecker.isLimitReached());
    }

    @Test
    public void testManyThreadsFlow() throws Exception {
        LimitChecker limitChecker = new LimitChecker(10, 200);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> Assert.assertFalse(limitChecker.isLimitReached()));
            threads[i].run();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertFalse(limitChecker.isLimitReached());
        Thread.sleep(200L);
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> Assert.assertFalse(limitChecker.isLimitReached()));
            threads[i].run();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertFalse(limitChecker.isLimitReached());
    }
}

