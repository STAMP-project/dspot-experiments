package org.nd4j.jita.allocator.time.impl;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class SimpleTimerTest {
    @Test
    public void testIsAlive1() throws Exception {
        SimpleTimer timer = new SimpleTimer(2, TimeUnit.SECONDS);
        timer.triggerEvent();
        Assert.assertTrue(((timer.getNumberOfEvents()) == 1));
    }

    @Test
    public void testIsAlive2() throws Exception {
        SimpleTimer timer = new SimpleTimer(2, TimeUnit.SECONDS);
        timer.triggerEvent();
        Thread.sleep(3000);
        Assert.assertEquals(0, timer.getNumberOfEvents());
    }

    @Test
    public void testIsAlive3() throws Exception {
        SimpleTimer timer = new SimpleTimer(2, TimeUnit.SECONDS);
        timer.triggerEvent();
        timer.triggerEvent();
        Assert.assertEquals(2, timer.getNumberOfEvents());
    }

    @Test
    public void testIsAlive4() throws Exception {
        SimpleTimer timer = new SimpleTimer(10, TimeUnit.SECONDS);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1000);
        Assert.assertEquals(2, timer.getNumberOfEvents());
    }

    @Test
    public void testIsAlive5() throws Exception {
        SimpleTimer timer = new SimpleTimer(10, TimeUnit.SECONDS);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1100);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1100);
        timer.triggerEvent();
        timer.triggerEvent();
        Assert.assertEquals(6, timer.getNumberOfEvents());
        Thread.sleep(9000);
        Assert.assertEquals(2, timer.getNumberOfEvents());
    }

    @Test
    public void testIsAlive6() throws Exception {
        SimpleTimer timer = new SimpleTimer(20, TimeUnit.SECONDS);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1000);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1000);
        timer.triggerEvent();
        timer.triggerEvent();
        Assert.assertEquals(6, timer.getNumberOfEvents());
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(8000);
        Assert.assertEquals(8, timer.getNumberOfEvents());
    }

    @Test
    public void testIsAlive7() throws Exception {
        SimpleTimer timer = new SimpleTimer(5, TimeUnit.SECONDS);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1000);
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(1000);
        timer.triggerEvent();
        timer.triggerEvent();
        Assert.assertEquals(6, timer.getNumberOfEvents());
        timer.triggerEvent();
        timer.triggerEvent();
        Thread.sleep(6000);
        Assert.assertEquals(0, timer.getNumberOfEvents());
    }
}

