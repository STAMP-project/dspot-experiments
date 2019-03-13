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
public class BinaryTimerTest {
    @Test
    public void testIsAlive1() throws Exception {
        BinaryTimer timer = new BinaryTimer(2, TimeUnit.SECONDS);
        timer.triggerEvent();
        Assert.assertTrue(timer.isAlive());
    }

    @Test
    public void testIsAlive2() throws Exception {
        BinaryTimer timer = new BinaryTimer(2, TimeUnit.SECONDS);
        timer.triggerEvent();
        Thread.sleep(3000);
        Assert.assertFalse(timer.isAlive());
    }
}

