package org.nd4j.jita.allocator.concurrency;


import AccessState.TACK;
import AccessState.TICK;
import AccessState.TOE;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class AtomicStateTest {
    @Test
    public void testRequestTick1() throws Exception {
        AtomicState ticker = new AtomicState();
        Assert.assertEquals(TACK, ticker.getCurrentState());
    }

    @Test
    public void testRequestTick2() throws Exception {
        AtomicState ticker = new AtomicState();
        ticker.requestTick();
        Assert.assertEquals(TICK, ticker.getCurrentState());
        ticker.requestTack();
        Assert.assertEquals(TACK, ticker.getCurrentState());
        ticker.requestToe();
        Assert.assertEquals(TOE, ticker.getCurrentState());
        ticker.releaseToe();
        Assert.assertEquals(TACK, ticker.getCurrentState());
    }

    @Test
    public void testRequestTick3() throws Exception {
        AtomicState ticker = new AtomicState();
        ticker.requestTick();
        ticker.requestTick();
        Assert.assertEquals(TICK, ticker.getCurrentState());
        ticker.requestTack();
        Assert.assertEquals(TICK, ticker.getCurrentState());
        Assert.assertEquals(2, ticker.getTickRequests());
        Assert.assertEquals(1, ticker.getTackRequests());
        ticker.requestTack();
        Assert.assertEquals(TACK, ticker.getCurrentState());
        Assert.assertEquals(0, ticker.getTickRequests());
        Assert.assertEquals(0, ticker.getTackRequests());
    }

    /**
     * This test addresses reentrance for Toe state
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRequestTick4() throws Exception {
        AtomicState ticker = new AtomicState();
        ticker.requestTick();
        Assert.assertEquals(TICK, ticker.getCurrentState());
        ticker.requestTack();
        Assert.assertEquals(TACK, ticker.getCurrentState());
        ticker.requestToe();
        Assert.assertEquals(TOE, ticker.getCurrentState());
        ticker.requestToe();
        Assert.assertEquals(TOE, ticker.getCurrentState());
        ticker.releaseToe();
        Assert.assertEquals(TOE, ticker.getCurrentState());
        ticker.releaseToe();
        Assert.assertEquals(TACK, ticker.getCurrentState());
    }
}

