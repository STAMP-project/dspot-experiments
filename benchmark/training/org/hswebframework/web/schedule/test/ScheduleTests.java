package org.hswebframework.web.schedule.test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * TODO ????
 *
 * @author zhouhao
 */
@EnableScheduling
@Configuration
public class ScheduleTests extends SimpleWebApplicationTests {
    AtomicInteger counter = new AtomicInteger();

    CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void test() throws InterruptedException {
        countDownLatch.await(100, TimeUnit.SECONDS);
        Assert.assertTrue(((counter.get()) > 0));
    }
}

