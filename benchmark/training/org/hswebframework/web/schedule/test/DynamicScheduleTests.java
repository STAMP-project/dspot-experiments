package org.hswebframework.web.schedule.test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.hswebframework.web.service.schedule.ScheduleJobService;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author zhouhao
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DynamicScheduleTests extends SimpleWebApplicationTests {
    @Autowired
    private ScheduleJobService scheduleJobService;

    public static final CountDownLatch counter = new CountDownLatch(1);

    public static final AtomicLong value = new AtomicLong();

    private String id;

    @Test
    public void testCreateJob() throws InterruptedException {
        DynamicScheduleTests.counter.await(100, TimeUnit.SECONDS);
        Assert.assertTrue(((DynamicScheduleTests.value.get()) > 0));
    }
}

